# AUDIT KEPUTUSAN PENYIMPANAN FOTO & BIAYA INFRASTRUKTUR
**Produk:** Bubakan Green  
**Fase:** Admin Remediation  
**Status Keputusan:** **GUARDRAIL AKTIF — MEMERLUKAN PERSETUJUAN EKSPLISIT USER SEBELUM AKTIVASI PENAGIHAN**

---

## 1. Konteks & Larangan Kritis (Strict Guardrails)
1. **Dilarang Menyimpan Binary / Base64 di Firestore:**  
   Ukuran batas dokumen Firestore adalah 1 MiB per dokumen. Menyimpan data biner gambar langsung atau string terenkode Base64 di Firestore dilarang keras karena mengakibatkan pembengkakan kuota, degradasi performa query, dan pelanggaran arsitektur basis data.
2. **Paket Firebase Saat Ini:**  
   Proyek Firebase `bubakan-green` saat ini beroperasi pada **Paket Spark (Gratis)**.
3. **Persyaratan Cloud Storage for Firebase:**  
   Google Cloud / Firebase Storage mewajibkan proyek di-upgrade ke paket **Blaze (Pay-as-you-go)** dengan kartu kredit / akun penagihan aktif di Google Cloud Console.
4. **Aturan:**  
   AI Agent dilarang keras mengaktifkan Cloud Storage atau paket Blaze secara diam-diam.

---

## 2. Matriks Evaluasi 3 Opsi Penyimpanan Foto

| Parameter Evaluasi | Opsi A: Existing Architecture (URL HTTPS Terverifikasi / Aset Lokal) | Opsi B: Firebase Cloud Storage + Paket Blaze | Opsi C: Local App Cache + Dual-Path Hybrid |
| :--- | :--- | :--- | :--- |
| **Biaya Bulanan** | **Rp 0 (100% Gratis di Spark Plan)** | **Memerlukan Kartu Kredit (Blaze Plan)** | **Rp 0 (100% Gratis)** |
| **Keamanan** | Aman; URL difilter protokol resmi HTTPS dan domain terpercaya. | Sangat tinggi; diatur oleh `storage.rules` bawaan (`isAdmin()`). | Aman di sandbox internal Android. |
| **Pemeliharaan** | Nol pemeliharaan server backend. | Terkelola otomatis oleh Google Cloud. | Terkelola di dalam aplikasi klien. |
| **Dukungan Mobile Upload** | Terbatas (Admin memasukkan URL gambar yang valid atau memilih aset yang tersedia). | Native langsung via Firebase Storage Android SDK (`storageRef.putBytes()`). | Gambar kamera/galeri dikompresi lokal & disimpan di sandbox perangkat. |
| **Dukungan Public Read** | Sangat baik via CDN asal & Coil disk/memory caching. | Sangat baik via Firebase Download URL publik bertoken. | Hanya terbaca di perangkat lokal jika file lokal; untuk publik membutuhkan URL web. |
| **Dukungan Hapus / Ganti** | Instan; hanya memperbarui string URL di array `photos` Firestore. | Menghapus objek di bucket via SDK + update array Firestore. | Menghapus file lokal + update array Firestore. |
| **Kompleksitas Kode** | Rendah (tidak butuh library tambahan). | Menengah (tambah SDK Firebase Storage + upload coroutine). | Menengah. |
| **Status Kesiapan** | **SIAP DIGUNAKAN.** | **BLOCKED (Menunggu otorisasi akun penagihan Blaze).** | **SIAP DIGUNAKAN sebagai fallback.** |

---

## 3. Rekomendasi Arsitektur Unggah Foto Terpadu (Dual-Mode Architecture)

Untuk menjamin kelancaran fungsi tanpa melanggar guardrail biaya penagihan:
1. **Pipeline Kompresi Gambar Klien (`ImageCompressor.kt`):**
   - Menangani orientasi EXIF kamera secara otomatis.
   - Resize dimensi maksimal sisi terpanjang: 1600–1920 px.
   - Kompresi format JPEG/WebP dengan rasio kualitas 80–85%.
   - Menghasilkan pengurangan ukuran file signifikan (~80–90% lebih kecil dibanding foto kamera mentah 8–15 MB), tanpa penurunan kualitas visual yang tampak pada layar gawai.
2. **Penyimpanan di Firestore:**
   - Menyimpan array URL teks bersih:
     ```json
     {
       "photos": [
         "https://...",
         "https://..."
       ]
     }
     ```
   - Maksimal 3 string URL.
   - Dokumen Firestore berukuran sangat kecil (< 2 KB), jauh di bawah batas 1 MiB.
3. **Pilihan Sumber Foto di Admin UI:**
   - **Kamera:** Menggunakan `ActivityResultContracts.TakePicture()` kontekstual.
   - **Galeri:** Menggunakan Android Photo Picker (`ActivityResultContracts.PickVisualMedia()`) tanpa meminta izin penyimpanan luas yang tidak perlu.
   - **URL HTTPS:** Admin dapat menautkan URL foto gambar yang sudah diunggah di hosting web/cloud.

---

## 4. Pelaporan Status Blaze Plan
Jika pemilik proyek menyetujui aktivasi Firebase Cloud Storage pada paket Blaze:
- Dependensi `com.google.firebase:firebase-storage` akan diaktifkan di `app/build.gradle.kts`.
- Aturan `storage.rules` yang sudah tersedia di root repositori akan di-deploy ke Firebase Storage.
- Alur unggah byte langsung dari memori telepon ke bucket Firebase akan aktif secara otomatis.

Jika belum ada persetujuan Blaze:
- Fitur tetap beroperasi menggunakan validasi URL HTTPS terkompresi dan aset lokal tanpa menimbulkan biaya sepeser pun.
