# QR Management Specification & Signage Semantics — Bubakan Green

**Product**: BUBAKAN GREEN (Sistem Informasi Urban Farming & Taman Toga Kelurahan Bubakan)  
**Status**: DRAFT SPECIFICATION (PHASE 6)  
**Target Entities**: Master Plant, Garden Location  

---

## 1. Zero Binary Storage Contract (Section 26)

### 1.1 Architectural Rule
**QR code image bitmaps (PNG/JPEG) MUST NEVER be stored in Cloud Firestore or Firebase Storage.**

1. **Why Prohibited**:
   - Storing base64 strings or binary blobs in Firestore bloats document sizes, explodes document read bandwidth, and incurs storage costs.
   - Bitmaps stored in the database become stale if the rendering style, quiet zone, or resolution requirements change.
2. **Standard Implementation**:
   - The database stores **ONLY**:
     - `id`: Stable alphanumeric identifier (e.g., `pl-sereh-a1b2c3`, `loc-urban-farming-01`)
     - `isPublished`: Boolean visibility flag
     - `status`: Lifecycle state (`ACTIVE`, `PUBLISHED`, `DRAFT`, etc.)
   - QR Bitmaps are generated **dynamically on demand** on the client device using ZXing Core (`QrCodeGenerator.generateQrBitmap()`) at the required resolution (640x640px for UI preview, vector SVG for printing).

---

## 2. QR Signage Semantics & Physical Placement (Section 27 & 28)

Physical signage in Kelurahan Bubakan serves two distinct educational and civic functions:

```
                  PHYSICAL SIGNAGE DEPLOYMENT
                               │
        ┌──────────────────────┴──────────────────────┐
        ▼                                             ▼
  GARDEN ENTRANCE SIGN                        INDIVIDUAL PLANT LABEL
  (Plang Pintu Masuk Kebun)                   (Papan Akrilik / Kayu Tanaman)
        │                                             │
        ▼                                             ▼
  Location QR (`/location/<id>`)               Plant QR (`/plant/<id>`)
        │                                             │
        ▼                                             ▼
  Native `LocationDetailScreen`                Native `PlantDetailScreen`
  - Garden Name & RW                          - Botanical & Local Name
  - GPS Coordinates & Directions              - Mandarin Pronunciation & Audio
  - Complete Plant Inventory                  - Care & Cultivation Guide
```

### 2.1 Entrance Signage: Garden Location QR
- **Physical Placement**: Garden entrance gate, welcome board, or RW information post.
- **Canonical Pattern**: `https://bubakangreen.web.app/location/<stable-id>`
- **Expected User Experience**:
  - Scanning provides a complete overview of the garden plot: which RW manages it, contact PIC, verified coordinates, and all plant species planted in this garden.

### 2.2 Plot Signage: Individual Plant QR
- **Physical Placement**: Directly in front of the plant bed, pot, or polybag (acrylic stake or varnished wooden peg).
- **Canonical Pattern**: `https://bubakangreen.web.app/plant/<stable-id>`
- **Expected User Experience**:
  - Scanning immediately displays botanical knowledge, cultural notes, benefits, and optional user-triggered Mandarin audio pronunciation.
- **MVP Decision on Master vs Location Plant**:
  - For the Kelurahan Bubakan MVP, plant signages link directly to the **Master Plant encyclopedia resource** (`/plant/<master-plant-id>`). This ensures that visitors scanning Sereh in RW 01 or RW 03 access the complete educational profile, while the profile links back to all gardens where this species is cultivated.

---

## 3. Administrative QR Management UX (Section 25)

The Admin / PIC management interface provides lightweight, context-aware QR actions without creating a bloated secondary CMS:

```
┌─────────────────────────────────────────────────────────────┐
│  Detail Tanaman / Lokasi (Admin View)                       │
├─────────────────────────────────────────────────────────────┤
│  Status Publikasi:  ● AKTIF & TERVERIFIKASI                 │
│                                                             │
│  [  Kode QR Tersedia  ✓  ]                                  │
│                                                             │
│  ┌──────────────┐   ┌──────────────┐   ┌─────────────────┐  │
│  │   Lihat QR   │   │  Salin Link  │   │  Cetak Label ↗  │  │
│  └──────────────┘   └──────────────┘   └─────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

1. **State: Belum Memenuhi Syarat (`isPublished == false` or `DRAFT`)**:
   - Status badge: `DRAFT / BELUM DIPUBLIKASI`
   - Notice: *"Kode QR hanya dapat dibuat untuk data yang telah dipublikasikan secara resmi."*
   - Generate button is disabled to prevent printing invalid signages.
2. **State: Memenuhi Syarat (`isPublished == true`)**:
   - Badge: `✓ RESMI TERVERIFIKASI`
   - Canonical Link preview: `https://bubakangreen.web.app/plant/<id>`
   - Actions:
     - **Lihat QR**: Opens in-app `QrCodeDisplayDialog` with high-contrast botanical QR bitmap.
     - **Salin Link**: Copies canonical HTTPS URL to clipboard.
     - **Cetak Label**: Navigates to printable high-resolution label layout (standard 8x5cm field label format).
