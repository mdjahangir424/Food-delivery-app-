package com.example.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.FavoriteEntity
import com.example.data.model.FoodItem
import com.example.data.model.PromoBanner
import com.example.data.model.Restaurant
import com.example.ui.components.CategoryChips
import com.example.ui.components.FoodItemCard
import com.example.ui.components.PromotionCarousel
import com.example.ui.components.RestaurantCard
import com.example.ui.localization.AppStrings
import com.example.ui.viewmodel.QuickBiteViewModel

@Composable
fun HomeScreen(
    viewModel: QuickBiteViewModel,
    strings: AppStrings,
    favorites: List<FavoriteEntity>,
    onRestaurantClick: (String) -> Unit,
    onFoodClick: (FoodItem) -> Unit,
    onSearchClick: () -> Unit,
    onPromoClick: (PromoBanner) -> Unit,
    modifier: Modifier = Modifier
) {
    val foodRepo = viewModel.foodRepo
    val categories = foodRepo.categories
    val banners = foodRepo.promoBanners
    val restaurants = foodRepo.restaurants
    val recommendedFoods = foodRepo.foodItems.filter { it.isPopular }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen_content"),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Search trigger bar
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onSearchClick() }
                        .testTag("home_search_trigger")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = strings.navSearch,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = strings.searchPlaceholder,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Filter",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Promotional Carousel
        item {
            Column(modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)) {
                PromotionCarousel(
                    banners = banners,
                    strings = strings,
                    onPromoClick = onPromoClick
                )
            }
        }

        // Categories section
        item {
            Column(modifier = Modifier.padding(vertical = 6.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = strings.categories,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                CategoryChips(
                    categories = categories,
                    selectedCategoryId = null,
                    strings = strings,
                    onCategorySelect = { catId ->
                        if (catId != null) {
                            viewModel.setCategoryFilter(catId)
                            viewModel.setTab(com.example.ui.viewmodel.MainTab.SEARCH)
                        }
                    }
                )
            }
        }

        // Popular Restaurants Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 8.dp, top = 16.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strings.popularRestaurants,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                TextButton(
                    onClick = {
                        viewModel.setTab(com.example.ui.viewmodel.MainTab.SEARCH)
                    }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = strings.viewAll,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // Popular Restaurants list (top 4 featured)
        val featuredRestaurants = restaurants.take(4)
        items(featuredRestaurants) { restaurant ->
            val isFav = favorites.any { it.favoriteId == restaurant.id }
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                RestaurantCard(
                    restaurant = restaurant,
                    isFavorite = isFav,
                    strings = strings,
                    onClick = { onRestaurantClick(restaurant.id) },
                    onFavoriteToggle = { viewModel.toggleRestaurantFavorite(restaurant, isFav) }
                )
            }
        }

        // Recommended Dishes Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strings.recommendedDishes,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Recommended food cards
        items(recommendedFoods.take(5)) { food ->
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                FoodItemCard(
                    food = food,
                    strings = strings,
                    onAddClick = { onFoodClick(food) }
                )
            }
        }
    }
}
