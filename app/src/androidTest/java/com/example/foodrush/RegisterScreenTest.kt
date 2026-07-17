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

class RegisterScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testRegisterScreen_InputAndClick() {
        composeTestRule.setContent {
            RegistrationBody(rememberNavController(), UserViewModel(UserRepoImpl()))
        }

        composeTestRule.onNodeWithTag("registerName").performTextInput("Test User")
        composeTestRule.onNodeWithTag("registerEmail").performTextInput("testuser@gmail.com")
        composeTestRule.onNodeWithTag("registerPassword").performTextInput("password123")

        composeTestRule.onNodeWithTag("registerButton").performClick()
    }
}