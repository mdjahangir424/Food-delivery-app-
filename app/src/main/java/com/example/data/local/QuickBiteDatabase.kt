package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.AddressDao
import com.example.data.local.dao.CartDao
import com.example.data.local.dao.FavoriteDao
import com.example.data.local.dao.OrderDao
import com.example.data.local.entity.CartItemEntity
import com.example.data.local.entity.FavoriteEntity
import com.example.data.local.entity.OrderEntity
import com.example.data.local.entity.SavedAddressEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        CartItemEntity::class,
        OrderEntity::class,
        FavoriteEntity::class,
        SavedAddressEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class QuickBiteDatabase : RoomDatabase() {

    abstract fun cartDao(): CartDao
    abstract fun orderDao(): OrderDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun addressDao(): AddressDao

    companion object {
        @Volatile
        private var INSTANCE: QuickBiteDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): QuickBiteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QuickBiteDatabase::class.java,
                    "quickbite_db"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        seedInitialData(database)
                    }
                }
            }

            private suspend fun seedInitialData(database: QuickBiteDatabase) {
                val addressDao = database.addressDao()
                addressDao.insertAddress(
                    SavedAddressEntity(
                        labelBn = "বাসা (ধানমন্ডি)",
                        labelEn = "Home (Dhanmondi)",
                        addressDetailsBn = "বাড়ি #৩৪, রোড #৯/এ, ফ্ল্যাট ৪-বি",
                        addressDetailsEn = "House #34, Road #9/A, Flat 4-B",
                        areaBn = "ধানমন্ডি",
                        areaEn = "Dhanmondi",
                        contactPhone = "01711234567",
                        isDefault = true
                    )
                )
                addressDao.insertAddress(
                    SavedAddressEntity(
                        labelBn = "অফিস (গুলশান)",
                        labelEn = "Office (Gulshan)",
                        addressDetailsBn = "টাওয়ার ৭১, লেভেল ৮, গুলশান এভিনিউ",
                        addressDetailsEn = "Tower 71, Level 8, Gulshan Avenue",
                        areaBn = "গুলশান-২",
                        areaEn = "Gulshan-2",
                        contactPhone = "01819876543",
                        isDefault = false
                    )
                )

                // Seed initial favorite restaurant
                val favoriteDao = database.favoriteDao()
                favoriteDao.insertFavorite(
                    FavoriteEntity(
                        favoriteId = "rest_1",
                        itemType = "RESTAURANT",
                        nameBn = "গ্র্যান্ড নবাব কাচ্চি",
                        nameEn = "Grand Nawab Kacchi",
                        subtitleBn = "ঐতিহ্যবাহী পুরান ঢাকার বাসমতী কাচ্চি",
                        subtitleEn = "Authentic Old Dhaka Basmati Kacchi",
                        rating = 4.8f,
                        priceOrDelivery = "৳৪৫০",
                        imageUrl = "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=800"
                    )
                )
            }
        }
    }
}
