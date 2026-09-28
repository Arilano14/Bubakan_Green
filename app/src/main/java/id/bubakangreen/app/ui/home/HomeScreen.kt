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
import androidx.compose.foundation.shape.CircleShape
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
import id.bubakangreen.app.domain.model.Location
import id.bubakangreen.app.domain.model.LocationType
import id.bubakangreen.app.domain.model.MasterPlant
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
 * HomeScreen: Playful Botanical Education Discovery Hub.
 * Structure:
 * 1. TOP: BUBAKAN GREEN identity + friendly mascot greeting speech bubble.
 * 2. HERO: "Yuk, kenalan dengan tanaman Bubakan" interactive educational hero.
 * 3. PRIMARY DISCOVERY: Urban Farming & Taman Toga dual tactile chunky cards.
 * 4. FEATURED PLANTS: Botanical highlights with collectible bilingual badge styling.
 * 5. EDUCATIONAL DISCOVERY: "💡 Tahukah Kamu?" verified botanical fact card with mascot.
 * 6. EXPLORE BUBAKAN LOCATIONS: Flagship garden showcase.
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
                // 1. TOP: Mascot Greeting Header
                MascotGreetingSpeechCard()

                Spacer(modifier = Modifier.height(16.dp))

                // 2. HERO: "Yuk, kenalan dengan tanaman Bubakan"
                HeroEducationalBanner(
                    onExploreClick = onNavigateToCatalog
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 3. PRIMARY DISCOVERY: Urban Farming vs Taman Toga
                Text(
                    text = "Jelajahi Program Kebun",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = OnSurfaceForestDark
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    ChunkyProgramModule(
                        title = "Urban Farming",
                        subtitle = "Sayur & Pangan Mandiri",
                        icon = "🌱",
                        badgeText = "PANGAN",
                        containerColor = PrimaryContainerMint,
                        accentColor = PrimarySeedlingGreen,
                        onClick = { onNavigateToLocations(LocationType.URBAN_FARMING) },
                        modifier = Modifier.weight(1f)
                    )
                    ChunkyProgramModule(
                        title = "Taman Toga",
                        subtitle = "Apotek Hidup Herbal",
                        icon = "🌿",
                        badgeText = "HERBAL",
                        containerColor = AccentSunnyContainer,
                        accentColor = AccentSunnyGold,
                        onClick = { onNavigateToLocations(LocationType.TAMAN_TOGA) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 4. FEATURED PLANTS: Koleksi Tanaman Pilihan
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Koleksi Tanaman Pilihan",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = OnSurfaceForestDark
                        )
                        Text(
                            text = "Pelajari nama, latin, dan khasiat herbalnya",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurfaceSageMuted
                        )
                    }
                    Text(
                        text = "Perpustakaan →",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = PrimarySeedlingGreen,
                        modifier = Modifier.clickable { onNavigateToCatalog() }
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))

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
                        // Handled cleanly
                    }
                    is UiState.Error -> {
                        Text(
                            text = plants.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnSurfaceSageMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 5. EDUCATIONAL DISCOVERY: "💡 Tahukah Kamu?" Trivia Section
                EducationalTriviaFactCard()

                Spacer(modifier = Modifier.height(26.dp))

                // 6. EXPLORE BUBAKAN LOCATIONS: Flagship Garden Showcase
                Text(
                    text = "Jelajahi Kebun di Bubakan",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = OnSurfaceForestDark
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Kunjungi langsung kebun binaan warga di wilayah RW Bubakan",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceSageMuted
                )
                Spacer(modifier = Modifier.height(14.dp))

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
                        // Empty fallback
                    }
                    is UiState.Error -> {
                        StateErrorView(
                            message = featured.message,
                            onRetry = { viewModel.loadData() }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }
}

/**
 * Welcoming speech bubble card from companion mascot Si Buba.
 */
@Composable
private fun MascotGreetingSpeechCard() {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCardWhite),
        border = BorderStroke(1.5.dp, OutlineOrganic),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BubaMascot(
                state = BubaState.GREETING,
                size = 76.dp
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
                        fontWeight = FontWeight.ExtraBold,
                        color = PrimaryForestDark,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Halo! Aku Si Buba. Yuk, kenalan dengan tanaman herbal & kebun hijau di Bubakan hari ini!",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = OnSurfaceForestDark,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

/**
 * Educational Hero Banner: "Yuk, kenalan dengan tanaman Bubakan"
 */
@Composable
private fun HeroEducationalBanner(
    onExploreClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = PrimaryContainerMint),
        border = BorderStroke(1.5.dp, PrimarySeedlingGreen.copy(alpha = 0.35f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Yuk, Kenalan dengan Tanaman Bubakan! 🌿",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = PrimaryForestDark,
                        lineHeight = 28.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Temukan khasiat herbal alami dan sayuran segar langsung dari kebun warga.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurfaceForestDark,
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TactileButton(
                text = "Mulai Belajar Sekarang 🚀",
                onClick = onExploreClick,
                style = TactileButtonStyle.PRIMARY,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Chunky interactive program module card (Urban Farming / Taman Toga).
 */
@Composable
private fun ChunkyProgramModule(
    title: String,
    subtitle: String,
    icon: String,
    badgeText: String,
    containerColor: Color,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCardWhite),
        border = BorderStroke(1.5.dp, OutlineOrganic),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(containerColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = icon, fontSize = 24.sp)
                }
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = containerColor
                ) {
                    Text(
                        text = badgeText,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = OnSurfaceForestDark,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = OnSurfaceForestDark
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall ?: MaterialTheme.typography.bodyMedium,
                color = OnSurfaceSageMuted,
                lineHeight = 16.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Lihat Kebun →",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.ExtraBold,
                color = PrimarySeedlingGreen
            )
        }
    }
}

/**
 * Educational bite-sized botanical trivia fact card with mascot thinking posture.
 */
@Composable
private fun EducationalTriviaFactCard() {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = AccentSunnyContainer),
        border = BorderStroke(1.5.dp, AccentSunnyGold.copy(alpha = 0.45f)),
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
                size = 64.dp
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "💡 TAHUKAH KAMU?",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = OnAccentGoldDark,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Jahe Merah dan Temulawak di kebun Bubakan dirawat tanpa pestisida kimia untuk menjaga khasiat alami!",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3D2A00),
                    lineHeight = 19.sp
                )
            }
        }
    }
}
