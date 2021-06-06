package id.riverflows.snapcrime.app

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.messaging.FirebaseMessaging
import id.riverflows.snapcrime.BuildConfig
import id.riverflows.snapcrime.util.UtilConstants
import id.riverflows.snapcrime.util.UtilConstants.FCM_TOPIC_CASES_REPORT
import timber.log.Timber
import timber.log.Timber.DebugTree


class SnapCrime: Application(){
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
        FirebaseMessaging.getInstance().subscribeToTopic(FCM_TOPIC_CASES_REPORT)
    }

    companion object{
        fun getDefaultSharedPreference(context: Context): SharedPreferences = context.getSharedPreferences(
            context.packageName,
            Context.MODE_PRIVATE
        )
    }
}