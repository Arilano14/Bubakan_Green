# VISUAL DIRECTION: PLAYFUL BOTANICAL EDUCATION
## BUBAKAN GREEN — Sistem Informasi Urban Farming & Taman Toga Kelurahan Bubakan

**Document:** `docs/visual-redesign/VISUAL_DIRECTION.md`  
**Phase:** Visual & Educational UX Redesign  
**Status:** PROPOSED DIRECTION — AWAITING ACC VISUAL REDESIGN  
**Reference Principles:** High-level educational UX principles inspired by Duolingo (playful, bold, friendly, rounded, bite-sized, character-guided) fused with authentic **Bubakan Green Botanical Identity**.  

---

## 1. Executive Philosophy: Playful Botanical Education

The redesigned visual identity of **BUBAKAN GREEN** transitions the product:

$$\text{From a static municipal information directory} \longrightarrow \text{To an engaging, educational botanical discovery journey}$$

The experience is encapsulated in one guiding user emotion:
> *"Belajar tentang tanaman di Bubakan itu mudah, seru, dan menyenangkan."*

```
┌────────────────────────────────────────────────────────────────────────┐
│                        BRAND IDENTITY POSITIONING                      │
├────────────────────────────────────────────────────────────────────────┤
│  PRIMARY:        BUBAKAN GREEN                                         │
│  CIVIC ROOT:     Kelurahan Bubakan, Kecamatan Mijen, Kota Semarang     │
│  CORE DOMAIN:    Urban Farming • Taman Toga • Edukasi Botani Mandiri   │
│  VISUAL TONE:    Playful Botanical Education                           │
│  PERSONALITY:    Friendly, energetic, clear, encouraging, authentic   │
│                                                                        │
│  NOT:            ❌ Government CRUD dashboard                          │
│  NOT:            ❌ Academic botanical textbook                        │
│  NOT:            ❌ Commercial plant-care reminder app (Planta clone)   │
│  NOT:            ❌ Duolingo clone (No owls, no hearts, no fake games) │
└────────────────────────────────────────────────────────────────────────┘
```

---

## 2. Duolingo Benchmark: Adapted Principles vs. Strict Boundaries

