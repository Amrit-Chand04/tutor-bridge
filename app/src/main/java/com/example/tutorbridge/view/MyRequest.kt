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
import androidx.compose.ui.window.Dialog
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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tutorbridge.model.CreateRequestModel
import com.example.tutorbridge.view.ui.theme.TutorBridgeTheme
import com.example.tutorbridge.viewmodel.RequestViewModel

class MyRequest : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TutorBridgeTheme {
                MyRequestScreen()
            }
        }
    }
}

@Composable
fun MyRequestScreen() {

    val context = LocalContext.current
    val viewModel: RequestViewModel = viewModel()
    val requests by viewModel.requests.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var editingRequest by remember { mutableStateOf<CreateRequestModel?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadMyRequests()
    }

    editingRequest?.let { req ->
        EditRequestDialog(
            request = req,
            viewModel = viewModel,
            onDismiss = { editingRequest = null },
            onSuccess = {
                editingRequest = null
                viewModel.loadMyRequests()
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {

        Text(
            text = "My Requests",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A2E),
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 0.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            if (isLoading) {
                item {
                    Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF0066FF))
                    }
                }
            } else if (requests.isEmpty()) {
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
                            Text(text = "No requests yet", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A2E))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Create a request to find your tutor", fontSize = 13.sp, color = Color.Gray)
                        }
                    }
                }
            } else {
                items(requests) { request ->
                    RequestCard(
                        request = request,
                        onEdit = { editingRequest = request },
                        onDelete = {
                            viewModel.deleteRequest(request.requestId) { _, msg ->
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
fun EditRequestDialog(
    request: CreateRequestModel,
    viewModel: RequestViewModel,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current
    val isLoading by viewModel.isLoading.collectAsState()

    var subject by remember { mutableStateOf(request.subject) }
    var grade by remember { mutableStateOf(request.grade) }
    var preferredGender by remember { mutableStateOf(request.preferredGender) }
    var location by remember { mutableStateOf(request.location) }
    var budget by remember { mutableStateOf(request.budget) }
    var preferredTime by remember { mutableStateOf(request.preferredTime) }
    var description by remember { mutableStateOf(request.description) }
    var contactNumber by remember { mutableStateOf(request.contactNumber) }

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

                Text(text = "Edit Request", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))

                Spacer(modifier = Modifier.height(12.dp))

                Column(modifier = Modifier.verticalScroll(rememberScrollState()).weight(1f, fill = false)) {

                    Text(text = "Subject", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A2E))
                    Spacer(modifier = Modifier.height(3.dp))
                    OutlinedTextField(value = subject, onValueChange = { subject = it }, singleLine = true, shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth(), colors = fieldColors)

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "Grade / Class", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A2E))
                    Spacer(modifier = Modifier.height(3.dp))
                    OutlinedTextField(value = grade, onValueChange = { grade = it }, singleLine = true, shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth(), colors = fieldColors)

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "Preferred Gender", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A2E))
                    Spacer(modifier = Modifier.height(3.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Gray.copy(alpha = 0.06f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf("Male", "Female", "Any").forEach { gender ->
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                RadioButton(selected = preferredGender == gender, onClick = { preferredGender = gender }, colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF0066FF)))
                                Text(gender, fontSize = 11.sp, color = Color(0xFF1A1A2E))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "Location", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A2E))
                    Spacer(modifier = Modifier.height(3.dp))
                    OutlinedTextField(value = location, onValueChange = { location = it }, singleLine = true, shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth(), colors = fieldColors)

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "Budget (Rs.)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A2E))
                    Spacer(modifier = Modifier.height(3.dp))
                    OutlinedTextField(value = budget, onValueChange = { budget = it.filter { c -> c.isDigit() } }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth(), colors = fieldColors)

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "Preferred Time", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A2E))
                    Spacer(modifier = Modifier.height(3.dp))
                    OutlinedTextField(value = preferredTime, onValueChange = { preferredTime = it }, singleLine = true, shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth(), colors = fieldColors)

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "Description", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A2E))
                    Spacer(modifier = Modifier.height(3.dp))
                    OutlinedTextField(value = description, onValueChange = { description = it }, minLines = 2, maxLines = 3, shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth(), colors = fieldColors)

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "Contact Number", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A2E))
                    Spacer(modifier = Modifier.height(3.dp))
                    OutlinedTextField(value = contactNumber, onValueChange = { contactNumber = it.filter { c -> c.isDigit() } }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth(), colors = fieldColors)
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
                            viewModel.updateRequest(
                                request.requestId, request.userId,
                                subject, grade, preferredGender,
                                location, budget, preferredTime,
                                description, contactNumber
                            ) { success, msg ->
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
fun RequestCard(request: CreateRequestModel, onEdit: () -> Unit, onDelete: () -> Unit) {

    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(text = "Delete Request", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete this request?") },
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
                Text(text = request.subject, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
                Text(text = "Rs. ${request.budget}", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF24C16B))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Grade: ${request.grade}", fontSize = 13.sp, color = Color.Gray)
            Text(text = "Gender: ${request.preferredGender}", fontSize = 13.sp, color = Color.Gray)
            Text(text = "Location: ${request.location}", fontSize = 13.sp, color = Color.Gray)
            Text(text = "Time: ${request.preferredTime}", fontSize = 13.sp, color = Color.Gray)

            if (request.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = request.description, fontSize = 13.sp, color = Color.Gray, maxLines = 2)
            }

            Text(text = "Contact: ${request.contactNumber}", fontSize = 13.sp, color = Color.Gray)

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
