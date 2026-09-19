package com.civora.app.presentation.profile

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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
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
import com.civora.app.core.designsystem.AbsherLightHeroBg
import com.civora.app.core.designsystem.AbsherLightTextMuted
import com.civora.app.core.designsystem.AbsherLightTextPrimary
import com.civora.app.core.designsystem.AbsherMint
import com.civora.app.core.designsystem.AbsherTextMuted
import com.civora.app.core.designsystem.AppThemeMode
import com.civora.app.core.designsystem.ThemeState
import com.civora.app.core.model.UserProfile

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
    val pageBg = if (isDark) AbsherDarkSection else AbsherLightBg
    val heroBrush = if (isDark) {
        Brush.verticalGradient(listOf(AbsherGreenHeader, Color(0xFF005A38)))
    } else {
        Brush.verticalGradient(listOf(AbsherLightHeroBg, AbsherLightHeroBg))
    }
    val cardBg = if (isDark) AbsherCardBg else AbsherLightCardBg
    val cardBorder = if (isDark) AbsherCardBorder else AbsherLightCardBorder
    val primary = if (isDark) Color.White else AbsherLightTextPrimary
    val muted = if (isDark) AbsherTextMuted else AbsherLightTextMuted
    val accent = if (isDark) AbsherMint else AbsherGreenHeader

    Column(modifier = Modifier.fillMaxSize().background(pageBg)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(375.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
                    .background(heroBrush)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = if (isDark) Color.White else AbsherGreenHeader
                    )
                }
                Text(
                    text = "My Profile",
                    color = if (isDark) Color.White else AbsherLightTextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Normal
                )
            }

            ProfileIdentityCard(
                user = user,
                isDark = isDark,
                cardBg = cardBg,
                cardBorder = cardBorder,
                primary = primary,
                muted = muted,
                accent = accent,
                onDetailsClick = onNavigateToPersonalDetails,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 18.dp)
                    .height(240.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ProfileSquareCard(
                    title = "My Passport",
                    iconRes = R.drawable.ic_passport,
                    cardBg = cardBg,
                    cardBorder = cardBorder,
                    iconColor = accent,
                    textColor = primary,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToPassport
                )
                ProfileSquareCard(
                    title = "My Resident\nID",
                    iconRes = R.drawable.ic_visitor_doc,
                    cardBg = cardBg,
                    cardBorder = cardBorder,
                    iconColor = accent,
                    textColor = primary,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToResidentId
                )
            }

            ProfileWideActionCard("My Visa", R.drawable.ic_visa, cardBg, cardBorder, accent, primary)
            ProfileWideActionCard("My Driving License", R.drawable.ic_driver_license, cardBg, cardBorder, accent, primary)

            Card(
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onNavigateToPersonalDetails
                    )
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.travel_records_bg),
                        contentDescription = "Travel records",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xFF005835).copy(alpha = 0.86f),
                                        Color(0xFF00754A).copy(alpha = 0.55f),
                                        Color(0xFF008754).copy(alpha = 0.30f)
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Text("My Travel Records", color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                        Text("Find your last trips details", color = Color.White.copy(alpha = 0.9f), fontSize = 13.sp)
                    }
                    Text(
                        text = user.travelStatus,
                        color = Color(0xFF4A5568),
                        fontSize = 11.5.sp,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.White.copy(alpha = 0.92f))
                            .padding(horizontal = 12.dp, vertical = 5.dp)
                    )
                }
            }

            ProfileWideActionCard("Labor Importations", R.drawable.ic_labor_import, cardBg, cardBorder, accent, primary)

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF2A1C1C) else Color(0xFFFDF4F4)),
                border = BorderStroke(1.dp, if (isDark) Color(0xFF5A2A2A) else Color(0xFFF0D5D5)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onLogoutClick)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.AutoMirrored.Filled.ExitToApp, "Log out", tint = Color(0xFFD32F2F), modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(10.dp))
                    Text("Log Out of Absher", color = Color(0xFFD32F2F), fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ProfileIdentityCard(
    user: UserProfile,
    isDark: Boolean,
    cardBg: Color,
    cardBorder: Color,
    primary: Color,
    muted: Color,
    accent: Color,
    onDetailsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Card(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg),
            border = BorderStroke(1.dp, cardBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(42.dp))
                Text(user.fullNameEn.uppercase(), color = primary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text(user.fullNameAr, color = muted, fontSize = 13.sp)
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("ID No. ${user.nationalId}", color = muted, fontSize = 13.sp)
                    Spacer(Modifier.width(6.dp))
                    Text("▣", color = accent, fontSize = 18.sp)
                }
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable(onClick = onDetailsClick)
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("My Personal Details", color = accent, fontSize = 17.sp, fontWeight = FontWeight.Medium)
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Open personal details",
                        tint = accent,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        UserAvatarImage(
            photoUrl = user.photoUrl,
            contentDescription = "Profile photo",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-40).dp)
                .size(82.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(2.dp, cardBg, RoundedCornerShape(12.dp))
        )
    }
}

@Composable
private fun PersonalDetailsPanel(user: UserProfile, primary: Color, muted: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(muted.copy(alpha = 0.08f))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        ProfileDetailRow("Date of Birth", user.dateOfBirth, primary, muted)
        ProfileDetailRow("Nationality", user.nationality, primary, muted)
        ProfileDetailRow("Place of Birth", user.placeOfBirthEn, primary, muted)
        ProfileDetailRow("Religion", user.religionEn, primary, muted)
        ProfileDetailRow("Profession", user.professionEn, primary, muted)
        ProfileDetailRow("Sponsor", user.sponsorNameEn, primary, muted)
        ProfileDetailRow("Sponsor ID", user.sponsorId, primary, muted)
        ProfileDetailRow("Issue Place", user.issuePlaceEn, primary, muted)
        ProfileDetailRow("Work Place", user.workPlaceAr, primary, muted)
        ProfileDetailRow("Expiry Date", user.expiryDateEn, primary, muted)
    }
}

@Composable
private fun ProfileDetailRow(label: String, value: String, primary: Color, muted: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = muted, fontSize = 12.sp)
        Spacer(Modifier.width(10.dp))
        Text(value, color = primary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun ProfileSquareCard(
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
            .aspectRatio(1f)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(painterResource(iconRes), title, tint = iconColor, modifier = Modifier.size(42.dp))
            Text(title, color = textColor, fontSize = 15.sp, lineHeight = 18.sp)
        }
    }
}

@Composable
private fun ProfileWideActionCard(
    title: String,
    iconRes: Int,
    cardBg: Color,
    cardBorder: Color,
    iconColor: Color,
    textColor: Color
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(1.dp, cardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(painterResource(iconRes), title, tint = iconColor, modifier = Modifier.size(32.dp))
            Spacer(Modifier.width(16.dp))
            Text(title, color = textColor, fontSize = 15.sp)
        }
    }
}
