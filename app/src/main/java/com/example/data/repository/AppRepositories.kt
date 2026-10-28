package com.example.data.repository

import com.example.data.local.dao.AddressDao
import com.example.data.local.dao.CartDao
import com.example.data.local.dao.FavoriteDao
import com.example.data.local.dao.OrderDao
import com.example.data.local.entity.CartItemEntity
import com.example.data.local.entity.FavoriteEntity
import com.example.data.local.entity.OrderEntity
import com.example.data.local.entity.SavedAddressEntity
import com.example.data.model.FoodAddOn
import com.example.data.model.FoodItem
import com.example.data.model.FoodSize
import com.example.data.model.Restaurant
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class CartRepository(private val cartDao: CartDao) {

    val cartItems: Flow<List<CartItemEntity>> = cartDao.getAllCartItems()

    suspend fun addToCart(
        food: FoodItem,
        restaurant: Restaurant,
        quantity: Int,
        selectedSize: FoodSize?,
        selectedAddOns: List<FoodAddOn>,
        specialInstruction: String
    ): Long {
        val sizeDiff = selectedSize?.priceDiff ?: 0
        val baseWithDiff = food.price + sizeDiff
        val addOnsSum = selectedAddOns.sumOf { it.price }
        val unitPrice = baseWithDiff + addOnsSum

        val sizeText = selectedSize?.nameBn ?: "রেগুলার"
        val addOnsBn = selectedAddOns.joinToString(", ") { "${it.nameBn} (+৳${it.price})" }
        val addOnsEn = selectedAddOns.joinToString(", ") { "${it.nameEn} (+৳${it.price})" }

        val entity = CartItemEntity(
            foodId = food.id,
            restaurantId = restaurant.id,
            restaurantNameBn = restaurant.nameBn,
            restaurantNameEn = restaurant.nameEn,
            foodNameBn = food.nameBn,
            foodNameEn = food.nameEn,
            basePrice = food.price,
            addOnsPrice = addOnsSum,
            unitPrice = unitPrice,
            quantity = quantity,
            selectedSize = sizeText,
            addOnsSummaryBn = addOnsBn,
            addOnsSummaryEn = addOnsEn,
            specialInstruction = specialInstruction,
            imageUrl = food.imageUrl
        )
        return cartDao.insertItem(entity)
    }

    suspend fun updateQuantity(id: Long, newQuantity: Int) {
        if (newQuantity <= 0) {
            cartDao.deleteItemById(id)
        } else {
            cartDao.updateQuantity(id, newQuantity)
        }
    }

    suspend fun removeItem(id: Long) {
        cartDao.deleteItemById(id)
    }

    suspend fun clearCart() {
        cartDao.clearCart()
    }
}

class OrderRepository(private val orderDao: OrderDao) {

    val allOrders: Flow<List<OrderEntity>> = orderDao.getAllOrders()

    fun getOrder(orderId: String): Flow<OrderEntity?> = orderDao.getOrderById(orderId)

    suspend fun createOrder(
        cartItems: List<CartItemEntity>,
        subtotal: Int,
        deliveryFee: Int,
        discount: Int,
        grandTotal: Int,
        deliveryAddress: String,
        paymentMethod: String
    ): OrderEntity {
        val randomSuffix = Random.nextInt(10000, 99999)
        val orderId = "QB-$randomSuffix"

        val firstRestBn = cartItems.firstOrNull()?.restaurantNameBn ?: "কুইকবাইট রেস্তোরাঁ"
        val firstRestEn = cartItems.firstOrNull()?.restaurantNameEn ?: "QuickBite Restaurant"

        val summaryBn = cartItems.joinToString(", ") { "${it.foodNameBn} x${it.quantity}" }
        val summaryEn = cartItems.joinToString(", ") { "${it.foodNameEn} x${it.quantity}" }

        val order = OrderEntity(
            orderId = orderId,
            timestamp = System.currentTimeMillis(),
            restaurantNameBn = firstRestBn,
            restaurantNameEn = firstRestEn,
            itemCount = cartItems.sumOf { it.quantity },
            itemsSummaryBn = summaryBn,
            itemsSummaryEn = summaryEn,
            subtotal = subtotal,
            deliveryFee = deliveryFee,
            discount = discount,
            grandTotal = grandTotal,
            deliveryAddress = deliveryAddress,
            paymentMethod = paymentMethod,
            status = "PLACED"
        )
        orderDao.insertOrder(order)
        return order
    }

