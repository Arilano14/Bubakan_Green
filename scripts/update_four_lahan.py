#!/usr/bin/env python3
"""
scripts/update_four_lahan.py
Updates and verifies the 4 actual agricultural land plots in Firebase/Firestore for Bubakan Green:
1. Urban Farming dan Taman Toga RW 1 (loc_urban_farming_toga_rw01)
2. Taman Toga RW 2 (loc_taman_toga_rw02)
3. Taman Toga RW 3 (loc_taman_toga_rw03)
4. Urban Farming RW 3 (loc_urban_farming_rw03)

Decoupled Local Image Mode:
- Photos mapped via relative path: /assets/Lahan/...
- Idempotent execution
- Read-back verification
- NO deletion or corruption of existing documents
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

def firestore_get(collection, doc_id=None):
    url = f'{base_firestore_url}/{collection}'
    if doc_id:
        url = f'{url}/{doc_id}'
    req = urllib.request.Request(url, headers=auth_headers)
    try:
        with urllib.request.urlopen(req) as resp:
            return json.loads(resp.read().decode('utf-8'))
    except urllib.error.HTTPError as e:
        if e.code == 404:
            return None
        raise

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
    print("BUBAKAN GREEN: 4 LAHAN DATA & LOCAL IMAGE MODE SYNC")
    print(f"Project ID: {project_id}")
    print("=" * 80)
    
    now_ms = int(time.time() * 1000)

    # -------------------------------------------------------------
    # 1. Urban Farming dan Taman Toga RW 1
    # -------------------------------------------------------------
    loc1_id = 'loc_urban_farming_toga_rw01'
    loc1_photos = [
        '/assets/Lahan/RW 1_1.jpeg',
        '/assets/Lahan/RW 1_2.jpeg',
        '/assets/Lahan/RW 1_3.jpeg'
    ]
    loc1_fields = {
        'id': {'stringValue': loc1_id},
        'name': {'stringValue': 'Urban Farming dan Taman Toga RW 1'},
        'type': {'stringValue': 'URBAN_FARMING'},
        'rw': {'stringValue': 'RW 1'},
        'regionTag': {'stringValue': 'RW_01'},
        'address': {'stringValue': 'Lingkungan RW 01, Kelurahan Bubakan, Mijen'},
        'description': {'stringValue': 'Kawasan budidaya tanaman pangan dan tanaman obat keluarga (TOGA) warga RW 01 Kelurahan Bubakan.'},
        'latitude': {'doubleValue': -7.092056},
        'longitude': {'doubleValue': 110.319528},
        'coordinatesStatus': {'stringValue': 'VERIFIED'},
        'featured': {'booleanValue': True},
        'photos': {'arrayValue': {'values': [{'stringValue': p} for p in loc1_photos]}},
        'coverPhotoUrl': {'stringValue': loc1_photos[0]},
        'photoUrl': {'stringValue': loc1_photos[0]},
        'conditionNote': {'stringValue': 'Lahan percontohan urban farming dan toga aktif dengan pintu gerbang utama RW 01.'},
        'isPublished': {'booleanValue': True},
        'status': {'stringValue': 'ACTIVE'},
        'createdBy': {'stringValue': 'SYSTEM_SEED'},
        'picUid': {'stringValue': 'SYSTEM_SEED'},
        'updatedAt': {'integerValue': str(now_ms)}
    }
    firestore_patch('locations', loc1_id, loc1_fields, list(loc1_fields.keys()))
    print(f"[OK] Lahan 1 Updated: {loc1_id} (Urban Farming dan Taman Toga RW 1)")

    # -------------------------------------------------------------
    # 2. Taman Toga RW 2 (NEW)
    # -------------------------------------------------------------
    loc2_id = 'loc_taman_toga_rw02'
    loc2_photos = [
        '/assets/Lahan/RW 2_Toga.jpeg',
        '/assets/Lahan/RW 2_Toga 1.jpeg',
        '/assets/Lahan/RW 2_Toga 2.jpeg'
    ]
    loc2_fields = {
        'id': {'stringValue': loc2_id},
        'name': {'stringValue': 'Taman Toga RW 2'},
        'type': {'stringValue': 'TAMAN_TOGA'},
        'rw': {'stringValue': 'RW 2'},
        'regionTag': {'stringValue': 'RW_02'},
        'address': {'stringValue': 'Lingkungan RW 02, Kelurahan Bubakan, Mijen'},
        'description': {'stringValue': 'Taman tanaman obat keluarga warga RW 02 Kelurahan Bubakan.'},
        'latitude': {'doubleValue': -7.097694},
        'longitude': {'doubleValue': 110.320000},
        'coordinatesStatus': {'stringValue': 'VERIFIED'},
        'featured': {'booleanValue': False},
        'photos': {'arrayValue': {'values': [{'stringValue': p} for p in loc2_photos]}},
        'coverPhotoUrl': {'stringValue': loc2_photos[0]},
        'photoUrl': {'stringValue': loc2_photos[0]},
        'conditionNote': {'stringValue': 'Koleksi tanaman obat keluarga terpelihara dengan baik.'},
        'isPublished': {'booleanValue': True},
        'status': {'stringValue': 'ACTIVE'},
        'createdBy': {'stringValue': 'SYSTEM_SEED'},
        'picUid': {'stringValue': 'SYSTEM_SEED'},
        'createdAt': {'integerValue': str(now_ms)},
        'updatedAt': {'integerValue': str(now_ms)}
    }
    firestore_patch('locations', loc2_id, loc2_fields, list(loc2_fields.keys()))
    print(f"[OK] Lahan 2 Created: {loc2_id} (Taman Toga RW 2, -7.097694, 110.320000, 3 photos, 0 plants)")

    # -------------------------------------------------------------
    # 3. Taman Toga RW 3
    # -------------------------------------------------------------
    loc3_id = 'loc_taman_toga_rw03'
    loc3_photos = [
        '/assets/Lahan/RW 3_Toga.jpeg',
        '/assets/Lahan/RW 3_Toga 2.jpeg',
        '/assets/Lahan/RW 3_Toga 3.jpeg'
    ]
    loc3_fields = {
        'id': {'stringValue': loc3_id},
        'name': {'stringValue': 'Taman Toga RW 3'},
        'type': {'stringValue': 'TAMAN_TOGA'},
        'rw': {'stringValue': 'RW 3'},
        'regionTag': {'stringValue': 'RW_03'},
        'address': {'stringValue': 'Lingkungan RW 03, Kelurahan Bubakan, Mijen'},
        'description': {'stringValue': 'Taman tanaman obat keluarga warga RW 03 yang membudidayakan ragam tanaman herbal tradisional. Kolaborasi dengan Mahasiswa KKN.'},
        'latitude': {'doubleValue': -7.091944},
        'longitude': {'doubleValue': 110.325667},
        'coordinatesStatus': {'stringValue': 'VERIFIED'},
        'featured': {'booleanValue': True},
        'photos': {'arrayValue': {'values': [{'stringValue': p} for p in loc3_photos]}},
        'coverPhotoUrl': {'stringValue': loc3_photos[0]},
        'photoUrl': {'stringValue': loc3_photos[0]},
        'conditionNote': {'stringValue': 'Koleksi tanaman obat keluarga terpelihara dengan baik. Kolaborasi dengan Mahasiswa KKN.'},
        'isPublished': {'booleanValue': True},
        'status': {'stringValue': 'ACTIVE'},
        'updatedAt': {'integerValue': str(now_ms)}
    }
    firestore_patch('locations', loc3_id, loc3_fields, list(loc3_fields.keys()))
    print(f"[OK] Lahan 3 Updated: {loc3_id} (Taman Toga RW 3, 3 photos)")

    # -------------------------------------------------------------
    # 4. Urban Farming RW 3
    # -------------------------------------------------------------
    loc4_id = 'loc_urban_farming_rw03'
    loc4_photos = [
        '/assets/Lahan/RW 3_Urban.jpeg',
        '/assets/Lahan/RW 3_Urban 2.jpeg',
        '/assets/Lahan/RW 3_Urban 3.jpeg'
    ]
    loc4_fields = {
        'id': {'stringValue': loc4_id},
        'name': {'stringValue': 'Urban Farming RW 3'},
        'type': {'stringValue': 'URBAN_FARMING'},
        'rw': {'stringValue': 'RW 3'},
        'regionTag': {'stringValue': 'RW_03'},
        'address': {'stringValue': 'Lingkungan RW 03, Kelurahan Bubakan, Mijen'},
        'description': {'stringValue': 'Sentra budidaya pertanian mandiri dan tanaman pangan warga RW 03 Kelurahan Bubakan.'},
        'latitude': {'doubleValue': -7.092000},
        'longitude': {'doubleValue': 110.324806},
        'coordinatesStatus': {'stringValue': 'VERIFIED'},
        'featured': {'booleanValue': False},
        'photos': {'arrayValue': {'values': [{'stringValue': p} for p in loc4_photos]}},
        'coverPhotoUrl': {'stringValue': loc4_photos[0]},
        'photoUrl': {'stringValue': loc4_photos[0]},
        'conditionNote': {'stringValue': 'Lahan produktif terawat.'},
        'isPublished': {'booleanValue': True},
        'status': {'stringValue': 'ACTIVE'},
        'updatedAt': {'integerValue': str(now_ms)}
    }
    firestore_patch('locations', loc4_id, loc4_fields, list(loc4_fields.keys()))
    print(f"[OK] Lahan 4 Updated: {loc4_id} (Urban Farming RW 3, 3 photos)")

    # -------------------------------------------------------------
    # VERIFICATION READ-BACK
    # -------------------------------------------------------------
    print("\n--- PHASE 5: READ-BACK AUDIT ---")
    all_locs = firestore_get('locations').get('documents', [])
    target_ids = [loc1_id, loc2_id, loc3_id, loc4_id]
    
    for d in all_locs:
        doc_id = d['name'].split('/')[-1]
        if doc_id in target_ids:
            f = d.get('fields', {})
            name = f.get('name', {}).get('stringValue', '')
            rw = f.get('rw', {}).get('stringValue', '')
            lat = f.get('latitude', {}).get('doubleValue', '')
            lng = f.get('longitude', {}).get('doubleValue', '')
            photos = [p.get('stringValue', '') for p in f.get('photos', {}).get('arrayValue', {}).get('values', [])]
            print(f"VERIFIED: {doc_id}")
            print(f"  Name: {name}")
            print(f"  RW: {rw} | Coordinates: ({lat}, {lng})")
            print(f"  Photos ({len(photos)}): {photos}")
            print()

if __name__ == '__main__':
    main()
