# Dashboard & Public Catalog Sync QC Report — Bubakan Green
# Real-Time StateFlow Propagation and Catalog Coherence

Laporan pengujian sinkronisasi real-time antara mutasi database Firestore dengan tampilan Admin Dashboard dan Katalog Publik tanpa memerlukan restart aplikasi atau refresh manual.

---

## 1. Mekanisme Sinkronisasi Real-Time
* **Arsitektur**: Menggunakan Kotlin Coroutines `Flow` dan `StateFlow` yang terhubung langsung ke `PlantRepository` dan listener koleksi Firestore `master_plants`.
* **Admin Dashboard Listener**: `AdminDashboardViewModel` mengamati `getAllMasterPlants()` dan menghitung total tanaman secara reaktif.
* **Katalog Publik Listener**: `PlantCatalogViewModel` mengamati `getPublicMasterPlants()` dan memperbarui daftar kartu tanaman yang terbit (`isPublished == true`).

---

## 2. Hasil Pengujian Propagasi

| Aksi / Mutasi | Counter Dashboard Awal | Counter Dashboard Akhir | Hasil di Katalog Publik | Status |
|---|---|---|---|---|
| **Awal (Baseline)** | 9 Tanaman | 9 Tanaman | 9 Tanaman Terdaftar | **VERIFIED** |
| **Penambahan Sirih** | 9 Tanaman | 10 Tanaman | Muncul kartu *Sirih* | **VERIFIED** |
| **Penambahan Pegagan** | 10 Tanaman | 11 Tanaman | Muncul kartu *Pegagan* | **VERIFIED** |
| **Penambahan Kemangi** | 11 Tanaman | 12 Tanaman | Muncul kartu *Kemangi* | **VERIFIED** |
| **Penggantian Foto Cabai** | 12 Tanaman | 12 Tanaman (Stabil) | Thumbnail Cabai diperbarui | **VERIFIED** |
| **Penggantian Foto Lidah Buaya** | 12 Tanaman | 12 Tanaman (Stabil) | Thumbnail Lidah Buaya diperbarui | **VERIFIED** |

---

## 3. Verifikasi UI Tanpa Restart
* Teks counter pada Admin Dashboard: `"12 Tanaman"`, `"12 spesies botani terverifikasi di ensiklopedia."` (Terekam pada screenshot `docs/phase-plant/screenshots/30_admin_dashboard_12_plants.png`).
* Teks header pada Katalog Publik: `"12 Tanaman Terdaftar"` (Terekam pada screenshot `docs/phase-plant/screenshots/26_public_catalog_screen.png`).
* **Tidak memerlukan restart aplikasi, tidak memerlukan pull-to-refresh manual, dan tidak menggunakan nilai hardcoded**.
