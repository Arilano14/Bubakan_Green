# Bubakan Green — Local Land Data & Image Mode Quality Control Report

**Document ID:** `docs/phase-1/LOCAL_LAND_DATA_QC.md`  
**Phase:** PHASE 1 — LAND DATA ENRICHMENT & DECOUPLED LOCAL IMAGE MODE  
**Target Module:** `LocalLahanImageRegistry.kt`, `DefaultBotanicalData.kt`, `LocationImage.kt`, `preview.html`, Firestore Collection `locations` & `location_plants`  
**Timestamp:** 2026-10-11  
**Status:** `VERIFIED & IMPLEMENTED` (Build & Tests Passing: `PASS`)  
**Deployment Policy Compliance:** `STRICT NO-RELEASE / NO-PRODUCTION-DEPLOYMENT`  

---

## 1. Executive Summary

Laporan Quality Control (QC) ini mendokumentasikan pembaruan dan verifikasi data 4 (empat) lahan pertanian aktual di Kelurahan Bubakan, Mijen, Semarang pada aplikasi Bubakan Green, bersama implementasi arsitektur **Decoupled Local Image Mode**.

Karena layanan Firebase Storage untuk proyek belum dikonfigurasi / belum berada pada Blaze plan berbayar, seluruh 12 foto lahan aktual diintegrasikan menggunakan mode lokal terpisah yang aman:
- **Di Firestore:** Disimpan sebagai URL relatif portable `/assets/Lahan/<filename>.jpeg` (tanpa path absolut lokal Windows).
- **Di Android APK:** Dikonversi menjadi WebP lossy beresolusi optimal di `app/src/main/res/drawable-nodpi/` dan dipetakan melalui komponen `LocalLahanImageRegistry.kt`.
- **Di Web Preview / Portal:** Ditautkan langsung ke `web/public/assets/Lahan/` dengan penanganan URL encoding (`%20`).
- **Integritas Cloud:** Konfigurasi Firebase, Firestore rules, Storage rules, Authentication, dan Firestore collections tetap utuh 100% tanpa ada data yang dihapus atau dirusak.

---

## 2. Inventaris & Identitas 4 Lahan Aktual

Keempat lahan aktual telah berhasil diperbarui dan diverifikasi secara read-back langsung pada Firestore:

| No | Nama Lahan | Document ID | Wilayah | Tipe Lahan | Koordinat GPS (Decimal) | Status Koordinat | Jumlah Foto | Jumlah Tanaman |
|:---|:---|:---|:---|:---|:---|:---|:---:|:---:|
| 1 | **Urban Farming dan Taman Toga RW 1** | `loc_urban_farming_toga_rw01` | RW 01 | `URBAN_FARMING` | Lat: `-7.092056`<br>Lng: `110.319528` | `VERIFIED` | 3 | 25 |
| 2 | **Taman Toga RW 2** | `loc_taman_toga_rw02` | RW 02 | `TAMAN_TOGA` | Lat: `-7.097694`<br>Lng: `110.320000` | `VERIFIED` | 3 | 0 *(KOSONG)* |
| 3 | **Taman Toga RW 3** | `loc_taman_toga_rw03` | RW 03 | `TAMAN_TOGA` | Lat: `-7.091944`<br>Lng: `110.325667` | `VERIFIED` | 3 | 7 |
| 4 | **Urban Farming RW 3** | `loc_urban_farming_rw03` | RW 03 | `URBAN_FARMING` | Lat: `-7.092000`<br>Lng: `110.324806` | `VERIFIED` | 3 | 2 |

### Catatan Khusus Inventaris Lahan:
- **RW 2 Tetap Kosong:** Sesuai instruksi audit lapangan, `loc_taman_toga_rw02` memiliki 0 relasi tanaman di `location_plants`. Tidak ada data dummy yang diinjeksikan.
- **RW 3 Toga Anti-Duplikasi:** Tanaman bawaan (`sereh`, `jahe`, `kencur`, `kunyit`, `lidah_buaya`) dipertahankan; penambahan tanaman lapangan baru (`ciplukan`, `kitolot`) dilakukan tanpa menduplikasi dokumen `lidah_buaya`.
- **RW 1 Inventaris Lengkap:** Memiliki 25 tanaman terverifikasi, dengan pemisahan tegas antara cabai dan tomat sebagai spesies terpisah.

---

## 3. Pemetaan 12 Foto Lahan (Decoupled Local Image Bridge)

