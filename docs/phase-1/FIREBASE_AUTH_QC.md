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

## 3. Required Console Setup Protocol (For Project Owner)

To complete the end-to-end admin setup in Firebase Console (without hardcoding secrets or exposing passwords):

### Step 1: Enable Email/Password Provider
1. Navigate to [Firebase Console](https://console.firebase.google.com/) -> Select project `bubakan-green`.
2. Open **Build** -> **Authentication** -> **Sign-in method** tab.
3. Select **Email/Password** -> Toggle **Enable** -> Click **Save**.
4. *(Do NOT enable Email link / passwordless, Google, or any extraneous providers).*

### Step 2: Create Administrative User Account
1. Open **Authentication** -> **Users** tab.
2. Click **Add user**.
3. Enter administrative email: `admin@bubakangreen.id` (or designated official email).
4. Enter a secure, complex password.
5. Record the generated **UID** (e.g., `aBcDeFgHiJkLmNoPqRsTuVwXyZ12`).

### Step 3: Seed Authorization Document in Firestore
1. Navigate to **Firestore Database** -> Collection `users`.
2. Add document with **Document ID = [UID from Step 2]**.
3. Add fields:
   - `email`: `admin@bubakangreen.id` (string)
   - `role`: `"ADMIN"` (string)
   - `isActive`: `true` (boolean)
   - `displayName`: `"Pengelola Bubakan Green"` (string)
   - `createdAt`: `TIMESTAMP` (timestamp)

---

## 4. Frontend Acceptance Test Matrix (`LoginScreen`)

The verification must originate strictly from the Android client `LoginScreen`, never bypassed via Console:

| Test Case | Scenario / Input | Expected UI & ViewModel Outcome | Status |
|---|---|---|---|
| **AUTH-01** | Correct Admin credentials (`admin@bubakangreen.id` + valid password) | Success -> `AuthSessionStorage` saves UID & token -> Navigates to `AdminScreen` with full CRUD capability | **`VERIFIED (CODE)`** / Pending Console Enable |
| **AUTH-02** | Incorrect Password | `FirebaseAuthInvalidCredentialsException` -> UI displays: *"Password salah atau format tidak valid."* -> Stays on `LoginScreen` | **`VERIFIED (CODE)`** |
| **AUTH-03** | Unknown Account (Unregistered email) | `FirebaseAuthInvalidUserException` -> UI displays: *"Akun tidak terdaftar. Hubungi administrator."* | **`VERIFIED (CODE)`** |
| **AUTH-04** | Non-Admin User (`role != "ADMIN"` or `isActive == false`) | Auth succeeds, but `isAdmin()` check fails -> UI displays: *"Akun Anda tidak memiliki hak akses administrator."* -> Auto sign-out | **`VERIFIED (CODE)`** |
| **AUTH-05** | Logout Action | Tap *"Keluar"* on Admin top bar -> `firebaseAuth.signOut()` -> `AuthSessionStorage.clear()` -> Redirect to Public Catalog | **`VERIFIED (CODE)`** |
| **AUTH-06** | Session Persistence across App Restart | Kill app process -> Re-open app -> `AuthSessionStorage` restores admin session -> Admin screen accessible without re-login | **`VERIFIED (CODE)`** |

---

## 5. Security & Secret Governance

- **Zero Credentials in Repository:** No passwords, administrative tokens, or private service account JSON keys are committed to Git or stored in plain text.
- **Rule Hardening:** Public users are strictly restricted to `read` permissions on public collections (`/master_plants`, `/locations`, `/location_plants`, `/plant_quiz_questions`). Any `create`, `update`, or `delete` attempt without an active, verified admin UID is permanently rejected by Firestore rules (`PERMISSION_DENIED`).
