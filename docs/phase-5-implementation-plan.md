# Phase 5 — Comprehensive Implementation Plan

**Product**: Bubakan Green — Sistem Informasi Urban Farming & Taman Toga Kelurahan Bubakan  
**Package Release**: `id.bubakangreen.app`  
**Package Debug**: `id.bubakangreen.app.debug`  
**Canonical Domain**: `https://bubakangreen.web.app`  
**Document Status**: PHASE 5 PLANNING (STOPPED & AWAITING `ACC PHASE 5`)  

---

## 1. Current Repository State

- **Branch**: `main` (clean working directory, commit `9931756`).
- **Phase 4 Navigation Remediation**: Bottom navigation order successfully aligned to `ADMIN | BERANDA | LOKASI | KATALOG` (Admin leftmost) per Phase 4 Gate requirements.
- **Gradle Build**: `assembleDebug` builds cleanly (BUILD SUCCESSFUL in 1m 06s, zero warnings/errors).
- **Unit Test Suite**: 47 unit tests pass (100% success rate, duration 1.1s).
- **Android Manifest**: Intent filters for App Links with `autoVerify="true"` and path prefixes `/plant/` & `/location/` already defined.
- **Firebase Security Rules**: Active in `firestore.rules` and `web/firestore.rules`. Strictly guards write operations behind `isAdmin()` using token claims or `/users/{uid}` lookup. Public reads allowed only for active/published resources.

---

## 2. Phase 4 Verification Evidence

