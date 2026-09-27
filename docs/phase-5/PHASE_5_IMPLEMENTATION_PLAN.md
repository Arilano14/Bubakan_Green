# BUBAKAN GREEN — PHASE 5 IMPLEMENTATION PLAN
**Product:** BUBAKAN GREEN  
**Sub-title:** Sistem Informasi Urban Farming & Taman Toga Kelurahan Bubakan  
**Phase:** Phase 5 — QR, App Links, Web Fallback & Field Integration  
**Governance:** Implementation Plan strictly prepared before code execution. Approval gate required: `ACC PHASE 5`.  
**Date:** 2026-09-27  

---

## 1. Phase 4 Completion Status

Phase 4 (PIC & Admin Management Implementation) is **100% COMPLETE & VERIFIED**:
- Administrative authentication and persistent session management are operational.
- Firestore security rules strictly enforce role isolation (Public, PIC, Admin) and append-only civic audit logs.
- Single-shot GPS acquisition records horizontal accuracy and enforces the $\le 25\text{m}$ quality gate.
- Garden plot registration, plant-to-plot junction mapping, and Admin approval/rejection queues are fully functional.
- Zero fake production data, zero premature Phase 5 implementation, and working tree clean with zero remote push.
- Full details documented in [`PHASE_4_COMPLETION_AUDIT.md`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/docs/phase-5/PHASE_4_COMPLETION_AUDIT.md) and [`PHASE_4_CONNECTOR_VERIFICATION.md`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/docs/phase-5/PHASE_4_CONNECTOR_VERIFICATION.md).

---

## 2. Phase 5 Objective

Connect verified real-world Bubakan botanical specimens and garden plots with the digital application ecosystem:
$$\text{REAL BUBAKAN PLANT / PLOT} \longrightarrow \text{STABLE ID} \longrightarrow \text{QR URL} \longrightarrow \text{EXTERNAL PHONE SCANNER} \longrightarrow \text{HTTPS DESTINATION} \longrightarrow \begin{cases} \text{APP LINK (if installed)} & \longrightarrow \text{Native Detail Screen} \\ \text{WEB FALLBACK (if not installed)} & \longrightarrow \text{Lightweight Web Page} \end{cases}$$

---

## 3. Scope

1. **Production QR Code Contract**: Standardized HTTPS routing contract for botanical species and garden plots.
2. **Deterministic QR Generation**: Automated generation of vector/high-resolution PNG QR matrices for verified records.
3. **Physical QR Label Specification**: Weatherproof, botanical-themed label layout incorporating verified Kelurahan Bubakan identity, quiet zones, and scannable visual contrast.
4. **Android App Links Integration**: Complete HTTPS intent filters with `android:autoVerify="true"` and `assetlinks.json` verification.
5. **Universal Deep-Link Routing**: In-app navigation parsing for `/plant/{plantId}` and `/location/{locationId}`, managing loading, success, not-found, and error UI states.
6. **Responsive Web Fallback**: Public web experience (`plant.html` and `index.html`) on Firebase Hosting delivering botanical identity, trilingual nomenclature, and app download CTA.
7. **Field Integration Protocol**: Systematic 15-step on-site validation procedure for physical label installation and multi-device camera testing.

---

## 4. Out-of-Scope

- Redesigning authentication, Firestore security architecture, or PIC/Admin workflows.
- Web Admin CMS, Web PIC dashboard, or Web user account management (Web remains strictly read-only fallback).
- In-app live camera barcode scanner (unnecessary bloat; modern Android and iOS devices feature native camera QR scanners).
- Paid cloud storage, third-party URL shorteners, or paid dynamic link redirect services.
- Automated bulk sticker printing machinery drivers (Phase 5 exports standard print-ready PDF/PNG templates).

---

## 5. QR Architecture

- **Payload Principle**: The physical QR code encodes **only** the stable canonical HTTPS URL.
- **Payload Anti-Pattern**: Zero raw JSON, base64 data, or embedded botanical descriptions inside the QR matrix. This guarantees minimum matrix density (Version 2–4), maximum physical scannability, and high damage tolerance.
- **Data Update Independence**:
  $$\text{Botanical Information Update in Database} \implies \text{\textbf{ZERO}} \text{ Physical Sticker Reprint}$$
  Physical QR stickers are permanent assets that reflect database updates instantly upon scan.

---

## 6. Stable ID Strategy

To prevent URL breakage and ensure readability:
1. **Botanical Species (MasterPlant)**:
   - Pattern: `pl-<kebab-case-indonesian-name>`
   - Example: `pl-jahe-merah`, `pl-kumis-kucing`, `pl-temulawak`
   - Stability: Permanent; tied to taxonomic species definition.
