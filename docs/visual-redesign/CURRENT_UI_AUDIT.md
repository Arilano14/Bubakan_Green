# BUBAKAN GREEN — CURRENT UI AUDIT
## Comprehensive Visual and Component Inspection Prior to Visual Overhaul

**Document:** `docs/visual-redesign/CURRENT_UI_AUDIT.md`  
**Date:** 2026-09-28  
**Scope:** Design System, Components, Screens, Navigation, Assets  
**Evaluation Benchmark:** Duolingo-Inspired Educational UX + Bubakan Green Botanical Identity  

---

## 1. Design Tokens & Theme Foundation

### 1.1 Color System (`Color.kt`, `Theme.kt`)
- **CURRENT:** Palette Botani Ceria tokens exist, but many backward-compatible aliases (`PrimaryForest`, `SecondarySage`, `BackgroundLight`, `SurfaceWhite`, `OutlineGrey`) remain in active use across Compose files, which caused code to fall back to older Planta-like styling.
- **PROBLEM:** The UI frequently renders as a monochromatic muted olive/dark green government directory without sufficient energetic warmth or educational contrast.
- **DECISION:** **MODIFY**.
- **REPLACEMENT:** Eliminate dependency on muted legacy aliases. Enforce bold, cheerful, high-contrast role tokens:
  - `PrimarySeedlingGreen` (`#1E7B4D`) with 3D shadow base `PrimarySeedlingDark` (`#145836`).
  - `SecondarySprout` (`#38B000`) for fresh vegetative accents.
  - `AccentSunnyGold` (`#FFB703`) with container `#FFF3D6` and dark text `#5A3E00` for discovery and trivia.
  - `AccentDewTeal` (`#17C3B2`) with container `#E0F9F6` for Mandarin pronunciation and audio feedback.
  - `BackgroundVanilla` (`#FDFBF7`) as an inviting warm cream canvas.
  - `SurfaceCardWhite` (`#FFFFFF`) with pillowed organic outlines (`#E5EDE7`).
  - `OnSurfaceForestDark` (`#143625`) for deep botanical charcoal legibility.

### 1.2 Typography System (`Type.kt`)
- **CURRENT:** Default Material 3 typography with standard sizes (32sp headline, 16sp body) using `FontWeight.Normal` and standard letter spacings.
- **PROBLEM:** Hierarchy looks generic and lacks visual punch. Educational titles do not command attention, and micro-copy feels like legal disclaimers.
- **DECISION:** **MODIFY**.
- **REPLACEMENT:** Create a bold, friendly 6-tier typography scale:
  - `DisplayTitle` (`28sp`, `FontWeight.ExtraBold`, `-0.5sp` tracking).
  - `SectionTitle` (`20sp`, `FontWeight.Bold`, `0sp` tracking).
  - `CardHeadline` (`16sp`, `FontWeight.Bold`, `0.1sp` tracking).
  - `LessonBody` (`15sp`, `FontWeight.Normal`, `22sp` line height).
  - `BotanicalScientific` (`14sp`, `FontStyle.Italic`, `FontWeight.Medium`).
  - `TactileButtonText` (`15sp`, `FontWeight.ExtraBold`, `0.3sp` tracking).

---

## 2. Shared Components Audit

### 2.1 Top Application Bar (`BubakanTopBar.kt`)
- **CURRENT:** Standard flat `TopAppBar` with green title and grey subtitle.
- **PROBLEM:** Looks administrative and sterile. Does not establish the friendly, educational atmosphere when the app opens.
- **DECISION:** **MODIFY**.
- **REPLACEMENT:** A softer, friendlier Top Bar with a warm vanilla canvas, a civic pill badge (`"🌱 KELURAHAN BUBAKAN"`), rounded circular action containers, and clear back-button navigation.

