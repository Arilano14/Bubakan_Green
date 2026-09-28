# MASCOT CONCEPT PROPOSAL: ORIGINAL BUBAKAN GREEN COMPANION
## Sistem Informasi Urban Farming & Taman Toga Kelurahan Bubakan

**Document:** `docs/visual-redesign/MASCOT_CONCEPT.md`  
**Phase:** Visual & Educational UX Redesign  
**Status:** PROPOSED CONCEPTS — AWAITING ACC VISUAL REDESIGN  
**Mandate:** Propose 3 original botanical mascot concepts; recommend 1 coherent direction; strictly prohibit Duo/owl derivatives; avoid gamification clutter; respect civic dignity of Kelurahan Bubakan.

---

## 1. Executive Mascot Philosophy

The mascot of **BUBAKAN GREEN** functions as an **Educational Botanical Companion** (*"Teman Belajar Tanaman"*):
- It welcomes residents and visitors to the community gardens.
- It guides attention to botanical nomenclature, herbal benefits, and Mandarin pronunciation.
- It offers constructive encouragement during discovery (e.g., QR scanning, search results, empty states).

### 1.1 Core Personality Guardrails
- **Curious & Attentive:** Fascinated by soil, rain, chlorophyll, and traditional herbal remedies.
- **Friendly & Approachable:** Warm and polite, using encouraging Indonesian language (*Bahasa Indonesia yang santun dan bersahabat*).
- **Environmentally Conscious:** Passionate about local food security (*Urban Farming*) and family health (*Taman Toga*).
- **Zero Sarcasm or Guilt-Tripping:** Never mocks the user, never guilts the user for inactivity (no *"Kamu belum belajar hari ini!"* pressure).
- **No Baby-Talk or Slang Overload:** Clear, dignified, educational speech suitable for youth, mothers (Ibu-Ibu PKK), and community elders.

---

## 2. The 3 Original Mascot Concepts

```
┌───────────────────────────┬───────────────────────────┬───────────────────────────┐
│        CONCEPT A          │        CONCEPT B          │        CONCEPT C          │
│   "SI BUBA" (THE SPROUT)  │   "RANI" (THE RHIZOME)    │   "KANO" (THE LEAF BUDDY) │
├───────────────────────────┼───────────────────────────┼───────────────────────────┤
│ Concept: Young Seedling   │ Concept: Golden Ginger    │ Concept: Heart Herbal Leaf│
│ Focus: Farming & Toga     │ Focus: Toga Herbal Roots  │ Focus: Foliage & Discovery│
│ Form: Teardrop + Leaf Ears│ Form: Knobby Golden Root  │ Form: Sirih Leaf Outline  │
│ Tone: Inquisitive, Lively │ Tone: Nurturing, Grounded │ Tone: Scout, Adventurous  │
└───────────────────────────┴───────────────────────────┴───────────────────────────┘
```

---

### Concept A: "Si Buba" — Tunas Hijau Bubakan *(Recommended)*

#### A.1 Visual Form & Silhouette
- **Silhouette:** A plump, friendly organic teardrop silhouette resembling a freshly sprouted seed emerging from fertile volcanic soil.
- **Key Distinguishing Features:**
  - Two expressive, spoon-shaped green leaves on top of its head that act as expressive "ears" (they perk up with excitement, tilt when curious, or droop slightly in empty/help states).
  - A subtle warm terracotta seed-coat base (a natural earthen waistline) that gives it visual weight and prevents it from floating disconnectedly.
  - Big, friendly round eyes with bright white specular highlights and a wide, gentle smile.
  - **Zero bird/owl features:** No beak, no wings, no feathers, no claws. Pure botanical botany.

#### A.2 Personality
- Enthusiastic learner, proud of Bubakan's green gardens, always eager to inspect a leaf or hear a new plant name in Mandarin.

#### A.3 Botanical & Civic Connection
- Represents the starting point of all agriculture: **germination**. Fits both **Urban Farming** (sprouting vegetables: kangkung, bayam, cabai) and **Taman Toga** (young herbal shoots).
- Direct homage to the fertile earth of Kelurahan Bubakan, Kecamatan Mijen.

#### A.4 Color Palette
- Head & Body: `PrimarySeedlingGreen` (`#1E7B4D`) with soft mint belly highlight (`#E2F7EC`).
- Leaf Ears: Vibrant lime sprout (`#52B788`).
- Base / Pot accent: Warm terracotta amber (`#E07A5F`).
- Cheeks: Soft sunny blush (`#FFB703`).

#### A.5 Expression & State Matrix
1. **Greeting (Home):** Waving cheerfully from behind the search bar or hero banner.
2. **Mandarin Audio (Plant Detail):** Wearing a small audio headset or tilting its leaf-ears attentively with soundwaves.
3. **Empty State:** Peeking into an empty garden pot with a magnifying glass: *"Belum ada tanaman di sini. Yuk, mulai tanam!"*
4. **Error State:** Holding a small watering can with a thoughtful expression: *"Koneksi terputus. Buba sedang menunggu air mengalir kembali."*
5. **QR Scan Success:** Sprouting little celebratory sparkles: *"Tanaman ditemukan di kebun RW 03!"*