Seluruh 12 berkas foto lapangan asli dari folder repository `web/public/assets/Lahan/` dipetakan ke aset lokal Android drawable dan tag registri visual:

| No | File Asli (`web/public/assets/Lahan/`) | Relative URL (Firestore & Web) | Android Drawable Tag | Resource ID | Deskripsi Tag Visual | Lahan Sasaran |
|:---:|:---|:---|:---|:---|:---|:---|
| 1 | `RW 1_1.jpeg` | `/assets/Lahan/RW%201_1.jpeg` | `lahan_rw1_1` | `R.drawable.lahan_rw1_1` | Gerbang & Pintu Masuk RW 01 | `loc_urban_farming_toga_rw01` |
| 2 | `RW 1_2.jpeg` | `/assets/Lahan/RW%201_2.jpeg` | `lahan_rw1_2` | `R.drawable.lahan_rw1_2` | Bedeng Pembibitan & Sayur RW 01 | `loc_urban_farming_toga_rw01` |
| 3 | `RW 1_3.jpeg` | `/assets/Lahan/RW%201_3.jpeg` | `lahan_rw1_3` | `R.drawable.lahan_rw1_3` | Kebun TOGA & Konservasi Hijau RW 01 | `loc_urban_farming_toga_rw01` |
| 4 | `RW 2_Toga.jpeg` | `/assets/Lahan/RW%202_Toga.jpeg` | `lahan_rw2_toga_1` | `R.drawable.lahan_rw2_toga_1` | Taman Toga Utama RW 02 | `loc_taman_toga_rw02` |
| 5 | `RW 2_Toga 1.jpeg` | `/assets/Lahan/RW%202_Toga%201.jpeg` | `lahan_rw2_toga_2` | `R.drawable.lahan_rw2_toga_2` | Koleksi Herbal & Rimpang RW 02 | `loc_taman_toga_rw02` |
| 6 | `RW 2_Toga 2.jpeg` | `/assets/Lahan/RW%202_Toga%202.jpeg` | `lahan_rw2_toga_3` | `R.drawable.lahan_rw2_toga_3` | Penataan Pot & Konservasi RW 02 | `loc_taman_toga_rw02` |
| 7 | `RW 3_Toga.jpeg` | `/assets/Lahan/RW%203_Toga.jpeg` | `lahan_rw3_toga_1` | `R.drawable.lahan_rw3_toga_1` | Taman TOGA Kolaborasi KKN RW 03 | `loc_taman_toga_rw03` |
| 8 | `RW 3_Toga 2.jpeg` | `/assets/Lahan/RW%203_Toga%202.jpeg` | `lahan_rw3_toga_2` | `R.drawable.lahan_rw3_toga_2` | Bedeng TOGA Percontohan RW 03 | `loc_taman_toga_rw03` |
| 9 | `RW 3_Toga 3.jpeg` | `/assets/Lahan/RW%203_Toga%203.jpeg` | `lahan_rw3_toga_3` | `R.drawable.lahan_rw3_toga_3` | Area Konservasi Herbal RW 03 | `loc_taman_toga_rw03` |
| 10 | `RW 3_Urban.jpeg` | `/assets/Lahan/RW%203_Urban.jpeg` | `lahan_rw3_urban_1` | `R.drawable.lahan_rw3_urban_1` | Urban Farming Utama RW 03 | `loc_urban_farming_rw03` |
| 11 | `RW 3_Urban 2.jpeg` | `/assets/Lahan/RW%203_Urban%202.jpeg` | `lahan_rw3_urban_2` | `R.drawable.lahan_rw3_urban_2` | Jalur Tanam Pangan RW 03 | `loc_urban_farming_rw03` |
| 12 | `RW 3_Urban 3.jpeg` | `/assets/Lahan/RW%203_Urban%203.jpeg` | `lahan_rw3_urban_3` | `R.drawable.lahan_rw3_urban_3` | Area Pohon Buah & Durian RW 03 | `loc_urban_farming_rw03` |

---

## 4. Metrik Inventaris Tanaman & Relasi Firestore

### 4.1 Master Plants (`/master_plants`)
- **Total MasterPlants di Firestore:** 32 spesies unik.
- **Kategori Spesies:**
  - *Sayuran & Pangan:* Bayam, Caisim, Pakcoy, Daun Bawang, Daun Sintrong, Kacang Panjang, Kangkung, Kucai, Seledri, Talas, Terong, Tomat, Cabai, Pepaya, Durian.
  - *Tanaman Herbal / TOGA:* Bunga Bugang, Bunga Kenanga, Cepokak, Ciplukan, Jahe, Kemangi, Kencur, Kitolot, Kumis Kucing, Kunyit, Lengkuas, Lidah Buaya, Mahoni, Pandan, Pegagan, Sereh, Sirih.
