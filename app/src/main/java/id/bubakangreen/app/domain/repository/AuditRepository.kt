package id.bubakangreen.app.domain.repository

import id.bubakangreen.app.core.result.Result
import id.bubakangreen.app.domain.model.AuditLog
import kotlinx.coroutines.flow.Flow

interface AuditRepository {
    suspend fun recordAction(auditLog: AuditLog): Result<Unit>
    fun getAuditLogs(): Flow<Result<List<AuditLog>>>
}
