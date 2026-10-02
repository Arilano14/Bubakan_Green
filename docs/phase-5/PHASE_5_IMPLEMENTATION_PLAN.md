# Phase 5 Implementation Plan (Master Architecture Specification)

**Product**: Bubakan Green — Sistem Informasi Urban Farming & Taman Toga Kelurahan Bubakan  
**Package Debug**: `id.bubakangreen.app.debug`  
**Package Release**: `id.bubakangreen.app`  
**Canonical Domain**: `https://bubakangreen.web.app`  
**Phase State**: PHASE 5 PLANNED (AWAITING `ACC PHASE 5`)  

---

## 1. Scope Control & Boundaries

### 1.1 In-Scope Components (Phase 5 Only)
1. **QR & Canonical URL Contract**: Deterministic generation of URLs pointing strictly to `https://bubakangreen.web.app/plant/<id>` and `/location/<id>`.
2. **Stable Resource Identity**: `pl-xxxxxxxx` and `loc-xxxxxxxx` opaque immutable identifiers.
3. **In-App Google Code Scanner**: Integration of `play-services-code-scanner:16.1.0` with zero camera permissions declared in the host app manifest.
4. **Android App Links**: Manifest intent-filter, deep-link dispatching, and `assetlinks.json` verification handling.
5. **Web Fallback on Firebase Hosting**: `plant.html` and `location.html` smart product pitch, mascot, and direct APK download CTA.
6. **Data Consistency**: Strict Single Source of Truth via direct Firestore document lookups without duplicate databases.
7. **Graceful Error Handling**: Dedicated UI states for Not Found, Unavailable/Unpublished, and Offline Network errors.

### 1.2 Prohibited Features (Strictly Out of Scope)
- ❌ NO AI chatbots or external conversational agents.
- ❌ NO IoT or sensor integrations.
- ❌ NO background continuous GPS tracking.
- ❌ NO payment gateways or monetization frameworks.
- ❌ NO secondary backend servers (Node.js, Express, FastAPI, Django). Firebase Hosting & Cloud Firestore are sufficient.
- ❌ NO web admin management dashboards.

---

## 2. Implementation Subphases

The implementation will execute in strict sequence following approval:

* **Subphase 5A — Stable ID & Canonical URL Builder**:
  * Finalize `QrUrlBuilder.kt` with validation and domain consistency checks.
  * Comprehensive unit tests for URL generation.
* **Subphase 5B — QR Generation & Admin Sharing**:
  * In-app QR visual generation (SVG/Vector/Bitmap) for published plants and locations.
  * Admin / PIC dialog to view, copy URL, and share canonical QR.
* **Subphase 5C — In-App Google Code Scanner**:
  * Add `com.google.android.gms:play-services-code-scanner` dependency.
  * Zero camera permission in `AndroidManifest.xml`.
  * Safe scan result parser verifying canonical scheme and host.
* **Subphase 5D — Deep-Link Routing**:
  * Enhance `MainActivity.kt` and `BubakanNavHost.kt` to parse incoming deep links on cold start and warm start.
  * Graceful handling for unknown or unpublished IDs.
* **Subphase 5E — Android App Links Verification**:
  * Validate intent filters with `autoVerify="true"`.
  * Test domain verification using `adb shell pm get-app-links`.
* **Subphase 5F — Digital Asset Links Deployment**:
  * Extract genuine SHA-256 fingerprint from keystore.
  * Update `web/public/.well-known/assetlinks.json`.
* **Subphase 5G — Web Fallback Polishing**:
  * Ensure responsive layouts at 360px, 393px, 412px, and desktop.
  * Display botanical summary, RW details, mascot, and "Buka di Aplikasi" button.
* **Subphase 5H — APK Download Gateway**:
  * Real APK download URL with step-by-step Android installation guidance.
* **Subphase 5I — End-to-End Automated & Emulator Testing**:
  * Execute all 12 test cases in the Phase 5 Test Matrix.
* **Subphase 5J — Physical Field Verification (Post-Deployment)**:
  * Verification across distances (15cm, 30cm, 50cm), angles (0°-45°), and real garden lighting.

---

## 3. File Change Control Matrix

| File Path | Planned Action | Detailed Justification |
|---|---|---|
| `app/build.gradle.kts` | MODIFY | Add Google Code Scanner dependency. |
| `gradle/libs.versions.toml` | MODIFY | Add version and library alias for code scanner. |
| `app/src/main/java/id/bubakangreen/app/MainActivity.kt` | MODIFY | Add intent filter handling in `onCreate()` and `onNewIntent()`. |
| `app/src/main/java/id/bubakangreen/app/navigation/BubakanNavHost.kt` | MODIFY | Add scanner navigation and deep-link route resolution. |
| `app/src/main/java/id/bubakangreen/app/ui/scanner/` | CREATE | Launcher & UI state handler for Google Code Scanner. |
| `web/public/.well-known/assetlinks.json` | MODIFY | Replace placeholder with genuine keystore SHA-256. |
| `web/public/plant.html` | POLISH | Refine fallback botanical card and APK download link. |
| `web/public/location.html` | POLISH | Refine fallback location card and APK download link. |
| `app/src/main/java/id/bubakangreen/app/domain/model/*` | **DO NOT CHANGE** | Domain entities are finalized and stable. |
| `firestore.rules` | **DO NOT CHANGE** | Security rules already strictly guard access. |
| `app/src/main/java/id/bubakangreen/app/ui/theme/*` | **DO NOT CHANGE** | Botanical theme tokens are locked. |
| `app/src/main/java/id/bubakangreen/app/navigation/NavigationRoutes.kt` | **DO NOT CHANGE** | Admin-leftmost order verified. |

---

## 4. Acceptance Criteria

* **PHASE 5 IMPLEMENTED**:
  - [ ] Canonical QR URL generated solely from stable resource ID.
  - [ ] Google Code Scanner operational without camera permissions in manifest.
  - [ ] Deep-link parsing and navigation verified on cold & warm starts.
  - [ ] Web fallback resolves the identical Firestore data.
  - [ ] Invalid / unpublished IDs handled with friendly error screens.
* **PHASE 5 TESTED**:
  - [ ] Automated unit test suite passes 100%.
  - [ ] Full deep-link and scanner interaction tested on emulator.
* **PHASE 5 VERIFIED**:
  - [ ] Domain association verified via `pm get-app-links` using real signing certificate.
  - [ ] Installed and uninstalled scan flows verified.
  - [ ] Dynamic update test verified (same QR $\rightarrow$ new content).
  - [ ] Physical QR print field test completed.
