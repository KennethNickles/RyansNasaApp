package com.ryansama.workdaynasaapp

import android.app.Application
import com.ryansama.workdaynasaapp.di.AppContainer

class WorkdayNasaApplication: Application() {
    lateinit var appContainer: AppContainer

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(applicationContext)
    }
}
