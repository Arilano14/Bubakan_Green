package id.bubakangreen.app.ui.catalog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import id.bubakangreen.app.core.audio.AudioState
import id.bubakangreen.app.ui.common.UiState
import id.bubakangreen.app.ui.components.BubakanTopBar
import id.bubakangreen.app.ui.components.EmptyState
import id.bubakangreen.app.ui.components.MandarinSpeakerButton
import id.bubakangreen.app.ui.components.Mascot
import id.bubakangreen.app.ui.components.MascotType
import id.bubakangreen.app.ui.components.OfflineStatusBar
import id.bubakangreen.app.ui.components.PrimaryButton
import id.bubakangreen.app.ui.components.ShimmerBox
import id.bubakangreen.app.ui.theme.AccentSunnyContainer
import id.bubakangreen.app.ui.theme.AccentSunnyGold
import id.bubakangreen.app.ui.theme.BackgroundWarm
import id.bubakangreen.app.ui.theme.BorderDivider
import id.bubakangreen.app.ui.theme.PrimaryGreen
import id.bubakangreen.app.ui.theme.PrimaryGreenDark
import id.bubakangreen.app.ui.theme.PrimaryGreenLight
import id.bubakangreen.app.ui.theme.TextPrimary
import id.bubakangreen.app.ui.theme.TextSecondary

/**
 * PlantDetailScreen: Overhauled Botanical Learning Screen.
 * Conforms to Section 12, 14, and 15:
 * 1. [Back] navigation in top bar
 * 2. [Plant Image] Hero photography (primary visual subject)
 * 3. Plant Name & Scientific Latin Name
 * 4. Mandarin Pod: Hanzi, Pinyin, Speaker Audio
 * 5. "Kenalan lebih dekat 🌱": Educational companion section with MascotType.LEARNING (160dp)
 * 6. Success State: MascotType.HAPPY (200dp) with "Bagus! Kamu sudah mengenal tanaman ini."
 */
