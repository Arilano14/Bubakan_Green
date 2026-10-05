# Data Source Matrix

**Project:** Bubakan Green  
**Architecture Principle:** ONE PRODUCTION SOURCE OF TRUTH (Cloud Firestore)  
**Firebase Project:** `bubakan-green`  
**Database:** `(default)`  

---

## 1. Canonical Collection Hierarchy

All platforms (Android App, Website Fallback, and QR system) consume the exact same Firestore collections without divergence or localized shadow databases.

```
                            Cloud Firestore
                           (bubakan-green)
                                  │
    ┌─────────────────────────────┼─────────────────────────────┐
    │                             │                             │
    ▼                             ▼                             ▼
/master_plants               /locations                  /location_plants
(Canonical Plant Entity)     (Garden & Land Parcels)     (Physical Plant Placement)
```

---

## 2. Collection Mapping Matrix

| Collection Path | Description | Android Model | Android Repository | Web Endpoint | Seeder Source |
|---|---|---|---|---|---|
| `/master_plants` | Catalog of 9 approved herbal plants (Indonesian, Latin, Mandarin, photo, description). | `MasterPlant.kt` | `FirestorePlantRepository` | `GET .../master_plants/{id}` | `docs/data/DEFAULT_PLANT_CATALOG.json` |
| `/locations` | 2 approved featured gardens (Urban Farming Kelurahan Bubakan, Taman Toga RW 03). | `Location.kt` | `FirestoreLocationRepository` | `GET .../locations/{id}` | `docs/data/DEFAULT_LOCATIONS.json` |
| `/location_plants` | Many-to-many relationship linking a garden to canonical plants with local notes. | `LocationPlant.kt` | `FirestoreLocationRepository` | — | `docs/data/DEFAULT_LOCATION_PLANTS.json` |
| `/location_condition_logs` | Real-time garden status, health indicators, soil observations. | `LocationConditionLog.kt` | `FirestoreLocationRepository` | — | Dynamic App Logs |
| `/users` | User accounts, PIC assignments, Admin roles. | `User.kt` | `FirebaseAuthRepository` | — | Firebase Auth / Admin |
| `/audit_logs` | Immutable audit trail for Admin CRUD actions. | `AuditLog.kt` | `FirestoreAuditLogRepository` | — | Admin Operations |

---

## 3. Strict Anti-Patterns (Forbidden Collections)

To prevent fragmented data architectures, the following collections are explicitly forbidden:

- ❌ `/plants` (deprecated legacy naming; replaced by canonical `/master_plants`)
- ❌ `/plant_data` (deprecated)
- ❌ `/mobile_plants` (must never create mobile-only plant collections)
- ❌ `/web_plants` (must never create web-only plant collections)
- ❌ Hardcoded JSON fixtures serving as production truth in web or mobile bundles.

---

## 4. Entity Relationship Schema

### A. MasterPlant (`/master_plants/{plantId}`)
```json
{
  "id": "sereh",
  "nameId": "Sereh",
  "nameLatin": "Cymbopogon citratus",
  "nameMandarin": "柠檬草",
  "pinyin": "níng méng cǎo",
  "description": "Tanaman herbal aromatik dengan khasiat meredakan nyeri, menurunkan demam, dan melancarkan pencernaan.",
  "category": "HERBAL",
  "primaryPhotoUrl": "",
  "isPublished": true,
  "createdAt": 1740000000000,
  "updatedAt": 1740000000000
}
```

### B. Location (`/locations/{locationId}`)
```json
{
  "id": "loc_urban_farming_bubakan",
  "name": "Urban Farming Kelurahan Bubakan",
  "type": "URBAN_FARMING",
  "rw": "01",
  "address": "Kompleks Kantor Kelurahan Bubakan, Jl. Raya Bubakan",
  "description": "Pusat percontohan urban farming dan ketahanan pangan mandiri Kelurahan Bubakan.",
  "status": "ACTIVE",
  "isPublished": true,
  "featured": true,
  "latitude": null,
  "longitude": null,
  "createdAt": 1740000000000,
  "updatedAt": 1740000000000
}
```

### C. LocationPlant (`/location_plants/{relationId}`)
```json
{
  "id": "rel_uf_sereh",
  "locationId": "loc_urban_farming_bubakan",
  "masterPlantId": "sereh",
  "locationName": "Urban Farming Kelurahan Bubakan",
  "masterPlantName": "Sereh",
  "quantity": 15,
  "healthStatus": "HEALTHY",
  "notes": "Ditanam di bedengan herbal sisi timur",
  "isPublished": true,
  "createdAt": 1740000000000,
  "updatedAt": 1740000000000
}
```

---

## 5. Synchronization Flow

1. **Write Once:** Admin edits plant description via Android Admin Screen or Firebase Admin SDK.
2. **Realtime Push:** Android users currently on `CatalogScreen` or `PlantDetailScreen` receive instant snapshot updates via Firestore WebSocket watch stream.
3. **Web Instant:** Users scanning physical QR code with any smartphone browser fetch the updated REST document on demand.
4. **No Re-compilation:** Zero APK updates and zero Web deployments are required to update plant knowledge.
