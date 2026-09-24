package id.bubakangreen.app.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import id.bubakangreen.app.domain.model.Location
import id.bubakangreen.app.domain.model.LocationType
import id.bubakangreen.app.domain.model.MasterPlant
import id.bubakangreen.app.ui.common.UiState
import id.bubakangreen.app.ui.components.BubakanTopBar
import id.bubakangreen.app.ui.components.FeaturedLocationBanner
import id.bubakangreen.app.ui.components.OfflineStatusBar
import id.bubakangreen.app.ui.components.PlantCard
import id.bubakangreen.app.ui.components.ShimmerBox
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
 * HomeScreen (SCR-PUB-01): The primary public welcoming screen.
 * Displays official Kelurahan identity, featured gardens, category entry points, and botanical highlights.
 */
@Composable
fun HomeScreen(
    onLocationClick: (String) -> Unit,
    onPlantClick: (String) -> Unit,
    onNavigateToLocations: (LocationType?) -> Unit,
    onNavigateToCatalog: () -> Unit,
    onInfoClick: () -> Unit,
    onLoginClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            BubakanTopBar(
                title = "BUBAKAN GREEN",
                subtitle = "Kelurahan Bubakan, Mijen",
                canNavigateBack = false,
                onInfoClick = onInfoClick,
                onLoginClick = onLoginClick
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

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Greeting & Subtitle
                Text(
                    text = "Selamat Datang di Bubakan Green",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = OnSurfaceDark
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Sistem Informasi Urban Farming & Taman Toga Kelurahan Bubakan",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Section 1: Featured Location Hero
                when (val featured = uiState.featuredLocations) {
                    is UiState.Loading -> {
                        ShimmerBox(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp),
                            cornerRadius = 16.dp
                        )
                    }
                    is UiState.Success -> {
                        val primaryFeatured = featured.data.firstOrNull()
                        if (primaryFeatured != null) {
                            FeaturedLocationBanner(
                                location = primaryFeatured,
                                onClick = { onLocationClick(primaryFeatured.id) }
                            )
                        }
                    }
                    is UiState.Empty -> {
                        // Clean fallback if no location is marked featured
                    }
                    is UiState.Error -> {
                        StateErrorView(
                            message = featured.message,
                            onRetry = { viewModel.loadData() }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Section 2: Quick Category Access
                Text(
                    text = "Kategori Program",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = OnSurfaceDark
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CategoryHighlightCard(
                        title = "Urban Farming",
                        subtitle = "Budidaya Pangan Mandiri",
                        icon = "🌱",
                        onClick = { onNavigateToLocations(LocationType.URBAN_FARMING) },
                        modifier = Modifier.weight(1f)
                    )
                    CategoryHighlightCard(
                        title = "Taman Toga",
                        subtitle = "Tanaman Obat Keluarga",
                        icon = "🌿",
                        onClick = { onNavigateToLocations(LocationType.TAMAN_TOGA) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Section 3: Popular Botanical Knowledge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Koleksi Tanaman Unggulan",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = OnSurfaceDark
                    )
                    Text(
                        text = "Lihat Semua →",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryForest,
                        modifier = Modifier.clickable { onNavigateToCatalog() }
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))

                when (val plants = uiState.popularPlants) {
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
                            plants.data.forEach { plant ->
                                PlantCard(
                                    plant = plant,
                                    onClick = { onPlantClick(plant.id) }
                                )
                            }
                        }
                    }
                    is UiState.Empty -> {
                        // Empty state handled gracefully
                    }
                    is UiState.Error -> {
                        Text(
                            text = plants.message,
                            style = MaterialTheme.typography.bodySmall ?: MaterialTheme.typography.bodyMedium,
                            color = OnSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Section 4: About Bubakan Green Community Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PrimaryContainerMint.copy(alpha = 0.5f)),
                    border = BorderStroke(1.dp, OutlineGrey),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onInfoClick() }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Tentang Program Bubakan Green",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = OnSurfaceDark
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Inisiatif ketahanan pangan dan apotek hidup mandiri warga Kelurahan Bubakan, Kecamatan Mijen, Kota Semarang.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Pelajari Selengkapnya →",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryForest
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun CategoryHighlightCard(
    title: String,
    subtitle: String,
    icon: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        border = BorderStroke(1.dp, OutlineGrey),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = icon, style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = OnSurfaceDark
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = OnSurfaceVariant
            )
        }
    }
}
