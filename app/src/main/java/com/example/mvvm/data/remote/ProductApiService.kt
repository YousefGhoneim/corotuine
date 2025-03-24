package com.example.mvvm.data.remote

import com.example.mvvm.data.models.ProductResponse
import retrofit2.http.GET

interface ProductApiService {
    @GET("products")
    suspend fun getALLProducts(): ProductResponse
}