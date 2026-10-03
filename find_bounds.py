import subprocess, os, re

adb = os.path.expandvars(r'%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe')
subprocess.run([adb, 'shell', 'uiautomator', 'dump', '/sdcard/loc_detail_ui.xml'], check=True)
subprocess.run([adb, 'pull', '/sdcard/loc_detail_ui.xml', 'loc_detail_ui.xml'], check=True)
with open('loc_detail_ui.xml', 'r', encoding='utf-8') as f:
    text = f.read()

matches = re.findall(r'<node[^>]*content-desc="([^"]*)"[^>]*bounds="([^"]*)"', text)
for desc, bounds in matches:
    print(f'{desc} -> {bounds}')
