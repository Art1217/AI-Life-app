package com.example.ai_life.data.user

import com.example.ai_life.domain.model.User
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class FirebaseUserRepository : UserRepository {
    private val usersRef = Firebase.database.reference.child("users")

    override suspend fun saveUser(user: User): Result<Unit> = try {
        val uid = user.uid ?: throw IllegalArgumentException("User UID required")
        usersRef.child(uid).setValue(user).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getUser(uid: String): Result<User?> = try {
        val snapshot = usersRef.child(uid).get().await()
        val user = snapshot.getValue(User::class.java)
        Result.success(user)
    } catch (e: Exception) {
        Result.failure(e)
    }

}