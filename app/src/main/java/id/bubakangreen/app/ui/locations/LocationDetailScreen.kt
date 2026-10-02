package id.bubakangreen.app.ui.locations

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import id.bubakangreen.app.domain.model.CoordinatesStatus
import id.bubakangreen.app.domain.model.Location
import id.bubakangreen.app.domain.model.LocationType
import id.bubakangreen.app.ui.common.UiState
import id.bubakangreen.app.ui.components.BubakanTopBar
import id.bubakangreen.app.ui.components.OfflineStatusBar
import id.bubakangreen.app.ui.components.PlantCard
import id.bubakangreen.app.ui.components.ShimmerBox
import id.bubakangreen.app.ui.components.StateEmptyView
import id.bubakangreen.app.ui.components.StateErrorView
import id.bubakangreen.app.ui.components.TactileButton
import id.bubakangreen.app.ui.components.TactileButtonStyle
import id.bubakangreen.app.ui.theme.AccentSunnyContainer
import id.bubakangreen.app.ui.theme.AccentSunnyGold
import id.bubakangreen.app.ui.theme.BackgroundVanilla
import id.bubakangreen.app.ui.theme.OnAccentGoldDark
import id.bubakangreen.app.ui.theme.OnPrimaryWhite
import id.bubakangreen.app.ui.theme.OnSurfaceForestDark
import id.bubakangreen.app.ui.theme.OnSurfaceSageMuted
import id.bubakangreen.app.ui.theme.OutlineOrganic
import id.bubakangreen.app.ui.theme.PrimaryContainerMint
import id.bubakangreen.app.ui.theme.PrimaryForestDark
import id.bubakangreen.app.ui.theme.PrimarySeedlingGreen
import id.bubakangreen.app.ui.theme.SecondarySage
import id.bubakangreen.app.ui.theme.SurfaceCardWhite

/**
 * LocationDetailScreen: Detailed Garden Exploration Profile.
 * Displays garden identity, RW context, verified GPS coordinates, directions, and botanical inventory.
 */
