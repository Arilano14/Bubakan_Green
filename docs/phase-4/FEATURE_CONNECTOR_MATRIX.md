# FEATURE CONNECTOR MATRIX — BUBAKAN GREEN

**Sistem Informasi Urban Farming & Taman Toga**  
**Kelurahan Bubakan, Kecamatan Mijen, Kota Semarang**  
**Document:** `docs/phase-4/FEATURE_CONNECTOR_MATRIX.md`  
**Date:** 2026-09-24  
**Auditor Role:** Senior Android Architect & Integration Auditor  
**Status:** PASS — ALL CONNECTORS AUDITED & VERIFIED  

---

## 1. Connector Chain Integrity Principle

Every production feature in **BUBAKAN GREEN** must follow a strictly decoupled, end-to-end architectural chain:

```
Composable UI ──(Intent)──► ViewModel ──(Query)──► Repository ──► Data Source ──► Firebase / Cache
     ▲                         │                                                        │
     │                         ▼                                                        ▼
UiState<T> ◄──(StateFlow)── ViewModel ◄──────────(Result<T> Flow)───────────────────────┘
```

**Anti-Pattern Prohibitions Enforced:**
- ❌ No `// TODO` dummy connectors in production code paths.
- ❌ No mock connectors masquerading as real production implementations.
- ❌ No Composable function querying Firestore or Firebase directly.
- ❌ No hard-coded mock responses in repositories.
- ❌ No fake Firebase credentials or fabricated backend responses.

---

## 2. End-to-End Feature Connector Matrix

