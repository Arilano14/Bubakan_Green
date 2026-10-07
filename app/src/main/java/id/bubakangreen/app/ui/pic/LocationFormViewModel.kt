package id.bubakangreen.app.ui.pic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bubakangreen.app.core.result.Result
import id.bubakangreen.app.data.location.LocationClient
import id.bubakangreen.app.domain.model.AuditLog
import id.bubakangreen.app.domain.model.CoordinatesStatus
import id.bubakangreen.app.domain.model.Location
import id.bubakangreen.app.domain.model.LocationStatus
import id.bubakangreen.app.domain.model.LocationType
import id.bubakangreen.app.domain.repository.AuditRepository
import id.bubakangreen.app.domain.repository.LocationRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import id.bubakangreen.app.domain.model.RegionTag

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
    val isSuccess: Boolean = false
)

class LocationFormViewModel(
    private val locationRepository: LocationRepository,
    private val auditRepository: AuditRepository,
    private val locationClient: LocationClient? = null
) : ViewModel() {

    private val _state = MutableStateFlow(LocationFormState())
    val state: StateFlow<LocationFormState> = _state.asStateFlow()

    private val _saveSuccessEvent = MutableSharedFlow<Unit>()
    val saveSuccessEvent: SharedFlow<Unit> = _saveSuccessEvent.asSharedFlow()

    fun loadExistingLocation(locationId: String?) {
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
                updated.removeAt(index)
                it.copy(photos = updated, validationError = null)
            } else it
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

            val saveResult = if (current.locationId != null) {
                locationRepository.updateLocation(location)
            } else {
                locationRepository.createLocation(location)
            }

            when (saveResult) {
                is Result.Success -> {
                    auditRepository.recordAction(
                        AuditLog(
                            id = "AUDIT_${System.currentTimeMillis()}",
                            action = if (current.locationId != null) "LOCATION_UPDATED" else "LOCATION_CREATED",
                            targetEntityId = locationId,
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
}
