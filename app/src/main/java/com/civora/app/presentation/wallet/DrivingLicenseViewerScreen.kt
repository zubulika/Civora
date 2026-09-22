package com.civora.app.presentation.wallet

import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.civora.app.R
import com.civora.app.core.components.DynamicDrivingLicenseCard
import com.civora.app.core.designsystem.LanguageState
import com.civora.app.data.mock.CivoraMockDataSource
import com.civora.app.data.repository.UserRepository
import kotlinx.coroutines.launch

private val CharcoalModalBg = Color(0xFF353535)

@Composable
fun DrivingLicenseViewerScreen(
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
            // 1. Top Navigation Bar: Left Scan/Viewfinder, Right Close (X)
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

            // 2. Vertical Pager: Page 0 = Rotated Driving License Card | Page 1 = Rotated QR Code Card
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

                    // Both the License Card and QR Card share identical dimensions and 90° vertical rotation.
                    // Scale matches the digital resident ID document viewer exactly.
                    val maxHFromWidth = availableW * 0.90f
                    val maxHFromHeight = (availableH - 8.dp) / 1.586f
                    val cardH = minOf(maxHFromWidth, maxHFromHeight)
                    val cardW = cardH * 1.586f

                    when (page) {
                        0 -> {
                            // Page 0: Rotated Saudi Driving License Card
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
                                DynamicDrivingLicenseCard(
                                    user = user,
                                    language = currentLanguage,
                                    modifier = Modifier
                                        .requiredSize(width = cardW, height = cardH)
                                        .graphicsLayer {
                                            rotationZ = 90f
                                        }
                                )
                            }
                        }
                        1 -> {
                            // Page 1: Rotated QR Code Card (Following card rotation exactly)
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

        // 3. Bottom indicator dot
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
