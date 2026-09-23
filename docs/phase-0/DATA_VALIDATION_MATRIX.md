# DATA VALIDATION MATRIX — BUBAKAN GREEN

**Date:** 2026-09-23  
**Version:** 2.0 (Corrected)  
**Correction:** v1.0 incorrectly classified all items as NEEDS VALIDATION. v2.0 properly separates DOCUMENT-VERIFIED, USER-PROVIDED, and NEEDS-FIELD-VALIDATION items.

---

## Classification Legend

| Tag | Meaning |
|-----|---------|
| **DOCUMENT-VERIFIED** | Supported by official/project source documents |
| **USER-PROVIDED** | Explicitly provided by the product owner |
| **FIELD-VERIFIED** | Confirmed through real-world field work |
| **NEEDS-FIELD-VALIDATION** | Known requirement, actual value not yet confirmed |
| **UNKNOWN** | Insufficient information exists |

---

## Program & Product Context

| DATA | SOURCE | STATUS | CONFIDENCE | REQUIRES FIELD VALIDATION? | NOTES |
|------|--------|--------|------------|---------------------------|-------|
| Urban Farming is a required program | Proker Kelompok | DOCUMENT-VERIFIED | HIGH | No | Established in program planning |
| Taman Toga is a required program | Proker Kelompok | DOCUMENT-VERIFIED | HIGH | No | Established in program planning |
| Urban Farming centered at Kelurahan | Proker Kelompok | DOCUMENT-VERIFIED | HIGH | GPS/boundaries need field validation | General area known; exact coordinates unknown |
| Taman Toga directed toward RW 03 | Proker Kelompok | DOCUMENT-VERIFIED | HIGH | GPS/boundaries need field validation | RW assignment known; exact coordinates unknown |
| Plant additions are part of Urban Farming | Proker Kelompok | DOCUMENT-VERIFIED | HIGH | No | Activity scope confirmed |
| Barcode/QR is part of program output | Proker Kelompok | DOCUMENT-VERIFIED | HIGH | No | Concept confirmed; implementation details are architectural decisions |
| Buku Saku as Urban Farming output | Proker Kelompok | DOCUMENT-VERIFIED | MEDIUM | No | Confirmed output; relationship to digital system is product decision |
| Digital system (app/web) is proker individu | Proker Individu | DOCUMENT-VERIFIED | HIGH | No | The application itself is the proker individu deliverable |

---

## Product Requirements

| DATA | SOURCE | STATUS | CONFIDENCE | REQUIRES FIELD VALIDATION? | NOTES |
|------|--------|--------|------------|---------------------------|-------|
| Product name: BUBAKAN GREEN | Product Owner | USER-PROVIDED | HIGH | No | Locked |
| Product is Kelurahan Bubakan's, not KKN's | Product Owner | USER-PROVIDED | HIGH | No | Locked |
| Location is a first-class entity | Product Owner | USER-PROVIDED | HIGH | No | Architectural requirement |
| System supports adding new locations | Product Owner | USER-PROVIDED | HIGH | No | Extensibility requirement |
| GPS capture is single-point only | Product Owner | USER-PROVIDED | HIGH | No | Privacy constraint — locked |
| QR uses stable URL reference | Product Owner | USER-PROVIDED | HIGH | No | Architecture constraint — locked |
| QR blocked until real plants validated | Product Owner | USER-PROVIDED | HIGH | No | Process constraint — locked |
| Mandarin interaction is user-triggered | Product Owner | USER-PROVIDED | HIGH | No | UX constraint — locked |
| No autoplay / no looping audio | Product Owner | USER-PROVIDED | HIGH | No | UX constraint — locked |
| Data ≠ APK code | Product Owner | USER-PROVIDED | HIGH | No | Architecture principle — locked |
| Target cost Rp0 | Product Owner | USER-PROVIDED | HIGH | No | Cost constraint — locked |
| No internal QR scanner in app | Product Owner | USER-PROVIDED | HIGH | No | Use camera / Google Lens |

---

## Location Data

| DATA | SOURCE | STATUS | CONFIDENCE | REQUIRES FIELD VALIDATION? | WHO VALIDATES | BLOCKING |
|------|--------|--------|------------|---------------------------|--------------|----------|
| Urban Farming location existence | Proker Kelompok | DOCUMENT-VERIFIED (concept) | HIGH for existence, LOW for details | YES — exact boundaries, condition | KKN team on-site | Blocks production location record |
| Taman Toga RW 03 location existence | Proker Kelompok | DOCUMENT-VERIFIED (concept) | HIGH for existence, LOW for details | YES — exact boundaries, condition | KKN team on-site | Blocks production location record |
| Other existing locations | — | UNKNOWN | NONE | YES — field survey | KKN team + Kelurahan | Not blocking MVP but needed for completeness |
| Location official names | — | NEEDS-FIELD-VALIDATION | NONE | YES | Kelurahan staff | Blocks location creation in system |
| Location GPS coordinates | — | NEEDS-FIELD-VALIDATION | NONE | YES — must be captured on-site | KKN team with device | Blocks map markers for production data |
| Location photos | — | NEEDS-FIELD-VALIDATION | NONE | YES — must be taken on-site | KKN team on-site | Blocks location display |
| Location descriptions | — | NEEDS-FIELD-VALIDATION | NONE | YES | KKN team + Kelurahan | Blocks location content |
| Location RW assignments | — | NEEDS-FIELD-VALIDATION (except RW 03 for Toga) | MEDIUM for Toga | YES for others | Kelurahan staff | — |

