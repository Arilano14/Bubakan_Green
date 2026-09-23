# PHASE 2 FINAL READINESS AUDIT — BUBAKAN GREEN

**Sistem Informasi Urban Farming & Taman Toga**  
**Kelurahan Bubakan, Kecamatan Mijen, Kota Semarang**  
**Document:** `docs/phase-2/PHASE_2_FINAL_READINESS_AUDIT.md`  
**Date:** 2026-09-23  
**Status:** COMPLETE — PRE-EXECUTION AUDIT CERTIFIED  

---

## 1. Phase 0 Readiness Audit

| Requirement / Item | Design Decision | Phase 2 Implication | Implementation Target | Verification Method | Current Status |
|:---|:---|:---|:---|:---|:---:|
| **Product Identity** | `BUBAKAN GREEN` for Kelurahan Bubakan; zero KKN branding (`PROJECT_CONTEXT.md`) | Application metadata and string resources must be strictly Bubakan-centered. | `app_name` in `strings.xml`, package ID `id.bubakangreen.app`. | Static code audit; APK manifest inspection. | `DESIGNED` |
| **Location First-Class** | Location is an independent entity, not a field on Plant (`SCOPE_CONTROL.md` § P1). | Dedicated collection and domain entity required. | `Location.kt` domain model, `LocationRepository.kt`. | Unit tests for entity creation and serialization. | `DESIGNED` |
| **Location Types** | Must support `URBAN_FARMING` and `TAMAN_TOGA` for N locations (`PHASE_0_IP` § 10). | Enum with Firestore string mapping; dynamic query filters. | `enum class LocationType`. | Serialization unit tests with both types. | `DESIGNED` |
| **Plant Master vs Location Separation** | Botanical master encyclopedia decoupled from local garden plantings. | Two distinct entities: `MasterPlant` and `LocationPlant`. | Decoupled Kotlin models and repository contracts. | Unit tests validating foreign key relationships. | `DESIGNED` |
| **GPS Capture** | Single-shot capture only; no continuous/background tracking (`SCOPE_CONTROL.md` § P4). | Contextual permission & single-fix location client interface. | `LocationClient` interface with mock implementation. | Mock location provider test. | `DESIGNED` |
| **QR Contract** | Deterministic HTTPS URL reference; no botanical content inside QR (`ADR-005`). | Stable URL pattern `/plant/{id}` without internal scanner. | `QrUrlBuilder.kt` utility. | URL generation unit tests. | `DESIGNED` |
| **Mandarin Audio** | User-triggered playback; no autoplay, looping, or synthetic AI voice (`SCOPE_CONTROL.md` § P7). | Decoupled audio player contract; media handling deferred. | `AudioPlayer` interface state machine. | State transition unit test (`Idle` -> `Playing`). | `DESIGNED` |
| **Public Zero-Barrier** | Anonymous access; no public login or registration gates (`SCOPE_CONTROL.md` § D17). | Firestore security rules permit unauthenticated reads on published data. | `firestore.rules` public match blocks. | Security rules local simulation. | `DESIGNED` |
| **PIC / Admin Roles** | PIC manages assigned locations; Admin oversees kelurahan and approvals (`PHASE_0_IP` § 7). | Firebase Auth custom claims validation; role-scoped security rules. | `AuthRepository.kt` & `firestore.rules`. | Security rules local simulation. | `DESIGNED` |
| **Cost Guardrail** | Operate within free quotas by default; no paid services without approval. | No paid APIs, no Cloud Functions, no Firebase Storage in Phase 2. | Spark plan constraints enforced. | Architecture & billing audit. | `DESIGNED` |
| **Data ≠ APK Code** | Dynamic updates without APK re-compilation (`SCOPE_CONTROL.md` § P2). | All botanical & garden content fetched via Firestore. | Firestore data layer with offline persistence. | Model & repository contract test. | `DESIGNED` |

---

## 2. Phase 1 Readiness Audit

| Specification Item | Design Decision | Phase 2 Implication | Implementation Target | Verification Method | Current Status |
|:---|:---|:---|:---|:---|:---:|
| **Information Architecture** | 3-tab model: `Beranda`, `Lokasi & Peta`, `Katalog` (`PHASE_1_IP` § 3). | Route constants mapped in core domain. | `NavigationRoutes.kt` constants only (no UI). | Code review. | `DESIGNED` |
| **Design System Tokens** | Palette Alam Bubakan & Material 3 Typography (`PHASE_1_IP` § 26). | Theme definition files created for future UI phases. | `Color.kt`, `Type.kt`, `Theme.kt`. | Compilation check. | `DESIGNED` |
| **Web Fallback Card** | Responsive static HTML (<60KB) showing plant summary + APK CTA (`PHASE_1_IP` § 28). | Static files placed in `web/public/`. | `index.html`, `plant.html`, `style.css`. | Local HTTP file size & render test. | `DESIGNED` |
| **Offline UI States** | Non-intrusive status pill; local disk caching (`PHASE_1_IP` § 20). | Enable Firestore offline disk cache. | Firestore client initialization settings. | Offline persistence configuration test. | `DESIGNED` |
| **Anti-Slop Scope** | Prohibit chat, e-commerce, gamification, and carousels (`PHASE_1_IP` § 30). | Dependency tree stripped of any unapproved libraries. | Minimal `build.gradle.kts`. | Dependency tree audit. | `DESIGNED` |

