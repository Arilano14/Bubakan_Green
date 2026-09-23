# PHASE 3 IMPLEMENTATION PLAN — BUBAKAN GREEN

**Sistem Informasi Urban Farming & Taman Toga**  
**Kelurahan Bubakan, Kecamatan Mijen, Kota Semarang**  
**Document:** `docs/phase-3/PHASE_3_IMPLEMENTATION_PLAN.md`  
**Date:** 2026-09-23  
**Status:** DRAFT — AWAITING PRODUCT OWNER APPROVAL (`ACC PHASE 3`)  

---

## 1. Phase 2 Audit Result Summary

The Phase 2 Completion Audit (`docs/phase-3/PHASE_2_COMPLETION_AUDIT.md`) and Requirement Traceability Matrix (`docs/phase-3/PHASE_2_REQUIREMENT_TRACEABILITY.md`) confirm:
1. **Foundation Verified:** Root Gradle scaffold, Version Catalog (`libs.versions.toml`), AndroidManifest, decoupled domain entities (`Location`, `MasterPlant`, `LocationPlant`, `UserSession`), and Firestore repositories are cleanly implemented.
2. **Security Certified:** Declarative `firestore.rules` enforce public read-only access on published records and strict role isolation for PIC and Admin.
3. **Web Fallback Operational:** Static HTML fallback cards (<7KB) are in place at `web/public/`.
4. **Git Safety Intact:** Zero remote pushes executed; working tree is clean.
5. **Phase 2 Boundary Respected:** Zero premature UI screens were implemented in Phase 2.

---

## 2. Phase 3 Objective

Transform the approved Phase 1 Information Architecture, UX flows, and Design System tokens into the **production-grade Public Android Application**.

Phase 3 implements the public-facing user experience: discovering Kelurahan Bubakan's community gardens, exploring physical locations and plant collections, searching botanical knowledge, playing Mandarin pronunciation audio on demand, handling external QR deep links, and presenting resilient offline/loading/error states.

---

## 3. Exact Implementation Scope (Phase 3)

| Component / Screen | Screen ID | Role | Scope Details |
|:---|:---|:---:|:---|
| **App Navigation Shell** | `NAV-001` | Public | Standard 3-tab Bottom Navigation (`Beranda`, `Lokasi & Peta`, `Katalog`), Top App Bar, and Deep Link Uri routing. |
| **Home Screen** | `SCR-PUB-01` | Public | Official Kelurahan header, featured locations banner (`Urban Farming Kelurahan`, `Taman Toga RW 03`), quick category cards, program overview snippet. |
| **Location Directory & Map** | `SCR-PUB-02` | Public | Segmented toggle (`[Daftar]` vs `[Peta]`), category filter chips (`Semua`, `Urban Farming`, `Taman Toga`), location card grid, and provider-agnostic map visualizer. |
| **Location Detail Screen** | `SCR-PUB-03` | Public | Comprehensive garden profile (RW, address, description, coordinates badge, external map intent button) and list of plants physically grown at this location. |
| **Plant Catalog Screen** | `SCR-PUB-04` | Public | Debounced search bar (matching Indonesian, Latin, and medicinal benefits), filter chips, and instant botanical search results. |
| **Plant Detail Screen** | `SCR-PUB-05` | Public | Botanical encyclopedia card: Indonesian name, Latin name, Mandarin Hanzi (24sp), Pinyin, user-triggered Mandarin audio button `[ 🔊 ]`, pharmacological benefits, cultivation notes, and garden origin reference. |
| **About Screen / Modal** | `SCR-PUB-06` | Public | Information regarding Kelurahan Bubakan's urban farming program, QR code scanning guide, and app version metadata. |
| **QR Destination Handling** | `DEEP-001` | Public | Intent filter handling for `https://bubakangreen.web.app/plant/{id}` and `/location/{id}` routing directly to target screens. |
| **Resilient State Views** | `STATE-001` | Public | Non-intrusive `OfflineStatusBar` pill, `ShimmerCardPlaceholder` loading skeletons, `StateEmptyView`, and `StateErrorView` with retry action. |

---

## 4. Out of Scope (Strictly Prohibited in Phase 3)

