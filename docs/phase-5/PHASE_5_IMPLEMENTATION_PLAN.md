# Phase 5 Implementation Plan (Master Architecture Specification)

**Product**: BUBAKAN GREEN (Sistem Informasi Urban Farming & Taman Toga Kelurahan Bubakan)  
**Package Debug**: `id.bubakangreen.app.debug`  
**Package Release**: `id.bubakangreen.app`  
**Canonical Domain**: `https://bubakangreen.web.app`  
**Phase State**: PHASE 5 PLANNED (AWAITING EXPLICIT USER APPROVAL: `ACC PHASE 5`)  

---

## 1. Scope Control & Boundaries

### 1.1 In-Scope Components (Phase 5 Only)
1. **Canonical Firestore Collection**: Single unified collection (`/master_plants`) shared across Android, Web Fallback, Admin CRUD, and Seeding.
2. **Stable Resource Identity & URL Contract**: Immutable stable IDs (`pl-xxxxxxxx`, `loc-xxxxxxxx`) mapping deterministically to `https://bubakangreen.web.app/plant/<id>` and `/location/<id>`.
3. **In-App Google Code Scanner**: High-performance QR scanning via `play-services-code-scanner:16.1.0` with **zero camera permissions** in the host app manifest.
4. **Scan Security Validation**: Strict URL validator rejecting foreign domains, insecure HTTP, javascript:, and malformed paths.
5. **Android App Links & Back Stack**: Intent filtering with `autoVerify="true"`, cold start direct routing, warm start `onNewIntent()`, and deterministic back stack to `HomeScreen`.
6. **Digital Asset Links**: `.well-known/assetlinks.json` hosting on Firebase Hosting. Debug verified; release gated until production release keystore.
7. **Web Fallback on Firebase Hosting**: Smart fallback (`plant.html`, `location.html`), educational product pitch, friendly mascot branding, and clear "Buka di Aplikasi" and "Download APK" CTAs.
8. **Realtime Data Synchronization**: Cloud Firestore `.snapshots()` listeners ensuring Admin edits propagate to public user screens dynamically.

### 1.2 Prohibited Features (Strictly Out of Scope)
* ❌ NO AI chatbots or external conversational agents.
* ❌ NO IoT or physical sensor integrations.
* ❌ NO continuous background GPS tracking.
* ❌ NO payment gateways or monetization frameworks.
* ❌ NO secondary backend servers or web admin CMS.
* ❌ NO fictional community locations (Approved: Urban Farming Kelurahan Bubakan & Taman Toga RW 03).

---

## 2. File Change Control Matrix

### 2.1 Files That Must Change (During Phase 5 Implementation)
| File Path | Planned Action | Justification |
|---|---|---|
| `app/src/main/java/id/bubakangreen/app/data/remote/FirestorePlantRepository.kt` | MODIFIED & VERIFIED | Standardize collection to canonical `master_plants` (BUG-01). |
| `scripts/seed_default_catalog.py` | MODIFIED & VERIFIED | Standardize seed collection to `master_plants`. |
| `web/public/location.html` | MODIFIED & VERIFIED | Allow `ACTIVE` and `NEEDS_MAINTENANCE` garden statuses (BUG-02). |
| `web/public/plant.html` | VERIFY / POLISH | Query `/master_plants/{id}` with robust fallback UI. |
| `web/public/index.html` | POLISH | Reflect active development status and 4-step Android APK sideloading guidance. |
| `web/public/.well-known/assetlinks.json` | GATED | Update release SHA-256 fingerprint when production release keystore is created. |

### 2.2 Files That May Change (If Refinement Required During QA)
| File Path | Planned Action | Justification |
|---|---|---|
| `app/src/main/java/id/bubakangreen/app/navigation/BubakanNavHost.kt` | POLISH | Refine backstack popping logic across deep-link destinations. |
| `app/src/main/java/id/bubakangreen/app/ui/components/QrCodeDisplayDialog.kt` | POLISH | Sunlight readability contrast adjustments for field signage. |
| `app/src/main/java/id/bubakangreen/app/ui/scanner/CodeScannerHandler.kt` | POLISH | Enhance error callbacks for non-GMS devices. |

