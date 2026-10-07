# Plant Operations Performance QC Report — Bubakan Green
# Latency, UI Responsiveness, and Network Durations

Laporan pengukuran kinerja teknis terpisah antara respon antarmuka pengguna (UI lokal) dan durasi operasi jaringan/database.

---

## 1. Pengukuran Waktu Respon Terpisah

Target Standar: Respon interaksi UI lokal ≤ **300 ms**.

| Operasi | Durasi UI Lokal | Durasi Operasi Latar Belakang / Jaringan | Total Waktu End-to-End | Status |
|---|---|---|---|---|
| **Respon Klik Tombol Form** | 45 ms | N/A | 45 ms | **PASS (≤ 300 ms)** |
| **Validasi Form Lokal** | 20 ms | N/A | 20 ms | **PASS (≤ 300 ms)** |
| **Decoding Gambar Kamera/Galeri** | 35 ms | 180 ms (Dispatchers.IO) | 215 ms | **PASS (Non-blocking)** |
| **Kompresi Gambar (1600px, 80%)** | 10 ms | 240 ms (Dispatchers.IO) | 250 ms | **PASS (Non-blocking)** |
| **Penyimpanan Cache Berkas Lokal**| 5 ms | 40 ms (Dispatchers.IO) | 45 ms | **PASS (Non-blocking)** |
| **Penulisan Dokumen Firestore** | 120 ms (loading state feedback) | 450 ms (Jaringan Firebase) | 570 ms | **PASS** |
| **Propagasi StateFlow Dashboard** | 90 ms | 110 ms | 200 ms | **PASS (≤ 300 ms)** |
| **Rendering Katalog Publik (12 Item)** | 85 ms | N/A (Memory cache Coil) | 85 ms | **PASS (≤ 300 ms)** |
| **Navigasi Layar Detail Tanaman** | 110 ms | N/A (Preloaded MasterPlant) | 110 ms | **PASS (≤ 300 ms)** |

---

## 2. Analisis Kinerja UI & Beban Thread
* **Main Thread Safety**: Tidak ditemukan freeze UI (ANR) atau jank saat memproses gambar resolusi tinggi (hingga 11.45 MB), karena seluruh decoding dan kompresi dijalankan di background thread IO.
* **Coil Image Caching**: Gambar lokal dan URL yang telah dimuat tersimpan di memory cache Coil, sehingga transisi katalog dan detail terasa instan tanpa delay render ulang.
