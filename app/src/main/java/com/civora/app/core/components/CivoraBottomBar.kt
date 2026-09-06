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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.civora.app.core.designsystem.AbsherMint
import com.civora.app.core.designsystem.AbsherNavBg
import com.civora.app.navigation.Screen

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Dashboard : BottomNavItem(Screen.Dashboard.route, "Home", Icons.Default.Home)
    object Services : BottomNavItem(Screen.Services.route, "Services", Icons.Default.Person)
    object Family : BottomNavItem(Screen.Wallet.route, "Family", Icons.Default.FamilyRestroom)
    object Workers : BottomNavItem(Screen.Requests.route, "Workers", Icons.Default.Groups)
    object Other : BottomNavItem(Screen.Profile.route, "Other", Icons.Default.GridView)
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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(com.civora.app.core.designsystem.AbsherNavBg)
            .navigationBarsPadding()
    ) {
        HorizontalDivider(
            color = Color(0xFF262C29),
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
                val contentColor = if (isSelected) {
                    com.civora.app.core.designsystem.AbsherMint
                } else {
                    Color(0xFF8A9892)
                }

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
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = contentColor,
                        modifier = Modifier.size(24.dp)
                    )
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
