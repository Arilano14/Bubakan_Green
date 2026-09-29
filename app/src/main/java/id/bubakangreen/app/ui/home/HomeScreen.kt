package id.bubakangreen.app.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import id.bubakangreen.app.domain.model.Location
import id.bubakangreen.app.domain.model.LocationType
import id.bubakangreen.app.ui.common.UiState
import id.bubakangreen.app.ui.components.BubakanTopBar
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
import id.bubakangreen.app.ui.theme.MascotYellow
import id.bubakangreen.app.ui.theme.PrimaryGreen
import id.bubakangreen.app.ui.theme.PrimaryGreenDark
import id.bubakangreen.app.ui.theme.PrimaryGreenLight
import id.bubakangreen.app.ui.theme.Surface
import id.bubakangreen.app.ui.theme.TextPrimary
import id.bubakangreen.app.ui.theme.TextSecondary

/**
 * HomeScreen: Overhauled Duolingo-inspired Educational Experience for Bubakan Green.
 * Structure adhering strictly to Section 8 specifications:
 * 1. Top Hero: Character-led welcome section with Mascot GREETING (~180dp) breaking out, tilted +2°
 * 2. "Temukan Kebun Bubakan": 2 primary destinations (Urban Farming & Taman Toga)
 * 3. "Kenalan dengan Tanaman": Horizontal botanical discovery
 * 4. "Belajar Hari Ini": Small educational section with Mascot THINKING
 * 5. "Jelajah Lokasi": Balanced community garden exploration without overcrowding
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
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // 1. TOP HERO: Character-led Welcome Section (Section 8)
                HomeHeroSection(
                    onMulaiJelajah = onNavigateToCatalog
                )

                Spacer(modifier = Modifier.height(26.dp))

                // 2. "Temukan Kebun Bubakan": 2 Primary Destinations (Section 8)
                SectionHeader(
                    title = "Temukan Kebun Bubakan",
                    subtitle = "Dua destinasi kebun percontohan utama warga",
                    actionLabel = "Lihat Semua",
                    onActionClick = { onNavigateToLocations(null) }
                )
                Spacer(modifier = Modifier.height(12.dp))

                when (val locationsState = uiState.featuredLocations) {
                    is UiState.Loading -> {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            ShimmerBox(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(160.dp),
                                cornerRadius = 18.dp
                            )
                            ShimmerBox(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(160.dp),
                                cornerRadius = 18.dp
                            )
                        }
                    }
                    is UiState.Success -> {
                        val allLocations = locationsState.data
                        val urbanFarming = allLocations.find {
                            it.name.contains("Urban Farming", ignoreCase = true) || it.type == LocationType.URBAN_FARMING
                        } ?: allLocations.firstOrNull()

                        val tamanToga = allLocations.find {
                            it.name.contains("Taman Toga", ignoreCase = true) || it.type == LocationType.TAMAN_TOGA
                        } ?: allLocations.getOrNull(1)

                        HomeTwoPrimaryDestinations(
                            urbanFarmingLocation = urbanFarming,
                            tamanTogaLocation = tamanToga,
                            onLocationClick = onLocationClick
                        )
                    }
                    is UiState.Empty -> {
                        // Empty state handled cleanly
                    }
                    is UiState.Error -> {
                        StateErrorView(
                            message = locationsState.message,
                            onRetry = { viewModel.loadData() }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // 3. "Kenalan dengan Tanaman": Horizontal Discovery (Section 8)
                SectionHeader(
                    title = "Kenalan dengan Tanaman",
                    subtitle = "Pelajari nama botani, latin, dan khasiat herbalnya",
                    actionLabel = "Katalog Lengkap",
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
                                        .height(200.dp),
                                    cornerRadius = 18.dp
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
                    is UiState.Empty -> {}
                    is UiState.Error -> {
                        Text(
                            text = plants.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // 4. "Belajar Hari Ini": Small Educational Section (Section 8)
                SectionHeader(
                    title = "Belajar Hari Ini",
                    subtitle = "Wawasan edukasi pertanian organik ramah lingkungan"
                )
                Spacer(modifier = Modifier.height(8.dp))

                MascotCard(
                    title = "Tahukah Kamu?",
                    body = "Kangkung, bayam, dan aneka tanaman toga di kebun Bubakan dirawat dengan metode organik ramah lingkungan tanpa pestisida kimia sintetis.",
                    mascotType = MascotType.THINKING,
                    mascotSize = 140.dp,
                    containerColor = Surface,
                    borderColor = BorderDivider,
                    action = {
                        SecondaryButton(
                            text = "Jelajahi Katalog Tanaman 🌿",
                            onClick = onNavigateToCatalog,
                            height = 48.dp
                        )
                    }
                )

                Spacer(modifier = Modifier.height(28.dp))

                // 5. "Jelajah Lokasi": Balanced list of community plots without overcrowding (Section 8)
                SectionHeader(
                    title = "Jelajah Lokasi",
                    subtitle = "Petak kebun binaan warga di wilayah RW Bubakan",
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
                                cornerRadius = 18.dp
                            )
                        }
                    }
                    is UiState.Success -> {
                        val otherLocations = locations.data.filter { loc ->
                            !loc.name.contains("Urban Farming Kelurahan", ignoreCase = true) &&
                            !loc.name.contains("Taman Toga RW 03", ignoreCase = true)
                        }.ifEmpty { locations.data }

                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            otherLocations.take(2).forEach { location ->
                                LocationCard(
                                    location = location,
                                    onClick = { onLocationClick(location.id) }
                                )
                            }
                        }
                    }
                    is UiState.Empty -> {}
                    is UiState.Error -> {}
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

/**
 * Section 8 Top Hero Section:
 * Character-led welcome layout where the mascot visually breaks out of the card.
 */
