package id.bubakangreen.app.core.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import id.bubakangreen.app.core.result.Result
import id.bubakangreen.app.data.remote.FirebaseAuthRepository
import id.bubakangreen.app.data.remote.FirestoreAuditRepository
import id.bubakangreen.app.data.remote.FirestoreLocationRepository
import id.bubakangreen.app.data.remote.FirestorePlantRepository
import id.bubakangreen.app.domain.model.AuditLog
import id.bubakangreen.app.domain.model.CoordinatesStatus
import id.bubakangreen.app.domain.model.Location
import id.bubakangreen.app.domain.model.LocationPlant
import id.bubakangreen.app.domain.model.LocationStatus
import id.bubakangreen.app.domain.model.LocationType
import id.bubakangreen.app.domain.model.MasterPlant
import id.bubakangreen.app.domain.model.UserRole
import id.bubakangreen.app.domain.model.UserSession
import id.bubakangreen.app.domain.repository.AuditRepository
import id.bubakangreen.app.domain.repository.AuthRepository
import id.bubakangreen.app.domain.repository.LocationRepository
import id.bubakangreen.app.domain.repository.PlantRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf

/**
 * Service locator providing repositories to ViewModels.
 * Safely provides Firestore and FirebaseAuth repositories when Firebase is active,
 * or UI preview fixtures if Firebase is not yet configured.
 */
object RepositoryProvider {

    private var locationRepo: LocationRepository? = null
    private var plantRepo: PlantRepository? = null
    private var authRepo: AuthRepository? = null
    private var auditRepo: AuditRepository? = null

    fun getLocationRepository(): LocationRepository {
        return locationRepo ?: synchronized(this) {
            locationRepo ?: createLocationRepository().also { locationRepo = it }
        }
    }

    fun getPlantRepository(): PlantRepository {
        return plantRepo ?: synchronized(this) {
            plantRepo ?: createPlantRepository().also { plantRepo = it }
        }
    }

    fun getAuthRepository(): AuthRepository {
        return authRepo ?: synchronized(this) {
            authRepo ?: createAuthRepository().also { authRepo = it }
        }
    }

    fun getAuditRepository(): AuditRepository {
        return auditRepo ?: synchronized(this) {
            auditRepo ?: createAuditRepository().also { auditRepo = it }
        }
    }

    private fun createLocationRepository(): LocationRepository {
        return try {
            val firestore = FirebaseFirestore.getInstance()
            FirestoreLocationRepository(firestore)
        } catch (_: Exception) {
            UiPreviewOnlyLocationRepository
        }
    }

    private fun createPlantRepository(): PlantRepository {
        return try {
            val firestore = FirebaseFirestore.getInstance()
            FirestorePlantRepository(firestore)
        } catch (_: Exception) {
            UiPreviewOnlyPlantRepository
        }
    }

    private fun createAuthRepository(): AuthRepository {
        return try {
            val auth = FirebaseAuth.getInstance()
            FirebaseAuthRepository(auth)
        } catch (_: Exception) {
            UiPreviewOnlyAuthRepository
        }
    }

    private fun createAuditRepository(): AuditRepository {
        return try {
            val firestore = FirebaseFirestore.getInstance()
            FirestoreAuditRepository(firestore)
        } catch (_: Exception) {
            UiPreviewOnlyAuditRepository
        }
    }
}

/**
 * UI_PREVIEW_ONLY: Fixture repository used solely when Firebase is unconfigured.
 * Must never be treated as real production data.
 */
