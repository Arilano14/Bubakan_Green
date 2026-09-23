# PHASE 0 IMPLEMENTATION PLAN — BUBAKAN GREEN

**Sistem Informasi Urban Farming & Taman Toga**  
**Kelurahan Bubakan, Kecamatan Mijen, Kota Semarang**

**Date:** 2026-09-23  
**Version:** 2.0 (Corrected)  
**Status:** DRAFT — AWAITING APPROVAL  
**Correction:** v2.0 fixes source-of-truth classification, adds featured/coordinatesStatus to models, adds Field Validation Gate, defers Map decision, locks Mandarin interaction model.

---

## 1. PRODUCT DEFINITION

**BUBAKAN GREEN** is a mobile-first information system for documenting, managing, and publicly sharing Urban Farming and Taman Toga (medicinal herb garden) locations within Kelurahan Bubakan.

The system enables:
- **Public users** to browse locations, view plant information, and access plant details via QR codes
- **PIC (Penanggung Jawab / Person in Charge)** to manage locations and plants they are authorized for
- **Admin (Kelurahan)** to oversee all locations, approve new entries, and manage PICs

[USER-PROVIDED] Product identity is "BUBAKAN GREEN" — not a KKN/university application.

---

## 2. PROBLEM STATEMENT

Kelurahan Bubakan has Urban Farming and Taman Toga initiatives but lacks:
- A centralized digital record of locations and plants
- A way for the public to discover and learn about these initiatives
- A way for residents to access plant information (including multilingual — Mandarin)
- QR-based on-site plant identification
- A sustainable management tool for PICs to maintain and update data

[USER-PROVIDED] The system must be sustainably operable by Kelurahan/PIC after KKN ends.

---

## 3. TARGET USERS

| User | Description | Technical Proficiency |
|------|-------------|----------------------|
| **General Public** | Residents, visitors, anyone interested in urban farming / Toga | Low — use smartphone camera for QR, browse app |
| **PIC** | Designated person responsible for one or more locations | Low to Medium — can operate simple app forms |
| **Admin** | Kelurahan staff overseeing the program | Low to Medium — minimal admin operations |

[ASSUMPTION] PICs are residents or community members, not IT professionals.  
[FIELD-VALIDATION REQUIRED] Actual PIC identities and their device capabilities.

---

## 4. PRODUCT SCOPE

### In Scope
- Location management (create, read, update) for Urban Farming and Taman Toga sites
- Plant management within locations (CRUD)
- Public browsing of locations and plants
- Map view of locations
- QR code generation for selected plants
- App Links (deep link from QR → app or web fallback)
- Web fallback page for non-app users
- Mandarin name/pinyin display with user-triggered audio
- PIC authentication and per-location authorization
- GPS capture for new location registration
- Photo upload for locations and plants
- APK distribution via Google Drive

### Out of Scope (for entire project)
- E-commerce / plant sales
- Social media features
- Community forum
- IoT / sensor integration
- Weather integration
- AI plant identification
- Game mechanics / gamification

---

## 5. MVP SCOPE

The MVP includes the **minimum viable set** to demonstrate value:

| Feature | MVP? | Justification |
|---------|------|---------------|
| Browse locations (list + map) | ✅ YES | Core public value |
| Browse plants within a location | ✅ YES | Core public value |
| View plant detail | ✅ YES | Core public value |
| PIC login | ✅ YES | Required for data management |
| PIC: add/edit plants | ✅ YES | Core management function |
| PIC: add/edit location | ✅ YES | Core management function |
| PIC: upload photos | ✅ YES | Visual information is essential |
| GPS capture for location | ✅ YES | Required for map accuracy |
| QR generation for plants | ✅ YES | Core physical-digital bridge |
| App Links (QR → app) | ✅ YES | QR is useless without link handling |
| Web fallback (QR → web if no app) | ✅ YES | Must handle non-app users |
| Mandarin name + pinyin display | ✅ YES | Explicitly required feature |
| Mandarin audio playback | ⚠️ SHOULD HAVE | Value depends on audio availability |
| Admin: approve locations | ⚠️ SHOULD HAVE | Can use manual process initially |
| Offline read mode | ❌ DEFERRED | Adds significant complexity |
| Push notifications | ❌ DEFERRED | No validated need |

---

## 6. OUT OF SCOPE (MVP)

- Offline-first with sync (deferred to later phase)
- Push notifications
- Analytics dashboard
- Multi-language UI (the app UI is Indonesian; Mandarin is for plant names only)
- Admin web dashboard (admin uses the app)
- User registration for public users (public access is anonymous)
- Plant quantity tracking (not a validated requirement)
- Harvest tracking
- Reporting / export

---

## 7. USER ROLES

| Role | Authentication | Capabilities |
|------|---------------|--------------|
| **PUBLIC** | None (anonymous) | Browse locations, browse plants, view details, use QR, view map |
| **PIC** | Firebase Auth (email/password) | All PUBLIC + manage assigned locations + manage plants within assigned locations + upload photos + capture GPS |
| **ADMIN** | Firebase Auth (email/password) + admin flag | All PIC + manage ALL locations + approve new locations + manage PIC accounts + assign PIC to locations |

### Permission Model

```
ADMIN
  ├── can manage all locations
  ├── can approve new locations
  ├── can assign PIC to locations
  └── can manage PIC accounts

PIC
  ├── can manage ONLY assigned locations
  ├── can add/edit/delete plants in assigned locations
  ├── can upload photos for assigned locations/plants
  ├── can capture GPS for new locations (pending approval)
  └── CANNOT manage other PICs' locations

PUBLIC
  ├── can browse all published locations
  ├── can view all published plants
  ├── can view map
  └── can access via QR
```