@Composable
fun LocationDetailScreen(
    locationId: String,
    onNavigateBack: () -> Unit,
    onPlantClick: (String) -> Unit,
    onInfoClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LocationDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var showQrDialog by remember { mutableStateOf(false) }

    LaunchedEffect(locationId) {
        viewModel.loadLocationDetail(locationId)
    }

    val currentLocation = (uiState.location as? UiState.Success)?.data

    Scaffold(
        topBar = {
            BubakanTopBar(
                title = "Profil Kebun",
                subtitle = "Kelurahan Bubakan",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack,
                onInfoClick = onInfoClick,
                onQrClick = if (currentLocation?.isPublished == true) {
                    { showQrDialog = true }
                } else null
            )
        },
        containerColor = id.bubakangreen.app.ui.theme.BackgroundWarm,
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            OfflineStatusBar(isOffline = uiState.isOffline)

            when (val locState = uiState.location) {
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
                                .height(220.dp),
                            cornerRadius = 24.dp
                        )
                        ShimmerBox(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            cornerRadius = 20.dp
                        )
                    }
                }

                is UiState.Success -> {
                    val location = locState.data
                    val isUrbanFarming = location.type == LocationType.URBAN_FARMING

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                    ) {
                        // Hero Photograph with rounded bottom corners (16:9)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f / 9f)
                                .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                                .background(if (isUrbanFarming) PrimaryContainerMint else AccentSunnyContainer)
                        ) {
                            if (!location.photoUrl.isNullOrBlank()) {
                                AsyncImage(
                                    model = location.photoUrl,
                                    contentDescription = "Foto ${location.name}",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (isUrbanFarming) "🌱 Urban Farming" else "🌿 Taman Toga",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isUrbanFarming) PrimarySeedlingGreen else OnAccentGoldDark
                                    )
                                }
                            }

                            // Category badge
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isUrbanFarming) PrimarySeedlingGreen else AccentSunnyGold,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = if (isUrbanFarming) "🌱 Urban Farming" else "🌿 Taman Toga",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isUrbanFarming) OnPrimaryWhite else androidx.compose.ui.graphics.Color(0xFF2E1C00),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }

                        // Garden Identity Block
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = location.name,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = OnSurfaceForestDark,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = PrimaryContainerMint,
                                    border = BorderStroke(1.dp, PrimarySeedlingGreen.copy(alpha = 0.25f))
                                ) {
                                    Text(
                                        text = "RW ${location.rw}",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryForestDark,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "📍 ${location.address}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = OnSurfaceSageMuted
                            )

                            if (location.description.isNotBlank()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = location.description,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = OnSurfaceForestDark
                                )
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            // Coordinates & Map Direction CTA
                            Card(
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = SurfaceCardWhite),
                                border = BorderStroke(1.5.dp, OutlineOrganic),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Titik Koordinat:",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = OnSurfaceForestDark
                                        )
                                        Text(
                                            text = if (location.coordinatesStatus == CoordinatesStatus.VERIFIED) "✅ Terverifikasi" else "⏳ Belum Valid",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (location.coordinatesStatus == CoordinatesStatus.VERIFIED) PrimarySeedlingGreen else SecondarySage
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${location.latitude}, ${location.longitude}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = OnSurfaceSageMuted
                                    )
                                    Spacer(modifier = Modifier.height(14.dp))
                                    TactileButton(
                                        text = "📍 Petunjuk Arah ke Kebun Ini",
                                        onClick = { launchExternalMap(context, location) },
                                        style = TactileButtonStyle.PRIMARY,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Section: Plants at this location
                            Text(
                                text = "Koleksi Tanaman di Kebun Ini",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = OnSurfaceForestDark
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            when (val plantsState = uiState.plants) {
                                is UiState.Loading -> {
                                    repeat(2) {
                                        ShimmerBox(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(104.dp)
                                                .padding(vertical = 4.dp),
                                            cornerRadius = 20.dp
                                        )
                                    }
                                }
                                is UiState.Success -> {
                                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                        plantsState.data.forEach { plant ->
                                            PlantCard(
                                                plant = plant,
                                                onClick = { onPlantClick(plant.id) }
                                            )
                                        }
                                    }
                                }
                                is UiState.Empty -> {
                                    Text(
                                        text = plantsState.message,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = OnSurfaceSageMuted,
                                        modifier = Modifier.padding(vertical = 12.dp)
                                    )
                                }
                                is UiState.Error -> {
                                    Text(
                                        text = plantsState.message,
                                        style = MaterialTheme.typography.bodySmall ?: MaterialTheme.typography.bodyMedium,
                                        color = OnSurfaceSageMuted
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(32.dp).navigationBarsPadding())
                        }
                    }
                }

                is UiState.Empty -> {
                    StateEmptyView(
                        title = "Kebun Tidak Ditemukan",
                        message = locState.message,
                        actionLabel = "Kembali ke Daftar",
                        onActionClick = onNavigateBack
                    )
                }

                is UiState.Error -> {
                    StateErrorView(
                        message = locState.message,
                        onRetry = { viewModel.loadLocationDetail(locationId) }
                    )
                }
            }
        }
    }

    if (showQrDialog && currentLocation != null) {
        val canonicalUrl = remember(currentLocation.id) {
            id.bubakangreen.app.core.util.QrUrlBuilder.buildLocationUrl(currentLocation.id)
        }
        id.bubakangreen.app.ui.components.QrCodeDisplayDialog(
            title = currentLocation.name,
            subtitle = "RW ${currentLocation.rw} • Kelurahan Bubakan",
            canonicalUrl = canonicalUrl,
            onDismiss = { showQrDialog = false }
        )
    }
}

private fun launchExternalMap(context: Context, location: Location) {
    try {
        val geoUri = Uri.parse("geo:${location.latitude},${location.longitude}?q=${location.latitude},${location.longitude}(${Uri.encode(location.name)})")
        val intent = Intent(Intent.ACTION_VIEW, geoUri).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    } catch (_: Exception) {
        val browserUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=${location.latitude},${location.longitude}")
        val browserIntent = Intent(Intent.ACTION_VIEW, browserUri).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(browserIntent)
    }
}
