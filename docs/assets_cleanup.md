# Bubakan Green — Assets Audit & Cleanup Report

**Date:** 2026-10-01  
**Auditor:** Senior Android UI/UX Engineer & Performance Architect  
**Scope:** `app/src/main/res/` and Project Root Assets

---

## 1. Executive Summary

A comprehensive asset dependency audit was conducted across the Bubakan Green repository. All temporary screen dumps, duplicate images, and legacy build artifacts were analyzed against Kotlin and XML code references before cleanup.

---

## 2. Removed Unused & Temporary Assets

The following 9 temporary screen dumps and XML dumps (totaling **~4.7 MB**) were verified to have zero code references in the Android project and have been permanently removed:

| File Name | Location | Size | Status | Verification |
|:---|:---|:---:|:---:|:---|
| `dump.xml` | Project Root | 15.7 KB | **Deleted** | Zero Kotlin/XML reference |
| `screen_catalog.png` | Project Root | 780 KB | **Deleted** | Zero Kotlin/XML reference |
| `screen_catalog_2.png`| Project Root | 835 KB | **Deleted** | Zero Kotlin/XML reference |
| `screen_catalog_3.png`| Project Root | 871 KB | **Deleted** | Zero Kotlin/XML reference |
| `screen_detail.png` | Project Root | 1.20 MB | **Deleted** | Zero Kotlin/XML reference |
| `screen_detail_2.png` | Project Root | 230 KB | **Deleted** | Zero Kotlin/XML reference |
| `screen_detail_3.png` | Project Root | 202 KB | **Deleted** | Zero Kotlin/XML reference |
| `screen_home.png` | Project Root | 445 KB | **Deleted** | Zero Kotlin/XML reference |
| `screen_locations.png`| Project Root | 189 KB | **Deleted** | Zero Kotlin/XML reference |
| **Total Cleaned** | — | **~4.77 MB**| — | Workspace Cleaned |

---

## 3. Retained & Verified Active Assets

All remaining assets in `app/src/main/res/` are strictly verified, actively mapped to Kotlin enum bindings, and optimized for mobile memory footprint:

### 3.1 Mascot Emotion Assets (`drawable-nodpi/`)
All 9 mascot emotion poses are mapped 1-to-1 in `id.bubakangreen.app.ui.components.MascotType`:
1. `mascot_default.png` (313 KB) — Base brand presence
2. `mascot_greeting.png` (367 KB) — Welcome & Login screen companion
3. `mascot_learning.png` (364 KB) — Botanical educational lessons
4. `mascot_happy.png` (619 KB) — Lesson completion & milestone celebration
5. `mascot_thinking.png` (366 KB) — Empty state explorer & search helper
6. `mascot_warning.png` (319 KB) — Error boundaries & GPS accuracy alerts
7. `mascot_pointing.png` (163 KB) — QR Code guide & direction assistance
8. `mascot_cta_process.png` (468 KB) — Form step guidance
9. `mascot_splashscreen.png` (359 KB) — Initial launch greeting

### 3.2 Botanical WebP Compressed Images (`drawable-nodpi/`)
All 9 species images are WebP encoded (1280px max dimension, quality ~85, average 178 KB), strictly matching verified Creative Commons attribution:
1. `plant_sereh.webp` (60 KB)
2. `plant_cabai.webp` (91 KB)
3. `plant_kangkung.webp` (145 KB)
4. `plant_tomat.webp` (332 KB)
5. `plant_terong.webp` (37 KB)
6. `plant_jahe.webp` (278 KB)
7. `plant_kencur.webp` (257 KB)
8. `plant_kunyit.webp` (107 KB)
9. `plant_lidah_buaya.webp` (327 KB)

### 3.3 Core Branding (`drawable-nodpi/icon/` & `mipmap-*/`)
- `logo_bubakan.png` — High-definition circular vector-rendered symbol for launcher icon generations across all density buckets (`mdpi`, `hdpi`, `xhdpi`, `xxhdpi`, `xxxhdpi`).

---

## 4. Verification Check
- No duplicate assets exist across resource directories.
- No uncompressed raw photographic files (>5 MB) exist in the build path.
- App compile and package size are optimized for rapid device installation.
