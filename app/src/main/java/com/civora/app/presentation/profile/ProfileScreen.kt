package com.civora.app.presentation.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.civora.app.R
import com.civora.app.core.components.UserAvatarImage
import com.civora.app.core.designsystem.AbsherCardBg
import com.civora.app.core.designsystem.AbsherCardBorder
import com.civora.app.core.designsystem.AbsherDarkSection
import com.civora.app.core.designsystem.AbsherGreenHeader
import com.civora.app.core.designsystem.AbsherLightBg
import com.civora.app.core.designsystem.AbsherLightCardBg
import com.civora.app.core.designsystem.AbsherLightCardBorder
import com.civora.app.core.designsystem.AbsherLightTextMuted
import com.civora.app.core.designsystem.AbsherLightTextPrimary
import com.civora.app.core.designsystem.AbsherMint
import com.civora.app.core.designsystem.AbsherTextMuted
import com.civora.app.core.designsystem.AppThemeMode
import com.civora.app.core.designsystem.ThemeState

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToPassport: () -> Unit = {},
    onNavigateToResidentId: () -> Unit = {},
    onNavigateToPersonalDetails: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val systemDark = isSystemInDarkTheme()
    val isDark = when (ThemeState.currentThemeMode) {
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
        AppThemeMode.SYSTEM -> systemDark
    }

    val user by viewModel.userProfile.collectAsState()

    val bgColor = if (isDark) AbsherDarkSection else AbsherLightBg
    val cardBg = if (isDark) AbsherCardBg else AbsherLightCardBg
    val cardBorder = if (isDark) AbsherCardBorder else AbsherLightCardBorder
    val textPrimary = if (isDark) Color.White else AbsherLightTextPrimary
    val textMuted = if (isDark) AbsherTextMuted else AbsherLightTextMuted
    val iconColor = if (isDark) AbsherMint else AbsherGreenHeader

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        // Top App Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (isDark) AbsherDarkSection else AbsherLightBg)
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = if (isDark) Color.White else AbsherGreenHeader
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "My Profile",
                    color = if (isDark) Color.White else AbsherLightTextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }

        // Profile Cards Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // User Profile Summary Card with Edit Action
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, cardBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        UserAvatarImage(
                            photoUrl = user.photoUrl,
                            contentDescription = "User Avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(54.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = user.fullNameEn,
                                color = textPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = user.fullNameAr,
                                color = textMuted,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "ID No.: ${user.nationalId}",
                                color = if (isDark) AbsherMint else AbsherGreenHeader,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Official MOI Verified Status Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isDark) Color(0xFF19382B) else Color(0xFFE8F5E9))
                            .border(
                                1.dp,
                                if (isDark) AbsherMint.copy(alpha = 0.5f) else Color(0xFFA5D6A7),
                                RoundedCornerShape(20.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Verified by Ministry of Interior",
                                tint = if (isDark) AbsherMint else AbsherGreenHeader,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Verified",
                                color = if (isDark) AbsherMint else AbsherGreenHeader,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
            // Row 1: 2-Column Grid (My Passport & My Resident ID)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ProfileSquareCard(
                    title = "My Passport",
                    iconRes = R.drawable.ic_passport,
                    cardBg = cardBg,
                    cardBorder = cardBorder,
                    iconColor = iconColor,
                    textColor = textPrimary,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToPassport
                )
                ProfileSquareCard(
                    title = "My Resident\nID",
                    iconRes = R.drawable.ic_visitor_doc,
                    cardBg = cardBg,
                    cardBorder = cardBorder,
                    iconColor = iconColor,
                    textColor = textPrimary,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToResidentId
                )
            }

            // Card 3: My Visa
            ProfileWideActionCard(
                title = "My Visa",
                iconRes = R.drawable.ic_visa,
                cardBg = cardBg,
                cardBorder = cardBorder,
                iconColor = iconColor,
                textColor = textPrimary,
                onClick = {}
            )

            // Card 4: My Driving License
            ProfileWideActionCard(
                title = "My Driving License",
                iconRes = R.drawable.ic_driver_license,
                cardBg = cardBg,
                cardBorder = cardBorder,
                iconColor = iconColor,
                textColor = textPrimary,
                onClick = {}
            )

            // Card 5: My Travel Records (Aerial Beach Banner Card)
            Card(
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(138.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onNavigateToPersonalDetails() }
            ) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // 1. Photographic beach background
                    Image(
                        painter = painterResource(id = R.drawable.travel_banner_bg),
                        contentDescription = "Travel Banner Background",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // 2. Translucent emerald tint overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF005835).copy(alpha = 0.82f),
                                        Color(0xFF00754A).copy(alpha = 0.55f),
                                        Color(0xFF008754).copy(alpha = 0.35f)
                                    )
                                )
                            )
                    )

                    // 3. Card Content overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        // "Inside Kingdom" pill badge on top right
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFFF2F4F3).copy(alpha = 0.92f))
                                .padding(horizontal = 12.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = user.travelStatus,
                                color = Color(0xFF4A5568),
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Normal
                            )
                        }

                        // Content on bottom left
                        Column(
                            modifier = Modifier.align(Alignment.BottomStart)
                        ) {
                            Text(
                                text = "My Travel Records",
                                color = Color.White,
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Find your last trips details",
                                color = Color.White.copy(alpha = 0.90f),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }
                }
            }

            // Card 6: Labor Importations
            ProfileWideActionCard(
                title = "Labor Importations",
                iconRes = R.drawable.ic_labor_import,
                cardBg = cardBg,
                cardBorder = cardBorder,
                iconColor = iconColor,
                textColor = textPrimary,
                onClick = {}
            )

            // Card 7: Log Out Section
            Spacer(modifier = Modifier.height(14.dp))
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDark) Color(0xFF2A1C1C) else Color(0xFFFDF4F4)
                ),
                border = BorderStroke(1.dp, if (isDark) Color(0xFF5A2A2A) else Color(0xFFF0D5D5)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onLogoutClick() }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Log Out",
                        tint = Color(0xFFD32F2F),
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Log Out of Absher",
                        color = Color(0xFFD32F2F),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ProfileSquareCard(
    title: String,
    iconRes: Int,
    cardBg: Color,
    cardBorder: Color,
    iconColor: Color,
    textColor: Color,
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
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = title,
                tint = iconColor,
                modifier = Modifier.size(34.dp)
            )
            Text(
                text = title,
                color = textColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 17.sp
            )
        }
    }
}

@Composable
fun ProfileWideActionCard(
    title: String,
    iconRes: Int,
    cardBg: Color,
    cardBorder: Color,
    iconColor: Color,
    textColor: Color,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(1.dp, cardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = title,
                tint = iconColor,
                modifier = Modifier.size(30.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                color = textColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal
            )
        }
    }
}
