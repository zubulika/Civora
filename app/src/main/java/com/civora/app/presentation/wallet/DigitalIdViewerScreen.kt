package com.civora.app.presentation.wallet

import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.civora.app.R
import com.civora.app.core.components.DynamicMuqeemCard
import com.civora.app.core.designsystem.AppLanguage
import com.civora.app.core.designsystem.AppThemeMode
import com.civora.app.core.designsystem.LanguageState
import com.civora.app.core.designsystem.LocalThemeMode
import com.civora.app.core.model.UserProfile
import com.civora.app.core.util.OfficialQrGenerator
import com.civora.app.data.mock.CivoraMockDataSource
import com.civora.app.data.repository.UserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DigitalIdViewerScreen(
    userRepository: UserRepository? = null,
    onBackClick: () -> Unit
) {
    val currentLanguage = LanguageState.currentLanguage
    val isDark = LocalThemeMode.current == AppThemeMode.DARK

    val userProfileState = userRepository?.userProfile?.collectAsState(initial = CivoraMockDataSource.currentUser)
    val user = userProfileState?.value ?: CivoraMockDataSource.currentUser

    val bgColor = if (isDark) Color(0xFF141715) else Color(0xFFF4F6F4)
    val topBarTextColor = if (isDark) Color.White else Color(0xFF1B382B)
    val iconColor = if (isDark) Color.White else Color(0xFF1B382B)

    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 2 })
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .pointerInput(pagerState.currentPage) {
                detectVerticalDragGestures { _, dragAmount ->
                    if (dragAmount < -30 && pagerState.currentPage == 0) {
                        // Swipe UP on Card -> reveal QR code
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(1, animationSpec = tween(350))
                        }
                    } else if (dragAmount > 30 && pagerState.currentPage == 1) {
                        // Swipe DOWN on QR -> return to Card
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(0, animationSpec = tween(350))
                        }
                    }
                }
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // 1. Top Navigation Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = iconColor,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = if (pagerState.currentPage == 0) {
                            if (currentLanguage == AppLanguage.ENGLISH) "Resident Identity" else "هوية مقيم"
                        } else {
                            if (currentLanguage == AppLanguage.ENGLISH) "Quick Verification QR" else "الرمز السريع للتحقق"
                        },
                        color = topBarTextColor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // Instant Language Toggle Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isDark) Color(0xFF233228) else Color(0xFFE2EBE5))
                            .border(
                                BorderStroke(0.8.dp, Color(0xFF00A859).copy(alpha = 0.5f)),
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { LanguageState.toggleLanguage() }
                            .padding(horizontal = 9.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (currentLanguage == AppLanguage.ENGLISH) "العربية" else "EN",
                            color = Color(0xFF008744),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Toggle QR / Card Icon Button
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            val target = if (pagerState.currentPage == 0) 1 else 0
                            pagerState.animateScrollToPage(target, animationSpec = tween(350))
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCode2,
                        contentDescription = "Toggle QR / Card",
                        tint = if (pagerState.currentPage == 1) Color(0xFF00C853) else Color(0xFF008744),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            // 2. Horizontal Pager: Page 0 = Digital ID Card | Page 1 = QR Code View
            // Supports natural horizontal swipe gestures in both directions!
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { page ->
                when (page) {
                    0 -> {
                        // Page 0: Horizontal Digital Resident Card (Responsive & unrotated)
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            DynamicMuqeemCard(
                                user = user,
                                language = currentLanguage,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .widthIn(max = 560.dp)
                                    .aspectRatio(1.58f)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(1, animationSpec = tween(350))
                                        }
                                    }
                            )
                        }
                    }
                    1 -> {
                        // Page 1: Official QR Code View with Absher emblem in center
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 20.dp, vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            QrVerificationCard(user = user)
                        }
                    }
                }
            }

            // 3. Subtle Page Indicator Dots (Card • QR)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(2) { index ->
                    val isSelected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(if (isSelected) 8.dp else 6.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) Color(0xFF008744)
                                else (if (isDark) Color.White.copy(alpha = 0.25f) else Color.Black.copy(alpha = 0.2f))
                            )
                            .clickable {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index, animationSpec = tween(350))
                                }
                            }
                    )
                }
            }
        }
    }
}

@Composable
private fun QrVerificationCard(
    user: UserProfile
) {
    var remainingSeconds by remember { mutableIntStateOf(24) }
    var qrTimestamp by remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            if (remainingSeconds > 0) {
                remainingSeconds--
            } else {
                remainingSeconds = 30
                qrTimestamp = System.currentTimeMillis() // Dynamically refresh cryptographic timestamp
            }
        }
    }

    val qrPayload = remember(user.nationalId, user.expiryDateDigits, qrTimestamp) {
        OfficialQrGenerator.buildPayload(user, qrTimestamp)
    }

    val qrBitmap = remember(qrPayload) {
        OfficialQrGenerator.generateBitmap(qrPayload, size = 640)
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        border = BorderStroke(0.8.dp, Color(0xFFE2EBE5)),
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 400.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // QR Code with Authentic Absher Center Emblem
            Box(
                modifier = Modifier.size(240.dp),
                contentAlignment = Alignment.Center
            ) {
                if (qrBitmap != null) {
                    Image(
                        bitmap = qrBitmap.asImageBitmap(),
                        contentDescription = "Resident Identity Verification QR",
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // White Square Center Logo with authentic Absher green emblem
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .background(Color.White, RoundedCornerShape(4.dp))
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_absher_qr_emblem),
                        contentDescription = "Absher QR Center Emblem",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Bottom Timer Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "QR code updates every 30 seconds",
                    color = Color(0xFF7A8B84),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFEEF2F0))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = String.format("00:%02d", remainingSeconds),
                        color = Color(0xFF384640),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
