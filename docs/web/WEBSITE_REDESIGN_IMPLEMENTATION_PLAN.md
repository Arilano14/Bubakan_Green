# BUBAKAN GREEN — WEBSITE REDESIGN IMPLEMENTATION PLAN
**Document Version:** 2.0.0 (Pre-Implementation Gate Revision)  
**Status:** DRAFT (Awaiting Approval: `ACC WEBSITE REDESIGN REVISION`)  
**Product Canonical Identity:** Bubakan Green  
**Architecture Baseline:** Lightweight Vanilla Web + Cloud Firestore REST API + Local APK Sideloading  

---

## 1. Existing Audit Baseline

1. **Struktur File Web Saat Ini:**
   - `web/public/index.html` (4,983 bytes) — Tata letak mobile container lama (< 520px) dengan konten fitur dasar.
   - `web/public/plant.html` (9,343 bytes) — Halaman fallback tanaman yang membaca Firestore REST API, tetapi belum memiliki arsitektur QR micro-landing terfokus (fun-fact terpisah).
   - `web/public/location.html` (9,525 bytes) — Halaman fallback kebun yang membaca Firestore REST API.
   - `web/public/style.css` (7,781 bytes) — CSS terpusat pada container mobile sempit.
   - `web/public/mascot.png` (313,866 bytes) — Maskot lama yang keliru digunakan sebagai header logo.
   - `web/public/.well-known/assetlinks.json` (611 bytes) — Digital Asset Links aktif untuk verifikasi Android App Links.
2. **Status Aset Visual Asli (Android Baseline):**
   - Mockup: `image 3.png` (393×831 px), `image 4.png` (400×831 px), `image 5.png` (393×831 px) di `app/src/main/res/drawable-nodpi/Mockup/`.
   - Logo Resmi: `new_logo.png` (1994×1994 px RGBA, ~2.98 MB) di `app/src/main/res/drawable-nodpi/`.
   - Maskot: 8 pose asli (`mascot_greeting`, `mascot_learning`, `mascot_pointing`, `mascot_happy`, dll.).
3. **Status Berkas APK:**
   - Tersedia build pengujian: `app/build/outputs/apk/debug/app-debug.apk` (31,680,056 bytes ≈ 30.2 MB).
   - **Status Rilis:** Merupakan **DEBUG APK**. Belum ada Release APK bertanda tangan produksi.

---

## 2. Revised Architecture (Arsitektur Revisi)

- **Website Role:**
  - Halaman Beranda (`/`): *Product Landing Page* yang memperkenalkan aplikasi Bubakan Green, menampilkan mockup nyata, menjelaskan nilai manfaat bagi warga, dan memfasilitasi unduhan APK.
  - Halaman QR Micro-Landing (`/plant/{plantId}`): Dirancang kilat untuk pemindaian label QR di kebun fisik. Memprioritaskan Nama Tanaman, Gambar, Fun Fact Faktual, dan Tombol Pasang Aplikasi.
- **Data Model:**
  - Cloud Firestore sebagai **satu-satunya sumber kebenaran (Single Source of Truth)**.
  - Model pemuatan data: **Fetch-on-Request** via Firestore REST API saat halaman dibuka (bukan listener WebSocket real-time).
- **Integritas Semantik:**
  - Field `funFact` dipisahkan secara tegas dari `benefits` (khasiat herbal).
  - Jika field `funFact` belum tersedia di database, web menampilkan fallback jujur *"Fun fact tanaman ini belum tersedia."* tanpa mengarang fakta fiktif.

---

## 3. File Change Control (Kontrol Perubahan Berkas)

### 3.1 MUST CHANGE (Wajib Berubah)
| Path Berkas | Alasan & Rencana Perubahan |
| :--- | :--- |
| `web/public/index.html` | Desain ulang menjadi Product Landing Page modern (Hero dengan 3-mockup composition, Tentang Bubakan Green, Mengapa Bubakan Green, APK Gateway, Footer kontak resmi & kredit KKN terverifikasi). |
| `web/public/plant.html` | Restrukturisasi menjadi QR Micro-Landing Page (prioritas nama, foto, fun fact faktual, ejaan Mandarin/Pinyin, CTA pasang aplikasi). |
| `web/public/location.html` | Pembaruan visual header & footer dengan logo baru resmi dan integrasi tema botani bersih. |
| `web/public/style.css` | Pembaruan sistem desain: variabel warna botani Bubakan (`#1B4332`, `#40916C`, `#D8F3DC`), fluid typography, CSS Grid responsif (320px–1440px+), anti-AI-slop styling. |