We reference modern digital learning benchmarks (specifically Duolingo's documented product and illustration philosophy) for **pedagogical clarity and visual friendliness**. We explicitly reject cloning proprietary assets or coercive gamification.

### 2.1 Principles Adapted

| Principle | Pedagogical Rationale | Implementation in Bubakan Green |
|:---|:---|:---|
| **Bite-Sized Discovery** | Learners absorb knowledge faster when content is divided into digestible cards. | Plant details are partitioned into progressive modules: *"Kenali Tanaman"*, *"Ciri & Karakteristik"*, *"Manfaat Sehat"*, and *"Temukan di Bubakan"*. |
| **Character-Guided Engagement** | A relatable botanical companion reduces anxiety and invites curiosity. | Introducing **Si Buba**, an original seedling mascot who introduces plant facts, celebrates pronunciation attempts, and cheers garden discovery. |
| **Tactile & Rounded Affordance** | Soft, pillowed geometries feel approachable and physically satisfying to touch. | Generous corner radii (`20dp` for cards, `16dp` for buttons, pill shapes for chips), subtle 2dp tactile depth on primary action buttons. |
| **High Visual Contrast & Bold Color** | Visual energy stimulates learning retention without visual clutter. | Vibrant, nature-inspired primary green (`#1E7B4D`), sunny golden bloom (`#FFB703`), fresh dew teal (`#17C3B2`), balanced on warm vanilla canvas (`#FDFBF7`). |
| **Minimalist Educational Illustration** | Visuals clarify botanical concepts without decorative distraction. | Clean vector silhouettes with bold outlines and expressive shapes, supporting real plant photographs rather than replacing them. |
| **Pronunciation as an Interactive Milestone** | Language learning thrives on multisensory feedback. | Mandarin Hanzi + Pinyin is presented as a friendly speech bubble with tactile audio interaction and visual mascot reaction. |

### 2.2 Strict Non-Cloning Boundaries (What We Do NOT Copy)

- ❌ **NO Duo or Owl Characters:** No green owls, birds, feathers, or Duo-like silhouettes.
- ❌ **NO Coercive Gamification:** No XP grinding, streak freezes, hearts/lives, leagues, competitive leaderboards, or countdown timers.
- ❌ **NO Visual Theft:** No copying Duolingo’s proprietary typeface (Feather), exact button gradients, or proprietary SVG assets.
- ❌ **NO Generic Language:** Copy remains dignified and grounded in Kelurahan Bubakan, avoiding baby-talk or manipulative guilt-tripping.

---

## 3. Brand Identity & Typography System

### 3.1 Typography Philosophy
The typographic palette must feel **approachable, rounded, yet completely clear** across botanical binomial names, Indonesian prose, and Chinese Hanzi glyphs.

We utilize a restrained 6-level typographic hierarchy mapped to Compose Material 3:

| Role | Style / Weight | Size / Line-Height | Purpose |
|:---|:---|:---|:---|
| **Display** | Rounded Bold | `28sp / 36sp` | Hero discovery greetings, mascot speech bubbles |
| **Title** | SemiBold | `20sp / 28sp` | Plant common names, garden location headers |
| **Subtitle (Latin)** | Medium Italic | `15sp / 22sp` | Binomial botanical names (*Zingiber officinale*) |
| **Section** | Bold | `16sp / 24sp` | Module headers ("Kenali Tanaman", "Manfaat Herbal") |
| **Body** | Regular | `15sp / 24sp` | Digestible educational prose, garden descriptions |
| **Caption / Label** | SemiBold | `12sp / 16sp` | RW badges, category chips, audio hints |
| **Mandarin Hanzi** | Bold Noto SC | `26sp / 34sp` | Chinese medicinal nomenclature (生姜) |
| **Pinyin** | Medium | `14sp / 20sp` | Phonetic guide (*shēng jiāng*) |

---

## 4. Color System: Palette Botani Ceria Bubakan

The color system upgrades the previous muted palette into a **vibrant, energetic, botanical ecosystem** while retaining high contrast compliance (WCAG 2.1 AA).

```
┌────────────────────────────────────────────────────────────────────────┐
│                   PALETTE BOTANI CERIA BUBAKAN                         │
├────────────────────────────────────────────────────────────────────────┤
│  PrimarySeedlingGreen    #1E7B4D   Energetic chlorophyll evergreen     │
│  PrimaryContainerMint    #E2F7EC   Fresh morning sprout wash           │
│  OnPrimaryWhite          #FFFFFF   High-contrast text on primary       │
│                                                                        │
│  AccentSunnyGold         #FFB703   Warm botanical blossom / discovery  │
│  AccentSunnyContainer    #FFF3D6   Soft sunny callout background       │
│  OnAccentGoldDark        #5A3E00   Accessible text on golden container │
│                                                                        │
│  AccentDewTeal           #17C3B2   Fresh water / Mandarin audio cue    │
│  AccentDewContainer      #E0F9F6   Audio module surface wash           │
│                                                                        │
│  BackgroundVanilla       #FDFBF7   Warm organic canvas (low eye-strain)│
│  SurfaceCardWhite        #FFFFFF   Elevated tactile learning cards     │
│  OnSurfaceForestDark     #143625   Deep legible botanical charcoal     │
│  OnSurfaceSageMuted      #51685B   Muted scientific secondary text     │
│  OutlineOrganic          #E5EDE7   Soft pillowed card borders          │
│                                                                        │
│  StatusPublishedGreen    #1E7B4D   Active / verified garden badge      │
│  StatusReviewAmber       #F77F00   Pending review / draft badge        │
│  ErrorRestrainedRed      #D62828   Gentle, constructive error alert    │
└────────────────────────────────────────────────────────────────────────┘
```

### 4.1 Functional Color Mapping

Color in BUBAKAN GREEN communicates functional educational roles:
- **Green (`#1E7B4D`):** Primary progression, verified status, active exploration, botanical discovery.
- **Sunny Gold (`#FFB703`):** *"Tahukah Kamu?"* trivia callouts, featured flagship gardens, discovery celebration.
- **Dew Teal (`#17C3B2`):** Audio pronunciation triggers, water/nurture tips, interactive sound states.
- **Vanilla Canvas (`#FDFBF7`):** Warm, inviting, non-sterile surface that makes white learning cards pop.

---

## 5. Tactile Component Language

### 5.1 Playful Tactile Buttons
Action buttons convey friendly affordance through rounded geometry and subtle tactile depth (3D rim effect on bottom border):
- **Corner Radius:** `16dp` for primary CTAs, `12dp` for secondary buttons.
- **Physical Feel:** `PrimarySeedlingGreen` background with a subtle darker bottom edge (`#145836`, `3dp` offset) that depresses slightly on press (`0dp` offset), providing satisfying tactile feedback.
- **Touch Target:** Minimum `52dp` height for effortless thumb interaction.

### 5.2 Educational Learning Cards
- **No Card Clutter:** Cards are only deployed for structured discovery units (featured garden, plant item, educational fact callout).
- **Corner Radius:** `20dp` soft continuous curvature.
- **Surface & Stroke:** Pure white (`#FFFFFF`) with a `1.5dp` border of `OutlineOrganic` (`#E5EDE7`) and zero muddy black drop-shadows.

### 5.3 Speech Bubbles & Educational Callouts
- Mascot tips and Mandarin hints use playful speech bubbles with a tiny directional pointer (`4dp` organic tail), visually linking the mascot or feature to the educational content.

---

## 6. Illustration & Iconography Strategy

1. **Simple Organic Silhouettes:** Iconography favors bold, friendly, filled strokes with rounded endpoints.
2. **Standard Core Icon Set:** Avoid heavy external icon libraries; leverage standard Compose Material symbols stylized with soft rounded containers (`Surface(shape = CircleShape, color = PrimaryContainerMint)`).
3. **No AI Photo Hallucination:** Photographs must represent real Bubakan plants and physical gardens (Urban Farming Kelurahan & Taman Toga RW 03). Vector illustrations are strictly reserved for the mascot companion, decorative accents, and schematic botanical diagrams.

---

## 7. Animation & Interaction Policy

Animations are **functional teaching aids**, never distracting novelties:
- **Mascot Reactivity:** When the user taps the Mandarin speaker button, the mascot companion transitions gently from *Neutral Idle* to *Listening/Talking* (`250ms` spring interpolation).
- **Card Reveal:** Staggered fade-and-slide up (`150ms` per card) on screen entry.
- **No Endless Loops:** Mascot does not continuously bounce, loop, or wave in a way that drains battery or distracts reading.
- **Reduced Motion Support:** Respects system accessibility animation settings (`Modifier.animateContentSize()` disables gracefully).

---

## 8. Locality Guarantee

Every educational element remains anchored to **Kelurahan Bubakan**:
- Flagship locations: **Urban Farming Kelurahan** (sayur mayur mandiri) and **Taman Toga RW 03** (tanaman herbal keluarga).
- Educational notes reflect local cultivation practices and herbal wisdom recorded in Bubakan field studies.
- Civic identification *"Kelurahan Bubakan, Mijen, Semarang"* remains present in navigation headers.
