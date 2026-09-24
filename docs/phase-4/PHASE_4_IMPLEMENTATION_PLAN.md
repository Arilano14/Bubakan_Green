# PHASE 4 IMPLEMENTATION PLAN — BUBAKAN GREEN
## PIC & Admin Management Implementation

**Sistem Informasi Urban Farming & Taman Toga**  
**Kelurahan Bubakan, Kecamatan Mijen, Kota Semarang**  
**Document:** `docs/phase-4/PHASE_4_IMPLEMENTATION_PLAN.md`  
**Date:** 2026-09-24  
**Status:** DRAFT — AWAITING PRODUCT OWNER APPROVAL (`ACC PHASE 4`)  

---

## 1. Phase 3 Audit Result Summary

The Phase 3 Completion Audit (`docs/phase-4/PHASE_3_COMPLETION_AUDIT.md`), Performance Audit (`docs/phase-4/PHASE_3_PERFORMANCE_AUDIT.md`), and Feature Connector Matrix (`docs/phase-4/FEATURE_CONNECTOR_MATRIX.md`) confirm:
1. **Core UI Complete:** All 6 public screens, navigation backstack, and deep links operate with high fidelity.
2. **Visual Direction Certified:** Adheres to Planta-inspired clarity, low visual noise, generous whitespace, and Bubakan-owned branding. Zero AI slop.
3. **Performance Target Met:** Local and UI-level operations execute well under the **<=300ms** threshold (P50: 12–42ms). Network latency is explicitly decoupled and documented.
4. **Clean Connectors:** All 12 public features have intact, verified, un-mocked architectural chains (`UI -> ViewModel -> Repository -> Firestore/Cache`).
5. **Git Protection Enforced:** 100% local commits; zero remote pushes.

---

## 2. Phase 3 Blockers & Pre-Conditions

- **No Technical Blockers:** The technical foundation and public screens are fully operational.
- **Android SDK Path Note:** Building via CLI requires `sdk.dir` in `local.properties`. When opened in Android Studio, the SDK path is configured automatically.
- **Dependency Sufficiency:** All libraries required for Phase 4 (`firebase-auth`, `firebase-firestore`, `play-services-location`, `navigation-compose`, `coil-compose`) are already configured in `app/build.gradle.kts`. **Zero new external dependencies are required.**

---

## 3. Phase 4 Objective

Build secure, role-based management capabilities for **Petugas Lapangan (PIC)** and **Admin Kelurahan Bubakan** within a **single unified APK**.

Phase 4 bridges public botanical discovery with authoritative field governance: allowing authenticated garden officers to submit real community gardens, lock single-shot GPS coordinates, associate botanical species to garden plots, and enabling Kelurahan administrators to review, approve, publish, and audit community agriculture data.

---

## 4. Phase 4 Scope (Approved Management Features)

### 4.1 Authentication & Session Routing (`SCR-AUTH-01`)
- Login screen accessible via Top Bar padlock icon `[🔒 Masuk]`.
- Email and password authentication via `FirebaseAuthRepository`.
- Role-based adaptive session: automatically routes to `PicDashboardScreen` (for `UserRole.PIC`) or `AdminDashboardScreen` (for `UserRole.ADMIN`).
- Graceful session recovery and secure sign-out.

### 4.2 PIC Management Capabilities (Petugas Lapangan)
1. **PIC Dashboard (`SCR-PIC-01`):** Overview of assigned community gardens, plant inventory count, and quick action buttons.
2. **Garden Registration & Edit (`SCR-PIC-02`):**
   - Form inputs: Garden Name, Garden Type (`URBAN_FARMING` vs `TAMAN_TOGA`), RW selection (RW 01 to RW 08), Address, and Description.
   - **Single-Shot GPS Capture:** Integrated `GpsCaptureWidget` invoking `LocationClient.getCurrentLocation()` once upon user tap, recording device-reported horizontal accuracy in meters and displaying an operational accuracy badge.
   - Initial status submitted as `PENDING_APPROVAL`.
