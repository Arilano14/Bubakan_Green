# RENCANA MIGRASI SKEMA ADMIN & AUDIT PENYIMPANAN FOTO
**Produk:** Bubakan Green  
**Fase:** Phase Admin Remediation  
**Status:** DRAFT ARCHITECTURE REVIEW  

---

## 1. Rencana Migrasi Skema Data (Schema Migration Plan)

### Koleksi Firestore `/locations`

| Field Lama | Field Baru | Perubahan / Aksi | Penanganan Kompatibilitas Legacy | Tingkat Risiko |
| :--- | :--- | :--- | :--- | :---: |
| `coverPhotoUrl: String?` | `photos: List<String>` | Menambah array URL foto (maks 3 item) | Tetap mengisi `coverPhotoUrl = photos.firstOrNull()` dan membaca `coverPhotoUrl` jika `photos` kosong pada dokumen lama. | **Rendah** |
| `photoUrl: String?` | `photos: List<String>` | Depresiasi bertahap | Menjaga `photoUrl = photos.firstOrNull() ?: coverPhotoUrl`. | **Rendah** |
| `description: String` | `description: String` | Menjadi opsional di UI | Model domain diberi default `description: String = ""`. Jika form dikosongkan, dokumen menyimpan string kosong `""`. | **Rendah** |
| `rw: String` | `regionTag: String` & `rw: String` | Dibatasi hanya 6 nilai kanonikal: `KELURAHAN`, `RW_01`, `RW_02`, `RW_03`, `RW_04`, `RW_05` | String lama seperti `"01"`, `"03"` dinormalisasi otomatis menjadi `"RW 01"`, `"RW 03"`. Nilai disimpan konsisten ke `rw` dan `regionTag`. | **Rendah** |
| `status: String` | `status: String` | Menghilangkan `PENDING_APPROVAL` dari alur pembuatan lahan | Dokumen baru langsung tersimpan sebagai `PUBLISHED` atau `ACTIVE`. Dokumen lama dengan `PENDING_APPROVAL` dapat dibaca sebagai aktif atau dimigrasikan ke `PUBLISHED`. | **Rendah** |

### Koleksi Firestore `/master_plants`

| Field Lama | Field Baru | Perubahan / Aksi | Penanganan Kompatibilitas Legacy | Tingkat Risiko |
| :--- | :--- | :--- | :--- | :---: |
| `nameId: String` | `nameId: String` | Tetap WAJIB (*) | Validasi di ViewModel dan UI memastikan tidak kosong. | **Nol** |
| `primaryPhotoUrl: String?` | `primaryPhotoUrl: String` | Menjadi WAJIB (*) | Menolak penyimpanan jika foto belum dipilih/diunggah. Dokumen eksisting 9 tanaman telah memiliki foto. | **Rendah** |
| `nameLatin: String` | `nameLatin: String` | Menjadi OPSIONAL | Jika dikosongkan di UI, disimpan sebagai string kosong `""`. | **Rendah** |
| `description: String` | `description: String` | Menjadi OPSIONAL | Jika dikosongkan di UI, disimpan sebagai string kosong `""`. | **Rendah** |
| `imageSource: String?` | (Dihapus dari Form UI) | Field lama tidak lagi ditampilkan di UI Admin | Tetap dipertahankan di domain model dan serializer agar metadata 9 tanaman eksisting tidak hilang. | **Nol** |
| `imageLicense: String?` | (Dihapus dari Form UI) | Field lama tidak lagi ditampilkan di UI Admin | Tetap dipertahankan di domain model. | **Nol** |
| `imageAuthor: String?` | (Dihapus dari Form UI) | Field lama tidak lagi ditampilkan di UI Admin | Tetap dipertahankan di domain model. | **Nol** |
| `sourceReferences: String` | (Dihapus dari Form UI) | Field lama tidak lagi ditampilkan di UI Admin | Tetap dipertahankan di domain model. | **Nol** |

---

## 2. Audit Opsi Penyimpanan Foto (Storage Options Audit)

Sesuai arahan pada **Section H & I**, Firestore memiliki batas ukuran dokumen 1 MiB dan dilarang keras menyimpan binary/Base64 langsung di dalam dokumen Firestore. Berikut audit 3 opsi penyimpanan:

### Opsi A: Existing Configured Architecture (URL Referensi / Aset Lokal Terverifikasi)
- **Mekanisme:** Admin memasukkan tautan HTTPS foto valid (misal dari CDN publik / Wikimedia / URL foto web kelurahan) atau memilih dari katalog aset lokal. Foto diproses dan divalidasi keamanannya sebelum disimpan sebagai URL string di Firestore.
- **Biaya:** **Rp 0 / Bulan (100% Gratis - Tetap di Firebase Spark Plan).**
- **Keamanan:** Bebas risiko exposure kredensial penyimpanan; link divalidasi protokol HTTPS resmi.
- **Pemeliharaan:** Nol pemeliharaan server backend.
- **Kompleksitas Implementasi:** Rendah. Tidak perlu penambahan SDK storage pihak ketiga.
- **Dukungan Mobile Upload:** Terbatas pada referensi URL dan aset lokal yang sudah dioptimasi, belum mendukung pengunggahan file biner langsung dari memori telepon ke cloud storage milik proyek tanpa storage backend.
- **Dukungan Public Read:** Sangat tinggi via CDN publik dan Coil caching.