@Composable
fun PlantDetailScreen(
    plantId: String,
    onNavigateBack: () -> Unit,
    onInfoClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlantDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    var isLessonCompleted by remember { mutableStateOf(false) }

    LaunchedEffect(plantId) {
        viewModel.loadPlantDetail(plantId)
    }

    LaunchedEffect(uiState.audioState) {
        if (uiState.audioState is AudioState.Error) {
            snackbarHostState.showSnackbar((uiState.audioState as AudioState.Error).message)
        }
    }

    Scaffold(
        topBar = {
            BubakanTopBar(
                title = "Detail Tanaman",
                subtitle = "Ensiklopedi Tanaman Bubakan",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack,
                onInfoClick = onInfoClick
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = BackgroundWarm,
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            OfflineStatusBar(isOffline = uiState.isOffline)

            when (val plantState = uiState.plant) {
                is UiState.Loading -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ShimmerBox(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp),
                            cornerRadius = 24.dp
                        )
                        ShimmerBox(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(90.dp),
                            cornerRadius = 20.dp
                        )
                        ShimmerBox(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            cornerRadius = 20.dp
                        )
                    }
                }

                is UiState.Success -> {
                    val plant = plantState.data

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                    ) {
                        // 1. [Plant Image] Hero Botanical Photo (Visual Main Subject)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f / 10f)
                                .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
                                .background(PrimaryGreenLight)
                        ) {
                            if (!plant.primaryPhotoUrl.isNullOrBlank()) {
                                AsyncImage(
                                    model = plant.primaryPhotoUrl,
                                    contentDescription = "Foto tanaman ${plant.nameId}",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "🌿",
                                        style = MaterialTheme.typography.displayMedium
                                    )
                                }
                            }
                        }

                        // Content Container
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 20.dp)
                        ) {
                            // 2. NOMENCLATURE: Plant Name + Scientific Latin Name
                            Text(
                                text = plant.nameId,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimary
                            )

                            if (plant.nameLatin.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = plant.nameLatin,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontStyle = FontStyle.Italic,
                                    fontWeight = FontWeight.Medium,
                                    color = TextSecondary
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // 3. MANDARIN DISCOVERY POD: Hanzi, Pinyin, Speaker Audio
                            if (!plant.nameMandarin.isNullOrBlank() || !plant.pinyin.isNullOrBlank()) {
                                MandarinDiscoveryPod(
                                    hanzi = plant.nameMandarin.orEmpty(),
                                    pinyin = plant.pinyin.orEmpty(),
                                    audioState = uiState.audioState,
                                    hasAudio = !plant.mandarinAudioUrl.isNullOrBlank(),
                                    onPlayAudio = { viewModel.playMandarinAudio() }
                                )
                                Spacer(modifier = Modifier.height(20.dp))
                            }

                            // 4. "Kenalan lebih dekat 🌱" (Section 12: Educational content with Mascot LEARNING 160dp)
                            Card(
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(containerColor = id.bubakangreen.app.ui.theme.Surface),
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
                                                text = "Kenalan lebih dekat 🌱",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = PrimaryGreenDark,
                                                fontSize = 18.sp
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "Panduan botani & perawatan",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = TextSecondary
                                            )
                                        }

                                        // Companion Mascot LEARNING (160dp)
                                        Mascot(
                                            type = MascotType.LEARNING,
                                            size = 140.dp,
                                            modifier = Modifier.padding(start = 8.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    Text(
                                        text = plant.description.ifBlank {
                                            "Tanaman ini merupakan salah satu flora unggulan yang dibudidayakan di kebun binaan Kelurahan Bubakan untuk penghijauan serta pemenuhan tanaman obat keluarga (TOGA) mandiri."
                                        },
                                        style = MaterialTheme.typography.bodyMedium,
                                        lineHeight = 22.sp,
                                        color = TextPrimary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // 5. GARDEN PRESENCE IN BUBAKAN
                            Card(
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = PrimaryGreenLight.copy(alpha = 0.5f)),
                                border = BorderStroke(1.5.dp, PrimaryGreen.copy(alpha = 0.3f)),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "📍", fontSize = 24.sp)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "Lokasi di Kebun Bubakan",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = PrimaryGreenDark
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "Dapat ditemukan dan dipelajari langsung di Taman Toga RW 03 dan Urban Farming Bubakan.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // 6. SUCCESS STATE (Section 14: Mascot HAPPY 200dp, green dominant, no gamification)
                            if (isLessonCompleted) {
                                Card(
                                    shape = RoundedCornerShape(24.dp),
                                    colors = CardDefaults.cardColors(containerColor = PrimaryGreenLight),
                                    border = BorderStroke(2.dp, PrimaryGreen),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Mascot(
                                            type = MascotType.HAPPY,
                                            size = 200.dp
                                        )

                                        Spacer(modifier = Modifier.height(16.dp))

                                        Text(
                                            text = "Bagus! Kamu sudah mengenal tanaman ini.",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = PrimaryGreenDark,
                                            textAlign = TextAlign.Center,
                                            fontSize = 18.sp
                                        )

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Text(
                                            text = "Terima kasih sudah belajar bersama Bubakan Green 🌱",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = TextSecondary,
                                            textAlign = TextAlign.Center
                                        )

                                        Spacer(modifier = Modifier.height(16.dp))

                                        PrimaryButton(
                                            text = "Kembali ke Katalog",
                                            onClick = onNavigateBack,
                                            height = 48.dp,
                                            modifier = Modifier.fillMaxWidth(0.8f)
                                        )
                                    }
                                }
                            } else {
                                PrimaryButton(
                                    text = "Selesai Mengenal Tanaman Ini ✨",
                                    onClick = { isLessonCompleted = true },
                                    height = 50.dp
                                )
                            }

                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }
                }

                is UiState.Empty -> {
                    EmptyState(
                        title = "Informasi Belum Tersedia",
                        message = "Data tanaman ini belum lengkap.",
                        actionLabel = "Kembali ke Katalog",
                        onActionClick = onNavigateBack
                    )
                }

                is UiState.Error -> {
                    EmptyState(
                        title = "Oops! Ada Sedikit Kendala",
                        message = plantState.message,
                        actionLabel = "Coba Lagi",
                        onActionClick = { viewModel.loadPlantDetail(plantId) }
                    )
                }
            }
        }
    }
}

/**
 * Mandarin pod with Hanzi, Pinyin, and speaker audio pronunciation button.
 */
@Composable
private fun MandarinDiscoveryPod(
    hanzi: String,
    pinyin: String,
    audioState: AudioState,
    hasAudio: Boolean,
    onPlayAudio: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = id.bubakangreen.app.ui.theme.Surface),
        border = BorderStroke(1.5.dp, BorderDivider),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PrimaryGreenLight
                ) {
                    Text(
                        text = "PELAFALAN BAHASA MANDARIN",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = PrimaryGreenDark,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.Bottom) {
                    if (hanzi.isNotBlank()) {
                        Text(
                            text = hanzi,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PrimaryGreenDark
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                    }
                    if (pinyin.isNotBlank()) {
                        Text(
                            text = pinyin,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondary
                        )
                    }
                }
            }

            MandarinSpeakerButton(
                audioState = audioState,
                onClick = onPlayAudio,
                enabled = hasAudio
            )
        }
    }
}
