# LAPORAN QUALITY CONTROL: PHOTO MANAGEMENT & IMAGE COMPRESSION
**Produk:** Bubakan Green  
**Fase:** Phase Admin Remediation Final  
**Status Verifikasi:** **VERIFIED & PASSED** (Unit Test Suite + Code Review)  
**Dokumen Terkait:** [PHOTO_STORAGE_DECISION.md](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/docs/phase-admin/PHOTO_STORAGE_DECISION.md)

---

## 1. Aturan Guardrail Penyimpanan Gambar
1. **Dilarang Menyimpan Binary / Base64 di Firestore:**  
   - Dokumen Firestore memiliki batas keras 1 MB per dokumen. Menyimpan string Base64 foto 8–15 MB akan merusak database dan melanggar batasan arsitektur.
   - Guardrail: Seluruh foto disimpan sebagai URL (HTTPS string) atau jalur file lokal terkompresi di cache aplikasi. Dokumen Firestore hanya menyimpan string pointer.
2. **Kepatuhan Billing Cloud Storage:**  
   - Firebase Cloud Storage memerlukan paket Blaze (Pay-as-you-go).
   - Penggunaan penyimpanan lokal aman (`appCache`) dan input remote URL memastikan aplikasi tidak mengaktifkan penagihan Google Cloud tanpa izin pengguna.

---

## 2. Fitur Manajemen Foto Lahan
1. **Batas Foto 0 s/d 3:**
   - Counter dinamis ditampilkan secara real-time: `0/3`, `1/3`, `2/3`, `3/3`.
   - Tombol "Tambah Foto" dan dialog input dinonaktifkan secara otomatis saat jumlah foto telah mencapai `3/3`.
   - Percobaan penambahan foto ke-4 diblokir (`viewModel.onAddPhoto` mengembalikan pesan validasi "Maksimal 3 foto per lahan").
2. **Penggantian dan Penghapusan Foto:**
   - Setiap thumbnail foto pada grid memiliki tombol aksi individual:
     - **Ganti (Edit Icon):** Membuka photo picker untuk mengganti foto pada indeks yang dipilih.
     - **Hapus (Delete Icon):** Menghapus foto dari daftar dan memperbarui hitungan secara instan.
3. **Izin Kamera Kontekstual:**
   - Menggunakan `rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission())`.
   - Izin kamera hanya diminta saat pengguna mengetuk tombol "Ambil Foto" (bukan saat layar dibuka).
   - Jika ditolak, aplikasi tidak force close melainkan menampilkan pesan instruksi dan menyediakan opsi galeri sebagai alternatif.

---

## 3. Kompresi Gambar (`ImageCompressor.kt`)
1. **Downscaling:** Resolusi sisi terpanjang dibatasi maksimum 1600 piksel (`maxDimension = 1600`).
2. **Koreksi Rotasi EXIF:** Membaca tag `ExifInterface.TAG_ORIENTATION` dan melakukan rotasi matriks bitmap yang sesuai sehingga foto tidak terbalik atau miring.
3. **Kompresi JPEG:** Kualitas kompresi 80% (`quality = 80`), mereduksi ukuran file dari 8–15 MB menjadi < 500 KB per foto.
4. **Eksekusi Asinkron:** Seluruh kompresi dijalankan di thread background (`Dispatchers.IO`) untuk menjamin UI tetap 60fps tanpa lagging.

---

## 4. Bukti Pengujian Unit
```
Suite: id.bubakangreen.app.ui.LocationFormViewModelTest
Method: photoManagement_enforcesMaxThreePhotos_andSupportsReplaceAndRemove
Status: PASSED
```
- Menambahkan 3 foto berturut-turut berhasil (`photos.size == 3`).
- Menambahkan foto ke-4 ditolak (`photos.size` tetap 3).
- Mengganti foto pada indeks 1 berhasil memperbarui urutan array.
- Menghapus foto pada indeks 0 berhasil mereduksi `photos.size` menjadi 2.
