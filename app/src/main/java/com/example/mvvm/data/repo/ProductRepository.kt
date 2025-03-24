package com.example.mvvm.data.repo

import com.example.mvvm.data.local.ProductLocalDataSource
import com.example.mvvm.data.models.Product
import com.example.mvvm.data.remote.ProductRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf


class ProductRepository private constructor(
    private val productRemoteDataSource: ProductRemoteDataSource,
    private val productLocalDataSource: ProductLocalDataSource
){
    suspend  fun getAllMProducts(isOnline: Boolean): Flow<List<Product>> {
        return if (isOnline) {
            flowOf(productRemoteDataSource.getAllProducts().products)
        } else {
            productLocalDataSource.getAllFavoriteProducts()
        }

    }

    suspend fun insertProduct(product: Product) : Long {
        return productLocalDataSource.insertProduct(product)
    }
    suspend fun deleteProduct(product: Product) : Int {
        return productLocalDataSource.deleteProduct(product)
    }

    companion object {
        @Volatile private var INSTANCE: ProductRepository? = null
        fun getInstance(productRemoteDataSource: ProductRemoteDataSource, productLocalDataSource: ProductLocalDataSource): ProductRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = ProductRepository(productRemoteDataSource, productLocalDataSource)
                INSTANCE = instance
                instance
            }
        }
    }
}