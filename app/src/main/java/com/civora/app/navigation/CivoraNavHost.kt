package com.civora.app.navigation

import android.Manifest
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.civora.app.core.components.CivoraBottomBar
import com.civora.app.core.di.AppContainer
import com.civora.app.core.update.AppUpdateInfo
import com.civora.app.core.update.UpdateDialog
import com.civora.app.core.update.UpdateManager
import kotlinx.coroutines.launch
import com.civora.app.presentation.auth.AbsherLoadingScreen
import com.civora.app.presentation.auth.AbsherLoginFormScreen
import com.civora.app.presentation.auth.AbsherOtpScreen
import com.civora.app.presentation.auth.AuthViewModel
import com.civora.app.presentation.auth.LoginScreen
import com.civora.app.presentation.dashboard.DashboardScreen
import com.civora.app.presentation.dashboard.DashboardViewModel
import com.civora.app.presentation.family.FamilyScreen
import com.civora.app.presentation.notifications.NotificationsScreen
import com.civora.app.presentation.notifications.NotificationsViewModel
import com.civora.app.presentation.profile.PassportDetailScreen
import com.civora.app.presentation.profile.ProfileScreen
import com.civora.app.presentation.profile.ProfileViewModel
import com.civora.app.presentation.profile.ResidentIdDetailScreen
import com.civora.app.presentation.requests.RequestsScreen
import com.civora.app.presentation.requests.RequestsViewModel
import com.civora.app.presentation.services.ServiceDetailScreen
import com.civora.app.presentation.services.ServicesScreen
import com.civora.app.presentation.services.ServicesViewModel
import com.civora.app.presentation.settings.SettingsScreen
import com.civora.app.presentation.wallet.DigitalIdViewerScreen
import com.civora.app.presentation.wallet.WalletScreen
import com.civora.app.presentation.wallet.WalletViewModel
import com.civora.app.presentation.workers.WorkersScreen

