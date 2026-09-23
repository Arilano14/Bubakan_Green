package id.bubakangreen.app.ui.about

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.bubakangreen.app.ui.components.BubakanTopBar
import id.bubakangreen.app.ui.theme.BackgroundLight
import id.bubakangreen.app.ui.theme.OnPrimaryContainerDark
import id.bubakangreen.app.ui.theme.OnSurfaceDark
import id.bubakangreen.app.ui.theme.OnSurfaceVariant
import id.bubakangreen.app.ui.theme.OutlineGrey
import id.bubakangreen.app.ui.theme.PrimaryContainerMint
import id.bubakangreen.app.ui.theme.PrimaryForest
import id.bubakangreen.app.ui.theme.SecondarySage
import id.bubakangreen.app.ui.theme.SurfaceWhite

/**
 * AboutScreen (SCR-PUB-06): Official information profile of the Bubakan Green program.
 * Outlines the civic vision, QR code physical scanning guide, and product identity.
 */
@Composable
fun AboutScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            BubakanTopBar(
                title = "Tentang Program",
                subtitle = "Kelurahan Bubakan",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        },
        containerColor = BackgroundLight,
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            // Header Identity Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                border = BorderStroke(1.dp, OutlineGrey),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(PrimaryContainerMint),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🌱", style = MaterialTheme.typography.headlineLarge)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "BUBAKAN GREEN",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryForest
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Sistem Informasi Urban Farming & Taman Toga",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = OnSurfaceDark,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Kelurahan Bubakan, Kecamatan Mijen\nKota Semarang",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Vision & Civic Goals
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                border = BorderStroke(1.dp, OutlineGrey),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Latar Belakang & Tujuan",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = OnSurfaceDark
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Program BUBAKAN GREEN digagas sebagai platform digital untuk mendokumentasikan, mengedukasi, dan mempromosikan inisiatif pertanian perkotaan (Urban Farming) dan taman tanaman obat keluarga (Taman Toga) yang dikelola oleh warga di lingkungan rukun warga (RW) se-Kelurahan Bubakan.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurfaceDark,
                        lineHeight = 22.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // QR Scanning Instructions
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                border = BorderStroke(1.dp, OutlineGrey),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Panduan Memindai Stiker QR di Kebun",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = OnSurfaceDark
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    QrStepItem(
                        number = "1",
                        title = "Buka Kamera HP atau Google Lens",
                        description = "Gunakan aplikasi kamera bawaan smartphone Anda tanpa perlu memasang aplikasi pemindai tambahan."
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
                        description = "Ketuk tautan yang muncul untuk membuka khasiat herbal, panduan budidaya, dan mendengarkan pelafalan bahasa Mandarin."
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Version & Handover Metadata
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Versi 1.0.0 (Build Produksi)\nPemerintah Kelurahan Bubakan • Semarang",
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVariant,
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
            color = PrimaryContainerMint,
            modifier = Modifier.size(28.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = number,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = OnPrimaryContainerDark
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = OnSurfaceDark
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall ?: MaterialTheme.typography.bodyMedium,
                color = OnSurfaceVariant
            )
        }
    }
}
