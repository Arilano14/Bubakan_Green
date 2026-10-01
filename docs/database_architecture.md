# Bubakan Green — Cloud Firestore & Storage Database Architecture

**Document Version:** 1.0.0  
**Target Environment:** Firebase Cloud Firestore & Cloud Storage for Firebase (Production & Local Emulator)  
**Security Model:** Role-Based Access Control (Public Read / Admin Write)

---

## 1. Architectural Overview & Single Source of Truth

Bubakan Green operates on a decoupled relational document model in Cloud Firestore designed to:
1. Prevent botanical duplication (e.g., *Cymbopogon citratus* exists physically in both Urban Farming and Taman Toga RW 03, but is stored once in `plants`).
2. Maintain strict mobile performance and zero unnecessary database read costs.
3. Completely eliminate binary image storage in database documents (zero base64 strings in Firestore; binary assets stored in Cloud Storage or app bundle).

```
[users] ──(uid)──> [audit_logs]
                      │
[locations] ◄─── [location_plants] ───► [plants / master_plants]
                      │                         │
            (physical condition)          (botanical taxonomy)
```

---

## 2. Core Collections Schema Specification

### 2.1 Collection: `users`
Stores authorized administrative and field officer profiles.

| Field Name | Type | Purpose | Example Value |
|:---|:---|:---|:---|
| `uid` | String | Firebase Auth UID (Document Key) | `"admin_kelurahan_01"` |
| `email` | String | Official contact email | `"admin@bubakangreen.id"` |
| `displayName` | String | Full user name | `"Pengelola Kelurahan Bubakan"` |
| `role` | String | Access control role (`ADMIN` / `PIC`) | `"ADMIN"` |
| `isActive` | Boolean | Account operational status | `true` |
| `createdAt` | Timestamp | Account generation date | `2026-09-23T08:00:00Z` |
| `lastLoginAt` | Timestamp | Last authentication session | `2026-10-01T07:15:00Z` |

---

### 2.2 Collection: `locations`
Documents registered community garden plots (Urban Farming & Taman Toga).

| Field Name | Type | Purpose | Example Value |
|:---|:---|:---|:---|
| `id` | String | Unique slug / location identifier | `"urban-farming-bubakan"` |
| `name` | String | Official garden name | `"Kebun Urban Farming RW 01"` |
| `type` | String | Categorization (`URBAN_FARMING` / `TAMAN_TOGA`) | `"URBAN_FARMING"` |
| `rw` | String | Neighborhood ward number in Bubakan | `"01"` |
| `address` | String | Physical road address / landmark | `"Jl. Raya Bubakan No. 12"` |
| `description` | String | Detailed educational overview | `"Pusat budidaya sayuran hidroponik warga..."` |
| `status` | String | State (`PUBLISHED`, `PENDING_APPROVAL`, `ACTIVE`) | `"PUBLISHED"` |
| `latitude` | Number | Verified GPS latitude coordinate | `-7.065432` |
| `longitude` | Number | Verified GPS longitude coordinate | `110.328911` |
| `coordinatesStatus` | String | GPS verification tag (`VERIFIED` / `PENDING`) | `"VERIFIED"` |
| `photoUrl` | String (URL) | Cloud Storage HTTPS image link | `"https://firebasestorage.googleapis.com/.../hero.webp"` |
| `isPublished` | Boolean | Visibility flag for public catalog | `true` |
| `createdAt` | Timestamp | Date added to directory | `2026-09-24T10:00:00Z` |
| `updatedAt` | Timestamp | Last record modification timestamp | `2026-10-01T06:30:00Z` |

---

### 2.3 Collection: `plants` (or `master_plants`)
The authoritative trilingual botanical encyclopedia. Exactly one record per botanical species.

| Field Name | Type | Purpose | Example Value |
|:---|:---|:---|:---|
| `id` | String | Canonical species ID | `"sereh"` |
| `nameId` | String | Common Indonesian name | `"Sereh"` |
| `nameLatin` | String | Scientific binomial botanical name | `"Cymbopogon citratus"` |
| `family` | String | Botanical family classification | `"Poaceae"` |
| `nameMandarin` | String | Trilingual Hanzi representation | `"柠檬草"` |
| `pinyin` | String | Pinyin transcription with tone marks | `"níng méng cǎo"` |
| `description` | String | Short overview of the species | `"Tanaman rumput aromatik beraroma sitrun segar..."` |
| `characteristics`| String | Botanical physical morphology | `"Rumpun menahun berdaun pita menyempit, tepi kasar..."` |
| `commonUses` | String | Traditional health and culinary uses | `"Bumbu dapur, aromaterapi penenang, teh herbal..."` |
| `cultivationNotes`| String | Urban farming planting & soil advice | `"Mudah tumbuh dari anakan, butuh sinar matahari penuh..."` |
| `imageSourceType`| String | Source indicator (`LOCAL` / `REMOTE_URL`) | `"LOCAL"` |
| `imageAssetName` | String | Internal Android drawable asset reference | `"plant_sereh"` |
| `primaryPhotoUrl`| String (URL) | Cloud Storage HTTPS web link | `"https://firebasestorage.googleapis.com/.../sereh.webp"` |
| `imageAuthor` | String | Legal attribution author | `"Forest & Kim Starr"` |
| `imageLicense` | String | Creative Commons license identifier | `"CC BY-SA 3.0"` |
| `sourceReferences`| String | Botanical taxonomy authority | `"Royal Botanic Gardens, Kew (POWO)"` |
| `mandarinAudioUrl`| String (URL) | Audio pronunciation stream link | `"https://.../audio/ning_meng_cao.mp3"` |

