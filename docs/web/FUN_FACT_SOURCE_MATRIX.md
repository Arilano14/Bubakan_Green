# BUBAKAN GREEN — PLANT FUN FACT SOURCE MATRIX
**Document Version:** 1.0.0  
**Status:** VERIFIED & REFERENCED  
**Application Target:** Cloud Firestore `/master_plants/{plantId}` & QR Micro Landing Page (`/plant/{plantId}`)  
**Standard:** Strict evidence-based botanical facts. Zero marketing claims. Zero fabricated trivia.  

---

## 1. Methodology & Verification Standard

Setiap data `funFact` yang diajukan untuk katalog botani Bubakan Green harus memenuhi kriteria:
1. **Faktual & Ilmiah:** Bersumber dari publikasi ilmiah, badan penelitian pertanian nasional (BRIN/Balittro/Kementan), universitas terakreditasi, atau basis data botani internasional (Kew Gardens / USDA).
2. **Ringkas & Menarik (Snappy):** Dapat dipahami warga dan pengunjung kebun dalam 5–10 detik saat memindai label QR.
3. **Bukan Khasiat Herbal Umum:** Berbeda secara semantik dari khasiat obat rutin (bukan sekadar "mengobati batuk" atau "menurunkan kolesterol", melainkan fakta keunikan biologis, adaptasi tanaman, atau sejarah budidaya).
4. **Status Verifikasi:** Diberi label `VERIFIED` hanya jika rujukan pustaka telah diverifikasi.

---

## 2. Seeded Plants Fun Fact Matrix (9 Spesies Utama)

| Plant ID | Nama Tanaman & Latin | Usulan Fun Fact (Bahasa Indonesia) | Sumber Ilmiah / Institusi Rujukan | Status Verifikasi |
| :--- | :--- | :--- | :--- | :--- |
| **`cabai`** | **Cabai**<br>*(Capsicum annuum)* | Rasa pedas cabai berasal dari senyawa *kapsaisin*, yang berevolusi secara alami untuk mengusir hama mamalia namun tidak dirasakan sama sekali oleh burung pemencar biji. | *Balai Penelitian Tanaman Sayuran (Balitsa / BRIN)*; *Tewksbury & Nabhan (Nature, 2001)*. | `VERIFIED` |
| **`jahe`** | **Jahe**<br>*(Zingiber officinale)* | Jahe merupakan tanaman yang telah kehilangan kemampuan memproduksi biji secara alami; seluruh jahe di dunia diperbanyak secara vegetatif melalui klon rimpangnya oleh tangan manusia selama ribuan tahun. | *Balai Penelitian Tanaman Rempah dan Obat (Balittro / BRIN)*; *Ravindran & Nirmal Babu (Ginger: The Genus Zingiber, CRC Press)*. | `VERIFIED` |
| **`kangkung`** | **Kangkung**<br>*(Ipomoea aquatica)* | Batang kangkung berongga seperti pipa udara alami (*aerenkim*) yang memungkinkannya mengapung dan tetap bernapas meski tumbuh di lahan tergenang air ekstrem. | *Fakultas Pertanian Institut Pertanian Bogor (IPB)*; *Austin, D. F. (Economic Botany)*. | `VERIFIED` |
| **`kencur`** | **Kencur**<br>*(Kaempferia galanga)* | Daun kencur tumbuh mendatar rapat menempel di atas permukaan tanah (*prostrat*) sebagai strategi alami menghambat penguapan air tanah dan mencegah tumbuhnya gulma pesaing. | *Pusat Studi Biofarmaka Tropika LPPM IPB*; *Heyne, K. (Tumbuhan Berguna Indonesia)*. | `VERIFIED` |
| **`kunyit`** | **Kunyit**<br>*(Curcuma longa)* | Zat warna kuning cerah pada kunyit (*kurkumin*) secara alami peka terhadap tingkat keasaman (pH) dan dapat berfungsi sebagai indikator asam-basa alami sederhana. | *Departemen Kimia FMIPA Universitas Gadjah Mada (UGM)*; *Kementerian Pertanian RI*. | `VERIFIED` |
| **`lidah_buaya`** | **Lidah Buaya**<br>*(Aloe vera)* | Gel lidah buaya tersusun atas lebih dari 98% air yang terikat dalam struktur polisakarida unik, memungkinkannya bertahan hidup berbulan-bulan tanpa pasokan air tanah. | *Pusat Riset Biologi BRIN*; *Reynolds, T. (Aloes: The Genus Aloe, CRC Press)*. | `VERIFIED` |
| **`sereh`** | **Sereh**<br>*(Cymbopogon citratus)* | Batang sereh mengandung minyak asiri alami (*sitronelal* dan *geraniol*) yang aromanya membingungkan sensor penciuman nyamuk sehingga efektif sebagai pengusir serangga alami. | *Balai Besar Penelitian dan Pengembangan Pascapanen Pertanian (Kementan)*; *WHO Monograph on Selected Medicinal Plants*. | `VERIFIED` |
| **`terong`** | **Terong**<br>*(Solanum melongena)* | Secara botani, terong termasuk kategori buah beri sejati (*berry*), dan masih berkerabat dekat satu keluarga (*Solanaceae*) dengan tomat, cabai, serta kentang. | *Royal Botanic Gardens, Kew*; *USDA Agricultural Research Service (ARS)*. | `VERIFIED` |
| **`tomat`** | **Tomat**<br>*(Solanum lycopersicum)* | Kandungan antioksidan *likopen* pada tomat justru meningkat dan lebih mudah diserap oleh tubuh manusia setelah tomat dimasak atau dipanaskan dibandingkan saat dimakan mentah. | *Departemen Gizi Masyarakat IPB*; *Dewanto et al. (Journal of Agricultural and Food Chemistry, 2002)*. | `VERIFIED` |

---

## 3. Aturan Fallback Jika `funFact` Belum Diisi di Database

1. **Prinsip Utama:** Tidak ada fabrikasi fakta di sisi frontend web jika field `funFact` tidak ditemukan pada dokumen Firestore.
2. **Teks Fallback Standar:**
   > *"Fun fact tanaman ini belum tersedia."*
3. **Pemisahan Semantik Mutlak:**
   - Field `benefits` (khasiat herbal) **TIDAK BOLEH** digunakan sebagai pengganti `funFact`.
   - Field `description` (deskripsi botani umum) **TIDAK BOLEH** dipaksakan menjadi `funFact`.
4. **Pembaruan Data:**
   - Ketika data `funFact` dimasukkan ke Firestore via panel admin atau skrip migrasi, halaman QR `/plant/{plantId}` akan langsung menampilkan data tersebut pada pemuatan halaman (*refresh*) berikutnya tanpa kompilasi ulang web.
