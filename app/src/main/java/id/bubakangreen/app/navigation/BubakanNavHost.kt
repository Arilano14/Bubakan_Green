package id.bubakangreen.app.navigation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import id.bubakangreen.app.core.di.RepositoryProvider
import id.bubakangreen.app.data.location.AndroidLocationClient
import id.bubakangreen.app.ui.about.AboutScreen
import id.bubakangreen.app.ui.admin.AdminDashboardScreen
import id.bubakangreen.app.ui.admin.AdminDashboardViewModel
import id.bubakangreen.app.ui.admin.LocationApprovalScreen
import id.bubakangreen.app.ui.admin.LocationApprovalViewModel
import id.bubakangreen.app.ui.admin.MasterPlantFormScreen
import id.bubakangreen.app.ui.admin.MasterPlantViewModel
import id.bubakangreen.app.ui.auth.LoginScreen
import id.bubakangreen.app.ui.auth.LoginViewModel
import id.bubakangreen.app.ui.catalog.CatalogScreen
import id.bubakangreen.app.ui.catalog.PlantDetailScreen
import id.bubakangreen.app.ui.home.HomeScreen
import id.bubakangreen.app.ui.locations.LocationDetailScreen
import id.bubakangreen.app.ui.locations.LocationsScreen
import id.bubakangreen.app.ui.pic.LocationFormScreen
import id.bubakangreen.app.ui.pic.LocationFormViewModel
import id.bubakangreen.app.ui.pic.PicDashboardScreen
import id.bubakangreen.app.ui.pic.PicDashboardViewModel
import id.bubakangreen.app.ui.pic.PlantFormScreen
import id.bubakangreen.app.ui.pic.PlantFormViewModel
import id.bubakangreen.app.ui.theme.OnSurfaceVariant
import id.bubakangreen.app.ui.theme.OutlineGrey
import id.bubakangreen.app.ui.theme.PrimaryContainerMint
import id.bubakangreen.app.ui.theme.PrimaryForest
import id.bubakangreen.app.ui.theme.SurfaceWhite

/**
 * Main application navigation shell.
 * Coordinates 3-tab bottom bar, screen backstack, deep-link routing,
 * and authenticated PIC & Admin governance workflows.
 */
