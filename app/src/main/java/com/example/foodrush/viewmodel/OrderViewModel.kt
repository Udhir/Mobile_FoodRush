package com.example.foodrush.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.foodrush.model.OrderModel
import com.example.foodrush.repo.OrderRepo

class OrderViewModel(private val repo: OrderRepo) : ViewModel() {
    private val _orders = MutableLiveData<List<OrderModel>>()
    val orders: MutableLiveData<List<OrderModel>> get() = _orders

    private val _allOrders = MutableLiveData<List<OrderModel>>()
    val allOrders: MutableLiveData<List<OrderModel>> get() = _allOrders

    private val _loading = MutableLiveData<Boolean>()
    val loading: MutableLiveData<Boolean> get() = _loading

    fun placeOrder(order: OrderModel, callback: (Boolean, String) -> Unit) {
        repo.placeOrder(order, callback)
    }

    fun getOrders(userId: String) {
        _loading.value = true
        repo.getOrders(userId) { success, _, data ->
            _loading.value = false
            _orders.value = if (success) data else emptyList()
        }
    }

    fun getAllOrders() {
        _loading.value = true
        repo.getAllOrders { success, _, data ->
            _loading.value = false
            _allOrders.value = if (success) data else emptyList()
        }
    }

    fun updateOrderStatus(orderId: String, status: String) {
        repo.updateOrderStatus(orderId, status) { _, _ -> }
    }
}