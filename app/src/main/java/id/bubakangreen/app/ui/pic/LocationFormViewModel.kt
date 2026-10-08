package id.bubakangreen.app.ui.pic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bubakangreen.app.core.di.RepositoryProvider
import id.bubakangreen.app.core.result.Result
import id.bubakangreen.app.data.fixture.DefaultBotanicalData
import id.bubakangreen.app.data.location.LocationClient
import id.bubakangreen.app.domain.model.AuditLog
import id.bubakangreen.app.domain.model.CoordinatesStatus
import id.bubakangreen.app.domain.model.Location
import id.bubakangreen.app.domain.model.LocationPlant
import id.bubakangreen.app.domain.model.LocationStatus
import id.bubakangreen.app.domain.model.LocationType
import id.bubakangreen.app.domain.model.MasterPlant
import id.bubakangreen.app.domain.model.RegionTag
import id.bubakangreen.app.domain.repository.AuditRepository
import id.bubakangreen.app.domain.repository.LocationRepository
import id.bubakangreen.app.domain.repository.PlantRepository
import id.bubakangreen.app.domain.repository.StorageRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LocationFormState(
    val locationId: String? = null,
    val name: String = "",
    val type: LocationType = LocationType.URBAN_FARMING,
    val regionTag: RegionTag = RegionTag.RW_01,
    val rw: String = "01",
    val address: String = "",
    val description: String = "",
    val photos: List<String> = emptyList(),
    val latitude: Double? = null,
    val longitude: Double? = null,
    val accuracyMeters: Float? = null,
    val capturedAt: Long? = null,
    val isGpsLoading: Boolean = false,
    val gpsWarning: String? = null,
    val isSaving: Boolean = false,
    val validationError: String? = null,
    val isSuccess: Boolean = false,

    // Lahan Inventory Tanaman State
    val assignedPlants: List<MasterPlant> = emptyList(),
    val allMasterPlants: List<MasterPlant> = DefaultBotanicalData.defaultMasterPlants,
    val isInventoryLoading: Boolean = false,
    val isSavingInventory: Boolean = false,
    val inventoryFeedback: String? = null
)