### 2.2 Bottom Navigation Bar (`BubakanNavHost.kt`)
- **CURRENT:** Default Material 3 `NavigationBar` with standard rectangular selection indicators and thin borders.
- **PROBLEM:** Looks like a default Android template app.
- **DECISION:** **MODIFY**.
- **REPLACEMENT:** Custom tactile navigation bar with rounded top corners (`24dp`), pill-shaped mint selection containers (`PrimaryContainerMint`), bold icons (`Home`, `Catalog/Search`, `Garden/Location`), and energetic label styling.

### 2.3 Plant Card (`PlantCard.kt`)
- **CURRENT:** Row layout with an 84dp thumbnail and small text lines.
- **PROBLEM:** Feels like a database record or ecommerce row rather than a collectible botanical learning badge.
- **DECISION:** **MODIFY**.
- **REPLACEMENT:** Chunky learning badge card with a large high-contrast thumbnail, bold common name, italic Latin binomial, Mandarin Hanzi + Pinyin in a soft teal pill badge, and a distinct tactile action cue (`"Pelajari Tanaman →"`).

### 2.4 Location Card (`LocationCard.kt`)
- **CURRENT:** 16:9 photo container with standard text and outline.
- **PROBLEM:** Lacks clear thematic visual distinction between Urban Farming and Taman Toga plots.
- **DECISION:** **MODIFY**.
- **REPLACEMENT:** Thematic garden exploration card with strong color identity:
  - **Urban Farming:** Seedling Green header and sprout badge (`🌱 Urban Farming`).
  - **Taman Toga:** Sunny Gold header and amber blossom badge (`🌿 Taman Toga`).
  - Integrated `TactileButton` for Google Maps route navigation.

### 2.5 Action Buttons (`TactileButton.kt`)
- **CURRENT:** `TactileButton.kt` was created, but older screens still used default `Button` or `OutlinedButton` with standard flat styling.
- **PROBLEM:** Inconsistent tactile feel across different screens.
- **DECISION:** **MODIFY / ENFORCE GLOBALLY**.
- **REPLACEMENT:** Replace all primary CTAs across `HomeScreen`, `PlantDetailScreen`, `LocationsScreen`, and `LocationDetailScreen` with `TactileButton` (3D bottom rim, tactile 3dp depression on tap).

### 2.6 Mandarin Speaker Button (`SpeakerButton.kt`)
- **CURRENT:** Circular button with pulse scale.
- **PROBLEM:** Functional, but lacks clear educational guidance text for learners.
- **DECISION:** **MODIFY**.
- **REPLACEMENT:** Interactive pronunciation pill containing: `[ 🔊 Dengarkan Pelafalan ]` paired directly with the Mandarin Hanzi/Pinyin pod, accompanied by live companion mascot listener feedback.

### 2.7 State Views (`StateEmptyView.kt`, `StateErrorView.kt`)
- **CURRENT:** Empty/error views updated with basic Buba mascot.
- **PROBLEM:** Spacing and typography can be more playful and empathetic.
- **DECISION:** **MODIFY**.
- **REPLACEMENT:** Friendly, empathetic empty & error screens with prominent Si Buba poses (`SEARCHING` with pot inspection, `RESTING` with gentle encouragement) and high-visibility `TactileButton` recovery triggers.

---

## 3. Primary Screens Audit

### 3.1 Home Screen (`HomeScreen.kt`)
- **CURRENT:** Displays greeting banner, hero banner, category cards, plant cards, and about card.
- **PROBLEM:** Still feels structurally like an administrative dashboard or plant directory. The storytelling and visual momentum are weak.
- **DECISION:** **MAJOR REDESIGN**.
- **REPLACEMENT:**
  1. **Top Identity & Mascot Greeting:** Friendly welcoming speech bubble from Si Buba: *"Halo! Yuk, kenalan dengan tanaman & kebun di Bubakan!"*
  2. **Interactive Discovery Hero:** "Yuk, Kenalan dengan Tanaman Bubakan" with vibrant visual framing.
  3. **Dual Exploration Modules:** Large chunky cards for **🌱 Urban Farming** (Sayur & Pangan) and **🌿 Taman Toga** (Tanaman Obat) with huge visual icons and punchy subtitles.
  4. **"💡 Tahukah Kamu?" Educational Fact Pod:** Bite-sized verified botanical trivia with thinking mascot.
  5. **Koleksi Tanaman Pilihan:** Visual cards with bilingual taxonomy.
  6. **Jelajahi Kebun Bubakan:** Flagship showcase for Urban Farming Kelurahan and Taman Toga RW 03.

