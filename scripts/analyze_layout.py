import re

with open('new_icons_full.svg', 'r', encoding='utf-8') as f:
    content = f.read()

pattern = re.compile(r'<path d="([^"]+)" fill="([^"]+)" fill-rule="evenodd"/>')
matches = pattern.findall(content)
print(f'Total paths in SVG: {len(matches)}')

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

print('\nY-band distribution:')
bands = {}
for p in paths:
    band = int(p['cy'] // 500) * 500
    bands[band] = bands.get(band, 0) + 1
for b in sorted(bands):
    print(f'  y={b}-{b+499}: {bands[b]} paths')

print('\nDetailed X analysis per Y band:')
for y_start in range(0, 5000, 500):
    y_end = y_start + 500
    in_band = [p for p in paths if y_start <= p['cy'] < y_end]
    if in_band:
        x_vals = [p['cx'] for p in in_band]
        print(f'  y={y_start}-{y_end}: {len(in_band)} paths, x-range={min(x_vals):.0f}..{max(x_vals):.0f}')
        x_sorted = sorted(in_band, key=lambda p2: p2['cx'])
        prev_x = x_sorted[0]['cx']
        for p2 in x_sorted[1:]:
            if p2['cx'] - prev_x > 250:
                print(f'    GAP at x={prev_x:.0f} -> {p2["cx"]:.0f}')
            prev_x = p2['cx']
