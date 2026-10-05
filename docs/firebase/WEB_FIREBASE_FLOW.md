# Web Firebase Data Flow (Website Fallback)

**Project:** Bubakan Green  
**Firebase Project:** `bubakan-green`  
**Hosting Domain:** `https://bubakangreen.web.app`  
**Status:** IMPLEMENTED / TESTED  

---

## 1. Architectural Role of Website Fallback

The Web client serves as the universal, zero-friction fallback for Bubakan Green QR codes placed on physical garden signs in Kelurahan Bubakan:
- **App Installed:** Android App Links / Intent Filters intercept `https://bubakangreen.web.app/plant/<id>` and open natively.
- **App Not Installed / Desktop / iOS:** Web client loads instantaneously in any browser (< 2KB vanilla script, zero framework overhead, no bundler required).

```
               Physical QR Code on Garden Sign
                              │
            https://bubakangreen.web.app/plant/<id>
                              │
             ┌────────────────┴────────────────┐
             │                                 │
      [App Installed]                 [App Not Installed]
             │                                 │
             ▼                                 ▼
    Android Native App              Mobile / Desktop Browser
  (PlantDetailScreen.kt)           (web/public/plant.html)
             │                                 │
             │       Same Cloud Firestore       │
             └───────────────┬─────────────────┘
                             │
                             ▼
             Cloud Firestore (bubakan-green)
               /master_plants/{plantId}
```

---

## 2. Route Endpoints & Firestore Mapping

The web fallback uses canonical Firestore REST endpoints matching the Android app collections:

| Web Route | Local File | Firestore Endpoint | Query Target |
|---|---|---|---|
| `/` | `web/public/index.html` | — | Landing page & APK download |
| `/plant/<plantId>` | `web/public/plant.html` | `https://firestore.googleapis.com/v1/projects/bubakan-green/databases/(default)/documents/master_plants/{plantId}` | Single `MasterPlant` |
| `/location/<locationId>` | `web/public/location.html` | `https://firestore.googleapis.com/v1/projects/bubakan-green/databases/(default)/documents/locations/{locationId}` | Single `Location` |

---

## 3. Data Retrieval Mechanism

Per Section 23 of architecture guidelines, client uses lightweight native `fetch()` against Firestore REST API:

```javascript
var projectId = 'bubakan-green';
var firestoreUrl = 'https://firestore.googleapis.com/v1/projects/' + projectId +
  '/databases/(default)/documents/master_plants/' + encodeURIComponent(plantId);

fetch(firestoreUrl)
  .then(function(response) {
    if (response.status === 404) throw new Error('NOT_FOUND');
    if (!response.ok) throw new Error('FETCH_ERROR');
    return response.json();
  })
  .then(function(doc) {
    renderPlant(doc.fields || {});
  });
```

### Advantages:
1. **Zero Client Secrets:** Uses public REST endpoint governed by `firestore.rules`.
2. **Instant Rendering:** First contentful paint in milliseconds without loading heavy JavaScript bundles.
3. **Identical Source of Truth:** Edits made by Admins in Firestore immediately reflect on the website on next refresh without rebuilding or redeploying code.

---

## 4. UI States

Both `plant.html` and `location.html` implement clean, graceful states:
- **Loading State:** CSS shimmer placeholders.
- **Success State:** Displays plant photos, Indonesian name, Latin botanical name, Mandarin pronunciation card (Hanzi + Pinyin), and herbal health benefits.
- **Unpublished State:** Rendered if `isPublished == false` ("Tanaman Belum Diterbitkan").
- **Not Found State:** Rendered if document does not exist (`404`) ("Tanaman Tidak Ditemukan").
- **Offline / Fallback State:** Formats ID slug into a readable label if network drops.

---

## 5. Hosting Configuration (`web/firebase.json`)

Rewrites in `web/firebase.json` map clean URLs directly to the respective HTML viewers:
```json
{
  "hosting": {
    "public": "public",
    "ignore": ["firebase.json", "**/.*", "**/node_modules/**"],
    "rewrites": [
      {
        "source": "/plant/**",
        "destination": "/plant.html"
      },
      {
        "source": "/location/**",
        "destination": "/location.html"
      }
    ]
  }
}
```
This ensures URL paths like `https://bubakangreen.web.app/plant/sereh` are properly routed to `plant.html` where JavaScript parses `window.location.pathname`.