---

## Plant Data

| DATA | SOURCE | STATUS | CONFIDENCE | REQUIRES FIELD VALIDATION? | WHO VALIDATES | BLOCKING |
|------|--------|--------|------------|---------------------------|--------------|----------|
| Actual plant inventory (which species) | — | NEEDS-FIELD-VALIDATION | NONE | YES — on-site identification | KKN team + PIC | Blocks all plant records |
| Plants actually planted (vs planned) | — | NEEDS-FIELD-VALIDATION | NONE | YES — on-site verification | KKN team on-site | Blocks QR generation |
| Plant Indonesian names | — | NEEDS-FIELD-VALIDATION | NONE | YES | KKN team + PIC | Blocks plant records |
| Plant Latin/scientific names | — | NEEDS-FIELD-VALIDATION | NONE | YES — research required | KKN team (botany) | Not blocking but important |
| Plant Mandarin names (漢字) | — | NEEDS-FIELD-VALIDATION | NONE | YES — linguistic verification | KKN Mandarin speakers | Blocks Mandarin display for that plant |
| Plant Pinyin | — | NEEDS-FIELD-VALIDATION | NONE | YES | KKN Mandarin speakers | Blocks Mandarin display for that plant |
| Plant descriptions/benefits | — | NEEDS-FIELD-VALIDATION | NONE | YES — must be factual | KKN team (research) | Blocks plant content |
| Plant photos | — | NEEDS-FIELD-VALIDATION | NONE | YES — of actual plants | KKN team on-site | Blocks plant display |
| Plants selected for QR | — | NEEDS-FIELD-VALIDATION | NONE | YES — depends on inventory | KKN team + PIC | **HARD BLOCKER for QR generation** |

---

## Personnel Data

| DATA | SOURCE | STATUS | CONFIDENCE | REQUIRES FIELD VALIDATION? | WHO VALIDATES | BLOCKING |
|------|--------|--------|------------|---------------------------|--------------|----------|
| PIC identities | — | NEEDS-FIELD-VALIDATION | NONE | YES | Kelurahan staff | Blocks PIC account creation |
| PIC contact/email | — | NEEDS-FIELD-VALIDATION | NONE | YES | PICs themselves | Blocks authentication setup |
| PIC ↔ location assignments | — | NEEDS-FIELD-VALIDATION | NONE | YES | Kelurahan staff | Blocks authorization model |
| Admin identity | — | NEEDS-FIELD-VALIDATION | NONE | YES | Kelurahan staff | Blocks admin account |
| Firebase project owner (post-KKN) | — | UNKNOWN | NONE | YES | Kelurahan leadership | Blocks handover (Phase 9) |

---

## Audio Data

| DATA | SOURCE | STATUS | CONFIDENCE | REQUIRES FIELD VALIDATION? | WHO VALIDATES | BLOCKING |
|------|--------|--------|------------|---------------------------|--------------|----------|
| Audio production method (TTS vs human) | — | UNKNOWN — DEFERRED | NONE | Deferred until plant list finalized | Product Owner | Blocks audio production, NOT audio playback architecture |
| Actual audio files | — | NEEDS-FIELD-VALIDATION | NONE | YES — depends on plant list + method | KKN team | Blocks Mandarin audio feature content |

---

## Technical Environment

| DATA | SOURCE | STATUS | CONFIDENCE | REQUIRES FIELD VALIDATION? | WHO VALIDATES | BLOCKING |
|------|--------|--------|------------|---------------------------|--------------|----------|
| Target users' Android versions | — | NEEDS-FIELD-VALIDATION | NONE | YES — device survey | KKN team | Informs minimum API level (default: API 26) |
| Internet connectivity at locations | — | NEEDS-FIELD-VALIDATION | NONE | YES — on-site test | KKN team | Informs offline strategy priority |
| Target users' sideloading comfort | — | NEEDS-FIELD-VALIDATION | NONE | YES — user survey | KKN team | Informs distribution strategy |

---

## Summary

| Classification | Count |
|----------------|-------|
| DOCUMENT-VERIFIED | 8 items (program context, initial locations, activities) |
| USER-PROVIDED | 12 items (product requirements, constraints) |
| FIELD-VERIFIED | 0 items (no field work completed yet) |
| NEEDS-FIELD-VALIDATION | 20+ items (real-world data points) |
| UNKNOWN | 3+ items (post-KKN ownership, audio method, additional locations) |

> [!IMPORTANT]  
> The system architecture and app features can be designed and built based on DOCUMENT-VERIFIED and USER-PROVIDED items. NEEDS-FIELD-VALIDATION items block **production data population**, not application development. The exception is the map provider decision which requires a technical evaluation (see ADR-004).
