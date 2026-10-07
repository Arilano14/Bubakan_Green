# LAPORAN QUALITY CONTROL: LAHAN CRUD (KELOLA LAHAN)
**Produk:** Bubakan Green  
**Fase:** Phase Admin Remediation Final  
**Status Verifikasi:** **VERIFIED & PASSED** (Unit Test Suite + Code Review)  
**Dokumen Terkait:** [ADMIN_CRUD_TEST_MATRIX.md](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/docs/phase-admin/ADMIN_CRUD_TEST_MATRIX.md)

---

## 1. Ringkasan Perubahan Arsitektur
1. **Terminologi Resmi:**  
   - Seluruh teks antarmuka diubah secara konsisten dari "Kelola Lokasi" menjadi **"Kelola Lahan"**.
   - Judul form CRUD disesuaikan menjadi "Tambah Lahan" dan "Edit Lahan".
2. **Peniadaan Antrean Persetujuan (Zero Approval Queue):**  
   - Alur `PENDING_APPROVAL`, `LocationApprovalScreen`, dan `LocationApprovalViewModel` telah dipensiunkan sepenuhnya.
   - Admin Kelurahan bertindak sebagai pembuat langsung (*author*). Lahan yang disimpan langsung berstatus `LocationStatus.PUBLISHED` dan `CoordinatesStatus.VERIFIED`.
3. **Restriksi Tag Wilayah (Canonical Region Tags):**  
   - Tag wilayah dibatasi secara ketat ke 6 nilai kanonikal:
     - `Kelurahan`
     - `RW 01`
     - `RW 02`
     - `RW 03`
     - `RW 04`
     - `RW 05`
   - Pemilihan dilakukan melalui segmented chip UI yang ergonomis (`FilterChip`).
4. **Validasi Geospasial & Koordinat:**  
   - Kuncian GPS wajib (`latitude` dan `longitude` non-null) sebelum form dapat disimpan.
   - Koordinat divalidasi menggunakan algoritma ray-casting Jordan Curve Polygon `BubakanGeoValidator` sesuai batas resmi Kelurahan Bubakan (Mijen, Semarang).
5. **Deskripsi Opsional:**  
   - Field deskripsi lahan bersifat opsional, tidak lagi memblokir penyimpanan jika kosong.

---

## 2. Bukti Empiris Pengujian Kode (Automated Test Evidence)

```
Suite: id.bubakangreen.app.ui.LocationFormViewModelTest
Tests Run: 7
Failures: 0
Errors: 0
Status: PASSED
```

### Rincian Kasus Uji
| Test Method | Skenario | Hasil |
| :--- | :--- | :---: |
| `saveLocation_withShortName_setsValidationError` | Nama < 3 karakter ditolak dengan pesan validasi | PASSED |
| `saveLocation_withoutGps_setsValidationError` | Form tanpa kuncian GPS ditolak sebelum simpan | PASSED |
| `captureGps_withHighAccuracy_setsCoordinatesAndNoWarning` | Akurasi < 25m diterima tanpa peringatan | PASSED |
| `captureGps_withLowAccuracy_setsWarningPromptingRetry` | Akurasi > 25m memunculkan peringatan kuning | PASSED |
| `saveLocation_withCoordinatesOutsideBubakan_setsGeospatialValidationError` | Koordinat luar batas Bubakan ditolak ketat | PASSED |
| `saveLocation_success_createsPublishedLocationAndAuditLog` | Lahan tersimpan langsung `PUBLISHED` & `ADMIN` audit log tercatat | PASSED |
| `photoManagement_enforcesMaxThreePhotos_andSupportsReplaceAndRemove` | Batas foto 0..3, ganti, dan hapus berjalan presisi | PASSED |

---

## 3. Kompatibilitas Data Firestore
- Field baru `photos` disimpan sebagai `List<String>`.
- Field `regionTag` disimpan sebagai kode kanonikal (`KELURAHAN`, `RW_01`, ..., `RW_05`).
- Kompatibilitas mundur (*backward compatibility*):
  - Field `rw` tetap diserialisasi untuk mendukung query legacy.
  - Field `coverPhotoUrl` dan `photoUrl` tetap diisi dari thumbnail pertama (`photos.firstOrNull()`).
