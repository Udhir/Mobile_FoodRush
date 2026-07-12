package com.example.foodrush

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodrush.model.OrderModel
import com.example.foodrush.repo.CartRepoImpl
import com.example.foodrush.repo.OrderRepoImpl
import com.example.foodrush.ui.theme.FoodRushTheme
import com.example.foodrush.ui.theme.OrangePrimary
import com.example.foodrush.viewmodel.CartViewModel
import com.example.foodrush.viewmodel.OrderViewModel
import com.google.firebase.auth.FirebaseAuth
import java.util.Locale

class CheckoutActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FoodRushTheme {
                CheckoutScreen(onBack = { finish() })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val cartViewModel = remember { CartViewModel(CartRepoImpl()) }
    val orderViewModel = remember { OrderViewModel(OrderRepoImpl()) }
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val cartItems by cartViewModel.cartItems.observeAsState(emptyList())

    var address by remember { mutableStateOf("") }
    var isPlacingOrder by remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            cartViewModel.getCartItems(userId)
        }
    }

    val totalPrice = cartItems.sumOf { it.foodPrice * it.quantity }

    // ADDED: Common style for input field visibility!
    val inputFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = Color(0xFFF5F5F5), // Light Gray Background
        unfocusedContainerColor = Color(0xFFF5F5F5), // Light Gray Background
        focusedBorderColor = OrangePrimary,
        unfocusedBorderColor = Color(0xFFE0E0E0), // Soft visible border
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black,
        focusedLabelColor = OrangePrimary,
        unfocusedLabelColor = Color.Gray
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF8F9FA))
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("Shipping Address", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(10.dp))

            // APPLIED THE COLORS HERE
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Enter your delivery address") },
                shape = RoundedCornerShape(12.dp),
                colors = inputFieldColors
            )

            Spacer(modifier = Modifier.height(30.dp))
            Text("Order Summary", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(10.dp))

            cartItems.forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("${item.foodName} x ${item.quantity}", color = Color.Gray)
                    Text("$${String.format(Locale.US, "%.2f", item.foodPrice * item.quantity)}")
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 15.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text(
                    "$${String.format(Locale.US, "%.2f", totalPrice)}",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    color = OrangePrimary
                )
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = {
                    if (address.isBlank()) {
                        Toast.makeText(context, "Please enter an address", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    isPlacingOrder = true
                    val order = OrderModel(
                        userId = userId,
                        items = cartItems,
                        totalPrice = totalPrice,
                        address = address
                    )
                    orderViewModel.placeOrder(order) { success, message ->
                        isPlacingOrder = false
                        if (success) {
                            Toast.makeText(context, "Order placed successfully!", Toast.LENGTH_SHORT).show()
                            cartViewModel.clearCart(userId)

                            // Route to Success Screen
                            context.startActivity(android.content.Intent(context, OrderSuccessActivity::class.java))
                            onBack()
                        } else {
                            Toast.makeText(context, "Error: $message", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(55.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                shape = RoundedCornerShape(16.dp),
                enabled = !isPlacingOrder && cartItems.isNotEmpty()
            ) {
                if (isPlacingOrder) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Place Order", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}