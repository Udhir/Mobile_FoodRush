package com.example.foodrush

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.foodrush.ui.theme.FoodRushTheme
import com.example.foodrush.ui.theme.OrangePrimary
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ProfileScreen(navController: NavHostController) {
    // Check if we are in preview mode to avoid Firebase initialization error
    if (LocalInspectionMode.current) {
        ProfileContent(
            userName = "User Name",
            userEmail = "user@example.com",
            onLogout = {},
            navController = navController
        )
    } else {
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        ProfileContent(
            userName = user?.displayName ?: "User Name",
            userEmail = user?.email ?: "user@example.com",
            onLogout = {
                auth.signOut()
                navController.navigate(Screen.Login.route) {
                    popUpTo(0) { inclusive = true }
                }
            },
            navController = navController
        )
    }
}

@Composable
fun ProfileContent(
    userName: String,
    userEmail: String,
    onLogout: () -> Unit,
    navController: NavHostController
) {
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8F8F8))
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(OrangePrimary),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(50.dp), tint = OrangePrimary)
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(userName, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(userEmail, color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                }
            }

            Spacer(Modifier.height(20.dp))

            // Profile Options
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                ProfileOption(Icons.Default.ListAlt, "My Orders") {
                    // Navigate to Orders
                }
                ProfileOption(Icons.Default.Settings, "Settings") {
                    // Navigate to Settings
                }
                ProfileOption(Icons.Default.ExitToApp, "Logout", Color.Red) {
                    onLogout()
                }
            }
        }
    }
}

@Composable
fun ProfileOption(icon: ImageVector, title: String, color: Color = Color.Black, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = color)
            Spacer(Modifier.width(15.dp))
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = color)
            Spacer(Modifier.weight(1f))
            Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    FoodRushTheme {
        ProfileScreen(rememberNavController())
    }
}
