# Photo Replacement & Document Stability QC Report — Bubakan Green
# Cabai & Lidah Buaya Replacement Scenarios

Laporan pengujian penggantian foto pada tanaman terdaftar tanpa mengubah `plantId` dan tanpa menciptakan duplikasi data di Firestore.

---

## 1. Tujuan & Guardrails
* Mengganti foto tanaman default yang sudah ada (*Cabai* dan *Lidah Buaya*).
* Menjaga stabilitas `plantId` agar relasi kebun, penanda lokasi (`location_plants`), dan QR code tidak rusak.
* Memperbarui metadata `updatedAt` secara otomatis.
* Menjaga integritas bidang-bidang botani lainnya agar tidak ter-reset atau terhapus secara tidak sengaja.

---

## 2. Hasil Audit Penggantian Foto

### A. Cabai (`cabai`) — Saluran Galeri
* **Plant ID**: `cabai` (Stabil / Tidak berubah)
* **Koleksi**: `master_plants/cabai`
* **Foto Sebelumnya**: `plant_cabai` (WebP drawable bawaan APK)
* **Foto Baru**: `/data/user/0/id.bubakangreen.app/files/photos/plant_cabai_gallery.jpg`
* **Sumber & Lisensi**: Wikimedia Commons `File:Capsicum annuum MHNT.BOT.2007.40.74.jpg` (CC BY-SA 3.0)
* **Bidang Lain**: Nama Indonesia, nama Latin (*Capsicum annuum*), deskripsi, karakteristik, dan manfaat tetap utuh 100%.
* **Status**: **PASS / VERIFIED**.

### B. Lidah Buaya (`lidah_buaya`) — Saluran Kamera
* **Plant ID**: `lidah_buaya` (Stabil / Tidak berubah)
* **Koleksi**: `master_plants/lidah_buaya`
* **Foto Sebelumnya**: `plant_lidah_buaya` (WebP drawable bawaan APK)
* **Foto Baru**: `/data/user/0/id.bubakangreen.app/files/photos/plant_lidah_buaya_camera.jpg`
* **Sumber & Lisensi**: Wikimedia Commons `File:Aloe-Vera-Sitia-Crete-Greece.jpg` (CC BY-SA 4.0)
* **Bidang Lain**: Nama Indonesia, nama Latin (*Aloe vera*), deskripsi, karakteristik, dan manfaat tetap utuh 100%.
* **Status**: **PASS / VERIFIED**.

---

## 3. Verifikasi Dampak Database & Bebas Dokumen Ganda
* Tidak ada dokumen baru dengan ID acak yang tercipta saat melakukan *Edit*.
* Query Firestore mengonfirmasi tepat **1 dokumen** untuk Cabai (`cabai`) dan tepat **1 dokumen** untuk Lidah Buaya (`lidah_buaya`).
