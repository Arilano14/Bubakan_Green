# BUBAKAN GREEN — WEBSITE REDESIGN ARCHITECTURAL PLAN
**Document Version:** 1.0.0  
**Status:** DRAFT (Awaiting Approval: `ACC WEBSITE REDESIGN`)  
**Target Systems:** `web/public/` (Hosting: `https://bubakan-green.web.app`), Cloud Firestore (`bubakan-green`)  
**Target Audience:** Kelurahan Bubakan Residents, Visitors, Urban Farming Practitioners, Botanical QR Scanners  

---

## 1. Executive Summary & Role Definition

As a collaborative multidisciplinary effort across **Senior Product Design, UX Engineering, Web Frontend Engineering, Firebase Integration, and Performance & QA Engineering**, this plan establishes the complete blueprint for transforming the existing Bubakan Green web fallback into a **high-impact, product-oriented landing page and streamlined QR plant fun-fact portal**.

### Core Product Positioning
- **The Android Mobile Application is the Primary Product:** The web surface is **NOT** a standalone mega-portal or a generic municipal website.
- **The Website Serves Five Precise Roles:**
  1. **Smart QR Fallback:** Instant, friction-free plant fun-fact discovery when physical garden labels are scanned.
  2. **Product Introduction:** Crisp, compelling storytelling introducing the Bubakan Green app and its capabilities.
  3. **App Visual Showcase:** Authentic device mockups presenting the real Android application UI.
  4. **Direct APK Installation Gateway:** Direct local APK distribution with explicit, trustworthy sideloading instructions (zero fake Play Store links).
  5. **Lightweight Public Landing Page:** Ultra-fast, responsive web presence representing Kelurahan Bubakan's Urban Farming & Taman Toga initiatives.

---

## 2. Melolo UX Reference vs. Bubakan Green Identity (Anti-Clone Philosophy)

