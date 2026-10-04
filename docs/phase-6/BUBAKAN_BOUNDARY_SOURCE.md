# Official Administrative Boundary Source & Geospatial Specification — Kelurahan Bubakan

**Product**: BUBAKAN GREEN (Sistem Informasi Urban Farming & Taman Toga)  
**Jurisdiction**: Kelurahan Bubakan, Kecamatan Mijen, Kota Semarang, Jawa Tengah  
**Administrative Code**: `33.74.14.1002`  
**Status**: OFFICIALLY SOURCED, VERIFIED & ENFORCED  

---

## 1. Provenance & Dataset Information

| Parameter | Authoritative Detail |
|---|---|
| **Authoritative Source** | Pemerintah Kota Semarang (Dinas Komunikasi, Informatika, Statistik dan Persandian) |
| **Geoportal URL** | [dataspasial.semarangkota.go.id](https://dataspasial.semarangkota.go.id/) |
| **Layer Endpoint** | `https://dataspasial.semarangkota.go.id/kml/batas_kelurahan.kml` |
| **Dataset Title** | Peta Batas Kelurahan Kota Semarang (Tegal Wareng WebGIS) |
| **Feature Name** | `Bubakan` (FID: 16) |
| **Administrative Level** | Level 4 Indonesia (Kelurahan / Village level) |
| **Coordinate Reference System (CRS)** | WGS 84 (EPSG:4326), `urn:ogc:def:crs:OGC:1.3:CRS84` |
| **Collection & Download Date** | 2026-10-03 |
| **Attribution** | © Pemerintah Kota Semarang — Sistem Informasi Geospasial Warga Kota Semarang |

---

## 2. Geometry & Optimization Metrics

To guarantee flawless mobile WebView performance and zero layout lag on mobile devices, the boundary polygon underwent a controlled Ramer-Douglas-Peucker (RDP) optimization with a strict 2.2-meter maximum deviation threshold ($\epsilon = 0.00002^\circ$).

| Metric | Original Geometry | Optimized Mobile Geometry | Variance / Fidelity |
|---|---|---|---|
| **Format** | KML LinearRing / Polygon | GeoJSON Feature Polygon | Identical topology |
| **Vertices Count** | 250 vertices | 182 vertices | 27.2% reduction in points |
| **Calculated Polygon Area** | $2.54928\text{ km}^2$ | $2.54866\text{ km}^2$ | $\mathbf{0.0241\%}$ difference |
| **Official Gov Land Area** | $2.57964\text{ km}^2$ | $2.57964\text{ km}^2$ | $98.8\%$ match with land records |
| **Bounding Box** | `110.31365 to 110.33041 E`<br>`-7.10638 to -7.08322 S` | `110.31365 to 110.33041 E`<br>`-7.10638 to -7.08322 S` | Identical spatial bounds |
| **Centroid (Lng, Lat)** | `110.32203, -7.09480` | `110.32203, -7.09480` | Identical center |
| **Local File Size** | 20.6 KB (pretty JSON) | **4.55 KB** (compact GeoJSON) | Negligible mobile overhead |
| **Local Storage Path** | N/A (Source KML: 1.29 MB) | `app/src/main/assets/map/bubakan_boundary.geojson` | Local offline bundled |

---

## 3. Neighboring Boundaries Sanity Cross-Check

The extracted boundary was verified against historical territorial boundaries and surrounding Kelurahan placemarks from the same official dataset:

- **North**: Kelurahan Tambangan (Centroid: `110.31806, -7.07949`) — *Verified North*
- **South**: Kecamatan Boja, Kabupaten Kendal (Southern rim of Bubakan at `lat: -7.10638`) — *Verified South*
- **West**: Kelurahan Cangkiran (Centroid: `110.30898, -7.09316`) — *Verified West*
- **East**: Kelurahan Polaman (`110.33620, -7.09110`), Purwosari (`110.33356, -7.07806`), Karangmalang (`110.33414, -7.09862`) — *Verified East*

**Sanity Check Verdict**: The polygon boundary strictly fits within the administrative borders of Kelurahan Bubakan without encroaching upon or including any neighboring Kelurahan.

---

## 4. Government Anchor Coordinate Verification

The official Kantor Kelurahan Bubakan facility was cross-checked with the polygon:
- **Facility**: Kantor Kelurahan Bubakan
- **Address**: Jl. Bubakan, Kecamatan Mijen, Kota Semarang, 50216
- **Official Coordinates**: `Latitude: -7.09236996, Longitude: 110.3203602`
- **Point-in-Polygon Result**: **`TRUE`** (Located comfortably within the polygon, ~320m from centroid)

---

## 5. Architectural Enforcement Contract

1. **Map View Lock (Leaflet)**:
   - Initial Camera: `fitBounds(bubakanBoundaryLayer.getBounds(), { padding: [20, 20] })`
   - Movement Constraint: `map.setMaxBounds(bubakanBounds.pad(0.20))` (20% visual context buffer to prevent arbitrary panning across Central Java while keeping nearby roads usable).
   - Boundary Rendering: Subtle organic boundary outline (Stroke: `#1B5E20`, weight 2, dashArray `4, 4`, fill: `#2E7D32` at 6% opacity) ensuring complete legibility of underlying roads and garden markers.

2. **Geospatial Point-in-Polygon Validation (Kotlin & Map)**:
   - Implemented in `BubakanGeoValidator.kt` using the Jordan Curve (Ray-Casting) theorem.
   - Every location must pass `BubakanGeoValidator.isInsideBubakan(latitude, longitude)` before being accepted in `LocationFormViewModel` or emitted as a public map marker.
