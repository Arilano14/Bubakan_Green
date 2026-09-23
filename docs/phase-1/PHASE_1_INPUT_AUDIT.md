# PHASE 1 INPUT AUDIT — BUBAKAN GREEN

**Sistem Informasi Urban Farming & Taman Toga**  
**Kelurahan Bubakan, Kecamatan Mijen, Kota Semarang**  
**Document:** `docs/phase-1/PHASE_1_INPUT_AUDIT.md`  
**Date:** 2026-09-23  
**Status:** COMPLETE — PHASE 1 INPUT BASELINE  

---

## 1. Executive Summary

This input audit examines all approved Phase 0 deliverables (`PROJECT_CONTEXT.md`, `PHASE_0_IMPLEMENTATION_PLAN.md`, `SCOPE_CONTROL.md`, `ARCHITECTURE_DECISIONS.md`, `DATA_VALIDATION_MATRIX.md`, `PHASE_0_CORRECTION_REPORT.md`, `PHASE_ROADMAP.md`) to establish the authoritative constraints, verified inputs, and field boundaries governing the Information Architecture (IA) and UX/UI specifications of **BUBAKAN GREEN**.

Every UX decision in Phase 1 is anchored directly to an approved Phase 0 requirement or constraint. No synthetic requirements, no feature creep, and no unverified field data are permitted to leak into the UI as factual content.

---

## 2. Classification Legend

| Classification Tag | Definition | Phase 1 UX Rule |
|:---|:---|:---|
| **`[LOCKED]`** | Non-negotiable architectural or product constraints approved in Phase 0 | UX must strictly implement this pattern. No alternatives considered. |
| **`[APPROVED]`** | Approved Phase 0 product requirements & functional capabilities | Forms the baseline scope of screens, flows, and states. |
| **`[FIELD DATA REQUIRED]`** | Legitimate data point whose schema is approved, but real-world value is pending on-site verification | UI must use neutral content templates, empty states, or badges ("Belum divalidasi"). NEVER display synthetic facts. |
| **`[UNKNOWN]`** | Open governance or technical parameter pending explicit resolution | UX must adopt safe defaults that do not block progress or trap future maintainers. |
| **`[PROVISIONAL UX]`** | UX pattern designed to accommodate pending decisions (e.g. map provider fallback, TTS vs human audio) | Modular component design that functions cleanly regardless of technical resolution. |

---

## 3. Audit of Phase 0 Requirements Used

