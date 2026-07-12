package com.example.foodrush.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AdminOrdersActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FoodRushTheme {
                AdminOrdersScreen(onBack = { finish() })
            }
        }
    }
}

@Composable
fun AdminOrdersScreen(onBack: () -> Unit) {
    val orderViewModel = remember { OrderViewModel(OrderRepoImpl()) }

    LaunchedEffect(Unit) {
        orderViewModel.getAllOrders()
    }

    val allOrders by orderViewModel.allOrders.observeAsState(emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8F8))
            .systemBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(OrangePrimary)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Text("Manage All Orders", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        if (allOrders.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No orders to manage.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(allOrders) { order ->
                    AdminOrderCard(order = order, viewModel = orderViewModel)
                }
            }
        }
    }
}

@Composable
fun AdminOrderCard(order: OrderModel, viewModel: OrderViewModel) {
    val dateStr = remember(order.timestamp) {
        SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(order.timestamp))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Order #${order.orderId.takeLast(6)}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(dateStr, color = Color.Gray, fontSize = 12.sp)
            Spacer(Modifier.height(8.dp))

            Text("Delivery Address:", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text(order.address, fontSize = 14.sp)

            Spacer(Modifier.height(8.dp))

            order.items.forEach {
                Text("${it.quantity}x ${it.foodName}", fontSize = 14.sp, color = Color.DarkGray)
            }

            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Total: $${"%.2f".format(order.totalPrice)}", fontWeight = FontWeight.ExtraBold, color = OrangePrimary)

                // ADDED: Simple Button instead of Buggy Dropdown
                if (order.status != "Delivered") {
                    Button(
                        onClick = { viewModel.updateOrderStatus(order.orderId, "Delivered") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("Mark Delivered", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Text("✔ Delivered", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}