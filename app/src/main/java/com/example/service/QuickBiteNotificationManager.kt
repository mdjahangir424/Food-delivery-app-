package com.example.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import com.example.data.preferences.NotificationPreferences
import com.example.ui.localization.BengaliNumberFormatter
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import kotlin.random.Random

object QuickBiteNotificationManager {

    private const val TAG = "QuickBiteNotification"

    const val CHANNEL_ORDERS = "quickbite_orders_channel"
    const val CHANNEL_PROMOTIONS = "quickbite_promotions_channel"

    const val EXTRA_NAV_SCREEN = "extra_nav_screen"
    const val EXTRA_ORDER_ID = "extra_order_id"
    const val EXTRA_PROMO_CODE = "extra_promo_code"

    fun initNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Orders Channel (High priority)
            val ordersChannel = NotificationChannel(
                CHANNEL_ORDERS,
                "Order & Delivery Updates (অর্ডার ও ডেলিভারি)",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Real-time order confirmation, preparation, and rider delivery notifications"
                enableVibration(true)
                enableLights(true)
                setShowBadge(true)
            }

            // Promotions Channel (Default priority)
            val promosChannel = NotificationChannel(
                CHANNEL_PROMOTIONS,
                "Offers & Discounts (অফার ও ছাড়)",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily special Dhaka food deals, free delivery alerts, and promo vouchers"
                enableVibration(true)
                setShowBadge(true)
            }

            notificationManager.createNotificationChannel(ordersChannel)
            notificationManager.createNotificationChannel(promosChannel)
        }
    }

    fun initFCM(context: Context, onTokenRetrieved: (String) -> Unit) {
        try {
            // Check if Firebase is initialized
            if (FirebaseApp.getApps(context).isEmpty()) {
                FirebaseApp.initializeApp(context)
            }

            FirebaseMessaging.getInstance().token
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val token = task.result
                        Log.d(TAG, "FCM Registration Token: $token")
                        if (!token.isNullOrEmpty()) {
                            onTokenRetrieved(token)
                        }
                    } else {
                        Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                    }
                }

            // Subscribe to common food delivery topics
            FirebaseMessaging.getInstance().subscribeToTopic("all_users")
            FirebaseMessaging.getInstance().subscribeToTopic("dhaka_offers")
            FirebaseMessaging.getInstance().subscribeToTopic("order_updates")
        } catch (e: Exception) {
            Log.w(TAG, "FCM initialization fallback: ${e.message}")
        }
    }

    private fun canSendNotifications(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun showOrderConfirmationNotification(
        context: Context,
        orderId: String,
        restaurantName: String,
        totalAmount: Int,
        isBn: Boolean,
        prefs: NotificationPreferences = NotificationPreferences()
    ) {
        if (!prefs.orderUpdatesEnabled || !canSendNotifications(context)) return

        val title = if (isBn) "অর্ডার সফলভাবে নিশ্চিত হয়েছে! 🎉" else "Order Confirmed Successfully! 🎉"
        val totalFormatted = BengaliNumberFormatter.formatCurrency(totalAmount, isBn)
        val content = if (isBn) {
            "আপনার $restaurantName এর অর্ডার (#$orderId) প্রস্তুত করা হচ্ছে। মোট: $totalFormatted"
        } else {
            "Your order (#$orderId) from $restaurantName is being prepared. Total: $totalFormatted"
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_NAV_SCREEN, "ORDER_TRACKING")
            putExtra(EXTRA_ORDER_ID, orderId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            Random.nextInt(1000, 9999),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ORDERS)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setColor(0xFFE64A19.toInt())
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .apply {
                if (prefs.soundEnabled) {
                    setDefaults(NotificationCompat.DEFAULT_ALL)
                } else {
                    setNotificationSilent()
                }
            }
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(orderId.hashCode(), notification)
    }

    fun showDeliveryUpdateNotification(
        context: Context,
        orderId: String,
        status: String,
        restaurantName: String,
        isBn: Boolean,
        prefs: NotificationPreferences = NotificationPreferences()
    ) {
        if (!prefs.deliveryAlertsEnabled || !canSendNotifications(context)) return

        val (title, body) = when (status) {
            "PREPARING" -> Pair(
                if (isBn) "খাবার তৈরি হচ্ছে 👨‍🍳" else "Food Being Prepared 👨‍🍳",
                if (isBn) "$restaurantName আপনার সুস্বাদু খাবার গরম ও তাজা বানাচ্ছে।" else "$restaurantName is cooking your fresh meal."
            )
            "ON_THE_WAY" -> Pair(
                if (isBn) "রাইডার খাবার নিয়ে রওয়ানা হয়েছেন! 🛵" else "Rider On the Way! 🛵",
                if (isBn) "ডেলিভারি হিরো আপনার ঠিকানার পথে রওয়ানা হয়েছেন। শীঘ্রই পৌঁছাবে।" else "Your delivery hero is speeding towards your location."
            )
            "DELIVERED" -> Pair(
                if (isBn) "খাবার পৌঁছে গেছে! 🍲" else "Order Delivered! 🍲",
                if (isBn) "আপনার কাঙ্ক্ষিত খাবার ডেলিভারি সম্পন্ন হয়েছে। কুইকবাইটের সাথে খাবার উপভোগ করুন!" else "Your order has arrived! Enjoy your delicious meal with QuickBite."
            )
            else -> Pair(
                if (isBn) "অর্ডার আপডেট (#$orderId)" else "Order Update (#$orderId)",
                if (isBn) "আপনার অর্ডারের নতুন স্ট্যাটাস আপডেট হয়েছে।" else "New update on your order status."
            )
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_NAV_SCREEN, "ORDER_TRACKING")
            putExtra(EXTRA_ORDER_ID, orderId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            Random.nextInt(1000, 9999),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ORDERS)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setColor(0xFFE64A19.toInt())
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .apply {
                if (prefs.soundEnabled) {
                    setDefaults(NotificationCompat.DEFAULT_ALL)
                } else {
                    setNotificationSilent()
                }
            }
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(orderId.hashCode() + status.hashCode(), notification)
    }

    fun showSpecialOfferNotification(
        context: Context,
        title: String,
        body: String,
        promoCode: String,
        isBn: Boolean,
        prefs: NotificationPreferences = NotificationPreferences()
    ) {
        if (!prefs.promotionsEnabled || !canSendNotifications(context)) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_NAV_SCREEN, "CART")
            putExtra(EXTRA_PROMO_CODE, promoCode)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            Random.nextInt(1000, 9999),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_PROMOTIONS)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText("$body\n${if (isBn) "কুপন কোড" else "Promo Code"}: $promoCode"))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setColor(0xFF2E7D32.toInt())
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(promoCode.hashCode(), notification)
    }
}
