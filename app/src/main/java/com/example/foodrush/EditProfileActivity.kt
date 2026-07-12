package com.example.foodrush

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.example.foodrush.model.UserModel
import com.example.foodrush.repo.UserRepoImpl
import com.example.foodrush.ui.theme.FoodRushTheme
import com.example.foodrush.ui.theme.OrangePrimary
import com.example.foodrush.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth

class EditProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FoodRushTheme {
                EditProfileScreen(onBack = { finish() })
            }
        }
    }
}

@Composable
fun EditProfileScreen(onBack: () -> Unit) {
    val userViewModel = remember { UserViewModel(UserRepoImpl()) }
    val context = LocalContext.current
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    LaunchedEffect(Unit) {
        userViewModel.getUserById(userId)
    }

    val user by userViewModel.users.observeAsState(null)
    var name by remember { mutableStateOf("") }
    var isUpdating by remember { mutableStateOf(false) }

    LaunchedEffect(user) {
        user?.let { name = it.name }
    }

    // ADDED: The solid, visible text field style for BOTH active and disabled fields
    val inputFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = Color(0xFFF5F5F5),
        unfocusedContainerColor = Color(0xFFF5F5F5),
        disabledContainerColor = Color(0xFFF5F5F5), // Keeps background solid when disabled
        focusedBorderColor = OrangePrimary,
        unfocusedBorderColor = Color(0xFFE0E0E0),
        disabledBorderColor = Color(0xFFE0E0E0), // Visible border when disabled
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black,
        disabledTextColor = Color.Gray, // Gray text for disabled email
        focusedLabelColor = OrangePrimary,
        unfocusedLabelColor = Color.Gray,
        disabledLabelColor = Color.Gray,
        cursorColor = OrangePrimary
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .systemBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(OrangePrimary)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Spacer(Modifier.width(8.dp))
            Text(text = "Edit Profile", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(25.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = inputFieldColors // APPLIED COLORS HERE
            )

            Spacer(Modifier.height(15.dp))

            OutlinedTextField(
                value = user?.email ?: "Loading...",
                onValueChange = {},
                label = { Text("Email Address") },
                enabled = false, // Cannot change email without Re-Auth
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = inputFieldColors // APPLIED COLORS HERE
            )

            Spacer(Modifier.height(40.dp))

            Button(
                onClick = {
                    if (name.isEmpty()) {
                        Toast.makeText(context, "Name cannot be empty", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    isUpdating = true
                    val updatedUser = UserModel(
                        id = userId,
                        name = name,
                        email = user?.email ?: "",
                        password = user?.password ?: "",
                        isAdmin = user?.isAdmin ?: false
                    )

                    userViewModel.editProfile(userId, updatedUser) { success, msg ->
                        isUpdating = false
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        if (success) onBack()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(55.dp),
                enabled = !isUpdating && user != null,
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isUpdating) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("SAVE CHANGES", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}