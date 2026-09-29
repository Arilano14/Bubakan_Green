package id.bubakangreen.app.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import id.bubakangreen.app.domain.model.Location
import id.bubakangreen.app.domain.model.LocationType
import id.bubakangreen.app.domain.model.MasterPlant
import id.bubakangreen.app.ui.common.UiState
import id.bubakangreen.app.ui.components.BubakanTopBar
import id.bubakangreen.app.ui.components.FeaturedLocationBanner
import id.bubakangreen.app.ui.components.LocationCard
import id.bubakangreen.app.ui.components.Mascot
import id.bubakangreen.app.ui.components.MascotCard
import id.bubakangreen.app.ui.components.MascotType
import id.bubakangreen.app.ui.components.OfflineStatusBar
import id.bubakangreen.app.ui.components.PlantHorizontalCard
import id.bubakangreen.app.ui.components.PrimaryButton
import id.bubakangreen.app.ui.components.SecondaryButton
import id.bubakangreen.app.ui.components.SectionHeader
import id.bubakangreen.app.ui.components.ShimmerBox
import id.bubakangreen.app.ui.components.StateErrorView
import id.bubakangreen.app.ui.theme.BackgroundWarm
import id.bubakangreen.app.ui.theme.BorderDivider
import id.bubakangreen.app.ui.theme.PrimaryGreen
import id.bubakangreen.app.ui.theme.PrimaryGreenDark
import id.bubakangreen.app.ui.theme.PrimaryGreenLight
import id.bubakangreen.app.ui.theme.TextPrimary
import id.bubakangreen.app.ui.theme.TextSecondary

