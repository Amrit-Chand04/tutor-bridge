package com.example.tutorbridge.view

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.foundation.clickable
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tutorbridge.model.ApplyTuitionModel
import com.example.tutorbridge.view.ui.theme.TutorBridgeTheme
import com.example.tutorbridge.viewmodel.ApplyTuitionViewModel
import com.example.tutorbridge.viewmodel.ReviewViewModel

class ViewApplications : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val requestId = intent.getStringExtra("requestId") ?: ""
        val subject = intent.getStringExtra("subject") ?: ""
        setContent {
            TutorBridgeTheme {
                ViewApplicationsScreen(requestId = requestId, subject = subject)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewApplicationsScreen(requestId: String, subject: String) {

    val context = LocalContext.current
    val activity = context as? Activity
    val viewModel: ApplyTuitionViewModel = viewModel()
    val reviewViewModel: ReviewViewModel = viewModel()
    val applications by viewModel.requestApplications.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val myReviews by reviewViewModel.myReviews.collectAsState()
    var isReady by remember { mutableStateOf(false) }
    var reviewingApplication by remember { mutableStateOf<ApplyTuitionModel?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadApplicationsForRequest(requestId) { apps ->
            val acceptedIds = apps.filter { it.status == "accepted" }.map { it.tutorId }
            reviewViewModel.loadMyReviewsForTutors(acceptedIds) {
                isReady = true
            }
        }
    }

    reviewingApplication?.let { app ->
        ReviewDialog(
            tutorId = app.tutorId,
            subject = app.subject,
            existingReview = myReviews[app.tutorId],
            reviewViewModel = reviewViewModel,
            onDismiss = { reviewingApplication = null }
        )
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFFF8FAFC), Color(0xFFEAF2FF), Color(0xFFE4FBE8))
                )
            ),
        containerColor = Color.Transparent,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "Applications", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
                },
                navigationIcon = {
                    IconButton(onClick = { activity?.finish() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF1A1A2E))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            when {
                !isReady -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF0066FF))
                    }
                }
                applications.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .background(Color(0xFFEAF2FF), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.DateRange,
                                    contentDescription = null,
                                    tint = Color(0xFF0066FF),
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(text = "No applications yet", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A2E))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "No tutors have applied for $subject", fontSize = 13.sp, color = Color.Gray)
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
                            Text(
                                text = "${applications.size} applicant${if (applications.size > 1) "s" else ""} for $subject",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                        items(applications) { application ->
                            ApplicationItemCard(
                                application = application,
                                isLoading = isLoading,
                                myReview = myReviews[application.tutorId],
                                onAccept = {
                                    viewModel.acceptApplication(application) { _, msg ->
                                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                    }
                                },
                                onReview = { reviewingApplication = application }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ApplicationItemCard(application: ApplyTuitionModel, isLoading: Boolean, myReview: com.example.tutorbridge.model.ReviewModel? = null, onAccept: () -> Unit, onReview: () -> Unit = {}) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = application.subject, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
                Box(
                    modifier = Modifier
                        .background(
                            when (application.status) {
                                "accepted" -> Color(0xFFEAF8EF)
                                "rejected" -> Color(0xFFFFEEEE)
                                else -> Color(0xFFFFF8E1)
                            },
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = application.status.replaceFirstChar { it.uppercase() },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = when (application.status) {
                            "accepted" -> Color(0xFF24C16B)
                            "rejected" -> Color.Red
                            else -> Color(0xFFF59E0B)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color(0xFFF0F0F0))
            Spacer(modifier = Modifier.height(8.dp))

            if (application.tutorName.isNotBlank()) {
                Text(text = "Tutor Name: ${application.tutorName}", fontSize = 13.sp, color = Color(0xFF1A1A2E), fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(2.dp))
            }
            Text(text = "Contact: ${application.contactNumber}", fontSize = 14.sp, color = Color(0xFF0066FF), fontWeight = FontWeight.SemiBold)

            Spacer(modifier = Modifier.height(12.dp))

            if (application.status != "accepted") {
                Button(
                    onClick = onAccept,
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth().height(40.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues()
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize().background(
                            brush = Brush.horizontalGradient(colors = listOf(Color(0xFF0066FF), Color(0xFF16D64D))),
                            shape = RoundedCornerShape(10.dp)
                        ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                        } else {
                            Text(text = "Accept", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            } else {
                Button(
                    onClick = onReview,
                    modifier = Modifier.fillMaxWidth().height(40.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (myReview != null) Color(0xFFEAF8EF) else Color(0xFFFFF8E1)
                    )
                ) {
                    Text(
                        text = if (myReview != null) "★ View My Review" else "★ Rate Tutor",
                        fontSize = 13.sp,
                        color = if (myReview != null) Color(0xFF24C16B) else Color(0xFFF59E0B),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun ReviewDialog(
    tutorId: String,
    subject: String,
    existingReview: com.example.tutorbridge.model.ReviewModel?,
    reviewViewModel: ReviewViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val isLoading by reviewViewModel.isLoading.collectAsState()

    // mode: "view" | "form"
    var mode by remember { mutableStateOf(if (existingReview == null) "form" else "view") }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    var rating by remember { mutableStateOf(existingReview?.rating ?: 0) }
    var comment by remember { mutableStateOf(existingReview?.comment ?: "") }

    val fieldColors = TextFieldDefaults.colors(
        focusedContainerColor = Color(0xFFF8FAFC),
        unfocusedContainerColor = Color(0xFFF8FAFC),
        focusedIndicatorColor = Color(0xFF0066FF),
        unfocusedIndicatorColor = Color(0xFFE0E0E0)
    )

    if (showDeleteConfirm) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Review", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete your review?") },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = {
                    showDeleteConfirm = false
                    reviewViewModel.deleteReview(tutorId, existingReview!!.reviewId) { success, msg ->
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        if (success) onDismiss()
                    }
                }) { Text("Delete", color = Color.Red, fontWeight = FontWeight.SemiBold) }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel", color = Color(0xFF0066FF))
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {

                Text(
                    text = if (mode == "view") "Your Review" else if (existingReview != null) "Edit Review" else "Rate Tutor",
                    fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(subject, fontSize = 13.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(14.dp))

                if (mode == "view" && existingReview != null) {
                    // View mode — show existing review
                    Text(StarText(existingReview.rating), fontSize = 28.sp, color = Color(0xFFF59E0B))
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = Color(0xFFF0F0F0))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(existingReview.comment, fontSize = 14.sp, color = Color(0xFF1A1A2E))
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { showDeleteConfirm = true },
                            modifier = Modifier.weight(1f).height(42.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEEEE))
                        ) {
                            Text("Delete", color = Color.Red, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }
                        Button(
                            onClick = {
                                rating = existingReview.rating
                                comment = existingReview.comment
                                mode = "form"
                            },
                            modifier = Modifier.weight(1f).height(42.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEAF2FF))
                        ) {
                            Text("Edit", color = Color(0xFF0066FF), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }
                        Button(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f).height(42.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF0F0F0))
                        ) {
                            Text("Close", color = Color(0xFF1A1A2E), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }
                    }

                } else {
                    // Form mode — create or edit
                    Text("Rating", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A2E))
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        (1..5).forEach { star ->
                            Text(
                                text = if (star <= rating) "★" else "☆",
                                fontSize = 30.sp,
                                color = if (star <= rating) Color(0xFFF59E0B) else Color(0xFFCCCCCC),
                                modifier = Modifier.clickable { rating = star }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Comment", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A2E))
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = comment,
                        onValueChange = { comment = it },
                        placeholder = { Text("Write your experience...", color = Color.Gray) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5,
                        colors = fieldColors
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = { if (existingReview != null) mode = "view" else onDismiss() },
                            modifier = Modifier.weight(1f).height(42.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF0F0F0))
                        ) {
                            Text("Cancel", color = Color(0xFF1A1A2E), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }
                        Button(
                            onClick = {
                                if (existingReview != null) {
                                    reviewViewModel.updateReview(existingReview, rating, comment) { success, msg ->
                                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                        if (success) onDismiss()
                                    }
                                } else {
                                    reviewViewModel.submitReview(tutorId, "", rating, comment) { success, msg ->
                                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                        if (success) onDismiss()
                                    }
                                }
                            },
                            enabled = !isLoading,
                            modifier = Modifier.weight(1f).height(42.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            contentPadding = PaddingValues()
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize().background(
                                    brush = Brush.horizontalGradient(colors = listOf(Color(0xFF0066FF), Color(0xFF24C16B))),
                                    shape = RoundedCornerShape(10.dp)
                                ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                                } else {
                                    Text("Submit", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
