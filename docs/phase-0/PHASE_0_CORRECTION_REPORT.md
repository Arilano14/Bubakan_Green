# PHASE 0 CORRECTION REPORT — BUBAKAN GREEN

**Date:** 2026-09-23  
**Triggered by:** Product Owner review of v1.0 Phase 0 deliverables  
**Status:** Corrections applied. Awaiting re-approval.

---

## Summary

The Product Owner reviewed the v1.0 Phase 0 plan and identified **7 material issues** that needed correction before approval. The plan structure (inspect → plan → self-review → stop → wait) was confirmed correct. The issues were about **content accuracy and classification**, not structural failures.

---

## What Was Wrong

### Issue 1: "Zero verified data" classification

**PROBLEM:** v1.0 Data Validation Matrix classified ALL 25+ items as `NEEDS VALIDATION`, including product requirements that are established from project documents.

**WHY IT MATTERS:** If the AI treats all requirements as unverified, it loses the established product context. The system knows that Urban Farming is a required program centered at the Kelurahan, and Taman Toga is directed at RW 03. These are DOCUMENT-VERIFIED facts from Proker Kelompok/Individu, not hypotheses waiting for field confirmation.

**WHAT WAS CHANGED:**
- Created 5-tier classification: DOCUMENT-VERIFIED, USER-PROVIDED, FIELD-VERIFIED, NEEDS-FIELD-VALIDATION, UNKNOWN
- Reclassified 8 items as DOCUMENT-VERIFIED (program context from Proker docs)
- Reclassified 12 items as USER-PROVIDED (product requirements from owner)
- Kept 20+ items as NEEDS-FIELD-VALIDATION (actual field data)
- Added BLOCKING status to each validation item

**WHAT REMAINS UNKNOWN:** Actual plant inventory, GPS coordinates, PIC identities, photos — these correctly remain NEEDS-FIELD-VALIDATION.

---

### Issue 2: No source-of-truth baseline

**PROBLEM:** v1.0 was created without access to project source documents and did not establish a product context baseline.

**WHY IT MATTERS:** Without a baseline, the plan cannot distinguish between what is established program knowledge and what needs discovery.

**WHAT WAS CHANGED:**
- Created `docs/source/PROJECT_CONTEXT.md` — the source-of-truth baseline
- Documents established facts: programs (Proker Kelompok), locations (Urban Farming at Kelurahan, Taman Toga at RW 03), activities (plant additions, barcode concept, buku saku), individual proker (app/website)
- Explicitly separates known facts from field-validation items

---

### Issue 3: Map decision was premature

**PROBLEM:** ADR-004 chose osmdroid/OpenStreetMap primarily because it's free, without properly analyzing MAP SDK vs MAP DATA vs MAP TILE PROVIDER.

**WHY IT MATTERS:** A premature decision based on a single criterion (cost) may miss important tradeoffs in integration quality, maintenance, and UX.

**WHAT WAS CHANGED:**
- ADR-004 rewritten with proper conceptual separation (SDK/DATA/TILES)
- 3 options analyzed against 8 criteria each (cost, API key, billing, usage limits, offline, Compose integration, licensing, maintenance)
- Decision deferred to Product Owner with analysis framework provided
- No premature winner declared

**WHAT REMAINS:** Product Owner must decide. The key question: is a Google Cloud billing account acceptable?

---

### Issue 4: Mandarin audio production decided too early

**PROBLEM:** v1.0 recommended pre-recorded TTS as the audio source, but this decision depends on which plants are in the final inventory.

**WHY IT MATTERS:** Producing audio for hypothetical plants is waste. The audio production method should be decided after the plant list is finalized.

**WHAT WAS CHANGED:**
- Mandarin interaction model LOCKED (user-triggered, play once, no autoplay)
- Audio production method explicitly DEFERRED until plant list is validated
- Decision timeline documented: Phase 0 (interaction) → Field Inventory (plants) → Mandarin Content Phase (audio method + production)

---

### Issue 5: Scope inflation (22 MUST HAVE mixing capabilities with principles)

**PROBLEM:** v1.0 had 22 "MUST HAVE" items, but many were architectural principles (e.g., "data ≠ code", "Rp0 cost", "single-point GPS") rather than system capabilities.

