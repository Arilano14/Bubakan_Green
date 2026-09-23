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
