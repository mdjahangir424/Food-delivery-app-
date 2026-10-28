package com.example

import android.app.NotificationManager
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.preferences.NotificationPreferences
import com.example.data.repository.FoodRepository
import com.example.service.QuickBiteNotificationManager
import com.example.ui.localization.BengaliNumberFormatter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("QuickBite", appName)
    }

    @Test
    fun `bengali number formatter converts correctly`() {
        val formatted = BengaliNumberFormatter.formatCurrency(450, true)
        assertEquals("৳ ৪৫০", formatted)

        val formattedEn = BengaliNumberFormatter.formatCurrency(450, false)
        assertEquals("৳ 450", formattedEn)
    }

    @Test
    fun `food repository contains dhaka restaurants and foods`() {
        val repo = FoodRepository()
        assertTrue(repo.restaurants.isNotEmpty())
        assertTrue(repo.dhakaAreas.isNotEmpty())
        val grandNawab = repo.getRestaurantById("rest_1")
        assertNotNull(grandNawab)
        assertEquals("গ্র্যান্ড নবাব কাচ্চি", grandNawab?.nameBn)
    }

    @Test
    fun `notification channels are created properly`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        QuickBiteNotificationManager.initNotificationChannels(context)

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val ordersChannel = manager.getNotificationChannel(QuickBiteNotificationManager.CHANNEL_ORDERS)
        val promosChannel = manager.getNotificationChannel(QuickBiteNotificationManager.CHANNEL_PROMOTIONS)

        assertNotNull("Orders notification channel must be created", ordersChannel)
        assertNotNull("Promotions notification channel must be created", promosChannel)
    }

    @Test
    fun `notification preferences default to enabled`() {
        val defaultPrefs = NotificationPreferences()
        assertTrue(defaultPrefs.orderUpdatesEnabled)
        assertTrue(defaultPrefs.promotionsEnabled)
        assertTrue(defaultPrefs.deliveryAlertsEnabled)
        assertTrue(defaultPrefs.soundEnabled)
    }
}
