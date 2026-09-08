package com.civora.app.presentation.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.civora.app.R
import com.civora.app.core.components.AbsherHeaderBranding
import com.civora.app.core.designsystem.AbsherCardBg
import com.civora.app.core.designsystem.AbsherDarkSection
import com.civora.app.core.designsystem.AbsherGreenHeader
import com.civora.app.core.designsystem.AbsherLightBg
import com.civora.app.core.designsystem.AbsherLightCardBg
import com.civora.app.core.designsystem.AbsherLightCardBorder
import com.civora.app.core.designsystem.AbsherLightHeroBg
import com.civora.app.core.designsystem.AbsherLightTextMuted
import com.civora.app.core.designsystem.AbsherLightTextPrimary
import com.civora.app.core.designsystem.AppThemeMode
import com.civora.app.core.designsystem.LocalThemeMode

import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight

data class PublicServiceCardItem(
    val title: String,
    val iconRes: Int? = null,
    val iconVector: ImageVector? = null
)

@Composable
fun LoginScreen(
    onLoginClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onNotificationsClick: () -> Unit
) {
    val themeMode = LocalThemeMode.current
    val systemDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
        AppThemeMode.SYSTEM -> systemDark
    }

    var isExpanded by remember { mutableStateOf(false) }

    val bgColor = if (isDark) AbsherDarkSection else AbsherLightBg
    val heroBg = if (isDark) Color(0xFF13201A) else AbsherLightHeroBg
    val cardBg = if (isDark) AbsherCardBg else AbsherLightCardBg
    val cardBorder = if (isDark) Color(0xFF2B3830) else AbsherLightCardBorder
    val textPrimary = if (isDark) Color(0xFFE2ECE7) else AbsherLightTextPrimary
    val textMuted = if (isDark) Color(0xFF8F9E97) else AbsherLightTextMuted

    val allServices = remember {
        listOf(
            PublicServiceCardItem("Manage Digital Identity", iconVector = Icons.Outlined.Person),
            PublicServiceCardItem("Absher Travel for Visitors", iconRes = R.drawable.ic_absher_travel),
            PublicServiceCardItem("Civil Affairs Appointments", iconRes = R.drawable.ic_appointment),
            PublicServiceCardItem("Passport Appointments", iconRes = R.drawable.ic_passport),
            PublicServiceCardItem("Authentication Services", iconRes = R.drawable.ic_authenticator),
            PublicServiceCardItem("View Digital Documents", iconRes = R.drawable.ic_qr_viewfinder),
            PublicServiceCardItem("Visitor Document Issuance", iconRes = R.drawable.ic_visitor_doc),
            PublicServiceCardItem("Activation Devices Locator", iconRes = R.drawable.ic_activation_device)
        )
    }

    val visibleServices = if (isExpanded) allServices else allServices.take(4)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .verticalScroll(rememberScrollState())
    ) {
        // 1. Pale Mint Hero Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
                .background(heroBg)
                .statusBarsPadding()
                .padding(bottom = 24.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Top Right Action Buttons (Settings & Notifications)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onSettingsClick,
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "Settings",
                            tint = AbsherGreenHeader,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    IconButton(
                        onClick = onNotificationsClick,
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notifications",
                            tint = AbsherGreenHeader,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                // Center Absher Logo (Raw SVG Barcode + Circular Ministry Emblem)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AbsherHeaderBranding(
                        logoHeight = 56.dp,
                        color = AbsherGreenHeader
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "Absher E-Services 24/7",
                        color = textPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Primary Log In Button
                    Button(
                        onClick = onLoginClick,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AbsherGreenHeader,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(
                            text = "Log In",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 2. Public Services Vertically Stacked
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 36.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Public Services",
                    color = textPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (isExpanded) "Show Less" else "See All",
                    color = AbsherGreenHeader,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { isExpanded = !isExpanded }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Vertically stacked service cards
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                visibleServices.forEach { item ->
                    VerticalPublicServiceCard(
                        item = item,
                        cardBg = cardBg,
                        cardBorder = cardBorder,
                        textPrimary = textPrimary,
                        textMuted = textMuted,
                        onClick = onLoginClick
                    )
                }
            }
        }
    }
}

@Composable
private fun VerticalPublicServiceCard(
    item: PublicServiceCardItem,
    cardBg: Color,
    cardBorder: Color,
    textPrimary: Color,
    textMuted: Color,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(1.dp, cardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (item.iconRes != null) {
                Icon(
                    painter = painterResource(id = item.iconRes),
                    contentDescription = item.title,
                    tint = AbsherGreenHeader,
                    modifier = Modifier.size(28.dp)
                )
            } else if (item.iconVector != null) {
                Icon(
                    imageVector = item.iconVector,
                    contentDescription = item.title,
                    tint = AbsherGreenHeader,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = item.title,
                color = textPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = textMuted,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
