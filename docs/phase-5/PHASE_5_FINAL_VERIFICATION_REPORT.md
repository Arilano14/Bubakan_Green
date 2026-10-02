# Phase 5 Final Verification Report
**Product**: Bubakan Green — Sistem Informasi Urban Farming & Taman Toga Kelurahan Bubakan  
**Package Debug**: `id.bubakangreen.app.debug`  
**Package Release**: `id.bubakangreen.app`  
**Canonical Domain**: `https://bubakangreen.web.app`  
**Verification Date**: 2026-10-02  
**Device Verified**: Google Pixel 7 Emulator (`emulator-5554`, Android 17 / API 35)

---

## 1. Executive Summary

Phase 5 has been executed and verified in accordance with the Phase 5 Implementation Plan.
All acceptance criteria have been tested with empirical evidence:

| Subphase | Component | Status | Verification Evidence |
|---|---|---|---|
| **5A** | Stable ID & Canonical URL Builder | **VERIFIED** | 10/10 unit tests passing in [QrUrlBuilderTest.kt](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/app/src/test/java/id/bubakangreen/app/core/util/QrUrlBuilderTest.kt) |
| **5B** | QR Generation & Admin Dialog | **VERIFIED** | 6/6 unit tests passing in [QrCodeGeneratorTest.kt](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/app/src/test/java/id/bubakangreen/app/core/util/QrCodeGeneratorTest.kt); visual modal verified on live emulator |
| **5C** | In-App Google Code Scanner | **IMPLEMENTED** | `play-services-code-scanner:16.1.0` integrated via [CodeScannerHandler.kt](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/app/src/main/java/id/bubakangreen/app/ui/scanner/CodeScannerHandler.kt); **ZERO camera permissions** in manifest |
| **5D** | Deep-Link Routing | **VERIFIED** | Plant & Location deep links dispatch correctly on cold & warm start via [BubakanNavHost.kt](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/app/src/main/java/id/bubakangreen/app/navigation/BubakanNavHost.kt) |
| **5E** | Android App Links | **VERIFIED** | Domain verification `approved` on device; implicit intent opens native app directly |
| **5F** | Digital Asset Links | **VERIFIED** | Real SHA-256 fingerprint extracted from debug keystore and configured in [assetlinks.json](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/web/public/.well-known/assetlinks.json) |
| **5G** | Web Fallback Pages | **IMPLEMENTED** | `plant.html` and `location.html` fetch from Firestore REST API with graceful offline/error fallback |
| **5H** | APK Download Gateway | **IMPLEMENTED** | Added APK download card & Android step-by-step installation instructions in `index.html` |
| **5I** | Automated & Emulator Testing | **VERIFIED** | 60/60 unit tests pass (100%); deep links verified on live emulator |
| **5J** | Physical Field Verification | **READY FOR FIELD** | Protocol established; in-app QR dialogs produce standard 512px scannable barcodes |

---

## 2. Security & Zero Camera Permission Audit

Per strict architectural constraints, the host application does **NOT** declare `android.permission.CAMERA`:
```xml
<!-- AndroidManifest.xml (Confirmed) -->
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <!-- Network permissions for Firestore & Auth -->
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

    <!-- Single-point location acquisition for PIC location registration -->
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <!-- ZERO CAMERA PERMISSION DECLARED -->
```
Google Play Services Code Scanner (`GmsBarcodeScanning.getClient(context, options)`) executes camera scanning entirely out-of-process.

---

## 3. Genuine Keystore Fingerprint

Extracted via official JDK `keytool` from `$env:USERPROFILE\.android\debug.keystore`:
```
Alias name: androiddebugkey
Certificate fingerprints:
  SHA1:   81:E5:42:36:0C:B2:AA:1F:3C:5F:95:41:12:CD:AD:47:24:33:97:F8
  SHA256: 8B:A0:1E:1B:2F:92:7B:75:90:24:27:68:70:39:10:F0:C1:56:02:08:70:94:EA:1E:5C:5A:27:16:4F:36:45:AB
```
Configured into [assetlinks.json](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/web/public/.well-known/assetlinks.json) for package `id.bubakangreen.app.debug`.

