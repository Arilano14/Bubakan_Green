package id.bubakangreen.app.ui.pic

import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import id.bubakangreen.app.core.util.ImageCompressor
import id.bubakangreen.app.data.location.AndroidLocationClient
import id.bubakangreen.app.domain.model.LocationType
import id.bubakangreen.app.domain.model.MasterPlant
import id.bubakangreen.app.domain.model.RegionTag
import id.bubakangreen.app.ui.theme.AlertOrange
import id.bubakangreen.app.ui.theme.BackgroundLight
import id.bubakangreen.app.ui.theme.BorderCard
import id.bubakangreen.app.ui.theme.ForestGreen
import id.bubakangreen.app.ui.theme.FriendlyRed
import id.bubakangreen.app.ui.theme.LeafGreen
import id.bubakangreen.app.ui.theme.LeafGreenLight
import id.bubakangreen.app.ui.theme.OnPrimaryWhite
import id.bubakangreen.app.ui.theme.OnSurfaceDark
import id.bubakangreen.app.ui.theme.OnSurfaceVariant
import id.bubakangreen.app.ui.theme.OutlineGrey
import id.bubakangreen.app.ui.theme.PrimaryContainerMint
import id.bubakangreen.app.ui.theme.PrimaryForest
import id.bubakangreen.app.ui.theme.StatusPendingOrange
import id.bubakangreen.app.ui.theme.StatusVerifiedGreen
import id.bubakangreen.app.ui.theme.SurfaceCard
import id.bubakangreen.app.ui.theme.SurfaceWhite
import id.bubakangreen.app.ui.theme.TextPrimary
import id.bubakangreen.app.ui.theme.TextSecondary
import kotlinx.coroutines.launch

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
    val scope = rememberCoroutineScope()
    val locationClient = AndroidLocationClient(context)

    var cameraNoticeMessage by remember { mutableStateOf<String?>(null) }
    var showUrlInputDialog by remember { mutableStateOf(false) }
    var replaceIndexTarget by remember { mutableStateOf<Int?>(null) }
    var rawUrlInput by remember { mutableStateOf("") }
    var isProcessingPhoto by remember { mutableStateOf(false) }

    // Plant Inventory State (Phase 4 & 5)
    var showPlantPickerSheet by remember { mutableStateOf(false) }
    var plantSearchQuery by remember { mutableStateOf("") }
    val selectedPlantIds = remember { mutableStateListOf<String>() }
    var plantToRemove by remember { mutableStateOf<MasterPlant?>(null) }

    // Minimal Plant Creation from Lahan State (Phase 5)
    var showCreateMinimalPlantDialog by remember { mutableStateOf(false) }
    var minimalPlantName by remember { mutableStateOf("") }
    var minimalPhotoBytes by remember { mutableStateOf<ByteArray?>(null) }
    var minimalPhotoPreviewUri by remember { mutableStateOf<Uri?>(null) }
    var minimalPhotoError by remember { mutableStateOf<String?>(null) }
    var isProcessingMinimalPhoto by remember { mutableStateOf(false) }

    // Minimal Plant Camera Launcher
    val minimalCameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            scope.launch {
                isProcessingMinimalPhoto = true
                try {
                    val compressed = ImageCompressor.compressBitmap(bitmap, maxDimension = 1600, quality = 80)
                    minimalPhotoBytes = compressed
                    minimalPhotoError = null
                } catch (e: Exception) {
                    minimalPhotoError = "Gagal memproses foto kamera: ${e.localizedMessage}"
                } finally {
                    isProcessingMinimalPhoto = false
                }
            }
        }
    }

    // Minimal Plant Gallery Picker Launcher
    val minimalPhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            minimalPhotoPreviewUri = uri
            scope.launch {
                isProcessingMinimalPhoto = true
                try {
                    val compressed = ImageCompressor.compressAndResizeImage(context, uri, maxDimension = 1600, quality = 80)
                    minimalPhotoBytes = compressed
                    minimalPhotoError = null
                } catch (e: Exception) {
                    minimalPhotoError = "Gagal memproses foto galeri: ${e.localizedMessage}"
                } finally {
                    isProcessingMinimalPhoto = false
                }
            }
        }
    }

    // GPS Permission
    val gpsPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fineGranted || coarseGranted) {
            viewModel.captureGps(locationClient)
        }
    }

    // Camera Capture Launcher (returns thumbnail bitmap)
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            scope.launch {
                isProcessingPhoto = true
                try {
                    val compressed = ImageCompressor.compressBitmap(bitmap, maxDimension = 1600, quality = 80)
                    val cachedPath = ImageCompressor.saveCompressedToAppCache(context, compressed, "lahan")
                    val replaceIdx = replaceIndexTarget
                    if (replaceIdx != null) {
                        viewModel.onReplacePhoto(replaceIdx, cachedPath)
                        replaceIndexTarget = null
                    } else {
                        viewModel.onAddPhoto(cachedPath)
                    }
                } catch (e: Exception) {
                    cameraNoticeMessage = "Gagal memproses foto: ${e.localizedMessage}"
                } finally {
                    isProcessingPhoto = false
                }
            }
        }
    }

    // Camera Permission Launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(null)
        } else {
            cameraNoticeMessage = "Kamera tidak diizinkan. Gunakan Galeri untuk memilih foto."
        }
    }

    // Photo Picker Launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                isProcessingPhoto = true
                try {
                    val compressed = ImageCompressor.compressAndResizeImage(context, uri, maxDimension = 1600, quality = 80)
                    val cachedPath = ImageCompressor.saveCompressedToAppCache(context, compressed, "lahan")
                    val replaceIdx = replaceIndexTarget
                    if (replaceIdx != null) {
                        viewModel.onReplacePhoto(replaceIdx, cachedPath)
                        replaceIndexTarget = null
                    } else {
                        viewModel.onAddPhoto(cachedPath)
                    }
                } catch (e: Exception) {
                    cameraNoticeMessage = "Gagal memproses gambar galeri: ${e.localizedMessage}"
                } finally {
                    isProcessingPhoto = false
                }
            }
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
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            Surface(
                color = SurfaceWhite,
                shadowElevation = 8.dp,
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .imePadding()
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    id.bubakangreen.app.ui.components.PrimaryButton(
                        text = if (locationId != null) "Simpan Perubahan Lahan" else "Tambah Lahan",
                        onClick = { viewModel.saveLocation(picUid) },
                        enabled = !state.isSaving && !isProcessingPhoto,
                        loading = state.isSaving,
                        height = 54.dp,
                        shapeRadius = 27.dp
                    )
                }
            }
        },
        containerColor = BackgroundLight,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (locationId != null) "Edit Lahan" else "Tambah Lahan",
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

            // Camera Notice Message Banner
            if (cameraNoticeMessage != null) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = cameraNoticeMessage ?: "",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(onClick = { cameraNoticeMessage = null }) {
                            Text("Tutup", fontSize = 12.sp)
                        }
                    }
                }
            }

            // 1. Nama Lahan *
            Text(
                text = "Nama Lahan *",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = OnSurfaceDark)
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = state.name,
                onValueChange = viewModel::onNameChange,
                placeholder = { Text("Contoh: Urban Farming RW 02") },
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

            // 2. Kategori Lahan *
            Text(
                text = "Jenis Lahan *",
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

            // 3. Tag Wilayah * (Strict 6 tags)
            Text(
                text = "Tag Wilayah *",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = OnSurfaceDark)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                RegionTag.entries.forEach { tag ->
                    FilterChip(
                        selected = state.regionTag == tag,
                        onClick = { viewModel.onRegionTagChange(tag) },
                        label = { Text(tag.label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryForest,
                            selectedLabelColor = OnPrimaryWhite
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 4. Deskripsi Lahan (Opsional)
            Text(
                text = "Deskripsi Lahan (Opsional)",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = OnSurfaceDark)
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = state.description,
                onValueChange = viewModel::onDescriptionChange,
                placeholder = { Text("Jelaskan jenis tanaman, luasan, dan pengelola lahan...") },
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

            Spacer(modifier = Modifier.height(18.dp))

            // 5. Foto Lahan (0/3 - 3/3)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Foto Lahan (${state.photos.size}/3)",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = OnSurfaceDark)
                )
                if (isProcessingPhoto) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = PrimaryForest)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Action buttons row for adding photos
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val canAddPhoto = state.photos.size < 3 && !isProcessingPhoto

                OutlinedButton(
                    onClick = {
                        replaceIndexTarget = null
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    },
                    enabled = canAddPhoto,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryForest)
                ) {
                    Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Ambil Foto", fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = {
                        replaceIndexTarget = null
                        photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                    enabled = canAddPhoto,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryForest)
                ) {
                    Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Pilih dari Galeri", fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = {
                        replaceIndexTarget = null
                        rawUrlInput = ""
                        showUrlInputDialog = true
                    },
                    enabled = canAddPhoto,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryForest)
                ) {
                    Icon(imageVector = Icons.Default.Link, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("+ Tautan URL", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Thumbnail Grid
            if (state.photos.isEmpty()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SurfaceCard,
                    border = BorderStroke(1.dp, BorderCard),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Belum ada foto lahan.",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVariant
                        )
                        Text(
                            text = "Dapat menambahkan hingga maksimal 3 foto.",
                            style = MaterialTheme.typography.labelSmall,
                            color = PrimaryForest
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    state.photos.forEachIndexed { index, photoUrl ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, BorderCard),
                            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                            modifier = Modifier.width(110.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(98.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(BackgroundLight)
                                ) {
                                    AsyncImage(
                                        model = photoUrl,
                                        contentDescription = "Thumbnail foto lahan ${index + 1}",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    IconButton(
                                        onClick = {
                                            replaceIndexTarget = index
                                            photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Ganti foto ${index + 1}",
                                            tint = ForestGreen,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    IconButton(
                                        onClick = { viewModel.onRemovePhoto(index) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Hapus foto ${index + 1}",
                                            tint = FriendlyRed,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 6. Alamat / Patokan Lokasi (Opsional)
            Text(
                text = "Alamat / Patokan Lokasi",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = OnSurfaceDark)
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = state.address,
                onValueChange = viewModel::onAddressChange,
                placeholder = { Text("Contoh: Samping Balai RW 02, RT 01") },
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

            Spacer(modifier = Modifier.height(22.dp))

            // 7. Single-Shot GPS Capture Widget
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                border = BorderStroke(1.dp, BorderCard),
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
                        text = "Ambil titik koordinat saat berada langsung di lokasi lahan. Otomatis tervalidasi di batas administrasi Bubakan.",
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
                            gpsPermissionLauncher.launch(
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

            // 8. Koleksi Tanaman pada Lahan (PHASE 4 & 5)
            if (locationId != null) {
                Spacer(modifier = Modifier.height(24.dp))
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    border = BorderStroke(1.dp, BorderCard),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Koleksi Tanaman",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = OnSurfaceDark
                                    )
                                )
                                Text(
                                    text = "${state.assignedPlants.size} spesies tanaman terhubung",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = OnSurfaceVariant
                                    )
                                )
                            }

                            Button(
                                onClick = {
                                    selectedPlantIds.clear()
                                    plantSearchQuery = ""
                                    showPlantPickerSheet = true
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryForest),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("+ Tambah Tanaman", fontSize = 12.sp)
                            }
                        }

                        // Feedback message if any
                        if (state.inventoryFeedback != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = PrimaryContainerMint,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = state.inventoryFeedback ?: "",
                                        style = MaterialTheme.typography.bodySmall.copy(color = ForestGreen),
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(
                                        onClick = { viewModel.clearInventoryFeedback() },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = "Tutup", tint = ForestGreen, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (state.isInventoryLoading) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = PrimaryForest)
                            }
                        } else if (state.assignedPlants.isEmpty()) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = BackgroundLight,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Belum ada koleksi tanaman di lahan ini.",
                                        style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceVariant)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Klik '+ Tambah Tanaman' untuk menambahkan dari ensiklopedia.",
                                        style = MaterialTheme.typography.labelSmall.copy(color = PrimaryForest)
                                    )
                                }
                            }
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                state.assignedPlants.forEach { plant ->
                                    Card(
                                        shape = RoundedCornerShape(10.dp),
                                        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                                        border = BorderStroke(1.dp, BorderCard),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(48.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(BackgroundLight)
                                            ) {
                                                AsyncImage(
                                                    model = plant.primaryPhotoUrl ?: plant.defaultPhotoUrl,
                                                    contentDescription = plant.nameId,
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = plant.nameId,
                                                    style = MaterialTheme.typography.titleSmall.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        color = TextPrimary
                                                    )
                                                )
                                                if (plant.nameLatin.isNotBlank()) {
                                                    Text(
                                                        text = plant.nameLatin,
                                                        style = MaterialTheme.typography.bodySmall.copy(
                                                            fontStyle = FontStyle.Italic,
                                                            color = TextSecondary
                                                        )
                                                    )
                                                }
                                            }
                                            OutlinedButton(
                                                onClick = { plantToRemove = plant },
                                                colors = ButtonDefaults.outlinedButtonColors(contentColor = FriendlyRed),
                                                border = BorderStroke(1.dp, FriendlyRed.copy(alpha = 0.5f)),
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                                modifier = Modifier.height(34.dp)
                                            ) {
                                                Text("Lepas dari Lahan", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Dialog for Direct HTTPS URL Input
    if (showUrlInputDialog) {
        AlertDialog(
            onDismissRequest = { showUrlInputDialog = false },
            title = { Text("Masukkan Tautan Foto") },
            text = {
                Column {
                    Text("Tautan harus menggunakan protokol HTTPS (misal foto Wikimedia atau CDN web).", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = rawUrlInput,
                        onValueChange = { rawUrlInput = it },
                        placeholder = { Text("https://...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val trimmed = rawUrlInput.trim()
                        if (trimmed.startsWith("https://", ignoreCase = true) || trimmed.startsWith("http://", ignoreCase = true)) {
                            val replaceIdx = replaceIndexTarget
                            if (replaceIdx != null) {
                                viewModel.onReplacePhoto(replaceIdx, trimmed)
                                replaceIndexTarget = null
                            } else {
                                viewModel.onAddPhoto(trimmed)
                            }
                            showUrlInputDialog = false
                        } else {
                            cameraNoticeMessage = "URL harus diawali dengan https://"
                        }
                    }
                ) {
                    Text("Tambahkan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showUrlInputDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    // Bottom Sheet: Plant Picker (Phase 4)
    if (showPlantPickerSheet) {
        ModalBottomSheet(
            onDismissRequest = { showPlantPickerSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = SurfaceWhite
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tambah Tanaman ke Lahan",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = OnSurfaceDark
                        )
                    )
                    IconButton(onClick = { showPlantPickerSheet = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Search field
                OutlinedTextField(
                    value = plantSearchQuery,
                    onValueChange = { plantSearchQuery = it },
                    placeholder = { Text("Cari nama tanaman (Indonesia / Latin)...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (plantSearchQuery.isNotEmpty()) {
                            IconButton(onClick = { plantSearchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Bersihkan")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryForest,
                        unfocusedBorderColor = OutlineGrey,
                        focusedContainerColor = SurfaceWhite,
                        unfocusedContainerColor = SurfaceWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // "Tanaman belum terdaftar?" -> "+ Tambah Tanaman Baru"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tanaman belum terdaftar?",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                    TextButton(
                        onClick = {
                            minimalPlantName = ""
                            minimalPhotoBytes = null
                            minimalPhotoPreviewUri = null
                            minimalPhotoError = null
                            showCreateMinimalPlantDialog = true
                        }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = PrimaryForest)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+ Tambah Tanaman Baru", fontWeight = FontWeight.Bold, color = PrimaryForest, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                val currentlyAssignedIds = remember(state.assignedPlants) {
                    state.assignedPlants.map { it.id }.toSet()
                }

                val filteredPlants = remember(state.allMasterPlants, plantSearchQuery) {
                    val query = plantSearchQuery.trim().lowercase()
                    val list = if (query.isEmpty()) {
                        state.allMasterPlants
                    } else {
                        state.allMasterPlants.filter {
                            it.nameId.lowercase().contains(query) ||
                            it.nameLatin.lowercase().contains(query) ||
                            (it.nameMandarin?.lowercase()?.contains(query) == true)
                        }
                    }
                    list.sortedBy { it.nameId.lowercase() }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .heightIn(max = 380.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (filteredPlants.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (plantSearchQuery.isBlank()) "Tidak ada tanaman di ensiklopedia." else "Tanaman '$plantSearchQuery' tidak ditemukan.",
                                    style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceVariant)
                                )
                            }
                        }
                    }

                    items(filteredPlants, key = { it.id }) { plant ->
                        val isAssigned = currentlyAssignedIds.contains(plant.id)
                        val isSelected = selectedPlantIds.contains(plant.id)

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) PrimaryContainerMint else SurfaceWhite,
                            border = BorderStroke(1.dp, if (isSelected) LeafGreen else BorderCard),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = !isAssigned) {
                                    if (isSelected) {
                                        selectedPlantIds.remove(plant.id)
                                    } else {
                                        selectedPlantIds.add(plant.id)
                                    }
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(BackgroundLight)
                                ) {
                                    AsyncImage(
                                        model = plant.primaryPhotoUrl ?: plant.defaultPhotoUrl,
                                        contentDescription = plant.nameId,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = plant.nameId,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (isAssigned) OnSurfaceVariant else TextPrimary
                                        )
                                    )
                                    if (plant.nameLatin.isNotBlank()) {
                                        Text(
                                            text = plant.nameLatin,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontStyle = FontStyle.Italic,
                                                color = OnSurfaceVariant
                                            )
                                        )
                                    }
                                }

                                if (isAssigned) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = LeafGreenLight
                                    ) {
                                        Text(
                                            text = "Sudah ditambahkan",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = ForestGreen,
                                                fontWeight = FontWeight.SemiBold
                                            ),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                } else {
                                    Checkbox(
                                        checked = isSelected,
                                        onCheckedChange = { checked ->
                                            if (checked) {
                                                selectedPlantIds.add(plant.id)
                                            } else {
                                                selectedPlantIds.remove(plant.id)
                                            }
                                        },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = PrimaryForest,
                                            checkmarkColor = OnPrimaryWhite
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Sticky footer: "X tanaman dipilih" + "Tambahkan X Tanaman" button
                Surface(
                    color = SurfaceWhite,
                    shadowElevation = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${selectedPlantIds.size} tanaman dipilih",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = OnSurfaceDark
                            )
                        )

                        Button(
                            onClick = {
                                viewModel.addSelectedPlantsToLahan(selectedPlantIds.toSet(), picUid)
                                showPlantPickerSheet = false
                            },
                            enabled = selectedPlantIds.isNotEmpty() && !state.isSavingInventory,
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryForest),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = if (selectedPlantIds.isNotEmpty()) "Tambahkan ${selectedPlantIds.size} Tanaman" else "Pilih Tanaman",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }

    // Dialog: Confirmation Remove Plant from Lahan
    if (plantToRemove != null) {
        val targetPlant = plantToRemove!!
        AlertDialog(
            onDismissRequest = { plantToRemove = null },
            title = { Text("Lepas Tanaman dari Lahan?") },
            text = {
                Text(
                    text = "Apakah Anda yakin ingin melepas '${targetPlant.nameId}' dari lahan ini?\n\nSpesies ini tetap tersimpan di Ensiklopedia Tanaman global dan tidak akan terhapus.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.removePlantFromLahan(targetPlant.id, picUid)
                        plantToRemove = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FriendlyRed)
                ) {
                    Text("Lepas dari Lahan")
                }
            },
            dismissButton = {
                TextButton(onClick = { plantToRemove = null }) {
                    Text("Batal")
                }
            }
        )
    }

    // Dialog: Minimal Plant Creation from Lahan (Phase 5)
    if (showCreateMinimalPlantDialog) {
        AlertDialog(
            onDismissRequest = {
                if (!state.isSavingInventory && !isProcessingMinimalPhoto) {
                    showCreateMinimalPlantDialog = false
                }
            },
            title = { Text("Tambah Tanaman Baru", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = LeafGreenLight,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Tanaman baru akan otomatis dibuat dengan profil ringkas dan langsung dihubungkan ke lahan ini.",
                            style = MaterialTheme.typography.bodySmall.copy(color = ForestGreen),
                            modifier = Modifier.padding(10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Nama Indonesia *",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = minimalPlantName,
                        onValueChange = { minimalPlantName = it },
                        placeholder = { Text("Contoh: Daun Salam") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryForest,
                            unfocusedBorderColor = OutlineGrey,
                            focusedContainerColor = SurfaceWhite,
                            unfocusedContainerColor = SurfaceWhite
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Foto Tanaman *",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    if (minimalPhotoBytes != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(BackgroundLight)
                        ) {
                            val bitmap = remember(minimalPhotoBytes) {
                                minimalPhotoBytes?.let {
                                    BitmapFactory.decodeByteArray(it, 0, it.size)
                                }
                            }
                            if (bitmap != null) {
                                Image(
                                    bitmap = bitmap.asImageBitmap(),
                                    contentDescription = "Preview Foto",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                minimalCameraLauncher.launch(null)
                            },
                            enabled = !isProcessingMinimalPhoto && !state.isSavingInventory,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Kamera", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                minimalPhotoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            enabled = !isProcessingMinimalPhoto && !state.isSavingInventory,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Galeri", fontSize = 12.sp)
                        }
                    }

                    if (minimalPhotoError != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = minimalPhotoError ?: "",
                            style = MaterialTheme.typography.bodySmall.copy(color = FriendlyRed)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val photoBytes = minimalPhotoBytes
                        if (minimalPlantName.trim().length < 2) {
                            minimalPhotoError = "Nama tanaman minimal 2 karakter."
                        } else if (photoBytes == null) {
                            minimalPhotoError = "Foto tanaman wajib diambil atau dipilih."
                        } else {
                            viewModel.createMinimalPlantAndAssign(
                                name = minimalPlantName,
                                photoBytes = photoBytes,
                                adminUid = picUid
                            ) {
                                showCreateMinimalPlantDialog = false
                                showPlantPickerSheet = false
                            }
                        }
                    },
                    enabled = !state.isSavingInventory && !isProcessingMinimalPhoto && minimalPlantName.isNotBlank() && minimalPhotoBytes != null,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryForest)
                ) {
                    if (state.isSavingInventory) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = OnPrimaryWhite, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Text("Simpan & Tambahkan")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showCreateMinimalPlantDialog = false },
                    enabled = !state.isSavingInventory
                ) {
                    Text("Batal")
                }
            }
        )
    }
}
