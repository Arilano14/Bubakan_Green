# PHASE 2 IMPLEMENTATION PLAN — BUBAKAN GREEN

**Sistem Informasi Urban Farming & Taman Toga**  
**Kelurahan Bubakan, Kecamatan Mijen, Kota Semarang**  
**Document:** `docs/phase-2/PHASE_2_IMPLEMENTATION_PLAN.md`  
**Date:** 2026-09-23  
**Status:** DRAFT — AWAITING PRODUCT OWNER APPROVAL (`ACC PHASE 2`)  

---

## 1. Audit Summary

The Phase 0 and Phase 1 completion audits confirm:
1. **Requirements & UX Integrity:** All 12 MVP core capabilities, 10 architectural principles, and 14 UI screens are completely defined and traced in documentation (`docs/source`, `docs/phase-0`, `docs/phase-1`).
2. **Current Repository State:** Greenfield documentation repository. Zero lines of application code exist. No Gradle scaffold, no Firebase resources, and no web files have been created.
3. **Environment Audit:** Node.js v24.14.1 and npm 11.11.0 are available. Android Studio is installed at `C:\Program Files\Android\Android Studio` with bundled OpenJDK 21 at `jbr\bin\java.exe`. Android SDK path needs local initialization.
4. **Implementation Boundary:** Phase 2 establishes solely the **technical foundation** (project structure, Gradle configuration, data models, repository abstractions, security rules, static web fallback, and test harnesses). User-facing UI implementation is strictly reserved for Phase 3+.

---

## 2. Phase 0 Status

- **Product Identity:** `LOCKED` (BUBAKAN GREEN — Kelurahan Bubakan).
- **Core Constraints:** `LOCKED` (No login for public, single-point GPS, external QR scanning, user-triggered Mandarin audio, Rp0 target cost, data ≠ APK code).
- **Architecture Decisions:** `LOCKED` (Kotlin + Jetpack Compose, Firebase NoSQL & Auth, static HTML web fallback, MVVM + Repository pattern).
- **Deferred Decisions:** Map Provider selection (ADR-004: Google Maps vs. osmdroid) and Mandarin audio production method remain deferred without blocking Phase 2.

---

## 3. Phase 1 Status

- **Information Architecture:** `LOCKED` (3-Tab structure: `Beranda`, `Lokasi & Peta`, `Katalog`).
- **Screen Inventory:** `LOCKED` (10 P0 screens, 3 P1 screens, 1 P2 modal).
- **Interaction Models:** `LOCKED` (Contextual GPS acquisition, external QR App Links, non-intrusive offline status banner).
- **Design System Tokens:** `LOCKED` (Palette Alam Bubakan, Material 3 Typography & Spacing).

---

## 4. Current Repository Status

- **Branch:** `main` (clean working tree).
- **Files Present:** 12 markdown documents across `docs/source/`, `docs/phase-0/`, and `docs/phase-1/`.
- **Source Code Present:** None (0% code implementation).
- **Git Remote:** `https://github.com/Arilano14/Bubakan_Green.git` (Strict rule: NO REMOTE PUSH).

---

## 5. Gaps to Close in Phase 2

1. Lack of native Android Gradle project scaffold targeting API 26–35 with Package ID `id.bubakangreen.app`.
2. Lack of type-safe domain entities separating Master Botanical data from Location-specific plantings.
3. Lack of repository contracts and offline-enabled Firestore data access layers.
4. Lack of backend security rules enforcing public read and role-scoped PIC/Admin writes.
5. Lack of static web fallback pages for QR users without the native app.
6. Lack of reproducible local development and build verification documentation.

---

## 6. Phase 2 Objectives

1. Initialize a clean, robust, modular Android project structure using Kotlin Gradle DSL (`.kts`) with Jetpack Compose Material 3 dependencies.
2. Implement the core domain model architecture:
   - `Location` (First-class entity with `LocationType`, `coordinatesStatus`, and `featured` flag).
   - `MasterPlant` (Botanical master record: Indonesian, Latin, Mandarin Hanzi, Pinyin, description, audio URL).
   - `LocationPlant` (Junction entity: links a MasterPlant to a specific garden Location with local photos, quantities, and QR eligibility).
   - `UserSession` (Auth state, role resolution for Public, PIC, and Admin).