---

## 3. Revised Phase 2 Implementation Scope

Phase 2 establishes the **technical foundation only**. It is explicitly constrained as follows:

### What IS in Phase 2:
1. Native Android project scaffold (`id.bubakangreen.app`) with Gradle Kotlin DSL (`.kts`).
2. Build toolchain matrix configuration (JDK 21, Gradle 8.10.2, AGP 8.7.3, Kotlin 2.0.21, Compose Compiler Plugin).
3. Domain models: `Location`, `MasterPlant`, `LocationPlant`, `UserSession`, `Coordinates`.
4. Data access contracts: `LocationRepository`, `PlantRepository`, `AuthRepository` (interfaces + Firestore skeletons).
5. Safe Google Services configuration (conditional plugin application, zero fake credentials).
6. Declarative backend security rules (`firestore.rules`) enforcing strict RBAC.
7. Offline persistence strategy configuration for Cloud Firestore.
8. Single-shot GPS location provider contract (`LocationClient`) with test fake.
9. Deterministic QR URL builder utility (`QrUrlBuilder`).
10. Static web fallback structure (<60KB) in `web/public/`.
11. Unit test suite for models, URL builder, and location client contract.
12. Local development guide (`docs/phase-2/LOCAL_DEVELOPMENT.md`).

### What is REMOVED / DEFERRED from Phase 2:
- ❌ **Firebase Storage SDK:** Completely removed. No image upload, no audio upload, no media management in Phase 2.
- ❌ **Navigation Compose Library:** Removed from Phase 2. Screen routing belongs to Phase 3.
- ❌ **All Production UI Screens:** Zero feature screens (`HomeScreen`, `LocationDetailScreen`, `PlantCatalogScreen`, `PlantDetailScreen`, `LoginScreen`). `MainActivity` is strictly a minimal compilation test shell.
- ❌ **In-App QR Scanner:** Permanently out of scope.
- ❌ **Interactive Map SDK UI:** Deferred to Phase 3.
- ❌ **Production QR Generation & Printing:** Blocked until real field validation.
- ❌ **Remote Git Push:** Permanently prohibited.

---

## 4. Strict Dependency Minimalism Audit

Every single proposed library has been scrutinized against the minimalist standard:

| Dependency | Phase 2 Need | Justification | Alternative Evaluated | Complexity / Risk | Verdict |
|:---|:---:|:---|:---|:---|:---:|
| `com.google.firebase:firebase-bom:33.9.0` | **YES** | Manages official compatible versions for Firestore and Auth. | Manual versioning (prone to mismatch). | Low risk; Google standard. | **APPROVED** |
| `com.google.firebase:firebase-firestore` | **YES** | Core database access layer and offline disk persistence. | SQLite/Room (requires custom sync server). | Low risk; fits NoSQL schema. | **APPROVED (Main Module)** |
| `com.google.firebase:firebase-auth` | **YES** | Authentication foundation for PIC and Admin roles. | Custom token auth (adds server complexity). | Low risk; free up to 50K MAU. | **APPROVED (Main Module)** |
| `com.google.firebase:firebase-storage` | **NO** | Media handling is deferred to later phases. | Local files / external links. | None. Removed. | **REMOVED** |
| `androidx.compose.material3:material3` | **YES** | Validates design system theme tokens (`Color.kt`, `Theme.kt`). | Legacy XML theme (deprecated). | Low risk; modern Android standard. | **APPROVED** |
| `androidx.navigation:navigation-compose` | **NO** | No UI screens or navigation graphs built in Phase 2. | Not needed until Phase 3. | Unnecessary for foundation. | **REMOVED** |
| `com.google.android.gms:play-services-location` | **YES** | Required to implement `AndroidLocationClient` for single-shot GPS. | Android `LocationManager` (less accurate outdoors). | Minimal; standard Google API. | **APPROVED** |
| `junit:junit:4.13.2` | **YES** | Unit testing domain models and URL builders. | None. | Standard test framework. | **APPROVED** |
| `org.jetbrains.kotlinx:kotlinx-coroutines-test` | **YES** | Unit testing repository coroutines and async state. | Blocking test thread (unreliable). | Standard Kotlin test library. | **APPROVED** |

