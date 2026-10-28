package com.example.data.repository

import com.example.data.model.DhakaArea
import com.example.data.model.FoodAddOn
import com.example.data.model.FoodCategory
import com.example.data.model.FoodItem
import com.example.data.model.FoodSize
import com.example.data.model.PromoBanner
import com.example.data.model.PromoCode
import com.example.data.model.Restaurant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FoodRepository {

    val dhakaAreas = listOf(
        DhakaArea("dhanmondi", "ধানমন্ডি", "Dhanmondi"),
        DhakaArea("gulshan", "গুলশান-২", "Gulshan-2"),
        DhakaArea("banani", "বনানী", "Banani"),
        DhakaArea("uttara", "উত্তরা সেক্টর ৩", "Uttara Sector 3"),
        DhakaArea("mirpur", "মিরপুর-১০", "Mirpur-10"),
        DhakaArea("old_dhaka", "পুরান ঢাকা (লালবাগ)", "Old Dhaka (Lalbagh)"),
        DhakaArea("mohammadpur", "মোহাম্মদপুর", "Mohammadpur"),
        DhakaArea("bailey_road", "বেইলি রোড", "Bailey Road"),
        DhakaArea("bashundhara", "বসুন্ধরা আ/এ", "Bashundhara R/A")
    )

    val categories = listOf(
        FoodCategory(
            id = "cat_biryani",
            nameBn = "বিরিয়ানি ও পোলাও",
            nameEn = "Biryani & Polao",
            emoji = "🥘",
            imageUrl = "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=400"
        ),
        FoodCategory(
            id = "cat_burger",
            nameBn = "বার্গার ও ফাস্টফুড",
            nameEn = "Burger & Fast Food",
            emoji = "🍔",
            imageUrl = "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=400"
        ),
        FoodCategory(
            id = "cat_traditional",
            nameBn = "বাঙালি ঐতিহ্য",
            nameEn = "Traditional Bengali",
            emoji = "🍛",
            imageUrl = "https://images.unsplash.com/photo-1626777552726-4a6b54c97e46?w=400"
        ),
        FoodCategory(
            id = "cat_kebab",
            nameBn = "কাবাব ও গ্রিল",
            nameEn = "Kebab & Grill",
            emoji = "🍢",
            imageUrl = "https://images.unsplash.com/photo-1555939594-58d7cb561ad1?w=400"
        ),
        FoodCategory(
            id = "cat_pizza",
            nameBn = "পিজ্জা ও পাস্তা",
            nameEn = "Pizza & Pasta",
            emoji = "🍕",
            imageUrl = "https://images.unsplash.com/photo-1513104890138-7c749659a591?w=400"
        ),
        FoodCategory(
            id = "cat_sweets",
            nameBn = "মিষ্টি ও ডেজার্ট",
            nameEn = "Sweets & Desserts",
            emoji = "🍮",
            imageUrl = "https://images.unsplash.com/photo-1587314168485-3236d6710814?w=400"
        ),
        FoodCategory(
            id = "cat_street",
            nameBn = "ফুচকা ও চাট",
            nameEn = "Fuchka & Chaat",
            emoji = "🥟",
            imageUrl = "https://images.unsplash.com/photo-1601050690597-df0568f70950?w=400"
        ),
        FoodCategory(
            id = "cat_cafe",
            nameBn = "কফি ও বেকারি",
            nameEn = "Coffee & Bakery",
            emoji = "☕",
            imageUrl = "https://images.unsplash.com/photo-1509042239860-f550ce710b93?w=400"
        )
    )

    val promoBanners = listOf(
        PromoBanner(
            id = "promo_1",
            titleBn = "কাচ্চি ফিয়েস্তা - ২০% পর্যন্ত ছাড়!",
            titleEn = "Kacchi Fiesta - Up to 20% OFF!",
            subtitleBn = "সুলতান্স ডাইন ও গ্র্যান্ড নবাবের সেরা বিরিয়ানিতে বিশেষ ছাড়। কোড: KACCHI50",
            subtitleEn = "Special discount on Sultan's Dine & Grand Nawab. Code: KACCHI50",
            promoCode = "KACCHI50",
            badgeBn = "জনপ্রিয় অফার",
            badgeEn = "Popular Offer",
            colorHex = 0xFFE64A19
        ),
        PromoBanner(
            id = "promo_2",
            titleBn = "ফ্রি ডেলিভারি উৎসব!",
            titleEn = "Free Delivery Festival!",
            subtitleBn = "ঢাকা শহরের যেকোনো অর্ডারে ডেলিভারি চার্জ সম্পূর্ণ ফ্রি। কোড: DHAKAFREE",
            subtitleEn = "Zero delivery fee on any order anywhere in Dhaka. Code: DHAKAFREE",
            promoCode = "DHAKAFREE",
            badgeBn = "০৳ ডেলিভারি",
            badgeEn = "0৳ Delivery",
            colorHex = 0xFF1976D2
        ),
        PromoBanner(
            id = "promo_3",
            titleBn = "কুইক ৫০ টাকা ফ্ল্যাট ডিসকাউন্ট",
            titleEn = "Flat ৳50 OFF with Quick50",
            subtitleBn = "৩০০ টাকার অর্ডারে সাথে সাথেই ৫০ টাকা ছাড়। কোড: QUICK50",
            subtitleEn = "Instant ৳50 discount on minimum ৳300 order. Code: QUICK50",
            promoCode = "QUICK50",
            badgeBn = "৫০৳ অফ",
            badgeEn = "৳50 OFF",
            colorHex = 0xFF2E7D32
        )
    )

    val restaurants = listOf(
        Restaurant(
            id = "rest_1",
            nameBn = "গ্র্যান্ড নবাব কাচ্চি",
            nameEn = "Grand Nawab Kacchi",
            descriptionBn = "ঐতিহ্যবাহী পুরান ঢাকার সুস্বাদু বাসমতী কাচ্চি ও খাঁটি বোরহানি।",
            descriptionEn = "Authentic Old Dhaka aromatic basmati mutton kacchi and fresh borhani.",
            cuisineBn = "কাচ্চি বিরিয়ানি • মুঘলাই",
            cuisineEn = "Kacchi Biryani • Mughlai",
            rating = 4.9f,
            reviewCount = 2840,
            deliveryTimeMin = 25,
            deliveryFee = 40,
            minOrder = 200,
            areaBn = "ধানমন্ডি ৭/এ",
            areaEn = "Dhanmondi 7/A",
            imageUrl = "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=800",
            coverUrl = "https://images.unsplash.com/photo-1544025162-d76694265947?w=1200",
            discountTextBn = "২০% ছাড় (KACCHI50)",
            discountTextEn = "20% OFF (KACCHI50)",
            isHalal = true,
            isFeatured = true,
            tags = listOf("Biryani", "Basmati", "Mutton", "Halal")
        ),
        Restaurant(
            id = "rest_2",
            nameBn = "সুলতান্স ডাইন",
            nameEn = "Sultan's Dine",
            descriptionBn = "ঢাকাবাসীর প্রিয় কাচ্চি বিরিয়ানি প্ল্যাটার, জালি কাবাব ও জর্দা।",
            descriptionEn = "Dhaka's premier Kacchi feast with Jali Kebab, Borhani and Shahi Jorda.",
            cuisineBn = "শাহী বিরিয়ানি • কাচ্চি",
            cuisineEn = "Shahi Biryani • Kacchi Feast",
            rating = 4.8f,
            reviewCount = 5120,
            deliveryTimeMin = 30,
            deliveryFee = 45,
            minOrder = 300,
            areaBn = "গুলশান-২ ও ধানমন্ডি",
            areaEn = "Gulshan-2 & Dhanmondi",
            imageUrl = "https://images.unsplash.com/photo-1589302168068-964664d93dc0?w=800",
            coverUrl = "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=1200",
            discountTextBn = "ফ্রি বোরহানি অফার",
            discountTextEn = "Free Borhani Offer",
            isHalal = true,
            isFeatured = true,
            tags = listOf("Kacchi", "Platter", "Celebration")
        ),
        Restaurant(
            id = "rest_3",
            nameBn = "স্টার কাবাব ও রেস্তোরাঁ",
            nameEn = "Star Kabab & Restaurant",
            descriptionBn = "বিখ্যাত মাটন কাবাব, স্পেশাল চিকেন রোস্ট ও ঘি মাখানো লুচি।",
            descriptionEn = "Legendary Mutton Kebab, rich Chicken Roast and fresh crispy Luchi.",
            cuisineBn = "কাবাব • বাঙালি খাবার",
            cuisineEn = "Kebabs • Bengali Traditional",
            rating = 4.7f,
            reviewCount = 4390,
            deliveryTimeMin = 20,
            deliveryFee = 35,
            minOrder = 150,
            areaBn = "বনানী ১১",
            areaEn = "Banani 11",
            imageUrl = "https://images.unsplash.com/photo-1555939594-58d7cb561ad1?w=800",
            coverUrl = "https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=1200",
            discountTextBn = "১০% ছাড়",
            discountTextEn = "10% OFF",
            isHalal = true,
            isFeatured = true,
            tags = listOf("Kebab", "Roast", "Classic")
        ),
        Restaurant(
            id = "rest_4",
            nameBn = "টেকআউট বার্গার",
            nameEn = "Takeout Burger",
            descriptionBn = "জুসি বিফ প্যাটি, চিজ বাবল এবং স্পেশাল স্মোকি সস বার্গার।",
            descriptionEn = "Juicy beef gourmet patties, molten cheese and secret smoky sauce.",
            cuisineBn = "বার্গার • আমেরিকান ফাস্টফুড",
            cuisineEn = "Gourmet Burgers • Fast Food",
            rating = 4.8f,
            reviewCount = 3800,
            deliveryTimeMin = 25,
            deliveryFee = 40,
            minOrder = 200,
            areaBn = "ধানমন্ডি ২",
            areaEn = "Dhanmondi 2",
            imageUrl = "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=800",
            coverUrl = "https://images.unsplash.com/photo-1550547660-d9450f859349?w=1200",
            discountTextBn = "৫০৳ অফ (QUICK50)",
            discountTextEn = "৳50 OFF (QUICK50)",
            isHalal = true,
            isFeatured = true,
            tags = listOf("Burger", "Beef", "Cheesy")
        ),
        Restaurant(
            id = "rest_5",
            nameBn = "চিলক্স বার্গার",
            nameEn = "Chillox Burger",
            descriptionBn = "জনপ্রিয় নাগা স্পাইসি চিকেন ও বিফ বার্গারের আসল ঠিকানা।",
            descriptionEn = "Home of blazing hot Naga spicy chicken burgers and loaded fries.",
            cuisineBn = "স্পাইসি বার্গার • ক্রিস্পি ফ্রাইস",
            cuisineEn = "Spicy Burgers • Loaded Fries",
            rating = 4.6f,
            reviewCount = 3120,
            deliveryTimeMin = 20,
            deliveryFee = 35,
            minOrder = 180,
            areaBn = "মিরপুর ও উত্তরা",
            areaEn = "Mirpur & Uttara",
            imageUrl = "https://images.unsplash.com/photo-1586190848861-99aa4a171e90?w=800",
            coverUrl = "https://images.unsplash.com/photo-1561758033-d89a9ad46330?w=1200",
            discountTextBn = "১৫% ছাড়",
            discountTextEn = "15% OFF",
            isHalal = true,
            isFeatured = false,
            tags = listOf("Naga", "Spicy", "Crispy")
        ),
        Restaurant(
            id = "rest_6",
            nameBn = "কস্তুরী বাঙালি ভোজ",
            nameEn = "Kasturi Bengali Cuisine",
            descriptionBn = "খাঁটি সর্ষে ইলিশ, রূপচাঁদা ফ্রাই এবং ১২ পদের ভর্তা স্পেশাল।",
            descriptionEn = "Authentic Shorshe Ilish, Rupchanda fry and 12 varieties of Vorta.",
            cuisineBn = "ঐতিহ্যবাহী বাঙালি মাছ ও ভর্তা",
            cuisineEn = "Authentic Bengali Fish & Vorta",
            rating = 4.7f,
            reviewCount = 2100,
            deliveryTimeMin = 35,
            deliveryFee = 45,
            minOrder = 250,
            areaBn = "পল্টন ও গুলশান",
            areaEn = "Paltan & Gulshan",
            imageUrl = "https://images.unsplash.com/photo-1626777552726-4a6b54c97e46?w=800",
            coverUrl = "https://images.unsplash.com/photo-1610057099443-fde8c4d50f91?w=1200",
            discountTextBn = "ফ্রি ডেলিভারি",
            discountTextEn = "Free Delivery",
            isHalal = true,
            isFeatured = false,
            tags = listOf("Ilish", "Vorta", "Fish", "Rice")
        ),
        Restaurant(
            id = "rest_7",
            nameBn = "বিসমিল্লাহ কাবাব ঘর",
            nameEn = "Bismillah Kabab Ghor",
            descriptionBn = "নাজিরা বাজারের বিখ্যাত বিফ চাপ, বটি কাবাব ও গরম মোগলাই পরোটা।",
            descriptionEn = "Nazira Bazar's iconic Beef Chaap, Boti Kebab and fresh crunchy Paratha.",
            cuisineBn = "পুরান ঢাকা কাবাব • চাপ",
            cuisineEn = "Old Dhaka Chaap • Kebab",
            rating = 4.8f,
            reviewCount = 1890,
            deliveryTimeMin = 30,
            deliveryFee = 40,
            minOrder = 180,
            areaBn = "নাজিরা বাজার, পুরান ঢাকা",
            areaEn = "Nazira Bazar, Old Dhaka",
            imageUrl = "https://images.unsplash.com/photo-1544025162-d76694265947?w=800",
            coverUrl = "https://images.unsplash.com/photo-1529193591184-b1d58069ecdd?w=1200",
            discountTextBn = "অরিজিনাল স্বাদ",
            discountTextEn = "Original Taste",
            isHalal = true,
            isFeatured = false,
            tags = listOf("Beef Chaap", "Paratha", "Old Dhaka")
        ),
        Restaurant(
            id = "rest_8",
            nameBn = "রস মিষ্টান্ন ভাণ্ডার",
            nameEn = "Rosh Sweets & Bakery",
            descriptionBn = "খাঁটি ছানার রসমালাই, বগুড়ার মিষ্টি দই ও স্পেশাল চমচম।",
            descriptionEn = "Pure creamy Roshomalai, Bogura's Mishti Doi and traditional Chomchom.",
            cuisineBn = "বাঙালি মিষ্টি • ডেজার্ট",
            cuisineEn = "Bengali Sweets • Traditional Desserts",
            rating = 4.9f,
            reviewCount = 3450,
            deliveryTimeMin = 18,
            deliveryFee = 30,
            minOrder = 150,
            areaBn = "বেইলি রোড ও ধানমন্ডি",
            areaEn = "Bailey Road & Dhanmondi",
            imageUrl = "https://images.unsplash.com/photo-1587314168485-3236d6710814?w=800",
            coverUrl = "https://images.unsplash.com/photo-1579372786545-d24232daf58c?w=1200",
            discountTextBn = "মিষ্টি উৎসব",
            discountTextEn = "Sweet Fest",
            isHalal = true,
            isFeatured = true,
            tags = listOf("Sweets", "Roshomalai", "Doi")
        )
    )

    val foodItems = listOf(
        // Grand Nawab Kacchi Foods
        FoodItem(
            id = "food_101",
            restaurantId = "rest_1",
            nameBn = "বাসমতী মাটন কাচ্চি বিরিয়ানি",
            nameEn = "Basmati Mutton Kacchi Biryani",
            descriptionBn = "সুগন্ধি বাসমতী চাল, নরম রসালো মাটন, সোনালী আলু এবং ডিম দিয়ে তৈরি রাজকীয় কাচ্চি।",
            descriptionEn = "Fragrant long-grain basmati, tender succulent mutton chunks, golden potato and egg.",
            price = 450,
            originalPrice = 490,
            categoryId = "cat_biryani",
            isVeg = false,
            isBestseller = true,
            isPopular = true,
            imageUrl = "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=800",
            prepTimeMin = 20,
            calories = 780,
            sizes = listOf(
                FoodSize("sz_half", "হাফ প্লেট (১ পিস মাটন)", "Half Plate (1 Pc Mutton)", -80),
                FoodSize("sz_reg", "রেগুলar প্লেট (২ পিস মাটন)", "Regular Plate (2 Pcs Mutton)", 0),
                FoodSize("sz_full", "ফুল প্লেট (৩ পিস মাটন)", "Full Plate (3 Pcs Mutton)", 150)
            ),
            addOns = listOf(
                FoodAddOn("ao_borhani", "স্পেশাল বোরহানি (২৫০ মিলি)", "Special Borhani (250ml)", 60),
                FoodAddOn("ao_jali", "মাটন জালি কাবাব (১ পিস)", "Mutton Jali Kebab (1 pc)", 70),
                FoodAddOn("ao_alooo", "এক্সট্রা কাচ্চি আলু (১ পিস)", "Extra Kacchi Potato (1 pc)", 30),
                FoodAddOn("ao_jorda", "শাহী জর্দা", "Shahi Jorda", 60)
            )
        ),
        FoodItem(
            id = "food_102",
            restaurantId = "rest_1",
            nameBn = "ঐতিহ্যবাহী বোরহানি (বড় বোতল)",
            nameEn = "Traditional Borhani (500ml)",
            descriptionBn = "টক দই, পুদিনা, ধনেপাতা এবং গোপন মশলায় তৈরি হজমকারক সুস্বাদু পানীয়।",
            descriptionEn = "Traditional digestive yogurt beverage with fresh mint, coriander and special spices.",
            price = 120,
            originalPrice = 140,
            categoryId = "cat_biryani",
            isVeg = true,
            isBestseller = false,
            isPopular = true,
            imageUrl = "https://images.unsplash.com/photo-1540420773420-3366772f4999?w=800",
            prepTimeMin = 5,
            calories = 140
        ),
        FoodItem(
            id = "food_103",
            restaurantId = "rest_1",
            nameBn = "চিকেন মোসলেমহ বিরিয়ানি কম্বো",
            nameEn = "Chicken Roast Polao Combo",
            descriptionBn = "ঘিয়ে ভাজা সুবাসিত চিনিগুঁড়া পোলাও, মিষ্টি ঝাল চিকেন রোস্ট ও ডিম।",
            descriptionEn = "Aromatic Chinigura rice polao sauteed in pure ghee with rich Shahi Chicken Roast & boiled egg.",
            price = 320,
            originalPrice = 360,
            categoryId = "cat_biryani",
            isVeg = false,
            isBestseller = true,
            isPopular = true,
            imageUrl = "https://images.unsplash.com/photo-1589302168068-964664d93dc0?w=800",
            prepTimeMin = 15,
            calories = 650,
            sizes = listOf(
                FoodSize("sz_std", "স্ট্যান্ডার্ড প্লেট", "Standard Plate", 0),
                FoodSize("sz_combo", "মেগা কম্বো (বোরহানি সহ)", "Mega Combo (+Borhani)", 70)
            ),
            addOns = listOf(
                FoodAddOn("ao_egg", "অতিরিক্ত সিদ্ধ ডিম", "Extra Boiled Egg", 25),
                FoodAddOn("ao_salad", "ফ্রেশ সালাদ ও লেবু", "Fresh Cucumber Salad", 20)
            )
        ),

        // Sultan's Dine Foods
        FoodItem(
            id = "food_201",
            restaurantId = "rest_2",
            nameBn = "সুলতান্স শাহী কাচ্চি প্ল্যাটার",
            nameEn = "Sultan's Shahi Kacchi Platter",
            descriptionBn = "কাচ্চি বিরিয়ানি, চিকেন রোস্ট, জালি কাবাব, বোরহানি এবং শাহী জর্দা সহ সম্পূর্ণ ফিস্ট।",
            descriptionEn = "Complete royal feast: Kacchi Biryani, Chicken Roast, Jali Kebab, Borhani and Shahi Jorda.",
            price = 560,
            originalPrice = 620,
            categoryId = "cat_biryani",
            isVeg = false,
            isBestseller = true,
            isPopular = true,
            imageUrl = "https://images.unsplash.com/photo-1544025162-d76694265947?w=800",
            prepTimeMin = 25,
            calories = 950,
            addOns = listOf(
                FoodAddOn("ao_kabab2", "এক্সট্রা কাবাব", "Extra Kebab", 70),
                FoodAddOn("ao_coke", "কোকাকোলা (২৫০ মিলি)", "Coca-Cola (250ml)", 35)
            )
        ),
        FoodItem(
            id = "food_202",
            restaurantId = "rest_2",
            nameBn = "বিফ তেহারি (সরিষার তেলে রান্না)",
            nameEn = "Old Dhaka Mustard Beef Tehari",
            descriptionBn = "সরিষার খাঁটি তেল, ছোট ছোট বিফ কিউব ও কাঁচামরিচে রান্না সুস্বাদু ঢাকাই তেহারি।",
            descriptionEn = "Aromatic Chinigura rice slow-cooked in pure mustard oil with succulent spiced beef cubes.",
            price = 280,
            originalPrice = 310,
            categoryId = "cat_biryani",
            isVeg = false,
            isBestseller = false,
            isPopular = true,
            imageUrl = "https://images.unsplash.com/photo-1633945274405-b6c8069047b0?w=800",
            prepTimeMin = 15,
            calories = 620,
            addOns = listOf(
                FoodAddOn("ao_borhani", "বোরহানি", "Borhani", 50),
                FoodAddOn("ao_egg", "সিদ্ধ ডিম", "Boiled Egg", 25)
            )
        ),

        // Takeout Burger Foods
        FoodItem(
            id = "food_301",
            restaurantId = "rest_4",
            nameBn = "বিফ চিজ ডিলাইট বার্গার",
            nameEn = "Beef Cheese Delight Burger",
            descriptionBn = "১৫০ গ্রাম স্ম্যাশড বিফ প্যাটি, গলানো ডাবল চেডার চিজ, ক্যারামেলাইজড অনিয়ন ও সিগনেচার সস।",
            descriptionEn = "150g fresh grilled beef patty, double molten cheddar, caramelized onions, secret sauce.",
            price = 340,
            originalPrice = 380,
            categoryId = "cat_burger",
            isVeg = false,
            isBestseller = true,
            isPopular = true,
            imageUrl = "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=800",
            prepTimeMin = 15,
            calories = 580,
            sizes = listOf(
                FoodSize("sz_single", "সিঙ্গেল প্যাটি", "Single Patty", 0),
                FoodSize("sz_double", "ডাবল প্যাটি (+১০০ গ্রাম বিফ)", "Double Patty (+100g Beef)", 120)
            ),
            addOns = listOf(
                FoodAddOn("ao_cheese", "এক্সট্রা চিজ স্লাইস", "Extra Cheddar Slice", 40),
                FoodAddOn("ao_fries", "পেরি পেরি ফ্রেঞ্চ ফ্রাইস", "Peri Peri French Fries", 90),
                FoodAddOn("ao_mayo", "স্মোকি মেয়ো ডিপ", "Smoky Mayo Dip", 30)
            )
        ),
        FoodItem(
            id = "food_302",
            restaurantId = "rest_4",
            nameBn = "ক্রিস্পি চিকেন বার্গার উইথ চিজ",
            nameEn = "Crispy Chicken Cheese Burger",
            descriptionBn = "মুচমুচে ফ্রায়েড চিকেন ব্রেস্ট ফিলে, ফ্রেশ লেটুস পাতা ও ক্রিমি গার্লিক মেয়ো।",
            descriptionEn = "Golden crunchy fried chicken fillet, fresh iceberg lettuce and creamy garlic mayo.",
            price = 290,
            categoryId = "cat_burger",
            isVeg = false,
            isBestseller = false,
            isPopular = true,
            imageUrl = "https://images.unsplash.com/photo-1550547660-d9450f859349?w=800",
            prepTimeMin = 15,
            calories = 520,
            addOns = listOf(
                FoodAddOn("ao_cheese", "এক্সট্রা চিজ", "Extra Cheese", 40),
                FoodAddOn("ao_jalapeno", "জালাপিনো পেপারস", "Jalapenos", 30)
            )
        ),

        // Star Kabab Foods
        FoodItem(
            id = "food_401",
            restaurantId = "rest_3",
            nameBn = "স্টার স্পেশাল মাটন শিক কাবাব",
            nameEn = "Star Special Mutton Sheekh Kebab",
            descriptionBn = "কয়লার আগুনে ঝলসে নেওয়া রসালো মাটন কিমা কাবাব, পুদিনার চাটনি সহ পরিবেশিত।",
            descriptionEn = "Charcoal-grilled juicy minced mutton skewers served with fresh mint chutney.",
            price = 190,
            categoryId = "cat_kebab",
            isVeg = false,
            isBestseller = true,
            isPopular = true,
            imageUrl = "https://images.unsplash.com/photo-1555939594-58d7cb561ad1?w=800",
            prepTimeMin = 15,
            calories = 380,
            addOns = listOf(
                FoodAddOn("ao_paratha", "স্পেশাল পরোটা (১ পিস)", "Special Paratha (1 pc)", 25),
                FoodAddOn("ao_naan", "বাটার নান", "Butter Naan", 40)
            )
        ),
        FoodItem(
            id = "food_402",
            restaurantId = "rest_3",
            nameBn = "চিকেন তন্দুরি ফুল",
            nameEn = "Whole Tandoori Chicken",
            descriptionBn = "তন্দুরে সেঁকা লাল মশলার স্বাদে ভরা সম্পূর্ণ চিকেন তন্দুরি।",
            descriptionEn = "Whole marinated chicken roasted in authentic clay tandoor with aromatic spices.",
            price = 420,
            originalPrice = 460,
            categoryId = "cat_kebab",
            isVeg = false,
            isBestseller = false,
            isPopular = true,
            imageUrl = "https://images.unsplash.com/photo-1599488615731-7e5c2823ff28?w=800",
            prepTimeMin = 25,
            calories = 720,
            sizes = listOf(
                FoodSize("sz_half", "হাফ তন্দুরি", "Half Tandoori", -190),
                FoodSize("sz_full", "ফুল তন্দুরি", "Full Tandoori", 0)
            )
        ),

        // Kasturi Traditional Foods
        FoodItem(
            id = "food_501",
            restaurantId = "rest_6",
            nameBn = "খাঁটি পদ্মার সর্ষে ইলিশ",
            nameEn = "Padma River Shorshe Ilish",
            descriptionBn = "তাজা ইলিশের পেটি, হলুদ ও কালো সর্ষে বাটা ও কাঁচামরিচ দিয়ে রান্না।",
            descriptionEn = "Fresh prime Hilsa steak simmered in pungent mustard seed paste and green chillies.",
            price = 480,
            categoryId = "cat_traditional",
            isVeg = false,
            isBestseller = true,
            isPopular = true,
            imageUrl = "https://images.unsplash.com/photo-1626777552726-4a6b54c97e46?w=800",
            prepTimeMin = 20,
            calories = 490,
            addOns = listOf(
                FoodAddOn("ao_rice", "এক বাটি গরম সাদা ভাত", "Steamed Basmati Rice", 50),
                FoodAddOn("ao_dal", "ঘিয়ে বাগার দেওয়া ডাল", "Ghee Tadka Daal", 40)
            )
        ),
        FoodItem(
            id = "food_502",
            restaurantId = "rest_6",
            nameBn = "বাঙালি পঞ্চ ভর্তা প্ল্যাটার",
            nameEn = "Bengali Pancha Vorta Platter",
            descriptionBn = "বেগুন ভর্তা, আলু ভর্তা, টমেটো ভর্তা, সরিষা ইলিশের কাঁটা ভর্তা ও ডাল ভর্তা।",
            descriptionEn = "5 varieties of traditional mashed delicacies: Aubergine, Potato, Tomato, Fish and Lentil.",
            price = 220,
            categoryId = "cat_traditional",
            isVeg = false,
            isBestseller = false,
            isPopular = true,
            imageUrl = "https://images.unsplash.com/photo-1610057099443-fde8c4d50f91?w=800",
            prepTimeMin = 10,
            calories = 310
        ),

        // Rosh Sweets
        FoodItem(
            id = "food_601",
            restaurantId = "rest_8",
            nameBn = "স্পেশাল মালাই রসমালাই (হাফ কেজি)",
            nameEn = "Special Malai Roshomalai (500g)",
            descriptionBn = "ছোট নরম ছানার রসগোল্লা, ঘন ক্ষীর ও পেস্তাবাদামের মিশেলে স্বর্গীয় স্বাদ।",
            descriptionEn = "Soft cottage cheese balls soaked in thick thickened milk cream flavored with cardamom and pistachios.",
            price = 360,
            categoryId = "cat_sweets",
            isVeg = true,
            isBestseller = true,
            isPopular = true,
            imageUrl = "https://images.unsplash.com/photo-1587314168485-3236d6710814?w=800",
            prepTimeMin = 5,
            calories = 450,
            sizes = listOf(
                FoodSize("sz_250", "২৫০ গ্রাম", "250 Grams", -180),
                FoodSize("sz_500", "৫০০ গ্রাম", "500 Grams", 0),
                FoodSize("sz_1kg", "১ কেজি বক্স", "1 Kg Family Pack", 340)
            )
        ),
        FoodItem(
            id = "food_602",
            restaurantId = "rest_8",
            nameBn = "ঐতিহ্যবাহী বগুড়ার মিষ্টি দই (হাঁড়ি)",
            nameEn = "Traditional Bogura Mishti Doi (Pot)",
            descriptionBn = "মাটির হাঁড়িতে খাঁটি দুধ জ্বাল দিয়ে তৈরি লাল মিষ্টি দই।",
            descriptionEn = "Authentic caramelized sweet curd set slowly in natural earthen clay pots.",
            price = 240,
            categoryId = "cat_sweets",
            isVeg = true,
            isBestseller = false,
            isPopular = true,
            imageUrl = "https://images.unsplash.com/photo-1579372786545-d24232daf58c?w=800",
            prepTimeMin = 5,
            calories = 320
        ),

        // Bismillah Kabab Ghor
        FoodItem(
            id = "food_701",
            restaurantId = "rest_7",
            nameBn = "পুরান ঢাকা স্পেশাল বিফ চাপ",
            nameEn = "Old Dhaka Special Beef Chaap",
            descriptionBn = "তাওয়ায় মচমচে করে ভাজা পাতলা বিফ চাপ, সালাদ ও পুদিনা চাটনি সহ।",
            descriptionEn = "Tender flattened beef cutlet shallow fried crispy on large iron skillet.",
            price = 180,
            categoryId = "cat_kebab",
            isVeg = false,
            isBestseller = true,
            isPopular = true,
            imageUrl = "https://images.unsplash.com/photo-1544025162-d76694265947?w=800",
            prepTimeMin = 12,
            calories = 440,
            addOns = listOf(
                FoodAddOn("ao_luchi", "গরম লুচি (৪ পিস)", "Hot Luchi (4 pcs)", 40),
                FoodAddOn("ao_salad", "শসা ও কাঁচামরিচ সালাদ", "Cucumber Onion Salad", 15)
            )
        )
    )

    val promoCodes = listOf(
        PromoCode(
            code = "QUICK50",
            discountFlat = 50,
            minSpend = 300,
            descriptionBn = "৩০০ টাকার অর্ডারে ৫০ টাকা ফ্ল্যাট ছাড়",
            descriptionEn = "Flat ৳50 OFF on orders above ৳300"
        ),
        PromoCode(
            code = "DHAKAFREE",
            isFreeDelivery = true,
            minSpend = 200,
            descriptionBn = "২০০ টাকার অর্ডারে ফ্রি ডেলিভারি",
            descriptionEn = "Free delivery on orders above ৳200"
        ),
        PromoCode(
            code = "KACCHI50",
            discountPercent = 20,
            minSpend = 400,
            descriptionBn = "৪০০ টাকার অর্ডারে ২০% ছাড় (সর্বোচ্চ ১২০ টাকা)",
            descriptionEn = "20% OFF on orders above ৳400 (max ৳120)"
        ),
        PromoCode(
            code = "BURGER20",
            discountPercent = 15,
            minSpend = 250,
            descriptionBn = "২৫০ টাকার বার্গার অর্ডারে ১৫% ছাড়",
            descriptionEn = "15% OFF on burger orders above ৳250"
        ),
        PromoCode(
            code = "EID100",
            discountFlat = 100,
            minSpend = 600,
            descriptionBn = "৬০০ টাকার বড় অর্ডারে ১০০ টাকা ছাড়",
            descriptionEn = "Flat ৳100 OFF on feasts above ৳600"
        )
    )

    fun getRestaurantsFlow(): Flow<List<Restaurant>> = flowOf(restaurants)

    fun getRestaurantById(id: String): Restaurant? {
        return restaurants.firstOrNull { it.id == id }
    }

    fun getFoodItemsForRestaurant(restaurantId: String): List<FoodItem> {
        return foodItems.filter { it.restaurantId == restaurantId }
    }

    fun getFoodItemById(foodId: String): FoodItem? {
        return foodItems.firstOrNull { it.id == foodId }
    }

    fun search(query: String): List<Pair<Restaurant?, FoodItem?>> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return emptyList()

        val matchingRestaurants = restaurants.filter {
            it.nameBn.lowercase().contains(q) ||
            it.nameEn.lowercase().contains(q) ||
            it.cuisineBn.lowercase().contains(q) ||
            it.cuisineEn.lowercase().contains(q) ||
            it.tags.any { tag -> tag.lowercase().contains(q) }
        }.map { Pair(it, null as FoodItem?) }

        val matchingFoods = foodItems.filter {
            it.nameBn.lowercase().contains(q) ||
            it.nameEn.lowercase().contains(q) ||
            it.descriptionBn.lowercase().contains(q) ||
            it.descriptionEn.lowercase().contains(q)
        }.map { Pair(restaurants.firstOrNull { r -> r.id == it.restaurantId }, it) }

        return matchingRestaurants + matchingFoods
    }
}
