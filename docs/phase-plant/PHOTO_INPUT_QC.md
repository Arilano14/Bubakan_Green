# Photo Input Channels QC Report — Bubakan Green
# Camera, Gallery, and URL Import Scenarios

Laporan pengujian 3 saluran input foto tanaman pada antarmuka Admin:
1. **Kamera** (*Sirih*, *Lidah Buaya*)
2. **Galeri** (*Pegagan*, *Cabai*)
3. **URL Import** (*Kemangi*)

---

## 1. Skenario A: Input Foto via Kamera (Sirih)

* **Kontrak Activity Result**: `ActivityResultContracts.TakePicturePreview()`.
* **Manajemen Izin (`Manifest.permission.CAMERA`)**:
  * Izin **TIDAK** diminta saat aplikasi dibuka, tidak diminta saat dashboard admin dibuka, dan tidak diminta saat list tanaman dibuka.
  * Izin diminta secara *just-in-time* hanya ketika admin menekan tombol `"Kamera"`.
* **Alur Pengujian**:
  1. Admin membuka form input spesies tanaman.
  2. Menekan tombol `"Kamera"`.
  3. Dialog izin sistem muncul. Setelah disetujui, viewfinder kamera sistem aktif.
  4. Pengguna mengambil foto melalui tombol *Shutter*.
  5. Meninjau foto (*Done / Retake / Cancel*).
  6. Menekan tombol centang (*Done*).
  7. Bitmap ditangkap -> dikompresi di background thread -> disimpan ke cache aplikasi -> ditampilkan pada thumbnail pratinjau form.
* **Hasil**: **PASS** (Terekam pada screenshot `docs/phase-plant/screenshots/12_camera_opened.png` dan `docs/phase-plant/screenshots/14_camera_photo_confirmed.png`).

---

## 2. Skenario B: Input Foto via Galeri (Pegagan)

* **Kontrak Activity Result**: `ActivityResultContracts.PickVisualMedia(ImageOnly)`.
* **Manajemen Izin**:
  * Menggunakan Android Photo Picker bawaan sistem (AndroidX Activity Result).
  * Tidak memerlukan dan tidak meminta izin `READ_EXTERNAL_STORAGE` yang berlebihan.
* **Alur Pengujian**:
  1. Admin menekan tombol `"Galeri"`.
  2. Photo Picker sistem terbuka menampilkan koleksi foto perangkat.
  3. Pengguna memilih berkas gambar (`pegagan.jpg`).
  4. Uri diproses oleh `ImageCompressor.compressAndResizeImage` -> disimpan ke cache -> ditampilkan pada thumbnail pratinjau.
* **Hasil**: **PASS**.

---

## 3. Skenario C: Input Foto via URL / Import Gambar (Kemangi)

* **Komponen Form**: Tombol toggle `"Input URL / Aset Gambar"` yang membuka bidang teks `OutlinedTextField`.
* **Protokol Keamanan**:
  * Enforcing HTTPS: Memblokir tautan tidak aman (`http://`).
  * Menerima URL web aman (`https://upload.wikimedia.org/wikipedia/commons/1/1e/Ocimum_basilicum_CG_NBG_LR.jpg`).
* **Pemuatan Gambar**:
  * Coil `AsyncImage` memuat gambar secara langsung via jaringan dengan cache memori dan cache disk otomatis.
* **Hasil**: **PASS** (Terekam pada screenshot `docs/phase-plant/screenshots/26_public_catalog_screen.png`).

---

## 4. Evaluasi Ketahanan Input (Robustness)
* **Pembatalan Kamera / Galeri**: Jika pengguna menekan tombol *Back* atau *Cancel* pada kamera atau photo picker, form tidak mengalami crash dan mempertahankan state data input yang telah diisi.
* **Format Berkas yang Didukung**: JPEG, WebP, PNG.