### 2.3 Files That Must NOT Change (Architecture Locked)
| File Path | Locked Reason |
|---|---|
| `app/src/main/AndroidManifest.xml` | **LOCKED**: Zero camera permission invariant. App links intent filters locked. |
| `firestore.rules` | **LOCKED**: Authoritative security rules enforcing `isAdmin()` writes and public read access. |
| `app/src/main/java/id/bubakangreen/app/core/auth/AuthSessionStorage.kt` | **LOCKED**: UI preview helper only; cannot grant backend authority. |
| `app/src/main/java/id/bubakangreen/app/domain/model/*` | **LOCKED**: Core domain models finalized (`MasterPlant`, `Location`, `LocationPlant`). |
| `app/src/main/java/id/bubakangreen/app/navigation/NavigationRoutes.kt` | **LOCKED**: Admin leftmost navigation order (`Admin \| Beranda \| Lokasi \| Katalog`) approved. |

---

## 3. Detailed Subphase Sequence (5A to 5L)

### Subphase 5A: Resolve Canonical Firestore Collection
* **Objective**: Eliminate collection naming discrepancy between mobile (`plants`) and web (`master_plants`).
* **Files**: `FirestorePlantRepository.kt`, `seed_default_catalog.py`.
* **Dependencies**: None.
* **Inputs**: Existing repository code.
* **Outputs**: Single canonical collection `master_plants`.
* **Acceptance Criteria**: Both mobile repository and web fallback query `/master_plants`.
* **Test**: `.\gradlew.bat testDebugUnitTest --no-daemon` passes 100%.
* **Rollback**: Revert `FirestorePlantRepository.kt` to commit `a02a374`.

### Subphase 5B: Stable ID & Canonical URL Contract
* **Objective**: Generate and validate immutable resource URLs.
* **Files**: `QrUrlBuilder.kt`, `QrUrlBuilderTest.kt`.
* **Dependencies**: 5A.
* **Inputs**: Resource stable ID (`pl-xxxxxxxx`, `loc-xxxxxxxx`).
* **Outputs**: `https://bubakangreen.web.app/plant/<id>` and `/location/<id>`.
* **Acceptance Criteria**: Strict regex `^[a-zA-Z0-9_-]+$`, HTTPS enforcement, rejection of invalid schemes.
* **Test**: `QrUrlBuilderTest` unit test suite passes 100%.
* **Rollback**: Git checkout `QrUrlBuilder.kt`.

### Subphase 5C: QR Generation & Display Dialog
* **Objective**: Render high-contrast QR codes in-app for published plants and locations.
* **Files**: `QrCodeGenerator.kt`, `QrCodeDisplayDialog.kt`, `QrCodeGeneratorTest.kt`.
* **Dependencies**: 5B.
* **Inputs**: Published botanical or garden entity.
* **Outputs**: On-screen QR dialog with Share and Copy URL actions.
* **Acceptance Criteria**: QR generated ONLY if `isPublished == true` and `status in {PUBLISHED, ACTIVE}`.
* **Test**: `QrCodeGeneratorTest` unit test suite.
* **Rollback**: Revert `QrCodeDisplayDialog.kt`.

### Subphase 5D: In-App Google Code Scanner
* **Objective**: Launch out-of-process QR code scanner without host camera permission.
* **Files**: `CodeScannerHandler.kt`, `build.gradle.kts`.
* **Dependencies**: `play-services-code-scanner:16.1.0`.
* **Inputs**: Tap on "Scan QR" button.
* **Outputs**: Scanner UI handled by Google Play Services; raw barcode string returned.
* **Acceptance Criteria**: Zero `android.permission.CAMERA` requested by host app. Clean cancellation handling.
* **Test**: Manual scanner launch and cancellation test on emulator/device.
* **Rollback**: Revert `CodeScannerHandler.kt`.

### Subphase 5E: Scan Result Validation & Security Gate
* **Objective**: Sanitize and validate scanned payloads.
* **Files**: `CodeScannerHandler.kt`, `QrUrlBuilder.kt`.
* **Dependencies**: 5B, 5D.
* **Inputs**: Raw barcode string from scanner.
* **Outputs**: `ParsedQrResult.Plant`, `ParsedQrResult.Location`, or `ParsedQrResult.Invalid`.
* **Acceptance Criteria**: Immediate rejection of foreign domains, insecure HTTP, javascript:, or non-canonical paths.
* **Test**: `QrUrlBuilderTest.parseCanonicalUrl_*`.
* **Rollback**: Restore validator logic.

