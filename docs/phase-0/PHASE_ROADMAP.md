# PHASE ROADMAP — BUBAKAN GREEN

**Date:** 2026-09-23  
**Version:** 2.0 (Corrected)

---

## Phase Overview

The original 12-phase proposal has been consolidated to **10 phases** to reduce handoff overhead while maintaining clear separation of concerns.

| Phase | Name | Dependency | Deliverable |
|-------|------|------------|-------------|
| 0 | Discovery + Planning | None | Approved plan + documentation |
| 1 | Project Setup + Data Model | Phase 0 approved | Android project + Firebase + Firestore schema + security rules |
| 2 | Public Read (Core App) | Phase 1 | Browsable app: locations, plants, map, plant detail |
| 3 | PIC Auth + Management | Phase 2 | PIC login, CRUD for locations/plants, photo upload, GPS capture |
| 4 | Admin Functions | Phase 3 | Location approval, PIC management |
| 5 | QR + App Links + Web Fallback | Phase 2 (app), Phase 1 (hosting) | QR generation, App Links, web fallback page |
| 6 | Mandarin Audio | Phase 2 (plant detail screen) | Audio playback on plant detail |
| 7 | Field Data Population | Phase 3 (PIC functions working) | Real data entered, QR labels created |
| 8 | QA + Field Testing | Phase 7 (real data) | Bug fixes, usability validation |
| 9 | Deployment + Handover | Phase 8 (tested) | Final APK, documentation, ownership transfer |
| 10 | (FUTURE) Offline Support | Phase 9 (deployed) | Explicit offline mode with sync |

---

## Field Validation Gate

> [!CAUTION]  
> This is a **hard dependency chain**, not a suggestion. Phases can be developed in parallel for code, but production data operations follow this gate.

```
PHASE 0: Architecture + Planning
       ↓
PHASES 1-6: Application Development (uses sample/test data)
       ↓
       ↓  ← FIELD VALIDATION GATE ←
       ↓
PHASE 7: Field Inventory
  → Identify actual locations
  → GPS capture at actual locations
  → Inventory actual plants
  → Photograph actual plants/locations
  → Select featured plants (QR candidates)
       ↓
  → Enter real data into system (via PIC app)
       ↓
  → ONLY NOW: Generate QR for featured plants
       ↓
  → Print QR labels
       ↓
  → Place labels at physical locations
       ↓
  → Field-test QR scanning
```

**Hard blockers:**
- NO FIELD INVENTORY → NO PRODUCTION PLANT RECORDS
- NO VALIDATED PLANTS → NO FINAL QR
- NO FINAL QR → NO PHYSICAL LABELS
- NO FINALIZED PLANT LIST → NO MANDARIN AUDIO PRODUCTION

Mandarin audio production method (TTS vs human recording) is decided after the plant list is finalized, not during architecture planning.

---

## Phase Consolidation Rationale

| Original Phase | Merged Into | Why |
|----------------|-------------|-----|
| Phase 1 (Information Architecture + UX) | Phase 0 + Phase 1 | IA is part of planning (Phase 0). UX is validated during implementation (Phase 2). Separate UX-only phase adds overhead without code output. |
| Phase 2 (Data Model + Backend) | Phase 1 | Data model and backend setup are one logical unit — you can't test one without the other. |
| Phase 3 (Public Android) + Phase 4 (PIC/Admin) | Phase 2 + Phase 3 + Phase 4 | Split by user role (public → PIC → admin) is cleaner than split by technical layer. Each phase delivers a complete vertical slice. |
| Phase 9 (Offline) | Phase 10 (Future) | Offline with sync is complex and not validated as MVP-critical. Deferred. |
| Phase 10 (QA) + Phase 11 (Deployment) | Phase 8 + Phase 9 | QA and deployment are each smaller than a full phase once real data exists. |

---

## Detailed Phase Descriptions

---

### PHASE 0 — Discovery + Validation + Planning ← CURRENT

**Goal:** Understand the problem, define the product, plan the architecture, get approval.

**Deliverables:**
- [x] Repository audit (`REPOSITORY_AUDIT.md`)
- [x] Implementation plan (`PHASE_0_IMPLEMENTATION_PLAN.md`)
- [x] Data validation matrix (`DATA_VALIDATION_MATRIX.md`)
- [x] Scope control (`SCOPE_CONTROL.md`)
- [x] Architecture decisions (`ARCHITECTURE_DECISIONS.md`)
- [x] Phase roadmap (`PHASE_ROADMAP.md`)
- [x] Self-review (`PHASE_0_SELF_REVIEW.md`)
- [ ] Product owner approval

**Exit criteria:** Explicit `ACC PHASE 0` from product owner.

**No code is written in this phase.**

---

### PHASE 1 — Project Setup + Data Model

**Goal:** Initialize the Android project and Firebase backend. Define and deploy the data schema.

