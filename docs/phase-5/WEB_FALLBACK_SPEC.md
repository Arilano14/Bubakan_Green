# Web Fallback Specification — Bubakan Green

**Product**: Bubakan Green  
**Canonical Domain**: `https://bubakangreen.web.app`  
**Hosting Infrastructure**: Firebase Hosting  

---

## 1. Role of Web Fallback

The Web Fallback is **NOT** a second full web application, clone of the mobile app, or admin dashboard.
Its strict purpose is:
1. **Physical QR Fallback**: Provide an instant, accessible view of botanical or garden plot information when a user scans a garden QR code on a device without Bubakan Green installed (or non-Android devices).
2. **Product Pitch**: Introduce Kelurahan Bubakan's Urban Farming and Taman Toga initiative with warm botanical branding, educational tone, and mascot.
3. **APK Download CTA**: Provide a clear, honest gateway to download the official Android application.

---

## 2. Route Handling in `firebase.json`

Configured in [web/firebase.json](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/web/firebase.json):
* `/plant/**` $\rightarrow$ Rewrites to `/plant.html`
* `/location/**` $\rightarrow$ Rewrites to `/location.html`
* `**` $\rightarrow$ Rewrites to `/index.html`

---

## 3. Data Flow & Single Source of Truth

```
         [ Same Firestore Database ]
           /master_plants/{id}
                    │
       ┌────────────┴────────────┐
       ▼                         ▼
[ Android App ]          [ Web Fallback ]
Native Kotlin Repo         Firebase JS Client SDK
```

- **Zero Duplicate DB**: The web pages dynamically query the exact same Firestore collections (`/master_plants` and `/locations`).
- **Dynamic Updates**: Modifying plant attributes (name, benefits, scientific name) in Firestore updates both the Android screen and the Web page instantly under the exact same QR URL.

---

## 4. Download APK CTA Requirements

- Must link to the actual compiled APK binary on Firebase Hosting or release bucket.
- Must display concise installation instructions:
  1. Klik tombol **Download APK**.
  2. Buka file hasil unduhan di perangkat Android Anda.
  3. Ikuti dialog instalasi sistem Android.
  4. Buka aplikasi **Bubakan Green**.
- Must **NOT** claim silent or automatic installation.
