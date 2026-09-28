package com.civora.app.core.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.civora.app.R
import com.civora.app.core.designsystem.AppLanguage
import com.civora.app.core.designsystem.LanguageState
import com.civora.app.core.model.UserProfile
import com.civora.app.core.util.OfficialQrGenerator

private val CardLabelTextColor = Color.White
private val CardLabelStrokeColor = Color(0xFF111111)
private val CardValueColor = Color(0xFF1E1E1E)
private val CardNameArColor = Color(0xFF222222)
private val CardNameEnColor = Color(0xFF222222)
private val CardDisclaimerColor = Color(0xFF2B2B2B)
private val CardTextFont = FontFamily.SansSerif
private val CardArabicLabelFont = FontFamily(Font(R.font.tajawal_regular))
private val DisclaimerArabicFont = FontFamily(
    Font(R.font.noto_kufi_arabic_900, FontWeight.Black),
    Font(R.font.noto_kufi_arabic_800, FontWeight.ExtraBold),
    Font(R.font.noto_kufi_arabic_700, FontWeight.Bold)
)

private data class DrivingLicenseField(
    val labelEn: String,
    val valueEn: String,
    val labelAr: String,
    val valueAr: String
)

/**
 * 4-Line Arabic Official Security Disclaimer rendered with authentic Noto Kufi Arabic Black typography,
 * identical to the primary digital document (DynamicMuqeemCard).
 */