- ❌ **PIC / Admin Feature Screens:** No `PicDashboardScreen`, no `LocationFormScreen`, no `PlantFormScreen`, no `LocationApprovalScreen`. (Reserved for Phase 5 & 6).
- ❌ **User Authentication Flow UI:** No `LoginScreen` or credential forms in Phase 3. (Reserved for Phase 5).
- ❌ **Camera & Image Upload:** No in-app photo capture, image picker, or image upload pipeline.
- ❌ **In-App QR Scanner:** General public uses native camera / Google Lens. No internal camera scanner.
- ❌ **Production QR Code Generation:** Printing and physical label export belong to Phase 5.
- ❌ **Synthetic Botanical Data:** No fictitious plants or fake addresses committed to database.
- ❌ **Remote Git Push:** `git push` is permanently prohibited.

---

## 5. Screen Implementation Order

```
1. Core UI Components & Theme Polish (Card, Chip, Shimmer, Error/Empty states)
   ↓
2. App Navigation Shell & Top Bar (3 Bottom Tabs + Scaffold)
   ↓
3. Screen 1: HomeScreen (SCR-PUB-01)
   ↓
4. Screen 2: LocationListMapScreen (SCR-PUB-02)
   ↓
5. Screen 3: LocationDetailScreen (SCR-PUB-03)
   ↓
6. Screen 4: PlantCatalogScreen (SCR-PUB-04)
   ↓
7. Screen 5: PlantDetailScreen (SCR-PUB-05) + Mandarin Audio Player
   ↓
8. Screen 6: AboutScreen (SCR-PUB-06)
   ↓
9. Deep Linking & QR Destination Integration
   ↓
10. Offline & Error State Verification
```

---

## 6. Navigation Architecture

### 6.1 Bottom Navigation Bar (Strict 3 Tabs)
Using `androidx.navigation.compose`:

```
┌────────────────────────────────────────────────────────┐
│  BUBAKAN GREEN                              [ℹ️ Info]  │ Top App Bar
├────────────────────────────────────────────────────────┤
│                                                        │
│                    SCREEN CONTENT                      │
│                                                        │
├────────────────────────────────────────────────────────┤
│   [ 🏠 Beranda ]    [ 📍 Lokasi ]    [ 🌿 Katalog ]    │ Bottom Navigation Bar
└────────────────────────────────────────────────────────┘
```

### 6.2 Navigation Graph Routes (`NavigationRoutes.kt`)
```kotlin
sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Locations : Screen("locations")
    data object LocationDetail : Screen("location/{locationId}") {
        fun createRoute(locationId: String) = "location/$locationId"
    }
    data object Catalog : Screen("catalog")
    data object PlantDetail : Screen("plant/{plantId}") {
        fun createRoute(plantId: String) = "plant/$plantId"
    }
    data object About : Screen("about")
}
```

### 6.3 Deep Link Intent Handling
`NavHost` registers deep link URI patterns matching verified App Link domains:
- `uriPattern = "https://bubakangreen.web.app/plant/{plantId}"` → Opens `PlantDetailScreen`
- `uriPattern = "https://bubakangreen.web.app/location/{locationId}"` → Opens `LocationDetailScreen`
- Backstack behavior: Pressing system Back from a QR-launched screen routes safely back to `HomeScreen` (Root).

---

## 7. State Management Architecture

Strict MVVM pattern with unidirectional data flow (UDF):

```
Composable UI ──(User Intent)──► ViewModel ──(Query)──► Repository
      ▲                              │                      │
      │                              ▼                      ▼
  StateFlow ◄──(UiState<T>)───── ViewModel ◄──(Flow)─── Firestore SDK / Cache
```

### Standardized Presentation State Wrapper
```kotlin
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T, val isFromCache: Boolean = false) : UiState<T>
    data class Empty(val message: String) : UiState<Nothing>
    data class Error(val message: String, val canRetry: Boolean = true) : UiState<Nothing>
}
```

---

## 8. Repository Usage in ViewModels

1. **`HomeViewModel`:**
   - Consumes `LocationRepository.getFeaturedLocations()`
   - Exposes `StateFlow<UiState<List<Location>>>`