2. **Garden Plot (Location)**:
   - Pattern: `loc-<type>-<rw>-<slug>`
   - Example: `loc-toga-rw03-bersemi`, `loc-farm-rw01-makmur`
   - Stability: Permanent; tied to the physical civic plot boundary.

---

## 7. Approved QR Types

| QR Type | Target Entity | Canonical Path | Primary Destination Screen | Web Fallback Destination |
|---|---|---|---|---|
| **PLANT QR** | Botanical Species (`MasterPlant`) | `/plant/{stableId}` | `PlantDetailScreen.kt` | `/plant.html?id={stableId}` |
| **LOCATION QR** | Garden Plot (`Location`) | `/location/{stableId}` | `LocationDetailScreen.kt` | `/index.html?loc={stableId}` |

---

## 8. Canonical URL Strategy

- **Primary Canonical Domain**: `https://bubakangreen.web.app`
- **Secondary Domain (Future / Custom)**: `https://bubakangreen.app`
- **Path Standards**:
  - Species Encyclopedia: `https://bubakangreen.web.app/plant/pl-jahe-merah`
  - Plot Directory: `https://bubakangreen.web.app/location/loc-toga-rw03-bersemi`

---

## 9. Android App Links Specification

- **Manifest Declaration** in [`AndroidManifest.xml`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/app/src/main/AndroidManifest.xml):
  ```xml
  <intent-filter android:autoVerify="true">
      <action android:name="android.intent.action.VIEW" />
      <category android:name="android.intent.category.DEFAULT" />
      <category android:name="android.intent.category.BROWSABLE" />
      <data android:scheme="https" android:host="bubakangreen.web.app" android:pathPrefix="/plant/" />
      <data android:scheme="https" android:host="bubakangreen.web.app" android:pathPrefix="/location/" />
  </intent-filter>
  ```
- **Disambiguation Dialog Elimination**: `android:autoVerify="true"` instructs Android OS to verify domain ownership via Digital Asset Links. Once verified, scanning the QR opens Bubakan Green directly without prompting "Open with Chrome or Bubakan Green".

---

## 10. Digital Asset Links (`assetlinks.json`) Strategy

- **File Location**: Hosted on Firebase Hosting at `https://bubakangreen.web.app/.well-known/assetlinks.json`.
- **Payload Schema**:
  ```json
  [
    {
      "relation": ["delegate_permission/common.handle_all_urls"],
      "target": {
        "namespace": "android_app",
        "package_name": "id.bubakangreen.app",
        "sha256_cert_fingerprints": [
          "SHA256_DEBUG_KEYSTORE_FINGERPRINT",
          "SHA256_RELEASE_KEYSTORE_FINGERPRINT"
        ]
      }
    }
  ]
  ```
- **Deployment Process**: Deployed alongside Firebase Hosting assets; verified using Google Digital Asset Links tester.

---

## 11. Web Fallback Architecture

- **Role**: Lightweight, instant-loading botanical viewer for users who do not have the native Android app installed (including iOS users and desktop visitors).
- **Core Content Displayed**:
  - Bubakan Green header with Kelurahan Bubakan civic identity.
  - High-resolution botanical photography.
  - Trilingual botanical nomenclature (Indonesian Common, Latin Italic, Mandarin Hanzi & Pinyin).
  - Medicinal properties and herbal usage breakdown.
  - Call-to-Action (CTA): *"📲 Buka di Aplikasi BUBAKAN GREEN"* (routes to Google Play / direct APK download).
- **Technology**: Vanilla HTML5 + CSS3 (`style.css`), zero heavy JavaScript frameworks, zero external tracking scripts, $<50\text{KB}$ total payload for near-instant rendering on 3G mobile connections.

---

## 12. In-App Deep Link Routing & Navigation Flow

- **Entry Point**: `MainActivity.kt` $\longrightarrow$ `BubakanAppNavHost.kt`.
- **Route Matching**:
  - `Screen.PlantDetail.route = "plant_detail/{plantId}"`
  - `Screen.LocationDetail.route = "location_detail/{locationId}"`
- **State Handling**:
  - `UiState.Loading`: Renders `ShimmerBox` placeholders while fetching document from Firestore/cache.
  - `UiState.Success`: Renders botanical card or garden plot overview.
  - `UiState.Empty`: Renders `StateEmptyView` ("Tanaman Tidak Ditemukan" or "Lokasi Belum Terbit").
  - `UiState.Error`: Renders `StateErrorView` with retry button.
- **Backstack Safety**: `onNavigateBack` pops the backstack; if opened cold from external scanner (empty backstack), automatically falls back to `Screen.Home` to preserve normal app exploration.

---

## 13. Firestore Data Dependency

QR codes resolve records from two collections in Cloud Firestore:
1. `/master_plants/{plantId}`:
   - Must have `nameId`, `nameLatin`, `description`.
   - Optional: `nameMandarin`, `pinyin`, `primaryPhotoUrl`, `mandarinAudioUrl`.
