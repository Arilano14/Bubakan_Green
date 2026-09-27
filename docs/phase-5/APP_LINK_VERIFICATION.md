# BUBAKAN GREEN — ANDROID APP LINKS VERIFICATION AUDIT
**Product:** BUBAKAN GREEN  
**Sub-title:** Sistem Informasi Urban Farming & Taman Toga Kelurahan Bubakan  
**Phase:** Phase 5 — QR, App Links, Web Fallback & Field Integration  
**Governance:** `android:autoVerify="true"` in AndroidManifest is NOT proof of verification. Verification requires reachable HTTPS domain, valid `assetlinks.json`, matching keystore SHA-256 fingerprint, and active OS domain verification handoff.  
**Date:** 2026-09-27  

---

## 1. Domain Specification

- **Primary Canonical Domain:** `bubakangreen.web.app` (Firebase Hosting)
- **Secondary / Future Domain:** `bubakangreen.app` (Custom domain, if provisioned by Kelurahan)
- **Protocol:** Strictly `https://` (Unencrypted `http://` is forbidden by Android App Links specification)

---

## 2. Application Identity

- **Application ID / Package Name:** `id.bubakangreen.app`
- **Minimum SDK:** 26 (Android 8.0 Oreo)
- **Target SDK:** 35 (Android 15)

---

## 3. Manifest Intent Filter Declaration

Declared in [`app/src/main/AndroidManifest.xml`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/app/src/main/AndroidManifest.xml):
```xml
<intent-filter android:autoVerify="true">
    <action android:name="android.intent.action.VIEW" />

    <category android:name="android.intent.category.DEFAULT" />
    <category android:name="android.intent.category.BROWSABLE" />

    <data
        android:scheme="https"
        android:host="bubakangreen.web.app"
        android:pathPrefix="/plant/" />
    <data
        android:scheme="https"
        android:host="bubakangreen.web.app"
        android:pathPrefix="/location/" />
</intent-filter>
```

---

## 4. Expected URL Path Mappings

| Inbound HTTPS URL Pattern | NavHost Deep Link Pattern | Target Composable Screen | Resource Parameter |
|---|---|---|---|
| `https://bubakangreen.web.app/plant/{plantId}` | `https://bubakangreen.web.app/plant/{plantId}` | `PlantDetailScreen.kt` | `plantId` (e.g. `pl-jahe-merah`) |
| `https://bubakangreen.web.app/location/{locationId}` | `https://bubakangreen.web.app/location/{locationId}` | `LocationDetailScreen.kt` | `locationId` (e.g. `loc-toga-rw03-bersemi`) |

---

## 5. Digital Asset Links (`assetlinks.json`) Hosting Location

- **Canonical URL:** `https://bubakangreen.web.app/.well-known/assetlinks.json`
- **File System Path:** [`web/public/.well-known/assetlinks.json`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/web/public/.well-known/assetlinks.json)
- **Hosting Requirements:**
  - Content-Type: `application/json`
  - HTTP Status: `200 OK`
  - Zero HTTP redirects (`301` or `302` redirects cause Android domain verification failure)
  - Must NOT be intercepted by SPA catch-all rewrites (`"source": "**"` in `firebase.json`)

---

## 6. Certificate Fingerprint Source & Distinctions

Android App Links verification requires an exact match between the SHA-256 fingerprint declared in `assetlinks.json` and the cryptographic certificate used to sign the APK installed on the user's device.

### A. Debug Keystore (Local Development / Testing)
- **Source:** Local development keystore (`~/.android/debug.keystore`).
- **Scope:** Used for local emulator tests and development sideloading.
- **Fingerprint Status:** Locally discoverable via `keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey`.
- **Limitation:** Only valid for APKs signed with this specific machine's debug key; invalid for public end-users.

