# BUBAKAN GREEN — FIELD QR TEST RESULTS
**Product:** BUBAKAN GREEN  
**Sub-title:** Sistem Informasi Urban Farming & Taman Toga Kelurahan Bubakan  
**Phase:** Phase 5 — QR & Field Integration  
**Governance:** Zero fabricated test data. Only actual hardware test sessions conducted in real physical conditions may be recorded. Unexecuted physical tests remain strictly `NOT TESTED`.  
**Date:** 2026-09-27  
**Revision:** Final Pre-Execution Revision  

---

## 1. Governance & Status Standard

Every physical QR candidate in Kelurahan Bubakan is tracked according to strict real-world testing statuses:
- **`NOT TESTED`**: Physical test session has not yet taken place on real hardware.
- **`TESTING`**: Physical label deployed; multi-device test session currently in progress.
- **`PASS`**: Scanned successfully across distances, angles, and lighting; App Link and Web Fallback fully verified on physical devices.
- **`FAIL`**: Scanned failed, link failed to resolve, or data mismatch observed; logged for remediation.
- **`BLOCKED`**: Test blocked by missing physical asset, unpublished database record, or missing domain connectivity.

---

## 2. Physical Field Test Log (Hardware Execution)

| QR ID | Resource Target | Physical Location | Test Device | OS Version | App Installed? | Scan Distance | Ambient Lighting | Scan Angle | App Link Result | Web Fallback Result | Data Accurate? | Notes | Test Status |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| `QR-PL-001` | `pl-jhem-01` | Kebun Toga RW 03 | -- | -- | YES | -- | -- | -- | -- | N/A | -- | Awaiting physical printing & field session | `NOT TESTED` |
| `QR-PL-001-WEB` | `pl-jhem-01` | Kebun Toga RW 03 | -- | -- | NO | -- | -- | -- | N/A | -- | -- | Awaiting physical printing & field session | `NOT TESTED` |
| `QR-PL-002` | `pl-kmkc-02` | Kebun Toga RW 03 | -- | -- | YES | -- | -- | -- | -- | N/A | -- | Awaiting physical printing & field session | `NOT TESTED` |
| `QR-PL-003` | `pl-tmlw-03` | Kebun Toga RW 03 | -- | -- | YES | -- | -- | -- | -- | N/A | -- | Awaiting physical printing & field session | `NOT TESTED` |
| `QR-PL-004` | `pl-ldby-04` | Kebun Toga RW 03 | -- | -- | YES | -- | -- | -- | -- | N/A | -- | Awaiting physical printing & field session | `NOT TESTED` |
| `QR-PL-005` | `pl-srwg-05` | Kebun Toga RW 03 | -- | -- | YES | -- | -- | -- | -- | N/A | -- | Awaiting physical printing & field session | `NOT TESTED` |
| `QR-PL-006` | `pl-pggn-06` | Kebun Toga RW 03 | -- | -- | YES | -- | -- | -- | -- | N/A | -- | Awaiting physical printing & field session | `NOT TESTED` |
| `QR-PL-007` | `pl-knyt-07` | Kebun Toga RW 03 | -- | -- | YES | -- | -- | -- | -- | N/A | -- | Awaiting physical printing & field session | `NOT TESTED` |
| `QR-PL-008` | `pl-smbl-08` | Kebun Toga RW 03 | -- | -- | YES | -- | -- | -- | -- | N/A | -- | Awaiting physical printing & field session | `NOT TESTED` |
| `QR-LOC-001` | `loc-tg03-01` | Balai RW 03 | -- | -- | YES | -- | -- | -- | -- | N/A | -- | Awaiting plot verification & printing | `NOT TESTED` |
| `QR-LOC-002` | `loc-uf01-02` | Kebun RW 01 | -- | -- | YES | -- | -- | -- | -- | N/A | -- | Awaiting plot verification & printing | `NOT TESTED` |

---

## 3. Preliminary Software Pipeline Diagnostics (Emulated / Diagnostic Only)

*The following diagnostic tests validate software navigation logic only and do NOT substitute for physical field testing.*

| Software Pipeline Test | Method | Destination Screen | Error Handling | Result | Scope / Distinction |
|---|---|---|---|---|---|
| Inbound Intent Dispatch | `am start -a android.intent.action.VIEW -d "https://bubakangreen.web.app/plant/pl-jhem-01"` | `PlantDetailScreen` | N/A (Valid ID) | **PASS (SOFTWARE ROUTE ONLY)** | Validates NavHost parameter parsing; NOT physical scan. |
| Inbound Intent (Unknown ID)| `am start -a android.intent.action.VIEW -d "https://bubakangreen.web.app/plant/invalid-id-999"` | `PlantDetailScreen` | Shows `StateEmptyView` | **PASS (SOFTWARE ROUTE ONLY)** | Validates defensive empty state handling. |
| Inbound Intent (Malformed) | `am start -a android.intent.action.VIEW -d "https://bubakangreen.web.app/invalid-path"` | Default Home | Falls back to `HomeScreen` | **PASS (SOFTWARE ROUTE ONLY)** | Validates fallback without app crash. |
| Web Fallback Local Load | HTTP GET `http://localhost:8080/plant.html?id=pl-jhem-01` | Web Botanical Card | Renders ID & layout | **PASS (LOCAL WEB ONLY)** | Validates responsive fallback markup. |
