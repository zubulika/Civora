package com.civora.app.presentation.wallet

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.HorizontalDivider
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
import com.civora.app.core.designsystem.CivoraGold
import com.civora.app.core.designsystem.CivoraGreenDark
import com.civora.app.core.designsystem.CivoraGreenPrimary
import com.civora.app.core.model.DigitalDocument
import com.civora.app.core.model.DocumentType

@Composable
fun WalletScreen(
    viewModel: WalletViewModel,
    onNavigateToNotifications: () -> Unit,
    onNavigateToDrivingLicense: () -> Unit = {},
    onNavigateToDigitalId: () -> Unit = {}
) {
    val documents by viewModel.documents.collectAsState()
    val selectedId by viewModel.selectedDocumentId.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CivoraTopBar(
            title = "Digital Document Wallet",
            subtitle = "Official Authenticated Documents",
            onNotificationsClick = onNavigateToNotifications
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Text(
                    text = "Active Citizen Credentials",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Legally recognized official digital documents under Royal Decree.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            items(documents, key = { it.id }) { doc ->
                val isExpanded = selectedId == doc.id
                DigitalDocumentCard(
                    document = doc,
                    isExpanded = isExpanded,
                    onToggleExpand = { viewModel.selectDocument(doc.id) },
                    onOpenViewer = {
                        when (doc.type) {
                            DocumentType.DRIVING_LICENSE -> onNavigateToDrivingLicense()
                            DocumentType.NATIONAL_ID -> onNavigateToDigitalId()
                            else -> {}
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun DigitalDocumentCard(
    document: DigitalDocument,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onOpenViewer: () -> Unit = {}
) {
    val cardGradient = when (document.type) {
        DocumentType.NATIONAL_ID -> Brush.linearGradient(
            listOf(CivoraGreenDark, Color(0xFF0C563E))
        )
        DocumentType.DRIVING_LICENSE -> Brush.linearGradient(
            listOf(Color(0xFF1E3A5F), Color(0xFF0F233E))
        )
        DocumentType.VEHICLE_REGISTRATION -> Brush.linearGradient(
            listOf(Color(0xFF334155), Color(0xFF1E293B))
        )
        DocumentType.PASSPORT -> Brush.linearGradient(
            listOf(Color(0xFF422006), Color(0xFF2A1504))
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(cardGradient)
            .border(1.dp, CivoraGold.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            .clickable(onClick = onToggleExpand)
            .padding(18.dp)
    ) {
        Column {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Emblem",
                            tint = CivoraGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = document.title,
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.White
                        )
                        Text(
                            text = document.subtitle,
                            style = MaterialTheme.typography.labelSmall,
                            color = CivoraGold
                        )
                    }
                }
                DocumentStatusBadge(status = document.status)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Document Number & Expiry
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "DOCUMENT NUMBER",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    Text(
                        text = document.documentNumber,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "VALID UNTIL",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    Text(
                        text = document.expiryDate,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                }
            }

            // Expandable details drawer
            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    HorizontalDivider(
                        color = Color.White.copy(alpha = 0.2f),
                        thickness = 1.dp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    document.details.forEach { (key, value) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = key,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                            Text(
                                text = value,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // QR Code simulation box for official verification
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.1f))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.QrCode2,
                                contentDescription = "QR Verify",
                                tint = CivoraGold,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Encrypted Verification Token",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White
                                )
                                Text(
                                    text = "Ready for official scanner inspection",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.Nfc,
                            contentDescription = "NFC",
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    if (document.type == DocumentType.DRIVING_LICENSE || document.type == DocumentType.NATIONAL_ID) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(CivoraGold)
                                .clickable { onOpenViewer() }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (document.type == DocumentType.DRIVING_LICENSE) "View Official Driving License Card" else "View Official Resident ID Card",
                                style = MaterialTheme.typography.labelLarge,
                                color = Color(0xFF1B2C1A),
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Expand / Collapse Action Row
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White.copy(alpha = 0.08f))
                    .border(
                        1.dp,
                        Color.White.copy(alpha = 0.15f),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp)
                ) {
                    Text(
                        text = if (isExpanded) "Hide Full Details" else "View Official Details & QR",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White
                    )
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