### 3.2 Catalog Screen (`CatalogScreen.kt`)
- **CURRENT:** Standard text search field above a list of plant cards.
- **PROBLEM:** Feels like an ecommerce search screen.
- **DECISION:** **MAJOR REDESIGN**.
- **REPLACEMENT:**
  - "Perpustakaan Botani" concept.
  - Chunky rounded search bar (`16dp`) with friendly prompt: *"Cari jahe, kunyit, bayam, toga..."*.
  - Category pill filter row (`Semua`, `Taman Toga`, `Urban Farming`).
  - Rich botanical discovery cards with clear bilingual badges.
  - Empathetic empty search state with Si Buba searching.

### 3.3 Plant Detail Screen (`PlantDetailScreen.kt`)
- **CURRENT:** Hero photo with stacked informational text sections.
- **PROBLEM:** Reads like a raw encyclopedia article or static Wikipedia page.
- **DECISION:** **MAJOR REDESIGN**.
- **REPLACEMENT:** Transform into an interactive **Mini Botanical Lesson**:
  1. **Expansive Photo Header:** 16:10 photograph with organic 24dp curved bottom corners.
  2. **Bilingual Botanical Nomenclature:** Common Indonesian name (`24sp`, bold) + Latin binomial (`16sp`, italic).
  3. **Interactive Mandarin Pod:** Large Hanzi (`32sp`, bold) + Pinyin with tone marks + `[ 🔊 Dengarkan ]` button + **live listening mascot reaction**.
  4. **"Kenali Tanaman Ini":** Friendly, approachable summary.
  5. **"Khasiat & Manfaat Herbal":** Bulleted benefit cards with herbal icons.
  6. **"Yang Perlu Kamu Tahu":** Practical care and preparation tips.
  7. **"Tanaman Ini Ada di...":** Direct link to physical garden plot in Kelurahan Bubakan.

### 3.4 Locations Screen & Detail Screen (`LocationsScreen.kt`, `LocationDetailScreen.kt`)
- **CURRENT:** Tabbed list/map view with standard cards.
- **PROBLEM:** Looks like GIS surveying software.
- **DECISION:** **MAJOR REDESIGN**.
- **REPLACEMENT:**
  - "Panduan Jelajah Kebun Bubakan".
  - Highlighting flagship hubs: Urban Farming Kelurahan & Taman Toga RW 03.
  - Intuitive map visual container with elevated garden preview cards.
  - Direct tactile navigation button: `"📍 Petunjuk Arah ke Kebun Ini"`.

---

## 4. Implementation Plan for Visual Overhaul

1. **Step 1:** Refine `Color.kt` and `Type.kt` with the bold, friendly educational tokens.
2. **Step 2:** Refine `BubakanTopBar.kt` and `BubakanNavHost.kt` navigation chrome.
3. **Step 3:** Overhaul `HomeScreen.kt` into the character-guided discovery hub.
4. **Step 4:** Overhaul `PlantDetailScreen.kt` into the interactive mini-lesson experience.
5. **Step 5:** Overhaul `CatalogScreen.kt` and `LocationsScreen.kt`.
6. **Step 6:** Validate with `.\gradlew.bat compileDebugKotlin`, `.\gradlew.bat test`, and `.\gradlew.bat assembleDebug`.
7. **Step 7:** Create `docs/visual-redesign/VISUAL_BEFORE_AFTER.md`.
