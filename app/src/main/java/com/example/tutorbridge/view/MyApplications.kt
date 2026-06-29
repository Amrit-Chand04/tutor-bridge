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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
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
    var editingApplication by remember { mutableStateOf<ApplyTuitionModel?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadMyApplications()
    }

    editingApplication?.let { app ->
        EditApplicationDialog(
            application = app,
            viewModel = viewModel,
            onDismiss = { editingApplication = null },
            onSuccess = { editingApplication = null }
        )
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
                        onEdit = { editingApplication = application },
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
fun EditApplicationDialog(
    application: ApplyTuitionModel,
    viewModel: ApplyTuitionViewModel,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current
    val isLoading by viewModel.isLoading.collectAsState()
    var contactNumber by remember { mutableStateOf(application.contactNumber) }

    val fieldColors = TextFieldDefaults.colors(
        focusedContainerColor = Color(0xFFF8FAFC),
        unfocusedContainerColor = Color(0xFFF8FAFC),
        focusedIndicatorColor = Color(0xFF0066FF),
        unfocusedIndicatorColor = Color(0xFFE0E0E0)
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {

                Text(text = "Edit Application", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))

                Spacer(modifier = Modifier.height(12.dp))

                Column(modifier = Modifier.verticalScroll(rememberScrollState()).weight(1f, fill = false)) {

                    Text(text = "Subject", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.Gray)
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(text = application.subject, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A2E))

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(text = "Grade", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.Gray)
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(text = application.grade, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A2E))

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(text = "Budget", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.Gray)
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(text = "Rs. ${application.budget}", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF24C16B))

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = "Contact Number", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A2E))
                    Spacer(modifier = Modifier.height(3.dp))
                    OutlinedTextField(
                        value = contactNumber,
                        onValueChange = { contactNumber = it.filter { c -> c.isDigit() } },
                        placeholder = { Text("98XXXXXXXX", color = Color.Gray) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = fieldColors
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(42.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF0F0F0))
                    ) {
                        Text("Cancel", color = Color(0xFF1A1A2E), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }

                    Button(
                        onClick = {
                            viewModel.updateApplication(application, contactNumber) { success, msg ->
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                if (success) onSuccess()
                            }
                        },
                        enabled = !isLoading,
                        modifier = Modifier.weight(1f).height(42.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.horizontalGradient(colors = listOf(Color(0xFF0066FF), Color(0xFF24C16B))),
                                    shape = RoundedCornerShape(10.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                            } else {
                                Text("Save", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ApplicationCard(application: ApplyTuitionModel, onEdit: () -> Unit, onDelete: () -> Unit) {

    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(text = "Delete Application", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete this application?") },
            confirmButton = {
                TextButton(onClick = { showDeleteDialog = false; onDelete() }) {
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
            Text(text = "Contact: ${application.contactNumber}", fontSize = 13.sp, color = Color.Gray)

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
                    onClick = onEdit,
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
