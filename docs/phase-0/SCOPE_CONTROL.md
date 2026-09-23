# SCOPE CONTROL — BUBAKAN GREEN

**Date:** 2026-09-23  
**Version:** 2.0 (Corrected)  
**Correction:** v1.0 had 22 MUST HAVE items mixing system capabilities with architectural principles. v2.0 separates them and tightens MVP to 12 core capabilities.

---

## ARCHITECTURAL PRINCIPLES (Non-negotiable design rules)

These are NOT features. They are constraints that every feature must respect.

| # | Principle | Source |
|---|-----------|--------|
| P1 | Location is a first-class entity (not a field on Plant) | [USER-PROVIDED] |
| P2 | Data changes do NOT require APK rebuild | [USER-PROVIDED] |
| P3 | New locations can be added (not hard-coded) | [USER-PROVIDED] |
| P4 | GPS capture is single-point only (no tracking) | [USER-PROVIDED] |
| P5 | QR uses stable URL reference (data changes don't require reprint) | [USER-PROVIDED] |
| P6 | QR generation blocked until real plants are field-validated | [USER-PROVIDED] |
| P7 | Mandarin audio is user-triggered only (no autoplay, no looping) | [USER-PROVIDED] |
| P8 | Target infrastructure cost Rp0 within free tiers | [USER-PROVIDED] |
| P9 | Product identity is Kelurahan Bubakan (not KKN) | [USER-PROVIDED] |
| P10 | PIC can only manage locations they are authorized to manage | [USER-PROVIDED] |

---

## MVP CORE CAPABILITIES (12 — Non-negotiable)

These are the system capabilities required for the product to deliver value.

| # | Capability | Description | Source |
|---|-----------|-------------|--------|
| C1 | Browse locations | Public list of Urban Farming and Taman Toga locations | Core value |
| C2 | Browse plants | Public list of plants within a location | Core value |
| C3 | Plant detail | Indonesian name, Latin name, Mandarin + Pinyin (when available), description, photo | Core value |
| C4 | Map view | Locations displayed as pins on a map | Core value |
| C5 | PIC authentication | PIC login (email/password) with per-location authorization | Required for management |
| C6 | Plant CRUD | PIC can add, edit, archive plants within their assigned locations (including photo upload) | [USER-PROVIDED] |
| C7 | Location CRUD | PIC can add, edit locations (including photo upload) | [USER-PROVIDED] |
| C8 | GPS capture | PIC captures single-point GPS when registering or editing a location | [USER-PROVIDED] |
| C9 | QR / App Link | QR code generation for validated plants; App Link handling for deep navigation | [USER-PROVIDED] |
| C10 | Web fallback | Static web page for QR users without the app (plant info + APK download link) | [USER-PROVIDED] |
| C11 | Mandarin speaker | User-triggered 🔊 audio playback on plant detail (when audio file exists) | [USER-PROVIDED] |
| C12 | Basic offline cache | Firestore built-in offline persistence for previously loaded data | Usability |

---

## SHOULD HAVE (High value — include if feasible)

| # | Feature | Justification | Risk if Deferred |
|---|---------|---------------|-----------------|
| S1 | Admin: approve new locations (status workflow) | Data quality control | Low — can be done via Firebase Console initially |
| S2 | Admin: manage PIC accounts (via Firebase Console + guide) | Operational necessity | Low — developer does it initially |
| S3 | Location type filter on browse list | Usability for distinguishing Urban Farming vs Taman Toga | Low — small list |
| S4 | Image compression before upload | Storage savings (5GB free tier) | Medium — uncompressed photos eat storage fast |
| S5 | Forgot password flow | PICs will forget passwords | Low — Firebase Auth built-in, trivial |
| S6 | Soft delete (archive) instead of hard delete | Data safety | Medium — accidental deletion is irreversible |
| S7 | Featured / regular distinction for locations and plants | Scope control for QR (only featured plants get QR) | Low — but improves data management |

---

## COULD HAVE (Nice-to-have — only if time permits)

| # | Feature | Justification | Complexity |
|---|---------|---------------|------------|
| C1 | Search/filter plants by name | Usability for large inventories | LOW |
| C2 | Location directions link (open in external Maps app) | Convenience | LOW |
| C3 | Multiple photos per location/plant (gallery) | Richer content | MEDIUM |
| C4 | QR generation for locations (not just plants) | Allows location-level QR | LOW |
| C5 | Batch QR generation | Efficiency for PICs with many plants | MEDIUM |
| C6 | QR label template (printable PDF) | Convenience for physical deployment | MEDIUM |
| C7 | Pull-to-refresh | UX polish | LOW |
| C8 | Loading states / shimmer UI | UX polish | LOW |

---

## DO NOT BUILD (Prohibited unless explicitly approved)

| # | Feature | Why NOT | Source |
|---|---------|---------|-------|
| D1 | Internal QR scanner in the app | Camera / Google Lens handles QR | [USER-PROVIDED] |
| D2 | Background GPS tracking | Privacy violation, not needed | [USER-PROVIDED] |
| D3 | Continuous GPS monitoring / geofencing | Not needed | [USER-PROVIDED] |
| D4 | Location history / route navigation | Not needed | [USER-PROVIDED] |
| D5 | Autoplay / looping / live AI audio | Must be user-triggered, pre-recorded | [USER-PROVIDED] |
| D6 | Separate web admin dashboard | Admin uses the app | Anti-slop |
| D7 | Full CMS | PIC uses app forms | Anti-slop |
| D8 | Microservices architecture | Firebase is sufficient | Anti-slop |
| D9 | AI plant identification | Out of scope | — |
| D10 | E-commerce / plant sales | Out of scope | — |
| D11 | Social media / community forum | Out of scope | — |
| D12 | IoT / sensor integration | Out of scope | — |
| D13 | Gamification | Out of scope | — |
| D14 | Weather integration | Out of scope | — |
| D15 | Push notifications (MVP) | No validated need | Deferred |
| D16 | Analytics dashboard | Not MVP | Deferred |
| D17 | User registration for public users | Public access is anonymous | — |
| D18 | Excessive animations | Anti-slop | — |
| D19 | KKN/university branding in app | Product is Kelurahan's | [USER-PROVIDED] |
| D20 | QR generation before field inventory | Real plants must be validated first | [USER-PROVIDED] |
| D21 | Mandarin audio files before plant list finalized | Audio production depends on finalized plants | [USER-PROVIDED] |

---

## Field Validation Gate (HARD DEPENDENCY)

> [!CAUTION]  
> The following capabilities are **code-complete possible** but **data-blocked** until field validation occurs.

| Capability | Blocked Until | Hard Blocker? |
|-----------|--------------|---------------|
| QR code generation for production use | Actual plants inventoried + validated + selected | **YES** |
| QR label printing | QR generation unblocked | **YES** |
| Mandarin audio content | Plant list finalized + audio produced | **YES** (for audio; text display is not blocked) |
| Production location records | GPS captured on-site | **YES** |
| Production plant records | On-site plant identification | **YES** |

```
ARCHITECTURE DEVELOPMENT (not blocked)
       ↓
FIELD VALIDATION (blocks production data)
       ↓
DATA POPULATION (blocks QR)
       ↓
QR GENERATION (blocks physical labels)
       ↓
FIELD DEPLOYMENT
```

**NO FIELD VALIDATION → NO FINAL QR**

This is a hard dependency, not a suggestion.

---

## Scope Change Process

Any request to move an item from DO NOT BUILD or COULD HAVE into MVP or SHOULD HAVE requires:

1. Written justification (why it's now needed)
2. Impact assessment (time, complexity, cost)
3. Explicit Product Owner approval

No silent scope expansion is permitted.
