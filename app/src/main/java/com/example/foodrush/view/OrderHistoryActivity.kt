package com.example.foodrush.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodrush.model.OrderModel
import com.example.foodrush.repo.OrderRepoImpl
import com.example.foodrush.ui.theme.FoodRushTheme
import com.example.foodrush.ui.theme.OrangePrimary
import com.example.foodrush.viewmodel.OrderViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderHistoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FoodRushTheme {
                OrderHistoryScreen(onBack = { finish() })
            }
        }
    }
}

@Composable
fun OrderHistoryScreen(onBack: () -> Unit) {
    val orderViewModel = remember { OrderViewModel(OrderRepoImpl()) }
    val orders by orderViewModel.orders.observeAsState(emptyList())
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        orderViewModel.getUserOrders(userId)
        kotlinx.coroutines.delay(800)
        isLoading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // --- CUSTOM SIMPLE TOP BAR ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
            }
            Text("My Orders", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.Black)
        }

        // Add a tiny shadow-like divider under the top bar
        HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 1.dp)

        // --- MAIN CONTENT ---
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = OrangePrimary)
            }
        } else if (orders.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("You have no past orders.", color = Color.Gray, fontSize = 16.sp)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
                items(orders) { order ->
                    UserOrderCard(order = order)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun UserOrderCard(order: OrderModel) {
    val date = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(order.timestamp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Order ID: ${order.orderId.takeLast(6)}", fontWeight = FontWeight.Bold)
                Text(date, fontSize = 12.sp, color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Address: ${order.address}", fontSize = 14.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Items: ${order.items.joinToString { "${it.foodName} (x${it.quantity})" }}", fontSize = 14.sp, color = Color.DarkGray)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Total: NPR ${order.totalPrice}", fontWeight = FontWeight.Bold, color = OrangePrimary)

                Surface(
                    color = if (order.status == "Delivered") Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = order.status,
                        color = if (order.status == "Delivered") Color(0xFF2E7D32) else OrangePrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}