# Firestore Runtime Diagnosis Report
**Project:** Bubakan Green  
**Date:** 2026-10-05  
**Component:** Android Application + Cloud Firestore Connection  

---

## 1. Issue Description

When the Bubakan Green Android application launches on the emulator, the Home screen displays an error view with the sad mascot:
- **Title:** "Oops! Ada Sedikit Kendala"
- **Message:** "Error getting Query snapshot"
- **Behavior:** Katalog and Lokasi data fail to load.

---

## 2. Diagnostics & Captured Evidence

### Diagnostic Profile (Section 4)

- **Error:** `"Error getting Query snapshot"` (displayed on Android UI via `StateErrorView`)
- **Actual Exception:**  
  `com.google.firebase.firestore.FirebaseFirestoreException: PERMISSION_DENIED: Missing or insufficient permissions.`
- **Firebase Project:** `bubakan-green` (Project Number: `1054147629887`)
- **Affected Collections:**
  - `/master_plants`
  - `/locations`
- **Queries Executed by Client:**
  - `Query(target=Query(locations order by __name__);limitType=LIMIT_TO_FIRST)`  
    (Triggered by `FirestoreLocationRepository.getPublishedLocations()`)
  - `Query(target=Query(master_plants order by __name__);limitType=LIMIT_TO_FIRST)`  
    (Triggered by `FirestorePlantRepository.getAllMasterPlants()`)
- **Active Cloud Rule:**
  Firebase Console default production rule currently active in cloud:
  ```javascript
  rules_version = '2';
  service cloud.firestore {
    match /databases/{database}/documents {
      match /{document=**} {
        allow read, write: if false;
      }
    }
  }
  ```

---

## 3. Empirical Evidence (Logcat & REST API)

### A. Android Logcat Evidence
Captured from device `emulator-5554` (PID 1313):
```text
10-05 14:28:02.768  1313  2266 W Firestore: (25.1.2) [Firestore]: Listen for Query(target=Query(locations order by __name__);limitType=LIMIT_TO_FIRST) failed: Status{code=PERMISSION_DENIED, description=Missing or insufficient permissions., cause=null}
10-05 14:28:02.811  1313  2266 W Firestore: (25.1.2) [Firestore]: Listen for Query(target=Query(master_plants order by __name__);limitType=LIMIT_TO_FIRST) failed: Status{code=PERMISSION_DENIED, description=Missing or insufficient permissions., cause=null}
10-05 14:38:09.954  1313  2266 W Firestore: (25.1.2) [Firestore]: Listen for Query(target=Query(locations order by __name__);limitType=LIMIT_TO_FIRST) failed: Status{code=PERMISSION_DENIED, description=Missing or insufficient permissions., cause=null}
10-05 14:38:23.411  1313  2266 W Firestore: (25.1.2) [Firestore]: Listen for Query(target=Query(master_plants order by __name__);limitType=LIMIT_TO_FIRST) failed: Status{code=PERMISSION_DENIED, description=Missing or insufficient permissions., cause=null}
```

### B. Firestore REST API Evidence (Web Client Endpoint)
Testing the public Firestore REST API endpoint:
```http
GET https://firestore.googleapis.com/v1/projects/bubakan-green/databases/(default)/documents/master_plants
```
Live HTTP Response:
```json
HTTP/1.1 403 Forbidden
{
  "error": {
    "code": 403,
    "message": "Missing or insufficient permissions.",
    "status": "PERMISSION_DENIED"
  }
}
```
**Conclusion:**
The Firestore database `(default)` in project `bubakan-green` is active, online, and responsive. However, because newly created production Firestore databases default to `allow read, write: if false;`, both Android WatchStream and Web REST clients receive `PERMISSION_DENIED`.


---

## 4. Root Cause Analysis

1. **Unpublished Cloud Firestore Rules:**  
   When the Cloud Firestore database was created in the Firebase Console for project `bubakan-green`, it defaulted to standard Production Mode (`allow read, write: if false;`). The project's custom [`firestore.rules`](../../firestore.rules) (which permits public read on `master_plants`, `location_plants`, and `locations`) has not yet been published/deployed to Firebase Cloud. Therefore, any read attempt by an unauthenticated public client is rejected at the Google Cloud gateway with `PERMISSION_DENIED`.

2. **"Rules Are Not Filters" Query Constraint on `/locations`:**  
   In local [`firestore.rules`](../../firestore.rules), `/locations/{locationId}` specifies:
   ```javascript
   match /locations/{locationId} {
     allow read: if resource.data.isPublished == true ||
                    resource.data.status == 'PUBLISHED' ||
                    resource.data.status == 'ACTIVE' ||
                    resource.data.status == 'NEEDS_MAINTENANCE' ||
                    isAdmin();
     allow write: if isAdmin();
   }
   ```
   In Firestore, security rules do not filter query results. If a client queries `collection("locations").snapshots()` without a `.whereEqualTo("isPublished", true)` query filter, Firestore rejects the query entirely because the rule requires inspecting `resource.data` which could theoretically contain unpermitted documents.  
   To allow the botanical garden directory to be read publicly (like `/master_plants`), the rule for public read on `/locations` must be:
   ```javascript
   match /locations/{locationId} {
     allow read: if true;
     allow write: if isAdmin();
   }
   ```
   This retains strict Admin authorization for all write/edit operations while allowing public read queries to proceed without rejection.

---

## 5. Required Fix & Remediation Plan

### Step 1: Update Local `firestore.rules`
Ensure `/locations/{locationId}` allows public read (`allow read: if true; allow write: if isAdmin();`), matching `/master_plants` and `/location_plants`. Write access remains strictly restricted to authenticated Admins with active roles.

### Step 2: Publish Rules to Firebase Console
Copy and publish the content of [`firestore.rules`](../../firestore.rules) into:
> **Firebase Console** -> **Project `bubakan-green`** -> **Firestore Database** -> **Rules** tab -> Click **Publish**.

### Step 3: Seed Real Canonical Data
Execute the idempotent seeder to populate:
- 9 verified `master_plants`
- 2 approved featured `locations` (Urban Farming Kelurahan Bubakan, Taman Toga RW 03)
- 10 approved `location_plants` relations

### Step 4: Verify Runtime
Re-test the Android app on the emulator and verify that the snapshot listener receives actual data, resolving "Error getting Query snapshot".
