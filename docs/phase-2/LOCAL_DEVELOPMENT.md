# PANDUAN PENGEMBANGAN LOKAL — BUBAKAN GREEN

**Sistem Informasi Urban Farming & Taman Toga Kelurahan Bubakan**  
**Dokumen:** `docs/phase-2/LOCAL_DEVELOPMENT.md`  
**Target:** Pengembang Android & Backend / Reviewer Teknis  

---

## 1. Prasyarat Lingkungan (Environment Prerequisites)

Untuk menjalankan dan mengompilasi proyek BUBAKAN GREEN di komputer lokal, dibutuhkan komponen berikut:

| Komponen | Versi / Lokasi Standar | Keterangan |
|:---|:---|:---|
| **Sistem Operasi** | Windows 10/11 (atau macOS / Linux) | Lingkungan pengujian terverifikasi di Windows. |
| **Java Development Kit** | OpenJDK 21.0.x (LTS) | Tersedia langsung di dalam instalasi Android Studio: `C:\Program Files\Android\Android Studio\jbr` |
| **Android SDK** | API Level 26 s.d. 35 | Dikelola melalui Android Studio SDK Manager. |
| **Node.js & npm** | Node >= 20.x, npm >= 10.x | Digunakan untuk pengujian server web lokal statis. |

---

## 2. Pengaturan Android SDK & `local.properties`

Gradle membutuhkan lokasi direktori Android SDK di komputer lokal Anda:
1. Salin berkas `local.properties.template` menjadi `local.properties` di akar proyek:
   ```powershell
   Copy-Item local.properties.template local.properties
   ```
2. Buka `local.properties` dan atur path ke Android SDK Anda.  
   Contoh untuk Windows:
   ```properties
   sdk.dir=C\:\\Users\\Arilano\\AppData\\Local\\Android\\Sdk
   ```
   *(Atau gunakan forward slash: `sdk.dir=C:/Users/Arilano/AppData/Local/Android/Sdk`)*

---

## 3. Menjalankan Build & Pengujian Unit via Command Line

### A. Mengatur `JAVA_HOME` ke JDK 21 Android Studio
Di PowerShell:
```powershell
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
$env:Path = "$env:JAVA_HOME\bin;" + $env:Path
java -version
```
*Pastikan output menunjukkan OpenJDK version 21.*

### B. Menjalankan Unit Tests
Proyek menyertakan pengujian serialisasi model domain, validasi kontrak URL QR, dan mock location client:
```powershell
.\gradlew.bat test
```
Laporan pengujian HTML akan dibuat di:  
`app/build/reports/tests/testDebugUnitTest/index.html`

### C. Mengompilasi Debug APK
Untuk memverifikasi integrasi build Android secara penuh:
```powershell
.\gradlew.bat assembleDebug
```
Berkas APK hasil kompilasi akan berada di:  
`app/build/outputs/apk/debug/app-debug.apk`

---

## 4. Membuka Proyek di Android Studio

1. Buka **Android Studio**.
2. Pilih menu **File > Open**, lalu arahkan ke direktori:  
   `Bubakan Green/`
3. Tunggu proses **Gradle Sync** selesai.
4. Pastikan JDK Gradle diatur ke JDK 21:  
   Buka **Settings > Build, Execution, Deployment > Build Tools > Gradle > Gradle JDK** -> Pilih **Embedded JDK 21** (`jbr-21`).

---

## 5. Konfigurasi Google Services & Firebase Live

Proyek ini dirancang aman dengan **Nol Kredensial Palsu**:
- Build Gradle menerapkan plugin `com.google.gms.google-services` **hanya jika** berkas `app/google-services.json` fisik ditemukan di komputer lokal.
- Tanpa berkas tersebut, seluruh model domain, repository, dan pengujian unit lokal tetap dapat dikompilasi dan diuji 100%.

### Langkah Menyambungkan ke Proyek Firebase Asli (Kelurahan Bubakan):
1. Buka [Firebase Console](https://console.firebase.google.com/).
2. Buat / pilih proyek Firebase untuk Kelurahan Bubakan.
3. Daftarkan aplikasi Android dengan Package ID:
   ```
   id.bubakangreen.app
   ```
4. Unduh berkas `google-services.json`.
5. Letakkan berkas tersebut ke:
   ```
   Bubakan Green/app/google-services.json
   ```
6. Jalankan kembali Gradle sync / build.

---

## 6. Menguji Fallback Web Statis Secara Lokal

Halaman fallback web berada di direktori `web/public/`:
1. Buka terminal di direktori proyek.
2. Jalankan HTTP server lokal sederhana menggunakan Node/npx:
   ```powershell
   npx serve web/public -l 5000
   ```
3. Buka browser:
   - Halaman Utama: `http://localhost:5000/index.html`
   - Halaman Fallback QR Tanaman: `http://localhost:5000/plant.html?id=PLANT_JAHE_MERAH`
4. Verifikasi tata letak responsif pada tampilan ponsel (Mobile View).

---

## 7. Aturan Keamanan Git (Zero Remote Push)

> [!CAUTION]
> **ATURAN MUTLAK SISTEM:**
> Jangan pernah menjalankan perintah `git push` ke repositori remote dari terminal otomatis.
> Seluruh commit dan versi harus dikelola secara lokal di komputer pengembang hingga ada instruksi serah terima resmi.
