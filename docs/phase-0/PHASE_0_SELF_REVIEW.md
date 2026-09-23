# PHASE 0 SELF-REVIEW — BUBAKAN GREEN

**Date:** 2026-09-23  
**Version:** 2.0 (Post-Correction)  
**Reviewer Role:** Skeptical Senior Engineer  
**Documents Reviewed (v2.0):**
- `docs/source/PROJECT_CONTEXT.md` (NEW)
- `REPOSITORY_AUDIT.md` (unchanged)
- `PHASE_0_IMPLEMENTATION_PLAN.md` (revised)
- `DATA_VALIDATION_MATRIX.md` (rewritten)
- `SCOPE_CONTROL.md` (restructured)
- `ARCHITECTURE_DECISIONS.md` (ADR-004 revised)
- `PHASE_ROADMAP.md` (field validation gate added)
- `PHASE_0_CORRECTION_REPORT.md` (NEW)

---

## Checklist Review

### Data Integrity

- [x] **Any invented data?**  
  **PASS.** No plant names, GPS coordinates, PIC identities, or location details are fabricated. DOCUMENT-VERIFIED items cite Proker Kelompok/Individu as source. NEEDS-FIELD-VALIDATION items are explicitly marked with WHO validates and WHAT is blocking.

- [x] **Any unsupported assumptions?**  
  **PASS.** All assumptions are labeled `[ASSUMPTION]`. Verified requirements now properly classified as `[DOCUMENT-VERIFIED]` or `[USER-PROVIDED]` instead of being lumped into "NEEDS VALIDATION."

### Source of Truth Correction (v2.0 specific)

- [x] **Are DOCUMENT-VERIFIED items actually supported by documents?**  
  **PASS.** Items marked DOCUMENT-VERIFIED trace to Proker Kelompok (Urban Farming & Taman Toga as required programs, centered at Kelurahan/RW 03, barcode concept, buku saku output) and Proker Individu (app/website as individual proker). These are established program facts, not field observations.

- [x] **Are NEEDS-FIELD-VALIDATION items correctly separated?**  
  **PASS.** Field data (GPS, plant inventory, photos, PIC identities, quantities, Mandarin names) remain NEEDS-FIELD-VALIDATION. The correction properly distinguishes "we know the program exists" from "we haven't measured the coordinates."

### Feature Scope

- [x] **Any unnecessary feature?**  
  **PASS.** MVP tightened to 12 core capabilities (from 22 items that mixed capabilities with principles). Architectural principles separated into their own section.

- [x] **Are the 12 MVP capabilities correct?**  
  **PASS.** Browse locations, browse plants, plant detail, map, PIC auth, plant CRUD, location CRUD, GPS capture, QR/App Link, web fallback, Mandarin speaker, basic offline cache. Each directly maps to a user-provided requirement or core product value.

- [x] **Any scope creep disguised as scope control?**  
  **PASS.** Reduced from 22 "MUST HAVE" items to 12 capabilities + 10 principles. The previous version inflated scope by counting architectural constraints as features.

### Models

- [x] **Does Location model support GPS from day 1?**  
  **PASS.** `latitude`, `longitude`, and `coordinatesStatus` (PENDING/VERIFIED) are in the model. This ensures GPS is a first-class concern, not a Phase 5 afterthought.

- [x] **Does Location model support featured concept?**  
  **PASS.** `featured: boolean` added. Initial locations (Urban Farming Kelurahan, Taman Toga RW 03) can be marked featured for priority display.

- [x] **Does Plant model support featured/QR distinction?**  
  **PASS.** `featured: boolean` added. Only featured plants should have QR generated. This keeps QR scope controlled and prevents premature QR generation for every catalog entry.

### QR / Field Validation Gate

- [x] **Is QR generation properly gated behind field validation?**  
  **PASS.** The QR model now includes a FIELD VALIDATION GATE section with a hard dependency chain: FIELD INVENTORY → VERIFY → SELECT FEATURED → STABLE IDs → GENERATE QR → PRINT → FIELD TEST. Stated as "NO FIELD VALIDATION → NO FINAL QR."

- [x] **Is this gate a hard dependency, not just a note?**  
  **PASS.** Gate appears in three places: QR Model (Section 12), SCOPE_CONTROL.md (Field Validation Gate section), and PHASE_ROADMAP.md (Field Validation Gate section). It's structural, not advisory.

### Mandarin Audio

