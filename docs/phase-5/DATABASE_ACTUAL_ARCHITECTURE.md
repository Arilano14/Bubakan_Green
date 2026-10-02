# Bubakan Green — Database Actual Architecture

**Product**: BUBAKAN GREEN (Sistem Informasi Urban Farming & Taman Toga Kelurahan Bubakan)  
**Backend**: Google Cloud Firestore (Serverless NoSQL)  
**Audit Date**: 2026-10-02  
**Verification Method**: Source Code Inspection (`FirestorePlantRepository.kt`, `FirestoreLocationRepository.kt`, `firestore.rules`)  

---

## 1. Actual Collection Identification & Discrepancy Note

### 1.1 Verified Collections in Production Code
Inspection of the actual repository code reveals the following collections in active use:

| Collection Name in Code | Purpose | Repository Class |
|---|---|---|
| `plants` | Master botanical encyclopedia (canonical species) | `FirestorePlantRepository.kt` (`masterPlantsCollection`) |
| `master_plants` | Alias / rule match for canonical botanical records | `firestore.rules`, `web/public/plant.html` |
| `locations` | First-class physical garden plots (Urban Farming & Taman Toga) | `FirestoreLocationRepository.kt` (`collection`) |
| `location_plants` | Junction linking a master species to a physical garden site | `FirestorePlantRepository.kt` (`locationPlantsCollection`) |
| `location_condition_logs` | Audit trail of physical garden condition changes | `FirestoreLocationRepository.kt` (`conditionLogsCollection`) |
| `users` | Role definitions & active account status | `FirebaseAuthRepository.kt`, `firestore.rules` |
| `audit_logs` | Append-only administrative activity audit log | `FirestoreAuditRepository.kt`, `firestore.rules` |

> [!NOTE]
> **Canonical Collection Standardization (RESOLVED)**:
> In `FirestorePlantRepository.kt` (line 22), the master collection has been standardized to `firestore.collection("master_plants")` to match `web/public/plant.html` (line 138), `scripts/seed_default_catalog.py`, and `firestore.rules` (line 39). Both Mobile and Web Fallback now query the exact same canonical collection `/master_plants`.

---

## 2. Detailed Collection Schemas

### 2.1 Collection: `master_plants` (or `plants`)
* **Document ID**: Stable immutable ID (e.g. `pl-xxxxxxxx`, or legacy timestamp ID `PLANT_xxxxxxxxx`).
* **Relationship**: One-to-Many with `location_plants`. One canonical species record is referenced across multiple garden plots. Never duplicated per garden plot.

| Field | Type | Purpose | Required? | Read By | Written By | Relationship |
|---|---|---|---|---|---|---|
| `id` | String | Unique immutable stable ID | Yes | Public, PIC, Admin | Admin | Primary Key |
| `nameId` / `name` | String | Indonesian common name (e.g. Sereh) | Yes | Public, PIC, Admin | Admin | Botanical Identity |
| `scientificName` / `nameLatin` | String | Binomial botanical name (e.g. Cymbopogon citratus) | Yes | Public, PIC, Admin | Admin | Botanical Identity |
| `mandarinName` / `nameMandarin` | String? | Simplified Chinese character (e.g. 柠檬草) | Optional | Public, PIC, Admin | Admin | Trilingual Feature |
| `mandarinPinyin` / `pinyin` | String? | Pinyin pronunciation (e.g. níngméngcǎo) | Optional | Public, PIC, Admin | Admin | Trilingual Feature |
| `mandarinAudioUrl` | String? | HTTPS URL to audio pronunciation guide | Optional | Public, PIC, Admin | Admin | Trilingual Feature |
| `description` | String | Overview and botanical background | Yes | Public, PIC, Admin | Admin | Educational Content |
| `characteristics` | String | Physical morphology & identification notes | Optional | Public, PIC, Admin | Admin | Educational Content |
| `commonUses` / `benefits` | String | Herbal uses, toga remedies, traditional health benefits | Yes | Public, PIC, Admin | Admin | Educational Content |
| `cultivationNotes` / `plantingGuide` | String | Care, watering, soil, and propagation guidance | Optional | Public, PIC, Admin | Admin | Educational Content |
| `defaultPhotoUrl` / `primaryPhotoUrl` | String? | HTTPS URL or local asset identifier | Optional | Public, PIC, Admin | Admin | Media Reference |
| `imageSourceType` | String | `LOCAL` or `REMOTE_URL` | Yes | Public, PIC, Admin | Admin | Metadata |
| `imageAssetName` | String? | Asset file name if bundled locally | Optional | Public, PIC, Admin | Admin | Metadata |
| `imageSource` | String? | Attribution/source of the photograph | Optional | Public, PIC, Admin | Admin | Copyright/Audit |
| `imageLicense` | String? | License type (e.g. CC-BY-SA, Public Domain) | Optional | Public, PIC, Admin | Admin | Copyright/Audit |
| `imageAuthor` | String? | Photographer/author name | Optional | Public, PIC, Admin | Admin | Copyright/Audit |
| `sourceReferences` | String | Literature, pharmacopeia, or journal sources | Optional | Public, PIC, Admin | Admin | Botanical Verification |
| `isPublished` | Boolean | Visibility toggle for public apps | Yes | Public, PIC, Admin | Admin | Governance |
| `createdAt` | Long | Epoch milliseconds creation timestamp | Yes | Public, PIC, Admin | Admin | Audit |
| `updatedAt` | Long | Epoch milliseconds last modification timestamp | Yes | Public, PIC, Admin | Admin | Concurrency/Sync |

