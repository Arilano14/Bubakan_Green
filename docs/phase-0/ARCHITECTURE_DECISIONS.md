# ARCHITECTURE DECISIONS — BUBAKAN GREEN

**Date:** 2026-09-23  
**Version:** 1.0

---

## ADR-001: Android with Kotlin + Jetpack Compose

**DECISION:** Build the mobile app using Kotlin with Jetpack Compose (declarative UI).

**WHY:**
- Google's recommended modern Android toolkit
- Declarative UI reduces boilerplate compared to XML Views
- Strong community support and documentation
- Single language (Kotlin) for all app logic and UI
- Better tooling support in Android Studio

**ALTERNATIVES CONSIDERED:**

| Alternative | Why Not Chosen |
|-------------|----------------|
| Kotlin + XML Views | Older approach, more boilerplate, Google is deprecating XML-first development |
| Flutter (Dart) | Cross-platform is unnecessary (Android-only product), adds Dart language complexity, different ecosystem from Android native |
| React Native (JavaScript) | Cross-platform unnecessary, JavaScript ecosystem introduces npm complexity, bridge performance concerns |
| Java | Kotlin is the official language since 2019, Java is legacy for new Android projects |

**COST IMPACT:** Rp0 — all tools are free.  
**MAINTENANCE IMPACT:** Kotlin + Compose is the standard going forward. Future Android developers will know it.  
**RISK:** PIC/Kelurahan cannot modify app code regardless of technology choice — this is acceptable since data changes don't require code changes.

---

## ADR-002: Firebase as Backend

**DECISION:** Use Firebase (Firestore + Authentication + Storage + Hosting) as the backend.

**WHY:**
- Fully managed — no server to maintain
- Generous free tier covers expected usage
- Built-in authentication (email/password)
- Real-time data sync via Firestore listeners
- Built-in offline cache for Firestore
- Firebase Hosting supports App Links configuration
- Firebase Storage for photos and audio
- Single ecosystem reduces integration complexity

**ALTERNATIVES CONSIDERED:**

| Alternative | Why Not Chosen |
|-------------|----------------|
| Supabase | Good alternative but less mature Android SDK, PostgreSQL requires more schema management, self-hosting for truly free usage adds maintenance burden |
| Custom server (Node.js/Django + PostgreSQL) | Requires server hosting (paid), ongoing server maintenance, SSL certificate management, more complex deployment |
| Static JSON files on GitHub Pages | Zero cost, but no authentication, no real-time updates, PIC cannot update data via app — requires JSON file editing |
| AppWrite | Self-hosted is complex, cloud version has limited free tier |

**COST IMPACT:** Rp0 within free tier. Free tier limits:
- Firestore: 1GB storage, 50K reads/day, 20K writes/day
- Auth: 10K authentications/month
- Storage: 5GB, 1GB/day download
- Hosting: 10GB storage, 10GB/month transfer

**MAINTENANCE IMPACT:** LOW — Firebase is managed by Google. No server patching, no database backups (Firestore handles this).  
**RISK:** Vendor lock-in (migrating away from Firebase requires significant effort). Acceptable for this project's scope and lifetime.

---

## ADR-003: Cloud Firestore (NoSQL) as Database

**DECISION:** Use Cloud Firestore as the primary database.

**WHY:**
- Part of Firebase ecosystem (single provider)
- NoSQL document model fits the Location → Plants hierarchy
- Built-in offline persistence (SDK caches data locally)
- Real-time listeners for live data updates
- Security rules enforced server-side
- No SQL schema migrations needed

**ALTERNATIVES CONSIDERED:**

| Alternative | Why Not Chosen |
|-------------|----------------|
| Firebase Realtime Database | Older, less powerful query capabilities, flat data structure less intuitive than Firestore's collections |
| SQLite (local only) | No cloud sync, no multi-device access, PIC data changes are local-only |
| PostgreSQL (via Supabase/custom) | Requires server hosting, schema migrations, more operational overhead |

**COST IMPACT:** Rp0 within free tier.  
**MAINTENANCE IMPACT:** LOW — no schema migrations, no database server management.  
**RISK:** Complex queries are limited (no JOINs). Acceptable — our data model is simple (locations + plants, no complex relationships).

---

## ADR-004: Map Provider — DECISION PENDING

**STATUS:** Analysis complete. Awaiting Product Owner decision.

### Conceptual Separation

Map implementation involves three distinct components:

| Component | What It Is | Example |
|-----------|-----------|---------|
| **MAP SDK** | Library used to render maps in the Android app | Google Maps SDK, osmdroid, Mapbox SDK |
| **MAP DATA** | Geographic data source (roads, labels, POIs) | Google Maps data, OpenStreetMap data, Mapbox data |
| **MAP TILE PROVIDER** | Server providing rendered map tile images | Google tile servers, OSM tile servers, Mapbox tile servers |

