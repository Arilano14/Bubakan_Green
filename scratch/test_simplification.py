import json
import math

with open('scratch/analyze_bubakan.py') as f:
    pass

# We have the 250 coords in bubakan_coords
# Let's import shapely if available, or write Ramer-Douglas-Peucker
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
        rec_results1 = rdp(points[:index+1], epsilon)
        rec_results2 = rdp(points[index:], epsilon)
        return rec_results1[:-1] + rec_results2
    else:
        return [points[0], points[end]]

# Let's run on bubakan_coords
import xml.etree.ElementTree as ET
tree = ET.parse('scratch/batas_kelurahan.kml')
root = tree.getroot()
for p in root.findall('.//{http://www.opengis.net/kml/2.2}Placemark'):
    n = p.find('{http://www.opengis.net/kml/2.2}name')
    if n is not None and n.text and n.text.strip() == 'Bubakan':
        c_el = p.find('.//{http://www.opengis.net/kml/2.2}coordinates')
        orig_coords = [[float(x.split(',')[0]), float(x.split(',')[1])] for x in c_el.text.strip().split()]

print(f'Original vertices: {len(orig_coords)}')

# Test epsilons
for eps in [0.00001, 0.00002, 0.00005, 0.0001]:
    simplified = rdp(orig_coords, eps)
    # Ensure closed
    if simplified[0] != simplified[-1]:
        simplified.append(simplified[0])
    print(f'Epsilon {eps} (~{eps*111000:.1f}m tolerance): {len(simplified)} vertices')