[ASSUMPTION] Initial admin account will be created manually during deployment.  
[ASSUMPTION] PIC accounts will be created by admin, not self-registration.

---

## 8. INFORMATION ARCHITECTURE

```
BUBAKAN GREEN
├── Home
│   ├── Featured locations summary
│   └── Quick access to browse
├── Locations (List)
│   ├── Filter by type (Urban Farming / Taman Toga)
│   └── Location Card → Location Detail
├── Location Detail
│   ├── Name, type, description, photo, address, RW
│   ├── Map pin
│   └── Plants list → Plant Detail
├── Plants (optional flat list across all locations)
├── Plant Detail
│   ├── Indonesian name
│   ├── Latin name
│   ├── Mandarin name + Pinyin + 🔊
│   ├── Description / benefits
│   ├── Photos
│   ├── Location reference
│   └── QR access point
├── Map View
│   └── All locations with pins → Location Detail
├── PIC Dashboard (authenticated)
│   ├── My Locations
│   ├── Add Location (with GPS)
│   ├── Edit Location
│   ├── Add Plant
│   ├── Edit Plant
│   └── Upload Photos
└── Admin (authenticated)
    ├── All PIC functions
    ├── Approve Locations
    └── Manage PICs
```

---

## 9. USER FLOWS

### 9.1 Public: Browse Location

```
Open App → Home → Locations List → Tap Location → Location Detail → Browse Plants → Tap Plant → Plant Detail
```

### 9.2 Public: Use QR

```
Scan QR (camera/Google Lens) → HTTPS App Link →
  IF app installed → Open plant/location detail in app
  IF app NOT installed → Web fallback page → View plant info + Download APK link
```

### 9.3 PIC: Add New Location

```
Login → PIC Dashboard → Add Location →
  Enter name → Select type (Urban Farming / Taman Toga) →
  Enter RW → Enter description → Capture GPS → Take/upload photo →
  Preview on map → Confirm → Submit →
  Status: PENDING_APPROVAL → Admin approves → PUBLISHED
```

### 9.4 PIC: Add Plant to Location

```
Login → My Locations → Select Location → Add Plant →
  Enter Indonesian name → Enter Latin name →
  (Optional) Enter Mandarin name + Pinyin →
  Enter description/benefits →
  Upload photo(s) → Save → Plant visible in location
```

### 9.5 PIC: Generate QR

```
Login → My Locations → Select Location → Select Plant →
  "Generate QR" → System creates QR with stable App Link URL →
  Preview QR → Download/Print
```

### 9.6 Public: Mandarin Audio

```
Plant Detail → See Mandarin name + Pinyin →
  Tap 🔊 icon → Audio plays once → Stops
```

---

## 10. LOCATION MODEL

```
Location {
  id: string (auto-generated, stable)
  name: string
  type: enum [URBAN_FARMING, TAMAN_TOGA]
  description: string
  rw: string (e.g., "03")
  address: string (optional descriptive address)
  latitude: number
  longitude: number
  coordinatesStatus: enum [PENDING, VERIFIED]
  featured: boolean (initial/highlighted locations)
  photoUrl: string (primary photo)
  photoUrls: string[] (gallery, optional)
  picUid: string (assigned PIC user ID)
  status: enum [DRAFT, PENDING_APPROVAL, PUBLISHED, ARCHIVED]
  createdAt: timestamp
  updatedAt: timestamp
  createdBy: string (user ID)
}
```

**Key design decisions:**
- `id` is auto-generated and stable — never changes after creation
- `type` is an enum, not free text — ensures consistent categorization
- `coordinatesStatus` tracks whether GPS coordinates have been field-verified (PIC captures → PENDING, admin verifies → VERIFIED)
- `featured` distinguishes initial/highlighted locations (Urban Farming Kelurahan, Taman Toga RW 03) from future additions by PIC. Featured locations may receive priority display.
- `status` supports approval workflow
- `picUid` links to a specific PIC — supports per-location authorization
- Location is a **first-class entity**, not a field on a plant

[ASSUMPTION] One PIC per location for MVP. Multi-PIC per location can be added later.  
[DOCUMENT-VERIFIED] Urban Farming at Kelurahan and Taman Toga at RW 03 are the initial featured locations.  
[NEEDS-FIELD-VALIDATION] Actual RW values, GPS coordinates, location names, addresses.

---

## 11. PLANT MODEL

```
Plant {
  id: string (auto-generated, stable)
  locationId: string (reference to parent Location)
  nameId: string (Indonesian name)
  nameLatin: string (Latin/scientific name)
  nameMandarin: string (optional, Mandarin characters)
  pinyin: string (optional, Mandarin romanization)
  mandarinAudioUrl: string (optional, URL to audio file)
  description: string (benefits, usage, etc.)
  photoUrl: string (primary photo)
  photoUrls: string[] (gallery, optional)
  featured: boolean (selected for QR / physical label)
  qrGenerated: boolean (whether QR has been generated)
  status: enum [ACTIVE, ARCHIVED]
  createdAt: timestamp
  updatedAt: timestamp
  createdBy: string (user ID)
}
```

**Key design decisions:**
- `id` is stable — QR codes reference this ID, must never change
- `locationId` creates the Location → Plants relationship
- `featured` marks plants selected for QR labels / physical information targets. Only featured plants should have QR generated. This keeps QR scope controlled.
- Mandarin fields are optional — not all plants will have Mandarin data initially
- `mandarinAudioUrl` is a file reference, not embedded audio
- `qrGenerated` is a convenience flag, not a security mechanism

[NEEDS-FIELD-VALIDATION] Actual plant names, Latin names, Mandarin names, descriptions.  
[USER-PROVIDED] QR generation blocked until real plants are inventoried and featured plants are selected.

---

## 12. QR MODEL

QR is **not a separate database entity**. QR is a generated artifact based on a stable URL pattern:

