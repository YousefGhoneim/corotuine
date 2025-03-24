package com.example.mvvm.favproduct

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.mvvm.data.local.ProductDatabase
import com.example.mvvm.data.local.ProductLocalDataSource
import com.example.mvvm.data.models.Product
import com.example.mvvm.data.models.UiState
import com.example.mvvm.data.remote.ProductRemoteDataSource
import com.example.mvvm.data.remote.RetrofitHelper
import com.example.mvvm.data.repo.ProductRepository
import com.example.mvvm.favproduct.ui.theme.MVVMTheme
import kotlinx.coroutines.launch

class FavProductActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val productRepository = ProductRepository.getInstance(
            productRemoteDataSource = ProductRemoteDataSource(RetrofitHelper.service) ,
            productLocalDataSource = ProductLocalDataSource(ProductDatabase.getInstance(this).productDao())
        )

        enableEdgeToEdge()
        setContent {
            MVVMTheme {
                FavProductScreen(productRepository)
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavProductScreen(productRepository: ProductRepository) {
    val viewModel: FavProductsViewModel = viewModel(
        factory = FavProductsViewModelFactory(productRepository)
    )

    val favProducts by viewModel.favProducts.collectAsStateWithLifecycle()
    val message by viewModel.message.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    val snackbarState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.getFavoriteProducts()
    }

    LaunchedEffect(key1 = message) {
        if (message.isNotEmpty()) {
            snackbarState.showSnackbar(message)
        }
        viewModel.resetMsg()
    }

    when (favProducts) {
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
            val myFavProducts = (favProducts as UiState.Success).data
            Scaffold(
                topBar = { TopAppBar(title = { Text("Favorite Products") }) },
                snackbarHost = {
                    SnackbarHost(hostState = snackbarState)
                }
            ) { paddingValues ->
                Column(modifier = Modifier.padding(paddingValues)) {
                    if (myFavProducts.isEmpty()) {
                        Text("No favorite products found", modifier = Modifier.padding(16.dp))
                    } else {
                        LazyColumn {
                            items(myFavProducts) { product ->
                                FavoriteProductItem(product) {
                                    coroutineScope.launch {
                                        viewModel.removeProductFromFavorites(product)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        UiState.Empty -> {
            Toast.makeText(LocalContext.current, "No Products Found", Toast.LENGTH_SHORT).show()
        }
        is UiState.Error -> {
            Toast.makeText(LocalContext.current, (favProducts as UiState.Error).message, Toast.LENGTH_SHORT).show()
        }
    }

    }



@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun FavoriteProductItem(product: Product, onRemoveClicked: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            GlideImage(
                modifier = Modifier
                    .size(80.dp)
                    .padding(end = 16.dp),
                model = product.thumbnail,
                contentScale = ContentScale.Crop,
                contentDescription = null
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(text = product.title, style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(8.dp))

                Button(onClick = { onRemoveClicked() }) {
                    Text("Remove from Favorites")
                }
            }
        }
    }
}

