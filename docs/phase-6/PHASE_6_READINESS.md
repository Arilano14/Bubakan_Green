# Phase 6 Final Readiness Gate & Approval Submission — Bubakan Green

**Product**: BUBAKAN GREEN (Sistem Informasi Urban Farming & Taman Toga Kelurahan Bubakan)  
**Status**: IMPLEMENTED & EMPIRICALLY VERIFIED ON EMULATOR (PHASE 6 COMPLETE)  
**Approval Trigger Received**: `ACC PHASE 6` (Executed & Verified)  

---

## 1. File Change Control Matrix (Completed Execution)

| Change Category | Target Files & Directories | Purpose / Scope | Verification Status |
|---|---|---|---|
| **CHANGED** | `app/src/main/assets/map/` (`leaflet.js`, `leaflet.css`, `map_template.html`, `bubakan_boundary.geojson`) | Local Leaflet 1.9.4 engine + official 182-vertex Kelurahan Bubakan boundary GeoJSON from Pemkot Semarang geoportal. | **`VERIFIED`** (Rendered via `L.geoJSON`, initial camera locked to Bubakan bounds) |
| **CHANGED** | `app/src/main/java/id/bubakangreen/app/core/util/BubakanGeoValidator.kt` | New Jordan Curve (Ray-Casting) point-in-polygon validator enforcing strict Bubakan administrative boundaries. | **`VERIFIED`** (`BubakanGeoValidatorTest` passing with 0 errors) |
| **CHANGED** | `app/src/main/java/id/bubakangreen/app/ui/pic/LocationFormViewModel.kt` | Enforces geospatial boundary validation on GPS capture and location creation. | **`VERIFIED`** (`LocationFormViewModelTest` rejects out-of-boundary coordinates) |
| **CHANGED** | `app/src/main/java/id/bubakangreen/app/ui/components/BubakanMapView.kt` | Jetpack Compose Map component filtering public markers strictly inside Bubakan boundary. | **`VERIFIED`** (`mixedContentMode`, `destroy()` on dispose, PIP filter) |
| **CHANGED** | `app/src/main/java/id/bubakangreen/app/ui/locations/LocationsScreen.kt` | Interactive boundary-locked map with top floating legend and bottom garden preview card. | **`VERIFIED`** (`test_peta_sebaran_boundary.png`, `test_tap_amber_pin.png`) |
| **CHANGED** | `app/src/main/java/id/bubakangreen/app/ui/home/HomeViewModel.kt` | Adds dynamic `GardenSummary` calculation from published locations. | **`VERIFIED`** (3 total, 2 Urban Farm, 1 Taman Toga) |
| **CHANGED** | `app/src/main/java/id/bubakangreen/app/ui/home/HomeScreen.kt` | "Rekapitulasi Kebun Bubakan" badges and interactive discovery mini-map (190dp). | **`VERIFIED`** (`test_home_minimap_view.png`) |
| **UNCHANGED** | `app/src/main/java/id/bubakangreen/app/domain/model/Location.kt` | Core domain entity preserved without breaking changes. | **`INTACT`** |
| **UNCHANGED** | `app/src/main/java/id/bubakangreen/app/domain/model/MasterPlant.kt` | Botanical encyclopedia schema remains unchanged. | **`INTACT`** |
| **UNCHANGED** | `app/src/main/java/id/bubakangreen/app/core/util/QrUrlBuilder.kt` | Canonical URL contract (`https://bubakangreen.web.app/...`) preserved. | **`INTACT`** |
| **UNCHANGED** | `app/src/main/AndroidManifest.xml` (Permissions) | ZERO CAMERA PERMISSION & ZERO RUNTIME LOCATION PERMISSION rule strictly maintained. | **`INTACT`** |

---

## 2. No False Pass & Honest Reporting Rules (Post-Implementation Status)

In strict adherence to project principles:
1. **QR Status**:
   - `QrUrlBuilder` and `QrCodeGenerator` are **`VERIFIED`** via unit tests (`BUILD SUCCESSFUL`).
   - In-app Google Code Scanner is **`TESTED`** on emulator.
   - Physical hardware stickers on garden stakes remain reported honestly as **`NOT TESTED ON PHYSICAL HARDWARE`**.
2. **App Links Status**:
   - Debug App Links are **`VERIFIED`** (`approved` on Android 14 emulator).
   - Production Release App Links remain reported honestly as **`BLOCKED FOR PRODUCTION VERIFICATION`** pending release keystore creation.
3. **Web Fallback Status**:
   - HTML files and rewrites are **`VERIFIED`** in local codebase.
   - Live domain `bubakangreen.web.app` returns HTTP 404 and is reported honestly as **`BLOCKED`** pending project owner execution of `firebase deploy`.
4. **Map Status**:
   - Interactive Administrative Boundary-Locked Map is **`VERIFIED ON EMULATOR`** on Pixel 7 (Android 14 API 34).
   - Official administrative boundary polygon (FID: 16 from `dataspasial.semarangkota.go.id`) rendered with subtle dashed green border and 5% fill.
   - Camera view locked to Bubakan boundary with small visual padding (`fitBounds` + `maxBounds` with 20% pad).
   - Point-in-polygon validation enforced on location creation in `LocationFormViewModel` and on public map markers.

---

## 3. Empirical Verification Evidence

- `test_peta_sebaran_boundary.png`: Official Kelurahan Bubakan administrative boundary outline rendered on OpenStreetMap tiles with green/amber markers.
- `test_tap_amber_pin.png`: Marker selection halo and preview card for Taman Toga RW 03 inside Bubakan boundary.
- `test_detail_screen_success.png`: Direct transition from preview card CTA to `LocationDetailScreen`.
- `test_home_minimap_view.png`: Home dynamic recap badges and boundary-locked discovery mini-map.
- `testDebugUnitTest`: 22 tasks executed/up-to-date, 0 failures (including `BubakanGeoValidatorTest` and `LocationFormViewModelTest`).
- `app-debug.apk`: Built and installed on Pixel 7 emulator.