```
QR Content = https://{domain}/plant/{plantId}
```

Or for location-level QR:
```
QR Content = https://{domain}/location/{locationId}
```

**Properties:**
- QR encodes a URL, not data
- URL contains only the stable ID
- Changing plant data does NOT require reprinting QR
- QR is generated client-side or via a simple library
- QR is downloadable as an image for printing
- Only plants with `featured: true` should have QR generated

**No separate QR database table is needed.** The QR is deterministically derived from the plant/location ID and the domain.

### FIELD VALIDATION GATE (HARD BLOCKER)

> [!CAUTION]  
> QR generation for production use is **BLOCKED** until the following chain is complete:

```
FIELD INVENTORY (identify actual plants)
       ↓
VERIFY ACTUAL PLANTS (confirm they are physically present)
       ↓
SELECT FEATURED PLANTS (mark featured: true)
       ↓
CREATE STABLE PLANT IDs (in Firestore)
       ↓
GENERATE QR (using stable URL)
       ↓
PRINT LABEL
       ↓
FIELD TEST (scan QR at physical location)
```

**NO FIELD VALIDATION → NO FINAL QR**

The QR generation UI can be built and tested with sample data during development. But production QR codes must not be generated until real plants are validated.

[ASSUMPTION] Domain will be the Firebase Hosting domain (free subdomain) unless a custom domain is provided.

---

## 13. GPS FLOW

```
PIC taps "Add Location" →
  App requests ACCESS_FINE_LOCATION permission (one-time) →
  App gets single GPS fix →
  Displays coordinates on a map preview →
  PIC confirms or retries →
  Coordinates saved with location record →
  Permission usage ends
```

**Constraints:**
- Single GPS capture only — no background tracking
- Uses `ACCESS_FINE_LOCATION` (not `BACKGROUND_LOCATION`)
- GPS is only used during "Add Location" or "Edit Location" flow
- No geofencing, no route tracking, no location history
- Fallback: PIC can manually enter coordinates if GPS fails

[ASSUMPTION] `ACCESS_FINE_LOCATION` is sufficient for single-point capture.

---

## 14. MANDARIN AUDIO FLOW

### Interaction Model (LOCKED)

```
Plant Detail screen →
  IF Mandarin name exists →
    Display: 漢字 (Mandarin characters)
    Display: hàn zì (Pinyin)
    Display: 🔊 button
  
  User taps 🔊 →
    IF audio file exists →
      Play audio file once → Stop
    IF audio file does NOT exist →
      🔊 button is hidden or disabled
      (no error, no fallback)
```

**This interaction model is LOCKED.** [USER-PROVIDED]
- No autoplay
- No looping
- No automatic narration
- No live AI voice generation
- User taps → plays once → stops

### Audio Production Method (DEFERRED)

The audio production method is **intentionally not decided in Phase 0**.

| Option | Cost | Quality | Effort |
|--------|------|---------|--------|
| Human recording | Rp0 (if volunteer available) | High | High — need speaker + recording per plant |
| Pre-recorded TTS files (generated once, stored as static files) | Rp0 (use free TTS tool offline) | Medium | Medium — batch generate, then upload |
| Live API TTS | Potentially paid | High | Low — but adds runtime dependency and cost |

**Why deferred:** The audio production method depends on which plants are in the final inventory. Producing audio for hypothetical plants is waste.

**Decision timeline:**
```
PHASE 0: Mandarin interaction model = LOCKED ✅
PHASE Field Inventory: Actual plants = validated
PHASE Mandarin Content: Determine audio production method
PHASE Audio Production: Create audio files for validated plants
```

**What CAN be built now:** The playback architecture (🔊 button, MediaPlayer, Storage URL reference). The UI and technical implementation do not depend on the audio source method.

[NEEDS-FIELD-VALIDATION] Which plants need Mandarin names — depends on final plant inventory.

---

## 15. DATA LIFECYCLE

```
FIELD SURVEY
  → Identify locations
  → Inventory plants
  → Take photos
  → Record GPS
  → Identify PICs

DATA ENTRY (by PIC via app)
  → Create location
  → Add plants
  → Upload photos
  → Add Mandarin names (if available)

REVIEW (by Admin)
  → Approve locations
  → Verify data quality

PUBLICATION
  → Location + plants visible to public
  → QR codes generated for selected plants
  → QR labels printed and placed at physical locations

ONGOING MAINTENANCE (by PIC)
  → Update plant info
  → Add new plants
  → Update photos
  → Add new locations (with admin approval)
```

**Critical principle:** Data changes NEVER require an APK rebuild. Data lives in Firestore, not in the APK.

---

## 16. ADMIN/PIC PERMISSION MODEL

### Firestore Security Rules Concept

```
// Locations
locations/{locationId}:
  READ: anyone (published locations only)
  CREATE: authenticated PIC (status = PENDING_APPROVAL)
  UPDATE: PIC assigned to this location OR admin
  DELETE: admin only

// Plants
plants/{plantId}:
  READ: anyone (if parent location is published)
  CREATE: PIC assigned to parent location OR admin
  UPDATE: PIC assigned to parent location OR admin
  DELETE: PIC assigned to parent location OR admin

// Users (PIC/Admin profiles)
users/{userId}:
  READ: self OR admin
  CREATE: admin only
  UPDATE: self (limited fields) OR admin
```

### Custom Claims Approach

```
Firebase Auth Custom Claims:
{
  role: "admin" | "pic",
  assignedLocations: ["locationId1", "locationId2"]  // PIC only
}
```

[ASSUMPTION] Firebase Auth Custom Claims is the simplest way to encode role and location assignment without a separate permissions table.  
[ASSUMPTION] Custom claims are set via a Cloud Function or Firebase Admin SDK script (one-time setup tool).

