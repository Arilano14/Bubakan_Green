# Map Technology Evaluation & Architectural Decision — Bubakan Green

**Product**: BUBAKAN GREEN (Sistem Informasi Urban Farming & Taman Toga Kelurahan Bubakan)  
**Phase**: Phase 6 — Location Visualization & Interactive Map  
**Decision Gate**: ARCHITECTURE COMMITTEE APPROVAL REQUIRED  
**Author**: Senior Android Architect & Backend Architect  

---

## 1. Context & Core Constraints

Phase 6 requires an interactive map displaying all registered Urban Farming and Taman Toga garden plots in Kelurahan Bubakan. The map must support:
- Multiple dynamic markers fed directly from Firestore collection `locations`.
- Visual category distinction: 🌱 Urban Farming (Green) vs 🌿 Taman Toga (Amber/Gold).
- Marker tap interaction to display a compact card and navigate to `LocationDetailScreen`.
- Strict **Rp0 infrastructure cost** and low-complexity maintenance for Kelurahan Bubakan.

---

## 2. Options Comparative Matrix

| Evaluation Criteria | Option A: Google Maps SDK | Option B: Google Maps Embed / Intent | Option C1: OpenStreetMap + Leaflet (WebView) | Option C2: Osmdroid Native Library | Option D: Pure Compose Canvas SVG |
|---|---|---|---|---|---|
| **1. No Billing Account** | ❌ **FAIL** (Requires GCP billing account & credit card) | ⚠️ **PARTIAL** (Intent is free; Embed API requires GCP billing) | ✅ **PASS** (100% Free, zero billing account) | ✅ **PASS** (100% Free, zero billing account) | ✅ **PASS** (100% Free, zero billing account) |
| **2. No API Key Required** | ❌ **FAIL** (Requires GCP API Key restricted to SHA-1) | ⚠️ **PARTIAL** (Intent requires no key; Embed requires key) | ✅ **PASS** (Zero API keys required) | ✅ **PASS** (Zero API keys required) | ✅ **PASS** (Zero API keys required) |
| **3. Low Dependency Weight** | ❌ **FAIL** (~3-4 MB SDK bloat) | ✅ **PASS** (0 KB, uses system apps) | ✅ **PASS** (~55 KB assets: Leaflet 1.9.4 JS+CSS) | ⚠️ **MODERATE** (~1.5 MB native AAR) | ✅ **PASS** (0 KB, uses Compose) |
| **4. Multiple Custom Markers** | ✅ **PASS** (BitmapDescriptor) | ❌ **FAIL** (External intent shows 1 coordinate) | ✅ **PASS** (Custom SVG/HTML pins with icons) | ✅ **PASS** (Native Drawables) | ⚠️ **LIMITED** (Requires custom projection math) |
| **5. Dynamic Firestore Data** | ✅ **PASS** (Composable Markers) | ❌ **FAIL** (Cannot pass dynamic multi-markers to intent) | ✅ **PASS** (JSON injected via evaluateJavascript) | ✅ **PASS** (Native OverlayItems) | ⚠️ **MODERATE** (Custom Canvas drawing) |
| **6. Marker Category Color** | ✅ **PASS** (HUE colors) | ❌ **FAIL** (Red pin only in intent) | ✅ **PASS** (Emerald Green vs Amber Gold SVG pins) | ✅ **PASS** (Custom icons) | ✅ **PASS** (Compose Color) |
| **7. Marker Tap Interaction** | ✅ **PASS** (onClick callback) | ❌ **FAIL** (Leaves application) | ✅ **PASS** (JavaScriptInterface to Kotlin callback) | ✅ **PASS** (ItemizedIconOverlay) | ⚠️ **COMPLEX** (Custom hit-testing) |
| **8. Maintainability** | ⚠️ **FRAGILE** (API key expiration, billing changes) | ✅ **SIMPLE** (Standard intent) | ✅ **HIGH** (Self-contained HTML/JS in assets) | ⚠️ **MODERATE** (Native lifecycle handling) | ⚠️ **HIGH EFFORT** (Custom GIS projection) |
| **9. Viewport Responsiveness** | ✅ **PASS** (Compose MapView) | ❌ **FAIL** (External app) | ✅ **PASS** (CSS 100% width/height, auto-fitBounds) | ✅ **PASS** (MapView) | ✅ **PASS** (Compose Box) |
| **10. Legal & Attribution** | ✅ **PASS** (Built into SDK) | ✅ **PASS** (Google Maps app) | ✅ **PASS** (OSM Attribution © OpenStreetMap contributors) | ✅ **PASS** (OSM Attribution) | ✅ **PASS** (None required) |

