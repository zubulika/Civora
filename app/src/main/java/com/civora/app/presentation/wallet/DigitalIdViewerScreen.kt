package com.civora.app.presentation.wallet

import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.civora.app.R
import com.civora.app.core.components.DynamicMuqeemCard
import com.civora.app.core.designsystem.AppLanguage
import com.civora.app.core.designsystem.LanguageState
import com.civora.app.core.model.UserProfile
import com.civora.app.core.util.OfficialQrGenerator
import com.civora.app.data.mock.CivoraMockDataSource
import com.civora.app.data.repository.UserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val CharcoalModalBg = Color(0xFF353535)

@Composable
fun DigitalIdViewerScreen(
    userRepository: UserRepository? = null,
    onBackClick: () -> Unit
) {
    val currentLanguage = LanguageState.currentLanguage

    val userProfileState = userRepository?.userProfile?.collectAsState(initial = CivoraMockDataSource.currentUser)
    val user = userProfileState?.value ?: CivoraMockDataSource.currentUser

    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 2 })
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CharcoalModalBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // 1. Top Navigation Bar (Clean matching screenshots: Left scan/viewfinder, Right close X)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (pagerState.currentPage == 0) {
                    IconButton(onClick = { /* Fullscreen / scan viewfinder action */ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_viewfinder),
                            contentDescription = "Fullscreen",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.size(24.dp))
                }

                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            // 2. Vertical Pager: Page 0 = Rotated ID Card | Page 1 = Rotated QR Code Card
            VerticalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { page ->
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 6.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val availableW = maxWidth
                    val availableH = maxHeight

                    // Both the ID Card and QR Card share identical dimensions and 90° vertical rotation.
                    // When rotated 90° in portrait view:
                    //   visual width on screen = cardH
                    //   visual height on screen = cardW = cardH * 1.586f
                    // Maximize cardH to fill nearly the entire viewport width with a slim margin,
                    // while ensuring visual height (cardW) fits within available vertical space.
                    // The reference viewer uses the card as the hero of the screen.
                    // Keep a small breathing room while allowing the rotated card to
                    // use almost the full device width instead of the previous narrow fit.
                    val maxHFromWidth = availableW * 0.90f
                    val maxHFromHeight = (availableH - 8.dp) / 1.586f
                    val cardH = minOf(maxHFromWidth, maxHFromHeight)
                    val cardW = cardH * 1.586f

                    when (page) {
                        0 -> {
                            // Page 0: Rotated Saudi Resident ID Card
                            Box(
                                modifier = Modifier
                                    .size(width = cardH, height = cardW)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(1, animationSpec = tween(380))
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                DynamicMuqeemCard(
                                    user = user,
                                    language = currentLanguage,
                                    modifier = Modifier
                                        // graphicsLayer rotation does not participate in
                                        // measurement. Bypass the parent's narrow width
                                        // constraint so the rotated card keeps its full size.
                                        .requiredSize(width = cardW, height = cardH)
                                        .graphicsLayer {
                                            rotationZ = 90f
                                        }
                                )
                            }
                        }
                        1 -> {
                            // Page 1: Rotated QR Code Card (Following the card rotation exactly)
                            Box(
                                modifier = Modifier
                                    .size(width = cardH, height = cardW)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(0, animationSpec = tween(380))
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                RotatedQrCard(
                                    user = user,
                                    cardW = cardW,
                                    cardH = cardH,
                                    modifier = Modifier
                                        .requiredSize(width = cardW, height = cardH)
                                        .graphicsLayer {
                                            rotationZ = 90f
                                        }
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Subtle bottom indicator dot (as seen in Screenshot 2)
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 24.dp)
                .size(4.dp)
                .clip(CircleShape)
                .background(if (pagerState.currentPage == 1) Color(0xFF00C853) else Color.Transparent)
        )
    }
}

@Composable
private fun RotatedQrCard(
    user: UserProfile,
    cardW: Dp,
    cardH: Dp,
    modifier: Modifier = Modifier
) {
    var remainingSeconds by remember { mutableIntStateOf(23) }
    var qrTimestamp by remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            if (remainingSeconds > 0) {
                remainingSeconds--
            } else {
                remainingSeconds = 30
                qrTimestamp = System.currentTimeMillis()
            }
        }
    }

    val qrPayload = remember(user.nationalId, user.expiryDateDigits, qrTimestamp) {
        OfficialQrGenerator.buildPayload(user, qrTimestamp)
    }

    val qrBitmap = remember(qrPayload) {
        OfficialQrGenerator.generateBitmap(qrPayload, size = 640)
    }

    val scale = (cardH / 220.dp).coerceIn(0.85f, 1.4f)
    val qrBoxSize = cardH * 0.64f

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // QR Code Container with subtle border
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(0.8.dp, Color(0xFFE5E5EA)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier.size(qrBoxSize)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (qrBitmap != null) {
                        Image(
                            bitmap = qrBitmap.asImageBitmap(),
                            contentDescription = "Resident Identity Verification QR",
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Authentic Center Absher Vector Emblem
                    Box(
                        modifier = Modifier
                            .size(qrBoxSize * 0.22f)
                            .background(Color.White, RoundedCornerShape(3.dp))
                            .padding(2.5.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_absher_qr_emblem),
                            contentDescription = "Absher QR Center Emblem",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp * scale))

            // Timer and Update Notice Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "QR code updates every 30 seconds",
                    color = Color(0xFF8E8E93),
                    fontSize = (11.5f * scale).sp,
                    fontWeight = FontWeight.Normal
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFF2F2F7))
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = String.format("00:%02d", remainingSeconds),
                        color = Color(0xFF384640),
                        fontSize = (11.5f * scale).sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
