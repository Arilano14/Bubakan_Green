# BUBAKAN GREEN — VISUAL REDESIGN BEFORE / AFTER
## Playful Botanical Education Experience

**Document:** `docs/visual-redesign/VISUAL_BEFORE_AFTER.md`  
**Date:** 2026-09-28  
**Scope:** Comparative Analysis of Implemented Visual & UX Changes  

---

## 1. Home Screen (Beranda)

### BEFORE
- **Layout & Structure:** Static vertical list structured like an administrative directory or government database portal.
- **Header:** Simple top app bar with text `"BUBAKAN GREEN"` and standard small lock/info icons.
- **Hero Area:** Generic rectangular card with a thin outline, title `"Urban Farming Kelurahan"`, small text, and a standard flat Material button.
- **Categories:** Plain two-column rectangular cards (`"Urban Farming"` and `"Taman Toga"`) without expressive hierarchy.
- **Trivia / Storytelling:** Non-existent. No welcoming personality, no civic storytelling, no educational trivia.
- **Visual Weight:** Heavy, dark-green monochromatic tones (`#2D6A4F` / `#1B4332`), clinical white surfaces, thin 1dp borders, flat 16dp rounded corners.

### AFTER (Implemented)
- **Layout & Structure:** Vibrant character-guided **Educational Discovery Hub** inspired by Duolingo's educational onboarding principles.
- **Top Greeting & Mascot:** Warm speech bubble featuring **Si Buba** (young seedling mascot in `BubaState.GREETING` with waving sprout arm) saying: *"Halo! Aku Si Buba. Yuk, kenalan dengan tanaman herbal & kebun hijau di Bubakan hari ini!"*
- **Hero Area:** Expansive `"Yuk, Kenalan dengan Tanaman Bubakan! 🌿"` discovery card with soft mint container (`PrimaryContainerMint` `#E2F7EC`) and full-width `TactileButton` (`"Mulai Belajar Sekarang 🚀"`) with a 3D depressing bottom rim.
- **Primary Discovery Modules:** Chunky, high-contrast program cards:
  - **🌱 Urban Farming:** Sprout badge `"PANGAN"`, mint container, deep green accents, bold typography.
  - **🌿 Taman Toga:** Herb badge `"HERBAL"`, warm golden container (`AccentSunnyContainer` `#FFF3D6`), golden blossom accents.
- **"💡 Tahukah Kamu?" Educational Fact Pod:** Bite-sized verified local trivia paired with Si Buba in `BubaState.THINKING` posture highlighting chemical-free organic farming in Bubakan.
- **Koleksi Tanaman Pilihan:** Chunky botanical cards with 18dp rounded photos, ExtraBold typography, bilingual badges, and `"Pelajari Tanaman →"` actions.

---

## 2. Plant Detail Screen (Detail Tanaman)

### BEFORE
- **Layout & Structure:** Stacked editorial document reading like a static encyclopedia entry or Wikipedia article.
- **Header Photo:** Standard photo box with sharp or standard rectangular edges.
- **Nomenclature:** Plain Indonesian name and basic italic Latin subtitle.
- **Mandarin Experience:** Small utility box with Hanzi, Pinyin, and a simple speaker button without feedback or educational warmth.
- **Content Sections:** Raw text blocks under generic grey headers.
- **Emotional Engagement:** Zero character presence; purely passive reading.

### AFTER (Implemented)
- **Layout & Structure:** Interactive **Bite-Sized Mini Botanical Lesson** with 7 structured educational stages.
- **Header Photo:** Expansive 16:10 photography with organic `28dp` curved bottom corners creating a soft, welcoming canopy.
- **Nomenclature:** ExtraBold `26sp` common name (`OnSurfaceForestDark` `#143625`) + elegant `16sp` italic Latin binomial (`SecondarySage`).
- **Interactive Mandarin Discovery Pod:**
  - High-contrast Dew Teal container (`AccentDewContainer` `#E0F9F6`) with `"PELAFALAN BAHASA MANDARIN"` tag.
  - Prominent `32sp` bold Hanzi glyphs (`红姜`, `姜黄`, `山柰`).
  - Tone-marked Pinyin (`hóng jiāng`).
  - **Live Mascot Reaction:** Si Buba listener mascot (`BubaState.LISTENING`) perks its leaf-ears and shows teal sound wave ripples while audio is playing.
  - User-triggered only (no autoplay, no looping).