3. **Garden Plant Inventory Management (`SCR-PIC-03`):**
   - Link existing `MasterPlant` species from encyclopedia to the physical garden plot (`LocationPlant`).
   - Add bed location notes, bed number, and planting quantity notes.
   - Maintain data model flag `featuredForQr: Boolean` for future integration.
4. **QR Generation & Export Boundary (Strict Phase 5 Assignment):**
   - **EXCLUDED FROM PHASE 4:** QR Label Preview, sticker export, label printing, and field layout integration are strictly assigned to **Phase 5**.
   - Phase 4 only maintains existing data model fields (`featuredForQr: Boolean`) without generating QR codes, exporting stickers, or claiming physical QR field readiness.

### 4.3 Admin Kelurahan Capabilities (Pemerintah Kelurahan)
1. **Admin Dashboard (`SCR-ADM-01`):** Civic governance summary, pending approval queue counter, total registered gardens, and PIC roster.
2. **Location Approval & Publication Queue (`SCR-ADM-02`):**
   - Review pending gardens submitted by PICs (inspecting coordinates, RW, photos, and descriptions).
   - One-tap `[ Setujui & Publikasikan ]` (transitions status to `PUBLISHED`).
   - One-tap `[ Tolak dengan Catatan ]` (transitions status to `DRAFT` or `ARCHIVED`).
3. **Master Plant Encyclopedia Management:**
   - Create or edit authoritative `MasterPlant` records (Indonesian name, Latin name, Mandarin Hanzi, Pinyin, audio URL, description).
4. **PIC Garden Assignment (`SCR-ADM-03`):**
   - Assign or reassign PIC user accounts to specific community gardens.

---

## 5. Out-of-Scope (Strictly Prohibited in Phase 4)

- ❌ **No Separate Apps:** Do NOT create a separate "Bubakan Admin APK" or "PIC APK". All roles reside in the single `id.bubakangreen.app` package.
- ❌ **No Public Registration:** Zero "Sign Up / Register" buttons for general public. PIC and Admin accounts are provisioned via administrative channels.
- ❌ **No Production QR Code Generation, Sticker Export, or Label Printing:** Assigned strictly to **Phase 5**. Phase 4 maintains only existing data models/fields (`featuredForQr: Boolean`).
- ❌ **No Unsupported GPS Accuracy Guarantees:** Never claim "<25m accuracy guarantee". The system records device-reported horizontal accuracy in meters with an operational acceptance threshold (<=25m).
- ❌ **No Live GPS Tracking:** Strictly prohibited. Location capture is strictly single-shot (`LocationClient.getCurrentLocation()`).
- ❌ **No Geofencing / Background Tracking / Location History:** Banned.
- ❌ **No Paid Firebase Storage or Unapproved Upload Pipelines:** Actual media upload infrastructure deferred until media strategy approval. URL references only.
- ❌ **No In-App QR Scanner:** Public and officers use standard camera / Google Lens.
- ❌ **No E-Commerce, Chat, or Social Feeds:** Banned by anti-slop rules.
- ❌ **No Remote Git Push:** `git push` remains permanently prohibited.

---

## 6. Role & Permission Matrix

