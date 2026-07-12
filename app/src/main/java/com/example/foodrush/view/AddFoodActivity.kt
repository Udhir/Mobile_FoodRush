package com.example.foodrush.view

import com.example.foodrush.R
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.foodrush.model.FoodModel
import com.example.foodrush.repo.FoodRepoImpl
import com.example.foodrush.repo.ImageRepoImpl
import com.example.foodrush.ui.theme.FoodRushTheme
import com.example.foodrush.ui.theme.OrangePrimary
import com.example.foodrush.viewmodel.FoodViewModel
import com.example.foodrush.viewmodel.ImageViewModel

class AddFoodActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FoodRushTheme {
                AddFoodScreen(
                    onClose = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFoodScreen(
    foodId: String? = null,
    onClose: () -> Unit,
    foodViewModel: FoodViewModel = viewModel(factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FoodViewModel(FoodRepoImpl()) as T
        }
    }),
    imageViewModel: ImageViewModel = viewModel(factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ImageViewModel(ImageRepoImpl()) as T
        }
    })
) {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var existingImageUrl by remember { mutableStateOf<String?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    var isEditMode by remember { mutableStateOf(false) }

    LaunchedEffect(foodId) {
        if (foodId != null) {
            isEditMode = true
            foodViewModel.repo.getFoodById(foodId) { success, _, food ->
                if (success && food != null) {
                    name = food.name
                    description = food.description
                    price = food.price.toString()
                    category = food.category
                    existingImageUrl = food.imageUrl
                }
            }
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> imageUri = uri }
    )

    // ADDED: Common style for all input fields to make them easily visible
    val inputFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = Color(0xFFF5F5F5), // Light Gray Background
        unfocusedContainerColor = Color(0xFFF5F5F5), // Light Gray Background
        focusedBorderColor = OrangePrimary, // Turns orange when clicked
        unfocusedBorderColor = Color(0xFFE0E0E0), // Soft visible border when not clicked
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black,
        focusedLabelColor = OrangePrimary,
        unfocusedLabelColor = Color.Gray
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OrangePrimary)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(50.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onClose) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    Text(
                        text = if (isEditMode) "Edit Food" else "Add New Food",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                }

                if (isEditMode) {
                    IconButton(onClick = {
                        foodId?.let {
                            foodViewModel.deleteFood(it) { success, msg ->
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                if (success) onClose()
                            }
                        }
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.White,
                shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(25.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFF5F5F5))
                                .clickable {
                                    launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (imageUri != null) {
                                AsyncImage(
                                    model = imageUri,
                                    contentDescription = "Selected Image",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else if (existingImageUrl != null) {
                                AsyncImage(
                                    model = existingImageUrl,
                                    contentDescription = "Existing Image",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.outline_add_photo_alternate_24),
                                        contentDescription = "Upload",
                                        modifier = Modifier.size(50.dp),
                                        tint = Color.Gray
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Tap to pick image from gallery", color = Color.Gray)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(25.dp))

                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Food Name") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = inputFieldColors // ADDED
                        )

                        Spacer(modifier = Modifier.height(15.dp))

                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Description") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = inputFieldColors // ADDED
                        )

                        Spacer(modifier = Modifier.height(15.dp))

                        OutlinedTextField(
                            value = price,
                            onValueChange = { price = it },
                            label = { Text("Price ($)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = inputFieldColors // ADDED
                        )

                        Spacer(modifier = Modifier.height(15.dp))

                        OutlinedTextField(
                            value = category,
                            onValueChange = { category = it },
                            label = { Text("Category (e.g., Burger, Pizza)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = inputFieldColors // ADDED
                        )

                        Spacer(modifier = Modifier.height(40.dp))

                        Button(
                            onClick = {
                                if (name.isEmpty() || price.isEmpty() || category.isEmpty() || (imageUri == null && existingImageUrl == null)) {
                                    Toast.makeText(context, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                isUploading = true
                                val priceDouble = price.toDoubleOrNull() ?: 0.0

                                val saveFood = { finalImageUrl: String ->
                                    val foodData = FoodModel(
                                        id = foodId ?: "",
                                        name = name,
                                        description = description,
                                        price = priceDouble,
                                        category = category,
                                        imageUrl = finalImageUrl,
                                        isAvailable = true
                                    )

                                    if (isEditMode) {
                                        val map = mapOf(
                                            "name" to name,
                                            "description" to description,
                                            "price" to priceDouble,
                                            "category" to category,
                                            "imageUrl" to finalImageUrl
                                        )
                                        foodViewModel.updateFood(foodId!!, map) { success, msg ->
                                            isUploading = false
                                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                            if (success) onClose()
                                        }
                                    } else {
                                        foodViewModel.repo.addFood(foodData) { success, msg ->
                                            isUploading = false
                                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                            if (success) onClose()
                                        }
                                    }
                                }

                                if (imageUri != null) {
                                    imageViewModel.uploadImage(context, imageUri!!) { imageUrl ->
                                        if (imageUrl != null) {
                                            saveFood(imageUrl)
                                        } else {
                                            isUploading = false
                                            Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } else {
                                    saveFood(existingImageUrl!!)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(55.dp),
                            enabled = !isUploading,
                            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (isUploading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("SAVING DATA...", fontWeight = FontWeight.Bold)
                            } else {
                                Text(if (isEditMode) "UPDATE FOOD ITEM" else "ADD FOOD ITEM", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun AddFoodScreenPreview() {
    FoodRushTheme {
        AddFoodScreen(
            onClose = {}
        )
    }
}