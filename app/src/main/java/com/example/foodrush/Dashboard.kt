package com.example.foodrush

// FIXED: Perfectly imported Coil without the "3"

import android.widget.Toast
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import coil3.compose.AsyncImagePainter
import com.example.foodrush.model.FoodModel
import com.example.foodrush.model.OrderModel
import com.example.foodrush.repo.OrderRepoImpl
import com.example.foodrush.repo.UserRepoImpl
import com.example.foodrush.ui.theme.OrangePrimary
import com.example.foodrush.viewmodel.FoodViewModel
import com.example.foodrush.viewmodel.OrderViewModel
import com.example.foodrush.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardBody(navController: NavHostController, viewModel: FoodViewModel) {
    var selectedTab by remember { mutableStateOf(0) }

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
            if (selectedTab == 0 && currentUser?.isAdmin == true) {
                FloatingActionButton(
                    onClick = { navController.navigate("add_food") },
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
            when (selectedTab) {
                0 -> HomeContent(navController, viewModel, currentUser?.name ?: "Guest", currentUser?.isAdmin ?: false)
                1 -> CartScreen(navController = navController, onCheckout = { selectedTab = 0 })
                2 -> OrderHistoryTab()
                3 -> ProfileScreen(navController)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(navController: NavHostController, viewModel: FoodViewModel, userName: String, isAdmin: Boolean) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    val context = LocalContext.current

    val foods by viewModel.foods.observeAsState(emptyList())
    val categories by viewModel.categories.observeAsState(emptyList())
    val loading by viewModel.loading.observeAsState(false)

    LaunchedEffect(Unit) {
        viewModel.getAllFood()
        viewModel.getAllCategories()
    }

    val filteredFoods = foods.filter { food ->
        val matchesSearch = if (searchQuery.isBlank()) true else food.name.contains(searchQuery, ignoreCase = true)
        val matchesCategory = if (selectedCategory == "All") true else food.category == selectedCategory
        matchesSearch && matchesCategory
    }

    val chunkedFoods = filteredFoods.chunked(2)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA)),
        contentPadding = PaddingValues(bottom = 20.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, bottom = 10.dp, start = 20.dp, end = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Hello, $userName \uD83D\uDC4B", color = Color.Black, fontSize = 14.sp)
                    Text("What do you want to eat?", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = Color.Black)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // ADDED: Manual Refresh Button
                    Box(
                        modifier = Modifier
                            .padding(end = 10.dp)
                            .size(45.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .clickable {
                                viewModel.getAllFood()
                                Toast.makeText(context, "Refreshing Menu...", Toast.LENGTH_SHORT).show()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = OrangePrimary)
                    }

                    Box(
                        modifier = Modifier
                            .size(45.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .clickable { Toast.makeText(context, "No new notifications", Toast.LENGTH_SHORT).show() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.NotificationsNone, contentDescription = "Notifications", tint = OrangePrimary)
                    }
                }
            }
        }

        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 20.dp),
                placeholder = { Text("Search your favorite food...", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = OrangePrimary) },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = OrangePrimary,
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    cursorColor = OrangePrimary
                ),
                singleLine = true
            )
        }

        item {
            val displayCategories = listOf("All") + categories.map { it.name }
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
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
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 15.dp, horizontal = 20.dp)
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
                        painter = painterResource(R.drawable.burger), // This is fine for the static banner
                        contentDescription = null,
                        modifier = Modifier.size(100.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }

        item {
            Text(
                "Popular Menu",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Black,
                modifier = Modifier.padding(vertical = 10.dp, horizontal = 20.dp)
            )
        }

        if (loading) {
            item {
                Box(Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = OrangePrimary)
                }
            }
        }

        items(chunkedFoods) { rowItems ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                for (food in rowItems) {
                    // KEY ensures the grid forces a re-render when data changes
                    key(food.id) {
                        Box(modifier = Modifier.weight(1f)) {
                            ModernFoodCard(
                                food = food,
                                isAdmin = isAdmin,
                                onCardClick = {
                                    navController.navigate("food_detail/${food.id}")
                                },
                                onEditClick = {
                                    navController.navigate("add_food?foodId=${food.id}")
                                }
                            )
                        }
                    }
                }

                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun ModernFoodCard(food: FoodModel, isAdmin: Boolean, onCardClick: () -> Unit, onEditClick: () -> Unit) {
    LaunchedEffect(food.imageUrl) {
        Log.d("FoodRushDebug", "Loading image for ${food.name}: ${food.imageUrl}")
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(120.dp).background(Color(0xFFEEEEEE))) {

                // FIXED: Robust image loading with state handling for Coil 3
                SubcomposeAsyncImage(
                    model = food.imageUrl,
                    contentDescription = food.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                ) {
                    // Collect the StateFlow as a Compose state
                    val state by painter.state.collectAsState()

                    when (state) {
                        is AsyncImagePainter.State.Loading -> {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = OrangePrimary, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                            }
                        }
                        is AsyncImagePainter.State.Error, is AsyncImagePainter.State.Empty -> {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Warning, contentDescription = "Error", tint = Color.LightGray)
                            }
                        }
                        else -> {
                            SubcomposeAsyncImageContent()
                        }
                    }
                }

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

                if (isAdmin) {
                    IconButton(
                        onClick = onEditClick,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(32.dp)
                            .background(Color.White.copy(alpha = 0.9f), CircleShape)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = OrangePrimary, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Column(Modifier.padding(12.dp)) {
                Text(
                    text = food.name,
                    color = Color.Black,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = food.category,
                    color = Color.DarkGray,
                    fontSize = 12.sp
                )
                Spacer(Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("NPR ${"%.2f".format(food.price)}", color = OrangePrimary, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)

                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(OrangePrimary)
                            .clickable { onCardClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

// FIXED: Completely removed the empty AsyncImage function that was shadowing the real library!

@Composable
fun OrderHistoryTab() {
    val orderViewModel = remember { OrderViewModel(OrderRepoImpl()) }
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    LaunchedEffect(Unit) {
        if (userId.isNotEmpty()) {
            orderViewModel.getOrders(userId)
        }
    }

    val orders by orderViewModel.orders.observeAsState(emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        Text(
            "My Orders",
            modifier = Modifier.padding(top = 40.dp, start = 20.dp, end = 20.dp, bottom = 10.dp),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 24.sp,
            color = Color.Black
        )

        if (orders.isEmpty()) {
            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("You have no orders yet.", color = OrangePrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(orders) { order ->
                    OrderHistoryCard(order)
                }
            }
        }
    }
}

@Composable
fun OrderHistoryCard(order: OrderModel) {
    val dateStr = remember(order.timestamp) {
        SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(order.timestamp))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Order #${order.orderId.takeLast(6)}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                StatusBadge(order.status)
            }
            Spacer(Modifier.height(4.dp))
            Text(dateStr, fontSize = 12.sp, color = OrangePrimary, fontWeight = FontWeight.Bold)

            Spacer(Modifier.height(8.dp))

            order.items.forEach {
                Text("${it.quantity} x ${it.foodName}", fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.Medium)
            }

            Spacer(Modifier.height(8.dp))
            Text("Total: NPR ${"%.2f".format(order.totalPrice)}", fontWeight = FontWeight.ExtraBold, color = OrangePrimary, fontSize = 16.sp)
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val color = when (status) {
        "Delivered" -> Color(0xFF4CAF50)
        "Cancelled" -> Color(0xFFF44336)
        "OnTheWay" -> Color(0xFF2196F3)
        else -> Color(0xFFFF9800)
    }
    Surface(shape = RoundedCornerShape(20.dp), color = color.copy(alpha = 0.15f)) {
        Text(
            text = status,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun BottomNavigationBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text("Home") },
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = OrangePrimary, selectedTextColor = OrangePrimary, indicatorColor = OrangePrimary.copy(alpha = 0.1f))
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
            label = { Text("Cart") },
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = OrangePrimary, selectedTextColor = OrangePrimary, indicatorColor = OrangePrimary.copy(alpha = 0.1f))
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.ListAlt, contentDescription = null) },
            label = { Text("Orders") },
            selected = selectedTab == 2,
            onClick = { onTabSelected(2) },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = OrangePrimary, selectedTextColor = OrangePrimary, indicatorColor = OrangePrimary.copy(alpha = 0.1f))
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = null) },
            label = { Text("Profile") },
            selected = selectedTab == 3,
            onClick = { onTabSelected(3) },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = OrangePrimary, selectedTextColor = OrangePrimary, indicatorColor = OrangePrimary.copy(alpha = 0.1f))
        )
    }
}