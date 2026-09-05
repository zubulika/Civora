package com.civora.app.presentation.services

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.VerifiedUser
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.civora.app.core.components.CivoraCard
import com.civora.app.core.components.CivoraPrimaryButton
import com.civora.app.core.components.CivoraTopBar
import com.civora.app.core.designsystem.CivoraGreenContainer
import com.civora.app.core.designsystem.CivoraGreenPrimary
import com.civora.app.core.designsystem.CivoraOnGreenContainer
import com.civora.app.core.model.GovernmentService

@Composable
fun ServiceDetailScreen(
    serviceId: String,
    viewModel: ServicesViewModel,
    onBackClick: () -> Unit,
    onNavigateToRequests: () -> Unit
) {
    val services by viewModel.filteredServices.collectAsState()
    val submissionState by viewModel.submissionSuccess.collectAsState()
    val service = viewModel.getServiceById(serviceId)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CivoraTopBar(
            title = service?.title ?: "Service Details",
            subtitle = service?.category?.displayName,
            showBackButton = true,
            onBackClick = onBackClick
        )

        if (service == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = CivoraGreenPrimary)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Service Overview Card
                CivoraCard(cornerRadius = 18.dp) {
                    Text(
                        text = service.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = service.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Key Service Metadata
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetadataTile(
                        title = "Fee",
                        value = service.fee,
                        icon = Icons.Default.Payments,
                        modifier = Modifier.weight(1f)
                    )
                    MetadataTile(
                        title = "Duration",
                        value = service.processingTime,
                        icon = Icons.Default.AccessTime,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Required Documents & Eligibility
                CivoraCard(cornerRadius = 18.dp) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = "Docs",
                            tint = CivoraGreenPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Requirements & Prerequisites",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    service.requiredDocuments.forEach { req ->
                        Row(
                            modifier = Modifier.padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(CivoraGreenPrimary)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = req,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(CivoraGreenContainer)
                            .padding(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = "Verified",
                            tint = CivoraOnGreenContainer,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Verified via Digital Citizen Security Key",
                            style = MaterialTheme.typography.labelSmall,
                            color = CivoraOnGreenContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (submissionState != null) {
                    CivoraCard(
                        containerColor = CivoraGreenContainer,
                        cornerRadius = 16.dp
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Success",
                                tint = CivoraGreenPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Request Successfully Submitted!",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = CivoraOnGreenContainer
                                )
                                Text(
                                    text = "Reference: ${submissionState!!.referenceNumber}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = CivoraOnGreenContainer
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        CivoraPrimaryButton(
                            onClick = {
                                viewModel.clearSubmissionState()
                                onNavigateToRequests()
                            }
                        ) {
                            Text("Track in Requests")
                        }
                    }
                } else {
                    CivoraPrimaryButton(
                        onClick = { viewModel.submitRequest(service) }
                    ) {
                        Text("Submit Electronic Request")
                    }
                }
            }
        }
    }
}

@Composable
fun MetadataTile(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    CivoraCard(
        modifier = modifier,
        cornerRadius = 14.dp
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = CivoraGreenPrimary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
