package id.riverflows.snapcrime.fcm

import com.google.firebase.messaging.FirebaseMessagingService
import id.riverflows.snapcrime.app.SnapCrime
import id.riverflows.snapcrime.util.UtilConstants.PREF_FCM_TOKEN

class FcmNotificationService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        SnapCrime.getDefaultSharedPreference(this).edit().apply {
            putString(PREF_FCM_TOKEN, token)
        }.apply()
    }
}