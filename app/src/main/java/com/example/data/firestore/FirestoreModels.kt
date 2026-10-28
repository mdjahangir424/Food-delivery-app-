package com.example.data.firestore

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import org.json.JSONArray
import org.json.JSONObject

@IgnoreExtraProperties
data class FirestoreUserProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val defaultAddress: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class FirestoreOrderRecord(
    @DocumentId val id: String = "",
    val userId: String = "",
    val restaurantId: String = "",
    val restaurantName: String = "",
    val itemsCount: Int = 1,
    val totalAmount: Double = 0.0,
    val status: String = "PLACED",
    val deliveryAddress: String = "",
    val phone: String = "",
    val paymentMethod: String = "Cash on Delivery",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class FirestoreFavoriteItem(
    @DocumentId val id: String = "",
    val userId: String = "",
    val restaurantId: String = "",
    val restaurantName: String = "",
    val cuisine: String = "",
    val rating: Double = 4.5,
    @ServerTimestamp val createdAt: Timestamp? = null
)

enum class OperationType(val value: String) {
    CREATE("create"),
    UPDATE("update"),
    DELETE("delete"),
    LIST("list"),
    GET("get"),
    WRITE("write"),
}

fun handleFirestoreError(exception: Exception, operationType: OperationType, path: String?): String {
    val auth = Firebase.auth
    val currentUser = auth.currentUser

    val providerInfoList = currentUser?.providerData?.map { provider ->
        JSONObject().apply {
            put("providerId", provider.providerId)
            put("email", provider.email)
        }
    } ?: emptyList()

    val authInfoJson = JSONObject().apply {
        put("userId", currentUser?.uid)
        put("email", currentUser?.email)
        put("emailVerified", currentUser?.isEmailVerified)
        put("tenantId", currentUser?.tenantId)
        put("providerInfo", JSONArray(providerInfoList))
    }

    val errorInfoJson = JSONObject().apply {
        put("error", exception.message ?: exception.toString())
        put("operationType", operationType.value)
        put("path", path)
        put("authInfo", authInfoJson)
    }

    val jsonString = errorInfoJson.toString()
    Log.e("FirestoreError", "Firestore Error: $jsonString")
    return jsonString
}
