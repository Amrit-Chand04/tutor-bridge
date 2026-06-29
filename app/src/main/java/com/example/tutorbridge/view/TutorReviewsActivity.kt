package com.example.tutorbridge.view

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tutorbridge.model.ReviewModel
import com.example.tutorbridge.view.ui.theme.TutorBridgeTheme
import com.example.tutorbridge.viewmodel.ReviewViewModel

class TutorReviewsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val tutorId = intent.getStringExtra("tutorId") ?: ""
        setContent {
            TutorBridgeTheme {
                TutorReviewsScreen(tutorId = tutorId)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TutorReviewsScreen(tutorId: String) {
    val context = LocalContext.current
    val activity = context as? Activity
    val viewModel: ReviewViewModel = viewModel()
    val reviews by viewModel.reviews.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadReviews(tutorId)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().background(
            brush = Brush.linearGradient(colors = listOf(Color(0xFFF8FAFC), Color(0xFFEAF2FF), Color(0xFFE4FBE8)))
        ),
        containerColor = Color.Transparent,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Reviews", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E)) },
                navigationIcon = {
                    IconButton(onClick = { activity?.finish() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF1A1A2E))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { innerPadding ->

        Box(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 16.dp)) {
            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF0066FF))
                    }
                }
                reviews.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier.size(72.dp).background(Color(0xFFEAF2FF), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Outlined.DateRange, contentDescription = null, tint = Color(0xFF0066FF), modifier = Modifier.size(36.dp))
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Text("No reviews yet", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A2E))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Reviews from students will appear here", fontSize = 13.sp, color = Color.Gray)
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Average Rating", fontSize = 13.sp, color = Color.Gray)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "%.1f / 5.0".format(viewModel.averageRating),
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1A1A2E)
                                        )
                                    }
                                    Text(
                                        text = StarText(viewModel.averageRating.toInt()),
                                        fontSize = 22.sp,
                                        color = Color(0xFFF59E0B)
                                    )
                                }
                            }
                        }
                        items(reviews) { review ->
                            ReviewCard(review = review)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewCard(review: ReviewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(review.studentName.ifBlank { "Student" }, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
                Text(StarText(review.rating), fontSize = 16.sp, color = Color(0xFFF59E0B))
            }
            Spacer(modifier = Modifier.height(6.dp))
            HorizontalDivider(color = Color(0xFFF0F0F0))
            Spacer(modifier = Modifier.height(8.dp))
            Text(review.comment, fontSize = 13.sp, color = Color.Gray)
        }
    }
}

fun StarText(rating: Int): String = "★".repeat(rating.coerceIn(0, 5)) + "☆".repeat((5 - rating).coerceIn(0, 5))
