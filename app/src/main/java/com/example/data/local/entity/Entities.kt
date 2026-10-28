package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val foodId: String,
    val restaurantId: String,
    val restaurantNameBn: String,
    val restaurantNameEn: String,
    val foodNameBn: String,
    val foodNameEn: String,
    val basePrice: Int,
    val addOnsPrice: Int,
    val unitPrice: Int,
    val quantity: Int,
    val selectedSize: String,
    val addOnsSummaryBn: String,
    val addOnsSummaryEn: String,
    val specialInstruction: String,
    val imageUrl: String
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey
    val orderId: String,
    val timestamp: Long,
    val restaurantNameBn: String,
    val restaurantNameEn: String,
    val itemCount: Int,
    val itemsSummaryBn: String,
    val itemsSummaryEn: String,
    val subtotal: Int,
    val deliveryFee: Int,
    val discount: Int,
    val grandTotal: Int,
    val deliveryAddress: String,
    val paymentMethod: String,
    val status: String // "PLACED", "PREPARING", "ON_THE_WAY", "DELIVERED"
)

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey
    val favoriteId: String, // e.g., "rest_1" or "food_101"
    val itemType: String, // "RESTAURANT" or "FOOD"
    val nameBn: String,
    val nameEn: String,
    val subtitleBn: String,
    val subtitleEn: String,
    val rating: Float,
    val priceOrDelivery: String,
    val imageUrl: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "saved_addresses")
data class SavedAddressEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val labelBn: String, // e.g. "বাসা", "অফিস"
    val labelEn: String, // e.g. "Home", "Office"
    val addressDetailsBn: String,
    val addressDetailsEn: String,
    val areaBn: String, // e.g. "ধানমন্ডি", "গুলশান-২"
    val areaEn: String,
    val contactPhone: String,
    val isDefault: Boolean
)
