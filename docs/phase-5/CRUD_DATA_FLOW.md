# Bubakan Green — Admin CRUD Data Flow & Write Strategy

**Product**: BUBAKAN GREEN (Sistem Informasi Urban Farming & Taman Toga Kelurahan Bubakan)  
**Document**: CRUD Architecture & End-to-End Tracing  
**Audit Date**: 2026-10-02  
**Target Audience**: Engineering, Security Audit, Quality Assurance  

---

## 1. Architectural Principles

1. **Android Admin as Sole Management Interface**:
   * All CRUD operations are executed exclusively through the secure Android mobile app via `AdminDashboardScreen`, `MasterPlantFormScreen`, and `LocationApprovalScreen`.
   * No separate or redundant web CMS exists.
2. **Authoritative Backend Security**:
   * Every write operation is validated and authenticated via Firebase Authentication UID and enforced by `firestore.rules` via the `isAdmin()` function.
   * Client-side UI validation is an assistive layer; backend security rules are the authoritative gatekeeper.

---

## 2. End-to-End CRUD Data Flow Tracing

### 2.1 Master Botanical Plant CRUD Flow

```
ADMIN USER
    │
    ▼ [Tap "Tambah Spesies" or "Edit Spesies"]
MasterPlantFormScreen (Jetpack Compose)
    │
    ▼ [User enters botanical metadata: nameId, nameLatin, Mandarin, benefits, image]
Local Form State (MasterPlantFormState)
    │
    ▼ [Client-Side Data Validation: Required fields, HTTPS check, duplicate species detection]
MasterPlantViewModel.savePlant(adminUid)
    │
    ▼ [Constructs MasterPlant domain entity with stable ID or timestamp ID]
PlantRepository.createMasterPlant() / updateMasterPlant()
    │
    ▼ [Firestore Document Reference: plants/{plantId} or master_plants/{plantId}]
Firestore SDK: docRef.set(plant.toMap())
    │
    ▼ [Network Dispatch to Google Cloud Firestore]
Firestore Security Rules Evaluation
    ├─► check: request.auth != null
    └─► check: isAdmin() (token custom claim OR /users/{uid}.role == 'ADMIN' && isActive == true)
    │
    ▼ [Write Committed to Firestore Database]
Audit Trail Emission: AuditRepository.recordAction() -> /audit_logs/{logId}
    │
    ▼ [Firestore Snapshot Trigger / Completion Result]
Repository Result.Success emitted
    │
    ▼ [ViewModel updates StateFlow: isSaving = false, emits successEvent]
MasterPlantFormScreen navigates back / displays toast
    │
    ▼ [Realtime Snapshot Flow notifies active screens]
CatalogScreen, HomeScreen, and PlantDetailScreen re-render with updated botanical record
```

---

### 2.2 Location CRUD & Governance Flow

```
COMMUNITY / PIC / ADMIN
    │
    ▼ [Submit or Edit Garden Plot]
LocationFormScreen / LocationApprovalScreen
    │
    ▼ [Client Validation: Name, Type, RW, GPS latitude/longitude, condition]
LocationApprovalViewModel / AdminDashboardViewModel
    │
    ▼ [Repository Invocation]
LocationRepository.updateLocation() / updateLocationCondition()
    │
    ▼ [Firestore Write: locations/{locationId}]
docRef.set(location.toMap()) or docRef.update(...)
    │
    ▼ [Condition Log: location_condition_logs/{logId}]
Append-only log record created
    │
    ▼ [Firestore Security Rules: isAdmin() enforces authorization]
Write Committed
    │
    ▼ [Audit Log: /audit_logs/{logId}]
Administrative action permanently recorded
    │
    ▼ [Active Snapshot Flow Emits New State]
HomeScreen, LocationScreen, LocationDetailScreen re-render
```

---

## 3. Database Write Strategy & Availability Matrix

