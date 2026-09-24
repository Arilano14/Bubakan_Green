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

data class LocationFormState(
    val locationId: String? = null,
    val name: String = "",
    val type: LocationType = LocationType.URBAN_FARMING,
    val rw: String = "01",
    val address: String = "",
    val description: String = "",
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
                _state.update {
                    it.copy(
                        locationId = loc.id,
                        name = loc.name,
                        type = loc.type,
                        rw = loc.rw,
                        address = loc.address,
                        description = loc.description,
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
    fun onRwChange(rw: String) = _state.update { it.copy(rw = rw) }
    fun onAddressChange(address: String) = _state.update { it.copy(address = address, validationError = null) }
    fun onDescriptionChange(desc: String) = _state.update { it.copy(description = desc, validationError = null) }

    fun captureGps(clientOverride: LocationClient? = null) {
        val client = clientOverride ?: locationClient ?: return
        viewModelScope.launch {
            _state.update { it.copy(isGpsLoading = true, gpsWarning = null) }
            when (val result = client.getCurrentLocation()) {
                is Result.Success -> {
                    val coords = result.data
                    val accuracy = coords.accuracyMeters
                    val warning = if (accuracy > 25f) {
                        "Akurasi GPS saat ini ${accuracy.toInt()}m (>25m). Disarankan mencoba kembali di area terbuka."
                    } else null

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
            }
        }
    }

    fun saveLocation(picUid: String) {
        val current = _state.value
        if (current.name.trim().length < 3) {
            _state.update { it.copy(validationError = "Nama kebun minimal 3 karakter.") }
            return
        }
        if (current.address.trim().isBlank()) {
            _state.update { it.copy(validationError = "Alamat kebun tidak boleh kosong.") }
            return
        }
        if (current.description.trim().isBlank()) {
            _state.update { it.copy(validationError = "Deskripsi kebun tidak boleh kosong.") }
            return
        }
        if (current.latitude == null || current.longitude == null) {
            _state.update { it.copy(validationError = "Titik koordinat GPS wajib dikunci terlebih dahulu.") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, validationError = null) }

            val locationId = current.locationId ?: "LOC_${System.currentTimeMillis()}"
            val location = Location(
                id = locationId,
                name = current.name.trim(),
                type = current.type,
                rw = current.rw,
                address = current.address.trim(),
                description = current.description.trim(),
                latitude = current.latitude,
                longitude = current.longitude,
                accuracyMeters = current.accuracyMeters,
                capturedAt = current.capturedAt ?: System.currentTimeMillis(),
                coordinatesStatus = CoordinatesStatus.PENDING,
                picUid = picUid.ifBlank { "system_preview" },
                status = LocationStatus.PENDING_APPROVAL
            )

            val saveResult = if (current.locationId != null) {
                locationRepository.updateLocation(location)
            } else {
                locationRepository.createLocation(location)
            }

            when (saveResult) {
                is Result.Success -> {
                    // Record immutable civic audit log
                    auditRepository.recordAction(
                        AuditLog(
                            id = "AUDIT_${System.currentTimeMillis()}",
                            action = if (current.locationId != null) "LOCATION_UPDATED" else "LOCATION_CREATED",
                            targetEntityId = locationId,
                            targetEntityType = "LOCATION",
                            actorUid = picUid,
                            actorRole = "PIC",
                            details = "Pengajuan kebun: ${location.name} RW ${location.rw}"
                        )
                    )
                    _state.update { it.copy(isSaving = false, isSuccess = true) }
                    _saveSuccessEvent.emit(Unit)
                }
                is Result.Error -> {
                    _state.update {
                        it.copy(
                            isSaving = false,
                            validationError = saveResult.message ?: "Gagal menyimpan pengajuan kebun."
                        )
                    }
                }
            }
        }
    }
}
