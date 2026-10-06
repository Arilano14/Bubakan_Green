# BUBAKAN GREEN — WEBSITE CONTENT SOURCE MATRIX & COPYWRITING SPECIFICATION
**Document Version:** 2.0.0 (Revised)  
**Status:** DRAFT (Awaiting Approval: `ACC WEBSITE REDESIGN REVISION`)  
**Target Surface:** `web/public/index.html`, `web/public/plant.html`, `web/public/location.html`  
**Classification Standards:**  
- `VERIFIED`: Berdasarkan data resmi pemerintah (Data Spasial Semarang) atau sumber ilmiah botani terverifikasi.  
- `USER-PROVIDED`: Diberikan langsung oleh pemohon/instruksi proyek.  
- `ASSUMPTION`: Asumsi arsitektur/desain logis yang menunggu konfirmasi rilis.  
- `NEEDS-VERIFICATION`: Informasi yang memerlukan validasi sebelum publikasi final.  

---

## 1. Identitas Produk & Metadata Global

| Elemen Konten | Teks / Spesifikasi Bahasa Indonesia | Klasifikasi | Keterangan & Sumber |
| :--- | :--- | :--- | :--- |
| **Nama Produk Kanonikal** | `Bubakan Green` (Bukan "Buba-Green", bukan "KKN GIAT 17") | `USER-PROVIDED` | Nama merek kanonikal tunggal di seluruh aplikasi & web. |
| **Posisi Produk** | `Sistem Informasi Urban Farming & Taman Toga Kelurahan Bubakan, Kecamatan Mijen, Kota Semarang` | `USER-PROVIDED` | Fokus produk warga lokal, bukan portal mahasiswa. |
| **Konteks KKN** | KKN GIAT 17 UNNES bertindak sebagai mitra kolaborasi pengembangan, bukan pemilik merek utama. | `USER-PROVIDED` | Ditempatkan di bagian footer/kredit. |
| **Judul Halaman (`index.html`)** | `Bubakan Green — Sistem Informasi Urban Farming & Taman Toga Kelurahan Bubakan` | `USER-PROVIDED` | SEO Title standar. |
| **Meta Description (`index.html`)** | `Aplikasi resmi untuk mengenal tanaman obat keluarga (Taman Toga) dan kebun pangan mandiri (Urban Farming) di Kelurahan Bubakan, Kecamatan Mijen, Kota Semarang.` | `USER-PROVIDED` | Ringkasan pencarian mesin telusur. |
| **Judul Halaman (`plant.html`)** | `[Nama Tanaman] — Fun Fact & Khasiat Tanaman · Bubakan Green` | `USER-PROVIDED` | Dinamis per tanaman dari Firestore. |
| **Judul Halaman (`location.html`)**| `[Nama Kebun] — Lokasi Kebun · Bubakan Green` | `USER-PROVIDED` | Dinamis per lokasi dari Firestore. |

---

## 2. Konten Halaman Utama (`index.html`)

### 2.1 Navigation Bar
| Komponen | Salinan Teks | Klasifikasi | Catatan |
| :--- | :--- | :--- | :--- |
| **Brand Logo** | Logo resmi baru (`/assets/brand/new-logo.webp`) | `VERIFIED` | Sumber dari `app/src/main/res/drawable-nodpi/new_logo.png`. |
| **Brand Wordmark**| `Bubakan Green` | `USER-PROVIDED` | Huruf kapital teratur. |
| **Menu Tautan** | `Tentang`, `Fitur`, `Mengapa Kami` | `USER-PROVIDED` | Navigasi jangkar internal (`#about`, `#features`, `#why-us`). |
| **Tombol Navigasi**| `Pasang Aplikasi` | `USER-PROVIDED` | Mengarah ke `#download`. |

---

