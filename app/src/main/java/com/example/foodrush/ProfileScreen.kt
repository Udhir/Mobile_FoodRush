// ProfileScreen.kt (used in the Profile tab)
package com.example.foodrush

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodrush.repo.UserRepoImpl
import com.example.foodrush.ui.theme.OrangePrimary
import com.example.foodrush.view.AddFoodActivity
import com.example.foodrush.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ProfileScreen() {
    val userViewModel = remember { UserViewModel(UserRepoImpl()) }
    val context = LocalContext.current
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    LaunchedEffect(Unit) { userViewModel.getUserById(userId) }
    val user by userViewModel.users.observeAsState(null)

    Column(
        Modifier.fillMaxSize().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(20.dp))
        Box(
            modifier = Modifier
                .size(90.dp)
                .background(OrangePrimary.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Person, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(50.dp))
        }
        Spacer(Modifier.height(16.dp))
        Text(user?.name ?: "", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(user?.email ?: "", fontSize = 14.sp, color = Color.Gray)

        Spacer(Modifier.height(30.dp))

        ProfileOptionRow("Edit Profile") {
            context.startActivity(Intent(context, EditProfileActivity::class.java))
        }
        ProfileOptionRow("My Orders") {
            // handled via bottom nav Orders tab
        }

        // NEW: Add Food option (Admin feature)
        if (user?.isAdmin == true) {
            ProfileOptionRow("Add New Food Item") {
                context.startActivity(Intent(context, AddFoodActivity::class.java))
            }
        }
        Spacer(Modifier.weight(1f))

        Button(
            onClick = {
                userViewModel.logout { _, _ ->
                    context.startActivity(Intent(context, Login::class.java))
                    (context as? android.app.Activity)?.finish()
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.9f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("LOGOUT", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ProfileOptionRow(label: String, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF7F7F7),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() }
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, fontSize = 15.sp)
            Text(">", color = Color.Gray)
        }
    }
}