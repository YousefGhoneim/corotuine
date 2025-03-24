package com.example.mvvm.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mvvm.data.models.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProduct(product: Product): Long

    @Query("SELECT * FROM products")
    fun getAllFavoriteProducts(): Flow<List<Product>>

    @Delete
    suspend fun deleteProduct(product: Product) : Int
}