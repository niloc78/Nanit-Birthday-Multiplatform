package com.example.nanitbirthday

import android.app.Application
import com.example.nanitbirthday.di.initKoin
import org.koin.android.ext.koin.androidContext

class BirthdayApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@BirthdayApplication)
        }
    }
}
