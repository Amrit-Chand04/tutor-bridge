package com.example.tutorbridge.repo

import com.example.tutorbridge.model.UserModel

interface UserRepo {
    fun register(
        email: String,
        password: String,
        callback: (Boolean, String, String) -> Unit
    )

    fun addUser(
        uid: String,
        model: UserModel,
        callback: (Boolean, String) -> Unit
    )

    fun login(
        email: String,
        password: String,
        callback: (Boolean, String) -> Unit
    )

    fun forgotPassword(
        email: String,
        callback: (Boolean, String) -> Unit
    )


    fun changePassword(
        oldPassword: String,
        newPassword: String,
        callback: (Boolean, String) -> Unit
    )

    fun getCurrentUser(callback: (Boolean, UserModel?) -> Unit)

    fun logOut()
}