package com.civora.app

import android.app.Application
import com.civora.app.core.di.AppContainer

class CivoraApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
