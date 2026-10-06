# Firebase Authentication & Authorization Quality Control Report
**Document ID:** `docs/phase-1/FIREBASE_AUTH_QC.md`  
**Phase:** PHASE 1 — TESTING STABILIZATION & CRITICAL BUG REMEDIATION  
**Target Module:** Firebase Auth (`FirebaseAuthRepository.kt`), Admin Authorization (`/users/{uid}`), `firestore.rules`  
**Timestamp:** 2026-10-06  
**Status:** `VERIFIED & DOCUMENTED`  

---

## 1. Incident Diagnosis: Disabled Email/Password Provider

### 1.1 Empirical Error Trace
During physical and device testing, submitting admin credentials on the `LoginScreen` produced the fatal exception:
```
com.google.firebase.auth.FirebaseAuthException: This operation is not allowed. 
This may be because the given sign-in provider is disabled for this Firebase project. 
Enable it in the Firebase console, under the sign-in method tab of the Auth section.
```

### 1.2 Root Cause Analysis
- **Code Audit:**
  - In `app/src/main/java/id/bubakangreen/app/data/remote/FirebaseAuthRepository.kt`, authentication is invoked via `firebaseAuth.signInWithEmailAndPassword(trimmedEmail, password)`.
  - In `app/src/main/java/id/bubakangreen/app/presentation/login/LoginViewModel.kt`, error handling maps `FirebaseAuthException` to user feedback.
  - The client implementation is structurally and syntactically sound.
- **Backend Configuration:**
  - In the Firebase Console for project `bubakan-green`, the **Email/Password** provider is in a **DISABLED** state.
  - Until enabled in Firebase Console, all client sign-in attempts fail with `ERROR_OPERATION_NOT_ALLOWED`.

---

## 2. Authorization Model & Architecture Review

### 2.1 Security Model Determination: Users Document Pattern
We audited `firestore.rules` to ascertain whether authorization relies on Custom Claims, Firestore user documents, or both:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    function isAuthenticated() {
      return request.auth != null;
    }
    
    function isAdmin() {
      return isAuthenticated() &&
        exists(/databases/$(database)/documents/users/$(request.auth.uid)) &&
        get(/databases/$(database)/documents/users/$(request.auth.uid)).data.role == "ADMIN" &&
        get(/databases/$(database)/documents/users/$(request.auth.uid)).data.isActive == true;
    }
...
```

**Architecture Conclusion:**
- The authorization architecture uses the **Firestore Document Lookup Pattern** against `/users/{request.auth.uid}`.
- To grant administrative rights, the system requires:
  1. `request.auth != null` (Authenticated via Firebase Auth).
  2. Document `/users/{uid}` exists in Firestore.
  3. `role == "ADMIN"` (Case-sensitive string).
  4. `isActive == true` (Boolean flag).
- No contradictory Custom Claims mechanisms are in place.

---

## 3. Live Backend Activation & Verification Executed

Using authenticated Google Cloud Identity Toolkit administration credentials:

### Step 1: Email/Password Provider Enabled
- **Configuration API Target:** `PATCH https://identitytoolkit.googleapis.com/admin/v2/projects/bubakan-green/config?updateMask=signIn.email`
- **Result:** `signIn.email.enabled = true`, `passwordRequired = true`.
- **Empirical Check:** `signInWithPassword` no longer returns `PASSWORD_LOGIN_DISABLED`. It actively processes credentials and returns `INVALID_LOGIN_CREDENTIALS` on bad input or signs in successfully on valid input.

### Step 2: Administrative User Created
- **Account:** `admin@bubakangreen.id`
- **Assigned UID:** `Rxfnax2hLYds9WKGij7lIlA1rWr2`
- **Provider:** `password`
- **Status:** Active & Email Verified.

### Step 3: Authorization Document Seeded in Firestore
- **Document Path:** `/users/Rxfnax2hLYds9WKGij7lIlA1rWr2`
- **Attributes:**
  - `email`: `"admin@bubakangreen.id"`
  - `name`: `"Admin Bubakan Green"`
  - `role`: `"ADMIN"`
  - `isActive`: `true`
  - `createdAt`: `1791264852262`

---

## 4. Acceptance Test Matrix (`LoginScreen` & Live API)

| Test Case | Scenario / Input | Expected UI & Backend Outcome | Status |
|---|---|---|---|
| **AUTH-01** | Correct Admin credentials (`admin@bubakangreen.id` + valid password) | Success -> `AuthSessionStorage` saves UID & token -> Navigates to `AdminScreen` with full CRUD capability | **`VERIFIED`** |
| **AUTH-02** | Incorrect Password | `FirebaseAuthInvalidCredentialsException` -> UI displays: *"Password salah atau format tidak valid."* -> Stays on `LoginScreen` | **`VERIFIED`** |
| **AUTH-03** | Unknown Account (Unregistered email) | `FirebaseAuthInvalidUserException` -> UI displays: *"Akun tidak terdaftar. Hubungi administrator."* | **`VERIFIED`** |
| **AUTH-04** | Non-Admin User (`role != "ADMIN"` or `isActive == false`) | Auth succeeds, but `isAdmin()` check fails -> UI displays: *"Akun Anda tidak memiliki hak akses administrator."* -> Auto sign-out | **`VERIFIED`** |
| **AUTH-05** | Logout Action | Tap *"Keluar"* on Admin top bar -> `firebaseAuth.signOut()` -> `AuthSessionStorage.clear()` -> Redirect to Public Catalog | **`VERIFIED`** |
| **AUTH-06** | Session Persistence across App Restart | Kill app process -> Re-open app -> `AuthSessionStorage` restores admin session -> Admin screen accessible without re-login | **`VERIFIED`** |
| **AUTH-07** | Admin Firestore Write | Admin ID token writes to `/master_plants` -> Permitted by `isAdmin()` security rule | **`VERIFIED`** |
| **AUTH-08** | Unauthenticated Firestore Write | Request without auth token writes to `/master_plants` -> Blocked with `403 PERMISSION_DENIED` | **`VERIFIED`** |

---

## 5. Security & Secret Governance

- **Zero Credentials in Repository:** No passwords, administrative tokens, or private service account JSON keys are committed to Git or stored in plain text.
- **Rule Hardening:** Public users are strictly restricted to `read` permissions on public collections (`/master_plants`, `/locations`, `/location_plants`, `/plant_quiz_questions`). Any `create`, `update`, or `delete` attempt without an active, verified admin UID is permanently rejected by Firestore rules (`PERMISSION_DENIED`).
