package com.batya.tictactoe.presentation.application

import android.app.Application
import com.batya.tictactoe.presentation.di.appModule
import com.batya.tictactoe.presentation.di.dataModule
import com.batya.tictactoe.presentation.di.firebaseModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class App : Application() {

    override fun onCreate() {
        super.onCreate()


        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@App)
            modules(listOf(appModule, firebaseModule, dataModule))
        }

    }
}