@Composable
fun CivoraApp(
    container: AppContainer,
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var autoUpdateInfo by remember { mutableStateOf<AppUpdateInfo?>(null) }
    var isDownloadingAutoUpdate by remember { mutableStateOf(false) }
    var autoDownloadProgress by remember { mutableFloatStateOf(0f) }
    var autoDownloadStatusText by remember { mutableStateOf("") }

    // 1. Prompt necessary runtime permissions (Notification, Camera) on very first app launch only
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { /* Handled gracefully by Android OS */ }

    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences("civora_app_prefs", Context.MODE_PRIVATE)
        val hasPrompted = prefs.getBoolean("has_prompted_initial_permissions", false)
        if (!hasPrompted) {
            prefs.edit().putBoolean("has_prompted_initial_permissions", true).apply()
            val permissionsToRequest = mutableListOf<String>()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
            }
            permissionsToRequest.add(Manifest.permission.CAMERA)
            permissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }

    // 2. Automatic update check logic (triggered on cold launch and on login)
    val triggerUpdateCheck: () -> Unit = remember(context) {
        {
            coroutineScope.launch {
                try {
                    val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                    val currentVersion = pInfo.versionName ?: "1.0.0"
                    val result = UpdateManager(context).checkForUpdate(currentVersion)
                    if (result.isSuccess) {
                        val info = result.getOrNull()
                        if (info != null && info.isUpdateAvailable) {
                            autoUpdateInfo = info
                        }
                    }
                } catch (_: Exception) {
                    // Silently ignore if offline or rate-limited
                }
            }
        }
    }

    // Check for updates on cold start
    LaunchedEffect(Unit) {
        triggerUpdateCheck()
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Dashboard.route

    // Show bottom bar on primary tabs and detail screens
    val showBottomBar = currentRoute in listOf(
        Screen.Dashboard.route,
        Screen.Services.route,
        Screen.Family.route,
        Screen.Workers.route,
        Screen.Other.route,
        Screen.Profile.route,
        Screen.PassportDetail.route,
        Screen.ResidentIdDetail.route
    )

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (showBottomBar) {
                CivoraBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        if (route == Screen.Dashboard.route) {
                            navController.navigate(Screen.Dashboard.route) {
                                popUpTo(Screen.Dashboard.route) { inclusive = false }
                                launchSingleTop = true
                            }
                        } else {
                            navController.navigate(route) {
                                popUpTo(Screen.Dashboard.route) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            CivoraNavHost(
                container = container,
                navController = navController,
                onUserLoggedIn = { triggerUpdateCheck() }
            )
        }
    }

    if (autoUpdateInfo != null) {
        UpdateDialog(
            updateInfo = autoUpdateInfo!!,
            isDownloading = isDownloadingAutoUpdate,
            downloadProgress = autoDownloadProgress,
            downloadStatusText = autoDownloadStatusText,
            onConfirmUpdate = {
                val info = autoUpdateInfo ?: return@UpdateDialog
                isDownloadingAutoUpdate = true
                autoDownloadProgress = 0f
                autoDownloadStatusText = "Connecting to background download service..."
                val manager = UpdateManager(context)
                manager.startBackgroundDownload(
                    downloadUrl = info.downloadUrl,
                    onProgress = { progress, statusText ->
                        autoDownloadProgress = progress
                        autoDownloadStatusText = statusText
                    },
                    onComplete = {
                        isDownloadingAutoUpdate = false
                        autoUpdateInfo = null
                    },
                    onError = { error ->
                        isDownloadingAutoUpdate = false
                        Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                    }
                )
            },
            onDismiss = {
                if (!isDownloadingAutoUpdate) {
                    autoUpdateInfo = null
                }
            }
        )
    }
}

@Composable
fun CivoraNavHost(
    container: AppContainer,
    navController: NavHostController,
    onUserLoggedIn: () -> Unit = {}
) {
    val isUserLoggedIn = remember { container.authRepository.isUserLoggedIn }
    val startDestination = if (isUserLoggedIn) Screen.Dashboard.route else Screen.Login.route

    LaunchedEffect(isUserLoggedIn) {
        if (isUserLoggedIn) {
            val activeId = container.authRepository.savedUserIdentifier
            if (!activeId.isNullOrBlank()) {
                container.userRepository.loadUserByIdentifier(activeId)
            }
            onUserLoggedIn()
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // 1. Dashboard
        composable(Screen.Dashboard.route) {
            val viewModel: DashboardViewModel = viewModel(
                factory = DashboardViewModel.provideFactory(container.getDashboardDataUseCase)
            )
            DashboardScreen(
                viewModel = viewModel,
                onNavigateToServices = { navController.navigate(Screen.Services.route) },
                onNavigateToServiceDetail = { serviceId ->
                    navController.navigate(Screen.ServiceDetail.createRoute(serviceId))
                },
                onNavigateToWallet = { navController.navigate(Screen.Wallet.route) },
                onNavigateToRequests = { navController.navigate(Screen.Requests.route) },
                onNavigateToNotifications = { navController.navigate(Screen.Notifications.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onNavigateToIdViewer = { navController.navigate(Screen.DigitalIdViewer.route) }
            )
        }

        // 2. Services Directory
        composable(Screen.Services.route) {
            val viewModel: ServicesViewModel = viewModel(
                factory = ServicesViewModel.provideFactory(
                    container.getServicesUseCase,
                    container.submitServiceRequestUseCase
                )
            )
            ServicesScreen(
                viewModel = viewModel,
                onNavigateToDetail = { serviceId ->
                    navController.navigate(Screen.ServiceDetail.createRoute(serviceId))
                },
                onNavigateToNotifications = { navController.navigate(Screen.Notifications.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }

        // 3. Service Detail
        composable(
            route = Screen.ServiceDetail.route,
            arguments = listOf(navArgument("serviceId") { type = NavType.StringType })
        ) { backStackEntry ->
            val serviceId = backStackEntry.arguments?.getString("serviceId") ?: ""
            val viewModel: ServicesViewModel = viewModel(
                factory = ServicesViewModel.provideFactory(
                    container.getServicesUseCase,
                    container.submitServiceRequestUseCase
                )
            )
            ServiceDetailScreen(
                serviceId = serviceId,
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onNavigateToRequests = { navController.navigate(Screen.Requests.route) }
            )
        }

        // 4. Digital Document Wallet
        composable(Screen.Wallet.route) {
            val viewModel: WalletViewModel = viewModel(
                factory = WalletViewModel.provideFactory(container.getUserDocumentsUseCase)
            )
            WalletScreen(
                viewModel = viewModel,
                onNavigateToNotifications = { navController.navigate(Screen.Notifications.route) }
            )
        }

        // 5. Service Requests Tracker
        composable(Screen.Requests.route) {
            val viewModel: RequestsViewModel = viewModel(
                factory = RequestsViewModel.provideFactory(container.requestRepository)
            )
            RequestsScreen(
                viewModel = viewModel,
                onNavigateToNotifications = { navController.navigate(Screen.Notifications.route) }
            )
        }

        // 6. Citizen Profile
        composable(Screen.Profile.route) {
            val viewModel: ProfileViewModel = viewModel(
                factory = ProfileViewModel.provideFactory(container.userRepository)
            )
            ProfileScreen(
                viewModel = viewModel,
                onBackClick = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onNavigateToNotifications = { navController.navigate(Screen.Notifications.route) },
                onNavigateToPassport = { navController.navigate(Screen.PassportDetail.route) },
                onNavigateToResidentId = { navController.navigate(Screen.DigitalIdViewer.route) },
                onNavigateToPersonalDetails = { navController.navigate(Screen.ResidentIdDetail.route) },
                onLogoutClick = {
                    container.authRepository.signOut()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // 7. Family Tab
        composable(Screen.Family.route) {
            FamilyScreen(
                onSettingsClick = { navController.navigate(Screen.Settings.route) },
                onNotificationsClick = { navController.navigate(Screen.Notifications.route) }
            )
        }

        // 8. Workers Tab
        composable(Screen.Workers.route) {
            WorkersScreen(
                onSettingsClick = { navController.navigate(Screen.Settings.route) },
                onNotificationsClick = { navController.navigate(Screen.Notifications.route) }
            )
        }

        // 9. Notifications
        composable(Screen.Notifications.route) {
            val viewModel: NotificationsViewModel = viewModel(
                factory = NotificationsViewModel.provideFactory(container.userRepository)
            )
            NotificationsScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        // 10. Settings & Theme Switcher & Logout
        composable(Screen.Settings.route) {
            SettingsScreen(
                onBackClick = { navController.popBackStack() },
                onLogoutClick = {
                    container.authRepository.signOut()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // 11. Welcome / Guest Login & Public Services Screen
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginClick = {
                    navController.navigate(Screen.LoginForm.route)
                },
                onViewDigitalDocumentsClick = {
                    navController.navigate(Screen.DigitalIdViewer.route)
                },
                onSettingsClick = { navController.navigate(Screen.Settings.route) },
                onNotificationsClick = { navController.navigate(Screen.Notifications.route) }
            )
        }

        // 12. Absher Login Form Screen
        composable(Screen.LoginForm.route) {
            val authViewModel: AuthViewModel = viewModel(
                factory = AuthViewModel.provideFactory(container.authRepository, container.userRepository)
            )
            AbsherLoginFormScreen(
                onBackClick = { navController.popBackStack() },
                onLoginSubmit = {
                    navController.navigate(Screen.Otp.route)
                },
                viewModel = authViewModel
            )
        }

        // 13. Absher Authenticator OTP Screen
        composable(Screen.Otp.route) {
            AbsherOtpScreen(
                onBackClick = { navController.popBackStack() },
                onOtpVerified = {
                    navController.navigate(Screen.Loading.route)
                }
            )
        }

        // 14. Absher Loading Animation Screen
        composable(Screen.Loading.route) {
            AbsherLoadingScreen(
                onLoadingFinished = {
                    val activeId = container.authRepository.savedUserIdentifier
                    if (!activeId.isNullOrBlank()) {
                        container.userRepository.loadUserByIdentifier(activeId)
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
            )
        }

        // 15. Other Services Tab
        composable(Screen.Other.route) {
            com.civora.app.presentation.other.OtherServicesScreen(
                onNavigateToDetail = { serviceId ->
                    navController.navigate(Screen.ServiceDetail.createRoute(serviceId))
                },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToNotifications = { navController.navigate(Screen.Notifications.route) }
            )
        }

        // 16. My Passport Detail
        composable(Screen.PassportDetail.route) {
            PassportDetailScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        // 17. My Resident ID Detail / Personal Details
        composable(Screen.ResidentIdDetail.route) {
            val viewModel: ProfileViewModel = viewModel(
                factory = ProfileViewModel.provideFactory(container.userRepository)
            )
            ResidentIdDetailScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        // 18. Full-Screen Digital ID Card Viewer (Opens vertically from bottom)
        composable(
            route = Screen.DigitalIdViewer.route,
            enterTransition = {
                androidx.compose.animation.slideInVertically(
                    initialOffsetY = { fullHeight -> fullHeight },
                    animationSpec = androidx.compose.animation.core.tween(380, easing = androidx.compose.animation.core.FastOutSlowInEasing)
                ) + androidx.compose.animation.fadeIn(animationSpec = androidx.compose.animation.core.tween(280))
            },
            exitTransition = {
                androidx.compose.animation.slideOutVertically(
                    targetOffsetY = { fullHeight -> fullHeight },
                    animationSpec = androidx.compose.animation.core.tween(300, easing = androidx.compose.animation.core.FastOutLinearInEasing)
                ) + androidx.compose.animation.fadeOut(animationSpec = androidx.compose.animation.core.tween(200))
            },
            popEnterTransition = {
                androidx.compose.animation.fadeIn(animationSpec = androidx.compose.animation.core.tween(250))
            },
            popExitTransition = {
                androidx.compose.animation.slideOutVertically(
                    targetOffsetY = { fullHeight -> fullHeight },
                    animationSpec = androidx.compose.animation.core.tween(300, easing = androidx.compose.animation.core.FastOutLinearInEasing)
                ) + androidx.compose.animation.fadeOut(animationSpec = androidx.compose.animation.core.tween(200))
            }
        ) {
            DigitalIdViewerScreen(
                userRepository = container.userRepository,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
