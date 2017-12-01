package com.herscher.cribbage.core

import android.app.Application
import com.herscher.cribbage.BuildConfig
import timber.log.Timber

class CribbageApplication : Application() {
    lateinit var component: ApplicationComponent
        private set

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        component = DaggerApplicationComponent.builder()
                .applicationModule(ApplicationModule(this))
                .build()
    }
}