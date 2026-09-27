# BUBAKAN GREEN — QR INTEGRATION MATRIX
**Product:** BUBAKAN GREEN  
**Sub-title:** Sistem Informasi Urban Farming & Taman Toga Kelurahan Bubakan  
**Phase:** Phase 5 — QR & Field Integration  
**Governance:** Zero dummy data for production QR. Production QR requires `FIELD-VERIFIED` status. No QR code may be claimed as verified without physical hardware testing.  
**Date:** 2026-09-27  
**Revision:** Final Pre-Execution Revision  

---

## 1. Architectural URL & Stable ID Principles

### A. The Canonical HTTPS Payload Contract
- **Payload Guarantee:** Physical QR codes contain strictly the canonical HTTPS destination URL (e.g. `https://bubakangreen.web.app/plant/<stable-id>`).
- **Zero Raw Data Embedding:** Complete botanical records, descriptions, and images are **never** embedded directly in the QR matrix.
- **Validity Contract:**
  > *"QR remains valid as long as the canonical URL and underlying stable resource remain active and published in the system."*
- **Database Update Independence:**
  $$\text{Botanical Description / Photo / Benefit Update} \implies \text{\textbf{ZERO}} \text{ Physical Sticker Reprint}$$
  Updates made in Firestore reflect immediately on next scan.
- **Inactive / Deleted Resource Handling:**
  If a botanical species or garden plot is unpublished, archived, or deleted:
  - Native App displays `StateEmptyView` ("Tanaman Tidak Tersedia" or "Lokasi Belum Terbit").
  - Web fallback displays a polite civic notice that the record is inactive with a link to browse active public gardens.

---

### B. Opaque Stable Identity Strategy

| Strategy Dimension | Botanical Species (`MasterPlant`) | Garden Plot (`Location`) |
|---|---|---|
| **Identity Creator** | System-generated upon Admin creation | System-generated upon initial creation |
| **Creation Trigger** | Admin publishes botanical entry | PIC registers plot in `PENDING_APPROVAL` |
| **Mutability** | **IMMUTABLE** (Never changes once created) | **IMMUTABLE** (Never changes once created) |
| **Display Name Change** | Stable ID remains unchanged; only `nameId` is updated in DB | Stable ID remains unchanged; only `name` is updated in DB |
| **Plot Relocation** | N/A (Master species is location-agnostic) | GPS coordinates updated in DB; Stable ID remains unchanged |
| **Plant Bed Move** | `LocationPlant` junction updated; MasterPlant ID unchanged | MasterPlant ID remains unchanged; zero QR reprint |
| **Unpublished State** | Displays "Tanaman Belum Diterbitkan" | Displays "Lokasi Sedang Ditinjau / Tidak Aktif" |
| **Deleted State** | Displays "Tanaman Tidak Ditemukan" | Displays "Lokasi Tidak Ditemukan" |
| **Naming Pattern** | Opaque alphanumeric slug: `pl-[a-z0-9]{6,12}` (e.g. `pl-jhem-01`) | Opaque alphanumeric slug: `loc-[a-z0-9]{6,12}` (e.g. `loc-tg03-01`) |
| **Dependency Rule** | **MUST NOT** depend on botanical name, Latin name, or RW text | **MUST NOT** depend on plot name, RW number, or temporary order |

---

## 2. Real-Data Verification Taxonomy

1. **`FIELD-VERIFIED`**: Physical specimen inspected on-site in Bubakan by agricultural team and confirmed against digital record. **Mandatory prerequisite for production QR printing.**
2. **`DOCUMENT-VERIFIED`**: Verified against Kelurahan botanical literature; pending physical bed confirmation.
3. **`NEEDS-FIELD-VALIDATION`**: Garden plot or planting record submitted by PIC; awaiting on-site GPS and specimen verification.
4. **`TEST_ONLY`**: Development fixtures and diagnostic records; permanently barred from production printing.
5. **`UNKNOWN`**: Field status not yet established.

---

## 3. QR Integration Matrix

