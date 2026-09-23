# VISUAL DIRECTION: PLANTA-INSPIRED, BUBAKAN-OWNED
## BUBAKAN GREEN — Sistem Informasi Urban Farming & Taman Toga Kelurahan Bubakan

**Document:** `docs/phase-3/VISUAL_DIRECTION.md`  
**Phase:** Phase 3 (Product UI & Core User Experience Implementation)  
**Status:** APPROVED BASELINE FOR IMPLEMENTATION  
**Authority:** Derived from Phase 0 Product Requirements, Phase 1 Approved IA/UX Specification, and Phase 3 Visual Reference Directives (Parts W–AL).

---

## 1. Executive Vision & Visual Identity

The visual goal of **BUBAKAN GREEN** is:

> *"Planta-like botanical usability, visual clarity, and calm organic aesthetics, but distinctly owned by Kelurahan Bubakan."*

BUBAKAN GREEN is **not** a generic plant-care app, **not** a personal watering reminder tool, and **not** a bureaucratic government dashboard. It is a modern, dignified, accessible community information system connecting residents and visitors to the real Urban Farming and Taman Toga initiatives of Kelurahan Bubakan.

```
┌────────────────────────────────────────────────────────────────────────┐
│                        BRAND IDENTITY HIERARCHY                        │
├────────────────────────────────────────────────────────────────────────┤
│  PRIMARY:       BUBAKAN GREEN                                          │
│  SECONDARY:     Kelurahan Bubakan, Kecamatan Mijen, Kota Semarang      │
│  CONTEXT:       Urban Farming • Taman Toga • Edukasi Tanaman           │
│                 Informasi Lokasi • Pengetahuan Khasiat Herbal          │
└────────────────────────────────────────────────────────────────────────┘
```

---

## 2. What is Borrowed Conceptually from Planta vs. What is Intentionally Different

We reference **Planta (Plant & Garden Care by Planta AB)** as an industry benchmark for botanical presentation and user experience excellence. However, BUBAKAN GREEN is an original, tailored product for a physical administrative community.

### 2.1 Conceptual Borrowings (Design Principles Adapted)

| Principle | How Planta Does It | How Bubakan Green Adapts It |
|:---|:---|:---|
| **Plant-First Visual Hierarchy** | High-resolution photography dominates screen tops; botanical details take center stage. | Plant photos are hero elements. Names and botanical features lead; technical metadata is de-emphasized. |
| **Clean, Natural Backgrounds** | Crisp white and warm off-white surfaces with ample breathing room. | Background uses warm off-white (`#F8F9FA`) and pure white cards (`#FFFFFF`), avoiding harsh dark modes or gray slop. |
| **Calm Green Palette** | Muted, organic forest and sage greens that evoke nature without blinding neon hues. | Rooted in *Palette Alam Bubakan*: Deep forest green (`#2D6A4F`), sage (`#52796F`), and soft mint (`#D8F3DC`). |
| **Generous Whitespace** | Wide margins, spacious padding, and low-density layouts that feel relaxing. | Strict 8-point grid with 16dp margins and 24dp section spacing. Cards never crowd each other. |
| **Low-Noise Information Cards** | Thin, clean outlines; flat surfaces; zero drop-shadow clutter. | 16dp rounded cards with flat surfaces and 1dp subtle border (`#D0DBCE`). No heavy drop shadows. |
| **Clear Typographic Hierarchy** | Bold common names, distinguished scientific/Latin names in distinct weight/style. | Indonesian common name in bold `HeadlineMedium (24sp)`, scientific name in italic `OnSurfaceVariant (16sp)`. |
| **Content-First Interaction** | Immediate presentation of value; no obstructive popups or registration walls. | 100% open public access. No login gate, no registration prompt, no onboarding roadblocks. |
| **Modern Mobile-First Ergonomics** | Bottom navigation, thumb-zone reachable actions, smooth touch feedback. | 3-tab bottom bar (`Beranda`, `Lokasi & Peta`, `Katalog`), large 48dp touch targets, backstack clarity. |

