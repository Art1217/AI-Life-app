package com.example.ai_life.domain.usecase

import com.example.ai_life.data.auth.AuthRepository

class LoginUserUseCase(
    private val authRepo: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String) =
        authRepo.login(email, password)
}