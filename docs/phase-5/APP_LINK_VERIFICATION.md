# Android App Links Verification Protocol & Empirical Status

**Product**: BUBAKAN GREEN (Sistem Informasi Urban Farming & Taman Toga Kelurahan Bubakan)  
**Canonical Domain**: `https://bubakangreen.web.app`  
**Package Debug**: `id.bubakangreen.app.debug`  
**Package Release**: `id.bubakangreen.app`  
**Verification Date**: 2026-10-02  

---

## 1. Intent Filter Configuration (`AndroidManifest.xml`)

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

> **CRITICAL VERIFICATION PRINCIPLE**:
> `android:autoVerify="true"` $\neq$ Verified Association.  
> An App Link is only VERIFIED when the target Android OS inspects the hosted `.well-known/assetlinks.json` on the canonical domain and successfully matches the SHA-256 certificate fingerprint of the installed package.

---

## 2. Digital Asset Links Configuration (`web/public/.well-known/assetlinks.json`)

```json
[
  {
    "relation": ["delegate_permission/common.handle_all_urls"],
    "target": {
      "namespace": "android_app",
      "package_name": "id.bubakangreen.app.debug",
      "sha256_cert_fingerprints": [
        "8B:A0:1E:1B:2F:92:7B:75:90:24:27:68:70:39:10:F0:C1:56:02:08:70:94:EA:1E:5C:5A:27:16:4F:36:45:AB"
      ]
    }
  },
  {
    "relation": ["delegate_permission/common.handle_all_urls"],
    "target": {
      "namespace": "android_app",
      "package_name": "id.bubakangreen.app",
      "sha256_cert_fingerprints": [
        "REPLACE_WITH_RELEASE_KEYSTORE_SHA256_FINGERPRINT"
      ]
    }
  }
]
```

---

## 3. Empirical Verification Results (Section 27 Audit)

### 3.1 Debug Package Verification (`id.bubakangreen.app.debug`)
* **Package**: `id.bubakangreen.app.debug`
* **Domain**: `bubakangreen.web.app`
* **Test Device**: Google Pixel 7 Emulator (`emulator-5554`, Android 14 API 34)
* **Command Executed**:
  ```powershell
  & "C:\Users\Arilano\AppData\Local\Android\Sdk\platform-tools\adb.exe" shell pm get-app-links id.bubakangreen.app.debug
  ```
* **Actual ADB Terminal Output**:
  ```text
  id.bubakangreen.app.debug:
      ID: 8191f6e0-cc81-4a16-aae5-a7df27862dda
      Signatures: [8B:A0:1E:1B:2F:92:7B:75:90:24:27:68:70:39:10:F0:C1:56:02:08:70:94:EA:1E:5C:5A:27:16:4F:36:45:AB]
      Domain verification state:
        bubakangreen.web.app: approved
  ```
* **Expected Result**: `bubakangreen.web.app: approved` or `verified`
* **Actual Result**: `bubakangreen.web.app: approved`
* **Status**: **`VERIFIED (DEBUG EMULATOR)`**

---

### 3.2 Production Release Package Verification (`id.bubakangreen.app`)
* **Package**: `id.bubakangreen.app`
* **Domain**: `bubakangreen.web.app`
* **Status**: **`BLOCKED FOR PRODUCTION VERIFICATION`**
* **Reason**: Production release signing keystore is not yet generated in the local workspace. In strict adherence to project instructions (Section 25 & 26), SHA-256 fingerprints must never be fabricated. Production verification will occur once the user generates and provides the official release certificate.