- **Pencegahan Redundansi:** Tidak ada duplikasi ID spesies (misal `sereh` digunakan bersama untuk bumbu dan toga, tidak ada `sereh_dapur` terpisah).

### 4.2 Relasi Location-Plant (`/location_plants`)
1. **`loc_urban_farming_toga_rw01` (25 tanaman):**
   `bayam`, `bunga_bugang`, `bunga_kenanga`, `cabai`, `caisim`, `cepokak`, `daun_bawang`, `daun_sintrong`, `jahe`, `kacang_panjang`, `kemangi`, `kucai`, `kumis_kucing`, `kunyit`, `lengkuas`, `mahoni`, `pakcoy`, `pandan`, `pegagan`, `pepaya`, `seledri`, `sereh`, `talas`, `terong`, `tomat`.
2. **`loc_taman_toga_rw02` (0 tanaman):**
   Status kosong terverifikasi (menunggu input survei lapangan resmi warga RW 02).
3. **`loc_taman_toga_rw03` (7 tanaman):**
   `sereh`, `jahe`, `kencur`, `kunyit`, `lidah_buaya`, `ciplukan`, `kitolot`.
4. **`loc_urban_farming_rw03` (2 tanaman):**
   `durian`, `jahe`.

---

## 5. Arsitektur Decoupled Local Image Bridge

```
┌─────────────────────────────────────────────────────────────┐
│                    FIRESTORE DATABASE                       │
│    Document fields: photoUrl, coverPhotoUrl, photos[]       │
│    Format: "/assets/Lahan/RW 1_1.jpeg" (Portable Relative)   │
└──────────────┬──────────────────────────────┬───────────────┘
               │                              │
               ▼ (Web / Preview)              ▼ (Android APK)
┌──────────────────────────────┐ ┌──────────────────────────────┐
│        WEB CLIENT            │ │      ANDROID CLIENT          │
│ - Relative URL HTTP resolver │ │ - LocalLahanImageRegistry    │
│ - URL encoding (%20) handler │ │ - Tag & filename matcher     │
│ - Fallback mascot/placeholder│ │ - R.drawable.lahan_* WebP    │
│                              │ │ - Remote HTTPS bypass        │
└──────────────────────────────┘ └──────────────────────────────┘
               │                              │
               ▼                              ▼
  Siap switch ke Firebase Storage saat Blaze Plan diaktifkan kelak
```

### Mekanisme Kerja:
1. **Pemisahan Tanggung Jawab:** Firestore hanya menyimpan string path relatif generik `/assets/Lahan/...`, tanpa mengetahui apakah klien yang membuka adalah aplikasi Android offline, web lokal, atau web hosting.
2. **Client-Side Resolution di Android:** `LocalLahanImageRegistry.kt` membaca path tersebut, memotong prefix, melakukan decode URL, lalu mencocokkan nama file ke `R.drawable.lahan_*`.
3. **Pemuatan Hybrid di Compose:** `LocationImage.kt` mendeteksi tipe data:
   - Jika `Int` (Drawable Resource ID) -> dimuat instan tanpa kuota internet melalui `painterResource(id)`.
   - Jika `String` berawalan `http://` / `https://` -> dimuat melalui Coil AsyncImage.
   - Jika `null` / kosong -> menampilkan visual placeholder bertema dedaunan hijau.
4. **Zero Lock-in:** Ketika admin nantinya mengunggah foto ke Firebase Storage dan field di Firestore berubah menjadi `https://firebasestorage.googleapis.com/...`, aplikasi Android dan Web akan langsung otomatis merender foto dari cloud tanpa perlu modifikasi kode ataupun pembaruan schema.

---

## 6. Hasil Verifikasi & Uji Mutu (QC Matrix)

