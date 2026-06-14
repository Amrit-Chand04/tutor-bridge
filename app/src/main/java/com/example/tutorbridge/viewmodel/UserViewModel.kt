package com.example.tutorbridge.viewmodel

import androidx.lifecycle.ViewModel
import com.example.tutorbridge.model.UserModel
import com.example.tutorbridge.repo.UserRepo
import com.example.tutorbridge.repo.UserRepoImpl

class UserViewModel : ViewModel() {

    private val repo: UserRepo = UserRepoImpl()

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
}