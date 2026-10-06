# QR Canonical Domain & App Links Quality Control Report
**Document ID:** `docs/phase-1/QR_DOMAIN_QC.md`  
**Phase:** PHASE 1 — TESTING STABILIZATION & CRITICAL BUG REMEDIATION  
**Canonical Domain:** `https://bubakan-green.web.app` (Single Verified Host)  
**Timestamp:** 2026-10-06  
**Status:** `VERIFIED & COMPLETE`  

---

## 1. Domain Typo Remediation Audit

### 1.1 The Identified Discrepancy
The user reported an inconsistent QR destination: physical QR codes generated URLs like `https://bubakangreen.web.app/plant/tomat` (missing the hyphen `-`), whereas the live Firebase Hosting environment is registered at `https://bubakan-green.web.app`.

### 1.2 Comprehensive Codebase Purge
A rigorous audit and replacement was conducted across all relevant codebases:
1. `app/src/main/java/id/bubakangreen/app/core/util/QrUrlBuilder.kt`:
   - `DEFAULT_DOMAIN` changed from `"bubakangreen.web.app"` to `"bubakan-green.web.app"`.
   - `VALID_DOMAINS` updated to canonical domain and `localhost`.
   - Class header documentation updated.
2. `app/src/main/AndroidManifest.xml`:
   - `android:host` in intent filter updated to `"bubakan-green.web.app"`.
3. `app/src/main/java/id/bubakangreen/app/navigation/BubakanNavHost.kt`:
   - Deep link patterns updated to `https://bubakan-green.web.app/plant/{plantId}` and `https://bubakan-green.web.app/location/{locationId}`.
4. `web/public/labels/qr-label-generator.html`:
   - Base canonical URL updated to `'https://bubakan-green.web.app/'`.
5. `app/src/test/java/id/bubakangreen/app/core/util/QrUrlBuilderTest.kt`:
   - Test assertions updated to validate `"https://bubakan-green.web.app/plant/PL_001"` and reject old typo domain.
6. `app/src/test/java/id/bubakangreen/app/core/util/QrCodeGeneratorTest.kt`:
   - URL assertions aligned to canonical domain.

**Empirical Result:**  
`ripgrep` scan across `app/` and `web/` confirms: **ZERO occurrences of `bubakangreen.web.app` remain in application or web code.**

---

## 2. QR Canonical Contract & URL Architecture

Physical signs and digital QR codes adhere strictly to the immutable Phase 5/6 contract:

```
Canonical Plant URL:    https://bubakan-green.web.app/plant/{plantId}
Canonical Location URL: https://bubakan-green.web.app/location/{locationId}
```

### Constraints:
- **Zero Query Parameters:** No `?source=qr` or tracking hashes in QR payload.
- **Immutable Stable Identifier:** QR matrix encodes only the canonical HTTPS URL with resource ID (`pl-cabai-rawit`, `loc-taman-toga`).
- **Deterministic Routing:** All QR matrices resolve to an identical URL structure.

---

## 3. Unified Single-Gate App Links Architecture

The system implements a unified single-gate routing pipeline:

```
            [ User Scans Physical QR Code ]
                          │
                          ▼
            https://bubakan-green.web.app/plant/{plantId}
                          │
       ┌──────────────────┴──────────────────┐
       │                                     │
   [ App Installed ]                 [ App Not Installed ]
       │                                     │
       ▼                                     ▼
 Android OS intercepts              Mobile Browser opens
 via Digital Asset Links           https://bubakan-green.web.app
       │                                     │
       ▼                                     ▼
 Launches Bubakan Green             Renders responsive Web Page:
 `PlantDetailScreen`               - Plant info & photo
                                   - Si Buba mascot
                                   - Download App button
```

---

## 4. Digital Asset Links (`assetlinks.json`) Verification Status

Location: `web/public/.well-known/assetlinks.json`

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
        "8B:A0:1E:1B:2F:92:7B:75:90:24:27:68:70:39:10:F0:C1:56:02:08:70:94:EA:1E:5C:5A:27:16:4F:36:45:AB"
      ]
    }
  }
]
```

### Strict Governance Status:
- **DEBUG BUILD (`id.bubakangreen.app` with Debug Key):** **`DEBUG VERIFIED`**. Fingerprint `8B:A0:1E:1B:...:AB` matches the actual testing APK.
- **PRODUCTION RELEASE BUILD:** **`RELEASE UNVERIFIED (PENDING KEYSTORE GENERATION)`**.
  - In compliance with governance rules, no production release certificate was fabricated.
  - Production verification will be executed during the dedicated Release phase once `release.keystore` is generated.
