# Bubakan Green — Comprehensive UX/UI Audit Report

**Date:** 2026-10-01  
**Auditor:** Senior Android UI/UX Engineer, Product Designer & Mobile Architecture Reviewer  
**Target Platform:** Android (Compose, Material 3)  
**Reference Design Language:** Duolingo-inspired Educational Eco Application (Character-Driven, Friendly, Botanical Paper Aesthetic)

---

## 1. Executive Summary & Design Principles Applied

This audit evaluates the Bubakan Green Android application across 11 core screens and navigation flows against international human-computer interaction standards:
- **Nielsen Norman Usability Heuristics** (Visibility of system status, Consistency and standards, Recognition rather than recall, Flexibility and efficiency of use).
- **Fitts's Law** (Touch target sizes $\ge 48\text{dp}$, placement of critical interactive targets within the thumb zone).
- **Hick's Law** (Minimizing choices per screen to prevent cognitive overload, particularly in administrative and catalog navigation).
- **Jakob's Law** (Familiar mobile patterns for bottom bars, backstack behaviors, and form submissions).
- **Gestalt Principles** (Proximity, similarity, and figure-ground contrast in card surfaces).
- **Mobile-First Principles** (Responsive layouts across 360dp, 412dp, and 600dp+ viewport widths).

---

## 2. Screen-by-Screen Detailed Audit

### 2.1 Home Screen (`HomeScreen`)
- **Identified Issues:**
  - *Hero Composition:* The hero header combines an open green container with text and mascot side-by-side. On small screens (360dp width), text wraps aggressively and creates vertical misalignment.
  - *Hardcoded Colors:* Uses raw `Color.White` with varying alpha values (`0.85f`, `0.6f`) rather than centralized theme tokens.
  - *Inconsistent Card Geometry:* Garden cards use 20dp corners, while action buttons use 16dp and icon containers use 12dp.
  - *Gestalt Grouping:* The transition between "Jelajah Kebun" and "Kenalan dengan Tanaman" lacks distinct visual rhythm, causing content to blend together into a single monotonous scroll.
- **Refactoring Strategy:**
  - Standardize surface cards with 20–24dp corner radii and soft elevation.
  - Apply adaptive typography using `MaterialTheme.typography` and centralized `Dimensions` spacing.
  - Hero mascot with subtle breathing/floating micro-motion to immediately establish a playful, friendly personality.

---

### 2.2 Explore Location Screen (`LocationsScreen`)
- **Identified Issues:**
  - *Tab & Filter Proximity:* The top category filter chips and search bar compete for vertical space.
  - *Card Information Density:* Location cards have mixed badge styling (RW pill, verified status, photo aspect ratio) causing visual noise.
  - *Empty State Presentation:* When a filter yields no results, the layout leaves awkward white space with minimal guidance.
- **Refactoring Strategy:**
  - Introduce character-led empty state using `MascotCard` (`mascot_thinking`) with clear recovery CTA ("Lihat Semua Lokasi").
  - Clean card surface with standardized 20dp radius, consistent badge pill, and 48dp minimum touch targets for navigation.

---

### 2.3 Plant Catalog Screen (`CatalogScreen`)
- **Identified Issues:**
  - *Visual Hierarchy:* The search input is visually heavy, drawing attention away from the plants.
  - *Card Interaction:* Plant grid cards have varying text lengths that can cause uneven card heights if not bounded.
  - *Bilingual Text:* Mandarin characters and Pinyin tones need clear typographical separation from Latin and Indonesian names.
- **Refactoring Strategy:**
  - Educational card layout: High-contrast image thumbnail, botanical family badge, Latin nomenclature in italic with gentle tone, and trilingual badge.
  - Friendly search bar with rounded pill style (`52dp` height) and responsive clear button.

---

