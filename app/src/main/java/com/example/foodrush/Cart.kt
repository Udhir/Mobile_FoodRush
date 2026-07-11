package com.example.foodrush

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.foodrush.model.CartModel
import com.example.foodrush.model.OrderModel
import com.example.foodrush.repo.CartRepoImpl
import com.example.foodrush.repo.OrderRepoImpl
import com.example.foodrush.ui.theme.FoodRushTheme
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

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF8F8F8))) {
        Text(
            "My Cart",
            modifier = Modifier.padding(20.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            items(cartItems) { item ->
                CartItemCard(item, cartViewModel)
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
            shadowElevation = 8.dp
        ) {
            Column(modifier = Modifier.padding(30.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total Payment", color = Color.Gray, fontSize = 16.sp)
                    Text("$${String.format(Locale.US, "%.2f", totalPrice)}", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = OrangePrimary)
                }

                Spacer(Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (cartItems.isEmpty()) return@Button
                        isCheckingOut = true
                        val order = OrderModel(
                            userId = userId,
                            items = cartItems,
                            totalPrice = totalPrice,
                            address = "Your Location, City", // Should be dynamic in a real app
                            status = "Pending"
                        )
                        orderViewModel.placeOrder(order) { success, msg ->
                            if (success) {
                                cartViewModel.clearCart(userId)
                                Toast.makeText(context, "Order Placed Successfully!", Toast.LENGTH_SHORT).show()
                                onCheckout()
                            } else {
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            }
                            isCheckingOut = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(55.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                    shape = RoundedCornerShape(15.dp),
                    enabled = cartItems.isNotEmpty() && !isCheckingOut
                ) {
                    if (isCheckingOut) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Checkout", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
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
                modifier = Modifier.size(70.dp).clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(15.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.foodName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("$${item.foodPrice}", color = OrangePrimary, fontWeight = FontWeight.SemiBold)
                
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
                Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun CartScreenPreview() {
    FoodRushTheme {
        CartScreen {}
    }
}
