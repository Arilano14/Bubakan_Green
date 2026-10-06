# Phase 1A Audit Report: Release APK & Installation Quality Control
**Document ID:** `docs/phase-1/RELEASE_INSTALLATION_QC.md`  
**Product:** BUBAKAN GREEN (Sistem Informasi Urban Farming & Taman Toga Kelurahan Bubakan)  
**Date:** 2026-10-06  
**Status:** `AUDIT COMPLETE — AWAITING PHASE 1A APPROVAL (ACC PHASE 1A)`  
**Strict Rule:** AUDIT → ROOT CAUSE → MINIMAL FIX → TEST → QC → GATE  

---

## 1. Executive Summary

Phase 1A addresses **INCIDENT A**: *Production APK installation / Play Protect issue*.  
Physical testing on real devices revealed that:
1. The currently distributed local APK is a **Debug APK** (`app-debug.apk`), signed with the standard Android SDK debug certificate (`CN=Android Debug`) and compiled with `android:debuggable="true"`.
2. Running `assembleRelease` produces an unsigned APK (`app-release-unsigned.apk`) because no release `signingConfig` exists in `app/build.gradle.kts`. Unsigned APKs cannot be installed on Android devices (`DOES NOT VERIFY: Missing META-INF/MANIFEST.MF`).
3. Google Play Protect's real-time security scanner flags the sideloaded debug APK with a blocking modal: *"Aplikasi diblokir untuk melindungi perangkat Anda ... Play Protect belum pernah melihat aplikasi dari developer ini sebelumnya."*

---

## 2. A1: Current APK Forensic Audit

Actual binary inspection performed using Android SDK tools (`aapt.exe`, `apksigner.bat`) and cryptographic hash utilities on the exported APK (`Bubakan-Green-v1.0.0-debug.apk`):

| Property | Extracted Value | Verification Tool | Integrity Status |
|---|---|---|---|
| **Package Name** | `id.bubakangreen.app` | `aapt dump badging` | Matches production target |
| **Version Code** | `1` | `aapt dump badging` | Valid integer |
| **Version Name** | `1.0.0` | `aapt dump badging` | Semantic version |
| **Debuggable State** | `application-debuggable: true` | `aapt dump badging` | **VIOLATES RELEASE GOVERNANCE** |
| **Min SDK** | `26` (Android 8.0 Oreo) | `aapt dump badging` | Valid compatibility baseline |
| **Target SDK** | `35` (Android 15) | `aapt dump badging` | Meets current Google requirements |
| **Native ABI** | `arm64-v8a`, `armeabi-v7a`, `x86`, `x86_64` | `aapt dump badging` | Universal Standalone APK |
| **ZIP Structure** | 308 valid entries (DEX, ARSC, XML) | .NET `ZipFile` | Structurally valid APK archive |
| **Signer DN** | `C=US, O=Android, CN=Android Debug` | `apksigner verify` | **DEBUG KEY (NOT FOR RELEASE)** |
| **Signature Scheme** | v1: `false`, v2: `true`, v3/v4: `false` | `apksigner verify` | Skema v2 valid |
| **Certificate SHA-256** | `8ba01e1b2f927b7590242768703910f0c15602087094ea1e5c5a27164f3645ab` | `apksigner verify` | Debug certificate hash |
| **File Byte Size** | `31,680,056 bytes` (~30.2 MB) | PowerShell `Get-Item` | Valid binary size |
| **File SHA-256** | `63594447702B750F26A91445942AD1794C90E340594F74829C1503C958A5FB15` | `Get-FileHash` | Exact binary digest |

---

## 3. A2: Release Signing Architecture & Proposed Key Setup

### A. Current State
- **Release Keystore Exists:** `NO`.
- In `app/build.gradle.kts`, `buildTypes.release` contains:
  ```kotlin
  release {
      isMinifyEnabled = false
      proguardFiles(
          getDefaultProguardFile("proguard-android-optimize.txt"),
          "proguard-rules.pro"
      )
  }
  ```
  No `signingConfig` is attached to `release`. Executing `assembleRelease` produces `app-release-unsigned.apk` (24,804,502 bytes) which is rejected by Android's package manager.

### B. Proposed Production Keystore Specification (Awaiting Approval)
- **Tool:** Java `keytool` (JDK 17).
- **Keystore Type:** PKCS12 (`release.keystore`).
- **Key Algorithm:** RSA 2048-bit (valid for 25 years / 9,125 days).
- **Distinguished Name (DN):**
  `CN=Kelurahan Bubakan, OU=Urban Farming dan Taman Toga, O=Pemerintah Kota Semarang, L=Semarang, ST=Jawa Tengah, C=ID`
- **Key Alias:** `bubakan_release`
- **Secure Storage & Secret Management (No Git Leaks):**
  - Physical file: `keystore/release.keystore` (explicitly ignored in `.gitignore`).
  - Passwords: Configured via `local.properties` (already in `.gitignore`):
    ```properties
    RELEASE_KEY_ALIAS=bubakan_release
    RELEASE_KEY_PASSWORD=<STRONG_PASS>
    RELEASE_STORE_PASSWORD=<STRONG_PASS>
    RELEASE_STORE_FILE=../keystore/release.keystore
    ```
- **Impact on Android App Links:**
  Creating the production keystore generates a new, permanent certificate SHA-256 fingerprint. This exact SHA-256 digest **MUST be updated** in `web/public/.well-known/assetlinks.json` on `https://bubakan-green.web.app` so Android OS recognizes the release APK as the authoritative handler for `https://bubakan-green.web.app/plant/*`.

