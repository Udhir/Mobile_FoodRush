package com.example.foodrush.model

data class OrderModel(
    val orderId: String = "",
    val userId: String = "",
    val items: List<CartModel> = emptyList(),
    val totalPrice: Double = 0.0,
    val address: String = "",
    val status: String = "Pending",
    val timestamp: Long = System.currentTimeMillis()
)