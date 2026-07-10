package com.example.foodrush.repo

import com.example.foodrush.model.FoodModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FoodRepoImpl : Foodrepo {

    private val database = FirebaseDatabase.getInstance()
    private val foodRef = database.getReference("foods")
    private val categoryRef = database.getReference("categories")

    override fun addFood(food: FoodModel, callback: (Boolean, String) -> Unit) {
        val id = foodRef.push().key ?: return callback(false, "Failed to generate ID")
        foodRef.child(id).setValue(food.copy(id = id)).addOnCompleteListener {
            if (it.isSuccessful) callback(true, "Food added") else callback(false, "${it.exception?.message}")
        }
    }

    override fun getAllFood(callback: (Boolean, String, List<FoodModel>) -> Unit) {
        foodRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { it.getValue(FoodModel::class.java) }
                callback(true, "fetched", list)
            }
            override fun onCancelled(error: DatabaseError) = callback(false, error.message, emptyList())
        })
    }

    override fun getFoodByCategory(category: String, callback: (Boolean, String, List<FoodModel>) -> Unit) {
        foodRef.orderByChild("category").equalTo(category)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = snapshot.children.mapNotNull { it.getValue(FoodModel::class.java) }
                    callback(true, "fetched", list)
                }
                override fun onCancelled(error: DatabaseError) = callback(false, error.message, emptyList())
            })
    }

    override fun getFoodById(id: String, callback: (Boolean, String, FoodModel?) -> Unit) {
        foodRef.child(id).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                callback(true, "fetched", snapshot.getValue(FoodModel::class.java))
            }
            override fun onCancelled(error: DatabaseError) = callback(false, error.message, null)
        })
    }

    override fun getAllCategories(callback: (Boolean, String, List<CategoryModel>) -> Unit) {
        categoryRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { it.getValue(CategoryModel::class.java) }
                callback(true, "fetched", list)
            }
            override fun onCancelled(error: DatabaseError) = callback(false, error.message, emptyList())
        })
    }

    override fun deleteFood(id: String, callback: (Boolean, String) -> Unit) {
        foodRef.child(id).removeValue().addOnCompleteListener {
            if (it.isSuccessful) callback(true, "Deleted") else callback(
                false,
                "${it.exception?.message}"
            )
        }
    }

}