#### A.6 Technical Scalability & Risks
- **Scalability:** Extremely high. The bold geometric teardrop and leaf-ear silhouette remain instantly recognizable at `24dp` favicon size, `48dp` chip size, or `160dp` hero illustration.
- **Production Complexity:** Low. Composed of clean bezier curves; easily rendered via Compose `Canvas`, vector drawable, or lightweight WebP/SVG.
- **Risks:** Needs distinct leaf-ear proportions so it is not mistaken for a generic clover.

---

### Concept B: "Rani" — Rimpang Berani (The Golden Rhizome)

#### B.1 Visual Form & Silhouette
- **Silhouette:** A sturdy, warm golden-amber ginger rhizome (*Zingiber officinale*) with rounded, knobby arms and an emerald sprout crown on top.
- **Key Distinguishing Features:**
  - Warm golden-yellow body with soft organic segment lines like fresh ginger or curcuma.
  - A small crown of three pointed herbal leaves sprouting from its top.
  - Warm, comforting, nurturing eyes with a knowing, maternal/friendly expression.

#### B.2 Personality
- Grounded, wise, gentle, nurturing. Acts like a friendly herbalist guide who knows traditional remedies for colds, digestion, and vitality.

#### B.3 Botanical & Civic Connection
- Directly honors **Taman Toga RW 03**, where ginger, red ginger (*jahe merah*), and curcuma (*temulawak*) are flagship communal plants.

#### B.4 Color Palette
- Body: Warm Golden Ginger (`#FFB703` & `#F4A261`).
- Crown: Deep Forest Green (`#2D6A4F`).
- Segment outlines: Earthen sienna (`#9A3412`).

#### B.5 Technical Scalability & Risks
- **Scalability:** Medium. Rhizome segment lines can look cluttered at small resolutions (<32dp).
- **Production Complexity:** Medium. Irregular organic silhouettes require more path detail.
- **Risks:** Strongly emphasizes Taman Toga (medicinal herbs), but under-represents leafy vegetable Urban Farming.

---

### Concept C: "Kano" — Daun Rindang (The Leaf Scout)

#### C.1 Visual Form & Silhouette
- **Silhouette:** A heart-shaped broadleaf character inspired by *Piper betle* (Daun Sirih) or Moringa (*Daun Kelor*), walking with sturdy little root-feet.
- **Key Distinguishing Features:**
  - Broad heart-leaf face with prominent, friendly leaf veins that form natural smile lines.
  - Carries a small crossbody canvas seed-bag across its chest.

#### C.2 Personality
- Energetic field explorer, loves mapping garden plots, recording coordinates, and finding hidden herbs.

#### C.3 Botanical & Civic Connection
- Represents the foliage, shade (*rindang*), and medicinal leaves ubiquitous throughout household gardens in Mijen.

#### C.4 Color Palette
- Body: Leaf Herbal Green (`#40916C`).
- Veins: Pale mint (`#D8F3DC`).
- Bag: Natural canvas straw (`#E9D8A6`).

#### C.5 Technical Scalability & Risks
- **Scalability:** Moderate. Crossbody strap and root-feet become indistinct at small sizes.
- **Production Complexity:** Medium to high.
- **Risks:** The thin leaf edge can look flat or paper-like if not shaded carefully.

---

## 3. Coherent Recommendation & Rationale

### Recommendation: **CONCEPT A — "SI BUBA" (Tunas Hijau Bubakan)**

```
             ╭─────────╮
           🌿│ Halo!   │
          (•‿•)╰─────────╯
         /|   |\
        / |   | \
       (  \___/  )
        \_______/
```

### Why Concept A is the Superior Choice:
1. **Holistic Coverage:** A seedling sprout is the universal symbol for **both** Urban Farming (food cultivation) and Taman Toga (medicinal herbs). Concept B is too rhizome-specific; Concept C is too leaf-specific.
2. **Iconic Geometric Simplicity:** A teardrop base with two leaf-ears is an iconic silhouette that can be rendered crisply in pure Kotlin Jetpack Compose Canvas or vector XML without relying on multi-megabyte bitmap sprite sheets.
3. **Expressive Anatomical Utility:** The dual leaf-ears provide instant visual state feedback (perked up for audio, angled for questions, joyful for discovery) without requiring complex humanoid limbs.
4. **Complete Separation from Duolingo:** Completely devoid of avian, beak, or owl motifs. It is unmistakably a botanical seedling character belonging to Bubakan Green.
5. **Community Acceptance:** Adorable without being childish; perfectly relatable to children, students, PKK cadres, and elderly community members.

---

## 4. Mascot Governance & Anti-Fatigue Rules

To maintain educational dignity and prevent user annoyance:
1. **Screen Frequency Limit:** Si Buba must appear on **no more than 35%** of user touchpoints.
   - ✅ Allowed: Home Welcome Hero, Plant Detail Mandarin Pronunciation, Empty Search Results, Network Error Dialog, QR App Link Discovery Banner.
   - ❌ Forbidden: Top app bar, every list item, form input fields, admin dashboard, map markers, settings screens.
2. **Animation Constraint:** Static SVG/vector by default. Reactive motion is strictly user-triggered (e.g., gentle nod when tapping audio). No perpetual looping bounce.
3. **Voice Consistency:** All speech bubble copy must be written in grammatically clear, warm Indonesian.