3. Author production-grade Firebase security rules (`firestore.rules`) enforcing strict role-based access control.
4. Scaffold the lightweight, responsive static web fallback (`web/public/index.html` and `web/public/plant.html`).
5. Establish a baseline unit test harness and local build verification guide (`docs/phase-2/LOCAL_DEVELOPMENT.md`).

---

## 7. Exact Implementation Scope (Phase 2)

| Component | Scope Included in Phase 2 | Scope Excluded from Phase 2 |
|:---|:---|:---|
| **Android Project Scaffold** | Root `build.gradle.kts`, `settings.gradle.kts`, `gradle/libs.versions.toml`, `app/build.gradle.kts`, `AndroidManifest.xml`, `MainActivity.kt` (stub shell), application theme tokens. | Feature screens, navigation graph execution, custom Compose widgets. |
| **Domain Data Models** | Immutable Kotlin data classes: `Location`, `MasterPlant`, `LocationPlant`, `UserSession`, `Coordinates`. Enums: `LocationType`, `LocationStatus`, `CoordinatesStatus`, `UserRole`. | Mock UI view models, sample data hardcoding in APK. |
| **Data Access Layer** | Repository interfaces (`LocationRepository`, `PlantRepository`, `AuthRepository`) and initial Firestore client implementation with local disk caching enabled. | Complete CRUD UI integration, network retry managers. |
| **Hardware & Sensors** | Location provider interface (`LocationClient`) for single-shot GPS capture; App Link intent-filter manifest configuration. | Camera UI, actual GPS acquisition dialogs in Compose, internal QR scanner. |
| **Security & Backend** | Declarative `firestore.rules` covering `locations`, `master_plants`, `location_plants`, and `users` collections; `firebase.json` configuration. | Production Firebase deployment, Firebase Admin SDK scripts. |
| **Web Fallback** | Static HTML5/CSS files in `web/public/` implementing responsive card layout (<60KB) for `/plant/{plantId}`. | React/Next.js frameworks, Web Admin dashboard, CMS. |
| **Testing & Local Docs** | Unit tests for data model serialization and URL builders; `docs/phase-2/LOCAL_DEVELOPMENT.md`. | End-to-end UI tests, automated CI/CD pipelines. |

---

## 8. Out of Scope (Strictly Prohibited in Phase 2)

- ❌ DO NOT build user-facing screens (`HomeScreen`, `LocationDetailScreen`, `PlantDetailScreen`, `LoginScreen`).
- ❌ DO NOT implement production camera photo capture or image picker UI.
- ❌ DO NOT implement interactive map rendering (Mapbox / Google Maps Compose).
- ❌ DO NOT create an in-app QR scanner.
- ❌ DO NOT push commits to the remote GitHub repository.
- ❌ DO NOT populate Firestore with synthetic or unverified Bubakan botanical data.
- ❌ DO NOT enable any paid third-party cloud services.

---

## 9. Files & Modules to Create