2. **`LocationsViewModel`:**
   - Consumes `LocationRepository.getPublishedLocations()` and `getLocationsByType(type)`
   - Manages tab view mode: `List` vs `Map`
3. **`LocationDetailViewModel`:**
   - Consumes `LocationRepository.getLocationById(locationId)` and `PlantRepository.getPlantsAtLocation(locationId)`
   - Joins location instance with master plant metadata
4. **`PlantCatalogViewModel`:**
   - Consumes `PlantRepository.getAllMasterPlants()`
   - Implements debounced search query filtering (300ms) on `nameId`, `nameLatin`, and `description`
5. **`PlantDetailViewModel`:**
   - Consumes `PlantRepository.getMasterPlantById(plantId)`
   - Coordinates `AudioPlayer` state machine (`Idle`, `Loading`, `Playing`, `Error`)

---

## 9. Reusable UI Component Inventory

| Component Name | File | Description & Ergonomics |
|:---|:---|:---|
| **`BubakanTopBar`** | `ui/components/BubakanTopBar.kt` | Header displaying product title and `[ℹ️ Info]` action button. |
| **`LocationCard`** | `ui/components/LocationCard.kt` | Card displaying photo, garden name, RW badge, category chip, and plant count. Min touch target 48dp. |
| **`FeaturedLocationBanner`**| `ui/components/FeaturedBanner.kt` | Highlight card on Home for Urban Farming Kelurahan and Taman Toga RW 03. |
| **`PlantCard`** | `ui/components/PlantCard.kt` | Vertical/Grid card with plant photo, Indonesian name, Latin name (italic), and medicinal tag. |
| **`MandarinSpeakerButton`**| `ui/components/SpeakerButton.kt` | Circular 48x48dp interactive speaker button. Tapping triggers single-play audio. |
| **`CategoryFilterChip`** | `ui/components/FilterChip.kt` | Horizontal scrollable chip bar for category selection (`Semua`, `Urban Farming`, `Taman Toga`). |
| **`OfflineStatusBar`** | `ui/components/OfflineStatusBar.kt` | Subtle pill banner: `"Menampilkan data tersimpan (Mode Offline)"`. Non-intrusive. |
| **`StateEmptyView`** | `ui/components/StateEmptyView.kt` | Clean outline illustration, explanatory text, and action CTA (e.g. "Reset Pencarian"). |
| **`StateErrorView`** | `ui/components/StateErrorView.kt` | Human-readable error message with prominent `[ Coba Lagi ]` button. |
| **`ShimmerPlaceholder`** | `ui/components/ShimmerPlaceholder.kt`| Skeleton loading box simulating cards while Firestore streams initialize. |

---

## 10. Visual Direction & Design Strategy (Planta-Inspired, Bubakan-Owned)

Full specification available in [`docs/phase-3/VISUAL_DIRECTION.md`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/docs/phase-3/VISUAL_DIRECTION.md).

### 10.1 Visual Reference Analysis (Planta Adaptation)
We adapt the visual clarity, generous breathing room, and botanical dignity of **Planta (Plant & Garden Care by Planta AB)** without cloning its proprietary assets, layouts, typography, or monetization mechanics.
- **Plant-First Visual Hierarchy:** Botanical photography leads; botanical attributes take center stage over administrative statistics.
- **Clean, Natural Surfaces:** Off-white canvas (`#F8F9FA`) and pure white card surfaces (`#FFFFFF`) provide calm contrast.
- **Low Visual Noise:** Flat surfaces with delicate 1dp borders (`OutlineGrey` `#D0DBCE`) and 16dp rounded corners; zero heavy drop-shadows.
- **Generous Whitespace:** Strict 8-point grid with 16dp standard gutters and 24dp section spacing.
- **Content-First Ergonomics:** 100% open public access without registration gates or paywalls.

### 10.2 Bubakan Brand Strategy
- **Hierarchy:** PRIMARY: `BUBAKAN GREEN`; SECONDARY: `Kelurahan Bubakan, Kecamatan Mijen, Kota Semarang`.
- **Product Context:** Urban Farming, Taman Toga, Edukasi Tanaman, Informasi Lokasi, Pengetahuan Khasiat Herbal.
- **Tone:** Dignified, community-owned, educational civic utility. Not a student app, not a bureaucratic government dashboard, and not a generic consumer houseplant app.
- **Locality Integration:** Geographical context (RW numbers, garden names, local coordinate pins) is woven into real content, avoiding repetitive coat-of-arms clutter.

