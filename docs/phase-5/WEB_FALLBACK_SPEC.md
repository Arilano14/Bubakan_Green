# Web Fallback Specification — Bubakan Green

**Product**: BUBAKAN GREEN (Sistem Informasi Urban Farming & Taman Toga Kelurahan Bubakan)  
**Canonical Domain**: `https://bubakangreen.web.app`  
**Hosting Infrastructure**: Firebase Hosting (Static & Edge CDN)  
**Audit Date**: 2026-10-02  

---

## 1. Role & Identity of Web Fallback

The Web Fallback is **NOT** a second full web application, clone of the mobile app, or admin dashboard.
Its strict purpose is:
1. **Physical QR Fallback**: Provide an instant, lightweight, mobile-responsive view of botanical or garden plot information when a visitor scans a physical QR sign without the Bubakan Green app installed (e.g. iOS users, first-time visitors).
2. **Product Pitch**: Introduce Kelurahan Bubakan's Urban Farming and Taman Toga initiative with warm botanical branding, educational tone, and friendly mascot.
3. **Download Entry Point**: Offer an honest, clear gateway to download the official Android application.

---

## 2. Visual Direction & Page Structure

```
+------------------------------------------+
|             [ MASCOT / LOGO ]            |
|              BUBAKAN GREEN               |
|      Kenali Tanaman di Kelurahan Bubakan  |
+------------------------------------------+
|            [ HERO FOTO TANAMAN ]         |
+------------------------------------------+
|  Nama Tanaman (Indonesia)                |
|  Nama Ilmiah (Latin)                     |
|  Karakter Mandarin & Pinyin              |
|                                          |
|  [🌿 Khasiat & Manfaat Herbal]           |
|  Khasiat toga dan cara penggunaan umum   |
|                                          |
|  [🌱 Lokasi Budidaya]                    |
|  Kebun Urban Farming / Taman Toga RW...  |
+------------------------------------------+
|      [ 📲 BUKA DI APLIKASI ]             |
|      [ ⬇️ DOWNLOAD APK BUBAKAN GREEN ]    |
+------------------------------------------+
```

---

## 3. Hosting & Route Handling (`web/firebase.json`)

Configured in [web/firebase.json](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/web/firebase.json):
* `/plant/**` $\longrightarrow$ Rewrites to `/plant.html`
* `/location/**` $\longrightarrow$ Rewrites to `/location.html`
* `**` $\longrightarrow$ Rewrites to `/index.html`
* Headers: `/.well-known/assetlinks.json` $\longrightarrow$ `Content-Type: application/json`

---

## 4. Single Source of Truth & Data Consistency Risk

```
                  [ Cloud Firestore ]
                 /master_plants/{id}
                          │
             ┌────────────┴────────────┐
             ▼                         ▼
      [ Android App ]          [ Web Fallback ]
     Firestore SDK Flow       Firestore REST API GET
```

> [!WARNING]
> **Data Consistency Risk Identified (BUG-01)**:
> In `FirestorePlantRepository.kt`, the Android app queries `collection("plants")`. In `web/public/plant.html`, the fetch target is `documents/master_plants/{id}`.
> *Risk*: If documents are populated only under `/plants`, the web fallback returns HTTP 404 from Firestore REST API.
> *Remediation*: The collection name must be aligned to `/master_plants` across both mobile and web fallback before Phase 5 deployment.

---

## 5. Download APK CTA Requirements & Installation UX

* Must link to the actual compiled APK binary on Firebase Hosting or release bucket.
* During active development prior to production release build: displays explicit test/preview status rather than fake local filesystem paths.
* Displays concise 4-step installation guidance for Android users:
  1. Unduh file APK melalui tombol unduh.
  2. Buka notifikasi unduhan atau folder *Download* di pengelola file HP.
  3. Jika muncul notifikasi keamanan, pilih **Setelan &rarr; Izinkan dari sumber ini**.
  4. Tekan **Instal** dan buka aplikasi Bubakan Green.
* **Security & Honesty Invariant**: Never claim automatic or silent background APK installation.
