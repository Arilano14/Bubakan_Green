# Phase 5 — QR & Canonical URL Contract

**Product**: Bubakan Green — Sistem Informasi Urban Farming & Taman Toga Kelurahan Bubakan  
**Domain**: `https://bubakangreen.web.app`  
**Document Status**: APPROVED SPECIFICATION  

---

## 1. Core Principles

1. **Minimal Payload**: The QR code MUST ONLY store the canonical HTTPS URL string.
2. **Zero Inlined Data**:
   - ❌ NO JSON payloads.
   - ❌ NO plant descriptions, attributes, or photos.
   - ❌ NO Firestore document snapshots.
   - ❌ NO authentication tokens, credentials, or session data.
   - ❌ NO mutable metadata (e.g., localized names or tags).
3. **Single Source of Truth**:
   The QR code points to an immutable resource identity. Both the Android application and the Web Fallback fetch real-time data dynamically from the same underlying Firestore collection using this identity.

```
       [ Physical QR Code on Garden Sign ]
                       │
                       ▼
    https://bubakangreen.web.app/plant/pl-a84f21c9
                       │
         ┌─────────────┴─────────────┐
         ▼                           ▼
[ Android App Link ]        [ Web Fallback ]
  Resolves via Repo          Resolves via Web JS
         │                           │
         └─────────────┬─────────────┘
                       ▼
          [ Firestore /master_plants ]
```

---

## 2. Canonical URL Schema

### 2.1 Master Botanical Plant Detail
- **Canonical Schema**: `https://bubakangreen.web.app/plant/<stable-id>`
- **Example**: `https://bubakangreen.web.app/plant/pl-a84f21c9`
- **Firestore Target**: `/master_plants/{stable-id}` (or `/plants/{stable-id}`)
- **Identifier Format**: Prefix `pl-` followed by an alphanumeric opaque ID (e.g., `pl-8f92b1a0` or existing Firestore master plant doc ID).

### 2.2 Garden Plot / Location Detail
- **Canonical Schema**: `https://bubakangreen.web.app/location/<stable-id>`
- **Example**: `https://bubakangreen.web.app/location/loc-91af7b23`
- **Firestore Target**: `/locations/{stable-id}`
- **Identifier Format**: Prefix `loc-` followed by an alphanumeric opaque ID (e.g., `loc-c4b9d012` or existing Firestore location doc ID).

---

## 3. Stable Identifier Specifications

| Attribute | Specification | Violation Examples (Forbidden) |
|---|---|---|
| **Opaqueness** | Identity does not derive from or convey human-readable titles. | `/plant/jahe-merah`, `/plant/sereh` |
| **Immutability** | Identity NEVER changes when name, scientific name, or image updates. | Changing ID when spelling is corrected. |
| **URL-Safety** | Consists solely of ASCII lowercase alphanumeric characters and hyphens `[a-z0-9_-]`. | `/plant/Jahe Merah%20#1?id=9` |
| **Collision Resistance** | Globally unique within the entity domain. | Reusing IDs across multiple species. |

---

## 4. Eligibility & Generation Rules

A resource can ONLY have an active physical QR code generated if it satisfies:
1. **Existence**: The document exists in Firestore.
2. **Approval**: Status is `PUBLISHED` or `ACTIVE` (not `DRAFT`, `PENDING_APPROVAL`, or `ARCHIVED`).
3. **Field Verification**: Coordinates/botanical content has undergone required field verification.
4. **Stable Identity**: Contains a compliant, non-empty stable ID.

> **Regeneration Rule**:
> Re-rendering or re-printing a QR code MUST NOT change the stable ID. The existing stable ID and URL are preserved so that existing physical signs in Kelurahan Bubakan garden plots remain functional indefinitely.

---

## 5. Resolution & Deep Link Dispatch

### 5.1 Android In-App Dispatch
When an intent is received (`android.intent.action.VIEW` matching `https://bubakangreen.web.app/plant/*` or `/location/*`):
1. Extract URI: `data.pathSegments`.
2. Extract resource type (`plant` or `location`) and entity ID.
3. Validate entity ID format (reject malformed/empty IDs).
4. Fetch entity from repository (`PlantRepository.getPlantById` or `LocationRepository.getLocationById`).
5. Verify `isPublished == true`:
   - If **Published**: Render `PlantDetailScreen` or `LocationDetailScreen`.
   - If **Unpublished / Draft**: Show Graceful "Informasi ini sedang tidak tersedia."
   - If **Not Found**: Show Graceful "Data tidak ditemukan."
   - If **Network Error**: Show "Periksa koneksi internet dan coba lagi."

### 5.2 Web Fallback Dispatch
When opened on a browser (device without the app or non-Android OS):
1. Hosting rewrite maps `/plant/**` to `/plant.html` and `/location/**` to `/location.html`.
2. Client-side script parses `window.location.pathname`.
3. Queries Firestore client SDK for the document.
4. Renders botanical summary, mascot, and smart CTA:
   - "Buka di Aplikasi" (intent URL).
   - "Download Bubakan Green APK".
