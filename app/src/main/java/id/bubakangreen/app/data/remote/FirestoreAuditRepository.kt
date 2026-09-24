package id.bubakangreen.app.data.remote

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import id.bubakangreen.app.core.result.Result
import id.bubakangreen.app.domain.model.AuditLog
import id.bubakangreen.app.domain.repository.AuditRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FirestoreAuditRepository(
    private val firestore: FirebaseFirestore
) : AuditRepository {

    private val collection by lazy {
        firestore.collection("audit_logs")
    }

    override suspend fun recordAction(auditLog: AuditLog): Result<Unit> {
        return try {
            val docRef = if (auditLog.id.isNotBlank()) {
                collection.document(auditLog.id)
            } else {
                collection.document()
            }
            val finalLog = auditLog.copy(id = docRef.id)
            docRef.set(finalLog.toMap()).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e, e.localizedMessage)
        }
    }

    override fun getAuditLogs(): Flow<Result<List<AuditLog>>> {
        return collection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .snapshots()
            .map { snapshot ->
                Result.Success(snapshot.documents.mapNotNull { it.toAuditLog() })
            }
            .catch { emit(Result.Error(it, it.localizedMessage)) }
    }

    companion object {
        fun AuditLog.toMap(): Map<String, Any?> = mapOf(
            "id" to id,
            "action" to action,
            "targetEntityId" to targetEntityId,
            "targetEntityType" to targetEntityType,
            "actorUid" to actorUid,
            "actorRole" to actorRole,
            "details" to details,
            "timestamp" to timestamp
        )

        fun DocumentSnapshot.toAuditLog(): AuditLog? {
            if (!exists()) return null
            val id = getString("id") ?: id
            val action = getString("action") ?: return null
            val targetEntityId = getString("targetEntityId") ?: ""
            val targetEntityType = getString("targetEntityType") ?: ""
            val actorUid = getString("actorUid") ?: ""
            val actorRole = getString("actorRole") ?: ""
            val details = getString("details")
            val timestamp = getLong("timestamp") ?: System.currentTimeMillis()

            return AuditLog(
                id = id,
                action = action,
                targetEntityId = targetEntityId,
                targetEntityType = targetEntityType,
                actorUid = actorUid,
                actorRole = actorRole,
                details = details,
                timestamp = timestamp
            )
        }
    }
}
