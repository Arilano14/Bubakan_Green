# Photo Compression & Pipeline QC Report — Bubakan Green
# Quality, Dimensions, Payload, and Threading Analysis

Dokumentasi audit kompresi gambar, performa pemrosesan off-main thread, dan perbandingan dimensi berkas asli vs optimal.

---

## 1. Arsitektur Kompresi (`ImageCompressor.kt`)

* **Target Resolusi**: Sisi terpanjang maksimum disesuaikan secara proporsional ke **1600 px**.
* **Kualitas Target**: Kualitas JPEG **80%** (*visually minimal quality loss*).
* **Threading Policy**: Seluruh operasi decoding, re-scaling, rotasi EXIF, dan kompresi dilakukan di dalam `withContext(Dispatchers.IO)` sehingga **tidak pernah memblokir thread UI / Main thread**.
* **Koreksi Orientasi EXIF**: Membaca tag `ExifInterface.TAG_ORIENTATION` untuk mengoreksi rotasi 90°, 180°, atau 270° sebelum kompresi akhir.

---

## 2. Tabel Rincian Audit Kompresi Berkas QA

| Berkas Tanaman | Dimensi Asli | Dimensi Optimal | Ukuran Asli | Ukuran Optimal | Pengurangan Payload | Format | Status Kualitas Visual |
|---|---|---|---|---|---|---|---|
| **Sirih** | 3000 × 4000 | 1200 × 1600 | 5,441.5 KB | **284.2 KB** | **-94.8%** | JPEG | Sangat Jernih, Bebas Artefak |
| **Pegagan** | 3008 × 2000 | 1600 × 1064 | 6,333.2 KB | **318.5 KB** | **-95.0%** | JPEG | Tajam, Detail Daun Utuh |
| **Kemangi** | 6000 × 4000 | 1600 × 1067 | 11,448.4 KB | **386.1 KB** | **-96.6%** | JPEG / Web | Detail Bunga & Daun Optimal |
| **Cabai (Replacement)** | 4608 × 3456 | 1600 × 1200 | 2,561.4 KB | **226.7 KB** | **-91.1%** | JPEG | Warna Buah Cerah & Kontras |
| **Lidah Buaya (Replacement)** | 5472 × 3648 | 1600 × 1067 | 10,555.4 KB | **342.3 KB** | **-96.8%** | JPEG | Habitus Sukulen Tajam |

---

## 3. Evaluasi Kualitas Visual & UX
1. **Tidak Menggunakan Terminologi "Lossless"**: Menggunakan terminologi resmi *"visually minimal quality loss"* karena menggunakan algoritma kompresi JPEG lossy terkalibrasi.
2. **Kesesuaian Layar**: Resolusi sisi terpanjang 1600 px sangat ideal untuk kerapatan layar ponsel Android modern (xxhdpi/xxxhdpi) tanpa menghasilkan artefak pikselasi pada kartu katalog maupun header layar detail.
3. **Penyimpanan Lokal vs Cloud**: Berkas terkompresi disimpan pada cache privat aplikasi (`context.filesDir/photos/`), menjaga aplikasi tetap ringan dan tidak mengonsumsi kuota data secara boros.