```
Bubakan Green/
├── docs/
│   └── phase-2/
│        ├── PHASE_0_1_COMPLETION_AUDIT.md        [CREATED]
│        ├── REQUIREMENT_TRACEABILITY.md          [CREATED]
│        ├── PHASE_2_IMPLEMENTATION_PLAN.md       [THIS FILE]
│        ├── PHASE_2_SELF_REVIEW.md               [NEXT]
│        └── LOCAL_DEVELOPMENT.md                 [DURING EXECUTION]
│
├── gradle/
│   └── wrapper/
│        ├── gradle-wrapper.jar
│        └── gradle-wrapper.properties
│
├── build.gradle.kts                              [Root build file]
├── settings.gradle.kts                           [Project settings]
├── gradlew                                       [Unix Gradle wrapper]
├── gradlew.bat                                   [Windows Gradle wrapper]
│
├── app/
│   ├── build.gradle.kts                          [App module build file]
│   ├── proguard-rules.pro                        [R8 optimization rules]
│   └── src/
│        ├── main/
│        │    ├── AndroidManifest.xml             [Manifest, App Links, Permissions]
│        │    ├── java/id/bubakangreen/app/
│        │    │    ├── BubakanApplication.kt      [Application class & Firebase init]
│        │    │    ├── MainActivity.kt            [Single Activity entry point stub]
│        │    │    ├── core/
│        │    │    │    ├── result/Result.kt      [Type-safe functional result wrapper]
│        │    │    │    ├── network/ConnectivityObserver.kt
│        │    │    │    └── util/QrUrlBuilder.kt  [Deterministic QR URL constructor]
│        │    │    ├── domain/
│        │    │    │    ├── model/
│        │    │    │    │    ├── Location.kt      [First-class Location entity]
│        │    │    │    │    ├── MasterPlant.kt   [Master botanical entity]
│        │    │    │    │    ├── LocationPlant.kt [Location-specific plant junction]
│        │    │    │    │    ├── UserSession.kt   [User profile & role model]
│        │    │    │    │    └── Coordinates.kt   [Lat/Lng & accuracy holder]
│        │    │    │    └── repository/
│        │    │    │         ├── LocationRepository.kt
│        │    │    │         ├── PlantRepository.kt
│        │    │    │         └── AuthRepository.kt
│        │    │    ├── data/
│        │    │    │    ├── remote/
│        │    │    │    │    ├── FirestoreLocationRepository.kt
│        │    │    │    │    ├── FirestorePlantRepository.kt
│        │    │    │    │    └── FirebaseAuthRepository.kt
│        │    │    │    └── location/
│        │    │    │         └── AndroidLocationClient.kt [Single-shot GPS provider]
│        │    │    └── ui/theme/
│        │    │         ├── Color.kt              [Bubakan Green Design Tokens]
│        │    │         ├── Type.kt               [Material 3 Typography tokens]
│        │    │         └── Theme.kt              [BubakanGreenTheme Compose wrapper]
│        │    └── res/
│        │         ├── values/
│        │         │    ├── strings.xml           [App branding strings]
│        │         │    └── colors.xml
│        │         └── mipmap-anydpi-v26/
│        └── test/
│             └── java/id/bubakangreen/app/
│                  ├── domain/model/ModelSerializationTest.kt
│                  └── core/util/QrUrlBuilderTest.kt
│
├── web/
│   ├── firebase.json                             [Firebase Hosting & rules config]
│   ├── firestore.rules                           [Declarative security rules]
│   └── public/
│        ├── index.html                           [Landing & APK download fallback]
│        ├── plant.html                           [QR destination fallback card]
│        ├── style.css                            [Responsive lightweight CSS]
│        └── .well-known/
│             └── assetlinks.json                 [Digital Asset Links template]
```

---

## 10. Files / Modules to Modify

None. The existing files in `docs/source`, `docs/phase-0`, and `docs/phase-1` are locked baselines. They will remain untouched to preserve documentation integrity.

---

## 11. Technical Architecture (Android + Backend)

```
┌─────────────────────────────────────────────────────────────┐
│                       ANDROID CLIENT                        │
│                 Package: id.bubakangreen.app                 │
│                                                             │
│   [Presentation Layer (Compose Stubs / Themes)]             │
│        │                                                    │
│        ▼                                                    │
│   [Domain Layer]                                            │
│        ├── Entities: Location, MasterPlant, LocationPlant   │
│        ├── Value Objects: Coordinates, LocationType, Status │
│        └── Repository Interfaces (Clean Architecture)       │
│        │                                                    │
│        ▼                                                    │
│   [Data Layer]                                              │
│        ├── Firestore Repositories (Offline Disk Persistence)│
│        ├── Firebase Auth Adapter (Token & Custom Claims)    │
│        └── Fused Location Client (Single-Shot GPS Capture)  │
└──────────────────────────────┬──────────────────────────────┘
                               │ HTTPS / TLS 1.3
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                    FIREBASE INFRASTRUCTURE                  │
│                     Tier: Spark (Free Rp0)                  │
│                                                             │
│   ├── Cloud Firestore (NoSQL Document Store)                │
│   │    └── Rules: firestore.rules (Strict RBAC Enforcement) │
│   ├── Firebase Authentication (Email/Password)              │
│   ├── Firebase Storage (Photos & Audio Blobs)               │
│   └── Firebase Hosting (Static Fallback + assetlinks.json)  │
└─────────────────────────────────────────────────────────────┘
```

---

## 12. Data Model Specification

To prevent data redundancy and maintain accurate botanical records, Phase 2 implements decoupled master-location entities:

