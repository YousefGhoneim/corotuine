package com.example.mvvm.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey val id: Int,
    val title: String,
    val description: String,
    val thumbnail: String
)

sealed class UiState {
    object Loading : UiState()
    data class Success(val data: List<Product>) : UiState()
    data class Error(val message: String) : UiState()
    object Empty : UiState()
}

data class ProductResponse(
    val limit: Int,
    val products: List<Product>,
    val skip: Int,
    val total: Int
)