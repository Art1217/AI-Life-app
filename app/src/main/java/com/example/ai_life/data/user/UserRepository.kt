package com.example.ai_life.data.user

import com.example.ai_life.domain.model.User

interface UserRepository {
    suspend fun saveUser(user: User): Result<Unit>
    suspend fun getUser(uid: String): Result<User?>
}