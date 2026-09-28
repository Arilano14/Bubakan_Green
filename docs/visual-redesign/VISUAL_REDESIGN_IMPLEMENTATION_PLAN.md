# BUBAKAN GREEN — VISUAL REDESIGN IMPLEMENTATION PLAN
## Playful Botanical Education (Duolingo-Inspired UX, Bubakan-Owned Identity)

**Document:** `docs/visual-redesign/VISUAL_REDESIGN_IMPLEMENTATION_PLAN.md`  
**Phase:** Visual & Educational UX Redesign  
**Status:** PROPOSED PLAN — AWAITING ACC VISUAL REDESIGN  
**Scope:** Frontend UI, Jetpack Compose Design System, Educational Interactions, Mascot Integration  
**Preserved:** Backend Firestore schema, Auth logic, QR/App Link routing, Repository contracts  

---

## 1. Current UI Audit

A comprehensive inspection of the existing Phase 3 & Phase 5 Android implementation reveals:
- **Strengths:** 
  - Rock-solid clean architecture (`UI -> ViewModel -> Repository -> Firestore/Cache`).
  - High contract discipline with `Result<T>` and strict offline caching support.
  - Zero crashes, clean compilation, and 100% passing unit tests (36/36).
- **Visual Gaps (Why Redesign is Needed):**
  - **Overly Static & Editorial:** While the Planta-inspired direction was clean, it feels like a quiet reference encyclopedia rather than an engaging educational tool.
  - **Lack of Emotional Connection:** There is no welcoming guide or character to invite learners or community residents into the botanical world.
  - **Mandarin Experience is Functional but Flat:** The Mandarin card is purely a utility box with Hanzi, Pinyin, and a speaker button; it lacks the interactive excitement of language learning.
  - **Home Screen Feels Like a Directory:** The home screen displays lists of cards without storytelling or progressive discovery.

---

## 2. Existing Component Reuse Strategy

We preserve and restyle existing robust components rather than rewriting from scratch:

| Existing Component | Current Role | Redesign Treatment |
|:---|:---|:---|
| `BubakanTopBar` | Standard App Bar | Keep layout and backstack logic; soften background to `BackgroundVanilla`, round icons in subtle mint containers. |
| `PlantCard` | Catalog & Home list items | Transform into a tactile learning card with `20dp` rounded corners, bolder botanical badges, and playful category pills. |
| `LocationCard` | Garden discovery item | Enhance with thematic color tags: Forest Green for Urban Farming, Sunny Amber for Taman Toga. |
| `FeaturedLocationBanner` | Flagship showcase | Upgrade with organic curved frame, celebratory badge, and direct call-to-action button. |
| `MandarinSpeakerButton` | Audio pronunciation trigger | Enhance with tactile circular feedback, pulsating audio ring, and mascot reaction listener. |
| `StateErrorView` / `EmptyView` | Feedback screens | Integrate **Si Buba** mascot in empathetic, helpful poses with clear action buttons. |
| `OfflineStatusBar` | Connectivity indicator | Keep banner logic; restyle with warm amber pill styling. |

---

## 3. New Visual Direction: Playful Botanical Education

- **Primary Metaphor:** *An Outdoor Botanical Classroom*.
- **Aesthetic Attributes:**
  - Soft, chunky, tactile geometries (no sharp 90-degree corners).
  - Bold organic nature tones with vibrant sunlight accents.
  - Friendly mascot presence (*Si Buba*) acting as a guide, not a dictator.
  - Bite-sized educational modules with generous breathing room.

---

## 4. Color System Implementation

