#!/usr/bin/env python3
"""
scripts/update_master_plants_images.py
Updates all 20 master_plants in Firestore with their new local image tags and Wikimedia metadata:
- imageAssetName = plant_<id>
- primaryPhotoUrl = plant_<id>
- defaultPhotoUrl = plant_<id>
- imageSourceType = LOCAL
- imageSource = <wikimedia url>
- imageLicense = <license>
- imageAuthor = <author>
"""

import json
import urllib.request
import urllib.parse
import sys
import time

try:
    sys.stdout.reconfigure(encoding='utf-8')
except Exception:
    pass

# Load Firebase configuration
with open('app/google-services.json') as f:
    gs = json.load(f)
api_key = gs['client'][0]['api_key'][0]['current_key']
project_id = gs['project_info']['project_id']
base_firestore_url = f'https://firestore.googleapis.com/v1/projects/{project_id}/databases/(default)/documents'

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
auth_headers = {
    'Authorization': f'Bearer {id_token}',
    'Content-Type': 'application/json'
}

def firestore_patch(collection, doc_id, fields, update_mask_fields=None):
    url = f'{base_firestore_url}/{collection}/{doc_id}'
    if update_mask_fields:
        params = [('updateMask.fieldPaths', f) for f in update_mask_fields]
        url += '?' + urllib.parse.urlencode(params)
    body = json.dumps({'fields': fields}).encode('utf-8')
    req = urllib.request.Request(url, data=body, headers=auth_headers, method='PATCH')
    with urllib.request.urlopen(req) as resp:
        return json.loads(resp.read().decode('utf-8'))

def main():
    print("=" * 80)
    print("UPDATING 20 MASTER PLANTS IN FIRESTORE WITH IMAGE TAGS")
    print("=" * 80)

    with open('scripts/fetched_plant_images_meta.json', 'r', encoding='utf-8') as f:
        meta = json.load(f)

    for pid, data in meta.items():
        tag = data['tag']
        patch_fields = {
            'imageAssetName': {'stringValue': tag},
            'primaryPhotoUrl': {'stringValue': tag},
            'defaultPhotoUrl': {'stringValue': tag},
            'imageSourceType': {'stringValue': 'LOCAL'},
            'imageSource': {'stringValue': data['image_source']},
            'imageLicense': {'stringValue': data['image_license']},
            'imageAuthor': {'stringValue': data['image_author']}
        }
        mask = list(patch_fields.keys())
        try:
            firestore_patch('master_plants', pid, patch_fields, mask)
            print(f"[OK] Updated {pid:15} -> {tag} ({data['image_license']})")
        except Exception as e:
            print(f"[ERR] Failed to update {pid}: {e}")

    # Read-back verification
    print("\n--- READ-BACK AUDIT OF ALL 32 MASTER PLANTS ---")
    req = urllib.request.Request(f'{base_firestore_url}/master_plants?pageSize=100', headers=auth_headers)
    with urllib.request.urlopen(req) as resp:
        docs = json.loads(resp.read().decode('utf-8')).get('documents', [])

    tagged_count = 0
    for d in sorted(docs, key=lambda x: x['name']):
        doc_id = d['name'].split('/')[-1]
        f = d.get('fields', {})
        asset = f.get('imageAssetName', {}).get('stringValue', '')
        p_url = f.get('primaryPhotoUrl', {}).get('stringValue', '')
        if asset or p_url:
            tagged_count += 1
            print(f"VERIFIED: {doc_id:15} | asset: {asset:20} | primaryPhoto: {p_url}")
        else:
            print(f"WARNING:  {doc_id:15} | MISSING IMAGE TAG!")

    print("=" * 80)
    print(f"TOTAL TAGGED MASTER PLANTS: {tagged_count} / {len(docs)}")
    print("=" * 80)

if __name__ == '__main__':
    main()
