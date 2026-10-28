package com.example.service

import android.util.Log
import com.example.data.preferences.UserPreferencesRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class QuickBiteFirebaseMessagingService : FirebaseMessagingService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val prefsRepo by lazy { UserPreferencesRepository(applicationContext) }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM Token received: $token")
        serviceScope.launch {
            prefsRepo.saveFcmToken(token)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "FCM message received from: ${remoteMessage.from}")

        serviceScope.launch {
            val userPrefs = prefsRepo.userPreferencesFlow.first()
            val notifPrefs = userPrefs.notifications
            val isBn = userPrefs.languageCode == "bn"

            val data = remoteMessage.data
            val notification = remoteMessage.notification

            val type = data["type"] ?: "GENERAL"
            val title = data["title"] ?: notification?.title ?: "QuickBite Notification"
            val body = data["body"] ?: notification?.body ?: "You have a new update from QuickBite"

            when (type) {
                "ORDER_CONFIRMATION" -> {
                    val orderId = data["order_id"] ?: "QB-DEMO"
                    val restaurant = data["restaurant_name"] ?: "QuickBite Restaurant"
                    val total = data["total"]?.toIntOrNull() ?: 500
                    QuickBiteNotificationManager.showOrderConfirmationNotification(
                        context = applicationContext,
                        orderId = orderId,
                        restaurantName = restaurant,
                        totalAmount = total,
                        isBn = isBn,
                        prefs = notifPrefs
                    )
                }
                "DELIVERY_UPDATE" -> {
                    val orderId = data["order_id"] ?: "QB-DEMO"
                    val status = data["status"] ?: "ON_THE_WAY"
                    val restaurant = data["restaurant_name"] ?: "QuickBite"
                    QuickBiteNotificationManager.showDeliveryUpdateNotification(
                        context = applicationContext,
                        orderId = orderId,
                        status = status,
                        restaurantName = restaurant,
                        isBn = isBn,
                        prefs = notifPrefs
                    )
                }
                "SPECIAL_OFFER" -> {
                    val promoCode = data["promo_code"] ?: "QUICK50"
                    QuickBiteNotificationManager.showSpecialOfferNotification(
                        context = applicationContext,
                        title = title,
                        body = body,
                        promoCode = promoCode,
                        isBn = isBn,
                        prefs = notifPrefs
                    )
                }
                else -> {
                    // General notification
                    QuickBiteNotificationManager.showSpecialOfferNotification(
                        context = applicationContext,
                        title = title,
                        body = body,
                        promoCode = "QUICK50",
                        isBn = isBn,
                        prefs = notifPrefs
                    )
                }
            }
        }
    }

    companion object {
        private const val TAG = "QuickBiteFCM"
    }
}
