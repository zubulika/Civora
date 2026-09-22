"""
Render the two icon groups from the y=2500-3000 band as separate SVG files
for visual inspection. Then generate the Android vector drawables.
"""
import re, os

with open('new_icons_full.svg', 'r', encoding='utf-8') as f:
    content = f.read()

pattern = re.compile(r'<path d="([^"]+)" fill="([^"]+)" fill-rule="evenodd"/>')
matches = pattern.findall(content)

paths = []
for d, color in matches:
    nums = [float(x) for x in re.findall(r'[-+]?(?:\d*\.\d+|\d+)(?:[eE][-+]?\d+)?', d)]
    if nums:
        xs, ys = nums[0::2], nums[1::2]
        paths.append({
            'd': d, 'color': color,
            'min_x': min(xs), 'max_x': max(xs),
            'min_y': min(ys), 'max_y': max(ys),
            'cx': (min(xs) + max(xs)) / 2,
            'cy': (min(ys) + max(ys)) / 2,
        })

def render_region(paths, bbox, filename, bg_color='white'):
    bx1, by1, bx2, by2 = bbox
    matched = [p for p in paths if bx1 <= p['cx'] <= bx2 and by1 <= p['cy'] <= by2]
    print(f"Region ({bx1},{by1})->({bx2},{by2}): {len(matched)} paths -> {filename}")
    w = bx2 - bx1
    h = by2 - by1
    with open(filename, 'w', encoding='utf-8') as f:
        f.write(f'<svg xmlns="http://www.w3.org/2000/svg" viewBox="{bx1} {by1} {w} {h}" width="400" height="400">\n')
        f.write(f'  <rect x="{bx1}" y="{by1}" width="{w}" height="{h}" fill="{bg_color}"/>\n')
        for p in matched:
            f.write(f'  <path d="{p["d"]}" fill="{p["color"]}" fill-rule="evenodd"/>\n')
        f.write('</svg>\n')

# Render the main band y=2500-3000 as a whole
render_region(paths, (0, 2400, 2400, 3100), 'extracted_icons/band_2500_overview.svg')

# The gap is at x=844->1349 -- so icon1 is left, icon2 is right
render_region(paths, (0, 2400, 900, 3100), 'extracted_icons/new_icon_left.svg')
render_region(paths, (1300, 2400, 2400, 3100), 'extracted_icons/new_icon_right.svg')

# Also try the full extent but clipped more tightly on Y
for y_top in range(2400, 2600, 50):
    for y_bot in range(3000, 3200, 50):
        left = [p for p in paths if 0 <= p['cx'] <= 900 and y_top <= p['cy'] <= y_bot]
        right = [p for p in paths if 1300 <= p['cx'] <= 2400 and y_top <= p['cy'] <= y_bot]
        if left or right:
            print(f"  y={y_top}-{y_bot}: left={len(left)}, right={len(right)}")
