# BUBAKAN GREEN — PHASE 5 BUILD FAILURE AUDIT
**Date:** 2026-09-27  
**Scope:** Root cause classification of `:app:compileDebugKotlin` failures  
**Policy:** Build repair only — no architectural drift, no new features, no fake data, no git push.

---

## 1. Executive Summary

A clean compilation run of `:app:compileDebugKotlin` revealed 28 compiler error diagnostics across 10 Kotlin files. 
Crucially, these fall into 5 distinct categories:
1. Syntax & scope corruption due to a missing closing brace in a repository (1 root cause -> 5 cascade errors).
2. Interface compliance & Flow return type inference in Firestore plant repository (2 root causes).
3. Non-exhaustive `when` branches on `id.bubakangreen.app.core.result.Result` which defines `Loading` as a 3rd sealed variant (7 files, 11 instances).
4. Nullable GPS accuracy receiver call without safe check (1 instance).
5. Unresolved Material Icons and missing imports (7 files, 9 instances + 1 syntax typo).

---

## 2. Complete Error Classification Matrix

| File & Line | Diagnostic | Classification | Root Cause / Rationale |
|---|---|---|---|
| `FirestoreLocationRepository.kt:93` | (Missing `}`) | **ROOT CAUSE** | `getAssignedLocations` is missing a closing `}`. |
| `FirestoreLocationRepository.kt:97:63` | Unresolved reference 'toLocation' | **CASCADE ERROR** | Result of scope corruption; extension method inaccessible due to malformed class boundary. |
| `FirestoreLocationRepository.kt:103:5` | Modifier 'override' not applicable to 'local function' | **CASCADE ERROR** | Parsed as local function inside unclosed `getAssignedLocations`. |
| `FirestoreLocationRepository.kt:112:5` | Unresolved reference 'companion' | **CASCADE ERROR** | Parsed inside unclosed method scope. |
| `FirestoreLocationRepository.kt:112:14` | Syntax error: Unexpected tokens | **CASCADE ERROR** | Parsed inside unclosed method scope. |
| `FirestoreLocationRepository.kt:180:2` | Syntax error: Missing '}' | **CASCADE ERROR** | File ends while class scope is still open. |
| `FirestorePlantRepository.kt:16:1` | Does not implement abstract member 'updateLocationPlant' | **ROOT CAUSE** | `PlantRepository` defines `suspend fun updateLocationPlant(locationPlant: LocationPlant): Result<Unit>`, which was omitted in implementation. |
| `FirestorePlantRepository.kt:34:27` | Argument type mismatch: actual `Result.Error`, expected `Result.Success<List<MasterPlant>>` | **ROOT CAUSE** | Kotlin Flow `map` inferred return type `Result.Success<...>` instead of interface contract `Result<List<MasterPlant>>`. `.catch` cannot emit `Result.Error` into `Flow<Result.Success>`. |
| `FirestorePlantRepository.kt:40:24` | Argument type mismatch: actual `Result.Error`, expected `Result.Success<MasterPlant?>` | **ROOT CAUSE** | Same Flow `map` inference issue in `getMasterPlantById`. |
| `FirestorePlantRepository.kt:51:27` | Argument type mismatch: actual `Result.Error`, expected `Result.Success<List<LocationPlant>>` | **ROOT CAUSE** | Same Flow `map` inference issue in `getPlantsAtLocation`. |
| `LocationApprovalViewModel.kt:33:13` | 'when' expression must be exhaustive (Loading) | **INDEPENDENT ERROR** | `Result<T>` is sealed: `Success<T>`, `Error`, `Loading`. Missing explicit `is Result.Loading`. |
| `LocationApprovalViewModel.kt:57:13` | 'when' expression must be exhaustive (Loading) | **INDEPENDENT ERROR** | Missing explicit `is Result.Loading` branch. |
| `LocationApprovalViewModel.kt:84:13` | 'when' expression must be exhaustive (Loading) | **INDEPENDENT ERROR** | Missing explicit `is Result.Loading` branch. |
| `MasterPlantViewModel.kt:110:13` | 'when' expression must be exhaustive (Loading) | **INDEPENDENT ERROR** | Missing explicit `is Result.Loading` branch. |
| `LoginViewModel.kt:65:13` | 'when' expression must be exhaustive (Loading) | **INDEPENDENT ERROR** | Missing explicit `is Result.Loading` branch. |
| `LocationFormViewModel.kt:88:13` | 'when' expression must be exhaustive (Loading) | **INDEPENDENT ERROR** | Missing explicit `is Result.Loading` branch. |
| `LocationFormViewModel.kt:165:13` | 'when' expression must be exhaustive (Loading) | **INDEPENDENT ERROR** | Missing explicit `is Result.Loading` branch. |
| `PicDashboardViewModel.kt:37:17` | 'when' expression must be exhaustive (Loading) | **INDEPENDENT ERROR** | Missing explicit `is Result.Loading` branch. |
| `PlantFormViewModel.kt:103:13` | 'when' expression must be exhaustive (Loading) | **INDEPENDENT ERROR** | Missing explicit `is Result.Loading` branch. |
| `PlantFormViewModel.kt:133:13` | 'when' expression must be exhaustive (Loading) | **INDEPENDENT ERROR** | Missing explicit `is Result.Loading` branch. |
| `LocationFormViewModel.kt:92:48` | Operator call prohibited on nullable receiver `Float?` | **INDEPENDENT ERROR** | `accuracyMeters` is `Float?`. Direct `accuracy > 25f` comparison fails. Must handle null safely without `!!` or fake 0f values. |
| `LocationFormViewModel.kt:93:57` | Only safe or non-null asserted calls allowed on `Float?` | **CASCADE ERROR** | `accuracy.toInt()` without safe check on nullable receiver. |
| `LoginScreen.kt:95:47` | Unresolved reference 'CenterVertifocal' | **INDEPENDENT ERROR** | Accidental typo `Alignment.CenterVertifocal(Alignment.CenterVertically)`. |
| `BubakanTopBar.kt:70:84` | Unresolved reference 'Lock' | **INDEPENDENT ERROR** | Missing import `import androidx.compose.material.icons.filled.Lock`. |
| `PlantFormScreen.kt:27:47` | Unresolved reference 'QrCode' | **INDEPENDENT ERROR** | Unused import in `PlantFormScreen.kt`. |
| `AdminDashboardScreen.kt:22, 199` | Unresolved reference 'Eco' | **INDEPENDENT ERROR** | `Eco` not in approved `material-icons-core`. Substitute with available `Icons.Default.Star`. |
| `PlantFormScreen.kt:26, 371` | Unresolved reference 'Eco' | **INDEPENDENT ERROR** | `Eco` not in approved `material-icons-core`. Substitute with available `Icons.Default.Star`. |
| `PicDashboardScreen.kt:25, 298` | Unresolved reference 'Schedule' | **INDEPENDENT ERROR** | `Schedule` not in approved `material-icons-core`. Substitute with available `Icons.Default.DateRange`. |
| `SpeakerButton.kt:14, 87` | Unresolved reference 'VolumeUp' | **INDEPENDENT ERROR** | `VolumeUp` not in approved `material-icons-core`. Substitute with available `Icons.Default.PlayArrow`. |
| `LoginScreen.kt:23, 24, 244` | Unresolved reference 'Visibility', 'VisibilityOff' | **INDEPENDENT ERROR** | Icons not in `material-icons-core`. Substitute with available `Icons.Default.Lock` / `Icons.Default.Check` (or custom minimal vector path). |

