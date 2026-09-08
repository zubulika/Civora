package com.civora.app.presentation.profile

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.civora.app.R
import com.civora.app.core.designsystem.AbsherCardBg
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
import com.civora.app.core.designsystem.LocalThemeMode

@Composable
fun PassportDetailScreen(
    onBackClick: () -> Unit
) {
    val themeMode = LocalThemeMode.current
    val systemDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
        AppThemeMode.SYSTEM -> systemDark
    }

    val context = LocalContext.current
    var isExpanded by remember { mutableStateOf(true) }

    val bgColor = if (isDark) AbsherDarkSection else Color(0xFFFBFDFC)
    val cardBg = if (isDark) AbsherCardBg else Color.White
    val cardBorder = if (isDark) Color(0xFF2B3830) else Color(0xFFE8EFEA)
    val textPrimary = if (isDark) Color(0xFFE2ECE7) else Color(0xFF212825)
    val textMuted = if (isDark) Color(0xFF8F9E97) else Color(0xFF8C9B93)
    val iconColor = if (isDark) AbsherMint else AbsherGreenHeader

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        // Top Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (isDark) AbsherDarkSection else Color(0xFFFBFDFC))
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
                    text = "My Passport",
                    color = if (isDark) Color.White else AbsherLightTextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Deposit Info Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, cardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 14.dp)
                ) {
                    Text(
                        text = "Amount deposit",
                        color = textMuted,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "SAR 0.00",
                        color = textMuted.copy(alpha = 0.8f),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // 2. Normal Passport Details Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, cardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Expandable Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isExpanded = !isExpanded }
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_passport),
                                contentDescription = "Passport",
                                tint = iconColor,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Text(
                                text = "Normal Passport",
                                color = textPrimary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Icon(
                            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = "Expand",
                            tint = textMuted,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    if (isExpanded) {
                        HorizontalDivider(
                            color = cardBorder,
                            thickness = 1.dp,
                            modifier = Modifier.padding(horizontal = 18.dp)
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Passport Number
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = "Passport Number", color = textMuted, fontSize = 12.sp)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(text = "EM0962248", color = textMuted.copy(alpha = 0.85f), fontSize = 15.sp)
                                }
                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("Passport Number", "EM0962248"))
                                        Toast.makeText(context, "Copied EM0962248", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.ContentCopy,
                                        contentDescription = "Copy",
                                        tint = AbsherGreenHeader,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            // Type
                            Column {
                                Text(text = "Type", color = textMuted, fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(text = "Normal", color = textMuted.copy(alpha = 0.85f), fontSize = 15.sp)
                            }

                            // Issuing Date
                            Column {
                                Text(text = "Issuing Date", color = textMuted, fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(text = "07/01/2025", color = textMuted.copy(alpha = 0.85f), fontSize = 15.sp)
                            }

                            // Expiry Date
                            Column {
                                Text(text = "Expiry Date", color = textMuted, fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(text = "06/01/2030", color = textMuted.copy(alpha = 0.85f), fontSize = 15.sp)
                            }

                            // Issuing City
                            Column {
                                Text(text = "Issuing City", color = textMuted, fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(text = "دكا", color = textMuted.copy(alpha = 0.85f), fontSize = 15.sp)
                            }

                            // Status
                            Column {
                                Text(text = "Status", color = textMuted, fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(text = "-", color = textMuted.copy(alpha = 0.85f), fontSize = 15.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
