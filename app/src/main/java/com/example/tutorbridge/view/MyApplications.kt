package com.example.tutorbridge.view

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tutorbridge.model.ApplyTuitionModel
import com.example.tutorbridge.view.ui.theme.TutorBridgeTheme
import com.example.tutorbridge.viewmodel.ApplyTuitionViewModel

class MyApplications : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TutorBridgeTheme {
                MyApplicationsScreen()
            }
        }
    }
}

@Composable
fun MyApplicationsScreen() {

    val context = LocalContext.current
    val viewModel: ApplyTuitionViewModel = viewModel()
    val applications by viewModel.myApplications.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadMyApplications()
    }

    Column(modifier = Modifier.fillMaxSize()) {

        Text(
            text = "My Applications",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A2E),
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 0.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (isLoading) {
                item {
                    Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF0066FF))
                    }
                }
            } else if (applications.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
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
                            Text(text = "Apply for tuition requests to see them here", fontSize = 13.sp, color = Color.Gray)
                        }
                    }
                }
            } else {
                items(applications) { application ->
                    ApplicationCard(
                        application = application,
                        onDelete = {
                            viewModel.deleteApplication(application.applicationId) { _, msg ->
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ApplicationCard(application: ApplyTuitionModel, onDelete: () -> Unit) {

    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(text = "Delete Application", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete this application?") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    onDelete()
                }) {
                    Text("Delete", color = Color.Red, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel", color = Color(0xFF0066FF))
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White
        )
    }

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
                Text(text = application.subject, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
                Text(text = "Rs. ${application.budget}", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF24C16B))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Grade: ${application.grade}", fontSize = 13.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(6.dp))

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

            Spacer(modifier = Modifier.height(12.dp))

            Row {
                Button(
                    onClick = { },
                    modifier = Modifier.weight(1f).height(40.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0066FF))
                ) {
                    Text(text = "Edit", fontSize = 13.sp, color = Color.White)
                }

                Spacer(modifier = Modifier.width(10.dp))

                Button(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.weight(1f).height(40.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text(text = "Delete", fontSize = 13.sp, color = Color.White)
                }
            }
        }
    }
}