---

## 17. TECH STACK OPTIONS

### Android

| Option | Pros | Cons | Cost |
|--------|------|------|------|
| **Kotlin + Jetpack Compose** | Modern, Google-recommended, declarative UI | Steeper learning curve for handover | Free |
| Kotlin + XML Views | Familiar, more tutorials | Outdated approach, more boilerplate | Free |
| Flutter | Cross-platform | Overkill for Android-only, different ecosystem | Free |
| React Native | Cross-platform | Overkill, JavaScript dependency | Free |

### Backend

| Option | Pros | Cons | Cost |
|--------|------|------|------|
| **Firebase (Firestore + Auth + Storage + Hosting)** | Free tier generous, real-time, managed, familiar | Vendor lock-in, custom claims need Cloud Functions | Free tier: 1GB Firestore, 5GB Storage, 10GB Hosting |
| Supabase | Open-source, PostgreSQL | Less mature, self-hosting needed for free | Free tier available |
| Custom server | Full control | Must host and maintain, cost | Paid |
| Static JSON + GitHub Pages | Zero cost | No auth, no real-time, PIC can't update via app | Free |

### Hosting (Web Fallback)

| Option | Pros | Cons | Cost |
|--------|------|------|------|
| **Firebase Hosting** | Free tier, integrated with Firebase, supports App Links | Part of Firebase ecosystem | Free (10GB/month) |
| GitHub Pages | Free, simple | Separate from backend | Free |
| Netlify | Free tier | Separate service | Free |

---

## 18. RECOMMENDED TECH STACK

| Component | Technology | Justification |
|-----------|-----------|---------------|
| **Android** | Kotlin + Jetpack Compose | Google-recommended, modern, efficient UI development |
| **Architecture** | MVVM + Repository pattern | Standard Android architecture, testable, maintainable |
| **Navigation** | Jetpack Navigation Compose | Standard navigation, supports deep links |
| **Backend** | Firebase | Managed, free tier, real-time sync, integrated auth |
| **Database** | Cloud Firestore | NoSQL, real-time listeners, offline cache built-in, free tier |
| **Authentication** | Firebase Authentication | Email/password for PIC/Admin, free |
| **File Storage** | Firebase Storage | Photos, audio files, free tier (5GB) |
| **Web Fallback** | Static HTML/CSS/JS | Minimal, no framework needed |
| **Web Hosting** | Firebase Hosting | Free, integrated, supports App Links config |
| **Maps** | **PENDING** (see ADR-004) | Google Maps or osmdroid/OSM — awaiting Product Owner decision |
| **QR Generation** | ZXing library (client-side) | Free, well-maintained, generates QR as bitmap |
| **APK Distribution** | Google Drive | Free, simple sharing link |
| **Dependency Injection** | Hilt | Standard for Android, reduces boilerplate |
| **Image Loading** | Coil | Kotlin-first, lightweight, Compose-native |
| **Media Playback** | Android MediaPlayer | Built-in, no extra dependency for simple audio |

**Total estimated cost: Rp0** (within free tiers for expected usage volume)

---

## 19. ARCHITECTURE DIAGRAM

```
┌─────────────────────────────────────────────────────┐
│                    USERS                             │
│   Public (anonymous)   PIC (auth)   Admin (auth)     │
└──────────┬──────────────┬──────────────┬─────────────┘
           │              │              │
           ▼              ▼              ▼
┌─────────────────────────────────────────────────────┐
│              BUBAKAN GREEN Android App               │
│  ┌──────────────────────────────────────────────┐    │
│  │  Jetpack Compose UI Layer                     │    │
│  │  - Home / Locations / Plants / Map / Detail   │    │
│  │  - PIC Dashboard / Admin                      │    │
│  └──────────────────┬───────────────────────────┘    │
│  ┌──────────────────┴───────────────────────────┐    │
│  │  ViewModel Layer (MVVM)                       │    │
│  └──────────────────┬───────────────────────────┘    │
│  ┌──────────────────┴───────────────────────────┐    │
│  │  Repository Layer                             │    │
│  │  - LocationRepository                         │    │
│  │  - PlantRepository                            │    │
│  │  - AuthRepository                             │    │
│  │  - StorageRepository                          │    │
│  └──────────────────┬───────────────────────────┘    │
└──────────────────────┼───────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────┐
│                FIREBASE                              │
│  ┌─────────────┐ ┌──────────┐ ┌───────────────────┐ │
│  │  Firestore   │ │  Auth    │ │  Storage          │ │
│  │  - locations │ │  - PIC   │ │  - photos         │ │
│  │  - plants    │ │  - Admin │ │  - audio          │ │
│  │  - users     │ │          │ │                   │ │
│  └─────────────┘ └──────────┘ └───────────────────┘ │
│  ┌──────────────────────────────────────────────┐    │
│  │  Firebase Hosting                             │    │
│  │  - Web fallback pages                         │    │
│  │  - App Links config (.well-known/assetlinks)  │    │
│  └──────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────┐
│              QR CODE FLOW                            │
│                                                      │
│  Physical QR Label                                   │
│       │                                              │
│       ▼                                              │
│  https://{domain}/plant/{plantId}                    │
│       │                                              │
│       ├─── App installed ──→ Open in BUBAKAN GREEN   │
│       │                                              │
│       └─── No app ────────→ Web fallback page        │
│                               ├── Plant info         │
│                               └── Download APK link  │
└─────────────────────────────────────────────────────┘
```

---

## 20. DATA FLOW

### Read Flow (Public)
```
App → Firestore (read locations/plants) → Display in UI
App → Storage (fetch photos/audio URLs) → Display/Play
```

### Write Flow (PIC)
```
PIC → App Form → Validate → Repository →
  → Firestore (write location/plant data)
  → Storage (upload photo/audio)
  → Firestore Security Rules (verify PIC authorization)
```

