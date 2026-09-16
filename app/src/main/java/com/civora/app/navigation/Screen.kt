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
    object Settings : Screen("settings")
    object Login : Screen("login")
    object LoginForm : Screen("login_form")
    object Otp : Screen("otp")
    object Loading : Screen("loading")
    object Family : Screen("family")
    object Workers : Screen("workers")
    object Other : Screen("other")
    object PassportDetail : Screen("passport_detail")
    object ResidentIdDetail : Screen("resident_id_detail")
    object DigitalIdViewer : Screen("digital_id_viewer")
}
