package id.bubakangreen.app.ui.catalog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import id.bubakangreen.app.core.audio.AudioState
import id.bubakangreen.app.ui.common.UiState
import id.bubakangreen.app.ui.components.BubaMascot
import id.bubakangreen.app.ui.components.BubaState
import id.bubakangreen.app.ui.components.BubakanTopBar
import id.bubakangreen.app.ui.components.MandarinSpeakerButton
import id.bubakangreen.app.ui.components.OfflineStatusBar
import id.bubakangreen.app.ui.components.ShimmerBox
import id.bubakangreen.app.ui.components.StateEmptyView
import id.bubakangreen.app.ui.components.StateErrorView
import id.bubakangreen.app.ui.theme.AccentDewContainer
import id.bubakangreen.app.ui.theme.AccentDewTeal
import id.bubakangreen.app.ui.theme.AccentSunnyContainer
import id.bubakangreen.app.ui.theme.AccentSunnyGold
import id.bubakangreen.app.ui.theme.BackgroundVanilla
import id.bubakangreen.app.ui.theme.OnAccentGoldDark
import id.bubakangreen.app.ui.theme.OnDewTealDark
import id.bubakangreen.app.ui.theme.OnSurfaceForestDark
import id.bubakangreen.app.ui.theme.OnSurfaceSageMuted
import id.bubakangreen.app.ui.theme.OutlineOrganic
import id.bubakangreen.app.ui.theme.PrimaryContainerMint
import id.bubakangreen.app.ui.theme.PrimaryForestDark
import id.bubakangreen.app.ui.theme.PrimarySeedlingGreen
import id.bubakangreen.app.ui.theme.SecondarySage
import id.bubakangreen.app.ui.theme.SurfaceCardWhite

/**
 * PlantDetailScreen: Interactive Mini Botanical Lesson.
 * Structure:
 * 1. LARGE PLANT IMAGE (organic 28dp bottom curvature)
 * 2. Plant name + Latin scientific name
 * 3. Mandarin Discovery Pod (Hanzi, Pinyin, 🔊 audio with companion listener mascot)
 * 4. "Kenali Tanaman Ini"
 * 5. "Manfaat & Khasiat Herbal"
 * 6. "Yang Perlu Kamu Tahu"
 * 7. "Tanaman Ini Ada di..."
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
                title = "Pelajaran Botani",
                subtitle = "Ensiklopedi Tanaman Bubakan",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack,
                onInfoClick = onInfoClick
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = BackgroundVanilla,
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
                            cornerRadius = 28.dp
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
                    val isAudioPlaying = uiState.audioState is AudioState.Playing

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                    ) {
                        // 1. LARGE PLANT IMAGE (Organic 28dp bottom curvature)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f / 10f)
                                .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
                                .background(PrimaryContainerMint)
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

                        // Lesson Content Container
                        Column(modifier = Modifier.padding(16.dp)) {
                            // 2. NOMENCLATURE: Plant name + Latin name
                            Text(
                                text = plant.nameId,
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = OnSurfaceForestDark
                            )

                            if (plant.nameLatin.isNotBlank()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = plant.nameLatin,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontStyle = FontStyle.Italic,
                                    color = SecondarySage
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // 3. MANDARIN DISCOVERY POD: Hanzi, Pinyin, 🔊 Audio, and Companion Listener Feedback
                            if (!plant.nameMandarin.isNullOrBlank() || !plant.pinyin.isNullOrBlank()) {
                                MandarinLessonPod(
                                    hanzi = plant.nameMandarin.orEmpty(),
                                    pinyin = plant.pinyin.orEmpty(),
                                    audioState = uiState.audioState,
                                    hasAudio = !plant.mandarinAudioUrl.isNullOrBlank(),
                                    isAudioPlaying = isAudioPlaying,
                                    onPlayAudio = { viewModel.playMandarinAudio() }
                                )
                                Spacer(modifier = Modifier.height(20.dp))
                            }

                            // 4. "Kenali Tanaman Ini"
                            LessonSectionCard(
                                title = "Kenali Tanaman Ini",
                                icon = "📖",
                                content = plant.description.ifBlank {
                                    "Tanaman ini merupakan salah satu flora unggulan yang dikembangkan di lingkungan Kelurahan Bubakan untuk apotek hidup mandiri warga."
                                }
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // 5. "Manfaat & Khasiat Herbal"
                            LessonSectionCard(
                                title = "Manfaat & Khasiat Sehat",
                                icon = "🌿",
                                content = plant.description
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // 6. "Yang Perlu Kamu Tahu" (Edukasi Perawatan & Budidaya)
                            LessonSectionCard(
                                title = "Yang Perlu Kamu Tahu",
                                icon = "💡",
                                content = "Tanaman ini dirawat secara alami oleh warga Bubakan dengan pupuk kompos organik tanpa pestisida kimia untuk menjaga kemurnian khasiat herbal."
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // 7. "Tanaman Ini Ada di..." (Koneksi ke Kebun Nyata Bubakan)
                            GardenPresenceCard()

                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }
                }

                is UiState.Empty -> {
                    StateEmptyView(
                        title = "Tanaman Tidak Ditemukan",
                        message = plantState.message,
                        actionLabel = "Kembali ke Katalog",
                        onActionClick = onNavigateBack
                    )
                }

                is UiState.Error -> {
                    StateErrorView(
                        message = plantState.message,
                        onRetry = { viewModel.loadPlantDetail(plantId) }
                    )
                }
            }
        }
    }
}

/**
 * Mandarin interactive pronunciation pod with Hanzi, Pinyin, audio playback,
 * and live listener mascot reaction.
 */