### 2.2 Hero Section
| Elemen | Salinan Teks | Klasifikasi | Catatan Desain |
| :--- | :--- | :--- | :--- |
| **Badge Kicker** | `🌱 Urban Farming & Taman Toga · Kelurahan Bubakan` | `USER-PROVIDED` | Label pengantar kategori. |
| **Headline (H1)** | `Bubakan Green` | `USER-PROVIDED` | Tipografi tegas, bersih, dan berwibawa. |
| **Subheadline** | *"Satu aplikasi untuk mengenal tanaman dan kebun Urban Farming & Taman Toga di Kelurahan Bubakan."* | `USER-PROVIDED` | Ringkas, lugas, tanpa klaim hiperbolik. |
| **Primary CTA (Produksi)** | `Install Bubakan Green` | `ASSUMPTION` | **Hanya ditampilkan jika berkas Release APK sudah ada.** |
| **Primary CTA (Pengujian)** | `Download APK (Testing)` / `APK Publik Segera Tersedia` | `VERIFIED` | Ditampilkan jika yang tersedia adalah Debug APK atau belum ada Release APK. |
| **Secondary CTA** | `Lihat Cara Kerja` | `USER-PROVIDED` | Smooth scroll ke `#about`. |
| **Aset Visual Hero** | Mockup 3 layar Android (`image 5` di tengah, `image 3` & `image 4` di sisi) + `mascot_greeting.png` | `VERIFIED` | Sesuai audit aset asli tanpa distorsi. |

---

### 2.3 Tentang Bubakan Green (`#about`)
| Elemen | Salinan Teks | Klasifikasi | Catatan |
| :--- | :--- | :--- | :--- |
| **Judul Bagian (H2)** | `Tentang Bubakan Green` | `USER-PROVIDED` | Judul resmi. |
| **Ringkasan Misi** | *"Bubakan Green adalah sistem informasi dan aplikasi digital untuk Urban Farming dan Taman Toga di Kelurahan Bubakan, Kecamatan Mijen, Kota Semarang."* | `USER-PROVIDED` | Penjelasan ringkas dan berbasis kebutuhan nyata. |
| **Pilar Manfaat 1** | `Mengenal Tanaman` — Informasi jenis tanaman toga dan sayuran pangan mandiri. | `USER-PROVIDED` | Fitur katalog botani. |
| **Pilar Manfaat 2** | `Menemukan Kebun` — Mengetahui sebaran kebun warga di wilayah RW Kelurahan Bubakan. | `USER-PROVIDED` | Fitur daftar kebun. |
| **Pilar Manfaat 3** | `Informasi Tanaman Lengkap` — Panduan rawat, khasiat herbal, dan pelafalan audio Mandarin. | `USER-PROVIDED` | Fitur detail tanaman. |
| **Pilar Manfaat 4** | `Akses Informasi melalui QR` — Pindai label fisik langsung di kebun untuk membaca fakta menarik. | `USER-PROVIDED` | Fitur QR scanner. |
| **Pilar Manfaat 5** | `Navigasi Lokasi` — Menemukan titik lokasi kebun dengan petunjuk koordinat terintegrasi. | `USER-PROVIDED` | Integrasi koordinat GPS. |
| **Maskot Pendamping**| `mascot_learning.png` | `VERIFIED` | Maskot sedang belajar/meneliti, tidak menutupi teks. |

---

### 2.4 Mengapa Bubakan Green? (`#why-us`)
| Nomor | Judul Kartu | Salinan Deskripsi | Klasifikasi | Anti-AI-Slop Check |
| :--- | :--- | :--- | :--- | :--- |
| **01** | `Kenali Tanaman` | Informasi tanaman yang mudah dipahami warga dan keluarga. | `USER-PROVIDED` | Tanpa kata "revolusioner", "tercanggih". |
| **02** | `Scan QR` | Temukan informasi tanaman langsung dari label fisik di kebun. | `USER-PROVIDED` | Berbasis fitur nyata. |
| **03** | `Jelajahi Kebun` | Temukan Urban Farming dan Taman Toga Bubakan di lingkungan RW. | `USER-PROVIDED` | Berbasis data kebun nyata. |
| **04** | `Terhubung dengan Bubakan` | Informasi dikembangkan khusus untuk kebutuhan Kelurahan Bubakan. | `USER-PROVIDED` | Relevansi lokal nyata. |

---