### 12.1 `Location` (Collection: `locations/{locationId}`)
```kotlin
data class Location(
    val id: String,
    val name: String,
    val type: LocationType, // URBAN_FARMING, TAMAN_TOGA
    val rw: String,         // e.g. "03"
    val address: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val coordinatesStatus: CoordinatesStatus, // PENDING, VERIFIED
    val featured: Boolean = false,
    val photoUrl: String? = null,
    val picUid: String,
    val status: LocationStatus, // DRAFT, PENDING_APPROVAL, PUBLISHED, ARCHIVED
    val createdAt: Long,
    val updatedAt: Long
)
```

### 12.2 `MasterPlant` (Collection: `master_plants/{plantId}`)
```kotlin
data class MasterPlant(
    val id: String,
    val nameId: String,          // Indonesian common name (e.g. "Jahe Merah")
    val nameLatin: String,       // Scientific name (e.g. "Zingiber officinale var. rubrum")
    val nameMandarin: String?,   // Chinese characters (e.g. "红生姜")
    val pinyin: String?,         // Romanized pronunciation (e.g. "hóng shēng jiāng")
    val description: String,     // Herbal benefits, pharmacology, cultivation guide
    val primaryPhotoUrl: String? = null,
    val mandarinAudioUrl: String? = null,
    val createdAt: Long,
    val updatedAt: Long
)
```

### 12.3 `LocationPlant` (Collection: `location_plants/{locationPlantId}`)
```kotlin
data class LocationPlant(
    val id: String,
    val locationId: String,      // Foreign key to locations/{locationId}
    val masterPlantId: String,   // Foreign key to master_plants/{plantId}
    val localPhotoUrl: String?,  // Specific photo of the specimen at this garden
    val quantityNote: String?,   // e.g. "15 polybag"
    val notes: String?,          // Local planting notes / bed location
    val featuredForQr: Boolean = false, // Eligible for QR label generation
    val status: PlantStatus,     // ACTIVE, ARCHIVED
    val createdAt: Long,
    val updatedAt: Long
)
```

### 12.4 `UserSession` (Collection: `users/{userId}`)
```kotlin
data class UserSession(
    val uid: String,
    val email: String,
    val displayName: String,
    val role: UserRole,          // PUBLIC, PIC, ADMIN
    val assignedLocations: List<String> = emptyList() // List of locationIds
)
```

---

## 13. Authentication & Authorization Model

1. **Anonymous / Public User:**
   - Unauthenticated.
   - May read any document in `locations` where `status == "PUBLISHED"`.
   - May read any document in `master_plants` and `location_plants` where `status == "ACTIVE"`.
   - Has zero write permissions anywhere in the database.
2. **PIC (Petugas Kebun):**
   - Authenticated via Firebase Auth (Email/Password).
   - Custom Claims: `{ "role": "pic" }`.
   - May create new locations with `status == "PENDING_APPROVAL"`.
   - May update locations and create/update `location_plants` **only** if `resource.data.picUid == request.auth.uid`.
3. **Admin Kelurahan:**
   - Authenticated via Firebase Auth (Email/Password).
   - Custom Claims: `{ "role": "admin" }`.
   - Full read/write access to all collections, including approving locations (`status = "PUBLISHED"`) and managing user assignments.

---

## 14. Security Rules Specification (`firestore.rules`)

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

    // LOCATIONS
    match /locations/{locationId} {
      allow read: if resource.data.status == 'PUBLISHED' || isPIC();
      allow create: if isPIC() && request.resource.data.status == 'PENDING_APPROVAL';
      allow update: if isAdmin() || (isPIC() && resource.data.picUid == request.auth.uid && request.resource.data.status == resource.data.status);
      allow delete: if isAdmin();
    }

    // MASTER PLANTS
    match /master_plants/{plantId} {
      allow read: if true;
      allow write: if isPIC();
    }

    // LOCATION PLANTS
    match /location_plants/{id} {
      allow read: if true;
      allow create: if isPIC();
      allow update, delete: if isAdmin() || (isPIC() && get(/databases/$(database)/documents/locations/$(resource.data.locationId)).data.picUid == request.auth.uid);
    }

    // USERS (Metadata)
    match /users/{userId} {
      allow read: if isAuthenticated() && request.auth.uid == userId;
      allow write: if isAdmin();
    }
  }
}
```

---

## 15. Environment & Configuration Strategy

- **Build Variants:** Standard `debug` and `release`.
- **Application ID:** `id.bubakangreen.app`
- **Minimum SDK:** API 26 (Android 8.0 Oreo)
- **Target / Compile SDK:** API 35 (Android 15)
- **Secret Management:** No API keys, credentials, or service account JSON files committed to Git. `google-services.json` is treated as a local configuration file ignored by source control, with a clear `.template` provided for setup.

---

## 16. Test Strategy

1. **Model Serialization Unit Tests:**  
   Verify that `Location`, `MasterPlant`, and `LocationPlant` correctly serialize/deserialize to/from Firestore Maps without schema corruption.
2. **Deterministic QR Builder Tests:**  
   Verify `QrUrlBuilder.buildPlantUrl("plant_123")` outputs exactly `https://bubakangreen.web.app/plant/plant_123`.
