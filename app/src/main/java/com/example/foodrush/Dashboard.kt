package com.example.foodrush

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.foodrush.model.FoodModel
import com.example.foodrush.repo.UserRepoImpl
import com.example.foodrush.ui.theme.OrangePrimary
import com.example.foodrush.viewmodel.FoodViewModel
import com.example.foodrush.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun DashboardBody(navController: NavHostController, viewModel: FoodViewModel) {
    // Track selected tab (0 = Home, 1 = Cart, 2 = Profile)
    var selectedTab by remember { mutableStateOf(0) }

    // Fetch User to check Admin Status
    val userViewModel = remember { UserViewModel(UserRepoImpl()) }
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) userViewModel.getUserById(userId)
    }

    val currentUser by userViewModel.users.observeAsState(null)

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        },
        floatingActionButton = {
            // SECURITY: Only show the Add Food FAB if the user is an ADMIN
            if (selectedTab == 0 && currentUser?.isAdmin == true) {
                FloatingActionButton(
                    onClick = { navController.navigate(Screen.AddFood.route) },
                    containerColor = OrangePrimary,
                    contentColor = Color.White,
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Food")
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            // Switch screens dynamically inside the main Scaffold
            when (selectedTab) {
                0 -> HomeContent(navController, viewModel, currentUser?.name ?: "Guest")
                1 -> CartScreen(onCheckout = { selectedTab = 0 })
                2 -> ProfileScreen(navController)
            }
        }
    }
}

@Composable
fun HomeContent(navController: NavHostController, viewModel: FoodViewModel, userName: String) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val foods by viewModel.foods.observeAsState(emptyList())
    val categories by viewModel.categories.observeAsState(emptyList())
    val loading by viewModel.loading.observeAsState(false)

    LaunchedEffect(Unit) {
        viewModel.getAllFood()
        viewModel.getAllCategories()
    }

    // Filter Logic based on Search and Category selection
    val filteredFoods = foods.filter { food ->
        val matchesSearch = if (searchQuery.isBlank()) true else food.name.contains(searchQuery, ignoreCase = true)
        val matchesCategory = if (selectedCategory == "All") true else food.category == selectedCategory
        matchesSearch && matchesCategory
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // Welcome Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, start = 20.dp, end = 20.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Hello, $userName \uD83D\uDC4B", color = Color.Gray, fontSize = 14.sp)
                Text("What do you want to eat?", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = Color.Black)
            }
            Box(
                modifier = Modifier
                    .size(45.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.NotificationsNone, contentDescription = "Notifications", tint = OrangePrimary)
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp),
            placeholder = { Text("Search your favorite food...", color = Color.Gray) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = OrangePrimary) },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = OrangePrimary,
                unfocusedBorderColor = Color.Transparent
            ),
            singleLine = true
        )

        // Horizontal Category Scrolling
        val displayCategories = listOf("All") + categories.map { it.name }
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(displayCategories) { category ->
                val isSelected = selectedCategory == category
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) OrangePrimary else Color.White)
                        .clickable { selectedCategory = category }
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = category,
                        color = if (isSelected) Color.White else Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // Beautiful Promo Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .height(140.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF212121))
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Special Offer", color = OrangePrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("Get 50% OFF\non your first order", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                }
                Image(
                    painter = painterResource(R.drawable.burger), // Make sure you have this image in your drawable folder!
                    contentDescription = null,
                    modifier = Modifier.size(100.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }

        Text(
            "Popular Menu",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Color.Black,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
        )

        if (loading) {
            Box(Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = OrangePrimary)
            }
        }

        // Display Firebase Foods in a 2-Column Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(15.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredFoods) { food ->
                ModernFoodCard(food = food, onClick = {
                    navController.navigate(Screen.FoodDetail.createRoute(food.id))
                })
            }
        }
    }
}

@Composable
fun ModernFoodCard(food: FoodModel, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(120.dp)) {
                // AsyncImage automatically handles the Firebase Storage URL!
                AsyncImage(
                    model = food.imageUrl,
                    contentDescription = food.name,
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.burger), // Shows while loading
                    error = painterResource(R.drawable.burger),       // Shows if Firebase link breaks
                    modifier = Modifier.fillMaxSize().background(Color(0xFFEEEEEE))
                )
                // Floating Rating Pill
                Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.9f))
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                        .align(Alignment.TopStart),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(14.dp))
                    Text(food.rating.toString(), fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 4.dp))
                }
            }

            Column(Modifier.padding(12.dp)) {
                Text(food.name, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(food.category, color = Color.Gray, fontSize = 12.sp)
                Spacer(Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("$${"%.2f".format(food.price)}", color = OrangePrimary, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                    // Mini Quick-Add Button (Decorative for now, leads to details screen)
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(OrangePrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = { Icon(if (selectedTab == 0) Icons.Default.Home else Icons.Default.Home, contentDescription = null) },
            label = { Text("Home") },
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = OrangePrimary, selectedTextColor = OrangePrimary, indicatorColor = OrangePrimary.copy(alpha = 0.1f))
        )
        NavigationBarItem(
            icon = { Icon(if (selectedTab == 1) Icons.Default.ShoppingCart else Icons.Default.ShoppingCart, contentDescription = null) },
            label = { Text("Cart") },
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = OrangePrimary, selectedTextColor = OrangePrimary, indicatorColor = OrangePrimary.copy(alpha = 0.1f))
        )
        NavigationBarItem(
            icon = { Icon(if (selectedTab == 2) Icons.Default.Person else Icons.Default.PersonOutline, contentDescription = null) },
            label = { Text("Profile") },
            selected = selectedTab == 2,
            onClick = { onTabSelected(2) },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = OrangePrimary, selectedTextColor = OrangePrimary, indicatorColor = OrangePrimary.copy(alpha = 0.1f))
        )
    }
}