### 2.2 What is Intentionally Different (Bubakan-Owned Specialization)

| Dimension | Commercial App (Planta) | BUBAKAN GREEN (Kelurahan Bubakan) |
|:---|:---|:---|
| **Core Purpose** | Commercial consumer app for home plant care & watering reminders. | Civic digital information system for community agriculture and medicinal herb knowledge. |
| **Business Model** | Freemium subscriptions, paywalls, upselling premium features. | 100% free public civic utility for community empowerment. Zero paywalls. |
| **User Account** | Requires email/Google sign-up, personal garden profiling. | Completely anonymous for general public. Login is reserved solely for verified field officers (PIC/Admin). |
| **Physical Anchoring** | Plants exist in virtual pots inside user's private rooms. | Plants are grounded in **real physical community gardens** in Kelurahan Bubakan (e.g., RW 03). |
| **Community Categories** | Indoor vs Outdoor houseplant tags. | **Urban Farming** (pangan/sayur kelurahan) & **Taman Toga** (tanaman obat keluarga RW). |
| **Multilingual Knowledge** | English/Swedish plant care instructions. | Tri-lingual botanical education: Indonesian name, Scientific Latin, and **Mandarin Hanzi + Pinyin + Audio**. |
| **Physical Touchpoints** | Push notifications and calendar reminders. | **Physical QR Code stickers** affixed to garden plots in Bubakan, opening instant App Links. |
| **Geographic Context** | None (location-agnostic). | Coordinates, RW numbers, and direct navigation links to real Bubakan plots via Google Maps. |

---

## 3. Brand Identity & Typography Strategy

### 3.1 Brand Presence in the Interface
- **App Bar & Headers:** Prominently identifies **BUBAKAN GREEN** with subtle subtitle *"Kelurahan Bubakan, Mijen"*.
- **No Logo Overload:** Local identity is established through real garden contexts, RW designations, and botanical content rather than repeating government logos on every card.
- **Tone of Voice:** Dignified, welcoming, community-oriented, educational, and free from bureaucratic jargon or student-project phrasing.

### 3.2 Typography Tokens (Material 3 Scale)
Typography balances modern readability with clear support for Indonesian botanical prose, Latin binomial nomenclature, and Chinese Hanzi glyphs.

| Token | Size / Weight | Line Height | Color Role | Applied Usage |
|:---|:---|:---|:---|:---|
| **HeadlineLarge** | `32sp` Bold | `40sp` | `OnSurfaceDark` (`#1B4332`) | Home Screen Greeting ("BUBAKAN GREEN") |
| **HeadlineMedium** | `24sp` SemiBold | `32sp` | `OnSurfaceDark` (`#1B4332`) | Plant Detail Title, Location Detail Title |
| **HeadlineSmall** | `20sp` SemiBold | `28sp` | `OnSurfaceDark` (`#1B4332`) | Section Headers ("Lokasi Unggulan", "Koleksi Tanaman") |
| **TitleMedium** | `16sp` SemiBold | `24sp` | `OnSurfaceDark` (`#1B4332`) | Plant Card Title, Location Card Title |
| **TitleSmall (Latin)**| `14sp` Italic Medium | `20sp` | `OnSurfaceVariant` (`#5B7065`) | Botanical Scientific Name (*Zingiber officinale*) |
| **BodyLarge** | `16sp` Regular | `24sp` | `OnSurfaceDark` (`#1B4332`) | Benefits (Khasiat), Descriptions, Educational paragraphs |
| **BodyMedium** | `14sp` Regular | `20sp` | `OnSurfaceVariant` (`#5B7065`) | Garden address, RW info, cultivation notes |
| **LabelLarge** | `14sp` Medium | `20sp` | `OnPrimary` / `Primary` | Action buttons, chip filters, tab labels |
| **LabelSmall** | `11sp` Medium | `16sp` | `OnSurfaceVariant` | RW badges, category chips, status pills |
| **MandarinGlyph** | `24sp` Regular | `32sp` | `PrimaryForest` (`#2D6A4F`) | Chinese Hanzi characters (姜, 生姜) — Noto Sans SC |
| **MandarinPinyin** | `14sp` Medium | `20sp` | `SecondarySage` (`#52796F`) | Pinyin phonetic transcription (*jiāng*) |

