package id.bubakangreen.app.ui.admin

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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.bubakangreen.app.domain.model.Location
import id.bubakangreen.app.domain.model.LocationStatus
import id.bubakangreen.app.domain.model.LocationType
import id.bubakangreen.app.domain.model.MasterPlant
import id.bubakangreen.app.ui.common.UiState
import id.bubakangreen.app.ui.components.Mascot
import id.bubakangreen.app.ui.components.MascotType
import id.bubakangreen.app.ui.components.PrimaryButton
import id.bubakangreen.app.ui.theme.AlertOrange
import id.bubakangreen.app.ui.theme.BorderCard
import id.bubakangreen.app.ui.theme.BotanicalPaper
import id.bubakangreen.app.ui.theme.ForestGreen
import id.bubakangreen.app.ui.theme.FriendlyRed
import id.bubakangreen.app.ui.theme.LeafGreen
import id.bubakangreen.app.ui.theme.LeafGreenLight
import id.bubakangreen.app.ui.theme.NaturalGreen
import id.bubakangreen.app.ui.theme.SurfaceCard
import id.bubakangreen.app.ui.theme.TextOnColor
import id.bubakangreen.app.ui.theme.TextPrimary
import id.bubakangreen.app.ui.theme.TextSecondary
import id.bubakangreen.app.ui.theme.WarmYellow
import id.bubakangreen.app.ui.theme.WarmYellowLight

/**
 * Mobile-First Admin Dashboard Redesign.
 * Section 7 & 8 hierarchy:
 * - Header: "Halo, Admin 🌱" + Supporting text + static small mascot in reserved box.
 * - Primary Actions:
 *   1. [ Kelola Tanaman ]
 *   2. [ Kelola Lokasi ]
 *   3. [ Persetujuan ]
 * - Zero dense desktop tables or hardcoded pixel overflow.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: AdminDashboardViewModel,
    onNavigateToLocationForm: (String?) -> Unit,
    onNavigateToPlantForm: (String) -> Unit,
    onNavigateToMasterPlantForm: (String?) -> Unit,
    onSignedOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val session by viewModel.session.collectAsState()
    val actionMessage by viewModel.actionMessage.collectAsState()

    var activeSection by remember { mutableStateOf(AdminSection.OVERVIEW) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Admin Kelurahan",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = ForestGreen
                        )
                        Text(
                            text = session?.displayName ?: session?.email ?: "Kelurahan Bubakan, Mijen",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.signOut(onSignedOut) },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Keluar dari Akun Admin",
                            tint = FriendlyRed
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BotanicalPaper)
            )
        },
        containerColor = BotanicalPaper,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Action Feedback Banner
            actionMessage?.let { msg ->
                Surface(
                    color = LeafGreen,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = msg,
                            color = TextOnColor,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { viewModel.clearActionMessage() },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Tutup",
                                tint = TextOnColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            when (val state = uiState) {
                is UiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = ForestGreen)
                    }
                }

                is UiState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Gagal memuat data admin",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = FriendlyRed
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = state.message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            PrimaryButton(
                                text = "Muat Ulang",
                                onClick = { viewModel.loadData() },
                                height = 48.dp
                            )
                        }
                    }
                }

                is UiState.Empty, is UiState.Success -> {
                    val data = (state as? UiState.Success)?.data
                        ?: AdminDashboardData(0, 0, 0, "", emptyList(), emptyList())

                    when (activeSection) {
                        AdminSection.OVERVIEW -> {
                            AdminOverviewContent(
                                data = data,
                                onOpenPlants = { activeSection = AdminSection.PLANTS },
                                onOpenLocations = { activeSection = AdminSection.LOCATIONS }
                            )
                        }

                        AdminSection.PLANTS -> {
                            AdminPlantsListContent(
                                plants = data.masterPlants,
                                onBackToHub = { activeSection = AdminSection.OVERVIEW },
                                onEditPlant = { onNavigateToMasterPlantForm(it.id) },
                                onAddPlant = { onNavigateToMasterPlantForm(null) }
                            )
                        }

                        AdminSection.LOCATIONS -> {
                            AdminLocationsListContent(
                                locations = data.locations,
                                onBackToHub = { activeSection = AdminSection.OVERVIEW },
                                onEditLocation = { onNavigateToLocationForm(it.id) },
                                onAddLocation = { onNavigateToLocationForm(null) }
                            )
                        }
                    }
                }
            }
        }
    }
}

private enum class AdminSection {
    OVERVIEW,
    LOCATIONS,
    PLANTS
}

/**
 * Section 8: Simplified Admin Dashboard Content Hierarchy.
 * Header:
 * "Halo, Admin 🌱"
 * "Kelola informasi kebun dan tanaman Bubakan."
 * Optional static mascot (80-100dp) in reserved layout region.
 * PRIMARY ACTIONS:
 * 1. [ Kelola Tanaman ]
 * 2. [ Kelola Lokasi ]
 * 3. [ Persetujuan ]
 * Then small summary section.
 */