---

### 2.4 Collection: `location_plants`
Relational bridge mapping which master species are planted in which garden plots.

| Field Name | Type | Purpose | Example Value |
|:---|:---|:---|:---|
| `id` | String | Composite relationship ID | `"uf-sereh"` |
| `locationId` | String | Foreign key to `locations.id` | `"urban-farming-bubakan"` |
| `masterPlantId` | String | Foreign key to `plants.id` | `"sereh"` |
| `localCustomName`| String | Garden-specific identifier/bed name | `"Sereh Dapur Bedengan 1"` |
| `condition` | String | Health (`EXCELLENT`, `HEALTHY`, `NEEDS_CARE`) | `"HEALTHY"` |
| `plantedAt` | Timestamp | Estimated planting date | `2026-08-15T00:00:00Z` |
| `verifiedAt` | Timestamp | Field officer verification date | `2026-09-28T09:00:00Z` |

---

### 2.5 Collection: `plant_categories`
Classification taxonomies for catalog filtering.

| Field Name | Type | Purpose | Example Value |
|:---|:---|:---|:---|
| `id` | String | Category key | `"tanaman-toga"` |
| `label` | String | Display title | `"Tanaman Obat Keluarga (Toga)"` |
| `icon` | String | Emoji/Symbol marker | `"🌿"` |
| `displayOrder` | Number | Sort weight in UI filter bar | `1` |

---

### 2.6 Collection: `audit_logs`
Immutable compliance and security record of all administrative changes.

| Field Name | Type | Purpose | Example Value |
|:---|:---|:---|:---|
| `id` | String | Auto-generated log ID | `"log_20261001_001"` |
| `timestamp` | Timestamp | Action occurrence time | `2026-10-01T08:00:00Z` |
| `actorUid` | String | Authenticated user ID | `"admin_kelurahan_01"` |
| `actorRole` | String | Role at time of action | `"ADMIN"` |
| `action` | String | Executed operation verb | `"MASTER_PLANT_CREATED"` |
| `targetEntity` | String | Affected entity identifier | `"plants/sereh"` |
| `details` | String | Descriptive audit parameters | `"Created botanical record Cymbopogon citratus"` |

---

## 3. Image Storage Strategy (Zero Cost, No Blobs in Firestore)

### 3.1 Prohibited Practices
- **NO Base64 Strings in Firestore:** Base64 encoding inflates document size by ~33%, drastically increasing Firestore read bandwidth and risking the 1MB document limit.
- **NO Raw High-Res Uploads:** Avoid uncompressed camera originals (>10MB).

### 3.2 Standard Production Cloud Storage Architecture
When deployed to Firebase Cloud Storage, assets follow standard dual-resolution hierarchy:
```
gs://bubakangreen.appspot.com/
├── plants/
│   ├── sereh/
│   │   ├── thumbnail.webp   (400x400 px, ~25 KB)
│   │   └── detail.webp      (1280x853 px, ~120 KB)
│   └── cabai/
│       ├── thumbnail.webp
│       └── detail.webp
└── locations/
    ├── urban-farming-bubakan/
    │   └── hero.webp        (1280x720 px, ~140 KB)
    └── taman-toga-rw03/
        └── hero.webp
```

In Firestore, only the resulting public HTTPS URL string (`https://firebasestorage.googleapis.com/...`) is saved in `primaryPhotoUrl` or `photoUrl`.

---

## 4. Query Efficiency & Indexing

1. **Composite Indexes Required:**
   - `locations`: `status` (Ascending) + `type` (Ascending) + `createdAt` (Descending) for filtered directory queries.
   - `location_plants`: `locationId` (Ascending) + `condition` (Ascending) for fast garden inventory lookups.
2. **Lightweight Document Payloads:**
   - Average document size in `plants`: ~1.2 KB.
   - Average document size in `locations`: ~0.8 KB.
   - Paging: `limit(20)` on catalog exploration to maintain constant sub-50ms query times.

---

## 5. Security Rules Verification
- **Read Access:** Unauthenticated public users can freely read `isPublished == true` locations, all `plants` encyclopedia entries, and `location_plants`.
- **Write Access:** Enforced strictly via Firebase Authentication and Firestore Security Rules (`isAdmin()` verifying `request.auth.token.role == 'ADMIN'` or `users/{uid}.role == 'ADMIN'`). Unauthenticated writes and non-admin modifications are rejected at the database engine level.
