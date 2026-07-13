package com.example.foodrush.repo

import com.example.foodrush.model.CartModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CartRepoImpl : CartRepo {
    private val database by lazy { FirebaseDatabase.getInstance() }
    private val cartRef by lazy { database.getReference("carts") }

    override fun addToCart(cartItem: CartModel, callback: (Boolean, String) -> Unit) {
        val id = cartRef.push().key ?: return callback(false, "Failed to generate ID")
        cartRef.child(id).setValue(cartItem.copy(cartId = id)).addOnCompleteListener {
            if (it.isSuccessful) callback(true, "Added to cart") else callback(
                false,
                "${it.exception?.message}"
            )
        }
    }

    override fun getCartItems(
        userId: String,
        callback: (Boolean, String, List<CartModel>) -> Unit
    ) {
        cartRef.orderByChild("userId").equalTo(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = snapshot.children.mapNotNull { it.getValue(CartModel::class.java) }
                    callback(true, "fetched", list)
                }

                override fun onCancelled(error: DatabaseError) =
                    callback(false, error.message, emptyList())
            })
    }

    override fun updateQuantity(
        cartId: String,
        quantity: Int,
        callback: (Boolean, String) -> Unit
    ) {
        cartRef.child(cartId).child("quantity").setValue(quantity).addOnCompleteListener {
            if (it.isSuccessful) callback(true, "Updated") else callback(
                false,
                "${it.exception?.message}"
            )
        }
    }

    override fun removeFromCart(cartId: String, callback: (Boolean, String) -> Unit) {
        cartRef.child(cartId).removeValue().addOnCompleteListener {
            if (it.isSuccessful) callback(true, "Removed") else callback(
                false,
                "${it.exception?.message}"
            )
        }
    }

    override fun clearCart(userId: String, callback: (Boolean, String) -> Unit) {
        cartRef.orderByChild("userId").equalTo(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach { it.ref.removeValue() }
                    callback(true, "Cart cleared")
                }

                override fun onCancelled(error: DatabaseError) = callback(false, error.message)
            })
    }
}
