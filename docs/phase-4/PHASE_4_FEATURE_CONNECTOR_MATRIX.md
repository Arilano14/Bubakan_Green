# PHASE 4 FEATURE CONNECTOR MATRIX — BUBAKAN GREEN

**Sistem Informasi Urban Farming & Taman Toga**  
**Kelurahan Bubakan, Kecamatan Mijen, Kota Semarang**  
**Document:** `docs/phase-4/PHASE_4_FEATURE_CONNECTOR_MATRIX.md`  
**Date:** 2026-09-24  
**Auditor Role:** Senior Software Architect & Integration Auditor  
**Status:** ARCHITECTURALLY VERIFIED & TIED TO FIRESTORE SECURITY RULES  

---

## 1. Architectural Chain Standard (Phase 4)

Every management capability in Phase 4 connects the full stack:

```
Composable UI ──(Intent)──► ViewModel ──► Repository ──► Firestore SDK / Rules ──► StateFlow ──► Compose UI
```

**Scope Boundary Rules Enforced:**
- ❌ Zero production QR generation in Phase 4 (strictly assigned to Phase 5).
- ❌ Zero live tracking in Phase 4 (single-shot location capture only).
- ❌ Zero un-mocked fake data.
- ❌ All management actions verified against `web/firestore.rules`.

---

## 2. End-to-End Management Connector Matrix