### 3.2 MAY CHANGE (Dapat Berubah Jika Diperlukan)
| Path Berkas | Kondisi & Rencana |
| :--- | :--- |
| `web/firebase.json` | Penambahan header cache untuk aset statis baru (`/assets/**`), memastikan rute rewrites `/plant/**` dan `/location/**` tetap utuh. |

### 3.3 MUST NOT CHANGE (Dilarang Diubah)
| Path Berkas | Alasan Larangan |
| :--- | :--- |
| `app/src/main/res/drawable-nodpi/Mockup/*` | Aset asli Android tidak boleh dimodifikasi atau dipindahkan. |
| `app/src/main/res/drawable-nodpi/new_logo.png` | Aset master logo Android tidak boleh diubah. |
| `web/public/.well-known/assetlinks.json` | Sidik jari SHA-256 App Links aktif tidak boleh terganggu. |
| Kode Sumber Kotlin Android (`app/src/main/java/...`) | Tidak ada perubahan kode Android sebelum rilis web selesai. |
| `firestore.rules` | Aturan keamanan Firestore (read publik, write admin) sudah benar dan tidak boleh dilonggarkan. |

---

## 4. Files to Add (Berkas yang Akan Ditambahkan)

| Path Berkas Baru | Tujuan & Deskripsi |
| :--- | :--- |
| `web/public/assets/brand/new-logo.webp` | Logo resmi Bubakan Green hasil optimasi web (512×512 px, ~35 KB). |
| `web/public/assets/brand/new-logo.png` | Logo resmi PNG fallback transparan (512×512 px, ~48 KB). |
| `web/public/favicon.ico` & `favicon.png` | Favicon peramban menggunakan emblem logo baru. |
| `web/public/assets/mockups/mockup-home.webp` | Optimasi web `image 5.png` (Layar Beranda, ~60 KB). |
| `web/public/assets/mockups/mockup-location.webp` | Optimasi web `image 3.png` (Layar Lokasi, ~55 KB). |
| `web/public/assets/mockups/mockup-catalog.webp` | Optimasi web `image 4.png` (Layar Katalog, ~65 KB). |
| `web/public/assets/mascot/mascot-greeting.webp` | Optimasi web maskot untuk Hero (~45 KB). |
| `web/public/assets/mascot/mascot-learning.webp` | Optimasi web maskot untuk About (~45 KB). |
| `web/public/assets/mascot/mascot-pointing.webp` | Optimasi web maskot untuk QR Fun Fact (~30 KB). |
| `web/public/assets/mascot/mascot-happy.webp` | Optimasi web maskot untuk Download CTA (~50 KB). |
| `web/public/downloads/bubakan-green.apk` | Target distribusi APK lokal (akan diisi dengan status rilis yang jujur). |

---

## 5. Files to Delete (Berkas yang Akan Dihapus / Dinonaktifkan)

| Path Berkas | Evaluasi & Tindakan |
| :--- | :--- |
| `web/public/mascot.png` | **Pemeriksaan Ketergantungan:** Digunakan di header lama sebagai logo. Setelah logo baru aktif, berkas ini dinonaktifkan dari referensi HTML. Tidak dihapus secara destruktif sebelum memastikan tidak ada link eksternal yang memanggilnya. |

---

## 6. Firebase Impact Analysis

1. **Firestore Connection:**
   - Proyek: `bubakan-green` (Project Number: `1054147629887`).
   - Endpoint REST: `https://firestore.googleapis.com/v1/projects/bubakan-green/databases/(default)/documents/master_plants/{id}`.
2. **Kueri Data:**
   - Tidak ada scanning massal koleksi di sisi klien web; hanya membaca dokumen tunggal per permintaan ID.
   - Pemanfaatan caching HTTP peramban standar untuk meminimalkan pembacaan REST yang berulang.
3. **Dampak Aturan Keamanan:**
   - Nol dampak risiko keamanan; `allow read: if true;` tetap berlaku untuk pembacaan publik dokumen botani & lokasi.

---

## 7. APK Distribution & Release Strategy

