package com.example.mvvm.data.local

import com.example.mvvm.data.models.Product
import kotlinx.coroutines.flow.Flow

interface IProductLocalDataSource

class ProductLocalDataSource(private val productDao: ProductDao) : IProductLocalDataSource {

    suspend fun insertProduct(product: Product) : Long {
        return productDao.insertProduct(product)
    }

    fun getAllFavoriteProducts() : Flow<List<Product>> {
        return productDao.getAllFavoriteProducts()
    }

    suspend fun deleteProduct(product: Product) : Int {
        return if (product != null)
            productDao.deleteProduct(product)
        else
            -1

    }


}