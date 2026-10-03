# Location Data Contract & Map Consistency Specification — Bubakan Green

**Product**: BUBAKAN GREEN (Sistem Informasi Urban Farming & Taman Toga Kelurahan Bubakan)  
**Firestore Collection**: `/locations`  
**Status**: APPROVED DATA CONTRACT (PHASE 6)  

---

## 1. Single Source of Truth Principle (Section 10)

All garden location visualizations across the application—including the **Location List ("Daftar Kebun")**, **Interactive Map ("Peta Sebaran")**, and **Home Mini-Map**—are driven exclusively by the single Firestore collection:

```
/locations/{locationId}
```

Creating duplicate collections (e.g. `map_markers`, `public_locations`, or `cached_plots`) is strictly prohibited.

---

## 2. Document Schema & Map DTO Contract

### 2.1 Firestore `/locations/{id}` Document Specification

| Field Name | Type | Mandatory for Map? | Validation Constraints | Description |
|---|---|---|---|---|
| `id` | String | **YES** | `^[a-z0-9_-]+$` | Unique stable identifier (e.g. `loc-urban-farming-01`) |
| `name` | String | **YES** | Non-empty, $\le 60$ chars | Official garden name |
| `type` | String | **YES** | `URBAN_FARMING` \| `TAMAN_TOGA` | Category determining marker color & icon |
| `rw` | String | **YES** | `^0[1-7]$` | Administrative RW in Kelurahan Bubakan |
| `address` | String | Optional | $\le 120$ chars | Street address or local landmark |
| `description` | String | Optional | Non-empty text | Civic background & cultivation context |
| `latitude` | Double | **YES** | $-90.0 \le \text{lat} \le 90.0 \land \text{lat} \ne 0.0$ | GPS Latitude coordinate |
| `longitude` | Double | **YES** | $-180.0 \le \text{lng} \le 180.0 \land \text{lng} \ne 0.0$ | GPS Longitude coordinate |
| `coordinatesStatus`| String | **YES** | `VERIFIED` \| `PENDING` | Verification status of captured coordinates |
| `isPublished` | Boolean | **YES** | `true` for public visibility | Publication visibility flag |
| `status` | String | **YES** | `ACTIVE` \| `PUBLISHED` | Lifecycle status |
| `featured` | Boolean | Optional | Default `false` | Highlighted in Home featured reel |
| `coverPhotoUrl` | String | Optional | HTTPS URL or local asset name | Garden photo for preview card |

---

## 3. Strict Coordinate Validation Rules (Section 24)

Coordinates must represent actual, physically verified garden locations within or adjacent to Kelurahan Bubakan, Kecamatan Mijen, Kota Semarang (approximate bounding box: Latitude $-7.090$ to $-7.050$, Longitude $110.310$ to $110.350$).

### 3.1 Rejection Criteria:
- `latitude == 0.0 && longitude == 0.0` &rarr; **REJECTED** (Null Island default).
- `latitude < -90.0 || latitude > 90.0` &rarr; **REJECTED** (Invalid latitude).
- `longitude < -180.0 || longitude > 180.0` &rarr; **REJECTED** (Invalid longitude).
- `coordinatesStatus == "PENDING"` &rarr; **EXCLUDED FROM PUBLIC MAP** until verified.

### 3.2 Error State Handling:
Locations that lack verified GPS coordinates are flagged as:
```
LOCATION NEEDS GPS VALIDATION
```
Such locations continue to appear in the administrative audit list and the public text list (with a *"Koordinat Belum Terverifikasi"* badge), but **are never plotted as a false marker on the map**.

---

## 4. Map + List Consistency Guarantee (Section 22)

To avoid data discrepancies where a user sees a garden in the list but cannot locate it on the map:

$$\text{Count}(\text{List}) = \text{Count}(\text{Map}) + \text{Count}(\text{Needs GPS Validation})$$

1. **Shared State**: Both list and map views consume the identical `LocationsUiState` emitted by `LocationsViewModel.loadLocations()`.
2. **Transparent Exclusion Counter**: If 5 locations are published but 1 has pending coordinates:
   - The map tab displays a subtle info chip: *"4 lokasi dipetakan • 1 lokasi menunggu verifikasi GPS"*.
   - Selecting the unmapped location in the list displays an honest banner: *"Titik peta sedang dalam proses verifikasi lapangan oleh PIC."*
