package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.QuickBiteDatabase
import com.example.data.local.entity.CartItemEntity
import com.example.data.local.entity.FavoriteEntity
import com.example.data.local.entity.OrderEntity
import com.example.data.local.entity.SavedAddressEntity
import com.example.data.model.DhakaArea
import com.example.data.model.FoodAddOn
import com.example.data.model.FoodItem
import com.example.data.model.FoodSize
import com.example.data.model.PromoCode
import com.example.data.model.Restaurant
import com.example.data.preferences.NotificationPreferences
import com.example.data.preferences.UserPreferencesRepository
import com.example.data.repository.AddressRepository
import com.example.data.repository.CartRepository
import com.example.data.repository.FavoritesRepository
import com.example.data.repository.FoodRepository
import com.example.data.repository.OrderRepository
import com.example.service.QuickBiteNotificationManager
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.AppStrings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class Screen {
    data object Splash : Screen()
    data object Welcome : Screen()
    data object Main : Screen()
    data class RestaurantDetail(val restaurantId: String) : Screen()
    data object Cart : Screen()
    data object Checkout : Screen()
    data class OrderTracking(val orderId: String) : Screen()
    data object SavedAddresses : Screen()
    data object HelpCenter : Screen()
    data object NotificationSettings : Screen()
}

enum class MainTab {
    HOME, SEARCH, ORDERS, FAVORITES, PROFILE
}

class QuickBiteViewModel(application: Application) : AndroidViewModel(application) {

    private val database = QuickBiteDatabase.getDatabase(application, viewModelScope)
    private val preferencesRepo = UserPreferencesRepository(application)
    val foodRepo = FoodRepository()
    val cartRepo = CartRepository(database.cartDao())
    val orderRepo = OrderRepository(database.orderDao())
    val favRepo = FavoritesRepository(database.favoriteDao())
    val addressRepo = AddressRepository(database.addressDao())

    // Navigation Stack
    private val _navigationStack = MutableStateFlow<List<Screen>>(listOf(Screen.Splash))
    val currentScreen: StateFlow<Screen> = _navigationStack.combine(MutableStateFlow(Unit)) { stack, _ ->
        stack.lastOrNull() ?: Screen.Main
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Screen.Splash)

    private val _currentTab = MutableStateFlow(MainTab.HOME)
    val currentTab: StateFlow<MainTab> = _currentTab.asStateFlow()

    // Language & Preferences
    private val _language = MutableStateFlow(AppLanguage.BANGLA)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    val strings: StateFlow<AppStrings> = _language.combine(MutableStateFlow(Unit)) { lang, _ ->
        AppStrings(lang)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppStrings(AppLanguage.BANGLA))

    private val _selectedArea = MutableStateFlow(foodRepo.dhakaAreas.first())
    val selectedArea: StateFlow<DhakaArea> = _selectedArea.asStateFlow()

