package com.civora.app.presentation.other

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.civora.app.core.components.CivoraTopBar

@Composable
fun OtherServicePlaceholderScreen(
    serviceId: String,
    onBackClick: () -> Unit
) {
    val title = otherServiceTitle(serviceId)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CivoraTopBar(
            title = title,
            showBackButton = true,
            onBackClick = onBackClick
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Coming soon",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "$title will be available here soon.",
                modifier = Modifier.padding(top = 8.dp),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun otherServiceTitle(serviceId: String): String = when (serviceId) {
    "appointments" -> "Manage Appointments"
    "delivery" -> "Document Delivery"
    "travel" -> "Absher Travel"
    "auth" -> "Manage Authorizations"
    "furijat" -> "Donate with Furijat"
    "ehsan" -> "Donate with Ehsan"
    "visit_visa" -> "Manage Visit Visa"
    "activation" -> "Absher Activation Sites"
    "qabul" -> "Manage Qabul Requests"
    "birth_cert" -> "Birth Certificates"
    "death_cert" -> "Death Certificates"
    "payments" -> "Government Payments"
    else -> "Service"
}
