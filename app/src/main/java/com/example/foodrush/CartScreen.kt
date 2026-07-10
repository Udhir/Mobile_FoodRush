// CartScreen.kt (composable used inside Dashboard's Cart tab)
package com.example.foodrush

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import coil.compose.AsyncImage
import com.example.foodrush.ui.theme.OrangePrimary
import com.google.firebase.auth.FirebaseAuth

@Composable
fun CartScreen() {
    val cartViewModel = remember { CartViewModel(CartRepoImpl()) }
    val context = LocalContext.current
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    LaunchedEffect(Unit) { cartViewModel.getCartItems(userId) }
    val cartItems by cartViewModel.cartItems.observeAsState(emptyList())
    val total = cartItems.sumOf { it.price * it.quantity }

    Column(Modifier.fillMaxSize()) {
        Text(
            "My Cart",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        if (cartItems.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Your cart is empty", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(cartItems) { item ->
                    CartItemRow(
                        item = item,
                        onIncrease = { cartViewModel.updateQuantity(userId, item.id, item.quantity + 1) },
                        onDecrease = { cartViewModel.updateQuantity(userId, item.id, item.quantity - 1) },
                        onRemove = { cartViewModel.removeFromCart(userId, item.id) }
                    )
                }
            }

            Surface(shadowElevation = 8.dp, color = Color.White) {
                Column(Modifier.padding(16.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total", fontSize = 16.sp, color = Color.Gray)
                        Text("$${"%.2f".format(total)}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = {
                            val intent = Intent(context, CheckoutActivity::class.java)
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth().height(55.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("CHECKOUT", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemRow(item: CartModel, onIncrease: () -> Unit, onDecrease: () -> Unit, onRemove: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFFF7F7F7),
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
    ) {
        Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = item.foodImage,
                contentDescription = item.foodName,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(64.dp).clip(RoundedCornerShape(10.dp)).background(Color.White)
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(item.foodName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("$${"%.2f".format(item.price)}", color = OrangePrimary, fontSize = 13.sp)
            }
            IconButton(onClick = onDecrease) { Text("-", fontSize = 18.sp) }
            Text("${item.quantity}", fontSize = 14.sp)
            IconButton(onClick = onIncrease) { Text("+", fontSize = 18.sp) }
            TextButton(onClick = onRemove) { Text("Remove", color = Color.Red, fontSize = 12.sp) }
        }
    }
}