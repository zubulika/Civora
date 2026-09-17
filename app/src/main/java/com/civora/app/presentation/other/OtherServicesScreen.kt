package com.civora.app.presentation.other

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Layers
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
import com.civora.app.core.designsystem.AbsherLightCardBorder
import com.civora.app.core.designsystem.AbsherLightHeroBg
import com.civora.app.core.designsystem.AppThemeMode
import com.civora.app.core.designsystem.LocalThemeMode

data class OtherServiceItem(
    val id: String,
    val title: String,
    val iconRes: Int,
    val isMultiLayer: Boolean = false
)

@Composable
fun OtherServicesScreen(
    onNavigateToDetail: (String) -> Unit = {},
    onNavigateToSettings: () -> Unit,
    onNavigateToNotifications: () -> Unit
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
    val preferredBg = if (isDark) Color(0xFF16251E) else Color(0xFFE8F3EE)
    val cardBg = if (isDark) AbsherCardBg else Color.White
    val cardBorder = if (isDark) Color(0xFF2B3830) else Color(0xFFE8EFEA)
    val textPrimary = if (isDark) Color(0xFFE2ECE7) else Color(0xFF212825)
    val textMuted = if (isDark) Color(0xFF8F9E97) else Color(0xFF8C9B93)
    val searchBg = if (isDark) Color(0xFF1E2822) else Color(0xFFF0F4F2)
    val iconColor = AbsherGreenHeader

    val preferredItems = listOf(
        OtherServiceItem("appointments", "Manage\nAppointments", R.drawable.ic_appointment),
        OtherServiceItem("delivery", "Document\nDelivery", R.drawable.ic_parcel_box)
    )

    val gridItems = listOf(
        OtherServiceItem("travel", "Absher Travel", R.drawable.ic_absher_travel),
        OtherServiceItem("auth", "Manage\nAuthorizations", R.drawable.ic_manage_auth),
        OtherServiceItem("furijat", "Donate with\nFurijat", R.drawable.ic_furijat),
        OtherServiceItem("ehsan", "Donate with\nEhsan", R.drawable.ic_ehsan),
        OtherServiceItem("visit_visa", "Manage Visit\nVisa", R.drawable.ic_visa),
        OtherServiceItem("activation", "Absher\nActivation Sit...", R.drawable.ic_activation_device),
        OtherServiceItem("qabul", "Manage Qabul\nRequests", R.drawable.ic_qabul),
        OtherServiceItem("birth_cert", "Birth\nCertificates S...", R.drawable.ic_birth_certificates, isMultiLayer = true),
        OtherServiceItem("death_cert", "Death\nCertificates S...", R.drawable.ic_death_certificates, isMultiLayer = true),
        OtherServiceItem("payments", "Government\nPayments", R.drawable.ic_gov_payments)
    )

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
            // 1. Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 12.dp, top = 8.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AbsherHeaderBranding(
                    logoHeight = 40.dp,
                    color = AbsherGreenHeader
                )

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

            // 2. Large "Other Services" Title
            Text(
                text = "Other Services",
                color = textPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 10.dp)
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
                        text = "Search by Service",
                        color = textMuted,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4. Scrollable Content
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 90.dp)
            ) {
                // Preferred Services Section
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(preferredBg)
                            .padding(14.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Preferred Services",
                                color = textPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                preferredItems.forEach { item ->
                                    Card(
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(containerColor = cardBg),
                                        border = BorderStroke(1.dp, cardBorder),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(118.dp)
                                            .clickable { onNavigateToDetail(item.id) }
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(14.dp),
                                            verticalArrangement = Arrangement.SpaceBetween,
                                            horizontalAlignment = Alignment.Start
                                        ) {
                                            Icon(
                                                painter = painterResource(id = item.iconRes),
                                                contentDescription = item.title,
                                                tint = iconColor,
                                                modifier = Modifier.size(30.dp)
                                            )
                                            Text(
                                                text = item.title,
                                                color = textPrimary,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Normal,
                                                lineHeight = 17.sp,
                                                maxLines = 2
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Grid Rows of Other Services (2 per row)
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        for (i in gridItems.indices step 2) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                val item1 = gridItems[i]
                                ServiceCardItem(
                                    item = item1,
                                    cardBg = cardBg,
                                    cardBorder = cardBorder,
                                    iconColor = iconColor,
                                    textPrimary = textPrimary,
                                    modifier = Modifier.weight(1f),
                                    onClick = { onNavigateToDetail(item1.id) }
                                )

                                if (i + 1 < gridItems.size) {
                                    val item2 = gridItems[i + 1]
                                    ServiceCardItem(
                                        item = item2,
                                        cardBg = cardBg,
                                        cardBorder = cardBorder,
                                        iconColor = iconColor,
                                        textPrimary = textPrimary,
                                        modifier = Modifier.weight(1f),
                                        onClick = { onNavigateToDetail(item2.id) }
                                    )
                                } else {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        }

        // 5. Floating Emerald Chat Assistant Button
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

@Composable
private fun ServiceCardItem(
    item: OtherServiceItem,
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
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .height(118.dp)
            .clickable { onClick() }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.Start
            ) {
                Icon(
                    painter = painterResource(id = item.iconRes),
                    contentDescription = item.title,
                    tint = if (item.id in listOf("ehsan", "furijat")) Color.Unspecified else iconColor,
                    modifier = Modifier.size(30.dp)
                )

                Text(
                    text = item.title,
                    color = textPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 17.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (item.isMultiLayer) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFE4ECE7))
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Layers,
                        contentDescription = "Multi-item",
                        tint = Color(0xFF6B7D74),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}
