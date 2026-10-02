# Phase 5 — End-to-End Test Matrix

**Product**: Bubakan Green  
**Scope**: QR, In-App Scanner, Android App Links, Web Fallback, Firebase Integration  
**Execution Phase**: Phase 5 (Post-Approval)  

---

## 1. Test Matrix Overview

| Test ID | Scenario | Pre-conditions | Action | Expected Result | Evidence Required |
|---|---|---|---|---|---|
| **TEST-01** | App installed, App Links verified, scan Plant QR | App installed with verified domain association | Scan physical/screen QR for `https://bubakangreen.web.app/plant/<id>` via system scanner | App launches directly to `PlantDetailScreen` for the matching ID without browser disambiguation dialog | adb logcat / Intent dispatch log / App screen capture |
| **TEST-02** | App installed, App Links verified, scan Location QR | App installed with verified domain association | Scan physical/screen QR for `https://bubakangreen.web.app/location/<id>` via system scanner | App launches directly to `LocationDetailScreen` for the matching ID without browser disambiguation dialog | adb logcat / Intent dispatch log / App screen capture |
| **TEST-03** | App NOT installed, scan Plant QR | App uninstalled or disabled | Scan `https://bubakangreen.web.app/plant/<id>` via generic camera / browser | Mobile browser loads `plant.html` fallback displaying plant name, scientific name, mascot, botanical overview, and APK download CTA | Browser URL + rendered page screenshot |
| **TEST-04** | App NOT installed, scan Location QR | App uninstalled or disabled | Scan `https://bubakangreen.web.app/location/<id>` via generic camera / browser | Mobile browser loads `location.html` fallback displaying garden plot name, RW, description, and APK download CTA | Browser URL + rendered page screenshot |
| **TEST-05** | Install APK after visiting Web Fallback | Web page viewed on browser, user downloads & installs APK | Re-scan or tap the identical QR code | Android resolves domain association to installed app; deep link opens directly in native screen | adb shell pm get-app-links / screen capture |
| **TEST-06** | Update Firestore plant data (Same QR) | Existing physical QR deployed | Update plant description/benefits in Firestore (`master_plants/<id>`) | Scan the SAME QR code; both App and Web Fallback reflect updated text immediately without regenerating QR | Firestore document timestamp + App & Web updated UI |
| **TEST-07** | Update Firestore location data (Same QR) | Existing physical QR deployed | Update location condition note/status in Firestore (`locations/<id>`) | Scan the SAME QR code; updated garden condition appears in App and Web Fallback without changing QR URL | Firestore document timestamp + App & Web updated UI |
| **TEST-08** | Unpublish resource | Published entity has active QR; resource marked `isPublished = false` | Scan QR code | App shows graceful UNAVAILABLE state: "Informasi ini sedang tidak tersedia." Web fallback shows equivalent unlisted state. No crashes. | App & Web UI showing friendly notice |
| **TEST-09** | Invalid / Non-existent resource ID | Any device | Open deep link or scan QR with non-existent ID (e.g. `/plant/pl-invalid999`) | App displays graceful NOT FOUND state: "Data tanaman/lokasi tidak ditemukan." No unhandled exceptions or crashes. | App state verification logcat / UI |
| **TEST-10** | In-App Scanner Cancellation | App opened to scanner launcher | User opens Google Code Scanner UI then presses back / cancel | App dismisses scanner cleanly and returns to previous screen without crashing or hanging | UI transition / Lifecycle log |
| **TEST-11** | Network Unavailable during QR resolution | Device in airplane mode / offline | App opens deep link from QR | App displays graceful NETWORK ERROR state: "Periksa koneksi internet dan coba lagi." Retry button offered. | Airplane mode UI capture |
| **TEST-12** | Responsive Frame & Density Validation | Physical device / emulator at 360dp, 393dp, 412dp, landscape | Navigate QR display and detail screens across all densities | No text clipping, no horizontal overflow, touch targets >= 48dp, navigation bar properly padded | Automated Compose screenshot / Layout inspector |

---

## 2. Evidence Gathering Template

For each test executed during Phase 5 verification, record:
```text
Test ID: TEST-XX
Device / Emulator: [e.g., Pixel 7 API 34 Emulator / Xiaomi 12 Android 13 Physical]
Android OS Version: [e.g., Android 14.0 (API 34)]
App Version: 1.0.0 (versionCode 1)
Build Type: debug / release
Target URL: https://bubakangreen.web.app/plant/<id>
Action Executed: [Description of exact gesture, scan, or command]
Expected Result: [Statement from matrix]
Actual Result: [Observed behavior]
Evidence Attachment: [Logcat snippet / Screenshot path / adb command output]
Verification Verdict: PASS / FAIL
Timestamp: [ISO 8601 string]
```