2. `/locations/{locationId}`:
   - Must have `name`, `type`, `rw`, `address`, `status == 'PUBLISHED'`.
   - Security rule enforces that unauthenticated public scans cannot view locations in `DRAFT` or `PENDING_APPROVAL` status.

---

## 14. Production Data Dependency & Verification Workflow

Production QR codes must strictly follow the data dependency pipeline:
$$\text{FIELD INVENTORY} \longrightarrow \text{BOTANICAL VALIDATION} \longrightarrow \text{ADMIN APPROVAL} \longrightarrow \text{ASSIGN STABLE ID} \longrightarrow \text{GENERATE QR} \longrightarrow \text{FIELD DEPLOYMENT}$$

Every record is explicitly categorized:
- `DOCUMENT-VERIFIED`: Confirmed against authoritative botanical catalog.
- `FIELD-VERIFIED`: Inspected and verified on physical ground in Bubakan.
- `NEEDS-FIELD-VALIDATION`: Plot or specimen pending physical boundary / species confirmation.
- `TEST_ONLY`: Mock development fixtures barred from production printing.

---

## 15. QR Generation Approach

- **Generation Engine**: Standards-compliant QR matrix generation (using `qrcode` or Python `qrcode` utility during label compilation).
- **Parameters**:
  - Error Correction: Level M (15% redundancy) or Level Q (25% redundancy) to withstand minor physical abrasion or outdoor weathering.
  - Resolution: Minimum $1000 \times 1000\text{px}$ export for crisp 300 DPI print fidelity.
  - Clean Quiet Zone: Exactly 4 module widths of clean whitespace around the data matrix.

---

## 16. Physical QR Label Specification

- **Dimensions**: $60\text{mm} \times 90\text{mm}$ (portrait card) or $70\text{mm} \times 70\text{mm}$ (square stake).
- **Visual Design (Planta-Inspired, Bubakan-Owned)**:
  - Header: `BUBAKAN GREEN` (14pt Bold, `PrimaryForest` `#1B4332`).
  - Subheader: `Taman Toga & Urban Farming Kelurahan Bubakan` (10pt Regular, `SecondarySage` `#40916C`).
  - Center: High-contrast black QR matrix on pure white card ($45\text{mm} \times 45\text{mm}$).
  - Specimen Label: Indonesian Common Name (14pt Bold) + Botanical Latin (11pt Italic).
  - Micro-Instruction: *"Scan menggunakan kamera ponsel untuk informasi tanaman & khasiat"* (9pt Muted).
- **Substrate**: Waterproof outdoor matte vinyl sticker with UV-protective lamination mounted on weather-resistant acrylic / aluminium stakes.

---

## 17. Security Architecture

1. **Authorization Protection**: QR URLs identify public resources. Scanning a QR grants **zero** elevated permissions, zero admin tokens, and zero access to unpublished records.
2. **Access Control**: Public Firestore rules prevent retrieval of unpublished (`PENDING_APPROVAL` or `DRAFT`) plots.
3. **Data Sanitization**: Stable IDs are strictly alphanumeric slugs with hyphens (`[a-z0-9-]+`); prevents path traversal or injection attacks.

---

## 18. Offline Behavior

- **Native App**:
  - If user previously opened the app or synced catalog, Firestore offline persistence serves the botanical record instantly from local disk cache.
  - If completely offline and uncached, `OfflineStatusBar` notifies the user and displays cached partial information or an offline retry prompt.
- **Web Fallback**:
  - Requires standard browser connectivity; if offline, browser displays standard network connection error without corrupting cached state.

---

## 19. Performance Standards

| Phase 5 Interaction | Performance Target | Measurement Strategy |
|---|---|---|
| **App Link Handoff to Native App** | $\le 300\text{ms}$ (UI feedback) | Measure from Intent dispatch to Activity compose first frame. |
| **Deep Link In-App Navigation** | $\le 100\text{ms}$ | Measure NavHost route resolution to `PlantDetailScreen` composition. |
| **Local Cache Render** | $\le 200\text{ms}$ | Document load from Room/Firestore disk cache. |
| **First Contentful Paint (Web Fallback)** | $\le 800\text{ms}$ on 4G | Vanilla HTML/CSS on Firebase CDN without blocking scripts. |
| **Network Document Fetch** | Separately measured | Network latency ($200 - 600\text{ms}$) reported transparently. |

---

## 20. Responsive Web Behavior

