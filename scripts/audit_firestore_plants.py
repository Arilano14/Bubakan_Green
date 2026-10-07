#!/usr/bin/env python3
import urllib.request
import json
import sys

try:
    sys.stdout.reconfigure(encoding='utf-8')
except Exception:
    pass

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

url = f'https://firestore.googleapis.com/v1/projects/{project_id}/databases/(default)/documents/master_plants'
req = urllib.request.Request(url, headers={'Authorization': f'Bearer {id_token}'})
with urllib.request.urlopen(req) as resp:
    data = json.loads(resp.read().decode('utf-8'))
    docs = data.get('documents', [])

print("==========================================================================================")
print(f"FIRESTORE AUDIT REPORT: master_plants ({len(docs)} documents)")
print("==========================================================================================")
print(f"{'ID':14} | {'Name (ID)':15} | {'Latin Name':32} | {'Hanzi':6} | {'Photo Reference':50}")
print("-" * 125)

for d in sorted(docs, key=lambda x: x['name']):
    doc_id = d['name'].split('/')[-1]
    f = d.get('fields', {})
    name = f.get('nameId', {}).get('stringValue', '')
    latin = f.get('nameLatin', {}).get('stringValue', '')
    hanzi = f.get('nameMandarin', {}).get('stringValue', '-')
    photo = f.get('primaryPhotoUrl', {}).get('stringValue', '')
    # Check for Base64 (should NOT be present)
    is_base64 = "base64" in photo.lower() or len(photo) > 500
    b64_flag = " [FAIL: BASE64 DETECTED]" if is_base64 else " [OK: STRING PATH]"
    print(f"{doc_id:14} | {name:15} | {latin:32} | {hanzi:6} | {photo[:45]:45}{b64_flag}")

print("==========================================================================================")