---

## 3. Detailed Technology Analysis

### Option A: Google Maps SDK for Android (`play-services-maps`)
- **Fatal Obstacle**: While Google provides a $200 monthly free credit, creating the API key **mandates an active Google Cloud Platform billing account backed by a valid credit card**.
- Kelurahan Bubakan and community administrators cannot be burdened with maintaining recurring cloud billing accounts that risk service suspension or accidental overage charges.
- **Verdict**: **REJECTED** on Cost and Billing Principles.

### Option B: External Google Maps Intent (`geo:...`)
- **Role**: Already implemented in `LocationsScreen.kt` and `LocationDetailScreen.kt` for "Petunjuk Arah" (turn-by-turn navigation in external app).
- **Limitation**: Cannot display an in-app interactive map with multiple simultaneous garden markers, RW filtering, or category legends.
- **Verdict**: **RETAIN AS NAVIGATION FALLBACK**, but insufficient for in-app overview.

### Option C1: OpenStreetMap + Leaflet.js via Android Compose WebView (RECOMMENDED)
- **Why It Wins**:
  1. **Zero Billing & Zero API Key**: 100% free and open-source under Open Database License (ODbL) and BSD-2-Clause.
  2. **Zero Native Dependency Bloat**: Uses Android's built-in `android.webkit.WebView`. Leaflet.js (v1.9.4, ~40 KB minified) and Leaflet CSS (~15 KB) are bundled directly inside `app/src/main/assets/map/`.
  3. **Rich Botanical Visuals**: Allows custom HTML/SVG markers styled with Bubakan Green tokens:
     - 🌱 **Urban Farming**: Emerald Green `#2E7D32` with white seedling glyph.
     - 🌿 **Taman Toga**: Botanical Amber `#D97706` with white medicinal leaf glyph.
  4. **Dynamic Data Flow**: Kotlin ViewModel serializes Firestore `Location` entities into a compact JSON array and invokes `evaluateJavascript("updateMarkers(...)")`.
  5. **Bidirectional Bridge**: Tapping a marker invokes `window.AndroidBridge.onMarkerClick(locationId)`, updating Compose state to show the floating preview card.
  6. **Graceful Fallback**: If internet connectivity is offline, the webview retains the local HTML framework and displays an offline banner with the list selection.

---

## 4. OpenStreetMap Tile Usage Policy Compliance

In strict compliance with the [OpenStreetMap Foundation Tile Usage Policy](https://operations.osmfoundation.org/policies/tiles/):
1. **User-Agent Requirement**:
   - The WebView is configured with a valid, descriptive User-Agent:
     `BubakanGreen-Android/1.0.0 (contact: admin@bubakangreen.id)`
2. **Attribution**:
   - Mandatory attribution string is visibly rendered on the map corner:
     `© OpenStreetMap contributors`
3. **Caching**:
   - Android WebView standard disk and memory caching (`WebSettings.LOAD_DEFAULT`) is enabled to minimize redundant tile downloads.
4. **Traffic Footprint**:
   - The map bounds are strictly restricted to the Kelurahan Bubakan boundary (center: `-7.069, 110.331`, zoom: 14 to 17), preventing unnecessary global tile requests.

---

## 5. Architectural Recommendation

**Adopt Option C1 (Leaflet.js + Compose WebView)** as the official map engine for Bubakan Green.  
Retain **Option B (Google Maps Intent)** as the secondary action for turn-by-turn navigation via "📍 Petunjuk Arah".
