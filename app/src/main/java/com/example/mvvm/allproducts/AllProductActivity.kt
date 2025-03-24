package com.example.mvvm.allproducts

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.mvvm.allproducts.ui.theme.MVVMTheme
import com.example.mvvm.data.local.ProductDatabase
import com.example.mvvm.data.local.ProductLocalDataSource
import com.example.mvvm.data.models.Product
import com.example.mvvm.data.models.UiState
import com.example.mvvm.data.remote.ProductRemoteDataSource
import com.example.mvvm.data.remote.RetrofitHelper
import com.example.mvvm.data.repo.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AllProductActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = AllProductsViewModel(
            ProductRepository.getInstance(
                ProductRemoteDataSource(RetrofitHelper.service),
                ProductLocalDataSource(ProductDatabase.getInstance(this).productDao())
            )
        )

        enableEdgeToEdge()
        setContent {
            MVVMTheme {
                ProductListScreen(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(viewModel: AllProductsViewModel) {
    val products by viewModel.products.collectAsStateWithLifecycle()
    val message by viewModel.message.collectAsStateWithLifecycle()

    val coroutineScope = rememberCoroutineScope()
    val snackbarState = remember { SnackbarHostState() }
    LaunchedEffect(Unit) {
        viewModel.getAllProducts(true)
    }

    LaunchedEffect(message) {
        if (message.isNotEmpty()) {
            snackbarState.showSnackbar(message)
        }
        viewModel.resetMsg()
    }
    when (products) {
        is UiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            )
            {
                CircularProgressIndicator()
            }
        }

        is UiState.Success -> {
            val myProduct = (products as UiState.Success).data
            Scaffold(topBar = {
                TopAppBar(title = { Text("All Products") })
            }, snackbarHost = {
                SnackbarHost(hostState = snackbarState)
            }, floatingActionButton = {}) { paddingValues ->
                Column(modifier = Modifier.padding(paddingValues)) {
                    LazyColumn(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(myProduct) { product ->
                            ProductItem(product = product, isFavScreen = false, onButtonClicked = {
                                coroutineScope.launch(Dispatchers.IO) {
                                    viewModel.insertProduct(it)
                                    Log.d("ProductListScreen", "Added ${it.title} to favorites")
                                }
                            })
                        }
                    }
                }
            }
        }

        UiState.Empty -> {
            Toast.makeText(LocalContext.current, "No Products Found", Toast.LENGTH_SHORT).show()
        }
        is UiState.Error -> {
            Toast.makeText( LocalContext.current, (products as UiState.Error).message, Toast.LENGTH_SHORT).show()
        }
    }
}


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ProductItem(
    product: Product, isFavScreen: Boolean, onButtonClicked: (Product) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = Color.LightGray, shape = RoundedCornerShape(8.dp))
            .padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween
    ) {
        GlideImage(
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(12.dp))
                .padding(end = 16.dp),
            model = product.thumbnail,
            contentDescription = "Product Image",
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = product.title, fontWeight = FontWeight.Medium, fontSize = 18.sp
            )

            Button(modifier = Modifier.fillMaxWidth(), onClick = { onButtonClicked(product) }) {
                Text(
                    text = if (isFavScreen) "Remove From Favorites" else "Add To Favorites"
                )
            }
        }
    }
}
