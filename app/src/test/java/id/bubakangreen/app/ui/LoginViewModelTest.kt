package id.bubakangreen.app.ui

import com.google.common.truth.Truth.assertThat
import id.bubakangreen.app.core.result.Result
import id.bubakangreen.app.domain.model.UserRole
import id.bubakangreen.app.domain.model.UserSession
import id.bubakangreen.app.domain.repository.AuthRepository
import id.bubakangreen.app.ui.auth.LoginNavigationEvent
import id.bubakangreen.app.ui.auth.LoginViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private class FakeAuthRepository : AuthRepository {
        val sessionState = MutableStateFlow(
            UserSession(uid = "", email = "", displayName = "", role = UserRole.PUBLIC)
        )
        override val currentUserSession: Flow<UserSession> = sessionState

        var shouldFail: Boolean = false

        override suspend fun signInWithEmail(email: String, password: String): Result<UserSession> {
            if (shouldFail) {
                return Result.Error(Exception("Email atau kata sandi salah."))
            }
            val role = when {
                email.contains("admin", ignoreCase = true) -> UserRole.ADMIN
                email.contains("pic", ignoreCase = true) -> UserRole.PIC
                else -> UserRole.PUBLIC
            }
            val session = UserSession(
                uid = "uid_test",
                email = email,
                displayName = "Test User",
                role = role
            )
            sessionState.value = session
            return Result.Success(session)
        }

        override suspend fun signOut(): Result<Unit> {
            sessionState.value = UserSession(uid = "", email = "", displayName = "", role = UserRole.PUBLIC)
            return Result.Success(Unit)
        }

        override fun isUserSignedIn(): Boolean = sessionState.value.role != UserRole.PUBLIC
    }

    private lateinit var fakeAuthRepo: FakeAuthRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeAuthRepo = FakeAuthRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun signIn_withEmptyEmail_setsErrorMessage() = runTest(testDispatcher) {
        val viewModel = LoginViewModel(fakeAuthRepo)
        viewModel.onPasswordChange("password123")
        viewModel.signIn()
        advanceUntilIdle()

        assertThat(viewModel.uiState.value.errorMessage).isEqualTo("Email tidak boleh kosong.")
    }

    @Test
    fun signIn_withEmptyPassword_setsErrorMessage() = runTest(testDispatcher) {
        val viewModel = LoginViewModel(fakeAuthRepo)
        viewModel.onEmailChange("pic@bubakan.id")
        viewModel.signIn()
        advanceUntilIdle()

        assertThat(viewModel.uiState.value.errorMessage).isEqualTo("Kata sandi tidak boleh kosong.")
    }

    @Test
    fun signIn_withPicCredentials_navigatesToPicDashboard() = runTest(testDispatcher) {
        val viewModel = LoginViewModel(fakeAuthRepo)
        viewModel.onEmailChange("pic.kebun@bubakan.id")
        viewModel.onPasswordChange("password123")

        var navigatedToPic = false
        val job = launch {
            viewModel.navigationEvent.collect { event ->
                if (event is LoginNavigationEvent.NavigateToPicDashboard) {
                    navigatedToPic = true
                }
            }
        }

        viewModel.signIn()
        advanceUntilIdle()

        assertThat(navigatedToPic).isTrue()
        assertThat(viewModel.uiState.value.signedInSession?.role).isEqualTo(UserRole.PIC)
        job.cancel()
    }

    @Test
    fun signIn_withAdminCredentials_navigatesToAdminDashboard() = runTest(testDispatcher) {
        val viewModel = LoginViewModel(fakeAuthRepo)
        viewModel.onEmailChange("admin.kelurahan@bubakan.id")
        viewModel.onPasswordChange("password123")

        var navigatedToAdmin = false
        val job = launch {
            viewModel.navigationEvent.collect { event ->
                if (event is LoginNavigationEvent.NavigateToAdminDashboard) {
                    navigatedToAdmin = true
                }
            }
        }

        viewModel.signIn()
        advanceUntilIdle()

        assertThat(navigatedToAdmin).isTrue()
        assertThat(viewModel.uiState.value.signedInSession?.role).isEqualTo(UserRole.ADMIN)
        job.cancel()
    }

    @Test
    fun signIn_failure_showsErrorMessage() = runTest(testDispatcher) {
        fakeAuthRepo.shouldFail = true
        val viewModel = LoginViewModel(fakeAuthRepo)
        viewModel.onEmailChange("wrong@bubakan.id")
        viewModel.onPasswordChange("wrongpass")
        viewModel.signIn()
        advanceUntilIdle()

        assertThat(viewModel.uiState.value.errorMessage).contains("salah")
    }
}