---

## 4. Color Token Strategy (Palette Alam Bubakan)

The palette draws directly from the lush, verdant agricultural landscape of Kelurahan Bubakan in Kecamatan Mijen, Semarang.

```
┌────────────────────────────────────────────────────────────────────────┐
│                        PALETTE ALAM BUBAKAN                            │
├────────────────────────────────────────────────────────────────────────┤
│  PrimaryForest           #2D6A4F   Deep botanical evergreen            │
│  PrimaryContainerMint    #D8F3DC   Soft natural mint wash              │
│  OnPrimaryContainer      #081C15   Deep forest shadow                  │
│  SecondarySage           #52796F   Earthy herbal sage                  │
│  SecondaryContainer      #B7E4C7   Taman Toga accent tone              │
│  BackgroundLight         #F8F9FA   Warm botanical off-white canvas     │
│  SurfaceWhite            #FFFFFF   Crisp, clean card surface           │
│  OnSurfaceDark           #1B4332   Deep earthy charcoal/green (12.8:1) │
│  OnSurfaceVariant        #5B7065   Sage grey for scientific secondary  │
│  OutlineGrey             #D0DBCE   Delicate garden fence border        │
│  StatusVerifiedGreen     #2D6A4F   Natural verified badge              │
│  StatusPendingOrange     #E09F3E   Warm harvest amber                  │
│  ErrorRed                #BA1A1A   Restrained warning red              │
└────────────────────────────────────────────────────────────────────────┘
```

### 4.1 Contrast & Accessibility Compliance (WCAG 2.1 AA)
- `OnSurfaceDark` (`#1B4332`) on `SurfaceWhite` (`#FFFFFF`): **12.8:1** (Far exceeds the 4.5:1 requirement).
- `PrimaryForest` (`#2D6A4F`) on `SurfaceWhite` (`#FFFFFF`): **5.9:1** (Fully accessible for buttons and primary headers).
- `OnSurfaceVariant` (`#5B7065`) on `SurfaceWhite` (`#FFFFFF`): **4.6:1** (Fully accessible for secondary body text).
- `OnPrimary` (`#FFFFFF`) on `PrimaryForest` (`#2D6A4F`): **5.9:1** (High legibility for button text).

---

## 5. Spacing, Elevation & Corner Radii

### 5.1 Spacing Scale (8-Point Grid)
- `4dp`: Micro-spacing (badge internal padding, icon-text gap).
- `8dp`: Compact spacing (gap between related items, chip padding, card internal element gap).
- `16dp`: Standard margin & padding (screen horizontal gutters, card interior padding).
- `24dp`: Section spacing (vertical distance between distinct functional blocks).
- `32dp`: Hero spacing (top of screen clearance, major content transitions).

### 5.2 Corner Radii
- **Cards (`Card`):** `16dp` — Soft, organic curves matching natural leaf contours without exaggerated balloon shapes.
- **Action Buttons (`Button`):** `12dp` — Modern, friendly rounded rectangular buttons.
- **Pills & Chips (`FilterChip`, `StatusPill`):** `50% / Circle` or `8dp` — High touch-affordance.
- **Bottom Navigation Bar:** Flat base, subtle top outline (`1dp` of `#D0DBCE`).
- **Modal / Bottom Sheet:** `24dp` top corner curvature.

### 5.3 Elevation & Shadow Restraint
- **Default Cards:** `0dp` elevation with a crisp `1dp` border of `OutlineGrey` (`#D0DBCE`). This completely avoids dirty drop-shadows and creates an airy, editorial botanical look.
- **Floating Actions / Modals:** `2dp` subtle, warm ambient shadow (`#1B4332` at 8% opacity). No harsh black drop-shadows.

---

## 6. Photography & Image Treatment

Photography is the primary visual anchor of BUBAKAN GREEN. Plants and community gardens must be portrayed with authenticity and respect.

