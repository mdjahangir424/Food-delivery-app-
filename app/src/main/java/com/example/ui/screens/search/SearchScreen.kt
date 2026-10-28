package com.example.ui.screens.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.FavoriteEntity
import com.example.data.model.FoodItem
import com.example.data.model.Restaurant
import com.example.ui.components.CategoryChips
import com.example.ui.components.FoodItemCard
import com.example.ui.components.RestaurantCard
import com.example.ui.localization.AppStrings
import com.example.ui.theme.GoldStar
import com.example.ui.viewmodel.QuickBiteViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchScreen(
    viewModel: QuickBiteViewModel,
    strings: AppStrings,
    favorites: List<FavoriteEntity>,
    onRestaurantClick: (String) -> Unit,
    onFoodClick: (FoodItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val foodRepo = viewModel.foodRepo
    var query by remember { mutableStateOf(viewModel.searchQuery.value) }
    var filterRatingOnly by remember { mutableStateOf(false) }
    var filterFastOnly by remember { mutableStateOf(false) }
    var filterFreeDeliveryOnly by remember { mutableStateOf(false) }

    val selectedCategory = viewModel.selectedCategoryFilter.value

    val popularSuggestions = listOf(
        "কাচ্চি বিরিয়ানি", "বিফ বার্গার", "মাটন কাবাব", "তেহারি", "সর্ষে ইলিশ", "বোরহানি", "রসমালাই"
    )

    // Filter restaurants
    var filteredRestaurants = foodRepo.restaurants.filter { rest ->
        val matchesQuery = query.isEmpty() ||
                rest.nameBn.contains(query, ignoreCase = true) ||
                rest.nameEn.contains(query, ignoreCase = true) ||
                rest.cuisineBn.contains(query, ignoreCase = true) ||
                rest.cuisineEn.contains(query, ignoreCase = true) ||
                rest.tags.any { it.contains(query, ignoreCase = true) }

        val matchesRating = !filterRatingOnly || rest.rating >= 4.7f
        val matchesFast = !filterFastOnly || rest.deliveryTimeMin <= 25
        val matchesFree = !filterFreeDeliveryOnly || rest.deliveryFee == 0 || rest.discountTextBn?.contains("ফ্রি") == true

        matchesQuery && matchesRating && matchesFast && matchesFree
    }

    // Filter foods
    val filteredFoods = foodRepo.foodItems.filter { food ->
        val matchesQuery = query.isEmpty() ||
                food.nameBn.contains(query, ignoreCase = true) ||
                food.nameEn.contains(query, ignoreCase = true) ||
                food.descriptionBn.contains(query, ignoreCase = true)

        val matchesCategory = selectedCategory == null || food.categoryId == selectedCategory

        matchesQuery && matchesCategory
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("search_screen_content"),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Search Input Header
        item {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = query,
                    onValueChange = {
                        query = it
                        viewModel.updateSearchQuery(it)
                    },
                    placeholder = { Text(strings.searchPlaceholder, style = MaterialTheme.typography.bodyMedium) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = {
                                query = ""
                                viewModel.updateSearchQuery("")
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_text_input")
                )
            }
        }

        // Filter chips (Rating 4.5+, Fast Delivery, Free Delivery)
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterChip(
                        selected = filterRatingOnly,
                        onClick = { filterRatingOnly = !filterRatingOnly },
                        label = { Text(strings.filterRating4Plus) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = GoldStar,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                item {
                    FilterChip(
                        selected = filterFastOnly,
                        onClick = { filterFastOnly = !filterFastOnly },
                        label = { Text(strings.filterFastDelivery) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                item {
                    FilterChip(
                        selected = filterFreeDeliveryOnly,
                        onClick = { filterFreeDeliveryOnly = !filterFreeDeliveryOnly },
                        label = { Text(strings.filterFreeDelivery) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }

        // Category filter row
        item {
            Column(modifier = Modifier.padding(top = 10.dp, bottom = 6.dp)) {
                CategoryChips(
                    categories = foodRepo.categories,
                    selectedCategoryId = selectedCategory,
                    strings = strings,
                    onCategorySelect = { catId -> viewModel.setCategoryFilter(catId) }
                )
            }
        }

        // Popular search suggestions tags when search input is empty
        if (query.isEmpty()) {
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                    Text(
                        text = strings.popularSearches,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        popularSuggestions.forEach { tag ->
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable {
                                        query = tag
                                        viewModel.updateSearchQuery(tag)
                                    }
                            ) {
                                Text(
                                    text = tag,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Results Section Header
        item {
            val totalResults = filteredRestaurants.size + filteredFoods.size
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strings.searchResults,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${filteredRestaurants.size} ${if (strings.isBn) "রেস্তোরাঁ" else "restaurants"}, ${filteredFoods.size} ${if (strings.isBn) "পদ" else "items"}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Empty state check
        if (filteredRestaurants.isEmpty() && filteredFoods.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = strings.noResultsFound,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = strings.tryDifferentSearch,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Restaurants results
        items(filteredRestaurants) { restaurant ->
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

        // Food dishes results
        items(filteredFoods) { food ->
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
