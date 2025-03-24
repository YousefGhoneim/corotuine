package com.example.mvvm.allproducts

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.mvvm.data.models.Product
import com.example.mvvm.data.models.UiState
import com.example.mvvm.data.repo.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class AllProductsViewModel(private val productRepository: ProductRepository) : ViewModel() {
    private val _products = MutableStateFlow<UiState>(UiState.Loading)
    val products = _products.asStateFlow()

    private val _message = MutableStateFlow<String>("")
    val message = _message.asStateFlow()

    fun getAllProducts(isOnline: Boolean) {
        viewModelScope.launch {
            productRepository.getAllMProducts(isOnline)
                .catch {
                    _products.value = UiState.Error(it.message ?: "Unknown error")
                }
                .collect{
                    _products.value = UiState.Success(it)
                }
        }

    }




    fun insertProduct(product: Product?){
        viewModelScope.launch(Dispatchers.IO) {
            if (product != null) {
                val result = productRepository.insertProduct(product)
                if(result>0){
                    _message.value=("Added Successfully")
                }else{
                    _message.value=("Item is Already added")
                }
            }

        }
    }

    fun resetMsg(){
        _message.value=("")
    }
    

}