---

## 3. Repair Plan & Step-by-Step Order

1. **Step 1: Fix `FirestoreLocationRepository.kt` syntax & structure**
   - Add missing closing brace `}` on line 93.
   - Recompile to verify cascade errors vanish.

2. **Step 2: Fix `FirestorePlantRepository.kt` contract & type mismatch**
   - Implement `updateLocationPlant(locationPlant: LocationPlant): Result<Unit>`.
   - Explicitly cast/type Flow map return as `Result<T>`.

3. **Step 3: Fix Icon imports, typos & substitutions**
   - Correct typo `CenterVertifocal` in `LoginScreen.kt`.
   - Add import `androidx.compose.material.icons.filled.Lock` in `BubakanTopBar.kt`.
   - Remove unused import `QrCode` in `PlantFormScreen.kt`.
   - Replace `Eco` with `Icons.Default.Star`.
   - Replace `Schedule` with `Icons.Default.DateRange`.
   - Replace `VolumeUp` with `Icons.Default.PlayArrow`.
   - Replace `Visibility`/`VisibilityOff` with `Icons.Default.Check`/`Icons.Default.Lock`.

4. **Step 4: Fix Nullable GPS in `LocationFormViewModel.kt`**
   - Provide clean nullable branch: if null -> warning that accuracy info unavailable; if >25f -> warning; else -> null.

5. **Step 5: Fix Exhaustive `when` on `Result` in all ViewModels**
   - Add explicit `is Result.Loading` handling across all 6 ViewModels.

6. **Step 6: Build Verification**
   - Run `.\gradlew.bat compileDebugKotlin`.
   - Run `.\gradlew.bat test`.
   - Document final state in `docs/phase-5/BUILD_REPAIR_REPORT.md`.
