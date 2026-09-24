package com.civora.app.core.components

import android.graphics.Bitmap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
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

private val CardLabelColor = Color(0xFF817F70)
private val CardValueColor = Color(0xFF3E3D3B)
private val CardNameArColor = Color(0xFF343436)
private val CardNameEnColor = Color(0xFF32322A)
private val CardDisclaimerColor = Color(0xFF2B2B2B)
private val CardTextFont = FontFamily.SansSerif
private val CardArabicLabelFont = FontFamily(Font(R.font.tajawal_regular))

@Composable
private fun ArabicDisclaimerLine(
    text: String,
    fontSize: TextUnit,
    lineHeight: TextUnit
) {
    Text(
        text = text,
        fontFamily = CardTextFont,
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

/**
 * Extension helper to convert ASCII digits (0-9) to Eastern Arabic numerals (٠-٩).
 */
fun String.toEasternArabicDigits(): String {
    val western = "0123456789"
    val eastern = "٠١٢٣٤٥٦٧٨٩"
    val sb = StringBuilder()
    for (ch in this) {
        val idx = western.indexOf(ch)
        if (idx != -1) sb.append(eastern[idx]) else sb.append(ch)
    }
    return sb.toString()
}

/**
 * Authentic Saudi Resident ID / Muqeem Digital Document Card.
 * Uses the official Ministry of Interior template background (bg_resident_card.webp)
 * with 1:1 pixel-perfect alignments for the photo cutout, QR code, 1D barcode,
 * and dynamic resident fields matching the reference image.
 */
@Composable
fun DynamicMuqeemCard(
    user: UserProfile,
    modifier: Modifier = Modifier,
    language: AppLanguage = LanguageState.currentLanguage
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFCFBF7)),
        border = BorderStroke(1.dp, Color(0xFFE2DDD0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = modifier.aspectRatio(1.586f) // ISO/IEC 7810 ID-1 standard aspect ratio
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val cardWidth = maxWidth
            val cardHeight = maxHeight

            // Proportional scaling multiplier based on reference width (350dp) with readable mobile floor
            val scale = (cardWidth / 350.dp).coerceIn(0.82f, 2.2f)

            // 1. Authentic Template Background (Guilloche Waves, Watermark, Seals, Calligraphy)
            Image(
                painter = painterResource(id = R.drawable.bg_resident_card),
                contentDescription = "Saudi Resident ID Template Background",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize()
            )

            // 2. Version Indicator (aligned immediately to the left of 'رقم النسخة' in top-left)
            Box(
                modifier = Modifier
                    .offset(
                        x = cardWidth * 0.098f,
                        y = cardHeight * 0.155f
                    )
            ) {
                Text(
                    text = if (language == AppLanguage.ENGLISH) "1" else user.versionNumber.toEasternArabicDigits(),
                    color = CardNameArColor,
                    fontFamily = CardTextFont,
                    fontSize = (15.5f * scale).sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            // 3. Citizen Portrait Photo (Fitted precisely inside template's photo frame cutout)
            Box(
                modifier = Modifier
                    .offset(
                        x = cardWidth * 0.058f,
                        y = cardHeight * 0.282f
                    )
                    .size(
                        width = cardWidth * 0.254f,
                        height = cardHeight * 0.461f
                    )
                    .clip(RoundedCornerShape(2.dp * scale))
                    .background(Color(0xFFE8EEF4))
            ) {
                UserAvatarImage(
                    photoUrl = user.photoUrl,
                    contentDescription = "Cardholder Photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // 4. Lower Verification Box (White Box with QR Code + 4-Line Arabic Security Disclaimer)
            val qrPayload = remember(user.nationalId, user.expiryDateDigits) {
                OfficialQrGenerator.buildPayload(user)
            }
            val qrBitmap = remember(qrPayload) {
                OfficialQrGenerator.generateBitmap(qrPayload, size = 200)
            }

            Box(
                modifier = Modifier
                    .offset(
                        x = cardWidth * 0.046f,
                        y = cardHeight * 0.758f
                    )
                    .size(
                        width = cardWidth * 0.270f,
                        height = cardHeight * 0.165f
                    )
                    .background(Color.White, RoundedCornerShape(3.dp * scale))
                    .border(BorderStroke(0.6.dp, Color(0xFFD0CAC0)), RoundedCornerShape(3.dp * scale))
                    .padding(start = 0.5.dp * scale, end = 2.dp * scale, top = 0.5.dp * scale, bottom = 0.5.dp * scale),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxSize()
                ) {
                    // QR Code touching the left border of the white container (full-height prominent sizing)
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .aspectRatio(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        if (qrBitmap != null) {
                            Image(
                                bitmap = qrBitmap.asImageBitmap(),
                                contentDescription = "Card QR Code",
                                contentScale = ContentScale.FillBounds,
                                modifier = Modifier.fillMaxSize()
                            )
                            // Authentic Centered Absher Emblem over QR Code
                            Box(
                                modifier = Modifier
                                    .size(cardHeight * 0.048f)
                                    .align(Alignment.Center)
                                    .background(Color.White, RoundedCornerShape(1.dp * scale))
                                    .padding(0.6.dp * scale),
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

                    // 4-Line Arabic Official Security Disclaimer (Crisp Black Bold Typography)
                    Column(
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(end = 1.dp * scale)
                    ) {
                        val discFontSize = (6.4f * scale).sp
                        val discLineHeight = (7.5f * scale).sp
                        ArabicDisclaimerLine("يجب التحقق", discFontSize, discLineHeight)
                        ArabicDisclaimerLine("من الرمز السريع", discFontSize, discLineHeight)
                        ArabicDisclaimerLine("قبل اعتماد", discFontSize, discLineHeight)
                        ArabicDisclaimerLine("التعامل مع الهوية", discFontSize, discLineHeight)
                    }
                }
            }

            // 5. 1D Barcode Strip at Bottom-Left (under verification box)
            Box(
                modifier = Modifier
                    .offset(
                        x = cardWidth * 0.057f,
                        y = cardHeight * 0.930f
                    )
                    .size(
                        width = cardWidth * 0.254f,
                        height = cardHeight * 0.058f
                    )
                    .background(Color.White)
                    .padding(vertical = 1.dp * scale)
            ) {
                ResidentBarcode()
            }

            // 6. Dynamic Citizen Data Fields (Aligned across the guilloche security region)
            Box(
                modifier = Modifier
                    .offset(
                        x = cardWidth * 0.315f,
                        y = cardHeight * 0.238f
                    )
                    .size(
                        width = cardWidth * 0.650f,
                        height = cardHeight * 0.722f
                    )
            ) {
                if (language == AppLanguage.ENGLISH) {
                    EnglishDataLayout(user = user, scale = scale)
                } else {
                    ArabicDataLayout(user = user, scale = scale)
                }
            }
        }
    }
}

/**
 * Authentic 1D Barcode Component using Canvas rendering.
 */
@Composable
private fun ResidentBarcode(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val pattern = listOf(
            2, 1, 1, 2, 3, 1, 2, 2, 1, 3, 1, 2, 1, 1, 3, 2,
            1, 2, 2, 1, 1, 3, 2, 1, 3, 1, 1, 2, 2, 2, 1, 1,
            2, 3, 1, 2, 1, 2, 2, 1, 3, 1, 2, 2, 1, 1, 2, 3,
            1, 2, 1, 3, 2, 1, 2, 2, 1, 2, 3, 1, 1, 2, 2, 2
        )
        val totalUnits = pattern.sum()
        val unitWidth = size.width / totalUnits.toFloat()

        var currentX = 0f
        var isBlack = true
        for (w in pattern) {
            val barW = w * unitWidth
            if (isBlack) {
                drawRect(
                    color = Color.Black,
                    topLeft = Offset(currentX, 0f),
                    size = Size(barW, size.height)
                )
            }
            currentX += barW
            isBlack = !isBlack
        }
    }
}

/**
 * Authentic Arabic Muqeem Data Layout:
 * Formatted with official RTL (Right-to-Left) direction, bold Arabic name on top,
 * English uppercase name below, and pixel-aligned data rows matching the reference ID.
 */
@Composable
private fun ArabicDataLayout(user: UserProfile, scale: Float) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            // Names Header (Arabic on top, English below)
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = user.fullNameAr,
                    color = CardNameArColor,
                    fontFamily = CardTextFont,
                    fontSize = (17.8f * scale).sp,
                    lineHeight = (20.0f * scale).sp,
                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Start,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(1.dp * scale))
                Text(
                    text = user.fullNameEn.uppercase(),
                    color = CardNameEnColor,
                    fontFamily = CardTextFont,
                    fontSize = (12.8f * scale).sp,
                    lineHeight = (14.8f * scale).sp,
                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                    fontWeight = FontWeight.Medium,
                    letterSpacing = (0.38f * scale).sp,
                    textAlign = TextAlign.Start,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Row 1: Expiry Date (Left col) | National ID (Right col)
            TwoColumnArabicRow(
                rightLabel = "رقم الهوية:",
                rightValue = user.nationalId.toEasternArabicDigits(),
                leftLabel = "تاريخ الانتهاء:",
                leftValue = user.expiryDateAr.ifEmpty { user.expiryDateEn.toEasternArabicDigits() },
                scale = scale
            )

            // Row 2: Place of Birth (Left col) | Date of Birth (Right col)
            TwoColumnArabicRow(
                rightLabel = "تاريخ الميلاد:",
                rightValue = user.dateOfBirthAr.ifEmpty { user.dateOfBirth.toEasternArabicDigits() },
                leftLabel = "مكان الميلاد:",
                leftValue = user.placeOfBirthAr,
                scale = scale
            )

            // Row 3: Religion (Left col) | Nationality (Right col)
            TwoColumnArabicRow(
                rightLabel = "الجنسية:",
                rightValue = user.nationalityAr,
                leftLabel = "الديانة:",
                leftValue = user.religionAr,
                scale = scale
            )

            // Row 4: Profession / Occupation
            SingleArabicRow(
                label = "المهنة:",
                value = user.professionAr,
                scale = scale
            )

            // Row 5: Employer / Sponsor ID
            SingleArabicRow(
                label = "هوية صاحب العمل:",
                value = user.sponsorId.toEasternArabicDigits(),
                scale = scale
            )

            // Row 6: Place of Issue
            SingleArabicRow(
                label = "مكان الإصدار:",
                value = user.issuePlace,
                scale = scale
            )

            // Row 7: Place of Work
            SingleArabicRow(
                label = "مكان العمل:",
                value = user.workPlaceAr,
                scale = scale
            )

            // Row 8: Employer Name (visible along bottom border)
            if (user.sponsorName.isNotEmpty()) {
                SingleArabicRow(
                    label = "اسم صاحب العمل:",
                    value = user.sponsorName,
                    scale = scale
                )
            }
        }
    }
}

@Composable
private fun TwoColumnArabicRow(
    rightLabel: String,
    rightValue: String,
    leftLabel: String,
    leftValue: String,
    scale: Float,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 0.5.dp * scale),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // In RTL: first child is on the RIGHT (Right Column)
        Row(
            modifier = Modifier.weight(1.05f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = rightLabel,
                color = CardLabelColor,
                fontFamily = CardArabicLabelFont,
                fontSize = (9.8f * scale).sp,
                lineHeight = (11.9f * scale).sp,
                style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                softWrap = false
            )
            Spacer(modifier = Modifier.width(3.dp * scale))
            Text(
                text = rightValue,
                color = CardValueColor,
                fontFamily = CardTextFont,
                fontSize = (11.2f * scale).sp,
                lineHeight = (13.2f * scale).sp,
                style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(4.dp * scale))

        // In RTL: second child is on the LEFT (Left Column)
        Row(
            modifier = Modifier.weight(1.10f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = leftLabel,
                color = CardLabelColor,
                fontFamily = CardArabicLabelFont,
                fontSize = (9.8f * scale).sp,
                lineHeight = (11.9f * scale).sp,
                style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                softWrap = false
            )
            Spacer(modifier = Modifier.width(3.dp * scale))
            Text(
                text = leftValue,
                color = CardValueColor,
                fontFamily = CardTextFont,
                fontSize = (11.2f * scale).sp,
                lineHeight = (13.2f * scale).sp,
                style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun SingleArabicRow(
    label: String,
    value: String,
    scale: Float,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 0.5.dp * scale),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = CardLabelColor,
            fontFamily = CardArabicLabelFont,
            fontSize = (10.0f * scale).sp,
            lineHeight = (12.2f * scale).sp,
            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            softWrap = false
        )
        Spacer(modifier = Modifier.width(3.5.dp * scale))
        Text(
            text = value,
            color = CardValueColor,
            fontFamily = CardTextFont,
            fontSize = (11.4f * scale).sp,
            lineHeight = (13.4f * scale).sp,
            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * English Resident ID Data Grid: Names Header + 2-Column Details.
 */
@Composable
private fun EnglishDataLayout(user: UserProfile, scale: Float) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Names Header
        Column(modifier = Modifier.padding(bottom = 1.dp * scale)) {
            Text(
                text = user.fullNameAr,
                color = CardNameArColor,
                fontFamily = CardTextFont,
                fontSize = (13.5f * scale).sp,
                lineHeight = (15.7f * scale).sp,
                style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = user.fullNameEn.uppercase(),
                color = CardNameEnColor,
                fontFamily = CardTextFont,
                fontSize = (13.5f * scale).sp,
                lineHeight = (16.2f * scale).sp,
                style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                fontWeight = FontWeight.Medium,
                letterSpacing = (0.3f * scale).sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Two-Column Grid
        // Row 1: Iqama Number & Expiry Date
        Row(modifier = Modifier.fillMaxWidth()) {
            EnglishFieldItem(
                label = "Iqama Number:",
                value = user.nationalId,
                scale = scale,
                modifier = Modifier.weight(1.2f)
            )
            EnglishFieldItem(
                label = "Expiry Date:",
                value = user.expiryDateEn,
                scale = scale,
                modifier = Modifier.weight(1f)
            )
        }

        // Row 2: Date of Birth & Place of Birth
        Row(modifier = Modifier.fillMaxWidth()) {
            EnglishFieldItem(
                label = "Date of Birth :",
                value = user.dateOfBirth,
                scale = scale,
                modifier = Modifier.weight(1.2f)
            )
            EnglishFieldItem(
                label = "Place of Birth :",
                value = user.placeOfBirthEn,
                scale = scale,
                modifier = Modifier.weight(1f)
            )
        }

        // Row 3: Nationality & Religion
        Row(modifier = Modifier.fillMaxWidth()) {
            EnglishFieldItem(
                label = "Nationality:",
                value = user.nationality,
                scale = scale,
                modifier = Modifier.weight(1.2f)
            )
            EnglishFieldItem(
                label = "Religion:",
                value = user.religionEn,
                scale = scale,
                modifier = Modifier.weight(1f)
            )
        }

        // Row 4: Occupation
        EnglishFieldItem(
            label = "Occupation :",
            value = user.professionEn,
            scale = scale,
            modifier = Modifier.fillMaxWidth()
        )

        // Row 5: Sponsor ID
        EnglishFieldItem(
            label = "Sponsor ID:",
            value = user.sponsorId,
            scale = scale,
            modifier = Modifier.fillMaxWidth()
        )

        // Row 6: Issuing Place
        EnglishFieldItem(
            label = "Issuing Place:",
            value = user.issuePlaceEn,
            scale = scale,
            modifier = Modifier.fillMaxWidth()
        )

        // Row 7: Place of Work
        EnglishFieldItem(
            label = "Work Place:",
            value = "Riyadh Region",
            scale = scale,
            modifier = Modifier.fillMaxWidth()
        )

        // Row 8: Sponsor Name
        EnglishFieldItem(
            label = "Sponsor Name:",
            value = user.sponsorNameEn.ifEmpty { user.sponsorName },
            scale = scale,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun EnglishFieldItem(
    label: String,
    value: String,
    scale: Float,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(vertical = 0.5.dp * scale),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = CardLabelColor,
            fontFamily = CardTextFont,
            fontSize = (9.5f * scale).sp,
            lineHeight = (11.5f * scale).sp,
            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
            fontWeight = FontWeight.Normal
        )
        Spacer(modifier = Modifier.width(3.5.dp * scale))
        Text(
            text = value,
            color = CardValueColor,
            fontFamily = CardTextFont,
            fontSize = (10.1f * scale).sp,
            lineHeight = (12.1f * scale).sp,
            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
