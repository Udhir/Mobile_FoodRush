package com.example.foodrush

import com.example.foodrush.model.CartModel
import com.example.foodrush.repo.CartRepo
import com.example.foodrush.viewmodel.CartViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class CartViewModelTest {
    @Test
    fun addToCart_success_test() {
        val repo = mock<CartRepo>()
        val viewModel = CartViewModel(repo)
        val dummyCartItem = CartModel(userId = "123", foodId = "abc", foodName = "Burger")

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(1)
            callback(true, "Added to cart")
            null
        }.`when`(repo).addToCart(eq(dummyCartItem), any())

        var successResult = false
        var messageResult = ""

        viewModel.addToCart(dummyCartItem) { success, msg ->
            successResult = success
            messageResult = msg
        }

        assertTrue(successResult)
        assertEquals("Added to cart", messageResult)
        verify(repo).addToCart(eq(dummyCartItem), any())
    }
}