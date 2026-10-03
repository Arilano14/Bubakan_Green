# Map Architecture & Interaction Specification — Bubakan Green

**Product**: BUBAKAN GREEN (Sistem Informasi Urban Farming & Taman Toga Kelurahan Bubakan)  
**Status**: APPROVED ARCHITECTURE SPECIFICATION (PHASE 6)  
**Target Viewports**: 360dp, 393dp, 412dp, Landscape  

---

## 1. Unidirectional Map Data Flow (Section 14)

```
        ┌──────────────────────────────────────────────┐
        │        Cloud Firestore: `/locations`         │
        └──────────────────────┬───────────────────────┘
                               │
                               ▼
        ┌──────────────────────────────────────────────┐
        │             LocationRepository               │
        │  `getPublishedLocations(): Flow<Result<...>>`│
        └──────────────────────┬───────────────────────┘
                               │ (Flow collection)
                               ▼
        ┌──────────────────────────────────────────────┐
        │   LocationsViewModel / HomeViewModel         │
        │   - Filter: `isPublished && status == ACTIVE`│
        │   - Validate GPS: `lat != 0.0 && lng != 0.0` │
        │   - Transform to `MapLocationMarkerDto`      │
        └──────────────────────┬───────────────────────┘
                               │ (StateFlow: `UiState.Success`)
                               ▼
        ┌──────────────────────────────────────────────┐
        │           Compose Map Container              │
        │       (`BubakanMapView` / WebView)           │
        └──────────────────────┬───────────────────────┘
                               │ (JS Bridge: `updateMarkers(json)`)
                               ▼
        ┌──────────────────────────────────────────────┐
        │             Leaflet.js Engine                │
        │    - Renders Custom Botanical Markers        │
        │    - Bounds fit Kelurahan Bubakan            │
        └──────────────────────┬───────────────────────┘
                               │ (User taps marker)
                               ▼
        ┌──────────────────────────────────────────────┐
        │    `AndroidBridge.onMarkerSelected(locId)`   │
        └──────────────────────┬───────────────────────┘
                               │
                               ▼
        ┌──────────────────────────────────────────────┐
        │        Floating Preview Bottom Card          │
        │   [📍 Buka Maps]   [Detail Kebun →]          │
        └──────────────────────────────────────────────┘
```

> **CRITICAL RULE**: The Composable Map UI never interacts directly with Firebase. All database operations strictly transit through `LocationRepository`.

---

## 2. Marker Eligibility & Validation Rules (Section 15 & 24)

A location document is displayed on the public map **ONLY IF** it satisfies all criteria:
1. `isPublished == true`
2. `status in [ACTIVE, PUBLISHED]`
3. `latitude in -90.0 .. 90.0 && latitude != 0.0`
4. `longitude in -180.0 .. 180.0 && longitude != 0.0`
5. `coordinatesStatus != CoordinatesStatus.PENDING` (must have verified coordinates)

Locations with missing or invalid coordinates are excluded from the map rendering pipeline and marked internally as `LOCATION NEEDS GPS VALIDATION` to prevent displaying false markers at (0,0) in the Atlantic Ocean.

---

## 3. Marker Visual Language & Category Semantics (Section 16)

Markers strictly adopt the Bubakan Green botanical palette:

| Category | Primary Color | Hex Code | Icon Glyph | Semantic Meaning |
|---|---|---|---|---|
| **🌱 Urban Farming** | Emerald Seedling | `#2E7D32` | Seedling (`🌱`) | Food security, vegetable plots, hydroponics |
| **🌿 Taman Toga** | Botanical Amber | `#D97706` | Medicinal Leaf (`🌿`) | Family medicinal plants, herbal conservation |

### Design Rules:
- ❌ **RED is strictly prohibited** for markers because red visually conveys danger, errors, or closed sites.
- Dual visual encoding: Every marker combines a distinct color, an SVG icon glyph, and a text label to ensure accessibility for color-blind users.
- A floating legend is permanently visible on the top-right corner of the map:
  ```
  ┌───────────────────────────────┐
  │  ● 🌱 Kebun Urban Farming     │
  │  ● 🌿 Taman Toga Tradisional  │
  └───────────────────────────────┘
  ```

---

## 4. Marker Tap Interaction & Preview Card (Section 17)

Tapping a marker **does NOT** open a disruptive full-screen modal or giant popup. Instead, it activates a lightweight floating card at the bottom of the screen:

```
┌─────────────────────────────────────────────────────────────┐
│  Urban Farming Kelurahan Bubakan                   [ RW 01 ]│
│  Jl. Raya Bubakan No. 1, Kel. Bubakan                       │
│                                                             │
│  ┌───────────────────────┐   ┌───────────────────────────┐  │
│  │   📍 Petunjuk Arah    │   │      Lihat Kebun →        │  │
│  └───────────────────────┘   └───────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

1. **Information Displayed**:
   - Location Name (1 line, bold)
   - Type Badge & RW Context
   - Short Street Address
2. **Interactive CTAs**:
   - **📍 Petunjuk Arah**: Triggers external Google Maps Intent (`geo:lat,lng?q=...`) for real-time driving/walking navigation.
   - **Lihat Kebun →**: Navigates internally to `LocationDetailScreen(locationId)` within the app.

---

## 5. Anti-Clustering Rationale (Section 18)

Kelurahan Bubakan covers an administrative area of approximately 5.8 km² with an expected density of 5 to 20 community gardens across 7 RWs.
- With this geographic dispersion, marker visual overlap at standard zoom (14 to 16) is minimal.
- Marker clustering adds unnecessary computational overhead, cluster de-spidering complexity, and hides garden identities.
- **Decision**: Render individual markers directly without clustering for MVP.

---

## 6. Privacy & Security Boundary (Section 30 & 31)

1. **Zero Location Permission for Public Map**:
   - Public visitors browsing the map are exploring Bubakan's community assets, not tracking their own personal fitness routes.
   - **The public map requests ZERO GPS permissions.**
2. **Data Minimization in Bridge**:
   - Only public-safe fields are serialized to the JavaScript map layer:
     `{ id, name, type, rw, address, lat, lng }`
   - Private administrative data (PIC phone number, internal notes, audit timestamps, user UIDs) is completely excluded from the map DTO.

---

## 7. Performance & Caching Contract (Section 29 & 41)

1. **Single Fetch Per Screen**:
   - Locations are fetched once when `LocationsScreen` or `HomeScreen` initializes.
   - Panning and zooming the map manipulates the local viewport and **NEVER re-queries Firestore**.
2. **Interaction Latency Targets**:
   - Marker selection to card display: $\le 50\text{ms}$ (Local state update).
   - Card click to `LocationDetailScreen` transition: $\le 200\text{ms}$.
