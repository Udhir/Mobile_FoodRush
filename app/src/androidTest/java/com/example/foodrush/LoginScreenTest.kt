package com.example.foodrush

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.rememberNavController
import com.example.foodrush.repo.UserRepoImpl
import com.example.foodrush.viewmodel.UserViewModel
import org.junit.Rule
import org.junit.Test

class LoginScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testLoginScreen_InputAndClick() {
        composeTestRule.setContent {
            LoginBody(rememberNavController(), UserViewModel(UserRepoImpl()))
        }

        // Exact pattern from Sir's video
        composeTestRule.onNodeWithTag("emailField").performTextInput("test@gmail.com")
        composeTestRule.onNodeWithTag("passwordField").performTextInput("123456")

        composeTestRule.onNodeWithTag("loginButton").performClick()
    }
}