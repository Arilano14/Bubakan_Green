package id.bubakangreen.app.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bubakangreen.app.core.result.Result
import id.bubakangreen.app.domain.model.AuditLog
import id.bubakangreen.app.domain.model.CoordinatesStatus
import id.bubakangreen.app.domain.model.Location
import id.bubakangreen.app.domain.model.LocationStatus
import id.bubakangreen.app.domain.repository.AuditRepository
import id.bubakangreen.app.domain.repository.LocationRepository
import id.bubakangreen.app.ui.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LocationApprovalViewModel(
    private val locationRepository: LocationRepository,
    private val auditRepository: AuditRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<Location>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Location>>> = _uiState.asStateFlow()

    init {
        loadPendingLocations()
    }

    fun loadPendingLocations() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            when (val result = locationRepository.getPendingLocations()) {
                is Result.Success -> {
                    val list = result.data
                    if (list.isEmpty()) {
                        _uiState.value = UiState.Empty("Semua pengajuan kebun telah selesai ditinjau.")
                    } else {
                        _uiState.value = UiState.Success(list)
                    }
                }
                is Result.Error -> {
                    _uiState.value = UiState.Error(result.message ?: "Gagal memuat antrean persetujuan.")
                }
            }
        }
    }

    fun approveLocation(location: Location, adminUid: String) {
        viewModelScope.launch {
            val approved = location.copy(
                status = LocationStatus.PUBLISHED,
                coordinatesStatus = CoordinatesStatus.VERIFIED,
                rejectionNote = null,
                updatedAt = System.currentTimeMillis()
            )
            when (locationRepository.updateLocation(approved)) {
                is Result.Success -> {
                    auditRepository.recordAction(
                        AuditLog(
                            id = "AUDIT_${System.currentTimeMillis()}",
                            action = "LOCATION_APPROVED",
                            targetEntityId = location.id,
                            targetEntityType = "LOCATION",
                            actorUid = adminUid,
                            actorRole = "ADMIN",
                            details = "Menyetujui dan menerbitkan kebun: ${location.name} RW ${location.rw}"
                        )
                    )
                    loadPendingLocations()
                }
                is Result.Error -> {}
            }
        }
    }

    fun rejectLocation(location: Location, rejectionNote: String, adminUid: String) {
        viewModelScope.launch {
            val rejected = location.copy(
                status = LocationStatus.DRAFT,
                rejectionNote = rejectionNote.ifBlank { "Revisi diperlukan oleh admin kelurahan." },
                updatedAt = System.currentTimeMillis()
            )
            when (locationRepository.updateLocation(rejected)) {
                is Result.Success -> {
                    auditRepository.recordAction(
                        AuditLog(
                            id = "AUDIT_${System.currentTimeMillis()}",
                            action = "LOCATION_REJECTED",
                            targetEntityId = location.id,
                            targetEntityType = "LOCATION",
                            actorUid = adminUid,
                            actorRole = "ADMIN",
                            details = "Menolak pengajuan kebun ${location.name} dengan catatan: $rejectionNote"
                        )
                    )
                    loadPendingLocations()
                }
                is Result.Error -> {}
            }
        }
    }
}
