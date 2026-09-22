package com.civora.app.core.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.civora.app.R
import com.civora.app.core.designsystem.AppLanguage
import com.civora.app.core.designsystem.LanguageState
import com.civora.app.core.model.UserProfile
import com.civora.app.core.util.OfficialQrGenerator

private val LicenseLabelColor = Color(0xFF4A5568)
private val LicenseValueColor = Color(0xFF111111)
private val LicenseTitleGreen = Color(0xFF005835)
private val CardArabicFont = FontFamily(Font(R.font.tajawal_regular))

/**
 * Authentic Saudi Driving License Digital Document Card.
 * Uses the official driving license template background (bg_driving_license.webp)
 * with precise proportional alignment for holder photo, verification QR code,
 * header branding, and bilingual driving license credentials.
 */
@Composable
fun DynamicDrivingLicenseCard(
    user: UserProfile,
    modifier: Modifier = Modifier,
    language: AppLanguage = LanguageState.currentLanguage
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFCFBF7)),
        border = BorderStroke(1.dp, Color(0xFFE2DDD0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = modifier.aspectRatio(1.586f) // Standard ID-1 aspect ratio matching resident ID
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val cardWidth = maxWidth
            val cardHeight = maxHeight

            // Proportional scaling multiplier based on reference width (350dp)
            val scale = (cardWidth / 350.dp).coerceIn(0.82f, 2.2f)

            // 1. Template Background (Guilloche Waves, Emblem Watermark, Official Graphics)
            Image(
                painter = painterResource(id = R.drawable.bg_driving_license),
                contentDescription = "Saudi Driving License Background",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize()
            )

            // 2. Header Area: Left Arabic Title ("رخصة سياقة") & Right Emblem + Ministry of Interior
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = cardWidth * 0.045f, vertical = cardHeight * 0.035f)
            ) {
                // Top-Left: "رخصة سياقة"
                Text(
                    text = "رخصة سياقة",
                    color = LicenseTitleGreen,
                    fontFamily = CardArabicFont,
                    fontSize = (13f * scale).sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.align(Alignment.TopStart)
                )

                // Top-Right: Saudi Ministry of Interior Emblem + Header Text
                Row(
                    modifier = Modifier.align(Alignment.TopEnd),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier.padding(end = 4.dp * scale)
                    ) {
                        Text(
                            text = "المملكة العربية السعودية",
                            color = Color(0xFF1E293B),
                            fontFamily = CardArabicFont,
                            fontSize = (7.5f * scale).sp,
                            fontWeight = FontWeight.Bold,
                            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
                        )
                        Text(
                            text = "وزارة الداخلية",
                            color = Color(0xFF475569),
                            fontFamily = FontFamily(Font(R.font.tajawal_regular)),
                            fontSize = (6.8f * scale).sp,
                            fontWeight = FontWeight.Medium,
                            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
                        )
                    }

                    Image(
                        painter = painterResource(id = R.drawable.ic_saudi_ministry_interior),
                        contentDescription = "Saudi Emblem",
                        modifier = Modifier.size(24.dp * scale)
                    )
                }
            }

            // 3. Holder Name Section (Placed immediately below header on the right side)
            Column(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(
                        x = -(cardWidth * 0.045f),
                        y = cardHeight * 0.165f
                    ),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = user.fullNameAr.ifEmpty { "محمد بالا مد حسين أوسين" },
                    color = LicenseValueColor,
                    fontFamily = CardArabicFont,
                    fontSize = (11f * scale).sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
                )
                Text(
                    text = user.fullNameEn.ifEmpty { "MD BALAL HOSSAIN" }.uppercase(),
                    color = Color(0xFF334155),
                    fontFamily = FontFamily.SansSerif,
                    fontSize = (8.5f * scale).sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
                )
            }

            // 4. Left Column: Holder Photo + Verification QR Box
            // Holder Photo
            Box(
                modifier = Modifier
                    .offset(
                        x = cardWidth * 0.052f,
                        y = cardHeight * 0.280f
                    )
                    .size(
                        width = cardWidth * 0.252f,
                        height = cardHeight * 0.440f
                    )
                    .clip(RoundedCornerShape(3.dp * scale))
                    .background(Color(0xFFE2E8F0))
            ) {
                UserAvatarImage(
                    photoUrl = user.photoUrl,
                    contentDescription = "License Holder Photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Verification Box (White Box with QR Code + 4-Line Arabic Verification Disclaimer)
            val qrPayload = remember(user.nationalId, user.expiryDateDigits) {
                OfficialQrGenerator.buildPayload(user)
            }
            val qrBitmap = remember(qrPayload) {
                OfficialQrGenerator.generateBitmap(qrPayload, size = 180)
            }

            Box(
                modifier = Modifier
                    .offset(
                        x = cardWidth * 0.052f,
                        y = cardHeight * 0.742f
                    )
                    .size(
                        width = cardWidth * 0.252f,
                        height = cardHeight * 0.215f
                    )
                    .clip(RoundedCornerShape(3.dp * scale))
                    .background(Color.White)
                    .border(0.5.dp, Color(0xFFE2E8F0), RoundedCornerShape(3.dp * scale))
                    .padding(horizontal = 3.dp * scale, vertical = 2.dp * scale)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // QR Code Image
                    if (qrBitmap != null) {
                        Image(
                            bitmap = qrBitmap.asImageBitmap(),
                            contentDescription = "Driving License Verification QR",
                            modifier = Modifier
                                .size(cardHeight * 0.185f)
                                .clip(RoundedCornerShape(2.dp * scale))
                        )
                    }

                    // 4-Line Arabic Warning Disclaimer
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.weight(1f)
                    ) {
                        val disclaimerStyle = TextStyle(
                            fontFamily = FontFamily(Font(R.font.tajawal_regular)),
                            fontSize = (4.8f * scale).sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF334155),
                            lineHeight = (6.2f * scale).sp,
                            textAlign = TextAlign.Center,
                            platformStyle = PlatformTextStyle(includeFontPadding = false)
                        )
                        Text("يجب التحقق", style = disclaimerStyle)
                        Text("من الرمز السريع", style = disclaimerStyle)
                        Text("قبل اعتماد", style = disclaimerStyle)
                        Text("التعامل مع الهوية", style = disclaimerStyle)
                    }
                }
            }

            // 5. Right Column: 7 Bilingual License Credentials Rows
            val fields = listOf(
                Triple("ID Number:", user.nationalId, ":رقم الهوية" to user.nationalId.toEasternArabicDigits()),
                Triple("License Type:", user.licenseTypeEn, ":نوع الرخصة" to user.licenseTypeAr),
                Triple("Issue Date:", user.licenseIssueDateEn, ":تاريخ الإصدار" to user.licenseIssueDateAr.ifEmpty { user.licenseIssueDateEn.toEasternArabicDigits() }),
                Triple("Date of Birth:", user.dateOfBirth, ":تاريخ الميلاد" to user.dateOfBirthAr.ifEmpty { user.dateOfBirth.toEasternArabicDigits() }),
                Triple("Nationality:", user.nationality, ":الجنسية" to user.nationalityAr),
                Triple("Expiry Date:", user.licenseExpiryDateEn, ":تاريخ الانتهاء" to user.licenseExpiryDateAr.ifEmpty { user.licenseExpiryDateEn.toEasternArabicDigits() }),
                Triple("Blood Type:", user.bloodType, ":فصيلة الدم" to user.bloodType)
            )

            Column(
                modifier = Modifier
                    .offset(
                        x = cardWidth * 0.330f,
                        y = cardHeight * 0.315f
                    )
                    .width(cardWidth * 0.630f)
                    .fillMaxHeight(0.640f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                fields.forEach { (labelEn, valueEn, arPair) ->
                    val (labelAr, valueAr) = arPair
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left sub-column: English label + value
                        Row(
                            modifier = Modifier.weight(0.51f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = labelEn,
                                color = LicenseLabelColor,
                                fontSize = (6.6f * scale).sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = FontFamily.SansSerif,
                                style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
                            )
                            Spacer(modifier = Modifier.width(3.dp * scale))
                            Text(
                                text = valueEn,
                                color = LicenseValueColor,
                                fontSize = (6.8f * scale).sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.SansSerif,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
                            )
                        }

                        // Right sub-column: Arabic label + value (RTL)
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                            Row(
                                modifier = Modifier.weight(0.49f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Text(
                                    text = labelAr.trimStart(':'),
                                    color = LicenseLabelColor,
                                    fontSize = (6.6f * scale).sp,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = FontFamily(Font(R.font.tajawal_regular)),
                                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
                                )
                                Text(
                                    text = ":",
                                    color = LicenseLabelColor,
                                    fontSize = (6.6f * scale).sp,
                                    fontWeight = FontWeight.SemiBold,
                                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
                                )
                                Spacer(modifier = Modifier.width(2.5.dp * scale))
                                Text(
                                    text = valueAr,
                                    color = LicenseValueColor,
                                    fontSize = (7.0f * scale).sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = CardArabicFont,
                                    maxLines = 1,
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
