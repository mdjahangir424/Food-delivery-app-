package com.example.ui.screens.profile

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.preferences.NotificationPreferences
import com.example.service.QuickBiteNotificationManager
import com.example.ui.localization.AppStrings
import com.example.ui.theme.FreshGreen
import com.example.ui.viewmodel.QuickBiteViewModel
import kotlinx.coroutines.launch

@Composable
fun NotificationSettingsScreen(
    viewModel: QuickBiteViewModel,
    notificationPrefs: NotificationPreferences,
    strings: AppStrings,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("notification_settings_back")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                    Text(
                        text = strings.notificationSettings,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("notification_settings_content"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // FCM Connection Status Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = FreshGreen,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.CloudDone,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = strings.fcmStatusActive,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Firebase Cloud Messaging (FCM) v1",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = FreshGreen
                                )
                            }
                        }

                        if (notificationPrefs.fcmToken.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = strings.fcmTokenLabel,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = notificationPrefs.fcmToken.take(24) + "...",
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    IconButton(
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            clipboard.setPrimaryClip(ClipData.newPlainText("FCM Token", notificationPrefs.fcmToken))
                                            scope.launch {
                                                snackbarHostState.showSnackbar(
                                                    if (strings.isBn) "FCM টোকেন কপি করা হয়েছে" else "FCM token copied to clipboard"
                                                )
                                            }
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Copy Token",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Preferences Toggles Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        // Order Updates
                        NotificationToggleRow(
                            icon = Icons.Default.ReceiptLong,
                            title = strings.orderUpdatesToggle,
                            description = strings.orderUpdatesDesc,
                            checked = notificationPrefs.orderUpdatesEnabled,
                            onCheckedChange = { viewModel.updateNotificationPreferences(orderUpdates = it) }
                        )

                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                        // Delivery Alerts
                        NotificationToggleRow(
                            icon = Icons.Default.DeliveryDining,
                            title = strings.deliveryAlertsToggle,
                            description = strings.deliveryAlertsDesc,
                            checked = notificationPrefs.deliveryAlertsEnabled,
                            onCheckedChange = { viewModel.updateNotificationPreferences(deliveryAlerts = it) }
                        )

                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                        // Promotions & Offers
                        NotificationToggleRow(
                            icon = Icons.Default.LocalOffer,
                            title = strings.promotionsToggle,
                            description = strings.promotionsDesc,
                            checked = notificationPrefs.promotionsEnabled,
                            onCheckedChange = { viewModel.updateNotificationPreferences(promotions = it) }
                        )

                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                        // Sound & Vibration
                        NotificationToggleRow(
                            icon = Icons.Default.VolumeUp,
                            title = strings.soundToggle,
                            description = strings.soundDesc,
                            checked = notificationPrefs.soundEnabled,
                            onCheckedChange = { viewModel.updateNotificationPreferences(sound = it) }
                        )
                    }
                }
            }

            // Test Notifications Section
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = strings.testNotifications,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Button 1: Test Order Confirmation
                        OutlinedButton(
                            onClick = {
                                QuickBiteNotificationManager.showOrderConfirmationNotification(
                                    context = context,
                                    orderId = "QB-DEMO99",
                                    restaurantName = if (strings.isBn) "গ্র্যান্ড নবাব কাচ্চি" else "Grand Nawab Kacchi",
                                    totalAmount = 540,
                                    isBn = strings.isBn,
                                    prefs = notificationPrefs
                                )
                                scope.launch {
                                    snackbarHostState.showSnackbar(strings.notificationTestSent)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_test_order_notification"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = strings.testOrderNotification)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Button 2: Test Delivery Update
                        OutlinedButton(
                            onClick = {
                                QuickBiteNotificationManager.showDeliveryUpdateNotification(
                                    context = context,
                                    orderId = "QB-DEMO99",
                                    status = "ON_THE_WAY",
                                    restaurantName = if (strings.isBn) "সুলতান্স ডাইন" else "Sultan's Dine",
                                    isBn = strings.isBn,
                                    prefs = notificationPrefs
                                )
                                scope.launch {
                                    snackbarHostState.showSnackbar(strings.notificationTestSent)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_test_delivery_notification"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.DeliveryDining, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = strings.testDeliveryNotification)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Button 3: Test Special Offer
                        OutlinedButton(
                            onClick = {
                                QuickBiteNotificationManager.showSpecialOfferNotification(
                                    context = context,
                                    title = if (strings.isBn) "🔥 বিশেষ কাচ্চি উৎসব ছাড়!" else "🔥 Special Kacchi Fiesta Discount!",
                                    body = if (strings.isBn) "আজকের অর্ডারে KACCHI50 কোড দিয়ে উপভোগ করুন ২০% ছাড়।" else "Get 20% discount on today's order using code KACCHI50.",
                                    promoCode = "KACCHI50",
                                    isBn = strings.isBn,
                                    prefs = notificationPrefs
                                )
                                scope.launch {
                                    snackbarHostState.showSnackbar(strings.notificationTestSent)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_test_offer_notification"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.LocalOffer, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = strings.testOfferNotification)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationToggleRow(
    icon: ImageVector,
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
            modifier = Modifier.size(36.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}
