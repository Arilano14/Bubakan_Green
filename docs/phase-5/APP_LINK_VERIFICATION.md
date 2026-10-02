# Android App Links Verification Protocol & Status

**Product**: Bubakan Green  
**Canonical Domain**: `https://bubakangreen.web.app`  
**Package Debug**: `id.bubakangreen.app.debug`  
**Package Release**: `id.bubakangreen.app`  

---

## 1. Intent Filter Configuration

The Android Manifest ([AndroidManifest.xml](file:///c:/Users/Arilano/Downloads/Project%20ARICE/Bubakan%20Green/app/src/main/AndroidManifest.xml)) defines the following intent-filter on `MainActivity`:

```xml
<intent-filter android:autoVerify="true">
    <action android:name="android.intent.action.VIEW" />
    <category android:name="android.intent.category.DEFAULT" />
    <category android:name="android.intent.category.BROWSABLE" />
    <data android:scheme="https" android:host="bubakangreen.web.app" android:pathPrefix="/plant/" />
    <data android:scheme="https" android:host="bubakangreen.web.app" android:pathPrefix="/location/" />
</intent-filter>
```

> **CRITICAL VERIFICATION RULE**:
> The presence of `android:autoVerify="true"` alone is **NOT** verification evidence. Verification requires hosting a valid `assetlinks.json` matched against the actual certificate fingerprint of the installed APK.

---

## 2. Digital Asset Links (`assetlinks.json`)

### 2.1 File Location
Target URL: `https://bubakangreen.web.app/.well-known/assetlinks.json`  
Local Source: `web/public/.well-known/assetlinks.json`

### 2.2 Current File Content
```json
[
  {
    "relation": ["delegate_permission/common.handle_all_urls"],
    "target": {
      "namespace": "android_app",
      "package_name": "id.bubakangreen.app",
      "sha256_cert_fingerprints": [
        "REPLACE_WITH_RELEASE_OR_DEBUG_KEYSTORE_SHA256_FINGERPRINT"
      ]
    }
  }
]
```

---

## 3. Blocker Status for Production Verification

* **Status**: **`BLOCKED FOR PRODUCTION VERIFIED STATUS`**.
* **Reason**: `assetlinks.json` contains a placeholder. Per Section 18 of project instructions:
  - Do NOT fabricate a fake SHA-256 fingerprint.
  - Phase 5 coding may continue for non-verification components, but production verified status remains blocked until the official release signing identity is generated or provided.
* **Verification Command (Post-Deployment)**:
  ```bash
  adb shell pm get-app-links id.bubakangreen.app.debug
  ```
  Expected output for verified domain:
  ```text
  bubakangreen.web.app: verified
  ```
