#!/usr/bin/env python3
import urllib.request
import urllib.parse
import json
import time
import os
import subprocess

ADB = os.path.expandvars(r"%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe")

def run_adb(cmd):
    full_cmd = f'"{ADB}" {cmd}'
    res = subprocess.run(full_cmd, shell=True, capture_output=True, text=True)
    return res.stdout.strip()

print("==================================================")
print("1. DEPLOYING PHOTOS TO EMULATOR APP STORAGE")
print("==================================================")

# Push images to /data/local/tmp
run_adb(r'push assets\qa\plants\sirih.jpg /data/local/tmp/plant_sirih_camera.jpg')
run_adb(r'push assets\qa\plants\pegagan.jpg /data/local/tmp/plant_pegagan_gallery.jpg')
run_adb(r'push assets\qa\plants\cabai_replacement.jpg /data/local/tmp/plant_cabai_gallery.jpg')
run_adb(r'push assets\qa\plants\lidah_buaya_replacement.jpg /data/local/tmp/plant_lidah_buaya_camera.jpg')

# Copy to app private files/photos
run_adb('shell "run-as id.bubakangreen.app mkdir -p /data/user/0/id.bubakangreen.app/files/photos"')
run_adb('shell "run-as id.bubakangreen.app cp /data/local/tmp/plant_sirih_camera.jpg /data/user/0/id.bubakangreen.app/files/photos/plant_sirih_camera.jpg"')
run_adb('shell "run-as id.bubakangreen.app cp /data/local/tmp/plant_pegagan_gallery.jpg /data/user/0/id.bubakangreen.app/files/photos/plant_pegagan_gallery.jpg"')
run_adb('shell "run-as id.bubakangreen.app cp /data/local/tmp/plant_cabai_gallery.jpg /data/user/0/id.bubakangreen.app/files/photos/plant_cabai_gallery.jpg"')
run_adb('shell "run-as id.bubakangreen.app cp /data/local/tmp/plant_lidah_buaya_camera.jpg /data/user/0/id.bubakangreen.app/files/photos/plant_lidah_buaya_camera.jpg"')
run_adb('shell "run-as id.bubakangreen.app chmod 644 /data/user/0/id.bubakangreen.app/files/photos/*.jpg"')

print("Photos deployed to /data/user/0/id.bubakangreen.app/files/photos/ successfully.")

print("\n==================================================")
print("2. AUTHENTICATING AS FIREBASE ADMIN")
print("==================================================")

with open('app/google-services.json') as f:
    gs = json.load(f)
api_key = gs['client'][0]['api_key'][0]['current_key']
project_id = gs['project_info']['project_id']

auth_url = f'https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key={api_key}'
payload = json.dumps({'email': 'admin@bubakangreen.id', 'password': 'BubakanAdmin2026!', 'returnSecureToken': True}).encode('utf-8')
auth_req = urllib.request.Request(auth_url, data=payload, headers={'Content-Type': 'application/json'})
with urllib.request.urlopen(auth_req) as resp:
    token_data = json.loads(resp.read().decode('utf-8'))
id_token = token_data['idToken']
admin_uid = token_data['localId']
print(f"Authenticated successfully as Admin: {admin_uid}")

BASE_FS_URL = f'https://firestore.googleapis.com/v1/projects/{project_id}/databases/(default)/documents/master_plants'

def fs_delete_doc(doc_id):
    url = f"{BASE_FS_URL}/{doc_id}"
    req = urllib.request.Request(url, headers={'Authorization': f'Bearer {id_token}'}, method='DELETE')
    try:
        with urllib.request.urlopen(req) as resp:
            print(f"  [DELETED] Document {doc_id}")
    except urllib.error.HTTPError as e:
        print(f"  [DELETE_SKIP] Document {doc_id}: {e.code}")

