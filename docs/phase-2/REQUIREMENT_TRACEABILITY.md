# REQUIREMENT TRACEABILITY MATRIX — BUBAKAN GREEN

**Sistem Informasi Urban Farming & Taman Toga**  
**Kelurahan Bubakan, Kecamatan Mijen, Kota Semarang**  
**Document:** `docs/phase-2/REQUIREMENT_TRACEABILITY.md`  
**Date:** 2026-09-23  
**Status:** COMPLETE — PHASE 2 TRACEABILITY BASELINE  

---

## 1. Traceability Methodology

To ensure zero requirement loss across the software development lifecycle, every approved capability and architectural constraint from Phase 0 and Phase 1 is traced forward through:
`REQUIREMENT → PHASE ORIGIN → UX / ARCHITECTURE DECISION → PHASE 2 FOUNDATION → LATER PHASE IMPLEMENTATION → TEST METHOD → VERIFICATION CRITERIA`

---

## 2. Core Traceability Matrix

### 2.1 Product Identity & Scope Control

| Item | Trace Chain |
|:---|:---|
| **REQ-01: Product Identity (BUBAKAN GREEN)** | **Origin:** Phase 0 (`PROJECT_CONTEXT.md` § Product Identity; `SCOPE_CONTROL.md` § P9)<br>**UX/ADR:** Phase 1 (`PHASE_1_IP` § 2.4, 26)<br>**Phase 2 Foundation:** Package name `id.bubakangreen.app`, `app_name = "BUBAKAN GREEN"` in `strings.xml`, clean branding tokens.<br>**Later Phases:** Phase 3 (Public UI branding), Phase 7 (APK release metadata).<br>**Test Method:** Static string & manifest inspection; visual review of UI headers.<br>**Verification:** Zero occurrences of KKN/student branding in user-facing UI or application metadata. |
| **REQ-02: Strict Anti-Slop Scope Boundaries** | **Origin:** Phase 0 (`SCOPE_CONTROL.md` § D1–D21); Phase 1 (`PHASE_1_IP` § 30)<br>**UX/ADR:** Explicit prohibition of e-commerce, AI scanners, chatbots, gamification, and social feeds.<br>**Phase 2 Foundation:** Dependency audit (`build.gradle.kts`) excluding speculative libraries or bloated frameworks.<br>**Later Phases:** All UI construction phases strictly reject unapproved widgets.<br>**Test Method:** Gradle dependency tree scan (`./gradlew app:dependencies`).<br>**Verification:** Zero unapproved libraries, zero speculative screens or UI widgets in the codebase. |

---

### 2.2 Location Domain & Hierarchy

| Item | Trace Chain |
|:---|:---|
| **REQ-03: Location as First-Class Entity** | **Origin:** Phase 0 (`SCOPE_CONTROL.md` § P1; `PHASE_0_IP` § 10)<br>**UX/ADR:** Phase 1 (`PHASE_1_IP` § 3, 9.2, 9.3) — Hierarchy: `Location → Plants in Location → Plant Detail`.<br>**Phase 2 Foundation:** Kotlin data class `Location.kt`, Firestore collection `locations`, `LocationRepository.kt`.<br>**Later Phases:** Phase 3 (Location List & Detail UI), Phase 5 (PIC Location Form UI).<br>**Test Method:** Unit tests for `Location` serialization & Firestore query filters.<br>**Verification:** Locations exist independently of plants; supports N dynamic locations beyond the initial two. |
| **REQ-04: Location Types (Urban Farming & Taman Toga)** | **Origin:** Phase 0 (`PROJECT_CONTEXT.md` § 34-47); `PHASE_0_IP` § 10<br>**UX/ADR:** Phase 1 (`PHASE_1_IP` § 26.1) — Filter chips and semantic color roles.<br>**Phase 2 Foundation:** Kotlin enum `LocationType { URBAN_FARMING, TAMAN_TOGA }` with Firestore string mapping.<br>**Later Phases:** Phase 3 (Filter chip filtering logic on location directory).<br>**Test Method:** Domain model unit test validating enum serialization.<br>**Verification:** Validated type safety; invalid type strings rejected by schema. |
| **REQ-05: Featured vs. Regular Locations** | **Origin:** Phase 0 (`PHASE_0_CORRECTION` § 7); Phase 1 (`PHASE_1_IP` § 9.1)<br>**UX/ADR:** Initial featured locations (Urban Farming Kelurahan & Taman Toga RW 03) highlighted in Home.<br>**Phase 2 Foundation:** `featured: Boolean = false` field in `Location.kt` and Firestore query `whereEqualTo("featured", true)`.<br>**Later Phases:** Phase 3 (Home featured banner rendering).<br>**Test Method:** Firestore query unit test filtering featured locations.<br>**Verification:** System dynamically handles featured vs non-featured without hardcoded layout branching. |

