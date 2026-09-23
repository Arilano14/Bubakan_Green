package id.bubakangreen.app.domain.model

enum class UserRole {
    PUBLIC,
    PIC,
    ADMIN
}

data class UserSession(
    val uid: String,
    val email: String,
    val displayName: String,
    val role: UserRole = UserRole.PUBLIC,
    val assignedLocations: List<String> = emptyList()
)
