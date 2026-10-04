import xml.etree.ElementTree as ET
import re

tree = ET.parse('scratch/batas_kelurahan.kml')
root = tree.getroot()

# namespaces
ns = {'kml': 'http://www.opengis.net/kml/2.2'}

placemarks = root.findall('.//{http://www.opengis.net/kml/2.2}Placemark')
if not placemarks:
    placemarks = root.findall('.//Placemark')

print(f'Total placemarks found: {len(placemarks)}')

bubakan_placemark = None
for p in placemarks:
    text = ET.tostring(p, encoding='utf-8').decode('utf-8', errors='ignore')
    if 'bubakan' in text.lower():
        print('Found placemark with "Bubakan"!')
        bubakan_placemark = p
        # Print name / description / SimpleData
        for elem in p.iter():
            tag = elem.tag.split('}')[-1]
            if tag in ['name', 'description', 'SimpleData']:
                attrib_str = ' '.join(f'{k}="{v}"' for k,v in elem.attrib.items())
                print(f'  <{tag} {attrib_str}>{elem.text}</{tag}>')

if bubakan_placemark is not None:
    # Look for Polygon / MultiGeometry / coordinates
    coords = bubakan_placemark.findall('.//{http://www.opengis.net/kml/2.2}coordinates')
    if not coords:
        coords = bubakan_placemark.findall('.//coordinates')
    print(f'Coordinate blocks found: {len(coords)}')
    for i, c in enumerate(coords):
        coord_text = c.text.strip() if c.text else ''
        points = [p.strip() for p in coord_text.split() if p.strip()]
        print(f'Block {i}: {len(points)} coordinate points')
        print(f'Sample points: {points[:3]} ... {points[-2:]}')