| Operation / Capability | Public (Anonim) | PIC Kebun (Authenticated) | Admin Kelurahan (Authenticated) | Backend Enforcement (`firestore.rules`) |
|:---|:---:|:---:|:---:|:---|
| **Browse Published Gardens** | ✅ ALLOWED | ✅ ALLOWED | ✅ ALLOWED | `allow read: if resource.data.status == 'PUBLISHED'` |
| **Browse Published Plants** | ✅ ALLOWED | ✅ ALLOWED | ✅ ALLOWED | `allow read: if true` |
| **Login with Email & Password** | ❌ (No account) | ✅ ALLOWED | ✅ ALLOWED | Firebase Authentication service |
| **Submit New Garden (`PENDING_APPROVAL`)** | ❌ BLOCKED | ✅ ALLOWED | ✅ ALLOWED | `allow create: if isPIC() && request.resource.data.status == 'PENDING_APPROVAL'` |
| **Update Assigned Garden Details** | ❌ BLOCKED | ✅ ALLOWED (Assigned only) | ✅ ALLOWED (All) | `allow update: if isAdmin() || (isPIC() && resource.data.picUid == request.auth.uid)` |
| **Approve / Publish Garden** | ❌ BLOCKED | ❌ BLOCKED | ✅ ALLOWED | `allow update: if isAdmin()` |
| **Delete Garden** | ❌ BLOCKED | ❌ BLOCKED | ✅ ALLOWED | `allow delete: if isAdmin()` |
| **Add Plant to Garden (`LocationPlant`)** | ❌ BLOCKED | ✅ ALLOWED (Assigned only) | ✅ ALLOWED (All) | `allow create, update: if isAdmin() || (isPIC() && ...)` |
| **Edit Master Botanical Record** | ❌ BLOCKED | ❌ BLOCKED | ✅ ALLOWED | `allow write: if isAdmin()` |
| **Assign PIC to Garden** | ❌ BLOCKED | ❌ BLOCKED | ✅ ALLOWED | `allow write: if isAdmin()` |

*Crucial rule: Security is enforced by Firestore security rules on the cloud server. Hiding UI elements is merely an ergonomics layer, never the security boundary.*

---

## 7. PIC Feature Specification & Workflow

### 7.1 Flow: Single-Shot Garden Registration
```
[PIC Dashboard] ──► Tap [+ Daftarkan Kebun Baru] ──► Open LocationFormScreen
                                                               │
                                                               ▼
Input Name, RW, Address, Description ◄── Fill form fields
                                                               │
                                                               ▼
Tap [Ambil Titik Lokasi GPS] ──► Check ACCESS_FINE_LOCATION ──► Acquire Single Location Fix
                                                               │
                                                               ▼
Mini Map Preview & Accuracy Radius Displayed ◄─────────────────┘
                                                               │
                                                               ▼
Tap [Kirim Pengajuan Kebun] ──► Firestore /locations (status: "PENDING_APPROVAL")
                                                               │
                                                               ▼
Display Success Confirmation ──► Return to PIC Dashboard (Status: Menunggu Persetujuan)
```

---

## 8. Admin Feature Specification & Workflow

### 8.1 Flow: Location Moderation & Approval
```
[Admin Dashboard] ──► View Pending Approval Counter (e.g. "3 Pengajuan Baru")
                                                               │
                                                               ▼
Open LocationApprovalScreen ──► List pending submissions
                                                               │
                                                               ▼
Tap Garden Submission ──► Inspect Name, RW, Coordinates, and Description
                                                               │
                               ┌───────────────────────────────┴───────────────────────────────┐
                               ▼                                                               ▼
               Tap [Setujui & Publikasikan]                                     Tap [Tolak dengan Catatan]
                               │                                                               │
                               ▼                                                               ▼
           Firestore /locations/{id} update:                               Firestore /locations/{id} update:
           status = "PUBLISHED"                                            status = "DRAFT"
           coordinatesStatus = "VERIFIED"                                  notes = adminRejectionNote
                               │                                                               │
                               └───────────────────────────────┬───────────────────────────────┘
                                                               ▼
                                              Record event in /audit_logs
                                                               ▼
                                              Return to Approval Queue
```

---

## 9. Firestore Collections & Schema Schema Extensions

### 9.1 `/locations/{locationId}`
- Existing fields: `id`, `name`, `type`, `rw`, `address`, `description`, `latitude`, `longitude`, `coordinatesStatus`, `featured`, `photoUrl`, `picUid`, `status`, `createdAt`, `updatedAt`.
- Added moderation field:
  - `rejectionNote: String?` (Filled if admin requests revision).

