package com.civora.app.presentation.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.civora.app.R
import com.civora.app.core.designsystem.AbsherCardBg
import com.civora.app.core.designsystem.AbsherCardBorder
import com.civora.app.core.designsystem.AbsherDarkSection
import com.civora.app.core.designsystem.AbsherGreenHeader
import com.civora.app.core.designsystem.AbsherGreenSection
import com.civora.app.core.designsystem.AbsherGreenSectionBottom
import com.civora.app.core.designsystem.AbsherMint
import com.civora.app.core.designsystem.AbsherMintFAB
import com.civora.app.core.designsystem.AbsherSearchBg
import com.civora.app.core.designsystem.AbsherTextMuted

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AbsherDarkSection)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 1. Top Bar with Absher Emblem, Settings, and Bell
            AbsherTopBar(
                onSettingsClick = { onNavigateToRequests() },
                onNotificationsClick = onNavigateToNotifications
            )

            // Scrollable Home Screen Body
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 120.dp)
            ) {
                // 2. Search Bar + "My Digital Documents" Section
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(AbsherDarkSection)
                            .padding(bottom = 16.dp)
                    ) {
                        // Search Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(AbsherSearchBg)
                                .clickable { onNavigateToServices() }
                                .padding(horizontal = 14.dp, vertical = 12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = AbsherMint,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Look for a Service",
                                    color = AbsherTextMuted,
                                    fontSize = 14.sp
                                )
                            }
                        }

                        // My Digital Documents Header
                        Text(
                            text = "My Digital Documents",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )

                        // Digital Document Card Preview (Saudi Muqeem Resident ID Card)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 2.dp)
                                .clickable { onNavigateToWallet() }
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.muqeem_card),
                                contentDescription = "Muqeem Resident Digital ID",
                                contentScale = ContentScale.FillWidth,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                // 3. Quick Access Section (Deep Emerald Green Container)
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        AbsherGreenSection,
                                        AbsherGreenSectionBottom
                                    )
                                )
                            )
                            .padding(horizontal = 16.dp, vertical = 16.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Quick Access",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            // Card 1: My Vehicles
                            AbsherWideCard(
                                iconRes = R.drawable.ic_car_front_outline,
                                title = "My Vehicles",
                                subtitle = "View details, renew documents,\nreport accidents, and much more",
                                onClick = onNavigateToServices
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // 2x2 Grid of Quick Access services
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                AbsherGridCard(
                                    title = "Authentication\nServices",
                                    iconVector = Icons.Default.Fingerprint,
                                    modifier = Modifier.weight(1f),
                                    onClick = onNavigateToServices
                                )
                                AbsherGridCard(
                                    title = "Absher Travel",
                                    iconRes = R.drawable.ic_absher_travel,
                                    modifier = Modifier.weight(1f),
                                    onClick = onNavigateToServices
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                AbsherGridCard(
                                    title = "Report Minor\nAccident",
                                    iconRes = R.drawable.ic_minor_accident,
                                    modifier = Modifier.weight(1f),
                                    onClick = onNavigateToServices
                                )
                                AbsherGridCard(
                                    title = "Update\nResident Pho...",
                                    iconRes = R.drawable.ic_update_photo,
                                    modifier = Modifier.weight(1f),
                                    onClick = onNavigateToServices
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Card 6: My Weapons
                            AbsherWideCard(
                                iconRes = R.drawable.ic_weapon,
                                title = "My Weapons",
                                subtitle = "View weapons details, issue and\nview carry permits",
                                onClick = onNavigateToServices
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }

        // 4. Floating Assistant Action Button (Sparkles Chat Bubble)
        FloatingActionButton(
            onClick = onNavigateToServices,
            containerColor = AbsherMintFAB,
            contentColor = Color(0xFF084834),
            shape = CircleShape,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 12.dp)
                .size(50.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_assistant_chat),
                contentDescription = "Assistant",
                tint = Color(0xFF084834),
                modifier = Modifier.size(26.dp)
            )
        }
    }
}

@Composable
fun AbsherTopBar(
    onSettingsClick: () -> Unit,
    onNotificationsClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(AbsherGreenHeader)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Absher Emblem
            Image(
                painter = painterResource(id = R.drawable.absher_logo_transparent),
                contentDescription = "Absher Logo",
                modifier = Modifier.height(46.dp)
            )

            // Right: Settings & Notifications Icons
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onSettingsClick,
                    modifier = Modifier.size(38.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "Settings",
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(
                    onClick = onNotificationsClick,
                    modifier = Modifier.size(38.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notifications",
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AbsherWideCard(
    iconRes: Int,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = AbsherCardBg),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = title,
                tint = AbsherMint,
                modifier = Modifier.size(34.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = subtitle,
                    color = AbsherTextMuted,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
fun AbsherGridCard(
    title: String,
    iconRes: Int? = null,
    iconVector: androidx.compose.ui.graphics.vector.ImageVector? = null,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = AbsherCardBg),
        modifier = modifier
            .height(115.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            if (iconRes != null) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = title,
                    tint = AbsherMint,
                    modifier = Modifier.size(32.dp)
                )
            } else if (iconVector != null) {
                Icon(
                    imageVector = iconVector,
                    contentDescription = title,
                    tint = AbsherMint,
                    modifier = Modifier.size(32.dp)
                )
            }

            Text(
                text = title,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 17.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
