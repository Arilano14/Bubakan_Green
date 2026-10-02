# Scanner Architecture & Permission Matrix

**Product**: Bubakan Green  
**Component**: In-App Scanner & Device Permissions  

---

## 1. Scanner Selection

* **Selected Engine**: **Google Code Scanner** (`com.google.android.gms:play-services-code-scanner:16.1.0`).
* **Design Decision**:
  * Using Google Code Scanner delegates camera hardware interaction and scanning UI to Google Play Services.
  * **ZERO CAMERA PERMISSION REQUIRED** in `AndroidManifest.xml` from the host application.
  * Avoids bloating Bubakan Green with CameraX, ML Kit Vision models, or manual autofocus logic.

---

## 2. Permission Matrix

| Capability | Manifest Permission | Runtime Prompt | Justification |
|---|---|---|---|
| **In-App Google Code Scanner** | **NONE** | NONE | Scanning lifecycle is handled out-of-process by Google Play Services |
| **Android App Links (Deep Linking)** | **NONE** | NONE | Standard OS Intent matching |
| **Opening Web Fallback** | **NONE** | NONE | Browser intent invocation |
| **Network Requests** | `android.permission.INTERNET`<br>`android.permission.ACCESS_NETWORK_STATE` | Normal (Auto-granted) | Required for Firestore and Auth operations |
| **PIC Location Acquisition** | `android.permission.ACCESS_FINE_LOCATION`<br>`android.permission.ACCESS_COARSE_LOCATION` | Runtime Permission | Existing Phase 3/4 feature for geotagging garden plots |

---

## 3. Supported Barcode Formats

Per Section 14, Bubakan Green explicitly restricts scanner parsing to:
1. **QR Code** (`Barcode.FORMAT_QR_CODE`) — Primary format for botanical garden signs.
2. Formats are constrained via `GmsBarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_QR_CODE)`.
3. Generic or non-QR barcodes outside canonical schemas are rejected safely.
