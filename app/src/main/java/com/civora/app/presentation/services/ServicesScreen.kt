package com.civora.app.presentation.services

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.civora.app.R
import com.civora.app.core.designsystem.AbsherCardBg
import com.civora.app.core.designsystem.AbsherDarkSection
import com.civora.app.core.designsystem.AbsherGreenHeader
import com.civora.app.core.designsystem.AbsherLightBg
import com.civora.app.core.designsystem.AbsherLightCardBorder
import com.civora.app.core.designsystem.AppThemeMode
import com.civora.app.core.designsystem.LocalThemeMode

data class AbsherServiceGridItem(
    val id: String,
    val title: String,
    val iconRes: Int
)

@Composable
fun ServicesScreen(
    viewModel: ServicesViewModel,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToSettings: () -> Unit = {}
) {
    val themeMode = LocalThemeMode.current
    val systemDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
        AppThemeMode.SYSTEM -> systemDark
    }

    var searchQuery by remember { mutableStateOf("") }

    val bgColor = if (isDark) AbsherDarkSection else Color(0xFFFBFDFC)
    val cardBg = if (isDark) AbsherCardBg else Color.White
    val cardBorder = if (isDark) Color(0xFF2B3830) else Color(0xFFE8EFEA)
    val textPrimary = if (isDark) Color(0xFFE2ECE7) else Color(0xFF212825)
    val textMuted = if (isDark) Color(0xFF8F9E97) else Color(0xFF8C9B93)
    val searchBg = if (isDark) Color(0xFF1E2822) else Color(0xFFF0F4F2)
    val iconColor = AbsherGreenHeader

    val allServices = remember {
        listOf(
            AbsherServiceGridItem("travel", "Absher Travel", R.drawable.ic_absher_travel),
            AbsherServiceGridItem("newborn", "Register\nNewborn", R.drawable.ic_register_newborn),
            AbsherServiceGridItem("driving", "Renew Driving\nLicense", R.drawable.ic_driver_license),
            AbsherServiceGridItem("resident_id", "Renew\nResident ID", R.drawable.ic_driver_license),
            AbsherServiceGridItem("auth", "Authentication\nServices", R.drawable.ic_authenticator),
            AbsherServiceGridItem("photo", "Update\nResident Pho...", R.drawable.ic_update_photo),
            AbsherServiceGridItem("accident", "Report Minor\nAccident", R.drawable.ic_minor_accident),
            AbsherServiceGridItem("passport", "Update\nPassport ...", R.drawable.ic_update_passport)
        )
    }

    val displayedServices = if (searchQuery.isBlank()) {
        allServices
    } else {
        allServices.filter { it.title.replace("\n", " ").contains(searchQuery, ignoreCase = true) }
    }

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
            // 1. Top Bar with Absher Emblem + Settings & Notifications
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 12.dp, top = 8.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Absher Emblem & Vector Barcode
                com.civora.app.core.components.AbsherHeaderBranding(
                    logoHeight = 38.dp,
                    color = AbsherGreenHeader
                )

                // Action Icons (Settings & Notifications)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onNavigateToSettings,
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
                        onClick = onNavigateToNotifications,
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
            }

            // 2. Large "My Services" Title
            Text(
                text = "My Services",
                color = textPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 12.dp)
            )

            // 3. Search Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(searchBg)
                    .clickable { }
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "Search",
                        tint = textMuted,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Search by Service...",
                        color = textMuted,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. 2-Column Grid of Service Cards
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 18.dp, end = 18.dp, bottom = 80.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(displayedServices, key = { it.id }) { service ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        border = BorderStroke(1.dp, cardBorder),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 1.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1.08f)
                            .clickable { onNavigateToDetail(service.id) }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.SpaceBetween,
                            horizontalAlignment = Alignment.Start
                        ) {
                            // Icon
                            Icon(
                                painter = painterResource(id = service.iconRes),
                                contentDescription = service.title,
                                tint = if (service.id == "passport") Color.Unspecified else iconColor,
                                modifier = Modifier.size(32.dp)
                            )

                            // Title
                            Text(
                                text = service.title,
                                color = textPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                lineHeight = 18.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        // 5. Floating Emerald Assistant Chatbot Button
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 18.dp, bottom = 18.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .shadow(elevation = 6.dp, shape = CircleShape)
                    .clip(CircleShape)
                    .background(AbsherGreenHeader)
                    .clickable { },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_assistant_chat),
                    contentDescription = "Absher AI Chat",
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}
