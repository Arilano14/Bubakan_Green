#!/usr/bin/env python3
"""
scripts/update_plant_photos_firestore.py
Updates photo fields for Kemangi, Pegagan, Sirih, Cabai, and Lidah Buaya in Firestore.
Uses Firestore REST API PATCH with field masks to strictly update only photo fields.
"""
import urllib.request
import json
import sys
import time

try:
    sys.stdout.reconfigure(encoding='utf-8')
except Exception:
    pass

with open('app/google-services.json') as f:
    gs = json.load(f)
api_key = gs['client'][0]['api_key'][0]['current_key']
project_id = gs['project_info']['project_id']

# 1. Authenticate as Admin
auth_url = f'https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key={api_key}'
payload = json.dumps({
    'email': 'admin@bubakangreen.id',
    'password': 'BubakanAdmin2026!',
    'returnSecureToken': True
}).encode('utf-8')

auth_req = urllib.request.Request(auth_url, data=payload, headers={'Content-Type': 'application/json'})
with urllib.request.urlopen(auth_req) as resp:
    token_data = json.loads(resp.read().decode('utf-8'))
id_token = token_data['idToken']

print("==========================================================================")
print("FIRESTORE DATABASE PHOTO UPDATE SCRIPT")
print("==========================================================================")

TARGET_UPDATES = {
    'kemangi': {
        'nameId': 'Kemangi',
        'fields': {
            'primaryPhotoUrl': {'stringValue': 'https://upload.wikimedia.org/wikipedia/commons/9/97/Kemangi.jpg'},
            'defaultPhotoUrl': {'stringValue': 'https://upload.wikimedia.org/wikipedia/commons/9/97/Kemangi.jpg'},
            'imageSource': {'stringValue': 'https://commons.wikimedia.org/wiki/File:Kemangi.jpg'},
            'imageAuthor': {'stringValue': 'Kembangraps'},
            'imageLicense': {'stringValue': 'CC BY-SA 3.0'},
            'imageSourceType': {'stringValue': 'REMOTE_URL'}
        }
    },
    'pegagan': {
        'nameId': 'Pegagan',
        'fields': {
            'primaryPhotoUrl': {'stringValue': 'https://upload.wikimedia.org/wikipedia/commons/3/3e/Centella_Asiatica.jpg'},
            'defaultPhotoUrl': {'stringValue': 'https://upload.wikimedia.org/wikipedia/commons/3/3e/Centella_Asiatica.jpg'},
            'imageSource': {'stringValue': 'https://commons.wikimedia.org/wiki/File:Centella_Asiatica.jpg'},
            'imageAuthor': {'stringValue': 'Miekks'},
            'imageLicense': {'stringValue': 'CC BY-SA 4.0'},
            'imageSourceType': {'stringValue': 'REMOTE_URL'}
        }
    },
    'sirih': {
        'nameId': 'Sirih',
        'fields': {
            'primaryPhotoUrl': {'stringValue': 'https://upload.wikimedia.org/wikipedia/commons/d/d4/Betel_leaf_plant_%28Piper_betle%29.jpg'},
            'defaultPhotoUrl': {'stringValue': 'https://upload.wikimedia.org/wikipedia/commons/d/d4/Betel_leaf_plant_%28Piper_betle%29.jpg'},
            'imageSource': {'stringValue': 'https://commons.wikimedia.org/wiki/File:Betel_leaf_plant_(Piper_betle).jpg'},
            'imageAuthor': {'stringValue': 'Santhosh Notagar99'},
            'imageLicense': {'stringValue': 'CC0'},
            'imageSourceType': {'stringValue': 'REMOTE_URL'}
        }
    },
    'cabai': {
        'nameId': 'Cabai',
        'fields': {
            'primaryPhotoUrl': {'stringValue': 'plant_cabai'},
            'defaultPhotoUrl': {'stringValue': 'plant_cabai'},
            'imageAssetName': {'stringValue': 'plant_cabai'},
            'imageSourceType': {'stringValue': 'LOCAL'}
        }
    },
    'lidah_buaya': {
        'nameId': 'Lidah Buaya',
        'fields': {
            'primaryPhotoUrl': {'stringValue': 'plant_lidah_buaya'},
            'defaultPhotoUrl': {'stringValue': 'plant_lidah_buaya'},
            'imageAssetName': {'stringValue': 'plant_lidah_buaya'},
            'imageSourceType': {'stringValue': 'LOCAL'}
        }
    }
}

# 2. Record Pre-Update State (Audit Trail)
print("\n[PRE-UPDATE AUDIT]")
for doc_id, spec in TARGET_UPDATES.items():
    doc_url = f'https://firestore.googleapis.com/v1/projects/{project_id}/databases/(default)/documents/master_plants/{doc_id}'
    req = urllib.request.Request(doc_url, headers={'Authorization': f'Bearer {id_token}'})
    with urllib.request.urlopen(req) as resp:
        curr_doc = json.loads(resp.read().decode('utf-8'))
        f = curr_doc.get('fields', {})
        old_photo = f.get('primaryPhotoUrl', {}).get('stringValue', '')
        print(f"Plant: {spec['nameId']:12} | DocId: {doc_id:12} | Old primaryPhotoUrl: {old_photo}")

# 3. Perform Targeted PATCH Updates
print("\n[APPLYING TARGETED UPDATES]")
now_ms = int(time.time() * 1000)

for doc_id, spec in TARGET_UPDATES.items():
    field_masks = list(spec['fields'].keys()) + ['updatedAt']
    mask_params = '&'.join([f'updateMask.fieldPaths={k}' for k in field_masks])
    patch_url = f'https://firestore.googleapis.com/v1/projects/{project_id}/databases/(default)/documents/master_plants/{doc_id}?{mask_params}'

    body_fields = dict(spec['fields'])
    body_fields['updatedAt'] = {'integerValue': str(now_ms)}
    payload = json.dumps({'fields': body_fields}).encode('utf-8')

    patch_req = urllib.request.Request(
        patch_url,
        data=payload,
        headers={
            'Authorization': f'Bearer {id_token}',
            'Content-Type': 'application/json'
        },
        method='PATCH'
    )

    with urllib.request.urlopen(patch_req) as resp:
        res_data = json.loads(resp.read().decode('utf-8'))
        print(f"Updated {doc_id} -> HTTP {resp.status}")

# 4. Post-Update Verification (Read Back)
print("\n[POST-UPDATE VERIFICATION - READ BACK]")
for doc_id, spec in TARGET_UPDATES.items():
    doc_url = f'https://firestore.googleapis.com/v1/projects/{project_id}/databases/(default)/documents/master_plants/{doc_id}'
    req = urllib.request.Request(doc_url, headers={'Authorization': f'Bearer {id_token}'})
    with urllib.request.urlopen(req) as resp:
        verified_doc = json.loads(resp.read().decode('utf-8'))
        f = verified_doc.get('fields', {})
        new_photo = f.get('primaryPhotoUrl', {}).get('stringValue', '')
        new_source_type = f.get('imageSourceType', {}).get('stringValue', '')
        print(f"Plant: {spec['nameId']:12} | DocId: {doc_id:12} | New primaryPhotoUrl: {new_photo[:60]} | SourceType: {new_source_type}")

print("\nAll database updates verified and committed successfully.")
print("==========================================================================")
