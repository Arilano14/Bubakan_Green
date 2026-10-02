package id.bubakangreen.app.ui.catalog

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.QrCode2
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import id.bubakangreen.app.ui.components.EmptyState
import id.bubakangreen.app.ui.components.MandarinSpeakerButton
import id.bubakangreen.app.ui.components.Mascot
import id.bubakangreen.app.ui.components.MascotType
import id.bubakangreen.app.ui.components.OfflineStatusBar
import id.bubakangreen.app.ui.components.PlantImage
import id.bubakangreen.app.ui.components.PrimaryButton
import id.bubakangreen.app.ui.components.ShimmerBox
import id.bubakangreen.app.ui.theme.AquaAccent
import id.bubakangreen.app.ui.theme.BackgroundWarm
import id.bubakangreen.app.ui.theme.BorderDivider
import id.bubakangreen.app.ui.theme.ForestGreen
import id.bubakangreen.app.ui.theme.PrimaryGreen
import id.bubakangreen.app.ui.theme.PrimaryGreenDark
import id.bubakangreen.app.ui.theme.PrimaryGreenLight
import id.bubakangreen.app.ui.theme.TextPrimary
import id.bubakangreen.app.ui.theme.TextSecondary

/**
 * PlantDetailScreen: Redesigned as a mini lesson experience.
 *
 * Structure: Hero Photo → Nomenclature → Mandarin → Educational Guide → Location → Completion
 * Single mascot rule enforced: only LEARNING or HAPPY visible at any time.
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
    var showQrDialog by remember { mutableStateOf(false) }

    BackHandler {
        onNavigateBack()
    }

    LaunchedEffect(plantId) {
        viewModel.loadPlantDetail(plantId)
    }

    LaunchedEffect(uiState.audioState) {
        if (uiState.audioState is AudioState.Error) {
            snackbarHostState.showSnackbar((uiState.audioState as AudioState.Error).message)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (uiState.plant is UiState.Success) {
                Surface(
                    color = id.bubakangreen.app.ui.theme.SurfaceCard,
                    shadowElevation = 8.dp,
                    tonalElevation = 2.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                    ) {
                        if (isLessonCompleted) {
                            PrimaryButton(
                                text = "Kembali ke Katalog",
                                onClick = onNavigateBack,
                                height = 54.dp
                            )
                        } else {
                            PrimaryButton(
                                text = "Selesai Mengenal Tanaman Ini ✨",
                                onClick = { isLessonCompleted = true },
                                height = 54.dp
                            )
                        }
                    }
                }
            }
        },
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
                        modifier = Modifier.fillMaxSize().padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ShimmerBox(
                            modifier = Modifier.fillMaxWidth().height(240.dp),
                            cornerRadius = 24.dp
                        )
                        ShimmerBox(
                            modifier = Modifier.fillMaxWidth().height(80.dp),
                            cornerRadius = 20.dp
                        )
                        ShimmerBox(
                            modifier = Modifier.fillMaxWidth().height(140.dp),
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
                        // Hero plant photo with overlaid back button
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f / 10f)
                                .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
                                .background(PrimaryGreenLight)
                        ) {
                            PlantImage(
                                plant = plant,
                                contentDescription = "Foto tanaman ${plant.nameId}",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            // Navigation & Action buttons overlay (UX Law: 48dp touch target)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.95f))
                                        .clickable(onClick = onNavigateBack),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Kembali",
                                        tint = TextPrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                if (plant.isPublished) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                            .background(Color.White.copy(alpha = 0.95f))
                                            .clickable { showQrDialog = true },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.QrCode2,
                                            contentDescription = "Lihat Kode QR",
                                            tint = ForestGreen,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Content
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 20.dp)
                        ) {
                            // Plant name & scientific name
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

                            Spacer(modifier = Modifier.height(20.dp))

                            // Mandarin pronunciation pod
                            if (!plant.nameMandarin.isNullOrBlank() || !plant.pinyin.isNullOrBlank()) {
                                MandarinPod(
                                    hanzi = plant.nameMandarin.orEmpty(),
                                    pinyin = plant.pinyin.orEmpty(),
                                    audioState = uiState.audioState,
                                    hasAudio = !plant.mandarinAudioUrl.isNullOrBlank(),
                                    onPlayAudio = { viewModel.playMandarinAudio() }
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                            }

                            // Educational guide section with mascot
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Kenalan lebih dekat 🌱",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = PrimaryGreenDark
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Panduan botani & perawatan",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary
                                    )
                                }
                                if (!isLessonCompleted) {
                                    Mascot(
                                        type = MascotType.LEARNING,
                                        size = 100.dp,
                                        baseRotation = -2f,
                                        animateIdle = true
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = plant.description.ifBlank {
                                    "Tanaman ini merupakan salah satu flora unggulan yang dibudidayakan di kebun binaan Kelurahan Bubakan untuk penghijauan serta pemenuhan tanaman obat keluarga (TOGA) mandiri."
                                },
                                style = MaterialTheme.typography.bodyLarge,
                                lineHeight = 24.sp,
                                color = TextPrimary
                            )

                            if (plant.characteristics.isNotBlank()) {
                                Spacer(modifier = Modifier.height(16.dp))
                                BotanicalInfoCard(
                                    title = "Karakteristik Tanaman",
                                    icon = "🔍",
                                    content = plant.characteristics
                                )
                            }

                            val usefulBenefits = plant.commonUses.ifBlank { plant.benefits }
                            if (usefulBenefits.isNotBlank()) {
                                Spacer(modifier = Modifier.height(16.dp))
                                BotanicalInfoCard(
                                    title = "Khasiat & Kegunaan",
                                    icon = "✨",
                                    content = usefulBenefits
                                )
                            }

                            val cultivation = plant.cultivationNotes.ifBlank { plant.plantingGuide }
                            if (cultivation.isNotBlank()) {
                                Spacer(modifier = Modifier.height(16.dp))
                                BotanicalInfoCard(
                                    title = "Panduan Budidaya",
                                    icon = "🌱",
                                    content = cultivation
                                )
                            }

                            if (!plant.imageAuthor.isNullOrBlank() || !plant.imageLicense.isNullOrBlank() || plant.sourceReferences.isNotBlank()) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(id.bubakangreen.app.ui.theme.PrimaryContainerMint.copy(alpha = 0.4f))
                                        .padding(14.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = "Sumber & Lisensi",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = PrimaryGreenDark
                                        )
                                        if (!plant.imageAuthor.isNullOrBlank() || !plant.imageLicense.isNullOrBlank()) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "Foto: ${plant.imageAuthor.orEmpty()} (${plant.imageLicense.orEmpty()})",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = TextSecondary
                                            )
                                        }
                                        if (plant.sourceReferences.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = "Taksonomi: ${plant.sourceReferences}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = TextSecondary
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Garden presence
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(PrimaryGreenLight.copy(alpha = 0.5f))
                                    .padding(16.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "📍", fontSize = 24.sp)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "Di mana kamu bisa menemukannya?",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = PrimaryGreenDark
                                        )
                                        Text(
                                            text = "Dapat ditemukan dan dipelajari langsung di Taman Toga RW 03 dan Urban Farming Bubakan.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(28.dp))

                            // Completion state celebration
                            if (isLessonCompleted) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Mascot(
                                        type = MascotType.HAPPY,
                                        size = 160.dp,
                                        animateIdle = true
                                    )
                                    Spacer(modifier = Modifier.height(14.dp))
                                    Text(
                                        text = "Hebat! Kamu sudah mengenal tanaman ini 🎉",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = PrimaryGreenDark,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Terima kasih sudah belajar bersama Bubakan Green 🌱",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextSecondary,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            } else {
                                Text(
                                    text = "💡 Ketuk tombol di bawah setelah selesai mempelajari tanaman ini.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))
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

    if (showQrDialog && uiState.plant is UiState.Success) {
        val currentPlant = (uiState.plant as UiState.Success).data
        val canonicalUrl = remember(currentPlant.id) {
            id.bubakangreen.app.core.util.QrUrlBuilder.buildPlantUrl(currentPlant.id)
        }
        id.bubakangreen.app.ui.components.QrCodeDisplayDialog(
            title = currentPlant.nameId,
            subtitle = currentPlant.nameLatin,
            canonicalUrl = canonicalUrl,
            onDismiss = { showQrDialog = false }
        )
    }
}

/**
 * Mandarin pronunciation pod — clean, teal-accented.
 */
@Composable
private fun MandarinPod(
    hanzi: String,
    pinyin: String,
    audioState: AudioState,
    hasAudio: Boolean,
    onPlayAudio: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AquaAccent.copy(alpha = 0.1f))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "PELAFALAN MANDARIN",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = PrimaryGreenDark,
                    letterSpacing = 0.5.sp
                )
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

@Composable
private fun BotanicalInfoCard(
    title: String,
    icon: String,
    content: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = icon, fontSize = 20.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryGreenDark
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp,
                color = TextPrimary
            )
        }
    }
}
