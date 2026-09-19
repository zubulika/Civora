package com.civora.app.presentation.workers

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.civora.app.R
import com.civora.app.core.designsystem.AbsherDarkSection
import com.civora.app.core.designsystem.AbsherGreenHeader
import com.civora.app.core.designsystem.AbsherLightBg
import com.civora.app.core.designsystem.AbsherLightTextMuted
import com.civora.app.core.designsystem.AbsherLightTextPrimary
import com.civora.app.core.designsystem.AbsherMintFAB
import com.civora.app.core.designsystem.AbsherSearchBg
import com.civora.app.core.designsystem.AbsherTextMuted
import com.civora.app.core.designsystem.AppThemeMode
import com.civora.app.core.designsystem.ThemeState

@Composable
fun WorkersScreen(
    onSettingsClick: () -> Unit,
    onNotificationsClick: () -> Unit
) {
    val systemDark = isSystemInDarkTheme()
    val isDark = when (ThemeState.currentThemeMode) {
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
        AppThemeMode.SYSTEM -> systemDark
    }

    val bgColor = if (isDark) AbsherDarkSection else AbsherLightBg
    val textPrimary = if (isDark) Color.White else AbsherLightTextPrimary
    val textMuted = if (isDark) AbsherTextMuted else AbsherLightTextMuted
    val searchBg = if (isDark) AbsherSearchBg else Color(0xFFEFF3F1)
    val headerIconTint = if (isDark) Color.White else AbsherGreenHeader

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Top Bar with Emblem, Settings, and Bell
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                com.civora.app.core.components.AbsherHeaderBranding(
                    logoHeight = 42.dp,
                    color = headerIconTint
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onSettingsClick,
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "Settings",
                            tint = headerIconTint,
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
                            tint = headerIconTint,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // Screen Title: "Workers"
            Text(
                text = "Workers",
                color = textPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )

            // Search Bar: "Search by name, ID"
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(searchBg)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = textMuted,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Search by name, ID",
                        color = textMuted,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Empty State Graphic
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_workers_solid),
                    contentDescription = null,
                    tint = textMuted.copy(alpha = 0.6f),
                    modifier = Modifier.size(72.dp)
                )

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "No Workers",
                    color = textPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Once you have workers under\nyour sponsorship they will\ndisplay here.",
                    color = textMuted,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.weight(1.4f))
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = {},
            containerColor = if (isDark) AbsherMintFAB else AbsherGreenHeader,
            contentColor = if (isDark) Color(0xFF084834) else Color.White,
            shape = CircleShape,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp)
                .size(50.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_assistant_chat),
                contentDescription = "Assistant",
                tint = if (isDark) Color(0xFF084834) else Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
