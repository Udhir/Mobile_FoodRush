package com.example.foodrush.repo

import com.example.foodrush.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.collections.toMap

class UserRepoImpl : UserRepo {
    val auth = FirebaseAuth.getInstance()

    val database = FirebaseDatabase.getInstance()

    val ref = database.getReference("users")

    override fun login(
        email: String,
        password: String,
        callback: (Boolean, String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback(true, "Login Success")
                } else {
                    callback(false, "${it.exception?.message}")
                }
            }
    }

    override fun register(
        email: String,
        password: String,
        callback: (Boolean, String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback(true, "Registration Success")
                } else {
                    callback(false, "${it.exception?.message}")
                }
            }
    }

    override fun forgotPassword(
        email: String,
        callback: (Boolean, String) -> Unit
    ) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback(true,"Reset Link sent to $email")
                } else {
                    callback(false,"${it.exception?.message}")
                }
            }
    }

    override fun addUser(
        id: String,
        model: UserModel,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(id).setValue(model).addOnCompleteListener { if(it.isSuccessful){
            callback(true,"User Registered")
        }else{
            callback(false,"${it.exception?.message}")
        }
        }
    }

    override fun editProfile(
        id: String,
        model: UserModel,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(id).updateChildren(model.toMap()).addOnCompleteListener {
            if (it.isSuccessful){
                callback(true,"Profile updated")
            }else{
                callback(false,"${it.exception?.message}")
            }
        }
    }

    override fun getAllUser(callback: (Boolean, String, List<UserModel?>) -> Unit) {
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val allUsers = mutableListOf<UserModel>()
                if(snapshot.exists()){
                    for(user in snapshot.children){
                        val data = user.getValue(UserModel::class.java)
                        if(data != null){
                            allUsers.add(data)
                        }
                    }
                }
                callback(true,"fetched",allUsers)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false,error.message,emptyList())
            }
        })
    }

    override fun getUserById(
        id: String,
        callback: (Boolean, String, UserModel?) -> Unit
    ) {
        ref.child(id).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val user = snapshot.getValue(UserModel::class.java)
                    callback(true,"user fetched",user)
                } else {
                    callback(false, "User not found", null)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false,"${error.message}",null)
            }
        })
    }

    override fun logout(callback: (Boolean, String) -> Unit) {
        try{
            auth.signOut()
            callback(true,"Logout successful")
        }catch (e: Exception) {
            callback(false,e.toString())
        }
    }

    override fun deleteUser(
        id: String,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(id).removeValue().addOnCompleteListener {
            if(it.isSuccessful){
                callback(true,"Account deleted successfully")
            }else{
                callback(false,"${it.exception?.message}")

            }
        }
    }
    }

