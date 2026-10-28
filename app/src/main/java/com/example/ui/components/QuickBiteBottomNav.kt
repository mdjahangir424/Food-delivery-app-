package com.example.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.localization.AppStrings
import com.example.ui.localization.BengaliNumberFormatter
import com.example.ui.viewmodel.MainTab

@Composable
fun QuickBiteBottomNav(
    currentTab: MainTab,
    onTabSelected: (MainTab) -> Unit,
    strings: AppStrings,
    activeOrdersCount: Int = 0,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
        modifier = modifier.testTag("quickbite_bottom_navigation")
    ) {
        // Home
        NavigationBarItem(
            selected = currentTab == MainTab.HOME,
            onClick = { onTabSelected(MainTab.HOME) },
            icon = {
                Icon(
                    imageVector = if (currentTab == MainTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                    contentDescription = strings.navHome,
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = strings.navHome,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = if (currentTab == MainTab.HOME) FontWeight.Bold else FontWeight.Normal
                    )
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.testTag("nav_tab_home")
        )

        // Search
        NavigationBarItem(
            selected = currentTab == MainTab.SEARCH,
            onClick = { onTabSelected(MainTab.SEARCH) },
            icon = {
                Icon(
                    imageVector = if (currentTab == MainTab.SEARCH) Icons.Filled.Search else Icons.Outlined.Search,
                    contentDescription = strings.navSearch,
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = strings.navSearch,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = if (currentTab == MainTab.SEARCH) FontWeight.Bold else FontWeight.Normal
                    )
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.testTag("nav_tab_search")
        )

        // Orders
        NavigationBarItem(
            selected = currentTab == MainTab.ORDERS,
            onClick = { onTabSelected(MainTab.ORDERS) },
            icon = {
                BadgedBox(
                    badge = {
                        if (activeOrdersCount > 0) {
                            Badge(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ) {
                                Text(
                                    text = BengaliNumberFormatter.formatNumber(activeOrdersCount, strings.isBn),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (currentTab == MainTab.ORDERS) Icons.Filled.ReceiptLong else Icons.Outlined.ReceiptLong,
                        contentDescription = strings.navOrders,
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            label = {
                Text(
                    text = strings.navOrders,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = if (currentTab == MainTab.ORDERS) FontWeight.Bold else FontWeight.Normal
                    )
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.testTag("nav_tab_orders")
        )

        // Favorites
        NavigationBarItem(
            selected = currentTab == MainTab.FAVORITES,
            onClick = { onTabSelected(MainTab.FAVORITES) },
            icon = {
                Icon(
                    imageVector = if (currentTab == MainTab.FAVORITES) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = strings.navFavorites,
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = strings.navFavorites,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = if (currentTab == MainTab.FAVORITES) FontWeight.Bold else FontWeight.Normal
                    )
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.testTag("nav_tab_favorites")
        )

        // Profile
        NavigationBarItem(
            selected = currentTab == MainTab.PROFILE,
            onClick = { onTabSelected(MainTab.PROFILE) },
            icon = {
                Icon(
                    imageVector = if (currentTab == MainTab.PROFILE) Icons.Filled.Person else Icons.Outlined.PersonOutline,
                    contentDescription = strings.navProfile,
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = strings.navProfile,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = if (currentTab == MainTab.PROFILE) FontWeight.Bold else FontWeight.Normal
                    )
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.testTag("nav_tab_profile")
        )
    }
}
