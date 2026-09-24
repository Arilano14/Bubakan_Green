package id.bubakangreen.app.domain.model

/**
 * Domain entity for civic administrative audit logging.
 * Enforces immutable audit trails for community garden submissions,
 * approvals, rejections, and botanical assignments.
 */
data class AuditLog(
    val id: String,
    val action: String,
    val targetEntityId: String,
    val targetEntityType: String,
    val actorUid: String,
    val actorRole: String,
    val details: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
