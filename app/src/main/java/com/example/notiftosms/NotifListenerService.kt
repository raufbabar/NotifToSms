package com.example.notiftosms

import android.app.Notification
import android.content.SharedPreferences
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.telephony.SmsManager

class NotifListenerService : NotificationListenerService() {

    private val allowedPackages = listOf(
        "com.whatsapp",
        "com.instagram.android",
        "com.facebook.katana",
        "com.facebook.orca",
        "org.telegram.messenger",
        "com.twitter.android"
    )

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)

        val packageName = sbn.packageName

        if (allowedPackages.isNotEmpty() && !allowedPackages.contains(packageName)) {
            return
        }

        val extras: Bundle = sbn.notification.extras
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString() ?: ""
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: ""

        if (title.isEmpty() && text.isEmpty()) return

        val appLabel = try {
            packageManager.getApplicationLabel(
                packageManager.getApplicationInfo(packageName, 0)
            ).toString()
        } catch (e: Exception) {
            packageName
        }

        val message = "[$appLabel] $title: $text"

        sendSms(message)
    }

    private fun sendSms(message: String) {
        val prefs: SharedPreferences = getSharedPreferences("notif_to_sms_prefs", MODE_PRIVATE)
        val targetNumber = prefs.getString("target_number", null) ?: return

        try {
            val smsManager = SmsManager.getDefault()
            val parts = smsManager.divideMessage(message)
            smsManager.sendMultipartTextMessage(targetNumber, null, parts, null, null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