@Composable
private fun AdminOverviewContent(
    data: AdminDashboardData,
    onOpenPlants: () -> Unit,
    onOpenLocations: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ── SECTION HEADER (MASCOT + GREETING) ──
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = SurfaceCard,
                border = BorderStroke(1.dp, BorderCard),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Halo, Admin 🌱",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = ForestGreen
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Kelola informasi kebun dan tanaman Bubakan.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            lineHeight = 20.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Reserved layout region for mascot (static on Admin, Section 15 & 16)
                    Box(
                        modifier = Modifier.size(88.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Mascot(
                            type = MascotType.GREETING,
                            size = 88.dp,
                            animateIdle = false
                        )
                    }
                }
            }
        }

        // ── SUMMARY STAT PILLS ──
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SummaryPill(
                        label = "Tanaman",
                        value = "${data.totalMasterPlants}",
                        color = ForestGreen,
                        bgColor = LeafGreenLight,
                        modifier = Modifier.weight(1f)
                    )
                    SummaryPill(
                        label = "Total Lahan",
                        value = "${data.activeLocations}/${data.totalLocations}",
                        color = LeafGreen,
                        bgColor = LeafGreenLight,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SummaryPill(
                        label = "Urban Farming",
                        value = "${data.totalUrbanFarming}",
                        color = ForestGreen,
                        bgColor = LeafGreenLight,
                        modifier = Modifier.weight(1f)
                    )
                    SummaryPill(
                        label = "Taman Toga",
                        value = "${data.totalTamanToga}",
                        color = LeafGreen,
                        bgColor = LeafGreenLight,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // ── PRIMARY ACTION 1: KELOLA TANAMAN ──
        item {
            AdminPrimaryActionCard(
                title = "Kelola Tanaman",
                description = "${data.totalMasterPlants} spesies botani terverifikasi di ensiklopedia.",
                icon = Icons.Default.Grass,
                iconTint = ForestGreen,
                iconBg = LeafGreenLight,
                buttonText = "Buka Kelola Tanaman",
                onClick = onOpenPlants
            )
        }

        // ── PRIMARY ACTION 2: KELOLA LAHAN ──
        item {
            AdminPrimaryActionCard(
                title = "Kelola Lahan",
                description = "${data.totalLocations} lahan binaan terdaftar (Urban Farming & Taman Toga).",
                icon = Icons.Default.Place,
                iconTint = LeafGreen,
                iconBg = LeafGreenLight,
                buttonText = "Buka Kelola Lahan",
                onClick = onOpenLocations
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SummaryPill(
    label: String,
    value: String,
    color: Color,
    bgColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = bgColor,
        border = BorderStroke(1.dp, color.copy(alpha = 0.2f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondary,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun AdminPrimaryActionCard(
    title: String,
    description: String,
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    buttonText: String,
    onClick: () -> Unit,
    badgeText: String? = null,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = BorderStroke(1.dp, BorderCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = iconBg,
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    if (badgeText != null) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = badgeText,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = iconTint
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                lineHeight = 20.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(14.dp))

            PrimaryButton(
                text = buttonText,
                onClick = onClick,
                height = 48.dp,
                shapeRadius = 24.dp
            )
        }
    }
}

/**
 * Section 9: Admin Plant Management.
 * Rows with Plant Name, Scientific Name, status/short metadata, and action.
 * No long descriptions in list rows. No horizontal overflow at 360dp.
 */
@Composable
private fun AdminPlantsListContent(
    plants: List<MasterPlant>,
    onBackToHub: () -> Unit,
    onEditPlant: (MasterPlant) -> Unit,
    onAddPlant: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Safe Adaptive Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackToHub,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Kembali ke Menu Utama",
                    tint = ForestGreen
                )
            }

            Text(
                text = "Ensiklopedia (${plants.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            PrimaryButton(
                text = "+ Tambah",
                onClick = onAddPlant,
                height = 38.dp,
                shapeRadius = 19.dp,
                modifier = Modifier.widthIn(min = 90.dp)
            )
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(plants, key = { it.id }) { plant ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    border = BorderStroke(1.dp, BorderCard),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = plant.nameId,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = plant.nameLatin,
                                style = MaterialTheme.typography.bodySmall,
                                fontStyle = FontStyle.Italic,
                                color = TextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (plant.nameMandarin?.isNotBlank() == true) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${plant.nameMandarin} • ${plant.pinyin ?: ""}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ForestGreen,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = { onEditPlant(plant) },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Tanaman ${plant.nameId}",
                                tint = ForestGreen
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

/**
 * Section 10: Admin Location Management.
 * Rows with Location name, Type, RW, Status, and action.
 * No full address or heavy text inside list rows. Safe at 360dp width.
 */
@Composable
private fun AdminLocationsListContent(
    locations: List<Location>,
    onBackToHub: () -> Unit,
    onEditLocation: (Location) -> Unit,
    onAddLocation: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Safe Adaptive Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackToHub,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Kembali ke Menu Utama",
                    tint = ForestGreen
                )
            }

            Text(
                text = "Daftar Lahan (${locations.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            PrimaryButton(
                text = "+ Tambah",
                onClick = onAddLocation,
                height = 38.dp,
                shapeRadius = 19.dp,
                modifier = Modifier.widthIn(min = 90.dp)
            )
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(locations, key = { it.id }) { loc ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    border = BorderStroke(1.dp, BorderCard),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = loc.name,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${loc.displayRegionTag} • ${if (loc.type == LocationType.URBAN_FARMING) "Urban Farming" else "Taman Toga"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (loc.status == LocationStatus.PUBLISHED || loc.status == LocationStatus.ACTIVE) LeafGreenLight else WarmYellowLight
                            ) {
                                Text(
                                    text = if (loc.status == LocationStatus.PUBLISHED || loc.status == LocationStatus.ACTIVE) "Aktif / Terbit" else "Nonaktif",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (loc.status == LocationStatus.PUBLISHED || loc.status == LocationStatus.ACTIVE) ForestGreen else AlertOrange,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = { onEditLocation(loc) },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Lahan ${loc.name}",
                                tint = ForestGreen
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
