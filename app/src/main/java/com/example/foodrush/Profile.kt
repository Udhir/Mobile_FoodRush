package com.example.foodrush

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.foodrush.repo.UserRepoImpl
import com.example.foodrush.ui.theme.OrangePrimary
import com.example.foodrush.view.AdminOrdersActivity
import com.example.foodrush.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlin.jvm.java

@Composable
fun ProfileScreen(navController: NavHostController) {
    val userViewModel = remember { UserViewModel(UserRepoImpl()) }
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid ?: ""

    LaunchedEffect(Unit) { userViewModel.getUserById(userId) }
    val user by userViewModel.users.observeAsState(null)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(
                    color = OrangePrimary,
                    shape = RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = 20.dp)) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(50.dp))
                }
                Spacer(Modifier.height(12.dp))
                Text(user?.name ?: "Loading...", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text(user?.email ?: "", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
            }
        }

        Spacer(Modifier.height(20.dp))

        // Profile Options
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text("General", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 10.dp))

            ProfileOptionRow(Icons.Default.Settings, "Edit Profile") {
                context.startActivity(Intent(context, EditProfileActivity::class.java))
            }

            ProfileOptionRow(Icons.Default.ListAlt, "My Order History") {
                context.startActivity(Intent(context, EditProfileActivity::class.java))
            }

            if (user?.isAdmin == true) {
                Spacer(Modifier.height(20.dp))
                Text("Admin Panel", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 10.dp))

                ProfileOptionRow(Icons.Default.Add, "Add New Food Item") {
                    navController.navigate(Screen.AddFood.route)
                }

                ProfileOptionRow(Icons.Default.Edit, "Manage Food Items") {
                    navController.navigate(Screen.AdminFoodList.route)
                }


                ProfileOptionRow(Icons.Default.ListAlt, "Manage All Orders") {
                    context.startActivity(Intent(context, AdminOrdersActivity::class.java))
                }
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = {
                    auth.signOut()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(55.dp).padding(bottom = 20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEBEE), contentColor = Color.Red),
                shape = RoundedCornerShape(15.dp),
                elevation = ButtonDefaults.buttonElevation(0.dp)
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("LOGOUT", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun ProfileOptionRow(icon: ImageVector, label: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = OrangePrimary)
            Spacer(Modifier.width(15.dp))
            Text(label, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.Black)
            Spacer(Modifier.weight(1f))
            Text(">", color = Color.Gray, fontWeight = FontWeight.Bold)
        }
    }
}