### QR Flow
```
Camera scans QR → Opens URL → Android Intent →
  → App Link handler (if app installed) → Navigate to plant/location detail
  → Web browser (if app not installed) → Render static plant info page + APK link
```

---

## 21. SECURITY APPROACH

| Concern | Approach |
|---------|----------|
| Public read access | Firestore rules allow read on published locations/plants without auth |
| PIC write access | Firestore rules verify auth UID matches `picUid` on location |
| Admin access | Custom claim `role: "admin"` checked in Firestore rules |
| Data validation | Firestore rules validate required fields, data types, string lengths |
| Photo upload | Storage rules restrict upload to authenticated users, limit file size |
| Admin account creation | Manual initial setup via Firebase Console or Admin SDK script |
| PIC account creation | Admin creates PIC accounts (no self-registration) |
| API key protection | Firebase API keys are restricted in Google Cloud Console |
| No sensitive personal data | System stores minimal PIC info (name, email for auth) — no KTP, no financial data |

[ASSUMPTION] No sensitive personal data is stored beyond PIC email and name.  
[ASSUMPTION] Public data (locations, plants) is intentionally public and not confidential.

---

## 22. OFFLINE/ONLINE STRATEGY

### MVP (Phase 3–8)
- **Online-first**: App requires internet for full functionality
- **Firestore offline cache**: Firestore SDK has built-in offline persistence — previously loaded data is readable offline automatically
- **No explicit offline mode**: No custom sync logic, no offline write queue
- **Graceful degradation**: Show cached data when offline, show "no connection" message for actions that require network

### Future (Phase 9, if approved)
- Explicit offline support with write queue
- Conflict resolution for simultaneous edits
- Background sync when connection restores

**Rationale:** Full offline-first adds significant complexity (conflict resolution, sync queues, UI state management). Firestore's built-in cache provides "good enough" offline read for MVP. Most PIC operations (adding plants, uploading photos) inherently require internet.

---

## 23. HOSTING STRATEGY

| Asset | Host | Cost |
|-------|------|------|
| Web fallback pages | Firebase Hosting | Free (10GB bandwidth/month) |
| App Links config (`assetlinks.json`) | Firebase Hosting | Free |
| Plant photos | Firebase Storage | Free (5GB storage, 1GB/day download) |
| Mandarin audio files | Firebase Storage | Free (within 5GB) |
| APK file | Google Drive | Free |

**Firebase Hosting free tier:** 10GB storage, 10GB/month transfer, custom domain support, SSL included.

**Firebase Storage free tier:** 5GB storage, 1GB/day download, 20K/day upload operations.

[ASSUMPTION] Expected data volume is well within free tiers (dozens of locations, hundreds of plants, not millions).

---

## 24. APK DISTRIBUTION STRATEGY

```
Build signed APK →
  Upload to Google Drive (shared folder) →
  Share link on web fallback page →
  Users download and sideload
```

**Why not Google Play?**
- Google Play requires $25 one-time fee
- Review process adds delay
- For a kelurahan-specific app, sideload distribution via Google Drive is sufficient
- Web fallback page serves as the "app store" page

**Installation flow:**
```
User gets link (from web fallback / QR / shared) →
  Download APK from Google Drive →
  Enable "Install unknown apps" (one-time) →
  Install →
  Done
```

[ASSUMPTION] Users can follow sideload instructions.  
[FIELD-VALIDATION REQUIRED] Target users' comfort with sideloading.

---

## 25. COST ESTIMATE

| Service | Free Tier | Expected Usage | Monthly Cost |
|---------|-----------|---------------|--------------|
| Firebase Firestore | 1GB storage, 50K reads/day | ~100 locations, ~500 plants, <1K reads/day | **Rp0** |
| Firebase Auth | 10K auth/month | <50 PIC/admin accounts | **Rp0** |
| Firebase Storage | 5GB, 1GB/day download | ~500 photos (~2GB), ~100 audio (~100MB) | **Rp0** |
| Firebase Hosting | 10GB storage, 10GB/month transfer | Static pages, minimal traffic | **Rp0** |
| Map SDK | **PENDING** — see ADR-004 | <1K loads/month expected | **Rp0** (both options free at this scale) |
| Google Drive (APK) | 15GB | One APK file (~20MB) | **Rp0** |
| Domain name (optional) | — | Optional custom domain | **Rp0 if using Firebase subdomain** |

**Total estimated monthly cost: Rp0**

> [!NOTE]  
> Map SDK decision (Google Maps vs osmdroid/OSM) is pending Product Owner input. Both are Rp0 at expected usage volume. See ADR-004 for detailed analysis.

---

## 26. FIELD VALIDATION REQUIREMENTS

These items MUST be validated through field work before production data entry:

| Item | Priority | Who Validates | Status |
|------|----------|--------------|--------|
| Existing Urban Farming locations | HIGH | KKN team + Kelurahan | NEEDS VALIDATION |
| Existing Taman Toga locations | HIGH | KKN team + Kelurahan | NEEDS VALIDATION |
| Plant inventory per location | HIGH | KKN team + PIC | NEEDS VALIDATION |
| Plant Indonesian names | HIGH | KKN team + PIC | NEEDS VALIDATION |
| Plant Latin names | MEDIUM | KKN team (research) | NEEDS VALIDATION |
| Plant Mandarin names + Pinyin | MEDIUM | KKN team (Mandarin speakers) | NEEDS VALIDATION |
| GPS coordinates per location | HIGH | KKN team (on-site) | NEEDS VALIDATION |
| Location photos | HIGH | KKN team (on-site) | NEEDS VALIDATION |
| Plant photos | HIGH | KKN team (on-site) | NEEDS VALIDATION |
| PIC identities per location | HIGH | Kelurahan | NEEDS VALIDATION |
| PIC device types (Android version) | MEDIUM | KKN team | NEEDS VALIDATION |
| Internet connectivity at locations | MEDIUM | KKN team (on-site) | NEEDS VALIDATION |
| Target audience comfort with sideloading | LOW | KKN team (survey) | NEEDS VALIDATION |