    suspend fun advanceOrderStatus(orderId: String, currentStatus: String): String {
        val nextStatus = when (currentStatus) {
            "PLACED" -> "PREPARING"
            "PREPARING" -> "ON_THE_WAY"
            "ON_THE_WAY" -> "DELIVERED"
            else -> "DELIVERED"
        }
        orderDao.updateOrderStatus(orderId, nextStatus)
        return nextStatus
    }
}

class FavoritesRepository(private val favoriteDao: FavoriteDao) {

    val allFavorites: Flow<List<FavoriteEntity>> = favoriteDao.getAllFavorites()

    fun isFavorite(id: String): Flow<Boolean> = favoriteDao.isFavorite(id)

    suspend fun toggleRestaurantFavorite(restaurant: Restaurant, isCurrentlyFav: Boolean) {
        if (isCurrentlyFav) {
            favoriteDao.deleteFavorite(restaurant.id)
        } else {
            favoriteDao.insertFavorite(
                FavoriteEntity(
                    favoriteId = restaurant.id,
                    itemType = "RESTAURANT",
                    nameBn = restaurant.nameBn,
                    nameEn = restaurant.nameEn,
                    subtitleBn = restaurant.cuisineBn,
                    subtitleEn = restaurant.cuisineEn,
                    rating = restaurant.rating,
                    priceOrDelivery = "৳${restaurant.deliveryFee} ডেলিভারি",
                    imageUrl = restaurant.imageUrl
                )
            )
        }
    }

    suspend fun toggleFoodFavorite(food: FoodItem, isCurrentlyFav: Boolean) {
        if (isCurrentlyFav) {
            favoriteDao.deleteFavorite(food.id)
        } else {
            favoriteDao.insertFavorite(
                FavoriteEntity(
                    favoriteId = food.id,
                    itemType = "FOOD",
                    nameBn = food.nameBn,
                    nameEn = food.nameEn,
                    subtitleBn = "৳${food.price}",
                    subtitleEn = "৳${food.price}",
                    rating = 4.8f,
                    priceOrDelivery = "৳${food.price}",
                    imageUrl = food.imageUrl
                )
            )
        }
    }

    suspend fun removeFavorite(favoriteId: String) {
        favoriteDao.deleteFavorite(favoriteId)
    }
}

class AddressRepository(private val addressDao: AddressDao) {

    val allAddresses: Flow<List<SavedAddressEntity>> = addressDao.getAllAddresses()

    suspend fun setDefaultAddress(id: Long) {
        addressDao.resetDefaults()
        addressDao.setDefaultAddress(id)
    }

    suspend fun addAddress(
        labelBn: String,
        labelEn: String,
        detailsBn: String,
        detailsEn: String,
        areaBn: String,
        areaEn: String,
        phone: String,
        isDefault: Boolean
    ): Long {
        if (isDefault) {
            addressDao.resetDefaults()
        }
        return addressDao.insertAddress(
            SavedAddressEntity(
                labelBn = labelBn,
                labelEn = labelEn,
                addressDetailsBn = detailsBn,
                addressDetailsEn = detailsEn,
                areaBn = areaBn,
                areaEn = areaEn,
                contactPhone = phone,
                isDefault = isDefault
            )
        )
    }

    suspend fun deleteAddress(id: Long) {
        addressDao.deleteAddress(id)
    }
}
