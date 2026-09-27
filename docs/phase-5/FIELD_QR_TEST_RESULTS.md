# BUBAKAN GREEN — FIELD QR TEST RESULTS
**Product:** BUBAKAN GREEN  
**Sub-title:** Sistem Informasi Urban Farming & Taman Toga Kelurahan Bubakan  
**Phase:** Phase 5 — QR & Field Integration  
**Governance:** Zero fabricated test data. Only actual hardware test sessions may be recorded as verified. Unexecuted physical tests remain UNKNOWN.  
**Date:** 2026-09-27  

---

## 1. Field Testing Governance & Log

This log captures real-world physical verification results for deployed QR codes across Kelurahan Bubakan.

- **Status Definitions:**
  - `TEST_PENDING`: Target identified and mapped; physical printing/testing in progress.
  - `VERIFIED`: Completed on physical hardware across test devices with pass criteria met.
  - `FAILED`: Scan, App Link, or data resolution failed; under remediation.
  - `UNKNOWN`: Field test not yet conducted on physical hardware.

---

## 2. Field Test Results Log

| QR ID | Resource | Physical Location | Device | OS | App Installed? | Scan Result | App Link Result | Web Fallback Result | Displayed Resource Correct? | Image Correct? | Content Correct? | Notes | Final Status |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| `QR-PL-001` | `pl-jahe-merah` | Kebun Toga RW 03 Bubakan | UNKNOWN | UNKNOWN | YES | UNKNOWN | UNKNOWN | N/A | UNKNOWN | UNKNOWN | UNKNOWN | Awaiting physical sticker production & deployment | `TEST_PENDING` |
| `QR-PL-001-WEB` | `pl-jahe-merah` | Kebun Toga RW 03 Bubakan | UNKNOWN | UNKNOWN | NO | UNKNOWN | N/A | UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN | Awaiting physical sticker production & deployment | `TEST_PENDING` |
| `QR-PL-002` | `pl-kumis-kucing` | Kebun Toga RW 03 Bubakan | UNKNOWN | UNKNOWN | YES | UNKNOWN | UNKNOWN | N/A | UNKNOWN | UNKNOWN | UNKNOWN | Awaiting physical sticker production & deployment | `TEST_PENDING` |
| `QR-PL-003` | `pl-temulawak` | Kebun Toga RW 03 Bubakan | UNKNOWN | UNKNOWN | YES | UNKNOWN | UNKNOWN | N/A | UNKNOWN | UNKNOWN | UNKNOWN | Awaiting physical sticker production & deployment | `TEST_PENDING` |
| `QR-PL-004` | `pl-lidah-buaya` | Kebun Toga RW 03 Bubakan | UNKNOWN | UNKNOWN | YES | UNKNOWN | UNKNOWN | N/A | UNKNOWN | UNKNOWN | UNKNOWN | Awaiting physical sticker production & deployment | `TEST_PENDING` |
| `QR-PL-005` | `pl-serai-wangi` | Kebun Toga RW 03 Bubakan | UNKNOWN | UNKNOWN | YES | UNKNOWN | UNKNOWN | N/A | UNKNOWN | UNKNOWN | UNKNOWN | Awaiting physical sticker production & deployment | `TEST_PENDING` |
| `QR-PL-006` | `pl-pegagan` | Kebun Toga RW 03 Bubakan | UNKNOWN | UNKNOWN | YES | UNKNOWN | UNKNOWN | N/A | UNKNOWN | UNKNOWN | UNKNOWN | Awaiting physical sticker production & deployment | `TEST_PENDING` |
| `QR-PL-007` | `pl-kunyit` | Kebun Toga RW 03 Bubakan | UNKNOWN | UNKNOWN | YES | UNKNOWN | UNKNOWN | N/A | UNKNOWN | UNKNOWN | UNKNOWN | Awaiting physical sticker production & deployment | `TEST_PENDING` |
| `QR-PL-008` | `pl-sambiloto` | Kebun Toga RW 03 Bubakan | UNKNOWN | UNKNOWN | YES | UNKNOWN | UNKNOWN | N/A | UNKNOWN | UNKNOWN | UNKNOWN | Awaiting physical sticker production & deployment | `TEST_PENDING` |
| `QR-LOC-001` | `loc-toga-rw03-bersemi` | Pos Kamling / Balai RW 03 | UNKNOWN | UNKNOWN | YES | UNKNOWN | UNKNOWN | N/A | UNKNOWN | UNKNOWN | UNKNOWN | Location plot pending admin approval in field | `TEST_PENDING` |
| `QR-LOC-002` | `loc-farm-rw01-makmur` | Lahan RW 01 Bubakan | UNKNOWN | UNKNOWN | YES | UNKNOWN | UNKNOWN | N/A | UNKNOWN | UNKNOWN | UNKNOWN | Location plot pending admin approval in field | `TEST_PENDING` |

---

## 3. Simulator & Emulation Baseline Check

| Test Scenario | Test Environment | Destination Verified | UI Rendering | Result | Notes |
|---|---|---|---|---|---|
| Deep Link Scheme via ADB | Android Studio Emulator | `Screen.PlantDetail` | `PlantDetailScreen` rendered successfully with plantId `pl-jahe-merah` | **PASS (EMULATED)** | Intent filter handles `https://bubakangreen.web.app/plant/{id}` |
| Deep Link Scheme via ADB | Android Studio Emulator | `Screen.LocationDetail` | `LocationDetailScreen` rendered successfully with locId `loc-toga-rw03-bersemi` | **PASS (EMULATED)** | Intent filter handles `https://bubakangreen.web.app/location/{id}` |
| Web Fallback Direct Load | Chrome / Edge Browser | `/plant.html` | Plant title, Latin name, Mandarin box, CTA rendered | **PASS (LOCAL WEB)** | Clean responsive layout on `http://localhost:8080/plant.html?id=pl-jahe-merah` |
| Web Fallback Not Found | Chrome / Edge Browser | `/plant.html` | Clear error / empty state displayed gracefully | **PASS (LOCAL WEB)** | Handled without uncaught script exceptions |

*(Note: Emulated and local web tests validate software routing pipelines only; real-world physical camera tests will be populated during field deployment upon Phase 5 approval).*
