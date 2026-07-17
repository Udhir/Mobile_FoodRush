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

class ForgotPasswordTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testForgotPassword_InputAndClick() {
        composeTestRule.setContent {
            ForgotPasswordScreen(rememberNavController(), UserViewModel(UserRepoImpl()))
        }

        composeTestRule.onNodeWithTag("forgotEmailField").performTextInput("test@gmail.com")
        composeTestRule.onNodeWithTag("resetButton").performClick()
    }
}