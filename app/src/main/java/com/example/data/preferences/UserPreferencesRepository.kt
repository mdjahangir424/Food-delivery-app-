package com.example.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

data class NotificationPreferences(
    val orderUpdatesEnabled: Boolean = true,
    val promotionsEnabled: Boolean = true,
    val deliveryAlertsEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val fcmToken: String = ""
)

data class UserPreferences(
    val languageCode: String,
    val userName: String,
    val userPhone: String,
    val selectedAreaBn: String,
    val selectedAreaEn: String,
    val hasCompletedOnboarding: Boolean,
    val notifications: NotificationPreferences = NotificationPreferences()
)

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val LANGUAGE_CODE = stringPreferencesKey("language_code")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_PHONE = stringPreferencesKey("user_phone")
        val SELECTED_AREA_BN = stringPreferencesKey("selected_area_bn")
        val SELECTED_AREA_EN = stringPreferencesKey("selected_area_en")
        val HAS_COMPLETED_ONBOARDING = booleanPreferencesKey("has_completed_onboarding")

        // Notification preferences
        val ORDER_UPDATES_ENABLED = booleanPreferencesKey("order_updates_enabled")
        val PROMOTIONS_ENABLED = booleanPreferencesKey("promotions_enabled")
        val DELIVERY_ALERTS_ENABLED = booleanPreferencesKey("delivery_alerts_enabled")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val FCM_TOKEN = stringPreferencesKey("fcm_token")
    }

    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data.map { preferences ->
        val languageCode = preferences[PreferencesKeys.LANGUAGE_CODE] ?: "bn"
        val userName = preferences[PreferencesKeys.USER_NAME] ?: "তানভীর আহমেদ"
        val userPhone = preferences[PreferencesKeys.USER_PHONE] ?: "01711234567"
        val selectedAreaBn = preferences[PreferencesKeys.SELECTED_AREA_BN] ?: "ধানমন্ডি"
        val selectedAreaEn = preferences[PreferencesKeys.SELECTED_AREA_EN] ?: "Dhanmondi"
        val hasCompletedOnboarding = preferences[PreferencesKeys.HAS_COMPLETED_ONBOARDING] ?: false

        val notifPrefs = NotificationPreferences(
            orderUpdatesEnabled = preferences[PreferencesKeys.ORDER_UPDATES_ENABLED] ?: true,
            promotionsEnabled = preferences[PreferencesKeys.PROMOTIONS_ENABLED] ?: true,
            deliveryAlertsEnabled = preferences[PreferencesKeys.DELIVERY_ALERTS_ENABLED] ?: true,
            soundEnabled = preferences[PreferencesKeys.SOUND_ENABLED] ?: true,
            fcmToken = preferences[PreferencesKeys.FCM_TOKEN] ?: ""
        )

        UserPreferences(
            languageCode = languageCode,
            userName = userName,
            userPhone = userPhone,
            selectedAreaBn = selectedAreaBn,
            selectedAreaEn = selectedAreaEn,
            hasCompletedOnboarding = hasCompletedOnboarding,
            notifications = notifPrefs
        )
    }

    suspend fun setLanguage(languageCode: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LANGUAGE_CODE] = languageCode
        }
    }

    suspend fun setDeliveryArea(areaBn: String, areaEn: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SELECTED_AREA_BN] = areaBn
            preferences[PreferencesKeys.SELECTED_AREA_EN] = areaEn
        }
    }

    suspend fun updateProfile(name: String, phone: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_NAME] = name
            preferences[PreferencesKeys.USER_PHONE] = phone
        }
    }

    suspend fun setOnboardingCompleted() {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.HAS_COMPLETED_ONBOARDING] = true
        }
    }

    suspend fun updateNotificationPreference(
        orderUpdates: Boolean? = null,
        promotions: Boolean? = null,
        deliveryAlerts: Boolean? = null,
        sound: Boolean? = null
    ) {
        context.dataStore.edit { preferences ->
            orderUpdates?.let { preferences[PreferencesKeys.ORDER_UPDATES_ENABLED] = it }
            promotions?.let { preferences[PreferencesKeys.PROMOTIONS_ENABLED] = it }
            deliveryAlerts?.let { preferences[PreferencesKeys.DELIVERY_ALERTS_ENABLED] = it }
            sound?.let { preferences[PreferencesKeys.SOUND_ENABLED] = it }
        }
    }

    suspend fun saveFcmToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.FCM_TOKEN] = token
        }
    }
}
