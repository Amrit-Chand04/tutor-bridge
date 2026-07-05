package com.example.tutorbridge.repo

import com.example.tutorbridge.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserRepoImpl : UserRepo {

    private val auth = FirebaseAuth.getInstance()

    private val database = FirebaseDatabase.getInstance()
    private val ref = database.getReference("users")

    override fun register(
        email: String,
        password: String,
        callback: (Boolean, String, String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: ""

                    callback(
                        true,
                        "Account created successfully",
                        uid
                    )
                } else {
                    callback(
                        false,
                        task.exception?.message ?: "Signup failed",
                        ""
                    )
                }
            }
    }

    override fun addUser(
        uid: String,
        model: UserModel,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(uid)
            .setValue(model)
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    callback(true, "Signup Successful")
                } else {
                    callback(false, task.exception?.message ?: "Failed to save user")
                }
            }
    }

    override fun login(
        email: String,
        password: String,
        callback: (Boolean, String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                callback(true, "Login successful")
            }

            .addOnFailureListener { exception ->

                val message = when (exception) {

                    is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException ->
                        "Incorrect email or password"

                    else ->
                        "Login failed. Please try again"
                }

                callback(false, message)
            }
    }

    override fun forgotPassword(
        email: String,
        callback: (Boolean, String) -> Unit
    ) {
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val isRegistered = snapshot.children.any {
                    it.getValue(UserModel::class.java)?.email.equals(email, ignoreCase = true)
                }

                if (!isRegistered) {
                    callback(false, "No account found with this email")
                    return
                }

                auth.sendPasswordResetEmail(email)
                    .addOnSuccessListener {
                        callback(true, "Reset email sent")
                    }
                    .addOnFailureListener {
                        callback(false, it.message ?: "Failed to send reset email")
                    }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, "Failed to verify email")
            }
        })
    }

    override fun changePassword(
        oldPassword: String,
        newPassword: String,
        callback: (Boolean, String) -> Unit
    ) {
        val user = auth.currentUser

        if (user == null) {
            callback(false, "User not logged in")
            return
        }

        val email = user.email

        // Email not available
        if (email.isNullOrEmpty()) {
            callback(false, "Email not found")
            return
        }

        val credential = com.google.firebase.auth.EmailAuthProvider.getCredential(
            email,
            oldPassword
        )

        user.reauthenticate(credential)
            .addOnSuccessListener {

                user.updatePassword(newPassword)
                    .addOnSuccessListener {
                        callback(true, "Password changed successfully")
                    }
                    .addOnFailureListener {
                        callback(false, it.message ?: "Failed to update password")
                    }
            }
            .addOnFailureListener {
                callback(false, "Old password is incorrect")
            }
    }

    override fun logOut() {
        auth.signOut()
    }

    override fun updateUser(uid: String, fullName: String, callback: (Boolean, String) -> Unit) {
        ref.child(uid).child("fullName").setValue(fullName)
            .addOnSuccessListener {
                callback(true, "Profile updated successfully")
            }
            .addOnFailureListener {
                callback(false, it.message ?: "Update failed")
            }
    }

    override fun getCurrentUser(callback: (Boolean, UserModel?) -> Unit) {
        val uid = auth.currentUser?.uid ?: run { callback(false, null); return }
        ref.child(uid).get()
            .addOnSuccessListener { snapshot ->
                val user = snapshot.getValue(UserModel::class.java)
                callback(true, user)
            }
            .addOnFailureListener {
                callback(false, null)
            }
    }

}