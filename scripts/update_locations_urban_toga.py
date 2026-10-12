#!/usr/bin/env python3
"""
update_locations_urban_toga.py
Sync Urban Farming Kelurahan photos & split RW 1 into:
  1. Urban Farming RW 1 (loc_urban_farming_rw01 & loc_urban_farming_toga_rw01) -> 12 food/vegetable plants
  2. Taman Toga RW 1 (loc_taman_toga_rw01) -> 13 TOGA/medicinal plants (adjacent to Urban Farming RW 1)
And update Urban Farming Kelurahan (loc_urban_farming_bubakan) with 2 photos:
  - /assets/Lahan/kelurahan_urban 1.png
  - /assets/Lahan/Kelurahan Urban 2.jpeg
"""

import urllib.request
import json
import time
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

def firestore_patch(collection, doc_id, fields, update_mask):
    mask_params = '&'.join([f'updateMask.fieldPaths={k}' for k in update_mask])
    url = f'https://firestore.googleapis.com/v1/projects/{project_id}/databases/(default)/documents/{collection}/{doc_id}?{mask_params}'
    body = json.dumps({'fields': fields}).encode('utf-8')
    req = urllib.request.Request(url, data=body, headers=auth_headers, method='PATCH')
    with urllib.request.urlopen(req) as resp:
        return json.loads(resp.read().decode('utf-8'))

def firestore_delete(collection, doc_id):
    url = f'https://firestore.googleapis.com/v1/projects/{project_id}/databases/(default)/documents/{collection}/{doc_id}'
    req = urllib.request.Request(url, headers=auth_headers, method='DELETE')
    try:
        with urllib.request.urlopen(req) as resp:
            return True
    except urllib.error.HTTPError as e:
        if e.code == 404:
            return False
        raise

def firestore_get(collection, doc_id=None):
    if doc_id:
        url = f'https://firestore.googleapis.com/v1/projects/{project_id}/databases/(default)/documents/{collection}/{doc_id}'
    else:
        url = f'https://firestore.googleapis.com/v1/projects/{project_id}/databases/(default)/documents/{collection}?pageSize=100'
    req = urllib.request.Request(url, headers=auth_headers)
    try:
        with urllib.request.urlopen(req) as resp:
            return json.loads(resp.read().decode('utf-8'))
    except urllib.error.HTTPError as e:
        if e.code == 404:
            return None
        raise

