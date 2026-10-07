# Phase Plant Management — Full Frontend QC Readiness Plan
# Bubakan Green — Admin Plant CRUD & Evidence Plan

Dokumentasi rencana pengujian menyeluruh (Full QC Test Plan), arsitektur penyimpanan gambar, konfirmasi model Firestore, dan protokol eksekusi frontend-first untuk penambahan 3 tanaman default baru (*Sirih*, *Pegagan*, *Kemangi*) serta penggantian foto 2 tanaman terdaftar (*Cabai*, *Lidah Buaya*).

---

## 1. System State & Architectural Confirmation

### 1.1 Storage Architecture Confirmation (Section 15 & 46)
- **Status Cloud Storage**: Proyek Firebase saat ini berjalan pada plan **Spark** (tanpa billing Blaze).
- **Billing Guardrail**: **TIDAK** mengaktifkan billing Blaze secara sepihak.
- **Strategi Penyimpanan**:
  1. Foto hasil jepretan Kamera dan pilihan Galeri dikompresi di background thread menggunakan `ImageCompressor` (sisi terpanjang maks 1600px, kualitas JPEG 80%).
  2. Berkas terkompresi disimpan ke direktori privat aplikasi (`context.filesDir/photos/plant_<timestamp>.jpg`).
  3. URL gambar web (`https://...`) disimpan langsung sebagai string HTTPS.
  4. Firestore **HANYA** menyimpan referensi string path/URL (`primaryPhotoUrl`).
  5. **TIDAK ADA** binary Base64 yang disimpan di dalam dokumen Firestore (mencegah batas 1 MiB dokumen).

### 1.2 Image Input Channels Confirmation (Section 8 – 12)
- **Skenario A (Sirih)**: Input via **KAMERA** (`"Kamera"`).
  - Menggunakan `ActivityResultContracts.TakePicturePreview()`.
  - Izin kamera (`Manifest.permission.CAMERA`) diminta secara *just-in-time* saat tombol ditekan.
  - Gambar referensi `assets/qa/plants/sirih.jpg` ditangkap melalui kamera emulator / real sensor.
- **Skenario B (Pegagan)**: Input via **GALERI** (`"Galeri"`).
  - Menggunakan `ActivityResultContracts.PickVisualMedia(ImageOnly)`.
  - Berkas `assets/qa/plants/pegagan.jpg` didorong ke penyimpanan emulator dan dipilih melalui Photo Picker sistem.
- **Skenario C (Kemangi)**: Input via **URL / IMAGE IMPORT**.
  - Menggunakan komponen input URL yang ada pada `MasterPlantFormScreen` (`"Input URL / Aset Gambar"`).
  - Memasukkan URL HTTPS gambar terverifikasi dari Wikimedia Commons.
- **Skenario Penggantian Foto (Cabai & Lidah Buaya)**:
  - **Cabai**: Penggantian via **Galeri** menggunakan `assets/qa/plants/cabai_replacement.jpg`.
  - **Lidah Buaya**: Penggantian via **Kamera** menggunakan `assets/qa/plants/lidah_buaya_replacement.jpg`.
  - ID Tanaman (`cabai` dan `lidah_buaya`) tetap stabil dan tidak berubah.

### 1.3 Firestore Data Model Confirmation (Section 1 & 16)
- **Koleksi**: `master_plants`
- **Bidang Wajib**:
  - `nameId` (*Nama Tanaman Bahasa Indonesia*)
  - `primaryPhotoUrl` (*Foto Tanaman*)
- **Bidang Opsional**:
  - `nameLatin`, `nameMandarin`, `pinyin`, `description`, `characteristics`, `commonUses`, `cultivationNotes`, `mandarinAudioUrl`.
- **Bidang yang Dihapus dari UI Admin**:
  - *URL Referensi Foto*, *Fotografer*, *Lisensi Foto*, *Referensi Taksonomi Ilmiah* (tetap dihapus sesuai Section 3).

### 1.4 Mandarin Voice Optionality Confirmation (Section 20, 21, 45)
- Suara Mandarin adalah **STRICTLY OPTIONAL**.
- Nilai `mandarinAudioUrl` = `null` untuk Sirih, Pegagan, dan Kemangi.
- `PlantDetailScreen` tidak mengalami crash atau NullPointerException; ikon audio disembunyikan/dinonaktifkan secara anggun jika URL audio bernilai null/kosong.

---

## 2. Test Execution Matrix (PLANT-01 s/d PLANT-20)

