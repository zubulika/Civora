package com.civora.app.core.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.civora.app.core.designsystem.AbsherMint
import com.civora.app.core.designsystem.AbsherNavBg
import androidx.compose.foundation.isSystemInDarkTheme
import com.civora.app.core.designsystem.AbsherGreenHeader
import com.civora.app.core.designsystem.AbsherLightCardBorder
import com.civora.app.core.designsystem.AbsherLightNavBg
import com.civora.app.core.designsystem.AbsherLightTextMuted
import com.civora.app.core.designsystem.AppThemeMode
import com.civora.app.core.designsystem.ThemeState
import com.civora.app.navigation.Screen
import com.civora.app.R

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val iconRes: Int? = null,
    val iconVector: ImageVector? = null
) {
    // The EPS does not contain a home glyph, so retain the platform icon only for Home.
    object Dashboard : BottomNavItem(Screen.Dashboard.route, "Home", iconVector = Icons.Default.Home)
    // Services uses the filled person glyph from the reference navigation,
    // rather than the thin resident-identity outline.
    object Services : BottomNavItem(Screen.Services.route, "Services", iconVector = Icons.Default.Person)
    object Family : BottomNavItem(Screen.Family.route, "Family", R.drawable.ic_family_solid)
    object Workers : BottomNavItem(Screen.Workers.route, "Workers", R.drawable.ic_workers_solid)
    object Other : BottomNavItem(Screen.Other.route, "Other", R.drawable.ic_other_grid)
}


@Composable
fun CivoraBottomBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        BottomNavItem.Dashboard,
        BottomNavItem.Services,
        BottomNavItem.Family,
        BottomNavItem.Workers,
        BottomNavItem.Other
    )

    val systemDark = isSystemInDarkTheme()
    val isDark = when (ThemeState.currentThemeMode) {
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
        AppThemeMode.SYSTEM -> systemDark
    }

    val navBg = if (isDark) AbsherNavBg else AbsherLightNavBg
    val dividerColor = if (isDark) Color(0xFF262C29) else AbsherLightCardBorder
    val selectedColor = if (isDark) AbsherMint else AbsherGreenHeader
    val unselectedColor = if (isDark) Color(0xFF8A9892) else AbsherLightTextMuted

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(navBg)
            .navigationBarsPadding()
    ) {
        HorizontalDivider(
            color = dividerColor,
            thickness = 1.dp
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp, horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = currentRoute == item.route
                val contentColor = if (isSelected) selectedColor else unselectedColor

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            if (!isSelected) {
                                onNavigate(item.route)
                            }
                        }
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                ) {
                    val clientIconRes = item.iconRes
                    if (clientIconRes != null) {
                        Icon(
                            painter = painterResource(clientIconRes),
                            contentDescription = item.title,
                            tint = contentColor,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Icon(
                            imageVector = item.iconVector ?: Icons.Default.Home,
                            contentDescription = item.title,
                            tint = contentColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.labelSmall,
                        color = contentColor
                    )
                }
            }
        }
    }
}
