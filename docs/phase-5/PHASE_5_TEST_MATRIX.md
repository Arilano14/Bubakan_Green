# Phase 5 End-to-End Test Matrix

**Product**: Bubakan Green  
**Execution Phase**: Post-Approval (Phase 5)  

---

## 1. Test Execution Matrix

| Test ID | Title | Scenario | Expected Result | Evidence Required | Status |
|---|---|---|---|---|---|
| Test ID | Title | Scenario | Expected Result | Evidence Required | Status |
|---|---|---|---|---|---|
| **TEST-01** | App installed, scan plant QR | App installed, autoVerify active, scan `https://bubakangreen.web.app/plant/<id>` | Directly opens `PlantDetailScreen` | Logcat intent dispatch & screen capture (`screen_deeplink_plant.png`) | VERIFIED |
| **TEST-02** | App installed, scan location QR | App installed, autoVerify active, scan `https://bubakangreen.web.app/location/<id>` | Directly opens `LocationDetailScreen` | Logcat intent dispatch & screen capture (`screen_deeplink_location.png`) | VERIFIED |
| **TEST-03** | App absent, open plant QR | App uninstalled, browser opens `https://bubakangreen.web.app/plant/<id>` | Web fallback `plant.html` loads plant data + mascot + APK download button | Source inspection & REST API payload | IMPLEMENTED |
| **TEST-04** | App absent, open location QR | App uninstalled, browser opens `https://bubakangreen.web.app/location/<id>` | Web fallback `location.html` loads location data + mascot + APK download button | Source inspection & REST API payload | IMPLEMENTED |
| **TEST-05** | Install app after visiting web / implicit dispatch | Implicit VIEW intent without package flag | System opens native screen in app directly via App Links | Implicit intent dispatch (`screen_applinks_cabai.png`) | VERIFIED |
| **TEST-06** | Data update (Same QR) | Update plant data in Firestore (`/master_plants/{id}`) | Scan SAME physical QR code; updated text appears immediately | Single source of truth / immutable stable ID | IMPLEMENTED |
| **TEST-07** | Location condition update (Same QR) | Update location condition in Firestore (`/locations/{id}`) | Scan SAME physical QR code; updated condition appears immediately | Single source of truth / immutable stable ID | IMPLEMENTED |
| **TEST-08** | Unpublish resource | Set `isPublished = false` | App shows graceful "Informasi ini sedang tidak tersedia." Web shows unavailable notice | QrCodeGenerator eligibility & detail fallback | IMPLEMENTED |
| **TEST-09** | Invalid / missing ID | Scan non-existent ID `/plant/non_existent_id` | App displays graceful "Informasi Belum Tersedia" with mascot; zero crash | Screen capture (`screen_invalid_plant.png`) | VERIFIED |
| **TEST-10** | In-app scanner cancellation | Launch Google Code Scanner, user taps back / dismisses UI | Clean dismissal, app returns to previous screen | `addOnCanceledListener` implementation | IMPLEMENTED |
| **TEST-11** | Network offline | Device in airplane mode, open QR | App displays graceful offline banner | `OfflineStatusBar` & cached fallback | IMPLEMENTED |
| **TEST-12** | Responsive frame check | Test on 412x915dp (Pixel 7 emulator), touch targets >= 48dp | No clipping, no horizontal overflow, touch targets >= 48dp | UI Automator bounds `[912,314][1038,440]` = 48dp | VERIFIED |