---

## 27. TEST STRATEGY

### Unit Tests
- ViewModel logic (data transformation, state management)
- Repository methods (mocked Firestore)
- Permission checks (role-based access)

### Integration Tests
- Firestore security rules (Firebase Emulator Suite)
- App Link handling
- GPS capture flow

### Manual Testing
- QR scan → app open flow
- QR scan → web fallback flow
- PIC CRUD operations
- Photo upload/display
- Mandarin audio playback
- Map display with pins
- Offline behavior (cache)

### Field Testing (Phase 10)
- QR labels at physical plant locations
- GPS accuracy at locations
- App usability by actual PICs
- Audio playback on target devices
- Network conditions at locations

---

## 28. DEPLOYMENT STRATEGY

```
Development → Local testing (Emulator) →
  Firebase Emulator testing (security rules) →
  Deploy Firestore rules + Storage rules →
  Deploy web fallback to Firebase Hosting →
  Build signed APK →
  Upload APK to Google Drive →
  Share with test PICs →
  Field testing →
  Fix issues →
  Final APK build →
  Distribute to Kelurahan
```

---

## 29. HANDOVER STRATEGY

### What is handed over:
1. **APK** (Google Drive link)
2. **Firebase project** (ownership transferred to Kelurahan Gmail or designated email)
3. **Documentation** (user guide for PIC, admin guide)
4. **Source code** (repository)
5. **QR templates** (printable labels)

### Sustainability requirements:
- PIC can add/edit locations and plants without developer help
- Admin can create PIC accounts via Firebase Console (with guide)
- Data updates do not require code changes or APK rebuilds
- System runs on free tier with no monthly costs

### Training needed:
- PIC: How to log in, add/edit locations, add/edit plants, upload photos
- Admin: How to approve locations, create PIC accounts (Firebase Console)

[FIELD-VALIDATION REQUIRED] Who will be the Firebase project owner after KKN.

---

## 30. IMPLEMENTATION PHASES

### PHASE 0 — Discovery + Validation + Planning ← CURRENT
- Repository audit
- Product definition
- Architecture decisions
- Implementation plan
- Data validation matrix
- Scope control
- Self-review
- User approval

### PHASE 1 — Project Setup + Data Model
- Initialize Android project (Kotlin + Compose)
- Configure Firebase project (Firestore, Auth, Storage, Hosting)
- Define Firestore collections and document schema
- Implement Firestore security rules
- Test security rules with Firebase Emulator

### PHASE 2 — Public Read (Core Android App)
- Home screen
- Location list screen (with type filter)
- Location detail screen
- Plant list (within location)
- Plant detail screen (including Mandarin display)
- Map view with location pins
- Image loading (Coil)
- Navigation (Jetpack Navigation Compose)

### PHASE 3 — PIC Authentication + Management
- PIC login screen
- PIC dashboard
- Add/Edit location form
- Add/Edit plant form
- Photo upload
- GPS capture for location
- Per-location authorization enforcement

### PHASE 4 — Admin Functions
- Admin dashboard
- Location approval workflow
- PIC account management (via Firebase Console + guide)

### PHASE 5 — QR + App Links + Web Fallback
- QR code generation (ZXing)
- QR download/print
- App Links configuration (`assetlinks.json`)
- Deep link handling in Android app
- Web fallback page (static HTML/CSS/JS on Firebase Hosting)

### PHASE 6 — Mandarin Audio
- Audio file upload by PIC
- Audio playback (tap 🔊 → play once)
- Audio file management in Firebase Storage

### PHASE 7 — Field Data Population
- Field inventory of actual plants
- GPS capture at actual locations
- Photo capture at actual locations
- Data entry into the app
- QR generation for selected plants
- QR label printing
- Physical QR placement

### PHASE 8 — QA + Field Testing
- End-to-end testing
- QR scanning test at physical locations
- PIC usability testing
- Network condition testing
- Device compatibility testing
- Bug fixes

### PHASE 9 — Deployment + Handover
- Final APK build
- Upload to Google Drive
- Firebase project ownership transfer
- User documentation (PIC guide, Admin guide)
- Training for PIC and Admin
- Final field verification

### PHASE 10 (FUTURE, IF APPROVED) — Offline Support
- Explicit offline read mode
- Offline write queue with sync
- Conflict resolution

**Note:** Original 12-phase plan consolidated to 10 phases. Phases 2–4 of original (UX, Data Model, Public App, PIC/Admin) merged because the data model, UI, and management features are best built together incrementally. Phase 7 (Field Data) represents the critical real-world bridge that must happen before deployment. Phase 10 (Offline) is explicitly deferred as a future enhancement.

---

## 31. ACCEPTANCE CRITERIA

### System-level
- [ ] Public user can browse locations without login
- [ ] Public user can view plant details
- [ ] Public user can see locations on map
- [ ] Public user can scan QR and reach plant detail (app) or web fallback (no app)
- [ ] PIC can log in
- [ ] PIC can add a new location with GPS
- [ ] PIC can add plants to their assigned locations
- [ ] PIC can upload photos
- [ ] PIC cannot modify other PICs' locations
- [ ] Admin can approve locations
- [ ] Admin can manage PIC accounts
- [ ] Plant detail shows Mandarin name + Pinyin + audio button (when data available)
- [ ] Audio plays once on tap, does not autoplay
- [ ] Data changes reflect without APK rebuild
- [ ] QR codes use stable URLs that survive data updates
- [ ] Web fallback shows plant info and APK download link
- [ ] System operates within Rp0 infrastructure cost

