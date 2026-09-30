package id.bubakangreen.app.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.bubakangreen.app.core.result.Result
import id.bubakangreen.app.domain.model.Location
import id.bubakangreen.app.domain.model.LocationConditionLog
import id.bubakangreen.app.domain.model.LocationPlant
import id.bubakangreen.app.domain.model.LocationStatus
import id.bubakangreen.app.domain.model.MasterPlant
import id.bubakangreen.app.domain.model.PlantCondition
import id.bubakangreen.app.domain.model.UserSession
import id.bubakangreen.app.domain.repository.AuthRepository
import id.bubakangreen.app.domain.repository.LocationRepository
import id.bubakangreen.app.domain.repository.PlantRepository
import id.bubakangreen.app.ui.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class AdminDashboardData(
    val totalLocations: Int,
    val activeLocations: Int,
    val needsMaintenanceLocations: Int,
    val latestUpdateText: String,
    val locations: List<Location>,
    val masterPlants: List<MasterPlant>,
    val pendingLocations: List<Location> = emptyList(),
    val totalPublished: Int = activeLocations,
    val totalMasterPlants: Int = masterPlants.size
)

class AdminDashboardViewModel(
    private val locationRepository: LocationRepository,
    private val plantRepository: PlantRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<AdminDashboardData>>(UiState.Loading)
    val uiState: StateFlow<UiState<AdminDashboardData>> = _uiState.asStateFlow()

    private val _session = MutableStateFlow<UserSession?>(null)
    val session: StateFlow<UserSession?> = _session.asStateFlow()

    private val _actionMessage = MutableStateFlow<String?>(null)
    val actionMessage: StateFlow<String?> = _actionMessage.asStateFlow()

    init {
        loadData()
    }

    fun clearActionMessage() {
        _actionMessage.value = null
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            authRepository.currentUserSession.collect { userSession ->
                _session.value = userSession

                val allLocationsResult = locationRepository.getAllLocations().firstOrNull()
                val allLocations = (allLocationsResult as? Result.Success)?.data ?: emptyList()

                val activeCount = allLocations.count { it.status == LocationStatus.ACTIVE || it.status == LocationStatus.PUBLISHED }
                val needsMaintCount = allLocations.count { it.status == LocationStatus.NEEDS_MAINTENANCE }
                val pendingList = allLocations.filter { it.status == LocationStatus.PENDING_APPROVAL }

                val plantsResult = plantRepository.getAllMasterPlants().firstOrNull()
                val masterPlants = (plantsResult as? Result.Success)?.data ?: emptyList()

                val mostRecentLocation = allLocations.maxByOrNull { it.conditionUpdatedAt ?: it.updatedAt }
                val latestTime = mostRecentLocation?.let { it.conditionUpdatedAt ?: it.updatedAt }
                val latestText = if (latestTime != null) {
                    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
                    "${mostRecentLocation.name} (${sdf.format(Date(latestTime))})"
                } else {
                    "Belum ada pembaruan"
                }

                _uiState.value = UiState.Success(
                    AdminDashboardData(
                        totalLocations = allLocations.size,
                        activeLocations = activeCount,
                        needsMaintenanceLocations = needsMaintCount,
                        latestUpdateText = latestText,
                        locations = allLocations,
                        masterPlants = masterPlants,
                        pendingLocations = pendingList,
                        totalPublished = activeCount,
                        totalMasterPlants = masterPlants.size
                    )
                )
            }
        }
    }

    fun updateLandCondition(
        locationId: String,
        status: String,
        note: String,
        photoUrl: String?,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            val user = _session.value?.displayName ?: "Admin Kelurahan"
            val result = locationRepository.updateLocationCondition(
                locationId = locationId,
                status = status,
                note = note,
                photoUrl = photoUrl,
                updatedBy = user
            )
            if (result is Result.Success) {
                _actionMessage.value = "Kondisi lahan berhasil diperbarui."
                loadData()
                onComplete()
            } else {
                _actionMessage.value = "Perubahan belum tersimpan. Coba lagi."
            }
        }
    }

    fun deactivateLocation(locationId: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            val result = locationRepository.deactivateLocation(locationId)
            if (result is Result.Success) {
                _actionMessage.value = "Lokasi dinonaktifkan."
                loadData()
                onComplete()
            } else {
                _actionMessage.value = "Perubahan belum tersimpan. Coba lagi."
            }
        }
    }

    fun addPlantToLocation(
        locationId: String,
        plantId: String,
        condition: PlantCondition,
        quantity: Int,
        notes: String?,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            val user = _session.value?.displayName ?: "Admin"
            val locPlant = LocationPlant(
                id = "LP_${System.currentTimeMillis()}",
                locationId = locationId,
                plantId = plantId,
                condition = condition,
                quantity = quantity,
                notes = notes,
                isPresent = true,
                createdBy = user,
                updatedBy = user
            )
            val result = plantRepository.addPlantToLocation(locPlant)
            if (result is Result.Success) {
                _actionMessage.value = "Tanaman berhasil ditambahkan ke lokasi."
                onComplete()
            } else {
                _actionMessage.value = "Gagal menambahkan tanaman."
            }
        }
    }

    fun updatePlantCondition(
        locationPlant: LocationPlant,
        condition: PlantCondition,
        quantity: Int,
        notes: String?,
        isPresent: Boolean,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            val user = _session.value?.displayName ?: "Admin"
            val updated = locationPlant.copy(
                condition = condition,
                quantity = quantity,
                notes = notes,
                isPresent = isPresent,
                updatedBy = user,
                updatedAt = System.currentTimeMillis()
            )
            val result = plantRepository.updateLocationPlant(updated)
            if (result is Result.Success) {
                _actionMessage.value = "Kondisi tanaman diperbarui."
                onComplete()
            } else {
                _actionMessage.value = "Perubahan belum tersimpan. Coba lagi."
            }
        }
    }

    fun getLocationPlants(locationId: String) = plantRepository.getPlantsAtLocation(locationId)

    fun getLocationConditionLogs(locationId: String) = locationRepository.getLocationConditionLogs(locationId)

    fun signOut(onSignedOut: () -> Unit) {
        viewModelScope.launch {
            authRepository.signOut()
            onSignedOut()
        }
    }
}