### 10.3 Bubakan Differentiation Strategy (vs Commercial Apps)
1. **Civic & Community Mission:** 100% free public access; zero login barriers for browsing, searching, or scanning QR codes.
2. **Physical Garden Anchoring:** Plants are linked to real community plots in Bubakan (`LocationPlant` -> `Location` e.g., RW 03).
3. **Dual Category Focus:** Distinct visual treatment for **Urban Farming** (food security / vegetables) and **Taman Toga** (family medicinal herbs).
4. **Trilingual Botanical Education:** Seamless integration of Indonesian common names, Scientific binomial Latin (*italic*), and Mandarin Hanzi + Pinyin + user-triggered audio pronunciation.
5. **Physical QR Code Touchpoints:** Deep links directly connected to physical weather-resistant signs in the community gardens.

### 10.4 Color Token Strategy (Palette Alam Bubakan)
Derived from Kelurahan Bubakan's lush natural landscape:
- **PrimaryForest (`#2D6A4F`):** Deep botanical green for primary actions, active tabs, and key headers.
- **PrimaryContainerMint (`#D8F3DC`):** Gentle mint wash for category chips and highlight cards.
- **OnPrimaryContainer (`#081C15`):** Deep forest contrast tone.
- **SecondarySage (`#52796F`):** Herbal sage green for secondary elements and Taman Toga accents.
- **BackgroundLight (`#F8F9FA`):** Warm botanical off-white canvas.
- **SurfaceWhite (`#FFFFFF`):** Crisp white card surface.
- **OnSurfaceDark (`#1B4332`):** Deep earthy charcoal/green text providing **12.8:1** contrast ratio (exceeds WCAG 2.1 AA requirement of 4.5:1).
- **OnSurfaceVariant (`#5B7065`):** Sage gray for scientific Latin names and secondary metadata.
- **OutlineGrey (`#D0DBCE`):** 1dp subtle garden border.
- **StatusVerifiedGreen (`#2D6A4F`) / StatusPendingOrange (`#E09F3E`):** Harvest amber for pending items.
- **ErrorRed (`#BA1A1A`):** Restrained warning red.

### 10.5 Typography Strategy
- **Material 3 Font Scale:** HeadlineLarge (`32sp` Bold), HeadlineMedium (`24sp` SemiBold), TitleMedium (`16sp` SemiBold), BodyLarge (`16sp` Regular), BodyMedium (`14sp` Regular).
- **Scientific Nomenclature:** Latin names formatted in *TitleSmall Italic* (`14sp`) in `OnSurfaceVariant` (`#5B7065`).
- **Mandarin Glyph Scale:** Chinese Hanzi displayed prominently at `24sp` using Noto Sans SC with Pinyin at `14sp`.

### 10.6 Photography Strategy
- **Authenticity First:** Real photography of Kelurahan Bubakan's community gardens and plants has top priority.
- **No Synthetic Slop:** Zero AI-generated fantasy images or fake stock photography claiming to be real Bubakan locations or plants.
- **Aspect Ratios:** Location heroes at `16:9`, plant cards at `4:3` or `1:1`, and plant detail heroes at `16:10`.
- **Graceful Loading:** Coil image loader with subtle crossfade and mint shimmer skeleton placeholder (`#D8F3DC`).

### 10.7 Component Visual System
- **`LocationCard`:** 16:9 photo, category badge (`Urban Farming` vs `Taman Toga`), RW indicator, plant count badge (`🌿 14 Koleksi Tanaman`), 16dp radius, 1dp outline.
- **`PlantCard`:** Clean botanical card with prominent photo, Indonesian common name, italic Latin name, and category chip.
- **`MandarinSpeakerButton`:** 48x48dp circular touch target, manual tap only, single-play audio, auto-resets to idle. Zero autoplay.
- **Card Styling Rule:** Low visual noise, 16dp rounded corners, flat surface `#FFFFFF`, 1dp border `#D0DBCE`, zero drop shadows.