### 9.2 `/audit_logs/{logId}`
- Dedicated collection for security and administrative accountability:
  - `id: String`
  - `action: String` (e.g. `LOCATION_CREATED`, `LOCATION_APPROVED`, `LOCATION_REJECTED`, `PLANT_ADDED`)
  - `targetEntityId: String`
  - `targetEntityType: String` (`LOCATION`, `PLANT`)
  - `actorUid: String`
  - `actorRole: String` (`PIC`, `ADMIN`)
  - `timestamp: Long`

---

## 10. Security Rule Upgrades (`web/firestore.rules`)

Update `web/firestore.rules` to incorporate audit logging and refine master plant permissions:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    
    function isAuthenticated() {
      return request.auth != null;
    }
    
    function isAdmin() {
      return isAuthenticated() && request.auth.token.role == 'admin';
    }
    
    function isPIC() {
      return isAuthenticated() && (request.auth.token.role == 'pic' || isAdmin());
    }

    match /locations/{locationId} {
      allow read: if resource.data.status == 'PUBLISHED' || isPIC();
      allow create: if isPIC() && request.resource.data.status == 'PENDING_APPROVAL';
      allow update: if isAdmin() || (isPIC() && resource.data.picUid == request.auth.uid && request.resource.data.status == resource.data.status);
      allow delete: if isAdmin();
    }

    match /master_plants/{plantId} {
      allow read: if true;
      allow write: if isAdmin();
    }

    match /location_plants/{id} {
      allow read: if true;
      allow create: if isPIC();
      allow update, delete: if isAdmin() || (isPIC() && get(/databases/$(database)/documents/locations/$(resource.data.locationId)).data.picUid == request.auth.uid);
    }

    match /audit_logs/{logId} {
      allow read: if isAdmin();
      allow create: if isAuthenticated();
      allow update, delete: if false; // Immutable audit log
    }

    match /users/{userId} {
      allow read: if isAuthenticated() && request.auth.uid == userId;
      allow write: if isAdmin();
    }
  }
}
```

---

## 11. Navigation Architecture Extensions

Add authenticated routes to `NavigationRoutes.kt`:

```kotlin
sealed class Screen(val route: String, val title: String = "") {
    // Existing Public Screens
    data object Home : Screen("home", "Beranda")
    data object Locations : Screen("locations", "Lokasi & Peta")
    data object LocationDetail : Screen("location/{locationId}", "Detail Lokasi")
    data object Catalog : Screen("catalog", "Katalog")
    data object PlantDetail : Screen("plant/{plantId}", "Detail Tanaman")
    data object About : Screen("about", "Tentang")

