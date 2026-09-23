# PHASE 2 IMPLEMENTATION PLAN — BUBAKAN GREEN (REVISED)

**Sistem Informasi Urban Farming & Taman Toga**  
**Kelurahan Bubakan, Kecamatan Mijen, Kota Semarang**  
**Document:** `docs/phase-2/PHASE_2_IMPLEMENTATION_PLAN.md`  
**Date:** 2026-09-23  
**Version:** 2.0 (Post-Audit Revision)  
**Status:** DRAFT — AWAITING PRODUCT OWNER APPROVAL (`ACC PHASE 2`)  

---

## 1. Audit Summary

The comprehensive completion audit (`docs/phase-2/PHASE_0_1_COMPLETION_AUDIT.md`) and pre-execution readiness audit (`docs/phase-2/PHASE_2_FINAL_READINESS_AUDIT.md`) establish:
1. **Requirements Traced:** All 12 MVP capabilities and 10 architectural principles are formally mapped in `REQUIREMENT_TRACEABILITY.md`.
2. **Current Repository:** Clean greenfield state on branch `main`. Contains documentation only; zero lines of production code.
3. **Environment:** JDK 21 available in Android Studio (`C:\Program Files\Android\Android Studio\jbr\bin\java.exe`). Android SDK configuration to be established via `local.properties`.
4. **Scope Control:** Phase 2 establishes strictly the **technical foundation**. Media storage (Firebase Storage), user-facing UI screens, map SDK rendering, and remote Git pushes are completely excluded.

---

## 2. Phase 0 Status

- **Product Identity:** `LOCKED` (BUBAKAN GREEN — Kelurahan Bubakan; zero KKN branding).
- **Core Principles:** `LOCKED` (Anonymous public access, single-point GPS, external QR scanning, user-triggered Mandarin audio, data ≠ APK code).
- **Architecture Decisions:** `LOCKED` (Kotlin + Compose, Firebase NoSQL & Auth, static HTML fallback, MVVM + Repository).
- **Deferred Decisions:** Map provider selection (ADR-004) and audio production method remain deferred without blocking Phase 2.

---

## 3. Phase 1 Status

- **Information Architecture:** `LOCKED` (3-Tab structure: `Beranda`, `Lokasi & Peta`, `Katalog`).
- **Screen Inventory:** `LOCKED` (10 P0 screens, 3 P1 screens, 1 P2 modal).
- **Interaction Models:** `LOCKED` (Contextual GPS capture, external QR via App Links, subtle offline pill).
- **Design System Tokens:** `LOCKED` (Palette Alam Bubakan, Material 3 Typography).

---

## 4. Current Repository Status

- **Branch:** `main` (clean working tree).
- **Files Present:** Documentation only (`docs/source/`, `docs/phase-0/`, `docs/phase-1/`, `docs/phase-2/`).
- **Source Code Present:** None (0% implementation).
- **Git Remote:** Never pushed without explicit instruction. All operations remain strictly local.

---

## 5. Gaps to Close in Phase 2

1. Missing native Android project scaffold with Kotlin DSL (`.kts`) and Package ID `id.bubakangreen.app`.
2. Missing decoupled Kotlin domain entities: `Location`, `MasterPlant`, `LocationPlant`, and `UserSession`.
3. Missing repository interfaces and offline-enabled Firestore data access layers.
4. Missing declarative backend security rules (`firestore.rules`).
5. Missing lightweight static HTML web fallback (<60KB) in `web/public/`.
6. Missing local build verification and reproducible environment guide (`docs/phase-2/LOCAL_DEVELOPMENT.md`).

---

## 6. Phase 2 Objectives

1. Initialize Android project structure using AGP 8.7.3, Gradle 8.10.2, Kotlin 2.0.21, and Compose Compiler Plugin.
2. Implement the decoupled domain model architecture separating botanical knowledge from physical garden plantings.
3. Author production-grade `firestore.rules` enforcing role-based access control.
4. Scaffold the static web fallback files for QR users without the native app.
5. Create a safe Google Services configuration that compiles cleanly without fabricating fake credentials.
6. Provide unit tests and a reproducible local development guide.

