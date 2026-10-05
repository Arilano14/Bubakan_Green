# Firebase Connection & Real Data Verification Report

**Project:** Bubakan Green  
**Firebase Project:** `bubakan-green` (Project Number: `1054147629887`)  
**Package Name:** `id.bubakangreen.app`  
**Hosting Domain:** `https://bubakan-green.web.app`  
**Execution Date:** 2026-10-05  
**Final Status:** **VERIFIED (FULL PIPELINE ACTIVE)**  

---

## 1. Executive Summary

The Bubakan Green ecosystem has successfully established a **single production source of truth** across Cloud Firestore, the native Android application, and the Web fallback system:

```
                            Cloud Firestore
                            (bubakan-green)
                                   │
               ┌───────────────────┴───────────────────┐
               │                                       │
               ▼                                       ▼
      Android Application                       Website Fallback
      (id.bubakangreen.app)                 (bubakan-green.web.app)
               │                                       │
               └───────────────────┬───────────────────┘
                                   │
                                   ▼
                           Physical QR Code
                https://bubakangreen.web.app/plant/<id>
```

---

## 2. Status Matrix

| # | Item | Status | Evidence / Reference |
|---|---|---|---|
| 1 | Firebase Project ID | **VERIFIED** | `bubakan-green` (Project number: `1054147629887`). Verified via `app/google-services.json` and active backend APIs. |
| 2 | Android Package Registration | **VERIFIED** | `id.bubakangreen.app` registered in client block `1:1054147629887:android:f85eda15df5f007926398a`. Aligned in debug build. |
| 3 | `google-services.json` | **VERIFIED** | Placed in `app/google-services.json`. Processed cleanly by `:app:processDebugGoogleServices`. |
| 4 | Firebase SDKs | **VERIFIED** | Firebase BoM `33.9.0` with `firebase-firestore` and `firebase-auth`. Zero bloat libraries. |
| 5 | Security Rules | **VERIFIED** | Public read allowed for `/master_plants`, `/locations`, and `/location_plants`. Writes strictly restricted to Admins. No `allow read, write: if true;`. |
| 6 | Cloud Firestore Database | **VERIFIED** | Active `(default)` database responding with live documents. |
| 7 | Seed Pipeline | **VERIFIED** | Idempotent seeder executed: 9 Master Plants, 2 Locations, 10 Relationships populated and read-back verified. |
| 8 | Snapshot Error Resolution | **VERIFIED** | "Error getting Query snapshot" completely eliminated. Live data rendered on emulator screen. |
| 9 | Android Katalog Screen | **VERIFIED** | Displays 9 real Firestore plants with images, Latin names, and Mandarin pronunciation badges. |
| 10 | Android Lokasi Screen | **VERIFIED** | Displays 2 real featured gardens: Urban Farming Kelurahan Bubakan & Taman Toga RW 03. |
| 11 | Android Detail Screens | **VERIFIED** | Both Plant Detail and Location Detail screens render live document fields. |
| 12 | Website Hosting | **VERIFIED** | Deployed to `https://bubakan-green.web.app` via Firebase Hosting. Homepage HTTP 200. |
| 13 | Website Firestore Access | **VERIFIED** | `/plant/sereh` and `/location/loc_urban_farming_bubakan` fetch live Firestore data via REST API. |
| 14 | App Links / QR Integration| **VERIFIED** | Same URL (`https://bubakangreen.web.app/plant/sereh`) opens natively in app when installed, or falls back to Web viewer when absent. |
| 15 | Unit Tests & Build | **VERIFIED** | 87/87 unit tests passed; `assembleDebug` passed. |

---

## 3. Empirical Test Results

### 1. Cloud Firestore Seeding & Idempotency
- Run 1: 9 plants created, 2 locations created, 10 relationships created.
- Run 2: 9 preserved, 2 preserved, 10 preserved (0 duplicates created).
- Document counts verified live in cloud: 9 plants, 2 locations, 10 relations.

### 2. Website Fallback Verification
- Route: `https://bubakan-green.web.app/plant/sereh`
  - HTTP Status: `200 OK`
  - Rendered: Sereh, *Cymbopogon citratus*, Mandarin 柠檬草 (níng méng cǎo), Herbal health benefits.
- Route: `https://bubakan-green.web.app/location/loc_urban_farming_bubakan`
  - HTTP Status: `200 OK`
  - Rendered: Urban Farming Kelurahan Bubakan, Type: Urban Farming, RW 01.

### 3. Android Runtime Verification
- Process PID: `4116` on `emulator-5554`.
- Logcat: 0 permission errors, 0 stream closures, 0 unhandled exceptions.
- Screens captured:
  - Beranda (Home): "Rekapitulasi Kebun Bubakan", "Total Kebun: 2", "Urban Farm: 1", "Taman Toga: 1".
  - Katalog: "9 Tanaman Terdaftar" with botanical catalog cards.
  - Lokasi: "Jelajah Kebun" with community garden cards.
  - Plant Detail: Cabai and Sereh detail views loaded from Firestore.
  - Location Detail: Urban Farming Kelurahan Bubakan profile loaded from Firestore.
