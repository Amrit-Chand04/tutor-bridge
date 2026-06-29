package com.example.tutorbridge.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.tutorbridge.model.UserModel
import com.example.tutorbridge.repo.UserRepo
import com.example.tutorbridge.repo.UserRepoImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UserViewModel : ViewModel() {

    private val repo: UserRepo = UserRepoImpl()
    var message = mutableStateOf("")

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _user = MutableStateFlow<UserModel?>(null)
    val user: StateFlow<UserModel?> = _user

    fun loadCurrentUser() {
        repo.getCurrentUser { success, userData ->
            if (success && userData != null) {
                _user.value = userData
            }
        }
    }

    fun register(
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String,
        role: String,
        callback: (Boolean, String) -> Unit
    ) {
        // Validation
        if (fullName.isBlank()) {
            callback(false, "Full name is required")
            return
        }

        if (email.isBlank()) {
            callback(false, "Email is required")
            return
        }

        if (password.isBlank()) {
            callback(false, "Password is required")
            return
        }

        if (confirmPassword.isBlank()) {
            callback(false, "Confirm Password is required")
            return
        }

        if (password != confirmPassword) {
            callback(false, "Passwords do not match")
            return
        }

        if (role.isBlank()) {
            callback(false, "Please select a role")
            return
        }

        _isLoading.value = true

        repo.register(email, password) { success, message, uid ->

            if (!success) {
                callback(false, message)
                return@register
            }

            val user = UserModel(
                uid = uid,
                fullName = fullName,
                email = email,
                role = role
            )

            repo.addUser(uid, user) { addSuccess, addMessage ->

                _isLoading.value = false

                if (addSuccess) {
                    callback(true, addMessage)
                } else {
                    callback(false, addMessage)
                }
            }
        }
    }
    fun login(
        email: String,
        password: String,
        callback: (Boolean, String, String) -> Unit
    ){
        if (email.isBlank()) {
            callback(false, "Email is required", "")
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            callback(false, "Enter a valid email", "")
            return
        }

        if (password.isBlank()) {
            callback(false, "Password is required", "")
            return
        }

        _isLoading.value = true

        repo.login(email.trim(), password.trim()) { success, message ->
            if (success) {
                repo.getCurrentUser { _, user ->
                    _isLoading.value = false
                    callback(true, message, user?.role ?: "")
                }
            } else {
                _isLoading.value = false
                callback(false, message, "")
            }
        }
    }

    fun forgotPassword(
        email: String,
        callback: (Boolean, String) -> Unit
    ){
        repo.forgotPassword(email, callback)
    }

    fun changePassword(
        oldPassword: String,
        newPassword: String,
        confirmPassword: String,
        callback: (Boolean, String) -> Unit
    ){
        // validation check
        if (newPassword.length < 6) {
            message.value = "Password must be at least 6 characters"
            return
        }

        if (newPassword != confirmPassword) {
            message.value = "New Password and Confirm Password do not match"
            return
        }

        // call repo
        repo.changePassword(oldPassword, newPassword) { success, msg ->
            message.value = msg
            callback(success, msg)
        }
    }
}