private object UiPreviewOnlyLocationRepository : LocationRepository {
    private val previewLocations = mutableListOf(
        Location(
            id = "LOC_PREVIEW_01",
            name = "Urban Farming Kelurahan",
            type = LocationType.URBAN_FARMING,
            rw = "01",
            address = "Jl. Raya Bubakan No. 1, Kel. Bubakan",
            description = "Kebun percontohan budidaya sayuran dan pangan mandiri binaan Kelurahan Bubakan.",
            latitude = -7.0681,
            longitude = 110.3289,
            coordinatesStatus = CoordinatesStatus.VERIFIED,
            featured = true,
            photoUrl = null,
            picUid = "system_preview",
            status = LocationStatus.PUBLISHED
        ),
        Location(
            id = "LOC_PREVIEW_02",
            name = "Taman Toga RW 03",
            type = LocationType.TAMAN_TOGA,
            rw = "03",
            address = "Lingkungan RW 03, Kelurahan Bubakan",
            description = "Taman tanaman obat keluarga warga RW 03 yang membudidayakan ragam tanaman herbal tradisional.",
            latitude = -7.0695,
            longitude = 110.3310,
            coordinatesStatus = CoordinatesStatus.VERIFIED,
            featured = true,
            photoUrl = null,
            picUid = "system_preview",
            status = LocationStatus.PUBLISHED
        ),
        Location(
            id = "LOC_PREVIEW_03",
            name = "Kebun Hidroponik RW 05",
            type = LocationType.URBAN_FARMING,
            rw = "05",
            address = "Pekarangan RW 05, Kelurahan Bubakan",
            description = "Pengembangan sayur hidroponik pakcoy dan selada warga RW 05.",
            latitude = -7.0710,
            longitude = 110.3340,
            coordinatesStatus = CoordinatesStatus.PENDING,
            featured = false,
            photoUrl = null,
            picUid = "pic_preview",
            status = LocationStatus.PENDING_APPROVAL
        )
    )

    override fun getPublishedLocations(): Flow<Result<List<Location>>> =
        flowOf(Result.Success(previewLocations.filter { it.status == LocationStatus.PUBLISHED }))

    override fun getFeaturedLocations(): Flow<Result<List<Location>>> =
        flowOf(Result.Success(previewLocations.filter { it.featured && it.status == LocationStatus.PUBLISHED }))

    override fun getLocationsByType(type: LocationType): Flow<Result<List<Location>>> =
        flowOf(Result.Success(previewLocations.filter { it.type == type && it.status == LocationStatus.PUBLISHED }))

    override fun getLocationById(locationId: String): Flow<Result<Location?>> =
        flowOf(Result.Success(previewLocations.find { it.id == locationId }))

    override suspend fun createLocation(location: Location): Result<String> {
        previewLocations.add(location)
        return Result.Success(location.id)
    }

    override suspend fun updateLocation(location: Location): Result<Unit> {
        val index = previewLocations.indexOfFirst { it.id == location.id }
        if (index != -1) {
            previewLocations[index] = location
        } else {
            previewLocations.add(location)
        }
        return Result.Success(Unit)
    }

    override suspend fun getAssignedLocations(picUid: String): Result<List<Location>> =
        Result.Success(previewLocations.filter { it.picUid == picUid || picUid == "system_preview" })

    override suspend fun getPendingLocations(): Result<List<Location>> =
        Result.Success(previewLocations.filter { it.status == LocationStatus.PENDING_APPROVAL })

    override suspend fun deleteLocation(locationId: String): Result<Unit> {
        previewLocations.removeAll { it.id == locationId }
        return Result.Success(Unit)
    }
}

/**
 * UI_PREVIEW_ONLY: Fixture repository used solely when Firebase is unconfigured.
 * Must never be treated as real production data.
 */
private object UiPreviewOnlyPlantRepository : PlantRepository {
    private val previewPlants = mutableListOf(
        MasterPlant(
            id = "PLANT_PREVIEW_01",
            nameId = "Jahe Merah",
            nameLatin = "Zingiber officinale var. rubrum",
            nameMandarin = "红姜",
            pinyin = "hóng jiāng",
            description = "Rimpang berkhasiat menghangatkan tubuh, meredakan peradangan, dan meningkatkan imunitas.",
            primaryPhotoUrl = null,
            mandarinAudioUrl = null
        ),
        MasterPlant(
            id = "PLANT_PREVIEW_02",
            nameId = "Kencur",
            nameLatin = "Kaempferia galanga",
            nameMandarin = "山柰",
            pinyin = "shān nài",
            description = "Tanaman obat keluarga dengan aroma khas, berkhasiat meredakan batuk dan melegakan tenggorokan.",
            primaryPhotoUrl = null,
            mandarinAudioUrl = null
        ),
        MasterPlant(
            id = "PLANT_PREVIEW_03",
            nameId = "Kunyit",
            nameLatin = "Curcuma longa",
            nameMandarin = "姜黄",
            pinyin = "jiāng huáng",
            description = "Herba rimpang kuning kaya kurkuminoid, berfungsi sebagai antioksidan alami dan kesehatan lambung.",
            primaryPhotoUrl = null,
            mandarinAudioUrl = null
        )
    )

