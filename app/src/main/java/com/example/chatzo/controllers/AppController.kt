package com.example.chatzo.controllers

import android.app.Application
import android.util.Log
import androidx.lifecycle.LifecycleObserver
import com.onesignal.OSNotification
import com.onesignal.OSNotificationOpenResult
import com.onesignal.OSSubscriptionObserver
import com.onesignal.OneSignal
import com.onesignal.OneSignal.NotificationOpenedHandler
import com.onesignal.OneSignal.NotificationReceivedHandler

/**
 * Created by admin on 02-Feb-18.
 */
class AppController : Application(), LifecycleObserver {
    override fun onCreate() {
        super.onCreate()
        instatnce = this
        OneSignal.startInit(this)
                .setNotificationReceivedHandler(MyNotificationReceivedHandler())
                .setNotificationOpenedHandler(MyNotificationOpenedHandler())
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init()
        OneSignal.addSubscriptionObserver(mSubscriptionObserver)
    }

    private val mSubscriptionObserver = OSSubscriptionObserver { stateChanges ->
        Log.d("SHANTAG", "onOSPermissionChanged: $stateChanges")
        if (!stateChanges.from.subscribed &&
                stateChanges.to.subscribed) {
            // get player ID
            val oneSignalID = stateChanges.to.userId
            // send oneSignalID to server
            Log.d("SHANTAG", "onOSPermissionChanged: $oneSignalID")
        }
    }

    private inner class MyNotificationOpenedHandler : NotificationOpenedHandler {
        override fun notificationOpened(result: OSNotificationOpenResult) {}
    }

    private inner class MyNotificationReceivedHandler : NotificationReceivedHandler {
        override fun notificationReceived(notification: OSNotification) {
            /* if(isAppInForeground()){
                Log.e("BTAG","inForeground");
                OneSignal.cancelNotification(notification.androidNotificationId);
            }else{

                Log.e("BTAG","inBackground");

            }*/
        }
    }

    companion object {
        var instatnce: AppController? = null
        var camCount = 0
    }
}