### Non-functional
- [ ] App works on Android 8.0+ (API 26+)
- [ ] App loads location list within 3 seconds on reasonable connection
- [ ] Photos are compressed before upload (max 1MB per photo)
- [ ] App handles no-internet gracefully (shows cached data or error message)

---

## 32. RISKS

| Risk | Probability | Impact | Mitigation |
|------|------------|--------|------------|
| Field data not collected in time | HIGH | HIGH | Start field inventory early, parallel with development |
| PIC not tech-savvy enough | MEDIUM | HIGH | Simple UI, training session, user guide |
| Google Maps requires billing account | HIGH | LOW | Alternative: use osmdroid with OpenStreetMap (free) |
| Firebase free tier exceeded | LOW | MEDIUM | Monitor usage, optimize queries |
| Post-KKN maintenance abandoned | HIGH | HIGH | Simple architecture, zero cost, documentation |
| Mandarin audio not available for all plants | MEDIUM | LOW | Audio is optional — graceful degradation |
| Target devices too old | MEDIUM | MEDIUM | Set minimum API 26 (Android 8.0, released 2017) |
| Internet connectivity poor at locations | MEDIUM | MEDIUM | Firestore offline cache helps for reads |

---

## 33. OPEN QUESTIONS

| # | Question | Why It Matters | Blocking? | Default if Unanswered |
|---|----------|---------------|-----------|----------------------|
| 1 | **Map provider:** Google Maps (billing account) or osmdroid/OSM (free)? | Affects SDK choice, Compose integration quality, billing account requirement. See ADR-004 for full analysis. | YES — blocks Phase 2 (Map View) | No default — Product Owner must decide |
| 2 | **Firebase project owner post-KKN?** Who will own the Gmail/email linked to the Firebase project? | Without an owner, Firebase project becomes inaccessible after KKN. All data and hosting would be lost. | YES — blocks Phase 9 (Handover) | Must be determined before deployment |
| 3 | **PIC account creation method:** Admin creates via Firebase Console (simpler, documented) or in-app admin panel (more complex)? | Affects Phase 4 scope. Firebase Console method is simpler but requires admin to use web browser. | NO — default to Firebase Console + guide | Firebase Console + guide |
| 4 | **Plant deletion policy:** Hard delete or soft delete (archive)? | Hard delete is irreversible. Archive preserves data but adds status management. | NO — default available | Archive (soft delete) |
| 5 | **New plant approval:** Auto-publish when PIC adds, or require admin approval? | Affects workflow complexity. Auto-publish is simpler; admin approval adds quality control. | NO — default available | Auto-publish (admin approval only for locations) |

**Questions removed from v1.0 (resolved):**
- Source documents → Product context baseline created from Product Owner input
- Mandarin audio source → Deferred until plant list finalized (not a Phase 0 decision)
- Minimum Android version → Default API 26 unless field survey shows otherwise
- Custom domain → Default Firebase subdomain
- Number of locations/plants → Architecture supports N; exact numbers are field data

---

## 34. DECISIONS REQUIRED FROM PRODUCT OWNER

> [!IMPORTANT]  
> The following decisions affect architecture and must be resolved before their blocking phase.

| # | Decision | Options | Blocks | Recommendation |
|---|----------|---------|--------|----------------|
| 1 | **Map provider** | Google Maps (billing account required) vs osmdroid/OSM (no billing) vs Mapbox. See ADR-004 for full 8-criteria analysis. | Phase 2 | Product Owner decides based on ADR-004 analysis |
| 2 | **Firebase project owner** | Kelurahan email, designated individual, or other | Phase 9 | Must decide before handover |
| 3 | **PIC account creation** | Firebase Console + guide (simpler) vs in-app (more complex) | Phase 4 | Firebase Console + guide |

---

## FEATURE DETAIL CARDS

### FEATURE: Browse Locations

**WHY:** Primary public value — users discover Urban Farming and Taman Toga locations  
**USER:** Public (anonymous)  
**INPUT:** None (browse) or filter by type  
**OUTPUT:** List of locations with name, type, photo, description preview  
**DEPENDENCY:** Firestore (locations collection)  
**TECHNICAL APPROACH:** Firestore query with filter, Compose LazyColumn  
**MVP?** YES  
**SCOPE LIMIT:** List view + type filter only. No search, no sorting, no pagination for MVP.  
**RISK:** Low  
**ACCEPTANCE CRITERIA:** User sees all published locations. Can filter by Urban Farming or Taman Toga.

---

### FEATURE: Location Detail

**WHY:** Users need to see full information about a location  
**USER:** Public (anonymous)  
**INPUT:** Tap on location from list or map  
**OUTPUT:** Full location info: name, type, description, photos, map pin, plants list  
**DEPENDENCY:** Firestore (locations + plants collections)  
**TECHNICAL APPROACH:** Firestore document read + subcollection query, Compose screen  
**MVP?** YES  
**SCOPE LIMIT:** Single photo for MVP (gallery later)  
**RISK:** Low  
**ACCEPTANCE CRITERIA:** User sees complete location information and can navigate to individual plants.

---

### FEATURE: Plant Detail

**WHY:** Core informational value — plant name, description, benefits, multilingual names  
**USER:** Public (anonymous), also QR landing target  
**INPUT:** Tap on plant from location detail, or deep link from QR  
**OUTPUT:** Plant detail: Indonesian name, Latin name, Mandarin + Pinyin + 🔊, description, photos  
**DEPENDENCY:** Firestore (plants collection), Firebase Storage (photos, audio)  
**TECHNICAL APPROACH:** Firestore document read, Coil for images, MediaPlayer for audio  
**MVP?** YES  
**SCOPE LIMIT:** No plant-to-plant navigation. No "related plants."  
**RISK:** Low  
**ACCEPTANCE CRITERIA:** User sees all plant information. Mandarin section appears only if data exists. Audio plays on tap.

