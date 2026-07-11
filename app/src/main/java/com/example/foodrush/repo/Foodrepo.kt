package com.example.foodrush.repo

import com.example.foodrush.model.FoodModel
import com.example.foodrush.model.CategoryModel

interface Foodrepo {

        fun addFood(food: FoodModel, callback: (Boolean, String) -> Unit)
        fun getAllFood(callback: (Boolean, String, List<FoodModel>) -> Unit)
        fun getFoodByCategory(category: String, callback: (Boolean, String, List<FoodModel>) -> Unit)
        fun getFoodById(id: String, callback: (Boolean, String, FoodModel?) -> Unit)
        fun getAllCategories(callback: (Boolean, String, List<CategoryModel>) -> Unit)
        fun deleteFood(id: String, callback: (Boolean, String) -> Unit)
        fun updateFood(id: String, data: Map<String, Any>, callback: (Boolean, String) -> Unit)
    }
