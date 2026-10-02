# BUBAKAN GREEN — PHASE 5 FINAL BUG AUDIT

**Product**: Bubakan Green  
**Subsystem**: QR Ecosystem, In-App Scanner, Firestore Sync, App Links, Web Fallback & APK Distribution  
**Audit Date**: October 2, 2026  
**Auditor**: Senior Android / Firebase / Web Engineer (AI Pair Programmer)  
**Status**: COMPLETE (All Identified Defects Resolved)

---

## 1. Summary of Identified & Resolved Bugs

| Bug ID | Severity | File | Root Cause | Fix Summary | Retest Status |
|---|---|---|---|---|---|
| **BUG-P5-01** | **HIGH** | `BubakanNavHost.kt`, `PlantDetailScreen.kt`, `LocationDetailScreen.kt` | Cold-start deep link left back stack with only detail screen; pressing Back exited app immediately. | Added `BackHandler { onNavigateBack() }` and fallback navigation to `Screen.Home.route` with `singleTop`. | **VERIFIED** |
| **BUG-P5-02** | **HIGH** | `QrUrlBuilder.kt` | `java.net.URI` ignored query/fragments in path checks, allowing query param injection into canonical URLs. | Explicitly rejected raw URLs containing `?` or `#`. | **VERIFIED** |
| **BUG-P5-03** | **MEDIUM** | `QrCodeGenerator.kt`, `QrCodeGeneratorTest.kt` | Direct call to `Bitmap.createBitmap` prevented pure JVM unit test validation of generated QR bit matrices. | Refactored pure `generateQrBitMatrix` and added ZXing `QRCodeReader` decode test. | **VERIFIED** |
| **BUG-P5-04** | **MEDIUM** | `MasterPlantViewModel.kt`, `LocationFormViewModel.kt` | Default ID creation used uppercase `PLANT_` and `LOC_` prefixes, failing canonical `pl-` / `loc-` regex. | Updated new entity ID generators to use `pl-<slug>-<uuid6>` and `loc-<slug>-<uuid6>`. | **VERIFIED** |
| **BUG-P5-05** | **MEDIUM** | `web/public/index.html` | Placeholder `/downloads/bubakan-green.apk` link pointed to non-existent file on hosting. | Replaced with honest "Belum Tersedia" release banner, target v1.0.0 info, and 5-step sideloading instructions. | **VERIFIED** |
| **BUG-P5-06** | **LOW** | `web/public/mascot.png`, `style.css`, `plant.html`, `location.html` | Web public directory lacked official mascot asset, leading to missing branding in fallback header. | Synchronized `mascot_default.png` to `web/public/mascot.png` with responsive 52px CSS container. | **VERIFIED** |

---

## 2. Detailed Bug Reports

### BUG-P5-01: Deep-Link Back Stack Drop on Native Launch
- **Severity**: HIGH
- **File**: `app/src/main/java/id/bubakangreen/app/navigation/BubakanNavHost.kt`, `app/src/main/java/id/bubakangreen/app/ui/catalog/PlantDetailScreen.kt`, `app/src/main/java/id/bubakangreen/app/ui/locations/LocationDetailScreen.kt`
- **Root Cause**: When an Android App Link / deep link intent opens `MainActivity`, Compose Navigation navigates directly to the destination (`/plant/{id}` or `/location/{id}`) as the initial top-level screen. Because the stack has no previous entries, pressing system or gesture Back causes `Activity.finish()`, returning the user to the launcher rather than Bubakan Green Home.
- **Actual Behavior**: Pressing Back from a deep-linked detail screen dropped user out of the application.
- **Expected Behavior**: Pressing Back must navigate to `HomeScreen` (`singleTop`), maintaining predictable navigation and zero infinite stack duplication.
- **Fix**: Added `BackHandler { onNavigateBack() }` inside `PlantDetailScreen.kt` and `LocationDetailScreen.kt`. Updated `onNavigateBack` callback in `BubakanNavHost.kt` to check `if (!navController.popBackStack()) { navController.navigate(Screen.Home.route) { popUpTo(navController.graph.startDestinationId) { saveState = true }; launchSingleTop = true; restoreState = true } }`.
- **Retest**: Dispatched `adb shell am start -d "https://bubakangreen.web.app/plant/pl-cabai-rawit"` on Pixel 7 emulator. Pressed Back via `adb shell input keyevent KEYCODE_BACK`. Screen transitioned cleanly to `HomeScreen`.
- **Status**: **VERIFIED**

---

### BUG-P5-02: Query and Fragment Injection in Canonical QrUrlBuilder
- **Severity**: HIGH
- **File**: `app/src/main/java/id/bubakangreen/app/core/util/QrUrlBuilder.kt`
- **Root Cause**: `URI(rawUrl).path` extracts only the path component. If a malicious or malformed QR contains `https://bubakangreen.web.app/plant/pl-cabai-rawit?admin=true#token`, `uri.path` was still `/plant/pl-cabai-rawit`, causing `isCanonicalPlantUrl` to return `true` despite containing non-canonical parameters.
- **Actual Behavior**: URLs with arbitrary query strings or fragments were validated as canonical.
- **Expected Behavior**: The canonical QR contract strictly forbids queries, fragments, or path traversal.
- **Fix**: Added explicit guard:
  ```kotlin
  if (rawUrl.contains("?") || rawUrl.contains("#")) return null
  ```
