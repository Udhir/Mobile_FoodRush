package com.example.foodrush.repo

import com.example.foodrush.model.OrderModel

interface OrderRepo {
    fun placeOrder(order: OrderModel, callback: (Boolean, String) -> Unit)
    fun getOrders(userId: String, callback: (Boolean, String, List<OrderModel>) -> Unit)
}