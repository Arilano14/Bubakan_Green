# PHASE 2 SELF-REVIEW — BUBAKAN GREEN

**Sistem Informasi Urban Farming & Taman Toga**  
**Kelurahan Bubakan, Kecamatan Mijen, Kota Semarang**  
**Document:** `docs/phase-2/PHASE_2_SELF_REVIEW.md`  
**Reviewer Role:** Senior Software Architect, Senior Android Lead & Security Auditor  
**Date:** 2026-09-23  
**Status:** COMPLETE — READY FOR APPROVAL GATE  

---

## 1. Review Objectives

This self-review critically examines `docs/phase-2/PHASE_2_IMPLEMENTATION_PLAN.md` against Phase 0 and Phase 1 requirements, software engineering rigor, security constraints, and operational feasibility.

---

## 2. Mandatory Verification Checklist

| # | Review Item | Status | Evaluation Notes |
|:---|:---|:---:|:---|
| 1 | Phase 0 requirements correctly traced | ✅ PASS | Every principle (P1–P10) and core capability (C1–C12) is mapped in `REQUIREMENT_TRACEABILITY.md`. |
| 2 | Phase 1 requirements correctly traced | ✅ PASS | IA, 3-tab navigation, and interaction models directly inform domain models and navigation scaffold. |
| 3 | No requirement was silently changed | ✅ PASS | Zero requirements modified. Deferrals (Map provider, audio production method) remain explicitly documented. |
| 4 | No fake production data introduced | ✅ PASS | Anti-synthetic botanical data rule enforced. Only generic, clearly marked test fixtures used for tests. |
| 5 | Location is first-class entity | ✅ PASS | `Location` entity is an independent collection, not a secondary attribute of plant records. |
| 6 | New locations are supported | ✅ PASS | Dynamic schema supporting N locations beyond initial featured ones (`Urban Farming Kelurahan`, `Taman Toga RW 03`). |
| 7 | GPS foundation is supported | ✅ PASS | Single-shot `FusedLocationProviderClient` interface with no background services or geofencing. |
| 8 | Plant master/location relationship is correct | ✅ PASS | Explicitly decoupled: `MasterPlant` (botanical encyclopedia) linked via `LocationPlant` junction records. |
| 9 | User/PIC/Admin roles are correct | ✅ PASS | Roles mapped to Firebase Custom Claims and enforced both in application logic and security rules. |
| 10 | Security is enforced at backend level | ✅ PASS | `firestore.rules` enforces authorization independent of client UI visibility. |
| 11 | Public does not need login | ✅ PASS | Public queries to published locations and active plants succeed unauthenticated. |
| 12 | Data is separated from application code | ✅ PASS | Dynamic Firestore data access ensures zero APK re-compilation for botanical updates. |
| 13 | QR is not prematurely generated | ✅ PASS | Code guard enforces `featuredForQr == true` and validated status prior to QR export. |
| 14 | Real plant inventory remains a field dependency | ✅ PASS | Documented as a hard blocker for production data population. |
| 15 | Mandarin audio remains user-triggered | ✅ PASS | Audio player interface exposes explicit user-play commands with zero autoplay logic. |
| 16 | Website remains supporting infrastructure | ✅ PASS | Static HTML fallback (<60KB) showing plant summary + APK download CTA; no web admin. |
| 17 | No unnecessary web admin exists | ✅ PASS | Admin uses the mobile Android app; zero web CMS or React dashboards. |
| 18 | No unnecessary server exists | ✅ PASS | Fully serverless (Firebase Firestore, Auth, Storage, Hosting). |
| 19 | No unnecessary dependencies exist | ✅ PASS | Clean Gradle setup using only official Google/AndroidX/Firebase BOM dependencies. |
| 20 | No paid service introduced silently | ✅ PASS | Target cost strictly Rp0 within Firebase Spark Free Tier. |
| 21 | Offline strategy is realistic | ✅ PASS | Standard Firestore persistent disk cache utilized; no redundant custom sync engines. |
| 22 | Test strategy exists | ✅ PASS | Unit testing for model serialization, URL builders, and security rule simulation. |
| 23 | Build strategy exists | ✅ PASS | Kotlin DSL Gradle build scripts targeting API 26–35. |
| 24 | Local setup is reproducible | ✅ PASS | Documented in `LOCAL_DEVELOPMENT.md` utilizing Android Studio's bundled JDK 21. |
| 25 | Scope is appropriate for Phase 2 | ✅ PASS | Confined to architectural foundation. No user-facing feature screens prematurely coded. |
| 26 | No unnecessary refactoring | ✅ PASS | Greenfield project; zero existing files rewritten. |
| 27 | No GitHub push will occur | ✅ PASS | Local Git version control only. All remote push commands strictly barred. |

---

## 3. Engineering Risk Analysis & Concrete Mitigations

### Issue 1: JDK & Android SDK Local Path Resolution
- **Problem:** `java` is not in the system Windows PATH, though Android Studio's JDK 21 exists at `C:\Program Files\Android\Android Studio\jbr\bin\java.exe`. Also, the Android SDK directory is not yet registered in standard AppData.
- **Severity:** HIGH
- **Why It Matters:** Running `./gradlew` from the command line will fail if `JAVA_HOME` or `ANDROID_HOME` are missing.
- **Recommendation:** In `LOCAL_DEVELOPMENT.md` and project root, provide automated scripts / `local.properties` configuration templates pointing explicitly to `C:\Program Files\Android\Android Studio\jbr` and establishing clear instructions for Android SDK configuration via Android Studio or command-line tools.
- **Plan Change:** Explicitly added to Section 15 and 17 of `PHASE_2_IMPLEMENTATION_PLAN.md`.

### Issue 2: Prevention of Accidental Remote Git Pushes
- **Problem:** User directive explicitly commands: "DO NOT push anything to GitHub. Never push to remote."
- **Severity:** CRITICAL
- **Why It Matters:** An accidental `git push` would violate strict project rules.
- **Recommendation:** Do not configure remote push credentials or run any push command. Keep all commits 100% local.
- **Plan Change:** Highlighted as a hard constraint in all Phase 2 documentation.

### Issue 3: Decoupling Master Plants from Location Instances
- **Problem:** Earlier conceptual designs risked conflating the botanical master description (medicinal claims, Hanzi, Pinyin) with the physical plot instance (planted in RW 03, 10 polybags).
- **Severity:** MEDIUM
- **Why It Matters:** If multiple gardens grow Red Ginger (Jahe Merah), copying botanical descriptions creates data drift and increases maintenance burden.
- **Recommendation:** Formalize `MasterPlant` as the canonical botanical encyclopedia, and `LocationPlant` as the lightweight junction entity referencing `masterPlantId`.
- **Plan Change:** Implemented in Section 12 of `PHASE_2_IMPLEMENTATION_PLAN.md`.

---

## 4. Self-Review Conclusion

The Phase 2 Implementation Plan is technically sound, architecturally minimal, strictly aligned with approved requirements, and ready for Product Owner review.

**Status:** PHASE 2 PLAN READY — WAITING FOR APPROVAL