@Composable
fun BubakanAppNavHost(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val context = LocalContext.current

    val rootRoutes = listOf(
        Screen.Home.route,
        Screen.Locations.route,
        Screen.Catalog.route
    )
    val showBottomBar = currentDestination?.route in rootRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = SurfaceWhite,
                    tonalElevation = 0.dp,
                    modifier = Modifier.border(BorderStroke(1.dp, OutlineGrey))
                ) {
                    bottomNavigationItems.forEach { item ->
                        val isSelected = currentDestination?.hierarchy?.any { it.route == item.screen.route } == true
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                navController.navigate(item.screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                val iconVector = when (item.screen) {
                                    Screen.Home -> if (isSelected) Icons.Filled.Home else Icons.Outlined.Home
                                    Screen.Locations -> if (isSelected) Icons.Filled.Place else Icons.Outlined.Place
                                    Screen.Catalog -> if (isSelected) Icons.Filled.Search else Icons.Outlined.Search
                                    else -> Icons.Filled.Home
                                }
                                Icon(
                                    imageVector = iconVector,
                                    contentDescription = item.label
                                )
                            },
                            label = {
                                Text(
                                    text = item.label,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = PrimaryForest,
                                selectedTextColor = PrimaryForest,
                                indicatorColor = PrimaryContainerMint,
                                unselectedIconColor = OnSurfaceVariant,
                                unselectedTextColor = OnSurfaceVariant
                            )
                        )
                    }
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            // TAB 1: Beranda (Home)
            composable(Screen.Home.route) {
                HomeScreen(
                    onLocationClick = { locationId ->
                        navController.navigate(Screen.LocationDetail.createRoute(locationId))
                    },
                    onPlantClick = { plantId ->
                        navController.navigate(Screen.PlantDetail.createRoute(plantId))
                    },
                    onNavigateToLocations = {
                        navController.navigate(Screen.Locations.route)
                    },
                    onNavigateToCatalog = {
                        navController.navigate(Screen.Catalog.route)
                    },
                    onInfoClick = {
                        navController.navigate(Screen.About.route)
                    },
                    onLoginClick = {
                        navController.navigate(Screen.Login.route)
                    }
                )
            }

            // TAB 2: Lokasi & Peta
            composable(Screen.Locations.route) {
                LocationsScreen(
                    onLocationClick = { locationId ->
                        navController.navigate(Screen.LocationDetail.createRoute(locationId))
                    },
                    onInfoClick = {
                        navController.navigate(Screen.About.route)
                    }
                )
            }

            // TAB 3: Katalog Tanaman
            composable(Screen.Catalog.route) {
                CatalogScreen(
                    onPlantClick = { plantId ->
                        navController.navigate(Screen.PlantDetail.createRoute(plantId))
                    },
                    onInfoClick = {
                        navController.navigate(Screen.About.route)
                    }
                )
            }

            // Location Detail Screen (Accessible via Card or Deep Link)
            composable(
                route = Screen.LocationDetail.route,
                arguments = listOf(
                    navArgument("locationId") { type = NavType.StringType }
                ),
                deepLinks = listOf(
                    navDeepLink { uriPattern = "https://bubakangreen.web.app/location/{locationId}" },
                    navDeepLink { uriPattern = "https://bubakangreen.app/location/{locationId}" }
                )
            ) { backStackEntry ->
                val locationId = backStackEntry.arguments?.getString("locationId") ?: ""
                LocationDetailScreen(
                    locationId = locationId,
                    onNavigateBack = {
                        if (!navController.popBackStack()) {
                            navController.navigate(Screen.Home.route)
                        }
                    },
                    onPlantClick = { plantId ->
                        navController.navigate(Screen.PlantDetail.createRoute(plantId))
                    },
                    onInfoClick = {
                        navController.navigate(Screen.About.route)
                    }
                )
            }

            // Plant Detail Screen (Accessible via Card, Catalog, or QR Deep Link)
            composable(
                route = Screen.PlantDetail.route,
                arguments = listOf(
                    navArgument("plantId") { type = NavType.StringType }
                ),
                deepLinks = listOf(
                    navDeepLink { uriPattern = "https://bubakangreen.web.app/plant/{plantId}" },
                    navDeepLink { uriPattern = "https://bubakangreen.app/plant/{plantId}" }
                )
            ) { backStackEntry ->
                val plantId = backStackEntry.arguments?.getString("plantId") ?: ""
                PlantDetailScreen(
                    plantId = plantId,
                    onNavigateBack = {
                        if (!navController.popBackStack()) {
                            navController.navigate(Screen.Home.route)
                        }
                    },
                    onInfoClick = {
                        navController.navigate(Screen.About.route)
                    }
                )
            }

            // About Screen
            composable(Screen.About.route) {
                AboutScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            // ==========================================
            // PHASE 4: AUTHENTICATED MANAGEMENT ROUTES
            // ==========================================

            // SCR-AUTH-01: Login Screen
            composable(Screen.Login.route) {
                val loginViewModel = viewModel {
                    LoginViewModel(RepositoryProvider.getAuthRepository())
                }
                LoginScreen(
                    viewModel = loginViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToPicDashboard = {
                        navController.navigate(Screen.PicDashboard.route) {
                            popUpTo(Screen.Home.route)
                        }
                    },
                    onNavigateToAdminDashboard = {
                        navController.navigate(Screen.AdminDashboard.route) {
                            popUpTo(Screen.Home.route)
                        }
                    }
                )
            }

            // SCR-PIC-01: PIC Dashboard
            composable(Screen.PicDashboard.route) {
                val picDashboardViewModel = viewModel {
                    PicDashboardViewModel(
                        RepositoryProvider.getLocationRepository(),
                        RepositoryProvider.getAuthRepository()
                    )
                }
                PicDashboardScreen(
                    viewModel = picDashboardViewModel,
                    onNavigateToLocationForm = { locId ->
                        navController.navigate(Screen.LocationForm.createRoute(locId))
                    },
                    onNavigateToPlantForm = { locId ->
                        navController.navigate(Screen.PlantForm.createRoute(locId))
                    },
                    onSignedOut = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                )
            }

            // SCR-PIC-02: Location Form with Single-Shot GPS
            composable(
                route = Screen.LocationForm.route,
                arguments = listOf(
                    navArgument("locationId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStackEntry ->
                val locationId = backStackEntry.arguments?.getString("locationId")
                val locationFormViewModel = viewModel {
                    LocationFormViewModel(
                        RepositoryProvider.getLocationRepository(),
                        RepositoryProvider.getAuditRepository(),
                        AndroidLocationClient(context)
                    )
                }
                LocationFormScreen(
                    viewModel = locationFormViewModel,
                    picUid = "pic_officer",
                    locationId = locationId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // SCR-PIC-03: Plant Form
            composable(
                route = Screen.PlantForm.route,
                arguments = listOf(
                    navArgument("locationId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val locationId = backStackEntry.arguments?.getString("locationId") ?: ""
                val plantFormViewModel = viewModel {
                    PlantFormViewModel(
                        locationId,
                        RepositoryProvider.getLocationRepository(),
                        RepositoryProvider.getPlantRepository(),
                        RepositoryProvider.getAuditRepository()
                    )
                }
                PlantFormScreen(
                    viewModel = plantFormViewModel,
                    picUid = "pic_officer",
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // SCR-ADM-01: Admin Dashboard
            composable(Screen.AdminDashboard.route) {
                val adminDashboardViewModel = viewModel {
                    AdminDashboardViewModel(
                        RepositoryProvider.getLocationRepository(),
                        RepositoryProvider.getPlantRepository(),
                        RepositoryProvider.getAuthRepository()
                    )
                }
                AdminDashboardScreen(
                    viewModel = adminDashboardViewModel,
                    onNavigateToApprovalQueue = {
                        navController.navigate(Screen.LocationApproval.route)
                    },
                    onNavigateToMasterPlantForm = { plantId ->
                        navController.navigate(Screen.MasterPlantForm.createRoute(plantId))
                    },
                    onSignedOut = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                )
            }

            // SCR-ADM-02: Location Approval Queue
            composable(Screen.LocationApproval.route) {
                val approvalViewModel = viewModel {
                    LocationApprovalViewModel(
                        RepositoryProvider.getLocationRepository(),
                        RepositoryProvider.getAuditRepository()
                    )
                }
                LocationApprovalScreen(
                    viewModel = approvalViewModel,
                    adminUid = "admin_kelurahan",
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Master Plant Encyclopedia Form
            composable(
                route = Screen.MasterPlantForm.route,
                arguments = listOf(
                    navArgument("plantId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStackEntry ->
                val plantId = backStackEntry.arguments?.getString("plantId")
                val masterPlantViewModel = viewModel {
                    MasterPlantViewModel(
                        RepositoryProvider.getPlantRepository(),
                        RepositoryProvider.getAuditRepository()
                    )
                }
                MasterPlantFormScreen(
                    viewModel = masterPlantViewModel,
                    adminUid = "admin_kelurahan",
                    plantId = plantId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
