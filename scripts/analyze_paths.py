import re
import numpy as np

def analyze_paths():
    with open('full_icons.svg', 'r', encoding='utf-8') as f:
        content = f.read()

    # Find all path tags: <path d="..." fill="..." fill-rule="..."/>
    pattern = re.compile(r'<path d="([^"]+)" fill="([^"]+)" fill-rule="evenodd"/>')
    matches = pattern.findall(content)

    print(f"Total paths: {len(matches)}")

    path_data = []
    for idx, (d, color) in enumerate(matches):
        # Extract all numbers
        # Numbers can be preceded by command letters
        nums = [float(x) for x in re.findall(r'[-+]?(?:\d*\.\d+|\d+)', d)]
        if not nums:
            continue
        xs = nums[0::2]
        ys = nums[1::2]
        min_x, max_x = min(xs), max(xs)
        min_y, max_y = min(ys), max(ys)
        path_data.append({
            'id': idx,
            'd': d,
            'color': color,
            'min_x': min_x,
            'max_x': max_x,
            'min_y': min_y,
            'max_y': max_y,
            'center_x': (min_x + max_x) / 2,
            'center_y': (min_y + max_y) / 2,
            'w': max_x - min_x,
            'h': max_y - min_y
        })

    print(f"Paths with coordinates: {len(path_data)}")
    # Group into connected/nearby clusters or grid cells
    # We know the artboard is approx 2757 x 4779
    # Let's see y ranges
    ys = [p['center_y'] for p in path_data]
    print(f"Y min: {min(ys)}, Y max: {max(ys)}")
    xs = [p['center_x'] for p in path_data]
    print(f"X min: {min(xs)}, X max: {max(xs)}")

if __name__ == '__main__':
    analyze_paths()
