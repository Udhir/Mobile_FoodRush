package com.example.foodrush

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodrush.Login
import com.example.foodrush.ui.theme.*
import kotlinx.coroutines.delay

class SplashBody : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Splash()
        }
    }
}

@Composable
fun Splash() {

    val context = LocalContext.current
    val activity = context as? Activity

    LaunchedEffect(Unit) {
        delay(3000)

        val intent = Intent(context, Login::class.java)
        context.startActivity(intent)

        activity?.finish() // SAFE now
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OrangePrimary),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = null,
            modifier = Modifier.size(140.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        CircularProgressIndicator(color = Color.White)
    }
}

@Preview
@Composable
fun SplashPreview() {
    Splash()
}