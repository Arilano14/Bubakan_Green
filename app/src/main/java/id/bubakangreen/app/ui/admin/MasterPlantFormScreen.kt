package id.bubakangreen.app.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.bubakangreen.app.ui.theme.BackgroundLight
import id.bubakangreen.app.ui.theme.OnPrimaryWhite
import id.bubakangreen.app.ui.theme.OnSurfaceDark
import id.bubakangreen.app.ui.theme.OutlineGrey
import id.bubakangreen.app.ui.theme.PrimaryForest
import id.bubakangreen.app.ui.theme.SurfaceWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MasterPlantFormScreen(
    viewModel: MasterPlantViewModel,
    adminUid: String,
    plantId: String?,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(plantId) {
        viewModel.loadPlant(plantId)
    }

    LaunchedEffect(Unit) {
        viewModel.successEvent.collect {
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
                        text = if (plantId != null) "Edit Spesies Tanaman" else "Tambah Spesies Tanaman",
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
            // Validation Error Banner
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

            // Nama Indonesia
            Text(
                text = "Nama Tanaman (Bahasa Indonesia) *",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = OnSurfaceDark)
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = state.nameId,
                onValueChange = viewModel::onNameIdChange,
                placeholder = { Text("Contoh: Jahe Merah") },
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

            Spacer(modifier = Modifier.height(16.dp))

            // Nama Latin
            Text(
                text = "Nama Ilmiah (Latin) *",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = OnSurfaceDark)
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = state.nameLatin,
                onValueChange = viewModel::onNameLatinChange,
                placeholder = { Text("Contoh: Zingiber officinale var. rubrum") },
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

            Spacer(modifier = Modifier.height(16.dp))

            // Nama Mandarin (Hanzi) & Pinyin
            Text(
                text = "Aksara Mandarin (Hanzi) & Pinyin",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = OnSurfaceDark)
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = state.nameMandarin,
                onValueChange = viewModel::onNameMandarinChange,
                placeholder = { Text("Contoh: 红姜") },
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
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = state.pinyin,
                onValueChange = viewModel::onPinyinChange,
                placeholder = { Text("Contoh: hóng jiāng") },
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

            Spacer(modifier = Modifier.height(16.dp))

            // Deskripsi Singkat
            Text(
                text = "Deskripsi Tanaman *",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = OnSurfaceDark)
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = state.description,
                onValueChange = viewModel::onDescriptionChange,
                placeholder = { Text("Deskripsi umum mengenai tanaman...") },
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

            Spacer(modifier = Modifier.height(16.dp))

            // Karakteristik Morfologi
            Text(
                text = "Karakteristik Morfologi Tanaman",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = OnSurfaceDark)
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = state.characteristics,
                onValueChange = viewModel::onCharacteristicsChange,
                placeholder = { Text("Bentuk daun, habitus, warna bunga, aroma batang...") },
                minLines = 2,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryForest,
                    unfocusedBorderColor = OutlineGrey,
                    focusedContainerColor = SurfaceWhite,
                    unfocusedContainerColor = SurfaceWhite
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Manfaat & Penggunaan
            Text(
                text = "Manfaat & Penggunaan (Kuliner / Herbal Tradisional)",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = OnSurfaceDark)
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = state.commonUses,
                onValueChange = viewModel::onCommonUsesChange,
                placeholder = { Text("Khasiat herbal tradisional atau kegunaan konsumsi...") },
                minLines = 2,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryForest,
                    unfocusedBorderColor = OutlineGrey,
                    focusedContainerColor = SurfaceWhite,
                    unfocusedContainerColor = SurfaceWhite
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Panduan Budidaya
            Text(
                text = "Panduan Budidaya & Perawatan",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = OnSurfaceDark)
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = state.cultivationNotes,
                onValueChange = viewModel::onCultivationNotesChange,
                placeholder = { Text("Kebutuhan sinar matahari, media tanam, penyiraman...") },
                minLines = 2,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryForest,
                    unfocusedBorderColor = OutlineGrey,
                    focusedContainerColor = SurfaceWhite,
                    unfocusedContainerColor = SurfaceWhite
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Foto URL Referensi (Remote URL)
            Text(
                text = "URL Foto Referensi (Wajib HTTPS jika URL web)",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = OnSurfaceDark)
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = state.primaryPhotoUrl,
                onValueChange = viewModel::onPhotoUrlChange,
                placeholder = { Text("https://upload.wikimedia.org/.../plant.jpg atau plant_sereh") },
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

            Spacer(modifier = Modifier.height(16.dp))

            // Lisensi & Pembuat Foto
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Fotografer / Pembuat",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = OnSurfaceDark)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = state.imageAuthor,
                        onValueChange = viewModel::onImageAuthorChange,
                        placeholder = { Text("Contoh: Wouter Hagens") },
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
                }
                Spacer(modifier = Modifier.size(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Lisensi Foto",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = OnSurfaceDark)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = state.imageLicense,
                        onValueChange = viewModel::onImageLicenseChange,
                        placeholder = { Text("CC BY-SA 4.0 / CC0") },
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
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Referensi Taksonomi Ilmiah
            Text(
                text = "Referensi Taksonomi Ilmiah",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = OnSurfaceDark)
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = state.sourceReferences,
                onValueChange = viewModel::onSourceReferencesChange,
                placeholder = { Text("Contoh: Royal Botanic Gardens, Kew (POWO)") },
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

            Spacer(modifier = Modifier.height(28.dp))

            // Submit Button
            Button(
                onClick = { viewModel.savePlant(adminUid) },
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
                        color = OnPrimaryWhite,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = if (plantId != null) "Perbarui Ensiklopedia" else "Simpan ke Ensiklopedia",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    )
                }
            }
        }
    }
}