def main():
    print("=" * 80)
    print("BUBAKAN GREEN: FIRESTORE LOCATION & PLANT PARTITION SYNC")
    print(f"Project ID: {project_id}")
    print("=" * 80)

    now_ms = int(time.time() * 1000)

    # -------------------------------------------------------------
    # 1. URBAN FARMING KELURAHAN (loc_urban_farming_bubakan)
    # -------------------------------------------------------------
    kelurahan_photos = [
        '/assets/Lahan/kelurahan_urban 1.png',
        '/assets/Lahan/Kelurahan Urban 2.jpeg'
    ]
    kelurahan_fields = {
        'id': {'stringValue': 'loc_urban_farming_bubakan'},
        'name': {'stringValue': 'Urban Farming Kelurahan Bubakan'},
        'type': {'stringValue': 'URBAN_FARMING'},
        'rw': {'stringValue': '01'},
        'regionTag': {'stringValue': 'KELURAHAN'},
        'address': {'stringValue': 'Jl. Raya Bubakan No. 1, Kelurahan Bubakan, Mijen'},
        'description': {'stringValue': 'Kebun percontohan budidaya sayuran dan pangan mandiri binaan Kelurahan Bubakan.'},
        'latitude': {'doubleValue': -7.093500},
        'longitude': {'doubleValue': 110.321500},
        'coordinatesStatus': {'stringValue': 'VERIFIED'},
        'featured': {'booleanValue': True},
        'photos': {'arrayValue': {'values': [{'stringValue': p} for p in kelurahan_photos]}},
        'coverPhotoUrl': {'stringValue': kelurahan_photos[0]},
        'photoUrl': {'stringValue': kelurahan_photos[0]},
        'conditionNote': {'stringValue': 'Tanaman dalam kondisi baik dan terawat.'},
        'isPublished': {'booleanValue': True},
        'status': {'stringValue': 'ACTIVE'},
        'updatedAt': {'integerValue': str(now_ms)}
    }
    firestore_patch('locations', 'loc_urban_farming_bubakan', kelurahan_fields, list(kelurahan_fields.keys()))
    print("[OK] loc_urban_farming_bubakan updated with 2 new photos.")

    # Also update loc-urban-farming-kelurahan-92b8e1 if it exists
    alt_kel = firestore_get('locations', 'loc-urban-farming-kelurahan-92b8e1')
    if alt_kel:
        alt_fields = dict(kelurahan_fields)
        alt_fields['id'] = {'stringValue': 'loc-urban-farming-kelurahan-92b8e1'}
        firestore_patch('locations', 'loc-urban-farming-kelurahan-92b8e1', alt_fields, list(alt_fields.keys()))
        print("[OK] loc-urban-farming-kelurahan-92b8e1 updated with 2 new photos.")

    # -------------------------------------------------------------
    # 2. URBAN FARMING RW 1 (loc_urban_farming_rw01 & loc_urban_farming_toga_rw01)
    # -------------------------------------------------------------
    rw1_uf_photos = [
        '/assets/Lahan/RW 1_1.jpeg',
        '/assets/Lahan/RW 1_2.jpeg',
        '/assets/Lahan/RW 1_3.jpeg'
    ]
    rw1_uf_fields = {
        'id': {'stringValue': 'loc_urban_farming_rw01'},
        'name': {'stringValue': 'Urban Farming RW 1'},
        'type': {'stringValue': 'URBAN_FARMING'},
        'rw': {'stringValue': '01'},
        'regionTag': {'stringValue': 'RW_01'},
        'address': {'stringValue': 'Lingkungan RW 01, Kelurahan Bubakan, Mijen'},
        'description': {'stringValue': 'Sentra budidaya tanaman pangan dan sayuran pekarangan mandiri warga RW 01.'},
        'latitude': {'doubleValue': -7.092056},
        'longitude': {'doubleValue': 110.319528},
        'coordinatesStatus': {'stringValue': 'VERIFIED'},
        'featured': {'booleanValue': True},
        'photos': {'arrayValue': {'values': [{'stringValue': p} for p in rw1_uf_photos]}},
        'coverPhotoUrl': {'stringValue': rw1_uf_photos[0]},
        'photoUrl': {'stringValue': rw1_uf_photos[0]},
        'conditionNote': {'stringValue': 'Lahan percontohan urban farming aktif dengan pintu gerbang utama RW 01.'},
        'isPublished': {'booleanValue': True},
        'status': {'stringValue': 'ACTIVE'},
        'createdBy': {'stringValue': 'SYSTEM_SEED'},
        'picUid': {'stringValue': 'SYSTEM_SEED'},
        'updatedAt': {'integerValue': str(now_ms)}
    }
    firestore_patch('locations', 'loc_urban_farming_rw01', rw1_uf_fields, list(rw1_uf_fields.keys()))
    print("[OK] loc_urban_farming_rw01 created/updated.")

    # Also update loc_urban_farming_toga_rw01 for backward compatibility
    rw1_compat_fields = dict(rw1_uf_fields)
    rw1_compat_fields['id'] = {'stringValue': 'loc_urban_farming_toga_rw01'}
    rw1_compat_fields['name'] = {'stringValue': 'Urban Farming RW 1'}
    firestore_patch('locations', 'loc_urban_farming_toga_rw01', rw1_compat_fields, list(rw1_compat_fields.keys()))
    print("[OK] loc_urban_farming_toga_rw01 updated to Urban Farming RW 1.")

    # -------------------------------------------------------------
    # 3. TAMAN TOGA RW 1 (loc_taman_toga_rw01 - Berdampingan)
    # -------------------------------------------------------------
    rw1_toga_photos = [
        '/assets/Lahan/RW 1_Taman Toga 1.jpeg',
        '/assets/Lahan/RW 1_Taman Toga.jpeg'
    ]
    rw1_toga_fields = {
        'id': {'stringValue': 'loc_taman_toga_rw01'},
        'name': {'stringValue': 'Taman Toga RW 1'},
        'type': {'stringValue': 'TAMAN_TOGA'},
        'rw': {'stringValue': '01'},
        'regionTag': {'stringValue': 'RW_01'},
        'address': {'stringValue': 'Lingkungan RW 01, Kelurahan Bubakan, Mijen (Berdampingan dengan Urban Farming RW 1)'},
        'description': {'stringValue': 'Taman tanaman obat keluarga (TOGA) warga RW 01 yang membudidayakan ragam tanaman herbal tradisional berdampingan dengan Urban Farming RW 1.'},
        'latitude': {'doubleValue': -7.092080},
        'longitude': {'doubleValue': 110.319550},
        'coordinatesStatus': {'stringValue': 'VERIFIED'},
        'featured': {'booleanValue': True},
        'photos': {'arrayValue': {'values': [{'stringValue': p} for p in rw1_toga_photos]}},
        'coverPhotoUrl': {'stringValue': rw1_toga_photos[0]},
        'photoUrl': {'stringValue': rw1_toga_photos[0]},
        'conditionNote': {'stringValue': 'Koleksi tanaman obat keluarga (TOGA) RW 01 tertata rapi berdampingan dengan Urban Farming RW 1.'},
        'isPublished': {'booleanValue': True},
        'status': {'stringValue': 'ACTIVE'},
        'createdBy': {'stringValue': 'SYSTEM_SEED'},
        'picUid': {'stringValue': 'SYSTEM_SEED'},
        'createdAt': {'integerValue': str(now_ms)},
        'updatedAt': {'integerValue': str(now_ms)}
    }
    firestore_patch('locations', 'loc_taman_toga_rw01', rw1_toga_fields, list(rw1_toga_fields.keys()))
    print("[OK] loc_taman_toga_rw01 created/updated.")

    # -------------------------------------------------------------
    # 4. PARTITION RW 1 PLANTS
    # -------------------------------------------------------------
    # 12 Food / Vegetable crops -> Urban Farming RW 1
    uf_food_plants = [
        'seledri', 'kucai', 'pakcoy', 'daun_bawang', 'cabai',
        'tomat', 'caisim', 'talas', 'terong', 'pepaya',
        'bayam', 'kacang_panjang'
    ]

    # 13 TOGA / Herbal medicinal plants -> Taman Toga RW 1
    toga_herbal_plants = [
        'jahe', 'lengkuas', 'kunyit', 'sereh', 'kumis_kucing',
        'pegagan', 'bunga_kenanga', 'bunga_bugang', 'kemangi',
        'pandan', 'mahoni', 'daun_sintrong', 'cepokak'
    ]

    print("\n--- PHASE 4: PARTITIONING RW 1 PLANTS ---")
    print(f"Urban Farming RW 1: {len(uf_food_plants)} food/vegetable plants")
    print(f"Taman Toga RW 1:    {len(toga_herbal_plants)} TOGA herbal plants")

    # A. Write relations to loc_taman_toga_rw01
    for pid in toga_herbal_plants:
        doc_id = f"loc_taman_toga_rw01_{pid}"
        lp_fields = {
            'id': {'stringValue': doc_id},
            'locationId': {'stringValue': 'loc_taman_toga_rw01'},
            'plantId': {'stringValue': pid},
            'masterPlantId': {'stringValue': pid},
            'quantity': {'integerValue': '10'},
            'quantityNote': {'stringValue': '10 tanaman/pot/rumpun'},
            'notes': {'stringValue': 'Koleksi tanaman obat keluarga (TOGA) binaan RW 01.'},
            'condition': {'stringValue': 'GOOD'},
            'isPresent': {'booleanValue': True},
            'featuredForQr': {'booleanValue': (pid in ['jahe', 'kunyit', 'sereh', 'kumis_kucing'])},
            'status': {'stringValue': 'ACTIVE'},
            'createdAt': {'integerValue': str(now_ms)},
            'updatedAt': {'integerValue': str(now_ms)}
        }
        firestore_patch('location_plants', doc_id, lp_fields, list(lp_fields.keys()))
        print(f"  [+] Taman Toga RW 1: {doc_id}")

    # B. Write relations to loc_urban_farming_rw01
    for pid in uf_food_plants:
        doc_id = f"loc_urban_farming_rw01_{pid}"
        lp_fields = {
            'id': {'stringValue': doc_id},
            'locationId': {'stringValue': 'loc_urban_farming_rw01'},
            'plantId': {'stringValue': pid},
            'masterPlantId': {'stringValue': pid},
            'quantity': {'integerValue': '12'},
            'quantityNote': {'stringValue': '12 polybag/bedengan'},
            'notes': {'stringValue': 'Budidaya sayuran dan pangan mandiri Urban Farming RW 01.'},
            'condition': {'stringValue': 'GOOD'},
            'isPresent': {'booleanValue': True},
            'featuredForQr': {'booleanValue': (pid in ['cabai', 'tomat', 'pakcoy', 'caisim'])},
            'status': {'stringValue': 'ACTIVE'},
            'createdAt': {'integerValue': str(now_ms)},
            'updatedAt': {'integerValue': str(now_ms)}
        }
        firestore_patch('location_plants', doc_id, lp_fields, list(lp_fields.keys()))
        print(f"  [+] Urban Farming RW 1: {doc_id}")

    # C. Delete TOGA plants from old loc_urban_farming_toga_rw01 so it only has food crops
    print("\n--- Cleaning TOGA plants from old loc_urban_farming_toga_rw01 ---")
    for pid in toga_herbal_plants:
        old_id = f"loc_urban_farming_toga_rw01_{pid}"
        deleted = firestore_delete('location_plants', old_id)
        if deleted:
            print(f"  [-] Removed {old_id} from old location")

    # -------------------------------------------------------------
    # VERIFICATION AUDIT
    # -------------------------------------------------------------
    print("\n" + "=" * 80)
    print("VERIFICATION READ-BACK AUDIT")
    print("=" * 80)
    loc_docs = firestore_get('locations').get('documents', [])
    for d in loc_docs:
        doc_id = d['name'].split('/')[-1]
        if doc_id in ['loc_urban_farming_bubakan', 'loc_urban_farming_rw01', 'loc_taman_toga_rw01', 'loc_urban_farming_toga_rw01']:
            f = d.get('fields', {})
            name = f.get('name', {}).get('stringValue', '')
            photos = [p.get('stringValue', '') for p in f.get('photos', {}).get('arrayValue', {}).get('values', [])]
            lat = f.get('latitude', {}).get('doubleValue', 0.0)
            lng = f.get('longitude', {}).get('doubleValue', 0.0)
            print(f"LOCATION: {doc_id}")
            print(f"  Name: {name} | Coordinates: ({lat}, {lng})")
            print(f"  Photos ({len(photos)}): {photos}")

    lp_docs = firestore_get('location_plants').get('documents', [])
    by_loc = {}
    for d in lp_docs:
        f = d.get('fields', {})
        lid = f.get('locationId', {}).get('stringValue', '')
        pid = f.get('plantId', {}).get('stringValue', '')
        by_loc.setdefault(lid, []).append(pid)

    print("\nLOCATION_PLANTS SUMMARY:")
    for lid in ['loc_urban_farming_bubakan', 'loc_urban_farming_rw01', 'loc_taman_toga_rw01', 'loc_urban_farming_toga_rw01', 'loc_taman_toga_rw03', 'loc_urban_farming_rw03']:
        pids = by_loc.get(lid, [])
        print(f"  {lid} ({len(pids)} plants): {sorted(pids)}")

if __name__ == '__main__':
    main()
