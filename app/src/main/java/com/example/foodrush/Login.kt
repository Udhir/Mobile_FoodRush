package com.example.foodrush

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import com.example.foodrush.repo.UserRepoImpl
import com.example.foodrush.ui.theme.FoodRushTheme
import com.example.foodrush.ui.theme.OrangePrimary
import com.example.foodrush.viewmodel.UserViewModel
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginBody(navController: NavHostController, viewModel: UserViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var visibility by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // ADDED: The solid, visible text field style from AddFoodActivity
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
        // Header
        Column(
            modifier = Modifier.fillMaxWidth().padding(top = 40.dp, bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("FoodRush", fontSize = 42.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
            Text("Deliciousness Delivered", fontSize = 16.sp, color = Color.White.copy(alpha = 0.8f))
        }

        // Bottom White Card
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
                    Text("Welcome Back", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Text("Login to your account", fontSize = 14.sp, color = Color.Gray)

                    Spacer(Modifier.height(30.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Email") },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                        trailingIcon = { Icon(painterResource(R.drawable.baseline_email_24), contentDescription = null, tint = Color.Gray) },
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
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                        visualTransformation = if (visibility) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { visibility = !visibility }) {
                                Icon(painterResource(if (visibility) R.drawable.baseline_visibility_24 else R.drawable.outline_visibility_off_24), contentDescription = null, tint = Color.Gray)
                            }
                        },
                        colors = inputFieldColors // APPLIED COLORS HERE
                    )

                    Text(
                        "Forgot Password?",
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.End)
                            .padding(top = 8.dp)
                            .clickable { navController.navigate(Screen.ForgotPassword.route) },
                        color = OrangePrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )

                    Spacer(Modifier.height(30.dp))

                    Button(
                        onClick = {
                            if (email.isEmpty() || password.isEmpty()) {
                                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            isLoading = true
                            viewModel.login(email, password) { success, msg ->
                                isLoading = false
                                if (success) {
                                    Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                                    navController.navigate(Screen.Dashboard.route) {
                                        popUpTo(Screen.Login.route) { inclusive = true }
                                    }
                                } else {
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
                            Text("LOGIN", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        Text("Don't have an account? ", color = Color.Gray)
                        Text(
                            "Sign Up",
                            color = OrangePrimary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { navController.navigate(Screen.Registration.route) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun LoginBodyPreview(){
    FoodRushTheme {
        LoginBody(rememberNavController(), UserViewModel(UserRepoImpl()))
    }
}

