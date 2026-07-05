package com.example.tutorbridge.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import com.example.tutorbridge.R
import com.example.tutorbridge.model.CreateRequestModel
import com.example.tutorbridge.view.ui.theme.TutorBridgeTheme
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.text.input.KeyboardType
import com.example.tutorbridge.viewmodel.ApplyTuitionViewModel
import com.example.tutorbridge.viewmodel.RequestViewModel
import com.example.tutorbridge.viewmodel.UserViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel

class TutorDashboard : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TutorBridgeTheme {
                TutorDashboardScreen()
            }
        }
    }
}
@Composable
fun TutorDashboardScreen() {

    var selectedIndex by remember { mutableStateOf(0) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFF8FAFC),
                        Color(0xFFEAF2FF),
                        Color(0xFFE4FBE8)
                    )
                )
            ),
        containerColor = Color.Transparent,
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets,
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 10.dp)
                    .clip(RoundedCornerShape(24.dp)),
                containerColor = Color.White,
                tonalElevation = 6.dp
            ) {
                val items = listOf(
                    "Home" to Icons.Default.Home,
                    "My Applications" to Icons.AutoMirrored.Filled.List,
                    "Settings" to Icons.Default.Settings
                )
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = { selectedIndex = index },
                        icon = {
                            Icon(
                                item.second,
                                contentDescription = item.first,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = { Text(item.first, fontSize = 12.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF24C16B),
                            selectedTextColor = Color(0xFF24C16B),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                            indicatorColor = Color(0xFFEAF8EF)
                        )
                    )
                }
            }
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when (selectedIndex) {
                0 -> TutorScreen()
                1 -> MyApplicationsScreen()
                2 -> SettingsScreen()
            }
        }
    }
}

@Composable
fun TutorScreen(
    userViewModel: UserViewModel = viewModel(),
    requestViewModel: RequestViewModel = viewModel(),
    applyViewModel: ApplyTuitionViewModel = viewModel()
) {
    val context = LocalContext.current
    val user by userViewModel.user.collectAsState()
    val isUserLoading by userViewModel.isUserLoading.collectAsState()
    val requests by requestViewModel.requests.collectAsState()
    val isLoading by requestViewModel.isLoading.collectAsState()
    val applyLoading by applyViewModel.isLoading.collectAsState()
    val appliedRequestIds by applyViewModel.appliedRequestIds.collectAsState()
    val filteredRequests = requests.filter { it.requestId !in appliedRequestIds }

    LaunchedEffect(Unit) {
        userViewModel.loadCurrentUser()
        requestViewModel.loadAllRequests()
        applyViewModel.loadAppliedRequestIds()
    }

    if (isUserLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFF0066FF))
        }
        return@TutorScreen
    }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {

        // Fixed top section
        Row(
            modifier = Modifier.fillMaxWidth().height(90.dp).padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.tutor_bridge),
                contentDescription = "Logo",
                modifier = Modifier.size(80.dp).clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEAF2FF))
                        .clickable { context.startActivity(Intent(context, NotificationActivity::class.java)) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = Color(0xFF0066FF), modifier = Modifier.size(22.dp))
                }

                Spacer(modifier = Modifier.width(12.dp))

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEAF2FF))
                        .clickable { context.startActivity(Intent(context, ProfileUpdate::class.java)) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = "Profile", tint = Color(0xFF0066FF), modifier = Modifier.size(22.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Welcome Back,", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = user?.fullName ?: "", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Apply for suitable tuition requests", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))

        Spacer(modifier = Modifier.height(20.dp))

        // Bordered scrollable section
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(width = 2.dp, color = Color(0xFFD0E4FF), shape = RoundedCornerShape(16.dp))
                .padding(12.dp)
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF0066FF))
                }

            } else if (filteredRequests.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "No requests found", color = Color.Gray, fontSize = 15.sp)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 12.dp)
                ) {
                    items(filteredRequests) { request ->
                        TutorRequestCard(
                            request = request,
                            isApplyLoading = applyLoading,
                            onApply = { contactNumber ->
                                applyViewModel.apply(request, contactNumber) { success, msg ->
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                    if (success) applyViewModel.loadAppliedRequestIds()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TutorRequestCard(request: CreateRequestModel, isApplyLoading: Boolean = false, onApply: (String) -> Unit) {

    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var contactNumber by remember { mutableStateOf("") }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(text = "Apply for this tuition?", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1A1A2E))
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Subject: ${request.subject}", fontSize = 14.sp, color = Color(0xFF1A1A2E))
                    Text(text = "Grade: ${request.grade}", fontSize = 14.sp, color = Color(0xFF1A1A2E))
                    Text(text = "Monthly Budget: Rs. ${request.budget}", fontSize = 14.sp, color = Color(0xFF1A1A2E))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Your Phone Number", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A2E))
                    OutlinedTextField(
                        value = contactNumber,
                        onValueChange = { contactNumber = it.filter { c -> c.isDigit() } },
                        placeholder = { Text("98XXXXXXXX", color = Color.Gray) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF8FAFC),
                            unfocusedContainerColor = Color(0xFFF8FAFC),
                            focusedIndicatorColor = Color(0xFF0066FF),
                            unfocusedIndicatorColor = Color(0xFFE0E0E0)
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (contactNumber.isBlank()) {
                            Toast.makeText(context, "Phone number is required", Toast.LENGTH_SHORT).show()
                        } else {
                            showDialog = false
                            onApply(contactNumber)
                            contactNumber = ""
                        }
                    },
                    enabled = !isApplyLoading,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues()
                ) {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .background(
                                brush = Brush.horizontalGradient(colors = listOf(Color(0xFF0066FF), Color(0xFF24C16B))),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .padding(horizontal = 20.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isApplyLoading) {
                            CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
                        } else {
                            Text("Apply", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false; contactNumber = "" }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE8F0FF)),
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

            Button(
                onClick = { showDialog = true },
                modifier = Modifier.fillMaxWidth().height(42.dp),
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
                    Text(text = "Apply", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TutorDashboardPreview() {
    TutorBridgeTheme {
        TutorDashboardScreen()
    }
}


