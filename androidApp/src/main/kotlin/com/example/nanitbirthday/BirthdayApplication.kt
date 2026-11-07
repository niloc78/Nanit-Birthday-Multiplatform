package com.example.nanitbirthday

import android.app.Application
import com.example.nanitbirthday.di.initKoin

class BirthdayApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // TODO Step 2 - Persistence via DataStore
        initKoin()
    }
}