---

### FEATURE: Map View

**WHY:** Spatial discovery of locations  
**USER:** Public (anonymous)  
**INPUT:** Navigate to map tab  
**OUTPUT:** Map with pins for all published locations, tap pin → location detail  
**DEPENDENCY:** Map SDK (osmdroid or Google Maps), Firestore (locations with coordinates)  
**TECHNICAL APPROACH:** Map composable with markers from Firestore data  
**MVP?** YES  
**SCOPE LIMIT:** View only. No directions, no routing, no clustering.  
**RISK:** MEDIUM — osmdroid Compose integration may need custom wrapper  
**ACCEPTANCE CRITERIA:** All published locations with GPS coordinates appear as pins on map. Tapping pin navigates to location detail.

---

### FEATURE: Add New Location (PIC)

**WHY:** Allow Kelurahan/PIC to document new Urban Farming/Taman Toga areas  
**USER:** PIC (authenticated)  
**INPUT:** Location name, type (Urban Farming / Taman Toga), RW, description, GPS coordinates, photo  
**OUTPUT:** New location record (status: PENDING_APPROVAL)  
**DEPENDENCY:** Firebase Auth, Firestore, Storage, GPS  
**TECHNICAL APPROACH:** Compose form, GPS capture via FusedLocationProviderClient, photo picker + upload  
**MVP?** YES  
**SCOPE LIMIT:** No background GPS tracking, no navigation, no geofencing. Single GPS capture only.  
**RISK:** MEDIUM — GPS accuracy varies, need retry mechanism  
**ACCEPTANCE CRITERIA:** PIC can create location with GPS. Location appears as pending. Admin can approve.

---

### FEATURE: Add/Edit Plant (PIC)

**WHY:** PIC maintains plant information for their locations  
**USER:** PIC (authenticated)  
**INPUT:** Plant name (ID, Latin, Mandarin, Pinyin), description, photos  
**OUTPUT:** New or updated plant record  
**DEPENDENCY:** Firebase Auth, Firestore, Storage  
**TECHNICAL APPROACH:** Compose form, photo picker + upload to Storage  
**MVP?** YES  
**SCOPE LIMIT:** PIC can only modify plants in their assigned locations  
**RISK:** LOW  
**ACCEPTANCE CRITERIA:** PIC adds plant, it appears in location. PIC edits plant, changes reflected. PIC cannot edit plants in other locations.

---

### FEATURE: QR Code Generation

**WHY:** Physical-digital bridge — visitors scan QR at plant to get info  
**USER:** PIC (initiates generation)  
**INPUT:** Select plant → "Generate QR"  
**OUTPUT:** QR code image (downloadable PNG)  
**DEPENDENCY:** ZXing library, stable plant ID, configured App Link domain  
**TECHNICAL APPROACH:** ZXing generates QR bitmap from URL, saved to device gallery  
**MVP?** YES  
**SCOPE LIMIT:** Client-side generation only. No batch generation. No label design.  
**RISK:** LOW  
**ACCEPTANCE CRITERIA:** Generated QR contains correct URL. QR is scannable and opens correct plant detail.

---

### FEATURE: App Links + Web Fallback

**WHY:** QR must work whether app is installed or not  
**USER:** Public (anyone scanning QR)  
**INPUT:** HTTPS URL from QR code  
**OUTPUT:** App opens to plant detail (if installed) OR web fallback page (if not)  
**DEPENDENCY:** Firebase Hosting (assetlinks.json + web pages), Android App Links config  
**TECHNICAL APPROACH:** Android App Links intent filter, static HTML fallback pages  
**MVP?** YES  
**SCOPE LIMIT:** Web fallback is read-only static page. No web app.  
**RISK:** MEDIUM — App Links verification requires correct hosting config and signing certificate fingerprint  
**ACCEPTANCE CRITERIA:** QR → app opens to correct plant. QR → web page shows plant info + APK download link.

---

### FEATURE: Mandarin Audio Playback

**WHY:** Educational value — users hear correct Mandarin pronunciation  
**USER:** Public (anyone viewing plant detail)  
**INPUT:** Tap 🔊 icon on plant detail  
**OUTPUT:** Audio plays once  
**DEPENDENCY:** Firebase Storage (audio files), Android MediaPlayer  
**TECHNICAL APPROACH:** Download or stream audio file from Storage URL, play via MediaPlayer  
**MVP?** SHOULD HAVE (depends on audio availability)  
**SCOPE LIMIT:** No autoplay, no looping, no live TTS. Audio is a pre-recorded file.  
**RISK:** LOW (technical). MEDIUM (content — audio files may not be ready)  
**ACCEPTANCE CRITERIA:** Tap 🔊 → audio plays once → stops. No audio file → button hidden.

---

### FEATURE: GPS Capture

**WHY:** Accurate location coordinates for map display  
**USER:** PIC (during Add/Edit Location)  
**INPUT:** PIC taps "Capture GPS" button  
**OUTPUT:** Latitude + longitude captured and displayed on mini-map preview  
**DEPENDENCY:** Android Location API (FusedLocationProviderClient), ACCESS_FINE_LOCATION permission  
**TECHNICAL APPROACH:** Request permission → single location fix → display on map → save  
**MVP?** YES  
**SCOPE LIMIT:** Single capture. No tracking, no background, no history.  
**RISK:** MEDIUM — permission handling complexity, GPS accuracy  
**ACCEPTANCE CRITERIA:** PIC captures GPS. Coordinates displayed on preview map. Coordinates saved with location.