@Composable
private fun HomeHeroSection(
    onMulaiJelajah: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Welcoming Card surface
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Surface,
            border = BorderStroke(1.dp, BorderDivider),
            tonalElevation = 0.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 18.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 18.dp, top = 20.dp, end = 120.dp, bottom = 20.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PrimaryGreenLight,
                    border = BorderStroke(0.8.dp, PrimaryGreen.copy(alpha = 0.35f))
                ) {
                    Text(
                        text = "🌱 KELURAHAN BUBAKAN",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = PrimaryGreenDark,
                        letterSpacing = 0.5.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Hai, teman Bubakan!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary,
                    fontSize = 22.sp,
                    lineHeight = 28.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Yuk, kenalan dengan\ntanaman di sekitar kita 🌱",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Normal,
                    color = TextSecondary,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                PrimaryButton(
                    text = "Mulai Jelajah →",
                    onClick = onMulaiJelajah,
                    height = 50.dp,
                    shapeRadius = 14.dp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Mascot GREETING (~180dp) breaking out of the card composition at top right
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 4.dp)
        ) {
            Mascot(
                type = MascotType.GREETING,
                size = 180.dp,
                baseRotation = 2f,
                animateIdle = true
            )
        }
    }
}

/**
 * Section 8 Two Primary Destinations:
 * Strong solid color-blocked destinations for Urban Farming and Taman Toga.
 */
@Composable
private fun HomeTwoPrimaryDestinations(
    urbanFarmingLocation: Location?,
    tamanTogaLocation: Location?,
    onLocationClick: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Destination 1: Urban Farming
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = PrimaryGreenLight,
            border = BorderStroke(1.5.dp, PrimaryGreen.copy(alpha = 0.6f)),
            tonalElevation = 0.dp,
            modifier = Modifier
                .weight(1f)
                .clickable {
                    urbanFarmingLocation?.let { onLocationClick(it.id) }
                }
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = "🌱", fontSize = 28.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Urban Farming",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = PrimaryGreenDark
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "RW 01 • Budidaya Sayuran & Pangan",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Kunjungi Kebun →",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = PrimaryGreenDark
                )
            }
        }

        // Destination 2: Taman Toga
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = Color(0xFFFFF5D6), // Sunny botanical container
            border = BorderStroke(1.5.dp, MascotYellow.copy(alpha = 0.8f)),
            tonalElevation = 0.dp,
            modifier = Modifier
                .weight(1f)
                .clickable {
                    tamanTogaLocation?.let { onLocationClick(it.id) }
                }
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = "🌿", fontSize = 28.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Taman Toga",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF5A4400)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "RW 03 • Herbal & Obat Keluarga",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Kunjungi Kebun →",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF5A4400)
                )
            }
        }
    }
}