    // Data streams from Room
    val cartItems: StateFlow<List<CartItemEntity>> = cartRepo.cartItems.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val allOrders: StateFlow<List<OrderEntity>> = orderRepo.allOrders.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val favorites: StateFlow<List<FavoriteEntity>> = favRepo.allFavorites.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val savedAddresses: StateFlow<List<SavedAddressEntity>> = addressRepo.allAddresses.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val notificationPreferences: StateFlow<NotificationPreferences> = preferencesRepo.userPreferencesFlow
        .map { it.notifications }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NotificationPreferences())

    // Cart calculations & Promo state
    private val _appliedPromo = MutableStateFlow<PromoCode?>(null)
    val appliedPromo: StateFlow<PromoCode?> = _appliedPromo.asStateFlow()

    private val _promoMessage = MutableStateFlow<String?>(null)
    val promoMessage: StateFlow<String?> = _promoMessage.asStateFlow()

    // Search & Filter
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategoryFilter = MutableStateFlow<String?>(null)
    val selectedCategoryFilter: StateFlow<String?> = _selectedCategoryFilter.asStateFlow()

    // Modals & Sheets
    private val _customizingFood = MutableStateFlow<FoodItem?>(null)
    val customizingFood: StateFlow<FoodItem?> = _customizingFood.asStateFlow()

    private val _showAddressPicker = MutableStateFlow(false)
    val showAddressPicker: StateFlow<Boolean> = _showAddressPicker.asStateFlow()

    private val _showLanguagePicker = MutableStateFlow(false)
    val showLanguagePicker: StateFlow<Boolean> = _showLanguagePicker.asStateFlow()

    init {
        // Initialize Notification Channels and FCM
        QuickBiteNotificationManager.initNotificationChannels(application)
        QuickBiteNotificationManager.initFCM(application) { token ->
            viewModelScope.launch {
                preferencesRepo.saveFcmToken(token)
            }
        }

        // Load initial preferences from DataStore
        viewModelScope.launch {
            val prefs = preferencesRepo.userPreferencesFlow.first()
            _language.value = AppLanguage.fromCode(prefs.languageCode)
            val matchedArea = foodRepo.dhakaAreas.firstOrNull { it.nameBn == prefs.selectedAreaBn }
            if (matchedArea != null) {
                _selectedArea.value = matchedArea
            }
        }
    }

    // Navigation Actions
    fun navigateTo(screen: Screen) {
        val current = _navigationStack.value.toMutableList()
        current.add(screen)
        _navigationStack.value = current
    }

    fun navigateBack(): Boolean {
        val current = _navigationStack.value.toMutableList()
        if (current.size > 1) {
            current.removeAt(current.size - 1)
            _navigationStack.value = current
            return true
        }
        return false
    }

    fun setTab(tab: MainTab) {
        _currentTab.value = tab
        if (_navigationStack.value.lastOrNull() !is Screen.Main) {
            _navigationStack.value = listOf(Screen.Main)
        }
    }

    fun completeSplash() {
        viewModelScope.launch {
            val prefs = preferencesRepo.userPreferencesFlow.first()
            if (prefs.hasCompletedOnboarding) {
                _navigationStack.value = listOf(Screen.Main)
            } else {
                _navigationStack.value = listOf(Screen.Welcome)
            }
        }
    }

    fun completeWelcome() {
        viewModelScope.launch {
            preferencesRepo.setOnboardingCompleted()
            _navigationStack.value = listOf(Screen.Main)
        }
    }

    // Language Toggle
    fun toggleLanguage() {
        val nextLang = if (_language.value == AppLanguage.BANGLA) AppLanguage.ENGLISH else AppLanguage.BANGLA
        setLanguage(nextLang)
    }

    fun setLanguage(lang: AppLanguage) {
        _language.value = lang
        viewModelScope.launch {
            preferencesRepo.setLanguage(lang.code)
        }
    }

    // Location
    fun selectDeliveryArea(area: DhakaArea) {
        _selectedArea.value = area
        _showAddressPicker.value = false
        viewModelScope.launch {
            preferencesRepo.setDeliveryArea(area.nameBn, area.nameEn)
        }
    }

    fun setShowAddressPicker(show: Boolean) {
        _showAddressPicker.value = show
    }

    fun setShowLanguagePicker(show: Boolean) {
        _showLanguagePicker.value = show
    }

    // Search & Filter
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCategoryFilter(categoryId: String?) {
        _selectedCategoryFilter.value = if (_selectedCategoryFilter.value == categoryId) null else categoryId
    }

    // Food customization dialog
    fun openFoodCustomization(food: FoodItem) {
        _customizingFood.value = food
    }

    fun closeFoodCustomization() {
        _customizingFood.value = null
    }

    // Cart operations
    fun addToCart(
        food: FoodItem,
        restaurant: Restaurant,
        quantity: Int,
        selectedSize: FoodSize?,
        selectedAddOns: List<FoodAddOn>,
        specialInstruction: String
    ) {
        viewModelScope.launch {
            cartRepo.addToCart(
                food = food,
                restaurant = restaurant,
                quantity = quantity,
                selectedSize = selectedSize,
                selectedAddOns = selectedAddOns,
                specialInstruction = specialInstruction
            )
            closeFoodCustomization()
        }
    }

    fun updateCartItemQuantity(itemId: Long, currentQty: Int, change: Int) {
        viewModelScope.launch {
            cartRepo.updateQuantity(itemId, currentQty + change)
        }
    }

    fun removeCartItem(itemId: Long) {
        viewModelScope.launch {
            cartRepo.removeItem(itemId)
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            cartRepo.clearCart()
            _appliedPromo.value = null
            _promoMessage.value = null
        }
    }

    // Coupons
    fun applyPromoCode(codeStr: String, subtotal: Int) {
        val trimmed = codeStr.trim().uppercase()
        val promo = foodRepo.promoCodes.firstOrNull { it.code.equals(trimmed, ignoreCase = true) }
        val s = strings.value
        if (promo == null) {
            _promoMessage.value = s.promoInvalid
            return
        }
        if (subtotal < promo.minSpend) {
            val needed = promo.minSpend - subtotal
            _promoMessage.value = if (s.isBn) "নূন্যতম ৳${promo.minSpend} অর্ডারে এই কুপন প্রযোজ্য (আরও ৳$needed প্রয়োজন)" else "Requires min spend ৳${promo.minSpend} (add ৳$needed more)"
            return
        }
        _appliedPromo.value = promo
        _promoMessage.value = s.promoAppliedSuccess
    }

    fun removePromoCode() {
        _appliedPromo.value = null
        _promoMessage.value = null
    }

    // Calculations
    fun calculateSubtotal(items: List<CartItemEntity>): Int {
        return items.sumOf { it.unitPrice * it.quantity }
    }

    fun calculateDeliveryFee(items: List<CartItemEntity>, promo: PromoCode?): Int {
        if (items.isEmpty()) return 0
        if (promo?.isFreeDelivery == true) return 0
        return 40 // Dhaka standard delivery charge
    }

    fun calculatePlatformFee(items: List<CartItemEntity>): Int {
        return if (items.isNotEmpty()) 10 else 0
    }

    fun calculateDiscount(subtotal: Int, promo: PromoCode?): Int {
        if (promo == null) return 0
        if (promo.discountFlat > 0) return promo.discountFlat
        if (promo.discountPercent > 0) {
            val calculated = (subtotal * promo.discountPercent) / 100
            return minOf(calculated, 150) // capped at ৳150
        }
        return 0
    }

    fun calculateGrandTotal(items: List<CartItemEntity>, promo: PromoCode?): Int {
        val sub = calculateSubtotal(items)
        val delivery = calculateDeliveryFee(items, promo)
        val platform = calculatePlatformFee(items)
        val disc = calculateDiscount(sub, promo)
        return maxOf(0, sub + delivery + platform - disc)
    }

    // Checkout & Order Placement
    fun placeOrder(
        recipientName: String,
        phone: String,
        address: String,
        notes: String,
        paymentMethod: String,
        onSuccess: (OrderEntity) -> Unit
    ) {
        val items = cartItems.value
        if (items.isEmpty()) return

        val promo = appliedPromo.value
        val subtotal = calculateSubtotal(items)
        val delivery = calculateDeliveryFee(items, promo)
        val disc = calculateDiscount(subtotal, promo)
        val total = calculateGrandTotal(items, promo)

        val fullAddress = "$address, ${_selectedArea.value.nameBn} ($notes)"

        viewModelScope.launch {
            val order = orderRepo.createOrder(
                cartItems = items,
                subtotal = subtotal,
                deliveryFee = delivery,
                discount = disc,
                grandTotal = total,
                deliveryAddress = fullAddress,
                paymentMethod = paymentMethod
            )
            cartRepo.clearCart()
            _appliedPromo.value = null
            _promoMessage.value = null

            // Show real system push notification for order confirmation
            QuickBiteNotificationManager.showOrderConfirmationNotification(
                context = getApplication(),
                orderId = order.orderId,
                restaurantName = if (strings.value.isBn) order.restaurantNameBn else order.restaurantNameEn,
                totalAmount = order.grandTotal,
                isBn = strings.value.isBn,
                prefs = notificationPreferences.value
            )

            onSuccess(order)
        }
    }

    // Advance order tracking stage
    fun advanceOrderTracking(orderId: String, currentStatus: String) {
        viewModelScope.launch {
            val nextStatus = orderRepo.advanceOrderStatus(orderId, currentStatus)
            val order = allOrders.value.firstOrNull { it.orderId == orderId }
            val restaurantName = if (strings.value.isBn) {
                order?.restaurantNameBn ?: "কুইকবাইট রেস্তোরাঁ"
            } else {
                order?.restaurantNameEn ?: "QuickBite Restaurant"
            }

            // Show real system push notification for live delivery status
            QuickBiteNotificationManager.showDeliveryUpdateNotification(
                context = getApplication(),
                orderId = orderId,
                status = nextStatus,
                restaurantName = restaurantName,
                isBn = strings.value.isBn,
                prefs = notificationPreferences.value
            )
        }
    }

    fun updateNotificationPreferences(
        orderUpdates: Boolean? = null,
        promotions: Boolean? = null,
        deliveryAlerts: Boolean? = null,
        sound: Boolean? = null
    ) {
        viewModelScope.launch {
            preferencesRepo.updateNotificationPreference(
                orderUpdates = orderUpdates,
                promotions = promotions,
                deliveryAlerts = deliveryAlerts,
                sound = sound
            )
        }
    }

    // Favorites
    fun toggleRestaurantFavorite(restaurant: Restaurant, isFav: Boolean) {
        viewModelScope.launch {
            favRepo.toggleRestaurantFavorite(restaurant, isFav)
        }
    }

    fun toggleFoodFavorite(food: FoodItem, isFav: Boolean) {
        viewModelScope.launch {
            favRepo.toggleFoodFavorite(food, isFav)
        }
    }

    fun removeFavorite(favoriteId: String) {
        viewModelScope.launch {
            favRepo.removeFavorite(favoriteId)
        }
    }

    // Saved Addresses
    fun addNewAddress(
        labelBn: String,
        labelEn: String,
        detailsBn: String,
        detailsEn: String,
        area: DhakaArea,
        phone: String
    ) {
        viewModelScope.launch {
            addressRepo.addAddress(
                labelBn = labelBn,
                labelEn = labelEn,
                detailsBn = detailsBn,
                detailsEn = detailsEn,
                areaBn = area.nameBn,
                areaEn = area.nameEn,
                phone = phone,
                isDefault = true
            )
        }
    }

    fun deleteAddress(id: Long) {
        viewModelScope.launch {
            addressRepo.deleteAddress(id)
        }
    }
}
