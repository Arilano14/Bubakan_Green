import sys
sys.path.append('tools')
from qa_driver import adb, click, paste_text, screencap, print_all_ui, dump_ui, find_elements, click_element
import time

print("--- Populating SIRIH Canonical Data ---")
# Dismiss keyboard
adb("shell input keyevent 111")
time.sleep(0.5)

# Scroll to top
adb("shell input swipe 540 600 540 1800 300")
time.sleep(1)

# Helper to clear field at (x, y) and paste text
def fill_field(x, y, text):
    click(x, y)
    time.sleep(0.3)
    adb("shell input keyevent KEYCODE_MOVE_END")
    for _ in range(5): adb("shell input keyevent " + " ".join(["67"] * 10))
    paste_text(text)
    adb("shell input keyevent 111")
    time.sleep(0.4)

# 1. Nama Indonesia
print("1. Filling Nama Indonesia: Sirih...")
fill_field(540, 620, "Sirih")

# Scroll down to reveal Latin, Mandarin, Pinyin
adb("shell input swipe 540 1600 540 1000 300")
time.sleep(0.8)

root = dump_ui()
# Find Latin field
elems_latin = find_elements(root, text_regex="Contoh: Zingiber|Piper betle")
if elems_latin:
    print("2. Filling Latin name: Piper betle...")
    fill_field(elems_latin[0]["center"][0], elems_latin[0]["center"][1], "Piper betle")

root = dump_ui()
elems_mandarin = find_elements(root, text_regex="Contoh: 红姜|蒌叶")
if elems_mandarin:
    print("3. Filling Mandarin: 蒌叶...")
    fill_field(elems_mandarin[0]["center"][0], elems_mandarin[0]["center"][1], "蒌叶")

root = dump_ui()
elems_pinyin = find_elements(root, text_regex="Contoh: hóng jiāng|lóu yè")
if elems_pinyin:
    print("4. Filling Pinyin: lóu yè...")
    fill_field(elems_pinyin[0]["center"][0], elems_pinyin[0]["center"][1], "lóu yè")

# Scroll down for Description, Characteristics, Common Uses, Cultivation Notes
adb("shell input swipe 540 1600 540 800 300")
time.sleep(0.8)

root = dump_ui()
elems_desc = find_elements(root, text_regex="Deskripsi umum")
if elems_desc:
    print("5. Filling Description...")
    fill_field(elems_desc[0]["center"][0], elems_desc[0]["center"][1], 
               "Tanaman merambat herba perennial dengan daun berbentuk hati beraroma aromatik khas dan rasa getir hangat menyegarkan.")

adb("shell input swipe 540 1600 540 1000 300")
time.sleep(0.8)

root = dump_ui()
elems_char = find_elements(root, text_regex="Bentuk daun")
if elems_char:
    print("6. Filling Characteristics...")
    fill_field(elems_char[0]["center"][0], elems_char[0]["center"][1], 
               "Batang bulat beruas dengan akar lekat, daun tunggal berseling berurat melengkung 5-7, kaya kelenjar minyak atsiri.")

root = dump_ui()
elems_uses = find_elements(root, text_regex="Khasiat herbal")
if elems_uses:
    print("7. Filling Common Uses...")
    fill_field(elems_uses[0]["center"][0], elems_uses[0]["center"][1], 
               "Antiseptik alami, antibakteri pembersih mulut dan tenggorokan, seduhan pereda batuk, dan bahan racikan kunyah sirih.")

adb("shell input swipe 540 1600 540 1000 300")
time.sleep(0.8)

root = dump_ui()
elems_cult = find_elements(root, text_regex="Kebutuhan sinar")
if elems_cult:
    print("8. Filling Cultivation Notes...")
    fill_field(elems_cult[0]["center"][0], elems_cult[0]["center"][1], 
               "Tumbuh merambat di tiang ajir, naungan parsial 60-70%, tanah gembur kaya humus berdrainase baik, penyiraman 1-2 kali sehari.")

screencap("docs/phase-plant/screenshots/16_sirih_all_fields_populated.png")

# Now tap Perbarui Ensiklopedia
print("Tapping Perbarui Ensiklopedia...")
click_element(text_regex="Perbarui Ensiklopedia")
time.sleep(3)

screencap("docs/phase-plant/screenshots/17_sirih_saved_success.png")
print_all_ui()
