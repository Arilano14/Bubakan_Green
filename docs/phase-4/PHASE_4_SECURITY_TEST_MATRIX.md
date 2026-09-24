# PHASE 4 SECURITY & ROLE TEST MATRIX — BUBAKAN GREEN

**Sistem Informasi Urban Farming & Taman Toga**  
**Kelurahan Bubakan, Kecamatan Mijen, Kota Semarang**  
**Document:** `docs/phase-4/PHASE_4_SECURITY_TEST_MATRIX.md`  
**Date:** 2026-09-24  
**Auditor Role:** Application Security & Cloud Governance Engineer  
**Status:** DEFINED & VERIFIED AGAINST FIRESTORE SECURITY RULES  

---

## 1. Security Architecture Principles

1. **Backend-Enforced Authorization:** Security is strictly enforced by Cloud Firestore Security Rules (`web/firestore.rules`). UI element visibility is purely an ergonomic aid; the server strictly rejects unauthorized writes regardless of client modifications.
2. **Principle of Least Privilege:**
   - **Public:** Read published data only. Write: 0% permission.
   - **PIC (Petugas Lapangan):** Write access restricted strictly to community gardens where `picUid == request.auth.uid`. New submissions must carry `status == 'PENDING_APPROVAL'`.
   - **Admin (Pemerintah Kelurahan):** Full administrative governance (moderation, publication, master data management, and PIC assignment).
3. **Immutable Audit Trail:** Actions in `/audit_logs` can only be appended; updating or deleting audit logs is permanently prohibited (`allow update, delete: if false;`).

---

## 2. Comprehensive Security Test Matrix

| Actor / Role | Operation / Target Resource | Expected UI Behavior | Expected Backend Rule Behavior (`firestore.rules`) | Rule Logic / Enforcement Clause | Verification Status |
|:---|:---|:---|:---|:---|:---:|
| **Public (Anonim)** | Read published garden (`status: "PUBLISHED"`) | Renders garden card & details | **ALLOW** | `resource.data.status == 'PUBLISHED'` | ✅ VERIFIED |
| **Public (Anonim)** | Read pending/draft garden (`status: "PENDING_APPROVAL"`) | Hidden from listings & search | **DENY** | Denied (Not PUBLISHED and not PIC/Admin) | ✅ VERIFIED |
| **Public (Anonim)** | Create new location | No form accessible | **DENY** | `isAuthenticated()` fails | ✅ VERIFIED |
| **Public (Anonim)** | Modify botanical master plant | No edit controls | **DENY** | `isAdmin()` fails | ✅ VERIFIED |
| **PIC A (Assigned Loc A)** | Create Location with `status: "PENDING_APPROVAL"` | Form submits successfully | **ALLOW** | `isPIC() && request.resource.data.status == 'PENDING_APPROVAL'` | ✅ VERIFIED |
| **PIC A (Assigned Loc A)** | Create Location directly with `status: "PUBLISHED"` | Blocked by form validation | **DENY (PERMISSION_DENIED)** | Denied: PIC cannot self-publish new locations | ✅ VERIFIED |
| **PIC A (Assigned Loc A)** | Update name/description of Location A | Edit form submits successfully | **ALLOW** | `isPIC() && resource.data.picUid == request.auth.uid` | ✅ VERIFIED |
| **PIC A (Assigned Loc A)** | Approve own Location A (`status: "PUBLISHED"`) | No approval button in UI | **DENY (PERMISSION_DENIED)** | `request.resource.data.status == resource.data.status` fails | ✅ VERIFIED |
| **PIC A (Assigned Loc A)** | Update Location B (Assigned to PIC B) | Location B not in PIC A's list | **DENY (PERMISSION_DENIED)** | Denied: `resource.data.picUid != request.auth.uid` | ✅ VERIFIED |
| **PIC A (Assigned Loc A)** | Add `LocationPlant` to Location A | Plant form submits successfully | **ALLOW** | `isPIC() && get(.../locations/LocA).data.picUid == request.auth.uid` | ✅ VERIFIED |
| **PIC A (Assigned Loc A)** | Add `LocationPlant` to Location B | Blocked by UI selection | **DENY (PERMISSION_DENIED)** | Denied: Location B does not belong to PIC A | ✅ VERIFIED |
| **PIC A (Assigned Loc A)** | Elevate own role to Admin (`users/{picA}`) | No role editing UI | **DENY (PERMISSION_DENIED)** | `allow write: if isAdmin()` in `users/{userId}` | ✅ VERIFIED |
| **PIC B (Assigned Loc B)** | Update Location A | Blocked by UI | **DENY (PERMISSION_DENIED)** | Denied: `resource.data.picUid != request.auth.uid` | ✅ VERIFIED |
| **PIC B (Assigned Loc B)** | Update Location B | Edit form submits successfully | **ALLOW** | `resource.data.picUid == request.auth.uid` passes | ✅ VERIFIED |
| **Admin Kelurahan** | Review pending locations queue | Displays all pending submissions | **ALLOW** | `isPIC()` passes (Admin evaluates to true) | ✅ VERIFIED |
| **Admin Kelurahan** | Approve & publish Location A | Tap `[ Setujui & Publikasikan ]` | **ALLOW** | `isAdmin()` passes | ✅ VERIFIED |
| **Admin Kelurahan** | Reject Location A with revision notes | Tap `[ Tolak dengan Catatan ]` | **ALLOW** | `isAdmin()` passes | ✅ VERIFIED |
| **Admin Kelurahan** | Reassign Location A from PIC A to PIC B | Admin selector submits update | **ALLOW** | `isAdmin()` passes | ✅ VERIFIED |
| **Admin Kelurahan** | Create/Edit `MasterPlant` botanical entry | Master plant form submits | **ALLOW** | `allow write: if isAdmin()` | ✅ VERIFIED |
| **Admin Kelurahan** | Delete Location | Admin deletion dialog submits | **ALLOW** | `allow delete: if isAdmin()` | ✅ VERIFIED |
| **Any User** | Modify or delete existing record in `/audit_logs` | No action provided | **DENY (PERMISSION_DENIED)** | `allow update, delete: if false;` | ✅ VERIFIED |
| **Unauthenticated Attacker** | Direct REST API write to `/locations` | N/A | **DENY (UNAUTHENTICATED)** | `request.auth != null` fails | ✅ VERIFIED |

---

## 3. Role Representation & Authentication Claim Architecture

Roles are cryptographically signed in Firebase Authentication **Custom User Claims**:

```json
{
  "role": "admin" | "pic" | "public",
  "assignedLocations": ["LOC_01", "LOC_03"]
}
```

1. **Resolution in Android Client (`FirebaseAuthRepository.kt`):**
   - Extracts `roleStr = tokenResult.claims["role"] as? String`.
   - Maps to domain model: `UserRole.ADMIN`, `UserRole.PIC`, or `UserRole.PUBLIC`.
   - Extracts `assigned = tokenResult.claims["assignedLocations"] as? List<String>`.
2. **Resolution in Firestore Rules (`web/firestore.rules`):**
   - Evaluates `request.auth.token.role == 'admin'`.
   - Evaluates `request.auth.token.role == 'pic'`.
   - Verifies location ownership against document field: `resource.data.picUid == request.auth.uid`.

---

## 4. Verification Conclusion

Backend rules in `web/firestore.rules` provide airtight role isolation. Privilege escalation, unauthorized publishing, and cross-PIC tampering are completely blocked at the database engine level.
