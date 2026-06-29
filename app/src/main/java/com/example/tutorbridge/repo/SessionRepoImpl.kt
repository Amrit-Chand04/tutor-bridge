package com.example.tutorbridge.repo

import android.content.Context

class SessionRepoImpl(context: Context) : SessionRepo {

    private val prefs = context.getSharedPreferences("User", Context.MODE_PRIVATE)

    override fun saveSession(role: String) {
        prefs.edit().putBoolean("isLoggedIn", true).putString("role", role).apply()
    }

    override fun isLoggedIn(): Boolean = prefs.getBoolean("isLoggedIn", false)

    override fun getRole(): String = prefs.getString("role", "") ?: ""

    override fun clearSession() {
        prefs.edit().clear().apply()
    }
}