| Evidence Item | Command / Source | Result / State |
|---|---|---|
| **Compilation** | `.\gradlew.bat assembleDebug --no-daemon` | `BUILD SUCCESSFUL in 1m 06s` (35 actionable tasks) |
| **Test Suite** | `.\gradlew.bat testDebugUnitTest --no-daemon` | `47 tests, 0 failures, 0 ignored, 100% successful` |
| **Navigation Order** | `NavigationRoutes.kt:70-95` & `AppBottomBar.kt` | `AdminDashboard` (pos 1), `Home` (pos 2), `Locations` (pos 3), `Catalog` (pos 4) |
| **Touch Targets** | `AppBottomBar.kt:127-137` | Height 64dp, interactive area >= 48dp (Fitts's Law compliant) |
| **Data Integrity** | `MasterPlant.kt`, `Location.kt`, `LocationPlant.kt` | Clean relational boundary: Master species separated from garden plot plantings |
| **Security Rules** | `firestore.rules:1-76` | Zero open writes; no `allow read, write: if true;` found |

---

## 3. Phase 5 Objectives

Deliver an end-to-end physical-to-digital QR and deep-linking system:
1. **Physical Signage**: Display physical QR codes in Kelurahan Bubakan garden plots.
2. **Canonical HTTPS URL**: Store solely `https://bubakangreen.web.app/plant/<id>` or `/location/<id>`.
3. **Smart Resolution**:
   - If app is installed and App Links verified: Open native Compose screen (`PlantDetailScreen` or `LocationDetailScreen`).
   - If app is absent or opened via desktop: Open web fallback on Firebase Hosting with botanical summary, mascot, and direct APK download CTA.
4. **In-App Scanner**: Embed Google Code Scanner (Play Services) for fast camera-permissionless scanning inside the app.
5. **Real-time Synchronization**: A single Firestore document updates both App and Web views without re-generating or re-printing QR codes.

---

## 4. QR Data Contract

- **Forbidden Payload**: Full JSON objects, botanical descriptions, base64 image strings, Firestore document snapshots, tokens, or dynamic query parameters.
- **Allowed Payload**: Solely a single valid canonical HTTPS URL string.
- **Format**:
  - Plant: `https://bubakangreen.web.app/plant/<stable-id>`
  - Location: `https://bubakangreen.web.app/location/<stable-id>`
- **Generator**: In-app QR generation (using ZXing core or Google Code Scanner / Vector renderer) takes only the stable URL and outputs an SVG/Bitmap for display or sharing by Kelurahan administrators.

---

## 5. Stable ID Contract

- **Format**: Opaque identifier prefixed with entity type:
  - Plant: `pl-[a-z0-9]{8}` (or existing alphanumeric Firestore doc ID, e.g. `pl-a84f21c9`).
  - Location: `loc-[a-z0-9]{8}` (or existing alphanumeric Firestore doc ID, e.g. `loc-91af7b23`).
- **Independence**: Never derived from human-language names (e.g. not `/plant/jahe-merah`). Renaming a plant in Kelurahan Bubakan will NOT break existing printed signs.
- **Immutability**: Once assigned to a garden plot or plant, the ID is immutable.

---

## 6. URL Contract

- **Scheme**: `https`
- **Host**: `bubakangreen.web.app` (canonical domain).
- **Paths**:
  - `/plant/{id}` -> Maps to MasterPlant detail.
  - `/location/{id}` -> Maps to Location garden plot detail.
- **Disallowed**: Dynamic query parameter state bypasses (e.g., `/plant?name=Jahe&desc=...` is prohibited).

---

## 7. Firestore Integration

- **Master Plants Collection**: `/master_plants/{plantId}` or `/plants/{plantId}`.
- **Locations Collection**: `/locations/{locationId}`.
- **Junction Collection**: `/location_plants/{locationPlantId}` linking location to master plant.
- **Single Source of Truth**: Both Android (`FirestorePlantRepository`) and Web Fallback (`web/public/plant.html` Firestore JS SDK) query the same Firestore collections using the stable identifier.

---

## 8. Scanner Architecture

- **Primary Scanner**: **Google Code Scanner** (`com.google.android.gms:play-services-code-scanner`).
- **Rationale**:
  - Does NOT require `android.permission.CAMERA` in the application manifest.
  - Google Play Services manages camera hardware, autofocus, and scanning UI.
  - Minimal APK footprint; models are downloaded and managed by Play Services.
  - Supports standard QR codes and barcodes out-of-the-box.
- **Fallback / Secondary**: If Google Play Services is unavailable on a specific device, deep links through the system camera or external scanner still function seamlessly via App Links.

---

## 9. Permission Architecture

| Capability | Permission Required | Notes |
|---|---|---|
| In-App Google Code Scanner | **NONE** (`CAMERA` not needed) | Scanner UI and camera lifecycle hosted by Play Services |
| App Links (Incoming) | **NONE** | Standard Android intent resolution |
| Web Fallback Opening | **NONE** | System browser handling |
| Network Requests | `INTERNET`, `ACCESS_NETWORK_STATE` | Already declared in manifest |
| PIC Location Geotagging | `ACCESS_FINE_LOCATION`, `ACCESS_COARSE_LOCATION` | Already declared for Phase 3/4 PIC form |

---

## 10. App Links Architecture

- **Intent Filter**: Declared on `MainActivity` in `AndroidManifest.xml`:
  ```xml
  <intent-filter android:autoVerify="true">
      <action android:name="android.intent.action.VIEW" />
      <category android:name="android.intent.category.DEFAULT" />
      <category android:name="android.intent.category.BROWSABLE" />
      <data android:scheme="https" android:host="bubakangreen.web.app" android:pathPrefix="/plant/" />
      <data android:scheme="https" android:host="bubakangreen.web.app" android:pathPrefix="/location/" />
  </intent-filter>
  ```
- **Lifecycle Dispatch**: Handled in `MainActivity.onCreate()` and `onNewIntent()` passing URI to NavController.

---

## 11. assetlinks.json Deployment

- **Hosting Location**: `https://bubakangreen.web.app/.well-known/assetlinks.json`.
- **Headers**: Must be served with `Content-Type: application/json` (configured in `web/firebase.json`).
- **Current State**: Contains placeholder `"REPLACE_WITH_RELEASE_OR_DEBUG_KEYSTORE_SHA256_FINGERPRINT"`.
- **Execution Plan**:
  1. Extract debug keystore SHA-256 via keytool for debug build testing.
  2. Obtain release keystore SHA-256 for production signing.
  3. Deploy updated `assetlinks.json` via Firebase Hosting.

---

## 12. Web Fallback Architecture

- **Static Hosting**: Firebase Hosting serving static HTML/CSS/JS from `web/public`.
- **Rewrites in `firebase.json`**:
  - `/plant/**` -> `/plant.html`
  - `/location/**` -> `/location.html`
- **Design Aesthetic**:
  - Retains Bubakan Green botanical identity (`#2D5A27`, `#689F38`, warm cream background `#F9FAF6`).
  - Displays mascot illustration and encouraging educational tone.
  - Clean card displaying plant/location info dynamically fetched from Firestore.
  - Prominent "Buka di Aplikasi" and "Download APK" action buttons.

---

## 13. APK Download Architecture

- **Hosting Target**: Direct link to the latest compiled release APK (e.g., hosted on Firebase Hosting `/downloads/bubakangreen.apk` or official release bucket).
- **User Guidance on Web**:
  1. Klik tombol **Download APK**.
  2. Buka file yang telah diunduh di perangkat Android Anda.
  3. Ikuti petunjuk instalasi sistem Android.
  4. Buka aplikasi **Bubakan Green**.
- **Honesty Principle**: No claims of "automatic silent installation". Clear guidance for Android unknown source/package installer prompts.

---

## 14. Deep-Link Routing Logic

```kotlin
// Inside NavHost / DeepLink parser
fun handleDeepLink(uri: Uri, navController: NavController) {
    val pathSegments = uri.pathSegments ?: return
    if (pathSegments.size >= 2) {
        val resourceType = pathSegments[0]
        val resourceId = pathSegments[1]
        when (resourceType) {
            "plant" -> navController.navigate(Screen.PlantDetail.createRoute(resourceId))
            "location" -> navController.navigate(Screen.LocationDetail.createRoute(resourceId))
            else -> navController.navigate(Screen.Home.route)
        }
    }
}
```

---

## 15. Error Handling & State UI

1. **NOT FOUND**: Resource ID not found in Firestore.
   - UI: "Data tanaman atau lokasi tidak ditemukan. Pastikan barcode yang dipindai adalah kode resmi Bubakan Green."
2. **UNAVAILABLE / UNPUBLISHED**: Resource exists but `isPublished == false` or `status != ACTIVE/PUBLISHED`.
   - UI: "Informasi tanaman/lokasi ini sedang tidak tersedia atau dalam proses verifikasi petugas."
3. **NETWORK ERROR**: No connection to resolve Firestore record.
   - UI: "Periksa koneksi internet Anda dan coba lagi." + Tombol Coba Lagi.
4. **MALFORMED QR**: QR code scanned is not a valid Bubakan Green URL.
   - UI: "Format kode QR tidak dikenali."

---

## 16. Test Matrix Reference

Detailed in [phase-5-test-matrix.md](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/docs/phase-5-test-matrix.md):
- TEST-01 to TEST-12 covering installed app routing, web fallback, offline handling, resource update propagation, responsive screen checks, and scanner lifecycle.

---

## 17. Device & Emulator Testing Procedure

1. **Emulator / Physical Device Setup**:
   - Ensure Google Play Services is available for Google Code Scanner.
   - Install APK: `adb install -r app/build/outputs/apk/debug/app-debug.apk`.
2. **App Links Verification Check**:
   - Command: `adb shell pm get-app-links id.bubakangreen.app.debug`
   - Verify domain status: `bubakangreen.web.app: verified` (or `1024` / selected by user).
3. **Intent Injection Test**:
   - Command: `adb shell am start -a android.intent.action.VIEW -d "https://bubakangreen.web.app/plant/pl-test123" id.bubakangreen.app.debug`
   - Observe screen transitions and logcat output.

---

## 18. Risks & Mitigations

| Risk | Impact | Mitigation Strategy |
|---|---|---|
| Google Play Services missing on barebones AOSP emulator | Scanner launch fails | Fallback gracefully with dialog prompting manual entry or external camera scan |
| App Links verification delayed by OS | Browser opens instead of app | Web fallback provides direct "Buka di Aplikasi" intent link |
| Keystore SHA-256 fingerprint mismatch | App Links autoVerify fails | Validate SHA-256 using `keytool -list -v -keystore` before deploying `assetlinks.json` |

---

## 19. Blockers & Pre-Conditions

1. **RELEASE KEYSTORE SHA-256**:
   - `web/public/.well-known/assetlinks.json` currently contains a placeholder.
   - **Status**: `BLOCKED FOR VERIFIED APP LINKS DEPLOYMENT` until actual release certificate or debug keystore SHA-256 is generated/supplied.
2. **APPROVAL GATE**:
   - Phase 5 coding cannot begin until explicit user command: `ACC PHASE 5`.

---

## 20. Files Expected to Change in Phase 5

1. `app/build.gradle.kts`: Add Play Services Code Scanner dependency (`com.google.android.gms:play-services-code-scanner:16.1.0`).
2. `gradle/libs.versions.toml`: Add version and library alias for code scanner.
3. `app/src/main/java/id/bubakangreen/app/MainActivity.kt`: Enhance intent handling for cold start & background deep links.
4. `app/src/main/java/id/bubakangreen/app/ui/scanner/`: Create in-app scanner launcher & state handler.
5. `app/src/main/java/id/bubakangreen/app/ui/admin/` & `pic/`: Add QR display/sharing dialog for published plants/locations.
6. `web/public/.well-known/assetlinks.json`: Update with actual keystore SHA-256.
7. `web/public/plant.html` & `location.html`: Polish smart fallback with mascot, details, and APK download link.

---

## 21. Files Expected NOT to Change

1. `app/src/main/java/id/bubakangreen/app/domain/model/*`: Domain entities (`MasterPlant`, `Location`, `LocationPlant`) are stable and satisfy all Phase 5 contracts.
2. `firestore.rules`: Existing security rules already properly enforce public read vs admin write.
3. `app/src/main/java/id/bubakangreen/app/ui/navigation/AppBottomBar.kt`: Bottom navigation layout and 4-item structure are complete.
4. Core theme and colors (`app/src/main/java/id/bubakangreen/app/ui/theme/*`): Visual tokens remain intact.

---

## 22. Acceptance Criteria

Phase 5 is declared **IMPLEMENTED** when:
- [ ] QR code encodes solely valid canonical HTTPS URL.
- [ ] Google Code Scanner functions without camera permission in manifest.
- [ ] Deep-link parser routes `/plant/{id}` and `/location/{id}` to correct detail screens.
- [ ] Web fallback renders botanical/location details and APK download CTA.
- [ ] assetlinks.json contains real SHA-256 certificate fingerprint.
- [ ] Invalid and unpublished resource IDs produce clean non-crashing error states.

Phase 5 is declared **VERIFIED** when:
- [ ] All 12 tests in `phase-5-test-matrix.md` produce verified PASS evidence on device/emulator.

---

## 23. Rollback Strategy

If Phase 5 changes introduce instability:
1. Git revert Phase 5 implementation commit: `git revert HEAD`.
2. Clean and rebuild: `.\gradlew.bat clean assembleDebug --no-daemon`.
3. Verify test suite: `.\gradlew.bat testDebugUnitTest --no-daemon`.
4. Restore `assetlinks.json` and web fallback files if Firebase Hosting deployment fails.
