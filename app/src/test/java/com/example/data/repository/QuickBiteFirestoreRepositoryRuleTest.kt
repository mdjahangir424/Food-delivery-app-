package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.firestore.QuickBiteFirestoreRepository
import java.util.UUID
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class QuickBiteFirestoreRepositoryRuleTest : FirestoreEmulatorTestBase() {

  @Test
  fun saveUserProfile_validData_succeeds() = runBlocking {
    val email = "alice_" + UUID.randomUUID().toString().take(8) + "@quickbite.bd"
    val uid = signInTestUser(email)
    val repo = QuickBiteFirestoreRepository(firestore)

    val saveResult = withTimeout(10000L) {
      repo.saveUserProfile(
        name = "Alice Rahman",
        email = email,
        phone = "+8801711223344",
        defaultAddress = "House 12, Road 5, Dhanmondi, Dhaka"
      )
    }
    assertTrue(saveResult.isSuccess)

    val profile = withTimeout(10000L) { repo.getUserProfile() }
    assertNotNull(profile)
    assertEquals(uid, profile?.userId)
    assertEquals("Alice Rahman", profile?.name)
  }

  @Test
  fun createOrder_validData_succeeds() = runBlocking {
    val email = "alice_orders_" + UUID.randomUUID().toString().take(8) + "@quickbite.bd"
    signInTestUser(email)
    val repo = QuickBiteFirestoreRepository(firestore)

    val orderId = "order_" + UUID.randomUUID().toString().replace("-", "_")
    val result = withTimeout(10000L) {
      repo.createOrder(
        orderId = orderId,
        restaurantId = "rest_dhaka_01",
        restaurantName = "Kacchi Bhai",
        itemsCount = 3,
        totalAmount = 1450.0,
        status = "PLACED",
        deliveryAddress = "Gulshan 2, Dhaka",
        phone = "+8801812345678",
        paymentMethod = "Cash on Delivery"
      )
    }
    assertTrue("Order creation failed: " + result.exceptionOrNull()?.message, result.isSuccess)
    assertEquals(orderId, result.getOrNull())
  }

  @Test
  fun saveFavorite_and_removeFavorite_succeeds() = runBlocking {
    val email = "alice_favs_" + UUID.randomUUID().toString().take(8) + "@quickbite.bd"
    signInTestUser(email)
    val repo = QuickBiteFirestoreRepository(firestore)

    val restId = "rest_" + UUID.randomUUID().toString().replace("-", "_")
    val saveResult = withTimeout(10000L) {
      repo.saveFavorite(
        restaurantId = restId,
        restaurantName = "Star Kabab",
        cuisine = "Biryani & Kebab",
        rating = 4.6
      )
    }
    assertTrue(saveResult.isSuccess)

    val removeResult = withTimeout(10000L) {
      repo.removeFavorite(restId)
    }
    assertTrue(removeResult.isSuccess)
  }

  @Test
  fun unauthenticatedAccess_throwsIllegalStateException() {
    auth.signOut()
    val repo = QuickBiteFirestoreRepository(firestore)
    var thrown = false
    try {
      repo.requireUserId()
    } catch (e: IllegalStateException) {
      thrown = true
    }
    assertTrue(thrown)
  }
}