### 2.4 Plant Detail Screen (`PlantDetailScreen`)
- **CRITICAL UX VIOLATION IDENTIFIED (Fitts's Law & Ergonomics):**
  - The completion button (`"Selesai Mengenal Tanaman Ini ✨"`) is located at the very bottom of a long scrollable column (lines 384–388).
  - *Problem:* A user must scroll past high-resolution photos, taxonomy, Mandarin pronunciation pods, botanical characteristics, common uses, cultivation notes, and location maps before they can tap the CTA. When the lesson completes, the mascot appears at the bottom of the scroll, requiring more scrolling.
- **Refactoring Strategy:**
  - **Fixed Bottom Action Bar:** Move the primary CTA into `Scaffold(bottomBar = { ... })` using a frosted/elevated surface. The button remains pinned and immediately tappable at all times regardless of scroll depth.
  - **Interactive Learning Flow:** When the user taps the fixed CTA, trigger a celebration dialog/state featuring `MascotType.HAPPY` with cheerful Duolingo-style educational encouragement.

---

### 2.5 Location Detail Screen (`LocationDetailScreen`)
- **Identified Issues:**
  - *Buried Action Target:* The "Buka Petunjuk Arah Google Maps" button is nested inside an inner card at the bottom of the identity block.
  - *Header Contrast:* The hero image has no subtle bottom scrim, occasionally making overlaid RW badges low-contrast against light images.
- **Refactoring Strategy:**
  - Pinned or high-priority CTA for navigation directions.
  - Rounded container cards (22dp) separating garden description, GPS verification badge, and plant inventory.

---

### 2.6 Admin Dashboard Screen (`AdminDashboardScreen`)
- **CRITICAL UX VIOLATION IDENTIFIED (Cognitive Overload & Desktop CRUD Anti-Pattern):**
  - Currently contains 1157 lines of dense tables, modal dialogs, tab rows, and micro-buttons (`Edit`, `Delete`, `Condition`, `History`, `Plants`).
  - *Problem:* Violates Hick's Law and mobile-first touch ergonomics. On a mobile phone, dense multi-column tabular data causes accidental taps, horizontal overflowing, and severe cognitive strain.
- **Refactoring Strategy:**
  - **Mobile-First Hub Design:** Replace dense tables with a friendly character-driven command center.
  - **Greeting Header:** Mascot welcoming the admin:
    > "Halo Admin 🌱\nKelola kebun Bubakan"
  - **Large Action Cards (22dp radius, tactile feedback):**
    1. **Kelola Lokasi:** Quick overview of active gardens, add garden, update status.
    2. **Kelola Tanaman:** Manage botanical master encyclopedia, view approved species.
    3. **Persetujuan (Approval Queue):** Pending garden proposals awaiting kelurahan review.
  - High-level metric pills and clear exit action.

---

### 2.7 Management Forms (`LocationFormScreen`, `PlantFormScreen`, `MasterPlantFormScreen`)
- **CRITICAL UX VIOLATION IDENTIFIED (Soft Keyboard Occlusion):**
  - Submit buttons are located at the bottom of the `verticalScroll` container.
  - *Problem:* When text fields are focused, the on-screen soft keyboard pops up and completely hides the submit button. Users are confused whether their input was registered and have to tap back or manually swipe down.
- **Refactoring Strategy:**
  - Wrap forms in `Scaffold(bottomBar = { Surface(...) { PrimaryButton(...) } })` with fixed bottom action area outside the scroll container.
  - Touch target size: `54dp` height, pill shape, high contrast.

---

### 2.8 Authentication Screen (`LoginScreen`)
- **Identified Issues:**
  - Uses a generic lock icon in a square surface, feeling like a standard administrative portal rather than a community eco-education application.
  - Form fields lack the friendly rounded geometry of the Bubakan Green brand.
- **Refactoring Strategy:**
  - Feature `Mascot(type = MascotType.GREETING)` welcoming the user warmly.
  - Pill-shaped input fields with gentle green focus indicators.
  - Fixed-width responsive form container bounded for comfortable thumb typing.

---

### 2.9 Navigation Shell (`BubakanNavHost` & Bottom Navigation)
- **CRITICAL UX VIOLATION IDENTIFIED (Inconsistent Footer & Missing Core Destinations):**
  - The previous footer had only 3 items (`Home`, `Locations`, `Catalog`), while Admin was hidden deep inside the "Tentang" screen at the bottom of a card.
  - Tapping Admin required 3 disjointed navigations (Home -> Info Icon -> About Screen -> Scroll to Bottom -> Admin Button).
- **Refactoring Strategy:**
  - **5-Item Universal Bottom Navigation (`AppBottomBar`):**
    1. **Admin** (`Icons.Default.AdminPanelSettings`) — Direct left-anchor entry for authenticated municipal managers.
    2. **Jelajah** (`Icons.Default.Place` / `Explore`) — Community garden locations and interactive map directory.
    3. **Beranda** (`Icons.Default.Home`) — Center home anchor with elevated brand pill.
    4. **Katalog** (`Icons.Default.Search` / Botanical Encyclopedia) — Trilingual plant database.
    5. **Profil** (`Icons.Default.Person` / `Info`) — Civic program overview, credits, and version metadata.
  - Active state animation with indicator pill, smooth transitions, and minimum 48dp touch targets.

---

## 3. Summary of Refactoring Actions

| Component / Screen | Current State | Required Refactor |
|:---|:---|:---|
| **Color System** | Ad-hoc aliases & hardcoded values | Eco-learning palette: Forest Green, Leaf Green, Warm Yellow, Soft Orange, Warm Cream Canvas. |
| **Typography** | Generic typography | Friendly rounded hierarchy, bold headings, short clear copy. |
| **Dimensions** | Hardcoded `dp` values across screens | Centralized `Dimensions.kt` and `MaterialTheme.spacing`. |
| **Mascots** | Scattered single-use instances | Reusable `MascotCard.kt` with START/END/CENTER positions, SMALL/MEDIUM/LARGE sizes, and 60 FPS breathing motion. |
| **Buttons** | Varying heights (48–52dp) and radii | Standardized Primary (54dp pill, strong contrast) and Secondary (outlined). |
| **Detail & Form CTAs**| Moving with scroll, hidden behind keyboard | Pinned in `Scaffold.bottomBar` outside the scroll viewport. |
| **Bottom Navigation** | 3 items, hidden Admin | Unified 5-item `AppBottomBar` with Admin, Explore, Home, Catalog, Profile. |
| **Admin Dashboard** | Dense desktop-style data tables | Large mobile-first cards with mascot greeting header. |
