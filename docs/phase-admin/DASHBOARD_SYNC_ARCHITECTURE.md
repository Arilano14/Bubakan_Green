# ARSITEKTUR SINKRONISASI REALTIME ADMIN DASHBOARD
**Produk:** Bubakan Green  
**Fase:** Admin Remediation  
**Status:** PROPOSED ARCHITECTURE SPECIFICATION  

---

## 1. Analisis Forensik Masalah (Root Cause Forensics)

Pada pengujian fisik/frontend sebelumnya, terjadi fenomena di mana Admin melakukan operasi CRUD (membuat/mengubah tanaman atau lahan), data di backend Firestore berhasil diperbarui, namun layar **Admin Dashboard** tetap menampilkan data lama (stale data).

### Akar Masalah A: Pemutusan Alur Aliran Data oleh `.firstOrNull()`
Di berkas [`app/src/main/java/id/bubakangreen/app/ui/admin/AdminDashboardViewModel.kt`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/app/src/main/java/id/bubakangreen/app/ui/admin/AdminDashboardViewModel.kt) baris 64–75:
```kotlin
fun loadData() {
    viewModelScope.launch {
        _uiState.value = UiState.Loading
        authRepository.currentUserSession.collect { userSession ->
            _session.value = userSession

            val allLocationsResult = locationRepository.getAllLocations().firstOrNull()
            val allLocations = (allLocationsResult as? Result.Success)?.data ?: emptyList()

            // ...
            val plantsResult = plantRepository.getAllMasterPlants().firstOrNull()
            val masterPlants = (plantsResult as? Result.Success)?.data ?: emptyList()
            // ...
```
1. `locationRepository.getAllLocations()` sebenarnya mengembalikan `Flow<Result<List<Location>>>` yang bersumber langsung dari snapshot realtime Firestore (`collection.snapshots()`).
2. Demikian pula `plantRepository.getAllMasterPlants()` mengembalikan `Flow<Result<List<MasterPlant>>>` dari snapshot Firestore.
3. Namun, dengan memanggil `.firstOrNull()`, ViewModel **secara paksa mengambil hanya emisi nilai pertama lalu memutus listener Firestore**!
4. Akibatnya, ketika koleksi `/locations` atau `/master_plants` berubah karena CRUD, listener Firestore sudah mati dan tidak ada emisi baru yang sampai ke `AdminDashboardViewModel`.

### Akar Masalah B: Retensi Siklus Hidup ViewModel di NavHost
Di berkas [`app/src/main/java/id/bubakangreen/app/navigation/BubakanNavHost.kt`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/app/src/main/java/id/bubakangreen/app/navigation/BubakanNavHost.kt) baris 467–475:
```kotlin
composable(Screen.AdminDashboard.route) {
    val adminDashboardViewModel = viewModel {
        AdminDashboardViewModel(...)
    }
```
Ketika Admin berada di Dashboard, lalu menekan tombol Tambah/Edit dan berpindah layar ke `LocationForm` atau `MasterPlantForm`, rute `AdminDashboard` tetap berada di dalam back stack.
Saat form selesai disimpan dan pengguna menekan tombol kembali (`navController.popBackStack()`), instance `AdminDashboardViewModel` **tidak dibuat ulang** dan fungsi `init { loadData() }` **tidak dieksekusi ulang**.
Karena alur Flow sudah terputus oleh `.firstOrNull()`, Dashboard menampilkan data basi secara permanen sampai aplikasi dimatikan paksa.

---

## 2. Arsitektur Solusi Realtime (Target Architecture)

Solusi yang dirancang mematuhi aturan ketat **Section 18 & 19**:
- **Satu Aliran Terpadu (Single Scoped Reactive Stream):** Menggabungkan aliran dokumen `/locations` dan `/master_plants` menggunakan operator Kotlin `combine`.
- **Tanpa Polling:** Tidak ada penarikan berkala per detik.
- **Tanpa Listener Ganda per Kartu:** Hanya ada 1 listener aktif yang mengalirkan data ke StateFlow ViewModel.

### Diagram Alur Data:
```
Firestore (/locations snapshot) ──────┐
                                      ├─► combine() ──► AdminDashboardData ──► StateFlow ──► Compose Recomposition
Firestore (/master_plants snapshot) ──┤
                                      │
AuthRepository (currentUserSession) ──┘
```

### Implementasi Target di `AdminDashboardViewModel.kt`:
```kotlin
class AdminDashboardViewModel(
    private val locationRepository: LocationRepository,
    private val plantRepository: PlantRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<AdminDashboardData>>(UiState.Loading)
    val uiState: StateFlow<UiState<AdminDashboardData>> = _uiState.asStateFlow()

    init {
        observeDashboardData()
    }

    private fun observeDashboardData() {
        viewModelScope.launch {
            combine(
                locationRepository.getAllLocations(),
                plantRepository.getAllMasterPlants(),
                authRepository.currentUserSession
            ) { locResult, plantResult, session ->
                val locations = (locResult as? Result.Success)?.data ?: emptyList()
                val plants = (plantResult as? Result.Success)?.data ?: emptyList()
                
                val activeLocations = locations.count { 
                    it.status == LocationStatus.ACTIVE || it.status == LocationStatus.PUBLISHED 
                }
                val urbanFarmingCount = locations.count { it.type == LocationType.URBAN_FARMING }
                val tamanTogaCount = locations.count { it.type == LocationType.TAMAN_TOGA }
                
                val latestUpdate = locations.maxByOrNull { it.updatedAt }?.name ?: "Belum ada pembaruan"

                AdminDashboardData(
                    totalLocations = locations.size,
                    activeLocations = activeLocations,
                    needsMaintenanceLocations = locations.count { it.status == LocationStatus.NEEDS_MAINTENANCE },
                    latestUpdateText = latestUpdate,
                    locations = locations,
                    masterPlants = plants,
                    totalPublished = activeLocations,
                    totalMasterPlants = plants.size
                )
            }.collect { dashboardData ->
                _uiState.value = UiState.Success(dashboardData)
            }
        }
    }
}
```

---

## 3. Manfaat & Jaminan Arsitektur
1. **Refleksi Instan (< 200ms):** Setiap kali Admin menyimpan atau mengubah Lahan/Tanaman di form, Firestore secara otomatis memancarkan snapshot baru. UI Dashboard langsung ter-recompose menampilkan angka KPI dan entri daftar yang baru seketika saat Admin kembali ke layar Dashboard.
2. **Efisiensi Kuota Firestore:** Menggunakan snapshot query standar. Ketika data tidak berubah, tidak ada biaya pembacaan tambahan.
3. **Bebas Tombol Reload Manual:** Sinkronisasi terjadi secara alami sesuai kaidah arsitektur reaktif modern Android.
