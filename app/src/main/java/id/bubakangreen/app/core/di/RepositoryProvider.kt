package id.bubakangreen.app.core.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import id.bubakangreen.app.core.result.Result
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
import id.bubakangreen.app.domain.model.PlantVoice
import id.bubakangreen.app.domain.model.QuizQuestion
import id.bubakangreen.app.domain.model.UserRole
import id.bubakangreen.app.domain.model.UserSession
import id.bubakangreen.app.domain.repository.AuditRepository
import id.bubakangreen.app.domain.repository.AuthRepository
import id.bubakangreen.app.domain.repository.LocationRepository
import id.bubakangreen.app.domain.repository.PlantRepository
import id.bubakangreen.app.domain.repository.QuizRepository
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
    private val previewLocations = mutableListOf(
        Location(
            id = "LOC_PREVIEW_01",
            name = "Urban Farming Kelurahan Bubakan",
            type = LocationType.URBAN_FARMING,
            rw = "01",
            address = "Jl. Raya Bubakan No. 1, Kel. Bubakan",
            description = "Kebun percontohan budidaya sayuran dan pangan mandiri binaan Kelurahan Bubakan.",
            latitude = -7.09350,
            longitude = 110.32150,
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
            latitude = -7.09050,
            longitude = 110.32300,
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
            latitude = -7.09600,
            longitude = 110.31850,
            coordinatesStatus = CoordinatesStatus.PENDING,
            featured = false,
            photoUrl = null,
            picUid = "pic_preview",
            status = LocationStatus.PENDING_APPROVAL
        )
    )

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
    private val previewPlants = mutableListOf(
        MasterPlant(
            id = "sereh",
            name = "Sereh",
            nameId = "Sereh",
            scientificName = "Cymbopogon citratus",
            nameLatin = "Cymbopogon citratus",
            mandarinName = "柠檬草",
            nameMandarin = "柠檬草",
            mandarinPinyin = "níng méng cǎo",
            pinyin = "níng méng cǎo",
            description = "Tanaman rumput aromatik beraroma sitrun segar yang banyak dibudidayakan di pekarangan urban dan kebun toga.",
            characteristics = "Rumpun daun memanjang berbentuk pita tebal berwarna hijau muda dengan pangkal batang berlapis padat beraroma minyak atsiri khas sitral.",
            commonUses = "Bumbu dapur aromatik rempah nusantara, bahan seduhan teh herbal penghangat tubuh, serta pengusir serangga alami.",
            cultivationNotes = "Tumbuh optimal di tanah gembur berdrainase baik dengan paparan sinar matahari penuh. Diperbanyak melalui anakan rumpun berakar.",
            benefits = "Menghangatkan tubuh, relaksasi otot, penyedap rasa alami masakan nusantara, dan membantu meredakan perut kembung secara tradisional.",
            plantingGuide = "Tanam tunas anakan sereh sedalam 5-10 cm pada tanah subur gembur. Siram teratur 1-2 kali sehari hingga akar baru terbentuk kokoh.",
            defaultPhotoUrl = "plant_sereh",
            primaryPhotoUrl = "plant_sereh",
            imageSourceType = "LOCAL",
            imageAssetName = "plant_sereh",
            imageSource = "https://commons.wikimedia.org/wiki/File:Cymbopogon_citratus_leaves.jpg",
            imageLicense = "CC BY-SA 3.0",
            imageAuthor = "Forest & Kim Starr",
            sourceReferences = "Royal Botanic Gardens, Kew (POWO); Flora of China",
            isPublished = true
        ),
        MasterPlant(
            id = "cabai",
            name = "Cabai",
            nameId = "Cabai",
            scientificName = "Capsicum annuum",
            nameLatin = "Capsicum annuum",
            mandarinName = "辣椒",
            nameMandarin = "辣椒",
            mandarinPinyin = "là jiāo",
            pinyin = "là jiāo",
            description = "Tanaman sayur semusim perdu dengan buah bercitarasa pedas yang kaya akan vitamin C dan senyawa kapsaisin.",
            characteristics = "Batang berkayu semak dengan daun tunggal menyirip dan bunga putih. Buah berbentuk lancip meruncing yang berubah hijau ke merah matang.",
            commonUses = "Bahan utama sambal dan penyedap bumbu kuliner. Kapsaisin dalam cabai secara tradisional digunakan sebagai stimulan sirkulasi.",
            cultivationNotes = "Mudah ditanam pada polybag maupun bedengan dengan sinar matahari cukup dan penyiraman teratur tanpa genangan.",
            benefits = "Kaya vitamin C alami, meningkatkan selera makan, dan kandungan kapsaisin memberikan rasa pedas yang menyegarkan metabolisme tubuh.",
            plantingGuide = "Semai biji cabai selama 2 minggu, pindahkan bibit ke polybag bermedia tanah dan kompos (1:1). Jaga kelembapan tanah.",
            defaultPhotoUrl = "plant_cabai",
            primaryPhotoUrl = "plant_cabai",
            imageSourceType = "LOCAL",
            imageAssetName = "plant_cabai",
            imageSource = "https://commons.wikimedia.org/wiki/File:Capsicum_annuum_var_01.JPG",
            imageLicense = "CC BY-SA 3.0",
            imageAuthor = "Wouter Hagens",
            sourceReferences = "Royal Botanic Gardens, Kew (POWO); IPNI",
            isPublished = true
        ),
        MasterPlant(
            id = "kangkung",
            name = "Kangkung",
            nameId = "Kangkung",
            scientificName = "Ipomoea aquatica",
            nameLatin = "Ipomoea aquatica",
            mandarinName = "空心菜",
            nameMandarin = "空心菜",
            mandarinPinyin = "kōng xīn cài",
            pinyin = "kōng xīn cài",
            description = "Sayuran daun hijau semi-akuatik berbatang berongga yang sangat populer dalam sistem urban farming dan pekarangan.",
            characteristics = "Batang basah berongga di tengah, merambat atau tegak, dengan daun berbentuk mata panah meruncing dan bunga putih keunguan.",
            commonUses = "Sayuran konsumsi hijau harian, kaya serat, vitamin A, zat besi, dan mineral yang mudah diolah menjadi tumisan bergizi.",
            cultivationNotes = "Sangat adaptif di lahan lembap, pot ember, maupun bedengan darat dengan penyiraman intensif 1-2 kali sehari.",
            benefits = "Sumber zat besi dan serat pangan nabati untuk melancarkan pencernaan serta membantu menjaga kebugaran tubuh harian.",
            plantingGuide = "Tanam benih kangkung berjarak 5 cm pada alur bedengan. Siram basah setiap pagi dan sore; dapat dipanen dalam 25-30 hari.",
            defaultPhotoUrl = "plant_kangkung",
            primaryPhotoUrl = "plant_kangkung",
            imageSourceType = "LOCAL",
            imageAssetName = "plant_kangkung",
            imageSource = "https://commons.wikimedia.org/wiki/File:Ipomoea_aquatica_01.JPG",
            imageLicense = "CC BY-SA 3.0",
            imageAuthor = "Frank Vincentz",
            sourceReferences = "Royal Botanic Gardens, Kew (POWO); Flora of China",
            isPublished = true
        ),
        MasterPlant(
            id = "tomat",
            name = "Tomat",
            nameId = "Tomat",
            scientificName = "Solanum lycopersicum",
            nameLatin = "Solanum lycopersicum",
            mandarinName = "番茄",
            nameMandarin = "番茄",
            mandarinPinyin = "fān qié",
            pinyin = "fān qié",
            description = "Tanaman hortikultura semusim dengan buah berdaging merah atau oranye yang kaya likopen dan antioksidan alami.",
            characteristics = "Batang lunak berbulu kelenjar halus dengan aroma khas. Daun majemuk menyirip dan buah buni berdaging banyak biji berair.",
            commonUses = "Bahan sayur masakan, jus buah segar, penambah rasa asam alami sambal, dan sumber antioksidan likopen.",
            cultivationNotes = "Memerlukan tiang ajir penyangga saat buah mulai berbobot. Media tanam gembur kaya bahan organik dengan penyiraman terkontrol.",
            benefits = "Mengandung likopen dosis tinggi dan vitamin A serta C untuk perlindungan sel tubuh dari radikal bebas dan kesehatan mata.",
            plantingGuide = "Tancapkan tiang bambu 1-1.5 meter di samping tanaman tomat. Pangkas tunas air yang tidak produktif agar nutrisi terfokus pada buah.",
            defaultPhotoUrl = "plant_tomat",
            primaryPhotoUrl = "plant_tomat",
            imageSourceType = "LOCAL",
            imageAssetName = "plant_tomat",
            imageSource = "https://commons.wikimedia.org/wiki/File:Tomatoes_on_vine.jpg",
            imageLicense = "CC BY-SA 3.0",
            imageAuthor = "Alvesgaspar",
            sourceReferences = "Royal Botanic Gardens, Kew (POWO); IPNI",
            isPublished = true
        ),
        MasterPlant(
            id = "terong",
            name = "Terong",
            nameId = "Terong",
            scientificName = "Solanum melongena",
            nameLatin = "Solanum melongena",
            mandarinName = "茄子",
            nameMandarin = "茄子",
            mandarinPinyin = "qié zi",
            pinyin = "qié zi",
            description = "Tanaman sayuran buah keluarga Solanaceae yang produktif dan bernilai pangan tinggi untuk ketahanan pangan keluarga.",
            characteristics = "Perdu rendah berkayu semi-lunak dengan daun lebar berbulu halus. Bunga berwarna ungu keputihan dengan buah buni silindris lonjong.",
            commonUses = "Bahan pangan sayur lodeh, balado, lalapan kukus, serta olahan pangan lokal bertekstur lembut kaya serat larut.",
            cultivationNotes = "Tumbuh prima pada media polybag besar atau bedengan terbuka dengan pencahayaan matahari penuh sepanjang hari.",
            benefits = "Tinggi antosianin (nasunin) pada kulit ungu sebagai antioksidan serta serat pangan untuk menjaga kadar kolesterol sehat.",
            plantingGuide = "Pindahkan bibit berdaun 4 ke polybag ukuran 40 cm. Berikan pupuk organik berkala setiap 2 minggu dan cegah genangan air.",
            defaultPhotoUrl = "plant_terong",
            primaryPhotoUrl = "plant_terong",
            imageSourceType = "LOCAL",
            imageAssetName = "plant_terong",
            imageSource = "https://commons.wikimedia.org/wiki/File:Solanum_melongena_002.JPG",
            imageLicense = "CC BY-SA 3.0",
            imageAuthor = "H. Zell",
            sourceReferences = "Royal Botanic Gardens, Kew (POWO); Flora of China",
            isPublished = true
        ),
        MasterPlant(
            id = "jahe",
            name = "Jahe",
            nameId = "Jahe",
            scientificName = "Zingiber officinale",
            nameLatin = "Zingiber officinale",
            mandarinName = "生姜",
            nameMandarin = "生姜",
            mandarinPinyin = "shēng jiāng",
            pinyin = "shēng jiāng",
            description = "Tanaman rimpang temu-temuan kaya senyawa gingerol beraroma pedas hangat sebagai primadona TOGA herbal.",
            characteristics = "Herba tegak berbatang semu berakar rimpang bercabang mendatar di dalam tanah dengan daging kuning berserat aromatik.",
            commonUses = "Bahan minuman wedang jahe penghangat, bumbu rempah aromatik, serta ramuan herbal pereda masuk angin dan mual.",
            cultivationNotes = "Media tanam gembur porous campuran tanah, sekam bakar, dan pupuk kandang agar rimpang berkembang leluasa tanpa busuk.",
            benefits = "Membantu meredakan mual, menghangatkan saluran pernapasan, mengurangi nyeri sendi ringan, dan mendukung sirkulasi darah sehat.",
            plantingGuide = "Tanam potongan rimpang berumur tua yang sudah bertunas sedalam 5 cm. Letakkan di tempat semi-teduh hingga tunas daun tumbuh kuat.",
            defaultPhotoUrl = "plant_jahe",
            primaryPhotoUrl = "plant_jahe",
            imageSourceType = "LOCAL",
            imageAssetName = "plant_jahe",
            imageSource = "https://commons.wikimedia.org/wiki/File:Ginger_Plant.jpg",
            imageLicense = "CC BY-SA 4.0",
            imageAuthor = "Vengolis",
            sourceReferences = "Royal Botanic Gardens, Kew (POWO); Pharmacopoeia of the People's Republic of China",
            isPublished = true
        ),
        MasterPlant(
            id = "kencur",
            name = "Kencur",
            nameId = "Kencur",
            scientificName = "Kaempferia galanga",
            nameLatin = "Kaempferia galanga",
            mandarinName = "沙姜",
            nameMandarin = "沙姜",
            mandarinPinyin = "shā jiāng",
            pinyin = "shā jiāng",
            description = "Tanaman herba obat berdaun mendatar di atas tanah dengan aroma etil p-metoksisinamat khas yang menyegarkan.",
            characteristics = "Herba roset rendah dengan daun mendatar rapat menyentuh tanah berbentuk jorong bundar dan bunga putih bergaris ungu halus.",
            commonUses = "Bahan utama jamu beras kencur, bumbu seblak atau pecel, serta obat kumur herbal alami pereda radang tenggorokan.",
            cultivationNotes = "Menyukai tanah gembur agak lembap dan ternaungi sebagian (tidak terpapar sinar matahari terik langsung terus-menerus).",
            benefits = "Secara tradisional dimanfaatkan melegakan tenggorokan berdahak, meredakan kembung, serta menyegarkan tenaga setelah aktivitas lelah.",
            plantingGuide = "Rebahkan rimpang kencur bertunas di atas permukaan tanah gembur lalu tutup tipis tanah 2 cm. Siram lembut agar tidak hanyut.",
            defaultPhotoUrl = "plant_kencur",
            primaryPhotoUrl = "plant_kencur",
            imageSourceType = "LOCAL",
            imageAssetName = "plant_kencur",
            imageSource = "https://commons.wikimedia.org/wiki/File:Kaempferia_galanga_01.JPG",
            imageLicense = "CC BY-SA 3.0",
            imageAuthor = "H. Zell",
            sourceReferences = "Royal Botanic Gardens, Kew (POWO); Flora of China; Farmakope Herbal Indonesia",
            isPublished = true
        ),
        MasterPlant(
            id = "kunyit",
            name = "Kunyit",
            nameId = "Kunyit",
            scientificName = "Curcuma longa",
            nameLatin = "Curcuma longa",
            mandarinName = "姜黄",
            nameMandarin = "姜黄",
            mandarinPinyin = "jiāng huáng",
            pinyin = "jiāng huáng",
            description = "Rimpang obat oranye cerah kaya kurkumin yang menjadi pilar utama jamu tradisional dan bumbu dapur nusantara.",
            characteristics = "Batang semu dengan daun lebar berurat sejajar. Rimpang induk bulat dengan rimpang cabang silindris berdaging jingga terang berbau aromatik khas.",
            commonUses = "Bahan jamu kunyit asam, pewarna alami kuning masakan (nasi kuning, gulai), serta ramuan pemelihara saluran cerna.",
            cultivationNotes = "Sangat mudah dirawat pada polybag pekarangan. Cukup berikan sinar matahari pagi dan penyiraman berkala 1 kali sehari.",
            benefits = "Kurkumin berperan sebagai antioksidan alami, membantu meredakan inflamasi ringan lambung, dan mendukung fungsi hati yang sehat.",
            plantingGuide = "Gunakan rimpang kunyit tua berbobot 20-30 gram. Tanam pada kedalaman 7 cm dengan mata tunas mengarah ke atas.",
            defaultPhotoUrl = "plant_kunyit",
            primaryPhotoUrl = "plant_kunyit",
            imageSourceType = "LOCAL",
            imageAssetName = "plant_kunyit",
            imageSource = "https://commons.wikimedia.org/wiki/File:Curcuma_longa_plant.jpg",
            imageLicense = "CC BY-SA 4.0",
            imageAuthor = "Pratheepps",
            sourceReferences = "Royal Botanic Gardens, Kew (POWO); WHO Monographs on Selected Medicinal Plants",
            isPublished = true
        ),
        MasterPlant(
            id = "lidah_buaya",
            name = "Lidah Buaya",
            nameId = "Lidah Buaya",
            scientificName = "Aloe vera",
            nameLatin = "Aloe vera",
            mandarinName = "芦荟",
            nameMandarin = "芦荟",
            mandarinPinyin = "lú huì",
            pinyin = "lú huì",
            description = "Tanaman sukulen tebal berlendir bening kaya polisakarida dan asam amino untuk perawatan kulit dan kesehatan alami.",
            characteristics = "Roset daun tebal berdaging sukulen tanpa batang jelas, tepi daun bergerigi duri tumpul lunak, berisi gel bening kental berair.",
            commonUses = "Gel pelembap alami kulit, pereda sengatan panas matahari ringan, bahan minuman segar nata de aloe, dan perawatan rambut.",
            cultivationNotes = "Sukulen yang sangat tahan kekeringan. Jangan menyiram berlebihan; pastikan pot memiliki lubang drainase lancar berpasir.",
            benefits = "Membantu menyejukkan kulit terbakar matahari, menjaga kelembapan alami jaringan kulit, serta melancarkan metabolisme secara aman.",
            plantingGuide = "Tanam anakan lidah buaya pada pot berpasir malang dan kompos (2:1). Siram cukup 2-3 hari sekali saat media mulai mengering.",
            defaultPhotoUrl = "plant_lidah_buaya",
            primaryPhotoUrl = "plant_lidah_buaya",
            imageSourceType = "LOCAL",
            imageAssetName = "plant_lidah_buaya",
            imageSource = "https://commons.wikimedia.org/wiki/File:Aloe_vera_-_jardin_botanique_de_Lyon.jpg",
            imageLicense = "CC BY-SA 4.0",
            imageAuthor = "Ji-Elle",
            sourceReferences = "Royal Botanic Gardens, Kew (POWO); Pharmacopoeia of the People's Republic of China",
            isPublished = true
        )
    )

    private val previewLocationPlants = mutableListOf(
        // Urban Farming (5 plants)
        LocationPlant(
            id = "lp_uf_sereh",
            locationId = "LOC_PREVIEW_01",
            plantId = "sereh",
            masterPlantId = "sereh",
            localPhotoUrl = "plant_sereh",
            photoUrl = "plant_sereh",
            quantity = 10,
            quantityNote = "10 rumpun polybag",
            notes = "Plot bedengan percontohan sereh wangi belakang kelurahan.",
            featuredForQr = true
        ),
        LocationPlant(
            id = "lp_uf_cabai",
            locationId = "LOC_PREVIEW_01",
            plantId = "cabai",
            masterPlantId = "cabai",
            localPhotoUrl = "plant_cabai",
            photoUrl = "plant_cabai",
            quantity = 15,
            quantityNote = "15 polybag cabai",
            notes = "Deretan polybag sayuran produktif konsumsi warga.",
            featuredForQr = false
        ),
        LocationPlant(
            id = "lp_uf_kangkung",
            locationId = "LOC_PREVIEW_01",
            plantId = "kangkung",
            masterPlantId = "kangkung",
            localPhotoUrl = "plant_kangkung",
            photoUrl = "plant_kangkung",
            quantity = 2,
            quantityNote = "2 bak instalasi bedengan kangkung",
            notes = "Bedengan sayuran daun cepat panen ramah pekarangan.",
            featuredForQr = false
        ),
        LocationPlant(
            id = "lp_uf_tomat",
            locationId = "LOC_PREVIEW_01",
            plantId = "tomat",
            masterPlantId = "tomat",
            localPhotoUrl = "plant_tomat",
            photoUrl = "plant_tomat",
            quantity = 12,
            quantityNote = "12 polybag tomat",
            notes = "Tanaman buah tomat dengan tiang ajir bambu.",
            featuredForQr = false
        ),
        LocationPlant(
            id = "lp_uf_terong",
            locationId = "LOC_PREVIEW_01",
            plantId = "terong",
            masterPlantId = "terong",
            localPhotoUrl = "plant_terong",
            photoUrl = "plant_terong",
            quantity = 8,
            quantityNote = "8 pot terong ungu",
            notes = "Pot tanaman terong ungu produktif.",
            featuredForQr = false
        ),

        // Taman Toga RW 03 (5 plants, Sereh REUSED!)
        LocationPlant(
            id = "lp_toga_sereh",
            locationId = "LOC_PREVIEW_02",
            plantId = "sereh",
            masterPlantId = "sereh",
            localPhotoUrl = "plant_sereh",
            photoUrl = "plant_sereh",
            quantity = 6,
            quantityNote = "6 rumpun serai dapur toga",
            notes = "Koleksi TOGA warga RW 03 sebagai tanaman aromatik obat.",
            featuredForQr = true
        ),
        LocationPlant(
            id = "lp_toga_jahe",
            locationId = "LOC_PREVIEW_02",
            plantId = "jahe",
            masterPlantId = "jahe",
            localPhotoUrl = "plant_jahe",
            photoUrl = "plant_jahe",
            quantity = 10,
            quantityNote = "10 polybag jahe merah",
            notes = "Rimpang obat unggulan penghangat tubuh.",
            featuredForQr = false
        ),
        LocationPlant(
            id = "lp_toga_kencur",
            locationId = "LOC_PREVIEW_02",
            plantId = "kencur",
            masterPlantId = "kencur",
            localPhotoUrl = "plant_kencur",
            photoUrl = "plant_kencur",
            quantity = 8,
            quantityNote = "8 pot kencur roset",
            notes = "Tanaman herba obat roset aromatik.",
            featuredForQr = false
        ),
        LocationPlant(
            id = "lp_toga_kunyit",
            locationId = "LOC_PREVIEW_02",
            plantId = "kunyit",
            masterPlantId = "kunyit",
            localPhotoUrl = "plant_kunyit",
            photoUrl = "plant_kunyit",
            quantity = 12,
            quantityNote = "12 polybag kunyit kuning",
            notes = "Rimpang kunyit kaya kurkuminoid.",
            featuredForQr = false
        ),
        LocationPlant(
            id = "lp_toga_lidah_buaya",
            locationId = "LOC_PREVIEW_02",
            plantId = "lidah_buaya",
            masterPlantId = "lidah_buaya",
            localPhotoUrl = "plant_lidah_buaya",
            photoUrl = "plant_lidah_buaya",
            quantity = 5,
            quantityNote = "5 pot lidah buaya sukulen",
            notes = "Sukulen gel pelembap alami.",
            featuredForQr = false
        )
    )

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
    private val previewQuestions = id.bubakangreen.app.data.fixture.DefaultLearningData.quizQuestions.toMutableList()

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

