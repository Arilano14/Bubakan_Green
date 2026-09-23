package id.bubakangreen.app.domain.repository

import id.bubakangreen.app.core.result.Result
import id.bubakangreen.app.domain.model.UserSession
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUserSession: Flow<UserSession>
    suspend fun signInWithEmail(email: String, password: String): Result<UserSession>
    suspend fun signOut(): Result<Unit>
    fun isUserSignedIn(): Boolean
}
