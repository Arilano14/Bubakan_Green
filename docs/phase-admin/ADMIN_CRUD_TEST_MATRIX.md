# MATRIKS PENGUJIAN FRONTEND-FIRST ADMIN CRUD & DASHBOARD SYNC
**Produk:** Bubakan Green  
**Fase:** Admin Remediation  
**Prinsip Verifikasi:** FRONTEND-FIRST ACCEPTANCE (Mutasi WAJIB berasal dari UI Android Admin, bukan Firebase Console)  
**Status:** **DRAFT SPECIFICATION**

---

## 1. Aturan Uji Mandatori (Strict Testing Rules)
1. Seluruh pengujian CRUD berstatus valid hanya jika dieksekusi melalui **Antarmuka Pengguna Admin (Android UI)**.
2. Dilarang menggunakan skrip injeksi Python, REST API langsung, atau pengeditan Firebase Console sebagai bukti kelulusan CRUD.
3. Firebase Console hanya diperbolehkan sebagai alat bantu inspeksi verifikasi status backend.
4. Nilai KPI di dashboard tidak boleh di-hardcode.

---

## 2. Matriks Kasus Uji (Test Matrix)

### A. Pengujian Sinkronisasi CRUD Tanaman (Plant CRUD & Dashboard Sync)

| ID Uji | Skenario Uji | Prosedur Langkah (Frontend UI) | Hasil yang Diharapkan | Status |
| :--- | :--- | :--- | :--- | :---: |
| **TEST-A** | Admin Creates Plant | 1. Buka Admin Dashboard.<br>2. Masuk ke "Kelola Tanaman" -> "+ Tambah".<br>3. Isi Nama Tanaman Indonesia (*) dan pilih Foto (*).<br>4. Kosongkan field opsional.<br>5. Klik Simpan. | - Form tersimpan tanpa error.<br>- Daftar tanaman bertambah 1.<br>- Counter Total Tanaman di Dashboard bertambah seketika.<br>- Dokumen Firestore `/master_plants` terbuat dengan ID stabil. | `UNVERIFIED` (Siap Diuji) |
| **TEST-B** | Admin Updates Plant | 1. Dari daftar tanaman, klik tombol Edit pada tanaman baru.<br>2. Ubah Nama Indonesia atau Foto.<br>3. Klik Perbarui Ensiklopedia. | - Data terupdate di Firestore.<br>- ID tanaman tidak berubah.<br>- Detail dan daftar tanaman menampilkan perubahan.<br>- Dashboard merefleksikan perubahan seketika. | `UNVERIFIED` (Siap Diuji) |
| **TEST-C** | Admin Archives/Deactivates Plant | 1. Dari form/daftar tanaman, pilih aksi arsipkan tanaman.<br>2. Konfirmasi tindakan. | - Tanaman berstatus nonaktif.<br>- Counter tanaman aktif di Dashboard berkurang otomatis tanpa reload manual. | `UNVERIFIED` (Siap Diuji) |

---

### B. Pengujian Sinkronisasi CRUD Lahan (Lahan CRUD & Dashboard Sync)

| ID Uji | Skenario Uji | Prosedur Langkah (Frontend UI) | Hasil yang Diharapkan | Status |
| :--- | :--- | :--- | :--- | :---: |
| **TEST-D** | Admin Creates Urban Farming | 1. Buka Dashboard Admin.<br>2. Pilih "Kelola Lahan" -> "+ Tambah".<br>3. Masukkan Nama Lahan (*).<br>4. Pilih Kategori "Urban Farming" (*).<br>5. Pilih Tag Wilayah "RW 01" (*).<br>6. Kosongkan deskripsi (opsional).<br>7. Simpan Lahan. | - Lahan tersimpan dengan status `PUBLISHED`/`ACTIVE`.<br>- Muncul langsung di daftar Kelola Lahan.<br>- Total Lahan di Dashboard bertambah otomatis.<br>- Tidak masuk antrean approval. | `UNVERIFIED` (Siap Diuji) |
| **TEST-E** | Admin Creates Taman Toga | 1. Masuk Form Tambah Lahan.<br>2. Masukkan Nama Lahan (*).<br>3. Pilih Kategori "Taman Toga" (*).<br>4. Pilih Tag Wilayah "Kelurahan" (*).<br>5. Simpan Lahan. | - Tersimpan langsung aktif.<br>- Muncul di daftar Lahan kategori Taman Toga.<br>- Dashboard bertambah otomatis. | `UNVERIFIED` (Siap Diuji) |
| **TEST-F** | Admin Edits Lahan | 1. Dari daftar Lahan, klik Edit.<br>2. Ubah Nama Lahan atau ganti kategori.<br>3. Simpan. | - Dokumen terupdate di Firestore.<br>- Daftar Lahan dan Dashboard langsung menampilkan nama/kategori baru seketika. | `UNVERIFIED` (Siap Diuji) |
| **TEST-G** | Admin Archives/Deactivates Lahan | 1. Nonaktifkan Lahan via Admin UI.<br>2. Konfirmasi. | - Status Lahan menjadi `INACTIVE`/`ARCHIVED`.<br>- Total Lahan Aktif di Dashboard berkurang otomatis. | `UNVERIFIED` (Siap Diuji) |

---

### C. Pengujian Manajemen Foto Lahan (Photo Management & Camera)

