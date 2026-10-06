# BUBAKAN GREEN — WEBSITE & FIRESTORE DATA CONTRACT
**Document Version:** 1.0.0  
**Status:** DRAFT (Awaiting Approval: `ACC WEBSITE REDESIGN REVISION`)  
**Architecture:** Single Source of Truth — Cloud Firestore (`bubakan-green`)  
**Data Retrieval Model:** On-Demand HTTP Request (`fetch()` on page load) — **NOT Persistent Real-Time Listener**  

---

## 1. Data Freshness & Retrieval Protocol

### 1.1 Kejujuran Teknis Perilaku Data Web
> **Pernyataan Kontrak:**  
> *"Data website berasal langsung dari Cloud Firestore sebagai satu-satunya sumber kebenaran (Single Source of Truth). Perubahan di database akan ditampilkan ketika halaman melakukan request ulang / dimuat kembali oleh peramban."*

### 1.2 Justifikasi Desain Tanpa Listener "Real-Time"
- Skenario penggunaan fallback QR di kebun fisik adalah:  
  **PINDAI QR → BUKA HALAMAN → AMBIL DATA TERKINI → BACA FAKTA → PASANG APLIKASI**.
- Menambahkan WebSockets atau persistent `onSnapshot()` listener pada halaman web fallback publik akan memboroskan kuota koneksi concurrent Firestore, memperlambat Time to First Contentful Paint (FCP) di jaringan 3G/4G kebun, dan menambah ketergantungan SDK berat (~100 KB JS).
- Penggunaan native `fetch()` ke **Firestore REST API** (`https://firestore.googleapis.com/v1/...`) menghasilkan ukuran bundle JavaScript yang sangat kecil (< 3 KB), waktu muat instan (< 100 ms), dan kebersihan arsitektural.

---

## 2. Koleksi Koleksi Utama (Canonical Firestore Collections)

```
Cloud Firestore (bubakan-green)
├── master_plants/
│   └── {plantId}         <-- Informasi botani & fun fact per spesies tanaman
└── locations/
    └── {locationId}      <-- Informasi titik kebun RW fisik & koordinat GPS
```

**Aturan Integritas:**
- **DILARANG** membuat file JSON sekunder (seperti `web_plants.json`) sebagai tiruan database produksi di web.
- **DILARANG** melakukan *hardcode* nama tanaman, Latin, atau fakta tanaman di dalam file HTML.

---

## 3. Skema Dokumen: `master_plants/{plantId}`

### 3.1 Struktur Field Dokumen Firestore
```json
{
  "name": "projects/bubakan-green/databases/(default)/documents/master_plants/tomat",
  "fields": {
    "id": { "stringValue": "tomat" },
    "nameId": { "stringValue": "Tomat" },
    "nameLatin": { "stringValue": "Solanum lycopersicum" },
    "nameMandarin": { "stringValue": "番茄" },
    "pinyin": { "stringValue": "fān qié" },
    "primaryPhotoUrl": { "stringValue": "https://..." },
    "description": { "stringValue": "Tomat adalah tanaman dari keluarga Solanaceae..." },
    "benefits": { "stringValue": "Meningkatkan imunitas, memelihara kesehatan mata..." },
    "funFact": { "stringValue": "Kandungan antioksidan likopen pada tomat meningkat..." },
    "isPublished": { "booleanValue": true },
    "updatedAt": { "integerValue": 1727800000000 }
  }
}
```

### 3.2 Kamus Data Field `master_plants`
| Nama Field | Tipe Firestore | Wajib / Opsional | Peran pada Halaman Web (`plant.html`) | Perilaku Jika Tidak Ada |
| :--- | :--- | :--- | :--- | :--- |
| `id` | `string` | Wajib | ID unik dokumen (slug). Digunakan sebagai fallback nama jika `nameId` kosong. | Menggunakan slug path URL. |
| `nameId` | `string` | Wajib | Nama tanaman dalam Bahasa Indonesia (H1). | Menggunakan `id`. |
| `nameLatin` | `string` | Opsional | Nama ilmiah botani (italic). | Disembunyikan jika kosong. |
| `nameMandarin` | `string` | Opsional | Karakter Hanzi pelafalan Mandarin. | Badge disembunyikan. |
| `pinyin` | `string` | Opsional | Ejaan fonetik Pinyin dengan tanda nada. | Disembunyikan. |
| `primaryPhotoUrl` | `string` | Opsional | URL foto spesimen tanaman. | Ditampilkan placeholder botani bersih. |
| `funFact` | `string` | **Opsional (Usulan Ekstensi)** | **Fakta unik botani singkat untuk QR scan.** | **Wajib menampilkan fallback:** *"Fun fact tanaman ini belum tersedia."* |
| `benefits` | `string` | Opsional | Khasiat herbal & manfaat kesehatan. | Ditampilkan pada kartu terpisah. **DILARANG menggantikan funFact.** |
| `description` | `string` | Opsional | Deskripsi morfologi botani umum. | Ditampilkan jika kartu deskripsi diaktifkan. |
| `isPublished` | `boolean` | Opsional (default: true) | Status rilis data ke publik. | Jika `false`, tampilkan layar *"Tanaman Belum Diterbitkan"*. |

