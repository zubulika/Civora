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

private val LicenseTextColor = Color(0xFF111111)
private val CardArabicFont = FontFamily(Font(R.font.tajawal_regular))

private data class DrivingLicenseField(
    val labelEn: String,
    val valueEn: String,
    val labelAr: String,
    val valueAr: String
)

/**
 * Authentic Saudi Driving License Digital Document Card.
 * Uses the official driving license template background (bg_driving_license.webp).
 *
 * NOTE: The background template already contains the official header branding:
 * - "رخصة سياقة" (Top-Left)
 * - "المملكة العربية السعودية / وزارة الداخلية" & MOI Coat of Arms (Top-Right)
 * - Photo frame outline and guilloche security patterns.
 * Therefore, dynamic content ONLY renders holder-specific fields:
 * 1. Holder Photo (aligned inside the template's green frame)
 * 2. Verification QR Box with authentic Absher center emblem and 4-line Arabic disclaimer
 * 3. Bilingual Holder Name (Arabic & English, right-aligned below MOI branding)
 * 4. 7 Bilingual Driving License Credential Rows
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
        modifier = modifier.aspectRatio(1.586f) // Standard ID-1 aspect ratio
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val cardWidth = maxWidth
            val cardHeight = maxHeight

            // Proportional scaling multiplier based on reference width (350dp)
            val scale = (cardWidth / 350.dp).coerceIn(0.82f, 2.2f)

            // 1. Template Background (Guilloche Waves, Official Headers, Watermarks)
            Image(
                painter = painterResource(id = R.drawable.bg_driving_license),
                contentDescription = "Saudi Driving License Background",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize()
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
                        y = cardHeight * 0.732f
                    )
                    .size(
                        width = cardWidth * 0.236f,
                        height = cardHeight * 0.144f
                    )
                    .padding(horizontal = 2.dp * scale, vertical = 1.dp * scale),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // QR Code with centered Absher emblem
                    Box(
                        modifier = Modifier.size(cardHeight * 0.138f),
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

                    // 4-Line Arabic Verification Warning Disclaimer
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 1.dp * scale)
                    ) {
                        val disclaimerStyle = TextStyle(
                            fontFamily = CardArabicFont,
                            fontSize = (4.8f * scale).sp,
                            fontWeight = FontWeight.Bold,
                            color = LicenseTextColor,
                            lineHeight = (6.0f * scale).sp,
                            textAlign = TextAlign.End,
                            platformStyle = PlatformTextStyle(includeFontPadding = false)
                        )
                        Text("يجب التحقق", style = disclaimerStyle)
                        Text("من الرمز السريع", style = disclaimerStyle)
                        Text("قبل اعتماد", style = disclaimerStyle)
                        Text("التعامل مع الهوية", style = disclaimerStyle)
                    }
                }
            }

            // 4. Holder Name Section (Right-aligned, immediately below MOI header)
            Column(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(
                        x = -(cardWidth * 0.055f),
                        y = cardHeight * 0.280f
                    ),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = user.fullNameAr.ifEmpty { "محمد بالا مد حسين أوسين" },
                    color = LicenseTextColor,
                    fontFamily = CardArabicFont,
                    fontSize = (12f * scale).sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
                )
                Spacer(modifier = Modifier.height(2.5.dp * scale))
                Text(
                    text = user.fullNameEn.ifEmpty { "MD BALAL HOSSAIN" }.uppercase(),
                    color = LicenseTextColor,
                    fontFamily = FontFamily.SansSerif,
                    fontSize = (8.8f * scale).sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
                )
            }

            // 5. 7 Bilingual License Credentials Rows
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
                        x = cardWidth * 0.338f,
                        y = cardHeight * 0.445f
                    )
                    .width(cardWidth * 0.607f)
                    .height(cardHeight * 0.520f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                fields.forEach { field ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left sub-column: English label + value
                        Row(
                            modifier = Modifier.weight(0.50f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Text(
                                text = field.labelEn,
                                color = LicenseTextColor,
                                fontSize = (6.8f * scale).sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.SansSerif,
                                style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
                            )
                            Spacer(modifier = Modifier.width(3.dp * scale))
                            Text(
                                text = field.valueEn,
                                color = LicenseTextColor,
                                fontSize = (7.0f * scale).sp,
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
                                modifier = Modifier.weight(0.50f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Text(
                                    text = field.labelAr,
                                    color = LicenseTextColor,
                                    fontSize = (6.8f * scale).sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = CardArabicFont,
                                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
                                )
                                Spacer(modifier = Modifier.width(3.dp * scale))
                                Text(
                                    text = field.valueAr,
                                    color = LicenseTextColor,
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