### 2.5 Area Unduh APK & Panduan Instalasi (`#download`)
| Elemen | Salinan Teks | Klasifikasi | Catatan Integritas Rilis |
| :--- | :--- | :--- | :--- |
| **Judul Bagian** | `Pasang Aplikasi Bubakan Green` | `USER-PROVIDED` | Ajakan aksi instalasi. |
| **Label Rilis Publik** | `Versi Rilis: v1.0.0 (Release APK)` | `ASSUMPTION` | Target nama file: `bubakan-green-v1.0.0.apk`. |
| **Kondisi Debug APK** | *"Berkas yang tersedia saat ini merupakan build pengujian internal (Debug APK). Versi rilis publik bertanda tangan segera hadir."* | `VERIFIED` | Fakta teknis: saat ini yang ada baru `app-debug.apk`. |
| **Pemberitahuan Status**| *"APK versi publik belum tersedia."* (jika release APK belum diunggah) | `VERIFIED` | Kejujuran status rilis. |
| **Tombol Unduh** | `[ Install Bubakan Green ]` (URL: `/downloads/bubakan-green.apk`) | `USER-PROVIDED` | Unduhan langsung, **TIDAK ADA tombol palsu Play Store**. |
| **Panduan Sideload 1**| `1. Unduh Berkas APK:` Klik tombol unduh di atas. | `USER-PROVIDED` | Instruksi manual. |
| **Panduan Sideload 2**| `2. Buka Berkas Unduhan:` Buka file dari bar notifikasi ponsel atau folder Download. | `USER-PROVIDED` | Tidak mengklaim instalasi otomatis oleh browser. |
| **Panduan Sideload 3**| `3. Izinkan Sumber:` Jika Android meminta izin, pilih **Setelan → Izinkan dari sumber ini**. | `USER-PROVIDED` | Prosedur standar Android sideloading. |
| **Panduan Sideload 4**| `4. Selesaikan Pemasangan:` Ketuk tombol **Instal**. | `USER-PROVIDED` | Konfirmasi sistem. |
| **Panduan Sideload 5**| `5. Buka Aplikasi:` Buka Bubakan Green dan mulai eksplorasi kebun. | `USER-PROVIDED` | Peluncuran aplikasi. |
| **Maskot Pendamping** | `mascot_happy.png` | `VERIFIED` | Menemani area unduhan dengan antusias. |

---

### 2.6 Kontak Resmi & Kredit Kolaborasi (Footer)

