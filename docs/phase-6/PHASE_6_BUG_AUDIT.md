# Phase 6 Pre-Execution Bug Audit & Technical Findings — Bubakan Green

**Product**: BUBAKAN GREEN (Sistem Informasi Urban Farming & Taman Toga Kelurahan Bubakan)  
**Date**: October 3, 2026  
**Auditor**: Senior Android Architect & QA Engineer  
**Status**: AUDITED — 4 CRITICAL FINDINGS IDENTIFIED  

---

## 1. Finding 1: Exact URL Identity Audit — `https://bubakangreen.web.app/plant/sereh`

### Investigation:
The Phase 6 prompt explicitly queries:
> *"Is 'sereh' currently: A. actual Firestore document ID, B. stable slug, C. alias, D. mutable display name, E. nonexistent? Report actual state."*

### Empirical Finding:
1. **In UI Preview Fixture (`UiPreviewOnlyPlantRepository.kt`)**:
   - `sereh` is the **actual document ID** (`id = "sereh"`).
   - When dispatched via ADB (`adb shell am start ... /plant/sereh`), it routes cleanly to `PlantDetailScreen` and renders all botanical, pronunciation, and cultivation fields (verified in screenshot `test_sereh2.png`).
2. **In Production Generator (`MasterPlantViewModel.kt:159-163`)**:
   - Newly created master plants generate canonical IDs using:
     `"pl-$slug-$randomSuffix"` (e.g. `pl-sereh-4a8f9c`).
3. **In Canonical URL Parser (`QrUrlBuilder.kt:33-45`)**:
   - `parseCanonicalUrl()` extracts the segment after `/plant/` using regex `^[a-z0-9_-]+$`.
   - Both `sereh` and `pl-sereh-4a8f9c` are valid alphanumeric slugs.
4. **Architectural Recommendation**:
   - **OPTION A (Keep Canonical)**: Maintain `pl-` and `loc-` as the default generated IDs for newly created entities.
   - For existing initial botanical assets (`sereh`, `cabai`, `kangkung`, `jahe`, etc.), treat these clean names as **official stable slugs** in the Firestore seeder so that legacy and current educational materials referencing `/plant/sereh` work seamlessly without requiring complex alias/redirect redirection servers.

---

## 2. Finding 2: Web Fallback Hosting 404 (Site Not Found)

### Empirical Finding:
Navigating to `https://bubakangreen.web.app/` or `https://bubakangreen.web.app/plant/sereh` via Chrome browser subagent returns:
```
404 Site Not Found
There is no site configured at this address.
```

### Root Cause:
1. The web files exist locally in `web/public/` (`index.html`, `plant.html`, `location.html`, `assetlinks.json`).
2. However, the site has **never been deployed to the live Firebase Hosting project** via `firebase deploy --only hosting`.
3. The local workstation lacks a global `firebase` CLI installation and is not authenticated with Google Cloud credentials for the project.

### Risk & Recommendation:
- On an Android device where Bubakan Green is **installed**, Android App Links intercept the URL locally, so the user never sees this 404.
- On a device where Bubakan Green is **not installed**, opening the URL in Chrome currently lands on Firebase's default 404 page.
- **Action**: Document the exact deployment instructions for the project owner (`npx firebase-tools deploy --only hosting`) in the deployment guide.

---

## 3. Finding 3: `LocationsScreen` Map Tab is Currently a Placeholder

### Empirical Finding:
Inspecting `LocationsScreen.kt:315-456`:
- The tab `Peta Sebaran` currently renders `MapVisualContainer`, which is merely a vertical `LazyColumn` of location cards with a *"Pilih Titik Kebun di Wilayah Bubakan"* header and a floating bottom card.
- **No map canvas, no tiles, and no geographic markers are rendered.**

### Impact & Solution:
- This confirms the exact rationale for Phase 6 Goal B.
- `MapVisualContainer` will be replaced with `BubakanMapView` (Leaflet.js embedded in Compose `AndroidView(WebView)`), retaining the floating preview card for seamless UX continuity.

---

## 4. Finding 4: Home Page Lacks Botanical Recap & Mini-Map

### Empirical Finding:
Inspecting `HomeViewModel.kt` and `HomeScreen.kt`:
- The Home screen displays `featuredLocations` and `popularPlants`.
- It does **not** yet display the required **"Rekapitulasi Kebun Bubakan"** section showing live dynamic counts:
  - Total Kebun
  - Urban Farming Count
  - Taman Toga Count
- It does not contain the compact discovery mini-map.

### Solution:
- Extend `HomeViewModel` to aggregate location counts dynamically from `LocationRepository.getPublishedLocations()`.
- Add `GardenRecapSection` and a compact mini-map to `HomeScreen.kt`.
