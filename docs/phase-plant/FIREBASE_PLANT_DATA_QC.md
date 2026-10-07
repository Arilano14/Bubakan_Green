# Firebase & Firestore Plant Data QC Report — Bubakan Green
# Collection Architecture, Documents, Fields, and Security Rules

Laporan audit teknis integritas penyimpanan Cloud Firestore, skema koleksi `master_plants`, kepatuhan terhadap batasan dokumen 1 MiB, dan audit aturan keamanan (*Security Rules*).

---

## 1. Arsitektur Dokumen & Audit Bebas Base64

Dokumen Firestore **HANYA** menyimpan referensi string URL/path foto dan metadata ringan. **TIDAK ADA biner Base64** yang disimpan di dalam dokumen.

| No | Document ID | Nama Tanaman (`nameId`) | Nama Ilmiah (`nameLatin`) | Aksara Hanzi | Foto Ref (`primaryPhotoUrl`) | Tipe Foto | Base64 Detected? |
|---|---|---|---|---|---|---|---|
| 1 | `cabai` | Cabai | *Capsicum annuum* | 辣椒 | `.../plant_cabai_gallery.jpg` | Local Cache | **NO (0 bytes b64)** |
| 2 | `jahe` | Jahe | *Zingiber officinale* | 生姜 | `plant_jahe` | Local Asset | **NO (0 bytes b64)** |
| 3 | `kangkung` | Kangkung | *Ipomoea aquatica* | 空心菜 | `plant_kangkung` | Local Asset | **NO (0 bytes b64)** |
| 4 | `kemangi` | Kemangi | *Ocimum basilicum var. anisatum* | 罗勒 | `https://upload.wikimedia...` | Remote HTTPS | **NO (0 bytes b64)** |
| 5 | `kencur` | Kencur | *Kaempferia galanga* | 沙姜 | `plant_kencur` | Local Asset | **NO (0 bytes b64)** |
| 6 | `kunyit` | Kunyit | *Curcuma longa* | 姜黄 | `plant_kunyit` | Local Asset | **NO (0 bytes b64)** |
| 7 | `lidah_buaya` | Lidah Buaya | *Aloe vera* | 芦荟 | `.../plant_lidah_buaya_camera.jpg` | Local Cache | **NO (0 bytes b64)** |
| 8 | `pegagan` | Pegagan | *Centella asiatica* | 积雪草 | `.../plant_pegagan_gallery.jpg` | Local Cache | **NO (0 bytes b64)** |
| 9 | `sereh` | Sereh | *Cymbopogon citratus* | 柠檬草 | `plant_sereh` | Local Asset | **NO (0 bytes b64)** |
| 10 | `sirih` | Sirih | *Piper betle* | 蒌叶 | `.../plant_sirih_camera.jpg` | Local Cache | **NO (0 bytes b64)** |
| 11 | `terong` | Terong | *Solanum melongena* | 茄子 | `plant_terong` | Local Asset | **NO (0 bytes b64)** |
| 12 | `tomat` | Tomat | *Solanum lycopersicum* | 番茄 | `plant_tomat` | Local Asset | **NO (0 bytes b64)** |

---

## 2. Audit Integritas Tipe Data
* `createdAt`: Integer / Long UNIX timestamp (milidetik).
* `updatedAt`: Integer / Long UNIX timestamp (milidetik).
* `isPublished`: Boolean (`true`).
* `mandarinAudioUrl`: `null` (opsional, tidak memblokir simpan).
* `description`, `characteristics`, `commonUses`, `cultivationNotes`: String lengkap.

---

## 3. Audit Aturan Keamanan (Security Rules)
Berdasarkan berkas `firestore.rules`:
* **Akses Baca (`master_plants`)**: Publik diizinkan membaca (`allow read: if true;`). Terverifikasi pada Katalog Publik aplikasi.
* **Akses Tulis (`master_plants`)**: Hanya pengguna terautentikasi dengan peran `ADMIN` (`allow write: if isAdmin();`). Permintaan tanpa kredensial admin ditolak dengan `HTTP Error 403: Forbidden`.
