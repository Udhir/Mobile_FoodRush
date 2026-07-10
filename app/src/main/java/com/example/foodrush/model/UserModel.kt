package com.example.foodrush.model

import com.google.firebase.database.PropertyName

data class UserModel(
    val id : String = "",
    val name : String = "",
    val email : String = "",
    val password : String = "",
    @get:PropertyName("isAdmin")
    @set:PropertyName("isAdmin")
    var isAdmin: Boolean = false
){
    fun toMap() : Map<String, Any>{
        return mapOf(
            "name" to name,
            "email" to email,
            "password" to password,
            "isAdmin" to isAdmin
        )
    }
}