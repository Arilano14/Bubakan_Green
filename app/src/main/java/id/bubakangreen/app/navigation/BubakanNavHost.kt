package id.bubakangreen.app.navigation

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.core.util.Consumer
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import id.bubakangreen.app.core.di.RepositoryProvider
import id.bubakangreen.app.core.util.ParsedQrResult
import id.bubakangreen.app.data.location.AndroidLocationClient
import id.bubakangreen.app.ui.scanner.CodeScannerHandler
import kotlinx.coroutines.launch
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
import id.bubakangreen.app.ui.splash.SplashScreen
import id.bubakangreen.app.ui.theme.BackgroundWarm

/**
 * Main application navigation shell.
 * Root-level Scaffold with stable bottom navigation and single backstack.
 */
@Composable
fun BubakanAppNavHost(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Handle deep links when app is already running (warm start / singleTop)
    DisposableEffect(Unit) {
        val activity = context as? ComponentActivity
        val listener = Consumer<android.content.Intent> { newIntent ->
            navController.handleDeepLink(newIntent)
        }
        activity?.addOnNewIntentListener(listener)
        onDispose {
            activity?.removeOnNewIntentListener(listener)
        }
    }

    val handleScanQr: () -> Unit = {
        CodeScannerHandler.startScan(
            context = context,
            onSuccess = { result ->
                when (result) {
                    is ParsedQrResult.Plant -> {
                        navController.navigate(Screen.PlantDetail.createRoute(result.stableId))
                    }
                    is ParsedQrResult.Location -> {
                        navController.navigate(Screen.LocationDetail.createRoute(result.stableId))
                    }
                    is ParsedQrResult.Invalid -> {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = "QR Code tidak dikenali: ${result.reason}"
                            )
                        }
                    }
                }
            },
            onFailure = { error ->
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = "Gagal memindai: ${error.localizedMessage ?: "Pemindai tidak tersedia"}"
                    )
                }
            }
        )
    }

    val authRepo = remember { RepositoryProvider.getAuthRepository() }
    val userSession by authRepo.currentUserSession.collectAsState(
        initial = id.bubakangreen.app.domain.model.UserSession(
            uid = "",
            email = "",
            displayName = "",
            role = id.bubakangreen.app.domain.model.UserRole.PUBLIC
        )
    )

    val rootRoutes = listOf(
        Screen.Home.route,
        Screen.Locations.route,
        Screen.Catalog.route,
        Screen.About.route,
        Screen.AdminDashboard.route,
        Screen.PicDashboard.route
    )
    val showBottomBar = currentDestination?.route in rootRoutes
    val effectiveRoute = if (currentDestination?.route == Screen.PicDashboard.route) {
        Screen.AdminDashboard.route
    } else {
        currentDestination?.route
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (showBottomBar) {
                id.bubakangreen.app.ui.navigation.AppBottomBar(
                    currentRoute = effectiveRoute,
                    onItemClick = { item ->
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        if (item.screen == Screen.AdminDashboard) {
                            if (authRepo.isUserSignedIn()) {
                                val targetRoute = if (userSession.role == id.bubakangreen.app.domain.model.UserRole.PIC) {
                                    Screen.PicDashboard.route
                                } else {
                                    Screen.AdminDashboard.route
                                }
                                if (currentDestination?.route != targetRoute) {
                                    navController.navigate(targetRoute) {
                                        popUpTo(Screen.Home.route) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            } else {
                                navController.navigate(Screen.Login.route)
                            }
                        } else if (item.screen == Screen.Home) {
                            if (currentDestination?.route != Screen.Home.route) {
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Home.route) {
                                        inclusive = false
                                    }
                                    launchSingleTop = true
                                }
                            }
                        } else {
                            val destination = item.screen.route
                            if (currentDestination?.route != destination) {
                                navController.navigate(destination) {
                                    popUpTo(Screen.Home.route) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    }
                )
            }
        },
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.exclude(WindowInsets.navigationBars),
        containerColor = BackgroundWarm,
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            // SPLASH: Branded Welcome Screen (±6 seconds)
            composable(Screen.Splash.route) {
                SplashScreen(
                    onSplashComplete = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Splash.route) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }

            // TAB 1: Beranda (Home)
            composable(Screen.Home.route) {
                HomeScreen(
                    onLocationClick = { locationId ->
                        navController.navigate(Screen.LocationDetail.createRoute(locationId))
                    },
                    onPlantClick = { plantId ->
                        navController.navigate(Screen.PlantDetail.createRoute(plantId))
                    },
                    onNavigateToLocations = { _ ->
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        navController.navigate(Screen.Locations.route) {
                            popUpTo(Screen.Home.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToCatalog = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        navController.navigate(Screen.Catalog.route) {
                            popUpTo(Screen.Home.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onInfoClick = {
                        navController.navigate(Screen.About.route)
                    },
                    onScanQrClick = handleScanQr
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
                    },
                    onScanQrClick = handleScanQr
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
                            navController.navigate(Screen.Home.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    inclusive = false
                                }
                                launchSingleTop = true
                            }
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
                            navController.navigate(Screen.Home.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    inclusive = false
                                }
                                launchSingleTop = true
                            }
                        }
                    },
                    onInfoClick = {
                        navController.navigate(Screen.About.route)
                    }
                )
            }

            // About Screen / Profile (Public Entry Point to Admin)
            composable(Screen.About.route) {
                AboutScreen(
                    onNavigateBack = {
                        if (!navController.popBackStack()) {
                            navController.navigate(Screen.Home.route)
                        }
                    },
                    canNavigateBack = navController.previousBackStackEntry != null,
                    onAdminClick = {
                        val authRepo = RepositoryProvider.getAuthRepository()
                        if (authRepo.isUserSignedIn()) {
                            navController.navigate(Screen.AdminDashboard.route)
                        } else {
                            navController.navigate(Screen.Login.route)
                        }
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
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToAdminDashboard = {
                        navController.navigate(Screen.AdminDashboard.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
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
                    onNavigateToLocationForm = { locId ->
                        navController.navigate(Screen.LocationForm.createRoute(locId))
                    },
                    onNavigateToPlantForm = { locId ->
                        navController.navigate(Screen.PlantForm.createRoute(locId))
                    },
                    onNavigateToMasterPlantForm = { plantId ->
                        navController.navigate(Screen.MasterPlantForm.createRoute(plantId))
                    },
                    onSignedOut = {
                        navController.navigate(Screen.About.route) {
                            popUpTo(Screen.Home.route)
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
