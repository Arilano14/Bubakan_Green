package id.bubakangreen.app.ui.home

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.graphics.Color
import id.bubakangreen.app.ui.components.BubakanMapView
import id.bubakangreen.app.ui.theme.ForestGreen
import id.bubakangreen.app.ui.theme.OnSurfaceForestDark
import id.bubakangreen.app.ui.theme.OnSurfaceSageMuted
import id.bubakangreen.app.ui.theme.OutlineOrganic
import id.bubakangreen.app.ui.theme.PrimaryContainerMint
import id.bubakangreen.app.ui.theme.SurfaceCardWhite
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import id.bubakangreen.app.domain.model.Location
import id.bubakangreen.app.domain.model.LocationType
import id.bubakangreen.app.ui.common.UiState
import id.bubakangreen.app.ui.components.Mascot
import id.bubakangreen.app.ui.components.MascotType
import id.bubakangreen.app.ui.components.OfflineStatusBar
import id.bubakangreen.app.ui.components.PlantHorizontalCard
import id.bubakangreen.app.ui.components.PrimaryButton
import id.bubakangreen.app.ui.components.SecondaryButton
import id.bubakangreen.app.ui.components.ShimmerBox
import id.bubakangreen.app.ui.components.StateErrorView
import id.bubakangreen.app.ui.theme.BackgroundWarm
import id.bubakangreen.app.ui.theme.MascotYellow
import id.bubakangreen.app.ui.theme.PrimaryGreen
import id.bubakangreen.app.ui.theme.PrimaryGreenDark
import id.bubakangreen.app.ui.theme.PrimaryGreenLight
import id.bubakangreen.app.ui.theme.Surface
import id.bubakangreen.app.ui.theme.TextPrimary
import id.bubakangreen.app.ui.theme.TextSecondary

/**
 * HomeScreen: Completely redesigned for educational mobile experience.
 *
 * Mental model: WELCOME → DISCOVER → LEARN → EXPLORE
 *
 * No dashboard cards. No card nesting. Open composition with
 * clear visual hierarchy, character-led hero, and section-based flow.
 */