Update `app/src/main/java/id/bubakangreen/app/ui/theme/Color.kt`:
```kotlin
// Palette Botani Ceria Bubakan
val PrimarySeedlingGreen   = Color(0xFF1E7B4D) // Energetic chlorophyll evergreen
val PrimaryContainerMint   = Color(0xFFE2F7EC) // Soft sprout wash
val OnPrimaryWhite         = Color(0xFFFFFFFF)

val AccentSunnyGold        = Color(0xFFFFB703) // Botanical blossom / discovery
val AccentSunnyContainer   = Color(0xFFFFF3D6)
val OnAccentGoldDark       = Color(0xFF5A3E00)

val AccentDewTeal          = Color(0xFF17C3B2) // Water / pronunciation accent
val AccentDewContainer     = Color(0xFFE0F9F6)

val BackgroundVanilla      = Color(0xFFFDFBF7) // Warm organic canvas
val SurfaceCardWhite       = Color(0xFFFFFFFF) // Crisp card surface
val OnSurfaceForestDark    = Color(0xFF143625) // Deep charcoal green
val OnSurfaceSageMuted     = Color(0xFF51685B) // Scientific secondary
val OutlineOrganic         = Color(0xFFE5EDE7) // Pillowed card border

val StatusPublishedGreen   = Color(0xFF1E7B4D)
val StatusReviewAmber      = Color(0xFFF77F00)
val ErrorRestrainedRed     = Color(0xFFD62828)
```

---

## 5. Typography Scale Implementation

Update `app/src/main/java/id/bubakangreen/app/ui/theme/Type.kt`:
- Configure a tight, friendly 6-role scale using system rounded fonts (`FontFamily.Default` with `FontWeight.Bold` and `FontWeight.SemiBold`).
- Preserve special styling for Latin binomial names (`FontStyle.Italic`) and Mandarin Hanzi glyphs.

---

## 6. Mascot Concept: "Si Buba"

We adopt **Concept A: Si Buba (Tunas Hijau Bubakan)**:
- Plump seedling teardrop body.
- Two expressive leaf-ears on top that emote.
- Warm terracotta earthen base.
- Wide, friendly eyes and welcoming smile.
- Implemented as a reusable, lightweight vector Composable: `BubaMascot(state = BubaState.GREETING, modifier = Modifier.size(80.dp))`.

---

## 7. Mascot States & Expressions

```kotlin
enum class BubaState {
    GREETING,     // Waving happily (Home Header)
    LISTENING,    // Wearing headphones / tilted leaf-ears (Mandarin Audio)
    THINKING,     // Leaf tilted, magnifying glass (Search & Trivia)
    CELEBRATING,  // Sparkles, jumping joyfully (QR scan success)
    SEARCHING,    // Peeking into an empty pot (Empty state)
    RESTING       // Patiently waiting with watering can (Error/offline state)
}
```

---

## 8. Home Screen Redesign (`HomeScreen.kt`)

Restructure Home into a multi-tiered educational hub:
1. **Mascot Header Bar:**
   - Greeting speech bubble: *"Halo! Yuk, jelajahi tanaman hijau di Bubakan hari ini!"*
   - Mini Si Buba avatar waving alongside the civic title.
2. **Flagship Garden Spotlight (Hero):**
   - High-contrast visual card with a "Kebun Unggulan" sunny badge.
   - Toggles between Urban Farming Kelurahan and Taman Toga RW 03.
3. **Quick Exploration Modules:**
   - Dual tactile category cards: **Urban Farming** (Sayur & Pangan) and **Taman Toga** (Tanaman Obat).
4. **"Tahukah Kamu?" Educational Fact Card:**
   - Bite-sized verified botanical trivia from local Bubakan field data.
5. **Koleksi Tanaman Pilihan:**
   - Horizontal or vertical cards displaying plant photograph, Indonesian name, Latin name, and Mandarin chip.

---

## 9. Catalog Screen Redesign (`CatalogScreen.kt`)

Transform from a generic list into a **Botanical Learning Library**:
- Chunky search bar with rounded corners (`16dp`) and a friendly placeholder: *"Cari jahe, kunyit, bayam..."*.
- Tactile pill filters (`Semua`, `Taman Toga`, `Urban Farming`).
- Plant cards featuring prominent hero thumbnail, clear bilingual badges, and a "Pelajari Tanaman" button.
- Mascot empty search state when no plant matches the query.

---

## 10. Location Screen Redesign (`LocationsScreen.kt`)

Transform from a raw list into an **Exploration Field Guide**:
- Clear distinction between Flagship Civic Hubs and local garden plots.
- Tactile location cards with RW badges, direct Google Maps navigation button, and verified status indicator.
- Educational summary of what plants grow at that specific location.

