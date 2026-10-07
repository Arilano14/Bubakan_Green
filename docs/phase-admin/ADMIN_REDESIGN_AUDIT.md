# AUDIT FORENSIK REMEDIASI ADMIN BUBALAN GREEN
**Produk:** Bubakan Green  
**Fase:** Admin Remediation (Lahan CRUD, Plant CRUD, Foto, QR Scope, Dashboard Realtime)  
**Prinsip Audit:** STRICT EVIDENCE-BASED (Pemisahan Tegas CURRENT STATE vs PROPOSED STATE)  
**Status:** **AUDIT LENGKAP — MENUNGGU PERSETUJUAN (`ACC PHASE ADMIN REMEDIATION FINAL`)**

---

## 1. Perbandingan Status: CURRENT vs PROPOSED

| Aspek / Fitur | CURRENT STATE (Kondisi Kode Saat Ini) | PROPOSED STATE (Kondisi Target Remediasi) | Bukti Berkas Kode Aktual |
| :--- | :--- | :--- | :--- |
| **QR Lokasi** | TopBar memiliki aksi `onQrClick` dan menampilkan dialog `QrCodeDisplayDialog` dengan URL `/location/{id}`. | **DIHAPUS TOTAL.** Tidak ada tombol QR, tidak ada dialog QR, tidak ada opsi salin/unduh QR lokasi. | [`LocationDetailScreen.kt:108-110, 364-374`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/app/src/main/java/id/bubakangreen/app/ui/locations/LocationDetailScreen.kt#L108-L110) |
| **QR Tanaman** | Berfungsi menampilkan QR tanaman via `QrUrlBuilder.buildPlantUrl()`. | **DIPERTAHANKAN 100%.** QR tanaman tetap utuh, deep link dan web fallback tetap aktif. | [`PlantDetailScreen.kt:525`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/app/src/main/java/id/bubakangreen/app/ui/catalog/PlantDetailScreen.kt#L525), [`QrUrlBuilder.kt:23`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/app/src/main/java/id/bubakangreen/app/core/util/QrUrlBuilder.kt#L23) |
| **Alur Approval Lahan** | Ada `LocationApprovalScreen`, `LocationApprovalViewModel`, antrean `PENDING_APPROVAL`, aksi Setuju/Tolak. Form kebun menyimpan `status = PENDING_APPROVAL`. | **DIHAPUS TOTAL.** Tidak ada antrean persetujuan. Admin membuat/mengubah lahan langsung berstatus `PUBLISHED`/`ACTIVE`. | [`LocationApprovalScreen.kt`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/app/src/main/java/id/bubakangreen/app/ui/admin/LocationApprovalScreen.kt), [`LocationFormViewModel.kt:176`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/app/src/main/java/id/bubakangreen/app/ui/pic/LocationFormViewModel.kt#L176) |
| **Hak Akses CRUD Lahan** | `firestore.rules` hanya mengizinkan `isAdmin()` untuk write ke `/locations/{locationId}`. Namun form masih dinamai PIC form dan mewajibkan `picUid`. | **DIKONSISTENKAN KE ADMIN.** Form berada di bawah domain Admin Kelurahan, langsung menulis via akun Admin yang diautentikasi. | [`firestore.rules:22-25`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/firestore.rules#L22-L25), [`LocationFormViewModel.kt:127`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/app/src/main/java/id/bubakangreen/app/ui/pic/LocationFormViewModel.kt#L127) |
| **Terminologi** | Menggunakan istilah campuran: "Kelola Lokasi", "Profil Kebun", "Daftar Kebun", "Pengajuan Kebun". | **KONSISTEN: "Kelola Lahan".** Digunakan pada judul layar, navigasi, tombol, empty state, dan tes. | [`AdminDashboardScreen.kt:383, 697`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/app/src/main/java/id/bubakangreen/app/ui/admin/AdminDashboardScreen.kt#L383), [`LocationFormScreen.kt:139`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/app/src/main/java/id/bubakangreen/app/ui/pic/LocationFormScreen.kt#L139) |
| **Kategori & Tag Wilayah** | Menggunakan filter chip RW bebas dari `01` sampai `08`. | **6 TAG KANONIKAL.** Hanya: `Kelurahan`, `RW 01`, `RW 02`, `RW 03`, `RW 04`, `RW 05`. Tidak boleh string bebas atau tag lain. | [`LocationFormScreen.kt:253`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/app/src/main/java/id/bubakangreen/app/ui/pic/LocationFormScreen.kt#L253) |
| **Deskripsi Lahan** | Divalidasi WAJIB tidak boleh kosong (`if (description.isBlank()) error`). | **OPSIONAL.** Boleh dikosongkan tanpa memicu error validasi. | [`LocationFormViewModel.kt:137`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/app/src/main/java/id/bubakangreen/app/ui/pic/LocationFormViewModel.kt#L137) |
| **Foto Lahan** | Belum ada input foto di form lahan. Model data hanya memiliki `coverPhotoUrl: String?`. | **MAKSIMAL 3 FOTO.** Komponen thumbnail grid (0/3 s/d 3/3) dengan preview, aksi ganti, hapus, dan proteksi batas 3 foto. | [`LocationFormScreen.kt`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/app/src/main/java/id/bubakangreen/app/ui/pic/LocationFormScreen.kt), [`Location.kt:40`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/app/src/main/java/id/bubakangreen/app/domain/model/Location.kt#L40) |
| **Field Plant CRUD** | `nameId`, `nameLatin`, dan `description` WAJIB. Menampilkan 4 field metadata lama (URL referensi, fotografer, lisensi, taksonomi). | **HANYA NAMA INDONESIA (*) & FOTO (*) WAJIB.** Semua field lain opsional. 4 field metadata lama dihapus dari UI. | [`MasterPlantViewModel.kt:106-117`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/app/src/main/java/id/bubakangreen/app/ui/admin/MasterPlantViewModel.kt#L106-L117), [`MasterPlantFormScreen.kt:330-422`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/app/src/main/java/id/bubakangreen/app/ui/admin/MasterPlantFormScreen.kt#L330-L422) |
| **ID Tanaman** | Membuat ID baru saat simpan. | **STABLE ID.** Saat update wajib mempertahankan ID yang ada, dilarang regenerate ID tanaman. | [`MasterPlantViewModel.kt:133`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/app/src/main/java/id/bubakangreen/app/ui/admin/MasterPlantViewModel.kt#L133) |
| **Sinkronisasi Dashboard** | Menggunakan `.firstOrNull()` satu kali tembak, menyebabkan data membeku/stale setelah Admin melakukan CRUD. | **REALTIME FLOW LISTENER.** Listener snapshot Firestore aktif terus selama ViewModel aktif via `combine()`. Refleksi instan tanpa reload. | [`AdminDashboardViewModel.kt:67, 74`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/app/src/main/java/id/bubakangreen/app/ui/admin/AdminDashboardViewModel.kt#L67) |
| **Penyimpanan Foto** | Proyek di Firebase Spark Plan (Gratis). Cloud Storage SDK tidak diinstal. Belum ada upload binary. | **AUDIT OPSI STORAGE LENGKAP.** Dilarang keras simpan Base64 di Firestore. Blaze Plan membutuhkan otorisasi eksplisit user. | [`app/build.gradle.kts:75`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/app/build.gradle.kts#L75), [`docs/data/DATA_IMPLEMENTATION_REVIEW.md:25`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/docs/data/DATA_IMPLEMENTATION_REVIEW.md#L25) |

---

## 2. Rincian Berkas yang Akan Diubah (Files to Modify)
1. [`app/src/main/java/id/bubakangreen/app/ui/locations/LocationDetailScreen.kt`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/app/src/main/java/id/bubakangreen/app/ui/locations/LocationDetailScreen.kt)
   - Hapus `onQrClick` di `BubakanTopBar`.
   - Hapus pemanggilan `QrCodeDisplayDialog` untuk lokasi.
2. [`app/src/main/java/id/bubakangreen/app/ui/admin/AdminDashboardScreen.kt`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/app/src/main/java/id/bubakangreen/app/ui/admin/AdminDashboardScreen.kt)
   - Ganti terminologi: "Kelola Lokasi" → "Kelola Lahan", "Daftar Kebun" → "Daftar Lahan".
   - Hapus kartu "Persetujuan" dan SummaryPill "Antrean".
   - Sinkronisasi data realtime StateFlow.
3. [`app/src/main/java/id/bubakangreen/app/ui/admin/AdminDashboardViewModel.kt`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/app/src/main/java/id/bubakangreen/app/ui/admin/AdminDashboardViewModel.kt)
   - Ganti pemanggilan `.firstOrNull()` menjadi `combine(locationRepository.getAllLocations(), plantRepository.getAllMasterPlants(), authRepository.currentUserSession)` yang berkelanjutan.
   - Hapus komputasi `pendingLocations`.
4. [`app/src/main/java/id/bubakangreen/app/ui/pic/LocationFormScreen.kt`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/app/src/main/java/id/bubakangreen/app/ui/pic/LocationFormScreen.kt)
   - Ganti judul: "Tambah Lahan" / "Edit Lahan".
   - Filter chip tag wilayah dibatasi hanya 6 pilihan.
   - Tanda bintang (*) hanya pada Nama Lahan, Jenis Lahan, dan Tag Wilayah. Deskripsi tanpa bintang.
   - Tambahkan komponen foto 0/3 dengan thumbnail grid, opsi kamera kontekstual & galeri (Photo Picker).
5. [`app/src/main/java/id/bubakangreen/app/ui/pic/LocationFormViewModel.kt`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/app/src/main/java/id/bubakangreen/app/ui/pic/LocationFormViewModel.kt)
   - Hilangkan validasi wajib pada deskripsi.
   - Kelola state daftar foto (`photos: List<String>`), batas maksimal 3.
   - Status penyimpanan langsung `PUBLISHED` atau `ACTIVE` (bukan `PENDING_APPROVAL`).
6. [`app/src/main/java/id/bubakangreen/app/ui/admin/MasterPlantFormScreen.kt`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/app/src/main/java/id/bubakangreen/app/ui/admin/MasterPlantFormScreen.kt)
   - Hapus input: URL Foto Referensi, Fotografer, Lisensi Foto, dan Referensi Taksonomi Ilmiah.
   - Wajibkan hanya Nama Indonesia (*) dan Foto (*). Field lain ditandai opsional.
7. [`app/src/main/java/id/bubakangreen/app/ui/admin/MasterPlantViewModel.kt`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/app/src/main/java/id/bubakangreen/app/ui/admin/MasterPlantViewModel.kt)
   - Validasi: `nameId` wajib, `primaryPhotoUrl` wajib. Field lain opsional.
   - Preservasi `plantId` lama pada operasi update.
8. [`app/src/main/java/id/bubakangreen/app/domain/model/Location.kt`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/app/src/main/java/id/bubakangreen/app/domain/model/Location.kt)
   - Tambahkan `photos: List<String> = emptyList()`.
   - Tambahkan `regionTag: String = ""`.
   - Ubah default `description: String = ""`.
9. [`app/src/main/java/id/bubakangreen/app/data/remote/FirestoreLocationRepository.kt`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/app/src/main/java/id/bubakangreen/app/data/remote/FirestoreLocationRepository.kt)
   - Dukung serialisasi dan deserialisasi `photos` dan `regionTag`.
   - Jaga kompatibilitas mundur membaca dokumen legacy (`coverPhotoUrl`, `rw`).
10. [`app/src/main/java/id/bubakangreen/app/navigation/BubakanNavHost.kt`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/app/src/main/java/id/bubakangreen/app/navigation/BubakanNavHost.kt)
    - Hapus composable dan aksi rute `Screen.LocationApproval`.

---

## 3. Rincian Berkas yang Dipensiunkan (Files to Retire)
1. [`app/src/main/java/id/bubakangreen/app/ui/admin/LocationApprovalScreen.kt`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/app/src/main/java/id/bubakangreen/app/ui/admin/LocationApprovalScreen.kt)
   - Dihapus dari hierarki navigasi aktif karena seluruh alur approval resmi ditiadakan.
2. [`app/src/main/java/id/bubakangreen/app/ui/admin/LocationApprovalViewModel.kt`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/app/src/main/java/id/bubakangreen/app/ui/admin/LocationApprovalViewModel.kt)
   - Dipensiunkan.
3. [`app/src/test/java/id/bubakangreen/app/ui/LocationApprovalViewModelTest.kt`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/app/src/test/java/id/bubakangreen/app/ui/LocationApprovalViewModelTest.kt)
   - Digantikan oleh test suite baru untuk Admin Lahan CRUD dan Dashboard Sync.
