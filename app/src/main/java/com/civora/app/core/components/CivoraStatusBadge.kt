package com.civora.app.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.civora.app.core.designsystem.CivoraError
import com.civora.app.core.designsystem.CivoraErrorContainer
import com.civora.app.core.designsystem.CivoraInfo
import com.civora.app.core.designsystem.CivoraInfoContainer
import com.civora.app.core.designsystem.CivoraSuccess
import com.civora.app.core.designsystem.CivoraSuccessContainer
import com.civora.app.core.designsystem.CivoraWarning
import com.civora.app.core.designsystem.CivoraWarningContainer
import com.civora.app.core.model.DocumentStatus
import com.civora.app.core.model.RequestStatus

@Composable
fun CivoraStatusBadge(
    text: String,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(containerColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = contentColor
        )
    }
}

@Composable
fun DocumentStatusBadge(status: DocumentStatus) {
    val (bgColor, textColor, label) = when (status) {
        DocumentStatus.ACTIVE -> Triple(CivoraSuccessContainer, CivoraSuccess, "Active")
        DocumentStatus.EXPIRING_SOON -> Triple(CivoraWarningContainer, CivoraWarning, "Expiring Soon")
        DocumentStatus.EXPIRED -> Triple(CivoraErrorContainer, CivoraError, "Expired")
        DocumentStatus.UNDER_RENEWAL -> Triple(CivoraInfoContainer, CivoraInfo, "Under Renewal")
    }
    CivoraStatusBadge(text = label, containerColor = bgColor, contentColor = textColor)
}

@Composable
fun RequestStatusBadge(status: RequestStatus) {
    val (bgColor, textColor) = when (status) {
        RequestStatus.SUBMITTED -> Pair(CivoraInfoContainer, CivoraInfo)
        RequestStatus.PROCESSING -> Pair(CivoraWarningContainer, CivoraWarning)
        RequestStatus.ACTION_REQUIRED -> Pair(CivoraErrorContainer, CivoraError)
        RequestStatus.READY_FOR_PICKUP -> Pair(CivoraSuccessContainer, CivoraSuccess)
        RequestStatus.COMPLETED -> Pair(CivoraSuccessContainer, CivoraSuccess)
        RequestStatus.REJECTED -> Pair(CivoraErrorContainer, CivoraError)
    }
    CivoraStatusBadge(text = status.label, containerColor = bgColor, contentColor = textColor)
}