*Result:* Total runtime dependencies reduced to the absolute bare minimum (Firestore, Auth, Compose Material3 foundation, and Location provider). All deprecated Firebase KTX libraries eliminated in favor of unified main modules.

---

## 5. Firebase Strategy & Cost Guardrail Audit

### 5.1 Revised Scope
- **Active in Phase 2:** Cloud Firestore, Firebase Authentication, Firebase Hosting configuration, Firestore Security Rules.
- **Removed from Phase 2:** Firebase Storage, Firebase Functions, Firebase Analytics.

### 5.2 Revised Free Quota Commitment
> [!IMPORTANT]
> **REVISED COST CLAUSE:**
> Phase 2 is designed to operate within available free quotas and uses no paid service by default. Any feature or architectural change that may require billing must trigger a formal `CHANGE_REQUEST.md` before implementation.
> 
> No Google Cloud billing account is activated by the project code. No credit card is required to compile, test, or run Phase 2 deliverables.

---

## 6. Security Model & Role Validation Audit

### 6.1 Role Definitions
- **`PUBLIC`:** Unauthenticated. Read-only access to documents where `status == "PUBLISHED"` in `locations` and `status == "ACTIVE"` in `master_plants` and `location_plants`. Zero write access anywhere.
- **`PIC`:** Authenticated via Firebase Auth. Must possess token custom claim `role == "pic"`.
  - May create new locations with initial status `PENDING_APPROVAL`.
  - May update location data *only* where `resource.data.picUid == request.auth.uid`.
  - Cannot alter `status` (cannot approve own location).
  - Cannot modify user roles or security claims.
- **`ADMIN`:** Authenticated via Firebase Auth. Must possess token custom claim `role == "admin"`.
  - Full management, approval authority (`status = "PUBLISHED"`), and user metadata assignment.

### 6.2 Custom Claims Governance
- Custom claims are issued strictly via administrative scripts using the Firebase Admin SDK (or Firebase Console).
- Client applications **never** set or modify their own claims.
- Application code validates user permissions using `auth.currentUser.getIdTokenResult()` and falls back gracefully to `PUBLIC` permissions if claims are absent or expired.
- Client UI visibility checks are purely cosmetic; actual access enforcement is handled 100% by `firestore.rules`.

---

## 7. Data Model Audit: Decoupled Master vs. Location Plants

```
[ master_plants ] (Collection)
  ├── id: "PLANT_JAHE_MERAH"
  ├── nameId: "Jahe Merah"
  ├── nameLatin: "Zingiber officinale var. rubrum"
  ├── nameMandarin: "红生姜"
  ├── pinyin: "hóng shēng jiāng"
  ├── description: "Tanaman rimpang herbal untuk daya tahan tubuh..."
  └── audioUrl: null (deferred to field validation)
         │
         │ (1 to N relationship)
         ▼
[ location_plants ] (Collection)
  ├── id: "LP_RW03_001"
  ├── locationId: "LOC_TOGA_RW03"  ───► References [ locations ]
  ├── masterPlantId: "PLANT_JAHE_MERAH"
  ├── quantityNote: "20 polybag"
  ├── notes: "Ditanam di bedeng herbal timur"
  ├── featuredForQr: true
  └── status: "ACTIVE"
```

- **Strict Botanical Rule:** No fictitious plants or fake Bubakan locations will be seeded. Unit tests will use clearly marked test fixtures (e.g. `TEST_FIXTURE_PLANT_001`).

---

## 8. QR Contract Audit

- **Concept:** QR encodes solely a stable URL referencing an entity ID:  
  `https://bubakangreen.web.app/plant/{masterPlantId}` (or `/location/{locationId}`).
- **Data Invariance:** Botanical descriptions, photos, and planting quantities can be updated in Firestore at any time without altering the QR URL or requiring label reprinting.
- **Phase 2 Boundary:** Phase 2 implements the deterministic `QrUrlBuilder` utility and unit tests. No physical QR codes are generated, printed, or deployed in Phase 2.

---

## 9. Web Fallback Audit

- **Architecture:** Lightweight static HTML5, CSS, and minimal vanilla JavaScript.
- **Payload:** Total assets `<60KB`.
- **Scope:** Renders plant botanical identity and displays a prominent "Unduh Aplikasi BUBAKAN GREEN (Android)" CTA.
- **Guardrail:** No full web application, no web admin panel, no CMS, no user login on web.
- **App Link Verification:** Digital Asset Links (`assetlinks.json`) template is documented, but App Link verification will remain marked `NOT CONFIGURED` until the live domain and signing keys are provisioned.

---

## 10. Offline Strategy Audit

- **Persistence Engine:** Cloud Firestore local disk persistence enabled by default via `persistentDiskCacheSettings`.
- **Read State:**
  - `ONLINE`: Serves latest server data and updates local cache.
  - `OFFLINE`: Serves cached data seamlessly.
