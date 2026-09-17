package com.civora.app.presentation.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.civora.app.R
import com.civora.app.core.components.AbsherHeaderBranding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.collectAsState
import com.civora.app.core.designsystem.AbsherCardBg
import com.civora.app.core.designsystem.AbsherDarkSection
import com.civora.app.core.designsystem.AbsherGreenHeader
import com.civora.app.core.designsystem.AppThemeMode
import com.civora.app.core.designsystem.LocalThemeMode

@Composable
fun AbsherLoginFormScreen(
    onBackClick: () -> Unit,
    onLoginSubmit: () -> Unit,
    viewModel: AuthViewModel? = null
) {
    val themeMode = LocalThemeMode.current
    val systemDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
        AppThemeMode.SYSTEM -> systemDark
    }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val authState = viewModel?.uiState?.collectAsState()?.value

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var keepMeLoggedIn by remember { mutableStateOf(true) }

    // Validation threshold: username not blank and password has at least 4 characters
    val isFormValid = username.isNotBlank() && password.length >= 4 && (authState?.isLoading != true)

    val bgColor = if (isDark) AbsherDarkSection else Color(0xFFFBFDFC)
    val textPrimary = if (isDark) Color(0xFFE2ECE7) else Color(0xFF1E2822)
    val textMuted = if (isDark) Color(0xFF8F9E97) else Color(0xFF88968F)
    val inputBorderColor = if (isDark) Color(0xFF2E3A33) else Color(0xFFD6E3DC)
    val activeBorderColor = AbsherGreenHeader
    val inputBg = if (isDark) AbsherCardBg else Color.White

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .statusBarsPadding()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState())
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

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Absher Logo & Emblem Center (Raw SVG Barcode Vector)
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AbsherHeaderBranding(
                logoHeight = 56.dp,
                color = AbsherGreenHeader
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Log In to Absher",
                color = textPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 3. Username / ID Number Field
        AbsherFormField(
            label = "Username or ID Number",
            placeholder = "Enter 10-digit National ID / Iqama",
            value = username,
            onValueChange = { username = it },
            inputBg = inputBg,
            borderColor = inputBorderColor,
            textPrimary = textPrimary,
            textMuted = textMuted,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 4. Password Field (styled with green border highlight)
        AbsherFormField(
            label = "Password",
            placeholder = "Enter Password",
            value = password,
            onValueChange = { password = it },
            inputBg = inputBg,
            borderColor = if (password.isNotEmpty()) activeBorderColor else inputBorderColor,
            isPassword = true,
            textPrimary = textPrimary,
            textMuted = textMuted,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (isFormValid) {
                        focusManager.clearFocus()
                        if (viewModel != null) {
                            viewModel.login(username, password) {
                                onLoginSubmit()
                            }
                        }
                    }
                }
            )
        )

        Spacer(modifier = Modifier.height(18.dp))

        // 5. Keep me logged in Checkbox
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    keepMeLoggedIn = !keepMeLoggedIn
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (keepMeLoggedIn) AbsherGreenHeader else Color.Transparent)
                    .border(
                        width = 1.5.dp,
                        color = if (keepMeLoggedIn) AbsherGreenHeader else textMuted,
                        shape = RoundedCornerShape(4.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (keepMeLoggedIn) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Checked",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = "Keep me logged in",
                color = textPrimary.copy(alpha = 0.85f),
                fontSize = 14.sp
            )
        }

        if (authState?.errorMessage != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isDark) Color(0xFF3B1F22) else Color(0xFFFFEBEE))
                    .border(
                        width = 1.dp,
                        color = if (isDark) Color(0xFFB71C1C) else Color(0xFFFFCDD2),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(12.dp)
            ) {
                Text(
                    text = authState.errorMessage,
                    color = if (isDark) Color(0xFFFF8A80) else Color(0xFFC62828),
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        } else {
            Spacer(modifier = Modifier.height(60.dp))
        }

        // 6. Log In Button (enabled only when meeting criteria)
        Button(
            onClick = {
                focusManager.clearFocus()
                if (viewModel != null) {
                    viewModel.login(username, password) {
                        onLoginSubmit()
                    }
                } else {
                    onLoginSubmit()
                }
            },
            enabled = isFormValid,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AbsherGreenHeader,
                contentColor = Color.White,
                disabledContainerColor = if (isDark) Color(0xFF283A31) else Color(0xFFA5C2B4),
                disabledContentColor = Color.White.copy(alpha = 0.7f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            if (authState?.isLoading == true) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Authenticating...",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            } else {
                Text(
                    text = "Log In",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        // 7. Forgot Password Link
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    Toast.makeText(
                        context,
                        "Please contact your Absher portal administrator to reset your password.",
                        Toast.LENGTH_LONG
                    ).show()
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Forgot Password",
                color = AbsherGreenHeader,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun AbsherFormField(
    label: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    inputBg: Color,
    borderColor: Color,
    textPrimary: Color,
    textMuted: Color,
    isPassword: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(inputBg)
            .border(
                width = 1.2.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = label,
                color = textMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Normal
            )
            Spacer(modifier = Modifier.height(2.dp))
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = TextStyle(
                    color = textPrimary,
                    fontSize = 14.sp
                ),
                cursorBrush = SolidColor(AbsherGreenHeader),
                visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { innerTextField ->
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            color = textMuted.copy(alpha = 0.7f),
                            fontSize = 14.sp
                        )
                    }
                    innerTextField()
                }
            )
        }
    }
}
