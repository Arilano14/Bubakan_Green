package id.bubakangreen.app.ui.pic

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.bubakangreen.app.data.location.AndroidLocationClient
import id.bubakangreen.app.domain.model.LocationType
import id.bubakangreen.app.ui.theme.BackgroundLight
import id.bubakangreen.app.ui.theme.OnPrimaryWhite
import id.bubakangreen.app.ui.theme.OnSurfaceDark
import id.bubakangreen.app.ui.theme.OnSurfaceVariant
import id.bubakangreen.app.ui.theme.OutlineGrey
import id.bubakangreen.app.ui.theme.PrimaryContainerMint
import id.bubakangreen.app.ui.theme.PrimaryForest
import id.bubakangreen.app.ui.theme.StatusPendingOrange
import id.bubakangreen.app.ui.theme.StatusVerifiedGreen
import id.bubakangreen.app.ui.theme.SurfaceWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationFormScreen(
    viewModel: LocationFormViewModel,
    picUid: String,
    locationId: String?,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val locationClient = AndroidLocationClient(context)

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fineGranted || coarseGranted) {
            viewModel.captureGps(locationClient)
        }
    }

    LaunchedEffect(locationId) {
        viewModel.loadExistingLocation(locationId)
    }

    LaunchedEffect(Unit) {
        viewModel.saveSuccessEvent.collect {
            onNavigateBack()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BackgroundLight,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (locationId != null) "Edit Profil Kebun" else "Daftarkan Kebun Baru",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = OnSurfaceDark
                        )
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali ke Dashboard",
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
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // Error Banner
            if (state.validationError != null) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Text(
                        text = state.validationError ?: "",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // Garden Name Field
            Text(
                text = "Nama Kebun Warga",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = OnSurfaceDark)
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = state.name,
                onValueChange = viewModel::onNameChange,
                placeholder = { Text("Contoh: Kebun Toga RW 03") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryForest,
                    unfocusedBorderColor = OutlineGrey,
                    focusedContainerColor = SurfaceWhite,
                    unfocusedContainerColor = SurfaceWhite
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Garden Type Selection
            Text(
                text = "Kategori Kebun",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = OnSurfaceDark)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = state.type == LocationType.URBAN_FARMING,
                    onClick = { viewModel.onTypeChange(LocationType.URBAN_FARMING) },
                    label = { Text("Urban Farming") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryContainerMint,
                        selectedLabelColor = PrimaryForest
                    )
                )
                FilterChip(
                    selected = state.type == LocationType.TAMAN_TOGA,
                    onClick = { viewModel.onTypeChange(LocationType.TAMAN_TOGA) },
                    label = { Text("Taman Toga") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryContainerMint,
                        selectedLabelColor = PrimaryForest
                    )
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // RW Selection
            Text(
                text = "Wilayah RW (Kelurahan Bubakan)",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = OnSurfaceDark)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                (1..8).map { String.format("%02d", it) }.forEach { rw ->
                    FilterChip(
                        selected = state.rw == rw,
                        onClick = { viewModel.onRwChange(rw) },
                        label = { Text("RW $rw") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryForest,
                            selectedLabelColor = OnPrimaryWhite
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Address Field
            Text(
                text = "Alamat / Patokan Lokasi",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = OnSurfaceDark)
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = state.address,
                onValueChange = viewModel::onAddressChange,
                placeholder = { Text("Contoh: Samping Balai RW 03, RT 02") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryForest,
                    unfocusedBorderColor = OutlineGrey,
                    focusedContainerColor = SurfaceWhite,
                    unfocusedContainerColor = SurfaceWhite
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Description Field
            Text(
                text = "Deskripsi Kebun",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = OnSurfaceDark)
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = state.description,
                onValueChange = viewModel::onDescriptionChange,
                placeholder = { Text("Jelaskan jenis tanaman, luasan, dan pengelola kebun...") },
                minLines = 3,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryForest,
                    unfocusedBorderColor = OutlineGrey,
                    focusedContainerColor = SurfaceWhite,
                    unfocusedContainerColor = SurfaceWhite
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(22.dp))

            // Single-Shot GPS Capture Widget
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Titik Koordinat GPS",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = OnSurfaceDark
                            )
                        )
                        if (state.latitude != null && state.longitude != null) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if ((state.accuracyMeters ?: 99f) <= 25f) PrimaryContainerMint else BackgroundLight
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = if ((state.accuracyMeters ?: 99f) <= 25f) StatusVerifiedGreen else StatusPendingOrange,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Akurasi: ±${state.accuracyMeters?.toInt() ?: 0}m",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if ((state.accuracyMeters ?: 99f) <= 25f) StatusVerifiedGreen else StatusPendingOrange,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Ambil titik koordinat saat berada langsung di lokasi kebun. Sistem merekam akurasi horizontal perangkat.",
                        style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceVariant)
                    )

                    if (state.latitude != null && state.longitude != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = BackgroundLight,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "Lat: ${String.format("%.5f", state.latitude)}, Long: ${String.format("%.5f", state.longitude)}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = OnSurfaceDark
                                    )
                                )
                                Text(
                                    text = "Status: Menunggu Verifikasi Admin",
                                    style = MaterialTheme.typography.labelSmall.copy(color = OnSurfaceVariant)
                                )
                            }
                        }
                    }

                    if (state.gpsWarning != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = StatusPendingOrange,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = state.gpsWarning ?: "",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedButton(
                        onClick = {
                            permissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        },
                        enabled = !state.isGpsLoading,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        if (state.isGpsLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = PrimaryForest
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Mengunci Sinyal Satelit...")
                        } else {
                            Icon(
                                imageVector = if (state.latitude != null) Icons.Default.Refresh else Icons.Default.LocationOn,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (state.latitude != null) "Perbarui Titik GPS" else "Kunci Titik Lokasi GPS",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Submit Button
            Button(
                onClick = { viewModel.saveLocation(picUid) },
                enabled = !state.isSaving,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryForest,
                    contentColor = OnPrimaryWhite
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp,
                        color = OnPrimaryWhite
                    )
                } else {
                    Text(
                        text = if (locationId != null) "Simpan Perubahan" else "Kirim Pengajuan Kebun",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
