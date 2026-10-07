#!/usr/bin/env python3
import urllib.request
import json
import time

with open('app/google-services.json') as f:
    gs = json.load(f)
api_key = gs['client'][0]['api_key'][0]['current_key']
project_id = gs['project_info']['project_id']

# Authenticate as Admin
auth_url = f'https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key={api_key}'
payload = json.dumps({'email': 'admin@bubakangreen.id', 'password': 'BubakanAdmin2026!', 'returnSecureToken': True}).encode('utf-8')
auth_req = urllib.request.Request(auth_url, data=payload, headers={'Content-Type': 'application/json'})
with urllib.request.urlopen(auth_req) as resp:
    token_data = json.loads(resp.read().decode('utf-8'))
id_token = token_data['idToken']

BASE_FS_URL = f'https://firestore.googleapis.com/v1/projects/{project_id}/databases/(default)/documents/master_plants'

# Fetch all docs
req = urllib.request.Request(BASE_FS_URL, headers={'Authorization': f'Bearer {id_token}'})
with urllib.request.urlopen(req) as resp:
    docs = json.loads(resp.read().decode('utf-8')).get('documents', [])

now_ms = int(time.time() * 1000)

for d in docs:
    doc_id = d['name'].split('/')[-1]
    f = d.get('fields', {})
    
    # Rebuild fields with strict types
    clean_fields = {}
    for k, v in f.items():
        if k in ("createdAt", "updatedAt"):
            # Ensure integerValue
            val_str = v.get('stringValue') or v.get('integerValue') or str(now_ms)
            try:
                val_int = int(val_str)
            except Exception:
                val_int = now_ms
            clean_fields[k] = {"integerValue": str(val_int)}
        elif k == "isPublished":
            b_val = v.get('booleanValue', True)
            clean_fields[k] = {"booleanValue": b_val}
        else:
            clean_fields[k] = v

    patch_url = f"{BASE_FS_URL}/{doc_id}"
    body = json.dumps({'fields': clean_fields}).encode('utf-8')
    patch_req = urllib.request.Request(patch_url, data=body, headers={
        'Authorization': f'Bearer {id_token}',
        'Content-Type': 'application/json'
    }, method='PATCH')
    with urllib.request.urlopen(patch_req) as resp:
        print(f"Fixed types for document '{doc_id}'")

print("All master_plants document field types validated and fixed.")