### 6.1 Photographic Principles
1. **Natural Sunlight:** Emphasize outdoor daylight, morning garden light, and true-to-life chlorophyll greens.
2. **Botanical Focus:** Clear macro/medium focus on identifiable plant anatomy (leaves, stems, flowers, rhizomes).
3. **Environmental Context:** Location photography should show genuine Bubakan garden beds, polybags, green fencing, and bamboo stakes.
4. **Aspect Ratios:**
   - Location Heroes / Banners: `16:9` widescreen for spatial panorama.
   - Plant Catalog Cards: `4:3` or `1:1` square for immediate visual identification.
   - Plant Detail Hero: `16:10` large prominent photography at top of screen.

### 6.2 Data vs Design Integrity Rule
- **NO FAKE FIELD PHOTOGRAPHY:** Never commit fabricated photos or AI-generated fantasy imagery claiming to be real Bubakan community locations.
- **Development Placeholders:** If high-resolution photography is pending field collection, use clearly marked, clean botanical illustrations or styled icon placeholders with mint backgrounds.
- **Loading & Fallback:** Use Coil with a smooth crossfade and a shimmering mint placeholder (`#D8F3DC`), never a blank or broken gray box.

---

## 7. Component Visual System

### 7.1 Location Card (`LocationCard.kt`)
- **Structure:**
  - Top: High-resolution photo (`16:9`) with subtle category pill (`Urban Farming` in Forest Green, `Taman Toga` in Sage Green) overlaying top-left.
  - Middle: Location Name (`TitleMedium`), RW badge (`LabelSmall`), and short description (`BodyMedium`).
  - Bottom: Plant count indicator (`🌿 14 Koleksi Tanaman`) and arrow affordance.
- **Borders:** `1dp` outline `#D0DBCE`, zero drop shadow, 16dp rounded corners.

### 7.2 Plant Card (`PlantCard.kt`)
- **Structure:**
  - Leading / Top: Plant photograph with 12dp rounded corners.
  - Content: Common Indonesian Name (`TitleMedium`), Scientific Latin Name (*`TitleSmall` Italic*), and Category tag.
  - Mandarin snippet (if available): Small, clean Hanzi character display.
- **Interaction:** Full card ripple touch target (>48dp height).

### 7.3 Plant Detail Hero & Knowledge Sections (`PlantDetailScreen.kt`)
- **Top:** Immersive botanical photo hero (`16:10`).
- **Identity Block:**
  - Common Indonesian name in large bold type.
  - Scientific binomial name in elegant italic sage.
- **Mandarin Audio Row:**
  - Hanzi glyph (`24sp`, Noto Sans SC) + Pinyin in bold sage.
  - Distinctive circular speaker button `[ 🔊 ]` (48x48dp target).
- **Knowledge Cards:**
  - Card 1: **Manfaat & Khasiat Herbal** (Clear bullet points with herbal icon).
  - Card 2: **Karakteristik & Morfologi** (Description of leaves, aroma, habitat).
  - Card 3: **Panduan Penanaman & Perawatan** (Practical instructions for home gardeners).
  - Card 4: **Lokasi Tanam di Bubakan** (Links directly to the physical garden in Bubakan where this plant thrives).

### 7.4 Mandarin Audio Interaction Model
- **Manual Tap Only:** Sound plays **only** when user explicitly taps `[ 🔊 ]`.
- **States:**
  - *Idle:* Forest green circle with white speaker icon.
  - *Loading:* Small mint spinner.
  - *Playing:* Subtle pulsing sound wave icon. Plays once (1–3 seconds) then resets to *Idle*.
  - *Unavailable / Offline:* Graceful disabled state with friendly tooltip: *"Audio memerlukan koneksi internet"*.
- **Strict Prohibition:** Absolutely zero autoplay on screen enter, zero looping, zero background audio service.

### 7.5 Empty, Loading & Error States
- **Loading:** Shimmering cards using `PrimaryContainerMint` (`#D8F3DC`) gradient sweep, keeping layout stable.
- **Empty State:** Clean, minimalist line illustration (pot or garden gate outline) with helpful guidance:
  - Catalog: *"Belum ada tanaman yang cocok dengan pencarian Anda."* -> `[ Reset Pencarian ]`
  - Location: *"Belum ada kebun terdaftar untuk filter ini."* -> `[ Tampilkan Semua ]`