---

## 4. Test Suite Execution Results

**Command**: `.\gradlew.bat testDebugUnitTest`  
**Total Tests**: 60  
**Passed**: 60  
**Failures**: 0  
**Errors**: 0  

Test Suite Breakdown:
- `QrUrlBuilderTest`: 10/10 passed (Canonical domain, deterministic format, path validation, tampering rejection).
- `QrCodeGeneratorTest`: 6/6 passed (Published and active status eligibility rules).
- `AdminAuthVerificationTest`: 6/6 passed (Strict Firebase Auth gate, no SharedPreferences bypass).
- `CatalogViewModelTest`: 6/6 passed.
- `LocationsViewModelTest`: 5/5 passed.
- `LocationFormViewModelTest`: 5/5 passed.
- `MasterPlantViewModelTest`: 5/5 passed.
- `LoginViewModelTest`: 5/5 passed.
- `LocationApprovalViewModelTest`: 3/3 passed.
- `PicDashboardViewModelTest`: 3/3 passed.
- `ModelSerializationTest`: 3/3 passed.
- `FakeLocationClientTest`: 2/2 passed.
- `HomeViewModelTest`: 1/1 passed.

---

## 5. Live Emulator Test Evidence

### TEST-01: Plant Deep Link Dispatch
- Command: `adb shell am start -a android.intent.action.VIEW -d "https://bubakangreen.web.app/plant/sereh" id.bubakangreen.app.debug`
- Result: Directly navigated to `PlantDetailScreen` displaying Sereh (`Cymbopogon citratus`), Mandarin pronunciation `柠檬草` (`níng méng cǎo`), botanical care guide, and QR Code display action button.
- Evidence: UI hierarchy dump and screen capture confirmed.

### TEST-02: Location Deep Link Dispatch
- Command: `adb shell am start -a android.intent.action.VIEW -d "https://bubakangreen.web.app/location/LOC_PREVIEW_01" id.bubakangreen.app.debug`
- Result: Directly navigated to `LocationDetailScreen` displaying "Urban Farming Kelurahan Bubakan", RW 01, GPS coordinates `-7.0681, 110.3289`, directions CTA, and associated plants.
- Evidence: UI hierarchy dump and screen capture confirmed.

### TEST-05: Implicit App Links Intent Dispatch
- Command: `adb shell am start -a android.intent.action.VIEW -d "https://bubakangreen.web.app/plant/cabai"` (No package name specified)
- Result: Android system immediately routed the URL to `id.bubakangreen.app.debug` via approved App Links configuration, opening Cabai (`Capsicum annuum`) without browser disambiguation.
- Evidence: UI hierarchy dump and screen capture confirmed.

### TEST-09: Invalid Plant ID Handling
- Command: `adb shell am start -a android.intent.action.VIEW -d "https://bubakangreen.web.app/plant/non_existent_id" id.bubakangreen.app.debug`
- Result: Safely transitioned to empty state with character mascot, "Informasi Belum Tersedia", "Data tanaman ini belum lengkap.", and "Kembali ke Katalog" button. Zero crashes.
- Evidence: Screen capture confirmed.

### In-App QR Generation Visual Dialog
- Action: Tapped QR action button on `PlantDetailScreen` hero overlay (`x=975, y=377`).
- Result: Rendered botanical dialog with "Kode QR Resmi Kelurahan Bubakan", botanical title, 512px dark green QR code, "RESMI TERVERIFIKASI" badge, canonical URL preview box, and "Salin URL" + "Tutup" buttons meeting Fitts's Law touch target criteria (>= 48dp).

---

## 6. Release Deployment Prerequisite

When the production release APK is built with the production release keystore:
1. Run `keytool -list -v -keystore <path-to-release.keystore> -alias <release-alias>`
2. Copy the release SHA-256 fingerprint into `web/public/.well-known/assetlinks.json` under `package_name: "id.bubakangreen.app"`
3. Deploy Firebase Hosting via `firebase deploy --only hosting` to host `https://bubakangreen.web.app/.well-known/assetlinks.json`