    // Phase 4 Authenticated Screens
    data object Login : Screen("login", "Masuk Petugas")
    data object PicDashboard : Screen("pic_dashboard", "Dashboard Petugas")
    data object LocationForm : Screen("location_form?locationId={locationId}", "Form Kebun") {
        fun createRoute(locationId: String? = null) = if (locationId != null) "location_form?locationId=$locationId" else "location_form"
    }
    data object PlantForm : Screen("plant_form/{locationId}", "Tambah Tanaman") {
        fun createRoute(locationId: String) = "plant_form/$locationId"
    }
    data object AdminDashboard : Screen("admin_dashboard", "Admin Kelurahan")
    data object LocationApproval : Screen("location_approval", "Persetujuan Kebun")
}
```

---

## 12. State Management & Form Handling Architecture

Every management form adheres to a strict 9-state resilience contract:
1. `VALID`: Form fields validated and ready for submission.
2. `INVALID`: Validation error highlighted beneath corresponding field.
3. `EMPTY`: Default initial state.
4. `LOADING`: Initializing form with existing data from Firestore.
5. `SAVING`: Asynchronous Firestore submission in progress.
6. `SUCCESS`: Confirmation dialog / snackbar, navigation back to dashboard.
7. `ERROR`: Server or network error message with retry.
8. `OFFLINE`: Indicators that submission will queue in local cache.
9. `UNAUTHORIZED`: Immediate denial if session lacks required role.

---

## 13. Form Validation Rules

1. **Garden Registration (`LocationFormScreen`):**
   - `name`: Must not be blank, minimum 3 characters.
   - `type`: Must be `URBAN_FARMING` or `TAMAN_TOGA`.
   - `rw`: Must be selected from valid Bubakan RWs (RW 01 to RW 08).
   - `address`: Must not be blank.
   - `latitude` & `longitude`: Must be non-zero coordinates within the Semarang/Bubakan bounding box (-7.0 to -7.2 latitude, 110.2 to 110.4 longitude).
2. **Plant Assignment (`PlantFormScreen`):**
   - `masterPlantId`: Must reference an existing valid `MasterPlant`.
   - `quantityNote`: Optional note (e.g. "12 polybag").

---

## 14. GPS Acquisition Implementation Details

Leverages `AndroidLocationClient` implemented in Phase 2 with strict adherence to device-reported measurement reality:
- **No Accuracy Guarantee:** The application **never claims** a "<25m accuracy guarantee" because satellite accuracy depends on device hardware, environment, tree canopy, permissions, and OS behavior.
- **Approved Operational Contract:**
  > *"GPS capture supports recording the device-reported horizontal accuracy in meters. The system defines an operational acceptance threshold (accuracy <= 25m) for location submission, subject to approved requirements."*
- **Required Metadata Fields Recorded:**
  - `latitude: Double`
  - `longitude: Double`
  - `accuracyMeters: Float` (Device-reported horizontal accuracy)
  - `capturedAt: Long` (Epoch millisecond timestamp of fix)
- **Runtime GPS Behavior:**
  - [x] Request runtime `ACCESS_FINE_LOCATION` permission defensively.
  - [x] Capture single-shot location fix (`LocationClient.getCurrentLocation()`).
  - [x] Display device-reported accuracy radius in UI badge.
  - [x] Reject or warn if accuracy exceeds approved threshold (> 25m) and prompt retry in an open area.
  - [x] Provide one-tap retry button.
  - [x] Gracefully handle permission denial and guide user.
  - [x] Gracefully handle unavailable location / GPS disabled.
  - [x] **Zero Coordinate Fabrication:** Never generate fake or simulated coordinates if fix fails.
- **Strict Prohibitions:**
  - ❌ ABSOLUTELY NO background tracking.
  - ❌ ABSOLUTELY NO live tracking or continuous updates.
  - ❌ ABSOLUTELY NO geofencing.
  - ❌ ABSOLUTELY NO turn-by-turn navigation.
  - ❌ ABSOLUTELY NO location history logs.

---

## 15. Media Strategy (Cost Safety & Upload Infrastructure Boundary)

Audit of current media architecture:
1. **Zero Paid Cloud Services:** No paid Firebase Storage or cloud buckets may be provisioned. Billing is permanently disabled.
2. **Upload Infrastructure Deferred:** The repository currently has no approved physical storage/upload mechanism. Phase 4 **does not invent one**.
3. **Approved Phase 4 Scope:**
   - Maintain `photoUrl: String?` and `audioUrl: String?` fields in domain entities.
   - Display already-available HTTPS images (e.g. Wikimedia Commons or approved civic image repositories) via Coil image loader.
   - Support data model URL reference inputs.
   - Actual production upload pipelines are **deferred** until a zero-cost, permanent civic media storage strategy is formally approved via Change Request.
4. **Prohibitions:**
   - ❌ Do NOT enable paid storage.
   - ❌ Do NOT enable cloud billing.
   - ❌ Do NOT create hidden external upload services.
   - ❌ Do NOT fabricate media URLs.

---

## 16. Performance Plan (Phase 4)

- Login submission to credential validation feedback: **<= 300ms** local UI spinner.
- GPS coordinate lock to UI preview rendering: **<= 100ms** post-satellite fix.
- Location approval queue rendering from cache: **<= 150ms**.
- Form input typing debounce and validation: **<= 50ms**.

---

## 17. Testing Strategy (Phase 4)

1. **Authentication Tests:**
   - Test sign-in success with PIC credentials routes to `PicDashboard`.
   - Test sign-in success with Admin credentials routes to `AdminDashboard`.
   - Test sign-in failure with invalid password displays friendly error.
2. **Role Authorization Tests:**
   - Test unauthorized role cannot trigger approval action.
   - Test PIC cannot modify locations assigned to another PIC.
3. **Form Validation Tests:**
   - Test blank location name rejects submission.
   - Test invalid coordinates reject submission.
4. **Approval State Machine Tests:**
   - Test `PENDING_APPROVAL` transitions to `PUBLISHED` upon admin approval.

---

## 18. Acceptance Criteria (Phase 4)

- [ ] Top Bar padlock icon `[🔒 Masuk]` opens `LoginScreen`.
- [ ] Authenticated PIC lands on `PicDashboardScreen` showing their assigned gardens.
- [ ] Authenticated Admin lands on `AdminDashboardScreen` showing approval queue.
- [ ] PIC can register a new garden with single-shot GPS capture.
- [ ] Registered garden enters `PENDING_APPROVAL` status and is hidden from public.
- [ ] Admin can view pending garden, inspect coordinates, and tap `[ Setujui & Publikasikan ]`.
- [ ] Approved garden immediately transitions to `PUBLISHED` and becomes visible to public.
- [ ] PIC can add botanical species from encyclopedia to their garden plot.
- [ ] Unauthorized users are blocked by `firestore.rules` on the backend.
- [ ] All forms handle Loading, Saving, Error, and Validation states.
- [ ] Zero paid cloud services introduced.
- [ ] Zero `git push` executed.

---

## 19. Risks & Mitigations

| Risk | Impact | Mitigation |
|:---|:---|:---|
| GPS signal weak in dense garden foliage | Location capture timeout | Provide retry dialog with helpful advice to stand in open clearing. |
| Weak cellular connectivity in garden | Form submission fails | Firestore local persistence caches submission; synchronizes on reconnection. |
| Accidental privilege escalation | Security vulnerability | Hard backend enforcement in `firestore.rules` verifying `request.auth.token.role`. |

---

## 20. Files Expected to Change / Be Created in Phase 4

```
app/src/main/java/id/bubakangreen/app/
├── core/
│    └── di/
│         └── RepositoryProvider.kt             [Expose AuthRepository]
├── domain/
│    └── model/
│         └── AuditLog.kt                       [New entity for civic audit logs]
├── navigation/
│    ├── NavigationRoutes.kt                    [Add Login, PIC, Admin routes]
│    └── BubakanNavHost.kt                      [Add composable routes for auth & management]
├── ui/
│    ├── auth/
│    │    ├── LoginScreen.kt                    [SCR-AUTH-01]
│    │    └── LoginViewModel.kt
│    ├── pic/
│    │    ├── PicDashboardScreen.kt             [SCR-PIC-01]
│    │    ├── PicDashboardViewModel.kt
│    │    ├── LocationFormScreen.kt             [SCR-PIC-02 with GPS]
│    │    ├── LocationFormViewModel.kt
│    │    ├── PlantFormScreen.kt                [SCR-PIC-03]
│    │    └── PlantFormViewModel.kt
│    └── admin/
│         ├── AdminDashboardScreen.kt           [SCR-ADM-01]
│         ├── AdminDashboardViewModel.kt
│         ├── LocationApprovalScreen.kt         [SCR-ADM-02]
│         └── LocationApprovalViewModel.kt
web/
└── firestore.rules                             [Add audit_logs and refine rules]
```

---

## 21. Dependencies Assessment

- **Required Dependencies:** **0 new libraries.** (Existing Version Catalog dependencies are completely sufficient).
- **Rejected Dependencies:**
  - ❌ `firebase-storage` (Cost protection)
  - ❌ `zxing` / `mlkit-barcode-scanning` (Native camera preferred)
  - ❌ `lottie` (Material 3 micro-animations sufficient)