---

## 11. Plant Detail Screen Redesign (`PlantDetailScreen.kt`)

Transform Plant Detail into an interactive **Mini Botanical Lesson**:
1. **Hero Imagery:** Expansive 16:10 photograph with rounded bottom corners.
2. **Nomenclature Header:** Indonesian common name in bold, Latin scientific name in italics.
3. **Mandarin Discovery Pod:** (See Section 12).
4. **"Kenali Tanaman Ini" (Deskripsi Singkat):** Bite-sized explanation in approachable language.
5. **"Khasiat & Manfaat Sehat":** Displayed as friendly bulleted cards with herbal icons.
6. **"Temukan di Kebun Bubakan":** Direct link to the physical garden (e.g. Taman Toga RW 03) where this plant is actively cultivated.

---

## 12. Mandarin Interaction & Pronunciation Pod

- Positioned directly beneath plant nomenclature.
- Visual Presentation:
  - Hanzi glyph displayed prominently (`28sp`, bold).
  - Pinyin transcription displayed with tone marks.
  - Tactile audio pill button: `[ 🔊 Dengarkan Pelafalan ]`.
- Interactive Behavior:
  - Tapping plays remote audio via `AudioState`.
  - While playing, Si Buba companion perks its leaf-ears in listening mode.
  - Strict compliance: User-initiated only; NO autoplay; NO looping.

---

## 13. Educational Micro-interactions

- **Tactile Button Press:** Subtle `2dp` vertical depression on press.
- **Audio Pulsing:** Soft ripple ring around speaker button while audio is buffering/playing.
- **Card Tap:** Gentle elevation change without layout shift.
- **QR Discovery Banner:** Cheerful celebration animation on deep-link arrival.

---

## 14. Animation Policy

- **Framerate Target:** 60fps locked on mid-range Android devices.
- **Duration Scale:** All transitions between `150ms` and `250ms`.
- **Interpolation:** `FastOutSlowInEasing` or spring physics for natural bounce.
- **Strict Prohibition:** No endless character bouncing, no battery-draining loop animations, no confetti explosions.

---

## 15. Responsive Layout Strategy

- **Small Phones (360dp):** Single column, 12dp horizontal padding, scalable text without truncation.
- **Standard Phones (390dp - 412dp):** Full 16dp margins, generous card spacing.
- **Tablets / Landscape:** 2-column grid for catalog and home sections; plant detail splits hero photo (left) and educational lesson (right).

---

## 16. Accessibility Compliance (WCAG 2.1 AA)

- All text contrast exceeds 4.5:1 (OnSurfaceForestDark `#143625` on Surface `#FFFFFF` achieves 13.2:1).
- Touch targets strictly $\ge 48\text{dp} \times 48\text{dp}$.
- Screen readers receive clear semantic content descriptions (e.g., `"Mascot Si Buba sedang menyapa"`, `"Putar audio pelafalan Mandarin untuk Jahe"`).
- Color is never the sole indicator of status (always paired with icon and text).

---

## 17. Performance Budget

- **Local UI response:** $\le 300\text{ms}$.
- **Cold start render:** $\le 800\text{ms}$.
- **Memory footprint:** Lightweight vector assets add $< 200\text{KB}$ to APK size.
- **Battery preservation:** Zero continuous background animation.

---

## 18. Files to Modify

