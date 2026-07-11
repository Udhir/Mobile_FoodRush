package com.example.foodrush

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
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
import coil.compose.AsyncImage
import com.example.foodrush.model.CartModel
import com.example.foodrush.repo.CartRepoImpl
import com.example.foodrush.repo.OrderRepoImpl
import com.example.foodrush.ui.theme.OrangePrimary
import com.example.foodrush.viewmodel.CartViewModel
import com.example.foodrush.viewmodel.OrderViewModel
import com.google.firebase.auth.FirebaseAuth
import java.util.Locale

@Composable
fun CartScreen(onCheckout: () -> Unit) {
    val context = LocalContext.current
    val cartViewModel = remember { CartViewModel(CartRepoImpl()) }
    val orderViewModel = remember { OrderViewModel(OrderRepoImpl()) }
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val cartItems by cartViewModel.cartItems.observeAsState(emptyList())
    var isCheckingOut by remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            cartViewModel.getCartItems(userId)
        }
    }

    val totalPrice = cartItems.sumOf { it.foodPrice * it.quantity }

    // Removed Scaffold, just returning the Column so it fits inside Dashboard
    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF8F9FA))) {
        Text(
            "My Cart",
            modifier = Modifier.padding(top = 40.dp, start = 20.dp, end = 20.dp, bottom = 10.dp),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 24.sp
        )

        if (cartItems.isEmpty()) {
            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("Your cart is empty \uD83D\uDE22", color = Color.Gray, fontSize = 16.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                items(cartItems) { item ->
                    CartItemCard(item, cartViewModel)
                }
            }
        }

        // Bottom Checkout Box
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
            shadowElevation = 16.dp
        ) {
            Column(modifier = Modifier.padding(30.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total Payment", color = Color.Gray, fontSize = 16.sp)
                    Text("$${String.format(Locale.US, "%.2f", totalPrice)}", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = OrangePrimary)
                }

                Spacer(Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (cartItems.isEmpty()) return@Button

                        val intent = Intent(context, CheckoutActivity::class.java)
                        context.startActivity(intent)

                    },
                    modifier = Modifier.fillMaxWidth().height(55.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                    shape = RoundedCornerShape(16.dp),
                    enabled = cartItems.isNotEmpty() && !isCheckingOut
                ) {
                    Text("Proceed to Checkout", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun CartItemCard(item: CartModel, viewModel: CartViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(15.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.foodImage,
                contentDescription = null,
                modifier = Modifier.size(70.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFEEEEEE)),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(15.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.foodName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("$${item.foodPrice}", color = OrangePrimary, fontWeight = FontWeight.ExtraBold)

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
                    IconButton(
                        onClick = { if (item.quantity > 1) viewModel.updateQuantity(item.cartId, item.quantity - 1) },
                        modifier = Modifier.size(24.dp).background(Color(0xFFF5F5F5), CircleShape)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                    Text(item.quantity.toString(), modifier = Modifier.padding(horizontal = 12.dp), fontWeight = FontWeight.Bold)
                    IconButton(
                        onClick = { viewModel.updateQuantity(item.cartId, item.quantity + 1) },
                        modifier = Modifier.size(24.dp).background(OrangePrimary, CircleShape)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            }
            IconButton(onClick = { viewModel.removeFromCart(item.cartId) }) {
                Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red.copy(alpha = 0.8f))
            }
        }
    }
}