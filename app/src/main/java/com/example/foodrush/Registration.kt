package com.example.foodrush

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.example.foodrush.model.UserModel
import com.example.foodrush.repo.UserRepoImpl
import com.example.foodrush.ui.theme.OrangePrimary
import com.example.foodrush.viewmodel.UserViewModel
import com.example.foodrush.utils.findActivity

class Registration : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { RegistrationBody() }
    }
}

@Composable
fun RegistrationBody() {
    val viewModel = remember { UserViewModel(UserRepoImpl()) }
    var email by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }
    var confirmPasswordVisibility by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val activity = context.findActivity()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OrangePrimary)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color.White
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Join FoodRush",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            Text(
                "Start your food journey today",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(Modifier.height(40.dp))

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.White,
                shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(30.dp)
                ) {
                    Text(
                        "Create Account",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(Modifier.height(25.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Full Name") },
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent
                        )
                    )

                    Spacer(Modifier.height(15.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Email") },
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent
                        )
                    )

                    Spacer(Modifier.height(15.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Password") },
                        shape = RoundedCornerShape(12.dp),
                        visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                                Icon(
                                    painter = painterResource(
                                        if (passwordVisibility) R.drawable.baseline_visibility_24
                                        else R.drawable.outline_visibility_off_24
                                    ),
                                    contentDescription = null
                                )
                            }
                        },
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent
                        )
                    )

                    Spacer(Modifier.height(15.dp))

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Confirm Password") },
                        shape = RoundedCornerShape(12.dp),
                        visualTransformation = if (confirmPasswordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { confirmPasswordVisibility = !confirmPasswordVisibility }) {
                                Icon(
                                    painter = painterResource(
                                        if (confirmPasswordVisibility) R.drawable.baseline_visibility_24
                                        else R.drawable.outline_visibility_off_24
                                    ),
                                    contentDescription = null
                                )
                            }
                        },
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent
                        )
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
                                    val currentUser = FirebaseAuth.getInstance().currentUser
                                    val userId = currentUser?.uid ?: ""
                                    val userModel = UserModel(
                                        id = userId,
                                        name = name,
                                        email = email
                                    )
                                    viewModel.addUser(userId, userModel) { dbSuccess, dbMsg ->
                                        isLoading = false
                                        if (dbSuccess) {
                                            Toast.makeText(context, "Registration Successful", Toast.LENGTH_SHORT).show()
                                            context.startActivity(Intent(context, Login::class.java))
                                            activity?.finish()
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp),
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("SIGN UP", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("Already have an account? ", color = Color.Gray)
                        Text(
                            "Sign In",
                            color = OrangePrimary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                context.startActivity(Intent(context, Login::class.java))
                                activity?.finish()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun RegistrationBodyPreview() {
    RegistrationBody()
}
