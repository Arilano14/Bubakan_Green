# Local APK Build & Export Report
**Document ID:** `docs/release/LOCAL_APK_BUILD_REPORT.md`  
**Date:** 2026-10-06  
**Project:** Bubakan Green (Sistem Informasi Urban Farming & Taman Toga Kelurahan Bubakan)  
**Status:** `VERIFIED` (Local Build & Export Complete)  

---

## 1. Build & Release Overview

- **Build type:** Debug  
  > *Note on Release Signing:* As observed in `app/build.gradle.kts`, the `release` build type has no configured `signingConfig`. Executing `assembleRelease` produces `app-release-unsigned.apk` (24,804,502 bytes) which lacks digital signatures and cannot be installed on Android devices. In strict compliance with Step 3 rules, no dummy credentials were created silently. Instead, the installable `debug` variant was compiled, validated, and explicitly labeled as a **DEBUG APK**.
- **Package:** `id.bubakangreen.app`
- **Version:** `1.0.0` (versionCode: `1`)
- **Min SDK:** `26` (Android 8.0 Oreo)
- **Target SDK:** `35` (Android 15)
- **Source APK:** `C:\Users\Arilano\Downloads\Project ARICE\Bubakan Green\app\build\outputs\apk\debug\app-debug.apk`
- **Exported APK:** `C:\Users\Arilano\Downloads\Project ARICE\Bubakan-Green-v1.0.0-debug.apk`
- **File Size:** `31,828,129 bytes` (~30.3 MB)  
- **SHA-256 Digest:** `72C35B59A5C6EA9628CA20F80C9AE15E84A3C6D7C28B9A75BF46C88F0CC28D81`  

---

## 2. Integrity & Cryptographic Hash Verification

| Location | Path | SHA-256 Checksum | Match Status |
|---|---|---|---|
| **Build Output** | `app/build/outputs/apk/debug/app-debug.apk` | `72C35B59A5C6EA9628CA20F80C9AE15E84A3C6D7C28B9A75BF46C88F0CC28D81` | Baseline Source |
| **Exported Location** | `C:\Users\Arilano\Downloads\Project ARICE\Bubakan-Green-v1.0.0-debug.apk` | `72C35B59A5C6EA9628CA20F80C9AE15E84A3C6D7C28B9A75BF46C88F0CC28D81` | **100% IDENTICAL** |

---

## 3. Toolchain & Structural Validation

- **`aapt dump badging`:**
  - `package: name='id.bubakangreen.app' versionCode='1' versionName='1.0.0'`
  - `sdkVersion:'26'`
  - `targetSdkVersion:'35'`
  - `application-label:'BUBAKAN GREEN'`
  - `application-debuggable`
  - `launchable-activity: name='id.bubakangreen.app.MainActivity'`
  - `native-code: 'arm64-v8a' 'armeabi-v7a' 'x86' 'x86_64'` (Universal ABI)
- **`apksigner verify --verbose --print-certs`:**
  - Verifies: `true`
  - APK Signature Scheme v2: `true`
  - APK Signature Scheme v1: `false`
  - Signer #1 DN: `C=US, O=Android, CN=Android Debug`
  - Certificate SHA-256: `8ba01e1b2f927b7590242768703910f0c15602087094ea1e5c5a27164f3645ab`
- **ZIP File Structure:**
  - Total Entries: `308`
  - `AndroidManifest.xml`: Present & Valid Binary XML
  - `classes.dex`: Present & Valid DEX
  - `resources.arsc`: Present & Valid ARSC Table
  - Corrupted/HTML Payload: **NO (Pure Valid Android APK Archive)**

---

## 4. Test Suite Execution

- **Task:** `.\gradlew.bat testDebugUnitTest --no-daemon`
- **Result:** `BUILD SUCCESSFUL in 1m 12s` (23 actionable tasks, 0 failures)
- **Tested Modules:**
  - `BubakanGeoValidatorTest` (Jordan Curve Ray-Casting boundary validation)
  - `LocationFormViewModelTest` (Geospatial location gating)
  - `QrUrlBuilderTest` (Canonical QR URL generation contract)

---

## 5. Installation Testing Status

- **Status:** `NOT TESTED` (No active Android device or emulator daemon running on ADB during build execution).
- **Previous Emulator Testing:** The exact same codebase passed UI, navigation, and functional testing on Pixel 7 (Android 14 API 34).
- **Physical Sideloading Note:** Because this APK is signed with `CN=Android Debug`, Android devices will prompt for unknown source installation (`REQUEST_INSTALL_PACKAGES`) and Google Play Protect will flag it as an unverified debug application.

---

## 6. Strict Governance Audit

- [x] Current project inspected on branch `main`
- [x] No `git push` executed
- [x] No UI / UX / database / Firebase / feature changes introduced
- [x] No external upload performed (local export only)
- [x] APK copied to root `C:\Users\Arilano\Downloads\Project ARICE\Bubakan-Green-v1.0.0-debug.apk`
- [x] Source and destination SHA-256 verified identical