@Composable
fun HomeScreen(
    onLocationClick: (String) -> Unit,
    onPlantClick: (String) -> Unit,
    onNavigateToLocations: (LocationType?) -> Unit,
    onNavigateToCatalog: () -> Unit,
    onInfoClick: () -> Unit,
    onScanQrClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundWarm)
    ) {
        OfflineStatusBar(isOffline = uiState.isOffline)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // ── SECTION 1: WELCOME HERO ──
            // Open composition: text left, mascot right. No card wrapper.
            WelcomeHero(
                onMulaiJelajah = onNavigateToCatalog,
                onScanQr = onScanQrClick,
                onInfoClick = onInfoClick
            )

            Spacer(modifier = Modifier.height(32.dp))

            // ── SECTION 2: DISCOVER GARDENS ──
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    text = "Jelajah Kebun",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )
                Text(
                    text = "Temukan kebun dan taman toga di Bubakan",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // ── REKAPITULASI KEBUN BUBAKAN (Section 23) ──
                GardenRecapSection(
                    totalGardens = uiState.gardenSummary.totalGardens,
                    urbanFarmingCount = uiState.gardenSummary.urbanFarmingCount,
                    tamanTogaCount = uiState.gardenSummary.tamanTogaCount,
                    onNavigateToLocations = onNavigateToLocations
                )

                Spacer(modifier = Modifier.height(20.dp))

                when (val locationsState = uiState.featuredLocations) {
                    is UiState.Loading -> {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            ShimmerBox(
                                modifier = Modifier.weight(1f).height(140.dp),
                                cornerRadius = 20.dp
                            )
                            ShimmerBox(
                                modifier = Modifier.weight(1f).height(140.dp),
                                cornerRadius = 20.dp
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

                        GardenDestinations(
                            urbanFarming = urbanFarming,
                            tamanToga = tamanToga,
                            onLocationClick = onLocationClick
                        )
                    }
                    is UiState.Empty -> {}
                    is UiState.Error -> {
                        StateErrorView(
                            message = locationsState.message,
                            onRetry = { viewModel.loadData() }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ── MINI MAP DISCOVERY (Section 24) ──
                HomeMiniMapSection(
                    locations = uiState.publishedLocations,
                    onLocationClick = onLocationClick,
                    onNavigateToLocations = { onNavigateToLocations(null) }
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── SECTION 3: LEARN / PLANT DISCOVERY ──
            Column(modifier = Modifier.padding(start = 20.dp)) {
                Text(
                    text = "Kenalan dengan Tanaman",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary,
                    modifier = Modifier.padding(end = 20.dp)
                )
                Text(
                    text = "Pelajari nama botani, latin, dan khasiat herbalnya",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    modifier = Modifier.padding(end = 20.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                when (val plants = uiState.popularPlants) {
                    is UiState.Loading -> {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth().padding(end = 20.dp)
                        ) {
                            repeat(2) {
                                ShimmerBox(
                                    modifier = Modifier.width(180.dp).height(220.dp),
                                    cornerRadius = 20.dp
                                )
                            }
                        }
                    }
                    is UiState.Success -> {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            contentPadding = PaddingValues(end = 20.dp),
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
                            color = TextSecondary,
                            modifier = Modifier.padding(end = 20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Katalog Lengkap →",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryGreen,
                    modifier = Modifier
                        .clickable { onNavigateToCatalog() }
                        .padding(end = 20.dp, top = 8.dp, bottom = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── SECTION 4: DAILY TIP ──
            DailyTipSection(onNavigateToCatalog = onNavigateToCatalog)

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

/**
 * Welcome Hero: Open composition, no enclosing card.
 * Text on the left with greeting, mascot on the right floating naturally.
 */
@Composable
private fun WelcomeHero(
    onMulaiJelajah: () -> Unit,
    onScanQr: () -> Unit,
    onInfoClick: () -> Unit
) {
    // Green gradient header band
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(PrimaryGreen)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, bottom = 24.dp, start = 20.dp, end = 20.dp)
        ) {
            // Top row: app name branding
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "BUBAKAN GREEN",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = id.bubakangreen.app.ui.theme.TextOnColor.copy(alpha = 0.9f),
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = "Kelurahan Bubakan, Mijen",
                        style = MaterialTheme.typography.labelSmall,
                        color = id.bubakangreen.app.ui.theme.TextOnColor.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Hero content: text left, mascot right
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Hai, teman\nBubakan!",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = id.bubakangreen.app.ui.theme.TextOnColor,
                        lineHeight = 36.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Yuk, kenalan dengan tanaman\ndi sekitar kita",
                        style = MaterialTheme.typography.bodyLarge,
                        color = id.bubakangreen.app.ui.theme.TextOnColor.copy(alpha = 0.9f),
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    PrimaryButton(
                        text = "Mulai Jelajah",
                        onClick = onMulaiJelajah,
                        height = 50.dp,
                        shapeRadius = 25.dp,
                        containerColor = id.bubakangreen.app.ui.theme.WarmYellow,
                        contentColor = id.bubakangreen.app.ui.theme.TextPrimary,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    SecondaryButton(
                        text = "Pindai Label QR",
                        onClick = onScanQr,
                        icon = Icons.Outlined.QrCodeScanner,
                        height = 48.dp,
                        shapeRadius = 24.dp,
                        textColor = id.bubakangreen.app.ui.theme.TextOnColor,
                        borderColor = id.bubakangreen.app.ui.theme.TextOnColor.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Mascot in reserved layout space (Section 14 & 15)
                Box(
                    modifier = Modifier.width(136.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Mascot(
                        type = MascotType.GREETING,
                        size = 136.dp,
                        baseRotation = 2f,
                        animateIdle = true
                    )
                }
            }
        }
    }
}

/**
 * Two garden destinations side by side with distinct color identities.
 * No borders, just clean color blocking.
 */
@Composable
private fun GardenDestinations(
    urbanFarming: Location?,
    tamanToga: Location?,
    onLocationClick: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Urban Farming — green tone
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(20.dp))
                .background(PrimaryGreenLight)
                .clickable { urbanFarming?.let { onLocationClick(it.id) } }
                .padding(16.dp)
        ) {
            Column {
                Icon(
                    imageVector = Icons.Default.Eco,
                    contentDescription = null,
                    tint = PrimaryGreenDark,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
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
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Kunjungi",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = PrimaryGreenDark
                )
            }
        }

        // Taman Toga — warm botanical tone
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(20.dp))
                .background(androidx.compose.ui.graphics.Color(0xFFFFF5D6))
                .clickable { tamanToga?.let { onLocationClick(it.id) } }
                .padding(16.dp)
        ) {
            Column {
                Icon(
                    imageVector = Icons.Default.Spa,
                    contentDescription = null,
                    tint = androidx.compose.ui.graphics.Color(0xFF5A4400),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Taman Toga",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = androidx.compose.ui.graphics.Color(0xFF5A4400)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "RW 03 • Herbal & Obat Keluarga",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Kunjungi",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = androidx.compose.ui.graphics.Color(0xFF5A4400)
                )
            }
        }
    }
}

/**
 * Daily educational tip with thinking mascot.
 * Uses an eco-green tinted background band — not a bordered card.
 */
@Composable
private fun DailyTipSection(
    onNavigateToCatalog: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(PrimaryGreenLight.copy(alpha = 0.5f))
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Tahukah Kamu?",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = PrimaryGreenDark
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Tanaman toga di kebun Bubakan dirawat dengan metode organik ramah lingkungan, tanpa pestisida kimia sintetis.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary,
                    lineHeight = 22.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                SecondaryButton(
                    text = "Jelajahi Katalog Tanaman",
                    onClick = onNavigateToCatalog,
                    height = 44.dp
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier.width(96.dp),
                contentAlignment = Alignment.Center
            ) {
                Mascot(
                    type = MascotType.THINKING,
                    size = 96.dp,
                    baseRotation = 3f
                )
            }
        }
    }
}

/**
 * Rekapitulasi Kebun Bubakan (Section 23).
 * Derived dynamically from LocationRepository.getPublishedLocations().
 * Zero hardcoded numbers.
 */
@Composable
private fun GardenRecapSection(
    totalGardens: Int,
    urbanFarmingCount: Int,
    tamanTogaCount: Int,
    onNavigateToLocations: (LocationType?) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCardWhite),
        border = BorderStroke(1.5.dp, OutlineOrganic),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Rekapitulasi Kebun Bubakan",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = OnSurfaceForestDark
                    )
                    Text(
                        text = "Data sebaran aktif kelurahan terkini",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceSageMuted
                    )
                }
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(PrimaryContainerMint),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = ForestGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Total Kebun
                RecapStatBadge(
                    count = totalGardens.toString(),
                    label = "Total Kebun",
                    icon = Icons.Default.Park,
                    containerColor = PrimaryContainerMint,
                    textColor = ForestGreen,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToLocations(null) }
                )

                // Urban Farming
                RecapStatBadge(
                    count = urbanFarmingCount.toString(),
                    label = "Urban Farm",
                    icon = Icons.Default.Eco,
                    containerColor = Color(0xFFE8F5E9),
                    textColor = Color(0xFF2E7D32),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToLocations(LocationType.URBAN_FARMING) }
                )

                // Taman Toga
                RecapStatBadge(
                    count = tamanTogaCount.toString(),
                    label = "Taman Toga",
                    icon = Icons.Default.Spa,
                    containerColor = Color(0xFFFFF8E1),
                    textColor = Color(0xFFD97706),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToLocations(LocationType.TAMAN_TOGA) }
                )
            }
        }
    }
}