- [x] **Is Mandarin interaction model locked?**  
  **PASS.** Section 14 explicitly states "This interaction model is LOCKED" with [USER-PROVIDED] tag. No autoplay, no looping, tap → play once → stop.

- [x] **Is audio production method correctly deferred?**  
  **PASS.** Audio production method (TTS vs human) is explicitly deferred until plant list is finalized. Decision timeline documented. What CAN be built now (playback architecture) is separated from what CANNOT (actual audio files).

### Maps Decision

- [x] **Is the Maps decision properly analyzed, not prematurely chosen?**  
  **PASS.** ADR-004 v2.0 separates MAP SDK, MAP DATA, and MAP TILE PROVIDER concepts. Evaluates Google Maps, osmdroid/OSM, and Mapbox against 8 criteria (cost, API key, billing, usage limits, offline, Compose integration, licensing, maintenance). Decision deferred to Product Owner with analysis framework.

### Architecture

- [x] **Does the architecture separate data from APK code?**  
  **PASS.** Data in Firestore, code in APK. No change from v1.0.

- [x] **Can new locations be added?**  
  **PASS.** Location is Firestore collection, not hard-coded.

- [x] **Can new plants be added?**  
  **PASS.** Plant is Firestore collection linked by locationId.

- [x] **Does QR use stable IDs?**  
  **PASS.** QR encodes URL with auto-generated Firestore document ID.

- [x] **Does QR handle both app-installed and not-installed cases?**  
  **PASS.** App Links + web fallback.

- [x] **Does GPS only capture (not track)?**  
  **PASS.** Single-point capture. DO NOT BUILD list includes background tracking, continuous monitoring, geofencing.

### Cost

- [x] **Any unnecessary cost?**  
  **PASS.** All components within free tiers. Map decision pending but both viable options are Rp0 at expected volume.

### Sustainability

- [x] **Can PIC operate without developer?**  
  **PASS for data operations.** PIC can CRUD locations/plants, upload photos, capture GPS, generate QR through the app.

- [x] **Can the project survive post-KKN?**  
  **CONDITIONAL PASS.** Requires Firebase project ownership transfer (Open Question #2 — blocking Phase 9).

---

## Issues Found in v2.0

### No new plan-breaking issues found.

The v1.0 issues identified by the Product Owner have been corrected:

| v1.0 Issue | v2.0 Status |
|-----------|-------------|
| "Zero verified data" — all items lumped as NEEDS VALIDATION | ✅ FIXED — 8 DOCUMENT-VERIFIED, 12 USER-PROVIDED, 20+ NEEDS-FIELD-VALIDATION |
| Source of truth missing | ✅ FIXED — PROJECT_CONTEXT.md created |
| Map decision premature | ✅ FIXED — ADR-004 deferred with full analysis |
| Mandarin audio method decided too early | ✅ FIXED — interaction locked, production method deferred |
| 22 MUST HAVE items mixed with principles | ✅ FIXED — 12 capabilities + 10 principles |
| QR field validation gate missing | ✅ FIXED — hard dependency in QR model, scope control, and roadmap |
| GPS coordinatesStatus missing from model | ✅ FIXED — added PENDING/VERIFIED enum |
| Featured concept missing | ✅ FIXED — featured flag on both Location and Plant |
| Open questions not actionable | ✅ FIXED — reduced to 5, each with WHY/BLOCKING/DEFAULT |

---

## Summary

| Check | Result |
|-------|--------|
| Invented data | ✅ NONE |
| Source of truth classification | ✅ CORRECTED |
| Unnecessary features | ✅ NONE (tightened to 12) |
| Unnecessary technology | ✅ PASS |
| Duplicate functionality | ✅ NONE |
| Unnecessary cost | ✅ Rp0 |
| Security holes | ⚠️ LOW — custom claims delay (documented, same as v1.0) |
| Privacy concerns | ✅ NONE |
| Missing user flows | ✅ Password reset added to SHOULD HAVE |
| Field validation gate | ✅ HARD DEPENDENCY — documented in 3 places |
| Maps decision | ✅ PROPERLY DEFERRED with analysis |
| Mandarin interaction locked | ✅ LOCKED |
| Mandarin audio production deferred | ✅ DEFERRED correctly |
| Featured concept | ✅ ADDED to both models |
| GPS coordinates in model | ✅ WITH STATUS TRACKING |
| Post-KKN maintainability | ✅ CONDITIONAL (ownership transfer needed) |

**Overall assessment: CORRECTED PLAN IS SOUND.** All Product Owner feedback has been addressed. No plan-breaking issues remain.
