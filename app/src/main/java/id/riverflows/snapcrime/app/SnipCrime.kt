package id.riverflows.snapcrime.app

import android.app.Application
import android.content.Context
import id.riverflows.snapcrime.BuildConfig
import timber.log.Timber
import timber.log.Timber.DebugTree


class SnipCrime: Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
    }
}