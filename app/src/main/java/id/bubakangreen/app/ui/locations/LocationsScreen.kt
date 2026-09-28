package id.bubakangreen.app.ui.locations

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import id.bubakangreen.app.domain.model.Location
import id.bubakangreen.app.domain.model.LocationType
import id.bubakangreen.app.ui.common.UiState
import id.bubakangreen.app.ui.components.BubakanTopBar
import id.bubakangreen.app.ui.components.CategoryFilterChip
import id.bubakangreen.app.ui.components.LocationCard
import id.bubakangreen.app.ui.components.OfflineStatusBar
import id.bubakangreen.app.ui.components.ShimmerBox
import id.bubakangreen.app.ui.components.StateEmptyView
import id.bubakangreen.app.ui.components.StateErrorView
import id.bubakangreen.app.ui.components.TactileButton
import id.bubakangreen.app.ui.components.TactileButtonStyle
import id.bubakangreen.app.ui.theme.AccentSunnyContainer
import id.bubakangreen.app.ui.theme.AccentSunnyGold
import id.bubakangreen.app.ui.theme.BackgroundVanilla
import id.bubakangreen.app.ui.theme.OnSurfaceForestDark
import id.bubakangreen.app.ui.theme.OnSurfaceSageMuted
import id.bubakangreen.app.ui.theme.OutlineOrganic
import id.bubakangreen.app.ui.theme.PrimaryContainerMint
import id.bubakangreen.app.ui.theme.PrimaryForestDark
import id.bubakangreen.app.ui.theme.PrimarySeedlingGreen
import id.bubakangreen.app.ui.theme.SecondarySage
import id.bubakangreen.app.ui.theme.SurfaceCardWhite

/**
 * LocationsScreen: "Jelajahi Lokasi Tanaman di Bubakan".
 * Field exploration guide highlighting flagship garden hubs (Urban Farming Kelurahan & Taman Toga RW 03).
 */
@Composable
fun LocationsScreen(
    onLocationClick: (String) -> Unit,
    onInfoClick: () -> Unit,
    modifier: Modifier = Modifier,
    initialCategory: LocationType? = null,
    viewModel: LocationsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(initialCategory) {
        if (initialCategory != null) {
            viewModel.setCategoryFilter(initialCategory)
        }
    }

    Scaffold(
        topBar = {
            BubakanTopBar(
                title = "Jelajah Kebun",
                subtitle = "Persebaran Kebun di Bubakan",
                canNavigateBack = false,
                onInfoClick = onInfoClick
            )
        },
        containerColor = BackgroundVanilla,
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            OfflineStatusBar(isOffline = uiState.isOffline)

            // Segmented Switch: [ Daftar Kebun ] | [ Peta Persebaran ]
            Surface(
                color = SurfaceCardWhite,
                border = BorderStroke(1.dp, OutlineOrganic)
            ) {
                TabRow(
                    selectedTabIndex = uiState.viewMode.ordinal,
                    containerColor = SurfaceCardWhite,
                    contentColor = PrimarySeedlingGreen,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[uiState.viewMode.ordinal]),
                            color = PrimarySeedlingGreen
                        )
                    }
                ) {
                    Tab(
                        selected = uiState.viewMode == ViewMode.LIST,
                        onClick = { viewModel.setViewMode(ViewMode.LIST) },
                        text = {
                            Text(
                                text = "Daftar Kebun",
                                fontWeight = if (uiState.viewMode == ViewMode.LIST) FontWeight.ExtraBold else FontWeight.Medium
                            )
                        }
                    )
                    Tab(
                        selected = uiState.viewMode == ViewMode.MAP,
                        onClick = { viewModel.setViewMode(ViewMode.MAP) },
                        text = {
                            Text(
                                text = "Peta Sebaran",
                                fontWeight = if (uiState.viewMode == ViewMode.MAP) FontWeight.ExtraBold else FontWeight.Medium
                            )
                        }
                    )
                }
            }

            // Category Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CategoryFilterChip(
                    label = "Semua Kebun",
                    selected = uiState.selectedType == null,
                    onClick = { viewModel.setCategoryFilter(null) }
                )
                CategoryFilterChip(
                    label = "🌱 Urban Farming",
                    selected = uiState.selectedType == LocationType.URBAN_FARMING,
                    onClick = { viewModel.setCategoryFilter(LocationType.URBAN_FARMING) }
                )
                CategoryFilterChip(
                    label = "🌿 Taman Toga",
                    selected = uiState.selectedType == LocationType.TAMAN_TOGA,
                    onClick = { viewModel.setCategoryFilter(LocationType.TAMAN_TOGA) }
                )
            }

            // Content Area (List vs Map)
            when (val locationsState = uiState.locations) {
                is UiState.Loading -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        repeat(3) {
                            ShimmerBox(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                cornerRadius = 22.dp
                            )
                        }
                    }
                }

                is UiState.Success -> {
                    if (uiState.viewMode == ViewMode.LIST) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                start = 16.dp,
                                end = 16.dp,
                                bottom = 24.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(locationsState.data, key = { it.id }) { location ->
                                LocationCard(
                                    location = location,
                                    onClick = { onLocationClick(location.id) }
                                )
                            }
                        }
                    } else {
                        // Map Mode (Agnostic Visual Container + Floating Card)
                        MapVisualContainer(
                            locations = locationsState.data,
                            selectedLocation = uiState.selectedMapLocation,
                            onSelectLocation = { viewModel.selectMapLocation(it) },
                            onOpenDetail = { onLocationClick(it.id) },
                            onOpenExternalMap = { launchGoogleMaps(context, it) }
                        )
                    }
                }

                is UiState.Empty -> {
                    StateEmptyView(
                        title = "Belum Ada Lokasi",
                        message = locationsState.message,
                        actionLabel = "Tampilkan Semua",
                        onActionClick = { viewModel.setCategoryFilter(null) }
                    )
                }

                is UiState.Error -> {
                    StateErrorView(
                        message = locationsState.message,
                        onRetry = { viewModel.loadLocations() }
                    )
                }
            }
        }
    }
}

