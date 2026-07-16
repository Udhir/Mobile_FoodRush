// model/FoodModel.kt
package com.example.foodrush.model

import com.google.firebase.database.PropertyName

data class FoodModel(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val category: String = "",
    val imageUrl: String = "",
    val rating: Double = 4.5,
    @get:PropertyName("available")
    @set:PropertyName("available")
    var isAvailable: Boolean = true
)