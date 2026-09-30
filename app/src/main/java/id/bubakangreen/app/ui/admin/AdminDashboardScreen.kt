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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn

import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import id.bubakangreen.app.core.result.Result
import id.bubakangreen.app.domain.model.Location
import id.bubakangreen.app.domain.model.LocationConditionLog
import id.bubakangreen.app.domain.model.LocationPlant
import id.bubakangreen.app.domain.model.LocationStatus
import id.bubakangreen.app.domain.model.LocationType
import id.bubakangreen.app.domain.model.MasterPlant
import id.bubakangreen.app.domain.model.PlantCondition
import id.bubakangreen.app.ui.common.UiState
import id.bubakangreen.app.ui.theme.BackgroundLight
import id.bubakangreen.app.ui.theme.BorderDivider
import id.bubakangreen.app.ui.theme.ErrorRed
import id.bubakangreen.app.ui.theme.OnPrimaryWhite
import id.bubakangreen.app.ui.theme.OnSurfaceDark
import id.bubakangreen.app.ui.theme.OnSurfaceVariant
import id.bubakangreen.app.ui.theme.OutlineGrey
import id.bubakangreen.app.ui.theme.PrimaryContainerMint
import id.bubakangreen.app.ui.theme.PrimaryForest
import id.bubakangreen.app.ui.theme.PrimaryGreen
import id.bubakangreen.app.ui.theme.PrimaryGreenDark
import id.bubakangreen.app.ui.theme.SecondarySage
import id.bubakangreen.app.ui.theme.StatusPendingOrange
import id.bubakangreen.app.ui.theme.StatusVerifiedGreen
import id.bubakangreen.app.ui.theme.SurfaceWhite
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var detailLocation by remember { mutableStateOf<Location?>(null) }
    var conditionDialogLocation by remember { mutableStateOf<Location?>(null) }
    var plantsDialogLocation by remember { mutableStateOf<Location?>(null) }
    var historyDialogLocation by remember { mutableStateOf<Location?>(null) }
    var deactivateLocationTarget by remember { mutableStateOf<Location?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BackgroundLight,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Admin Kelurahan Bubakan",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = OnSurfaceDark
                            )
                        )
                        Text(
                            text = session?.displayName ?: session?.email ?: "Pemerintah Kelurahan",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = OnSurfaceVariant
                            )
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
                            tint = OnSurfaceDark
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceWhite)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Action Feedback Banner
            actionMessage?.let { msg ->
                Surface(
                    color = PrimaryForest,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = msg,
                            color = OnPrimaryWhite,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { viewModel.clearActionMessage() },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Tutup",
                                tint = OnPrimaryWhite,
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
                        CircularProgressIndicator(color = PrimaryForest)
                    }
                }
                is UiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(onClick = viewModel::loadData) {
                            Text("Coba Lagi")
                        }
                    }
                }
                is UiState.Empty, is UiState.Success -> {
                    val data = (state as? UiState.Success)?.data ?: AdminDashboardData(0, 0, 0, "", emptyList(), emptyList())

                    Column(modifier = Modifier.fillMaxSize()) {
                        // Section 17 Metrics Grid
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SurfaceWhite)
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                StatCard(
                                    label = "Jumlah Lokasi",
                                    value = "${data.totalLocations}",
                                    icon = Icons.Default.LocationOn,
                                    tint = PrimaryForest,
                                    modifier = Modifier.weight(1f)
                                )
                                StatCard(
                                    label = "Lokasi Aktif",
                                    value = "${data.activeLocations}",
                                    icon = Icons.Default.Check,
                                    tint = StatusVerifiedGreen,
                                    modifier = Modifier.weight(1f)
                                )
                                StatCard(
                                    label = "Perlu Rawat",
                                    value = "${data.needsMaintenanceLocations}",
                                    icon = Icons.Default.Warning,
                                    tint = if (data.needsMaintenanceLocations > 0) StatusPendingOrange else SecondarySage,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = BackgroundLight,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = null,
                                        tint = OnSurfaceVariant,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Update Terbaru: ${data.latestUpdateText}",
                                        style = MaterialTheme.typography.labelSmall.copy(color = OnSurfaceVariant)
                                    )
                                }
                            }
                        }

                        // Section 17 Primary Actions Tabs: [ Kelola Lokasi ] & [ Kelola Tanaman ]
                        TabRow(
                            selectedTabIndex = selectedTabIndex,
                            containerColor = SurfaceWhite,
                            contentColor = PrimaryForest
                        ) {
                            Tab(
                                selected = selectedTabIndex == 0,
                                onClick = { selectedTabIndex = 0 },
                                text = { Text("Kelola Lokasi (${data.locations.size})", fontWeight = FontWeight.Bold) }
                            )
                            Tab(
                                selected = selectedTabIndex == 1,
                                onClick = { selectedTabIndex = 1 },
                                text = { Text("Kelola Tanaman (${data.masterPlants.size})", fontWeight = FontWeight.Bold) }
                            )
                        }

                        if (selectedTabIndex == 0) {
                            // Section 18: Location Management Flow
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Daftar Lahan Pertanian & Toga",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = OnSurfaceDark
                                    )
                                    Button(
                                        onClick = { onNavigateToLocationForm(null) },
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryForest),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.height(36.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("+ Tambah Lokasi", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    items(data.locations) { location ->
                                        LocationItemCard(
                                            location = location,
                                            onClick = { detailLocation = location }
                                        )
                                    }
                                }
                            }
                        } else {
                            // Master Plants Management View
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Master Ensiklopedia Tanaman",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = OnSurfaceDark
                                    )
                                    Button(
                                        onClick = { onNavigateToMasterPlantForm(null) },
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryForest),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.height(36.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("+ Tambah Spesies", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    items(data.masterPlants) { plant ->
                                        MasterPlantItemCard(
                                            plant = plant,
                                            onEdit = { onNavigateToMasterPlantForm(plant.id) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // ==========================================
    // SECTION 18: LOCATION DETAIL MODAL
    // ==========================================
    detailLocation?.let { loc ->
        Dialog(onDismissRequest = { detailLocation = null }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = SurfaceWhite,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Detail Lokasi",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = OnSurfaceDark
                        )
                        IconButton(onClick = { detailLocation = null }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(text = loc.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = PrimaryForest)
                    Text(
                        text = "${if (loc.type == LocationType.URBAN_FARMING) "Urban Farming" else "Taman Toga"} • RW ${loc.rw}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Detail Attributes
                    DetailRow(label = "Status Lokasi", value = loc.status.name)
                    DetailRow(
                        label = "Koordinat",
                        value = if (loc.latitude != 0.0) "${loc.latitude}, ${loc.longitude}" else "Pending GPS"
                    )
                    DetailRow(
                        label = "Kondisi Lahan",
                        value = if (loc.conditionNote.isNotBlank()) "${loc.status.name} - ${loc.conditionNote}" else loc.status.name
                    )
                    loc.conditionUpdatedBy?.let {
                        DetailRow(label = "Diperbarui Oleh", value = it)
                    }
                    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
                    DetailRow(
                        label = "Terakhir Diperbarui",
                        value = sdf.format(Date(loc.conditionUpdatedAt ?: loc.updatedAt))
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Section 18 Actions: [ Edit Lokasi ], [ Update Kondisi ], [ Kelola Tanaman ], [ Riwayat ]
                    Button(
                        onClick = {
                            detailLocation = null
                            onNavigateToLocationForm(loc.id)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryForest),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Edit Lokasi")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = {
                            val target = loc
                            detailLocation = null
                            conditionDialogLocation = target
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Update Kondisi")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = {
                            val target = loc
                            detailLocation = null
                            plantsDialogLocation = target
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Kelola Tanaman di Lokasi")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = {
                            val target = loc
                            detailLocation = null
                            historyDialogLocation = target
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Riwayat Kondisi")
                    }


                    Spacer(modifier = Modifier.height(14.dp))

                    // Soft Delete / Deactivate Button (Section 8)
                    TextButton(
                        onClick = {
                            val target = loc
                            detailLocation = null
                            deactivateLocationTarget = target
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = ErrorRed),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Nonaktifkan Lokasi (Soft Delete)", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }

    // ==========================================
    // SECTION 10: UPDATE KONDISI DIALOG
    // ==========================================
    conditionDialogLocation?.let { loc ->
        var selectedStatus by remember { mutableStateOf(loc.status.name) }
        var conditionNote by remember { mutableStateOf(loc.conditionNote) }
        var photoUrl by remember { mutableStateOf(loc.coverPhotoUrl ?: "") }

        AlertDialog(
            onDismissRequest = { conditionDialogLocation = null },
            title = { Text("Update Kondisi Lahan", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(text = loc.name, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Status Kondisi:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("ACTIVE", "NEEDS_MAINTENANCE", "TEMPORARILY_INACTIVE").forEach { statusName ->
                            FilterChip(
                                selected = selectedStatus == statusName,
                                onClick = { selectedStatus = statusName },
                                label = {
                                    Text(
                                        when (statusName) {
                                            "ACTIVE" -> "Aktif"
                                            "NEEDS_MAINTENANCE" -> "Perlu Rawat"
                                            else -> "Nonaktif Smtr"
                                        },
                                        fontSize = 11.sp
                                    )
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = conditionNote,
                        onValueChange = { conditionNote = it },
                        label = { Text("Catatan Kondisi") },
                        placeholder = { Text("contoh: Bedengan 2 baru disiram & dipupuk") },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = photoUrl,
                        onValueChange = { photoUrl = it },
                        label = { Text("URL Foto Terbaru (Opsional)") },
                        placeholder = { Text("https://...") },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateLandCondition(
                            locationId = loc.id,
                            status = selectedStatus,
                            note = conditionNote,
                            photoUrl = photoUrl.ifBlank { null },
                            onComplete = { conditionDialogLocation = null }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryForest)
                ) {
                    Text("Simpan Kondisi")
                }
            },
            dismissButton = {
                TextButton(onClick = { conditionDialogLocation = null }) {
                    Text("Batal")
                }
            }
        )
    }

    // ==========================================
    // SECTION 12 & 13: KELOLA TANAMAN DI LOKASI
    // ==========================================
    plantsDialogLocation?.let { loc ->
        val locPlantsResult by viewModel.getLocationPlants(loc.id).collectAsState(initial = Result.Loading)
        val uiSuccess = uiState as? UiState.Success
        val allMasterPlants = uiSuccess?.data?.masterPlants ?: emptyList()

        var showAddPlantForm by remember { mutableStateOf(false) }
        var selectedMasterId by remember { mutableStateOf(allMasterPlants.firstOrNull()?.id ?: "") }
        var addCondition by remember { mutableStateOf(PlantCondition.GOOD) }
        var addQuantity by remember { mutableStateOf("1") }
        var addNotes by remember { mutableStateOf("") }

        Dialog(onDismissRequest = { plantsDialogLocation = null }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = SurfaceWhite,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Tanaman di Lokasi", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(loc.name, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                        }
                        IconButton(onClick = { plantsDialogLocation = null }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup")
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (!showAddPlantForm) {
                        Button(
                            onClick = { showAddPlantForm = true },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryForest),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("+ Tambah Tanaman ke Lokasi")
                        }
                    } else {
                        // Section 12 Inline Form to Add Existing Master Plant
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = BackgroundLight),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("Pilih Master Tanaman:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(6.dp))

                                allMasterPlants.forEach { mp ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { selectedMasterId = mp.id }
                                            .padding(vertical = 4.dp)
                                    ) {
                                        androidx.compose.material3.RadioButton(
                                            selected = selectedMasterId == mp.id,
                                            onClick = { selectedMasterId = mp.id }
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Column {
                                            Text(mp.name.ifBlank { mp.nameId }, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                            Text(mp.scientificName.ifBlank { mp.nameLatin }, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = addQuantity,
                                    onValueChange = { addQuantity = it },
                                    label = { Text("Jumlah (Polybag/Tanaman)") },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = addNotes,
                                    onValueChange = { addNotes = it },
                                    label = { Text("Catatan / Blok Bedengan") },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = {
                                            viewModel.addPlantToLocation(
                                                locationId = loc.id,
                                                plantId = selectedMasterId,
                                                condition = addCondition,
                                                quantity = addQuantity.toIntOrNull() ?: 1,
                                                notes = addNotes.ifBlank { null },
                                                onComplete = { showAddPlantForm = false }
                                            )
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryForest),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Simpan")
                                    }
                                    OutlinedButton(
                                        onClick = { showAddPlantForm = false },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Batal")
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Existing plants list at location
                    when (val plantRes = locPlantsResult) {
                        is Result.Loading -> CircularProgressIndicator(color = PrimaryForest, modifier = Modifier.align(Alignment.CenterHorizontally))
                        is Result.Error -> Text("Gagal memuat tanaman.", color = ErrorRed)
                        is Result.Success -> {
                            val items = plantRes.data
                            if (items.isEmpty()) {
                                Text("Belum ada tanaman di lokasi ini.", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items.forEach { lp ->
                                        val master = allMasterPlants.find { it.id == lp.plantId || it.id == lp.masterPlantId }
                                        LocationPlantManageCard(
                                            locationPlant = lp,
                                            masterPlantName = master?.name?.ifBlank { master.nameId } ?: lp.plantId,
                                            onUpdateCondition = { newCondition ->
                                                viewModel.updatePlantCondition(
                                                    locationPlant = lp,
                                                    condition = newCondition,
                                                    quantity = lp.quantity,
                                                    notes = lp.notes,
                                                    isPresent = lp.isPresent,
                                                    onComplete = {}
                                                )
                                            },
                                            onTogglePresence = {
                                                viewModel.updatePlantCondition(
                                                    locationPlant = lp,
                                                    condition = lp.condition,
                                                    quantity = lp.quantity,
                                                    notes = lp.notes,
                                                    isPresent = !lp.isPresent,
                                                    onComplete = {}
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // ==========================================
    // SECTION 16: RIWAYAT KONDISI DIALOG
    // ==========================================
    historyDialogLocation?.let { loc ->
        val logsResult by viewModel.getLocationConditionLogs(loc.id).collectAsState(initial = Result.Loading)
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))

        AlertDialog(
            onDismissRequest = { historyDialogLocation = null },
            title = { Text("Riwayat Kondisi Lahan", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(loc.name, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                    Spacer(modifier = Modifier.height(12.dp))

                    when (val lRes = logsResult) {
                        is Result.Loading -> CircularProgressIndicator(color = PrimaryForest)
                        is Result.Error -> Text("Gagal memuat riwayat.", color = ErrorRed)
                        is Result.Success -> {
                            val logs = lRes.data
                            if (logs.isEmpty()) {
                                Text("Belum ada riwayat catatan kondisi.", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    logs.forEach { log ->
                                        Card(
                                            shape = RoundedCornerShape(8.dp),
                                            colors = CardDefaults.cardColors(containerColor = BackgroundLight),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(modifier = Modifier.padding(10.dp)) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text(log.status, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = PrimaryForest)
                                                    Text(sdf.format(Date(log.updatedAt)), style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                                                }
                                                if (log.note.isNotBlank()) {
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Text(log.note, style = MaterialTheme.typography.bodySmall)
                                                }
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text("Oleh: ${log.updatedBy}", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { historyDialogLocation = null },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryForest)
                ) {
                    Text("Tutup")
                }
            }
        )
    }

    // Deactivation Dialog
    deactivateLocationTarget?.let { loc ->
        AlertDialog(
            onDismissRequest = { deactivateLocationTarget = null },
            title = { Text("Nonaktifkan Lokasi?", fontWeight = FontWeight.Bold) },
            text = { Text("Lokasi '${loc.name}' akan disembunyikan dari aplikasi publik (isPublished = false).") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deactivateLocation(loc.id) {
                            deactivateLocationTarget = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) {
                    Text("Nonaktifkan")
                }
            },
            dismissButton = {
                TextButton(onClick = { deactivateLocationTarget = null }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
private fun LocationItemCard(
    location: Location,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, BorderDivider),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = location.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = OnSurfaceDark
                )
                StatusBadge(status = location.status)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${if (location.type == LocationType.URBAN_FARMING) "Urban Farming" else "Taman Toga"} • RW ${location.rw}",
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceVariant
            )
            if (location.conditionNote.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Kondisi: ${location.conditionNote}",
                    style = MaterialTheme.typography.bodySmall.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                    color = PrimaryForest
                )
            }
        }
    }
}

@Composable
private fun MasterPlantItemCard(
    plant: MasterPlant,
    onEdit: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, BorderDivider),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = plant.name.ifBlank { plant.nameId },
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = OnSurfaceDark
                )
                Text(
                    text = plant.scientificName.ifBlank { plant.nameLatin },
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant
                )
                plant.mandarinName?.let {
                    Text(
                        text = "$it (${plant.mandarinPinyin ?: plant.pinyin ?: ""})",
                        style = MaterialTheme.typography.labelSmall,
                        color = PrimaryForest
                    )
                }
            }
            IconButton(onClick = onEdit) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Master Tanaman", tint = PrimaryForest)
            }
        }
    }
}

@Composable
private fun LocationPlantManageCard(
    locationPlant: LocationPlant,
    masterPlantName: String,
    onUpdateCondition: (PlantCondition) -> Unit,
    onTogglePresence: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        border = BorderStroke(1.dp, BorderDivider),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(masterPlantName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when (locationPlant.condition) {
                        PlantCondition.GOOD -> PrimaryContainerMint
                        PlantCondition.NEEDS_ATTENTION -> StatusPendingOrange.copy(alpha = 0.2f)
                        PlantCondition.NOT_AVAILABLE -> ErrorRed.copy(alpha = 0.15f)
                    }
                ) {
                    Text(
                        text = when (locationPlant.condition) {
                            PlantCondition.GOOD -> "Subur (GOOD)"
                            PlantCondition.NEEDS_ATTENTION -> "Perlu Rawat"
                            PlantCondition.NOT_AVAILABLE -> "Kosong"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = when (locationPlant.condition) {
                            PlantCondition.GOOD -> PrimaryForest
                            PlantCondition.NEEDS_ATTENTION -> StatusPendingOrange
                            PlantCondition.NOT_AVAILABLE -> ErrorRed
                        },
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text("Jumlah: ${locationPlant.quantity} polybag/tanaman", style = MaterialTheme.typography.bodySmall)
            locationPlant.notes?.let {
                Text("Catatan: $it", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quick condition buttons
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                OutlinedButton(
                    onClick = { onUpdateCondition(PlantCondition.GOOD) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("Subur", fontSize = 11.sp)
                }
                OutlinedButton(
                    onClick = { onUpdateCondition(PlantCondition.NEEDS_ATTENTION) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("Rawat", fontSize = 11.sp)
                }
                OutlinedButton(
                    onClick = { onUpdateCondition(PlantCondition.NOT_AVAILABLE) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("Kosong", fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(status: LocationStatus) {
    val (text, bgColor, textColor) = when (status) {
        LocationStatus.ACTIVE, LocationStatus.PUBLISHED -> Triple("Aktif", PrimaryContainerMint, PrimaryForest)
        LocationStatus.NEEDS_MAINTENANCE -> Triple("Perlu Perawatan", StatusPendingOrange.copy(alpha = 0.2f), StatusPendingOrange)
        LocationStatus.TEMPORARILY_INACTIVE -> Triple("Nonaktif Smtr", BackgroundLight, OnSurfaceVariant)
        LocationStatus.INACTIVE, LocationStatus.ARCHIVED -> Triple("Tidak Aktif", ErrorRed.copy(alpha = 0.15f), ErrorRed)
        LocationStatus.PENDING_APPROVAL -> Triple("Review", StatusPendingOrange.copy(alpha = 0.2f), StatusPendingOrange)
        LocationStatus.DRAFT -> Triple("Draft", BackgroundLight, OnSurfaceVariant)
    }

    Surface(
        shape = RoundedCornerShape(6.dp),
        color = bgColor
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = textColor),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = OnSurfaceDark)
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = OnSurfaceDark
                )
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = OnSurfaceVariant,
                    fontSize = 11.sp
                )
            )
        }
    }
}