/**
 * Provider-agnostic visual map container.
 * Displays garden pins across Kelurahan Bubakan and an elevated preview card.
 */
@Composable
private fun MapVisualContainer(
    locations: List<Location>,
    selectedLocation: Location?,
    onSelectLocation: (Location) -> Unit,
    onOpenDetail: (Location) -> Unit,
    onOpenExternalMap: (Location) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PrimaryContainerMint.copy(alpha = 0.25f))
    ) {
        // Pins Column / Visual Garden List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Text(
                    text = "Pilih Titik Kebun di Wilayah Bubakan:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = OnSurfaceForestDark,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            items(locations, key = { it.id }) { loc ->
                val isSelected = loc.id == selectedLocation?.id
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) PrimaryContainerMint else SurfaceCardWhite
                    ),
                    border = BorderStroke(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) PrimarySeedlingGreen else OutlineOrganic
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectLocation(loc) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(if (loc.type == LocationType.URBAN_FARMING) PrimaryContainerMint else AccentSunnyContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (loc.type == LocationType.URBAN_FARMING) "🌱" else "🌿",
                                fontSize = 22.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = loc.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = OnSurfaceForestDark,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "RW ${loc.rw} • ${loc.address}",
                                style = MaterialTheme.typography.labelSmall,
                                color = OnSurfaceSageMuted,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            // Bottom space for floating card clearance
            item {
                Spacer(modifier = Modifier.height(240.dp))
            }
        }

        // Floating Garden Preview Card
        if (selectedLocation != null) {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCardWhite),
                border = BorderStroke(1.5.dp, OutlineOrganic),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = selectedLocation.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = OnSurfaceForestDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "RW ${selectedLocation.rw} • ${selectedLocation.address}",
                        style = MaterialTheme.typography.bodySmall ?: MaterialTheme.typography.bodyMedium,
                        color = OnSurfaceSageMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        TactileButton(
                            text = "📍 Buka Maps",
                            onClick = { onOpenExternalMap(selectedLocation) },
                            style = TactileButtonStyle.SECONDARY,
                            modifier = Modifier.weight(1f)
                        )
                        TactileButton(
                            text = "Detail Kebun →",
                            onClick = { onOpenDetail(selectedLocation) },
                            style = TactileButtonStyle.PRIMARY,
                            modifier = Modifier.weight(1.2f)
                        )
                    }
                }
            }
        }
    }
}

private fun launchGoogleMaps(context: Context, location: Location) {
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