---

## 7. Exact Implementation Scope (Revised)

| Component | Scope Included in Phase 2 | Scope Excluded from Phase 2 |
|:---|:---|:---|
| **Android Project Scaffold** | Root `build.gradle.kts`, `settings.gradle.kts`, `gradle/libs.versions.toml`, `app/build.gradle.kts`, `AndroidManifest.xml`, `MainActivity.kt` (stub test shell), design tokens (`Color.kt`, `Theme.kt`). | Feature screens, navigation graph execution, custom Compose widgets. |
| **Domain Data Models** | Kotlin data classes: `Location`, `MasterPlant`, `LocationPlant`, `UserSession`, `Coordinates`. Enums: `LocationType`, `LocationStatus`, `CoordinatesStatus`, `UserRole`. | Mock UI viewmodels, synthetic botanical data. |
| **Data Access Layer** | Repository interfaces (`LocationRepository`, `PlantRepository`, `AuthRepository`) and Firestore skeletons with offline disk persistence enabled. | Complete CRUD UI integration, retry managers. |
| **Hardware & Sensors** | `LocationClient` interface for single-shot GPS capture + `FakeLocationClient` for tests; App Link intent-filter manifest configuration. | Camera UI, interactive GPS dialogs, in-app QR scanner. |
| **Security & Backend** | Declarative `firestore.rules` covering `locations`, `master_plants`, `location_plants`, and `users`; `firebase.json` configuration. | Production cloud deployment, Firebase Storage SDK. |
| **Web Fallback** | Static HTML5/CSS files in `web/public/` (<60KB) for `/plant/{plantId}`. | React/Vue frameworks, web admin, CMS. |
| **Testing & Local Docs** | Unit tests for data models, QR URL builder, and location client contract; `docs/phase-2/LOCAL_DEVELOPMENT.md`. | End-to-end UI tests, CI/CD pipelines. |

---

## 8. Out of Scope (Strictly Prohibited in Phase 2)

- ❌ **Firebase Storage SDK:** Completely removed. No image upload, no audio upload, no media management in Phase 2.
- ❌ **Navigation Compose:** Removed from Phase 2. Screen routing belongs to Phase 3.
- ❌ **Production UI Screens:** Zero feature screens (`HomeScreen`, `LocationDetailScreen`, `PlantCatalogScreen`, `PlantDetailScreen`, `LoginScreen`).
- ❌ **In-App QR Scanner:** Permanently out of scope.
- ❌ **Interactive Map SDK UI:** Deferred to Phase 3.
- ❌ **Fake Google Services:** Zero fake `google-services.json` or fabricated credentials.
- ❌ **Remote Git Push:** Permanently prohibited.
- ❌ **Synthetic Data:** No fictitious plants or fake Bubakan locations.

---

## 9. Files & Modules to Create

