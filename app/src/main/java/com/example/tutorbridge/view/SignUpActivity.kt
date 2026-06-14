package com.example.tutorbridge.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tutorbridge.R
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tutorbridge.model.UserModel
import com.example.tutorbridge.view.ui.theme.TutorBridgeTheme
import com.example.tutorbridge.viewmodel.UserViewModel


class SignUpActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TutorBridgeTheme {
                SignUp()
            }
        }
    }
}

@Composable
fun SignUp() {

    val userViewModel: UserViewModel = viewModel()
    val context = LocalContext.current

    // State Variables
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Column(
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
            )
            .verticalScroll(scrollState)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(15.dp)
        ) {
            Spacer(modifier = Modifier.height(50.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.tutor_bridge_final),
                    contentDescription = "TutorBridge Logo",
                    modifier = Modifier.size(130.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(25.dp)
                    )
                    .padding(20.dp)
            ) {
                Text(
                    text = "Create Account!",
                    style = TextStyle(
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // FULL NAME FIELD
                Text(text = "Full Name", fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Enter your full name") },
                    colors = TextFieldDefaults.colors(
                        unfocusedIndicatorColor = Color.Transparent,
                        unfocusedContainerColor = Color.Gray.copy(alpha = 0.1f),
                        focusedContainerColor = Color.Gray.copy(alpha = 0.1f),
                        focusedIndicatorColor = Color.Blue,
                    )
                )

                Spacer(modifier = Modifier.height(15.dp))

                // EMAIL FIELD
                Text(text = "Email Address", fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Enter your email") },
                    colors = TextFieldDefaults.colors(
                        unfocusedIndicatorColor = Color.Transparent,
                        unfocusedContainerColor = Color.Gray.copy(alpha = 0.1f),
                        focusedContainerColor = Color.Gray.copy(alpha = 0.1f),
                        focusedIndicatorColor = Color.Blue,
                    )
                )


                Spacer(modifier = Modifier.height(15.dp))

                // PASSWORD FIELD
                Text(text = "Password", fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Enter your password") },
                    colors = TextFieldDefaults.colors(
                        unfocusedIndicatorColor = Color.Transparent,
                        unfocusedContainerColor = Color.Gray.copy(alpha = 0.1f),
                        focusedContainerColor = Color.Gray.copy(alpha = 0.1f),
                        focusedIndicatorColor = Color.Blue,
                    ),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                painter = if (passwordVisible) painterResource(R.drawable.outline_visibility_24)
                                else painterResource(R.drawable.outline_visibility_off_24),
                                contentDescription = "Toggle password visibility",
                                tint = Color(0xFF2E7D32)
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(15.dp))

                // CONFIRM PASSWORD FIELD
                Text(text = "Confirm Password", fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Confirm your password") },
                    colors = TextFieldDefaults.colors(
                        unfocusedIndicatorColor = Color.Transparent,
                        unfocusedContainerColor = Color.Gray.copy(alpha = 0.1f),
                        focusedContainerColor = Color.Gray.copy(alpha = 0.1f),
                        focusedIndicatorColor = Color.Blue,
                    ),
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                painter = if (confirmPasswordVisible) painterResource(R.drawable.outline_visibility_24)
                                else painterResource(R.drawable.outline_visibility_off_24),
                                contentDescription = "Toggle password visibility",
                                tint = Color(0xFF2E7D32)
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // SIGN UP BUTTON
                Button(
                    onClick = {
                        when {
                            fullName.isBlank() -> Toast.makeText(context, "Full name is required", Toast.LENGTH_SHORT).show()

                            email.isBlank() -> Toast.makeText(context, "Email is required", Toast.LENGTH_SHORT).show()


                            password.isBlank() -> Toast.makeText(context, "Password is required", Toast.LENGTH_SHORT).show()

                            confirmPassword.isBlank() -> Toast.makeText(context, "Confirm password is required", Toast.LENGTH_SHORT).show()

                            password != confirmPassword -> Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()

                            else -> {

                                userViewModel.register(email, password) { success, message, uid ->

                                    if (success) {

                                        val user = UserModel(
                                            uid = uid,
                                            fullName = fullName,
                                            email = email,
                                            password = password
                                        )

                                        userViewModel.addUser(uid, user) { addSuccess, addMessage ->

                                            if (addSuccess) {

                                                Toast.makeText(
                                                    context,
                                                    "Signup Successful",
                                                    Toast.LENGTH_LONG
                                                ).show()

                                                fullName = ""
                                                email = ""
                                                password = ""
                                                confirmPassword = ""

                                                passwordVisible = false
                                                confirmPasswordVisible = false

                                            } else {
                                                Toast.makeText(context, addMessage, Toast.LENGTH_LONG).show()
                                            }
                                        }

                                    } else {
                                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    contentPadding = PaddingValues()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(Color(0xFF0066FF), Color(0xFF16D64D))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Sign Up",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // BACK TO LOGIN LINK
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("Already have an account? ")
                    Text(
                        text = "Login",
                        color = Color(0xFF0066FF),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SignUpPreview() {
    TutorBridgeTheme {
        SignUp()
    }
}