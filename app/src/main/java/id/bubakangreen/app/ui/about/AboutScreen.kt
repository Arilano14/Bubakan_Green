package id.bubakangreen.app.ui.about

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.bubakangreen.app.ui.components.BubakanTopBar
import id.bubakangreen.app.ui.components.Mascot
import id.bubakangreen.app.ui.components.MascotType
import id.bubakangreen.app.ui.theme.BackgroundWarm
import id.bubakangreen.app.ui.theme.BorderDivider
import id.bubakangreen.app.ui.theme.PrimaryGreen
import id.bubakangreen.app.ui.theme.PrimaryGreenDark
import id.bubakangreen.app.ui.theme.PrimaryGreenLight
import id.bubakangreen.app.ui.theme.Surface
import id.bubakangreen.app.ui.theme.TextPrimary
import id.bubakangreen.app.ui.theme.TextSecondary

/**
 * AboutScreen: Profil Resmi Program Bubakan Green.
 * Features civic vision, mascot identity, and QR scanning guide with Mascot POINTING.
 */
@Composable
fun AboutScreen(
    onNavigateBack: () -> Unit,
    onAdminClick: () -> Unit = {},
    canNavigateBack: Boolean = true,
    modifier: Modifier = Modifier
) {

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            BubakanTopBar(
                title = "Tentang Program",
                subtitle = "Kelurahan Bubakan",
                canNavigateBack = canNavigateBack,
                onNavigateBack = onNavigateBack
            )
        },
        containerColor = BackgroundWarm,
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            // Header Identity Card with Mascot
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                border = BorderStroke(1.5.dp, BorderDivider),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Mascot(
                        type = MascotType.DEFAULT,
                        size = 120.dp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "BUBAKAN GREEN",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = PrimaryGreenDark
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Sistem Informasi Urban Farming & Taman Toga",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Kelurahan Bubakan, Kecamatan Mijen\nKota Semarang",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Vision & Civic Goals
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                border = BorderStroke(1.5.dp, BorderDivider),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Latar Belakang & Tujuan",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Program BUBAKAN GREEN digagas sebagai platform digital untuk mendokumentasikan, mengedukasi, dan mempromosikan inisiatif pertanian perkotaan (Urban Farming) dan taman tanaman obat keluarga (Taman Toga) yang dikelola oleh warga di lingkungan rukun warga (RW) se-Kelurahan Bubakan.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary,
                        lineHeight = 22.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // QR Scanning Instructions with Mascot POINTING (Section 16)
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                border = BorderStroke(1.5.dp, BorderDivider),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Panduan Memindai QR di Kebun",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Arahkan kamera ke QR tanaman",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = PrimaryGreenDark
                            )
                        }

                        Mascot(
                            type = MascotType.CTA_PROCESS,
                            size = 100.dp,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    QrStepItem(
                        number = "1",
                        title = "Buka Kamera HP atau Google Lens",
                        description = "Gunakan aplikasi kamera bawaan smartphone Anda tanpa perlu memasang aplikasi tambahan."
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    QrStepItem(
                        number = "2",
                        title = "Arahkan ke Stiker QR Tanaman",
                        description = "Arahkan lensa ke plang stiker QR yang terpasang di bedengan kebun fisik Bubakan."
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    QrStepItem(
                        number = "3",
                        title = "Akses Pengetahuan Botani Lengkap",
                        description = "Ketuk tautan untuk membuka khasiat herbal, panduan budidaya, dan pelafalan bahasa Mandarin."
                    )
                }
            }



            Spacer(modifier = Modifier.height(16.dp))

            // Referensi & Kredit
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                border = BorderStroke(1.5.dp, BorderDivider),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Referensi & Kredit",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "• Taksonomi Botani: Royal Botanic Gardens, Kew (Plants of the World Online - POWO) & Flora of China.\n• Aset Foto Default: Kontributor Wikimedia Commons (Forest & Kim Starr, Wouter Hagens, Frank Vincentz, Alvesgaspar, H. Zell, Vengolis, Pratheepps, Ji-Elle) berlisensi Creative Commons (CC BY-SA 3.0 / CC BY-SA 4.0).\n• Dokumentasi lengkap lisensi & sumber botani tersimpan di arsip dokumen aplikasi (docs/data/).",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Metadata footer

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Versi 1.0.0 • Bubakan Green\nPemerintah Kelurahan Bubakan • Kota Semarang",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun QrStepItem(
    number: String,
    title: String,
    description: String
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Surface(
            shape = CircleShape,
            color = PrimaryGreenLight,
            modifier = Modifier.size(28.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = number,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryGreenDark
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}
