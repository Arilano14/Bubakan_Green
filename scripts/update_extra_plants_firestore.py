#!/usr/bin/env python3
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
project_id = gs['project_info']['project_info'] if 'project_info' in gs['project_info'] else gs['project_info']['project_id']

# 1. Authenticate as Admin
auth_url = f'https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key={api_key}'
payload = json.dumps({'email': 'admin@bubakangreen.id', 'password': 'BubakanAdmin2026!', 'returnSecureToken': True}).encode('utf-8')
auth_req = urllib.request.Request(auth_url, data=payload, headers={'Content-Type': 'application/json'})
with urllib.request.urlopen(auth_req) as resp:
    token_data = json.loads(resp.read().decode('utf-8'))
id_token = token_data['idToken']

plants_to_update = {
    'kemangi': 'plant_kemangi',
    'pegagan': 'plant_pegagan',
    'sirih': 'plant_sirih'
}

print("=================================================================")
print("UPDATING FIRESTORE MASTER_PLANTS PHOTOS (PRE-UPDATE AUDIT)")
print("=================================================================")

for plant_id, new_asset in plants_to_update.items():
    doc_url = f'https://firestore.googleapis.com/v1/projects/{project_id}/databases/(default)/documents/master_plants/{plant_id}'
    req = urllib.request.Request(doc_url, headers={'Authorization': f'Bearer {id_token}'})
    with urllib.request.urlopen(req) as resp:
        doc = json.loads(resp.read().decode('utf-8'))
    fields = doc.get('fields', {})
    old_primary = fields.get('primaryPhotoUrl', {}).get('stringValue', '')
    print(f"Plant: {plant_id}")
    print(f"  Old primaryPhotoUrl: {old_primary}")
    print(f"  Target new asset:    {new_asset}")

    # Patch only photo fields using fieldMask
    now_ms = int(time.time() * 1000)
    patch_fields = {
        'primaryPhotoUrl': {'stringValue': new_asset},
        'defaultPhotoUrl': {'stringValue': new_asset},
        'photoUrl': {'stringValue': new_asset},
        'imageAssetName': {'stringValue': new_asset},
        'imageSourceType': {'stringValue': 'LOCAL'},
        'updatedAt': {'integerValue': str(now_ms)}
    }
    
    mask = "updateMask.fieldPaths=primaryPhotoUrl&updateMask.fieldPaths=defaultPhotoUrl&updateMask.fieldPaths=photoUrl&updateMask.fieldPaths=imageAssetName&updateMask.fieldPaths=imageSourceType&updateMask.fieldPaths=updatedAt"
    patch_url = f"{doc_url}?{mask}"
    patch_req = urllib.request.Request(
        patch_url,
        data=json.dumps({'fields': patch_fields}).encode('utf-8'),
        headers={'Authorization': f'Bearer {id_token}', 'Content-Type': 'application/json'},
        method='PATCH'
    )
    with urllib.request.urlopen(patch_req) as patch_resp:
        updated_doc = json.loads(patch_resp.read().decode('utf-8'))
    print(f"  Status: PATCH SUCCESSFUL")

print("\n=================================================================")
print("VERIFYING PERSISTENCE (READ-BACK FROM LIVE FIRESTORE)")
print("=================================================================")

for plant_id in plants_to_update:
    doc_url = f'https://firestore.googleapis.com/v1/projects/{project_id}/databases/(default)/documents/master_plants/{plant_id}'
    req = urllib.request.Request(doc_url, headers={'Authorization': f'Bearer {id_token}'})
    with urllib.request.urlopen(req) as resp:
        doc = json.loads(resp.read().decode('utf-8'))
    f = doc.get('fields', {})
    print(f"Plant [{plant_id}]:")
    print(f"  primaryPhotoUrl: {f.get('primaryPhotoUrl', {}).get('stringValue')}")
    print(f"  imageAssetName:  {f.get('imageAssetName', {}).get('stringValue')}")
    print(f"  imageSourceType: {f.get('imageSourceType', {}).get('stringValue')}")
    print(f"  updatedAt:       {f.get('updatedAt', {}).get('integerValue')}")

print("\nAll 3 plant documents successfully updated and verified in Firestore!")