/**
 * HomeScreen: Overhauled Eco-Green Visual Experience with Bubakan Green Mascot.
 * Structure adhering strictly to UX Laws & Specification:
 * 1. Top Hero: "Hai, teman Bubakan! Yuk, kenalan dengan tanaman di sekitar kita 🌱" + Mascot GREETING (180dp)
 * 2. Featured Location: "Urban Farming Bubakan"
 * 3. "Kenalan dengan Tanaman": Horizontal botanical discovery carousel
 * 4. "Jelajah Lokasi": Priority Urban Farming, Taman Toga RW 03, and backend community plots
 * 5. Educational Card: "Tahukah Kamu?" MascotCard with verified botanical facts
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
        containerColor = BackgroundWarm,
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
                // 1. TOP HERO CARD (Section 11)
                HomeHeroWelcomeCard(
                    onActionClick = onNavigateToCatalog
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 2. FEATURED LOCATION (Section 11: "Urban Farming Bubakan")
                when (val featured = uiState.featuredLocations) {
                    is UiState.Loading -> {
                        ShimmerBox(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp),
                            cornerRadius = 20.dp
                        )
                    }
                    is UiState.Success -> {
                        val featuredLocation = featured.data.find {
                            it.name.contains("Urban Farming", ignoreCase = true)
                        } ?: featured.data.firstOrNull()

                        if (featuredLocation != null) {
                            SectionHeader(
                                title = "Kebun Unggulan",
                                subtitle = "Pusat edukasi pertanian perkotaan Bubakan",
                                actionLabel = "Lihat Semua",
                                onActionClick = { onNavigateToLocations(null) }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            FeaturedLocationBanner(
                                location = featuredLocation,
                                onClick = { onLocationClick(featuredLocation.id) }
                            )
                        }
                    }
                    is UiState.Empty -> {
                        // Clean empty fallback
                    }
                    is UiState.Error -> {
                        StateErrorView(
                            message = featured.message,
                            onRetry = { viewModel.loadData() }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 3. "Kenalan dengan Tanaman" (Section 11: Horizontal card/list)
                SectionHeader(
                    title = "Kenalan dengan Tanaman",
                    subtitle = "Pelajari nama botani, latin, dan manfaat herbalnya",
                    actionLabel = "Lihat Semua",
                    onActionClick = onNavigateToCatalog
                )
                Spacer(modifier = Modifier.height(12.dp))

                when (val plants = uiState.popularPlants) {
                    is UiState.Loading -> {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            repeat(2) {
                                ShimmerBox(
                                    modifier = Modifier
                                        .width(200.dp)
                                        .height(220.dp),
                                    cornerRadius = 20.dp
                                )
                            }
                        }
                    }
                    is UiState.Success -> {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(horizontal = 0.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(plants.data, key = { it.id }) { plant ->
                                PlantHorizontalCard(
                                    plant = plant,
                                    onClick = { onPlantClick(plant.id) }
                                )
                            }
                        }
                    }
                    is UiState.Empty -> {
                        // Empty state handled
                    }
                    is UiState.Error -> {
                        Text(
                            text = plants.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 4. "Jelajah Lokasi" (Section 11: Urban Farming, Taman Toga RW 03, other locations)
                SectionHeader(
                    title = "Jelajah Lokasi",
                    subtitle = "Kunjungi kebun binaan warga di wilayah RW Bubakan",
                    actionLabel = "Peta Kebun",
                    onActionClick = { onNavigateToLocations(null) }
                )
                Spacer(modifier = Modifier.height(12.dp))

                when (val locations = uiState.featuredLocations) {
                    is UiState.Loading -> {
                        repeat(2) {
                            ShimmerBox(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .padding(vertical = 4.dp),
                                cornerRadius = 20.dp
                            )
                        }
                    }
                    is UiState.Success -> {
                        // Prioritize Urban Farming and Taman Toga RW 03, then others
                        val sortedLocations = locations.data.sortedWith(
                            compareByDescending<Location> { it.name.contains("Urban Farming", ignoreCase = true) }
                                .thenByDescending { it.rw == "03" || it.name.contains("RW 03", ignoreCase = true) }
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            sortedLocations.take(3).forEach { location ->
                                LocationCard(
                                    location = location,
                                    onClick = { onLocationClick(location.id) }
                                )
                            }
                        }
                    }
                    is UiState.Empty -> {
                        // Clean empty
                    }
                    is UiState.Error -> {
                        // Already surfaced in featured
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 5. EDUCATIONAL CARD: "Tahukah Kamu?" (Section 13)
                MascotCard(
                    title = "Tahukah Kamu?",
                    body = "Kangkung, bayam, dan aneka tanaman toga di kebun Bubakan dirawat dengan metode organik ramah lingkungan tanpa pestisida kimia sintetis.",
                    mascotType = MascotType.THINKING,
                    mascotSize = 140.dp,
                    containerColor = id.bubakangreen.app.ui.theme.Surface,
                    borderColor = BorderDivider,
                    action = {
                        SecondaryButton(
                            text = "Jelajahi Katalog Tanaman 🌿",
                            onClick = onNavigateToCatalog,
                            height = 48.dp
                        )
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

/**
 * Top Hero Welcome Card complying with Section 11 specifications:
 * Rounded 24dp, warm background, green accents, MascotType.GREETING (180dp).
 */
@Composable
private fun HomeHeroWelcomeCard(
    onActionClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = id.bubakangreen.app.ui.theme.Surface),
        border = BorderStroke(1.5.dp, BorderDivider),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 18.dp, top = 16.dp, end = 10.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PrimaryGreenLight,
                    border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.35f))
                ) {
                    Text(
                        text = "BUBAKAN GREEN",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = PrimaryGreenDark,
                        letterSpacing = 0.5.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Hai, teman Bubakan!",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary,
                    fontSize = 20.sp,
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Yuk, kenalan dengan\ntanaman di sekitar kita 🌱",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Normal,
                    color = TextSecondary,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                PrimaryButton(
                    text = "Mulai Belajar →",
                    onClick = onActionClick,
                    height = 48.dp,
                    shapeRadius = 14.dp,
                    modifier = Modifier.fillMaxWidth(0.92f)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Mascot(
                type = MascotType.GREETING,
                size = 180.dp,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}
