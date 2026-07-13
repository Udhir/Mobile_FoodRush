package com.example.foodrush.repo

import com.example.foodrush.model.CartModel

interface CartRepo {
    fun addToCart(cartItem: CartModel, callback: (Boolean, String) -> Unit)
    fun getCartItems(userId: String, callback: (Boolean, String, List<CartModel>) -> Unit)
    fun updateQuantity(cartId: String, quantity: Int, callback: (Boolean, String) -> Unit)
    fun removeFromCart(cartId: String, callback: (Boolean, String) -> Unit)
    fun clearCart(userId: String, callback: (Boolean, String) -> Unit)
}