3. **Security Rules Validation:**  
   Document test cases validating unauthenticated reads, PIC location isolation, and Admin override using the Firebase Local Emulator.
4. **Gradle Compilation Check:**  
   Execute `./gradlew assembleDebug` to verify dependency resolution and build integrity.

---

## 17. Local Development Guide Outline (`docs/phase-2/LOCAL_DEVELOPMENT.md`)

Will provide step-by-step instructions for any developer or reviewer:
1. Setting `JAVA_HOME` to Android Studio's bundled JDK 21 (`C:\Program Files\Android\Android Studio\jbr`).
2. Setting `ANDROID_HOME` to local Android SDK path.
3. Placing `google-services.json` in `app/`.
4. Running Gradle tasks (`./gradlew test`, `./gradlew assembleDebug`).
5. Running local static web server for testing web fallback (`npx serve web/public`).

---

## 18. Cost Impact Analysis

- **Android SDK & Tooling:** Rp0 (Free open source).
- **Firebase Authentication:** Rp0 (Up to 50,000 monthly active users on Spark plan).
- **Cloud Firestore:** Rp0 (1 GB storage, 50K reads/day, 20K writes/day covers Kelurahan Bubakan needs).
- **Firebase Hosting:** Rp0 (10 GB storage, 360 MB/day transfer).
- **Total Infrastructure Cost:** **Rp0 / Month**.

---

## 19. Risks & Mitigations

| Risk | Impact | Technical Mitigation |
|:---|:---|:---|
| Local Android SDK missing on machine | Build fails initially | Document explicit steps to point Gradle to the SDK via `local.properties` or Android Studio command-line tools. |
| Firebase project not yet created by user | Cannot connect to live cloud | Use local Firebase Emulator Suite or dummy `google-services.json` template for offline compilation. |
| Accidental Git Push to remote | Violates user directive | Remove remote push access or configure a pre-push Git hook preventing remote publishing. |

---

## 20. Acceptance Criteria (Phase 2)

- [ ] Android project compiles successfully via `./gradlew assembleDebug` with 0 errors.
- [ ] Application ID is registered as `id.bubakangreen.app` with Min SDK 26.
- [ ] Domain models for `Location`, `MasterPlant`, `LocationPlant`, and `UserSession` are implemented in Kotlin.
- [ ] Master plant data is strictly decoupled from location plant instances.
- [ ] Firestore security rules (`firestore.rules`) enforce role-based authorization.
- [ ] Static web fallback files exist in `web/public/` with total size <60KB.
- [ ] Unit tests pass for model serialization and URL builders.
- [ ] Local development setup is fully documented in `docs/phase-2/LOCAL_DEVELOPMENT.md`.
- [ ] No fake production botanical data is committed.
- [ ] Zero Git push commands executed.

---

## 21. Rollback Strategy

Since Phase 2 introduces new files on a clean working tree:
- If a build configuration fails unrecoverably, `git clean -fd` and `git checkout .` will revert the working tree back to commit `95cffd2` without data loss.

---

## 22. Next Phase Dependencies (Phase 3 Onward)

Phase 2 deliverables directly unlock:
- **Phase 3:** Public Android Experience (Home, Location List, Map, Plant Catalog, Detail with Mandarin Audio).
- **Phase 4:** Deep Linking & QR System (App Links verification, physical label asset generator).
- **Phase 5:** PIC Management Portal (Location registration form, GPS acquisition, Plant editor).
- **Phase 6:** Admin Governance (Approval queue, master oversight).
