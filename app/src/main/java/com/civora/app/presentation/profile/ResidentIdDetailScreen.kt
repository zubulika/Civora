package com.civora.app.presentation.profile

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.civora.app.R
import com.civora.app.core.components.UserAvatarImage
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
fun ResidentIdDetailScreen(
    viewModel: ProfileViewModel,
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
    val clipboardManager = LocalClipboardManager.current
    val user by viewModel.userProfile.collectAsState()

    var isEditing by remember { mutableStateOf(false) }

    // Section accordion states
    var isPersonalExpanded by remember { mutableStateOf(true) }
    var isSponsorExpanded by remember { mutableStateOf(false) }
    var isInsuranceExpanded by remember { mutableStateOf(false) }
    var isHajjExpanded by remember { mutableStateOf(false) }

    // Editable state holders initialized from user
    var editName by remember(user) { mutableStateOf<String>(user.fullNameEn) }
    var editNameAr by remember(user) { mutableStateOf<String>(user.fullNameAr) }
    var editBirthCity by remember(user) { mutableStateOf<String>(user.birthCity) }
    var editBirthCountry by remember(user) { mutableStateOf<String>(user.birthCountry) }
    var editDob by remember(user) { mutableStateOf<String>(user.dateOfBirth) }
    var editMaritalStatus by remember(user) { mutableStateOf<String>(user.maritalStatus) }
    var editTransfers by remember(user) { mutableStateOf<String>(user.sponsorshipTransfers) }
    var editReligion by remember(user) { mutableStateOf<String>(user.religionEn) }
    var editWorkPermit by remember(user) { mutableStateOf<String>(user.workPermit) }
    var editBiometrics by remember(user) { mutableStateOf<String>(user.biometricsCollected) }
    var editTravelStatus by remember(user) { mutableStateOf<String>(user.travelStatus) }

    var editSponsorName by remember(user) { mutableStateOf<String>(user.sponsorName.ifBlank { user.sponsorNameEn }) }
    var editSponsorId by remember(user) { mutableStateOf<String>(user.sponsorId) }
    var editEstStatus by remember(user) { mutableStateOf<String>(user.establishmentStatus) }

    var editInsuranceIssuingDate by remember(user) { mutableStateOf<String>(user.insuranceIssuingDate) }
    var editInsuranceExpiry by remember(user) { mutableStateOf<String>(user.insuranceExpiry) }
    var editBloodType by remember(user) { mutableStateOf<String>(user.bloodType) }
    var editInsuranceCompany by remember(user) { mutableStateOf<String>(user.insuranceCompany) }
    var editPolicyNo by remember(user) { mutableStateOf<String>(user.insurancePolicyNo) }
    var editInsuranceStatus by remember(user) { mutableStateOf<String>(user.insuranceStatus) }

    var editHajjEligibility by remember(user) { mutableStateOf<String>(user.hajjEligibility) }
    var editLastHajjYear by remember(user) { mutableStateOf<String>(user.lastHajjYear) }

    val bgColor = if (isDark) AbsherDarkSection else Color(0xFFFBFDFC)
    val cardBg = if (isDark) AbsherCardBg else Color.White
    val cardBorder = if (isDark) Color(0xFF2B3830) else Color(0xFFE8EFEA)
    val textPrimary = if (isDark) Color(0xFFE2ECE7) else Color(0xFF212825)
    val textMuted = if (isDark) Color(0xFF8F9E97) else Color(0xFF8C9B93)
    val iconColor = if (isDark) AbsherMint else AbsherGreenHeader

    fun saveChanges() {
        val updatedUser = user.copy(
            fullNameEn = editName.trim(),
            fullNameAr = editNameAr.trim(),
            birthCity = editBirthCity.trim(),
            birthCountry = editBirthCountry.trim(),
            dateOfBirth = editDob.trim(),
            maritalStatus = editMaritalStatus.trim(),
            sponsorshipTransfers = editTransfers.trim(),
            religionEn = editReligion.trim(),
            workPermit = editWorkPermit.trim(),
            biometricsCollected = editBiometrics.trim(),
            travelStatus = editTravelStatus.trim(),
            sponsorName = editSponsorName.trim(),
            sponsorNameEn = editSponsorName.trim(),
            sponsorId = editSponsorId.trim(),
            establishmentStatus = editEstStatus.trim(),
            insuranceCompany = editInsuranceCompany.trim(),
            insurancePolicyNo = editPolicyNo.trim(),
            insuranceStatus = editInsuranceStatus.trim(),
            insuranceExpiry = editInsuranceExpiry.trim(),
            insuranceIssuingDate = editInsuranceIssuingDate.trim(),
            bloodType = editBloodType.trim(),
            hajjEligibility = editHajjEligibility.trim(),
            lastHajjYear = editLastHajjYear.trim()
        )
        viewModel.updateProfile(updatedUser)
        isEditing = false
        Toast.makeText(context, "Personal details saved successfully", Toast.LENGTH_SHORT).show()
    }

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
                    text = "My Personal Details",
                    color = if (isDark) Color.White else AbsherLightTextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Personal Details Card (with Avatar and 10 fields)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, cardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isPersonalExpanded = !isPersonalExpanded }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            UserAvatarImage(
                                photoUrl = user.photoUrl,
                                contentDescription = "User Avatar",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Personal Details",
                                color = textPrimary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Icon(
                            imageVector = if (isPersonalExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = "Toggle",
                            tint = textMuted,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    if (isPersonalExpanded) {
                        HorizontalDivider(
                            color = cardBorder,
                            thickness = 1.dp,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            if (!isEditing) {
                                PersonalDetailField("Name", user.fullNameEn, textMuted, textPrimary)
                                PersonalDetailField("Birth City", user.birthCity, textMuted, textPrimary)
                                PersonalDetailField("Birth Country/Region", user.birthCountry, textMuted, textPrimary)
                                PersonalDetailField("Date of Birth", user.dateOfBirth, textMuted, textPrimary)
                                PersonalDetailField("Marital Status", user.maritalStatus, textMuted, textPrimary)
                                PersonalDetailField("No. of sponsorship transfers", user.sponsorshipTransfers, textMuted, textPrimary)
                                PersonalDetailField("Religion", user.religionEn, textMuted, textPrimary)
                                PersonalDetailField("Work Permit", user.workPermit, textMuted, textPrimary)
                                PersonalDetailField("Biometrics Collected", user.biometricsCollected, textMuted, textPrimary)
                                PersonalDetailField("Travel Status", user.travelStatus, textMuted, textPrimary)
                            } else {
                                EditFieldInput("Name", editName, { editName = it }, textPrimary, textMuted, cardBorder)
                                EditFieldInput("Name (Arabic)", editNameAr, { editNameAr = it }, textPrimary, textMuted, cardBorder)
                                EditFieldInput("Birth City", editBirthCity, { editBirthCity = it }, textPrimary, textMuted, cardBorder)
                                EditFieldInput("Birth Country/Region", editBirthCountry, { editBirthCountry = it }, textPrimary, textMuted, cardBorder)
                                EditFieldInput("Date of Birth", editDob, { editDob = it }, textPrimary, textMuted, cardBorder)
                                EditFieldInput("Marital Status", editMaritalStatus, { editMaritalStatus = it }, textPrimary, textMuted, cardBorder)
                                EditFieldInput("No. of sponsorship transfers", editTransfers, { editTransfers = it }, textPrimary, textMuted, cardBorder)
                                EditFieldInput("Religion", editReligion, { editReligion = it }, textPrimary, textMuted, cardBorder)
                                EditFieldInput("Work Permit", editWorkPermit, { editWorkPermit = it }, textPrimary, textMuted, cardBorder)
                                EditFieldInput("Biometrics Collected", editBiometrics, { editBiometrics = it }, textPrimary, textMuted, cardBorder)
                                EditFieldInput("Travel Status", editTravelStatus, { editTravelStatus = it }, textPrimary, textMuted, cardBorder)
                            }
                        }
                    }
                }
            }

            // 2. Sponsor Details Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, cardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isSponsorExpanded = !isSponsorExpanded }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Person,
                                contentDescription = "Sponsor",
                                tint = iconColor,
                                modifier = Modifier.size(26.dp)
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Text(
                                text = "Sponsor Details",
                                color = textPrimary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Icon(
                            imageVector = if (isSponsorExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = "Toggle",
                            tint = textMuted,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    if (isSponsorExpanded) {
                        HorizontalDivider(
                            color = cardBorder,
                            thickness = 1.dp,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            if (!isEditing) {
                                PersonalDetailField("Sponsor Name", user.sponsorName.ifBlank { user.sponsorNameEn }, textMuted, textPrimary)
                                PersonalDetailFieldWithCopy(
                                    label = "Sponsor ID Number",
                                    value = user.sponsorId,
                                    labelColor = textMuted,
                                    valueColor = textPrimary,
                                    onCopy = {
                                        clipboardManager.setText(AnnotatedString(user.sponsorId))
                                        Toast.makeText(context, "Sponsor ID copied", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            } else {
                                EditFieldInput("Sponsor Name", editSponsorName, { editSponsorName = it }, textPrimary, textMuted, cardBorder)
                                EditFieldInput("Sponsor ID Number", editSponsorId, { editSponsorId = it }, textPrimary, textMuted, cardBorder)
                            }
                        }
                    }
                }
            }

            // 3. Health Insurance Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, cardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isInsuranceExpanded = !isInsuranceExpanded }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_crescent),
                                contentDescription = "Health Insurance",
                                tint = iconColor,
                                modifier = Modifier.size(26.dp)
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Text(
                                text = "Health Insurance",
                                color = textPrimary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Icon(
                            imageVector = if (isInsuranceExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = "Toggle",
                            tint = textMuted,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    if (isInsuranceExpanded) {
                        HorizontalDivider(
                            color = cardBorder,
                            thickness = 1.dp,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            if (!isEditing) {
                                PersonalDetailField("Issuing Date", user.insuranceIssuingDate, textMuted, textPrimary)
                                PersonalDetailField("Expiry Date", user.insuranceExpiry, textMuted, textPrimary)
                                PersonalDetailField("Blood Type", user.bloodType, textMuted, textPrimary)
                            } else {
                                EditFieldInput("Issuing Date", editInsuranceIssuingDate, { editInsuranceIssuingDate = it }, textPrimary, textMuted, cardBorder)
                                EditFieldInput("Expiry Date", editInsuranceExpiry, { editInsuranceExpiry = it }, textPrimary, textMuted, cardBorder)
                                EditFieldInput("Blood Type", editBloodType, { editBloodType = it }, textPrimary, textMuted, cardBorder)
                            }
                        }
                    }
                }
            }

            // 4. Hajj Details Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, cardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isHajjExpanded = !isHajjExpanded }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_kaaba),
                                contentDescription = "Hajj Details",
                                tint = iconColor,
                                modifier = Modifier.size(26.dp)
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Text(
                                text = "Hajj Details",
                                color = textPrimary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Icon(
                            imageVector = if (isHajjExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = "Toggle",
                            tint = textMuted,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    if (isHajjExpanded) {
                        HorizontalDivider(
                            color = cardBorder,
                            thickness = 1.dp,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            if (!isEditing) {
                                Column {
                                    Text(
                                        text = "Hajj Status",
                                        color = textMuted,
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Normal
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    val isEligible = user.hajjEligibility.contains("Eligible", ignoreCase = true) &&
                                            !user.hajjEligibility.contains("Not Eligible", ignoreCase = true)
                                    val isDark = isSystemInDarkTheme()
                                    val badgeBg = when {
                                        isEligible -> if (isDark) Color(0xFF133E2B) else Color(0xFFE6F4EA)
                                        user.hajjEligibility.contains("Not", ignoreCase = true) -> if (isDark) Color(0xFF3E1C1C) else Color(0xFFFCE8E6)
                                        else -> if (isDark) Color(0xFF2A2A2A) else Color(0xFFF1F3F4)
                                    }
                                    val badgeColor = when {
                                        isEligible -> if (isDark) Color(0xFF81C995) else Color(0xFF0D652D)
                                        user.hajjEligibility.contains("Not", ignoreCase = true) -> if (isDark) Color(0xFFF28B82) else Color(0xFFC5221F)
                                        else -> textPrimary
                                    }
                                    val dotColor = when {
                                        isEligible -> if (isDark) Color(0xFF34A853) else Color(0xFF0D652D)
                                        user.hajjEligibility.contains("Not", ignoreCase = true) -> if (isDark) Color(0xFFEA4335) else Color(0xFFC5221F)
                                        else -> textMuted
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(percent = 50),
                                        color = badgeBg
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(7.dp)
                                                    .background(dotColor, CircleShape)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (isEligible) "Eligible for Hajj" else user.hajjEligibility.ifBlank { "-" },
                                                color = badgeColor,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                                PersonalDetailField("Last Hajj Year", user.lastHajjYear, textMuted, textPrimary)
                            } else {
                                EditFieldInput("Hajj Status", editHajjEligibility, { editHajjEligibility = it }, textPrimary, textMuted, cardBorder)
                                EditFieldInput("Last Hajj Year", editLastHajjYear, { editLastHajjYear = it }, textPrimary, textMuted, cardBorder)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Sticky Bottom Save Bar when editing
        AnimatedVisibility(visible = isEditing) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isDark) AbsherDarkSection else Color.White)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { isEditing = false },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel", color = textMuted)
                    }

                    Button(
                        onClick = { saveChanges() },
                        modifier = Modifier.weight(1.5f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AbsherGreenHeader)
                    ) {
                        Text("Save All Changes", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun PersonalDetailField(
    label: String,
    value: String,
    labelColor: Color,
    valueColor: Color
) {
    Column {
        Text(
            text = label,
            color = labelColor,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.Normal
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value.ifBlank { "-" },
            color = valueColor.copy(alpha = 0.88f),
            fontSize = 14.5.sp,
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
private fun PersonalDetailFieldWithCopy(
    label: String,
    value: String,
    labelColor: Color,
    valueColor: Color,
    onCopy: () -> Unit
) {
    Column {
        Text(
            text = label,
            color = labelColor,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.Normal
        )
        Spacer(modifier = Modifier.height(2.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = value.ifBlank { "-" },
                color = valueColor.copy(alpha = 0.88f),
                fontSize = 14.5.sp,
                fontWeight = FontWeight.Normal
            )
            IconButton(
                onClick = onCopy,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.ContentCopy,
                    contentDescription = "Copy $label",
                    tint = AbsherGreenHeader,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun EditFieldInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    textColor: Color,
    labelColor: Color,
    borderColor: Color
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 12.sp) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = textColor,
            unfocusedTextColor = textColor,
            focusedBorderColor = AbsherGreenHeader,
            unfocusedBorderColor = borderColor,
            focusedLabelColor = AbsherGreenHeader,
            unfocusedLabelColor = labelColor
        )
    )
}