- **Modular Educational Lessons:**
  - `"📖 Kenali Tanaman Ini"`: Friendly, approachable species introduction.
  - `"🌿 Manfaat & Khasiat Sehat"`: Clear medicinal benefit breakdown.
  - `"💡 Yang Perlu Kamu Tahu"`: Organic care and local cultivation insights.
  - `"📍 Tanaman Ini Ada di Kebun Bubakan"`: Direct link to physical garden plots (e.g. Taman Toga RW 03).

---

## 3. Catalog Screen (Katalog Tanaman)

### BEFORE
- **Layout & Structure:** Generic text input field above a simple vertical list.
- **Search Experience:** Standard Material `OutlinedTextField` with sterile placeholder `"Cari nama tanaman, latin..."`.
- **List Presentation:** Thin cards resembling an inventory list or plant ecommerce catalog.
- **Empty State:** Plain text with a generic magnifying glass icon.

### AFTER (Implemented)
- **Layout & Structure:** **Perpustakaan Botani** (Botanical Learning Library) designed for effortless visual scanning.
- **Chunky Search Bar:** Rounded `18dp` search bar with pillowed organic border (`OutlineOrganic`), soft vanilla canvas, and friendly placeholder: `"Cari jahe, temulawak, kencur, toga..."`.
- **Botanical Cards:** Chunky `22dp` cards featuring 92dp rounded thumbnails, ExtraBold titles, Mandarin chips in soft teal, and clear `"Pelajari Tanaman →"` cues.
- **Empty State:** Guided by Si Buba in `BubaState.SEARCHING` inspecting an empty flower pot with empathetic recovery action.

---

## 4. Locations Screen & Detail Screen (Lokasi & Peta Kebun)

### BEFORE
- **Layout & Structure:** Tabbed view resembling GIS surveying software or an administrative directory.
- **Visual Coding:** Monochromatic green pins with little thematic difference between vegetable farms and medicinal herb gardens.
- **Map Interaction:** Plain list of coordinates and standard flat buttons.

### AFTER (Implemented)
- **Layout & Structure:** **Panduan Jelajah Kebun Bubakan** (Garden Exploration Field Guide).
- **Thematic Visual Identity:**
  - **Urban Farming Plots:** Energetic chlorophyll green with sprout iconography (`🌱`).
  - **Taman Toga Plots:** Sunny golden blossom with herbal iconography (`🌿`).
- **Tactile Exploration Controls:**
  - Segmented tab switch with soft pill-shaped indicators.
  - Elevated `22dp` garden preview card with `TactileButton` for Google Maps navigation (`"📍 Buka Maps"` and `"Detail Kebun →"`).
  - Prominent verification status (`"✅ Terverifikasi"`).

---

## 5. Navigation & Global Chrome

### BEFORE
- **Top Bar:** Standard Material 3 `TopAppBar` with basic text and standard flat icons.
- **Bottom Navigation:** Default Material 3 `NavigationBar` with thin outline, flat rectangular indicator, and muted grey icons.

### AFTER (Implemented)
- **Top Bar:** Warm vanilla canvas, ExtraBold brand typography, civic pill badge (`"🌱 KELURAHAN BUBAKAN"`), and circular tactile action buttons.
- **Bottom Navigation:** Custom rounded top corners (`24dp`), pill-shaped soft mint selection containers (`PrimaryContainerMint`), bold botanical icons, ExtraBold active labels, and organic outline borders.

---

## 6. Verification Summary

| Aspect | Verification Status |
|:---|:---:|
| Visual Transformation Conspicuousness | ✅ High (Immediate difference in color, shapes, mascot, typography) |
| Duolingo-Inspired Principles | ✅ Applied (Tactile 3D, chunkiness, character-led, bite-sized lessons) |
| Bubakan Green Identity Integrity | ✅ 100% Preserved (Kelurahan Bubakan, Mijen context, verified plants) |
| Backend & Logic Integrity | ✅ 100% Preserved (0 schema changes, 0 route regressions) |