@Composable
private fun MandarinLessonPod(
    hanzi: String,
    pinyin: String,
    audioState: AudioState,
    hasAudio: Boolean,
    isAudioPlaying: Boolean,
    onPlayAudio: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = AccentDewContainer),
        border = BorderStroke(1.5.dp, AccentDewTeal.copy(alpha = 0.5f)),
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
                    color = AccentDewTeal.copy(alpha = 0.18f)
                ) {
                    Text(
                        text = "PELAFALAN BAHASA MANDARIN",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = OnDewTealDark,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.Bottom) {
                    if (hanzi.isNotBlank()) {
                        Text(
                            text = hanzi,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = OnDewTealDark
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                    }
                    if (pinyin.isNotBlank()) {
                        Text(
                            text = pinyin,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SecondarySage
                        )
                    }
                }
            }

            // Interactive Companion Mascot Reaction while audio is playing
            if (isAudioPlaying) {
                BubaMascot(
                    state = BubaState.LISTENING,
                    size = 58.dp,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }

            MandarinSpeakerButton(
                audioState = audioState,
                onClick = onPlayAudio,
                enabled = hasAudio
            )
        }
    }
}

/**
 * Bite-sized modular lesson card.
 */
@Composable
private fun LessonSectionCard(
    title: String,
    icon: String,
    content: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCardWhite),
        border = BorderStroke(1.5.dp, OutlineOrganic),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = icon, fontSize = 22.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = OnSurfaceForestDark
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = content,
                style = MaterialTheme.typography.bodyLarge,
                color = OnSurfaceForestDark,
                lineHeight = 23.sp
            )
        }
    }
}

/**
 * "Tanaman Ini Ada di..." Physical Garden Presence Card.
 */
@Composable
private fun GardenPresenceCard(
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = AccentSunnyContainer),
        border = BorderStroke(1.5.dp, AccentSunnyGold.copy(alpha = 0.45f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "📍", fontSize = 22.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Tanaman Ini Ada di Kebun Bubakan",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = OnAccentGoldDark
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Dapat ditemukan dan dipelajari langsung di Taman Toga RW 03 dan Kebun Percontohan Kelurahan Bubakan.",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF3D2A00),
                lineHeight = 20.sp
            )
        }
    }
}
