package id.bubakangreen.app.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalFlorist
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LocalFlorist
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Navigation destination contract for BUBAKAN GREEN.
 * Strictly models the approved 4-item global footer navigation:
 * 1. Beranda (Home)
 * 2. Lokasi (Explore)
 * 3. Katalog (LocalFlorist)
 * 4. Admin (Dashboard) - Rightmost
 */
sealed class Screen(
    val route: String,
    val title: String = ""
) {
    data object Home : Screen("home", "Beranda")
    data object Locations : Screen("locations", "Lokasi")
    data object LocationDetail : Screen("location/{locationId}", "Detail Kebun") {
        fun createRoute(locationId: String): String = "location/$locationId"
    }
    data object Catalog : Screen("catalog", "Katalog")
    data object PlantDetail : Screen("plant/{plantId}", "Detail Tanaman") {
        fun createRoute(plantId: String): String = "plant/$plantId"
    }
    data object About : Screen("about", "Tentang Program")

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
 * Global 4-item bottom navigation contract.
 * Order:
 * 1. BERANDA (Home)
 * 2. LOKASI (Explore)
 * 3. KATALOG (LocalFlorist)
 * 4. ADMIN (Dashboard - Rightmost)
 */
data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val activeIcon: ImageVector,
    val inactiveIcon: ImageVector
)

val bottomNavigationItems = listOf(
    BottomNavItem(
        screen = Screen.Home,
        label = "Beranda",
        activeIcon = Icons.Rounded.Home,
        inactiveIcon = Icons.Outlined.Home
    ),
    BottomNavItem(
        screen = Screen.Locations,
        label = "Lokasi",
        activeIcon = Icons.Rounded.Explore,
        inactiveIcon = Icons.Outlined.Explore
    ),
    BottomNavItem(
        screen = Screen.Catalog,
        label = "Katalog",
        activeIcon = Icons.Rounded.LocalFlorist,
        inactiveIcon = Icons.Outlined.LocalFlorist
    ),
    BottomNavItem(
        screen = Screen.AdminDashboard,
        label = "Admin",
        activeIcon = Icons.Rounded.Dashboard,
        inactiveIcon = Icons.Outlined.Dashboard
    )
)
