package com.civora.app.presentation.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
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
import com.civora.app.core.designsystem.AbsherLightBg
import com.civora.app.core.designsystem.AbsherLightCardBg
import com.civora.app.core.designsystem.AbsherLightCardBorder
import com.civora.app.core.designsystem.AbsherLightHeroBg
import com.civora.app.core.designsystem.AbsherLightTextMuted
import com.civora.app.core.designsystem.AbsherLightTextPrimary
import com.civora.app.core.designsystem.AbsherMint
import com.civora.app.core.designsystem.AbsherMintFAB
import com.civora.app.core.designsystem.AbsherSearchBg
import com.civora.app.core.designsystem.AbsherTextMuted
import com.civora.app.core.designsystem.AppThemeMode
import com.civora.app.core.designsystem.ThemeState

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToServices: () -> Unit,
    onNavigateToServiceDetail: (String) -> Unit,
    onNavigateToWallet: () -> Unit,
    onNavigateToRequests: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    val systemDark = isSystemInDarkTheme()
    val isDark = when (ThemeState.currentThemeMode) {
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
        AppThemeMode.SYSTEM -> systemDark
    }

    val topBg = if (isDark) AbsherDarkSection else AbsherLightHeroBg
    val bodyBg = if (isDark) AbsherDarkSection else AbsherLightBg
    val cardBg = if (isDark) AbsherCardBg else AbsherLightCardBg
    val cardBorder = if (isDark) AbsherCardBorder else AbsherLightCardBorder
    val textPrimary = if (isDark) Color.White else AbsherLightTextPrimary
    val textMuted = if (isDark) AbsherTextMuted else AbsherLightTextMuted
    val quickAccessBg = if (isDark) {
        Brush.verticalGradient(listOf(AbsherGreenSection, AbsherGreenSectionBottom))
    } else {
        Brush.verticalGradient(listOf(AbsherLightBg, AbsherLightBg))
    }
    val quickAccessHeaderColor = if (isDark) Color.White else AbsherLightTextPrimary
    val quickAccessCardBg = if (isDark) AbsherCardBg else AbsherLightCardBg
    val quickAccessCardBorder = if (isDark) AbsherCardBorder else AbsherLightCardBorder
    val quickAccessIconColor = if (isDark) AbsherMint else AbsherGreenHeader
    val quickAccessTextPrimary = if (isDark) Color.White else AbsherLightTextPrimary
    val quickAccessTextMuted = if (isDark) AbsherTextMuted else AbsherLightTextMuted

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bodyBg)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 1. Top Bar with Absher Emblem, Search, Settings, and Bell
            AbsherTopBar(
                isDark = isDark,
                onSearchClick = onNavigateToServices,
                onSettingsClick = onNavigateToSettings,
                onNotificationsClick = onNavigateToNotifications
            )

            // Scrollable Home Screen Body
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 120.dp)
            ) {
                // 2. Profile Card + "My Digital Documents" Section
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(topBg)
                            .padding(bottom = 16.dp)
                    ) {
                        // User Profile Summary Card (Above Digital Documents)
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = cardBg),
                            border = BorderStroke(1.dp, cardBorder),
                            elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 2.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .clickable { onNavigateToProfile() }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.user_avatar),
                                    contentDescription = "User Avatar",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                )

                                Spacer(modifier = Modifier.width(14.dp))

                                Column {
                                    Text(
                                        text = "MD ABDUL HALIM MEIA",
                                        color = textPrimary,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "ID No.: 2495685261",
                                        color = textMuted,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }

                        // My Digital Documents Header
                        Text(
                            text = "My Digital Documents",
                            color = textPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
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

                // 3. Quick Access Section (Adapts to Light / Dark Mode)
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(quickAccessBg)
                            .padding(horizontal = 16.dp, vertical = 16.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Quick Access",
                                color = quickAccessHeaderColor,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            // Card 1: My Vehicles
                            AbsherWideCard(
                                iconRes = R.drawable.ic_car_front_outline,
                                title = "My Vehicles",
                                subtitle = "View details, renew documents,\nreport accidents, and much more",
                                cardBg = quickAccessCardBg,
                                cardBorder = quickAccessCardBorder,
                                iconColor = quickAccessIconColor,
                                textPrimary = quickAccessTextPrimary,
                                textMuted = quickAccessTextMuted,
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
                                    cardBg = quickAccessCardBg,
                                    cardBorder = quickAccessCardBorder,
                                    iconColor = quickAccessIconColor,
                                    textPrimary = quickAccessTextPrimary,
                                    modifier = Modifier.weight(1f),
                                    onClick = onNavigateToServices
                                )
                                AbsherGridCard(
                                    title = "Absher Travel",
                                    iconRes = R.drawable.ic_absher_travel,
                                    cardBg = quickAccessCardBg,
                                    cardBorder = quickAccessCardBorder,
                                    iconColor = quickAccessIconColor,
                                    textPrimary = quickAccessTextPrimary,
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
                                    cardBg = quickAccessCardBg,
                                    cardBorder = quickAccessCardBorder,
                                    iconColor = quickAccessIconColor,
                                    textPrimary = quickAccessTextPrimary,
                                    modifier = Modifier.weight(1f),
                                    onClick = onNavigateToServices
                                )
                                AbsherGridCard(
                                    title = "Update\nResident Pho...",
                                    iconRes = R.drawable.ic_update_photo,
                                    cardBg = quickAccessCardBg,
                                    cardBorder = quickAccessCardBorder,
                                    iconColor = quickAccessIconColor,
                                    textPrimary = quickAccessTextPrimary,
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
                                cardBg = quickAccessCardBg,
                                cardBorder = quickAccessCardBorder,
                                iconColor = quickAccessIconColor,
                                textPrimary = quickAccessTextPrimary,
                                textMuted = quickAccessTextMuted,
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
            containerColor = if (isDark) AbsherMintFAB else AbsherGreenHeader,
            contentColor = if (isDark) Color(0xFF084834) else Color.White,
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
                tint = if (isDark) Color(0xFF084834) else Color.White,
                modifier = Modifier.size(26.dp)
            )
        }
    }
}

