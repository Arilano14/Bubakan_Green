# LAPORAN QUALITY CONTROL: PLANT CRUD (RESTRUKTURISASI ENSIKLOPEDIA)
**Produk:** Bubakan Green  
**Fase:** Phase Admin Remediation Final  
**Status Verifikasi:** **VERIFIED & PASSED** (Unit Test Suite + Code Review)  
**Dokumen Terkait:** [ADMIN_CRUD_TEST_MATRIX.md](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/docs/phase-admin/ADMIN_CRUD_TEST_MATRIX.md)

---

## 1. Ringkasan Perubahan Arsitektur
1. **Restrukturisasi Field Wajib vs Opsional:**
   - **WAJIB (\*):**
     - Nama Tanaman (Bahasa Indonesia) `nameId`
     - Foto Tanaman `primaryPhotoUrl`
   - **OPSIONAL:**
     - Nama Ilmiah (Latin) `nameLatin`
     - Aksara Mandarin (Hanzi) & Pinyin `nameMandarin`, `pinyin`
     - Deskripsi Tanaman `description`
     - Karakteristik Morfologi `characteristics`
     - Manfaat & Penggunaan `commonUses`
     - Panduan Budidaya & Perawatan `cultivationNotes`
2. **Pembersihan 4 Field Metadata Legacy:**
   - Field teks legacy berikut telah dihapus dari antarmuka pengguna:
     1. "URL Foto Referensi" (digantikan oleh pemilih foto modern yang mendukung Galeri, Kamera, dan URL/Aset).
     2. "Fotografer / Pembuat" (`imageAuthor`).
     3. "Lisensi Foto" (`imageLicense`).
     4. "Referensi Taksonomi Ilmiah" (`sourceReferences`).
3. **Preservasi ID Tanaman Stabil (Stable Plant ID Immutability):**
   - Pada saat mode edit tanaman, `savePlant` mempertahankan `current.plantId` (`plant.copy(id = current.plantId)`).
   - Menjamin bahwa URL QR fisik (`https://bubakan-green.web.app/plant/{id}`) tidak pernah berubah atau rusak ketika nama/foto tanaman diperbarui oleh Admin.
4. **Pencegahan Duplikasi Spesies Botani:**
   - Jika membuat tanaman baru (`plantId == null`), divalidasi agar tidak ada nama Latin atau nama Indonesia yang bentrok dengan spesies yang sudah ada di master database.

---

## 2. Bukti Empiris Pengujian Kode (Automated Test Evidence)

```
Suite: id.bubakangreen.app.ui.MasterPlantViewModelTest
Tests Run: 8
Failures: 0
Errors: 0
Status: PASSED
```

### Rincian Kasus Uji
| Test Method | Skenario | Hasil |
| :--- | :--- | :---: |
| `savePlant with duplicate Latin name is rejected with botanical duplicate error` | Nama Latin duplikat ditolak dengan rujukan spesies terdaftar | PASSED |
| `savePlant with duplicate Indonesian common name is rejected` | Nama Indonesia duplikat ditolak | PASSED |
| `savePlant without photo is rejected with validation error` | Foto kosong ditolak ("Foto tanaman wajib disertakan") | PASSED |
| `savePlant with only required fields (nameId and photoUrl) succeeds` | Pengisian hanya field wajib berhasil menyimpan tanpa error | PASSED |
| `updatePlant preserves stable plantId` | Update nama tanaman mempertahankan ID tanaman yang sama persis | PASSED |
| `savePlant with insecure HTTP URL is rejected` | Skema `http://` ditolak demi keamanan HTTPS | PASSED |
| `savePlant with valid HTTPS remote URL saves with REMOTE_URL source type` | Remote URL aman tersimpan sebagai `REMOTE_URL` | PASSED |
| `savePlant with local asset name saves with LOCAL source type` | Nama aset lokal tersimpan sebagai `LOCAL` | PASSED |

---

## 3. Verifikasi Integritas Detail Tanaman
- Tanaman yang disimpan dengan field opsional kosong tetap tampil elegan di `PlantDetailScreen.kt` publik tanpa NullPointerException atau layout glitch (fallback ke default text atau menyembunyikan section kosong).
