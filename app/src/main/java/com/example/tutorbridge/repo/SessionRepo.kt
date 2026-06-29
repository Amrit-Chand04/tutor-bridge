package com.example.tutorbridge.repo

interface SessionRepo {
    fun saveSession(role: String)
    fun isLoggedIn(): Boolean
    fun getRole(): String
    fun clearSession()
}
