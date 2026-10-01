package id.bubakangreen.app.core.auth

import android.content.Context
import id.bubakangreen.app.domain.model.UserRole
import id.bubakangreen.app.domain.model.UserSession

/**
 * AuthSessionStorage: Persistent storage for administrator session.
 * Ensures that once an admin logs in for the first time, the application permanently
 * retains the authenticated admin state across app restarts without requiring re-login.
 */
object AuthSessionStorage {
    private const val PREFS_NAME = "bubakan_auth_session"
    private const val KEY_IS_PERMANENT_ADMIN = "is_permanent_admin"
    private const val KEY_UID = "admin_uid"
    private const val KEY_EMAIL = "admin_email"
    private const val KEY_DISPLAY_NAME = "admin_display_name"

    fun isPermanentAdmin(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_IS_PERMANENT_ADMIN, false)
    }

    fun savePermanentAdmin(context: Context, session: UserSession) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_IS_PERMANENT_ADMIN, true)
            .putString(KEY_UID, session.uid)
            .putString(KEY_EMAIL, session.email)
            .putString(KEY_DISPLAY_NAME, session.displayName)
            .apply()
    }

    fun getPermanentAdminSession(context: Context): UserSession? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        if (!prefs.getBoolean(KEY_IS_PERMANENT_ADMIN, false)) return null
        return UserSession(
            uid = prefs.getString(KEY_UID, "admin_permanent_uid") ?: "admin_permanent_uid",
            email = prefs.getString(KEY_EMAIL, "admin@bubakangreen.id") ?: "admin@bubakangreen.id",
            displayName = prefs.getString(KEY_DISPLAY_NAME, "Admin Kelurahan Bubakan") ?: "Admin Kelurahan Bubakan",
            role = UserRole.ADMIN,
            isActive = true
        )
    }

    fun clearSession(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .clear()
            .apply()
    }
}
