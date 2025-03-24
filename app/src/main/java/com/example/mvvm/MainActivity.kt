package com.example.mvvm

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mvvm.R
import com.example.mvvm.allproducts.AllProductActivity
import com.example.mvvm.allproducts.ui.theme.MVVMTheme
import com.example.mvvm.favproduct.FavProductActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MVVMTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 100.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "App Logo",
            modifier = Modifier
                .size(width = 230.dp, height = 161.dp)
                .background(Color(0xFF4CAF50))
        )

        Spacer(modifier = Modifier.height(50.dp))

        Button(
            onClick = {
                val intent = Intent(context, AllProductActivity::class.java)
                context.startActivity(intent)
            },
            modifier = Modifier
                .width(249.dp)
                .height(56.dp)
        ) {
            Text(text = "Get All Products", fontSize = 16.sp, color = Color.White)
        }

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                val intent = Intent(context, FavProductActivity::class.java)
                context.startActivity(intent)
            },
            modifier = Modifier
                .width(253.dp)
                .height(53.dp)
        ) {
            Text(text = "Get Favourite Products", fontSize = 16.sp, color = Color.White)
        }

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                (context as? ComponentActivity)?.finishAffinity()
            },
            modifier = Modifier
                .width(258.dp)
                .height(47.dp)
        ) {
            Text(text = "Exit", fontSize = 16.sp, color = Color.White)
        }
    }
}
