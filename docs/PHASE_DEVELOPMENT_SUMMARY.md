# Ringkasan Eksekutif Pengembangan Fase — Bubakan Green

Dokumen ini merupakan konsolidasi ringkas dan komprehensif dari seluruh fase perancangan, implementasi, audit, dan kendali mutu (*quality control*) proyek **Bubakan Green** (Fase 0 hingga Fase 6, Fase Admin, Fase Tanaman, Visual Redesign, dan Web).

---

## 1. Ikhtisar & Arsitektur Utama (Fase 0 & Fase 1)

Proyek **Bubakan Green** adalah platform panduan digital interaktif resmi untuk warga dan pengunjung Kelurahan Bubakan, Kecamatan Mijen, Kota Semarang. Platform ini mengintegrasikan aplikasi mobile Android natif (Jetpack Compose) dan aplikasi web publik (Firebase Hosting & Cloud Firestore).

### Prinsip Arsitektur:
- **Clean Architecture & Offline-First:** Aplikasi Android memisahkan lapisan UI, Domain, dan Data. Data lokal (`DefaultBotanicalData.kt`) memastikan aplikasi dapat berfungsi penuh di kebun tanpa koneksi internet stabil.
- **Sinkronisasi Cloud Firestore:** Menggunakan Cloud Firestore untuk pembaruan katalog tanaman, lokasi lahan, kuis edukasi, dan status publikasi secara *real-time*.
- **Single Source of Truth:** Koleksi master Firestore (`master_plants` dan `locations`) menjadi rujukan bersama antara aplikasi Android dan website.

---

## 2. Katalog Botani & Fitur Edukasi (Fase 2 & Fase 3)

### Data Botani Terverifikasi:
- Memuat **32 tanaman** (tanaman obat keluarga / TOGA dan sayuran pangan mandiri perkotaan).
- Setiap entri tanaman dilengkapi:
  - Nama Indonesia, Nama Ilmiah (Latin), Nama Mandarin Hanzi beserta pelafalan Pinyin.
  - Fakta unik (*Fun Fact*) botani yang edukatif dan teruji.
  - Khasiat medis/kesehatan tradisional dan panduan pemanfaatan.
  - Audio pelafalan bahasa Mandarin yang jernih dan responsif.
  - Foto representatif berkualitas tinggi dalam format WebP terkompresi.

### Fitur Pembelajaran Interaktif:
- **Bank Kuis Botani:** Kuis interaktif pilihan ganda untuk menguji pemahaman warga dan pelajar tentang khasiat tanaman toga dan teknik budidaya.
- **Panduan Suara (*Speaker Button*):** Tombol pemutar audio untuk mendengar nama botani dan pelafalan Mandarin secara instan.

---

## 3. Sistem QR Code & Penemuan Harta Karun (Fase 4 & Fase 5)

- **Konsep "Peti Harta Karun" Botani:** Pengunjung memindai label fisik QR code yang tertancap pada bedengan tanaman di kebun warga.
- **Format URL QR Standar:** Mengarah ke format `https://bubakan-green.web.app/?plant={id}` atau `/plant/{id}`.
- **Resolusi Otomatis:**
  - Jika dibuka di HP yang sudah terinstal aplikasi Bubakan Green, *App Links* langsung membuka layar detail tanaman di aplikasi.
  - Jika dibuka lewat kamera HP biasa / browser, website langsung menampilkan animasi pembukaan peti dan kartu edukasi tanaman lengkap.
  - Cache memori lokal mempercepat rendering konten tanaman dalam waktu <10ms.

---

## 4. Peta Geografis & Persebaran Lahan (Fase 6)

- **Pemetaan Lahan Kelurahan Bubakan:**
  - Menampilkan batas administratif kelurahan (KML / GeoJSON resmi).
  - Titik koordinat presisi terverifikasi untuk seluruh sentra:
    1. **Urban Farming Kelurahan Bubakan** (`-7.093500, 110.321500`)
    2. **Urban Farming RW 01 Bubakan** (`-7.092056, 110.319528`) — 12 komoditas pangan/sayuran.
    3. **Taman Toga RW 01 Bubakan** (`-7.092080, 110.319550`) — 13 tanaman herbal (berdampingan dengan Urban Farming RW 1).
    4. **Taman Toga RW 02 Bubakan** (`-7.096389, 110.318889`)
    5. **Taman Toga RW 03 Bubakan** (`-7.091944, 110.325667`)
    6. **Urban Farming RW 03 Bubakan** (`-7.092000, 110.324806`)
- **Navigasi Langsung:** Terhubung dengan Google Maps untuk petunjuk arah rute perjalanan bagi wisatawan botani.

---

## 5. Portal Manajemen Admin & CMS (Fase Admin & Fase Tanaman)

- **Akses Pengelola:** Halaman manajemen berbasis otentikasi aman (`admin@bubakan.id` / `admin@bubakangreen.id`).
- **Kelola Tanaman & Lahan (CRUD Lengkap):**
  - Tambah, ubah, dan hapus tanaman serta lahan.
  - Dialog pop-up konfirmasi sebelum tindakan penghapusan untuk mencegah kehilangan data secara tidak sengaja.
  - Fitur unggah foto langsung dari kamera/galeri dengan kompresi otomatis ke format WebP sebelum diunggah ke storage.
  - Partisi otomatis hubungan tanaman dengan lokasi lahan masing-masing.

---

## 6. Visual Redesign, Karakter Maskot, & Distribusi Web

- **Identitas Visual Modern:**
  - Palet warna botani alam: *Forest Green* (`#3F7337`), *Sage Light* (`#EDF4EC`), dan aksen hangat.
  - Rangkaian karakter maskot ekspresif: *Mascot Flower* (bunga), *Mascot Greeting*, *Mascot Happy*, *Mascot Thinking*, dan *Side Mascot Companions*.
- **Desain Responsif:** Layout adaptif sempurna untuk semua resolusi (ponsel kecil, tablet, hingga desktop lebar).
- **Distribusi Direct Download APK:**
  - Semua tombol unduh APK di website terintegrasi langsung dengan link Dropbox direct download (`dl=1`), mengunduh file biner APK asli (~36.6 MB) secara instan tanpa melalui halaman pratinjau Dropbox.
  - Konfigurasi terpusat via `apk-config.js` dan fallback redirect 302 di Firebase Hosting.

---

*Ringkasan ini menggantikan seluruh log dan checklist sementara dari fase 0 hingga 6, fase-admin, dan fase-plant.*