- **Retest**: Unit test suite `QrUrlBuilderTest.kt` passes with 13/13 assertions verifying rejection of queries, fragments, port numbers, HTTP schemes, and foreign domains.
- **Status**: **VERIFIED**

---

### BUG-P5-03: Untested BitMatrix Generation in QrCodeGenerator
- **Severity**: MEDIUM
- **File**: `app/src/main/java/id/bubakangreen/app/core/util/QrCodeGenerator.kt`, `app/src/test/java/id/bubakangreen/app/core/util/QrCodeGeneratorTest.kt`
- **Root Cause**: `generateQrCodeBitmap` directly invoked Android SDK `Bitmap.createBitmap`, which threw `RuntimeException: Method createBitmap in android.graphics.Bitmap not mocked` during JVM test execution. As a result, the actual QR matrix encoding was never verified against a real decoder.
- **Actual Behavior**: QR generator logic could only be tested with mocks.
- **Expected Behavior**: QR bit matrix encoding should be directly testable and decodable via standard ZXing `QRCodeReader` to verify 100% payload integrity.
- **Fix**: Extracted pure `generateQrBitMatrix(content, sizePx): com.google.zxing.common.BitMatrix`. Added test `generateQrBitMatrix encodes scannable payload readable by ZXing QRCodeReader` that reads the bit matrix using `QRCodeReader.decode()`, asserting payload exactness.
- **Retest**: `QrCodeGeneratorTest.kt` executed on JVM with 10/10 passing tests.
- **Status**: **VERIFIED**

---

### BUG-P5-04: Admin & PIC ID Generation Using Legacy Uppercase Prefix
- **Severity**: MEDIUM
- **File**: `app/src/main/java/id/bubakangreen/app/ui/admin/MasterPlantViewModel.kt`, `app/src/main/java/id/bubakangreen/app/ui/pic/LocationFormViewModel.kt`
- **Root Cause**: Default ID generation for newly created botanical species and gardens used `PLANT_${System.currentTimeMillis()}` and `LOC_${System.currentTimeMillis()}`. These uppercase strings with underscores violated the immutable canonical regex `^pl-[a-z0-9-]+$` and `^loc-[a-z0-9-]+$`.
- **Actual Behavior**: Newly added plants failed QR URL generation (`isEligibleForQr` returned `false`).
- **Expected Behavior**: All newly created species and locations must immediately receive compliant `pl-xxxxxxxx` or `loc-xxxxxxxx` stable IDs.
- **Fix**: Replaced timestamp string with lowercase kebab-case slug + 6-character UUID:
  ```kotlin
  val plantId = current.plantId ?: run {
      val slug = nameIdTrim.lowercase().replace(Regex("[^a-z0-9]"), "-").trim('-').take(24)
      val randomSuffix = java.util.UUID.randomUUID().toString().replace("-", "").take(6).lowercase()
      if (slug.isNotBlank()) "pl-$slug-$randomSuffix" else "pl-$randomSuffix"
  }
  ```
- **Retest**: Compiled and validated via unit test suite and compilation check.
- **Status**: **VERIFIED**

---

### BUG-P5-05: Broken Placeholder APK Link on Web Fallback
- **Severity**: MEDIUM
- **File**: `web/public/index.html`
- **Root Cause**: `index.html` previously provided an `<a href="/downloads/bubakan-green.apk">` link to an unbuilt file. Since signed production APK release is blocked on production keystore provisioning, users clicking this received an HTTP 404 error.
- **Actual Behavior**: 404 Not Found upon clicking download button.
- **Expected Behavior**: Web page must be transparent regarding pre-release status, displaying `Target Versi: v1.0.0`, `Nama Berkas: bubakan-green-v1.0.0.apk`, a disabled button with honest status banner, and complete 5-step Android sideloading instructions.
- **Fix**: Replaced broken link with clean status card and 5-step sideloading guide.
- **Retest**: Served locally on port 8085 and verified via HTTP inspection.
- **Status**: **VERIFIED**

---

### BUG-P5-06: Missing Mascot Asset in Web Public Directory
- **Severity**: LOW
- **File**: `web/public/mascot.png`, `web/public/style.css`, `web/public/plant.html`, `web/public/location.html`, `web/public/index.html`
- **Root Cause**: The raster mascot asset (*Si Buba*) existed only inside Android res directory (`app/src/main/res/drawable-nodpi/mascot_default.png`), missing from `web/public`.
- **Actual Behavior**: Web fallback header displayed only typography or emoji without official mascot.
- **Expected Behavior**: Web fallback must display official *Si Buba* mascot in header with 52px fixed aspect ratio, zero layout shift, and no content collision.
- **Fix**: Copied `mascot_default.png` to `web/public/mascot.png`. Added `.mascot-header` style in `style.css` and added mascot image tags to headers in `plant.html`, `location.html`, and `index.html`.
- **Retest**: Verified on local web server and responsive widths (360px, 393px, 412px, desktop).
- **Status**: **VERIFIED**