```
Bubakan Green/
├── docs/
│   └── phase-2/
│        ├── PHASE_0_1_COMPLETION_AUDIT.md        [CREATED]
│        ├── REQUIREMENT_TRACEABILITY.md          [CREATED]
│        ├── PHASE_2_FINAL_READINESS_AUDIT.md     [CREATED]
│        ├── PHASE_2_IMPLEMENTATION_PLAN.md       [THIS FILE - REVISED]
│        ├── PHASE_2_SELF_REVIEW.md               [UPDATING]
│        └── LOCAL_DEVELOPMENT.md                 [DURING EXECUTION]
│
├── gradle/
│   ├── wrapper/
│   │    ├── gradle-wrapper.jar
│   │    └── gradle-wrapper.properties
│   └── libs.versions.toml                        [Version Catalog]
│
├── build.gradle.kts                              [Root build file]
├── settings.gradle.kts                           [Project settings]
├── gradlew                                       [Unix Gradle wrapper]
├── gradlew.bat                                   [Windows Gradle wrapper]
├── local.properties.template                     [SDK path template]
│
├── app/
│   ├── build.gradle.kts                          [App module build file]
│   ├── proguard-rules.pro                        [R8 optimization rules]
│   ├── google-services.json.template             [Documented template - NO FAKE DATA]
│   └── src/
│        ├── main/
│        │    ├── AndroidManifest.xml             [Manifest, App Links, Permissions]
│        │    ├── java/id/bubakangreen/app/
│        │    │    ├── BubakanApplication.kt      [Application class]
│        │    │    ├── MainActivity.kt            [Minimal compilation shell stub]
│        │    │    ├── core/
│        │    │    │    ├── result/Result.kt      [Functional Result wrapper]
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
│        │    │    │         ├── LocationClient.kt        [Contract]
│        │    │    │         └── AndroidLocationClient.kt [FusedLocationProvider]
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
│                  ├── core/util/QrUrlBuilderTest.kt
│                  └── data/location/FakeLocationClientTest.kt
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

None. All Phase 0 and Phase 1 documentation remain strictly immutable baselines.

---

## 11. Technical Architecture & Toolchain Matrix

### Toolchain Matrix
- **JDK:** OpenJDK 21 (bundled with Android Studio at `C:\Program Files\Android\Android Studio\jbr\bin\java.exe`).
- **Gradle:** 8.10.2.
- **Android Gradle Plugin (AGP):** 8.7.3.
- **Kotlin:** 2.0.21.
- **Compose Compiler:** `org.jetbrains.kotlin.plugin.compose:2.0.21`.
- **minSdk:** 26 (Android 8.0 Oreo).
- **targetSdk / compileSdk:** 35 (Android 15).

---

## 12. Data Model Specification

### 12.1 `Location` (`locations/{locationId}`)
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

### 12.2 `MasterPlant` (`master_plants/{plantId}`)
```kotlin
data class MasterPlant(
    val id: String,
    val nameId: String,          // Indonesian common name (e.g. "Jahe Merah")
    val nameLatin: String,       // Scientific name (e.g. "Zingiber officinale var. rubrum")
    val nameMandarin: String?,   // Chinese characters (e.g. "红生姜")
    val pinyin: String?,         // Romanized pronunciation (e.g. "hóng shēng jiāng")
    val description: String,     // Herbal benefits, pharmacology, cultivation guide
    val primaryPhotoUrl: String? = null,
    val mandarinAudioUrl: String? = null, // External HTTPS URL reference
    val createdAt: Long,
    val updatedAt: Long
)
```

### 12.3 `LocationPlant` (`location_plants/{locationPlantId}`)
```kotlin
data class LocationPlant(
    val id: String,
    val locationId: String,      // Foreign key to locations/{locationId}
    val masterPlantId: String,   // Foreign key to master_plants/{plantId}
    val localPhotoUrl: String?,  // Specific specimen photo at this garden
    val quantityNote: String?,   // e.g. "15 polybag"
    val notes: String?,          // Garden bed / plot location
    val featuredForQr: Boolean = false, // QR label generation eligibility
    val status: PlantStatus,     // ACTIVE, ARCHIVED
    val createdAt: Long,
    val updatedAt: Long
)
```

### 12.4 `UserSession` (`users/{userId}`)
```kotlin
data class UserSession(
    val uid: String,
    val email: String,
    val displayName: String,
    val role: UserRole,          // PUBLIC, PIC, ADMIN
    val assignedLocations: List<String> = emptyList()
)
```

---

## 13. Authentication & Authorization Model

- **Public:** Unauthenticated read of published locations and active plants. Zero write access.
- **PIC:** Authenticated via email/password. Token custom claim `role == "pic"`. Writes restricted to locations where `picUid == request.auth.uid`. Cannot alter location publication status.
- **Admin:** Authenticated via email/password. Token custom claim `role == "admin"`. Full approval and governance authority.

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

## 15. Safe Environment & Google Services Configuration

To guarantee that zero fake credentials enter the codebase:
1. `app/build.gradle.kts` conditionally applies the Google Services plugin:
   ```kotlin
   if (file("google-services.json").exists()) {
       apply(plugin = "com.google.gms.google-services")
   }
   ```
2. `app/google-services.json.template` documents the exact required schema and instructions for placing the real configuration file.
3. Live cloud connection status is reported as `NOT CONFIGURED / BLOCKED` until the real file is supplied.

---

## 16. Test Strategy

1. **Model Serialization Unit Tests:** Verify serialization and deserialization of `Location`, `MasterPlant`, and `LocationPlant`.
2. **Deterministic QR Builder Tests:** Verify `QrUrlBuilder.buildPlantUrl("plant_123")` outputs `https://bubakangreen.web.app/plant/plant_123`.
3. **GPS Contract Unit Tests:** Verify `FakeLocationClient` returns deterministic coordinates and accuracy metrics.
4. **Security Rules Validation:** Verify rule syntax and RBAC logic against specification.