| # | Requirement / Capability | Phase 0 Source | Status | UX & Design Implication |
|:---|:---|:---|:---|:---|
| 1 | **Product Identity: BUBAKAN GREEN** | `PROJECT_CONTEXT.md` § Product Identity | `[LOCKED]` | All app headers, typography, welcome cards, and logos read "BUBAKAN GREEN — Sistem Informasi Urban Farming & Taman Toga Kelurahan Bubakan". No KKN/student branding anywhere in user-facing UI. |
| 2 | **Location as First-Class Entity** | `SCOPE_CONTROL.md` § P1, `PHASE_0_IP` § 10 | `[LOCKED]` | Architecture is strictly: Location → Plants in Location → Plant Detail. Location is never a simple dropdown or flat tag on a plant card. |
| 3 | **Anonymous Public Access (No Login)** | `SCOPE_CONTROL.md` § D17, `PHASE_0_IP` § 7 | `[LOCKED]` | Home, Browse Locations, Map, Location Detail, Plant Detail, and Mandarin audio are accessible without login or registration barriers. |
| 4 | **Role-Based Auth (PIC & Admin only)** | `SCOPE_CONTROL.md` § C5, `PHASE_0_IP` § 7 | `[LOCKED]` | Single discreet entry point for management ("Login Petugas/PIC"). PIC sees only their assigned locations; Admin sees Kelurahan-wide locations and approval queues. |
| 5 | **Featured vs. Regular Hierarchy** | `SCOPE_CONTROL.md` § S7, `PHASE_0_CORRECTION` § 7 | `[APPROVED]` | Initial featured locations (Urban Farming Kelurahan & Taman Toga RW 03) receive highlighted positioning on Home, but share identical underlying UI components with regular locations. |
| 6 | **Contextual Single-Point GPS Capture** | `SCOPE_CONTROL.md` § P4, C8; `PHASE_0_IP` § 13 | `[LOCKED]` | GPS permission requested ONLY when PIC taps "Ambil Titik Lokasi Ini" in Location Form. Single-shot capture with accuracy circle and manual coordinate override. No live/background tracking. |
| 7 | **External QR Scanning via Camera/Lens** | `SCOPE_CONTROL.md` § D1, C9; `PHASE_0_IP` § 12 | `[LOCKED]` | App does NOT include an in-app camera/QR scanner. Public scans via system camera / Google Lens. QR opens App Link directly into the app or falls back to Web. |
| 8 | **Deterministic Stable QR URLs** | `SCOPE_CONTROL.md` § P5, `ARCHITECTURE_DECISIONS` § ADR-005 | `[LOCKED]` | QR encodes `https://{domain}/plant/{plantId}` or `https://{domain}/location/{locationId}`. Changing plant content never requires reprinting QR codes. |
| 9 | **QR Field Validation Gate** | `SCOPE_CONTROL.md` § P6, `PHASE_0_CORRECTION` § 6 | `[LOCKED]` | QR generation UI is restricted to validated, `featured: true` plants. Production QR printing cannot be triggered for placeholder or draft records. |
| 10 | **User-Triggered Mandarin Audio** | `SCOPE_CONTROL.md` § P7, C11; `PHASE_0_IP` § 14 | `[LOCKED]` | Tapping `[ 🔊 ]` plays pre-recorded audio once and stops. Strict prohibitions: NO autoplay on page open, NO looping, NO automatic whole-page narration, NO live AI voice synthesis. |
| 11 | **Lightweight Web Fallback** | `SCOPE_CONTROL.md` § C10, `ARCHITECTURE_DECISIONS` § ADR-007 | `[APPROVED]` | Web fallback (`/plant/{plantId}`) is a simple, static responsive card showing essential plant identity, photo, and a prominent "Download Aplikasi BUBAKAN GREEN" CTA. It is NOT a web app clone. |
| 12 | **Offline-Aware UI & Graceful Degrade** | `SCOPE_CONTROL.md` § C12, `ARCHITECTURE_DECISIONS` § ADR-002 | `[APPROVED]` | Leverages Firestore offline cache. UI displays non-intrusive status pills: "Menampilkan data tersimpan" when offline. No complex sync manager. |
| 13 | **Single APK for All Roles** | `PHASE_0_IP` § 7, `SCOPE_CONTROL.md` § D6 | `[LOCKED]` | No separate admin app or web portal. PIC and Admin log into the same mobile application; interface adapts dynamically based on authenticated role custom claims. |

---

## 4. Locked Architectural Decisions (ADR) Affecting Phase 1 UX