**WHY IT MATTERS:** Counting constraints as features inflates perceived scope. The actual system capabilities for MVP should be ~12, not 22.

**WHAT WAS CHANGED:**
- SCOPE_CONTROL.md restructured: 10 ARCHITECTURAL PRINCIPLES + 12 MVP CORE CAPABILITIES
- Principles (non-negotiable design rules) separated from capabilities (things users do)
- Capabilities tightened to: browse locations, browse plants, plant detail, map, PIC auth, plant CRUD, location CRUD, GPS capture, QR/App Link, web fallback, Mandarin speaker, basic offline cache

---

### Issue 6: QR field validation gate missing as hard dependency

**PROBLEM:** v1.0 mentioned field validation as a note, not as a blocking dependency for QR generation.

**WHY IT MATTERS:** Without a hard gate, there's risk of generating QR codes for unverified or hypothetical plants, violating the product requirement.

**WHAT WAS CHANGED:**
- Added FIELD VALIDATION GATE to QR Model (Section 12) with dependency chain diagram
- Added Field Validation Gate section to SCOPE_CONTROL.md with hard blocker table
- Added Field Validation Gate section to PHASE_ROADMAP.md with full flow
- Statement: "NO FIELD VALIDATION → NO FINAL QR"
- Added `featured: boolean` to Plant model (only featured plants get QR)

---

### Issue 7: Location and Plant models missing key fields

**PROBLEM:** Location model lacked `coordinatesStatus` and `featured`. Plant model lacked `featured`.

**WHY IT MATTERS:**
- `coordinatesStatus` (PENDING/VERIFIED) ensures GPS coordinates go through a verification flow (PIC captures → admin verifies)
- `featured` on Location distinguishes initial/highlighted locations from future additions
- `featured` on Plant marks QR-eligible plants, keeping QR scope controlled

**WHAT WAS CHANGED:**
- Location model: added `coordinatesStatus: enum [PENDING, VERIFIED]` and `featured: boolean`
- Plant model: added `featured: boolean`
- Documentation updated to explain the purpose of each field

---

## What Was Not Changed

| Item | Why Unchanged |
|------|---------------|
| Repository audit | Correctly identified greenfield project |
| Gate mechanism (inspect → plan → review → stop) | Working correctly — confirmed by Product Owner |
| Firebase as backend | Correct recommendation |
| Kotlin + Jetpack Compose | Correct recommendation |
| QR as URL reference (not entity) | Correct design |
| App Links approach | Correct design |
| User roles (Public/PIC/Admin) | Correct definition |
| Permission model | Correct design |
| Cost strategy (Rp0) | Correct constraint |

---

## What Remains Blocked Until Field Validation

| Item | Blocking |
|------|----------|
| Production location records | GPS coordinates must be captured on-site |
| Production plant records | Plants must be physically identified and inventoried |
| QR code generation | Depends on validated + featured plants |
| QR label printing | Depends on QR generation |
| Physical QR placement | Depends on printed labels |
| Mandarin audio production | Depends on finalized plant list |
| Mandarin names / Pinyin | Depends on finalized plant list |
| PIC account creation | Depends on Kelurahan confirming PIC identities |

---

## Correction Impact

| Document | Change | Size Impact |
|----------|--------|-------------|
| `docs/source/PROJECT_CONTEXT.md` | NEW | +4 KB |
| `DATA_VALIDATION_MATRIX.md` | REWRITTEN | ~8 KB (was 6 KB) |
| `SCOPE_CONTROL.md` | RESTRUCTURED | ~7 KB (similar) |
| `ARCHITECTURE_DECISIONS.md` | ADR-004 REVISED | ~18 KB (was 15 KB) |
| `PHASE_0_IMPLEMENTATION_PLAN.md` | 8 TARGETED EDITS | ~50 KB (was 45 KB) |
| `PHASE_ROADMAP.md` | FIELD GATE ADDED | ~15 KB (was 14 KB) |
| `PHASE_0_SELF_REVIEW.md` | REWRITTEN | ~8 KB (was 14 KB — reduced) |
| `PHASE_0_CORRECTION_REPORT.md` | NEW | +5 KB |

**Net result:** Plans are more precise, not just bigger. Open questions reduced from 10 to 5. MUST HAVE reduced from 22 to 12 capabilities.
