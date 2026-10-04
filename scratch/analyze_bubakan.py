import xml.etree.ElementTree as ET
import json
import math

tree = ET.parse('scratch/batas_kelurahan.kml')
root = tree.getroot()

bubakan_p = None
neighbors = {}
for p in root.findall('.//{http://www.opengis.net/kml/2.2}Placemark'):
    name_el = p.find('{http://www.opengis.net/kml/2.2}name')
    if name_el is not None and name_el.text:
        nm = name_el.text.strip()
        if nm == 'Bubakan':
            bubakan_p = p
        elif nm in ['Tambangan', 'Cangkiran', 'Polaman', 'Purwosari', 'Karangmalang']:
            c_el = p.find('.//{http://www.opengis.net/kml/2.2}coordinates')
            if c_el is not None and c_el.text:
                coords = []
                for pt in c_el.text.strip().split():
                    parts = pt.split(',')
                    coords.append((float(parts[0]), float(parts[1])))
                neighbors[nm] = coords

coords_el = bubakan_p.find('.//{http://www.opengis.net/kml/2.2}coordinates')
bubakan_coords = []
for pt in coords_el.text.strip().split():
    parts = pt.split(',')
    bubakan_coords.append([float(parts[0]), float(parts[1])]) # [lng, lat]

print(f'Bubakan vertices count: {len(bubakan_coords)}')

lngs = [c[0] for c in bubakan_coords]
lats = [c[1] for c in bubakan_coords]
min_lng, max_lng = min(lngs), max(lngs)
min_lat, max_lat = min(lats), max(lats)
center_lng = (min_lng + max_lng) / 2
center_lat = (min_lat + max_lat) / 2

print(f'Bounding box: min_lng={min_lng}, min_lat={min_lat}, max_lng={max_lng}, max_lat={max_lat}')
print(f'Center: lng={center_lng}, lat={center_lat}')

# Shoelace formula for spherical area (approximate area in km2)
def polygon_area_km2(coords):
    # R = 6371 km
    R = 6371.0
    area = 0.0
    n = len(coords)
    for i in range(n - 1):
        p1 = coords[i]
        p2 = coords[i+1]
        # convert to radians
        lon1 = math.radians(p1[0])
        lat1 = math.radians(p1[1])
        lon2 = math.radians(p2[0])
        lat2 = math.radians(p2[1])
        area += (lon2 - lon1) * (2 + math.sin(lat1) + math.sin(lat2))
    area = abs(area * R * R / 2.0)
    return area

calc_area = polygon_area_km2(bubakan_coords)
print(f'Calculated Area: {calc_area:.5f} km2 (Official from semarangkota.go.id: 2.57964 km2)')

# Check relative position of neighbors:
for nm, pts in neighbors.items():
    nlngs = [c[0] for c in pts]
    nlats = [c[1] for c in pts]
    n_clng = sum(nlngs)/len(nlngs)
    n_clat = sum(nlats)/len(nlats)
    rel_ew = 'East' if n_clng > center_lng else 'West'
    rel_ns = 'North' if n_clat > center_lat else 'South'
    print(f'Neighbor {nm}: center=({n_clng:.5f}, {n_clat:.5f}) -> {rel_ns}-{rel_ew} of Bubakan')
