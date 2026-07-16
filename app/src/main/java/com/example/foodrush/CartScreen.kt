package com.example.foodrush

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.foodrush.repo.CartRepoImpl
import com.example.foodrush.ui.theme.OrangePrimary
import com.example.foodrush.viewmodel.CartViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun CartScreen(navController: NavHostController, onCheckout: () -> Unit) {
    val context = LocalContext.current
    val cartViewModel = remember { CartViewModel(CartRepoImpl()) }
    val cartItems by cartViewModel.cartItems.observeAsState(emptyList())
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) cartViewModel.getCartItems(userId)
    }

    val totalPrice = cartItems.sumOf { it.foodPrice * it.quantity }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF8F9FA))) {
        Column(
            modifier = Modifier.fillMaxSize().padding(bottom = 120.dp) // Leave space for sticky footer
        ) {
            Text(
                "My Cart",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 24.sp,
                color = Color.Black,
                modifier = Modifier.padding(top = 40.dp, start = 20.dp, bottom = 20.dp)
            )

            if (cartItems.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Your cart is empty \uD83D\uDE22", color = Color.Gray, fontSize = 18.sp)
                }
            } else {
                LazyColumn(contentPadding = PaddingValues(horizontal = 20.dp)) {
                    items(items = cartItems) { item ->
                        CartItemCard(
                            item = item,
                            viewModel = cartViewModel,
                            onEmpty = {
                                // FIXED: If it's the last item in the cart, navigate directly to dashboard!
                                if (cartItems.size == 1) {
                                    navController.navigate("dashboard") {
                                        popUpTo("dashboard") { inclusive = true }
                                    }
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(15.dp))
                    }
                }
            }
        }

        // --- STICKY BOTTOM CHECKOUT BAR ---
        if (cartItems.isNotEmpty()) {
            Surface(
                modifier = Modifier.align(Alignment.BottomCenter),
                shadowElevation = 10.dp,
                shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
                color = Color.White
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total Payment", color = Color.Gray, fontSize = 14.sp)
                        Text("NPR ${"%.2f".format(totalPrice)}", color = OrangePrimary, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    Button(
                        onClick = { context.startActivity(Intent(context, CheckoutActivity::class.java)) },
                        modifier = Modifier.fillMaxWidth().height(55.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("PROCEED TO CHECKOUT", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}



@Composable
fun CartItemCard(item: CartModel, viewModel: CartViewModel, onEmpty: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp) // Beautiful drop shadow
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // FIXED: Removed burger placeholder. Real image only.
            AsyncImage(
                model = item.foodImage,
                contentDescription = item.foodName,
                modifier = Modifier.size(80.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFEEEEEE)),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.foodName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("NPR ${item.foodPrice}", color = OrangePrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { if (item.quantity > 1) viewModel.updateQuantity(item.cartId, item.quantity - 1) },
                        modifier = Modifier.size(28.dp).background(Color(0xFFF5F5F5), CircleShape)
                    ) { Text("-", fontWeight = FontWeight.Bold, fontSize = 18.sp) }

                    Text(item.quantity.toString(), modifier = Modifier.padding(horizontal = 12.dp), fontWeight = FontWeight.Bold)

                    IconButton(
                        onClick = { viewModel.updateQuantity(item.cartId, item.quantity + 1) },
                        modifier = Modifier.size(28.dp).background(OrangePrimary, CircleShape)
                    ) { Text("+", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp) }
                }
            }
            IconButton(
                onClick = {
                    onEmpty() // Trigger the navigation instantly
                    viewModel.removeFromCart(item.cartId) // Delete from backend
                },
                modifier = Modifier.background(Color(0xFFFFEBEE), CircleShape)
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
            }
        }
    }
}