---

### 2.3 Plant Architecture & Separation of Concerns

| Item | Trace Chain |
|:---|:---|
| **REQ-06: Master Plant vs. Location Plant Separation** | **Origin:** Phase 0 (`PHASE_0_IP` § 11); Phase 2 Architecture Directive<br>**UX/ADR:** Central botanical knowledge card reusable across garden plots without duplicating botanical descriptions.<br>**Phase 2 Foundation:** Decoupled models:<br>1. `MasterPlant.kt` (`id`, `nameId`, `nameLatin`, `nameMandarin`, `pinyin`, `description`, `photoUrl`, `audioUrl`)<br>2. `LocationPlant.kt` (`id`, `locationId`, `masterPlantId`, `customPhotoUrl`, `quantityNote`, `status`, `featuredForQr`)<br>**Later Phases:** Phase 3 (Public Catalog & Detail UI), Phase 5 (PIC Plant Management UI).<br>**Test Method:** Repository integration test validating join/reference integrity between collections.<br>**Verification:** Updating botanical description in Master Plant reflects across all garden instances without re-entry. |
| **REQ-07: User-Triggered Mandarin Audio** | **Origin:** Phase 0 (`SCOPE_CONTROL.md` § P7, C11); Phase 1 (`PHASE_1_IP` § 19)<br>**UX/ADR:** `[ 🔊 ]` Speaker button, single-play on demand, zero autoplay, zero looping.<br>**Phase 2 Foundation:** `AudioPlayer.kt` interface, `AudioPlaybackState` sealed class (`Idle`, `Loading`, `Playing`, `Error`).<br>**Later Phases:** Phase 3 (`MandarinSpeakerButton` Compose UI integration).<br>**Test Method:** Unit test of audio player state machine and completion callbacks.<br>**Verification:** No audio execution occurs upon component mount; audio plays exactly once upon user trigger. |

---

### 2.4 Authentication, Roles & Security

| Item | Trace Chain |
|:---|:---|
| **REQ-08: Anonymous Public Access (Zero Login)** | **Origin:** Phase 0 (`SCOPE_CONTROL.md` § D17); Phase 1 (`PHASE_1_IP` § 2.2)<br>**UX/ADR:** General public reads locations, plants, maps, and audio without credentials or registration.<br>**Phase 2 Foundation:** `firestore.rules` allowing unauthenticated `read` on published documents; public navigation graph root.<br>**Later Phases:** Phase 3 (Public browsing implementation).<br>**Test Method:** Firebase Local Emulator security rules test simulating unauthenticated requests.<br>**Verification:** Public queries succeed with 200 OK without Firebase Auth session. |
| **REQ-09: PIC Role-Based Authorization** | **Origin:** Phase 0 (`PHASE_0_IP` § 7); Phase 1 (`PHASE_1_IP` § 11)<br>**UX/ADR:** PIC dashboard limited to managing assigned locations only.<br>**Phase 2 Foundation:** `AuthRepository.kt` resolving custom claims (`role == 'pic'`, `assignedLocations`); `firestore.rules` enforcing `request.auth.uid == resource.data.picUid`.<br>**Later Phases:** Phase 5 (PIC Dashboard & Management UI).<br>**Test Method:** Emulator rules test verifying PIC A cannot write to PIC B's location.<br>**Verification:** Unauthorized write requests rejected with permission-denied (403). |
| **REQ-10: Admin Approval & Governance** | **Origin:** Phase 0 (`SCOPE_CONTROL.md` § S1, S2); Phase 1 (`PHASE_1_IP` § 12)<br>**UX/ADR:** Admin approves new location registrations submitted by PICs.<br>**Phase 2 Foundation:** `LocationStatus { DRAFT, PENDING_APPROVAL, PUBLISHED, ARCHIVED }`; rules allowing status update only for `admin`.<br>**Later Phases:** Phase 6 (Admin Approval UI).<br>**Test Method:** Emulator rules test verifying non-admin cannot update `status` to `PUBLISHED`.<br>**Verification:** Public cannot query `PENDING_APPROVAL` records; only published locations appear in public views. |

---

