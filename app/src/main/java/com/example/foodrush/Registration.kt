package com.example.foodrush

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.foodrush.model.UserModel
import com.example.foodrush.repo.UserRepoImpl
import com.example.foodrush.ui.theme.FoodRushTheme
import com.example.foodrush.ui.theme.OrangePrimary
import com.example.foodrush.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth


@Composable
fun RegistrationBody(navController: NavHostController, viewModel: UserViewModel) {
    var email by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }
    var confirmPasswordVisibility by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // ADDED: The solid, visible text field style
    val inputFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = Color(0xFFF5F5F5),
        unfocusedContainerColor = Color(0xFFF5F5F5),
        focusedBorderColor = OrangePrimary,
        unfocusedBorderColor = Color(0xFFE0E0E0),
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black,
        focusedLabelColor = OrangePrimary,
        unfocusedLabelColor = Color.Gray,
        cursorColor = OrangePrimary
    )

    Column(modifier = Modifier.fillMaxSize().background(OrangePrimary)) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(top = 40.dp, bottom = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Join FoodRush", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
            Text("Start your food journey today", fontSize = 16.sp, color = Color.White.copy(alpha = 0.8f))
        }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.White,
            shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(30.dp)
            ) {
                item {
                    Text("Create Account", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(Modifier.height(25.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Full Name") },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                        colors = inputFieldColors // APPLIED COLORS HERE
                    )

                    Spacer(Modifier.height(15.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Email") },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                        colors = inputFieldColors // APPLIED COLORS HERE
                    )

                    Spacer(Modifier.height(15.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Password") },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                        visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                                Icon(painterResource(if (passwordVisibility) R.drawable.baseline_visibility_24 else R.drawable.outline_visibility_off_24), contentDescription = null, tint = Color.Gray)
                            }
                        },
                        colors = inputFieldColors // APPLIED COLORS HERE
                    )

                    Spacer(Modifier.height(15.dp))

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Confirm Password") },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                        visualTransformation = if (confirmPasswordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { confirmPasswordVisibility = !confirmPasswordVisibility }) {
                                Icon(painterResource(if (confirmPasswordVisibility) R.drawable.baseline_visibility_24 else R.drawable.outline_visibility_off_24), contentDescription = null, tint = Color.Gray)
                            }
                        },
                        colors = inputFieldColors // APPLIED COLORS HERE
                    )

                    Spacer(Modifier.height(30.dp))

                    Button(
                        onClick = {
                            if (email.isEmpty() || name.isEmpty() || password.isEmpty()) {
                                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (password != confirmPassword) {
                                Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            isLoading = true
                            viewModel.register(email, password) { success, msg ->
                                if (success) {
                                    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                                    val userModel = com.example.foodrush.model.UserModel(id = userId, name = name, email = email)

                                    viewModel.addUser(userId, userModel) { dbSuccess, dbMsg ->
                                        isLoading = false
                                        if (dbSuccess) {
                                            Toast.makeText(context, "Registration Successful", Toast.LENGTH_SHORT).show()
                                            navController.navigate(Screen.Dashboard.route) {
                                                popUpTo(Screen.Registration.route) { inclusive = true }
                                            }
                                        } else {
                                            Toast.makeText(context, dbMsg, Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } else {
                                    isLoading = false
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(55.dp),
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("SIGN UP", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        Text("Already have an account? ", color = Color.Gray)
                        Text(
                            "Sign In",
                            color = OrangePrimary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { navController.navigate(Screen.Login.route) }
                        )
                    }
                }
            }
        }
    }
}
@Composable
@Preview(showBackground = true)
fun RegistrationBodyPreview(){
    FoodRushTheme {
        RegistrationBody(rememberNavController(), UserViewModel(UserRepoImpl()))
    }
}

