package com.example.data.firestore

import android.content.Context
import com.example.R
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class QuickBiteFirestoreRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth = Firebase.auth

    fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    suspend fun getUserProfile(): FirestoreUserProfile? {
        val uid = requireUserId()
        val path = "users/$uid"
        val docRef = db.collection("users").document(uid)
        return try {
            val snapshot = docRef.get().await()
            snapshot.toObject(FirestoreUserProfile::class.java)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, path)
            null
        }
    }

    suspend fun saveUserProfile(
        name: String,
        email: String,
        phone: String = "",
        defaultAddress: String = ""
    ): Result<Unit> {
        val uid = requireUserId()
        val path = "users/$uid"
        val docRef = db.collection("users").document(uid)
        return try {
            val existing = docRef.get().await()
            if (existing.exists()) {
                val updates = mutableMapOf<String, Any>(
                    "name" to name,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                if (phone.isNotBlank()) updates["phone"] = phone
                if (defaultAddress.isNotBlank()) updates["defaultAddress"] = defaultAddress
                docRef.update(updates).await()
            } else {
                val data = mutableMapOf<String, Any>(
                    "userId" to uid,
                    "name" to name,
                    "email" to email,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                if (phone.isNotBlank()) data["phone"] = phone
                if (defaultAddress.isNotBlank()) data["defaultAddress"] = defaultAddress
                docRef.set(data).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    fun observeOrders(): Flow<List<FirestoreOrderRecord>> = flow {
        val uid = requireUserId()
        val path = "users/$uid/orders"
        emitAll(
            db.collection("users").document(uid).collection("orders")
                .snapshots()
                .map { snapshot -> snapshot.toObjects(FirestoreOrderRecord::class.java) }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    suspend fun createOrder(
        orderId: String,
        restaurantId: String,
        restaurantName: String,
        itemsCount: Int,
        totalAmount: Double,
        status: String = "PLACED",
        deliveryAddress: String,
        phone: String,
        paymentMethod: String = "Cash on Delivery"
    ): Result<String> {
        val uid = requireUserId()
        val path = "users/$uid/orders/$orderId"
        val docRef = db.collection("users").document(uid).collection("orders").document(orderId)
        val payload = mapOf(
            "id" to orderId,
            "userId" to uid,
            "restaurantId" to restaurantId,
            "restaurantName" to restaurantName,
            "itemsCount" to itemsCount,
            "totalAmount" to totalAmount,
            "status" to status,
            "deliveryAddress" to deliveryAddress,
            "phone" to phone,
            "paymentMethod" to paymentMethod,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        return try {
            docRef.set(payload).await()
            Result.success(orderId)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    suspend fun updateOrderStatus(orderId: String, newStatus: String): Result<Unit> {
        val uid = requireUserId()
        val path = "users/$uid/orders/$orderId"
        val docRef = db.collection("users").document(uid).collection("orders").document(orderId)
        return try {
            docRef.update(
                mapOf(
                    "status" to newStatus,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    fun observeFavorites(): Flow<List<FirestoreFavoriteItem>> = flow {
        val uid = requireUserId()
        val path = "users/$uid/favorites"
        emitAll(
            db.collection("users").document(uid).collection("favorites")
                .snapshots()
                .map { snapshot -> snapshot.toObjects(FirestoreFavoriteItem::class.java) }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    suspend fun saveFavorite(
        restaurantId: String,
        restaurantName: String,
        cuisine: String,
        rating: Double
    ): Result<Unit> {
        val uid = requireUserId()
        val path = "users/$uid/favorites/$restaurantId"
        val docRef = db.collection("users").document(uid).collection("favorites").document(restaurantId)
        val payload = mapOf(
            "id" to restaurantId,
            "userId" to uid,
            "restaurantId" to restaurantId,
            "restaurantName" to restaurantName,
            "cuisine" to cuisine,
            "rating" to rating,
            "createdAt" to FieldValue.serverTimestamp()
        )
        return try {
            docRef.set(payload).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    suspend fun removeFavorite(restaurantId: String): Result<Unit> {
        val uid = requireUserId()
        val path = "users/$uid/favorites/$restaurantId"
        val docRef = db.collection("users").document(uid).collection("favorites").document(restaurantId)
        return try {
            docRef.delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, path)
            Result.failure(e)
        }
    }
}
