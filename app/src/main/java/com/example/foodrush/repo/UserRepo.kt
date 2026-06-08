package com.example.foodrush.repo


import com.example.foodrush.model.UserModel


interface UserRepo {
    fun login(email: String, password: String,callback:(Boolean, String) -> Unit)

    fun register(email : String, password: String,callback: (Boolean, String) -> Unit)

    fun forgotPassword(email: String, callback: (Boolean, String) -> Unit)

    fun addUser(id: String,model: UserModel,callback: (Boolean, String) -> Unit)

    fun editProfile(id: String, model: UserModel,callback: (Boolean, String) -> Unit)

    fun getAllUser(callback: (Boolean, String, List<UserModel?>) -> Unit)

    fun getUserById(id: String, callback: (Boolean, String, UserModel?) -> Unit)

    fun logout(callback: (Boolean, String) -> Unit)

    fun deleteUser(id: String,callback: (Boolean, String) -> Unit)
}