| ADR # | Decision | Scope / UX Impact | Classification |
|:---|:---|:---|:---|
| **ADR-001** | **Kotlin + Jetpack Compose** | Native Android UI ergonomics, standard Material 3 design tokens, predictable state hoists (`State<T>`), high-performance lazy lists, fluid micro-interactions. | `[LOCKED]` |
| **ADR-002** | **Firebase (Auth, Firestore, Storage, Hosting)** | Auth via Email/Password; image loading via Glide/Coil from Firebase Storage URLs; static web hosted on Firebase Hosting domain. | `[LOCKED]` |
| **ADR-003** | **Cloud Firestore NoSQL** | Denormalized reads for fast lists; reactive query streams (`Flow`); subcollections or parent-reference queries (`whereEqualTo("locationId", id)`). | `[LOCKED]` |
| **ADR-004** | **Map Provider Decision** | Google Maps Compose vs. osmdroid `AndroidView`. Map UX specification must define a clean, provider-agnostic container interface (pins, tap info window, type filtering) that functions identically regardless of underlying engine. | `[PROVISIONAL UX]` |
| **ADR-005** | **QR as URL Reference** | QR is derived deterministically (`https://{domain}/plant/{id}`); PIC download/share card displays generated QR bitmap preview with standard print-ready layout. | `[LOCKED]` |
| **ADR-006** | **Android App Links** | Handling URL intents for `/plant/{id}` and `/location/{id}`. Deep link parser routes directly to `PlantDetailScreen` or `LocationDetailScreen` with back stack falling back to Home. | `[LOCKED]` |
| **ADR-007** | **Static HTML Web Fallback** | Mobile-first static HTML/CSS/Vanilla JS page on Firebase Hosting. Simple card layout, zero build framework, minimal bundle (<50KB). | `[LOCKED]` |
| **ADR-008** | **Firebase Auth + Custom Claims** | Role badge in top bar (`PIC` / `Admin`); role-conditional UI blocks (e.g. "Kelola Tanaman" action button visible only to assigned PIC / Admin). | `[LOCKED]` |
| **ADR-009** | **APK Distribution via Google Drive / Web Link** | Web fallback and web landing page prominently feature a direct "Unduh APK (Android)" button with simple step-by-step sideloading guidance. | `[LOCKED]` |
| **ADR-010** | **Pre-Recorded Static Audio** | Audio player in Compose requires only a minimal media controller (`play`, `pause`, `loading`, `error`, `idle`). If `mandarinAudioUrl` is null/empty, speaker icon gracefully hides. | `[LOCKED]` |
| **ADR-013** | **Min API Level 26 (Android 8.0)** | Minimum target encompasses ~95%+ of active devices. Modern typography, standard vector drawables, accessible font scaling without legacy compat hacks. | `[LOCKED]` |

---

## 5. Unresolved Questions & UX Handling

| # | Unresolved Question | Phase 0 Status | Phase 1 UX Handling | Classification |
|:---|:---|:---|:---|:---|
| 1 | **Map Provider Selection** (ADR-004: Google Maps vs. osmdroid) | Deferred to Product Owner | Design a provider-agnostic Map screen specification. Layout, map header, category toggle chips, bottom preview card, and pin interactions will remain 100% identical regardless of whether Google Maps or osmdroid is compiled. | `[PROVISIONAL UX]` |
| 2 | **Post-KKN Firebase Owner** | Open governance question | No impact on user-facing mobile UX. Managed entirely at deployment & infrastructure handover. | `[UNKNOWN]` |
| 3 | **Mandarin Audio Production Method** (Human volunteer vs. batch TTS) | Deferred until plant list validated | Media player UX is completely decoupled from audio origin. As long as the file is a standard MP3/M4A accessible via HTTPS, the speaker button interaction, loading spinner, and error fallback behave identically. | `[PROVISIONAL UX]` |
| 4 | **Official Initial Location Names & Boundaries** | Needs field survey | UI specifications will use verified semantic identifiers ("Urban Farming Kelurahan Bubakan", "Taman Toga RW 03") with clear indicators that final cadastral/descriptive text is subject to field verification. | `[FIELD DATA REQUIRED]` |
| 5 | **Target User Device Spectrum in Bubakan** | Survey pending | UX adheres strictly to Android Go / low-spec performance guidelines: small layout hierarchy depth, image caching with memory limits, high contrast ratios, touch targets >= 48dp, and no GPU-heavy blur or heavy motion physics. | `[PROVISIONAL UX]` |

---

## 6. Field-Validation Dependencies & Prohibited Synthetic Data

The following critical rule is enforced across all Phase 1 UX documentation and wireframes:

