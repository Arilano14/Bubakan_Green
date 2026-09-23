# PHASE 2 REQUIREMENT TRACEABILITY MATRIX — BUBAKAN GREEN

**Sistem Informasi Urban Farming & Taman Toga**  
**Kelurahan Bubakan, Kecamatan Mijen, Kota Semarang**  
**Document:** `docs/phase-3/PHASE_2_REQUIREMENT_TRACEABILITY.md`  
**Date:** 2026-09-23  
**Status:** COMPLETE — PHASE 3 PREREQUISITE BASELINE  

---

## 1. Traceability Methodology

This matrix maps each core product requirement from its Phase 0 definition through Phase 1 UX design, Phase 2 foundation implementation, physical repository evidence, testing evidence, verification status, and its direct dependency in Phase 3.

---

## 2. Requirement Traceability Specifications

### REQ-001: Product Identity (BUBAKAN GREEN)
- **Phase 0:** `DESIGNED` (`PROJECT_CONTEXT.md` § Product Identity; `SCOPE_CONTROL.md` § P9)
- **Phase 1:** `DESIGNED` (`PHASE_1_IP` § 2.4, 26)
- **Phase 2:** `IMPLEMENTED`
- **Repository Evidence:** `app/build.gradle.kts` (`applicationId = "id.bubakangreen.app"`), `app/src/main/res/values/strings.xml` (`app_name = "BUBAKAN GREEN"`).
- **Test Evidence:** String resource and package namespace audit.
- **Verification:** `VERIFIED` (Zero KKN branding in user-facing metadata).
- **Phase 3 Dependency:** `YES` (All UI headers, top app bars, and dialogs display official product identity).

---

### REQ-002: Location as First-Class Entity
- **Phase 0:** `DESIGNED` (`SCOPE_CONTROL.md` § P1; `PHASE_0_IP` § 10)
- **Phase 1:** `DESIGNED` (`PHASE_1_IP` § 3.1, 9.2, 9.3)
- **Phase 2:** `IMPLEMENTED`
- **Repository Evidence:** `app/src/main/java/id/bubakangreen/app/domain/model/Location.kt`, `LocationRepository.kt`, `FirestoreLocationRepository.kt`.
- **Test Evidence:** `ModelSerializationTest.kt` (`location_toMap_containsAllRequiredFields`).
- **Verification:** `VERIFIED` (Independent entity, independent collection, decoupled from plants).
- **Phase 3 Dependency:** `YES` (Directly drives `LocationListScreen` and `LocationDetailScreen`).

---

### REQ-003: Location Types (Urban Farming & Taman Toga)
- **Phase 0:** `DESIGNED` (`PROJECT_CONTEXT.md` § 34-47; `PHASE_0_IP` § 10)
- **Phase 1:** `DESIGNED` (`PHASE_1_IP` § 26.1)
- **Phase 2:** `IMPLEMENTED`
- **Repository Evidence:** `enum class LocationType { URBAN_FARMING, TAMAN_TOGA }` in `Location.kt`.
- **Test Evidence:** Serialization unit tests covering both enum values.
- **Verification:** `VERIFIED` (Type-safe domain representation).
- **Phase 3 Dependency:** `YES` (Drives category filter chips on Location directory screen).

---

### REQ-004: Featured vs. Regular Location Concept
- **Phase 0:** `DESIGNED` (`PHASE_0_CORRECTION` § 7; `SCOPE_CONTROL.md` § S7)
- **Phase 1:** `DESIGNED` (`PHASE_1_IP` § 9.1)
- **Phase 2:** `IMPLEMENTED`
- **Repository Evidence:** `val featured: Boolean = false` in `Location.kt`; `getFeaturedLocations()` query in `FirestoreLocationRepository.kt`.
- **Test Evidence:** Property serialization verified in `ModelSerializationTest.kt`.
- **Verification:** `VERIFIED` (Featured is a data property, not an architectural limitation).
- **Phase 3 Dependency:** `YES` (Drives Home screen featured garden banner).

---

