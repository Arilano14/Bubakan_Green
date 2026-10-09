# Dokumentasi Aset Audio - Bubakan Green

Dokumen ini memuat atribusi, sumber, lisensi, dan spesifikasi teknis aset audio yang digunakan pada aplikasi Bubakan Green (Android & Web).

---

## 1. Background Music Kuis Tanaman

- **Judul Aset**: Kids Happy Background Music
- **Kreator / Artis**: SigmaMusicArt
- **Platform Sumber**: Pixabay Music
- **URL Sumber**: [https://pixabay.com/music/happy-childrens-tunes-kids-happy-background-music-401734/](https://pixabay.com/music/happy-childrens-tunes-kids-happy-background-music-401734/)
- **Tanggal Pengunduhan**: 9 Oktober 2026
- **Lisensi**: Pixabay Content License (Bebas royalti, gratis digunakan untuk kebutuhan komersial dan non-komersial tanpa kewajiban atribusi formal, namun didokumentasikan di sini sebagai etika dan transparansi kepemilikan aset).
- **Durasi Asli**: 1:28 (88 detik)
- **Ukuran Berkas Asli**: 2.817.044 bytes (~2,68 MB)
- **Lokasi Berkas Asli (Arsip)**: `web/public/assets/audio/sigmamusicart-kids-happy-background-music-401734.mp3`
- **Spesifikasi Optimasi**:
  - Sample Rate: 44.100 Hz
  - Saluran: Mono
  - Bitrate: 64 kbps (MP3 CBR via libmp3lame)
  - Ukuran Teroptimasi: ~704.722 bytes (~688 KB)
  - Pitch & Tempo: 100% dipertahankan sesuai aslinya
- **Lokasi Bundel Aplikasi**:
  - Android Raw Resource: `app/src/main/res/raw/quiz_background_music.mp3`
  - Web Public Asset: `web/public/assets/audio/quiz_background_music.mp3`
- **Konfigurasi Playback**:
  - Gain / Volume: ~3% (`0.03f`)
  - Transisi: Soft fade-in (~300–500 ms) saat kuis dimulai, looping kontinu antar soal, ducking/pause saat audio pelafalan botani aktif, soft fade-out (~200–300 ms) saat kuis selesai / keluar.

---

## 2. Sound Effect Jawaban Kuis ("Correct" & "Wrong")

### A. Sound Effect Jawaban Benar (`sound_correct.mp3`)
- **Judul Aset**: Cheerful Ascending Chime (G5 → C6 → E6)
- **Kreator / Desain**: Bubakan Green Project Synthesized Chime
- **Tipe Suara**: Bright Ascending Bell Arpeggio dengan peluruhan harmonik hangat
- **Durasi**: ~550 ms
- **Ukuran Berkas**: 5.058 bytes (~5,0 KB)
- **Format**: MP3 (44.100 Hz, mono, 64 kbps CBR)
- **Lokasi Bundel Aplikasi**:
  - Android Raw Resource: `app/src/main/res/raw/sound_correct.mp3`
  - Web Public Asset: `web/public/assets/audio/sound_correct.mp3`
- **Konfigurasi Playback**:
  - Volume: ~35% (`0.35f`)
  - Trigger: Diputar secara instan ketika pengguna menekan "Konfirmasi Jawaban" dan jawaban yang dipilih benar.

### B. Sound Effect Jawaban Salah (`sound_wrong.mp3`)
- **Judul Aset**: Gentle Descending Cue (Eb4 → Bb3)
- **Kreator / Desain**: Bubakan Green Project Synthesized Cue
- **Tipe Suara**: Soft Warm Marimba / Bell Cue (tidak kasar/menakutkan untuk anak, ramah edukasi)
- **Durasi**: ~450 ms
- **Ukuran Berkas**: 4.222 bytes (~4,2 KB)
- **Format**: MP3 (44.100 Hz, mono, 64 kbps CBR)
- **Lokasi Bundel Aplikasi**:
  - Android Raw Resource: `app/src/main/res/raw/sound_wrong.mp3`
  - Web Public Asset: `web/public/assets/audio/sound_wrong.mp3`
- **Konfigurasi Playback**:
  - Volume: ~35% (`0.35f`)
  - Trigger: Diputar secara instan ketika pengguna menekan "Konfirmasi Jawaban" dan jawaban yang dipilih salah.

---

## 3. Audio Pelafalan Botani & Evaluasi Kuis (Mandarin)

Seluruh 18 berkas audio lokal (.aac) yang telah terintegrasi di `app/src/main/res/raw/` dan `web/public/assets/audio/`:
- 12 Audio Pelafalan Botani: Cabai, Jahe, Kangkung, Kemangi, Kencur, Kunyit, Lidah Buaya, Pegagan, Sereh, Sirih, Terong, Tomat.
- 6 Audio Evaluasi Kuis:
  - `audio_feedback_perfect` (Skor 100)
  - `audio_feedback_excellent` (Skor 80)
  - `audio_feedback_good` (Skor 60)
  - `audio_feedback_low` (Skor 40)
  - `audio_feedback_encouragement` (Skor 20)
  - `audio_feedback_retry` (Skor 0)
