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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import id.bubakangreen.app.ui.theme.BackgroundLight
import id.bubakangreen.app.ui.theme.OnPrimaryContainerDark
import id.bubakangreen.app.ui.theme.OnPrimaryWhite
import id.bubakangreen.app.ui.theme.OnSurfaceDark
import id.bubakangreen.app.ui.theme.OnSurfaceVariant
import id.bubakangreen.app.ui.theme.OutlineGrey
import id.bubakangreen.app.ui.theme.PrimaryContainerMint
import id.bubakangreen.app.ui.theme.PrimaryForest
import id.bubakangreen.app.ui.theme.SecondarySage
import id.bubakangreen.app.ui.theme.SurfaceWhite

/**
 * LocationDetailScreen (SCR-PUB-03): Comprehensive profile of a physical garden.
 * Displays garden identity, RW context, GPS coordinates, external directions CTA, and on-site botanical inventory.
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

    LaunchedEffect(locationId) {
        viewModel.loadLocationDetail(locationId)
    }

    Scaffold(
        topBar = {
            BubakanTopBar(
                title = "Profil Kebun",
                subtitle = "Kelurahan Bubakan",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack,
                onInfoClick = onInfoClick
            )
        },
        containerColor = BackgroundLight,
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
                            cornerRadius = 16.dp
                        )
                        ShimmerBox(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            cornerRadius = 16.dp
                        )
                    }
                }

                is UiState.Success -> {
                    val location = locState.data
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                    ) {
                        // Hero Photograph (16:9)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f / 9f)
                                .background(PrimaryContainerMint)
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
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(PrimaryContainerMint),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (location.type == LocationType.URBAN_FARMING) "🌱 Urban Farming" else "🌿 Taman Toga",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = PrimaryForest
                                    )
                                }
                            }

                            // Category badge
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (location.type == LocationType.URBAN_FARMING) PrimaryForest else SecondarySage,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = if (location.type == LocationType.URBAN_FARMING) "Urban Farming" else "Taman Toga",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = OnPrimaryWhite,
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
                                    color = OnSurfaceDark,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = PrimaryContainerMint
                                ) {
                                    Text(
                                        text = "RW ${location.rw}",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = OnPrimaryContainerDark,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "📍 ${location.address}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = OnSurfaceVariant
                            )

                            if (location.description.isNotBlank()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = location.description,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = OnSurfaceDark
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Coordinates & Map Direction CTA
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                                border = BorderStroke(1.dp, OutlineGrey),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Titik Koordinat:",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = OnSurfaceDark
                                        )
                                        Text(
                                            text = if (location.coordinatesStatus == CoordinatesStatus.VERIFIED) "✅ Terverifikasi" else "⏳ Belum Valid",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (location.coordinatesStatus == CoordinatesStatus.VERIFIED) PrimaryForest else SecondarySage
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${location.latitude}, ${location.longitude}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = OnSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Button(
                                        onClick = { launchExternalMap(context, location) },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = PrimaryForest,
                                            contentColor = OnPrimaryWhite
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("📍 Petunjuk Arah ke Kebun Ini", style = MaterialTheme.typography.labelMedium)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Section: Plants at this location
                            Text(
                                text = "Koleksi Tanaman di Kebun Ini",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = OnSurfaceDark
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            when (val plantsState = uiState.plants) {
                                is UiState.Loading -> {
                                    repeat(2) {
                                        ShimmerBox(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(96.dp)
                                                .padding(vertical = 4.dp),
                                            cornerRadius = 16.dp
                                        )
                                    }
                                }
                                is UiState.Success -> {
                                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
                                        color = OnSurfaceVariant,
                                        modifier = Modifier.padding(vertical = 12.dp)
                                    )
                                }
                                is UiState.Error -> {
                                    Text(
                                        text = plantsState.message,
                                        style = MaterialTheme.typography.bodySmall ?: MaterialTheme.typography.bodyMedium,
                                        color = OnSurfaceVariant
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(28.dp))
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
