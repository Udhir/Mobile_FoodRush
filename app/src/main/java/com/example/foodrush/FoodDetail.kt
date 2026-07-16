package com.example.foodrush

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.example.foodrush.model.CartModel
import com.example.foodrush.model.FoodModel
import com.example.foodrush.repo.CartRepoImpl
import com.example.foodrush.ui.theme.OrangePrimary
import com.example.foodrush.viewmodel.CartViewModel
import com.example.foodrush.viewmodel.FoodViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun FoodDetailScreen(foodId: String, foodViewModel: FoodViewModel, navController: NavHostController, onBack: () -> Unit) {
    val context = LocalContext.current
    val cartViewModel = remember { CartViewModel(CartRepoImpl()) }
    var food by remember { mutableStateOf<FoodModel?>(null) }
    var quantity by remember { mutableStateOf(1) }
    var isLoading by remember { mutableStateOf(true) } // ADDED

    LaunchedEffect(foodId) {
        foodViewModel.repo.getFoodById(foodId) { success, _, data ->
            isLoading = false
            if (success) food = data
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize().background(Color.White), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = OrangePrimary)
        }
    } else if (food != null) {
        val item = food!!
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(rememberScrollState())
        ) {
            Box {
                // FIXED: Removed burger placeholder. Real image only.
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.name,
                    modifier = Modifier.fillMaxWidth().height(300.dp).background(Color(0xFFEEEEEE)),
                    contentScale = ContentScale.Crop
                )
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.padding(16.dp).background(Color.White, RoundedCornerShape(10.dp))
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null)
                }
            }

            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(item.name, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                    Text("NPR ${item.price}", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = OrangePrimary)
                }

                Spacer(Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(20.dp))
                    Text(item.rating.toString(), fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(start = 4.dp))
                    Spacer(Modifier.width(20.dp))
                    Text(item.category, color = Color.Gray, fontSize = 14.sp)
                }

                Spacer(Modifier.height(20.dp))
                Text("Description", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(item.description, color = Color.Gray, fontSize = 15.sp, lineHeight = 22.sp, modifier = Modifier.padding(top = 8.dp))

                Spacer(Modifier.height(30.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = { if (quantity > 1) quantity-- },
                        modifier = Modifier.background(Color(0xFFF5F5F5), CircleShape)
                    ) { Text("-", fontSize = 20.sp, fontWeight = FontWeight.Bold) }
                    Text(quantity.toString(), modifier = Modifier.padding(horizontal = 20.dp), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    IconButton(
                        onClick = { quantity++ },
                        modifier = Modifier.background(OrangePrimary, CircleShape)
                    ) { Text("+", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White) }
                }

                Spacer(Modifier.height(40.dp))

                Button(
                    onClick = {
                        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                        if (userId.isEmpty()) {
                            Toast.makeText(context, "Please login first", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        val cartItem = CartModel(
                            userId = userId,
                            foodId = item.id,
                            foodName = item.name,
                            foodPrice = item.price,
                            foodImage = item.imageUrl,
                            quantity = quantity
                        )
                        cartViewModel.addToCart(cartItem) { success, msg ->
                            if (success) {
                                Toast.makeText(context, "Added to cart!", Toast.LENGTH_SHORT).show()
                                // THIS LINE AUTOMATICALLY TAKES THEM TO THE CART PAGE!
                                navController.navigate("cart") {
                                    popUpTo("dashboard") { inclusive = false } // Keeps back button logical
                                }
                            } else {
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(55.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                    shape = RoundedCornerShape(15.dp)
                ) {
                    Text("ADD TO CART", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    } else {
        Box(modifier = Modifier.fillMaxSize().background(Color.White), contentAlignment = Alignment.Center) {
            Text("Food item not found.", color = Color.Gray)
        }
    }
}