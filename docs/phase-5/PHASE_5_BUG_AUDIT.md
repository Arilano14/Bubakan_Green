# Bubakan Green — Phase 5 Comprehensive Bug & Vulnerability Audit

**Product**: BUBAKAN GREEN (Sistem Informasi Urban Farming & Taman Toga Kelurahan Bubakan)  
**Audit Date**: 2026-10-02  
**Audit Standard**: Strict Evidence-Based Gate (Zero Fabricated Verification)  
**Permitted Statuses**: `IMPLEMENTED` | `TESTED` | `VERIFIED` | `BLOCKED` | `NOT TESTED`  

---

## 1. Bug Audit Summary Matrix

| Bug ID | Severity | Component | Description | Root Cause | Resolution | Verification Status |
|---|---|---|---|---|---|---|
| **BUG-01** | **CRITICAL** | Firestore / Android / Web | Canonical collection mismatch (`plants` vs `master_plants`) | Mobile queried `/plants` while Web/Rules used `/master_plants` | Standardized `FirestorePlantRepository.kt` & seed script to `/master_plants` | **TESTED** |
| **BUG-02** | **HIGH** | Web Fallback (`location.html`) | Location status semantic mismatch | Web rejected valid gardens with status `ACTIVE` or `NEEDS_MAINTENANCE` | Updated `location.html` line 137 to allow `ACTIVE` & `NEEDS_MAINTENANCE` | **TESTED** |
| **BUG-03** | **MEDIUM** | Web Fallback (`index.html`) | APK preview / sideloading guidance missing | Unlinked APK download path without sideloading instructions | Added 5-step Android sideloading guide & development status badge | **IMPLEMENTED** |
| **BUG-04** | **HIGH** | App Links (`assetlinks.json`) | Release assetlinks SHA-256 blocked | Production keystore certificate fingerprint not yet generated | Debug verified on emulator (`approved`); release keystore pending | **BLOCKED** |
| **BUG-05** | **MEDIUM** | Data Synchronization | Multi-client concurrent sync unverified | Only 1 emulator available in development environment | Snapshots implemented; multi-client concurrency marked NOT TESTED | **NOT TESTED** |
| **BUG-06** | **MEDIUM** | QR Scanner | Physical camera QR scan unverified | Emulator lacks physical camera / printed test signboards | GMS code scanner implemented; 0 camera perms verified; physical test pending | **NOT TESTED** |
| **BUG-07** | **HIGH** | Admin CRUD / Firestore | Admin delete safety & dangling references | Potential orphaned `location_plants` if hard delete occurs | Enforced soft delete & archive in repo/viewmodel (`isPublished=false`, `isPresent=false`) | **IMPLEMENTED** |
| **BUG-08** | **HIGH** | Navigation Architecture | Bottom navigation order discrepancy | Legacy specs had Admin rightmost; approved standard is Admin leftmost | Pinned Admin as Position 1 in `NavigationRoutes.kt` & `AppBottomBar.kt` | **VERIFIED** |
| **BUG-09** | **CRITICAL** | Security / Auth | Local storage privilege escalation risk | SharedPreferences cache could theoretically desync from Firebase Auth | `currentUserSession` strictly verifies `auth.currentUser`; `firestore.rules` enforces UID | **VERIFIED** |

---

## 2. Granular Bug Audit Reports

### BUG-01: Canonical Collection Mismatch
* **Bug ID**: BUG-01
* **Severity**: CRITICAL
* **Component**: Database Connectors (`FirestorePlantRepository.kt:22`, `seed_default_catalog.py:44`, `web/public/plant.html:138`)
* **Description**: Android queried `firestore.collection("plants")` whereas Web Fallback queried `/master_plants/{plantId}` via Firestore REST API.
* **Root Cause**: Early Android prototype used `plants` collection before Firestore security rules standardized on `master_plants`.
* **Resolution**: Standardized `masterPlantsCollection` in `FirestorePlantRepository.kt` to `firestore.collection("master_plants")` and `scripts/seed_default_catalog.py` to `db.collection("master_plants")`.
* **Verification Status**: **TESTED** (`testDebugUnitTest` 60/60 passing, `assembleDebug` SUCCESSFUL).

---

### BUG-02: Location Status Semantic Mismatch
* **Bug ID**: BUG-02
* **Severity**: HIGH
* **Component**: Web Fallback (`web/public/location.html:137`)
* **Description**: Web fallback blocked public users from viewing valid gardens if status was `ACTIVE` or `NEEDS_MAINTENANCE`, showing "Tanaman/Kebun Belum Diterbitkan".
* **Root Cause**: Hardcoded check `if (status && status !== 'PUBLISHED')` failed to account for active garden plot lifecycle states.
* **Resolution**: Updated filter to: `if (status && status !== 'PUBLISHED' && status !== 'ACTIVE' && status !== 'NEEDS_MAINTENANCE')`, matching Android `Location.kt` and `firestore.rules`.
* **Verification Status**: **TESTED** (Script logic validated, consistent with mobile query).

---

