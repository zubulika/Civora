package com.civora.app.navigation

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object Services : Screen("services")
    object ServiceDetail : Screen("services/{serviceId}") {
        fun createRoute(serviceId: String) = "services/$serviceId"
    }
    object Wallet : Screen("wallet")
    object Requests : Screen("requests")
    object Profile : Screen("profile")
    object Notifications : Screen("notifications")
}