---

## 17. Cost Impact & Guardrail Statement

> [!IMPORTANT]
> **REVISED COST COMMITMENT:**
> Phase 2 is designed to operate within available free quotas and uses no paid service by default. Any feature or dependency that may require billing must trigger a formal `CHANGE_REQUEST.md` before implementation.
> 
> No billing accounts are created or required. Zero paid APIs are introduced.

---

## 18. Local Development Guide Outline (`docs/phase-2/LOCAL_DEVELOPMENT.md`)

Will document:
1. `JAVA_HOME` configuration to Android Studio's bundled JDK 21 (`C:\Program Files\Android\Android Studio\jbr`).
2. `local.properties` setup pointing to the local Android SDK.
3. Running local unit tests: `./gradlew test`.
4. Assembling debug APK shell: `./gradlew assembleDebug`.
5. Testing static web fallback: `npx serve web/public`.

---

## 19. Risks & Mitigations

| Risk | Impact | Technical Mitigation |
|:---|:---|:---|
| Android SDK path missing | Build fails | Template `local.properties` provided with clear instructions. |
| Firebase live connection pending | Cannot sync to live Firestore | Safe conditional Gradle configuration; unit tests run without live backend. |
| Accidental Git Push | Violates command | Strict ban; all commits remain 100% local. |

---

## 20. Acceptance Criteria (Phase 2)

- [ ] Android project compiles successfully via `./gradlew assembleDebug` with 0 errors.
- [ ] Application ID is `id.bubakangreen.app` with Min SDK 26, Target SDK 35.
- [ ] Decoupled Kotlin domain models implemented for `Location`, `MasterPlant`, and `LocationPlant`.
- [ ] Firebase Storage SDK completely absent from dependencies.
- [ ] Safe Google Services configuration implemented without fake credentials.
- [ ] Declarative security rules (`firestore.rules`) enforce RBAC.
- [ ] Static web fallback files (<60KB) created in `web/public/`.
- [ ] Unit tests pass for models, URL builder, and location client contract.
- [ ] Local development setup documented in `docs/phase-2/LOCAL_DEVELOPMENT.md`.
- [ ] Zero fake botanical data committed.
- [ ] Zero Git push commands executed.

---

## 21. Rollback Strategy

Since Phase 2 introduces new files on a clean working tree:
- If a build configuration fails unrecoverably, `git clean -fd` and `git checkout .` will revert the working tree back to commit `6120934` without data loss.

---

## 22. Next Phase Dependencies

Phase 2 deliverables directly unlock:
- **Phase 3:** Public Android Experience (Home, Location List, Map, Plant Catalog, Detail with Mandarin Audio).
- **Phase 4:** Deep Linking & QR System (App Links verification, physical label asset generator).
- **Phase 5:** PIC Management Portal (Location registration form, GPS acquisition, Plant editor).
- **Phase 6:** Admin Governance (Approval queue, master oversight).