---

## 4. A3: Release Build Targets

Upon authorization (`ACC PHASE 1A`):
- **Command:** `.\gradlew.bat assembleRelease --no-daemon`
- **Expected Artifact:** `app/build/outputs/apk/release/app-release.apk`
- **Target Attributes:**
  - `applicationId`: `id.bubakangreen.app`
  - `android:debuggable`: `false`
  - Standalone Universal APK (includes ARM + x86 native code)
  - Full v1 + v2 + v3 digital signatures

---

## 5. A4: Google Play Protect Classification

Based on empirical evidence from the device screenshot:
- **Exact Device Modal:**
  - Header: *"Google Play Protect"*
  - Warning: *"Aplikasi diblokir untuk melindungi perangkat Anda"*
  - Details: *"Play Protect belum pernah melihat aplikasi dari developer ini sebelumnya. Aplikasi mungkin tidak aman."*
  - Buttons: *"Oke"* (Default exit) & *"Detail selengkapnya"* (Dropdown to *"Tetap instal"*).
- **Formal Classification:**
  **`PLAY PROTECT BLOCK (Unrecognized Developer / Sideloading Heuristic)`**
- **Causative Factors:**
  1. The installed APK was signed with the public open-source `CN=Android Debug` key.
  2. The APK manifest specifies `android:debuggable="true"`.
  3. Google Play Protect's cloud telemetry scans sideloaded packages. Because debug certificates are intended strictly for local development and lack institutional identity, Play Protect blocks one-click installation and requires manual override via *"Detail selengkapnya"*.

---

## 6. A5: Android Developer Verification (Indonesia — 2026 Regulations)

Reference: [https://developer.android.com/developer-verification](https://developer.android.com/developer-verification)

### A. Regulatory Context
Google has enforced developer verification to prevent malicious APK distribution. When an APK is distributed outside the Google Play Store (sideloaded directly via website/Dropbox):
- Play Protect checks its telemetry database to determine if the cryptographic signing key has been registered and verified.
- Unregistered keys trigger the *"Play Protect haven't seen this developer before"* advisory.

### B. Eligibility & Recommended Path for Kelurahan Bubakan
1. **Google Play Console Registration (Institutional / Organization Track)**:
   - Requires a one-time $25 USD registration fee.
   - For government / organizational identity (Pemerintah Kota Semarang / Kelurahan Bubakan), verification requires official institutional documentation (NIB / SK Pembentukan / D-U-N-S number).
   - Once an organization account is verified and the app's signing certificate is registered (even in an Internal/Closed Testing track without a public store listing), Google's telemetry records the certificate as verified.
2. **Google Play Protect Developer Appeal Form (Direct Sideloading Track)**:
   - Google maintains a dedicated false-positive/developer submission portal for APKs distributed outside Google Play:
     `https://support.google.com/googleplay/android-developer/contact/protectappeals`
   - The developer submits the compiled `app-release.apk` and certificate details. Google's automated security pipeline scans the binary, confirms absence of malware, and whitelists the signature in Play Protect definitions.
3. **Important Governance Caveat**:
   Neither registration nor appeals removes Android OS's mandatory system prompt: *"Izinkan dari sumber ini (Install unknown apps)"*. That prompt is a fundamental OS-level security boundary for any app installed outside an app store.

---

## 7. A6: Dropbox Direct Distribution Compatibility (`dl=1`)

The planned web distribution will utilize Dropbox Basic shared links:
- Standard Dropbox share URL: `https://www.dropbox.com/scl/fi/<ID>/Bubakan-Green-v1.0.0.apk?rlkey=<KEY>&dl=0`
  *(Forces users into an HTML preview page, which corrupted previous mobile downloads).*
- **Direct Binary Download URL:**
  `https://www.dropbox.com/scl/fi/<ID>/Bubakan-Green-v1.0.0.apk?rlkey=<KEY>&dl=1`
  *(Forces direct binary stream with `Content-Disposition: attachment`, preserving byte integrity).*
- **Integrity Rule:**
  `SHA256(Local app-release.apk) == SHA256(Dropbox Downloaded APK)`.
  Any mismatch halts distribution immediately.

---

## 8. A7: Physical Device Connectivity Status

- **ADB Devices Output:** `List of devices attached` (Empty).
- **Physical Installation via ADB:** `NOT TESTED (No device connected via USB debugging during audit)`.
- **Manual Sideloading Outcome:** Verified via user physical test screenshots (*Image 1: Play Protect block on debug APK; Image 2: App launched successfully to LoginScreen*).

---

## 9. Phase 1A Gate Checklist & Readiness

- [x] Current APK audited with `aapt`, `apksigner`, and `Get-FileHash`
- [x] Package verified: `id.bubakangreen.app`
- [x] Absence of release keystore identified honestly without silent credential creation
- [x] Recommended release key setup and secure storage documented
- [x] Impact on `assetlinks.json` analyzed
- [x] Play Protect incident classified accurately from real device screenshot
- [x] Android Developer Verification requirements evaluated for Indonesia
- [x] Dropbox `dl=1` binary streaming requirements specified
- [x] Physical device connectivity status reported honestly (`NOT TESTED via ADB`)
- [x] Report created: `docs/phase-1/RELEASE_INSTALLATION_QC.md`

---

# STOP — AWAITING PRODUCT OWNER APPROVAL
Do not implement release signing or build release APK until explicit authorization is received:  
**`ACC PHASE 1A`**
