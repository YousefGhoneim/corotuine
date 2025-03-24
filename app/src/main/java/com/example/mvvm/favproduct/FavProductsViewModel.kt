package com.example.mvvm.favproduct

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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

class FavProductsViewModel(private val productRepository: ProductRepository) : ViewModel() {
    private val _favProducts  = MutableStateFlow<UiState>(UiState.Loading)
    val favProducts = _favProducts.asStateFlow()

    private val _message = MutableStateFlow<String>("")
    val message = _message.asStateFlow()

    fun getFavoriteProducts() {
        viewModelScope.launch {
            productRepository.getAllMProducts(false)
                .catch {
                    _favProducts.value = UiState.Error(it.message ?: "Unknown error")
                    _message.value = it.message ?: "Unknown error"
                }
                .collect {
                    _favProducts.value = UiState.Success(it)
                }

        }

    }
        fun removeProductFromFavorites(product: Product) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val rowsDeleted = productRepository.deleteProduct(product)
                    if (rowsDeleted > 0) {
                        _message.value = ("${product.title} removed from favorites")
                        getFavoriteProducts()
                    } else {
                        _message.value = ("Failed to remove ${product.title}")
                    }
                } catch (e: Exception) {
                    _message.value = ("Error: ${e.message}")
                }
            }
        }

        fun resetMsg() {
            _message.value = ("")
        }

}

class FavProductsViewModelFactory(private val productRepository: ProductRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavProductsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FavProductsViewModel(productRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
