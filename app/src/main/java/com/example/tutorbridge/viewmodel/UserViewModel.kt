package com.example.tutorbridge.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.tutorbridge.model.UserModel
import com.example.tutorbridge.repo.UserRepo
import com.example.tutorbridge.repo.UserRepoImpl

class UserViewModel : ViewModel() {

    private val repo: UserRepo = UserRepoImpl()
    var message = mutableStateOf("")

    fun register(
        email: String,
        password: String,
        callback: (Boolean, String, String) -> Unit
    ) {
        repo.register(email, password, callback)
    }

    fun addUser(
        uid: String,
        model: UserModel,
        callback: (Boolean, String) -> Unit
    ) {
        repo.addUser(uid, model, callback)
    }

    fun login(
        email: String,
        password: String,
        callback: (Boolean, String) -> Unit
    ){
        repo.login(email,password,callback)
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