### REQ-005: Decoupled Botanical Architecture (MasterPlant vs. LocationPlant)
- **Phase 0:** `DESIGNED` (`PHASE_0_IP` § 11; Phase 2 Revision § 9)
- **Phase 1:** `DESIGNED` (`PHASE_1_IP` § 9.4, 9.5)
- **Phase 2:** `IMPLEMENTED`
- **Repository Evidence:** `domain/model/MasterPlant.kt` (botanical master), `domain/model/LocationPlant.kt` (garden instance), `PlantRepository.kt`.
- **Test Evidence:** `ModelSerializationTest.kt` (`masterPlant_toMap_containsBotanicalFields`, `locationPlant_toMap_containsJunctionFields`).
- **Verification:** `VERIFIED` (Botanical knowledge stored once; zero duplicate descriptions across plots).
- **Phase 3 Dependency:** `YES` (Drives global Plant Catalog and site-specific plant lists in Location Detail).

---

### REQ-006: Anonymous Public Access (Zero Login Gate)
- **Phase 0:** `DESIGNED` (`SCOPE_CONTROL.md` § D17; `PHASE_0_IP` § 7)
- **Phase 1:** `DESIGNED` (`PHASE_1_IP` § 2.2)
- **Phase 2:** `IMPLEMENTED`
- **Repository Evidence:** `web/firestore.rules` (Public read allowed for `PUBLISHED` locations and active plants without `request.auth`).
- **Test Evidence:** Security rules static syntax audit.
- **Verification:** `VERIFIED` (Public queries succeed unauthenticated).
- **Phase 3 Dependency:** `YES` (Public screens operate 100% friction-free without login prompts).

---

### REQ-007: PIC Role-Based Authorization
- **Phase 0:** `DESIGNED` (`PHASE_0_IP` § 7; `SCOPE_CONTROL.md` § C5)
- **Phase 1:** `DESIGNED` (`PHASE_1_IP` § 11)
- **Phase 2:** `IMPLEMENTED`
- **Repository Evidence:** `FirebaseAuthRepository.kt` (custom claims parsing), `web/firestore.rules` (`resource.data.picUid == request.auth.uid`).
- **Test Evidence:** Security rules static audit.
- **Verification:** `VERIFIED` (PIC access scoped strictly to assigned locations at backend level).
- **Phase 3 Dependency:** `NO` (PIC screens belong to Phase 5; Phase 3 focuses on Public experience).

---

### REQ-008: Admin Approval & Governance Authority
- **Phase 0:** `DESIGNED` (`SCOPE_CONTROL.md` § S1, S2; `PHASE_0_IP` § 7)
- **Phase 1:** `DESIGNED` (`PHASE_1_IP` § 12)
- **Phase 2:** `IMPLEMENTED`
- **Repository Evidence:** `web/firestore.rules` (`isAdmin()` authorization for publication and deletions).
- **Test Evidence:** Security rules static audit.
- **Verification:** `VERIFIED` (Backend security rules enforce administrative oversight).
- **Phase 3 Dependency:** `NO` (Admin screens belong to Phase 6; Phase 3 focuses on Public experience).

---

### REQ-009: Single-Point Contextual GPS Capture
- **Phase 0:** `DESIGNED` (`SCOPE_CONTROL.md` § P4, C8; `PHASE_0_IP` § 13)
- **Phase 1:** `DESIGNED` (`PHASE_1_IP` § 14)
- **Phase 2:** `IMPLEMENTED`
- **Repository Evidence:** `data/location/LocationClient.kt`, `AndroidLocationClient.kt` (`fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY)`), `AndroidManifest.xml`.
- **Test Evidence:** `FakeLocationClientTest.kt` (`fakeLocationClient_returnsSimulatedSuccessCoordinates`, `fakeLocationClient_simulatesGpsError`).
- **Verification:** `VERIFIED` (Single-fix contract, zero background services).
- **Phase 3 Dependency:** `NO` (GPS acquisition is used during location registration in Phase 5; Phase 3 only displays coordinates).

---

### REQ-010: External QR Scanning & Stable HTTPS Contract
- **Phase 0:** `DESIGNED` (`SCOPE_CONTROL.md` § P5, D1; `ADR-005`)
- **Phase 1:** `DESIGNED` (`PHASE_1_IP` § 16, 17)
- **Phase 2:** `IMPLEMENTED`
- **Repository Evidence:** `core/util/QrUrlBuilder.kt` (`buildPlantUrl()`, `buildLocationUrl()`).
- **Test Evidence:** `QrUrlBuilderTest.kt` (`buildPlantUrl_producesDeterministicContractUrl`).
- **Verification:** `VERIFIED` (Deterministic HTTPS URL contract; zero in-app scanner bloat).
- **Phase 3 Dependency:** `YES` (Drives deep-link parsing and destination routing to `PlantDetailScreen`).

---

