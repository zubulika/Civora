package com.civora.app.presentation.services

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.civora.app.core.components.CivoraTopBar

@Composable
fun ServicePlaceholderScreen(
    serviceId: String,
    onBackClick: () -> Unit
) {
    val title = serviceTitle(serviceId)
    val muted = MaterialTheme.colorScheme.onSurfaceVariant

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
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Layers,
                contentDescription = null,
                tint = muted.copy(alpha = 0.6f),
                modifier = Modifier.size(72.dp)
            )
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = "No services available",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "$title will be available here soon.",
                color = muted,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun serviceTitle(serviceId: String): String = when (serviceId) {
    "travel" -> "Absher Travel"
    "newborn" -> "Register Newborn"
    "driving" -> "Renew Driving License"
    "resident_id" -> "Renew Resident ID"
    "auth" -> "Authentication Services"
    "photo" -> "Update Resident Photo"
    "accident" -> "Report Minor Accident"
    "passport" -> "Update Passport"
    else -> "Service"
}
