# Firebase Connection Audit Report
**Project:** Bubakan Green  
**Date:** 2026-10-05  
**Audit Status:** AUDIT COMPLETE — AWAITING USER APPROVAL  

---

## 1. Executive Summary

An exhaustive audit of the Bubakan Green Android application build configuration, Firebase SDK declarations, `google-services.json`, security rules, repository provider architecture, and web hosting configuration was conducted prior to making any code modifications.

### Key Finding: CONFIGURATION MISMATCH
- Production package `id.bubakangreen.app` is registered in `app/google-services.json` and passes `processReleaseGoogleServices` successfully.
- Debug build type in `app/build.gradle.kts` specifies `applicationIdSuffix = ".debug"`, resulting in package name `id.bubakangreen.app.debug`.
- `app/google-services.json` does **NOT** contain an entry for `id.bubakangreen.app.debug`.
- Gradle execution of `processDebugGoogleServices` fails with:
  ```text
  Execution failed for task ':app:processDebugGoogleServices'.
  > No matching client found for package name 'id.bubakangreen.app.debug' in ...\app\google-services.json
  ```
- In accordance with Section 5 of the guidelines, `applicationId` was **NOT** silently modified. This report details the issue and recommended resolution options.

---

## 2. Detailed Audit Checklist (Items A – J)

| Item | Question | Audit Result | Evidence / Details |
|---|---|---|---|
| **A** | Is `google-services` plugin already declared? | **YES** | Root `build.gradle.kts` declares `alias(libs.plugins.google.services) apply false`. Version catalog `gradle/libs.versions.toml` defines `googleServices = "4.4.2"`. |
| **B** | Is it already applied to app module? | **YES (Conditional)** | `app/build.gradle.kts` applies `com.google.gms.google-services` dynamically when `file("google-services.json").exists()`. |
| **C** | Which Firebase BoM version is currently used? | **33.9.0** | Defined in `gradle/libs.versions.toml` (`firebaseBom = "33.9.0"`). Stable and compatible. |
| **D** | Which Firebase SDKs are already declared? | **Firestore & Auth only** | `firebase-firestore` and `firebase-auth` imported via `platform(libs.firebase.bom)`. No unnecessary SDKs (analytics, crashlytics, storage, database are absent). |
| **E** | Is `google-services.json` present? | **YES** | Located at `app/google-services.json` (677 bytes). Project ID: `bubakan-green`, Project Number: `1054147629887`. |
| **F** | Which Android package(s) are represented inside `google-services.json`? | **`id.bubakangreen.app`** | Only 1 client is configured: Client ID `1:1054147629887:android:f85eda15df5f007926398a`. |
| **G** | Is debug package `id.bubakangreen.app.debug` supported? | **NO (MISMATCH)** | `id.bubakangreen.app.debug` is missing from `google-services.json`. |
| **H** | Is production package `id.bubakangreen.app` supported? | **YES** | `processReleaseGoogleServices` compiles and succeeds in 6s. |
| **I** | Is Firebase currently real or preview fallback? | **Preview Fallback** | `RepositoryProvider.kt` catches exceptions when Firebase is not initialized and serves `UiPreviewOnly...`. Once Google Services compiles into debug/release, it connects to real Firebase. |
| **J** | Which files actually need modification? | **Pending Decision** | See Section 5 below. Depends on chosen strategy to resolve debug package mismatch. |

---

## 3. Inspection of Target Files

### 1. `root build.gradle.kts`
- Declares:
  ```kotlin
  plugins {
      alias(libs.plugins.android.application) apply false
      alias(libs.plugins.kotlin.android) apply false
      alias(libs.plugins.kotlin.compose) apply false
      alias(libs.plugins.google.services) apply false
  }
  ```
- **Status:** Complete, consistent, standard Version Catalog convention. No change required.

### 2. `app/build.gradle.kts`
- Contains conditional application:
  ```kotlin
  if (file("google-services.json").exists()) {
      apply(plugin = "com.google.gms.google-services")
  }
  ```
- `buildTypes.debug` defines:
  ```kotlin
  debug {
      applicationIdSuffix = ".debug"
  }
  ```
- Dependencies block uses:
  ```kotlin
  implementation(platform(libs.firebase.bom))
  implementation(libs.firebase.firestore)
  implementation(libs.firebase.auth)
  ```
- **Status:** Well-structured. If user approves standardizing plugin declaration or resolving debug suffix, this file is the focal point.

### 3. `gradle/libs.versions.toml`
- `firebaseBom = "33.9.0"`
- `googleServices = "4.4.2"`
- Official documentation notes `google-services: 4.5.0` and `firebase-bom: 34.19.0`. However, following the rule **Existing compatibility > Minimal change > Latest version**, current versions compile cleanly and do not require forced bumping unless a concrete incompatibility arises.
- **Status:** Compatible and stable.

### 4. `settings.gradle.kts` & `gradle.properties`
- Google Maven repository declared with regex filters in `settings.gradle.kts`.
- JVM args, AndroidX, and JDK 21 paths set in `gradle.properties`.
- **Status:** Healthy. No change required.

