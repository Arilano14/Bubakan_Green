import urllib.request
import io
from PIL import Image
import os

headers = {
    'User-Agent': 'BubakanGreen/1.0 (Android botanical educational catalog; contact: admin@bubakangreen.id)'
}

plants = {
    'plant_kemangi': 'https://upload.wikimedia.org/wikipedia/commons/9/97/Kemangi.jpg',
    'plant_pegagan': 'https://upload.wikimedia.org/wikipedia/commons/3/3e/Centella_Asiatica.jpg',
    'plant_sirih': 'https://upload.wikimedia.org/wikipedia/commons/d/d4/Betel_leaf_plant_%28Piper_betle%29.jpg'
}

dest_dirs = [
    os.path.abspath(r'app\src\main\res\drawable-nodpi'),
    os.path.abspath(r'web\public\assets\plants')
]

for plant_key, url in plants.items():
    print(f"Downloading {plant_key} from {url}...")
    req = urllib.request.Request(url, headers=headers)
    with urllib.request.urlopen(req, timeout=15) as resp:
        data = resp.read()
    
    img = Image.open(io.BytesIO(data))
    if img.mode in ('RGBA', 'LA') or (img.mode == 'P' and 'transparency' in img.info):
        img = img.convert('RGBA')
    else:
        img = img.convert('RGB')
    
    # Resize keeping aspect ratio, max 1024 on longest edge
    max_edge = 1024
    w, h = img.size
    if max(w, h) > max_edge:
        scale = max_edge / max(w, h)
        img = img.resize((int(w * scale), int(h * scale)), Image.Resampling.LANCZOS)
    
    for dest_dir in dest_dirs:
        os.makedirs(dest_dir, exist_ok=True)
        out_path = os.path.join(dest_dir, f"{plant_key}.webp")
        img.save(out_path, format='WEBP', quality=85)
        print(f"Saved: {out_path} ({os.path.getsize(out_path)} bytes)")

print("All extra plant assets successfully prepared!")