@Composable
fun AbsherTopBar(
    isDark: Boolean,
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onNotificationsClick: () -> Unit
) {
    val barBg = if (isDark) AbsherGreenHeader else AbsherLightHeroBg
    val iconTint = if (isDark) Color.White else AbsherGreenHeader

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(barBg)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Absher Emblem & Vector Barcode
            com.civora.app.core.components.AbsherHeaderBranding(
                logoHeight = 42.dp,
                color = iconTint
            )

            // Right: Search, Settings & Notifications Icons
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onSearchClick,
                    modifier = Modifier.size(38.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "Search",
                        tint = iconTint,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(2.dp))
                IconButton(
                    onClick = onSettingsClick,
                    modifier = Modifier.size(38.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "Settings",
                        tint = iconTint,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(2.dp))
                IconButton(
                    onClick = onNotificationsClick,
                    modifier = Modifier.size(38.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notifications",
                        tint = iconTint,
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
    cardBg: Color,
    cardBorder: Color,
    iconColor: Color,
    textPrimary: Color,
    textMuted: Color,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(1.dp, cardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
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
                tint = iconColor,
                modifier = Modifier.size(34.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = title,
                    color = textPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = subtitle,
                    color = textMuted,
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
    cardBg: Color,
    cardBorder: Color,
    iconColor: Color,
    textPrimary: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(1.dp, cardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
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
                    tint = iconColor,
                    modifier = Modifier.size(32.dp)
                )
            } else if (iconVector != null) {
                Icon(
                    imageVector = iconVector,
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier.size(32.dp)
                )
            }

            Text(
                text = title,
                color = textPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 17.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