**Deliverables:**
- Android project initialized (Kotlin + Compose + Hilt)
- Firebase project created (Firestore, Auth, Storage, Hosting)
- Firestore collections defined: `locations`, `plants`, `users`
- Firestore security rules implemented and tested
- Firebase Storage security rules implemented
- Firebase Hosting configured (for future App Links)
- Basic app navigation skeleton (empty screens)

**Key activities:**
1. `npx` or Android Studio: create Kotlin + Compose project
2. Add Firebase SDK dependencies
3. Create `google-services.json` configuration
4. Define Firestore document schemas (as documented in Implementation Plan)
5. Write Firestore security rules
6. Test rules with Firebase Emulator Suite
7. Deploy rules to Firebase

**Exit criteria:**
- Android project builds and runs (empty shell)
- Firestore rules deployed and tested
- Security rules pass: public read, PIC write (own locations only), admin write (all)

**Estimated effort:** 1–2 days

---

### PHASE 2 — Public Read (Core Android App)

**Goal:** Build the public-facing read-only experience. Anyone can browse locations and plants.

**Deliverables:**
- Home screen (featured locations)
- Location list screen (with type filter)
- Location detail screen (info, photo, map pin, plants list)
- Plant detail screen (names, description, photo, Mandarin display — no audio yet)
- Map view (osmdroid, pins for all locations)
- Navigation (Jetpack Navigation Compose)
- Image loading (Coil)

**Key activities:**
1. Implement MVVM ViewModels + Repositories
2. Build Compose screens
3. Integrate Firestore reads
4. Integrate osmdroid for map
5. Implement deep link intent filter (preparation for App Links)
6. Manual testing with sample data in Firestore

**Dependencies:** Phase 1 (project + schema must exist)

**Exit criteria:**
- App displays locations from Firestore
- App displays plants within a location
- Map shows location pins
- Plant detail shows all text fields (Mandarin text, no audio)
- Navigation between screens works

**Estimated effort:** 3–5 days

---

### PHASE 3 — PIC Authentication + Management

**Goal:** Enable PICs to log in and manage their assigned locations and plants.

**Deliverables:**
- Login screen (email/password)
- PIC dashboard (my locations)
- Add Location form (name, type, RW, description, GPS, photo)
- Edit Location form
- Add Plant form (names, description, photo)
- Edit Plant form
- Photo upload to Firebase Storage
- GPS capture (single point, FusedLocationProviderClient)
- Per-location authorization enforcement (client-side + Firestore rules)

**Key activities:**
1. Implement Firebase Auth integration
2. Build PIC-specific screens
3. Implement GPS capture with permission handling
4. Implement photo picker + upload
5. Enforce authorization (PIC can only edit assigned locations)
6. Test with emulator and physical device

**Dependencies:** Phase 2 (display screens must exist to show managed data)

**Exit criteria:**
- PIC can log in / log out
- PIC can create a location with GPS
- PIC can add/edit plants in their locations
- PIC cannot edit other PICs' locations
- Photos upload and display correctly

**Estimated effort:** 3–5 days

---

### PHASE 4 — Admin Functions

**Goal:** Enable admin to approve locations and manage PICs.

**Deliverables:**
- Admin dashboard (all locations, including pending)
- Location approval workflow (pending → published)
- PIC management guide (Firebase Console workflow documented)
- Admin vs PIC UI differentiation

**Key activities:**
1. Add admin-specific screens/actions
2. Implement status-based location filtering (admin sees pending)
3. Implement approval action
4. Document PIC account creation via Firebase Console
5. Optionally: simple in-app PIC creation (if time permits)

**Dependencies:** Phase 3 (PIC auth must work)

**Exit criteria:**
- Admin can see pending locations
- Admin can approve a location (status → PUBLISHED)
- PIC creation process is documented
- Admin and PIC see appropriate UI for their role

**Estimated effort:** 1–2 days

---

### PHASE 5 — QR + App Links + Web Fallback

**Goal:** Complete the QR-to-app flow — from physical label to digital plant detail.

**Deliverables:**
- QR code generation (ZXing, in-app for PIC)
- QR download as image
- `assetlinks.json` deployed to Firebase Hosting
- Android App Links intent filter configured and verified
- Web fallback page (static HTML/CSS/JS on Firebase Hosting)
- Web page reads Firestore data and displays plant/location info
- Web page includes APK download link

**Key activities:**
1. Integrate ZXing for QR bitmap generation
2. Implement QR preview + save to gallery
3. Configure `AndroidManifest.xml` intent filters for App Links
4. Create and deploy `/.well-known/assetlinks.json`
5. Build web fallback page (HTML + Firestore JS SDK)
6. Deploy web to Firebase Hosting
7. End-to-end test: scan QR → app opens (or web fallback)

**Dependencies:** Phase 2 (plant detail screen), Phase 1 (Firebase Hosting)

**Exit criteria:**
- PIC can generate QR for a plant
- Scanning QR opens plant detail in app (if installed)
- Scanning QR opens web fallback (if not installed)
- Web fallback shows plant info + APK download link

**Estimated effort:** 2–3 days

---

### PHASE 6 — Mandarin Audio

**Goal:** Add audio playback for Mandarin plant names.