### 5. `app/google-services.json`
- Project: `bubakan-green`
- Project Number: `1054147629887`
- Storage Bucket: `bubakan-green.firebasestorage.app`
- Single client: `id.bubakangreen.app`
- **Status:** Valid for production/release variant, but causes `CONFIGURATION MISMATCH` for debug variant.

### 6. `app/google-services.json.template`
- Present and intact. Documents setup steps and instructions for developers.
- **Status:** Preserved as documentation artifact.

### 7. `RepositoryProvider.kt`
- Implements resilient Service Locator pattern:
  - `createPlantRepository()` -> attempts `FirebaseFirestore.getInstance()` -> `FirestorePlantRepository`
  - `createLocationRepository()` -> attempts `FirebaseFirestore.getInstance()` -> `FirestoreLocationRepository`
  - `createAuthRepository()` -> attempts `FirebaseAuth.getInstance()` -> `FirebaseAuthRepository`
  - Falls back to `UiPreviewOnly...` when Firebase runtime is absent.
- **Status:** Ready for real Firebase activation without modifying domain interfaces.

### 8. `FirebaseAuthRepository.kt`
- Real `FirebaseAuth` flow:
  - `signInWithEmail(email, password)`
  - Reads `/users/{uid}` in Firestore
  - Enforces `role == "ADMIN"` and `isActive == true`
  - Unauthenticated sessions return `role = UserRole.PUBLIC`
  - Preserves Admin / PIC security model.
- **Status:** Complete and strictly adheres to specifications.

### 9. `FirestorePlantRepository.kt` & `FirestoreLocationRepository.kt`
- Uses canonical Firestore collections:
  - `/master_plants`
  - `/location_plants`
  - `/locations`
  - `/location_condition_logs`
- No non-canonical collection names (`plants`, `plant_data`, `mobile_plants`).
- **Status:** 100% compliant with canonical architecture.

### 10. `firestore.rules`
- Strict role-based security:
  - `/master_plants`, `/location_plants`, `/locations`: Public read, Admin write.
  - `/location_condition_logs`: Admin read/create, update/delete denied.
  - `/users/{userId}`: Read own document or Admin; write restricted to Admin.
  - `/audit_logs`: Authenticated create, Admin read.
- **Status:** Compliant. Write permissions remain strictly protected.

### 11. `web/firebase.json` & `web/public`
- Points to `firestore.rules` and `public/` directory.
- Rewrites configured for SPA (`index.html`, `plant.html`, `location.html`).
- Hosting Status: **NOT DEPLOYED** (per instruction, not deploying automatically).
- Live domain: `https://bubakangreen.web.app`

### 12. Application Initialization (`BubakanApplication.kt`)
- `RepositoryProvider.init(this)` called in `onCreate()`.
- Sets 100MB Firestore persistent cache settings when `FirebaseApp` is initialized.
- **Status:** Complete and ready.

---

## 4. Package Configuration Analysis & Mismatch

### Problem Statement
When running `./gradlew.bat assembleDebug`, Gradle executes task `:app:processDebugGoogleServices`.
The Google Services Gradle plugin parses `app/google-services.json` searching for a client whose `package_name` matches the current build variant's application ID.
- Debug build Application ID: `id.bubakangreen.app.debug`
- Registered packages in `google-services.json`: `id.bubakangreen.app` only.
- Result: **Build Failure** (`No matching client found for package name 'id.bubakangreen.app.debug'`).

By contrast, `processReleaseGoogleServices` succeeds because the release Application ID is `id.bubakangreen.app`.

---

## 5. Proposed Strategies to Resolve Mismatch

We recommend one of the two following strategies:

### Strategy 1 (Recommended — Firebase Console Multi-Client Registration)
1. In the Firebase Console for project `bubakan-green`, click **Project Settings** > **General** > **Add app** (Android).
2. Register the debug application ID: `id.bubakangreen.app.debug` (App nickname: `Bubakan Green Debug`).
3. Download the updated `google-services.json` (which will now contain **both** `id.bubakangreen.app` and `id.bubakangreen.app.debug` client blocks).
4. Replace `app/google-services.json` with this updated file.
5. **Pros:** Zero changes to Gradle build logic; preserves variant isolation; follows official Firebase multi-environment guidelines.

### Strategy 2 (Code-Level Resolution — Align Debug Application ID)
1. Modify `app/build.gradle.kts` to remove `applicationIdSuffix = ".debug"` so both debug and release variants use `id.bubakangreen.app`.
2. **Pros:** Can be applied immediately without returning to the Firebase Console.
3. **Cons:** Debug and release APKs cannot be installed side-by-side on the same physical device.

---

## 6. Audit Conclusion & Stop Condition Status

- **Proposed Version Upgrades:** NONE. Existing `google-services` plugin (4.4.2) and `firebase-bom` (33.9.0) are stable, tested, and compile cleanly.
- **Blockers:** The debug package mismatch must be resolved before `assembleDebug` and emulator tests can run against real Firebase.
- **Current State:** Audit complete. No project code modified.

**AWAITING EXPLICIT USER DECISION AND APPROVAL:**
`ACC FIREBASE ANDROID CONNECTION`
