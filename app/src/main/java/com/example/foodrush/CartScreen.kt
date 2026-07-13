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
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.foodrush.model.CartModel
import com.example.foodrush.repo.CartRepoImpl
import com.example.foodrush.ui.theme.OrangePrimary
import com.example.foodrush.viewmodel.CartViewModel
import com.google.firebase.auth.FirebaseAuth
import java.util.Locale

@Composable
fun CartScreen(navController: NavHostController, onCheckout: () -> Unit) {
    val context = LocalContext.current
    val cartViewModel = remember { CartViewModel(CartRepoImpl()) }
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    // Starts as empty while loading from Firebase
    val cartItems by cartViewModel.cartItems.observeAsState(emptyList())

    // NEW LOGIC: We track if the cart EVER had items in it during this visit
    var hasEverHadItems by remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            cartViewModel.getCartItems(userId)
        }
    }

    // THE FIX: Only navigate home if we HAD items, and now we don't (because you deleted them)
    LaunchedEffect(cartItems) {
        if (cartItems.isNotEmpty()) {
            hasEverHadItems = true // Yes, we have items!
        } else if (hasEverHadItems && cartItems.isEmpty()) {
            // We HAD items, but now it's empty (User deleted the last item). Go home.
            navController.navigate(Screen.Dashboard.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    val totalPrice = cartItems.sumOf { it.foodPrice * it.quantity }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF8F9FA))) {
        // Title
        Text(
            text = "My Cart",
            modifier = Modifier.padding(20.dp),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 28.sp,
            color = Color.Black
        )

        // Show empty message if cart is empty, otherwise show the list
        if (cartItems.isEmpty()) {
            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Your cart is empty \uD83D\uDE22",
                    color = Color.Gray,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            // LazyColumn uses weight(1f) to fill available space and leave room at the bottom
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(cartItems) { item ->
                    CartItemCard(item, cartViewModel)
                }
            }
        }

        // Bottom Checkout Surface - Pin to the bottom
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
            shadowElevation = 20.dp
        ) {
            Column(modifier = Modifier.padding(25.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total Payment", color = Color.Gray, fontSize = 16.sp)
                    Text("NPR ${String.format(Locale.US, "%.2f", totalPrice)}", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = OrangePrimary)
                }

                Spacer(Modifier.height(15.dp))

                Button(
                    onClick = {
                        if (cartItems.isEmpty()) return@Button
                        context.startActivity(Intent(context, CheckoutActivity::class.java))
                    },
                    modifier = Modifier.fillMaxWidth().height(55.dp),
                    colors = ButtonDefaults.buttonColors(
                        // Make button gray if cart is empty, orange if it has items
                        containerColor = if (cartItems.isEmpty()) Color.LightGray else OrangePrimary
                    ),
                    shape = RoundedCornerShape(16.dp),
                    enabled = cartItems.isNotEmpty() // Disable button if cart is empty
                ) {
                    Text("PROCEED TO CHECKOUT", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun CartItemCard(item: CartModel, viewModel: CartViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(15.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image
            AsyncImage(
                model = item.foodImage,
                contentDescription = null,
                modifier = Modifier.size(60.dp).clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(10.dp))

            // Name and Price + Controls
            Column(modifier = Modifier.weight(1f)) {
                Text(item.foodName, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.Black)
                Text("NPR ${item.foodPrice}", color = OrangePrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 5.dp)) {
                    IconButton(
                        onClick = { if (item.quantity > 1) viewModel.updateQuantity(item.cartId, item.quantity - 1) },
                        modifier = Modifier.size(24.dp).background(Color(0xFFEEEEEE), CircleShape)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(14.dp))
                    }

                    Text(item.quantity.toString(), modifier = Modifier.padding(horizontal = 8.dp), fontWeight = FontWeight.Bold)

                    IconButton(
                        onClick = { viewModel.updateQuantity(item.cartId, item.quantity + 1) },
                        modifier = Modifier.size(24.dp).background(OrangePrimary, CircleShape)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                    }
                }
            }

            // Explicit Delete Button
            IconButton(
                onClick = { viewModel.removeFromCart(item.cartId) },
                modifier = Modifier.size(40.dp).background(Color(0xFFFFEBEE), CircleShape)
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
            }
        }
    }
}