### Opsi B: Firebase Cloud Storage + Upgrade ke Paket Blaze
- **Mekanisme:** Menambahkan dependensi `com.google.firebase:firebase-storage`, mengunggah byte gambar hasil kompresi `ImageCompressor` ke bucket `gs://bubakan-green.appspot.com/locations/...` menggunakan Firebase SDK, dan menyimpan download URL yang dihasilkan ke dokumen Firestore.
- **Biaya:** **Memerlukan Kartu Kredit / Metode Pembayaran untuk mengaktifkan Paket Blaze (Pay-as-you-go).** Meskipun Google Cloud menyediakan free quota (5 GB storage, 1 GB/hari bandwidth keluar), aktivasi proyek Firebase Storage di Google Cloud Console mewajibkan penautan akun penagihan (billing account).
- **Keamanan:** Sangat tinggi via aturan keamanan bawaan Firebase Storage (`storage.rules`).
- **Pemeliharaan:** Terkelola penuh secara otomatis oleh Google Cloud.
- **Kompleksitas Implementasi:** Menengah (memerlukan penambahan library Firebase Storage dan penanganan upload task coroutine).
- **Dukungan Mobile Upload:** Sangat baik via native Android Firebase Storage SDK.
- **Dukungan Public Read:** Sangat baik via download URL bertoken.
- **Guardrail:** **TIDAK DIAKTIFKAN OTOMATIS** karena membutuhkan otorisasi penagihan / kartu kredit oleh pemilik akun Google Cloud.

### Opsi C: Penyimpanan Gambar Terkompresi Internal / CDN Gambar Gratis (e.g., Free Image Host API / Supabase / ImgBB / Local Internal Cache)
- **Mekanisme:**
  - *Alternatif C1 (Local Sandboxed Storage):* Foto yang diambil dari kamera/galeri dikompresi oleh `ImageCompressor` dan disimpan di direktori internal aplikasi (`context.filesDir/lahan_photos/`) dengan URI internal. Sangat cocok untuk pengujian perangkat fisik mandiri tanpa server berbayar, namun foto tidak tersinkronisasi ke perangkat pengguna lain secara publik.
  - *Alternatif C2 (Free Dedicated Image Hosting API / Cloudinary Free Tier):* Menggunakan layanan CDN gambar gratis tanpa kewajiban kartu kredit yang menyediakan REST upload endpoint via API key.
- **Biaya:** **Rp 0 / Bulan (Bebas biaya).**
- **Keamanan:** Tergantung penyedia layanan pihak ketiga; jika internal sandboxed, 100% privat di perangkat.
- **Pemeliharaan:** Memerlukan pengelolaan token API jika menggunakan layanan pihak ketiga eksternal.
- **Kompleksitas Implementasi:** Menengah.
- **Dukungan Mobile Upload:** Baik.

### Rekomendasi Arsitektur Penyimpanan:
Untuk mematuhi aturan ketat **Section I** (jangan mengaktifkan Blaze tanpa persetujuan eksplisit):
1. **Langkah 1 (Audit & Laporan):** Melaporkan status Blaze ini kepada user untuk mendapatkan arahan apakah proyek akan dihubungkan ke Blaze atau tetap di Spark plan.
2. **Langkah 2 (Implementasi Dual-Support):**
   - Di sisi UI dan ViewModel: Mengimplementasikan flow kompresi `ImageCompressor` (EXIF orientation fix, resize maks 1600-1920px, encode JPEG 80%).
   - Mendukung input foto dari galeri/kamera serta URL referensi HTTPS langsung yang terkompresi.
   - Jika belum ada bucket Blaze aktif, foto yang dipilih dari kamera/galeri disimpan di penyimpanan lokal aplikasi terkompresi dan/atau disimpan sebagai URL terverifikasi tanpa membuat dokumen Firestore bengkak.

---

## 3. Rencana Penghapusan Alur Approval (Approval Removal Plan)
1. **Pensiunkan UI Antrean:**
   - Hapus kartu "Persetujuan" dari `AdminDashboardScreen.kt`.
   - Hapus SummaryPill "Antrean".
   - Hapus rute `Screen.LocationApproval` dari `BubakanNavHost.kt`.
2. **Normalisasi Status Lahan:**
   - Ubah logika `LocationFormViewModel.kt`: dari semula menyimpan dengan `status = LocationStatus.PENDING_APPROVAL` menjadi langsung `status = LocationStatus.PUBLISHED` (atau `ACTIVE`).
   - Dengan ini, lahan yang didaftarkan langsung muncul di peta publik, daftar lahan, dan dashboard admin tanpa perlu proses approval manual.
3. **Depresiasi `LocationApprovalViewModel`:**
   - Tandai deprecated / bersihkan kode yang tidak terpakai tanpa merusak modul lain.

---

## 4. Rencana Penghapusan QR Lokasi (Location QR Removal Plan)
1. **Pembersihan UI:**
   - Di `LocationDetailScreen.kt`: hapus tombol QR di TopBar (`onQrClick = null`) dan blok `if (showQrDialog) { QrCodeDisplayDialog(...) }`.
2. **Pembersihan Admin:**
   - Pastikan layar Admin tidak memiliki opsi unduh, pratinjau, atau cetak QR untuk Lahan.
3. **Integritas QR Tanaman:**
   - QR tanaman di `PlantDetailScreen.kt` tetap utuh 100% menggunakan `QrUrlBuilder.buildPlantUrl()`.
   - Web fallback route `https://bubakan-green.web.app/plant/{id}` tetap utuh 100%.
