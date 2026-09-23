package id.bubakangreen.app.data.remote

import com.google.firebase.auth.FirebaseAuth
import id.bubakangreen.app.core.result.Result
import id.bubakangreen.app.domain.model.UserRole
import id.bubakangreen.app.domain.model.UserSession
import id.bubakangreen.app.domain.repository.AuthRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository(
    private val auth: FirebaseAuth
) : AuthRepository {

    override val currentUserSession: Flow<UserSession> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user == null) {
                trySend(UserSession(uid = "", email = "", displayName = "", role = UserRole.PUBLIC))
            } else {
                user.getIdToken(false).addOnSuccessListener { tokenResult ->
                    val roleStr = tokenResult.claims["role"] as? String
                    val role = when (roleStr?.lowercase()) {
                        "admin" -> UserRole.ADMIN
                        "pic" -> UserRole.PIC
                        else -> UserRole.PUBLIC
                    }
                    val assigned = (tokenResult.claims["assignedLocations"] as? List<*>)?.mapNotNull { it?.toString() } ?: emptyList()
                    trySend(
                        UserSession(
                            uid = user.uid,
                            email = user.email ?: "",
                            displayName = user.displayName ?: user.email ?: "",
                            role = role,
                            assignedLocations = assigned
                        )
                    )
                }.addOnFailureListener {
                    trySend(UserSession(uid = user.uid, email = user.email ?: "", displayName = "", role = UserRole.PUBLIC))
                }
            }
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override suspend fun signInWithEmail(email: String, password: String): Result<UserSession> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user ?: return Result.Error(IllegalStateException("User is null"))
            val tokenResult = user.getIdToken(false).await()
            val roleStr = tokenResult.claims["role"] as? String
            val role = when (roleStr?.lowercase()) {
                "admin" -> UserRole.ADMIN
                "pic" -> UserRole.PIC
                else -> UserRole.PUBLIC
            }
            val assigned = (tokenResult.claims["assignedLocations"] as? List<*>)?.mapNotNull { it?.toString() } ?: emptyList()
            Result.Success(
                UserSession(
                    uid = user.uid,
                    email = user.email ?: "",
                    displayName = user.displayName ?: user.email ?: "",
                    role = role,
                    assignedLocations = assigned
                )
            )
        } catch (e: Exception) {
            Result.Error(e, e.localizedMessage)
        }
    }

    override suspend fun signOut(): Result<Unit> {
        return try {
            auth.signOut()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e, e.localizedMessage)
        }
    }

    override fun isUserSignedIn(): Boolean = auth.currentUser != null
}