@Composable
private fun RecapStatBadge(
    count: String,
    label: String,
    icon: ImageVector,
    containerColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(containerColor)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = count,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = textColor
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = id.bubakangreen.app.ui.theme.TextSecondary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

/**
 * Compact discovery mini-map for HomeScreen (Section 24).
 * Reuses BubakanMapView. Same data, same marker logic, same category colors.
 */
@Composable
private fun HomeMiniMapSection(
    locations: List<Location>,
    onLocationClick: (String) -> Unit,
    onNavigateToLocations: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCardWhite),
        border = BorderStroke(1.5.dp, OutlineOrganic),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Peta Sebaran Interaktif",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = OnSurfaceForestDark
                    )
                    Text(
                        text = "Sebaran titik urban farming & toga warga",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceSageMuted
                    )
                }

                // Legend preview
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "🌱 Urban", style = MaterialTheme.typography.labelSmall, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                    Text(text = "🌿 Toga", style = MaterialTheme.typography.labelSmall, color = Color(0xFFD97706), fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Reusable BubakanMapView in a compact 190dp container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(BorderStroke(1.dp, OutlineOrganic))
            ) {
                BubakanMapView(
                    locations = locations,
                    onLocationSelect = { onLocationClick(it.id) },
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Lihat Semua Lokasi Pada Peta →",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = PrimaryGreen,
                modifier = Modifier
                    .clickable { onNavigateToLocations() }
                    .padding(vertical = 4.dp)
            )
        }
    }
}