def fs_save_doc(doc_id, doc_data):
    # Convert python dict to Firestore REST fields format
    fields = {}
    for k, v in doc_data.items():
        if v is None:
            fields[k] = {'nullValue': None}
        elif isinstance(v, bool):
            fields[k] = {'booleanValue': v}
        elif isinstance(v, int):
            fields[k] = {'integerValue': str(v)}
        elif isinstance(v, float):
            fields[k] = {'doubleValue': v}
        elif isinstance(v, str):
            fields[k] = {'stringValue': v}
        elif isinstance(v, list):
            array_values = []
            for item in v:
                if isinstance(item, str):
                    array_values.append({'stringValue': item})
            fields[k] = {'arrayValue': {'values': array_values}}

    url = f"{BASE_FS_URL}/{doc_id}"
    body = json.dumps({'fields': fields}).encode('utf-8')
    req = urllib.request.Request(url, data=body, headers={
        'Authorization': f'Bearer {id_token}',
        'Content-Type': 'application/json'
    }, method='PATCH')
    with urllib.request.urlopen(req) as resp:
        res = json.loads(resp.read().decode('utf-8'))
        print(f"  [SAVED] Document '{doc_id}' successfully written to Firestore.")

print("\n==================================================")
print("3. CLEANING TEMPORARY TEST RECORDS")
print("==================================================")
fs_delete_doc("pl-sirihplant-jpg-a0eaa1")

now_ms = int(time.time() * 1000)

print("\n==================================================")
print("4. EJECTING 3 CANONICAL DEFAULT PLANTS")
print("==================================================")

# 1. SIRIH (Camera Input Scenario)
sirih_data = {
    "id": "sirih",
    "name": "Sirih",
    "nameId": "Sirih",
    "scientificName": "Piper betle",
    "nameLatin": "Piper betle",
    "mandarinName": "蒌叶",
    "nameMandarin": "蒌叶",
    "mandarinPinyin": "lóu yè",
    "pinyin": "lóu yè",
    "mandarinAudioUrl": None,
    "description": "Tanaman merambat herba perennial dengan daun berbentuk hati beraroma aromatik khas dan rasa getir hangat menyegarkan.",
    "characteristics": "Batang bulat beruas dengan akar lekat, daun tunggal berseling berurat melengkung 5-7, kaya kelenjar minyak atsiri.",
    "commonUses": "Antiseptik alami, antibakteri pembersih mulut dan tenggorokan, seduhan pereda batuk, dan bahan racikan kunyah sirih.",
    "cultivationNotes": "Tumbuh merambat di tiang ajir, naungan parsial 60-70%, tanah gembur kaya humus berdrainase baik, penyiraman 1-2 kali sehari.",
    "benefits": "Antiseptik alami, antibakteri pembersih mulut dan tenggorokan, seduhan pereda batuk, dan bahan racikan kunyah sirih.",
    "plantingGuide": "Tumbuh merambat di tiang ajir, naungan parsial 60-70%, tanah gembur kaya humus berdrainase baik, penyiraman 1-2 kali sehari.",
    "primaryPhotoUrl": "/data/user/0/id.bubakangreen.app/files/photos/plant_sirih_camera.jpg",
    "defaultPhotoUrl": "/data/user/0/id.bubakangreen.app/files/photos/plant_sirih_camera.jpg",
    "imageSourceType": "LOCAL",
    "imageAssetName": None,
    "imageSource": "https://upload.wikimedia.org/wikipedia/commons/0/08/%27Paan%27_or_Betel_or_Piper_betle_leaf.jpg",
    "imageLicense": "CC BY-SA 4.0",
    "imageAuthor": "Wikimedia Commons",
    "sourceReferences": "Royal Botanic Gardens, Kew (POWO: urn:lsid:ipni.org:names:680608-1); Flora of China Vol. 4; Farmakope Herbal Indonesia Edisi II",
    "isPublished": True,
    "createdAt": now_ms,
    "updatedAt": now_ms
}
fs_save_doc("sirih", sirih_data)

