package com.civora.app.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.civora.app.core.components.CivoraCard
import com.civora.app.core.components.CivoraTopBar
import com.civora.app.core.components.DocumentStatusBadge
import com.civora.app.core.components.RequestStatusBadge
import com.civora.app.core.designsystem.CivoraGold
import com.civora.app.core.designsystem.CivoraGreenDark
import com.civora.app.core.designsystem.CivoraGreenLight
import com.civora.app.core.designsystem.CivoraGreenPrimary
import com.civora.app.core.model.GovernmentService
import com.civora.app.core.model.ServiceCategory

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToServices: () -> Unit,
    onNavigateToServiceDetail: (String) -> Unit,
    onNavigateToWallet: () -> Unit,
    onNavigateToRequests: () -> Unit,
    onNavigateToNotifications: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CivoraTopBar(
            title = "Civora Portal",
            subtitle = "Smart Public & Citizen Services",
            unreadNotificationCount = state.user.unreadNotificationsCount,
            onNotificationsClick = onNavigateToNotifications
        )

        val data = state
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
                // Citizen Header / Identity Banner
                item {
                    CitizenIdentityHeader(
                        fullNameEn = data.user.fullNameEn,
                        fullNameAr = data.user.fullNameAr,
                        nationalId = data.user.nationalId,
                        verificationTier = data.user.verificationLevel.label
                    )
                }

                // Digital Document Card Preview (National ID card)
                item {
                    val primaryDoc = data.primaryDocument
                    if (primaryDoc != null) {
                        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Digital Document Wallet",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = "View All (${data.user.totalDocuments})",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = CivoraGreenPrimary,
                                    modifier = Modifier.clickable { onNavigateToWallet() }
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))

                            CivoraCard(
                                onClick = onNavigateToWallet,
                                containerColor = CivoraGreenDark,
                                cornerRadius = 18.dp
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column {
                                        Text(
                                            text = primaryDoc.title,
                                            style = MaterialTheme.typography.titleSmall,
                                            color = Color.White
                                        )
                                        Text(
                                            text = primaryDoc.subtitle,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = CivoraGold
                                        )
                                    }
                                    DocumentStatusBadge(status = primaryDoc.status)
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    Column {
                                        Text(
                                            text = "ID NUMBER",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.White.copy(alpha = 0.7f)
                                        )
                                        Text(
                                            text = primaryDoc.documentNumber,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = Color.White
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "EXPIRY",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.White.copy(alpha = 0.7f)
                                        )
                                        Text(
                                            text = primaryDoc.expiryDate,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Quick Actions Section
                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Quick Services",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "All Services",
                                style = MaterialTheme.typography.labelLarge,
                                color = CivoraGreenPrimary,
                                modifier = Modifier.clickable { onNavigateToServices() }
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        // Grid of 4 quick services
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            QuickActionTile(
                                title = "Renew ID",
                                category = "Civil Affairs",
                                icon = Icons.Default.Fingerprint,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    val srv = data.quickActions.find { it.id == "srv_renew_id" }
                                    if (srv != null) onNavigateToServiceDetail(srv.id)
                                    else onNavigateToServices()
                                }
                            )
                            QuickActionTile(
                                title = "Driving License",
                                category = "Traffic",
                                icon = Icons.Default.DirectionsCar,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    val srv = data.quickActions.find { it.id == "srv_renew_license" }
                                    if (srv != null) onNavigateToServiceDetail(srv.id)
                                    else onNavigateToServices()
                                }
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            QuickActionTile(
                                title = "E-Passport",
                                category = "Travel",
                                icon = Icons.Default.FlightTakeoff,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    val srv = data.quickActions.find { it.id == "srv_issue_passport" }
                                    if (srv != null) onNavigateToServiceDetail(srv.id)
                                    else onNavigateToServices()
                                }
                            )
                            QuickActionTile(
                                title = "Violations",
                                category = "Traffic",
                                icon = Icons.Default.Warning,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    val srv = data.quickActions.find { it.id == "srv_traffic_violations" }
                                    if (srv != null) onNavigateToServiceDetail(srv.id)
                                    else onNavigateToServices()
                                }
                            )
                        }
                    }
                }

                // Active Requests Section
                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Active Service Requests",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "View All (${data.user.activeRequestsCount})",
                                style = MaterialTheme.typography.labelLarge,
                                color = CivoraGreenPrimary,
                                modifier = Modifier.clickable { onNavigateToRequests() }
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        data.activeRequests.forEach { request ->
                            CivoraCard(
                                onClick = onNavigateToRequests,
                                modifier = Modifier.padding(vertical = 4.dp),
                                cornerRadius = 14.dp
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = request.serviceTitle,
                                            style = MaterialTheme.typography.titleSmall,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "Ref: ${request.referenceNumber} • Expected: ${request.expectedCompletion}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    RequestStatusBadge(status = request.status)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

@Composable
fun CitizenIdentityHeader(
    fullNameEn: String,
    fullNameAr: String,
    nationalId: String,
    verificationTier: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        CivoraGreenDark,
                        CivoraGreenPrimary
                    )
                )
            )
            .padding(horizontal = 16.dp, vertical = 18.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f))
                    .border(2.dp, CivoraGold, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = "Citizen ID",
                    tint = CivoraGold,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = fullNameEn,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Verified",
                        tint = CivoraGold,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(
                    text = fullNameAr,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.85f)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "National ID: $nationalId • $verificationTier",
                    style = MaterialTheme.typography.labelSmall,
                    color = CivoraGold
                )
            }
        }
    }
}

@Composable
fun QuickActionTile(
    title: String,
    category: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    CivoraCard(
        modifier = modifier,
        cornerRadius = 14.dp,
        onClick = onClick
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(CivoraGreenPrimary.copy(alpha = 0.1f))
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = CivoraGreenPrimary,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = category,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