### 2.5 Hardware Sensors, QR & Deep Linking

| Item | Trace Chain |
|:---|:---|
| **REQ-11: Single-Point Contextual GPS Capture** | **Origin:** Phase 0 (`SCOPE_CONTROL.md` § P4, C8); Phase 1 (`PHASE_1_IP` § 14)<br>**UX/ADR:** GPS acquired only when PIC explicitly taps button in location form; no background tracking.<br>**Phase 2 Foundation:** `LocationManager.kt` wrapper using Google Play Services `FusedLocationProviderClient.getCurrentLocation(PRIORITY_HIGH_ACCURACY)`.<br>**Later Phases:** Phase 5 (PIC Location Form UI with mini map preview).<br>**Test Method:** Unit test verifying single location callback and immediate cancellation of location requests.<br>**Verification:** Zero background location listeners; permission requested strictly in context. |
| **REQ-12: External QR & Stable HTTPS URL** | **Origin:** Phase 0 (`SCOPE_CONTROL.md` § P5, D1); Phase 1 (`PHASE_1_IP` § 16)<br>**UX/ADR:** In-app QR scanner banned; QR encodes `https://bubakangreen.web.app/plant/{id}`.<br>**Phase 2 Foundation:** `QrUrlBuilder.kt` generating standard URLs; QR generator bitmap helper.<br>**Later Phases:** Phase 5 (QR Preview & Label Print UI).<br>**Test Method:** URL parser unit test validating deterministic output from entity IDs.<br>**Verification:** Modifying plant description does not alter QR URL; zero in-app scanner dependencies added. |
| **REQ-13: Verified Android App Links** | **Origin:** Phase 0 (`ADR-006`); Phase 1 (`PHASE_1_IP` § 17)<br>**UX/ADR:** QR scan opens native app directly if installed, without browser chooser dialog.<br>**Phase 2 Foundation:** `AndroidManifest.xml` intent-filter configuration with `autoVerify="true"` for domain `bubakangreen.web.app`; `assetlinks.json` template.<br>**Later Phases:** Phase 3 (Deep link argument parsing in Compose Navigation).<br>**Test Method:** `adb shell am start -a android.intent.action.VIEW -d "https://bubakangreen.web.app/plant/test"` simulation.<br>**Verification:** Deep link routes directly to destination screen when app is installed. |
| **REQ-14: Lightweight Static Web Fallback** | **Origin:** Phase 0 (`ADR-007`); Phase 1 (`PHASE_1_IP` § 18, 28)<br>**UX/ADR:** Responsive single-card HTML/CSS fallback (<60KB) showing essential plant data + APK download CTA.<br>**Phase 2 Foundation:** Static files `web/public/index.html` and `web/public/plant.html`; `firebase.json` hosting rewrites.<br>**Later Phases:** Phase 7 (Static web deployment to Firebase Hosting).<br>**Test Method:** Local HTTP server test validating layout responsiveness & asset payload size (<60KB).<br>**Verification:** Page renders plant information in browser without client-side build frameworks or NPM runtime. |

---

### 2.6 Offline Resilience & Cost Guardrails

| Item | Trace Chain |
|:---|:---|
| **REQ-15: Firestore Offline Persistence** | **Origin:** Phase 0 (`SCOPE_CONTROL.md` § C12; `ADR-002`); Phase 1 (`PHASE_1_IP` § 20)<br>**UX/ADR:** Seamless access to previously viewed locations/plants during offline garden visits.<br>**Phase 2 Foundation:** Firestore SDK initialization with `persistentDiskCacheSettingsEnabled = true`.<br>**Later Phases:** Phase 3 (`OfflineStatusBar` UI pill in Compose).<br>**Test Method:** Airplane mode instrumentation test reading previously cached collections.<br>**Verification:** Cached documents load without network exception when offline. |
| **REQ-16: Target Cost Rp0 Within Free Tiers** | **Origin:** Phase 0 (`SCOPE_CONTROL.md` § P8; `ADR-002`, `ADR-004`)<br>**UX/ADR:** Zero financial burden on Kelurahan Bubakan.<br>**Phase 2 Foundation:** Strict adherence to Firebase Spark Free Tier (Firestore 50K reads/day, Auth 10K/mo, Hosting 10GB).<br>**Later Phases:** Phase 7 (Billing & Handover documentation).<br>**Test Method:** Firebase console quota monitoring and architecture audit.<br>**Verification:** Zero paid Cloud Functions, zero paid third-party APIs required for operation. |
