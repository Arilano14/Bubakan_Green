# BUBAKAN GREEN — WEBSITE RESPONSIVE STRATEGY & QA MATRIX
**Document Version:** 1.0.0  
**Status:** DRAFT (Awaiting Approval: `ACC WEBSITE REDESIGN`)  
**Target Breakpoints:** 320px, 360px, 375px, 393px, 412px, 430px, 768px, 1024px, 1280px, 1440px+  
**Target Browsers:** Mobile Chrome (Android), Mobile Safari (iOS), Desktop Chrome, Edge, Firefox  

---

## 1. Responsive Breakpoint Specification

The layout employs a mobile-first, fluid CSS architecture utilizing `clamp()`, flexible grids, and media queries to ensure pixel-perfect rendering across all device tiers without horizontal scrolling or text clipping.

| Breakpoint Tier | Viewport Width | Typical Hardware Target | Layout & Presentation Strategy |
| :--- | :--- | :--- | :--- |
| **Mobile Compact** | `320px – 359px` | Galaxy A01, iPhone SE (1st gen) | Single column, tight padding (12px), single mockup centered (max 260px wide), full-width CTA buttons (min height 48px), stacked nav/footer. |
| **Mobile Standard** | `360px – 392px` | Galaxy S8/S9, Redmi 9A | Single column, 16px horizontal margins, primary mockup featured (`image 5`), cards stack vertically with 12px gaps. |
| **Mobile Modern** | `393px – 430px` | Pixel 7, iPhone 14/15/16 Pro | Fluid card padding (20px), primary mockup centerpiece with subtle side hints or stacked layout, touch targets ≥ 48px. |
| **Tablet Portrait** | `768px – 1023px` | iPad Mini, Galaxy Tab A | 2-column hero (copy left, 2-device mockup right), 2×2 grid for *Why Choose Us*, 2-column footer layout. |
| **Desktop / Laptop**| `1024px – 1279px`| MacBook Air, ThinkPad 14" | 3-device mockup composition with center elevation (`image 5` front, `image 3` left, `image 4` right), 4-column value proposition row, max container width 1140px. |
| **Wide Desktop** | `1280px – 1440px+`| 24"–27" Monitors, 4K Screens | Fully art-directed layout, spacious whitespace, max container width 1200px, subtle elevation shadows. |

---

## 2. Component-by-Component Responsive Matrix

### 2.1 Navigation Bar
- **Mobile (< 768px):** Compact height (60px), logo + wordmark left, single prominent CTA button (`Pasang`) right. Menu links hidden or accessible without blocking screen.
- **Tablet / Desktop (≥ 768px):** Height 72px, sticky with backdrop-filter glassmorphism (`rgba(255,255,255,0.85)` + `blur(12px)`), logo left, centered nav items (`Tentang`, `Fitur`, `Mengapa Kami`), high-contrast pill CTA right.

---

### 2.2 Hero Section & 3-Device Mockup Composition
- **Mobile (< 768px):**
  - Text and badge stacked vertically.
  - Mascot (`mascot_greeting.png`) appears as a welcoming 72px–90px companion badge beside the headline, never obscuring text.
  - **Single Featured Mockup:** Displays `image 5` (Beranda) in a realistic framed container (max-width 280px–320px, height auto, aspect ratio preserved 0.473).
  - Secondary screens (`image 3`, `image 4`) are optionally revealed in a subtle horizontal snap-carousel or presented in the dedicated Showcase section.
- **Desktop (≥ 1024px):**
  - Two-column hero container: Left column contains headline, subheadline, dual action buttons, and mascot. Right column contains the **3-Device Composition**:
    - **Center (`image 5` - Beranda):** Scaled 1.05, `z-index: 3`, realistic shadow `0 20px 40px rgba(27,67,50,0.15)`.
    - **Left (`image 3` - Lokasi):** Offset `-40px`, rotated `-4deg` or staggered down by 24px, `z-index: 2`, opacity 0.95.
    - **Right (`image 4` - Katalog):** Offset `+40px`, rotated `+4deg` or staggered down by 24px, `z-index: 2`, opacity 0.95.
  - Preserves exact aspect ratio (no image distortion).

---

