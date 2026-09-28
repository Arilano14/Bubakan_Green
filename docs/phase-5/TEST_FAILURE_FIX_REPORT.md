# BUBAKAN GREEN — UNIT TEST FAILURE FIX REPORT
**Date:** 2026-09-28  
**Scope:** Investigation and resolution of single unit test failure in `LoginViewModelTest`  
**Test Case:** `signIn_failure_showsErrorMessage`  
**Status:** ✅ ALL TESTS PASSING (36/36)

---

## 1. FAILED TEST

- **Test Suite:** `id.bubakangreen.app.ui.LoginViewModelTest`
- **Method:** `signIn_failure_showsErrorMessage()` (Line 145)
- **Assertion:**
  ```kotlin
  assertThat(viewModel.uiState.value.errorMessage).contains("salah")
  ```
- **Observed Failure Output:**
  ```text
  expected to contain: salah
  but was            : Gagal masuk. Periksa kembali email dan kata sandi Anda.
  ```

---

## 2. TRACE & INVESTIGATION

### Execution Trace:
1. `LoginViewModelTest` configured `fakeAuthRepo.shouldFail = true`.
2. `fakeAuthRepo.signInWithEmail()` returned:
   ```kotlin
   Result.Error(Exception("Email atau kata sandi salah."))
   ```
3. `Result.Error` is declared in `id.bubakangreen.app.core.result.Result.kt` as:
   ```kotlin
   data class Error(val exception: Throwable, val message: String? = null) : Result<Nothing>
   ```
   Calling `Result.Error(Exception(...))` supplied only the 1st parameter (`exception`). The 2nd parameter (`message`) was not provided and defaulted to `null`.
4. In `LoginViewModel.signIn()`:
   ```kotlin
   is Result.Error -> {
       _uiState.update {
           it.copy(
               isLoading = false,
               errorMessage = result.message ?: "Gagal masuk. Periksa kembali email dan kata sandi Anda."
           )
       }
   }
   ```
5. Because `result.message` was `null`, `LoginViewModel` selected the fallback text:
   `"Gagal masuk. Periksa kembali email dan kata sandi Anda."`
6. This fallback message does not contain the substring `"salah"`, causing Truth assertion to fail.

---

## 3. ROOT CAUSE CLASSIFICATION

**Classification: TEST FIXTURE ISSUE** (with defensive implementation hardening)

### Why TEST FIXTURE ISSUE:
- In production repositories (`FirebaseAuthRepository.kt:71`, `FirestoreLocationRepository.kt:74`, etc.), repositories instantiate `Result.Error` with **both** arguments:
  ```kotlin
  Result.Error(e, e.localizedMessage)
  ```
  This ensures `result.message` carries the localized human-readable error message to the UI.
- The test fixture `FakeAuthRepository` in `LoginViewModelTest.kt` omitted the second argument `message`, passing only the `exception`. Because the default parameter value is `null`, `result.message` was `null`, causing the view model to fall back to the generic error string.

### Implementation Hardening:
- In `LoginViewModel.kt`, error handling was made defensive by checking `result.exception.message` if `result.message` is null before resorting to the generic fallback string:
  ```kotlin
  errorMessage = result.message ?: result.exception.message ?: "Gagal masuk. Periksa kembali email dan kata sandi Anda."
  ```

---

## 4. FILES MODIFIED

1. **`app/src/main/java/id/bubakangreen/app/ui/auth/LoginViewModel.kt`**
   - **Line 91:** Fall back to `result.exception.message` when `result.message` is null.
2. **`app/src/test/java/id/bubakangreen/app/ui/LoginViewModelTest.kt`**
   - **Line 40 (`FakeAuthRepository`):** Pass `message` argument to `Result.Error(Exception(errorMsg), errorMsg)`, conforming to the standard repository error contract.
   - **The test assertion `signIn_failure_showsErrorMessage` was NOT altered.**

---

## 5. WHY FIX IS CORRECT

1. **Preserves Test Invariants:** The test assertion `contains("salah")` was not modified or relaxed.
2. **Conforms to Repository Contract:** The test fixture `FakeAuthRepository` now matches production repositories by supplying the user-facing message to `Result.Error`.
3. **Defensive ViewModel:** `LoginViewModel` gracefully handles cases where an exception message exists even if `result.message` is null.
4. **Zero Architecture Drift:** No changes to interfaces, sealed classes, or domain models.

---

## 6. VERIFICATION RESULTS

### Single Test Verification:
```text
PS C:\Users\Arilano\Downloads\Project ARICE\Bubakan Green> .\gradlew.bat :app:testDebugUnitTest --tests "*LoginViewModelTest.signIn_failure_showsErrorMessage"
> Task :app:compileDebugKotlin
> Task :app:compileDebugUnitTestKotlin
> Task :app:testDebugUnitTest
BUILD SUCCESSFUL in 55s
22 actionable tasks: 5 executed, 17 up-to-date
```

### Full Unit Test Suite:
```text
PS C:\Users\Arilano\Downloads\Project ARICE\Bubakan Green> .\gradlew.bat test
> Task :app:testDebugUnitTest
> Task :app:testReleaseUnitTest
> Task :app:test
BUILD SUCCESSFUL in 28s
45 actionable tasks: 8 executed, 37 up-to-date
```
- **Total Tests:** 36 executed
- **Failures:** 0
- **Errors:** 0
- **Skipped:** 0

### Full Debug Assembly:
```text
PS C:\Users\Arilano\Downloads\Project ARICE\Bubakan Green> .\gradlew.bat assembleDebug
> Task :app:assembleDebug
BUILD SUCCESSFUL in 10s
35 actionable tasks: 3 executed, 32 up-to-date
```
