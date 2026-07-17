package com.example.foodrush

import com.example.foodrush.repo.UserRepo
import com.example.foodrush.viewmodel.UserViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class RegisterViewModelTest {
    @Test
    fun register_success_test() {
        val repo = mock<UserRepo>()
        val viewModel = UserViewModel(repo)

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(2)
            callback(true, "Registration success")
            null
        }.`when`(repo).register(eq("newuser@gmail.com"), eq("pass123"), any())

        var successResult = false
        var messageResult = ""

        viewModel.register("newuser@gmail.com", "pass123") { success, msg ->
            successResult = success
            messageResult = msg
        }

        assertTrue(successResult)
        assertEquals("Registration success", messageResult)
        verify(repo).register(eq("newuser@gmail.com"), eq("pass123"), any())
    }
}
