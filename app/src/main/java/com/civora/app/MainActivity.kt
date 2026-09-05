package com.civora.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.civora.app.core.designsystem.CivoraTheme
import com.civora.app.navigation.CivoraApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val appContainer = (application as CivoraApplication).container

        setContent {
            CivoraTheme {
                CivoraApp(container = appContainer)
            }
        }
    }
}