| Test ID | Skenario / Target | Saluran Input | Ekspektasi Hasil | Target Metrik | Status Akhir |
|---|---|---|---|---|---|
| **PLANT-01** | Tambah Sirih | Kamera Admin UI | Dokumen Firestore tercipta, foto terkompresi, muncul di katalog | UI response ≤ 300ms | **PASS** |
| **PLANT-02** | Tambah Pegagan | Galeri Admin UI | Dokumen Firestore tercipta, foto terkompresi, muncul di katalog | UI response ≤ 300ms | **PASS** |
| **PLANT-03** | Tambah Kemangi | URL Import Admin UI | Dokumen Firestore tercipta, Coil merender URL HTTPS dengan mulus | UI response ≤ 300ms | **PASS** |
| **PLANT-04** | Ganti Foto Cabai | Galeri Admin UI | ID `cabai` stabil, foto diperbarui, data lain tidak berubah | Update Firestore | **PASS** |
| **PLANT-05** | Ganti Foto Lidah Buaya | Kamera Admin UI | ID `lidah_buaya` stabil, foto diperbarui, data lain tidak berubah | Update Firestore | **PASS** |
| **PLANT-06** | Validasi Nama Kosong | Form Submit Admin | Muncul pesan validasi: *"Nama tanaman (Indonesia) wajib diisi."*, tidak crash | Instan (< 100ms) | **PASS** |
| **PLANT-07** | Validasi Foto Kosong | Form Submit Admin | Muncul pesan validasi: *"Foto tanaman wajib disertakan."*, tidak crash | Instan (< 100ms) | **PASS** |
| **PLANT-08** | Input Foto Tidak Valid | Input URL Non-HTTPS | Menolak URL `http://`, menampilkan peringatan protokol HTTPS | Validasi form | **PASS** |
| **PLANT-09** | Izin Kamera Ditolak | Dialog Izin Kamera | Menampilkan notice informatif anggun, fallback galeri tetap aktif | Tanpa crash | **PASS** |
| **PLANT-10** | Batal Pilih Galeri | Photo Picker Cancel | Form tetap utuh, tidak mengubah foto yang sudah ada, tanpa crash | Responsif | **PASS** |
| **PLANT-11** | Deteksi Duplikasi | Tambah Tanaman Sama | Menolak spesies yang sudah ada berdasarkan nama Latin/Indonesia | Dialog peringatan | **PASS** |
| **PLANT-12** | Tanpa Suara Mandarin | Audio URL Null | Tersimpan sukses, detail screen tampil normal, tombol audio tersembunyi | Tanpa NPE | **PASS** |
| **PLANT-13** | Penulisan Firestore | Admin Submit | Dokumen `master_plants` terbuat, metadata timestamp lengkap, tanpa Base64 | Write Firestore | **PASS** |
| **PLANT-14** | Pembacaan Firestore | Admin List Read | Data langsung terbaca dari Firestore ke daftar Admin & Detail | Real-time / Flow | **PASS** |
| **PLANT-15** | Sinkronisasi Dashboard | Listener Real-time | Statistik total master plant di Admin Dashboard bertambah tanpa restart app | StateFlow update | **PASS** |
| **PLANT-16** | Sinkronisasi Katalog Publik | Public Catalog Read | Tanaman baru muncul di Katalog Publik dan dapat diklik menuju detail | Query publik | **PASS** |
| **PLANT-17** | Keamanan Akses Tulis | Aturan Firestore | Operasi tulis hanya diizinkan untuk ADMIN; Publik hanya baca | Security Rules | **PASS** |
| **PLANT-18** | Responsivitas UI | Tombol & Navigasi | Klik tombol, scrolling form, dan feedback visual instan | ≤ 300ms | **PASS** |
| **PLANT-19** | Kompresi Gambar | Pipeline ImageCompressor | Sisi terpanjang disesuaikan ke ≤ 1600px, kualitas 80%, off-main thread | Tidak block UI | **PASS** |
| **PLANT-20** | Durasi Upload/Penyimpanan | Penyimpanan Berkas | Penyimpanan berkas terkompresi lokal & update Firestore terekam | Pengukuran ms | **PASS** |

---

## 3. Preparation & Execution Protocol

1. **Aset QA Terunduh**:
   - `assets/qa/plants/sirih.jpg` (5.44 MB, 3000x4000)
   - `assets/qa/plants/pegagan.jpg` (6.33 MB, 3008x2000)
   - `assets/qa/plants/kemangi.jpg` (11.45 MB, 6000x4000)
   - `assets/qa/plants/cabai_replacement.jpg` (2.56 MB, 4608x3456)
   - `assets/qa/plants/lidah_buaya_replacement.jpg` (10.56 MB, 5472x3648)
2. **Kondisi Emulator**:
   - `Pixel_7` (emulator-5554) aktif dan terhubung.
   - Sesi Admin terautentikasi (`admin@bubakangreen.id`).
3. **Penyempurnaan Kode Sebelum Eksekusi**:
   - Menyesuaikan `PlantImage.kt` agar Coil dapat merender berkas foto lokal aplikasi (`/data/...`) selain URL HTTPS.
   - Menambahkan opsi `"Kamera"` dan `"Galeri"` pada card penggantian foto di `MasterPlantFormScreen.kt` agar admin dapat langsung memilih kamera atau galeri saat mengganti foto yang sudah ada.
