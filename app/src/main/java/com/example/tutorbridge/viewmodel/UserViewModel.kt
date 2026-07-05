package com.example.tutorbridge.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.example.tutorbridge.model.UserModel
import com.example.tutorbridge.repo.SessionRepo
import com.example.tutorbridge.repo.SessionRepoImpl
import com.example.tutorbridge.repo.UserRepo
import com.example.tutorbridge.repo.UserRepoImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val repo: UserRepo = UserRepoImpl()
    private val sessionRepo: SessionRepo = SessionRepoImpl(application)
    var message = mutableStateOf("")

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isUserLoading = MutableStateFlow(false)
    val isUserLoading: StateFlow<Boolean> = _isUserLoading

    private val _user = MutableStateFlow<UserModel?>(null)
    val user: StateFlow<UserModel?> = _user

    private val _isLoggedOut = MutableStateFlow(false)
    val isLoggedOut: StateFlow<Boolean> = _isLoggedOut

    private val _profileMessage = MutableStateFlow<String?>(null)
    val profileMessage: StateFlow<String?> = _profileMessage

    fun clearProfileMessage() {
        _profileMessage.value = null
    }

    fun updateUser(uid: String, fullName: String) {
        if (fullName.isBlank()) {
            _profileMessage.value = "Name cannot be empty"
            return
        }
        _isLoading.value = true
        repo.updateUser(uid, fullName) { success, msg ->
            _isLoading.value = false
            _profileMessage.value = msg
            if (success) {
                _user.value = _user.value?.copy(fullName = fullName)
            }
        }
    }

    fun isLoggedIn(): Boolean = sessionRepo.isLoggedIn()
    fun getRole(): String = sessionRepo.getRole()

    fun logOut() {
        repo.logOut()
        sessionRepo.clearSession()
        _isLoggedOut.value = true
    }

    fun loadCurrentUser() {
        _isUserLoading.value = true
        repo.getCurrentUser { success, userData ->
            _isUserLoading.value = false
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
        val trimmedEmail = email.trim()

        // Validation
        if (fullName.isBlank()) {
            callback(false, "Full name is required")
            return
        }

        if (trimmedEmail.isBlank()) {
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

        repo.register(trimmedEmail, password) { success, message, uid ->

            if (!success) {
                callback(false, message)
                return@register
            }

            val user = UserModel(
                uid = uid,
                fullName = fullName,
                email = trimmedEmail,
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
        val trimmedEmail = email.trim()

        if (trimmedEmail.isBlank()) {
            callback(false, "Email is required", "")
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            callback(false, "Enter a valid email", "")
            return
        }

        if (password.isBlank()) {
            callback(false, "Password is required", "")
            return
        }

        _isLoading.value = true

        repo.login(trimmedEmail, password) { success, message ->
            if (success) {
                repo.getCurrentUser { _, user ->
                    _isLoading.value = false
                    val role = user?.role ?: ""
                    sessionRepo.saveSession(role)
                    callback(true, message, role)
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
        _isLoading.value = true
        repo.forgotPassword(email) { success, message ->
            _isLoading.value = false
            callback(success, message)
        }
    }

    fun changePassword(
        oldPassword: String,
        newPassword: String,
        confirmPassword: String,
        callback: (Boolean, String) -> Unit
    ) {
        if (newPassword.length < 6) { callback(false, "Password must be at least 6 characters"); return }
        if (newPassword != confirmPassword) { callback(false, "New and confirm password do not match"); return }

        _isLoading.value = true
        repo.changePassword(oldPassword, newPassword) { success, msg ->
            _isLoading.value = false
            callback(success, msg)
        }
    }
}