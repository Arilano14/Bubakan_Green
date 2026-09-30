package id.bubakangreen.app.ui.auth

import com.google.common.truth.Truth.assertThat
import id.bubakangreen.app.core.result.Result
import id.bubakangreen.app.domain.model.Location
import id.bubakangreen.app.domain.model.LocationStatus
import id.bubakangreen.app.domain.model.LocationType
import id.bubakangreen.app.domain.model.UserRole
import id.bubakangreen.app.domain.model.UserSession
import id.bubakangreen.app.domain.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AdminAuthVerificationTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var mockAuthRepository: MockAuthRepository
    private lateinit var loginViewModel: LoginViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockAuthRepository = MockAuthRepository()
        loginViewModel = LoginViewModel(mockAuthRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun publicAccess_defaultSessionIsPublicWithoutLogin() = runTest(testDispatcher) {
        val session = mockAuthRepository.currentUserSession.first()
        assertThat(session.role).isEqualTo(UserRole.PUBLIC)
        assertThat(mockAuthRepository.isUserSignedIn()).isFalse()
    }

    @Test
    fun login_usernameAdminMapsInternallyToConfiguredEmail() = runTest(testDispatcher) {
        loginViewModel.onUsernameChange("admin")
        loginViewModel.onPasswordChange("admin_bubakan")
        loginViewModel.signIn()
        advanceUntilIdle()

        assertThat(mockAuthRepository.lastAttemptedEmail).isEqualTo("admin@bubakangreen.id")
    }

    @Test
    fun login_adminRoleVerified_emitsAdminDashboard() = runTest(testDispatcher) {
        mockAuthRepository.configuredRole = UserRole.ADMIN
        mockAuthRepository.configuredIsActive = true

        loginViewModel.onUsernameChange("admin")
        loginViewModel.onPasswordChange("admin_bubakan")
        loginViewModel.signIn()
        advanceUntilIdle()

        val state = loginViewModel.uiState.value
        assertThat(state.errorMessage).isNull()
        assertThat(state.signedInSession?.role).isEqualTo(UserRole.ADMIN)
        assertThat(state.signedInSession?.isActive).isTrue()
    }

    @Test
    fun login_unauthorizedRoleDenied_displaysErrorMessage() = runTest(testDispatcher) {
        mockAuthRepository.configuredRole = UserRole.PUBLIC
        mockAuthRepository.configuredIsActive = false

        loginViewModel.onUsernameChange("unauthorized_user")
        loginViewModel.onPasswordChange("secret")
        loginViewModel.signIn()
        advanceUntilIdle()

        val state = loginViewModel.uiState.value
        assertThat(state.errorMessage).isEqualTo("Akun ini tidak memiliki akses admin.")
    }

    @Test
    fun login_blankUsernameOrPassword_returnsValidationError() = runTest(testDispatcher) {
        loginViewModel.onUsernameChange("")
        loginViewModel.onPasswordChange("some_pass")
        loginViewModel.signIn()
        advanceUntilIdle()
        assertThat(loginViewModel.uiState.value.errorMessage).isEqualTo("Email tidak boleh kosong.")

        loginViewModel.onUsernameChange("admin")
        loginViewModel.onPasswordChange("")
        loginViewModel.signIn()
        advanceUntilIdle()
        assertThat(loginViewModel.uiState.value.errorMessage).isEqualTo("Kata sandi tidak boleh kosong.")
    }

    @Test
    fun location_softDeactivation_updatesStatusToInactive() {
        val location = Location(
            id = "LOC_TEST_01",
            name = "Kebun Percobaan",
            type = LocationType.URBAN_FARMING,
            rw = "02",
            description = "Kebun uji",
            latitude = -7.068,
            longitude = 110.328,
            status = LocationStatus.ACTIVE,
            isPublished = true
        )

        val deactivated = location.copy(
            isPublished = false,
            status = LocationStatus.INACTIVE
        )

        assertThat(deactivated.isPublished).isFalse()
        assertThat(deactivated.status).isEqualTo(LocationStatus.INACTIVE)
    }

    private class MockAuthRepository : AuthRepository {
        var lastAttemptedEmail: String? = null
        var configuredRole: UserRole = UserRole.ADMIN
        var configuredIsActive: Boolean = true

        private val sessionFlow = MutableStateFlow(
            UserSession(uid = "", email = "", displayName = "", role = UserRole.PUBLIC, isActive = true)
        )

        override val currentUserSession: Flow<UserSession> = sessionFlow.asStateFlow()

        override suspend fun signInWithEmail(email: String, password: String): Result<UserSession> {
            lastAttemptedEmail = email
            if (!configuredIsActive || configuredRole != UserRole.ADMIN) {
                return Result.Error(SecurityException("Akun ini tidak memiliki akses admin."))
            }
            val session = UserSession(
                uid = "test_admin_uid",
                email = email,
                displayName = "Admin Kelurahan",
                role = configuredRole,
                isActive = configuredIsActive
            )
            sessionFlow.value = session
            return Result.Success(session)
        }

        override suspend fun signOut(): Result<Unit> {
            sessionFlow.value = UserSession(uid = "", email = "", displayName = "", role = UserRole.PUBLIC, isActive = true)
            return Result.Success(Unit)
        }

        override fun isUserSignedIn(): Boolean = sessionFlow.value.role != UserRole.PUBLIC
    }
}
