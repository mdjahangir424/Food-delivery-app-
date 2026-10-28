package com.example

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.service.QuickBiteNotificationManager
import com.example.ui.components.DeliveryAreaBottomSheet
import com.example.ui.components.FoodDetailBottomSheet
import com.example.ui.components.LanguageSelectorDialog
import com.example.ui.components.QuickBiteBottomNav
import com.example.ui.components.QuickBiteTopBar
import com.example.ui.screens.cart.CartScreen
import com.example.ui.screens.checkout.CheckoutScreen
import com.example.ui.screens.favorites.FavoritesScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.order.OrderTrackingScreen
import com.example.ui.screens.orders.OrderHistoryScreen
import com.example.ui.screens.profile.HelpCenterScreen
import com.example.ui.screens.profile.NotificationSettingsScreen
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.profile.SavedAddressesScreen
import com.example.ui.screens.restaurant.RestaurantDetailScreen
import com.example.ui.screens.search.SearchScreen
import com.example.ui.screens.welcome.SplashScreen
import com.example.ui.screens.welcome.WelcomeScreen
import com.example.ui.theme.QuickBiteTheme
import com.example.ui.viewmodel.MainTab
import com.example.ui.viewmodel.QuickBiteViewModel
import com.example.ui.viewmodel.Screen

class MainActivity : ComponentActivity() {

    private var pendingIntent: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        pendingIntent = intent

        setContent {
            QuickBiteTheme {
                val viewModel: QuickBiteViewModel = viewModel()

                // Request POST_NOTIFICATIONS permission for Android 13+ (API 33+)
                val notificationPermissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    // Notification permission granted or declined
                }

                LaunchedEffect(Unit) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                    }

                    // Handle notification launch intent
                    handleNotificationIntent(pendingIntent, viewModel)
                    pendingIntent = null
                }

                QuickBiteApp(viewModel = viewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        // Handled via application lifecycle or direct trigger
    }

    private fun handleNotificationIntent(intent: Intent?, viewModel: QuickBiteViewModel) {
        intent ?: return
        val navScreen = intent.getStringExtra(QuickBiteNotificationManager.EXTRA_NAV_SCREEN) ?: return
        when (navScreen) {
            "ORDER_TRACKING" -> {
                val orderId = intent.getStringExtra(QuickBiteNotificationManager.EXTRA_ORDER_ID)
                if (!orderId.isNullOrEmpty()) {
                    viewModel.navigateTo(Screen.OrderTracking(orderId))
                }
            }
            "CART" -> {
                val promo = intent.getStringExtra(QuickBiteNotificationManager.EXTRA_PROMO_CODE)
                if (!promo.isNullOrEmpty()) {
                    viewModel.applyPromoCode(promo, 500)
                }
                viewModel.navigateTo(Screen.Cart)
            }
        }
    }
}