# 2. PEGAGAN (Gallery Input Scenario)
pegagan_data = {
    "id": "pegagan",
    "name": "Pegagan",
    "nameId": "Pegagan",
    "scientificName": "Centella asiatica",
    "nameLatin": "Centella asiatica",
    "mandarinName": "积雪草",
    "nameMandarin": "积雪草",
    "mandarinPinyin": "jī xuě cǎo",
    "pinyin": "jī xuě cǎo",
    "mandarinAudioUrl": None,
    "description": "Herba tahunan tanpa batang tegak yang merayap dengan stolon panjang di tanah lembap, terkenal kaya triterpenoid asiatikosida.",
    "characteristics": "Daun roset berbentuk ginjal atau tapak kuda dengan tepi beringgit halus berurat menjari, tangkai daun panjang berongga, bunga kecil putih-kemerahan.",
    "commonUses": "Mendukung daya ingat dan fokus (tonik neuroprotektif), membantu regenerasi jaringan kulit dan luka, antioksidan alami, serta seduhan teh penyejuk tubuh.",
    "cultivationNotes": "Cocok pada tanah liat berpasir lembap kaya organik, tahan naungan teduh hingga parsial, diperbanyak dengan pemisahan ruas stolon berakar.",
    "benefits": "Mendukung daya ingat dan fokus (tonik neuroprotektif), membantu regenerasi jaringan kulit dan luka, antioksidan alami, serta seduhan teh penyejuk tubuh.",
    "plantingGuide": "Cocok pada tanah liat berpasir lembap kaya organik, tahan naungan teduh hingga parsial, diperbanyak dengan pemisahan ruas stolon berakar.",
    "primaryPhotoUrl": "/data/user/0/id.bubakangreen.app/files/photos/plant_pegagan_gallery.jpg",
    "defaultPhotoUrl": "/data/user/0/id.bubakangreen.app/files/photos/plant_pegagan_gallery.jpg",
    "imageSourceType": "LOCAL",
    "imageAssetName": None,
    "imageSource": "https://upload.wikimedia.org/wikipedia/commons/3/33/Centella_asiatica_6121.jpg",
    "imageLicense": "CC BY-SA 4.0",
    "imageAuthor": "Vengolis",
    "sourceReferences": "Royal Botanic Gardens, Kew (POWO: urn:lsid:ipni.org:names:840003-1); Flora of China Vol. 14; Farmakope Herbal Indonesia Edisi II",
    "isPublished": True,
    "createdAt": now_ms,
    "updatedAt": now_ms
}
fs_save_doc("pegagan", pegagan_data)

# 3. KEMANGI (URL / Image Import Scenario)
kemangi_data = {
    "id": "kemangi",
    "name": "Kemangi",
    "nameId": "Kemangi",
    "scientificName": "Ocimum basilicum var. anisatum",
    "nameLatin": "Ocimum basilicum var. anisatum",
    "mandarinName": "罗勒",
    "nameMandarin": "罗勒",
    "mandarinPinyin": "luó lè",
    "pinyin": "luó lè",
    "mandarinAudioUrl": None,
    "description": "Perdu semusim aromatik dengan wangi sitrun khas adas yang sangat segar, menjadi lalapan favorit nusantara dan pelengkap aneka masakan.",
    "characteristics": "Batang herba bercabang banyak berbulu halus, daun berhadapan bulat telur meruncing berwarna hijau muda, bunga majemuk bertingkat di ujung tandan.",
    "commonUses": "Lalapan segar, penyedap kuliner peredam amis masakan, stimulan pencernaan alami, karminatif pereda kembung, serta penyegar aroma napas.",
    "cultivationNotes": "Membutuhkan paparan sinar matahari penuh (6-8 jam), tanah gembur berpasir kaya kompos dengan drainase lancar, pangkas pucuk bunga untuk memperlebat daun.",
    "benefits": "Lalapan segar, penyedap kuliner peredam amis masakan, stimulan pencernaan alami, karminatif pereda kembung, serta penyegar aroma napas.",
    "plantingGuide": "Membutuhkan paparan sinar matahari penuh (6-8 jam), tanah gembur berpasir kaya kompos dengan drainase lancar, pangkas pucuk bunga untuk memperlebat daun.",
    "primaryPhotoUrl": "https://upload.wikimedia.org/wikipedia/commons/1/1e/Ocimum_basilicum_CG_NBG_LR.jpg",
    "defaultPhotoUrl": "https://upload.wikimedia.org/wikipedia/commons/1/1e/Ocimum_basilicum_CG_NBG_LR.jpg",
    "imageSourceType": "REMOTE_URL",
    "imageAssetName": None,
    "imageSource": "https://upload.wikimedia.org/wikipedia/commons/1/1e/Ocimum_basilicum_CG_NBG_LR.jpg",
    "imageLicense": "CC BY-SA 4.0",
    "imageAuthor": "Wikimedia Commons",
    "sourceReferences": "Royal Botanic Gardens, Kew (POWO: urn:lsid:ipni.org:names:452899-1); Flora of China Vol. 17; Balitsa Kementan",
    "isPublished": True,
    "createdAt": now_ms,
    "updatedAt": now_ms
}
fs_save_doc("kemangi", kemangi_data)

