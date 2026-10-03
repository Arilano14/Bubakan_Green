import urllib.request
import urllib.error
import time

urls = [
    'https://bubakangreen.web.app/',
    'https://bubakangreen.web.app/plant/sereh',
    'https://bubakangreen.web.app/location/LOC_PREVIEW_01',
    'https://bubakangreen.web.app/.well-known/assetlinks.json'
]

print("=== LIVE HOSTING EMPIRICAL PROBE ===")
for url in urls:
    start = time.time()
    try:
        req = urllib.request.Request(url, headers={'User-Agent': 'BubakanGreen-Audit/1.0'})
        with urllib.request.urlopen(req, timeout=10) as resp:
            elapsed = int((time.time() - start) * 1000)
            body = resp.read().decode('utf-8', errors='ignore')
            ct = resp.headers.get("Content-Type", "")
            print(f"URL: {url}")
            print(f"  Status: {resp.status} {resp.reason}")
            print(f"  Latency: {elapsed}ms")
            print(f"  Content-Type: {ct}")
            print(f"  Body (first 100 chars): {repr(body[:100])}")
            print()
    except urllib.error.HTTPError as e:
        elapsed = int((time.time() - start) * 1000)
        body = e.read().decode('utf-8', errors='ignore')
        ct = e.headers.get("Content-Type", "")
        print(f"URL: {url}")
        print(f"  Status: {e.code} {e.reason}")
        print(f"  Latency: {elapsed}ms")
        print(f"  Content-Type: {ct}")
        print(f"  Body (first 100 chars): {repr(body[:100])}")
        print()
    except Exception as e:
        elapsed = int((time.time() - start) * 1000)
        print(f"URL: {url}")
        print(f"  Error: {e}")
        print(f"  Latency: {elapsed}ms")
        print()
