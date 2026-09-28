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
import id.bubakangreen.app.ui.theme.BackgroundVanilla
import id.bubakangreen.app.ui.theme.OnDewTealDark
import id.bubakangreen.app.ui.theme.OnSurfaceForestDark
import id.bubakangreen.app.ui.theme.OnSurfaceSageMuted
import id.bubakangreen.app.ui.theme.OutlineOrganic
import id.bubakangreen.app.ui.theme.PrimaryContainerMint
import id.bubakangreen.app.ui.theme.PrimaryForestDark
import id.bubakangreen.app.ui.theme.SecondarySage
import id.bubakangreen.app.ui.theme.SurfaceCardWhite

/**
 * PlantDetailScreen: Bite-sized Botanical Lesson.
 * Combines high-resolution photography, scientific Latin binomials,
 * Mandarin Hanzi/Pinyin with interactive audio listener feedback, and herbal benefits.
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
                title = "Detail Tanaman",
                subtitle = "Ensiklopedi Botani Bubakan",
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
                                .height(240.dp),
                            cornerRadius = 24.dp
                        )
                        ShimmerBox(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(88.dp),
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
                        // Hero Photograph with rounded bottom corners (16:10)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f / 10f)
                                .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
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

                        // Botanical Content Lesson
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Section 1: Nomenclature Header
                            Text(
                                text = plant.nameId,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
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

                            Spacer(modifier = Modifier.height(18.dp))

                            // Section 2: Mandarin Discovery Pod (with Audio & Mascot Listener Reaction)
                            if (!plant.nameMandarin.isNullOrBlank() || !plant.pinyin.isNullOrBlank()) {
                                MandarinDiscoveryPod(
                                    hanzi = plant.nameMandarin.orEmpty(),
                                    pinyin = plant.pinyin.orEmpty(),
                                    audioState = uiState.audioState,
                                    hasAudio = !plant.mandarinAudioUrl.isNullOrBlank(),
                                    isAudioPlaying = isAudioPlaying,
                                    onPlayAudio = { viewModel.playMandarinAudio() }
                                )
                                Spacer(modifier = Modifier.height(20.dp))
                            }

                            // Section 3: Khasiat & Manfaat Herbal
                            BotanicalSectionCard(
                                title = "Khasiat & Manfaat Sehat",
                                icon = "🌿",
                                content = plant.description
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Section 4: Informasi Budidaya di Bubakan
                            BotanicalSectionCard(
                                title = "Budidaya di Kebun Bubakan",
                                icon = "🌱",
                                content = "Tanaman ini dirawat oleh warga Kelurahan Bubakan sebagai bagian dari program ketahanan pangan mandiri dan apotek hidup keluarga."
                            )

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
 * Mandarin interactive learning pod with Hanzi, Pinyin, audio playback,
 * and playful listener mascot reaction.
 */
@Composable
private fun MandarinDiscoveryPod(
    hanzi: String,
    pinyin: String,
    audioState: AudioState,
    hasAudio: Boolean,
    isAudioPlaying: Boolean,
    onPlayAudio: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = AccentDewContainer),
        border = BorderStroke(1.5.dp, AccentDewTeal.copy(alpha = 0.45f)),
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
                    color = AccentDewTeal.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "BELAJAR NAMA MANDARIN",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = OnDewTealDark,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.Bottom) {
                    if (hanzi.isNotBlank()) {
                        Text(
                            text = hanzi,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = OnDewTealDark
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                    }
                    if (pinyin.isNotBlank()) {
                        Text(
                            text = pinyin,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = SecondarySage
                        )
                    }
                }
            }

            // If audio is playing, companion mascot perks up into listening mode!
            if (isAudioPlaying) {
                BubaMascot(
                    state = BubaState.LISTENING,
                    size = 56.dp,
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
 * Tactile botanical lesson content card.
 */
@Composable
private fun BotanicalSectionCard(
    title: String,
    icon: String,
    content: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCardWhite),
        border = BorderStroke(1.5.dp, OutlineOrganic),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = icon, fontSize = 20.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = OnSurfaceForestDark
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = content,
                style = MaterialTheme.typography.bodyLarge,
                color = OnSurfaceForestDark,
                lineHeight = 24.sp
            )
        }
    }
}