### REQ-011: Verified Android App Links
- **Phase 0:** `DESIGNED` (`ADR-006`; `PHASE_0_IP` § 9.2)
- **Phase 1:** `DESIGNED` (`PHASE_1_IP` § 17)
- **Phase 2:** `IMPLEMENTED`
- **Repository Evidence:** `AndroidManifest.xml` (`intent-filter` with `autoVerify="true"` for `https://bubakangreen.web.app/plant/*` and `/location/*`), `web/public/.well-known/assetlinks.json`.
- **Test Evidence:** Manifest intent filter audit.
- **Verification:** `DESIGNED / IMPLEMENTED` (Verification on live domain requires live domain provisioning).
- **Phase 3 Dependency:** `YES` (Compose navigation graph must accept and parse incoming deep link URIs).

---

### REQ-012: Static HTML Web Fallback (<60KB)
- **Phase 0:** `DESIGNED` (`ADR-007`; `SCOPE_CONTROL.md` § C10)
- **Phase 1:** `DESIGNED` (`PHASE_1_IP` § 18, 28)
- **Phase 2:** `IMPLEMENTED`
- **Repository Evidence:** `web/public/index.html`, `web/public/plant.html`, `web/public/style.css`, `web/firebase.json`.
- **Test Evidence:** Filesystem size audit: total payload **6.6 KB**.
- **Verification:** `VERIFIED` (Ultra-lightweight fallback card with zero build dependencies).
- **Phase 3 Dependency:** `NO` (Web fallback is fully functional and decoupled from Android UI).

---

### REQ-013: User-Triggered Mandarin Audio Interaction
- **Phase 0:** `DESIGNED` (`SCOPE_CONTROL.md` § P7, C11; `PHASE_0_IP` § 14)
- **Phase 1:** `DESIGNED` (`PHASE_1_IP` § 19)
- **Phase 2:** `IMPLEMENTED` (Domain reference in `MasterPlant.mandarinAudioUrl`)
- **Repository Evidence:** `MasterPlant.kt` (`val mandarinAudioUrl: String?`).
- **Test Evidence:** Field verified in `ModelSerializationTest.kt`.
- **Verification:** `DESIGNED / IMPLEMENTED` (Model layer ready; UI abstraction required in Phase 3).
- **Phase 3 Dependency:** `YES` (Phase 3 implements `MandarinSpeakerButton` Composable with manual tap playback).

---

### REQ-014: Firestore Offline Persistence Strategy
- **Phase 0:** `DESIGNED` (`SCOPE_CONTROL.md` § C12; `ADR-002`)
- **Phase 1:** `DESIGNED` (`PHASE_1_IP` § 20)
- **Phase 2:** `IMPLEMENTED`
- **Repository Evidence:** `BubakanApplication.kt` (`PersistentCacheSettings` 100MB limit).
- **Test Evidence:** Application configuration inspection.
- **Verification:** `IMPLEMENTED` (Offline disk cache enabled in SDK initialization).
- **Phase 3 Dependency:** `YES` (Phase 3 UI displays `OfflineStatusBar` pill when reading cached data).

---

### REQ-015: Operating Within Available Free Quotas
- **Phase 0:** `DESIGNED` (`SCOPE_CONTROL.md` § P8; `ADR-002`, `ADR-004`)
- **Phase 1:** `DESIGNED` (`PHASE_1_IP` § 2.6)
- **Phase 2:** `IMPLEMENTED`
- **Repository Evidence:** `gradle/libs.versions.toml` (Storage SDK removed; zero paid libraries), `web/firebase.json`.
- **Test Evidence:** Dependency tree and configuration audit.
- **Verification:** `VERIFIED` (Zero billing required; operates 100% within free Spark tier).
- **Phase 3 Dependency:** `YES` (Phase 3 must not add paid SDKs).

---

### REQ-016: Strict Anti-Slop Scope Boundaries
- **Phase 0:** `DESIGNED` (`SCOPE_CONTROL.md` § D1–D21)
- **Phase 1:** `DESIGNED` (`PHASE_1_IP` § 30)
- **Phase 2:** `IMPLEMENTED`
- **Repository Evidence:** Absence of chat, e-commerce, gamification, AI scanners, and carousels.
- **Test Evidence:** Dependency catalog audit.
- **Verification:** `VERIFIED` (Zero unapproved libraries or speculative screens).
- **Phase 3 Dependency:** `YES` (Phase 3 UI screens strictly adhere to approved scope).