#### A. Kontak Resmi Kelurahan Bubakan
*Semua data diverifikasi langsung dari basis data spasial resmi Diskominfo Kota Semarang:*  
[https://dataspasial.semarangkota.go.id/detail_kantor/71](https://dataspasial.semarangkota.go.id/detail_kantor/71)

| Bidang Kontak | Informasi Terverifikasi | Klasifikasi | Catatan Verifikasi |
| :--- | :--- | :--- | :--- |
| **Alamat Kantor** | `Jl. Bubakan, Kec. Mijen, Kota Semarang, Jawa Tengah 50216` | `VERIFIED` | Sesuai Data Spasial No. 71. |
| **Nomor Telepon** | `(0294) 70783032` | `VERIFIED` | Telepon kantor resmi. |
| **Email Kantor** | `Bubakan@gmail.com` | `VERIFIED` | Email resmi kantor kelurahan. |
| **Situs Web Resmi**| `bubakan.semarangkota.go.id` | `VERIFIED` | Domain resmi Pemkot Semarang. |
| **Instagram Resmi**| `@kelurahanbubakan` | `VERIFIED` | Akun media sosial resmi kantor. |
| **Kanal Tambahan** | **TIDAK MENAMPILKAN WhatsApp / Facebook fiktif** | `VERIFIED` | Tidak ada akun lain yang tercantum di sumber resmi. |

#### B. Kredit Kolaborasi KKN GIAT 17 UNNES
| Bidang Kontak | Informasi Kolaborasi | Klasifikasi | Catatan Pemisahan Merek |
| :--- | :--- | :--- | :--- |
| **Pernyataan Kredit**| *"Aplikasi dibuat melalui kolaborasi antara Kelurahan Bubakan dan Tim KKN GIAT 17 UNNES dalam penugasan daerah Kelurahan Bubakan • 2026."* | `USER-PROVIDED` | Kredit kolaborasi terpisah jelas dari identitas produk. |
| **Email Tim KKN** | `giat17kelbubakan@gmail.com` | `USER-PROVIDED` | Kontak korespondensi tim pengembang KKN. |
| **Instagram KKN** | `@giat17.kelurahanbubakan` | `USER-PROVIDED` | Akun dokumentasi penugasan. |
| **TikTok KKN** | `@giat17.kelurahanbubakan` | `USER-PROVIDED` | Akun publikasi video kegiatan. |

---

## 3. Konten Halaman Micro-Landing QR (`plant.html`)

| Komponen | Salinan Teks & Perilaku | Klasifikasi | Aturan Integritas Data |
| :--- | :--- | :--- | :--- |
| **Header** | Logo resmi Bubakan Green + Aksen Toga | `VERIFIED` | Menggunakan logo resmi baru. |
| **Kategori Chip** | `🌿 FUN FACT TANAMAN` | `USER-PROVIDED` | Menegaskan fokus penemuan kilat. |
| **Nama Tanaman (H1)**| Menampilkan `nameId` dokumen Firestore | `VERIFIED` | Sumber live Firestore `master_plants/{plantId}`. |
| **Nama Latin** | Menampilkan `nameLatin` (italic) | `VERIFIED` | Sumber live Firestore. |
| **Foto Tanaman** | Gambar dari `primaryPhotoUrl` | `VERIFIED` | Foto asli tanaman kebun. |
| **Kartu Fun Fact** | Menampilkan field `funFact` dari Firestore | `VERIFIED` | Rujukan ilmiah dari `FUN_FACT_SOURCE_MATRIX.md`. |
| **Fallback Fun Fact**| *"Fun fact tanaman ini belum tersedia."* | `USER-PROVIDED` | **DILARANG mengganti dengan teks khasiat (benefits).** |
| **Khasiat Herbal** | Menampilkan field `benefits` pada kartu terpisah | `VERIFIED` | Terpisah secara semantik dari Fun Fact. |
| **Audio/Mandarin** | Karakter Hanzi & Pinyin (opsional) | `VERIFIED` | Ditampilkan jika ada pada dokumen. |
| **CTA Buka Aplikasi**| `📲 Buka di Aplikasi` (App Link: `/plant/{id}`) | `USER-PROVIDED` | Membuka aplikasi Android langsung jika terpasang. |
| **CTA Pasang APK** | `📥 Pasang Bubakan Green (APK)` | `USER-PROVIDED` | Mengarahkan ke berkas unduhan APK. |

---

## 4. Matriks Pernyataan Kualitas & Anti-AI-Slop

| Klaim / Pernyataan | Status Diterima / Ditolak | Alasan & Koreksi |
| :--- | :--- | :--- |
| *"Aplikasi otomatis berubah real-time saat Firestore diedit."* | **DITOLAK (FALSE)** | Web melakukan request HTTP saat dimuat (*fetch-on-request*), bukan listener WebSocket real-time. |
| *"Khasiat tanaman otomatis dijadikan Fun Fact jika field kosong."* | **DITOLAK (SEMANTIC ERROR)** | Khasiat herbal (*benefits*) berbeda makna dari fakta unik botani (*funFact*). Wajib tampilkan fallback jujur. |
| *"Aplikasi pertanian digital AI tercanggih di Indonesia."* | **DITOLAK (AI-SLOP)** | Mengarang klaim pemasaran tanpa bukti. Diganti penjelasan fungsi nyata warga. |
| *"Unduh di Google Play Store."* | **DITOLAK (FALSE CTA)** | Aplikasi belum rilis di Google Play Store. Wajib menggunakan unduhan lokal APK yang jujur. |
| *"Debug APK langsung dijadikan file rilis publik."* | **DITOLAK (SECURITY/QA RISK)**| Debug APK berbobot besar dan tidak bertanda tangan produksi. Wajib menggunakan Release APK bertanda tangan. |