---

## 4. Skema Dokumen: `locations/{locationId}`

### 4.1 Struktur Field Dokumen Firestore
```json
{
  "name": "projects/bubakan-green/databases/(default)/documents/locations/loc_urban_farming_bubakan",
  "fields": {
    "id": { "stringValue": "loc_urban_farming_bubakan" },
    "name": { "stringValue": "Kebun Percontohan Urban Farming Bubakan" },
    "type": { "stringValue": "URBAN_FARMING" },
    "rw": { "stringValue": "02" },
    "address": { "stringValue": "Jl. Bubakan RT 03 / RW 02" },
    "description": { "stringValue": "Pusat budidaya sayuran pangan mandiri warga RW 02..." },
    "latitude": { "doubleValue": -7.092500 },
    "longitude": { "doubleValue": 110.320400 },
    "photoUrl": { "stringValue": "https://..." },
    "status": { "stringValue": "ACTIVE" },
    "isPublished": { "booleanValue": true }
  }
}
```

### 4.2 Kamus Data Field `locations`
| Nama Field | Tipe Firestore | Peran pada Halaman Web (`location.html`) | Perilaku Jika Tidak Ada |
| :--- | :--- | :--- | :--- |
| `name` | `string` | Nama kebun (H1). | Menggunakan format ID. |
| `type` | `string` | Tipe kebun (`URBAN_FARMING` atau `TAMAN_TOGA`). | Menampilkan badge tipe terkait. |
| `rw` | `string` | Nomor RW Kelurahan Bubakan. | Disembunyikan jika kosong. |
| `address` | `string` | Alamat jalan kebun. | Disembunyikan jika kosong. |
| `description`| `string` | Deskripsi kebun dan kegiatan warga. | Kartu deskripsi disembunyikan. |
| `latitude` | `double` | Koordinat lintang GPS. | Tombol navigasi Google Maps disembunyikan jika kosong. |
| `longitude` | `double` | Koordinat bujur GPS. | Tombol navigasi Google Maps disembunyikan jika kosong. |
| `photoUrl` | `string` | Foto lanskap kebun. | Tampil placeholder ikon kebun. |
| `status` | `string` | Status operasional (`ACTIVE`, `MAINTENANCE`). | Divalidasi bersama `isPublished`. |
| `isPublished`| `boolean`| Status publikasi resmi kelurahan. | Jika `false`, tampilkan layar *"Lokasi Belum Diterbitkan"*. |

---

## 5. Matriks Penanganan Error (Error State Matrix)

| Kondisi / Skenario | Kode Status HTTP / Internal | Respons Antarmuka Web | Teks Pesan Pengguna (Bahasa Indonesia) |
| :--- | :--- | :--- | :--- |
| **Plant ID Valid & Dokumen Ditemukan** | HTTP 200 OK | Render halaman normal | Data tanaman ditampilkan sesuai field. |
| **Plant ID Tidak Ada di Database** | HTTP 404 Not Found | Not Found Screen (🍃) | *"Tanaman belum ditemukan."* <br>Tautan: `[← Kembali ke Beranda]` |
| **Field `funFact` Kosong / Belum Ada** | HTTP 200 (field null) | Render kartu Fun Fact dengan fallback | *"Fun fact tanaman ini belum tersedia."* |
| **Status `isPublished: false`** | HTTP 200 (draft) | State Screen (🔒) | *"Tanaman Belum Diterbitkan. Sedang dalam proses verifikasi tim Kelurahan Bubakan."* |
| **Kegagalan Koneksi / Jaringan Firestore** | HTTP 5xx / Network Error | Error Screen (⚠️) | *"Informasi tanaman belum dapat dimuat. Coba lagi."* <br>Tombol: `[Muat Ulang]` |

**Keamanan Informasi (Information Security):**
- **DILARANG KERAS** mengekspos rincian teknis internal (seperti kode error Firestore `PERMISSION_DENIED`, endpoint URL REST mentah, stack trace JS, atau cuplikan JSON mentah) ke mata pengguna publik.

---

## 6. Keamanan & Otorisasi Firestore (Security Rules Alignment)

Sesuai `firestore.rules` aktif:
```javascript
match /master_plants/{plantId} {
  allow read: if true;
  allow write: if isAdmin();
}
match /locations/{locationId} {
  allow read: if true;
  allow write: if isAdmin();
}
```
- **Situs Web Publik:** Hanya memiliki akses **READ** anonim untuk koleksi publik yang diterbitkan.
- **TIDAK ADA AKSES WRITE:** Situs web publik tidak memiliki hak menulis, menghapus, atau mengubah data Firestore.
- Hak tulis hanya dimiliki oleh admin yang terotentikasi melalui Firebase Auth pada aplikasi Android.