### 2.3 About Buba-Green Section
- **Mobile (< 768px):** Single column. Mascot (`mascot_learning.png`) floats above or alongside the summary text (max width 120px). The 5 resident pillars render as clean vertical list items with botanical icons.
- **Desktop (≥ 768px):** Asymmetric editorial layout. Left side features large typography, mascot, and mission statement; right side displays the 5 resident value points in structured cards.

---

### 2.4 Why Choose Us (`01`–`04`)
- **Mobile (< 768px):** Stacked vertically (`01`, `02`, `03`, `04`). Each card has clear number badge, bold title, and 2-sentence description.
- **Tablet (768px – 1023px):** 2×2 grid layout.
- **Desktop (≥ 1024px):** 4-column horizontal card row with uniform card heights and subtle hover elevation.

---

### 2.5 Download APK Section & Sideloading Guidance
- **Mobile (< 768px):** Deep forest green card spanning full mobile width. Mascot (`mascot_happy.png`) placed at top or bottom (100px). Primary download button spans full width (min height 52px). Sideloading steps listed 1–5 in clean legible typography.
- **Desktop (≥ 1024px):** Two-column card. Left: Title, value statement, download button, and version meta. Right: Numbered 5-step sideloading guide with visual step pills.

---

### 2.6 Plant QR Experience (`/plant/{plantId}`)
- **Mobile-First Priority:**
  - Viewport optimized so that **Plant Name, Image, and Fun Fact** are completely visible within the initial 1.5 screen scrolls.
  - Image container uses `aspect-ratio: 16/10` with `object-fit: cover` and border-radius `16px`.
  - Fun fact card is highlighted with a gold/leaf accent border (`#40916C` or subtle gradient) to signal discovery.
  - Bottom sticky or fluid CTA card invites app installation without obstructing reading.
- **Desktop:** Centered reading card (max-width 680px) with elegant botanical framing.

---

### 2.7 Footer & Contact Columns
- **Mobile (< 768px):** Stacked vertically: Brand info → Kelurahan Bubakan verified contacts → KKN GIAT 17 collaboration credits → Social icon row.
- **Desktop (≥ 768px):** 3-column structured footer:
  - Column 1: Brand & Kelurahan Bubakan summary + small mascot.
  - Column 2: Official Kelurahan contacts (phone, email, website, address).
  - Column 3: Tim KKN GIAT 17 collaboration credits + social links (Instagram, TikTok, Email).

---

## 3. Usability & UX Laws Compliance

1. **Fitts's Law (Touch Targets):**
   - All interactive elements (CTA buttons, nav anchors, social icons, download links) have an active hit target of **at least 48 × 48 px**.
   - Primary mobile buttons have full-width affordance with generous vertical padding (14px–16px).
2. **Hick's Law (Decision Reduction):**
   - The primary CTA is unmistakable: `Install Bubakan Green`.
   - QR plant page eliminates extraneous menus, leaving only the plant facts and direct app installation gateway.
3. **Jakob's Law (Familiar Mental Models):**
   - Follows standard mobile web conventions: Logo top-left, primary action top-right/hero, clear scroll anchors.
   - Sideloading steps use familiar Android system settings vocabulary (*"Setelan → Izinkan sumber ini"*).
4. **Gestalt Principles (Proximity & Grouping):**
   - Botanical metadata (Latin name, Mandarin, Pinyin) grouped in close proximity to the plant title.
   - Distinct background surface colors separate the educational sections from the dark forest download card.

---

## 4. Accessibility (a11y) & WCAG AA Checklist

- [ ] **Color Contrast:** Forest Green (`#1B4332`) on White/Cream background achieves **11.4:1** contrast ratio (far exceeding WCAG AA requirement of 4.5:1).
- [ ] **Typography Scale:** Body text is at minimum 15px (`0.938rem`), line-height 1.65 for high legibility.
- [ ] **Alt Text Hygiene:**
  - Real botanical photos have descriptive alt text (`alt="Foto tanaman Tomat di Kebun Bubakan"`).
  - Decorative mascot illustrations have empty `alt=""` and `aria-hidden="true"`.
- [ ] **Keyboard Navigation:** All interactive elements feature visible, high-contrast `:focus-visible` outline rings (`2px solid #40916C`, offset `2px`).
- [ ] **Semantic Elements:** Page uses `<header>`, `<nav>`, `<main>`, `<section>`, `<article>`, and `<footer>` rather than generic `<div>` soup.

