package id.bubakangreen.app.core.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import id.bubakangreen.app.core.result.Result
import id.bubakangreen.app.data.fixture.DefaultBotanicalData
import id.bubakangreen.app.data.remote.FirebaseAuthRepository
import id.bubakangreen.app.data.remote.FirestoreAuditRepository
import id.bubakangreen.app.data.remote.FirestoreLocationRepository
import id.bubakangreen.app.data.remote.FirestorePlantRepository
import id.bubakangreen.app.data.remote.FirestoreQuizRepository
import id.bubakangreen.app.data.remote.FirestoreVoiceRepository
import id.bubakangreen.app.domain.model.AuditLog
import id.bubakangreen.app.domain.model.CoordinatesStatus
import id.bubakangreen.app.domain.model.Location
import id.bubakangreen.app.domain.model.LocationConditionLog
import id.bubakangreen.app.domain.model.LocationPlant
import id.bubakangreen.app.domain.model.LocationStatus
import id.bubakangreen.app.domain.model.LocationType
import id.bubakangreen.app.domain.model.MasterPlant
import id.bubakangreen.app.domain.model.PlantStatus
import id.bubakangreen.app.domain.model.PlantVoice
import id.bubakangreen.app.domain.model.QuizQuestion
import id.bubakangreen.app.domain.model.UserRole
import id.bubakangreen.app.domain.model.UserSession
import id.bubakangreen.app.data.local.LocalStorageRepository
import id.bubakangreen.app.data.remote.FirebaseStorageRepository
import id.bubakangreen.app.domain.repository.AuditRepository
import id.bubakangreen.app.domain.repository.AuthRepository
import id.bubakangreen.app.domain.repository.LocationRepository
import id.bubakangreen.app.domain.repository.PlantRepository
import id.bubakangreen.app.domain.repository.QuizRepository
import id.bubakangreen.app.domain.repository.StorageRepository
import id.bubakangreen.app.domain.repository.VoiceRepository
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

    private var appContext: android.content.Context? = null

    fun init(context: android.content.Context) {
        appContext = context.applicationContext
    }

    fun getAppContext(): android.content.Context? = appContext

    private var locationRepo: LocationRepository? = null
    private var plantRepo: PlantRepository? = null
    private var authRepo: AuthRepository? = null
    private var auditRepo: AuditRepository? = null
    private var quizRepo: QuizRepository? = null
    private var voiceRepo: VoiceRepository? = null
    private var storageRepo: StorageRepository? = null

    // PRE-BLAZE CONFIGURATION: Kept false until Cloud Billing is activated
    const val USE_FIREBASE_STORAGE = false

    fun getStorageRepository(): StorageRepository {
        return storageRepo ?: synchronized(this) {
            storageRepo ?: createStorageRepository().also { storageRepo = it }
        }
    }

    private fun createStorageRepository(): StorageRepository {
        return if (USE_FIREBASE_STORAGE) {
            FirebaseStorageRepository()
        } else {
            val ctx = appContext
            if (ctx != null) {
                LocalStorageRepository(ctx)
            } else {
                object : StorageRepository {
                    override suspend fun uploadImage(bytes: ByteArray, filename: String, folder: String): Result<String> {
                        return Result.Success("/mock_storage/$folder/$filename")
                    }
                    override suspend fun deleteImage(pathOrUrl: String): Result<Unit> {
                        return Result.Success(Unit)
                    }
                    override fun getImageUrl(pathOrUrl: String): String = pathOrUrl
                }
            }
        }
    }

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

    fun getQuizRepository(): QuizRepository {
        return quizRepo ?: synchronized(this) {
            quizRepo ?: createQuizRepository().also { quizRepo = it }
        }
    }

    fun getVoiceRepository(): VoiceRepository {
        return voiceRepo ?: synchronized(this) {
            voiceRepo ?: createVoiceRepository().also { voiceRepo = it }
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

    private fun createQuizRepository(): QuizRepository {
        return try {
            val firestore = FirebaseFirestore.getInstance()
            FirestoreQuizRepository(firestore)
        } catch (_: Exception) {
            UiPreviewOnlyQuizRepository
        }
    }

    private fun createVoiceRepository(): VoiceRepository {
        return try {
            val firestore = FirebaseFirestore.getInstance()
            FirestoreVoiceRepository(firestore)
        } catch (_: Exception) {
            UiPreviewOnlyVoiceRepository
        }
    }
}

/**
 * UI_PREVIEW_ONLY: Fixture repository used solely when Firebase is unconfigured.
 * Must never be treated as real production data.
 */
private object UiPreviewOnlyLocationRepository : LocationRepository {
    private val previewLocations = DefaultBotanicalData.defaultLocations.toMutableList()

    private val previewConditionLogs = mutableListOf<LocationConditionLog>()

    override fun getPublishedLocations(): Flow<Result<List<Location>>> =
        flowOf(Result.Success(previewLocations.filter { it.isPublished && it.status != LocationStatus.INACTIVE && it.status != LocationStatus.ARCHIVED }))

    override fun getFeaturedLocations(): Flow<Result<List<Location>>> =
        flowOf(Result.Success(previewLocations.filter { it.featured && it.isPublished && it.status != LocationStatus.INACTIVE }))

    override fun getLocationsByType(type: LocationType): Flow<Result<List<Location>>> =
        flowOf(Result.Success(previewLocations.filter { it.type == type && it.isPublished && it.status != LocationStatus.INACTIVE }))

    override fun getLocationById(locationId: String): Flow<Result<Location?>> =
        flowOf(Result.Success(previewLocations.find { it.id == locationId }))

    override fun getAllLocations(): Flow<Result<List<Location>>> =
        flowOf(Result.Success(previewLocations))

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

    override suspend fun updateLocationCondition(
        locationId: String,
        status: String,
        note: String,
        photoUrl: String?,
        updatedBy: String
    ): Result<Unit> {
        val now = System.currentTimeMillis()
        val index = previewLocations.indexOfFirst { it.id == locationId }
        val parsedStatus = runCatching { LocationStatus.valueOf(status) }.getOrDefault(LocationStatus.ACTIVE)
        if (index != -1) {
            val old = previewLocations[index]
            previewLocations[index] = old.copy(
                status = parsedStatus,
                conditionNote = note,
                conditionUpdatedAt = now,
                conditionUpdatedBy = updatedBy,
                coverPhotoUrl = photoUrl ?: old.coverPhotoUrl,
                photoUrl = photoUrl ?: old.photoUrl,
                updatedAt = now
            )
        }
        previewConditionLogs.add(
            LocationConditionLog(
                id = "LOG_${System.currentTimeMillis()}",
                locationId = locationId,
                status = parsedStatus.name,
                note = note,
                photoUrl = photoUrl,
                updatedBy = updatedBy,
                updatedAt = now
            )
        )
        return Result.Success(Unit)
    }

    override fun getLocationConditionLogs(locationId: String): Flow<Result<List<LocationConditionLog>>> =
        flowOf(Result.Success(previewConditionLogs.filter { it.locationId == locationId }.sortedByDescending { it.updatedAt }))

    override suspend fun deactivateLocation(locationId: String): Result<Unit> {
        val index = previewLocations.indexOfFirst { it.id == locationId }
        if (index != -1) {
            val old = previewLocations[index]
            previewLocations[index] = old.copy(
                isPublished = false,
                status = LocationStatus.INACTIVE,
                updatedAt = System.currentTimeMillis()
            )
        }
        return Result.Success(Unit)
    }

    override suspend fun getAssignedLocations(picUid: String): Result<List<Location>> =
        Result.Success(previewLocations.filter { it.picUid == picUid || picUid == "system_preview" })

    override suspend fun getPendingLocations(): Result<List<Location>> =
        Result.Success(previewLocations.filter { it.status == LocationStatus.PENDING_APPROVAL })

    override suspend fun deleteLocation(locationId: String): Result<Unit> =
        deactivateLocation(locationId)
}


/**
 * UI_PREVIEW_ONLY: Fixture repository used solely when Firebase is unconfigured.
 * Must never be treated as real production data.
 */
private object UiPreviewOnlyPlantRepository : PlantRepository {
    private val previewPlants = DefaultBotanicalData.defaultMasterPlants.toMutableList()
    private val previewLocationPlants = DefaultBotanicalData.defaultLocationPlants.toMutableList()

    override fun getAllMasterPlants(): Flow<Result<List<MasterPlant>>> =
        flowOf(Result.Success(previewPlants))

    override fun getMasterPlantById(plantId: String): Flow<Result<MasterPlant?>> =
        flowOf(Result.Success(previewPlants.find { it.id == plantId }))

    override fun getPlantsAtLocation(locationId: String): Flow<Result<List<LocationPlant>>> {
        val matches = previewLocationPlants.filter {
            it.locationId == locationId ||
            (locationId == "loc_urban_farming_bubakan" && it.locationId == "LOC_PREVIEW_01") ||
            (locationId == "loc_taman_toga_rw03" && it.locationId == "LOC_PREVIEW_02") ||
            (locationId == "LOC_PREVIEW_01" && it.locationId == "loc_urban_farming_bubakan") ||
            (locationId == "LOC_PREVIEW_02" && it.locationId == "loc_taman_toga_rw03")
        }
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

    override suspend fun addPlantsToLocation(locationId: String, plantIds: List<String>): Result<Int> {
        var added = 0
        plantIds.distinct().forEach { pid ->
            val relId = "${locationId}_${pid}"
            if (previewLocationPlants.none { it.id == relId }) {
                previewLocationPlants.add(
                    LocationPlant(
                        id = relId,
                        locationId = locationId,
                        plantId = pid,
                        masterPlantId = pid,
                        isPresent = true,
                        status = PlantStatus.ACTIVE
                    )
                )
                added++
            }
        }
        return Result.Success(added)
    }

    override suspend fun createMasterPlantWithLocation(plant: MasterPlant, locationId: String): Result<String> {
        previewPlants.add(plant)
        val relId = "${locationId}_${plant.id}"
        previewLocationPlants.add(
            LocationPlant(
                id = relId,
                locationId = locationId,
                plantId = plant.id,
                masterPlantId = plant.id,
                isPresent = true,
                status = PlantStatus.ACTIVE
            )
        )
        return Result.Success(plant.id)
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
    private val sessionState: MutableStateFlow<UserSession> by lazy {
        val context = RepositoryProvider.getAppContext()
        val savedSession = context?.let { id.bubakangreen.app.core.auth.AuthSessionStorage.getPermanentAdminSession(it) }
        MutableStateFlow(
            savedSession ?: UserSession(uid = "", email = "", displayName = "", role = UserRole.PUBLIC)
        )
    }

    override val currentUserSession: Flow<UserSession>
        get() = sessionState.asStateFlow()

    override suspend fun signInWithEmail(email: String, password: String): Result<UserSession> {
        val session = when {
            email.contains("admin", ignoreCase = true) -> {
                if (password != "admin_bubakan" && password != "admin" && password != "admin123") {
                    return Result.Error(Exception("Password salah."))
                }
                val adminSession = UserSession(
                    uid = "admin_preview_uid",
                    email = email,
                    displayName = "Admin Kelurahan Bubakan",
                    role = UserRole.ADMIN,
                    isActive = true
                )
                RepositoryProvider.getAppContext()?.let {
                    id.bubakangreen.app.core.auth.AuthSessionStorage.savePermanentAdmin(it, adminSession)
                }
                adminSession
            }
            email.contains("pic", ignoreCase = true) -> {
                UserSession(
                    uid = "pic_preview_uid",
                    email = email,
                    displayName = "PIC Kebun Bubakan",
                    role = UserRole.PIC,
                    isActive = true
                )
            }
            email.contains("unauthorized", ignoreCase = true) -> {
                UserSession(
                    uid = "unauthorized_preview_uid",
                    email = email,
                    displayName = "Warga Biasa",
                    role = UserRole.PUBLIC,
                    isActive = true
                )
            }
            else -> {
                return Result.Error(Exception("Username atau password salah."))
            }
        }
        sessionState.value = session
        return Result.Success(session)
    }

    override suspend fun signOut(): Result<Unit> {
        RepositoryProvider.getAppContext()?.let {
            id.bubakangreen.app.core.auth.AuthSessionStorage.clearSession(it)
        }
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

/**
 * UI_PREVIEW_ONLY: Fixture Quiz repository using 135 canonical questions.
 */
private object UiPreviewOnlyQuizRepository : QuizRepository {
    private val previewQuestions = id.bubakangreen.app.data.fixture.DefaultLearningData.allQuizQuestions.toMutableList()

    override fun getQuestionsByPlant(plantId: String): Flow<Result<List<QuizQuestion>>> =
        flowOf(Result.Success(previewQuestions.filter { it.plantId == plantId && it.isActive }))

    override suspend fun createQuestion(question: QuizQuestion): Result<String> {
        previewQuestions.add(question)
        return Result.Success(question.questionId)
    }
}

/**
 * UI_PREVIEW_ONLY: Fixture Voice repository using 9 default plant voice metadata.
 */
private object UiPreviewOnlyVoiceRepository : VoiceRepository {
    private val previewVoices = id.bubakangreen.app.data.fixture.DefaultLearningData.plantVoices.toMutableList()

    override fun getVoiceByPlant(plantId: String): Flow<Result<PlantVoice?>> =
        flowOf(Result.Success(previewVoices.find { it.plantId == plantId && it.isActive }))

    override suspend fun setPlantVoice(voice: PlantVoice): Result<Unit> {
        val idx = previewVoices.indexOfFirst { it.plantId == voice.plantId }
        if (idx != -1) previewVoices[idx] = voice else previewVoices.add(voice)
        return Result.Success(Unit)
    }
}

