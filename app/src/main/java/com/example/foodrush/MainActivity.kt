package com.example.foodrush

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.foodrush.ui.theme.FoodRushTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Keyboard will now successfully push the screen up!
        setContent {
            FoodRushTheme {
                AppNavigation() // This grabs your navigation from Navigation.kt
            }
        }
    }
}