# LAPORAN QUALITY CONTROL: QR SCOPE CORRECTION & PUBLIC INTEGRITY
**Produk:** Bubakan Green  
**Fase:** Phase Admin Remediation Final  
**Status Verifikasi:** **VERIFIED & PASSED** (Unit Test Suite + Code Review)  
**Dokumen Terkait:** [ADMIN_REDESIGN_AUDIT.md](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/docs/phase-admin/ADMIN_REDESIGN_AUDIT.md)

---

## 1. Lingkup QR Baru
Berdasarkan arahan produk Bubakan Green:
- **Lahan / Lokasi:** **TIDAK MEMILIKI QR**.
  - Seluruh tombol QR di TopBar `LocationDetailScreen.kt` telah dihapus.
  - Tidak ada dialog QR code, tombol unduh QR lokasi, atau generator QR lokasi di antarmuka publik maupun admin.
- **Tanaman (Plant):** **100% MEMILIKI QR & TETAP UTUH**.
  - Tombol QR di TopBar `PlantDetailScreen.kt` dipertahankan sepenuhnya.
  - Dialog `QrCodeDisplayDialog` berfungsi untuk menampilkan QR tanaman resolusi tinggi dengan opsi unduh/simpan ke galeri.
  - Rute deep link web canonical tetap mengarah ke:  
    `https://bubakan-green.web.app/plant/{plantId}`.

---

## 2. Immutabilitas & Stabilitas ID Tanaman (ID Stability)
- Pada saat Admin memperbarui nama Indonesia, foto, deskripsi, atau nama Latin tanaman, ID tanaman (`plantId`) **tidak pernah di-regenerasi**.
- Nilai URL QR fisik yang telah dicetak di plakat kebun warga tetap valid secara permanen tanpa perlu cetak ulang.
- Hal ini diverifikasi dalam suite pengujian `QrUrlBuilderTest` dan `MasterPlantViewModelTest`.

---

## 3. Bukti Empiris Pengujian Kode (Automated Test Evidence)

```
Suite: id.bubakangreen.app.core.util.QrUrlBuilderTest
Tests Run: 13
Failures: 0
Errors: 0
Status: PASSED

Suite: id.bubakangreen.app.core.util.QrCodeGeneratorTest
Tests Run: 9
Failures: 0
Errors: 0
Status: PASSED
```

### Rincian Kasus Uji Kunci
| Test Method | Skenario | Hasil |
| :--- | :--- | :---: |
| `buildPlantUrl_producesDeterministicContractUrl` | URL tanaman deterministik `https://bubakan-green.web.app/plant/{id}` | PASSED |
| `parseCanonicalUrl_validPlantUrl_returnsPlant` | Parsing deep link web tanaman mengembalikan `ParsedQrResult.Plant` | PASSED |
| `stableId_immutabilityAcrossAttributeModifications` | ID stabil tidak berubah meskipun atribut metadata tanaman diperbarui | PASSED |
| `generateQrBitmap_validInput_returnsNonEmptyBitmap` | Generator QR menghasilkan bitmap tajam dengan rasio kontras tinggi | PASSED |
