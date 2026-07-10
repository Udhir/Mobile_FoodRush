// FoodDetails.kt
package com.example.foodrush

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.foodrush.model.FoodModel
import com.example.foodrush.repo.FoodRepoImpl
import com.example.foodrush.ui.theme.FoodRushTheme
import com.example.foodrush.ui.theme.OrangePrimary
import com.example.foodrush.viewmodel.FoodViewModel
import com.google.firebase.auth.FirebaseAuth

class FoodDetails : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val foodId = intent.getStringExtra("foodId") ?: ""
        setContent {
            FoodRushTheme {
                FoodDetailsScreen(foodId)
            }
        }
    }
}

@Composable
fun FoodDetailsScreen(foodId: String) {
    val foodViewModel = remember { FoodViewModel(FoodRepoImpl()) }
    val cartViewModel = remember { CartViewModel(CartRepoImpl()) }
    val context = LocalContext.current
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    var food by remember { mutableStateOf<FoodModel?>(null) }
    var quantity by remember { mutableStateOf(1) }

    LaunchedEffect(foodId) {
        foodViewModel.repo.getFoodById(foodId) { _, _, data -> food = data }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box {
            AsyncImage(
                model = food?.imageUrl,
                contentDescription = food?.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .background(Color(0xFFF5F5F5))
            )
            IconButton(
                onClick = { (context as? android.app.Activity)?.finish() },
                modifier = Modifier
                    .padding(16.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        }

        Column(modifier = Modifier.padding(20.dp)) {
            Text(food?.name ?: "", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107))
                Spacer(Modifier.width(4.dp))
                Text("${food?.rating ?: 4.5}", fontSize = 14.sp)
                Spacer(Modifier.width(12.dp))
                Text(food?.category ?: "", fontSize = 14.sp, color = Color.Gray)
            }
            Spacer(Modifier.height(12.dp))
            Text(food?.description ?: "", fontSize = 14.sp, color = Color.Gray)
            Spacer(Modifier.height(20.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("$${"%.2f".format(food?.price ?: 0.0)}", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = OrangePrimary)
                Spacer(Modifier.weight(1f))
                IconButton(onClick = { if (quantity > 1) quantity-- }) { Text("-", fontSize = 20.sp) }
                Text("$quantity", fontSize = 18.sp, modifier = Modifier.padding(horizontal = 8.dp))
                IconButton(onClick = { quantity++ }) { Text("+", fontSize = 20.sp) }
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    val f = food ?: return@Button
                    if (userId.isEmpty()) {
                        Toast.makeText(context, "Please login first", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    val cartItem = CartModel(
                        foodId = f.id,
                        foodName = f.name,
                        foodImage = f.imageUrl,
                        price = f.price,
                        quantity = quantity,
                        userId = userId
                    )
                    cartViewModel.addToCart(userId, cartItem) { success, msg ->
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(55.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("ADD TO CART", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}