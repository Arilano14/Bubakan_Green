# Mandarin Voice Compatibility QC Report — Bubakan Green
# Audio Optionality, Null-Safety, and Pronunciation Pod Inspection

Laporan evaluasi kepatuhan aturan bahwa suara Mandarin bersifat **STRICTLY OPTIONAL** dan tidak boleh memblokir pembuatan tanaman maupun merusak layar detail tanaman.

---

## 1. Aturan Wajib Mandarin Voice (Section 20 & 21)
* Suara rekaman Mandarin **BUKAN** syarat wajib penyimpanan tanaman.
* Tanaman baru (*Sirih*, *Pegagan*, *Kemangi*) disimpan dengan nilai `mandarinAudioUrl = null`.
* Aplikasi **DILARANG** menggenerasi file audio palsu atau membuat rekaman Mandarin tiruan (*No Fake Audio*).
* Layar detail tanaman (`PlantDetailScreen.kt`) harus mampu menangani `mandarinAudioUrl == null` secara aman dan anggun.

---

## 2. Hasil Pengujian pada Layar Detail Tanaman Baru

### A. Sirih (`sirih`)
* **Aksara Hanzi**: `蒌叶` (Tampil rapi pada kartu *Pelafalan Mandarin*)
* **Pinyin**: `lóu yè` (Tampil jelas dengan tanda nada yang benar)
* **Audio URL**: `null`
* **Status Pemutar Audio**: Tombol audio disembunyikan/dinonaktifkan secara anggun.
* **Perilaku UI**: Layar detail memuat seluruh teks botani, foto, dan tombol navigasi tanpa ada NullPointerException atau crash.
* **Status**: **PASS**.

### B. Pegagan (`pegagan`)
* **Aksara Hanzi**: `积雪草`
* **Pinyin**: `jī xuě cǎo`
* **Audio URL**: `null`
* **Status**: **PASS**.

### C. Kemangi (`kemangi`)
* **Aksara Hanzi**: `罗勒`
* **Pinyin**: `luó lè`
* **Audio URL**: `null`
* **Status**: **PASS**.

---

## 3. Kesimpulan Kompatibilitas
Sistem sepenuhnya kompatibel dengan tanaman yang tidak memiliki rekaman audio suara Mandarin. Tidak ada bug NPE, tidak ada pembatalan simpan akibat audio kosong, dan integritas edukasi botani tetap terjaga optimal.
