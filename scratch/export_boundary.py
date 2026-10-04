import xml.etree.ElementTree as ET
import json
import math
import os

tree = ET.parse('scratch/batas_kelurahan.kml')
root = tree.getroot()

bubakan_p = None
for p in root.findall('.//{http://www.opengis.net/kml/2.2}Placemark'):
    n = p.find('{http://www.opengis.net/kml/2.2}name')
    if n is not None and n.text and n.text.strip() == 'Bubakan':
        bubakan_p = p
        break

coords_el = bubakan_p.find('.//{http://www.opengis.net/kml/2.2}coordinates')
orig_pts = []
for pt in coords_el.text.strip().split():
    parts = pt.split(',')
    orig_pts.append([round(float(parts[0]), 6), round(float(parts[1]), 6)])

# RDP simplification with 0.00002 tolerance (~2.2 meters)
def rdp(points, epsilon):
    def perpendicular_distance(pt, line_start, line_end):
        if line_start == line_end:
            return math.hypot(pt[0] - line_start[0], pt[1] - line_start[1])
        x, y = pt[0], pt[1]
        x1, y1 = line_start[0], line_start[1]
        x2, y2 = line_end[0], line_end[1]
        num = abs((y2 - y1)*x - (x2 - x1)*y + x2*y1 - y2*x1)
        den = math.hypot(y2 - y1, x2 - x1)
        return num / den

    dmax = 0.0
    index = 0
    end = len(points) - 1
    for i in range(1, end):
        d = perpendicular_distance(points[i], points[0], points[end])
        if d > dmax:
            index = i
            dmax = d

    if dmax > epsilon:
        rec1 = rdp(points[:index+1], epsilon)
        rec2 = rdp(points[index:], epsilon)
        return rec1[:-1] + rec2
    else:
        return [points[0], points[end]]

opt_pts = rdp(orig_pts, 0.00002)
if opt_pts[0] != opt_pts[-1]:
    opt_pts.append(opt_pts[0])

print(f'Original vertices: {len(orig_pts)}')
print(f'Optimized vertices: {len(opt_pts)}')

# Construct GeoJSON FeatureCollection
geojson_data = {
    "type": "FeatureCollection",
    "name": "Batas_Kelurahan_Bubakan",
    "crs": {
        "type": "name",
        "properties": {
            "name": "urn:ogc:def:crs:OGC:1.3:CRS84"
        }
    },
    "features": [
        {
            "type": "Feature",
            "properties": {
                "fid": 16,
                "kelurahan": "Bubakan",
                "kecamatan": "Mijen",
                "kota": "Semarang",
                "provinsi": "Jawa Tengah",
                "kode_wilayah": "33.74.14.1002",
                "luas_km2_resmi": 2.57964,
                "luas_km2_geometri": 2.54866,
                "sumber": "Sistem Informasi Geospasial Warga Kota Semarang (dataspasial.semarangkota.go.id)",
                "tanggal_unduh": "2026-10-03"
            },
            "geometry": {
                "type": "Polygon",
                "coordinates": [opt_pts]
            }
        }
    ]
}

out_path = 'app/src/main/assets/map/bubakan_boundary.geojson'
with open(out_path, 'w', encoding='utf-8') as f:
    json.dump(geojson_data, f, indent=2)

file_size_bytes = os.path.getsize(out_path)
print(f'Saved GeoJSON to {out_path} ({file_size_bytes} bytes, {file_size_bytes/1024:.2f} KB)')

# Also save original unsimplified version for comparison/archive
orig_geojson_data = {
    "type": "FeatureCollection",
    "name": "Batas_Kelurahan_Bubakan_Original",
    "features": [
        {
            "type": "Feature",
            "properties": {
                "fid": 16,
                "kelurahan": "Bubakan",
                "kode_wilayah": "33.74.14.1002"
            },
            "geometry": {
                "type": "Polygon",
                "coordinates": [orig_pts]
            }
        }
    ]
}
with open('scratch/bubakan_boundary_original.geojson', 'w', encoding='utf-8') as f:
    json.dump(orig_geojson_data, f, indent=2)
orig_size = os.path.getsize('scratch/bubakan_boundary_original.geojson')
print(f'Original GeoJSON size: {orig_size} bytes ({orig_size/1024:.2f} KB)')
