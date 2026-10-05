# Firestore Seed Verification Report

**Project:** Bubakan Green  
**Firebase Project ID:** `bubakan-green`  
**Database:** `(default)`  
**Seeder Script:** `scripts/seed_default_catalog.py`  
**Execution Timestamp:** 2026-10-05T17:52:19+07:00  
**Overall Result:** **100% VERIFIED & IDEMPOTENT**  

---

## 1. Summary Collection Verification Matrix

| Collection | Canonical File Source | Expected Docs | Cloud Read-Back | Final Result |
|---|---|---|---|---|
| `/master_plants` | `docs/data/DEFAULT_PLANT_CATALOG.json` | 9 | **9 documents verified** | **VERIFIED** |
| `/locations` | `docs/data/DEFAULT_LOCATIONS.json` | 2 | **2 documents verified** | **VERIFIED** |
| `/location_plants` | `docs/data/DEFAULT_LOCATION_PLANTS.json` | 10 | **10 documents verified** | **VERIFIED** |

---

## 2. Master Plants Catalog (9 Verified Documents)

All 9 plants conform strictly to `MasterPlant.kt` and were read back directly from Cloud Firestore:

| # | Stable Document ID | Indonesian Name | Botanical Latin Name | Mandarin Name | Pinyin | Cloud Verification |
|---|---|---|---|---|---|---|
| 1 | `cabai` | Cabai | *Capsicum annuum* | 辣椒 | là jiāo | **READ-BACK VERIFIED** |
| 2 | `jahe` | Jahe | *Zingiber officinale* | 生姜 | shēng jiāng | **READ-BACK VERIFIED** |
| 3 | `kangkung` | Kangkung | *Ipomoea aquatica* | 空心菜 | kōng xīn cài | **READ-BACK VERIFIED** |
| 4 | `kencur` | Kencur | *Kaempferia galanga* | 沙姜 | shā jiāng | **READ-BACK VERIFIED** |
| 5 | `kunyit` | Kunyit | *Curcuma longa* | 姜黄 | jiāng huáng | **READ-BACK VERIFIED** |
| 6 | `lidah_buaya` | Lidah Buaya | *Aloe vera* | 芦荟 | lú huì | **READ-BACK VERIFIED** |
| 7 | `sereh` | Sereh | *Cymbopogon citratus* | 柠檬草 | níng méng cǎo | **READ-BACK VERIFIED** |
| 8 | `terong` | Terong | *Solanum melongena* | 茄子 | qié zi | **READ-BACK VERIFIED** |
| 9 | `tomat` | Tomat | *Solanum lycopersicum* | 番茄 | fān qié | **READ-BACK VERIFIED** |

---

## 3. Approved Featured Locations (2 Verified Documents)

Coordinates are preserved as `null` (unresolved) per Section 8 ("A false GPS coordinate is worse than an empty coordinate. If actual verified coordinates do not exist: DO NOT fabricate"):

| Stable Document ID | Garden Name | Type | RW | Status | Coordinates | Cloud Verification |
|---|---|---|---|---|---|---|
| `loc_urban_farming_bubakan` | Urban Farming Kelurahan Bubakan | `URBAN_FARMING` | `01` | `ACTIVE` | Unresolved (`null`) | **READ-BACK VERIFIED** |
| `loc_taman_toga_rw03` | Taman Toga RW 03 | `TAMAN_TOGA` | `03` | `ACTIVE` | Unresolved (`null`) | **READ-BACK VERIFIED** |

---

## 4. Location-Plant Relationships (10 Verified Documents)

| Relation ID | Location ID | Master Plant ID | Location Name | Master Plant Name | Cloud Verification |
|---|---|---|---|---|---|
| `lp_uf_sereh` | `loc_urban_farming_bubakan` | `sereh` | Urban Farming Kelurahan Bubakan | Sereh | **READ-BACK VERIFIED** |
| `lp_uf_cabai` | `loc_urban_farming_bubakan` | `cabai` | Urban Farming Kelurahan Bubakan | Cabai | **READ-BACK VERIFIED** |
| `lp_uf_kangkung` | `loc_urban_farming_bubakan` | `kangkung` | Urban Farming Kelurahan Bubakan | Kangkung | **READ-BACK VERIFIED** |
| `lp_uf_tomat` | `loc_urban_farming_bubakan` | `tomat` | Urban Farming Kelurahan Bubakan | Tomat | **READ-BACK VERIFIED** |
| `lp_uf_terong` | `loc_urban_farming_bubakan` | `terong` | Urban Farming Kelurahan Bubakan | Terong | **READ-BACK VERIFIED** |
| `lp_toga_sereh` | `loc_taman_toga_rw03` | `sereh` | Taman Toga RW 03 | Sereh | **READ-BACK VERIFIED** |
| `lp_toga_jahe` | `loc_taman_toga_rw03` | `jahe` | Taman Toga RW 03 | Jahe | **READ-BACK VERIFIED** |
| `lp_toga_kencur` | `loc_taman_toga_rw03` | `kencur` | Taman Toga RW 03 | Kencur | **READ-BACK VERIFIED** |
| `lp_toga_kunyit` | `loc_taman_toga_rw03` | `kunyit` | Taman Toga RW 03 | Kunyit | **READ-BACK VERIFIED** |
| `lp_toga_lidah_buaya` | `loc_taman_toga_rw03` | `lidah_buaya` | Taman Toga RW 03 | Lidah Buaya | **READ-BACK VERIFIED** |

---

## 5. Idempotency Proof (Section 13)

Running `python scripts/seed_default_catalog.py` a second consecutive time yielded:
```text
Master plants summary: 0 created, 9 preserved (Total: 9).
Locations summary: 0 created, 2 preserved (Total: 2).
Location relationships summary: 0 created, 10 preserved (Total: 10).
Live Firestore Collection Counts:
  /master_plants:    9 documents
  /locations:        2 documents
  /location_plants:  10 documents
```
No duplicates, no suffix IDs (`-2`, `-copy`), exactly one production source of truth.
