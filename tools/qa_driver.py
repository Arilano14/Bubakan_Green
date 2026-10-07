import subprocess
import os
import re
import xml.etree.ElementTree as ET
import time
import sys

try:
    sys.stdout.reconfigure(encoding='utf-8')
except Exception:
    pass

ADB = os.path.expandvars(r"%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe")

def adb(cmd, check=True):
    full_cmd = f'"{ADB}" {cmd}'
    res = subprocess.run(full_cmd, shell=True, capture_output=True, text=True)
    if check and res.returncode != 0:
        print(f"Error running: {cmd}\nSTDOUT: {res.stdout}\nSTDERR: {res.stderr}")
    return res.stdout.strip()

def screencap(filename):
    os.makedirs(os.path.dirname(filename) if os.path.dirname(filename) else ".", exist_ok=True)
    subprocess.run(f'"{ADB}" exec-out screencap -p > "{filename}"', shell=True)
    print(f"Screenshot saved: {filename}")

def dump_ui():
    adb("shell uiautomator dump /sdcard/dump.xml")
    adb("pull /sdcard/dump.xml dump.xml")
    if os.path.exists("dump.xml"):
        try:
            tree = ET.parse("dump.xml")
            return tree.getroot()
        except Exception as e:
            print("Error parsing dump.xml:", e)
    return None

def find_elements(root, text_regex=None, desc_regex=None, class_regex=None):
    results = []
    if root is None:
        return results
    for elem in root.iter():
        text = elem.attrib.get('text', '')
        desc = elem.attrib.get('content-desc', '')
        cls = elem.attrib.get('class', '')
        bounds = elem.attrib.get('bounds', '')
        match = False
        if text_regex and re.search(text_regex, text, re.IGNORECASE):
            match = True
        if desc_regex and re.search(desc_regex, desc, re.IGNORECASE):
            match = True
        if class_regex and re.search(class_regex, cls, re.IGNORECASE):
            match = True
        if match:
            m = re.match(r'\[(\d+),(\d+)\]\[(\d+),(\d+)\]', bounds)
            if m:
                x1, y1, x2, y2 = map(int, m.groups())
                cx = (x1 + x2) // 2
                cy = (y1 + y2) // 2
                results.append({
                    'text': text, 
                    'desc': desc, 
                    'class': cls,
                    'bounds': (x1, y1, x2, y2), 
                    'center': (cx, cy), 
                    'elem': elem
                })
    return results

def click(cx, cy, delay=1.0):
    adb(f"shell input tap {cx} {cy}")
    time.sleep(delay)

def click_element(text_regex=None, desc_regex=None, delay=1.0):
    root = dump_ui()
    elems = find_elements(root, text_regex, desc_regex)
    if elems:
        e = elems[0]
        print(f"Clicking '{e['text']}' / '{e['desc']}' at {e['center']}")
        click(e['center'][0], e['center'][1], delay)
        return True
    print(f"Element NOT found: text={text_regex}, desc={desc_regex}")
    return False

def clear_focused_text():
    # Move cursor to end then backspace
    adb("shell input keyevent KEYCODE_MOVE_END")
    # Backspace 60 times
    for _ in range(6):
        adb("shell input keyevent " + " ".join(["67"] * 10))
    time.sleep(0.5)

def type_text(text):
    # For adb shell input text, characters need careful escaping
    # Better: use chunks of alphanumeric/spaces, or adb broadcast / clip
    # Simple characters:
    words = text.split(" ")
    for i, w in enumerate(words):
        if w:
            # escape special chars
            safe_w = re.sub(r'([&|;$><`"\'\(\)\*\?#~=])', r'\\\1', w)
            adb(f"shell input text '{safe_w}'")
        if i < len(words) - 1:
            adb("shell input keyevent 62") # KEYCODE_SPACE
        time.sleep(0.1)

def paste_text(text):
    # Escape for adb shell cmd clipboard
    escaped = text.replace('\\', '\\\\').replace('"', '\\"').replace('$', '\\$').replace('`', '\\`')
    adb(f'shell "cmd clipboard set text \\"{escaped}\\""')
    time.sleep(0.3)
    adb("shell input keyevent 279") # KEYCODE_PASTE
    time.sleep(0.4)

def set_field_text(target_placeholder_or_text, new_value):
    root = dump_ui()
    elems = find_elements(root, text_regex=target_placeholder_or_text)
    if elems:
        e = elems[0]
        print(f"Focusing field '{e['text']}' at {e['center']}")
        click(e['center'][0], e['center'][1], 0.5)
        clear_focused_text()
        type_text(new_value)
        adb("shell input keyevent 111") # KEYCODE_ESCAPE (hide soft keyboard)
        time.sleep(0.5)
        return True
    print(f"Field not found: {target_placeholder_or_text}")
    return False

def scroll_down(amount=700):
    adb(f"shell input swipe 540 1600 540 {1600 - amount} 300")
    time.sleep(0.8)

def scroll_up(amount=700):
    adb(f"shell input swipe 540 600 540 {600 + amount} 300")
    time.sleep(0.8)

def has_text(regex):
    root = dump_ui()
    elems = find_elements(root, text_regex=regex)
    return len(elems) > 0

def print_all_ui():
    root = dump_ui()
    if root is None:
        print("Root is None")
        return
    for elem in root.iter():
        t = elem.attrib.get('text', '')
        d = elem.attrib.get('content-desc', '')
        b = elem.attrib.get('bounds', '')
        if t or d:
            print(f"text='{t}' | desc='{d}' | bounds={b}")

if __name__ == "__main__":
    import sys
    cmd = sys.argv[1] if len(sys.argv) > 1 else "dump"
    if cmd == "dump":
        print_all_ui()
    elif cmd == "cap":
        screencap(sys.argv[2] if len(sys.argv) > 2 else "screenshot.png")
