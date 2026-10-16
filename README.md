<div align="center">

# Bubakan Green 🌱
### Panduan Digital Botani, Taman TOGA, & Urban Farming Kelurahan Bubakan

[![Android Version](https://img.shields.io/badge/Android-8.0%2B%20(API%2026%2B)-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://www.android.com/)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20%7C%20Material%203-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Firebase](https://img.shields.io/badge/Backend-Cloud%20Firestore-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)](https://firebase.google.com/)
[![Web Platform](https://img.shields.io/badge/Web-Live%20Hosting-0052CC?style=for-the-badge&logo=googlechrome&logoColor=white)](https://bubakan-green.web.app)

<br/>

**[📱 Unduh Langsung Berkas APK](https://www.dropbox.com/scl/fi/5f0307as5awimpj0h8iar/Bubakan-Green.apk?rlkey=bc2j9f4g51b0sq70o8u0n7lrj&st=48c5cbaq&dl=1)** &nbsp;•&nbsp; **[🌐 Kunjungi Website Resmi](https://bubakan-green.web.app)** &nbsp;•&nbsp; **[📖 Ringkasan Pengembangan](docs/PHASE_DEVELOPMENT_SUMMARY.md)**

<br/>

*Aplikasi panduan digital terintegrasi yang menghubungkan kebun fisik warga dengan teknologi interaktif untuk mendukung ketahanan pangan keluarga, pelestarian tanaman obat tradisional (TOGA), dan edukasi botani bagi generasi muda di Kelurahan Bubakan, Kecamatan Mijen, Kota Semarang.*

</div>

---

## 📌 Daftar Isi
1. [Latar Belakang & Fokus Aplikasi (SDGs)](#-latar-belakang--fokus-aplikasi-sdgs)
2. [Alur Integrasi & Pengalaman Pengguna](#-alur-integrasi--pengalaman-pengguna)
3. [Fitur Utama Aplikasi](#-fitur-utama-aplikasi)
   - [Perpustakaan Botani 32 Tanaman](#1-perpustakaan-botani-katalog-informatif)
   - [Kuis Edukatif Interaktif](#2-kuis-edukatif-interaktif)
   - [Jelajah Kebun & Pemetaan Lokasi](#3-jelajah-kebun--pemetaan-lokasi-lahan)
   - [Panel Manajemen Inventaris Admin](#4-panel-manajemen-inventaris-admin)
4. [Desain Visual & Maskot Si Hijau](#-desain-visual--maskot-si-hijau)
5. [Spesifikasi Teknis & Arsitektur](#-spesifikasi-teknis--arsitektur)
6. [Struktur Direktori Proyek](#-struktur-direktori-proyek)
7. [Panduan Instalasi & Menjalankan Proyek](#-panduan-instalasi--menjalankan-proyek)
8. [Daftar Titik Lahan Resmi Kelurahan](#-daftar-titik-lahan-resmi-kelurahan)

---

## 🎯 Latar Belakang & Fokus Aplikasi (SDGs)

Kelurahan Bubakan memiliki potensi perkebunan dan pekarangan aktif yang dikelola secara swadaya oleh masyarakat di tingkat Rukun Warga (RW). Namun, pengetahuan seputar tanaman obat keluarga (TOGA) serta teknik budidaya sayuran pangan pekarangan (Urban Farming) kerap kali belum terarsipkan secara rapi dan sulit diakses oleh anak muda serta wisatawan.

**Bubakan Green** hadir sebagai jembatan informasi yang modern, menghubungkan tanaman fisik di lahan dengan basis data botani digital.

<div align="center">
  <img src="PPT/Fokus%20Aplikasi.jpg" alt="Fokus Aplikasi Bubakan Green" width="100%" />
</div>

Aplikasi ini dirancang selaras dengan target **Tujuan Pembangunan Berkelanjutan (Sustainable Development Goals / SDGs)**:
- **SDG 3: Kehidupan Sehat dan Sejahtera (*Good Health and Well-being*)** — Mengedukasi pemanfaatan herbal TOGA untuk pencegahan penyakit dan pertolongan pertama kesehatan keluarga secara alami.
- **SDG 11: Kota dan Komunitas yang Berkelanjutan (*Sustainable Cities and Communities*)** — Mendorong pemanfaatan pekarangan rumah sebagai sentra pangan hijau mandiri di kawasan perkotaan.
- **SDG 13: Penanganan Perubahan Iklim (*Climate Action*)** — Mendukung gerakan penghijauan lingkungan lokal, penyerapan karbon pekarangan, dan konservasi ragam flora nusantara.

---

## 🔄 Alur Integrasi & Pengalaman Pengguna

Sistem Bubakan Green mengadopsi pendekatan **Phygital (*Physical to Digital*)**. Di setiap kebun percontohan, tertancap papan barcode QR Code resmi pada bedengan tanaman.

<div align="center">
  <img src="PPT/Alur%20Aplikasi.jpg" alt="Alur Sistem Aplikasi Bubakan Green" width="100%" />
</div>

### Tahapan Alur Kerja:
1. **Pindai Label QR di Kebun:** Pengunjung memindai kode QR fisik tanaman (contoh: `https://bubakan-green.web.app/plant/cabai`).
2. **Resolusi Cerdas (Deep Link):**
   - **Jika aplikasi sudah terpasang:** Sistem *Android App Links* otomatis langsung membuka halaman profil tanaman tersebut di aplikasi.
   - **Jika aplikasi belum terpasang:** Peramban membuka landing page website responsif yang menampilkan profil tanaman sekaligus opsi unduh langsung berkas APK resmi.
3. **Pemisahan Hak Akses:**
   - **Masyarakat & Pengunjung (Pengguna Umum):** Menikmati katalog botani, narasi audio, fakta unik, kuis interaktif, dan navigasi GPS menuju kebun lainnya.
   - **Kader Kelurahan & Pengelola (Admin):** Masuk melalui tab Manajemen untuk memperbarui data kebun, memutakhirkan stok tanaman, mengunggah foto lapangan baru, dan mencetak label QR resmi.

---

## 🌿 Fitur Utama Aplikasi

### 1. Perpustakaan Botani (Katalog Informatif)
Menampilkan ensiklopedia lengkap **32 tanaman** lokal yang terbagi ke dalam tanaman obat keluarga (TOGA) dan tanaman pangan mandiri (Urban Farming).

<div align="center">
  <img src="PPT/FItur%20Katalog%20tanaman.jpg" alt="Katalog Tanaman Informatif" width="100%" />
</div>

- **Identitas Lengkap:** Menyajikan Nama Indonesia, Nama Ilmiah (Latin), Nama Mandarin Hanzi, dan ejaan Pinyin.
- **Pelafalan Audio Mandarin:** Tombol audio interaktif untuk mendengarkan pelafalan nama tanaman secara tepat dalam bahasa Mandarin sebagai nilai tambah edukasi multibahasa.
- **Fakta Unik (*Fun Fact*):** Cerita menarik seputar asal-usul, keunikan morfologi, dan daya tahan tanaman.
- **Manfaat & Panduan Perawatan:** Petunjuk praktis kebutuhan sinar matahari, penyiraman, dan pemanfaatan herbal.

---

### 2. Kuis Edukatif Interaktif
Untuk memperkuat pemahaman warga dan pelajar setelah membaca profil tanaman, aplikasi menyediakan evaluasi mandiri berupa kuis interaktif.

<div align="center">
  <img src="PPT/Fitur%20Kuis.jpg" alt="Fitur Kuis Edukatif Interaktif" width="100%" />
</div>

- **Pertanyaan Berbasis Materi:** Soal pilihan ganda yang diacak dinamis menguji pemahaman morfologi, khasiat kesehatan, dan cara budidaya.
- **Konfirmasi Jawaban Real-Time:** Menampilkan umpan balik instan jika jawaban benar atau perlu diperbaiki.
- **Layar Hasil & Apresiasi:** Skor akhir (0–100) disertai animasi maskot ekspresif yang memberikan apresiasi atas pencapaian pengguna.

---

### 3. Jelajah Kebun & Pemetaan Lokasi Lahan
Menampilkan direktori dan persebaran sentra kebun produktif di Kelurahan Bubakan:
- **Pengelompokan Rukun Warga (RW):** Menampilkan Kebun Kelurahan, RW 01, RW 02, dan RW 03.
- **Foto Galeri Nyata:** Foto dokumentasi asli kondisi lapangan dan gerbang utama kebun warga.
- **Daftar Komoditas per Lahan:** Rincian tanaman apa saja yang dibudidayakan pada lahan tersebut (memisahkan lahan pangan dan kebun herbal toga berdampingan).
- **Integrasi Google Maps:** Tombol navigasi rute satu klik yang menghubungkan titik koordinat presisi ke aplikasi Google Maps.

---

### 4. Panel Manajemen Inventaris Admin
Portal pengelolaan konten yang aman bagi aparat kelurahan dan kader kelompok tani:

<div align="center">
  <img src="PPT/Fitur%20Manajemen%20Admin.jpg" alt="Fitur Manajemen Admin" width="100%" />
</div>

- **Dashboard Statistik:** Ringkasan jumlah tanaman aktif, total lahan terdaftar, dan status sinkronisasi cloud.
- **Kelola Tanaman & Lahan (CRUD):** Tambah, ubah data, perbarui foto lahan langsung dari kamera HP, serta fitur hapus dengan dialog konfirmasi aman.
- **Kompresi Gambar Otomatis:** Foto yang diunggah dikonversi ke format WebP terkompresi sebelum disimpan untuk menghemat kuota internet dan mempercepat pemuatan.
- **Generator Kode QR Resmi:** Menghasilkan kode QR beresolusi tajam siap cetak dan tempel di area kebun fisik.

---

## 🎨 Desain Visual & Maskot Si Hijau

Antarmuka Bubakan Green dibangun dengan filosofi desain modern, hangat, dan ramah pengguna (*user-friendly*) untuk semua rentang usia.

<div align="center">
  <img src="PPT/Design%20Aplikasi.jpg" alt="Desain Aplikasi dan Sistem Visual" width="100%" />
</div>

- **Palet Warna Alami (*Nature Palette*):**
  - *Primary Green* (`#2E7D32` & `#5B9B4A`): Melambangkan kesuburan, kehidupan, dan kelestarian alam.
  - *Warm Secondary* (`#FFAE0F` & `#FFD585`): Memberikan nuansa ramah, ceria, dan penuh energi.
  - *Clean Background* (`#F8FCF6` & `#E8F3E4`): Latar belakang bersih bernuansa daun muda yang nyaman di mata.
- **Tipografi:** Menggunakan jenis huruf **Poppins** dan **Plus Jakarta Sans** yang tegas, modern, dan mudah dibaca pada berbagai ukuran layar ponsel.
- **Karakter Maskot Edukasi (*Si Hijau*):** Rangkaian ilustrasi maskot tunas daun dan bunga yang ekspresif (Menyapa, Berpikir, Senang, Belajar, dan Mengapresiasi) untuk menemani perjalanan edukasi pengguna.

---

## 🛠 Spesifikasi Teknis & Arsitektur

Aplikasi dibangun mengikuti standar rekayasa perangkat lunak modern untuk menjamin performa tinggi, keamanan, dan keandalan di lapangan.

```
                    ┌───────────────────────────────┐
                    │       PENGGUNA / WARGA        │
                    └───────┬───────────────┬───────┘
                            │               │
                  Scan QR di Kebun    Buka Aplikasi
                            │               │
                            ▼               ▼
           ┌────────────────────────┐  ┌────────────────────────┐
           │   Website Responsif    │  │   Aplikasi Android     │
           │  (Firebase Hosting)    │  │   (Jetpack Compose)    │
           └────────────┬───────────┘  └───────────┬────────────┘
                        │                          │
                        └───────────┬──────────────┘
                                    │
                                    ▼
                 ┌─────────────────────────────────────┐
                 │       Google Cloud Firestore        │
                 │  (Master Plants, Locations, Quiz)   │
                 └─────────────────────────────────────┘
```

### 1. Aplikasi Mobile (Android)
- **Bahasa & Compiler:** Kotlin 2.0.21 (Target SDK 34 / Android 14, Minimum SDK 26 / Android 8.0).
- **Arsitektur Perangkat Lunak:** Clean Architecture berbasis MVVM (Model-View-ViewModel) dengan Dependency Injection terstruktur.
- **UI Toolkit:** 100% Deklaratif Jetpack Compose & Material Design 3.
- **Strategi Offline-First:** Menggunakan repository lokal terverifikasi (`DefaultBotanicalData.kt`) sebagai fallback instan sehingga aplikasi tetap dapat digunakan di area kebun dengan sinyal seluler minim.
- **Pemindai Kode Batang:** Google ML Kit Barcode Scanning API (ringan, cepat, dan presisi tinggi).
- **Manajemen Gambar & Audio:** Coil Image Loader (WebP disk cache) dan Media3/ExoPlayer untuk audio pemutar suara botani.

### 2. Aplikasi Web Publik
- **Teknologi:** Semantic HTML5, Vanilla CSS3 Modern (Flexbox, CSS Grid, Glassmorphism), dan Vanilla JavaScript murni tanpa overhead framework berlebih.
- **Hosting & Keamanan:** Firebase Hosting dengan enkripsi SSL/TLS global, HTTP/2, dan cache CDN cepat.
- **Distribusi Unduhan:** Konfigurasi `apk-config.js` terpusat yang mendistribusikan berkas APK langsung via direct CDN streaming tanpa halaman perantara.

---

## 📁 Struktur Direktori Proyek

```text
Bubakan-Green/
├── app/                                # Modul Utama Aplikasi Android
│   ├── src/main/java/id/bubakangreen/app/
│   │   ├── core/                       # Dependency Injection, Design Tokens, & Tema
│   │   ├── data/                       # Repositori Data, Firestore API, & Data Lokal Fallback
│   │   ├── domain/                     # Entity Model Bisnis, State, & Interface
│   │   └── ui/                         # Layar Compose: Beranda, Katalog, Lokasi, Kuis, Admin
│   └── src/main/res/                   # Ikon Aplikasi (mipmap) & Aset Visual (drawable-nodpi)
├── web/                                # Modul Website Publik & Konfigurasi Hosting
│   ├── public/
│   │   ├── index.html                  # Halaman Beranda Interaktif & Discovery Peti Botani
│   │   ├── preview.html                # Halaman Manajemen Web & Pratinjau Lahan
│   │   ├── style.css                   # Sistem Desain CSS Lengkap & Responsif
│   │   ├── apk-config.js               # Sumber Konfigurasi Tunggal Direct Download APK
│   │   └── assets/                     # Koleksi Foto Lahan Nyata & Maskot Edukasi
│   └── firebase.json                   # Konfigurasi Rules, Routing SPA, & 302 Redirects
├── PPT/                                # Berkas Presentasi & Dokumentasi Visual Sistem
├── docs/                               # Dokumentasi Teknis & Ringkasan Pengembangan Fase
│   ├── PHASE_DEVELOPMENT_SUMMARY.md    # Ringkasan Eksekutif Seluruh Fase
│   └── database_architecture.md        # Spesifikasi Skema Database Firestore
└── scripts/                            # Script Python Pemeliharaan & Sinkronisasi Database
```

---

## 🚀 Panduan Instalasi & Menjalankan Proyek

### Bagi Pengguna Umum (Pemasangan di Smartphone Android)
1. Buka peramban di ponsel Anda dan unduh berkas instalasi resmi: **[Unduh Bubakan-Green.apk](https://www.dropbox.com/scl/fi/5f0307as5awimpj0h8iar/Bubakan-Green.apk?rlkey=bc2j9f4g51b0sq70o8u0n7lrj&st=48c5cbaq&dl=1)** *(Ukuran berkas: ~36.6 MB)*.
2. Buka berkas yang telah selesai diunduh. Jika muncul dialog keamanan *"Instal aplikasi dari sumber tidak dikenal"*, pilih **Izinkan** / **Tetap Instal**.
3. Aplikasi siap dibuka dan langsung dapat digunakan menjelajahi kebun Bubakan.

---

### Bagi Pengembang & Auditor Teknis

#### Prasyarat:
- **Android Studio:** Hedgehog (2023.1.1) atau versi lebih baru.
- **Java Development Kit (JDK):** JDK 17 atau JDK 21.
- **Android SDK:** Platform SDK 34 dengan Build Tools 34.0.0.

#### Langkah Menjalankan Aplikasi Android:
```bash
# 1. Kloning repositori ini
git clone https://github.com/Arilano14/Bubakan_Green.git

# 2. Masuk ke direktori proyek
cd Bubakan_Green

# 3. Kompilasi dan periksa dependensi proyek
./gradlew.bat compileDebugKotlin

# 4. Bangun berkas APK Debug dan pasang langsung ke perangkat HP / Emulator
./gradlew.bat installDebug
```

#### Langkah Menjalankan & Memeriksa Website:
Modul web merupakan *vanilla static web* yang siap dijalankan menggunakan server lokal apa pun:
```bash
# Menggunakan Python built-in server
cd web/public
python -m http.server 8080

# Buka peramban di: http://localhost:8080
```

---

## 📍 Daftar Titik Lahan Resmi Kelurahan

Berikut adalah data sentra kebun produktif binaan warga yang telah dipetakan di Kelurahan Bubakan:

| No | Nama Lahan | Jenis Lahan | Wilayah | Titik Koordinat GPS | Status Data |
| :---: | :--- | :--- | :---: | :---: | :---: |
| **1** | **Urban Farming Kelurahan Bubakan** | Percontohan Sayuran & Pangan | Kantor Kelurahan | `-7.093500, 110.321500` | Terverifikasi |
| **2** | **Urban Farming RW 01 Bubakan** | Budidaya Sayuran Pekarangan (12 Tanaman) | Lingkungan RW 01 | `-7.092056, 110.319528` | Terverifikasi |
| **3** | **Taman TOGA RW 01 Bubakan** | Koleksi Tanaman Obat Herbal (13 Tanaman) | Lingkungan RW 01 | `-7.092080, 110.319550` | Terverifikasi |
| **4** | **Taman TOGA RW 02 Bubakan** | Tanaman Obat Keluarga Komunitas | Lingkungan RW 02 | `-7.096389, 110.318889` | Terverifikasi |
| **5** | **Taman TOGA RW 03 Bubakan** | Kebun TOGA Warga & Mahasiswa KKN | Lingkungan RW 03 | `-7.091944, 110.325667` | Terverifikasi |
| **6** | **Urban Farming RW 03 Bubakan** | Pertanian Pangan Mandiri Terbuka | Lingkungan RW 03 | `-7.092000, 110.324806` | Terverifikasi |

---

<div align="center">

**Kelurahan Bubakan • Kecamatan Mijen • Kota Semarang**  
*Mewujudkan Kawasan Mandiri Pangan, Asri, dan Berwawasan Botani Digital.*

</div>
