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
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import id.bubakangreen.app.domain.model.LocationType
import id.bubakangreen.app.ui.common.UiState
import id.bubakangreen.app.ui.components.BubaMascot
import id.bubakangreen.app.ui.components.BubaState
import id.bubakangreen.app.ui.components.BubakanTopBar
import id.bubakangreen.app.ui.components.FeaturedLocationBanner
import id.bubakangreen.app.ui.components.OfflineStatusBar
import id.bubakangreen.app.ui.components.PlantCard
import id.bubakangreen.app.ui.components.ShimmerBox
import id.bubakangreen.app.ui.components.StateErrorView
import id.bubakangreen.app.ui.components.TactileButton
import id.bubakangreen.app.ui.components.TactileButtonStyle
import id.bubakangreen.app.ui.theme.AccentSunnyContainer
import id.bubakangreen.app.ui.theme.AccentSunnyGold
import id.bubakangreen.app.ui.theme.BackgroundVanilla
import id.bubakangreen.app.ui.theme.OnAccentGoldDark
import id.bubakangreen.app.ui.theme.OnSurfaceForestDark
import id.bubakangreen.app.ui.theme.OnSurfaceSageMuted
import id.bubakangreen.app.ui.theme.OutlineOrganic
import id.bubakangreen.app.ui.theme.PrimaryContainerMint
import id.bubakangreen.app.ui.theme.PrimaryForestDark
import id.bubakangreen.app.ui.theme.PrimarySeedlingGreen
import id.bubakangreen.app.ui.theme.SurfaceCardWhite

/**
 * HomeScreen: Botanical Learning & Discovery Hub of Kelurahan Bubakan.
 * Duolingo-inspired playful educational experience with original Bubakan botanical identity.
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
        containerColor = BackgroundVanilla,
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
                // Section 1: Si Buba Companion Greeting Hero Banner
                BubaGreetingBanner()

                Spacer(modifier = Modifier.height(18.dp))

                // Section 2: Flagship Garden Spotlight
                when (val featured = uiState.featuredLocations) {
                    is UiState.Loading -> {
                        ShimmerBox(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp),
                            cornerRadius = 20.dp
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
                        // Handled cleanly
                    }
                    is UiState.Error -> {
                        StateErrorView(
                            message = featured.message,
                            onRetry = { viewModel.loadData() }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Section 3: Exploration Categories (Tactile Chunky Cards)
                Text(
                    text = "Jelajahi Program Kebun",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = OnSurfaceForestDark
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CategoryTactileCard(
                        title = "Urban Farming",
                        subtitle = "Budidaya Pangan",
                        icon = "🌱",
                        accentColor = PrimarySeedlingGreen,
                        containerColor = PrimaryContainerMint,
                        onClick = { onNavigateToLocations(LocationType.URBAN_FARMING) },
                        modifier = Modifier.weight(1f)
                    )
                    CategoryTactileCard(
                        title = "Taman Toga",
                        subtitle = "Tanaman Obat Sehat",
                        icon = "🌿",
                        accentColor = AccentSunnyGold,
                        containerColor = AccentSunnyContainer,
                        onClick = { onNavigateToLocations(LocationType.TAMAN_TOGA) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(22.dp))

                // Section 4: "Tahukah Kamu?" Educational Fact Card
                BotanicalFactCard()

                Spacer(modifier = Modifier.height(24.dp))

                // Section 5: Botanical Highlights (Plant Cards)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Koleksi Tanaman Pilihan",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = OnSurfaceForestDark
                    )
                    Text(
                        text = "Semua Tanaman →",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = PrimarySeedlingGreen,
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
                                    .height(104.dp)
                                    .padding(vertical = 4.dp),
                                cornerRadius = 20.dp
                            )
                        }
                    }
                    is UiState.Success -> {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
                            color = OnSurfaceSageMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Section 6: About Bubakan Green Community Card
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = PrimaryContainerMint.copy(alpha = 0.5f)),
                    border = BorderStroke(1.5.dp, PrimarySeedlingGreen.copy(alpha = 0.2f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onInfoClick() }
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Tentang Bubakan Green",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = OnSurfaceForestDark
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Inisiatif kemandirian pangan dan apotek hidup herbal warga Kelurahan Bubakan, Mijen, Kota Semarang.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnSurfaceSageMuted
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Pelajari Selengkapnya →",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = PrimarySeedlingGreen
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

/**
 * Playful welcoming banner featuring Si Buba, the botanical guide of Kelurahan Bubakan.
 */
@Composable
private fun BubaGreetingBanner() {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCardWhite),
        border = BorderStroke(1.5.dp, OutlineOrganic),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Animated Mascot in Greeting state
            BubaMascot(
                state = BubaState.GREETING,
                size = 72.dp
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PrimaryContainerMint,
                    border = BorderStroke(1.dp, PrimarySeedlingGreen.copy(alpha = 0.25f))
                ) {
                    Text(
                        text = "PANDUAN BOTANI",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryForestDark,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Halo! Yuk, jelajahi tanaman hijau & toga di Bubakan hari ini!",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = OnSurfaceForestDark,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

/**
 * Tactile category card with chunky icon and vibrant accents.
 */
@Composable
private fun CategoryTactileCard(
    title: String,
    subtitle: String,
    icon: String,
    accentColor: Color,
    containerColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCardWhite),
        border = BorderStroke(1.5.dp, OutlineOrganic),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(containerColor),
                contentAlignment = Alignment.Center
            ) {
                Text(text = icon, fontSize = 22.sp)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = OnSurfaceForestDark
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = OnSurfaceSageMuted
            )
        }
    }
}

/**
 * Educational bite-sized botanical trivia card.
 */
@Composable
private fun BotanicalFactCard() {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = AccentSunnyContainer),
        border = BorderStroke(1.5.dp, AccentSunnyGold.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BubaMascot(
                state = BubaState.THINKING,
                size = 54.dp
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "💡 Tahukah Kamu?",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = OnAccentGoldDark
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "Jahe Merah dan Temulawak di kebun Bubakan dirawat tanpa pestisida kimia untuk menjaga khasiat herbal alami!",
                    style = MaterialTheme.typography.bodySmall ?: MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF3D2A00),
                    lineHeight = 17.sp
                )
            }
        }
    }
}
