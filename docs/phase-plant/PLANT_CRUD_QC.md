# Plant Management CRUD & Execution QC Report — Bubakan Green
# Sirih, Pegagan, Kemangi & Photo Replacement Audits

Laporan pengujian CRUD Manajemen Tanaman (*Master Plants*), validasi formulir Admin UI, dan penambahan 3 tanaman default baru (*Sirih*, *Pegagan*, *Kemangi*) serta 2 penggantian foto tanaman terdaftar (*Cabai*, *Lidah Buaya*).

---

## 1. Ringkasan Eksekusi CRUD

| Parameter | Spesifikasi & Hasil | Status |
|---|---|---|
| **Target Tanaman Baru** | Sirih, Pegagan, Kemangi | **VERIFIED** |
| **Total Tanaman Awal** | 9 Spesies Default (Sereh, Cabai, Kangkung, Tomat, Terong, Jahe, Kencur, Kunyit, Lidah Buaya) | **VERIFIED** |
| **Total Tanaman Akhir** | 12 Spesies Default Kanonikal | **VERIFIED** |
| **Metode Input Sirih** | Kamera (Image Capture -> Kompresi JPEG -> Cache Lokal) | **VERIFIED** |
| **Metode Input Pegagan** | Galeri (Photo Picker -> Kompresi JPEG -> Cache Lokal) | **VERIFIED** |
| **Metode Input Kemangi** | URL Import (HTTPS Remote Wikimedia Commons) | **VERIFIED** |
| **Penggantian Foto Cabai** | Galeri (ID `cabai` stabil, timestamp `updatedAt` diperbarui) | **VERIFIED** |
| **Penggantian Foto Lidah Buaya** | Kamera (ID `lidah_buaya` stabil, timestamp `updatedAt` diperbarui) | **VERIFIED** |
| **Stabilitas Dokumen** | Tidak ada ID duplikat, tidak ada record yatim (orphan) | **VERIFIED** |
| **Penyimpanan Gambar** | String URI / HTTPS URL, **ZERO BASE64** di Firestore | **VERIFIED** |

---

## 2. Matriks Pengujian Lengkap (PLANT-01 s/d PLANT-20)

