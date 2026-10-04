import urllib.request
import ssl

ctx = ssl.create_default_context()
ctx.check_hostname = False
ctx.verify_mode = ssl.CERT_NONE

url = 'https://dataspasial.semarangkota.go.id/kml/batas_kelurahan.kml'
print('Fetching:', url)
req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
try:
    with urllib.request.urlopen(req, context=ctx, timeout=30) as resp:
        data = resp.read()
        print('Downloaded bytes:', len(data))
        with open('scratch/batas_kelurahan.kml', 'wb') as f:
            f.write(data)
        print('Saved to scratch/batas_kelurahan.kml')
except Exception as e:
    print('Error:', e)
