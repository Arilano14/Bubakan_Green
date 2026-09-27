# BUBAKAN GREEN — QR INTEGRATION MATRIX
**Product:** BUBAKAN GREEN  
**Sub-title:** Sistem Informasi Urban Farming & Taman Toga Kelurahan Bubakan  
**Phase:** Phase 5 — QR & Field Integration  
**Governance:** Zero dummy data for production QR. Physical scan tests marked strictly according to actual verification state.  
**Date:** 2026-09-27  

---

## 1. Architectural URL & Stable ID Specification

- **Domain:** `https://bubakangreen.web.app` (Primary Firebase Hosting HTTPS URL)
- **Secondary Domain (if provisioned):** `https://bubakangreen.app`
- **Stable ID Scheme:**
  - **Botanical Species (MasterPlant):** `pl-<kebab-case-indonesian-name>` (e.g. `pl-jahe-merah`, `pl-kumis-kucing`)
  - **Garden Plot (Location):** `loc-<type>-<rw>-<slug>` (e.g. `loc-toga-rw03-bersemi`, `loc-farm-rw01-lestari`)
- **QR URL Format:**
  - Plant: `https://bubakangreen.web.app/plant/{stableId}`
  - Location: `https://bubakangreen.web.app/location/{stableId}`
- **QR Code Content:** Strictly the HTTPS destination URL. Zero raw JSON or botanical records embedded in QR.
- **Data Update vs QR Reprint Rule:** Updating photo, description, care tips, or quantity in Firestore requires **ZERO QR REPRINT**. The printed physical QR remains permanently valid.

---

## 2. QR Integration Matrix

| Resource Type | Stable ID | Verification Category | QR URL | QR Generated | HTTPS Valid | App Link Path | App Routing | Web Fallback | Published State | Security Check | Physical Scan Test | Final Status |
|---|---|---|---|---|---|---|---|---|---|---|---|---|
| **Plant (Species)** | `pl-jahe-merah` | `DOCUMENT-VERIFIED` | `https://bubakangreen.web.app/plant/pl-jahe-merah` | READY (Spec approved) | PASS (Hosting configured) | `/plant/pl-jahe-merah` | PASS (NavHost deepLink configured) | PASS (`plant.html` configured) | PUBLISHED | PASS (Public read permitted) | UNKNOWN (Awaiting physical print) | **PLANNED** |
| **Plant (Species)** | `pl-kumis-kucing` | `DOCUMENT-VERIFIED` | `https://bubakangreen.web.app/plant/pl-kumis-kucing` | READY (Spec approved) | PASS (Hosting configured) | `/plant/pl-kumis-kucing` | PASS (NavHost deepLink configured) | PASS (`plant.html` configured) | PUBLISHED | PASS (Public read permitted) | UNKNOWN (Awaiting physical print) | **PLANNED** |
| **Plant (Species)** | `pl-temulawak` | `DOCUMENT-VERIFIED` | `https://bubakangreen.web.app/plant/pl-temulawak` | READY (Spec approved) | PASS (Hosting configured) | `/plant/pl-temulawak` | PASS (NavHost deepLink configured) | PASS (`plant.html` configured) | PUBLISHED | PASS (Public read permitted) | UNKNOWN (Awaiting physical print) | **PLANNED** |
| **Plant (Species)** | `pl-lidah-buaya` | `DOCUMENT-VERIFIED` | `https://bubakangreen.web.app/plant/pl-lidah-buaya` | READY (Spec approved) | PASS (Hosting configured) | `/plant/pl-lidah-buaya` | PASS (NavHost deepLink configured) | PASS (`plant.html` configured) | PUBLISHED | PASS (Public read permitted) | UNKNOWN (Awaiting physical print) | **PLANNED** |
| **Plant (Species)** | `pl-serai-wangi` | `DOCUMENT-VERIFIED` | `https://bubakangreen.web.app/plant/pl-serai-wangi` | READY (Spec approved) | PASS (Hosting configured) | `/plant/pl-serai-wangi` | PASS (NavHost deepLink configured) | PASS (`plant.html` configured) | PUBLISHED | PASS (Public read permitted) | UNKNOWN (Awaiting physical print) | **PLANNED** |
| **Plant (Species)** | `pl-pegagan` | `DOCUMENT-VERIFIED` | `https://bubakangreen.web.app/plant/pl-pegagan` | READY (Spec approved) | PASS (Hosting configured) | `/plant/pl-pegagan` | PASS (NavHost deepLink configured) | PASS (`plant.html` configured) | PUBLISHED | PASS (Public read permitted) | UNKNOWN (Awaiting physical print) | **PLANNED** |
| **Plant (Species)** | `pl-kunyit` | `DOCUMENT-VERIFIED` | `https://bubakangreen.web.app/plant/pl-kunyit` | READY (Spec approved) | PASS (Hosting configured) | `/plant/pl-kunyit` | PASS (NavHost deepLink configured) | PASS (`plant.html` configured) | PUBLISHED | PASS (Public read permitted) | UNKNOWN (Awaiting physical print) | **PLANNED** |
| **Plant (Species)** | `pl-sambiloto` | `DOCUMENT-VERIFIED` | `https://bubakangreen.web.app/plant/pl-sambiloto` | READY (Spec approved) | PASS (Hosting configured) | `/plant/pl-sambiloto` | PASS (NavHost deepLink configured) | PASS (`plant.html` configured) | PUBLISHED | PASS (Public read permitted) | UNKNOWN (Awaiting physical print) | **PLANNED** |
| **Location (Plot)** | `loc-toga-rw03-bersemi` | `NEEDS-FIELD-VALIDATION` | `https://bubakangreen.web.app/location/loc-toga-rw03-bersemi` | READY (Spec approved) | PASS (Hosting configured) | `/location/loc-toga-rw03-bersemi` | PASS (NavHost deepLink configured) | PASS (`index.html` configured) | PENDING_APPROVAL | PASS (Security enforces approval) | UNKNOWN (Awaiting physical print) | **PLANNED** |
| **Location (Plot)** | `loc-farm-rw01-makmur` | `NEEDS-FIELD-VALIDATION` | `https://bubakangreen.web.app/location/loc-farm-rw01-makmur` | READY (Spec approved) | PASS (Hosting configured) | `/location/loc-farm-rw01-makmur` | PASS (NavHost deepLink configured) | PASS (`index.html` configured) | PENDING_APPROVAL | PASS (Security enforces approval) | UNKNOWN (Awaiting physical print) | **PLANNED** |
| **Test Fixture (Dev)** | `test-plant-sample` | `TEST_ONLY` | `https://bubakangreen.web.app/plant/test-plant-sample` | TEST_ONLY | PASS | `/plant/test-plant-sample` | PASS | PASS | DRAFT | PASS | SIMULATED_ONLY | **TEST_ONLY** |

---

## 3. Data Integrity & Verification Standard

1. **DOCUMENT-VERIFIED**: Plant records cataloged from approved trilingual botanical manuscripts of Kelurahan Bubakan.
2. **NEEDS-FIELD-VALIDATION**: Physical garden plots submitted by PIC officers whose exact boundary and single-shot GPS accuracy must be confirmed in the field prior to physical sticker placement.
3. **TEST_ONLY**: Internal diagnostic mock IDs used strictly for local automated tests and Android Studio previews; barred from production QR printing.
4. **Physical Scan Test**: No QR code may be claimed as `VERIFIED` until a physical printed sticker has been scanned with a real mobile camera on both Android (with and without the app installed).