---

### 2.2 Collection: `locations`
* **Document ID**: Stable immutable ID (e.g. `loc-xxxxxxxx`, or Firestore auto-ID).
* **Relationship**: One-to-Many with `location_plants`, One-to-Many with `location_condition_logs`.

| Field | Type | Purpose | Required? | Read By | Written By | Relationship |
|---|---|---|---|---|---|---|
| `id` | String | Unique immutable stable ID | Yes | Public, PIC, Admin | Admin | Primary Key |
| `name` | String | Garden name (e.g. Taman Toga RW 03) | Yes | Public, PIC, Admin | Admin | Identity |
| `type` | String | `URBAN_FARMING` or `TAMAN_TOGA` | Yes | Public, PIC, Admin | Admin | Classification |
| `rw` | String | RW administrative division in Bubakan (e.g. "03") | Yes | Public, PIC, Admin | Admin | Territory |
| `address` | String | Physical street address or landmark | Optional | Public, PIC, Admin | Admin | Geo Context |
| `description` | String | Overview of the community garden plot | Yes | Public, PIC, Admin | Admin | Public Info |
| `latitude` | Double | WGS84 latitude coordinate | Yes | Public, PIC, Admin | Admin | Map Navigation |
| `longitude` | Double | WGS84 longitude coordinate | Yes | Public, PIC, Admin | Admin | Map Navigation |
| `coordinatesStatus` | String | `PENDING` or `VERIFIED` | Yes | Public, PIC, Admin | Admin | Quality Gate |
| `featured` | Boolean | Flag to highlight on Home screen banner | Yes | Public, PIC, Admin | Admin | Display Priority |
| `coverPhotoUrl` / `photoUrl` | String? | HTTPS URL to cover photograph | Optional | Public, PIC, Admin | Admin | Visual Header |
| `conditionNote` | String | Recent condition/maintenance note | Optional | Public, PIC, Admin | Admin, PIC | Garden Status |
| `conditionUpdatedAt` | Long? | Epoch millis of last condition change | Optional | Public, PIC, Admin | Admin, PIC | Timeline |
| `conditionUpdatedBy` | String? | Display name/UID of person updating condition | Optional | Public, PIC, Admin | Admin, PIC | Timeline |
| `status` | String | `ACTIVE`, `NEEDS_MAINTENANCE`, `PUBLISHED`, `INACTIVE`, `DRAFT`, `PENDING_APPROVAL`, `ARCHIVED` | Yes | Public, PIC, Admin | Admin | Lifecycle |
| `isPublished` | Boolean | True if visible to public users | Yes | Public, PIC, Admin | Admin | Visibility Gate |
| `createdBy` / `picUid` | String | UID of creator / assigned PIC | Yes | Admin, PIC | Admin, PIC | Ownership |
| `accuracyMeters` | Float? | GPS accuracy reading at capture | Optional | Admin, PIC | Admin, PIC | Telemetry |
| `capturedAt` | Long? | Epoch millis when GPS was acquired | Optional | Admin, PIC | Admin, PIC | Telemetry |
| `rejectionNote` | String? | Revision notes if rejected by Admin | Optional | Admin, PIC | Admin | Review Workflow |
| `createdAt` | Long | Epoch milliseconds creation timestamp | Yes | Public, PIC, Admin | Admin | Audit |
| `updatedAt` | Long | Epoch milliseconds last modification timestamp | Yes | Public, PIC, Admin | Admin | Concurrency/Sync |

---

### 2.3 Collection: `location_plants` (Junction Table)
* **Document ID**: Junction ID (e.g. `LP_xxxxxxxxxx` or Firestore auto-ID).
* **Relationship**: Foreign Key `masterPlantId` references `master_plants.id`; Foreign Key `locationId` references `locations.id`.

