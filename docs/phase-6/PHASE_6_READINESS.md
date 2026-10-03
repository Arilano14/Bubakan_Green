# Phase 6 Final Readiness Gate & Approval Submission — Bubakan Green

**Product**: BUBAKAN GREEN (Sistem Informasi Urban Farming & Taman Toga Kelurahan Bubakan)  
**Status**: AUDITED & PLANNED — STRICT STOP BEFORE EXECUTION  
**Approval Trigger**: `ACC PHASE 6`  

---

## 1. File Change Control Matrix (Section 46)

| Change Category | Target Files & Directories | Purpose / Scope |
|---|---|---|
| **MUST CHANGE** | `app/src/main/assets/map/` (`leaflet.js`, `leaflet.css`, `map_template.html`) | Local, offline-ready Leaflet map engine assets (~55 KB total). |
| **MUST CHANGE** | `app/src/main/java/id/bubakangreen/app/ui/components/BubakanMapView.kt` | New Jetpack Compose Map component wrapping WebView with bidirectional JS bridge. |
| **MUST CHANGE** | `app/src/main/java/id/bubakangreen/app/ui/locations/LocationsScreen.kt` | Replaces placeholder `MapVisualContainer` with interactive `BubakanMapView`. |
| **MUST CHANGE** | `app/src/main/java/id/bubakangreen/app/ui/home/HomeViewModel.kt` | Adds `GardenSummary` computation (Total, Urban Farming, Taman Toga counts). |
| **MUST CHANGE** | `app/src/main/java/id/bubakangreen/app/ui/home/HomeScreen.kt` | Adds "Rekapitulasi Kebun Bubakan" metrics and discovery mini-map. |
| **MAY CHANGE** | `app/src/main/java/id/bubakangreen/app/ui/locations/LocationsViewModel.kt` | Minor state refinement for marker selection and category synchronization. |
| **MAY CHANGE** | `app/src/main/java/id/bubakangreen/app/ui/components/QrCodeDisplayDialog.kt` | Adds link sharing intent and print label generator shortcut. |
| **MUST NOT CHANGE** | `app/src/main/java/id/bubakangreen/app/domain/model/Location.kt` | Core domain entity must remain stable. |
| **MUST NOT CHANGE** | `app/src/main/java/id/bubakangreen/app/domain/model/MasterPlant.kt` | Botanical encyclopedia schema remains unchanged. |
| **MUST NOT CHANGE** | `app/src/main/java/id/bubakangreen/app/core/util/QrUrlBuilder.kt` | Canonical URL contract (`https://bubakangreen.web.app/...`) must remain immutable. |
| **MUST NOT CHANGE** | `app/src/main/AndroidManifest.xml` (Permissions) | ZERO CAMERA PERMISSION rule remains strictly enforced. |

---

## 2. No False Pass & Honest Reporting Rules (Section 50)

In strict adherence to project principles:
1. **QR Status**:
   - `QrUrlBuilder` and `QrCodeGenerator` are **VERIFIED** via unit tests and emulator UI.
   - In-app Google Code Scanner is **TESTED** on emulator.
   - Physical hardware stickers on garden stakes are reported honestly as **`NOT TESTED ON PHYSICAL HARDWARE`**.
2. **App Links Status**:
   - Debug App Links are **`VERIFIED`** (`approved` on Android 14 emulator).
   - Production Release App Links are reported honestly as **`BLOCKED FOR PRODUCTION VERIFICATION`** pending release keystore creation.
3. **Web Fallback Status**:
   - HTML files and rewrites are **`VERIFIED`** in local codebase.
   - Live domain `bubakangreen.web.app` returns HTTP 404 and is reported honestly as **`BLOCKED`** pending project owner execution of `firebase deploy`.
4. **Map Status**:
   - Current codebase renders a list placeholder for `Peta Sebaran`. It is reported honestly as **`AWAITING IMPLEMENTATION`**.

---

## 3. Strict Pre-Execution Stop Statement (Section 51)

All audits, URL tests, data model inspections, technology trade-offs, and implementation plans for Phase 6 are complete.

**IN COMPLIANCE WITH SECTION 51, ALL AUTOMATED IMPLEMENTATION IS CURRENTLY HALTED.**

The system is ready for immediate execution upon receiving:
```
ACC PHASE 6
```
