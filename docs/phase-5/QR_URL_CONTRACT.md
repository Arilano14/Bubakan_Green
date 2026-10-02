# QR & Canonical URL Contract — Bubakan Green

**Product**: Bubakan Green — Sistem Informasi Urban Farming & Taman Toga Kelurahan Bubakan  
**Status**: APPROVED CONTRACT (Phase 5)  

---

## 1. Zero Fabricated / Zero Inlined Payload Principle

Physical QR codes deployed across Kelurahan Bubakan garden plots MUST encode ONLY the canonical HTTPS URL string.

### 1.1 Prohibited Elements in QR
- ❌ NO full botanical descriptions or characteristics.
- ❌ NO base64 encoded images or image URLs.
- ❌ NO raw Firestore document dumps.
- ❌ NO authorization tokens, UIDs, or private credentials.
- ❌ NO localized names, tags, or mutable attributes in URL segments.

---

## 2. Canonical URL Specification

| Resource Type | Canonical URL Pattern | Example | Backend Target |
|---|---|---|---|
| **Master Plant** | `https://bubakangreen.web.app/plant/<stable-id>` | `https://bubakangreen.web.app/plant/pl-a84f21c9` | `/master_plants/{stable-id}` |
| **Garden Location** | `https://bubakangreen.web.app/location/<stable-id>` | `https://bubakangreen.web.app/location/loc-91af7b23` | `/locations/{stable-id}` |

---

## 3. Stable Identifier Contract

1. **Prefix Convention**:
   - `pl-` for Master Botanical Plant species.
   - `loc-` for Physical Garden Locations.
2. **Opaque & Immutable**:
   - ID is an alphanumeric string generated once at creation.
   - **MUST NOT** be modified when a plant's name, Latin name, Mandarin name, or description changes.
   - Re-rendering or re-printing a QR code uses the EXACT SAME stable ID.
3. **URL Safety**:
   - Alphanumeric, lowercase, hyphen `[a-z0-9_-]`. No spaces, percent-encodings, or query strings.

---

## 4. Production QR Eligibility Gate

Before a QR code can be generated for physical signage, the target resource must satisfy:
- [x] Document exists in Firestore collection (`/master_plants` or `/locations`).
- [x] `status == 'ACTIVE'` or `status == 'PUBLISHED'`.
- [x] `isPublished == true`.
- [x] Compliant, non-empty stable ID.

Resources with status `DRAFT`, `PENDING_APPROVAL`, `TEST_ONLY`, or `ARCHIVED` are **BLOCKED** from public QR signage generation.