| Field | Type | Purpose | Required? | Read By | Written By | Relationship |
|---|---|---|---|---|---|---|
| `id` | String | Unique junction ID | Yes | Public, PIC, Admin | Admin, PIC | Primary Key |
| `locationId` | String | Target garden plot ID | Yes | Public, PIC, Admin | Admin, PIC | FK $\rightarrow$ `locations.id` |
| `plantId` / `masterPlantId` | String | Canonical species ID | Yes | Public, PIC, Admin | Admin, PIC | FK $\rightarrow$ `master_plants.id` |
| `photoUrl` / `localPhotoUrl` | String? | Optional site-specific photo of the planting | Optional | Public, PIC, Admin | Admin, PIC | Plot Media |
| `condition` | String | `GOOD`, `NEEDS_ATTENTION`, `NOT_AVAILABLE` | Yes | Public, PIC, Admin | Admin, PIC | Agronomic State |
| `quantity` | Int | Polybag or plant count | Yes | Public, PIC, Admin | Admin, PIC | Inventory |
| `quantityNote` | String? | Display label (e.g. "12 polybag") | Optional | Public, PIC, Admin | Admin, PIC | Display Format |
| `notes` | String? | Specific planting notes (e.g. "Blok Timur") | Optional | Public, PIC, Admin | Admin, PIC | Plot Notes |
| `isPresent` | Boolean | True if physically growing in this garden | Yes | Public, PIC, Admin | Admin, PIC | Presence Filter |
| `featuredForQr` | Boolean | True if featured on physical QR signage | Yes | Public, PIC, Admin | Admin, PIC | Signage Flag |
| `status` | String | `ACTIVE` or `ARCHIVED` | Yes | Public, PIC, Admin | Admin, PIC | Lifecycle |
| `createdBy` / `updatedBy` | String? | UID or display name of contributor | Optional | Public, PIC, Admin | Admin, PIC | Authorship |
| `createdAt` | Long | Creation timestamp | Yes | Public, PIC, Admin | Admin, PIC | Audit |
| `updatedAt` | Long | Modification timestamp | Yes | Public, PIC, Admin | Admin, PIC | Concurrency/Sync |

---

### 2.4 Collection: `location_condition_logs`
* **Document ID**: Firestore auto-ID.
* **Relationship**: Foreign Key `locationId` references `locations.id`. Append-only.

| Field | Type | Purpose | Required? | Read By | Written By | Relationship |
|---|---|---|---|---|---|---|
| `id` | String | Unique log ID | Yes | Admin | Admin, PIC | Primary Key |
| `locationId` | String | Associated garden ID | Yes | Admin | Admin, PIC | FK $\rightarrow$ `locations.id` |
| `status` | String | New condition status string | Yes | Admin | Admin, PIC | History Snapshot |
| `note` | String | Observational note | Yes | Admin | Admin, PIC | History Snapshot |
| `photoUrl` | String? | Optional site condition snapshot | Optional | Admin | Admin, PIC | History Snapshot |
| `updatedBy` | String | Actor display name or UID | Yes | Admin | Admin, PIC | Audit |
| `updatedAt` | Long | Timestamp of condition update | Yes | Admin | Admin, PIC | Timeline |

---

### 2.5 Collection: `users`
* **Document ID**: Firebase Auth `UID`.
* **Relationship**: Managed directly via Firebase Authentication UID.

| Field | Type | Purpose | Required? | Read By | Written By | Relationship |
|---|---|---|---|---|---|---|
| `uid` | String | Firebase Authentication UID | Yes | Self, Admin | Admin | Matches `request.auth.uid` |
| `name` | String | Full user display name | Yes | Self, Admin | Admin | Identity |
| `email` | String | User login email address | Yes | Self, Admin | Admin | Contact |
| `role` | String | `ADMIN`, `PIC`, or `PUBLIC` | Yes | Self, Admin | Admin | Authorization Token |
| `isActive` | Boolean | Must be `true` for permissions to hold | Yes | Self, Admin | Admin | Revocation Killswitch |
| `assignedLocations` | List<String> | List of `locationId` strings for PIC | Optional | Self, Admin | Admin | Multi-tenant PIC scoping |

---

## 3. Structural Rules & Invariants

1. **Canonical Botanical Record (MasterPlant)**:
   * A species (e.g. *Sereh*) has exactly ONE MasterPlant document.
   * Garden associations exist purely through junction `location_plants` records.
   * No duplication of master records across locations.
2. **Approved Locations**:
   * Approved conceptual locations: `Urban Farming Kelurahan Bubakan` and `Taman Toga RW 03`.
   * No fictional locations may be created.
3. **Delete Safety & Referential Integrity**:
   * Deleting a `Location` or removing a plant from a location performs soft deactivation (`isPublished = false`, `status = INACTIVE` or `isPresent = false`, `status = ARCHIVED`).
   * Master botanical records are never hard-deleted while junction records or physical QR signage exist.
