package com.example.ai_life.domain.usecase

import com.example.ai_life.data.user.UserRepository

class GetUserUseCase(
    private val repo: UserRepository
) {
    suspend operator fun invoke(uid: String) = repo.getUser(uid)
}