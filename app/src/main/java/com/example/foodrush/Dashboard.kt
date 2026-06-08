package com.example.foodrush

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodrush.ui.theme.OrangePrimary

class Dashboard : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { DashboardBody() }
    }
}

@Composable
fun DashboardBody() {
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        bottomBar = { BottomNavigationBar() }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8F8F8))
                .verticalScroll(rememberScrollState())
        ) {
            // Header Section
            HeaderSection()

            // Search Bar
            SearchBar(searchQuery) { searchQuery = it }

            // Categories Section
            CategoriesSection()

            // Promotional Banner
            PromoBanner()

            // Popular Section
            PopularSection()

            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
fun HeaderSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("Deliver to", color = Color.Gray, fontSize = 12.sp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Your Location, City", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
        Box(
            modifier = Modifier
                .size(45.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Notifications, contentDescription = null)
        }
    }
}

@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        placeholder = { Text("Search your favorite food...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = OrangePrimary) },
        shape = RoundedCornerShape(15.dp),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = OrangePrimary
        ),
        singleLine = true
    )
}

@Composable
fun CategoriesSection() {
    val categories = listOf("All", "Burger", "Pizza", "Pasta", "Sushi", "Drinks")
    
    Column(modifier = Modifier.padding(top = 20.dp)) {
        Text(
            "Categories",
            modifier = Modifier.padding(horizontal = 20.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 15.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(categories) { category ->
                CategoryItem(category)
            }
        }
    }
}

@Composable
fun CategoryItem(name: String) {
    val isSelected = name == "All"
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) OrangePrimary else Color.White)
            .clickable { }
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Text(
            name,
            color = if (isSelected) Color.White else Color.Black,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun PromoBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .height(150.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(OrangePrimary)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Get Special Discount", color = Color.White, fontSize = 14.sp)
                Text("Up to 50% OFF", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                Spacer(Modifier.height(10.dp))
                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = OrangePrimary),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 15.dp, vertical = 5.dp)
                ) {
                    Text("Order Now", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
            Image(
                painter = painterResource(R.drawable.burger),
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
fun PopularSection() {
    Column(modifier = Modifier.padding(top = 10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Popular Near You", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text("See All", color = OrangePrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }
        
        Spacer(Modifier.height(10.dp))
        
        // Using a Row with local items since I have limited assets
        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            FoodCard("Beef Burger Deluxe", "Burger King", "4.8", "$12.99", R.drawable.burger)
            FoodCard("Cheese Burger XL", "McDonald's", "4.5", "$10.50", R.drawable.burgerr)
        }
    }
}

@Composable
fun FoodCard(name: String, shop: String, rating: String, price: String, imageRes: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(imageRes),
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(15.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(15.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(shop, color = Color.Gray, fontSize = 13.sp)
                Spacer(Modifier.height(5.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(16.dp))
                    Text(rating, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.padding(start = 4.dp))
                    Text(" (100+)", color = Color.Gray, fontSize = 12.sp)
                }
            }
            Text(price, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = OrangePrimary)
        }
    }
}

@Composable
fun BottomNavigationBar() {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text("Home") },
            selected = true,
            onClick = { },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = OrangePrimary, selectedTextColor = OrangePrimary)
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
            label = { Text("Cart") },
            selected = false,
            onClick = { }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = null) },
            label = { Text("Profile") },
            selected = false,
            onClick = { }
        )
    }
}

@Preview
@Composable
fun DashboardPreview() {
    DashboardBody()
}
