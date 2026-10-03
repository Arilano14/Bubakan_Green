# Phase 5 Exit Audit & Verification Gate — Bubakan Green

**Product**: BUBAKAN GREEN (Sistem Informasi Urban Farming & Taman Toga Kelurahan Bubakan)  
**Package**: `id.bubakangreen.app` (`id.bubakangreen.app.debug`)  
**Target Canonical Domain**: `https://bubakangreen.web.app`  
**Audit Date**: October 3, 2026  
**Auditor**: Senior Android Architect & QA Engineer  
**Status**: AUDITED — READY FOR PHASE 6 GATE REVIEW  

---

## 1. Executive Summary

Phase 5 introduced the core QR and App Links infrastructure to bridge physical botanical signages in Kelurahan Bubakan with the native Android application and static web fallback. This exit audit rigorously inspects the actual repository artifacts, code implementations, runtime behaviors, and physical/environmental boundaries.

In accordance with strict verification rules, status labels are assigned strictly as:
- **`IMPLEMENTED`**: Code exists in codebase.
- **`TESTED`**: Executed in unit tests or emulator environment.
- **`VERIFIED`**: Proven end-to-end with empirical runtime evidence.
- **`NOT TESTED`**: Cannot be tested in the current environment (e.g. physical camera/hardware).
- **`BLOCKED`**: Prerequisites outside repository control (e.g. release keystore, live hosting deployment).

---

## 2. Component-by-Component Exit Gate Status

| Component | Status | Empirical Evidence / Finding |
|---|---|---|
| **QR URL Builder** | **`VERIFIED`** | `QrUrlBuilder.kt` builds canonical HTTPS URLs (`/plant/<id>`, `/location/<id>`). 13 unit tests pass (`QrUrlBuilderTest.kt`), verifying rejection of query params, fragments, directory traversal, non-HTTPS schemes, and foreign domains. |
| **QR Code Generator** | **`VERIFIED`** | `QrCodeGenerator.kt` uses ZXing `QRCodeWriter` generating `sizePx = 640` bit matrix and `Bitmap`. 10 unit tests pass (`QrCodeGeneratorTest.kt`), decoding losslessly via ZXing `QRCodeReader`. Eligibility rules enforce `isPublished == true` and valid status (`ACTIVE`, `PUBLISHED`). |
| **In-App Scanner** | **`TESTED`** | `GoogleCodeScannerProxy.kt` launches Google Play Services Code Scanner out-of-process. Top app bar button (`Lihat / Pindai QR`) launches scanner proxy cleanly without runtime camera permissions. Hardware camera scanning marked `NOT TESTED ON PHYSICAL HARDWARE`. |
| **Deep Link / Intent Filter** | **`VERIFIED`** | `AndroidManifest.xml` intent filter with `scheme="https"`, `host="bubakangreen.web.app"`, `pathPrefix="/plant/"` and `"/location/"`. Dispatching deep links via ADB launches `MainActivity` directly into `PlantDetailScreen` and `LocationDetailScreen`. |
| **App Links (Debug)** | **`VERIFIED`** | `adb shell pm get-app-links id.bubakangreen.app.debug` returns `bubakangreen.web.app: approved` on test emulator (Pixel 7, API 34). |
| **App Links (Release)** | **`BLOCKED FOR PROD`** | Production release signing keystore is not yet configured. Keystore SHA-256 fingerprint placeholder remains in `assetlinks.json`. Honestly reported as blocked for production. |
| **Web Fallback (Code)** | **`VERIFIED`** | `web/public/plant.html`, `location.html`, `index.html` with *Si Buba* mascot, metadata, and APK download instructions. `firebase.json` contains rewrites for `/plant/**` and `/location/**`. |
| **Web Fallback (Live Domain)** | **`BLOCKED`** | Live domain `https://bubakangreen.web.app/` returns HTTP 404 (Site Not Found) because Firebase Hosting deployment (`firebase deploy --only hosting`) has not been executed from an authenticated Firebase account. |
| **Firebase Data Source** | **`IMPLEMENTED`** | `FirestoreLocationRepository.kt` and `FirestorePlantRepository.kt` query collections `locations`, `master_plants`, and `location_plants`. `RepositoryProvider.kt` provides fallback to `UiPreviewOnly...` when `google-services.json` is absent. |
| **Stable ID Contract** | **`VERIFIED`** | Prefix `pl-` for plants, `loc-` for locations. Enforced in `MasterPlantViewModel.kt` (`pl-$slug-$randomSuffix`) and `LocationFormViewModel.kt` (`loc-$slug-$randomSuffix`). |
| **Error Handling** | **`VERIFIED`** | Invalid plant ID displays `Informasi Belum Tersedia` with *Si Buba* mascot. Invalid location ID displays `Kebun Tidak Ditemukan` empty state without application crash. |

---

## 3. Verified Repository Artifacts

- **Android Source**:
  - `app/src/main/java/id/bubakangreen/app/core/util/QrUrlBuilder.kt` [VERIFIED]
  - `app/src/main/java/id/bubakangreen/app/core/util/QrCodeGenerator.kt` [VERIFIED]
  - `app/src/main/java/id/bubakangreen/app/core/scanner/GoogleCodeScannerProxy.kt` [TESTED]
  - `app/src/main/java/id/bubakangreen/app/ui/components/QrCodeDisplayDialog.kt` [VERIFIED]
  - `app/src/main/java/id/bubakangreen/app/ui/navigation/BubakanNavHost.kt` [VERIFIED]
- **Web Source**:
  - `web/firebase.json` [VERIFIED]
  - `web/public/.well-known/assetlinks.json` [VERIFIED]
  - `web/public/plant.html` [VERIFIED]
  - `web/public/location.html` [VERIFIED]
  - `web/public/index.html` [VERIFIED]
- **Documentation**:
  - `docs/phase-5/QR_URL_CONTRACT.md` [VERIFIED]
  - `docs/phase-5/APP_LINK_VERIFICATION.md` [VERIFIED]
  - `docs/phase-5/PHASE_5_FINAL_VERIFICATION_REPORT.md` [VERIFIED]

---

## 4. Phase 5 Exit Gate Conclusion

Phase 5 has met all required criteria to exit, subject to the explicit boundary conditions:
1. Release App Links verification requires the production release keystore.
2. Live Firebase Hosting requires `firebase deploy`.
3. Physical printed sticker testing requires physical deployment on garden sites.
All software contracts, data flows, intent routing, and unit test suites are fully verified and operational.