| File Path | Nature of Modification |
|:---|:---|
| `app/src/main/java/id/bubakangreen/app/ui/theme/Color.kt` | Update to Palette Botani Ceria tokens |
| `app/src/main/java/id/bubakangreen/app/ui/theme/Type.kt` | Refine 6-tier typography scale |
| `app/src/main/java/id/bubakangreen/app/ui/theme/Theme.kt` | Bind updated color scheme |
| `app/src/main/java/id/bubakangreen/app/ui/components/BubaMascot.kt` | **NEW:** Vector mascot component & states |
| `app/src/main/java/id/bubakangreen/app/ui/components/TactileButton.kt` | **NEW:** Friendly tactile 3D-rim button |
| `app/src/main/java/id/bubakangreen/app/ui/components/PlantCard.kt` | Update to rounded tactile card |
| `app/src/main/java/id/bubakangreen/app/ui/components/LocationCard.kt` | Update with category styling |
| `app/src/main/java/id/bubakangreen/app/ui/components/FeaturedBanner.kt` | Update hero banner with mascot greeting |
| `app/src/main/java/id/bubakangreen/app/ui/components/SpeakerButton.kt` | Restyle speaker button with ripple & feedback |
| `app/src/main/java/id/bubakangreen/app/ui/components/StateEmptyView.kt` | Integrate Si Buba searching state |
| `app/src/main/java/id/bubakangreen/app/ui/components/StateErrorView.kt` | Integrate Si Buba resting state |
| `app/src/main/java/id/bubakangreen/app/ui/home/HomeScreen.kt` | Redesign to Educational Discovery Hub |
| `app/src/main/java/id/bubakangreen/app/ui/catalog/CatalogScreen.kt` | Redesign to Botanical Learning Library |
| `app/src/main/java/id/bubakangreen/app/ui/catalog/PlantDetailScreen.kt` | Redesign to Bite-sized Botanical Lesson |
| `app/src/main/java/id/bubakangreen/app/ui/locations/LocationsScreen.kt` | Redesign to Garden Exploration Guide |
| `app/src/main/java/id/bubakangreen/app/ui/locations/LocationDetailScreen.kt` | Restyle garden plot detail |

---

## 19. Files That MUST NOT Change

- `app/src/main/java/id/bubakangreen/app/data/**/*` (All Repositories, Data Sources, Firebase code)
- `app/src/main/java/id/bubakangreen/app/domain/**/*` (All Models, Repository interfaces, Domain rules)
- `app/src/main/java/id/bubakangreen/app/core/**/*` (Result sealed class, Audio Player engine)
- `app/src/main/java/id/bubakangreen/app/navigation/Screen.kt` (Core route structure preserved)
- `app/src/main/java/id/bubakangreen/app/ui/admin/**/*` (Admin approval dashboard retains clean governance UI)
- `app/src/main/java/id/bubakangreen/app/ui/pic/**/*` (PIC field capture forms retain utility ergonomics)
- `firestore.rules`, `firebase.json`, `assetlinks.json` (Security and App Link infrastructure)

---

## 20. Dependencies Impact

- **External Libraries Added:** **ZERO (0)**.
- Pure Jetpack Compose primitives (`Canvas`, `Path`, `animateFloatAsState`, `Modifier`) provide all mascot and tactile rendering.
- No heavy Lottie or raster animation dependencies.

---

## 21. Risks & Mitigations

| Risk | Likelihood | Impact | Mitigation Strategy |
|:---|:---:|:---:|:---|
| **App feels childish / trivialized** | Medium | High | Mascot copy is written in clear, dignified Indonesian; mascot appears on $\le 35\%$ of screens; admin/PIC forms remain clean tools. |
| **Accidental Duolingo visual clone** | Low | High | Strict prohibitions enforced: No owls, no feathers, no streak counters, no hearts, original seedling character, authentic Bubakan palette. |
| **Screen clutter on small displays** | Medium | Medium | Tested against 360dp devices; progressive disclosure hides secondary facts behind expandable sections. |
| **Performance drops on low-end phones** | Low | Medium | Mascot is rendered using vector math or optimized XML drawables; zero infinite recomposition loops. |

---

## 22. Acceptance Criteria

1. [ ] App launches with Palette Botani Ceria Bubakan (vibrant nature green, sunny gold, vanilla canvas).
2. [ ] Si Buba mascot appears organically on Home, Mandarin audio, Empty, and Error states.
3. [ ] Plant Detail screen presents bite-sized sections with high readability.
4. [ ] Mandarin Hanzi, Pinyin, and user-triggered audio work seamlessly with visual feedback.
5. [ ] No gamification mechanisms (XP, streak, lives) are introduced.
6. [ ] Urban Farming Kelurahan and Taman Toga RW 03 remain prominent and authentic.
7. [ ] All 36 unit tests pass with zero regressions.
8. [ ] `assembleDebug` builds cleanly with $\le 300\text{ms}$ local interaction latency.