> [!CAUTION]
> **ANTI-SYNTHETIC DATA RULE:**
> No wireframe, mock flow, or screen specification may present fictitious botanical names, fake medicinal claims, invented Bubakan addresses, or fake PIC personnel as settled facts.
> 
> Where sample visual context is required in wireframe layouts, the specification must explicitly use standardized content templates with `[CONTOH DATA LAPANGAN / BUTUH VALIDASI]` watermarks or neutral placeholders (e.g. "Nama Tanaman", "Nama Ilmiah", "Deskripsi manfaat tanaman berdasarkan verifikasi lapangan").

### Inventory of Field-Validation Dependencies:

1. **Plant Species Inventory (`plants` collection):**
   - Exact species physically planted in Kelurahan Urban Farming and RW 03 Taman Toga (`[FIELD DATA REQUIRED]`).
   - Indonesian common name, botanical Latin name, verified Chinese characters (Hanzi), and Pinyin (`[FIELD DATA REQUIRED]`).
   - Verified local cultivation notes and medicinal/herbal benefits (`[FIELD DATA REQUIRED]`).
2. **Location Realia (`locations` collection):**
   - Physical GPS coordinates (latitude, longitude) captured on-site with handheld device (`[FIELD DATA REQUIRED]`).
   - Actual photography of the garden sites, physical entrance, beds, and plant specimens (`[FIELD DATA REQUIRED]`).
   - Specific RW/RT administrative boundaries and field PIC identity confirmed by Kelurahan (`[FIELD DATA REQUIRED]`).
3. **QR Readiness Gate:**
   - Production QR generation is disabled until a plant is marked `featured: true` and validated against real garden beds (`[FIELD DATA REQUIRED]`).

---

## 7. Technical Constraints Directly Affecting UX

1. **Storage Free-Tier Optimization (Firebase Storage 5GB):**
   - Wireframes & flows for photo upload must specify client-side compression (max 1080p, JPEG quality 80%, target size ~300KB) and clear preview/retake controls before network submission.
2. **Network Volatility in Garden/Outdoor Environments:**
   - Garden plots may have spotty cellular signal. All create/edit forms must support offline optimism or clear local dirty state preservation so PICs do not lose form input if network drops.
   - Screen states must explicitly document `OfflineBanner`, cached data indicators, and retry triggers.
3. **Android Location Provider Reliability:**
   - Cold GPS fixes outdoors can take 5–15 seconds. GPS flow must display an active acquiring spinner with elapsed feedback, accuracy radius indicator, and fallback to manual coordinate input or retry.
4. **App Link Verification & Fallback Resilience:**
   - If App Link verification is delayed or user opens via an unverified third-party browser, the web fallback must immediately render the plant card and offer a zero-friction APK download link.

---

## 8. Anti-Slop Scope Boundaries for Phase 1

The following elements are strictly **EXCLUDED** from Phase 1 UX design:

- ❌ No e-commerce, shopping carts, or seed purchase buttons.
- ❌ No user registration for general public (strictly friction-free anonymous browsing).
- ❌ No in-app social media feeds, comments, likes, or user forums.
- ❌ No gamification, harvest points, streaks, badges, or virtual gardening levels.
- ❌ No AI chatbot, automatic plant diagnosis camera scanner, or LLM plant assistant.
- ❌ No weather forecasting widgets or IoT soil sensor dashboards.
- ❌ No push notification marketing center.
- ❌ No complex multi-layer nested navigation (strict 3-level maximum: Home/Map → Location → Plant).
- ❌ No decorative glassmorphism, heavy gradients, or battery-draining continuous background animations.

---

## 9. Conclusion & Baseline Certification

Phase 0 inputs are coherent, verified, and strictly bounded. Phase 1 proceeds to formulate the Information Architecture, Screen Inventory, User Flows, and Component Specifications adhering 100% to this audited baseline.