| Operation | Entity | Availability in UI | Document ID Strategy | Firestore Method | Fields Changed | Audit Logged? |
|---|---|---|---|---|---|---|
| **CREATE** | `MasterPlant` | **PRESENT** | `plant.id` if specified, or `PLANT_{System.currentTimeMillis()}` | `docRef.set(plant.toMap())` | All botanical fields, `createdAt`, `updatedAt` | YES (`MASTER_PLANT_CREATED` in `/audit_logs`) |
| **READ** | `MasterPlant` | **PRESENT** | Stable ID | `snapshots().map { ... }` | N/A | NO |
| **UPDATE** | `MasterPlant` | **PRESENT** | Existing stable `plant.id` | `masterPlantsCollection.document(plant.id).set(plant.toMap())` | All botanical fields, `updatedAt` | YES (`MASTER_PLANT_UPDATED` in `/audit_logs`) |
| **ARCHIVE / DELETE** | `MasterPlant` | **MISSING** | Existing `plant.id` | *No UI button or repo method exists in current codebase* | N/A | N/A |
| **PUBLISH / UNPUBLISH** | `MasterPlant` | **MISSING** | Existing `plant.id` | *No dedicated UI toggle exists in MasterPlantFormScreen* | N/A | N/A |
| **CREATE** | `Location` | **PRESENT (VIA PIC)**<br>*Direct Admin UI: MISSING* | `location.id` or auto-ID | `collection.document(id).set(location.toMap())` | All garden fields, `status = PENDING_APPROVAL` | YES (`LOCATION_CREATED`) |
| **READ** | `Location` | **PRESENT** | Stable ID | `snapshots().map { ... }` | N/A | NO |
| **UPDATE COND** | `Location` | **PRESENT** | Existing `location.id` | `collection.document(id).update(...)` + `conditionLogs.set(...)` | `status`, `conditionNote`, `conditionUpdatedAt`, `updatedAt` | YES (`LOCATION_CONDITION_UPDATED`) |
| **APPROVE (PUBLISH)** | `Location` | **PRESENT** | Existing `location.id` | `collection.document(id).set(approved.toMap())` | `status = PUBLISHED`, `coordinatesStatus = VERIFIED`, `updatedAt` | YES (`LOCATION_APPROVED`) |
| **DEACTIVATE (UNPUBLISH)**| `Location` | **PRESENT** | Existing `location.id` | `collection.document(id).update("isPublished", false, "status", "INACTIVE", ...)` | `isPublished = false`, `status = INACTIVE`, `updatedAt` | YES (`LOCATION_DEACTIVATED`) |
| **DELETE** | `Location` | **PRESENT (SOFT)** | Existing `location.id` | Delegated to `deactivateLocation(locationId)` | `isPublished = false`, `status = INACTIVE` | YES |
| **ASSIGN (ADD PLANT)** | `LocationPlant` | **PRESENT** | `LP_{System.currentTimeMillis()}` | `locationPlantsCollection.document(id).set(locPlant.toMap())` | `locationId`, `masterPlantId`, `condition`, `quantity`, `isPresent = true` | YES |
| **UPDATE PLANT COND** | `LocationPlant` | **PRESENT** | Existing `locationPlantId` | `locationPlantsCollection.document(id).set(updated.toMap())` | `condition`, `quantity`, `notes`, `isPresent`, `updatedAt` | YES |
| **REMOVE PLANT** | `LocationPlant` | **PRESENT (SOFT)** | Existing `locationPlantId` | `update("isPresent", false, "condition", "NOT_AVAILABLE", "status", "ARCHIVED")` | `isPresent = false`, `condition = NOT_AVAILABLE`, `status = ARCHIVED` | YES |

---

## 4. Rigorous Input Validation Rules

### 4.1 Master Plant Form Validation (`MasterPlantViewModel.kt`)
* **Indonesian Name (`nameId`)**: Must not be blank. Trimmed.
* **Binomial Scientific Name (`nameLatin`)**: Must not be blank. Trimmed.
* **Description & Benefits (`description`, `commonUses`)**: Must not be blank.
* **Remote Photo URL (`primaryPhotoUrl`)**: If not a local asset name (i.e. does not start with `plant_`), must strictly use `https://` protocol. Plain `http://` or non-standard schemes are blocked.
* **Duplicate Botanical Prevention**:
  Before saving a new master plant, the system queries all existing master plants and checks normalized latin names and Indonesian names:
  ```kotlin
  val normCurrentLatin = nameLatinTrim.lowercase().replace(Regex("[^a-z]"), "")
  val normCurrentName = nameIdTrim.lowercase()
  // Rejects duplicate if match found
  ```
  This guarantees that *Sereh* is never duplicated as "Sereh Urban Farming" and "Sereh Taman Toga".
* **Mandarin & Pinyin**: Validated if entered (trimmed, optional).

### 4.2 Location Form Validation (`LocationApprovalViewModel.kt`, `AdminDashboardViewModel.kt`)
* **Location Name**: Must not be blank.
* **Location Type**: Strictly enum `URBAN_FARMING` or `TAMAN_TOGA`.
* **RW**: Must be a valid Kelurahan Bubakan administrative identifier (e.g. "01", "03", "08").
* **Coordinates**: Valid WGS84 double values within Semarang/Bubakan bounds (`latitude ~ -7.0°`, `longitude ~ 110.3°`).
* **Coordinates Status**: Transitions from `PENDING` to `VERIFIED` only upon administrative approval.

---

## 5. Delete Safety & Referential Integrity Guarantee

### 5.1 Preventing Dangling Relationships
The Bubakan Green database architecture enforces **Soft Deletion & Archival (Strategy A)**:
1. **Master Botanical Species (`master_plants`)**:
   * Master plants are NEVER hard-deleted via `collection.delete()`.
   * If a plant is retired, its `isPublished` flag is set to `false`.
   * Because `location_plants` records reference `masterPlantId`, soft-archiving the master species prevents dangling foreign-key references in garden plot inventories.
2. **Garden Plots (`locations`)**:
   * Deleting a location calls `deactivateLocation()`, setting `isPublished = false` and `status = LocationStatus.INACTIVE`.
   * Existing `location_plants` remain associated with the location ID, preserving historical planting records and condition logs.
3. **Planting Junction (`location_plants`)**:
   * Removing a plant from a garden calls `removePlantFromLocation()`, updating `isPresent = false`, `condition = PlantCondition.NOT_AVAILABLE`, and `status = PlantStatus.ARCHIVED`.
   * It is never erased from Firestore, ensuring audit trails and historical reports remain intact.
