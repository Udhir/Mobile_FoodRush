// CheckoutActivity.kt
package com.example.foodrush

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodrush.ui.theme.FoodRushTheme
import com.example.foodrush.ui.theme.OrangePrimary
import com.google.firebase.auth.FirebaseAuth

class CheckoutActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { 
            FoodRushTheme {
                CheckoutScreen() 
            }
        }
    }
}

@Composable
fun CheckoutScreen() {
    val cartViewModel = remember { CartViewModel(CartRepoImpl()) }
    val orderViewModel = remember { OrderViewModel(OrderRepoImpl()) }
    val context = LocalContext.current
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    LaunchedEffect(Unit) { cartViewModel.getCartItems(userId) }
    val cartItems by cartViewModel.cartItems.observeAsState(emptyList())
    val total = cartItems.sumOf { it.price * it.quantity }

    var address by remember { mutableStateOf("") }
    var isPlacing by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Text("Checkout", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Delivery Address") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(Modifier.height(20.dp))
        Text("Payment Method: Cash on Delivery", fontSize = 14.sp)

        Spacer(Modifier.height(20.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Total Amount", fontSize = 16.sp)
            Text("$${"%.2f".format(total)}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = OrangePrimary)
        }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = {
                if (address.isEmpty()) {
                    Toast.makeText(context, "Please enter your address", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                if (cartItems.isEmpty()) {
                    Toast.makeText(context, "Your cart is empty", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                isPlacing = true
                val order = OrderModel(
                    userId = userId,
                    items = cartItems,
                    totalAmount = total,
                    address = address,
                    status = "Pending"
                )
                orderViewModel.placeOrder(order) { success, msg ->
                    isPlacing = false
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    if (success) {
                        cartViewModel.clearCart(userId) { _, _ -> }
                        context.startActivity(Intent(context, OrderSuccessActivity::class.java))
                        (context as? android.app.Activity)?.finish()
                    }
                }
            },
            enabled = !isPlacing,
            modifier = Modifier.fillMaxWidth().height(55.dp),
            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(if (isPlacing) "PLACING ORDER..." else "PLACE ORDER", fontWeight = FontWeight.Bold)
        }
    }
}