### Subphase 5F: Deep-Link Routing & Back Stack
* **Objective**: Route validated deep-link URLs to appropriate detail screens.
* **Files**: `MainActivity.kt`, `BubakanNavHost.kt`.
* **Dependencies**: 5E.
* **Inputs**: Scanned QR result or external VIEW intent.
* **Outputs**: Direct navigation to `PlantDetailScreen` or `LocationDetailScreen`.
* **Acceptance Criteria**: Cold start directly opens detail; Back button smoothly navigates to `HomeScreen` if backstack is empty.
* **Test**: `adb shell am start -W -a android.intent.action.VIEW -d "https://bubakangreen.web.app/plant/pl-cabai-rawit"`.
* **Rollback**: Restore `BubakanNavHost.kt`.

### Subphase 5G: Android App Links Integration
* **Objective**: Configure manifest intent filters for auto-verification.
* **Files**: `AndroidManifest.xml`.
* **Dependencies**: None.
* **Inputs**: App Links domain configuration.
* **Outputs**: Verified domain association in Android OS.
* **Acceptance Criteria**: Intent filter includes `autoVerify="true"`, `scheme="https"`, `host="bubakangreen.web.app"`.
* **Test**: Manifest compilation check.
* **Rollback**: Revert `AndroidManifest.xml`.

### Subphase 5H: Digital Asset Links Deployment
* **Objective**: Host `assetlinks.json` on canonical Firebase Hosting domain.
* **Files**: `web/public/.well-known/assetlinks.json`.
* **Dependencies**: 5G.
* **Inputs**: Keystore SHA-256 fingerprint.
* **Outputs**: `https://bubakangreen.web.app/.well-known/assetlinks.json` returns HTTP 200 with JSON.
* **Acceptance Criteria**: Debug fingerprint verified (`approved` on emulator); release fingerprint cleanly gated until release keystore exists.
* **Test**: `adb shell pm get-app-links id.bubakangreen.app.debug`.
* **Rollback**: Restore previous `assetlinks.json`.

### Subphase 5I: Web Fallback Polishing & Status Gate
* **Objective**: Provide lightweight fallback card for visitors without the mobile app.
* **Files**: `web/public/plant.html`, `web/public/location.html`, `web/public/style.css`.
* **Dependencies**: 5A.
* **Inputs**: HTTP request to `/plant/<id>` or `/location/<id>`.
* **Outputs**: Responsive botanical/garden card with mascot branding and CTAs.
* **Acceptance Criteria**: Resolves same Firestore `/master_plants` document; displays friendly Not Found on missing IDs.
* **Test**: Browser testing across 360px, 393px, 412px, and desktop viewports.
* **Rollback**: Git restore `web/public/*.html`.

### Subphase 5J: APK Download Flow & Installation UX
* **Objective**: Provide honest APK download gateway and step-by-step Android installation guide.
* **Files**: `web/public/index.html`.
* **Dependencies**: None.
* **Inputs**: Web visitor on homepage.
* **Outputs**: Clear download CTA and 4-step sideloading instructions.
* **Acceptance Criteria**: Zero claims of silent auto-installation; clear preview/development badge.
* **Test**: Manual navigation to download section on mobile browser.
* **Rollback**: Restore `index.html`.

### Subphase 5K: Realtime Data Synchronization Verification
* **Objective**: Verify that Admin edits propagate to public user devices dynamically.
* **Files**: `FirestorePlantRepository.kt`, `FirestoreLocationRepository.kt`.
* **Dependencies**: 5A.
* **Inputs**: Admin updates botanical description or garden condition.
* **Outputs**: Realtime recomposition on public client screens via `.snapshots()`.
* **Acceptance Criteria**: Description updates visible without app restart; offline banner active when network is lost.
* **Test**: Realtime emulator listener test and offline cache validation.
* **Rollback**: N/A (Repository uses built-in Firestore SDK Flow).

### Subphase 5L: End-to-End QA & Physical Field Gate
* **Objective**: Execute all 12 test cases in the Phase 5 Test Matrix.
* **Files**: Full codebase.
* **Dependencies**: 5A to 5K.
* **Inputs**: Automated tests, emulator verification, and physical QR prints.
* **Outputs**: Final QA sign-off report.
* **Acceptance Criteria**: 100% test pass rate, zero layout clipping, zero fatal crashes.
* **Test**: Execute `PHASE_5_TEST_MATRIX.md`.
* **Rollback**: Fix identified issues or revert specific subphase.