- **Write State & Security Boundary:**
  - Standard user reads function offline.
  - **Critical Security Operations** (e.g., Admin approving a location, assigning PIC roles) require network connectivity. Offline cached authorization is never treated as authoritative for security-critical mutations.

---

## 11. Android Build Toolchain Compatibility Matrix

| Tool / Layer | Target Version | Compatibility Validation |
|:---|:---|:---|
| **Android Studio** | Android Studio 2025.3.x | Installed at `C:\Program Files\Android\Android Studio`. |
| **JDK** | OpenJDK 21.0.9 (JetBrains Runtime) | Installed at `C:\Program Files\Android\Android Studio\jbr\bin\java.exe`. |
| **Gradle** | 8.10.2 | Fully compatible with JDK 21 and AGP 8.7+. |
| **Android Gradle Plugin (AGP)** | 8.7.3 | Official Google stable release; supports Gradle 8.9+, JDK 17–21. |
| **Kotlin** | 2.0.21 | Stable release compatible with AGP 8.7.3 and Compose Compiler Plugin. |
| **Compose Compiler Plugin** | `org.jetbrains.kotlin.plugin.compose:2.0.21` | Bundled directly with Kotlin 2.0+ (replaces deprecated compiler library). |
| **minSdk** | 26 (Android 8.0 Oreo) | Covers ~95%+ of active Android devices; supports modern java.time & security. |
| **targetSdk / compileSdk** | 35 (Android 15) | Current standard Android API level. |

---

## 12. Safe Google-Services Configuration (Zero Fake Credentials)

To guarantee that zero fake credentials or fabricated project IDs enter the codebase:
1. `build.gradle.kts` will define the Google Services plugin dependency in `buildscript/plugins`.
2. In `app/build.gradle.kts`, the Google Services plugin will be applied **conditionally**:
   ```kotlin
   if (file("google-services.json").exists()) {
       apply(plugin = "com.google.gms.google-services")
   }
   ```
3. A documented template file `app/google-services.json.template` will be provided explaining the exact JSON structure and placement.
4. If `google-services.json` is absent:
   - The project builds and compiles cleanly.
   - All domain models, utilities, and local unit tests execute 100%.
   - Live cloud connection status is reported truthfully as: `NOT CONFIGURED / BLOCKED`.
   - Zero fabricated credentials exist.

---

## 13. Testing Strategy (Phase 2)

| Test Category | Target Component | Method | Success Criteria |
|:---|:---|:---|:---|
| **Build Integrity** | Root & App Gradle scripts | `./gradlew assembleDebug` | Exit code 0, 0 compiler errors. |
| **Data Model Serialization** | `Location`, `MasterPlant`, `LocationPlant` | JUnit 4 + Truth unit tests | 100% roundtrip map conversion fidelity. |
| **QR Contract** | `QrUrlBuilder` | JUnit 4 unit tests | Outputs exact URL matching `/plant/{id}`. |
| **GPS Interface** | `LocationClient` contract | `FakeLocationClient` test | Returns deterministic coordinates without device hardware. |
| **Security Rules** | `firestore.rules` | Static syntax & structure audit | Valid rule structure matching RBAC specifications. |

---

## 14. Git Safety Verification

- **Command Audit:** Strict ban on `git push`, `git push --all`, `git push origin`, `git push --force`.
- **Status:** All Phase 2 work will remain strictly in the local working tree and local commits. No remote network interaction with GitHub will be executed.

---

## 15. Remaining Blockers & Unknown Items

1. **Android SDK Local Path (`BLOCKED` for CLI build without configuration):**
   - The Android SDK path is not in standard environment variables.
   - *Resolution:* Phase 2 scaffold will generate `local.properties` template and document setting `sdk.dir` or running builds inside Android Studio.
2. **Real `google-services.json` (`NOT CONFIGURED`):**
   - Requires the project owner to register the Firebase project and download the configuration file.
   - *Resolution:* Handled safely via conditional Gradle loading without fake files.
3. **Map Provider Selection (ADR-004) (`UNKNOWN`):**
   - Remains deferred to Product Owner. Does not block Phase 2 foundation.
4. **Mandarin Audio Production Method (`UNKNOWN`):**
   - Remains deferred until plant inventory validation. Does not block Phase 2 foundation.

---

## 16. Required User Decisions

No new architectural decisions are required from the user at this point. All adjustments requested in the revision directive (removal of Firebase Storage, cost statement refinement, toolchain matrix alignment, zero fake credentials, and dependency minimization) have been integrated into this readiness audit and updated implementation plan.

---

## 17. Final Pre-Execution Gate

All pre-execution revision audits are complete. No production code has been generated.

**READINESS STATUS:**
### `READY FOR ACC PHASE 2`
