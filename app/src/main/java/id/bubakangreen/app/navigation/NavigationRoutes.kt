package id.bubakangreen.app.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Search
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Navigation destination contract for BUBAKAN GREEN.
 * Strictly models the approved 5-tab architecture, detail screens, and deep-link targets.
 */
sealed class Screen(
    val route: String,
    val title: String = ""
) {
    data object Home : Screen("home", "Beranda")
    data object Locations : Screen("locations", "Jelajah Kebun")
    data object LocationDetail : Screen("location/{locationId}", "Detail Kebun") {
        fun createRoute(locationId: String): String = "location/$locationId"
    }
    data object Catalog : Screen("catalog", "Katalog Tanaman")
    data object PlantDetail : Screen("plant/{plantId}", "Detail Tanaman") {
        fun createRoute(plantId: String): String = "plant/$plantId"
    }
    data object About : Screen("about", "Profil Program")

    // Authenticated Management Screens
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

/**
 * 5-item bottom bar definition complying with the Duolingo-style navigation redesign.
 * LEFT: Admin, Explore
 * CENTER: Home
 * RIGHT: Catalog, Profile
 */
data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val activeIcon: ImageVector,
    val inactiveIcon: ImageVector
)

val bottomNavigationItems = listOf(
    BottomNavItem(
        screen = Screen.AdminDashboard,
        label = "Admin",
        activeIcon = Icons.Filled.AdminPanelSettings,
        inactiveIcon = Icons.Outlined.AdminPanelSettings
    ),
    BottomNavItem(
        screen = Screen.Locations,
        label = "Jelajah",
        activeIcon = Icons.Filled.Place,
        inactiveIcon = Icons.Outlined.Place
    ),
    BottomNavItem(
        screen = Screen.Home,
        label = "Beranda",
        activeIcon = Icons.Filled.Home,
        inactiveIcon = Icons.Outlined.Home
    ),
    BottomNavItem(
        screen = Screen.Catalog,
        label = "Katalog",
        activeIcon = Icons.Filled.Search,
        inactiveIcon = Icons.Outlined.Search
    ),
    BottomNavItem(
        screen = Screen.About,
        label = "Profil",
        activeIcon = Icons.Filled.Person,
        inactiveIcon = Icons.Outlined.Person
    )
)