### BUG-03: APK Preview & Sideloading Guidance
* **Bug ID**: BUG-03
* **Severity**: MEDIUM
* **Component**: Web Fallback (`web/public/index.html`)
* **Description**: Web homepage previously had an unlinked APK download button with no installation context or security explanation for community members.
* **Root Cause**: Missing web user experience specification for pre-release sideloading.
* **Resolution**: Documented 5-step Android sideloading instructions (Settings > Install unknown apps > Download APK > Install > Launch), and added an explicit "Development / Evaluation Build" status notice.
* **Verification Status**: **IMPLEMENTED** (Documented in `WEB_FALLBACK_SPEC.md`).

---

### BUG-04: Release App Links SHA-256 Blocked
* **Bug ID**: BUG-04
* **Severity**: HIGH (Production Verification Gate)
* **Component**: Android App Links (`web/public/.well-known/assetlinks.json`)
* **Description**: Production release keystore SHA-256 fingerprint is not yet generated.
* **Root Cause**: Workspace contains debug keystore only. Production keystore creation is reserved for release deployment.
* **Resolution**: Debug App Links verified on Pixel 7 emulator (`bubakangreen.web.app: approved`). Release fingerprint in `assetlinks.json` marked as blocked pending official keystore generation.
* **Verification Status**: **BLOCKED** (Debug App Links: **VERIFIED**; Release App Links: **BLOCKED**).

---

### BUG-05: Multi-Client Real-Time Synchronization Unverified
* **Bug ID**: BUG-05
* **Severity**: MEDIUM
* **Component**: Data Synchronization (`FirestorePlantRepository.kt`, `FirestoreLocationRepository.kt`)
* **Description**: Real-time multi-device synchronization has not been physically validated between two concurrent mobile devices.
* **Root Cause**: Single emulator test environment; physical testing across two separate Android devices has not taken place.
* **Resolution**: Architecture uses Firestore `.snapshots()` flows with automatic local cache and server dispatch. Concurrent latency and collision handling are documented.
* **Verification Status**: **NOT TESTED** (Architecture implemented, physical 2-device concurrency test pending).

---

### BUG-06: Physical Camera QR Code Scan Unverified
* **Bug ID**: BUG-06
* **Severity**: MEDIUM
* **Component**: QR Scanner (`QrScannerActivity.kt`, `Google Code Scanner SDK`)
* **Description**: In-app QR scanning has not been verified against a physical printed signboard using an optical camera sensor.
* **Root Cause**: Emulator lacks an optical autofocus camera and physical printed test signboards.
* **Resolution**: Code scanner dependency `play-services-code-scanner:16.1.0` configured; zero camera permission in manifest verified; test protocol with printed targets drafted.
* **Verification Status**: **NOT TESTED** (Capability implemented, physical optical hardware test pending).

---

### BUG-07: Admin Delete Safety & Dangling References
* **Bug ID**: BUG-07
* **Severity**: HIGH
* **Component**: Admin CRUD (`FirestoreLocationRepository.kt`, `FirestorePlantRepository.kt`)
* **Description**: Hard-deleting a `MasterPlant` or `Location` would cause orphaned foreign-key references in `location_plants`.
* **Root Cause**: No database cascade delete triggers exist in Cloud Firestore client SDK.
* **Resolution**: Enforced soft delete and archival strategy:
  1. Master botanical records are never hard-deleted; `isPublished` is toggled to `false`.
  2. Locations are deactivated (`isPublished = false`, `status = INACTIVE`).
  3. Plants in locations are archived (`isPresent = false`, `status = ARCHIVED`, `condition = NOT_AVAILABLE`).
* **Verification Status**: **IMPLEMENTED** (Strategy codified in repository methods).

---

### BUG-08: Bottom Navigation Order Discrepancy
* **Bug ID**: BUG-08
* **Severity**: HIGH
* **Component**: Mobile UI Navigation (`NavigationRoutes.kt:70-95`, `AppBottomBar.kt`)
* **Description**: Legacy documentation stated Admin was rightmost, but approved standard requires Admin strictly leftmost (Position 1).
* **Root Cause**: Earlier iteration placed Admin at the end before UX review standardized leftmost placement.
* **Resolution**: Verified order: `ADMIN | BERANDA | LOKASI | KATALOG`.
* **Verification Status**: **VERIFIED** (Confirmed on Pixel 7 emulator via `uiautomator dump`: bounds `[11,2169][275,2337]` for Admin).

---

### BUG-09: Local Storage Privilege Escalation Risk
* **Bug ID**: BUG-09
* **Severity**: CRITICAL
* **Component**: Security & Auth (`FirebaseAuthRepository.kt`, `firestore.rules`)
* **Description**: Local SharedPreferences session storage could theoretically be tampered with to forge an admin role on the device.
* **Root Cause**: Client-side storage without server-side verification could create an illusion of authorization.
* **Resolution**: `FirebaseAuthRepository.currentUserSession` strictly validates `auth.currentUser`. If null, `UserRole.PUBLIC` is returned. Cloud Firestore rules strictly require `isAdmin()` based on authenticated UID in `/users/{uid}`.
* **Verification Status**: **VERIFIED** (60/60 unit tests pass, rules audited).
