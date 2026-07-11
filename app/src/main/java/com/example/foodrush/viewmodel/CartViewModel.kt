package com.example.foodrush.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.foodrush.model.CartModel
import com.example.foodrush.repo.CartRepo

class CartViewModel(private val repo: CartRepo) : ViewModel() {
    private val _cartItems = MutableLiveData<List<CartModel>>()
    val cartItems: MutableLiveData<List<CartModel>> get() = _cartItems

    private val _loading = MutableLiveData<Boolean>()
    val loading: MutableLiveData<Boolean> get() = _loading

    fun addToCart(cartItem: CartModel, callback: (Boolean, String) -> Unit) {
        repo.addToCart(cartItem, callback)
    }

    fun getCartItems(userId: String) {
        _loading.value = true
        repo.getCartItems(userId) { success, _, data ->
            _loading.value = false
            _cartItems.value = if (success) data else emptyList()
        }
    }

    fun updateQuantity(cartId: String, quantity: Int) {
        repo.updateQuantity(cartId, quantity) { _, _ -> }
    }

    fun removeFromCart(cartId: String) {
        repo.removeFromCart(cartId) { _, _ -> }
    }

    fun clearCart(userId: String) {
        repo.clearCart(userId) { _, _ -> }
    }
}