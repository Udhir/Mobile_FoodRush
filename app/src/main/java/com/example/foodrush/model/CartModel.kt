package com.example.foodrush.model

data class CartModel(
    val cartId: String = "",
    val userId: String = "",
    val foodId: String = "",
    val foodName: String = "",
    val foodPrice: Double = 0.0,
    val foodImage: String = "",
    var quantity: Int = 1
)