1. **Prinsip Kejujuran Versi:**
   - Berkas `app-debug.apk` yang berukuran ~30.2 MB **TIDAK AKAN** dipublikasikan dengan klaim "Release APK".
   - Jika berkas debug ini disediakan untuk pengujian, tombol diberi label eksplisit:  
     `📥 Download APK (Testing / Debug Build)`  
     dengan catatan transparan mengenai status pengembangan.
2. **Target Rilis Produksi:**
   - Jalur rilis stabil: `/downloads/bubakan-green.apk`.
   - Jalur versi bertanda tangan: `/downloads/bubakan-green-v1.0.0.apk`.
   - Status sebelum release APK siap:  
     Menampilkan kartu status jujur: *"Berkas APK rilis publik sedang dalam tahap verifikasi akhir."*

---

## 8. Responsive Strategy (320px s.d. 1440px+)

1. **Breakpoints Kunci:**
   - `320px–360px`: Tata letak kolom tunggal ultra-kompak, mockup `image 5` tunggal (max 260px), padding 12px, nol overflow.
   - `375px–430px`: Standar ponsel modern, padding 20px, elemen sentuh &ge; 48px.
   - `768px–1023px`: Tablet, hero 2-kolom, grid fitur 2×2, 2-kolom footer.
   - `1024px–1440px+`: Desktop penuh, komposisi 3 mockup terangkat di kanan hero, 4 kolom horizontal *Why Choose Us*, kontainer terpusat max 1140px–1200px.
2. **Pengalaman Mobile QR:**
   - Konten tanaman (Nama, Gambar, Fun Fact) tampil utuh pada 1.5 kali scroll awal layar tanpa terhalang banner promosi besar.

---

## 9. Performance Strategy & Lightweight Budget

- **Nol Dependensi Berat:** Tanpa React, Next.js, jQuery, atau library animasi/carousel pihak ketiga.
- **Bobot HTML:** < 15 KB (markup semantik murni).
- **Bobot CSS:** < 20 KB (design system terpadu dalam satu berkas `style.css`).
- **Bobot JS:** < 5 KB (native ES6 `fetch()`, pengurai URL, dan penanganan status DOM).
- **Aset Gambar:** Semua gambar pendukung dikonversi ke WebP modern dengan resolusi terukur (tidak ada gambar mentah 3 MB yang disajikan ke web).
- **Total Payload Pemuatan Pertama:** **< 200 KB** (Sangat ramah sinyal minim di kebun).

---

## 10. QA Verification & Test Matrix

| Modul Uji | Target Verifikasi | Metode Pengujian | Status Harapan |
| :--- | :--- | :--- | :--- |
| **Home Route** | `/` mengembalikan 200 OK | Browser subagent / curl | `TESTED` |
| **Logo Baru** | `new-logo.webp` muncul di navbar, footer, dan favicon | Visual inspection | `TESTED` |
| **Old Logo Cleanup** | Referensi ke logo lama bernilai 0 di semua file web | `grep_search` | `TESTED` |
| **Hero Mockups** | 3 mockup tampil proporsional tanpa distorsi | Browser screenshot (Desktop & Mobile) | `TESTED` |
| **QR Plant Live** | `/plant/tomat` membaca data Firestore `tomat` | REST fetch verification | `TESTED` |
| **Fun Fact Handling**| Jika ada `funFact`: tampil fakta; jika tidak: tampil pesan fallback jujur | Firestore doc test | `TESTED` |
| **Semantik Data** | Field `benefits` tidak dijadikan `funFact` | Inspeksi kode JavaScript | `TESTED` |
| **Error Handling** | `/plant/unknown_xyz` memunculkan layar 404 bersih | Browser subagent | `TESTED` |
| **APK Sideload** | Download APK mengarah ke rute valid tanpa link fake Play Store | URL inspection | `TESTED` |
| **Kontak Kelurahan** | Alamat, telepon, email, web kelurahan sesuai sumber spasial | Inspeksi footer | `TESTED` |
| **Kredit KKN** | Kredit KKN terpisah dari branding produk | Inspeksi footer | `TESTED` |
| **Responsif Multi-layar** | Bebas horizontal scroll pada 320, 360, 393, 768, 1024, 1440 px | Subagent responsive test | `TESTED` |
| **App Link Safety** | `/.well-known/assetlinks.json` tetap utuh dan valid | HTTP request & JSON parse | `TESTED` |