We adopt [Melolo.org](https://www.melolo.org/) strictly as a **UX structural and storytelling benchmark**, deliberately rejecting any visual, textual, or aesthetic cloning.

| UX Dimension | Melolo Reference Principle | Bubakan Green Execution |
| :--- | :--- | :--- |
| **Hero Structure** | High-contrast headline, concise subhead, dual action CTA, preview hero visual | **Original Bubakan Botanical Identity:** `#1B4332` Forest Green, warm cream `#FBFDF9`, welcoming Bubakan mascot greeting, authentic Android device mockup centerpiece. |
| **Product Showcase** | Multi-screen application preview highlighting key user flows | **3-Device Composition:** Authentic app mockups (`image 3.png`, `image 4.png`, `image 5.png`) featuring Jelajah Kebun, Perpustakaan Botani, and Beranda. |
| **Value Propositions** | Numbered, compact feature blocks with clear visual hierarchy | **4 Numbered Cards (`01`–`04`):** Kenali Tanaman, Jelajahi Kebun, Scan QR, Terhubung dengan Bubakan. Concise, non-hyperbolic Indonesian copy. |
| **Conversion CTA** | Single-minded download invitation before the footer | **Mascot-Driven APK Gateway:** High-contrast installation card with 5-step Android sideloading guide and direct download button. |
| **Footer** | Clean information architecture with verified credentials | **Verified Public Kelurahan Data:** Official city geospatial contacts + clear, separate KKN GIAT 17 UNNES collaboration credits. |

---

## 3. Audited Assets Specification

### 3.1 Android Application Mockups (`app/src/main/res/drawable-nodpi/Mockup/`)
The original Android assets have been audited and will remain completely unmodified in the Android codebase. Optimized web versions will be generated in `web/public/assets/mockups/`.

| File Name | Dimensions | File Size | Aspect Ratio | Depicted App Screen | Recommended Web Format |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `image 3.png` | 393 × 831 px | 159.5 KB | 0.4729 (9:19) | **Lokasi Screen:** "Jelajah Kebun", RW garden cards, GPS direction buttons. | `mockup-location.webp` (~55 KB) |
| `image 4.png` | 400 × 831 px | 229.1 KB | 0.4813 (9:19) | **Katalog Screen:** "Perpustakaan Botani", botanical search, plant species cards. | `mockup-catalog.webp` (~65 KB) |
| `image 5.png` | 393 × 831 px | 161.3 KB | 0.4729 (9:19) | **Beranda Screen:** "Hai, teman Bubakan!", featured banner, quick actions, mascot. | `mockup-home.webp` (~60 KB) |

**Responsive Layout Architecture for Mockups:**
- **Desktop (≥ 1024px):** 3-device staggered composition. `mockup-home.webp` (image 5) takes center stage with elevated z-index, slight scale (1.04), and subtle elevation shadow. `mockup-location.webp` (left) and `mockup-catalog.webp` (right) are flanking with subtle perspective or staggered vertical offset (-16px).
- **Tablet (768px – 1023px):** Compact 3-device spread or 2-device focal spread.
- **Mobile (≤ 767px):** Single featured centerpiece (`mockup-home.webp`) with horizontal swipe peek or clean stacked reveal. No cramped 3-device squishing.

---

### 3.2 Canonical Brand Logo (`app/src/main/res/drawable-nodpi/new_logo.png`)
- **Original Asset:** 1994 × 1994 px RGBA PNG, 2,986 KB (~2.9 MB). Features circular green emblem with leaf accents, official typography `#BUBAKAN GREEN`, and the central mascot figure.
- **Web Migration Strategy:**
  - Export optimized web version to `web/public/assets/brand/new-logo.webp` (512 × 512 px, ~35 KB) and PNG fallback `new-logo.png` (512 × 512 px, ~48 KB).
  - Generate crisp 64 × 64 px and 32 × 32 px `favicon.png` and `favicon.ico`.
  - **Decommissioning Old Assets:** `web/public/mascot.png` (which was incorrectly acting as the header logo) will be decommissioned from logo duties.
  - **Verification:** All references to old logo across `index.html`, `plant.html`, and `location.html` will be reduced to exactly **ZERO**.

---

### 3.3 Bubakan Green Mascot Suite (`app/src/main/res/drawable-nodpi/mascot_*.png`)
Mascot assets audited and designated for intentional, art-directed web placements:

| Mascot Asset | Dimensions & Size | Assigned Web Role | Justification & Placement Rules |
| :--- | :--- | :--- | :--- |
| `mascot_greeting.png` | 1706 × 2293 px (358 KB) | **Hero Section Greeting** | Placed adjacent to hero headline / mockup preview. Conveys welcoming, friendly digital guide. |
| `mascot_learning.png` | 1728 × 2255 px (356 KB) | **About Buba-Green** | Illustrates botanical education and community knowledge sharing. |
| `mascot_pointing.png` | 1039 × 1223 px (160 KB) | **QR Fun Fact Badge / Why Us** | Guides visitor attention to the fun-fact card or value propositions. |
| `mascot_happy.png` | 3074 × 3074 px (605 KB) | **Download CTA Section** | Celebrates app discovery; positioned alongside installation steps. |
| `mascot_default.png` | 1443 × 2136 px (307 KB) | **Footer Brand Accent** | Small, subtle 48px accent next to copyright / kelurahan credits. |

**Anti-AI-Slop Mascot Rules:**
- Mascots must **never** overlap readable text, primary buttons, or plant photos.
- Maximum of **one** prominent mascot per viewport.
- All decorative mascots will have empty `alt=""` tags to preserve screen-reader accessibility.

---

## 4. Schema Audit & Fun-Fact Strategy

### 4.1 Schema Audit Findings
Inspection of `id.bubakangreen.app.domain.model.MasterPlant` and active Cloud Firestore documents in `/master_plants` reveals:
- **Existing Fields:** `id`, `nameId`, `nameLatin`, `nameMandarin`, `pinyin`, `description`, `characteristics`, `commonUses`, `cultivationNotes`, `benefits`, `plantingGuide`, `primaryPhotoUrl`, `isPublished`.
- **Finding:** There is currently **NO dedicated `funFact` field** in the Firestore schema or Kotlin model.
- **Suitability of Existing Fields:**
  - `description` contains comprehensive multi-paragraph botanical descriptions (too lengthy for instant QR reading).
  - `benefits` contains herbal pharmacology details (valuable, but not a concise, snappy "fun fact").

### 4.2 Proposed Schema Extension
We propose the smallest, non-breaking schema extension:
```typescript
// Proposed optional field in master_plants/{plantId}
funFact?: string; // Optional concise trivia, e.g. "Sereh mengandung minyak sitronela alami yang ampuh mengusir nyamuk tanpa bahan kimia!"
```
In Android `MasterPlant.kt`:
```kotlin
val funFact: String? = null
```

### 4.3 Client-Side Graceful Fallback Strategy (Immediate Readiness)
- When `/plant/{plantId}` loads, the web client checks `fields.funFact?.stringValue`.
- If `funFact` is present: renders the snappy fun fact card prominently.
- If `funFact` is absent: **DO NOT invent fake trivia**. The web client displays a clean fallback:
  > *"Fakta tanaman belum tersedia."*  
  or gracefully displays the concise summary from `benefits` if available, ensuring zero broken layouts.
- **Dynamic Live Sync:** Updating `master_plants/{plantId}` in Firestore automatically updates the QR page on next page load without editing HTML or redeploying hosting.

---

## 5. QR Plant Page Experience (`/plant/{plantId}`)

### 5.1 The QR Experience Contract
When a visitor scans a physical QR label in a Bubakan garden (e.g., `https://bubakangreen.web.app/plant/tomat`):
1. **Focus Exclusively on Scanned Plant:** Immediate gratification. The visitor wants to know *what plant this is* and *an interesting fact about it*.
2. **Above-the-Fold Priority:**
   - [ Bubakan Green Official Logo ]
   - Badge: `FUN FACT TANAMAN`
   - Plant Name: e.g. **TOMAT** *(Solanum lycopersicum)*
   - High-Quality Plant Image (from `primaryPhotoUrl`)
   - Fun Fact Card: Snappy, intriguing trivia
   - Small Mandarin Badge: Karakter + Pinyin (e.g. 番茄 · fān qié)
   - Secondary Benefit Card (concise herbal use)
   - Unobtrusive CTA Banner: *"Kenali lebih banyak tanaman & audio pelafalan di aplikasi"* -> `[ INSTALL BUBAKAN GREEN ]`
3. **No Overwhelming Clutter:** Do not render the entire homepage or corporate sections above the plant data.

---

## 6. Local APK Distribution & Installation UX

### 6.1 Distribution Strategy (No Fake App Stores)
- **Current Reality:** Google Play Store deployment is scheduled for a later milestone.
- **Rule:** **Zero fake Play Store buttons or badges**.
- **Distribution Path:** Static local hosting at `web/public/downloads/bubakan-green.apk`.
- **Dynamic Availability Detection:**
  ```javascript
  // Lightweight HEAD request to verify APK existence before triggering download
  fetch('/downloads/bubakan-green.apk', { method: 'HEAD' })
    .then(res => {
      if (res.ok) { enableDownloadButton(); }
      else { showDevelopmentNotice(); }
    });
  ```
- **If APK exists:** Downloads `bubakan-green.apk` directly on click.
- **If APK is absent:** Displays clean notification: *"Berkas APK rilis sedang dalam tahap verifikasi keamanan. Silakan kunjungi kembali segera."*

### 6.2 5-Step Sideloading Guidance
Clear, transparent Indonesian instructions explaining that sideloading is required:
1. **Unduh Berkas APK:** Klik tombol *Install Bubakan Green*.
2. **Buka Berkas:** Buka file `.apk` melalui panel notifikasi atau folder *Download*.
3. **Izinkan Sumber:** Jika Android meminta konfirmasi keamanan, pilih **Setelan → Izinkan dari sumber ini**.
4. **Selesaikan Instalasi:** Ketuk tombol **Instal**.
5. **Jelajahi Bubakan Green:** Buka aplikasi dan mulai eksplorasi kebun.

---

## 7. Web Architecture & Performance Budget

### 7.1 Tech Stack
- **Structure:** Pure Semantic HTML5 (`index.html`, `plant.html`, `location.html`).
- **Styling:** Modular Vanilla CSS (`style.css`), CSS Custom Properties design system, modern Flexbox & CSS Grid.
- **Logic:** Native Vanilla JavaScript (ES6+), zero external runtime dependencies.
- **Data Integration:** Firestore REST API (`v1/projects/bubakan-green/databases/(default)/documents/...`).
- **Routing:** Handled via Firebase Hosting rewrites (`web/firebase.json`).
- **App Links:** `.well-known/assetlinks.json` strictly preserved.

### 7.2 Performance Target Budget
| Resource | Target Size | Hard Limit | Optimization Method |
| :--- | :--- | :--- | :--- |
| **HTML** | < 15 KB | 30 KB | Semantic, clean markup, inline SVG icons for social/platform |
| **CSS** | < 20 KB | 40 KB | Single unified design system, minimal nesting, pure CSS |
| **JavaScript** | < 8 KB | 15 KB | Vanilla Fetch, zero framework overhead |
| **Images (Total Hero)**| < 150 KB | 250 KB | WebP compression, responsive srcset, lazy-loading |
| **Total Page Weight** | **< 200 KB** | **350 KB** | Ultra-lightweight fallback for 3G/4G field conditions |

---

## 8. Safety of Android App Links & Route Contracts

The redesign strictly maintains the established deep link contracts:
- `https://bubakangreen.web.app/plant/{plantId}`:
  - **Android App Installed:** Triggers Android App Link -> Native `PlantDetailScreen` inside the app.
  - **Android App Not Installed / Browser:** Falls back gracefully to `web/public/plant.html`.
- `https://bubakangreen.web.app/location/{locationId}`:
  - **Android App Installed:** Triggers Android App Link -> Native `LocationDetailScreen`.
  - **Browser:** Falls back gracefully to `web/public/location.html`.
- `/.well-known/assetlinks.json`: Preserved with active SHA-256 fingerprints.

---

## 9. Next Steps in Execution Plan

1. **Step 4:** `docs/web/WEBSITE_REDESIGN_PLAN.md` (This document).
2. **Step 5:** `docs/web/WEBSITE_CONTENT_MATRIX.md` (Complete copywriting, verified contacts, bilingual data).
3. **Step 6:** `docs/web/WEBSITE_RESPONSIVE_QC.md` (Breakpoint strategy, a11y, anti-AI-slop test matrix).
4. **Step 7:** **STOP and await explicit user approval:** `ACC WEBSITE REDESIGN`.
