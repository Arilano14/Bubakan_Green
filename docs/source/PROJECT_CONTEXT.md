# PROJECT CONTEXT — BUBAKAN GREEN

**Sistem Informasi Urban Farming & Taman Toga**  
**Kelurahan Bubakan, Kecamatan Mijen, Kota Semarang**

**Date:** 2026-09-23  
**Purpose:** Establish the verified product baseline from project documents and product owner input.

---

> [!IMPORTANT]  
> This document captures what is **known** about the project from official sources, product owner direction, and project planning materials. It is the source of truth that all other Phase 0 documents must reference.

---

## Product Identity

| Item | Value | Source |
|------|-------|--------|
| Product Name | BUBAKAN GREEN | [USER-PROVIDED] |
| Product Type | Sistem Informasi Urban Farming & Taman Toga | [USER-PROVIDED] |
| Owner/Context | Kelurahan Bubakan, Kecamatan Mijen, Kota Semarang | [USER-PROVIDED] |
| Development Context | KKN GIAT 17 | [USER-PROVIDED] — development context only, NOT product identity |

> [!CAUTION]  
> This is NOT a KKN GIAT application. KKN is the development context only. All UI copy, database concepts, documentation, and architecture focus on Kelurahan Bubakan.

---

## Established Program Context

These items are verified from project planning documents and product owner direction.

### Programs (DOCUMENT-VERIFIED)

| Program | Status | Source |
|---------|--------|--------|
| Urban Farming | Required program (proker wajib) | [DOCUMENT-VERIFIED] from Proker Kelompok |
| Taman Toga | Required program (proker wajib) | [DOCUMENT-VERIFIED] from Proker Kelompok |

### Initial Locations (DOCUMENT-VERIFIED)

| Location Concept | Indicated Area | Status | Source |
|-----------------|----------------|--------|--------|
| Urban Farming | Centered at Kelurahan Bubakan | [DOCUMENT-VERIFIED] from Proker Kelompok | Exact GPS, boundaries, and current condition are NEEDS-FIELD-VALIDATION |
| Taman Toga | Directed toward RW 03 | [DOCUMENT-VERIFIED] from Proker Kelompok | Exact GPS, boundaries, and current condition are NEEDS-FIELD-VALIDATION |

### Program Activities (DOCUMENT-VERIFIED)

| Activity | Status | Source |
|----------|--------|--------|
| Plant additions (penambahan tanaman) | [DOCUMENT-VERIFIED] | Proker Kelompok — Urban Farming includes adding plants |
| Barcode/QR concept | [DOCUMENT-VERIFIED] | Proker Kelompok — mentions barcode as part of Urban Farming output |
| Buku Saku (pocket book) as output | [DOCUMENT-VERIFIED] | Proker Kelompok — listed as Urban Farming output |

### Individual Program (DOCUMENT-VERIFIED)

| Item | Status | Source |
|------|--------|--------|
| Aplikasi/website tematik Urban Farming & Taman Toga | [DOCUMENT-VERIFIED] | Proker Individu — the digital system is a confirmed individual program |

---

## Product Requirements (USER-PROVIDED)

These are explicitly provided by the product owner through project direction.

| Requirement | Classification |
|-------------|---------------|
| Location is a first-class entity | [USER-PROVIDED] |
| System supports adding NEW Urban Farming and Taman Toga locations | [USER-PROVIDED] |
| Location → Plants → Optional QR hierarchy | [USER-PROVIDED] |
| PIC can capture GPS coordinates when registering a new location | [USER-PROVIDED] |
| GPS is single-point capture only (no tracking, no background, no geofencing) | [USER-PROVIDED] |
| QR references stable URL, not full plant data | [USER-PROVIDED] |
| QR generation blocked until real plants are inventoried and validated | [USER-PROVIDED] |
| Mandarin interaction is user-triggered (tap speaker icon → play audio once) | [USER-PROVIDED] |
| No autoplay, no looping, no live AI voice | [USER-PROVIDED] |
| Data changes must NOT require APK rebuild | [USER-PROVIDED] |
| Target cost Rp0 for MVP | [USER-PROVIDED] |
| Product identity is Kelurahan Bubakan, NOT KKN | [USER-PROVIDED] |
| No internal QR scanner — use camera / Google Lens | [USER-PROVIDED] |

---

## What Remains NEEDS-FIELD-VALIDATION

These items are known to be required but their **actual values** have not been determined yet.

| Data Point | Why Field Validation Required |
|-----------|------------------------------|
| Exact plant inventory per location | Must count and identify actual plants physically present |
| Final plants actually planted | Planting may still be in progress |
| Final plant quantities | Must count on-site |
| Exact GPS coordinates per location | Must be captured at physical location |
| Final physical location boundaries | Must be observed on-site |
| Final photos (plants, locations) | Must be taken on-site of actual subjects |
| Final PIC assignment per location | Must be confirmed by Kelurahan |
| Final QR plant selection list | Depends on validated plant inventory |
| Final plant-specific content (descriptions, benefits) | Must be researched and verified per plant |
| Final Mandarin names + Pinyin per plant | Depends on which plants are finalized |
| Final Mandarin audio content | Depends on finalized plant list + audio production method |
| Internet connectivity at locations | Must be tested on-site |
| PIC device capabilities | Must be surveyed |

---

## What is UNKNOWN

| Item | Impact |
|------|--------|
| Total number of locations beyond initial two | Design must support N locations |
| Whether additional Urban Farming or Taman Toga sites exist or are planned | Field survey required |
| Kelurahan's long-term digital capacity | Affects handover strategy |
| Post-KKN Firebase project owner | Must be determined before deployment |
| Mandarin audio production method (human vs TTS) | Deferred until plant list is finalized |

---

## Classification Legend

| Tag | Meaning |
|-----|---------|
| **[DOCUMENT-VERIFIED]** | Supported by official/project source documents (Proker Individu, Proker Kelompok, etc.) |
| **[USER-PROVIDED]** | Explicitly provided by the product owner |
| **[FIELD-VERIFIED]** | Confirmed through real-world field validation (none yet) |
| **[NEEDS-FIELD-VALIDATION]** | Known as a required data point but not yet confirmed through field work |
| **[UNKNOWN]** | Insufficient information exists |