These are not the same thing. A MAP SDK can sometimes use different MAP TILE PROVIDERs.

### Option Analysis

#### Option A: Google Maps SDK for Android

| Criterion | Assessment |
|-----------|-----------|
| **Cost** | Free tier: $200/month credit (~28,000 map loads). Beyond that: paid. For this project's expected volume (<1K loads/month), effectively Rp0 |
| **API key** | Required (Google Cloud Console) |
| **Billing requirement** | **YES — Google Cloud billing account with credit card on file required**, even for free tier |
| **Usage limits** | 28,000 Dynamic Maps loads/month free, then $7/1000 loads |
| **Offline behavior** | Limited offline caching (Google's discretion). No guaranteed offline tile access |
| **Android/Compose integration** | **Excellent** — official `maps-compose` library by Google. First-class Compose support. Well-documented |
| **Attribution/licensing** | Must display Google logo. Proprietary license. Cannot cache tiles for offline beyond SDK-managed cache |
| **Maintenance** | LOW — Google maintains SDK, documentation is extensive, StackOverflow support is strong |

#### Option B: osmdroid + OpenStreetMap

| Criterion | Assessment |
|-----------|-----------|
| **Cost** | Rp0 — completely free, no billing account |
| **API key** | Not required for default OSM tile servers |
| **Billing requirement** | **NO** |
| **Usage limits** | OSM tile usage policy: max 2 requests/second, must set User-Agent. Heavy usage should use own tile server or commercial provider |
| **Offline behavior** | **Good** — osmdroid supports offline tile caching and pre-downloaded tile archives |
| **Android/Compose integration** | **No official Compose library**. Requires `AndroidView` wrapper in Compose. More boilerplate, less idiomatic |
| **Attribution/licensing** | Must attribute OpenStreetMap contributors (© OpenStreetMap contributors). Open Database License (ODbL). Tiles from tile.openstreetmap.org subject to tile usage policy |
| **Maintenance** | MEDIUM — active open-source project but smaller community than Google Maps. Compose integration is DIY |

#### Option C: Mapbox SDK

| Criterion | Assessment |
|-----------|-----------|
| **Cost** | Free tier: 25,000 map loads/month. Beyond: paid |
| **API key** | Required (Mapbox access token) |
| **Billing requirement** | Account required, credit card for beyond free tier |
| **Usage limits** | 25,000 loads/month free |
| **Offline behavior** | Good offline support (Mapbox offline maps) |
| **Android/Compose integration** | Has Compose extensions, but less mature than Google Maps Compose |
| **Attribution/licensing** | Must display Mapbox logo. Proprietary license |
| **Maintenance** | MEDIUM — well-maintained but smaller ecosystem than Google Maps |

### Comparison Summary

| Criterion | Google Maps | osmdroid/OSM | Mapbox |
|-----------|------------|-------------|--------|
| Rp0 guaranteed | ⚠️ Needs billing account | ✅ Yes | ⚠️ Needs account |
| API key | Required | Not needed | Required |
| Compose integration | ✅ Excellent | ⚠️ AndroidView wrapper | ⚠️ Moderate |
| Offline tiles | Limited | ✅ Good | ✅ Good |
| UX quality | ✅ Best | ⚠️ Functional | ✅ Good |
| Maintenance burden | ✅ Low | ⚠️ Medium | ⚠️ Medium |
| Community/docs | ✅ Extensive | ⚠️ Adequate | ⚠️ Good |

### Recommendation Framework

The decision depends on which constraint matters most:

- **If Rp0 with zero billing accounts is absolute** → osmdroid/OSM (Option B)
- **If best UX and developer productivity matter most** → Google Maps (Option A), accepting billing account
- **If offline maps are critical** → osmdroid/OSM (Option B) or Mapbox (Option C)

> [!IMPORTANT]  
> **This decision is deferred to the Product Owner.** The analysis is provided; the tradeoff is clear. Both Option A and Option B are viable for this project's scale. The key question is: **Is a Google Cloud billing account (credit card on file, no expected charges) acceptable?**

**COST IMPACT:** Option A: Rp0 expected (within free tier, but billing account needed). Option B: Rp0 guaranteed.  
**MAINTENANCE IMPACT:** Option A: LOW. Option B: MEDIUM (Compose wrapper).  
**RISK:** Option A: billing account exposure. Option B: less polished UX, more integration work.

---

## ADR-005: QR as URL Reference (Not Separate Entity)

**DECISION:** QR codes encode an HTTPS URL containing a stable plant/location ID. QR is not a separate database entity.

**WHY:**
- QR content is deterministic: `https://{domain}/plant/{plantId}`
- No need to store QR data in database — it's derived from existing IDs
- Changing plant data does not change the QR URL → no reprint needed
- Simpler architecture — no QR table, no QR lifecycle management

**ALTERNATIVES CONSIDERED:**

| Alternative | Why Not Chosen |
|-------------|----------------|
| Store QR data in a separate collection | Unnecessary complexity. QR is a function of (domain + entity ID), not independent data |
| Encode full plant data in QR | QR would need reprinting whenever data changes. QR size increases. Violates requirement |
| Short URL / redirect service | Adds a dependency. Direct URL with stable ID is sufficient |

**COST IMPACT:** Rp0.  
**MAINTENANCE IMPACT:** NONE — no QR-specific maintenance.  
**RISK:** Domain change requires reprinting all QR codes. Mitigated by using a stable domain (Firebase Hosting subdomain or custom domain).

---

## ADR-006: App Links for QR → App Navigation

**DECISION:** Use Android App Links (verified HTTPS intent filters) for deep linking from QR codes.

**WHY:**
- Standard Android mechanism for URL → app navigation
- No user disambiguation dialog (unlike regular deep links)
- Falls back to web browser if app not installed
- HTTPS URLs are trusted and verifiable

**ALTERNATIVES CONSIDERED:**

| Alternative | Why Not Chosen |
|-------------|----------------|
| Custom URI scheme (`bubakangreen://`) | Not verifiable, shows disambiguation dialog, doesn't fall back to web |
| Firebase Dynamic Links | Deprecated by Google (sunset 2025). Not a viable option |
| Regular deep links (non-verified) | Shows "Open with..." dialog, worse UX |

**COST IMPACT:** Rp0 — requires hosting `assetlinks.json` on Firebase Hosting (free).  
**MAINTENANCE IMPACT:** LOW — `assetlinks.json` needs the APK signing certificate fingerprint. Must be updated if signing key changes.  
**RISK:** App Links verification can fail if `assetlinks.json` is misconfigured or domain doesn't match. Testing is essential.

---

## ADR-007: Static HTML Web Fallback (Not Web App)

**DECISION:** The web fallback for non-app users is a static HTML/CSS/JS page, not a web application.

**WHY:**
- Minimal complexity — simple HTML page with plant/location info
- Loads instantly (no framework, no bundle)
- Free hosting on Firebase Hosting
- Purpose is limited: show basic plant info + APK download link
- No authentication needed on web (read-only)

**ALTERNATIVES CONSIDERED:**

| Alternative | Why Not Chosen |
|-------------|----------------|
| Full web app (React/Next.js) | Massive overkill for a read-only fallback page. Adds build tooling, framework complexity, maintenance burden |
| No web fallback | Users without app installed get a dead link from QR. Unacceptable UX |
| Progressive Web App | Overkill — PWA capabilities (service workers, push) not needed for a read-only fallback |

**COST IMPACT:** Rp0.  
**MAINTENANCE IMPACT:** LOW — static HTML rarely needs updates.  
**RISK:** Web page must read from Firestore client SDK (JavaScript) to display dynamic plant data. Firestore JS SDK adds some payload size. Alternative: pre-render pages during QR generation (more complex build process).

> [!NOTE]  
> **Implementation approach for web fallback:** The simplest approach is a single HTML page that reads the plant/location ID from the URL, fetches data from Firestore using the JavaScript SDK, and renders it. This avoids pre-rendering complexity while keeping the page lightweight.

---

## ADR-008: Firebase Auth with Custom Claims for Authorization

**DECISION:** Use Firebase Authentication (email/password) with Custom Claims for role-based access control.

**WHY:**
- Firebase Auth is integrated with Firestore security rules
- Custom Claims (`role`, `assignedLocations`) are checked directly in security rules
- No separate permissions table needed
- Email/password is simple for PICs to understand

**ALTERNATIVES CONSIDERED:**

| Alternative | Why Not Chosen |
|-------------|----------------|
| Separate Firestore `permissions` collection | More queries, more complex security rules, sync issues between auth and permissions |
| Google Sign-In only | PICs may not have Google accounts; email/password is more universal |
| Phone number auth | Requires SMS billing (may not be free), more complex |
| No auth (public write) | Security disaster — anyone could modify data |

**COST IMPACT:** Rp0 within free tier.  
**MAINTENANCE IMPACT:** MEDIUM — Custom Claims require a Firebase Admin SDK script or Cloud Function to set. Initial setup is manual.  
**RISK:** Custom Claims are cached in the auth token (up to 1 hour). Claim changes (e.g., assigning PIC to new location) may not take effect immediately. Mitigated by requiring re-login after claim changes.

---

## ADR-009: APK Distribution via Google Drive (Not Play Store)

**DECISION:** Distribute the APK via Google Drive shared link, not Google Play Store.

**WHY:**
- Google Play requires $25 registration fee
- Google Play review process adds delay
- For a kelurahan-specific app with small user base, sideloading is acceptable
- Web fallback page serves as the "app store" page

**ALTERNATIVES CONSIDERED:**

| Alternative | Why Not Chosen |
|-------------|----------------|
| Google Play Store | $25 fee violates Rp0 constraint. Review delays. Ongoing compliance requirements |
| GitHub Releases | Technical — target users may not understand GitHub |
| Direct download from Firebase Hosting | Feasible alternative, but Google Drive is simpler to manage for non-developers |
| F-Droid | Open-source app store, but complex submission process and niche audience |

**COST IMPACT:** Rp0.  
**MAINTENANCE IMPACT:** LOW — upload new APK to Google Drive, update link.  
**RISK:** Users must enable "Install from unknown sources." Some users may find this confusing. Mitigated by including clear installation instructions on the web fallback page.

---

## ADR-010: Mandarin Audio as Pre-Recorded Static Files

**DECISION:** Mandarin pronunciation audio will be pre-recorded files (TTS or human), stored in Firebase Storage, not generated at runtime.

**WHY:**
- No runtime API dependency (no Google TTS API cost, no network dependency for audio)
- Audio files are small (~50KB per word)
- Files are cached by the app after first download
- One-time effort to generate/record

**ALTERNATIVES CONSIDERED:**

| Alternative | Why Not Chosen |
|-------------|----------------|
| Google Cloud Text-to-Speech API (runtime) | Paid service after free tier, adds network dependency for audio playback, latency |
| No audio | Feature is explicitly requested (SHOULD HAVE) |
| Embedded audio in APK | Violates data ≠ code principle. Adding new plants would require APK rebuild |

**COST IMPACT:** Rp0 — files stored in Firebase Storage (within 5GB free tier).  
**MAINTENANCE IMPACT:** LOW — audio files rarely change.  
**RISK:** Audio quality depends on TTS tool quality. Human recording is higher quality but higher effort.

---

## ADR-011: MVVM Architecture Pattern

**DECISION:** Use MVVM (Model-View-ViewModel) with Repository pattern for Android app architecture.

**WHY:**
- Official Google-recommended architecture for Android
- Clean separation of UI (Compose), state management (ViewModel), and data access (Repository)
- ViewModels survive configuration changes
- Testable — ViewModels can be unit tested without Android framework
- Repository pattern abstracts Firebase SDK specifics

**ALTERNATIVES CONSIDERED:**

| Alternative | Why Not Chosen |
|-------------|----------------|
| MVI (Model-View-Intent) | More complex state management, overkill for this app's complexity |
| MVP (Model-View-Presenter) | Older pattern, less recommended with Compose |
| No architecture | Leads to untestable, unmaintainable code |

**COST IMPACT:** Rp0.  
**MAINTENANCE IMPACT:** LOW — standard pattern, widely understood.  
**RISK:** None significant.

---

## ADR-012: Hilt for Dependency Injection

**DECISION:** Use Hilt for dependency injection.

**WHY:**
- Google's recommended DI for Android
- Integrates with ViewModel, Navigation, Compose
- Reduces boilerplate compared to manual DI
- Compile-time verification of dependency graph

**ALTERNATIVES CONSIDERED:**

| Alternative | Why Not Chosen |
|-------------|----------------|
| Koin | Runtime DI — errors caught at runtime instead of compile time. Simpler but less safe |
| Manual DI | More boilerplate, error-prone as app grows |
| Dagger (without Hilt) | More complex setup. Hilt simplifies Dagger for Android |

**COST IMPACT:** Rp0.  
**MAINTENANCE IMPACT:** LOW — standard in Android ecosystem.  
**RISK:** None significant. Hilt is mature and stable.

---

## ADR-013: Minimum API Level 26 (Android 8.0)

**DECISION:** Target minimum API level 26 (Android 8.0, Oreo, released August 2017).

**WHY:**
- Android 8.0+ covers ~95%+ of active devices (as of 2026)
- Enables modern APIs without compatibility workarounds
- 9-year-old cutoff is generous for a new app

**ALTERNATIVES CONSIDERED:**

| Alternative | Why Not Chosen |
|-------------|----------------|
| API 21 (Android 5.0) | Requires more compatibility code, covers marginally more devices |
| API 28 (Android 9.0) | Excludes some older devices. Not necessary |
| API 33 (Android 13) | Excludes too many devices |

**COST IMPACT:** Rp0.  
**MAINTENANCE IMPACT:** LOW.  
**RISK:** LOW — [FIELD-VALIDATION REQUIRED] Survey target users' actual devices to confirm API 26 is sufficient.
