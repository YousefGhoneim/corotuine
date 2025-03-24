package com.example.mvvm.data.remote

import com.example.mvvm.data.models.Product
import com.example.mvvm.data.models.ProductResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.catch
import retrofit2.HttpException
import java.io.IOException

class ProductRemoteDataSource(private val productApiService: ProductApiService) {

    suspend fun getAllProducts(): ProductResponse {
        return productApiService.getALLProducts()
    }
}
