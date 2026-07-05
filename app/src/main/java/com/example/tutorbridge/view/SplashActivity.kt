package com.example.tutorbridge.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tutorbridge.R
import com.example.tutorbridge.view.ui.theme.TutorBridgeTheme
import com.example.tutorbridge.viewmodel.UserViewModel
import kotlinx.coroutines.delay

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SplashBody()
        }
    }
}

@Composable
fun SplashBody(userViewModel: UserViewModel = viewModel()) {
    val context = LocalContext.current
    val activity = context as? Activity

    LaunchedEffect(Unit) {
        delay(3000)

        if (userViewModel.isLoggedIn()) {
            val destination = if (userViewModel.getRole() == "Teacher") TutorDashboard::class.java else StudentDashboard::class.java
            context.startActivity(Intent(context, destination))
            activity?.finish()
        } else {
            context.startActivity(Intent(context, LoginActivity::class.java))
            activity?.finish()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFF9FAFB),
                        Color(0xFFEAFBF3),
                        Color(0xFFD4F5E9)
                    )
                )
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.tutor_bridge_final),
            contentDescription = "App Logo",
            modifier = Modifier.size(200.dp)
        )

        CircularProgressIndicator(
            color = Color(0xFF16D64D)
        )
    }
}
