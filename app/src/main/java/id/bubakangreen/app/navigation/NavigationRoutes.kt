package id.bubakangreen.app.navigation

/**
 * Navigation destination contract for BUBAKAN GREEN.
 * Strictly models the approved 3-tab architecture, detail screens, and deep-link targets.
 */
sealed class Screen(
    val route: String,
    val title: String = ""
) {
    data object Home : Screen("home", "Beranda")
    data object Locations : Screen("locations", "Lokasi & Peta")
    data object LocationDetail : Screen("location/{locationId}", "Detail Lokasi") {
        fun createRoute(locationId: String): String = "location/$locationId"
    }
    data object Catalog : Screen("catalog", "Katalog")
    data object PlantDetail : Screen("plant/{plantId}", "Detail Tanaman") {
        fun createRoute(plantId: String): String = "plant/$plantId"
    }
    data object About : Screen("about", "Tentang")

    // Phase 4 Authenticated Management Screens
    data object Login : Screen("login", "Masuk Petugas")
    data object PicDashboard : Screen("pic_dashboard", "Dashboard Petugas")
    data object LocationForm : Screen("location_form?locationId={locationId}", "Form Kebun") {
        fun createRoute(locationId: String? = null): String =
            if (locationId != null) "location_form?locationId=$locationId" else "location_form"
    }
    data object PlantForm : Screen("plant_form/{locationId}", "Tambah Tanaman") {
        fun createRoute(locationId: String): String = "plant_form/$locationId"
    }
    data object AdminDashboard : Screen("admin_dashboard", "Admin Kelurahan")
    data object LocationApproval : Screen("location_approval", "Persetujuan Kebun")
    data object MasterPlantForm : Screen("master_plant_form?plantId={plantId}", "Master Tanaman") {
        fun createRoute(plantId: String? = null): String =
            if (plantId != null) "master_plant_form?plantId=$plantId" else "master_plant_form"
    }
}

data class BottomNavItem(
    val screen: Screen,
    val label: String
)

val bottomNavigationItems = listOf(
    BottomNavItem(Screen.Home, "Beranda"),
    BottomNavItem(Screen.Locations, "Lokasi"),
    BottomNavItem(Screen.Catalog, "Katalog")
)
