package id.bubakangreen.app.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import id.bubakangreen.app.core.result.Result
import id.bubakangreen.app.domain.model.UserRole
import id.bubakangreen.app.domain.model.UserSession
import id.bubakangreen.app.domain.repository.AuthRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore? = null
) : AuthRepository {

    private fun getFirestore(): FirebaseFirestore = firestore ?: FirebaseFirestore.getInstance()

    override val currentUserSession: Flow<UserSession> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user == null) {
                val permanentAdmin = id.bubakangreen.app.core.di.RepositoryProvider.getAppContext()
                    ?.let { id.bubakangreen.app.core.auth.AuthSessionStorage.getPermanentAdminSession(it) }
                if (permanentAdmin != null) {
                    trySend(permanentAdmin)
                } else {
                    trySend(UserSession(uid = "", email = "", displayName = "", role = UserRole.PUBLIC, isActive = true))
                }
            } else {
                getFirestore().collection("users").document(user.uid).get()
                    .addOnSuccessListener { doc ->
                        val roleStr = doc.getString("role")
                        val isActive = doc.getBoolean("isActive") ?: false
                        val role = if (isActive && roleStr.equals("ADMIN", ignoreCase = true)) {
                            UserRole.ADMIN
                        } else if (isActive && roleStr.equals("PIC", ignoreCase = true)) {
                            UserRole.PIC
                        } else {
                            UserRole.PUBLIC
                        }
                        trySend(
                            UserSession(
                                uid = user.uid,
                                email = user.email ?: "",
                                displayName = doc.getString("name") ?: user.displayName ?: user.email ?: "",
                                role = role,
                                isActive = isActive
                            )
                        )
                    }
                    .addOnFailureListener {
                        trySend(
                            UserSession(
                                uid = user.uid,
                                email = user.email ?: "",
                                displayName = user.email ?: "",
                                role = UserRole.PUBLIC,
                                isActive = false
                            )
                        )
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

            // Section 5: Verify role in users/{uid}
            val userDoc = getFirestore().collection("users").document(user.uid).get().await()
            val roleStr = userDoc.getString("role")
            val isActive = userDoc.getBoolean("isActive") ?: false

            if (!roleStr.equals("ADMIN", ignoreCase = true) || !isActive) {
                auth.signOut()
                return Result.Error(
                    SecurityException("Akun ini tidak memiliki akses admin.")
                )
            }

            val session = UserSession(
                uid = user.uid,
                email = user.email ?: "",
                displayName = userDoc.getString("name") ?: user.displayName ?: user.email ?: "Admin",
                role = UserRole.ADMIN,
                isActive = true
            )
            id.bubakangreen.app.core.di.RepositoryProvider.getAppContext()?.let {
                id.bubakangreen.app.core.auth.AuthSessionStorage.savePermanentAdmin(it, session)
            }
            Result.Success(session)
        } catch (e: Exception) {
            val friendlyMessage = when {
                e is SecurityException -> e.message ?: "Akun ini tidak memiliki akses admin."
                e.message?.contains("password", ignoreCase = true) == true ||
                e.message?.contains("credential", ignoreCase = true) == true ||
                e.message?.contains("user-not-found", ignoreCase = true) == true -> "Username atau password salah."
                e.message?.contains("network", ignoreCase = true) == true -> "Koneksi internet diperlukan untuk masuk ke sistem."
                else -> e.localizedMessage ?: "Gagal masuk. Periksa kembali username dan kata sandi Anda."
            }
            Result.Error(e, friendlyMessage)
        }
    }

    override suspend fun signOut(): Result<Unit> {
        return try {
            id.bubakangreen.app.core.di.RepositoryProvider.getAppContext()?.let {
                id.bubakangreen.app.core.auth.AuthSessionStorage.clearSession(it)
            }
            auth.signOut()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e, e.localizedMessage)
        }
    }

    override fun isUserSignedIn(): Boolean {
        if (auth.currentUser != null) return true
        val context = id.bubakangreen.app.core.di.RepositoryProvider.getAppContext() ?: return false
        return id.bubakangreen.app.core.auth.AuthSessionStorage.isPermanentAdmin(context)
    }
}

