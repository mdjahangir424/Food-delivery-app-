package com.example.data.model

data class DhakaArea(
    val id: String,
    val nameBn: String,
    val nameEn: String
)

data class FoodCategory(
    val id: String,
    val nameBn: String,
    val nameEn: String,
    val emoji: String,
    val imageUrl: String
)

data class FoodSize(
    val id: String,
    val nameBn: String,
    val nameEn: String,
    val priceDiff: Int
)

data class FoodAddOn(
    val id: String,
    val nameBn: String,
    val nameEn: String,
    val price: Int
)

data class FoodItem(
    val id: String,
    val restaurantId: String,
    val nameBn: String,
    val nameEn: String,
    val descriptionBn: String,
    val descriptionEn: String,
    val price: Int,
    val originalPrice: Int? = null,
    val categoryId: String,
    val isVeg: Boolean = false,
    val isBestseller: Boolean = false,
    val isPopular: Boolean = true,
    val imageUrl: String,
    val prepTimeMin: Int = 20,
    val calories: Int? = null,
    val sizes: List<FoodSize> = emptyList(),
    val addOns: List<FoodAddOn> = emptyList()
)

data class Restaurant(
    val id: String,
    val nameBn: String,
    val nameEn: String,
    val descriptionBn: String,
    val descriptionEn: String,
    val cuisineBn: String,
    val cuisineEn: String,
    val rating: Float,
    val reviewCount: Int,
    val deliveryTimeMin: Int,
    val deliveryFee: Int,
    val minOrder: Int,
    val areaBn: String,
    val areaEn: String,
    val imageUrl: String,
    val coverUrl: String,
    val discountTextBn: String? = null,
    val discountTextEn: String? = null,
    val isHalal: Boolean = true,
    val isFeatured: Boolean = false,
    val tags: List<String> = emptyList()
)

data class PromoBanner(
    val id: String,
    val titleBn: String,
    val titleEn: String,
    val subtitleBn: String,
    val subtitleEn: String,
    val promoCode: String,
    val badgeBn: String,
    val badgeEn: String,
    val colorHex: Long
)

data class PromoCode(
    val code: String,
    val discountFlat: Int = 0,
    val discountPercent: Int = 0,
    val minSpend: Int = 0,
    val isFreeDelivery: Boolean = false,
    val descriptionBn: String,
    val descriptionEn: String
)
