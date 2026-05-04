package com.joist.assestment.data

import kotlinx.coroutines.delay

interface ValidationRepository {
    suspend fun validate(text: String): Result<String>
}

class ValidationRepositoryImpl : ValidationRepository {

    override suspend fun validate(text: String): Result<String> {
        delay(SIMULATED_DELAY_MS)
        return if (text.startsWith("error", ignoreCase = true)) {
            Result.failure(Exception("Server rejected the input. Try a different text."))
        } else {
            Result.success(text)
        }
    }

    companion object {
        private const val SIMULATED_DELAY_MS = 1500L
    }
}
