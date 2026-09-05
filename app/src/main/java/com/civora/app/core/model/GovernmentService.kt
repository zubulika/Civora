package com.civora.app.core.model

enum class ServiceCategory(val displayName: String, val description: String) {
    CIVIL_AFFAIRS("Civil Affairs", "National ID, Family registry, Birth & Marital records"),
    TRAFFIC_VEHICLES("Traffic & Vehicles", "Driving licenses, vehicle titles, violations & permits"),
    PASSPORTS_TRAVEL("Passports & Travel", "E-passports, travel clearances, visa authorizations"),
    PERMITS_SECURITY("Permits & Security", "Work clearances, entry permits & security attestations")
}

data class GovernmentService(
    val id: String,
    val title: String,
    val description: String,
    val category: ServiceCategory,
    val processingTime: String,
    val fee: String,
    val isPopular: Boolean = false,
    val isQuickAction: Boolean = false,
    val requiredDocuments: List<String> = emptyList()
)
