# Android Firebase Data Flow

**Project:** Bubakan Green  
**Package:** `id.bubakangreen.app`  
**Firebase Project:** `bubakan-green`  
**Status:** IMPLEMENTED / TESTED  

---

## 1. Overview Architecture

The Bubakan Green Android application connects to Cloud Firestore using an MVVM + Clean Architecture flow with Kotlin Coroutines and Flows.

```
┌────────────────────────────────────────────────────────┐
│                   Google Services                      │
│               (app/google-services.json)               │
└──────────────────────────┬─────────────────────────────┘
                           │ registers
                           ▼
┌────────────────────────────────────────────────────────┐
│             FirebaseApp & SDK Initialization           │
│        FirebaseFirestore.getInstance()                 │
│        FirebaseAuth.getInstance()                      │
└──────────────────────────┬─────────────────────────────┘
                           │ injected via Service Locator
                           ▼
┌────────────────────────────────────────────────────────┐
│             RepositoryProvider                         │
│  - FirestorePlantRepository(db)                        │
│  - FirestoreLocationRepository(db)                     │
│  - FirebaseAuthRepository(auth, db)                    │
└──────────────────────────┬─────────────────────────────┘
                           │ exposed as Kotlin Flow<List<T>>
                           ▼
┌────────────────────────────────────────────────────────┐
│                 ViewModel Layer                        │
│  - CatalogViewModel (Plant list, search, filters)      │
│  - PlantDetailViewModel (Single MasterPlant by ID)     │
│  - LocationViewModel (Locations list, map, detail)     │
│  - AdminDashboardViewModel / PicDashboardViewModel     │
└──────────────────────────┬─────────────────────────────┘
                           │ emits UiState (Loading / Success / Empty / Error)
                           ▼
┌────────────────────────────────────────────────────────┐
│             Jetpack Compose UI Screens                 │
│  - CatalogScreen (Katalog)                             │
│  - PlantDetailScreen                                   │
│  - LocationScreen (Lokasi & Taman Toga)                │
│  - LocationDetailScreen                                │
└────────────────────────────────────────────────────────┘
```

---

## 2. Canonical Collection Endpoints

Android interacts exclusively with canonical collections:

| Collection | Schema Model | Read Access | Write Access | ViewModel Consumer |
|---|---|---|---|---|
| `/master_plants` | `MasterPlant.kt` | Public (Snapshot Listener) | Admin Only | `CatalogViewModel`, `PlantDetailViewModel`, `AdminPlantFormViewModel` |
| `/locations` | `Location.kt` | Public (Snapshot Listener) | Admin Only | `LocationViewModel`, `LocationDetailViewModel`, `AdminLocationFormViewModel` |
| `/location_plants` | `LocationPlant.kt` | Public (Snapshot Listener) | Admin Only | `LocationDetailViewModel`, `PicDashboardViewModel` |
| `/location_condition_logs`| `LocationConditionLog.kt` | Public Read | PIC / Admin Write | `LocationDetailViewModel`, `PicLogViewModel` |
| `/users` | `User.kt` | Authenticated Self / Admin | Authenticated Self / Admin | `AuthViewModel`, `ProfileViewModel` |
| `/audit_logs` | `AuditLog.kt` | Admin Only | Admin Only | `AdminDashboardViewModel` |

---

## 3. Realtime Flow Implementation

### Master Plants Flow (`FirestorePlantRepository.kt`)
```kotlin
override fun observeAllPlants(): Flow<List<MasterPlant>> = callbackFlow {
    val listener = firestore.collection("master_plants")
        .addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val plants = snapshot?.documents?.mapNotNull { it.toObject(MasterPlant::class.java) } ?: emptyList()
            trySend(plants)
        }
    awaitClose { listener.remove() }
}
```

### Locations Flow (`FirestoreLocationRepository.kt`)
```kotlin
override fun observeAllLocations(): Flow<List<Location>> = callbackFlow {
    val listener = firestore.collection("locations")
        .addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val locations = snapshot?.documents?.mapNotNull { it.toObject(Location::class.java) } ?: emptyList()
            trySend(locations)
        }
    awaitClose { listener.remove() }
}
```

---

## 4. UI State Transitions & Graceful Fallback

ViewModels consume the repository Flows and expose a reactive `UiState`:
- **`UiState.Loading`**: Displayed while initial snapshot listener is connecting.
- **`UiState.Success(data)`**: Displayed when Firestore returns documents.
- **`UiState.Empty`**: Displayed when collection query returns 0 documents (`"Belum ada tanaman terdata"` / `"Belum ada kebun terdaftar"`). Prevents jarring errors when database is fresh.
- **`UiState.Error(message)`**: Displayed when snapshot stream fails (e.g., `PERMISSION_DENIED`). Transformed into user-friendly message (`"Oops! Ada sedikit kendala"`) without exposing raw exceptions to end users.

---

## 5. Security Rules Compatibility

The Android app client connects without authentication by default for public catalog viewing:
- `firestore.rules` must allow `read: if true;` for `/master_plants`, `/locations`, and `/location_plants`.
- Conditional read filters such as `if resource.data.isPublished == true` must NOT be placed on root collections where client queries `collection("master_plants")` without explicit `.whereEqualTo("isPublished", true)` clauses, otherwise Firestore evaluates security rules as request constraints (not data filters) and rejects the entire stream with `PERMISSION_DENIED`.
