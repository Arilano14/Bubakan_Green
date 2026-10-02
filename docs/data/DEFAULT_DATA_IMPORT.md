# Panduan Impor & Seeding Data Default Bubakan Green

Dokumen ini menjelaskan prosedur resmi dan aman untuk mengimpor katalog default botani (9 MasterPlant) dan relasi kebun (10 LocationPlant) ke database Google Cloud Firestore.

---

## 1. Prinsip Utama Seeding

1. **Idempoten (Tidak Merusak Data Lapangan)**:
   - Jika dokumen MasterPlant atau LocationPlant dengan ID terkait sudah ada di Firestore dan berstatus terverifikasi (`FIELD_VERIFIED`), proses impor **TIDAK BOLEH** menimpa data lapangan tersebut.
   - Hanya dokumen yang belum ada yang akan dibuat.
2. **Satu Identitas Spesies Sereh (Single Source of Truth)**:
   - Spesies **Sereh** (*Cymbopogon citratus*) hanya memiliki **1 dokumen MasterPlant** (`master_plants/sereh`).
   - Sereh dihubungkan ke Urban Farming (`lp_uf_sereh`) dan Taman Toga RW 03 (`lp_toga_sereh`) dengan mereferensikan `masterPlantId: "sereh"`.
3. **Tanpa Data Palsu (No Fake Data)**:
   - Seluruh relasi awal berstatus `NEEDS_FIELD_VALIDATION`.
   - Tidak mencantumkan koordinat GPS palsu, jumlah panen fiktif, atau foto rekaan dari Bubakan.
4. **Bebas Biaya & Tanpa Firebase Storage**:
   - Gambar default merupakan WebP lokal yang dibundel di dalam APK (`app/src/main/res/drawable-nodpi/`).
   - Firestore hanya menyimpan metadata: `imageSourceType = "LOCAL"` dan `imageAssetName = "plant_sereh"`.

---

## 2. File Sumber Data

| File | Koleksi Firestore | Jumlah Dokumen | Deskripsi |
| :--- | :--- | :--- | :--- |
| `docs/data/DEFAULT_PLANT_CATALOG.json` | `master_plants` | 9 Dokumen | 9 Spesies master botani unik terverifikasi POWO |
| `docs/data/DEFAULT_LOCATION_PLANTS.json` | `location_plants` | 10 Dokumen | Relasi tanaman di Urban Farming & Taman Toga |

---

## 3. Prosedur Seeding Menggunakan Python (Firebase Admin SDK)

### Persyaratan
1. Python 3.10+ terpasang di sistem.
2. File Service Account JSON dari Firebase Console (Project Settings -> Service Accounts -> Generate new private key). Simpan sementara sebagai `serviceAccountKey.json` (jangan commit ke git).

### Skrip Eksekusi Idempoten
Jalankan skrip pembantu `scripts/seed_default_catalog.py`:

```bash
# Pasang dependensi firebase-admin
pip install firebase-admin

# Jalankan seeder idempoten
python scripts/seed_default_catalog.py --key path/to/serviceAccountKey.json
```

### Logika Kerja Skrip Seeder
```python
# Pseudokode Idempoten:
for plant in default_plants:
    doc_ref = db.collection("master_plants").document(plant["id"])
    snapshot = doc_ref.get()
    if not snapshot.exists:
        doc_ref.set(plant)
        print(f"Created MasterPlant: {plant['id']}")
    else:
        print(f"Skipped existing MasterPlant: {plant['id']} (Preserving field data)")

for rel in default_location_plants:
    doc_ref = db.collection("location_plants").document(rel["id"])
    snapshot = doc_ref.get()
    if not snapshot.exists:
        doc_ref.set(rel)
        print(f"Created LocationPlant: {rel['id']}")
    else:
        print(f"Skipped existing LocationPlant: {rel['id']}")
```

---

## 4. Prosedur Seeding Manual via Firebase Console (Alternatif GUI)

Jika Anda tidak menggunakan Service Account CLI:
1. Buka [Firebase Console](https://console.firebase.google.com/) -> Pilih Project `Bubakan Green`.
2. Masuk ke menu **Firestore Database**.
3. Buat koleksi `plants`:
   - Buka file `docs/data/DEFAULT_PLANT_CATALOG.json`.
   - Untuk setiap objek tanaman, klik **Add Document**, masukkan **Document ID** sesuai field `"id"` (misal: `sereh`, `cabai`, `jahe`).
   - Salin field-field JSON ke dokumen tersebut.
4. Buat koleksi `location_plants`:
   - Buka file `docs/data/DEFAULT_LOCATION_PLANTS.json`.
   - Untuk setiap relasi, klik **Add Document**, gunakan Document ID sesuai field `"id"` (misal: `lp_uf_sereh`, `lp_toga_sereh`).
   - Pastikan kedua dokumen Sereh mengarah ke `masterPlantId: "sereh"`.

---

## 5. Mode Offline / UI Preview Fixture

Aplikasi Bubakan Green telah dilengkapi mekanisme bawaan `UiPreviewOnlyPlantRepository` di dalam [RepositoryProvider.kt](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/app/src/main/java/id/bubakangreen/app/core/di/RepositoryProvider.kt).
Jika Firebase Firestore belum terhubung atau perangkat dalam keadaan offline tanpa koneksi internet:
- Seluruh 9 tanaman default katalog tetap tampil lengkap dan interaktif di halaman **Katalog**.
- Halaman **Detail Lokasi** (Urban Farming dan Taman Toga RW 03) otomatis menampilkan daftar tanaman yang relevan dengan memuat foto WebP lokal secara instan (≤ 300 ms).
- Tidak ada crash atau layar kosong.