### 10.8 Visual QA Checklist (Pre-Release Audit)
All screens must pass the 20 visual criteria detailed in [`docs/phase-3/VISUAL_DIRECTION.md`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/docs/phase-3/VISUAL_DIRECTION.md#10-visual-qa-checklist-mandatory-pre-release-audit) before final acceptance.

---

## 11. Offline Behavior

1. **Persistence:** Leverages Phase 2 configured Firestore disk cache (100MB).
2. **Behavior on Launch:** If device is offline, previously cached locations and plants render immediately.
3. **Indicator:** `OfflineStatusBar` appears smoothly below the Top Bar without obstructing content or blocking touch events.
4. **Audio Degradation:** If the audio file for a plant has not been cached, tapping `[ 🔊 ]` displays a transient Snackbar: `"Audio memerlukan koneksi internet."` Zero app crashes.

---

## 12. Error, Empty & Loading State Rules

- **Zero Technical Errors:** Exception messages (`NullPointerException`, `503 Unavailable`) are strictly trapped and translated into friendly Indonesian guidance.
- **No Infinite Spinners:** Loading is communicated via card shimmers. If loading exceeds 10 seconds, a fallback retry option is presented.
- **Actionable Empty States:** Every empty screen provides a recovery path (e.g. "Reset filter", "Muat ulang").

---

## 13. QR / Deep-Link Destination Experience

1. Incoming URI: `https://bubakangreen.web.app/plant/{plantId}`
2. Android OS triggers `MainActivity` via verified App Link intent filter.
3. Compose Navigation extracts `{plantId}` argument.
4. `PlantDetailScreen` is pushed to top of backstack.
5. Back navigation routes to `HomeScreen`.
6. Zero in-app scanner camera screens created.

---

## 14. Mandarin Audio Abstraction & Implementation

- **Architecture:** `AndroidAudioPlayer` implementing an `AudioPlayer` interface using Android's native `MediaPlayer`.
- **Interaction Contract:**
  - Default: `Idle` (Green speaker icon `[ 🔊 ]`).
  - User taps: State transitions to `Loading` (small 16dp spinner) -> `Playing` (pulsing volume icon).
  - Audio plays once (1–3 seconds) -> OnCompletionListener automatically transitions state back to `Idle`.
  - **Strict Prohibition:** NO autoplay on screen enter, NO looping, NO automatic whole-page narration.

---

## 15. Map Implementation Decision (Phase 3)

In accordance with Phase 0/1 ADR-004 and the provider-agnostic container design:
1. **In Tab 2 (`Lokasi & Peta`):** The primary view is the high-performance **Daftar Kebun** (List View).
2. **Segmented Switch (`[Daftar] | [Peta]`):**
   - Toggling to `[Peta]` displays a clean visual map container showing location pins with coordinates, garden types, and a floating preview card.
   - For directions, tapping `[Buka di Google Maps]` sends an explicit Android `geo:lat,lng` Intent to the user's installed Google Maps / Maps app.
   - This eliminates the immediate requirement for a paid Google Maps SDK billing account while providing full spatial utility to residents.

---

## 16. Test Strategy (Phase 3)

1. **ViewModel Unit Tests:**
   - Test `PlantCatalogViewModel` debounced search filtering.
   - Test `PlantDetailViewModel` audio player state transitions.
   - Test `HomeViewModel` featured location filtering.
2. **Deep Link Parsing Tests:**
   - Verify URI parsing for valid and malformed plant/location IDs.
3. **Accessibility Audit:**
   - Verify all interactive icons have meaningful Indonesian `contentDescription`s.
   - Verify touch targets maintain min 48dp bounding box.

---

## 17. Build Verification Strategy

- Execute `./gradlew.bat compileDebugKotlin` to verify Compose compiler passes with 0 errors.
- Execute `./gradlew.bat test` to verify unit test assertions pass.
- Verify APK size remains compact and performant.

---

## 18. Acceptance Criteria (Phase 3)

- [ ] 3-tab Bottom Navigation operates smoothly with backstack preservation.
- [ ] `HomeScreen` displays official Kelurahan identity and featured garden cards.
- [ ] `LocationListMapScreen` renders all published gardens with type filter chips.
- [ ] `LocationDetailScreen` displays garden metadata, coordinates, and plant collection.
- [ ] `PlantCatalogScreen` provides instant debounced botanical search.
- [ ] `PlantDetailScreen` displays complete botanical knowledge, Hanzi, Pinyin, and manual `[ 🔊 ]` audio button.
- [ ] Mandarin audio plays only upon intentional tap, plays once, and stops. Zero autoplay.
- [ ] Deep links (`https://bubakangreen.web.app/plant/{id}`) navigate directly to `PlantDetailScreen`.
- [ ] Offline status banner displays gracefully when disconnected.
- [ ] Zero fake Bubakan botanical data committed.
- [ ] Zero Git push commands executed.

---

## 19. Risks & Mitigations

| Risk | Impact | Technical Mitigation |
|:---|:---|:---|
| Network latency loading botanical images | Stutter / empty boxes | Use Coil image loader with memory/disk caching and crossfade animation. |
| Incomplete plant data from field | Broken UI fields | Nullable fields (e.g. Hanzi, Pinyin, audio) are hidden cleanly without blank gaps. |
| Memory leak in audio playback | Battery / audio stutter | `MediaPlayer` is released immediately in `onCleared()` lifecycle callback of ViewModel. |

---

## 20. Stop Conditions

Phase 3 execution will immediately stop and trigger a `CHANGE_REQUEST.md` if:
1. An unexpected paid SDK or billing account is required.
2. An architectural conflict forces a redesign of Phase 2 domain entities.
3. A scope addition (e.g. in-app QR scanner, user registration) is demanded.

---

## 21. Files Expected to Change / Be Created

```
app/
├── build.gradle.kts                          [Add navigation-compose and coil]
├── src/main/java/id/bubakangreen/app/
│    ├── MainActivity.kt                      [Set up NavHost]
│    ├── core/
│    │    └── audio/
│    │         ├── AudioPlayer.kt             [Interface & state model]
│    │         └── AndroidAudioPlayer.kt      [MediaPlayer implementation]
│    ├── navigation/
│    │    ├── NavigationRoutes.kt             [Route definitions]
│    │    └── BubakanNavHost.kt               [NavHost & deep link mapping]
│    ├── ui/
│    │    ├── components/
│    │    │    ├── BubakanTopBar.kt
│    │    │    ├── LocationCard.kt
│    │    │    ├── PlantCard.kt
│    │    │    ├── SpeakerButton.kt
│    │    │    ├── FilterChip.kt
│    │    │    ├── OfflineStatusBar.kt
│    │    │    ├── StateEmptyView.kt
│    │    │    ├── StateErrorView.kt
│    │    │    └── ShimmerPlaceholder.kt
│    │    ├── home/
│    │    │    ├── HomeScreen.kt
│    │    │    └── HomeViewModel.kt
│    │    ├── locations/
│    │    │    ├── LocationsScreen.kt
│    │    │    ├── LocationsViewModel.kt
│    │    │    ├── LocationDetailScreen.kt
│    │    │    └── LocationDetailViewModel.kt
│    │    ├── catalog/
│    │    │    ├── CatalogScreen.kt
│    │    │    ├── CatalogViewModel.kt
│    │    │    ├── PlantDetailScreen.kt
│    │    │    └── PlantDetailViewModel.kt
│    │    └── about/
│    │         └── AboutScreen.kt
```

---

## 22. Dependencies Required in Phase 3

| Dependency | Purpose | Justification |
|:---|:---|:---|
| `androidx.navigation:navigation-compose:2.8.5` | Multi-screen Compose navigation & deep link argument extraction. | Official Google standard for Compose navigation. |
| `io.coil-kt:coil-compose:2.7.0` | Asynchronous image loading with disk/memory caching for garden and plant photos. | Standard, lightweight, Kotlin-first Compose image loader. |

---

## 23. Dependencies Explicitly Rejected

- ❌ `firebase-storage`: Media upload remains deferred.
- ❌ In-app QR scanner libraries (`zxing`, `mlkit-barcode-scanning`): Banned; native camera is used.
- ❌ Complex animation libraries (`lottie`): Banned; standard Compose transitions are sufficient.
- ❌ Chat / social libraries: Banned by anti-slop rules.