| ID Uji | Skenario Uji | Prosedur Langkah | Hasil yang Diharapkan | Status |
| :--- | :--- | :--- | :--- | :---: |
| **PHOTO-01** | Hitungan Foto 0/3 s/d 3/3 | Tambah foto secara bertahap: 1, 2, hingga 3 foto. | - Teks hitungan foto berubah: `0/3`, `1/3`, `2/3`, `3/3`.<br>- Pada `3/3`, tombol "Tambah Foto" otomatis nonaktif. | `UNVERIFIED` (Siap Diuji) |
| **PHOTO-02** | Blokir Foto ke-4 | Coba tambahkan foto saat hitungan sudah 3/3. | - Aksi diblokir, tidak menimpa foto lama tanpa izin pengguna. | `UNVERIFIED` (Siap Diuji) |
| **PHOTO-03** | Ganti & Hapus Foto | 1. Klik tombol Ganti pada thumbnail 1.<br>2. Klik tombol Hapus pada thumbnail 2. | - Thumbnail 1 terganti dengan foto baru.<br>- Thumbnail 2 terhapus, hitungan foto berkurang.<br>- Array foto di Firestore tetap sinkron. | `UNVERIFIED` (Siap Diuji) |
| **PHOTO-04** | Kompresi Gambar | Pilih foto ukuran besar (8–15 MB dari kamera gawai). | - Gambar diproses off-main thread.<br>- Dimensi disesuaikan (maks ~1600–1920px).<br>- Ukuran payload turun signifikan (>80%).<br>- Visual tetap tajam pada layar. | `UNVERIFIED` (Siap Diuji) |
| **PHOTO-05** | Izin Kamera Kontekstual | 1. Buka Form Lahan (tanpa izin kamera diminta).<br>2. Klik "Ambil Foto".<br>3. Dialog izin kamera sistem muncul. | - Izin hanya diminta saat tombol diklik.<br>- Jika ditolak, aplikasi tidak crash, muncul pesan "Izin kamera belum diberikan" dan opsi galeri tetap dapat digunakan. | `UNVERIFIED` (Siap Diuji) |

---

### D. Pengujian Lingkup QR & Integritas Publik

| ID Uji | Skenario Uji | Prosedur Langkah | Hasil yang Diharapkan | Status |
| :--- | :--- | :--- | :--- | :---: |
| **QR-01** | QR Lokasi Terhapus | 1. Buka layar publik Detail Kebun/Lahan.<br>2. Periksa TopBar dan badan layar. | - Tombol QR di TopBar hilang.<br>- Tidak ada opsi tampilkan/unduh QR lokasi.<br>- Tidak ada dialog QR lokasi. | `UNVERIFIED` (Siap Diuji) |
| **QR-02** | QR Tanaman Tetap Utuh | 1. Buka Detail Tanaman publik.<br>2. Klik tombol QR di TopBar. | - Dialog QR tanaman muncul tajam dan terbaca.<br>- URL QR mengarah ke `https://bubakan-green.web.app/plant/{id}`.<br>- Scan QR membuka tanaman yang sesuai. | `UNVERIFIED` (Siap Diuji) |
| **QR-03** | Stabilitas QR Pasca-Update | 1. Update nama, deskripsi, atau foto tanaman.<br>2. Buka QR tanaman tersebut. | - QR tanaman tetap valid dan tidak perlu dicetak ulang karena ID tanaman stabil. | `UNVERIFIED` (Siap Diuji) |

---

### E. Pengujian Keamanan Firestore (Security Rules)

| ID Uji | Skenario Uji | Prosedur Langkah | Hasil yang Diharapkan | Status |
| :--- | :--- | :--- | :--- | :---: |
| **SEC-01** | Unauthenticated Write Denied | Akses tulis ke `/locations` atau `/master_plants` tanpa login. | Ditolak dengan status HTTP 403 / Firestore Permission Denied. | `UNVERIFIED` (Siap Diuji) |
| **SEC-02** | Public User Write Denied | Akun publik non-admin mencoba mutasi data lahan/tanaman. | Ditolak oleh aturan keamanan `isAdmin()`. | `UNVERIFIED` (Siap Diuji) |
| **SEC-03** | Admin Write Permitted | Admin dengan role `ADMIN` melakukan operasi CRUD dari UI. | Diizinkan dan data tersimpan sempurna di Firestore. | `UNVERIFIED` (Siap Diuji) |

---

### F. Pengujian Tampilan Responsif (Responsive Matrix)

| Dimensi Perangkat | Orientasi | Area Pengujian | Kriteria Lulus | Status |
| :--- | :--- | :--- | :--- | :---: |
| **360dp** (Small Phone) | Portrait | Dashboard, Form Lahan, Form Tanaman | Tidak ada teks terpotong, tombol simpan terlihat jelas, thumbnail foto pas. | `UNVERIFIED` (Siap Diuji) |
| **393dp** (Standard Phone) | Portrait | Dashboard, Form Lahan, Form Tanaman | Layout proporsional, scroll lancar. | `UNVERIFIED` (Siap Diuji) |
| **412dp** (Large Phone) | Portrait | Dashboard, Form Lahan, Form Tanaman | Card rapi, tombol sentuh mudah diakses. | `UNVERIFIED` (Siap Diuji) |
| **Landscape** | Landscape | Form Lahan & Form Tanaman | Keyboard tidak menutupi tombol CTA, konten dapat di-scroll vertikal. | `UNVERIFIED` (Siap Diuji) |
