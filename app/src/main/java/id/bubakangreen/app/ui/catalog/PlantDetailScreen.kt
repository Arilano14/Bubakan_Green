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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import id.bubakangreen.app.core.audio.AudioState
import id.bubakangreen.app.ui.common.UiState
import id.bubakangreen.app.ui.components.BubakanTopBar
import id.bubakangreen.app.ui.components.MandarinSpeakerButton
import id.bubakangreen.app.ui.components.OfflineStatusBar
import id.bubakangreen.app.ui.components.ShimmerBox
import id.bubakangreen.app.ui.components.StateEmptyView
import id.bubakangreen.app.ui.components.StateErrorView
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
 * PlantDetailScreen (SCR-PUB-05): Authoritative botanical encyclopedia card.
 * Integrates Indonesian common name, italic scientific Latin, Mandarin Hanzi/Pinyin,
 * user-triggered audio pronunciation, and medicinal benefit breakdown.
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
        containerColor = BackgroundLight,
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
                            cornerRadius = 16.dp
                        )
                        ShimmerBox(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp),
                            cornerRadius = 16.dp
                        )
                        ShimmerBox(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            cornerRadius = 16.dp
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
                        // Hero Photograph (16:10)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f / 10f)
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

                        // Botanical Identity & Nomenclature
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = plant.nameId,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = OnSurfaceDark
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

                            Spacer(modifier = Modifier.height(16.dp))

                            // Mandarin Knowledge & Pronunciation Card
                            if (!plant.nameMandarin.isNullOrBlank() || !plant.pinyin.isNullOrBlank()) {
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                                    border = BorderStroke(1.dp, OutlineGrey),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "Pelafalan Bahasa Mandarin:",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = OnSurfaceVariant
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Row(verticalAlignment = Alignment.Bottom) {
                                                if (!plant.nameMandarin.isNullOrBlank()) {
                                                    Text(
                                                        text = plant.nameMandarin,
                                                        fontSize = 26.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = PrimaryForest
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                }
                                                if (!plant.pinyin.isNullOrBlank()) {
                                                    Text(
                                                        text = plant.pinyin,
                                                        style = MaterialTheme.typography.titleSmall,
                                                        color = SecondarySage
                                                    )
                                                }
                                            }
                                        }

                                        MandarinSpeakerButton(
                                            audioState = uiState.audioState,
                                            onClick = { viewModel.playMandarinAudio() },
                                            enabled = !plant.mandarinAudioUrl.isNullOrBlank()
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(20.dp))
                            }

                            // Knowledge Section 1: Manfaat & Khasiat
                            KnowledgeSectionCard(
                                title = "Khasiat & Manfaat Herbal",
                                icon = "🌿",
                                content = plant.description
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Knowledge Section 2: Edukasi Budidaya
                            KnowledgeSectionCard(
                                title = "Informasi Budidaya & Tanaman",
                                icon = "🌱",
                                content = "Tanaman ini dibudidayakan di kebun percontohan Kelurahan Bubakan sebagai bagian dari program ketahanan pangan mandiri dan apotek hidup keluarga."
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

@Composable
private fun KnowledgeSectionCard(
    title: String,
    icon: String,
    content: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        border = BorderStroke(1.dp, OutlineGrey),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = icon, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = OnSurfaceDark
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = content,
                style = MaterialTheme.typography.bodyLarge,
                color = OnSurfaceDark,
                lineHeight = 22.sp
            )
        }
    }
}