| Feature | UI Component | ViewModel | Repository Interface | Data Source Impl | Firebase Collection / Disk Cache | Success State | Error State | Offline State | Automated Test | Audit Status |
|:---|:---|:---|:---|:---|:---|:---|:---|:---|:---|:---:|
| **1. Featured Garden Banner** | `HomeScreen` / `FeaturedLocationBanner` | `HomeViewModel` | `LocationRepository.getFeaturedLocations()` | `FirestoreLocationRepository` | Firestore `/locations` (`featured == true`, `status == "PUBLISHED"`) + 100MB Disk Cache | `UiState.Success<List<Location>>` | `UiState.Error` with human message | Displays cached featured garden immediately + `OfflineStatusBar` | `HomeViewModelTest.loadData_loadsFeaturedLocationsAndPopularPlants` | ✅ VERIFIED |
| **2. Public Garden Directory** | `LocationsScreen` / `LocationCard` | `LocationsViewModel` | `LocationRepository.getPublishedLocations()` | `FirestoreLocationRepository` | Firestore `/locations` (`status == "PUBLISHED"`) + Disk Cache | `UiState.Success<List<Location>>` | `UiState.Error` with retry CTA | Displays cached gardens + non-intrusive offline pill | `LocationsViewModelTest.loadLocations_exposesAllLocationsByDefault` | ✅ VERIFIED |
| **3. Garden Category Filtering** | `LocationsScreen` / `CategoryFilterChip` | `LocationsViewModel` | `LocationRepository.getLocationsByType(type)` | `FirestoreLocationRepository` | Firestore `/locations` (`type == type`, `status == "PUBLISHED"`) + Disk Cache | Filtered `UiState.Success` | `UiState.Error` | In-memory filter on cached gardens | `LocationsViewModelTest.filterByCategory_urbanFarming_returnsOnlyUrbanFarming` | ✅ VERIFIED |
| **4. Segmented Map & Pin Selection** | `LocationsScreen` / `MapVisualContainer` | `LocationsViewModel` | `LocationRepository` (Coordinates & Pin binding) | `FirestoreLocationRepository` | Firestore `/locations` (latitude, longitude, coordinatesStatus) | `selectedMapLocation` set in state + Floating Card | Empty view if 0 pins | Renders cached coordinates pin layout | `LocationsViewModelTest.selectMapLocation_updatesSelectedLocation` | ✅ VERIFIED |
| **5. External Map Directions** | `LocationsScreen` & `LocationDetailScreen` / `Button` | N/A (Direct Intent) | Android OS Location Manager | Android System Intent (`Intent.ACTION_VIEW`, `geo:lat,lng`) | Android Google Maps package / Web browser fallback | External Google Maps navigation launched | Browser Maps fallback | Works offline if offline map tiles downloaded on phone | Manual intent verification | ✅ VERIFIED |
| **6. Garden Detail & On-Site Inventory** | `LocationDetailScreen` | `LocationDetailViewModel` | `LocationRepository.getLocationById()` & `PlantRepository.getPlantsAtLocation()` | `FirestoreLocationRepository` & `FirestorePlantRepository` | Firestore `/locations/{id}` and `/location_plants` (`locationId == id`) + Disk Cache | Profile + `UiState.Success<List<MasterPlant>>` | `UiState.Error` with retry CTA | Full cached profile & on-site plants displayed | Component & ViewModel verification | ✅ VERIFIED |
| **7. Botanical Catalog Browsing** | `CatalogScreen` / `PlantCard` | `CatalogViewModel` | `PlantRepository.getAllMasterPlants()` | `FirestorePlantRepository` | Firestore `/master_plants` + Disk Cache | `UiState.Success<List<MasterPlant>>` | `UiState.Error` with retry CTA | Cached encyclopedia records rendered | `CatalogViewModelTest.loadAllPlants_exposesSuccessStateWithAllPlants` | ✅ VERIFIED |
| **8. Debounced Botanical Search** | `CatalogScreen` / `OutlinedTextField` | `CatalogViewModel` | `PlantRepository` (In-memory cached species query) | `FirestorePlantRepository` | In-memory `allPlantsCache` with `Flow.debounce(300)` | Instant filtered `UiState.Success` | `UiState.Empty` with "Reset Pencarian" CTA | Search runs completely offline against local cache | `CatalogViewModelTest.searchByIndonesianName_filtersAccuratelyAfterDebounce` | ✅ VERIFIED |
| **9. Botanical Knowledge Detail** | `PlantDetailScreen` / `KnowledgeSectionCard` | `PlantDetailViewModel` | `PlantRepository.getMasterPlantById(id)` | `FirestorePlantRepository` | Firestore `/master_plants/{id}` + Disk Cache | Full botanical knowledge card | `UiState.Error` with retry CTA | Cached plant profile renders offline | Component & ViewModel verification | ✅ VERIFIED |
| **10. Mandarin Audio Pronunciation** | `PlantDetailScreen` / `MandarinSpeakerButton` | `PlantDetailViewModel` | `AudioPlayer` interface | `AndroidAudioPlayer` wrapping `android.media.MediaPlayer` | Remote MP3 URL (`mandarinAudioUrl`) | `AudioState.Playing` (single-play) ──► `Idle` | `AudioState.Error` -> Snackbar alert | Graceful disabled state if audio file not cached | `AudioPlayer` interface contract verification | ✅ VERIFIED |
| **11. QR Code / App Link Routing** | `MainActivity` / `BubakanNavHost` | `NavHost` deepLink | `NavigationRoutes` | Android App Link Intent Filter | HTTPS URL `https://bubakangreen.web.app/plant/{id}` | Direct navigation to `PlantDetailScreen` | Falls back to Home root if plant not found | Opens cached detail if previously opened | `QrUrlBuilderTest.buildPlantUrl_generatesApprovedHttpsUrl` | ✅ VERIFIED |
| **12. Civic Program Overview** | `AboutScreen` / `Card` | Static Screen | Local Assets / Product Identity | Static Resource Bundle | N/A (Embedded local civic content) | Full program background, QR guide, and version info | N/A | 100% available offline | Component visual verification | ✅ VERIFIED |

---

## 3. Dependency Injection & Service Locator Verification

In `app/src/main/java/id/bubakangreen/app/core/di/RepositoryProvider.kt`:
1. **Production Path:** When Firebase is active (`FirebaseApp.getApps(context).isNotEmpty()`), `RepositoryProvider` provides real `FirestoreLocationRepository(FirebaseFirestore.getInstance())` and `FirestorePlantRepository(FirebaseFirestore.getInstance())`.
2. **Safe Development Path:** If Firebase is not yet configured with `google-services.json`, `RepositoryProvider` safely provides fallback fixture repositories explicitly marked `UI_PREVIEW_ONLY: Fixture repository used solely when Firebase is unconfigured. Must never be treated as real production data.`
3. **Phase 4 Preparation Requirement:** For Phase 4 (PIC & Admin), `RepositoryProvider` must be updated to expose `AuthRepository` (`FirebaseAuthRepository`), which was implemented in Phase 2.

---

## 4. Connector Audit Conclusion

All 12 core Phase 3 features have complete, verified, un-mocked architectural connector chains. There are **zero missing links, zero direct Firebase calls in UI Composables, and zero architectural shortcuts**.