---

## 5. Anti-AI-Slop Quality Control Checklist

The implementation must pass this strict rejection gate before being accepted:

- [ ] **NO Endless Generic Cards:** The layout uses varied editorial rhythm, large typography headers, device mockups, and structured lists, NOT uniform rounded rectangles repeated indefinitely.
- [ ] **NO Excessive Gradients or Random Blobs:** Colors are grounded in authentic botanical tones (`#1B4332`, `#40916C`, `#D8F3DC`, `#FBFDF9`). No neon purple/cyan floating blur blobs.
- [ ] **NO Mascot Occlusion:** Mascot illustrations are positioned in dedicated whitespace and never cover text, buttons, or photo subjects.
- [ ] **NO Fabricated Claims:** Zero instances of "AI-powered", "revolutionary", "terbaik di Indonesia", or fake marketing jargon.
- [ ] **NO Broken or Fake CTAs:** Zero fake "Google Play" badges or dead link anchors. Direct link to `/downloads/bubakan-green.apk`.
- [ ] **NO Shrunken Desktop Layout on Mobile:** Mobile layout is purpose-designed with vertical stacking and touch-first spacing, not a desktop layout scaled down with CSS zoom.

---

## 6. Comprehensive QA Verification Test Matrix

### A. Homepage Verification (`/`)
- [ ] `GET /` returns 200 OK.
- [ ] Official logo (`new-logo.webp`) renders crisply at top left.
- [ ] Zero references to old `mascot.png` as a logo.
- [ ] Hero headline and subhead render without text overflow.
- [ ] Dual CTAs work: `Install Bubakan Green` links to `#download`, `Lihat Cara Kerja` scrolls to `#about`.
- [ ] 3 Android mockups (`image 3`, `image 4`, `image 5`) render without distortion or clipped corners.
- [ ] Section *About Buba-Green* renders with 5 resident pillars and `mascot_learning`.
- [ ] Section *Why Choose Us* renders 4 numbered cards (`01`–`04`).
- [ ] Download section renders with APK download CTA and 5 sideloading steps.
- [ ] Footer renders verified Kelurahan Bubakan contact details and KKN GIAT 17 credits.

### B. Dynamic QR Plant Page (`/plant/{plantId}`)
- [ ] `/plant/tomat` loads live Firestore document `master_plants/tomat`.
- [ ] Plant title renders `Tomat`, Latin name renders `Solanum lycopersicum`.
- [ ] Mandarin name and Pinyin render properly if populated.
- [ ] Plant image loads from `primaryPhotoUrl`.
- [ ] Fun fact card renders:
  - If `funFact` exists: renders live trivia text.
  - If `funFact` missing: renders graceful fallback *"Fakta tanaman belum tersedia."* without crashing.
- [ ] `/plant/unknown_plant_id` returns clean Not Found state (`"Tanaman Tidak Ditemukan"`).
- [ ] Offline / Firestore network failure triggers clean error state with "Coba Lagi" button.

### C. APK Download & Distribution
- [ ] Local APK path `/downloads/bubakan-green.apk` verified.
- [ ] If file exists: clicking button downloads `.apk` file directly.
- [ ] If file absent: button displays graceful disabled/verification status.
- [ ] Zero fake Play Store buttons.

### D. Responsive Viewports Testing
- [ ] 320px (Mobile Compact): Verified zero horizontal scrollbar.
- [ ] 360px (Mobile Small): Verified clean text wrapping.
- [ ] 393px (Mobile Standard - Pixel 7): Verified mockup centered and touch targets ≥ 48px.
- [ ] 768px (Tablet Portrait): Verified 2-column grid transitions.
- [ ] 1024px (Laptop): Verified 3-device mockup composition.
- [ ] 1440px+ (Wide Desktop): Verified container max-width and margin centering.

### E. App Link & Route Safety
- [ ] `/.well-known/assetlinks.json` remains reachable with HTTP 200 and valid JSON.
- [ ] Route `/plant/**` rewrites to `/plant.html` in `firebase.json`.
- [ ] Route `/location/**` rewrites to `/location.html` in `firebase.json`.
- [ ] Android App Links continue to trigger native app when installed.