### B. Release Keystore (Production Field Deployment)
- **Source:** Production release keystore generated for Kelurahan Bubakan.
- **Scope:** Used for production APK distribution.
- **Fingerprint Status:** **NOT YET AVAILABLE / PENDING CREATION**.
- **Governance:** The production release key must be generated securely by project administrators and never committed to source control.

### C. Google Play App Signing (If Published to Play Store)
- **Source:** Google Play Console App Signing certificate.
- **Fingerprint Status:** **NOT YET APPLICABLE** (App is currently in direct distribution / sideloading phase).

---

## 7. Verification Test Method

When verifying App Links on hardware:
1. **Tool 1: Google Digital Asset Links API Tester**
   ```bash
   curl "https://digitalassetlinks.googleapis.com/v1/statements:check?source.web.site=https://bubakangreen.web.app&relation=delegate_permission/common.handle_all_urls&target.android_app.package_name=id.bubakangreen.app&target.android_app.certificate.sha256_fingerprint=<SHA256>"
   ```
2. **Tool 2: Android ADB Domain Verification Diagnostic**
   ```bash
   adb shell pm get-app-links id.bubakangreen.app
   ```
   *Expected Verified State:* `bubakangreen.web.app: verified`
3. **Tool 3: Physical External Scan Simulation**
   ```bash
   adb shell am start -a android.intent.action.VIEW -d "https://bubakangreen.web.app/plant/pl-jahe-merah" id.bubakangreen.app
   ```

---

## 8. Actual Verification Result

| Verification Dimension | Required Precondition | Current Actual State | Evaluation Status |
|---|---|---|---|
| **Manifest Intent Filter** | `<data>` with `https`, `host`, `pathPrefix` | Configured in `AndroidManifest.xml` | **PASS (READY)** |
| **Compose NavHost Routes** | Deep link uriPattern declared | Configured in `BubakanNavHost.kt` | **PASS (READY)** |
| **assetlinks.json Format** | Valid JSON schema with namespace `android_app` | Template present in `web/public/.well-known/` | **READY (TEMPLATE)** |
| **Domain Resolution** | `bubakangreen.web.app` deployed & reachable | Project pending Firebase deployment | **BLOCKED / NOT YET DEPLOYED** |
| **Release Signing SHA-256** | Real release keystore fingerprint populated | Placeholder in template | **BLOCKED / NOT YET AVAILABLE** |
| **OS Domain Verification** | `pm get-app-links` reports `verified` | Unexecuted on production domain | **NOT TESTED** |

---

## 9. Test Device & Environment Baseline

- **Device:** Android Virtual Device (AVD) Pixel 7 & Physical Test Device
- **OS Version:** Android 14 / Android 15 (API 34 / 35)
- **Scanner:** Google Lens / Native Camera App
- **Current Test Status:** Route parsing verified via ADB intent dispatch. Full domain association verification **PENDING RELEASE KEY & FIREBASE DEPLOYMENT**.

---

## 10. Timestamp

- **Audit Date:** 2026-09-27 14:00:00 UTC+7
- **Verification Milestone:** Phase 5 Pre-Execution Architecture Audit

---

## 11. Verification Evidence & Summary

- **Evidence:**
  - Intent filter inspected at [`AndroidManifest.xml#L30-L45`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/app/src/main/AndroidManifest.xml).
  - Deep link handler verified at [`BubakanNavHost.kt#L198-L200`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/app/src/main/java/id/bubakangreen/app/navigation/BubakanNavHost.kt) and [`BubakanNavHost.kt#L226-L228`](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/app/src/main/java/id/bubakangreen/app/navigation/BubakanNavHost.kt).
  - Web fallback verified on local preview at `http://localhost:8080/plant.html`.
- **Formal Conclusion:**
  - The software architecture for Android App Links is **READY**.
  - Production verification is **BLOCKED / NOT YET AVAILABLE** pending:
    1. Production release keystore generation and SHA-256 extraction.
    2. Live Firebase Hosting deployment of `bubakangreen.web.app`.
  - Android App Links will **NOT** be claimed as "VERIFIED" until live domain association passes.
