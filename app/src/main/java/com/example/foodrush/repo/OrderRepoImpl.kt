package com.example.foodrush.repo

import com.example.foodrush.model.OrderModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OrderRepoImpl : OrderRepo {
    private val database by lazy { FirebaseDatabase.getInstance() }
    private val orderRef by lazy { database.getReference("orders") }

    override fun placeOrder(order: OrderModel, callback: (Boolean, String) -> Unit) {
        val id = orderRef.push().key ?: return callback(false, "Failed to generate ID")
        orderRef.child(id).setValue(order.copy(orderId = id)).addOnCompleteListener {
            if (it.isSuccessful) callback(true, "Order placed") else callback(false, "${it.exception?.message}")
        }
    }

    override fun getOrders(userId: String, callback: (Boolean, String, List<OrderModel>) -> Unit) {
        orderRef.orderByChild("userId").equalTo(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { it.getValue(OrderModel::class.java) }
                callback(true, "fetched", list)
            }
            override fun onCancelled(error: DatabaseError) = callback(false, error.message, emptyList())
        })
    }
}