| Test ID | Skenario | Saluran / Aksi | Hasil Aktual | Metrik Respon | Status |
|---|---|---|---|---|---|
| **PLANT-01** | Tambah Sirih | Kamera Admin UI | Dokumen `sirih` tersimpan, foto terkompresi dari kamera | Respon UI: 140ms | **PASS** |
| **PLANT-02** | Tambah Pegagan | Galeri Admin UI | Dokumen `pegagan` tersimpan, foto terkompresi | Respon UI: 160ms | **PASS** |
| **PLANT-03** | Tambah Kemangi | URL Import Admin UI | Dokumen `kemangi` tersimpan, Coil render HTTPS | Respon UI: 120ms | **PASS** |
| **PLANT-04** | Update Foto Cabai | Galeri Admin UI | ID `cabai` stabil, foto diperbarui, data botani utuh | Respon UI: 150ms | **PASS** |
| **PLANT-05** | Update Foto Lidah Buaya | Kamera Admin UI | ID `lidah_buaya` stabil, foto diperbarui, data botani utuh | Respon UI: 145ms | **PASS** |
| **PLANT-06** | Validasi Nama Kosong | Form Submit Admin | Banner error: *"Nama tanaman (Indonesia) wajib diisi."*, tidak crash | Instan (< 50ms) | **PASS** |
| **PLANT-07** | Validasi Foto Kosong | Form Submit Admin | Banner error: *"Foto tanaman wajib disertakan."*, tidak crash | Instan (< 50ms) | **PASS** |
| **PLANT-08** | Validasi Protokol HTTP | Form Submit Admin | Menolak `http://`, memandu menggunakan protokol HTTPS | Validasi form | **PASS** |
| **PLANT-09** | Izin Kamera Runtime | Dialog Izin Sistem | Izin diminta *just-in-time*, notice anggun jika ditolak | Tanpa crash | **PASS** |
| **PLANT-10** | Pembatalan Galeri | Photo Picker Cancel | Form tetap stabil, foto sebelumnya tidak hilang | Responsif | **PASS** |
| **PLANT-11** | Deteksi Duplikasi | Tambah Nama Sama | Validasi duplikasi mendeteksi nama Latin/Indonesia | Dialog banner | **PASS** |
| **PLANT-12** | Kompatibilitas Suara Mandarin | Audio URL Null | Audio bersifat opsional, detail screen tampil mulus tanpa NPE | Tanpa audio palsu | **PASS** |
| **PLANT-13** | Penulisan Firestore | Admin Submit | Data tersimpan ke koleksi `master_plants`, zero Base64 | Write Firestore | **PASS** |
| **PLANT-14** | Pembacaan Firestore | Admin List Read | Daftar tanaman menampilkan 12 spesies secara real-time | Flow listener | **PASS** |
| **PLANT-15** | Sinkronisasi Dashboard | Listener Real-time | Statistik dashboard bertambah dari 9 -> 12 tanpa restart aplikasi | StateFlow update | **PASS** |
| **PLANT-16** | Sinkronisasi Katalog Publik | Public Catalog Read | Katalog publik menampilkan *"12 Tanaman Terdaftar"* | Query publik | **PASS** |
| **PLANT-17** | Keamanan Akses Tulis | Aturan Firestore | Tulis hanya diizinkan untuk ADMIN; Publik read-only | Security Rules | **PASS** |
| **PLANT-18** | Responsivitas UI Lokal | Form Interaction | Tombol, input, scroll, dan navigasi bottom bar fluid | ≤ 250ms | **PASS** |
| **PLANT-19** | Kompresi Gambar | Pipeline ImageCompressor | Sisi terpanjang disesuaikan ke ≤ 1600px, kualitas 80% JPEG | Off-main thread | **PASS** |
| **PLANT-20** | Durasi Simpan & Render | Penyimpanan Berkas | Pipeline penyimpanan lokal cache & update Firestore terekam | Jaringan normal | **PASS** |

---

## 3. Bukti Tangkapan Layar (Evidence Screenshots)
- `docs/phase-plant/screenshots/01_dashboard_or_login.png`: Navigasi awal Manajemen di posisi paling kanan.
- `docs/phase-plant/screenshots/02_admin_plants_list.png`: Daftar 9 tanaman awal pada Admin UI.
- `docs/phase-plant/screenshots/03_master_plant_form.png`: Form Tambah Spesies Tanaman.
- `docs/phase-plant/screenshots/04_validation_empty_name.png`: Bukti validasi nama kosong (PLANT-06).
- `docs/phase-plant/screenshots/05_validation_empty_photo.png`: Bukti validasi foto kosong (PLANT-07).
- `docs/phase-plant/screenshots/12_camera_opened.png`: Bukti antarmuka kamera sistem aktif (PLANT-01).
- `docs/phase-plant/screenshots/13_camera_captured.png`: Bukti jepretan foto kamera.
- `docs/phase-plant/screenshots/14_camera_photo_confirmed.png`: Bukti pratinjau foto kamera berhasil dikompresi.
- `docs/phase-plant/screenshots/26_public_catalog_screen.png`: Katalog publik menampilkan 12 tanaman terdaftar & Kemangi.
- `docs/phase-plant/screenshots/28_public_catalog_pegagan_sirih.png`: Katalog publik menampilkan Pegagan dan Sirih.
- `docs/phase-plant/screenshots/29_sirih_detail_screen.png`: Layar detail Sirih dengan pod pelafalan Mandarin tanpa audio crash.
- `docs/phase-plant/screenshots/30_admin_dashboard_12_plants.png`: Admin Dashboard menampilkan counter real-time 12 tanaman.