| Komponen Uji | Kriteria Pengujian | Hasil | Catatan |
|:---|:---|:---:|:---|
| **Firestore Lahan Schema** | 4 dokumen lahan memiliki koordinat, RW, dan photos array | `PASS` | Read-back REST API memvalidasi 4/4 dokumen. |
| **Firestore Tanaman RW 1** | 25 tanaman terhubung dengan `locationId` yang valid | `PASS` | Cabai & tomat terpisah. |
| **Firestore Tanaman RW 2** | 0 tanaman terhubung (strict empty) | `PASS` | Dokumen query mengembalikan 0 records. |
| **Firestore Tanaman RW 3 Toga** | 7 tanaman terhubung tanpa duplikat lidah buaya | `PASS` | 5 bawaan + 2 baru (ciplukan, kitolot). |
| **Firestore Tanaman RW 3 Urban** | 2 tanaman terhubung (durian, jahe) | `PASS` | Terdaftar di `location_plants`. |
| **Aset WebP Drawables** | 12 berkas WebP tersedia di `res/drawable-nodpi/` | `PASS` | Ukuran kompak, tidak ada missing asset. |
| **Registry Resolution** | `LocalLahanImageRegistry.resolvePhotosForLocation()` | `PASS` | Mampu me-resolve path ber-spasi maupun `%20`. |
| **Android Unit Tests** | `./gradlew testDebugUnitTest` | `PASS` | 23 tasks: BUILD SUCCESSFUL in 49s. |
| **Android Assemble Build** | `./gradlew assembleDebug` | `PASS` | Kompilasi Kotlin & Dexing sukses tanpa error. |
| **Emulator UI Rendering** | Tampilan kartu lahan dan galeri foto di emulator | `PASS` | Foto lahan RW 1, RW 2, dan RW 3 muncul jelas. |
| **Web Preview Compatibility**| `web/public/preview.html` rendering foto & fallback | `PASS` | 4 kartu lahan teruji dengan badge dan koordinat. |
| **Firebase Configuration** | File `google-services.json`, rules, dan Auth tidak rusak | `PASS` | Tidak ada perubahan destruktif. |

---

## 7. Audit Dampak Berkas & Lingkungan (Impact Assessment)

### 7.1 Berkas yang Diperbarui:
- `app/src/main/java/id/bubakangreen/app/data/local/LocalLahanImageRegistry.kt`
  *(Menambahkan enum `LandImageSourceType`, 12 tag foto lahan, URL encoder/decoder, dan resolver wilayah)*
- `app/src/main/java/id/bubakangreen/app/data/fixture/DefaultBotanicalData.kt`
  *(Menambahkan 4 data lokasi kanonikal dengan koordinat presisi dan daftar foto)*
- `app/src/main/res/drawable-nodpi/`
  *(Menambahkan 9 aset WebP baru untuk RW 2 Toga, RW 3 Toga, dan RW 3 Urban)*
- `web/public/preview.html`
  *(Memperbarui objek state locations dengan 4 lahan aktual, koordinat desimal, dan foto preview)*
- `scripts/update_four_lahan.py`
  *(Skrip pembaruan Firestore yang bersifat idempoten dan terverifikasi)*

### 7.2 Dampak pada Firebase & Infrastruktur:
- **Firestore Database:** AMAN (Hanya melakukan penambahan dokumen baru dan pembaruan field dokumen lahan/relasi tanpa merusak dokumen yang sudah ada).
- **Firebase Authentication:** AMAN (Akun admin `admin@bubakangreen.id` tetap aktif dan terverifikasi).
- **Firebase Storage:** TIDAK DISENTUH (Tidak ada file yang diunggah ke cloud storage karena belum Blaze plan; tidak ada tagihan cloud).
- **Security Rules (`firestore.rules` & `storage.rules`):** AMAN (Tidak mengalami perubahan).
- **Hosting / Domain:** AMAN (Tidak dilakukan deployment release).
- **Rilis APK:** TIDAK DILAKUKAN (Hanya build dan uji debug lokal).

---

## 8. Kesimpulan & Rekomendasi

Pekerjaan pengisian data lahan, penataan master tanaman, relasi lahan-tanaman, serta implementasi **Decoupled Local Image Mode** telah diselesaikan secara tuntas dengan status **VERIFIED & IMPLEMENTED**.

### Tindak Lanjut untuk Tim Lapangan:
1. Ketika survei tanaman untuk Taman Toga RW 2 telah selesai dilaksanakan, data tanaman dapat diinputkan ke `/location_plants` menggunakan skrip serupa tanpa mengganggu struktur yang sudah berjalan.
2. Jika akun Firebase telah di-upgrade ke Blaze Plan, foto dari `web/public/assets/Lahan/` dapat diunggah ke Firebase Storage, dan URL-nya cukup di-update di field `photos` dokumen `locations` di Firestore tanpa perlu merilis ulang kode aplikasi.