@Composable
fun QuickBiteApp(viewModel: QuickBiteViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val language by viewModel.language.collectAsStateWithLifecycle()
    val strings by viewModel.strings.collectAsStateWithLifecycle()
    val selectedArea by viewModel.selectedArea.collectAsStateWithLifecycle()

    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val allOrders by viewModel.allOrders.collectAsStateWithLifecycle()
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()
    val savedAddresses by viewModel.savedAddresses.collectAsStateWithLifecycle()
    val notificationPrefs by viewModel.notificationPreferences.collectAsStateWithLifecycle()

    val appliedPromo by viewModel.appliedPromo.collectAsStateWithLifecycle()
    val promoMessage by viewModel.promoMessage.collectAsStateWithLifecycle()

    val customizingFood by viewModel.customizingFood.collectAsStateWithLifecycle()
    val showAddressPicker by viewModel.showAddressPicker.collectAsStateWithLifecycle()
    val showLanguagePicker by viewModel.showLanguagePicker.collectAsStateWithLifecycle()

    val totalCartCount = cartItems.sumOf { it.quantity }
    val activeOrdersCount = allOrders.count { it.status != "DELIVERED" }

    when (val screen = currentScreen) {
        is Screen.Splash -> {
            SplashScreen(
                strings = strings,
                onComplete = { viewModel.completeSplash() }
            )
        }

        is Screen.Welcome -> {
            WelcomeScreen(
                strings = strings,
                currentLanguage = language,
                onLanguageToggle = { viewModel.toggleLanguage() },
                onGetStarted = { viewModel.completeWelcome() }
            )
        }

        is Screen.Main -> {
            Scaffold(
                topBar = {
                    QuickBiteTopBar(
                        selectedArea = selectedArea,
                        language = language,
                        strings = strings,
                        cartItemCount = totalCartCount,
                        onAreaClick = { viewModel.setShowAddressPicker(true) },
                        onLanguageClick = { viewModel.setShowLanguagePicker(true) },
                        onCartClick = { viewModel.navigateTo(Screen.Cart) }
                    )
                },
                bottomBar = {
                    QuickBiteBottomNav(
                        currentTab = currentTab,
                        onTabSelected = { viewModel.setTab(it) },
                        strings = strings,
                        activeOrdersCount = activeOrdersCount
                    )
                },
                modifier = Modifier.fillMaxSize()
            ) { innerPadding ->
                when (currentTab) {
                    MainTab.HOME -> {
                        HomeScreen(
                            viewModel = viewModel,
                            strings = strings,
                            favorites = favorites,
                            onRestaurantClick = { restId ->
                                viewModel.navigateTo(Screen.RestaurantDetail(restId))
                            },
                            onFoodClick = { food ->
                                viewModel.openFoodCustomization(food)
                            },
                            onSearchClick = {
                                viewModel.setTab(MainTab.SEARCH)
                            },
                            onPromoClick = { banner ->
                                viewModel.applyPromoCode(banner.promoCode, 500)
                                viewModel.navigateTo(Screen.Cart)
                            },
                            modifier = Modifier.padding(innerPadding)
                        )
                    }

                    MainTab.SEARCH -> {
                        SearchScreen(
                            viewModel = viewModel,
                            strings = strings,
                            favorites = favorites,
                            onRestaurantClick = { restId ->
                                viewModel.navigateTo(Screen.RestaurantDetail(restId))
                            },
                            onFoodClick = { food ->
                                viewModel.openFoodCustomization(food)
                            },
                            modifier = Modifier.padding(innerPadding)
                        )
                    }

                    MainTab.ORDERS -> {
                        OrderHistoryScreen(
                            orders = allOrders,
                            strings = strings,
                            onTrackOrder = { orderId ->
                                viewModel.navigateTo(Screen.OrderTracking(orderId))
                            },
                            onExploreFood = {
                                viewModel.setTab(MainTab.HOME)
                            },
                            modifier = Modifier.padding(innerPadding)
                        )
                    }

                    MainTab.FAVORITES -> {
                        FavoritesScreen(
                            viewModel = viewModel,
                            favorites = favorites,
                            strings = strings,
                            onRestaurantClick = { restId ->
                                viewModel.navigateTo(Screen.RestaurantDetail(restId))
                            },
                            onExploreFood = {
                                viewModel.setTab(MainTab.HOME)
                            },
                            modifier = Modifier.padding(innerPadding)
                        )
                    }

                    MainTab.PROFILE -> {
                        ProfileScreen(
                            viewModel = viewModel,
                            strings = strings,
                            language = language,
                            onNavigateToAddresses = {
                                viewModel.navigateTo(Screen.SavedAddresses)
                            },
                            onNavigateToHelp = {
                                viewModel.navigateTo(Screen.HelpCenter)
                            },
                            onNavigateToNotifications = {
                                viewModel.navigateTo(Screen.NotificationSettings)
                            },
                            onLanguageClick = {
                                viewModel.setShowLanguagePicker(true)
                            },
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }

        is Screen.RestaurantDetail -> {
            val restaurant = viewModel.foodRepo.getRestaurantById(screen.restaurantId)
                ?: viewModel.foodRepo.restaurants.first()
            val foodItems = viewModel.foodRepo.getFoodItemsForRestaurant(restaurant.id)

            RestaurantDetailScreen(
                restaurant = restaurant,
                foodItems = foodItems,
                favorites = favorites,
                cartItems = cartItems,
                strings = strings,
                onBack = { viewModel.navigateBack() },
                onFoodClick = { food -> viewModel.openFoodCustomization(food) },
                onFavoriteToggle = {
                    val isFav = favorites.any { it.favoriteId == restaurant.id }
                    viewModel.toggleRestaurantFavorite(restaurant, isFav)
                },
                onViewCart = { viewModel.navigateTo(Screen.Cart) }
            )
        }

        is Screen.Cart -> {
            CartScreen(
                viewModel = viewModel,
                cartItems = cartItems,
                appliedPromo = appliedPromo,
                promoMessage = promoMessage,
                strings = strings,
                onBack = { viewModel.navigateBack() },
                onProceedToCheckout = { viewModel.navigateTo(Screen.Checkout) },
                onExploreFood = {
                    viewModel.navigateBack()
                    viewModel.setTab(MainTab.HOME)
                }
            )
        }

        is Screen.Checkout -> {
            CheckoutScreen(
                viewModel = viewModel,
                cartItems = cartItems,
                appliedPromo = appliedPromo,
                selectedArea = selectedArea,
                strings = strings,
                onBack = { viewModel.navigateBack() },
                onOrderPlaced = { order ->
                    viewModel.navigateTo(Screen.OrderTracking(order.orderId))
                }
            )
        }

        is Screen.OrderTracking -> {
            OrderTrackingScreen(
                orderId = screen.orderId,
                viewModel = viewModel,
                strings = strings,
                onBack = { viewModel.navigateBack() },
                onGoToHome = {
                    viewModel.setTab(MainTab.HOME)
                }
            )
        }

        is Screen.SavedAddresses -> {
            SavedAddressesScreen(
                addresses = savedAddresses,
                viewModel = viewModel,
                strings = strings,
                onBack = { viewModel.navigateBack() }
            )
        }

        is Screen.HelpCenter -> {
            HelpCenterScreen(
                strings = strings,
                onBack = { viewModel.navigateBack() }
            )
        }

        is Screen.NotificationSettings -> {
            NotificationSettingsScreen(
                viewModel = viewModel,
                notificationPrefs = notificationPrefs,
                strings = strings,
                onBack = { viewModel.navigateBack() }
            )
        }
    }

    // Modal BottomSheet for Delivery Area Picker
    if (showAddressPicker) {
        DeliveryAreaBottomSheet(
            areas = viewModel.foodRepo.dhakaAreas,
            selectedArea = selectedArea,
            strings = strings,
            onAreaSelected = { viewModel.selectDeliveryArea(it) },
            onDismiss = { viewModel.setShowAddressPicker(false) }
        )
    }

    // Modal Dialog for Language Switcher
    if (showLanguagePicker) {
        LanguageSelectorDialog(
            currentLanguage = language,
            strings = strings,
            onLanguageSelected = { viewModel.setLanguage(it) },
            onDismiss = { viewModel.setShowLanguagePicker(false) }
        )
    }

    // Modal BottomSheet for Food Customization
    customizingFood?.let { food ->
        val restaurant = viewModel.foodRepo.getRestaurantById(food.restaurantId)
            ?: viewModel.foodRepo.restaurants.first()

        FoodDetailBottomSheet(
            food = food,
            restaurant = restaurant,
            strings = strings,
            onAddToCart = { qty, size, addOns, note ->
                viewModel.addToCart(
                    food = food,
                    restaurant = restaurant,
                    quantity = qty,
                    selectedSize = size,
                    selectedAddOns = addOns,
                    specialInstruction = note
                )
            },
            onDismiss = { viewModel.closeFoodCustomization() }
        )
    }
}
