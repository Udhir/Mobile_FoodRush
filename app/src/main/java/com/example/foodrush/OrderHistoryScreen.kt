// OrderHistoryScreen.kt (used in the Orders tab)
package com.example.foodrush

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodrush.ui.theme.OrangePrimary
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun OrderHistoryScreen() {
    val orderViewModel = remember { OrderViewModel(OrderRepoImpl()) }
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    LaunchedEffect(Unit) { orderViewModel.getOrdersByUser(userId) }
    val orders by orderViewModel.orders.observeAsState(emptyList())

    Column(Modifier.fillMaxSize()) {
        Text("My Orders", fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))

        if (orders.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No orders yet", color = Color.Gray)
            }
        } else {
            LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp)) {
                items(orders) { order -> OrderCard(order) }
            }
        }
    }
}

@Composable
fun OrderCard(order: OrderModel) {
    val dateStr = remember(order.orderDate) {
        SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(order.orderDate))
    }
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFFF7F7F7),
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Order #${order.id.takeLast(6)}", fontWeight = FontWeight.Bold)
                StatusBadge(order.status)
            }
            Spacer(Modifier.height(4.dp))
            Text(dateStr, fontSize = 12.sp, color = Color.Gray)
            Spacer(Modifier.height(8.dp))
            order.items.forEach {
                Text("${it.quantity} x ${it.foodName}", fontSize = 13.sp)
            }
            Spacer(Modifier.height(8.dp))
            Text("Total: $${"%.2f".format(order.totalAmount)}", fontWeight = FontWeight.Bold, color = OrangePrimary)
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val color = when (status) {
        "Delivered" -> Color(0xFF4CAF50)
        "Cancelled" -> Color(0xFFF44336)
        "OnTheWay" -> Color(0xFF2196F3)
        else -> Color(0xFFFF9800)
    }
    Surface(shape = RoundedCornerShape(20.dp), color = color.copy(alpha = 0.15f)) {
        Text(status, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
    }
}