package com.shuhaibnc.mpvnc

import android.app.Application
import com.shuhaibnc.mpvnc.di.AppModule
import com.shuhaibnc.mpvnc.di.DatabaseModule
import com.shuhaibnc.mpvnc.di.FileManagerModule
import com.shuhaibnc.mpvnc.di.PreferencesModule
import com.shuhaibnc.mpvnc.di.ViewModelModule
import com.shuhaibnc.mpvnc.presentation.crash.CrashActivity
import com.shuhaibnc.mpvnc.presentation.crash.GlobalExceptionHandler
import org.koin.android.ext.koin.androidContext
import org.koin.androix.startup.KoinStartup
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.koinConfiguration

@OptIn(KoinExperimentalAPI::class)
class App : Application(), KoinStartup {
  override fun onCreate() {
    super.onCreate()
    Thread.setDefaultUncaughtExceptionHandler(GlobalExceptionHandler(applicationContext, CrashActivity::class.java))
  }

  override fun onKoinStartup() = koinConfiguration {
    androidContext(this@App)
    modules(
      AppModule,
      PreferencesModule,
      DatabaseModule,
      FileManagerModule,
      ViewModelModule,
    )
  }
}