| Feature | Screen ID | ViewModel | Repository Interface | Data Source Implementation | Firestore Collection | Security Rule Clause | Success State | Error State | Offline Behavior | Test Coverage | Status |
|:---|:---|:---|:---|:---|:---|:---|:---|:---|:---|:---|:---:|
| **1. Petugas & Admin Login** | `LoginScreen` (`SCR-AUTH-01`) | `LoginViewModel` | `AuthRepository.signInWithEmail()` | `FirebaseAuthRepository` | Firebase Auth Service + Token Claims | N/A (Handled by Firebase Auth SDK) | `UserSession` with `role` (PIC/Admin) | `UiState.Error` with human-readable error | Offline rejected (Auth requires server token verification) | `AuthRepositoryTest` | ✅ VERIFIED |
| **2. Session-Adaptive Routing** | `BubakanNavHost` | `AuthViewModel` | `AuthRepository.currentUserSession` | `FirebaseAuthRepository` | Local Token & Claims | N/A | Routes to `PicDashboard` or `AdminDashboard` | Falls back to `LoginScreen` | Retains last authenticated session locally | NavHost unit tests | ✅ VERIFIED |
| **3. PIC Garden Portfolio** | `PicDashboardScreen` (`SCR-PIC-01`) | `PicDashboardViewModel` | `LocationRepository.getAssignedLocations()` | `FirestoreLocationRepository` | Firestore `/locations` (`picUid == auth.uid`) | `isPIC() && resource.data.picUid == request.auth.uid` | `UiState.Success<List<Location>>` | `UiState.Error` with retry CTA | Renders cached assigned gardens | `PicDashboardViewModelTest` | ✅ VERIFIED |
| **4. Single-Shot GPS Garden Registration** | `LocationFormScreen` (`SCR-PIC-02`) | `LocationFormViewModel` | `LocationRepository.createLocation()` & `LocationClient` | `FirestoreLocationRepository` & `AndroidLocationClient` | Firestore `/locations` (`status == "PENDING_APPROVAL"`) | `isPIC() && request.resource.data.status == 'PENDING_APPROVAL'` | Garden submitted (`PENDING_APPROVAL`) | `UiState.Error` on validation/network failure | Queues in local Firestore cache; syncs on reconnection | `LocationFormViewModelTest` | ✅ VERIFIED |
| **5. Garden Profile Update (Assigned PIC)** | `LocationFormScreen` (`SCR-PIC-02`) | `LocationFormViewModel` | `LocationRepository.updateLocation()` | `FirestoreLocationRepository` | Firestore `/locations/{id}` | `isPIC() && resource.data.picUid == request.auth.uid` | Profile updated in state & Firestore | `UiState.Error` with retry CTA | Writes to local cache, synced on reconnect | `LocationFormViewModelTest` | ✅ VERIFIED |
| **6. On-Site Plant Assignment** | `PlantFormScreen` (`SCR-PIC-03`) | `PlantFormViewModel` | `PlantRepository.addPlantToLocation()` | `FirestorePlantRepository` | Firestore `/location_plants` | `isPIC() && get(.../locations/id).data.picUid == request.auth.uid` | `LocationPlant` junction created | `UiState.Error` if plant reference invalid | Writes to local cache, synced on reconnect | `PlantFormViewModelTest` | ✅ VERIFIED |
| **7. Admin Governance Overview** | `AdminDashboardScreen` (`SCR-ADM-01`) | `AdminDashboardViewModel` | `LocationRepository.getPublishedLocations()` & pending queue | `FirestoreLocationRepository` | Firestore `/locations` (All statuses) | `isAdmin()` passes | Queue counter & civic garden stats | `UiState.Error` with retry CTA | Renders cached queue | `AdminDashboardViewModelTest` | ✅ VERIFIED |
| **8. Garden Approval & Publication** | `LocationApprovalScreen` (`SCR-ADM-02`) | `LocationApprovalViewModel` | `LocationRepository.updateLocation()` | `FirestoreLocationRepository` | Firestore `/locations/{id}` (`status = "PUBLISHED"`, `coordinatesStatus = "VERIFIED"`) | `isAdmin()` passes | Location transitioned to `PUBLISHED` | `UiState.Error` with retry CTA | Requires online admin token for publication | `LocationApprovalViewModelTest` | ✅ VERIFIED |
| **9. Garden Rejection with Feedback** | `LocationApprovalScreen` (`SCR-ADM-02`) | `LocationApprovalViewModel` | `LocationRepository.updateLocation()` | `FirestoreLocationRepository` | Firestore `/locations/{id}` (`status = "DRAFT"`, `rejectionNote = note`) | `isAdmin()` passes | Location transitioned to `DRAFT` | `UiState.Error` with retry CTA | Online admin sync required | `LocationApprovalViewModelTest` | ✅ VERIFIED |
| **10. Master Plant Catalog Admin** | `MasterPlantFormScreen` | `MasterPlantViewModel` | `PlantRepository.createMasterPlant()` / `updateMasterPlant()` | `FirestorePlantRepository` | Firestore `/master_plants` | `isAdmin()` passes | Master species saved | `UiState.Error` with validation message | Cached locally | `PlantRepositoryTest` | ✅ VERIFIED |
| **11. PIC Garden Stewardship Assignment** | `PicAssignmentModal` (`SCR-ADM-03`) | `AdminDashboardViewModel` | `LocationRepository.updateLocation()` | `FirestoreLocationRepository` | Firestore `/locations/{id}` (`picUid = newPicUid`) | `isAdmin()` passes | Garden assigned to new PIC | `UiState.Error` | Online admin sync required | `AdminDashboardViewModelTest` | ✅ VERIFIED |
| **12. Civic Audit Trail Logging** | Background interceptor | ViewModels | `AuditRepository.recordAction()` | `FirestoreAuditRepository` | Firestore `/audit_logs` | `allow create: if isAuthenticated(); allow update, delete: if false;` | Audit entry appended | Log failure warning | Appends to offline cache; synced on reconnect | `AuditRepositoryTest` | ✅ VERIFIED |

---

## 3. Connector Verification Summary

All 12 management features are strictly coupled to:
1. Strongly typed Domain Repositories (`LocationRepository`, `PlantRepository`, `AuthRepository`).
2. Declarative security rules in `web/firestore.rules`.
3. Standardized 9-state form handling models.
4. Clean separation between `MasterPlant` and `LocationPlant`.
5. Physical QR code production is **0% in Phase 4**, ensuring 100% scope compliance with Phase 5.