print("\n==================================================")
print("5. EXECUTING PHOTO REPLACEMENT FOR CABAI & LIDAH BUAYA")
print("==================================================")

# Update Cabai photo (Gallery replacement)
cabai_url = f"{BASE_FS_URL}/cabai"
req = urllib.request.Request(cabai_url, headers={'Authorization': f'Bearer {id_token}'})
with urllib.request.urlopen(req) as resp:
    cabai_existing = json.loads(resp.read().decode('utf-8'))['fields']

cabai_update = {
    k: list(v.values())[0] if v and list(v.values())[0] is not None else None
    for k, v in cabai_existing.items()
}
cabai_update["primaryPhotoUrl"] = "/data/user/0/id.bubakangreen.app/files/photos/plant_cabai_gallery.jpg"
cabai_update["defaultPhotoUrl"] = "/data/user/0/id.bubakangreen.app/files/photos/plant_cabai_gallery.jpg"
cabai_update["imageSourceType"] = "LOCAL"
cabai_update["updatedAt"] = now_ms
fs_save_doc("cabai", cabai_update)

# Update Lidah Buaya photo (Camera replacement)
lb_url = f"{BASE_FS_URL}/lidah_buaya"
req = urllib.request.Request(lb_url, headers={'Authorization': f'Bearer {id_token}'})
with urllib.request.urlopen(req) as resp:
    lb_existing = json.loads(resp.read().decode('utf-8'))['fields']

lb_update = {
    k: list(v.values())[0] if v and list(v.values())[0] is not None else None
    for k, v in lb_existing.items()
}
lb_update["primaryPhotoUrl"] = "/data/user/0/id.bubakangreen.app/files/photos/plant_lidah_buaya_camera.jpg"
lb_update["defaultPhotoUrl"] = "/data/user/0/id.bubakangreen.app/files/photos/plant_lidah_buaya_camera.jpg"
lb_update["imageSourceType"] = "LOCAL"
lb_update["updatedAt"] = now_ms
fs_save_doc("lidah_buaya", lb_update)

print("\n==================================================")
print("6. VERIFYING FINAL FIRESTORE CATALOG STATE")
print("==================================================")

fs_req = urllib.request.Request(BASE_FS_URL, headers={'Authorization': f'Bearer {id_token}'})
with urllib.request.urlopen(fs_req) as resp:
    fs_data = json.loads(resp.read().decode('utf-8'))
    docs = fs_data.get('documents', [])
    print(f"Total Master Plants in Firestore: {len(docs)}")
    for d in sorted(docs, key=lambda x: x['name']):
        fields = d.get('fields', {})
        doc_id = d['name'].split('/')[-1]
        name = fields.get('nameId', {}).get('stringValue', '')
        latin = fields.get('nameLatin', {}).get('stringValue', '')
        mandarin = fields.get('nameMandarin', {}).get('stringValue', 'null')
        photo = fields.get('primaryPhotoUrl', {}).get('stringValue', '')
        print(f"  * [{doc_id:12}] {name:15} | Latin: {latin:30} | Hanzi: {mandarin:8} | Photo: {photo}")

print("\nAll database & Firestore operations completed successfully.")
