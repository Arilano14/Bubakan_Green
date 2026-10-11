#!/usr/bin/env python3
"""
scripts/seed_actual_lahan.py
Seeds 3 actual agricultural land plots, new MasterPlant species, and LocationPlant relationships
into the live Firebase/Firestore database for Bubakan Green.
Includes full pre-audit, idempotent write, and read-back verification.
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

# Botanical data definitions for 20 new species
NEW_MASTER_PLANTS = [
    # RW 3 species
    {
        'id': 'durian',
        'name': 'Durian',
        'nameId': 'Durian',
        'scientificName': 'Durio zibethinus',
        'nameLatin': 'Durio zibethinus',
        'description': 'Pohon buah tropis penghasil buah berkulit duri tajam dengan aroma khas dan daging buah bertekstur lembut manis.',
        'characteristics': 'Pohon besar berkayu keras, daun lonjong berseling dengan permukaan bawah berwarna tembaga keemasan, buah berkayu berduri tajam.',
        'commonUses': 'Buah segar bernilai ekonomi tinggi, sumber energi, serat pangan, kalium, dan vitamin C.',
        'cultivationNotes': 'Tumbuh optimal di tanah lempung berpasir subur kaya organik, drainase lancar, paparan sinar matahari penuh, perbanyakan vegetatif bibit okulasi.',
        'sourceReferences': 'Flora Malesiana; Royal Botanic Gardens, Kew (POWO)'
    },
    {
        'id': 'ciplukan',
        'name': 'Ciplukan',
        'nameId': 'Ciplukan',
        'scientificName': 'Physalis angulata',
        'nameLatin': 'Physalis angulata',
        'description': 'Tanaman herbal semusim penghasil buah buni yang terbungkus kelopak menggelembung menyerupai lampion.',
        'characteristics': 'Batang basah bersegi tumpul berongga, daun bulat telur bertepi berlekuk dangkal, buah bulat kecil berwarna kekuningan di dalam kelopak menggelembung.',
        'commonUses': 'Herbal obat tradisional untuk membantu meredakan inflamasi, batuk, dan mendukung metabolisme tubuh.',
        'cultivationNotes': 'Mudah tumbuh di pekarangan dan polybag dengan tanah gembur cukup lembap serta penyiraman teratur.',
        'sourceReferences': 'Flora of China; Farmakope Herbal Indonesia'
    },
    {
        'id': 'kitolot',
        'name': 'Kitolot',
        'nameId': 'Kitolot',
        'scientificName': 'Hippobroma longiflora',
        'nameLatin': 'Hippobroma longiflora',
        'description': 'Tanaman herba liar berkhasiat obat dengan bunga putih panjang berbentuk bintang yang umum dimanfaatkan getah encernya.',
        'characteristics': 'Herba tegak bergetah putih dengan daun lanset bergerigi tajam, bunga tunggal berwarna putih bersih dengan tabung mahkota panjang.',
        'commonUses': 'Secara tradisional air perasan bunganya digunakan untuk membantu menyegarkan mata lelah dan mengatasi radang tenggorokan.',
        'cultivationNotes': 'Tumbuh di tempat lembap dan teduh, tanah berhumus gembur, mudah diperbanyak dengan biji atau stek.',
        'sourceReferences': 'POWO; Atlas Tumbuhan Obat Indonesia'
    },
    # RW 1 species
    {
        'id': 'seledri',
        'name': 'Seledri',
        'nameId': 'Seledri',
        'scientificName': 'Apium graveolens',
        'nameLatin': 'Apium graveolens',
        'description': 'Sayuran daun aromatik beraroma segar khas apiin yang umum digunakan sebagai bumbu tabur dan herba penurunan tekanan darah.',
        'characteristics': 'Herba semusim roset berbatang basah beralur memanjang, daun majemuk menyirip bertepi beringgit tajam beraroma tajam harum.',
        'commonUses': 'Bumbu dapur sup dan soto, diuretik alami, serta membantu menjaga kestabilan tekanan darah.',
        'cultivationNotes': 'Menyukai tanah gembur kaya humus, naungan ringan, dan penyiraman konsisten tanpa air tergenang.',
        'sourceReferences': 'Royal Botanic Gardens, Kew (POWO); Flora of China'
    },
    {
        'id': 'kucai',
        'name': 'Kucai',
        'nameId': 'Kucai',
        'scientificName': 'Allium tuberosum',
        'nameLatin': 'Allium tuberosum',
        'description': 'Tanaman rumpun keluarga bawang dengan daun pipih hijau beraroma khas gurih sedap.',
        'characteristics': 'Rumpun daun pipih padat berwarna hijau tua, tidak berongga seperti daun bawang, dengan bunga putih mungil berbentuk payung.',
        'commonUses': 'Bumbu aromatik masakan tumis, isi lumpia/pastel, sumber antioksidan allisin dan serat pangan.',
        'cultivationNotes': 'Tumbuh subur di tanah gembur dalam polybag atau bedengan, diperbanyak dengan memisahkan rumpun anakan.',
        'sourceReferences': 'Flora of China; USDA GRIN'
    },
    {
        'id': 'pakcoy',
        'name': 'Pakcoy',
        'nameId': 'Pakcoy',
        'scientificName': 'Brassica rapa subsp. chinensis',
        'nameLatin': 'Brassica rapa subsp. chinensis',
        'description': 'Sayuran sawi sendok berdaging tangkai daun tebal renyah dan cepat panen, primadona budidaya hidroponik dan kebun sayur pekarangan.',
        'characteristics': 'Roset daun tersusun rapat dengan tangkai daun lebar berdaging sendok putih kehijauan dan helaian daun bulat halus.',
        'commonUses': 'Bahan tumisan harian, sayur kuah, kaya vitamin A, C, dan K serta mineral untuk daya tahan tubuh.',
        'cultivationNotes': 'Semai benih pada media gembur berhumus, pindahkan saat berdaun 3, siram teratur, dan dapat dipanen dalam 30–40 hari.',
        'sourceReferences': 'Royal Botanic Gardens, Kew (POWO); USDA'
    },
    {
        'id': 'daun_bawang',
        'name': 'Daun Bawang',
        'nameId': 'Daun Bawang',
        'scientificName': 'Allium fistulosum',
        'nameLatin': 'Allium fistulosum',
        'description': 'Tanaman sayur bumbu berdaun silinder berongga dengan aroma khas allisin yang menyedapkan masakan.',
        'characteristics': 'Batang semu tersusun dari pelepah daun berongga silindris hijau dengan pangkal putih berlapis, berakar serabut padat.',
        'commonUses': 'Bumbu wajib martabak, sup, aneka tumisan, serta mengandung antioksidan dan antimikroba alami.',
        'cultivationNotes': 'Tanam anakan atau semai biji di tanah gembur berpasir kaya bahan organik, siram teratur 1–2 kali sehari.',
        'sourceReferences': 'Royal Botanic Gardens, Kew (POWO); Flora of China'
    },
    {
        'id': 'caisim',
        'name': 'Caisim',
        'nameId': 'Caisim',
        'scientificName': 'Brassica rapa subsp. parachinensis',
        'nameLatin': 'Brassica rapa subsp. parachinensis',
        'description': 'Sawi hijau berbatang renyah dan berdaun lonjong yang menjadi sayuran daun paling populer dalam kuliner nusantara.',
        'characteristics': 'Batang tegak langsing tidak bersendok tebal seperti pakcoy, helaian daun lonjong tipis berurat halus kehijauan.',
        'commonUses': 'Pelengkap mie ayam, bakso, tumisan harian, kaya serat nabati, zat besi, dan vitamin A.',
        'cultivationNotes': 'Tumbuh cepat di tanah remah gembur dengan penyiraman cukup, panen sekitar 25–35 hari setelah semai.',
        'sourceReferences': 'Royal Botanic Gardens, Kew (POWO)'
    },
    {
        'id': 'talas',
        'name': 'Talas',
        'nameId': 'Talas',
        'scientificName': 'Colocasia esculenta',
        'nameLatin': 'Colocasia esculenta',
        'description': 'Tanaman herba berumbi berpati tinggi dengan daun lebar berbentuk hati/tameng yang populer sebagai komoditas pangan mandiri.',
        'characteristics': 'Tangkai daun tegak panjang keluar dari umbi pangkal, helaian daun lebar menyerupai tameng pelindung air (efek lotus).',
        'commonUses': 'Sumber karbohidrat kompleks pengganti nasi, rendah indeks glikemik, kaya kalium dan serat pangan.',
        'cultivationNotes': 'Menyukai lahan lembap gembur kaya kompos, tanam umbi bertunas sedalam 10 cm, jaga kelembapan tanah.',
        'sourceReferences': 'Royal Botanic Gardens, Kew (POWO); PROSEA'
    },
    {
        'id': 'lengkuas',
        'name': 'Lengkuas',
        'nameId': 'Lengkuas',
        'scientificName': 'Alpinia galanga',
        'nameLatin': 'Alpinia galanga',
        'description': 'Tanaman rimpang temu-temuan beraroma rempah segar tajam berserat keras yang menjadi bumbu dasar masakan dan obat tradisional.',
        'characteristics': 'Batang semu tegak kokoh hingga 2 meter, rimpang tebal merayap keras berwarna kemerahan atau putih dengan aroma galangol khas.',
        'commonUses': 'Bumbu penyedap rendang, soto, gulai, serta herbal anti jamur, antiseptik, dan penghangat saluran cerna.',
        'cultivationNotes': 'Tanam rimpang berumur tua pada tanah gembur kaya kompos, tahan sinar matahari penuh, panen rimpang 10–12 bulan.',
        'sourceReferences': 'Royal Botanic Gardens, Kew (POWO); WHO Monographs'
    },
    {
        'id': 'pepaya',
        'name': 'Pepaya',
        'nameId': 'Pepaya',
        'scientificName': 'Carica papaya',
        'nameLatin': 'Carica papaya',
        'description': 'Pohon herba cepat tumbuh penghasil buah manis berair kaya enzim papain dan vitamin A serta C.',
        'characteristics': 'Batang silindris tunggal tanpa cabang dengan bekas tangkai daun berongga, daun menjari bertangkai panjang, buah buni bulat lonjong.',
        'commonUses': 'Konsumsi buah segar, daun muda untuk sayur lalap/buntil, melancarkan pencernaan berkat enzim papain.',
        'cultivationNotes': 'Tanam bibit di tanah gembur subur dengan drainase sangat lancar (tidak tahan air menggenang), paparan sinar matahari penuh.',
        'sourceReferences': 'Royal Botanic Gardens, Kew (POWO); IPNI'
    },
    {
        'id': 'bayam',
        'name': 'Bayam',
        'nameId': 'Bayam',
        'scientificName': 'Amaranthus sp.',
        'nameLatin': 'Amaranthus sp.',
        'description': 'Sayuran daun hijau semusim yang sangat cepat tumbuh dan kaya zat besi untuk pemenuhan gizi keluarga.',
        'characteristics': 'Batang basah lunak kehijauan atau kemerahan, daun bulat telur tipis lembut, bunga tersusun dalam bulir terminal rapat.',
        'commonUses': 'Sayur bening, peyek bayam, sumber zat besi, kalsium, folat, dan vitamin A untuk pencegahan anemia.',
        'cultivationNotes': 'Tabur benih merata pada bedengan halus, siram lembut setiap hari, panen cabut dalam 20–25 hari.',
        'sourceReferences': 'Flora of North America; Royal Botanic Gardens, Kew (POWO)'
    },
    {
        'id': 'kacang_panjang',
        'name': 'Kacang Panjang',
        'nameId': 'Kacang Panjang',
        'scientificName': 'Vigna unguiculata subsp. sesquipedalis',
        'nameLatin': 'Vigna unguiculata subsp. sesquipedalis',
        'description': 'Tanaman polong merambat dengan buah polong silindris panjang yang produktif untuk urban farming pekarangan.',
        'characteristics': 'Batang melilit membutuhkan tiang lanjaran, daun majemuk beranak daun tiga, bunga ungu/putih, polong gantung panjang mencapai 40–80 cm.',
        'commonUses': 'Sayur lodeh, tumisan, lalap segar mentah/rebus, sumber protein nabati, vitamin B kompleks, dan serat.',
        'cultivationNotes': 'Tancapkan tiang bambu lanjaran setinggi 1.5–2 meter setelah benih tumbuh, siram berkala dan beri pupuk kandang.',
        'sourceReferences': 'Royal Botanic Gardens, Kew (POWO); USDA GRIN'
    },
    {
        'id': 'daun_sintrong',
        'name': 'Daun Sintrong',
        'nameId': 'Daun Sintrong',
        'scientificName': 'Crassocephalum crepidioides',
        'nameLatin': 'Crassocephalum crepidioides',
        'description': 'Tumbuhan herba liar beraroma wangi lembut yang pucuk daun mudanya digemari sebagai lalapan dan sayuran segar berkhasiat.',
        'characteristics': 'Batang lunak beralur tegak hingga 1 meter, daun berlekuk menyirip beraroma aromatik menyenangkan, bunga bongkol menggelayut berwarna jingga kecokelatan.',
        'commonUses': 'Lalapan mentah atau kukus, penambah nafsu makan, serta pereda radang dan sakit perut herbal.',
        'cultivationNotes': 'Sangat adaptif dan mudah tumbuh di tanah lembap berhumus dengan biji berbulu parasut yang mudah disemai.',
        'sourceReferences': 'Flora Malesiana; Royal Botanic Gardens, Kew (POWO)'
    },
    {
        'id': 'kumis_kucing',
        'name': 'Kumis Kucing',
        'nameId': 'Kumis Kucing',
        'scientificName': 'Orthosiphon aristatus',
        'nameLatin': 'Orthosiphon aristatus',
        'description': 'Tanaman obat keluarga legendaris nusantara dengan bunga putih berbenang sari panjang menjuntai menyerupai kumis kucing.',
        'characteristics': 'Batang berkayu semak segi empat tumpul, daun bulat telur bergerigi tepi, bunga putih atau biru keunguan dengan benang sari melengkung panjang keluar.',
        'commonUses': 'Diuretik peluruh kencing alami, membantu melarutkan batu ginjal dan asam urat, serta antiinflamasi saluran kemih.',
        'cultivationNotes': 'Tumbuh baik pada tanah gembur kaya humus dengan penyiraman teratur dan penyinaran matahari pagi hingga sedang.',
        'sourceReferences': 'Farmakope Herbal Indonesia; Royal Botanic Gardens, Kew (POWO)'
    },
    {
        'id': 'bunga_kenanga',
        'name': 'Bunga Kenanga',
        'nameId': 'Bunga Kenanga',
        'scientificName': 'Cananga odorata',
        'nameLatin': 'Cananga odorata',
        'description': 'Pohon penghasil bunga harum legendaris dengan mahkota bunga menjuntai kuning kehijauan yang kaya minyak atsiri kenanga.',
        'characteristics': 'Pohon berkayu keras, daun bulat telur bergelombang, bunga bermahkota 6 helai sempit melengkung berwarna hijau lalu kuning wangi semerbak.',
        'commonUses': 'Bahan upacara adat tradisional, aromaterapi relaksasi penenang saraf, serta ramuan minyak wangi alami.',
        'cultivationNotes': 'Tanam bibit cangkok atau biji di tanah berdrainase baik dengan sinar matahari penuh.',
        'sourceReferences': 'Flora Malesiana; Royal Botanic Gardens, Kew (POWO)'
    },
    {
        'id': 'bunga_bugang',
        'name': 'Bunga Bugang',
        'nameId': 'Bunga Bugang',
        'scientificName': 'Clerodendrum calamitosum',
        'nameLatin': 'Clerodendrum calamitosum',
        'description': 'Tanaman perdu obat perdu dengan bunga putih mungil bertabung panjang yang secara etnomedisin digunakan untuk peluruh kencing.',
        'characteristics': 'Perdu tegak dengan batang berkayu bulat, daun berhadapan bulat telur bertepi beringgit atau bergigi kasar, bunga putih bergerombol di ujung ranting.',
        'commonUses': 'Secara tradisional air rebusan daunnya digunakan untuk peluruh batu ginjal, demam, dan radang saluran kemih.',
        'cultivationNotes': 'Tumbuh di tanah lembap berdrainase lancar, toleran terhadap naungan sebagian, perbanyakan melalui stek batang.',
        'sourceReferences': 'Flora of Java; POWO'
    },
    {
        'id': 'pandan',
        'name': 'Pandan',
        'nameId': 'Pandan',
        'scientificName': 'Pandanus amaryllifolius',
        'nameLatin': 'Pandanus amaryllifolius',
        'description': 'Tanaman rumpun daun aromatik wangi beraroma 2-asetil-1-pirolin yang esensial dalam kuliner dan tradisi nusantara.',
        'characteristics': 'Rumpun herba dengan daun berbentuk pita pipih memanjang hijau mengilap tanpa duri tepi yang tajam, berakar tunjang tebal.',
        'commonUses': 'Pewangi dan pewarna hijau alami kue dan masakan, seduhan aromaterapi penurun stres dan gula darah.',
        'cultivationNotes': 'Tumbuh subur di tanah lembap kaya organik di bawah naungan semi-teduh, diperbanyak dengan anakan bertunas.',
        'sourceReferences': 'Flora Malesiana; Royal Botanic Gardens, Kew (POWO)'
    },
    {
        'id': 'mahoni',
        'name': 'Mahoni',
        'nameId': 'Mahoni',
        'scientificName': 'Swietenia mahagoni',
        'nameLatin': 'Swietenia mahagoni',
        'description': 'Pohon kayu keras peneduh lingkungan yang biji buahnya terkenal berasa sangat pahit dan dimanfaatkan sebagai jamu herbal.',
        'characteristics': 'Pohon tinggi berkayu keras dengan daun majemuk menyirip berseling genap, buah bulat telur berkayu tebal memecah saat matang melepas biji bersayap.',
        'commonUses': 'Peneduh kawasan konservasi, kayu pertukangan bernilai tinggi, dan biji pahitnya digunakan secara empiris untuk membantu menurunkan gula darah.',
        'cultivationNotes': 'Tanam bibit pohon pada lahan terbuka berdrainase baik dengan sinar matahari penuh untuk fungsi peneduh dan konservasi.',
        'sourceReferences': 'Flora of the West Indies; POWO'
    },
    {
        'id': 'cepokak',
        'name': 'Cepokak',
        'nameId': 'Cepokak',
        'scientificName': 'Solanum torvum',
        'nameLatin': 'Solanum torvum',
        'description': 'Semak berduri anggota Solanaceae penghasil buah buni bulat kecil bergerombol (dikenal juga sebagai Takokak) yang kaya antioksidan.',
        'characteristics': 'Perdu bercabang lebat berbatang berduri tumpul, daun berlekuk berbulu halus, bunga putih bintang, buah bulat hijau kekuningan berdiameter 1 cm bergerombol. Alias: Takokak.',
        'commonUses': 'Lalapan sambal terong pipit/takokak, sayur tauco, sumber antioksidan, serta dipercaya membantu menjaga kesehatan mata dan peredaran darah.',
        'cultivationNotes': 'Sangat adaptif di lahan kering maupun pekarangan terbuka dengan sinar matahari penuh, mudah tumbuh dari biji buah matang.',
        'sourceReferences': 'Royal Botanic Gardens, Kew (POWO); Flora of China'
    }
]

def main():
    print("=" * 80)
    print("BUBAKAN GREEN: FIRESTORE SEEDING & VERIFICATION")
    print(f"Project ID: {project_id}")
    print("=" * 80)

    # 1. AUDIT CURRENT STATE
    print("\n--- PHASE 1: PRE-SEED AUDIT ---")
    current_locations = firestore_get('locations').get('documents', [])
    current_loc_ids = [d['name'].split('/')[-1] for d in current_locations]
    print(f"Current locations ({len(current_loc_ids)}): {current_loc_ids}")

    current_master = firestore_get('master_plants').get('documents', [])
    current_master_ids = [d['name'].split('/')[-1] for d in current_master]
    print(f"Current master_plants ({len(current_master_ids)}): {current_master_ids}")

    current_lp = firestore_get('location_plants').get('documents', [])
    current_lp_ids = [d['name'].split('/')[-1] for d in current_lp]
    print(f"Current location_plants ({len(current_lp_ids)}): {current_lp_ids}")

    # 2. WRITE MASTER PLANTS (Idempotent)
    print("\n--- PHASE 2: WRITING NEW MASTER PLANTS ---")
    created_mp_count = 0
    now_ms = int(time.time() * 1000)

    for p in NEW_MASTER_PLANTS:
        pid = p['id']
        fields = {
            'id': {'stringValue': pid},
            'name': {'stringValue': p['name']},
            'nameId': {'stringValue': p['nameId']},
            'scientificName': {'stringValue': p['scientificName']},
            'nameLatin': {'stringValue': p['nameLatin']},
            'description': {'stringValue': p['description']},
            'characteristics': {'stringValue': p['characteristics']},
            'commonUses': {'stringValue': p['commonUses']},
            'benefits': {'stringValue': p['commonUses']},
            'cultivationNotes': {'stringValue': p['cultivationNotes']},
            'plantingGuide': {'stringValue': p['cultivationNotes']},
            'sourceReferences': {'stringValue': p['sourceReferences']},
            'imageSourceType': {'stringValue': 'LOCAL'},
            'profileCompleteness': {'stringValue': 'COMPLETE'},
            'isPublished': {'booleanValue': True},
            'createdAt': {'integerValue': str(now_ms)},
            'updatedAt': {'integerValue': str(now_ms)}
        }
        
        # Patch/Put document
        firestore_patch('master_plants', pid, fields, list(fields.keys()))
        created_mp_count += 1
        print(f"  [OK] MasterPlant saved: {pid:15} | {p['name']:15} | {p['scientificName']}")

    # 3. WRITE / UPDATE LOCATIONS
    print("\n--- PHASE 3: WRITING / UPDATING LOCATIONS ---")

    # Lahan 1: Urban Farming RW 3 (NEW)
    loc1_id = 'loc_urban_farming_rw03'
    loc1_fields = {
        'id': {'stringValue': loc1_id},
        'name': {'stringValue': 'Urban Farming RW 3'},
        'type': {'stringValue': 'URBAN_FARMING'},
        'rw': {'stringValue': '03'},
        'regionTag': {'stringValue': 'RW_03'},
        'address': {'stringValue': 'Lingkungan RW 03, Kelurahan Bubakan, Mijen'},
        'description': {'stringValue': 'Sentra budidaya pertanian mandiri dan tanaman pangan warga RW 03 Kelurahan Bubakan.'},
        'latitude': {'doubleValue': -7.092000},
        'longitude': {'doubleValue': 110.324806},
        'coordinatesStatus': {'stringValue': 'VERIFIED'},
        'featured': {'booleanValue': False},
        'photos': {'arrayValue': {'values': []}},
        'conditionNote': {'stringValue': 'Lahan produktif terawat.'},
        'isPublished': {'booleanValue': True},
        'status': {'stringValue': 'ACTIVE'},
        'createdBy': {'stringValue': 'SYSTEM_SEED'},
        'picUid': {'stringValue': 'SYSTEM_SEED'},
        'createdAt': {'integerValue': str(now_ms)},
        'updatedAt': {'integerValue': str(now_ms)}
    }
    firestore_patch('locations', loc1_id, loc1_fields, list(loc1_fields.keys()))
    print(f"  [OK] Lahan 1 saved: {loc1_id} (Urban Farming RW 3, -7.092000, 110.324806)")

    # Lahan 2: Taman Toga RW 3 (UPDATE EXISTING loc_taman_toga_rw03)
    loc2_id = 'loc_taman_toga_rw03'
    loc2_updates = {
        'name': {'stringValue': 'Taman Toga RW 3'},
        'latitude': {'doubleValue': -7.091944},
        'longitude': {'doubleValue': 110.325667},
        'coordinatesStatus': {'stringValue': 'VERIFIED'},
        'description': {'stringValue': 'Taman tanaman obat keluarga warga RW 03 yang membudidayakan ragam tanaman herbal tradisional. Kolaborasi dengan Mahasiswa KKN.'},
        'conditionNote': {'stringValue': 'Koleksi tanaman obat keluarga terpelihara dengan baik. Kolaborasi dengan Mahasiswa KKN.'},
        'updatedAt': {'integerValue': str(now_ms)}
    }
    firestore_patch('locations', loc2_id, loc2_updates, list(loc2_updates.keys()))
    print(f"  [OK] Lahan 2 updated: {loc2_id} (Taman Toga RW 3, -7.091944, 110.325667, Kolaborasi KKN)")

    # Lahan 3: Urban Farming dan Taman Toga RW 1 (NEW with Photos)
    loc3_id = 'loc_urban_farming_toga_rw01'
    loc3_photos = [
        '/assets/Lahan/RW 1_1.jpeg',
        '/assets/Lahan/RW 1_2.jpeg',
        '/assets/Lahan/RW 1_3.jpeg'
    ]
    loc3_fields = {
        'id': {'stringValue': loc3_id},
        'name': {'stringValue': 'Urban Farming dan Taman Toga RW 1'},
        'type': {'stringValue': 'URBAN_FARMING'},
        'rw': {'stringValue': '01'},
        'regionTag': {'stringValue': 'RW_01'},
        'address': {'stringValue': 'Lingkungan RW 01, Kelurahan Bubakan, Mijen'},
        'description': {'stringValue': 'Kawasan budidaya tanaman pangan dan tanaman obat keluarga (TOGA) warga RW 01 Kelurahan Bubakan.'},
        'latitude': {'doubleValue': -7.092056},
        'longitude': {'doubleValue': 110.319528},
        'coordinatesStatus': {'stringValue': 'VERIFIED'},
        'featured': {'booleanValue': True},
        'photos': {'arrayValue': {'values': [{'stringValue': p} for p in loc3_photos]}},
        'coverPhotoUrl': {'stringValue': loc3_photos[0]},
        'photoUrl': {'stringValue': loc3_photos[0]},
        'conditionNote': {'stringValue': 'Lahan percontohan urban farming dan toga aktif dengan pintu gerbang utama RW 01.'},
        'isPublished': {'booleanValue': True},
        'status': {'stringValue': 'ACTIVE'},
        'createdBy': {'stringValue': 'SYSTEM_SEED'},
        'picUid': {'stringValue': 'SYSTEM_SEED'},
        'createdAt': {'integerValue': str(now_ms)},
        'updatedAt': {'integerValue': str(now_ms)}
    }
    firestore_patch('locations', loc3_id, loc3_fields, list(loc3_fields.keys()))
    print(f"  [OK] Lahan 3 saved: {loc3_id} (Urban Farming dan Taman Toga RW 1, -7.092056, 110.319528, 3 Photos)")

    # 4. WRITE LOCATION_PLANTS RELATIONS
    print("\n--- PHASE 4: WRITING LOCATION_PLANTS RELATIONS ---")
    
    relations_to_seed = []
    
    # Lahan 1 relations: Durian, Jahe
    for pid in ['durian', 'jahe']:
        relations_to_seed.append((loc1_id, pid))
        
    # Lahan 2 relations: Default already present (sereh, jahe, kencur, kunyit, lidah_buaya).
    # Add new: ciplukan, kitolot. (lidah_buaya is NOT re-added to prevent duplication).
    for pid in ['ciplukan', 'kitolot']:
        relations_to_seed.append((loc2_id, pid))
        
    # Lahan 3 relations: 25 plants (24 field entries with CabaTomat -> cabai & tomat)
    rw1_plants = [
        'seledri', 'kucai', 'pakcoy', 'daun_bawang', 'cabai', 'tomat', 'caisim',
        'talas', 'terong', 'jahe', 'lengkuas', 'kunyit', 'sereh', 'pepaya',
        'bayam', 'kacang_panjang', 'daun_sintrong', 'kumis_kucing', 'pegagan',
        'bunga_kenanga', 'bunga_bugang', 'kemangi', 'pandan', 'mahoni', 'cepokak'
    ]
    for pid in rw1_plants:
        relations_to_seed.append((loc3_id, pid))

    created_rel_count = 0
    for loc_id, plant_id in relations_to_seed:
        rel_id = f"{loc_id}_{plant_id}"
        rel_fields = {
            'id': {'stringValue': rel_id},
            'locationId': {'stringValue': loc_id},
            'plantId': {'stringValue': plant_id},
            'masterPlantId': {'stringValue': plant_id},
            'condition': {'stringValue': 'GOOD'},
            'quantity': {'integerValue': '1'},
            'quantityNote': {'stringValue': '1 polybag / bedengan'},
            'isPresent': {'booleanValue': True},
            'featuredForQr': {'booleanValue': False},
            'status': {'stringValue': 'ACTIVE'},
            'createdBy': {'stringValue': 'SYSTEM_SEED'},
            'updatedBy': {'stringValue': 'SYSTEM_SEED'},
            'createdAt': {'integerValue': str(now_ms)},
            'updatedAt': {'integerValue': str(now_ms)}
        }
        firestore_patch('location_plants', rel_id, rel_fields, list(rel_fields.keys()))
        created_rel_count += 1
        print(f"  [OK] LocationPlant: {rel_id:45} | Loc: {loc_id:28} | Plant: {plant_id}")

    # 5. POST-WRITE READ-BACK & VERIFICATION
    print("\n--- PHASE 5: READ-BACK AUDIT & VERIFICATION ---")

    print("\n[VERIFY 3 LOCATIONS]")
    for lid in [loc1_id, loc2_id, loc3_id]:
        doc = firestore_get('locations', lid)
        f = doc.get('fields', {})
        name = f.get('name', {}).get('stringValue')
        lat = f.get('latitude', {}).get('doubleValue')
        lng = f.get('longitude', {}).get('doubleValue')
        rw = f.get('rw', {}).get('stringValue')
        ltype = f.get('type', {}).get('stringValue')
        photos = [p['stringValue'] for p in f.get('photos', {}).get('arrayValue', {}).get('values', [])]
        desc = f.get('description', {}).get('stringValue')
        print(f"  Doc: {lid}")
        print(f"    Name: {name} | Type: {ltype} | RW: {rw}")
        print(f"    Coordinates: Lat={lat}, Lng={lng}")
        print(f"    Photos ({len(photos)}): {photos}")
        print(f"    Desc: {desc}")

    print("\n[VERIFY RELATIONS PER LAHAN]")
    all_lp = firestore_get('location_plants').get('documents', [])
    for lid in [loc1_id, loc2_id, loc3_id]:
        matching = []
        for d in all_lp:
            f = d.get('fields', {})
            if f.get('locationId', {}).get('stringValue') == lid:
                matching.append(f.get('plantId', {}).get('stringValue'))
        print(f"  Location {lid} has {len(matching)} plants: {sorted(matching)}")

    print("\n[VERIFY TOTAL MASTER PLANTS]")
    all_mp = firestore_get('master_plants').get('documents', [])
    print(f"  Total MasterPlant in Firestore: {len(all_mp)}")
    for d in sorted(all_mp, key=lambda x: x['name'].split('/')[-1]):
        mid = d['name'].split('/')[-1]
        f = d.get('fields', {})
        name = f.get('name', {}).get('stringValue')
        latin = f.get('scientificName', {}).get('stringValue')
        print(f"    {mid:18} | {name:16} | {latin}")

    print("\n==========================================================================")
    print("SEEDING COMPLETED SUCCESSFULLY WITHOUT ERRORS")
    print(f"New MasterPlants created: {created_mp_count}")
    print(f"Locations processed: 3 (2 created, 1 updated)")
    print(f"LocationPlant relations created: {created_rel_count}")
    print("==========================================================================")

if __name__ == '__main__':
    main()