class LocationFormViewModel(
    private val locationRepository: LocationRepository = RepositoryProvider.getLocationRepository(),
    private val auditRepository: AuditRepository = RepositoryProvider.getAuditRepository(),
    private val plantRepository: PlantRepository = RepositoryProvider.getPlantRepository(),
    private val storageRepository: StorageRepository = RepositoryProvider.getStorageRepository(),
    private val locationClient: LocationClient? = null
) : ViewModel() {

    private val _state = MutableStateFlow(LocationFormState())
    val state: StateFlow<LocationFormState> = _state.asStateFlow()

    private val _saveSuccessEvent = MutableSharedFlow<Unit>()
    val saveSuccessEvent: SharedFlow<Unit> = _saveSuccessEvent.asSharedFlow()

    init {
        loadAllMasterPlants()
    }

    private fun loadAllMasterPlants() {
        viewModelScope.launch {
            plantRepository.getAllMasterPlants().collect { result ->
                if (result is Result.Success) {
                    val plants = result.data.ifEmpty { DefaultBotanicalData.defaultMasterPlants }
                    _state.update { it.copy(allMasterPlants = plants) }
                }
            }
        }
    }

    fun loadExistingLocation(locationId: String?) {
        loadAllMasterPlants()
        if (locationId.isNullOrBlank()) return
        viewModelScope.launch {
            val result = locationRepository.getLocationById(locationId).firstOrNull()
            if (result is Result.Success && result.data != null) {
                val loc = result.data
                val parsedTag = RegionTag.entries.find { it.code == loc.regionTag }
                    ?: RegionTag.fromCodeOrRw(loc.rw)
                    ?: RegionTag.RW_01
                val photoList = (loc.photos.ifEmpty { listOfNotNull(loc.coverPhotoUrl) }).take(3)
                _state.update {
                    it.copy(
                        locationId = loc.id,
                        name = loc.name,
                        type = loc.type,
                        regionTag = parsedTag,
                        rw = loc.rw,
                        address = loc.address,
                        description = loc.description,
                        photos = photoList,
                        latitude = loc.latitude,
                        longitude = loc.longitude,
                        accuracyMeters = loc.accuracyMeters,
                        capturedAt = loc.capturedAt
                    )
                }
            }

            // Observe inventory relations and master plants reactively (in-memory join, zero N+1)
            observeLocationInventory(locationId)
        }
    }

    private fun observeLocationInventory(locationId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isInventoryLoading = true) }
            combine(
                plantRepository.getPlantsAtLocation(locationId),
                plantRepository.getAllMasterPlants()
            ) { locPlantsRes, allPlantsRes ->
                val locPlants = (locPlantsRes as? Result.Success)?.data ?: emptyList()
                val repoPlants = (allPlantsRes as? Result.Success)?.data ?: emptyList()
                val allPlants = repoPlants.ifEmpty {
                    _state.value.allMasterPlants.ifEmpty { DefaultBotanicalData.defaultMasterPlants }
                }

                val assignedIds = locPlants.filter { it.isPresent }.map { it.plantId }.toSet()
                val assignedList = allPlants.filter { it.id in assignedIds }
                    .sortedBy { it.nameId.lowercase() }

                Pair(assignedList, allPlants.sortedBy { it.nameId.lowercase() })
            }.collect { (assigned, all) ->
                _state.update {
                    it.copy(
                        assignedPlants = assigned,
                        allMasterPlants = all,
                        isInventoryLoading = false
                    )
                }
            }
        }
    }

    fun onNameChange(name: String) = _state.update { it.copy(name = name, validationError = null) }
    fun onTypeChange(type: LocationType) = _state.update { it.copy(type = type) }
    fun onRegionTagChange(tag: RegionTag) = _state.update {
        it.copy(
            regionTag = tag,
            rw = when (tag) {
                RegionTag.KELURAHAN -> "Kelurahan"
                else -> tag.label.removePrefix("RW ")
            }
        )
    }
    fun onRwChange(rw: String) = _state.update {
        val tag = RegionTag.fromCodeOrRw(rw) ?: it.regionTag
        it.copy(rw = rw, regionTag = tag)
    }
    fun onAddressChange(address: String) = _state.update { it.copy(address = address, validationError = null) }
    fun onDescriptionChange(desc: String) = _state.update { it.copy(description = desc, validationError = null) }

    fun onAddPhoto(photoUrl: String) {
        val trimmed = photoUrl.trim()
        if (trimmed.isBlank()) return
        _state.update {
            if (it.photos.size >= 3) {
                it.copy(validationError = "Maksimal 3 foto per lahan.")
            } else {
                it.copy(photos = it.photos + trimmed, validationError = null)
            }
        }
    }

    fun onReplacePhoto(index: Int, newPhotoUrl: String) {
        val trimmed = newPhotoUrl.trim()
        if (trimmed.isBlank()) return
        _state.update {
            if (index in it.photos.indices) {
                val updated = it.photos.toMutableList()
                updated[index] = trimmed
                it.copy(photos = updated, validationError = null)
            } else it
        }
    }

    fun onRemovePhoto(index: Int) {
        _state.update {
            if (index in it.photos.indices) {
                val updated = it.photos.toMutableList()
                val removed = updated.removeAt(index)
                // Clean up through storage repository abstraction if needed
                viewModelScope.launch { storageRepository.deleteImage(removed) }
                it.copy(photos = updated, validationError = null)
            } else it
        }
    }

    fun saveCompressedPhotoBytes(bytes: ByteArray, replaceIndex: Int? = null, onComplete: (String) -> Unit) {
        viewModelScope.launch {
            val locId = _state.value.locationId ?: "new"
            val filename = "lahan_${locId}_${System.currentTimeMillis()}.jpg"
            when (val uploadResult = storageRepository.uploadImage(bytes, filename, "lahan")) {
                is Result.Success -> {
                    val path = uploadResult.data
                    if (replaceIndex != null) {
                        onReplacePhoto(replaceIndex, path)
                    } else {
                        onAddPhoto(path)
                    }
                    onComplete(path)
                }
                is Result.Error -> {
                    _state.update { it.copy(validationError = uploadResult.message ?: "Gagal menyimpan foto lahan.") }
                }
                is Result.Loading -> {}
            }
        }
    }

    fun captureGps(clientOverride: LocationClient? = null) {
        val client = clientOverride ?: locationClient ?: return
        viewModelScope.launch {
            _state.update { it.copy(isGpsLoading = true, gpsWarning = null) }
            when (val result = client.getCurrentLocation()) {
                is Result.Success -> {
                    val coords = result.data
                    val accuracy = coords.accuracyMeters
                    val isInsideBubakan = id.bubakangreen.app.core.util.BubakanGeoValidator.isInsideBubakan(coords.latitude, coords.longitude)
                    val warning = when {
                        !isInsideBubakan -> "Peringatan: Koordinat GPS berada di luar batas administratif Kelurahan Bubakan. Lahan wajib berada di wilayah Bubakan."
                        accuracy == null -> "Informasi akurasi GPS tidak tersedia. Disarankan mencoba kembali di area terbuka."
                        accuracy > 25f -> "Akurasi GPS saat ini ${accuracy.toInt()}m (>25m). Disarankan mencoba kembali di area terbuka."
                        else -> null
                    }

                    _state.update {
                        it.copy(
                            latitude = coords.latitude,
                            longitude = coords.longitude,
                            accuracyMeters = accuracy,
                            capturedAt = System.currentTimeMillis(),
                            isGpsLoading = false,
                            gpsWarning = warning,
                            validationError = null
                        )
                    }
                }
                is Result.Error -> {
                    _state.update {
                        it.copy(
                            isGpsLoading = false,
                            gpsWarning = "Gagal mengunci titik koordinat GPS. Pastikan izin lokasi aktif dan berada di tempat terbuka."
                        )
                    }
                }
                is Result.Loading -> {
                    _state.update { it.copy(isGpsLoading = true) }
                }
            }
        }
    }

    fun saveLocation(adminUid: String = "admin_kelurahan") {
        val current = _state.value
        val nameTrim = current.name.trim()
        if (nameTrim.length < 3) {
            _state.update { it.copy(validationError = "Nama lahan minimal 3 karakter.") }
            return
        }

        val lat = current.latitude
        val lng = current.longitude
        if (lat == null || lng == null) {
            _state.update { it.copy(validationError = "Titik koordinat GPS wajib dikunci sebelum menyimpan lahan.") }
            return
        }

        val geoValidationError = id.bubakangreen.app.core.util.BubakanGeoValidator.validateCoordinates(lat, lng)
        if (geoValidationError != null) {
            _state.update { it.copy(validationError = geoValidationError) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, validationError = null) }

            val locationId = current.locationId ?: run {
                val slug = nameTrim.lowercase().replace(Regex("[^a-z0-9]"), "-").trim('-').take(24)
                val randomSuffix = java.util.UUID.randomUUID().toString().replace("-", "").take(6).lowercase()
                if (slug.isNotBlank()) "loc-$slug-$randomSuffix" else "loc-$randomSuffix"
            }

            val photoList = current.photos.take(3)
            val location = Location(
                id = locationId,
                name = nameTrim,
                type = current.type,
                rw = when (current.regionTag) {
                    RegionTag.KELURAHAN -> "Kelurahan"
                    else -> current.regionTag.label.removePrefix("RW ")
                },
                regionTag = current.regionTag.code,
                address = current.address.trim().ifBlank { "${current.regionTag.label}, Kelurahan Bubakan" },
                description = current.description.trim(),
                latitude = lat,
                longitude = lng,
                accuracyMeters = current.accuracyMeters,
                capturedAt = current.capturedAt ?: System.currentTimeMillis(),
                coordinatesStatus = CoordinatesStatus.VERIFIED,
                photos = photoList,
                coverPhotoUrl = photoList.firstOrNull(),
                photoUrl = photoList.firstOrNull(),
                picUid = adminUid,
                createdBy = adminUid,
                status = LocationStatus.PUBLISHED,
                isPublished = true
            )

            val currentAssignedPlantIds = _state.value.assignedPlants.map { it.id }
            val saveResult = if (current.locationId != null) {
                locationRepository.updateLocation(location)
            } else {
                locationRepository.createLocation(location)
            }

            when (saveResult) {
                is Result.Success -> {
                    val finalLocationId: String = current.locationId ?: (saveResult.data as? String) ?: location.id
                    if (current.locationId == null && currentAssignedPlantIds.isNotEmpty()) {
                        // Persist selected plants to the newly created location
                        plantRepository.addPlantsToLocation(finalLocationId, currentAssignedPlantIds)
                    }
                    auditRepository.recordAction(
                        AuditLog(
                            id = "AUDIT_${System.currentTimeMillis()}",
                            action = if (current.locationId != null) "LOCATION_UPDATED" else "LOCATION_CREATED",
                            targetEntityId = finalLocationId,
                            targetEntityType = "LOCATION",
                            actorUid = adminUid,
                            actorRole = "ADMIN",
                            details = "Penyimpanan data lahan: ${location.name} (${location.displayRegionTag})"
                        )
                    )
                    _state.update { it.copy(isSaving = false, isSuccess = true) }
                    _saveSuccessEvent.emit(Unit)
                }
                is Result.Error -> {
                    _state.update {
                        it.copy(
                            isSaving = false,
                            validationError = saveResult.message ?: "Gagal menyimpan data lahan."
                        )
                    }
                }
                is Result.Loading -> {
                    _state.update { it.copy(isSaving = true) }
                }
            }
        }
    }

    // ==========================================================
    // LAHAN INVENTORY OPERATIONS (PHASE 4 & 5)
    // ==========================================================

    fun addSelectedPlantsToLahan(selectedPlantIds: Set<String>, adminUid: String = "admin_kelurahan") {
        val locationId = _state.value.locationId
        val currentlyAssignedIds = _state.value.assignedPlants.map { it.id }.toSet()
        val toAdd = selectedPlantIds.filter { it !in currentlyAssignedIds }

        if (toAdd.isEmpty()) {
            _state.update { it.copy(inventoryFeedback = "Semua tanaman terpilih sudah ada di lahan ini.") }
            return
        }

        if (locationId == null) {
            // New Lahan flow: update state in-memory before location is saved
            val newAssigned = _state.value.allMasterPlants.filter { it.id in toAdd }
            _state.update {
                it.copy(
                    assignedPlants = (it.assignedPlants + newAssigned).distinctBy { p -> p.id },
                    inventoryFeedback = "${toAdd.size} tanaman ditambahkan ke daftar tanaman lahan."
                )
            }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSavingInventory = true, inventoryFeedback = null) }
            when (val res = plantRepository.addPlantsToLocation(locationId, toAdd)) {
                is Result.Success -> {
                    auditRepository.recordAction(
                        AuditLog(
                            id = "AUDIT_${System.currentTimeMillis()}",
                            action = "PLANTS_BATCH_ADDED_TO_LAHAN",
                            targetEntityId = locationId,
                            targetEntityType = "LOCATION_PLANT",
                            actorUid = adminUid,
                            actorRole = "ADMIN",
                            details = "Menambahkan ${res.data} tanaman ke lahan $locationId"
                        )
                    )
                    _state.update {
                        it.copy(
                            isSavingInventory = false,
                            inventoryFeedback = "${res.data} tanaman berhasil ditambahkan ke lahan."
                        )
                    }
                }
                is Result.Error -> {
                    _state.update {
                        it.copy(
                            isSavingInventory = false,
                            inventoryFeedback = res.message ?: "Gagal menambahkan tanaman ke lahan."
                        )
                    }
                }
                is Result.Loading -> {}
            }
        }
    }

    fun removePlantFromLahan(plantId: String, adminUid: String = "admin_kelurahan") {
        val locationId = _state.value.locationId
        if (locationId == null) {
            _state.update {
                it.copy(
                    assignedPlants = it.assignedPlants.filter { p -> p.id != plantId },
                    inventoryFeedback = "Tanaman dilepas dari daftar lahan baru."
                )
            }
            return
        }

        val relationId = "${locationId}_${plantId}"
        viewModelScope.launch {
            _state.update { it.copy(isSavingInventory = true) }
            when (val res = plantRepository.removePlantFromLocation(relationId)) {
                is Result.Success -> {
                    auditRepository.recordAction(
                        AuditLog(
                            id = "AUDIT_${System.currentTimeMillis()}",
                            action = "PLANT_REMOVED_FROM_LAHAN",
                            targetEntityId = relationId,
                            targetEntityType = "LOCATION_PLANT",
                            actorUid = adminUid,
                            actorRole = "ADMIN",
                            details = "Melepas relasi tanaman $plantId dari lahan $locationId"
                        )
                    )
                    _state.update {
                        it.copy(
                            isSavingInventory = false,
                            inventoryFeedback = "Tanaman berhasil dilepas dari lahan ini."
                        )
                    }
                }
                is Result.Error -> {
                    _state.update {
                        it.copy(
                            isSavingInventory = false,
                            inventoryFeedback = res.message ?: "Gagal melepas tanaman dari lahan."
                        )
                    }
                }
                is Result.Loading -> {}
            }
        }
    }

    fun createMinimalPlantAndAssign(
        name: String,
        photoBytes: ByteArray,
        adminUid: String = "admin_kelurahan",
        onSuccess: () -> Unit
    ) {
        val locationId = _state.value.locationId
        val nameTrim = name.trim()
        if (nameTrim.length < 2) {
            _state.update { it.copy(inventoryFeedback = "Nama tanaman minimal 2 karakter.") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSavingInventory = true, inventoryFeedback = null) }

            // 1. Generate stable plant ID
            val slug = nameTrim.lowercase().replace(Regex("[^a-z0-9]"), "-").trim('-').take(24)
            val suffix = (System.currentTimeMillis() % 10000).toString()
            val plantId = if (slug.isNotBlank()) "pl-$slug-$suffix" else "pl-plant-$suffix"

            // 2. Upload photo via StorageRepository abstraction
            val photoFilename = "${plantId}.jpg"
            val uploadRes = storageRepository.uploadImage(photoBytes, photoFilename, "plants")
            if (uploadRes !is Result.Success) {
                _state.update {
                    it.copy(
                        isSavingInventory = false,
                        inventoryFeedback = "Gagal memproses foto tanaman: ${(uploadRes as? Result.Error)?.message}"
                    )
                }
                return@launch
            }
            val photoPath = uploadRes.data

            // 3. Construct Minimal MasterPlant entity
            val now = System.currentTimeMillis()
            val minimalPlant = MasterPlant(
                id = plantId,
                name = nameTrim,
                nameId = nameTrim,
                scientificName = "",
                nameLatin = "",
                mandarinName = null,
                nameMandarin = null,
                mandarinPinyin = null,
                pinyin = null,
                description = "",
                characteristics = "",
                commonUses = "",
                cultivationNotes = "",
                benefits = "",
                plantingGuide = "",
                defaultPhotoUrl = photoPath,
                primaryPhotoUrl = photoPath,
                imageSourceType = "LOCAL",
                imageAssetName = null,
                sourceReferences = "",
                profileCompleteness = "MINIMAL",
                createdFrom = "LOCATION_INVENTORY",
                isPublished = true,
                createdAt = now,
                updatedAt = now
            )

            if (locationId != null) {
                // ATOMIC CREATION: Writes MasterPlant + LocationPlant together in single batch
                when (val createRes = plantRepository.createMasterPlantWithLocation(minimalPlant, locationId)) {
                    is Result.Success -> {
                        auditRepository.recordAction(
                            AuditLog(
                                id = "AUDIT_${System.currentTimeMillis()}",
                                action = "MINIMAL_PLANT_CREATED_FROM_LAHAN",
                                targetEntityId = plantId,
                                targetEntityType = "MASTER_PLANT",
                                actorUid = adminUid,
                                actorRole = "ADMIN",
                                details = "Pembuatan minimal tanaman '$nameTrim' langsung dari lahan $locationId"
                            )
                        )
                        _state.update {
                            it.copy(
                                isSavingInventory = false,
                                inventoryFeedback = "Tanaman baru '$nameTrim' berhasil dibuat dan ditambahkan."
                            )
                        }
                        onSuccess()
                    }
                    is Result.Error -> {
                        // Safe failure recovery: delete uploaded image
                        storageRepository.deleteImage(photoPath)
                        _state.update {
                            it.copy(
                                isSavingInventory = false,
                                inventoryFeedback = createRes.message ?: "Gagal membuat tanaman baru."
                            )
                        }
                    }
                    is Result.Loading -> {}
                }
            } else {
                // New Lahan flow: create MasterPlant and assign in-memory
                when (val createRes = plantRepository.createMasterPlant(minimalPlant)) {
                    is Result.Success -> {
                        _state.update {
                            it.copy(
                                allMasterPlants = (it.allMasterPlants + minimalPlant).distinctBy { p -> p.id },
                                assignedPlants = (it.assignedPlants + minimalPlant).distinctBy { p -> p.id },
                                isSavingInventory = false,
                                inventoryFeedback = "Tanaman baru '$nameTrim' berhasil dibuat dan ditambahkan ke daftar."
                            )
                        }
                        onSuccess()
                    }
                    is Result.Error -> {
                        storageRepository.deleteImage(photoPath)
                        _state.update {
                            it.copy(
                                isSavingInventory = false,
                                inventoryFeedback = createRes.message ?: "Gagal membuat tanaman baru."
                            )
                        }
                    }
                    is Result.Loading -> {}
                }
            }
        }
    }

    fun clearInventoryFeedback() {
        _state.update { it.copy(inventoryFeedback = null) }
    }
}
