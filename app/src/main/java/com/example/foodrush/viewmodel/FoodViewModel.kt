package com.example.foodrush.viewmodel


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.foodrush.model.FoodModel
import com.example.foodrush.model.CategoryModel
import com.example.foodrush.repo.Foodrepo

class FoodViewModel(val repo: Foodrepo) : ViewModel() {

    private val _foods = MutableLiveData<List<FoodModel>>()
    val foods: MutableLiveData<List<FoodModel>> get() = _foods

    private val _categories = MutableLiveData<List<CategoryModel>>()
    val categories: MutableLiveData<List<CategoryModel>> get() = _categories

    private val _loading = MutableLiveData<Boolean>()
    val loading: MutableLiveData<Boolean> get() = _loading

    fun getAllFood() {
        _loading.value = true
        repo.getAllFood { success, _, data ->
            _loading.value = false
            _foods.value = if (success) data else emptyList()
        }
    }


    fun getAllCategories() {
        repo.getAllCategories { success, _, data ->
            _categories.value = if (success) data else emptyList()
        }
    }

    fun getFoodByCategory(category: String) {
        _loading.value = true
        repo.getFoodByCategory(category) { success, _, data ->
            _loading.value = false
            _foods.value = if (success) data else emptyList()
        }
    }

    fun deleteFood(id: String, callback: (Boolean, String) -> Unit) {
        repo.deleteFood(id, callback)
    }

    fun updateFood(id: String, data: Map<String, Any>, callback: (Boolean, String) -> Unit) {
        repo.updateFood(id, data, callback)
    }
}