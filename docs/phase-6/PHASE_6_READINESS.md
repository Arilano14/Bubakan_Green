# Phase 6 Final Readiness Gate & Approval Submission — Bubakan Green

**Product**: BUBAKAN GREEN (Sistem Informasi Urban Farming & Taman Toga Kelurahan Bubakan)  
**Status**: IMPLEMENTED & EMPIRICALLY VERIFIED ON EMULATOR (PHASE 6 COMPLETE)  
**Approval Trigger Received**: `ACC PHASE 6` (Executed & Verified)  

---

## 1. File Change Control Matrix (Completed Execution)

| Change Category | Target Files & Directories | Purpose / Scope | Verification Status |
|---|---|---|---|
| **CHANGED** | `app/src/main/assets/map/` (`leaflet.js`, `leaflet.css`, `map_template.html`) | Local Leaflet 1.9.4 map engine + CSS + custom HTML canvas (~167 KB uncompressed, ~48 KB in APK). | **`VERIFIED`** (Loaded via `file:///android_asset/map/map_template.html`) |
| **CHANGED** | `app/src/main/java/id/bubakangreen/app/ui/components/BubakanMapView.kt` | Jetpack Compose Map component wrapping WebView with bidirectional JS bridge. | **`VERIFIED`** (`mixedContentMode`, `destroy()` on dispose) |
| **CHANGED** | `app/src/main/java/id/bubakangreen/app/ui/locations/LocationsScreen.kt` | Interactive `BubakanMapView` with top floating legend and bottom garden preview card. | **`VERIFIED`** (`test_interactive_map.png`, `test_filter_toga.png`) |
| **CHANGED** | `app/src/main/java/id/bubakangreen/app/ui/home/HomeViewModel.kt` | Adds dynamic `GardenSummary` calculation from published locations. | **`VERIFIED`** (3 total, 2 Urban Farm, 1 Taman Toga) |
| **CHANGED** | `app/src/main/java/id/bubakangreen/app/ui/home/HomeScreen.kt` | "Rekapitulasi Kebun Bubakan" badges and interactive discovery mini-map (190dp). | **`VERIFIED`** (`test_home_view.png`) |
| **UNCHANGED** | `app/src/main/java/id/bubakangreen/app/domain/model/Location.kt` | Core domain entity preserved without breaking changes. | **`INTACT`** |
| **UNCHANGED** | `app/src/main/java/id/bubakangreen/app/domain/model/MasterPlant.kt` | Botanical encyclopedia schema remains unchanged. | **`INTACT`** |
| **UNCHANGED** | `app/src/main/java/id/bubakangreen/app/core/util/QrUrlBuilder.kt` | Canonical URL contract (`https://bubakangreen.web.app/...`) preserved. | **`INTACT`** |
| **UNCHANGED** | `app/src/main/AndroidManifest.xml` (Permissions) | ZERO CAMERA PERMISSION & ZERO RUNTIME LOCATION PERMISSION rule strictly maintained. | **`INTACT`** |

---

## 2. No False Pass & Honest Reporting Rules (Post-Implementation Status)

In strict adherence to project principles:
1. **QR Status**:
   - `QrUrlBuilder` and `QrCodeGenerator` are **`VERIFIED`** via unit tests (`BUILD SUCCESSFUL in 24s`).
   - In-app Google Code Scanner is **`TESTED`** on emulator.
   - Physical hardware stickers on garden stakes remain reported honestly as **`NOT TESTED ON PHYSICAL HARDWARE`**.
2. **App Links Status**:
   - Debug App Links are **`VERIFIED`** (`approved` on Android 14 emulator).
   - Production Release App Links remain reported honestly as **`BLOCKED FOR PRODUCTION VERIFICATION`** pending release keystore creation.
3. **Web Fallback Status**:
   - HTML files and rewrites are **`VERIFIED`** in local codebase.
   - Live domain `bubakangreen.web.app` returns HTTP 404 and is reported honestly as **`BLOCKED`** pending project owner execution of `firebase deploy`.
4. **Map Status**:
   - Interactive Leaflet Map is **`VERIFIED ON EMULATOR`** on Pixel 7 (Android 14 API 34).
   - Dynamic marker generation (`#2E7D32` Urban Farming, `#D97706` Taman Toga), filter synchronization, marker click centering, preview card display, and navigation to `LocationDetailScreen` are **`FULLY FUNCTIONAL`**.
   - Home recap metrics and discovery mini-map are **`FULLY FUNCTIONAL`**.

---

## 3. Empirical Verification Evidence

- `test_interactive_map.png`: Leaflet map tiles, dual-category pins, floating legend, and selected garden card.
- `test_filter_toga.png`: Filter chip isolation with single amber pin for Taman Toga.
- `test_location_detail_opened.png`: Preview card CTA navigation to detail screen.
- `test_home_view.png`: Home dynamic recap badges (3 Kebun, 2 Urban Farming, 1 Taman Toga) and mini-map.
- `testDebugUnitTest`: 22 tasks executed/up-to-date, 0 failures.
- `app-debug.apk`: Built and verified on device (size: 28.5 MB, asset overhead: ~48 KB).