- **Error State:** Friendly human-readable Indonesian explanation (no raw stack traces) + prominent `[ Coba Lagi ]` button.
- **Offline Pill:** Calm top pill: `[ 📡 Mode Offline — Menampilkan data tersimpan ]` in soft sage neutral.

---

## 8. Screen Visual Hierarchy (Walkthrough)

### 8.1 Beranda (Home Screen — `SCR-PUB-01`)
```
┌────────────────────────────────────────────────────────┐
│  BUBAKAN GREEN                   [ℹ️ Info Kelurahan]   │ Header
├────────────────────────────────────────────────────────┤
│                                                        │
│  SELAMAT DATANG DI BUBAKAN GREEN                       │ Greeting
│  Sistem Informasi Urban Farming & Taman Toga           │ Subtitle
│                                                        │
│  ┌──────────────────────────────────────────────────┐  │
│  │ [FOTO HERO: KEBUN URBAN FARMING KELURAHAN]       │  │ Featured
│  │ LOKASI UNGGULAN KELURAHAN BUBAKAN                │  │ Location
│  │ RW 03 Bubakan • 24 Tanaman Terdata               │  │ Banner
│  │ [ Jelajahi Kebun Ini → ]                         │  │
│  └──────────────────────────────────────────────────┘  │
│                                                        │
│  KATEGORI KEGIATAN                                     │
│  [ 🌱 Urban Farming (Pangan) ] [ 🌿 Taman Toga (Obat) ]│ Quick Chips
│                                                        │
│  TANAMAN TOGA POPULER                                  │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │ Horizontal
│  │ [Foto Jahe]  │  │ [Foto Kencur]│  │ [Foto Kunyit]│  │ Botanical
│  │ Jahe Merah   │  │ Kencur       │  │ Kunyit Putih │  │ Cards
│  │ Zingiber off.│  │ Kaempferia g.│  │ Curcuma zedo.│  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
│                                                        │
│  TENTANG PROGRAM BUBAKAN GREEN                         │
│  Inisiatif ketahanan pangan dan apotek hidup mandiri   │ Educational
│  warga Kelurahan Bubakan, Mijen, Kota Semarang.        │ Card
│                                                        │
├────────────────────────────────────────────────────────┤
│   [ 🏠 Beranda ]    [ 📍 Lokasi ]    [ 🌿 Katalog ]    │ Bottom Nav
└────────────────────────────────────────────────────────┘
```

### 8.2 Lokasi & Peta (Location Directory — `SCR-PUB-02`)
- Top segment toggle: `[ Daftar Kebun ] | [ Peta Interaktif ]`.
- Filter chips: `[ Semua ] [ Urban Farming ] [ Taman Toga ]`.
- List mode: Vertical scroll of clean `LocationCard`s.
- Map mode: Provider-agnostic visual container with location pins, coordinate badges, and floating garden preview card.

### 8.3 Katalog Tanaman (Botanical Catalog — `SCR-PUB-04`)
- Prominent search input with magnifying glass: *"Cari nama tanaman, nama latin, atau khasiat..."*
- Filter chips for plant categories.
- 2-column botanical grid or clean single-column cards with strong photo hierarchy.

---

## 9. Examples of Correct vs. Incorrect Visual Usage

