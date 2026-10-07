# KESIAPAN AKHIR REMEDIASI ADMIN BUBALAN GREEN
**Produk:** Bubakan Green  
**Fase:** Admin Remediation  
**Status Kesiapan:** **SIAP IMPLEMENTASI — MENUNGGU SINYAL PERSETUJUAN AKHIR**  
**Sinyal Persetujuan:** `ACC PHASE ADMIN REMEDIATION FINAL`

---

## 1. Ringkasan Kesiapan Fase (Phase Readiness Checklist)

| No | Modul / Komponen | Rencana Aksi | Status Kesiapan | Berkas Terdampak Utama |
| :---: | :--- | :--- | :---: | :--- |
| **1** | **Location QR Removal** | Hapus aksi `onQrClick` dan modal QR di TopBar profil kebun publik. | **SIAP** | [`LocationDetailScreen.kt`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/app/src/main/java/id/bubakangreen/app/ui/locations/LocationDetailScreen.kt) |
| **2** | **Plant QR Preservation** | Pastikan QR tanaman tetap aktif dan domain kanonikal tetap `bubakan-green.web.app`. | **SIAP** | [`PlantDetailScreen.kt`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/app/src/main/java/id/bubakangreen/app/ui/catalog/PlantDetailScreen.kt), [`QrUrlBuilder.kt`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/app/src/main/java/id/bubakangreen/app/core/util/QrUrlBuilder.kt) |
| **3** | **Approval Workflow Removal** | Pensiunkan `LocationApprovalScreen`, hapus antrean approval di Dashboard, status kebun langsung `PUBLISHED`/`ACTIVE`. | **SIAP** | [`AdminDashboardScreen.kt`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/app/src/main/java/id/bubakangreen/app/ui/admin/AdminDashboardScreen.kt), [`LocationApprovalScreen.kt`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/app/src/main/java/id/bubakangreen/app/ui/admin/LocationApprovalScreen.kt) |
| **4** | **Terminology Standardization** | Standardisasi istilah: "Kelola Lokasi" -> "Kelola Lahan" di seluruh antarmuka admin. | **SIAP** | [`AdminDashboardScreen.kt`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/app/src/main/java/id/bubakangreen/app/ui/admin/AdminDashboardScreen.kt), [`LocationFormScreen.kt`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/app/src/main/java/id/bubakangreen/app/ui/pic/LocationFormScreen.kt) |
| **5** | **Canonical Region Tags** | Batasi pilihan tag wilayah hanya 6 nilai kanonikal: `Kelurahan`, `RW 01` - `RW 05`. | **SIAP** | [`LocationFormScreen.kt`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/app/src/main/java/id/bubakangreen/app/ui/pic/LocationFormScreen.kt), [`LocationFormViewModel.kt`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/app/src/main/java/id/bubakangreen/app/ui/pic/LocationFormViewModel.kt) |
| **6** | **Optional Description** | Jadikan deskripsi lahan opsional di form UI dan validasi ViewModel. | **SIAP** | [`LocationFormViewModel.kt`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/app/src/main/java/id/bubakangreen/app/ui/pic/LocationFormViewModel.kt), [`Location.kt`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/app/src/main/java/id/bubakangreen/app/domain/model/Location.kt) |
| **7** | **Lahan Photo Management** | Tambahkan komponen foto 0/3 (thumbnail grid, preview, ganti, hapus, proteksi batas 3 foto). | **SIAP** | [`LocationFormScreen.kt`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/app/src/main/java/id/bubakangreen/app/ui/pic/LocationFormScreen.kt), [`ImageCompressor.kt`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/app/src/main/java/id/bubakangreen/app/core/util/ImageCompressor.kt) |
| **8** | **Camera & Gallery Contextual** | Izin kamera diminta hanya saat tombol "Ambil Foto" diklik. Fallback galeri via Photo Picker. | **SIAP** | [`LocationFormScreen.kt`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/app/src/main/java/id/bubakangreen/app/ui/pic/LocationFormScreen.kt), [`AndroidManifest.xml`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/app/src/main/AndroidManifest.xml) |
| **9** | **Photo Storage Guardrail** | Dilarang simpan Base64 di Firestore. Evaluasi Blaze plan dilaporkan secara transparan. | **SIAP** | [`PHOTO_STORAGE_DECISION.md`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/docs/phase-admin/PHOTO_STORAGE_DECISION.md) |
| **10** | **Plant CRUD Restructure** | Hanya Nama Indonesia (*) dan Foto (*) yang wajib. Hapus 4 input metadata lama dari form UI. | **SIAP** | [`MasterPlantFormScreen.kt`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/app/src/main/java/id/bubakangreen/app/ui/admin/MasterPlantFormScreen.kt), [`MasterPlantViewModel.kt`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/app/src/main/java/id/bubakangreen/app/ui/admin/MasterPlantViewModel.kt) |
| **11** | **Dashboard Sync Realtime** | Ganti `.firstOrNull()` menjadi continuous reactive flow via `combine()` listener snapshot Firestore. | **SIAP** | [`AdminDashboardViewModel.kt`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/app/src/main/java/id/bubakangreen/app/ui/admin/AdminDashboardViewModel.kt) |
| **12** | **Firestore Security Rules** | Pastikan aturan keamanan memvalidasi bahwa hanya Admin yang dapat menulis data. | **SIAP** | [`firestore.rules`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/firestore.rules) |

---

## 2. Urutan Gerbang Eksekusi Implementasi (Implementation Sequence)

Setelah menerima sinyal persetujuan:
1. **Langkah 1:** Hapus QR lokasi pada [`LocationDetailScreen.kt`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/app/src/main/java/id/bubakangreen/app/ui/locations/LocationDetailScreen.kt).
2. **Langkah 2:** Hapus alur approval di [`AdminDashboardScreen.kt`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/app/src/main/java/id/bubakangreen/app/ui/admin/AdminDashboardScreen.kt) dan pensiunkan navigasi approval di [`BubakanNavHost.kt`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/app/src/main/java/id/bubakangreen/app/navigation/BubakanNavHost.kt).
3. **Langkah 3:** Standardisasi terminologi "Kelola Lahan" di seluruh Admin UI.
4. **Langkah 4:** Perbarui model data [`Location.kt`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/app/src/main/java/id/bubakangreen/app/domain/model/Location.kt) dan repository untuk mendukung `photos: List<String>`, `regionTag`, dan deskripsi opsional.
5. **Langkah 5:** Implementasikan Form Tambah/Edit Lahan dengan 6 tag kanonikal dan komponen foto 0/3.
6. **Langkah 6:** Hubungkan kompresi gambar [`ImageCompressor.kt`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/app/src/main/java/id/bubakangreen/app/core/util/ImageCompressor.kt) dengan pemilih kamera dan galeri (Photo Picker).
7. **Langkah 7:** Perbarui Form Tanaman [`MasterPlantFormScreen.kt`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/app/src/main/java/id/bubakangreen/app/ui/admin/MasterPlantFormScreen.kt) & ViewModel: hapus 4 field lama, wajibkan Nama Indonesia & Foto.
8. **Langkah 8:** Perbaiki [`AdminDashboardViewModel.kt`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/app/src/main/java/id/bubakangreen/app/ui/admin/AdminDashboardViewModel.kt) dengan listener `combine()` realtime berkelanjutan.
9. **Langkah 9:** Jalankan rangkaian pengujian unit test dan verifikasi build `./gradlew testDebugUnitTest`.
10. **Langkah 10:** Catat bukti empiris pengujian frontend CRUD.
