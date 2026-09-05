package com.civora.app.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
import com.civora.app.presentation.dashboard.DashboardScreen
import com.civora.app.presentation.dashboard.DashboardViewModel
import com.civora.app.presentation.notifications.NotificationsScreen
import com.civora.app.presentation.notifications.NotificationsViewModel
import com.civora.app.presentation.profile.ProfileScreen
import com.civora.app.presentation.profile.ProfileViewModel
import com.civora.app.presentation.requests.RequestsScreen
import com.civora.app.presentation.requests.RequestsViewModel
import com.civora.app.presentation.services.ServiceDetailScreen
import com.civora.app.presentation.services.ServicesScreen
import com.civora.app.presentation.services.ServicesViewModel
import com.civora.app.presentation.wallet.WalletScreen
import com.civora.app.presentation.wallet.WalletViewModel

@Composable
fun CivoraApp(
    container: AppContainer,
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Dashboard.route

    // Show bottom bar on primary top-level tabs only
    val showBottomBar = currentRoute in listOf(
        Screen.Dashboard.route,
        Screen.Services.route,
        Screen.Wallet.route,
        Screen.Requests.route,
        Screen.Profile.route
    )

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (showBottomBar) {
                CivoraBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
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
                navController = navController
            )
        }
    }
}

@Composable
fun CivoraNavHost(
    container: AppContainer,
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route
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
                onNavigateToNotifications = { navController.navigate(Screen.Notifications.route) }
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
                onNavigateToNotifications = { navController.navigate(Screen.Notifications.route) }
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
                onNavigateToNotifications = { navController.navigate(Screen.Notifications.route) }
            )
        }

        // 7. Notifications
        composable(Screen.Notifications.route) {
            val viewModel: NotificationsViewModel = viewModel(
                factory = NotificationsViewModel.provideFactory(container.userRepository)
            )
            NotificationsScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
