package id.bubakangreen.app.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bubakangreen.app.core.result.Result
import id.bubakangreen.app.domain.model.AuditLog
import id.bubakangreen.app.domain.model.MasterPlant
import id.bubakangreen.app.domain.repository.AuditRepository
import id.bubakangreen.app.domain.repository.PlantRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MasterPlantFormState(
    val plantId: String? = null,
    val nameId: String = "",
    val nameLatin: String = "",
    val nameMandarin: String = "",
    val pinyin: String = "",
    val description: String = "",
    val characteristics: String = "",
    val commonUses: String = "",
    val cultivationNotes: String = "",
    val primaryPhotoUrl: String = "",
    val imageSourceType: String = "LOCAL",
    val imageAssetName: String = "",
    val imageSource: String = "",
    val imageLicense: String = "",
    val imageAuthor: String = "",
    val sourceReferences: String = "",
    val mandarinAudioUrl: String = "",
    val isSaving: Boolean = false,
    val validationError: String? = null
)

class MasterPlantViewModel(
    private val plantRepository: PlantRepository,
    private val auditRepository: AuditRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MasterPlantFormState())
    val state: StateFlow<MasterPlantFormState> = _state.asStateFlow()

    private val _successEvent = MutableSharedFlow<Unit>()
    val successEvent: SharedFlow<Unit> = _successEvent.asSharedFlow()

    fun loadPlant(plantId: String?) {
        if (plantId.isNullOrBlank()) return
        viewModelScope.launch {
            val result = plantRepository.getMasterPlantById(plantId).firstOrNull()
            if (result is Result.Success && result.data != null) {
                val p = result.data
                _state.update {
                    it.copy(
                        plantId = p.id,
                        nameId = p.nameId,
                        nameLatin = p.nameLatin,
                        nameMandarin = p.nameMandarin ?: "",
                        pinyin = p.pinyin ?: "",
                        description = p.description,
                        characteristics = p.characteristics,
                        commonUses = p.commonUses.ifBlank { p.benefits },
                        cultivationNotes = p.cultivationNotes.ifBlank { p.plantingGuide },
                        primaryPhotoUrl = p.primaryPhotoUrl ?: "",
                        imageSourceType = p.imageSourceType,
                        imageAssetName = p.imageAssetName ?: "",
                        imageSource = p.imageSource ?: "",
                        imageLicense = p.imageLicense ?: "",
                        imageAuthor = p.imageAuthor ?: "",
                        sourceReferences = p.sourceReferences,
                        mandarinAudioUrl = p.mandarinAudioUrl ?: ""
                    )
                }
            }
        }
    }

    fun onNameIdChange(value: String) = _state.update { it.copy(nameId = value, validationError = null) }
    fun onNameLatinChange(value: String) = _state.update { it.copy(nameLatin = value, validationError = null) }
    fun onNameMandarinChange(value: String) = _state.update { it.copy(nameMandarin = value) }
    fun onPinyinChange(value: String) = _state.update { it.copy(pinyin = value) }
    fun onDescriptionChange(value: String) = _state.update { it.copy(description = value, validationError = null) }
    fun onCharacteristicsChange(value: String) = _state.update { it.copy(characteristics = value) }
    fun onCommonUsesChange(value: String) = _state.update { it.copy(commonUses = value) }
    fun onCultivationNotesChange(value: String) = _state.update { it.copy(cultivationNotes = value) }
    fun onPhotoUrlChange(value: String) = _state.update { it.copy(primaryPhotoUrl = value, validationError = null) }
    fun onImageSourceChange(value: String) = _state.update { it.copy(imageSource = value) }
    fun onImageLicenseChange(value: String) = _state.update { it.copy(imageLicense = value) }
    fun onImageAuthorChange(value: String) = _state.update { it.copy(imageAuthor = value) }
    fun onSourceReferencesChange(value: String) = _state.update { it.copy(sourceReferences = value) }
    fun onAudioUrlChange(value: String) = _state.update { it.copy(mandarinAudioUrl = value) }

    fun savePlant(adminUid: String) {
        val current = _state.value
        val nameIdTrim = current.nameId.trim()
        val nameLatinTrim = current.nameLatin.trim()
        val descriptionTrim = current.description.trim()
        val photoUrlTrim = current.primaryPhotoUrl.trim()

        if (nameIdTrim.isBlank()) {
            _state.update { it.copy(validationError = "Nama tanaman (Indonesia) tidak boleh kosong.") }
            return
        }
        if (nameLatinTrim.isBlank()) {
            _state.update { it.copy(validationError = "Nama ilmiah (Latin) tidak boleh kosong.") }
            return
        }
        if (descriptionTrim.isBlank()) {
            _state.update { it.copy(validationError = "Deskripsi manfaat tanaman tidak boleh kosong.") }
            return
        }

        // HTTPS validation for remote image URLs
        if (photoUrlTrim.isNotBlank() && !photoUrlTrim.startsWith("plant_")) {
            if (!photoUrlTrim.startsWith("https://", ignoreCase = true)) {
                _state.update {
                    it.copy(validationError = "URL foto jarak jauh harus menggunakan protokol HTTPS resmi yang aman (diawali https://).")
                }
                return
            }
        }

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, validationError = null) }

            // Duplicate Botanical Prevention (Section 15)
            if (current.plantId == null) {
                val existingResult = plantRepository.getAllMasterPlants().firstOrNull()
                if (existingResult is Result.Success) {
                    val normCurrentLatin = nameLatinTrim.lowercase().replace(Regex("[^a-z]"), "")
                    val normCurrentName = nameIdTrim.lowercase()

                    val duplicate = existingResult.data.find { existing ->
                        val normExistingLatin = existing.nameLatin.trim().lowercase().replace(Regex("[^a-z]"), "")
                        val normExistingName = existing.nameId.trim().lowercase()

                        (normCurrentLatin.isNotEmpty() && normCurrentLatin == normExistingLatin) ||
                            (normCurrentName.isNotEmpty() && normCurrentName == normExistingName)
                    }

                    if (duplicate != null) {
                        _state.update {
                            it.copy(
                                isSaving = false,
                                validationError = "Spesies tanaman ini sudah terdaftar sebagai '${duplicate.nameId}' (${duplicate.nameLatin}). Gunakan data master yang sudah ada untuk menambahkan ke lokasi kebun."
                            )
                        }
                        return@launch
                    }
                }
            }

            val plantId = current.plantId ?: "PLANT_${System.currentTimeMillis()}"
            val isRemote = photoUrlTrim.startsWith("https://", ignoreCase = true)
            val isLocalAsset = photoUrlTrim.startsWith("plant_") || current.imageAssetName.isNotBlank()
            val imageSourceType = when {
                isRemote -> "REMOTE_URL"
                isLocalAsset -> "LOCAL"
                else -> "LOCAL"
            }
            val imageAssetName = if (isLocalAsset) {
                current.imageAssetName.ifBlank { photoUrlTrim }
            } else null

            val plant = MasterPlant(
                id = plantId,
                name = nameIdTrim,
                nameId = nameIdTrim,
                scientificName = nameLatinTrim,
                nameLatin = nameLatinTrim,
                mandarinName = current.nameMandarin.trim().ifBlank { null },
                nameMandarin = current.nameMandarin.trim().ifBlank { null },
                mandarinPinyin = current.pinyin.trim().ifBlank { null },
                pinyin = current.pinyin.trim().ifBlank { null },
                description = descriptionTrim,
                characteristics = current.characteristics.trim(),
                commonUses = current.commonUses.trim(),
                cultivationNotes = current.cultivationNotes.trim(),
                benefits = current.commonUses.trim().ifBlank { descriptionTrim },
                plantingGuide = current.cultivationNotes.trim(),
                defaultPhotoUrl = photoUrlTrim.ifBlank { null },
                primaryPhotoUrl = photoUrlTrim.ifBlank { null },
                imageSourceType = imageSourceType,
                imageAssetName = imageAssetName,
                imageSource = current.imageSource.trim().ifBlank { null },
                imageLicense = current.imageLicense.trim().ifBlank { null },
                imageAuthor = current.imageAuthor.trim().ifBlank { null },
                sourceReferences = current.sourceReferences.trim(),
                mandarinAudioUrl = current.mandarinAudioUrl.trim().ifBlank { null }
            )

            val result = if (current.plantId != null) {
                plantRepository.updateMasterPlant(plant)
            } else {
                plantRepository.createMasterPlant(plant)
            }

            when (result) {
                is Result.Success -> {
                    auditRepository.recordAction(
                        AuditLog(
                            id = "AUDIT_${System.currentTimeMillis()}",
                            action = if (current.plantId != null) "MASTER_PLANT_UPDATED" else "MASTER_PLANT_CREATED",
                            targetEntityId = plantId,
                            targetEntityType = "MASTER_PLANT",
                            actorUid = adminUid,
                            actorRole = "ADMIN",
                            details = "Spesies tanaman: ${plant.nameId} (${plant.nameLatin})"
                        )
                    )
                    _state.update { it.copy(isSaving = false) }
                    _successEvent.emit(Unit)
                }
                is Result.Error -> {
                    _state.update {
                        it.copy(
                            isSaving = false,
                            validationError = result.message ?: "Gagal menyimpan spesies master tanaman."
                        )
                    }
                }
                is Result.Loading -> {
                    _state.update { it.copy(isSaving = true) }
                }
            }
        }
    }
}