    private val previewLocationPlants = mutableListOf(
        LocationPlant(
            id = "LOC_PLANT_0",
            locationId = "LOC_PREVIEW_01",
            masterPlantId = "PLANT_PREVIEW_01",
            localPhotoUrl = null,
            quantityNote = "Bedengan B-1",
            featuredForQr = true
        )
    )

    override fun getAllMasterPlants(): Flow<Result<List<MasterPlant>>> =
        flowOf(Result.Success(previewPlants))

    override fun getMasterPlantById(plantId: String): Flow<Result<MasterPlant?>> =
        flowOf(Result.Success(previewPlants.find { it.id == plantId }))

    override fun getPlantsAtLocation(locationId: String): Flow<Result<List<LocationPlant>>> {
        val matches = previewLocationPlants.filter { it.locationId == locationId }
        return flowOf(Result.Success(matches))
    }

    override suspend fun createMasterPlant(plant: MasterPlant): Result<String> {
        previewPlants.add(plant)
        return Result.Success(plant.id)
    }

    override suspend fun updateMasterPlant(plant: MasterPlant): Result<Unit> {
        val idx = previewPlants.indexOfFirst { it.id == plant.id }
        if (idx != -1) previewPlants[idx] = plant else previewPlants.add(plant)
        return Result.Success(Unit)
    }

    override suspend fun addPlantToLocation(locationPlant: LocationPlant): Result<String> {
        previewLocationPlants.add(locationPlant)
        return Result.Success(locationPlant.id)
    }

    override suspend fun updateLocationPlant(locationPlant: LocationPlant): Result<Unit> {
        val idx = previewLocationPlants.indexOfFirst { it.id == locationPlant.id }
        if (idx != -1) previewLocationPlants[idx] = locationPlant
        return Result.Success(Unit)
    }

    override suspend fun removePlantFromLocation(locationPlantId: String): Result<Unit> {
        previewLocationPlants.removeAll { it.id == locationPlantId }
        return Result.Success(Unit)
    }
}

/**
 * UI_PREVIEW_ONLY: Fixture Auth repository for offline UI testing and previews.
 */
private object UiPreviewOnlyAuthRepository : AuthRepository {
    private val sessionState = MutableStateFlow(
        UserSession(uid = "", email = "", displayName = "", role = UserRole.PUBLIC)
    )

    override val currentUserSession: Flow<UserSession> = sessionState.asStateFlow()

    override suspend fun signInWithEmail(email: String, password: String): Result<UserSession> {
        val session = when {
            email.contains("admin", ignoreCase = true) -> UserSession(
                uid = "admin_preview_uid",
                email = email,
                displayName = "Admin Kelurahan Bubakan",
                role = UserRole.ADMIN
            )
            email.contains("pic", ignoreCase = true) -> UserSession(
                uid = "pic_preview",
                email = email,
                displayName = "Petugas Lapangan RW 01",
                role = UserRole.PIC,
                assignedLocations = listOf("LOC_PREVIEW_01", "LOC_PREVIEW_03")
            )
            else -> UserSession(
                uid = "pic_preview",
                email = email,
                displayName = "Petugas Lapangan",
                role = UserRole.PIC,
                assignedLocations = listOf("LOC_PREVIEW_01")
            )
        }
        sessionState.value = session
        return Result.Success(session)
    }

    override suspend fun signOut(): Result<Unit> {
        sessionState.value = UserSession(uid = "", email = "", displayName = "", role = UserRole.PUBLIC)
        return Result.Success(Unit)
    }

    override fun isUserSignedIn(): Boolean = sessionState.value.role != UserRole.PUBLIC
}

/**
 * UI_PREVIEW_ONLY: Fixture Audit repository.
 */
private object UiPreviewOnlyAuditRepository : AuditRepository {
    private val logs = mutableListOf<AuditLog>()

    override suspend fun recordAction(auditLog: AuditLog): Result<Unit> {
        logs.add(auditLog)
        return Result.Success(Unit)
    }

    override fun getAuditLogs(): Flow<Result<List<AuditLog>>> =
        flowOf(Result.Success(logs.reversed()))
}