| Visual Dimension | ❌ INCORRECT (Avoid) | ✅ CORRECT (Approved Bubakan Standard) |
|:---|:---|:---|
| **Branding** | "Aplikasi KKN Mahasiswa Undip 2026", "KKN Giat 17 App" | "BUBAKAN GREEN — Sistem Informasi Urban Farming & Taman Toga Kelurahan Bubakan" |
| **Card Styling** | Heavy dark drop shadows (`elevation = 8dp`), neon green gradients, sharp square corners | Flat surface, `16dp` rounded corners, `1dp` border of `OutlineGrey` (`#D0DBCE`), zero shadow |
| **Visual Density** | Dense government dashboard with 8 statistics boxes, tiny pie charts, and bureaucratic tables | Planta-like airy botanical hierarchy: Large photo, clear bold title, italic scientific name, spacious cards |
| **Background Color**| Harsh dark mode (#121212) or stark cold hospital gray (#E0E0E0) | Warm botanical off-white canvas (`#F8F9FA`) with pure white card surfaces (`#FFFFFF`) |
| **Mandarin Audio** | Autoplaying Chinese audio on screen open; looping audio; robot TTS voice | Manual tap on clean 48dp speaker button `[ 🔊 ]`; plays once; stops; no autoplay |
| **Location Data** | Inventing 5 fake gardens with random coordinates to fill space | Rendering only verified Bubakan gardens; showing clean empty state if no data |
| **Botanical Data** | Fabricating mythical healing claims or non-existent plants | Presenting verified master botanical data; hiding missing fields cleanly without empty gaps |
| **Touch Targets** | 24dp tiny speaker icon that older residents struggle to press | Minimum 48dp × 48dp bounding box for all interactive buttons and chips |
| **Navigation** | 5 crowded bottom tabs with "E-Commerce", "Profil Pengguna", "Chat Warga" | Strict 3-tab navigation: `[ 🏠 Beranda ]`, `[ 📍 Lokasi ]`, `[ 🌿 Katalog ]` |

---

## 10. Visual QA Checklist (Mandatory Pre-Release Audit)

Before declaring Phase 3 UI complete, every screen must be audited against these 20 checks:

- [ ] 1. **Botanical Feel:** Does the screen feel botanical, fresh, and rooted in nature?
- [ ] 2. **Planta-Inspired Usability:** Does it reflect Planta's clarity and photo-first hierarchy without cloning its assets?
- [ ] 3. **Bubakan Ownership:** Is the identity of Kelurahan Bubakan clear without bureaucratic clutter?
- [ ] 4. **No KKN Branding:** Are all references to student KKN groups completely absent from UI and copy?
- [ ] 5. **Clean Background:** Is the background warm off-white (`#F8F9FA`) and cards crisp white (`#FFFFFF`)?
- [ ] 6. **Palette Fidelity:** Are all green tones derived from `Palette Alam Bubakan` (`#2D6A4F`, `#52796F`, `#D8F3DC`)?
- [ ] 7. **Typography Contrast:** Does primary text achieve at least 4.5:1 (actually 12.8:1) contrast against surface?
- [ ] 8. **Scientific Nomenclature:** Are Latin names properly italicized and rendered in `OnSurfaceVariant`?
- [ ] 9. **Mandarin Typography:** Are Chinese Hanzi characters rendered legibly at 24sp using Noto Sans SC?
- [ ] 10. **Audio Interaction:** Is Mandarin audio 100% manual tap, single-play, with zero autoplay?
- [ ] 11. **Card Cleanliness:** Are cards free from heavy drop-shadows, using 16dp radius and 1dp outline?
- [ ] 12. **Whitespace Generosity:** Is there generous breathing room (16dp gutters, 24dp section gaps)?
- [ ] 13. **Touch Accessibility:** Are all touch targets (speaker, chips, tabs, back button) at least 48dp × 48dp?
- [ ] 14. **TalkBack Labels:** Do all interactive icons have meaningful Indonesian `contentDescription`s?
- [ ] 15. **Loading States:** Are loading states represented by smooth shimmer skeletons, not blocking spinners?
- [ ] 16. **Offline Experience:** Does an offline status pill appear calmly without alarming red banners?
- [ ] 17. **Empty State Quality:** Do empty states feature clean outline illustrations and actionable recovery buttons?
- [ ] 18. **No Fake Data:** Is every displayed botanical item and garden location authentic and un-fabricated?
- [ ] 19. **No Ecommerce/Social Bloat:** Are carts, likes, comments, and gamification elements strictly absent?
- [ ] 20. **Visual Consistency:** Is the design system applied consistently across all 6 public screens?
