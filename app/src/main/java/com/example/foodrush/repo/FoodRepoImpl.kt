package com.example.foodrush.repo

import com.example.foodrush.model.FoodModel
import com.example.foodrush.model.CategoryModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FoodRepoImpl : Foodrepo {

    private val database by lazy { FirebaseDatabase.getInstance() }
    private val foodRef by lazy { database.getReference("foods") }
    private val categoryRef by lazy { database.getReference("categories") }

    override fun addFood(food: FoodModel, callback: (Boolean, String) -> Unit) {
        val id = foodRef.push().key ?: return callback(false, "Failed to generate ID")
        foodRef.child(id).setValue(food.copy(id = id)).addOnCompleteListener {
            if (it.isSuccessful) callback(true, "Food added") else callback(false, "${it.exception?.message}")
        }
    }

    override fun getAllFood(callback: (Boolean, String, List<FoodModel>) -> Unit) {
        foodRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<FoodModel>()
                for (child in snapshot.children) {
                    try {
                        // FIXED: Added check to prevent crash if data is corrupted (e.g. a String instead of an Object)
                        if (child.value is Map<*, *>) {
                            val food = child.getValue(FoodModel::class.java)
                            if (food != null && food.id.isNotEmpty()) {
                                list.add(food)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                callback(true, "fetched", list)
            }
            override fun onCancelled(error: DatabaseError) {
                callback(false, "Cancelled", emptyList())
            }
        })
    }



    override fun getFoodByCategory(category: String, callback: (Boolean, String, List<FoodModel>) -> Unit) {
        foodRef.orderByChild("category").equalTo(category)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<FoodModel>()
                    for (child in snapshot.children) {
                        try {
                            val food = child.getValue(FoodModel::class.java)
                            if (food != null) list.add(food)
                        } catch (e: Exception) {}
                    }
                    callback(true, "fetched", list)
                }
                override fun onCancelled(error: DatabaseError) = callback(false, error.message, emptyList())
            })
    }

    override fun getFoodById(id: String, callback: (Boolean, String, FoodModel?) -> Unit) {
        foodRef.child(id).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    callback(true, "fetched", snapshot.getValue(FoodModel::class.java))
                } catch (e: Exception) {
                    callback(false, "Corrupted data", null)
                }
            }
            override fun onCancelled(error: DatabaseError) = callback(false, error.message, null)
        })
    }

    override fun getAllCategories(callback: (Boolean, String, List<CategoryModel>) -> Unit) {
        categoryRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<CategoryModel>()
                for (child in snapshot.children) {
                    try {
                        val cat = child.getValue(CategoryModel::class.java)
                        if (cat != null) list.add(cat)
                    } catch (e: Exception) {}
                }
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

    override fun updateFood(id: String, data: Map<String, Any>, callback: (Boolean, String) -> Unit) {
        foodRef.child(id).updateChildren(data).addOnCompleteListener {
            if (it.isSuccessful) callback(true, "Updated") else callback(
                false,
                "${it.exception?.message}"
            )
        }
    }
}