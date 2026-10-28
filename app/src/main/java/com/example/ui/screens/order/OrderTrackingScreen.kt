package com.example.ui.screens.order

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.outlined.StarOutline
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.R
import com.example.data.local.entity.OrderEntity
import com.example.ui.localization.AppStrings
import com.example.ui.localization.BengaliNumberFormatter
import com.example.ui.theme.FreshGreen
import com.example.ui.theme.GoldStar
import com.example.ui.viewmodel.QuickBiteViewModel
import kotlinx.coroutines.launch

@Composable
fun OrderTrackingScreen(
    orderId: String,
    viewModel: QuickBiteViewModel,
    strings: AppStrings,
    onBack: () -> Unit,
    onGoToHome: () -> Unit
) {
    BackHandler { onBack() }

    val orderFlow = remember(orderId) { viewModel.orderRepo.getOrder(orderId) }
    val orderState by orderFlow.collectAsState(initial = null)

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var userRating by remember { mutableIntStateOf(0) }
    var ratingSubmitted by remember { mutableStateOf(false) }
    var lottieAnimKey by remember { mutableIntStateOf(0) }

    // Lottie Animation setup for 'DELIVERED' status
    val lottieComposition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.order_delivered_celebration)
    )

    val isDelivered = orderState?.status == "DELIVERED"

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
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.testTag("tracking_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                        Text(
                            text = strings.trackLiveOrder,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(
                        onClick = onGoToHome,
                        modifier = Modifier.testTag("tracking_home_button")
                    ) {
                        Icon(Icons.Default.Home, contentDescription = "Home", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (isDelivered) {
                        // When delivered: Options to Replay Animation or Return Home
                        OutlinedButton(
                            onClick = {
                                lottieAnimKey++
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        if (strings.isBn) "উদযাপন অ্যানিমেশন আবার শুরু হচ্ছে 🎉" else "Replaying celebration animation 🎉"
                                    )
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).height(50.dp)
                        ) {
                            Icon(Icons.Default.Replay, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = if (strings.isBn) "আবার দেখুন" else "Replay", fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = onGoToHome,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = FreshGreen),
                            modifier = Modifier.weight(1f).height(50.dp)
                        ) {
                            Icon(Icons.Default.Home, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = if (strings.isBn) "হোমে যান" else "Go to Home", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        // Advance Status Simulation Button when not yet delivered
                        Button(
                            onClick = {
                                val current = orderState?.status ?: "PLACED"
                                viewModel.advanceOrderTracking(orderId, current)
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        if (strings.isBn) "ট্র্যাকিং স্ট্যাটাস আপডেট হয়েছে!" else "Tracking stage updated!"
                                    )
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("advance_tracking_button")
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = strings.progressStepSimulation,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        val order = orderState
        if (order == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "অর্ডার লোড হচ্ছে...", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            val statusIndex = when (order.status) {
                "PLACED" -> 0
                "PREPARING" -> 1
                "ON_THE_WAY" -> 2
                "DELIVERED" -> 3
                else -> 0
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .testTag("order_tracking_content"),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Celebration Lottie Animation Card when status is DELIVERED
                if (statusIndex == 3) {
                    item {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("lottie_delivered_celebration_card")
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Lottie Animation Widget
                                Box(
                                    modifier = Modifier
                                        .size(180.dp)
                                        .clip(CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    androidx.compose.runtime.key(lottieAnimKey) {
                                        val lottieProgress by animateLottieCompositionAsState(
                                            composition = lottieComposition,
                                            iterations = 1,
                                            restartOnPlay = true
                                        )
                                        LottieAnimation(
                                            composition = lottieComposition,
                                            progress = { lottieProgress },
                                            modifier = Modifier.size(180.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Surface(
                                    color = FreshGreen.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(20.dp)
                                ) {
                                    Text(
                                        text = if (strings.isBn) "✨ ডেলিভারি সফল ✨" else "✨ Successfully Delivered ✨",
                                        color = FreshGreen,
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = if (strings.isBn) "খাবার পৌঁছে গেছে! উপভোগ করুন 🎉" else "Your Meal Has Arrived! Enjoy 🎉",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "${strings.orderIdLabel}: #${order.orderId} • ${if (strings.isBn) order.restaurantNameBn else order.restaurantNameEn}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(16.dp))
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                                Spacer(modifier = Modifier.height(16.dp))

                                // Interactive Rating Prompt for High Engagement
                                Text(
                                    text = if (strings.isBn) "কেমন ছিল আপনার খাবার? রেটিং দিন" else "How was your food? Rate your experience",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    (1..5).forEach { star ->
                                        IconButton(
                                            onClick = {
                                                userRating = star
                                                ratingSubmitted = true
                                                scope.launch {
                                                    snackbarHostState.showSnackbar(
                                                        if (strings.isBn) "ধন্যবাদ! আপনার $star★ রেটিং সংরক্ষিত হয়েছে ⭐" else "Thank you! Your $star-star rating was recorded ⭐"
                                                    )
                                                }
                                            },
                                            modifier = Modifier.size(40.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (star <= userRating) Icons.Default.Star else Icons.Outlined.StarOutline,
                                                contentDescription = "Star $star",
                                                tint = GoldStar,
                                                modifier = Modifier.size(32.dp)
                                            )
                                        }
                                    }
                                }

                                if (ratingSubmitted) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = if (strings.isBn) "রেটিং দেওয়ার জন্য আপনাকে ধন্যবাদ! ❤️" else "Thank you for sharing your feedback! ❤️",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = FreshGreen
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // Regular in-progress header card
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(18.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeliveryDining,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(44.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = strings.orderPlacedSuccess,
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${strings.orderIdLabel}: #${order.orderId}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.9f)
                                )

                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    color = Color.White.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(20.dp)
                                ) {
                                    Text(
                                        text = "${strings.estimatedArrival}: ${if (strings.isBn) "২৫-৩৫ মিনিট" else "25-35 mins"}",
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Interactive Progress Timeline
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            TrackingStepRow(
                                title = strings.orderStatusPlaced,
                                desc = strings.orderStatusPlacedDesc,
                                isCompleted = statusIndex >= 0,
                                isCurrent = statusIndex == 0,
                                isLast = false,
                                icon = Icons.Default.ReceiptLong
                            )
                            TrackingStepRow(
                                title = strings.orderStatusPreparing,
                                desc = strings.orderStatusPreparingDesc,
                                isCompleted = statusIndex >= 1,
                                isCurrent = statusIndex == 1,
                                isLast = false,
                                icon = Icons.Default.Fastfood
                            )
                            TrackingStepRow(
                                title = strings.orderStatusOnTheWay,
                                desc = strings.orderStatusOnTheWayDesc,
                                isCompleted = statusIndex >= 2,
                                isCurrent = statusIndex == 2,
                                isLast = false,
                                icon = Icons.Default.DeliveryDining
                            )
                            TrackingStepRow(
                                title = strings.orderStatusDelivered,
                                desc = strings.orderStatusDeliveredDesc,
                                isCompleted = statusIndex >= 3,
                                isCurrent = statusIndex == 3,
                                isLast = true,
                                icon = Icons.Default.CheckCircle
                            )
                        }
                    }
                }

                // Delivery Hero card
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = CircleShape,
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.DeliveryDining,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = strings.riderNameDemo,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = strings.riderBikeDemo,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            // Call button
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            if (strings.isBn) "রাইডারকে কল করা হচ্ছে (সিমুলেশন)..." else "Calling rider (Demo simulation)..."
                                        )
                                    }
                                },
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(FreshGreen)
                                    .testTag("call_rider_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = strings.callRider,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                // Order summary details card
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = strings.orderSummary,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = if (strings.isBn) order.restaurantNameBn else order.restaurantNameEn,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (strings.isBn) order.itemsSummaryBn else order.itemsSummaryEn,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = strings.grandTotal,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = BengaliNumberFormatter.formatCurrency(order.grandTotal, strings.isBn),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TrackingStepRow(
    title: String,
    desc: String,
    isCompleted: Boolean,
    isCurrent: Boolean,
    isLast: Boolean,
    icon: ImageVector
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                shape = CircleShape,
                color = when {
                    isCompleted -> FreshGreen
                    isCurrent -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.surfaceVariant
                },
                modifier = Modifier.size(32.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isCompleted) Icons.Default.Check else icon,
                        contentDescription = null,
                        tint = if (isCompleted || isCurrent) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(38.dp)
                        .background(
                            if (isCompleted) FreshGreen else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        )
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.padding(bottom = if (isLast) 0.dp else 16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = if (isCurrent || isCompleted) FontWeight.Bold else FontWeight.Medium
                ),
                color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
