# Phase 6 Implementation Plan — QR Reliability & Location Map

**Product**: BUBAKAN GREEN (Sistem Informasi Urban Farming & Taman Toga Kelurahan Bubakan)  
**Execution Condition**: PENDING USER APPROVAL (`ACC PHASE 6`)  
**Strict Rule**: DO NOT EXECUTE IMPLEMENTATION UNTIL `ACC PHASE 6` IS RECEIVED.  

---

## 1. Phased Implementation Roadmap

```
  PRE-EXECUTION (COMPLETED)
  ├── 1. Audit Phase 5 & QR behavior [DONE]
  ├── 2. Exact URL & Stable ID Audit [DONE]
  ├── 3. Map Technology Evaluation [DONE - Leaflet in Compose WebView]
  ├── 4. 10 Gate Documents Created [DONE]
  └── 5. STOP & Await `ACC PHASE 6` [CURRENT STATE]
        │
        ▼ (After `ACC PHASE 6`)
  POST-APPROVAL IMPLEMENTATION
  ├── Subphase 6A: QR Management UX Refinement
  ├── Subphase 6B: Map Assets & Leaflet Engine Integration (`assets/map/`)
  ├── Subphase 6C: Compose `BubakanMapView` & Android-JS Bridge
  ├── Subphase 6D: Category Markers & Dynamic Firestore Connector
  ├── Subphase 6E: Home Page "Rekapitulasi Kebun Bubakan" & Mini-Map
  ├── Subphase 6F: LocationsScreen "Peta Sebaran" Full Interactive Tab
  ├── Subphase 6G: CRUD & Real-Time Sync Verification
  └── Subphase 6H: Final Multi-Viewport & Performance QA (MAP-01 to MAP-13)
```

---

## 2. Detailed Subphase Tasks

### Subphase 6A: QR Management UX Refinement
- **File**: `app/src/main/java/id/bubakangreen/app/ui/components/QrCodeDisplayDialog.kt`
- Add share CTA and direct field label generator shortcut.
- Ensure `LocationDetailScreen` and `PlantDetailScreen` display appropriate eligibility status badges.

### Subphase 6B: Map Assets & Leaflet Engine Setup
- **Directory**: `app/src/main/assets/map/`
- Bundle self-contained, lightweight offline assets:
  - `leaflet.js` (v1.9.4 minified, ~40 KB)
  - `leaflet.css` (~15 KB)
  - `map_template.html` (~4 KB)
- Configure OpenStreetMap standard tile provider with custom User-Agent:
  `BubakanGreen-Android/1.0.0 (contact: admin@bubakangreen.id)`
- Add permanent OSM attribution: `© OpenStreetMap contributors`.

### Subphase 6C: Compose `BubakanMapView` & Bidirectional Bridge
- **Component**: `app/src/main/java/id/bubakangreen/app/ui/components/BubakanMapView.kt`
- Use `AndroidView` wrapping `android.webkit.WebView`:
  - Enable JavaScript and DOM storage.
  - Disable geolocation permission requests (zero permission principle).
  - Register `@JavascriptInterface class MapJsBridge` with callback:
    `onMarkerSelected(locationId: String)`
  - Provide helper method `updateMarkers(markers: List<LocationMarkerDto>)`.

### Subphase 6D: Marker Visual Language & Legend
- Custom SVG pin templates rendered in Leaflet:
  - **🌱 Urban Farming**: `#2E7D32` (Emerald Green)
  - **🌿 Taman Toga**: `#D97706` (Botanical Amber)
- Floating map legend with tactile toggle.

### Subphase 6E: Home Recap & Mini-Map
- **Files**: `HomeViewModel.kt`, `HomeScreen.kt`
- Extend `HomeUiState` with `gardenSummary: GardenSummary`:
  - `totalGardens: Int`
  - `urbanFarmingCount: Int`
  - `tamanTogaCount: Int`
- Add `GardenRecapSection` with dynamic count cards.
- Add compact `HomeMiniMap` with "Lihat Semua Lokasi" navigation CTA.

### Subphase 6F: `LocationsScreen` Interactive Peta Tab
- **File**: `LocationsScreen.kt`
- Replace placeholder `MapVisualContainer` with `BubakanMapView`.
- Link marker selection to floating preview bottom card:
  - Tap marker &rarr; shows card with garden name, RW, address.
  - "📍 Petunjuk Arah" &rarr; launches external Google Maps intent.
  - "Detail Kebun →" &rarr; opens `LocationDetailScreen`.

### Subphase 6G: Data Synchronization & CRUD Validation
- Verify that adding, updating, or deactivating a location in `AdminLocationViewModel` / `LocationFormViewModel` dynamically updates the map markers via Firestore snapshot listeners without APK rebuild.

### Subphase 6H: QA Matrix & Performance Validation
- Execute all tests MAP-01 through MAP-13 across 360dp, 393dp, and 412dp viewports.
- Measure and record P50, P95, and MAX latencies for marker rendering and interaction.
