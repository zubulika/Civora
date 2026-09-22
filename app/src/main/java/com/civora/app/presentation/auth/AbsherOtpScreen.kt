package com.civora.app.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.civora.app.core.components.AbsherHeaderBranding
import com.civora.app.core.designsystem.AbsherCardBg
import com.civora.app.core.designsystem.AbsherDarkSection
import com.civora.app.core.designsystem.AbsherGreenHeader
import com.civora.app.core.designsystem.AppThemeMode
import com.civora.app.core.designsystem.LocalThemeMode

@Composable
fun AbsherOtpScreen(
    onBackClick: () -> Unit,
    onOtpVerified: () -> Unit
) {
    val themeMode = LocalThemeMode.current
    val systemDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
        AppThemeMode.SYSTEM -> systemDark
    }

    // Initialize with a fresh random 6-digit code for authentic showoff display
    val initialCode = remember { (100000..999999).random().toString() }
    var textFieldValue by remember {
        mutableStateOf(
            TextFieldValue(
                text = initialCode,
                selection = TextRange(initialCode.length)
            )
        )
    }
    var hasUserEdited by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val bgColor = if (isDark) AbsherDarkSection else Color(0xFFFBFDFC)
    val textPrimary = if (isDark) Color(0xFFE2ECE7) else Color(0xFF1E2822)
    val textMuted = if (isDark) Color(0xFF8F9E97) else Color(0xFF88968F)
    val boxBg = if (isDark) AbsherCardBg else Color.White
    val boxBorder = if (isDark) Color(0xFF2E3A33) else Color(0xFFDCE6E1)

    // Auto-verify once user finishes typing 6 digits
    LaunchedEffect(textFieldValue.text, hasUserEdited) {
        if (hasUserEdited && textFieldValue.text.length == 6) {
            focusManager.clearFocus()
            keyboardController?.hide()
            onOtpVerified()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .statusBarsPadding()
            .padding(start = 24.dp, end = 24.dp, bottom = 48.dp)
    ) {
        // 1. Top Back Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = AbsherGreenHeader,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 2. Center Branding
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AbsherHeaderBranding(
                logoHeight = 56.dp,
                color = AbsherGreenHeader
            )

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Absher Authenticator",
                color = textPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Get the code from where Absher\nAuthenticator is enabled",
                color = textMuted,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
        }

        Spacer(modifier = Modifier.height(42.dp))

        // 3. 6-Digit OTP Boxes (Stacked: Visual boxes underneath, real BasicTextField filling full area)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    focusRequester.requestFocus()
                    keyboardController?.show()
                },
            contentAlignment = Alignment.Center
        ) {
            // Visual 6-Digit Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 0 until 6) {
                    val digit = textFieldValue.text.getOrNull(i)?.toString() ?: ""
                    val isCurrent = i == textFieldValue.text.length.coerceAtMost(5)

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(58.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(boxBg)
                            .border(
                                width = if (isCurrent) 1.8.dp else 1.2.dp,
                                color = if (isCurrent) AbsherGreenHeader else boxBorder,
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = digit,
                            color = textPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Real, full-bounds BasicTextField capturing all touches and IME input
            BasicTextField(
                value = textFieldValue,
                onValueChange = { newValue ->
                    hasUserEdited = true
                    val prevText = textFieldValue.text
                    val newText = newValue.text

                    val updatedOtp = if (prevText.length == 6 && newText.length > 6) {
                        // User started typing over a complete 6-digit code: start fresh with the new digit
                        val typedChar = newText.filter { it.isDigit() }.lastOrNull()?.toString() ?: ""
                        typedChar
                    } else {
                        // Regular typing or backspacing: filter digits and cap at 6
                        newText.filter { it.isDigit() }.take(6)
                    }

                    textFieldValue = TextFieldValue(
                        text = updatedOtp,
                        selection = TextRange(updatedOtp.length)
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                        onOtpVerified()
                    }
                ),
                textStyle = androidx.compose.ui.text.TextStyle(
                    color = Color.Transparent,
                    fontSize = 1.sp
                ),
                cursorBrush = SolidColor(Color.Transparent),
                modifier = Modifier
                    .fillMaxSize()
                    .focusRequester(focusRequester)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 4. Resend / Refresh Action
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Didn't receive code? ",
                color = textMuted,
                fontSize = 13.sp
            )
            Text(
                text = "Resend SMS",
                color = AbsherGreenHeader,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable {
                    val freshCode = (100000..999999).random().toString()
                    textFieldValue = TextFieldValue(
                        text = freshCode,
                        selection = TextRange(freshCode.length)
                    )
                    hasUserEdited = false
                }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // 5. Primary Action Button

        Button(
            onClick = {
                focusManager.clearFocus()
                keyboardController?.hide()
                onOtpVerified()
            },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AbsherGreenHeader,
                contentColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(
                text = "Receive Code via SMS",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

    }
}
