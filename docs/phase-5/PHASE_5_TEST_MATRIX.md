# Phase 5 End-to-End Test Matrix

**Product**: Bubakan Green  
**Execution Phase**: Post-Approval (Phase 5)  

---

## 1. Test Execution Matrix

| Test ID | Title | Scenario | Expected Result | Evidence Required | Status |
|---|---|---|---|---|---|
| **TEST-01** | App installed, scan plant QR | App installed, autoVerify active, scan `https://bubakangreen.web.app/plant/<id>` | Directly opens `PlantDetailScreen` | Logcat intent dispatch / Compose screen capture | NOT TESTED |
| **TEST-02** | App installed, scan location QR | App installed, autoVerify active, scan `https://bubakangreen.web.app/location/<id>` | Directly opens `LocationDetailScreen` | Logcat intent dispatch / Compose screen capture | NOT TESTED |
| **TEST-03** | App absent, open plant QR | App uninstalled, browser opens `https://bubakangreen.web.app/plant/<id>` | Web fallback `plant.html` loads plant data + mascot + APK download button | Browser screenshot | NOT TESTED |
| **TEST-04** | App absent, open location QR | App uninstalled, browser opens `https://bubakangreen.web.app/location/<id>` | Web fallback `location.html` loads location data + mascot + APK download button | Browser screenshot | NOT TESTED |
| **TEST-05** | Install app after visiting web | Install APK, tap identical QR code again | System opens native screen in app | App Links association output | NOT TESTED |
| **TEST-06** | Data update (Same QR) | Update plant data in Firestore (`/master_plants/{id}`) | Scan SAME physical QR code; updated text appears immediately | Firestore document timestamp + App UI | NOT TESTED |
| **TEST-07** | Location condition update (Same QR) | Update location condition in Firestore (`/locations/{id}`) | Scan SAME physical QR code; updated condition appears immediately | Firestore document timestamp + App UI | NOT TESTED |
| **TEST-08** | Unpublish resource | Set `isPublished = false` | App shows graceful "Informasi ini sedang tidak tersedia." Web shows unavailable notice | Screen captures | NOT TESTED |
| **TEST-09** | Invalid / missing ID | Scan non-existent ID `/plant/not-found` | App displays graceful "Data tanaman/lokasi tidak ditemukan." No crash | Screen capture | NOT TESTED |
| **TEST-10** | In-app scanner cancellation | Launch Google Code Scanner, user taps back / dismisses UI | Clean dismissal, app returns to previous screen | UI transition record | NOT TESTED |
| **TEST-11** | Network offline | Device in airplane mode, open QR | App displays graceful "Periksa koneksi internet dan coba lagi." | Airplane mode capture | NOT TESTED |
| **TEST-12** | Responsive frame check | Test on 360dp, 393dp, 412dp, landscape | No clipping, no horizontal overflow, touch targets >= 48dp | Layout inspector captures | NOT TESTED |
