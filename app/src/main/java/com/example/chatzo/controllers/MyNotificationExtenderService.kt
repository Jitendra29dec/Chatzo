package com.example.chatzo.controllers

import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.chatzo.R
import com.onesignal.NotificationExtenderService
import com.onesignal.OSNotificationReceivedResult
import java.math.BigInteger

//import android.support.v4.app.NotificationCompat;
/**
 * Created by user on 3/8/2018.
 */
class MyNotificationExtenderService : NotificationExtenderService() {
    override fun onNotificationProcessing(notification: OSNotificationReceivedResult): Boolean {
        val overrideSettings = OverrideSettings()
        overrideSettings.extender = NotificationCompat.Extender { builder -> // Sets the background notification color to Green on Android 5.0+ devices.
            builder
                    .setColor(BigInteger("e47e1c", 16).toInt())
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setBadgeIconType(R.mipmap.ic_launcher_round)
                    .setSound(null)
                    .setGroupSummary(true)
                    .setGroup("Group")
        }
        notification.payload.sound = null
        val displayedResult = displayNotification(overrideSettings)
        Log.d("OneSignalExample", "Notification displayed with id: " + displayedResult.androidNotificationId)
        return true
    }
}