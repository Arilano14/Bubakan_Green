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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.bubakangreen.app.domain.model.Location
import id.bubakangreen.app.domain.model.LocationStatus
import id.bubakangreen.app.domain.model.MasterPlant
import id.bubakangreen.app.ui.common.UiState
import id.bubakangreen.app.ui.components.Mascot
import id.bubakangreen.app.ui.components.MascotType
import id.bubakangreen.app.ui.components.PrimaryButton
import id.bubakangreen.app.ui.components.SecondaryButton
import id.bubakangreen.app.ui.theme.AlertOrange
import id.bubakangreen.app.ui.theme.BorderCard
import id.bubakangreen.app.ui.theme.BotanicalPaper
import id.bubakangreen.app.ui.theme.ForestGreen
import id.bubakangreen.app.ui.theme.FriendlyRed
import id.bubakangreen.app.ui.theme.LeafGreen
import id.bubakangreen.app.ui.theme.LeafGreenDark
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
 * Duolingo-inspired character-driven hub:
 * - Mascot Greeting Header: "Halo Admin 🌱 Kelola kebun Bubakan"
 * - 3 Large Action Cards (Kelola Lokasi, Kelola Tanaman, Approval)
 * - Zero dense desktop tables
 * - High-contrast readable botanical card architecture
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: AdminDashboardViewModel,
    onNavigateToApprovalQueue: () -> Unit,
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
                            text = "Pusat Admin Kelurahan",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = ForestGreen
                        )
                        Text(
                            text = session?.displayName ?: session?.email ?: "Kelurahan Bubakan, Mijen",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
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
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Tutup",
                                tint = TextOnColor,
                                modifier = Modifier.size(16.dp)
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
                        CircularProgressIndicator(color = LeafGreen)
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
                            Mascot(type = MascotType.WARNING, size = 140.dp)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Gagal Memuat Data",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = state.message,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            PrimaryButton(
                                text = "Coba Lagi",
                                onClick = viewModel::loadData,
                                modifier = Modifier.width(180.dp)
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
                                onOpenLocations = { activeSection = AdminSection.LOCATIONS },
                                onOpenPlants = { activeSection = AdminSection.PLANTS },
                                onOpenApprovals = onNavigateToApprovalQueue,
                                onAddNewLocation = { onNavigateToLocationForm(null) },
                                onAddNewPlant = { onNavigateToMasterPlantForm(null) }
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

                        AdminSection.PLANTS -> {
                            AdminPlantsListContent(
                                plants = data.masterPlants,
                                onBackToHub = { activeSection = AdminSection.OVERVIEW },
                                onEditPlant = { onNavigateToMasterPlantForm(it.id) },
                                onAddPlant = { onNavigateToMasterPlantForm(null) }
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

@Composable
private fun AdminOverviewContent(
    data: AdminDashboardData,
    onOpenLocations: () -> Unit,
    onOpenPlants: () -> Unit,
    onOpenApprovals: () -> Unit,
    onAddNewLocation: () -> Unit,
    onAddNewPlant: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))

            // ── HEADER: MASCOT GREETING ──
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = SurfaceCard,
                border = BorderStroke(1.5.dp, BorderCard),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Halo Admin 🌱",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = ForestGreen
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Kelola kebun & ensiklopedia botani Bubakan",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            lineHeight = 20.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Mascot(
                        type = MascotType.GREETING,
                        size = 90.dp,
                        animateIdle = true
                    )
                }
            }
        }

        // ── METRIC SUMMARY PILLS ──
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricPill(
                    label = "Kebun Aktif",
                    value = "${data.activeLocations}/${data.totalLocations}",
                    color = LeafGreen,
                    bgColor = LeafGreenLight,
                    modifier = Modifier.weight(1f)
                )
                MetricPill(
                    label = "Spesies Master",
                    value = "${data.totalMasterPlants}",
                    color = ForestGreen,
                    bgColor = LeafGreenLight,
                    modifier = Modifier.weight(1f)
                )
                MetricPill(
                    label = "Approval",
                    value = "${data.pendingLocations.size}",
                    color = if (data.pendingLocations.isNotEmpty()) AlertOrange else NaturalGreen,
                    bgColor = if (data.pendingLocations.isNotEmpty()) WarmYellowLight else LeafGreenLight,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // ── CARD 1: KELOLA LOKASI ──
        item {
            LargeActionCard(
                title = "1. Kelola Lokasi Kebun",
                description = "${data.totalLocations} kebun terdaftar (Urban Farming & Taman Toga di RW se-Bubakan). Pantau kondisi lahan dan GPS.",
                icon = Icons.Default.Place,
                iconBg = LeafGreenLight,
                iconTint = ForestGreen,
                primaryButtonText = "Buka Daftar Kebun",
                onPrimaryClick = onOpenLocations,
                secondaryButtonText = "+ Tambah Kebun Baru",
                onSecondaryClick = onAddNewLocation
            )
        }

        // ── CARD 2: KELOLA TANAMAN ──
        item {
            LargeActionCard(
                title = "2. Kelola Tanaman Master",
                description = "${data.totalMasterPlants} spesies botani terverifikasi. Kelola taksonomi latin ilmiah, aksara Hanzi trilingual, dan audio.",
                icon = Icons.Default.Grass,
                iconBg = LeafGreenLight,
                iconTint = LeafGreen,
                primaryButtonText = "Buka Ensiklopedia Botani",
                onPrimaryClick = onOpenPlants,
                secondaryButtonText = "+ Tambah Spesies Baru",
                onSecondaryClick = onAddNewPlant
            )
        }

        // ── CARD 3: PERSETUJUAN (APPROVAL) ──
        item {
            val pendingCount = data.pendingLocations.size
            LargeActionCard(
                title = "3. Antrean Persetujuan Kebun",
                description = if (pendingCount > 0) {
                    "$pendingCount pengajuan kebun baru dari petugas lapangan RW siap untuk diverifikasi."
                } else {
                    "Semua pengajuan kebun telah ditinjau dan terverifikasi rapi."
                },
                badgeText = if (pendingCount > 0) "$pendingCount Perlu Ditinjau" else "Semua Beres ✨",
                icon = Icons.AutoMirrored.Filled.FactCheck,
                iconBg = if (pendingCount > 0) WarmYellowLight else LeafGreenLight,
                iconTint = if (pendingCount > 0) AlertOrange else NaturalGreen,
                primaryButtonText = "Buka Antrean Approval",
                onPrimaryClick = onOpenApprovals,
                secondaryButtonText = null,
                onSecondaryClick = null
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun MetricPill(
    label: String,
    value: String,
    color: Color,
    bgColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = bgColor,
        border = BorderStroke(1.dp, color.copy(alpha = 0.25f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun LargeActionCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBg: Color,
    iconTint: Color,
    badgeText: String? = null,
    primaryButtonText: String,
    onPrimaryClick: () -> Unit,
    secondaryButtonText: String?,
    onSecondaryClick: (() -> Unit)?
) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = SurfaceCard,
        border = BorderStroke(1.5.dp, BorderCard),
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = iconBg,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(24.dp)
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

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 21.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            PrimaryButton(
                text = primaryButtonText,
                onClick = onPrimaryClick,
                height = 48.dp
            )

            if (secondaryButtonText != null && onSecondaryClick != null) {
                Spacer(modifier = Modifier.height(10.dp))
                SecondaryButton(
                    text = secondaryButtonText,
                    onClick = onSecondaryClick,
                    height = 48.dp
                )
            }
        }
    }
}

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
            .padding(horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "← Kembali ke Menu Utama",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = ForestGreen,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onBackToHub)
                    .padding(vertical = 6.dp, horizontal = 4.dp)
            )

            PrimaryButton(
                text = "+ Tambah Kebun",
                onClick = onAddLocation,
                height = 42.dp,
                modifier = Modifier.width(150.dp)
            )
        }

        Text(
            text = "Daftar Kebun Binaan (${locations.size})",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(locations, key = { it.id }) { loc ->
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = SurfaceCard,
                    border = BorderStroke(1.5.dp, BorderCard),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = loc.name,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "RW ${loc.rw} • ${loc.address}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (loc.status == LocationStatus.PUBLISHED || loc.status == LocationStatus.ACTIVE) LeafGreenLight else WarmYellowLight
                            ) {
                                Text(
                                    text = if (loc.status == LocationStatus.PUBLISHED || loc.status == LocationStatus.ACTIVE) "Aktif / Terbit" else "Menunggu / Non-aktif",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (loc.status == LocationStatus.PUBLISHED || loc.status == LocationStatus.ACTIVE) NaturalGreen else AlertOrange,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        IconButton(
                            onClick = { onEditLocation(loc) },
                            modifier = Modifier.size(44.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Kebun",
                                tint = ForestGreen
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

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
            .padding(horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "← Kembali ke Menu Utama",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = ForestGreen,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onBackToHub)
                    .padding(vertical = 6.dp, horizontal = 4.dp)
            )

            PrimaryButton(
                text = "+ Tambah Spesies",
                onClick = onAddPlant,
                height = 42.dp,
                modifier = Modifier.width(160.dp)
            )
        }

        Text(
            text = "Ensiklopedia Botani Master (${plants.size})",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(plants, key = { it.id }) { plant ->
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = SurfaceCard,
                    border = BorderStroke(1.5.dp, BorderCard),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = plant.nameId,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                if (plant.nameMandarin?.isNotBlank() == true) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "${plant.nameMandarin} (${plant.pinyin ?: ""})",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = ForestGreen
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = plant.nameLatin,
                                style = MaterialTheme.typography.bodySmall,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                color = TextSecondary
                            )

                            if (plant.characteristics.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = plant.characteristics,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    maxLines = 2
                                )
                            }
                        }

                        IconButton(
                            onClick = { onEditPlant(plant) },
                            modifier = Modifier.size(44.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Tanaman",
                                tint = ForestGreen
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