@Composable
private fun ArabicDisclaimerLine(
    text: String,
    fontSize: TextUnit,
    lineHeight: TextUnit
) {
    Text(
        text = text,
        fontFamily = DisclaimerArabicFont,
        fontSize = fontSize,
        lineHeight = lineHeight,
        color = CardDisclaimerColor,
        fontWeight = FontWeight.Black,
        textAlign = TextAlign.End,
        maxLines = 1,
        softWrap = false,
        style = TextStyle(
            platformStyle = PlatformTextStyle(includeFontPadding = false)
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

private val OutlineOffsets = listOf(
    Offset(-1f, -1f),
    Offset(0f, -1f),
    Offset(1f, -1f),
    Offset(-1f, 0f),
    Offset(1f, 0f),
    Offset(-1f, 1f),
    Offset(0f, 1f),
    Offset(1f, 1f)
)

/**
 * Placeholder permanent text rendered with an authentic white core fill and crisp black stroke/shadow,
 * exactly mimicking the pre-printed labels on official Saudi physical/digital driving license cards.
 *
 * Uses a multi-pass composite renderer:
 * 1. Soft dark feathered drop-shadow
 * 2. 8-direction cardinal & diagonal solid black offsets
 * 3. Round-join vector stroke outline in solid black
 * 4. Crisp pure white core glyph fill on top
 */
@Composable
private fun FeatheredStrokeLabel(
    text: String,
    modifier: Modifier = Modifier,
    fillColor: Color = CardLabelTextColor,
    strokeColor: Color = CardLabelStrokeColor,
    scale: Float = 1.0f,
    fontSize: TextUnit,
    fontWeight: FontWeight = FontWeight.Bold,
    fontFamily: FontFamily? = null,
    maxLines: Int = 1,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    textAlign: TextAlign? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    lineHeight: TextUnit = TextUnit.Unspecified
) {
    val density = LocalDensity.current
    val strokeWidthPx = with(density) { (2.0.dp * scale).toPx() }
    val shadowBlurPx = with(density) { (3.0.dp * scale).toPx() }
    val offsetDp = 0.85.dp * scale

    Box(modifier = modifier) {
        // 1. Soft feathered dark shadow halo
        Text(
            text = text,
            color = Color.Transparent,
            fontSize = fontSize,
            fontWeight = fontWeight,
            fontFamily = fontFamily,
            maxLines = maxLines,
            overflow = overflow,
            textAlign = textAlign,
            letterSpacing = letterSpacing,
            lineHeight = lineHeight,
            style = TextStyle(
                platformStyle = PlatformTextStyle(includeFontPadding = false),
                shadow = Shadow(
                    color = strokeColor.copy(alpha = 0.50f),
                    offset = Offset(0.5f * scale, 0.5f * scale),
                    blurRadius = shadowBlurPx
                )
            )
        )
        // 2. 8-direction cardinal & diagonal solid black outline offsets
        for (dir in OutlineOffsets) {
            Text(
                text = text,
                color = strokeColor,
                fontSize = fontSize,
                fontWeight = fontWeight,
                fontFamily = fontFamily,
                maxLines = maxLines,
                overflow = overflow,
                textAlign = textAlign,
                letterSpacing = letterSpacing,
                lineHeight = lineHeight,
                modifier = Modifier.offset(x = offsetDp * dir.x, y = offsetDp * dir.y),
                style = TextStyle(
                    platformStyle = PlatformTextStyle(includeFontPadding = false)
                )
            )
        }
        // 3. Solid round vector stroke outline in black
        Text(
            text = text,
            color = strokeColor,
            fontSize = fontSize,
            fontWeight = fontWeight,
            fontFamily = fontFamily,
            maxLines = maxLines,
            overflow = overflow,
            textAlign = textAlign,
            letterSpacing = letterSpacing,
            lineHeight = lineHeight,
            style = TextStyle(
                platformStyle = PlatformTextStyle(includeFontPadding = false),
                drawStyle = Stroke(
                    width = strokeWidthPx,
                    join = StrokeJoin.Round
                )
            )
        )
        // 4. Crisp pure white core glyph fill on top
        Text(
            text = text,
            color = fillColor,
            fontSize = fontSize,
            fontWeight = fontWeight,
            fontFamily = fontFamily,
            maxLines = maxLines,
            overflow = overflow,
            textAlign = textAlign,
            letterSpacing = letterSpacing,
            lineHeight = lineHeight,
            style = TextStyle(
                platformStyle = PlatformTextStyle(includeFontPadding = false),
                drawStyle = Fill
            )
        )
    }
}

/**
 * Authentic Saudi Driving License Digital Document Card.
 * Uses the official driving license template background (bg_driving_license.webp).
 *
 * All holder credentials (names, photo, verification QR, and 7 bilingual fields)
 * are rendered with authentic bold sizing, symmetric typography, and pre-printed feathered stroke labels.
 */
@Composable
fun DynamicDrivingLicenseCard(
    user: UserProfile,
    modifier: Modifier = Modifier,
    language: AppLanguage = LanguageState.currentLanguage
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.5.dp, Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = modifier
            .aspectRatio(1.586f)
            .clip(RoundedCornerShape(16.dp))
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
        ) {
            val cardWidth = maxWidth
            val cardHeight = maxHeight

            // Proportional scaling multiplier based on reference width (360dp) with dynamic responsive adaptation
            val scale = (cardWidth / 360.dp).coerceIn(0.5f, 2.0f)

            // 1. Template Background: Scaled slightly and clipped to eliminate any scan outline and outer artifacts
            Image(
                painter = painterResource(id = R.drawable.bg_driving_license),
                contentDescription = "Saudi Driving License Background",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = 1.05f
                        scaleY = 1.05f
                    }
            )

            // 2. Holder Photo (Positioned precisely within the template's photo frame cutout)
            Box(
                modifier = Modifier
                    .offset(
                        x = cardWidth * 0.065f,
                        y = cardHeight * 0.259f
                    )
                    .size(
                        width = cardWidth * 0.242f,
                        height = cardHeight * 0.446f
                    )
                    .clip(RoundedCornerShape(6.dp * scale))
                    .background(Color(0xFFE2E8F0))
            ) {
                UserAvatarImage(
                    photoUrl = user.photoUrl,
                    contentDescription = "License Holder Photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // 3. Verification Box: QR Code with centered Absher emblem + 4-line Arabic disclaimer
            val qrPayload = remember(user.nationalId, user.expiryDateDigits) {
                OfficialQrGenerator.buildPayload(user)
            }
            val qrBitmap = remember(qrPayload) {
                OfficialQrGenerator.generateBitmap(qrPayload, size = 180)
            }

            Box(
                modifier = Modifier
                    .offset(
                        x = cardWidth * 0.065f,
                        y = cardHeight * 0.725f
                    )
                    .size(
                        width = cardWidth * 0.242f,
                        height = cardHeight * 0.155f
                    )
                    .padding(horizontal = 1.dp * scale, vertical = 0.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // QR Code with centered Absher emblem
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .aspectRatio(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        if (qrBitmap != null) {
                            Image(
                                bitmap = qrBitmap.asImageBitmap(),
                                contentDescription = "Driving License Verification QR",
                                modifier = Modifier.fillMaxSize()
                            )
                            // Authentic Centered Absher Emblem over QR Code
                            Box(
                                modifier = Modifier
                                    .size(cardHeight * 0.044f)
                                    .background(Color.White, RoundedCornerShape(1.dp * scale))
                                    .padding(0.8.dp * scale),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_absher_qr_emblem),
                                    contentDescription = "Absher QR Emblem",
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }

                    // 4-Line Arabic Official Security Disclaimer (Identical font & weight to primary document)
                    Column(
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(end = 1.dp * scale, top = 0.5.dp * scale, bottom = 0.5.dp * scale)
                    ) {
                        val discSize = (4.8f * scale).sp
                        val discLineHeight = (5.6f * scale).sp
                        ArabicDisclaimerLine("يجب التحقق", discSize, discLineHeight)
                        ArabicDisclaimerLine("من الرمز السريع", discSize, discLineHeight)
                        ArabicDisclaimerLine("قبل اعتماد", discSize, discLineHeight)
                        ArabicDisclaimerLine("التعامل مع الهوية", discSize, discLineHeight)
                    }
                }
            }

            // 4. Holder Name Section (Prominent size, crisp bold typography matching reference image)
            Column(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(
                        x = -(cardWidth * 0.050f),
                        y = cardHeight * 0.258f
                    ),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = user.fullNameAr.ifEmpty { "محمد بالا مد حسين أوسين" },
                    fontFamily = CardTextFont,
                    fontSize = (16.5f * scale).sp,
                    lineHeight = (18.5f * scale).sp,
                    color = CardNameArColor,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
                )
                Spacer(modifier = Modifier.height(2.dp * scale))
                Text(
                    text = user.fullNameEn.ifEmpty { "MD BALAL HOSSAIN" }.uppercase(),
                    fontFamily = CardTextFont,
                    fontSize = (11.5f * scale).sp,
                    lineHeight = (13.5f * scale).sp,
                    color = CardNameEnColor,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = (0.35f * scale).sp,
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
                )
            }

            // 5. 7 Bilingual License Credentials Rows
            // Placeholder permanent labels use feathered stroke with primary document fonts (Tajawal for Arabic, SansSerif for English)
            // Values are rendered with crisp solid bold dark ink
            val fields = listOf(
                DrivingLicenseField(
                    labelEn = "ID Number:",
                    valueEn = user.nationalId.ifEmpty { "2631173305" },
                    labelAr = "رقم الهوية:",
                    valueAr = user.nationalId.ifEmpty { "2631173305" }.toEasternArabicDigits()
                ),
                DrivingLicenseField(
                    labelEn = "License Type:",
                    valueEn = user.licenseTypeEn.ifEmpty { "Private" },
                    labelAr = "نوع الرخصة:",
                    valueAr = user.licenseTypeAr.ifEmpty { "خصوصي" }
                ),
                DrivingLicenseField(
                    labelEn = "Issue Date:",
                    valueEn = user.licenseIssueDateEn.ifEmpty { "10/03/2026" },
                    labelAr = "تاريخ الإصدار:",
                    valueAr = user.licenseIssueDateAr.ifEmpty { user.licenseIssueDateEn.ifEmpty { "10/03/2026" }.toEasternArabicDigits() }
                ),
                DrivingLicenseField(
                    labelEn = "Date of Birth:",
                    valueEn = user.dateOfBirth.ifEmpty { "10/01/1984" },
                    labelAr = "تاريخ الميلاد:",
                    valueAr = user.dateOfBirthAr.ifEmpty { user.dateOfBirth.ifEmpty { "10/01/1984" }.toEasternArabicDigits() }
                ),
                DrivingLicenseField(
                    labelEn = "Nationality:",
                    valueEn = user.nationality.ifEmpty { "Bangladesh" },
                    labelAr = "الجنسية:",
                    valueAr = user.nationalityAr.ifEmpty { "بنجلاديش" }
                ),
                DrivingLicenseField(
                    labelEn = "Expiry Date:",
                    valueEn = user.licenseExpiryDateEn.ifEmpty { "21/11/2035" },
                    labelAr = "تاريخ الانتهاء:",
                    valueAr = user.licenseExpiryDateAr.ifEmpty { user.licenseExpiryDateEn.ifEmpty { "21/11/2035" }.toEasternArabicDigits() }
                ),
                DrivingLicenseField(
                    labelEn = "Blood Type:",
                    valueEn = user.bloodType.ifEmpty { "A+" },
                    labelAr = "فصيلة الدم:",
                    valueAr = user.bloodType.ifEmpty { "A+" }
                )
            )

            Column(
                modifier = Modifier
                    .offset(
                        x = cardWidth * 0.332f,
                        y = cardHeight * 0.430f
                    )
                    .width(cardWidth * 0.620f)
                    .height(cardHeight * 0.505f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                fields.forEach { field ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left sub-column: English label (Feathered stroke placeholder) + value (Crisp solid bold)
                        Row(
                            modifier = Modifier.weight(0.50f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            FeatheredStrokeLabel(
                                text = field.labelEn,
                                fontSize = (8.4f * scale).sp,
                                scale = scale,
                                fontFamily = CardTextFont,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(3.dp * scale))
                            Text(
                                text = field.valueEn,
                                color = CardValueColor,
                                fontSize = (9.2f * scale).sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = CardTextFont,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis,
                                style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
                            )
                        }

                        // Right sub-column: Arabic label (Feathered stroke placeholder with Tajawal) + value (Crisp solid bold)
                        Box(
                            modifier = Modifier.weight(0.50f),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Start
                                ) {
                                    FeatheredStrokeLabel(
                                        text = field.labelAr,
                                        fontSize = (8.8f * scale).sp,
                                        scale = scale,
                                        fontFamily = CardArabicLabelFont,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Spacer(modifier = Modifier.width(3.dp * scale))
                                    Text(
                                        text = field.valueAr,
                                        color = CardValueColor,
                                        fontSize = (9.6f * scale).sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = CardTextFont,
                                        maxLines = 1,
                                        softWrap = false,
                                        overflow = TextOverflow.Ellipsis,
                                        style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