The web fallback (`plant.html` and `index.html`) is audited across four breakpoints:
- **Mobile Narrow ($320\text{px} - 375\text{px}$)**: Single-column botanical card, full-width photo, text wrap without overflow.
- **Standard Mobile ($390\text{px} - 430\text{px}$)**: Optimal readability, $16\text{px}$ body font, $48\text{px}$ touch targets.
- **Tablet ($768\text{px} - 1024\text{px}$)**: Centered container ($\max 680\text{px}$), balanced whitespace.
- **Desktop ($>1024\text{px}$)**: Elegant standalone botanical card with subtle border and QR verification context.

---

## 21. Field Testing Procedure

Governed strictly by [`FIELD_QR_TEST_PROTOCOL.md`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/docs/phase-5/FIELD_QR_TEST_PROTOCOL.md):
- Physical site identification $\longrightarrow$ Specimen identity check $\longrightarrow$ Digital record validation $\longrightarrow$ Label printing $\longrightarrow$ Physical attachment $\longrightarrow$ Multi-device camera scan $\longrightarrow$ Native App Link test $\longrightarrow$ Web fallback test $\longrightarrow$ Logging into [`FIELD_QR_TEST_RESULTS.md`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/docs/phase-5/FIELD_QR_TEST_RESULTS.md).

---

## 22. Acceptance Criteria

1. [ ] Canonical QR URLs resolve to valid HTTP 200 endpoints on `bubakangreen.web.app`.
2. [ ] Scanning QR with external camera on a phone with Bubakan Green installed seamlessly opens `PlantDetailScreen` or `LocationDetailScreen` without disambiguation dialog.
3. [ ] Scanning QR on a phone without the app installed opens the lightweight, responsive botanical web fallback.
4. [ ] `assetlinks.json` is properly formatted, deployed, and validated against the app package and signing key.
5. [ ] Back navigation from deep-linked screens smoothly leads to the main public catalog/home without crashing.
6. [ ] Zero fake production data used in final QR deployment.
7. [ ] Physical scan tests verified on at least two distinct mobile devices in real outdoor lighting conditions.
8. [ ] Zero git pushes executed.

---

## 23. Risks & Mitigations

| Risk | Impact | Mitigation |
|---|---|---|
| **Outdoor Weathering of Stickers** | QR unreadable after rain/sun | Use UV-resistant outdoor vinyl with matte laminate; error correction Level Q. |
| **Android OS Domain Verification Delay** | Prompts browser choice dialog | Ensure `android:autoVerify="true"` and properly formatted `assetlinks.json` on HTTPS. |
| **Weak Cellular Signal at Garden Plot** | Slow document load | Leverage Firestore local persistence; web fallback $<50\text{KB}$ payload. |
| **Changed Botanical Nomenclature** | Discrepancy with physical plant | Database update updates digital display instantly — **zero QR reprint needed**. |

---

## 24. Dependencies

- **Firebase Hosting**: For serving `assetlinks.json`, `plant.html`, and `index.html`.
- **Cloud Firestore**: For authoritative botanical and location collections.
- **Android Gradle Plugin & SDK**: For building and signing `id.bubakangreen.app`.
- **Physical Field Coordination**: Kelurahan Bubakan & RW agricultural cadres for physical sticker placement.

---

## 25. Cost & Free-Tier Impact

- **Firebase Hosting**: Free tier includes 10 GB storage and 360 MB/day transfer (Web fallback uses $<50\text{KB}$ per scan $\implies >7,000$ scans/day completely free).
- **Firebase Firestore**: Free tier includes 50,000 reads/day.
- **Paid Storage / Services**: **ZERO**. No paid cloud storage, no third-party paid APIs.
- **Total Project Cost**: **Rp 0,- (Zero Cost)**.

---

## 26. Expected Files to Modify / Create

1. `web/public/.well-known/assetlinks.json` (Populate production SHA-256 fingerprint).
2. `web/public/plant.html` (Polish trilingual layout & dynamic Firestore client-side fetch).
3. `web/public/index.html` (Polish location fallback & app install CTA).
4. `web/public/style.css` (Palette Alam Bubakan tokens).
5. `app/src/main/AndroidManifest.xml` (Ensure autoVerify intent filters are complete).
6. `app/src/main/java/id/bubakangreen/app/navigation/BubakanNavHost.kt` (Verify deep link URI patterns and backstack resilience).
7. Label templates & vector QR assets in `web/public/labels/` or `docs/phase-5/assets/`.

---

## 27. Explicitly Rejected Features

- ❌ In-app camera barcode scanner (Redundant; modern smartphone camera apps handle this natively).
- ❌ Third-party URL shorteners like bit.ly / tinyurl (Security risk, URL fragility, unbranded).
- ❌ Firebase Dynamic Links (Deprecated by Google; replaced by standard Android App Links).
- ❌ Embedding entire plant descriptions inside QR raw text (Causes dense, unscannable QR codes).
- ❌ Web Admin CMS / Web PIC Login (Out of scope; administrative work is strictly native Android).