| Resource Type | Stable ID | Verification Category | Canonical QR URL | QR Spec Status | HTTPS Endpoint | App Link Path | App Routing | Web Fallback | Firestore State | Security Rule | Physical Hardware Test | Production Status |
|---|---|---|---|---|---|---|---|---|---|---|---|---|
| **Plant (Master)** | `pl-jhem-01` | `DOCUMENT-VERIFIED` | `https://bubakangreen.web.app/plant/pl-jhem-01` | READY | READY | `/plant/pl-jhem-01` | READY | READY | PUBLISHED | PASS (Public Read) | NOT TESTED | **PLANNED (AWAITING FIELD VERIFICATION)** |
| **Plant (Master)** | `pl-kmkc-02` | `DOCUMENT-VERIFIED` | `https://bubakangreen.web.app/plant/pl-kmkc-02` | READY | READY | `/plant/pl-kmkc-02` | READY | READY | PUBLISHED | PASS (Public Read) | NOT TESTED | **PLANNED (AWAITING FIELD VERIFICATION)** |
| **Plant (Master)** | `pl-tmlw-03` | `DOCUMENT-VERIFIED` | `https://bubakangreen.web.app/plant/pl-tmlw-03` | READY | READY | `/plant/pl-tmlw-03` | READY | READY | PUBLISHED | PASS (Public Read) | NOT TESTED | **PLANNED (AWAITING FIELD VERIFICATION)** |
| **Plant (Master)** | `pl-ldby-04` | `DOCUMENT-VERIFIED` | `https://bubakangreen.web.app/plant/pl-ldby-04` | READY | READY | `/plant/pl-ldby-04` | READY | READY | PUBLISHED | PASS (Public Read) | NOT TESTED | **PLANNED (AWAITING FIELD VERIFICATION)** |
| **Plant (Master)** | `pl-srwg-05` | `DOCUMENT-VERIFIED` | `https://bubakangreen.web.app/plant/pl-srwg-05` | READY | READY | `/plant/pl-srwg-05` | READY | READY | PUBLISHED | PASS (Public Read) | NOT TESTED | **PLANNED (AWAITING FIELD VERIFICATION)** |
| **Plant (Master)** | `pl-pggn-06` | `DOCUMENT-VERIFIED` | `https://bubakangreen.web.app/plant/pl-pggn-06` | READY | READY | `/plant/pl-pggn-06` | READY | READY | PUBLISHED | PASS (Public Read) | NOT TESTED | **PLANNED (AWAITING FIELD VERIFICATION)** |
| **Plant (Master)** | `pl-knyt-07` | `DOCUMENT-VERIFIED` | `https://bubakangreen.web.app/plant/pl-knyt-07` | READY | READY | `/plant/pl-knyt-07` | READY | READY | PUBLISHED | PASS (Public Read) | NOT TESTED | **PLANNED (AWAITING FIELD VERIFICATION)** |
| **Plant (Master)** | `pl-smbl-08` | `DOCUMENT-VERIFIED` | `https://bubakangreen.web.app/plant/pl-smbl-08` | READY | READY | `/plant/pl-smbl-08` | READY | READY | PUBLISHED | PASS (Public Read) | NOT TESTED | **PLANNED (AWAITING FIELD VERIFICATION)** |
| **Location (Plot)**| `loc-tg03-01` | `NEEDS-FIELD-VALIDATION` | `https://bubakangreen.web.app/location/loc-tg03-01` | READY | READY | `/location/loc-tg03-01` | READY | READY | PENDING_APPROVAL | PASS (Restricted) | NOT TESTED | **PLANNED (AWAITING FIELD APPROVAL)** |
| **Location (Plot)**| `loc-uf01-02` | `NEEDS-FIELD-VALIDATION` | `https://bubakangreen.web.app/location/loc-uf01-02` | READY | READY | `/location/loc-uf01-02` | READY | READY | PENDING_APPROVAL | PASS (Restricted) | NOT TESTED | **PLANNED (AWAITING FIELD APPROVAL)** |
| **Test Diagnostic**| `test-fixture-01` | `TEST_ONLY` | `https://bubakangreen.web.app/plant/test-fixture-01` | TEST_ONLY | READY | `/plant/test-fixture-01` | READY | READY | DRAFT | PASS | SIMULATED ONLY | **TEST_ONLY (NEVER PRINT FOR FIELD)** |

---

## 4. Production QR Generation Gate

Before any QR code is sent to print:
- [ ] Real physical specimen / garden plot identified on ground in Kelurahan Bubakan.
- [ ] Verification category marked `FIELD-VERIFIED`.
- [ ] Immutable Stable ID assigned.
- [ ] Record status is `PUBLISHED` in Firestore.
- [ ] Canonical URL verified returning HTTP 200.
- [ ] Destination screen verified via local simulation.