**Deliverables:**
- Audio file upload capability (PIC)
- 🔊 button on plant detail
- Tap → play once → stop
- Graceful handling when no audio available

**Key activities:**
1. Add audio upload to PIC plant edit form
2. Store audio in Firebase Storage
3. Implement MediaPlayer playback on plant detail
4. Handle loading, playing, error states
5. Hide 🔊 when no audio file exists

**Dependencies:** Phase 2 (plant detail screen)

**Exit criteria:**
- 🔊 button appears when audio exists
- Tap plays audio once
- Button hidden when no audio
- No autoplay, no looping

**Estimated effort:** 1 day

---

### PHASE 7 — Field Data Population

**Goal:** Populate the system with real-world data from field surveys.

**Deliverables:**
- Actual plant inventory entered per location
- Actual GPS coordinates captured per location
- Actual photos uploaded
- Mandarin names/audio added (where available)
- QR codes generated for selected plants
- QR labels printed
- QR labels physically placed at locations

**Key activities:**
1. Field survey: visit all locations, inventory plants
2. Capture GPS at each location using the app
3. Photograph plants and locations
4. Enter data through the app (PIC flow)
5. Research and enter Latin names, Mandarin names
6. Generate or record Mandarin audio
7. Generate QR for selected plants
8. Print QR labels
9. Place labels at physical plants

**Dependencies:** Phase 3 (PIC functions working), Phase 5 (QR generation)

**Exit criteria:**
- All known locations have real data (GPS, photos, descriptions)
- All inventoried plants have real data (names, descriptions, photos)
- QR labels are physically in place
- Data is real, not fabricated

**Estimated effort:** 3–7 days (depends on number of locations and field access)

**This is a FIELD WORK phase, not primarily a coding phase.**

---

### PHASE 8 — QA + Field Testing

**Goal:** Verify everything works in real conditions with real users.

**Deliverables:**
- End-to-end test results
- QR scan test at physical locations
- PIC usability feedback
- Bug fix list and resolution
- Device compatibility results

**Key activities:**
1. Test all user flows end-to-end
2. Visit physical locations and test QR scanning
3. Have PICs test the management features
4. Test on multiple devices / Android versions
5. Test network conditions (good, poor, offline)
6. Fix bugs found
7. Regression test after fixes

**Dependencies:** Phase 7 (real data exists)

**Exit criteria:**
- All critical user flows work correctly
- QR codes at physical locations scan correctly
- PICs can operate the app independently
- No critical bugs remain

**Estimated effort:** 2–3 days

---

### PHASE 9 — Deployment + Handover

**Goal:** Deliver the final product to Kelurahan Bubakan.

**Deliverables:**
- Final signed APK
- APK uploaded to Google Drive with share link
- Firebase project ownership transferred
- User guide for PIC (PDF or in-app)
- Admin guide (Firebase Console operations)
- Source code handed over
- QR templates archive
- Training session for PIC and Admin

**Key activities:**
1. Final APK build (release, signed, minified)
2. Upload to Google Drive
3. Transfer Firebase project ownership to designated email
4. Write PIC user guide
5. Write Admin guide
6. Conduct training session
7. Verify PIC can operate independently post-training
8. Final verification of all deployed services

**Dependencies:** Phase 8 (testing complete)

**Exit criteria:**
- Kelurahan has APK and distribution link
- Kelurahan has Firebase project ownership
- PIC can add/edit data independently
- Admin can approve locations independently
- Documentation is complete and accessible
- System runs at Rp0/month

**Estimated effort:** 1–2 days

---

### PHASE 10 (FUTURE) — Offline Support

**Status:** DEFERRED — Not part of MVP  
**Prerequisite:** Explicit product owner approval

**Goal:** Enable explicit offline mode with write queue and sync.

**Deliverables:**
- Offline read mode (beyond Firestore's built-in cache)
- Offline write queue (pending operations stored locally)
- Background sync when connection restores
- Conflict resolution for simultaneous edits
- Offline indicator in UI

**Why deferred:**
- Significant complexity (sync, conflicts, state management)
- Firestore's built-in offline cache provides basic read-offline capability
- Most write operations (adding plants, uploading photos) inherently need internet
- Internet connectivity at locations is [UNKNOWN] — may not be necessary

**Estimated effort:** 5–10 days (if approved)

---

## Timeline Summary

| Phase | Estimated Days | Cumulative |
|-------|---------------|------------|
| 0 | 1 | 1 |
| 1 | 1–2 | 2–3 |
| 2 | 3–5 | 5–8 |
| 3 | 3–5 | 8–13 |
| 4 | 1–2 | 9–15 |
| 5 | 2–3 | 11–18 |
| 6 | 1 | 12–19 |
| 7 | 3–7 | 15–26 |
| 8 | 2–3 | 17–29 |
| 9 | 1–2 | 18–31 |
| **TOTAL** | **18–31 days** | — |

> [!WARNING]  
> Phase 7 (Field Data Population) duration is highly variable and depends on field access, number of locations, and availability of field team. This is the most unpredictable phase.
