"""
Extract the two new icons from the 'new icons' EPS file:
  1. Update Resident Photo
  2. Manage Visit Visa

Then generate Android Vector Drawable XML files for:
  - ic_update_photo.xml   (replaces existing)
  - ic_visa.xml           (replaces existing)
"""
import os, re

EPS_FILE = 'new icons [Converted] copy.eps'


def transform_tokens(d, min_x, min_y, scale, offset_x, offset_y):
    tokens = d.split()
    new_tokens = []
    i = 0
    while i < len(tokens):
        tok = tokens[i]
        if tok in ('M', 'L'):
            x = float(tokens[i + 1])
            y = float(tokens[i + 2])
            nx = round((x - min_x) * scale + offset_x, 3)
            ny = round((y - min_y) * scale + offset_y, 3)
            new_tokens.extend([tok, f"{nx:.3f}".rstrip('0').rstrip('.'), f"{ny:.3f}".rstrip('0').rstrip('.')])
            i += 3
        elif tok == 'C':
            coords = [float(tokens[i + k]) for k in range(1, 7)]
            ncoords = []
            for k in range(0, 6, 2):
                nx = round((coords[k] - min_x) * scale + offset_x, 3)
                ny = round((coords[k + 1] - min_y) * scale + offset_y, 3)
                ncoords.extend([
                    f"{nx:.3f}".rstrip('0').rstrip('.'),
                    f"{ny:.3f}".rstrip('0').rstrip('.')
                ])
            new_tokens.extend([tok] + ncoords)
            i += 7
        elif tok == 'Z':
            new_tokens.append('Z')
            i += 1
        else:
            i += 1
    return ' '.join(new_tokens)


def generate_vector_drawable(name, paths_with_colors):
    all_xs, all_ys = [], []
    for d, _ in paths_with_colors:
        nums = [float(x) for x in re.findall(r'[-+]?(?:\d*\.\d+|\d+)(?:[eE][-+]?\d+)?', d)]
        if nums:
            all_xs.extend(nums[0::2])
            all_ys.extend(nums[1::2])

    if not all_xs:
        print(f"ERROR: No coordinates for {name}")
        return None

    min_x, max_x = min(all_xs), max(all_xs)
    min_y, max_y = min(all_ys), max(all_ys)
    w = max_x - min_x
    h = max_y - min_y

    target_size = 20.0
    max_dim = max(w, h)
    scale = target_size / max_dim
    offset_x = (24.0 - w * scale) / 2.0
    offset_y = (24.0 - h * scale) / 2.0

    xml = '''<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="28dp"
    android:height="28dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
'''
    for d, color in paths_with_colors:
        transformed_d = transform_tokens(d, min_x, min_y, scale, offset_x, offset_y)
        xml += f'''    <path
        android:fillColor="#FF000000"
        android:fillType="evenOdd"
        android:pathData="{transformed_d}" />
'''
    xml += '</vector>\n'
    return xml


def parse_eps_paths(eps_file):
    with open(eps_file, 'rb') as f:
        f.seek(32)
        raw = f.read()

    ps = raw.decode('latin1', errors='ignore')

    # Skip past XMP packet
    xmp_end = ps.find('<?xpacket end=')
    body = ps[xmp_end + 50:] if xmp_end >= 0 else ps

    tokens = body.split()
    svg_paths = []
    current_path = []
    current_color = '#000000'

    i = 0
    num_tokens = len(tokens)
    while i < num_tokens:
        tok = tokens[i]
        if tok == 'mo':
            x = tokens[i - 2]
            y = tokens[i - 1]
            current_path.append(f'M {x} {y}')
        elif tok == 'li':
            x = tokens[i - 2]
            y = tokens[i - 1]
            current_path.append(f'L {x} {y}')
        elif tok == 'cv':
            x1, y1 = tokens[i - 6], tokens[i - 5]
            x2, y2 = tokens[i - 4], tokens[i - 3]
            x3, y3 = tokens[i - 2], tokens[i - 1]
            current_path.append(f'C {x1} {y1} {x2} {y2} {x3} {y3}')
        elif tok in ('clp', 'np'):
            current_path = []
        elif tok == 'cp':
            current_path.append('Z')
        elif tok == 'cmyk':
            try:
                c = float(tokens[i - 4])
                m = float(tokens[i - 3])
                y_val = float(tokens[i - 2])
                k = float(tokens[i - 1])
                r = int(round(255 * (1 - c) * (1 - k)))
                g = int(round(255 * (1 - m) * (1 - k)))
                b = int(round(255 * (1 - y_val) * (1 - k)))
                current_color = f'#{r:02x}{g:02x}{b:02x}'
            except (ValueError, IndexError):
                pass
        elif tok == 'f':
            if current_path:
                d = ' '.join(current_path)
                svg_paths.append((d, current_color))
                current_path = []
        i += 1

    print(f"Parsed {len(svg_paths)} filled paths from EPS")
    return svg_paths


def analyze_paths(svg_paths):
    """Compute center of each path for spatial analysis."""
    path_data = []
    for d, color in svg_paths:
        nums = [float(x) for x in re.findall(r'[-+]?(?:\d*\.\d+|\d+)(?:[eE][-+]?\d+)?', d)]
        if not nums:
            continue
        xs, ys = nums[0::2], nums[1::2]
        path_data.append({
            'd': d, 'color': color,
            'min_x': min(xs), 'max_x': max(xs),
            'min_y': min(ys), 'max_y': max(ys),
            'cx': (min(xs) + max(xs)) / 2,
            'cy': (min(ys) + max(ys)) / 2,
        })
    return path_data


def dump_layout(path_data):
    """Print a spatial summary to find icon bounding regions."""
    print("\n=== PATH LAYOUT SUMMARY ===")
    print(f"Total paths: {len(path_data)}")
    if path_data:
        all_cx = [p['cx'] for p in path_data]
        all_cy = [p['cy'] for p in path_data]
        print(f"X range: {min(all_cx):.1f} .. {max(all_cx):.1f}")
        print(f"Y range: {min(all_cy):.1f} .. {max(all_cy):.1f}")
    print("\nFirst 5 paths:")
    for p in path_data[:5]:
        print(f"  cx={p['cx']:.1f} cy={p['cy']:.1f} bbox=({p['min_x']:.1f},{p['min_y']:.1f},{p['max_x']:.1f},{p['max_y']:.1f})")
    print("\nLast 5 paths:")
    for p in path_data[-5:]:
        print(f"  cx={p['cx']:.1f} cy={p['cy']:.1f} bbox=({p['min_x']:.1f},{p['min_y']:.1f},{p['max_x']:.1f},{p['max_y']:.1f})")

    # Try to cluster by X bands (icons laid out horizontally) 
    # or Y bands (icons laid out vertically)
    print("\n=== X-based clustering ===")
    xs_sorted = sorted(set(round(p['cx'] / 100) * 100 for p in path_data))
    for xb in xs_sorted:
        in_band = [p for p in path_data if abs(round(p['cx'] / 100) * 100 - xb) < 1]
        print(f"  ~x={xb}: {len(in_band)} paths, y-range={min(p['cy'] for p in in_band):.0f}..{max(p['cy'] for p in in_band):.0f}")


def save_full_svg(svg_paths, output='new_icons_full.svg'):
    """Save complete SVG for visual inspection."""
    with open(output, 'w', encoding='utf-8') as f:
        f.write('<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 3000 3000" width="1500" height="1500">\n')
        for d, color in svg_paths:
            f.write(f'  <path d="{d}" fill="{color}" fill-rule="evenodd"/>\n')
        f.write('</svg>\n')
    print(f"Saved {output}")


def extract_icon(path_data, bbox, name):
    """Extract paths within a bounding box."""
    bx1, by1, bx2, by2 = bbox
    matched = [p for p in path_data if bx1 <= p['cx'] <= bx2 and by1 <= p['cy'] <= by2]
    print(f"  {name}: {len(matched)} paths in bbox ({bx1},{by1},{bx2},{by2})")
    return [(p['d'], p['color']) for p in matched]


def main():
    svg_paths = parse_eps_paths(EPS_FILE)
    path_data = analyze_paths(svg_paths)
    dump_layout(path_data)
    save_full_svg(svg_paths)

    # After seeing the layout, we'll define bboxes for the two icons.
    # Run once to discover, then fill in below.
    # For now, try splitting the paths into two halves based on position.
    if not path_data:
        print("No paths found!")
        return

    all_cx = [p['cx'] for p in path_data]
    all_cy = [p['cy'] for p in path_data]
    mid_x = (min(all_cx) + max(all_cx)) / 2
    mid_y = (min(all_cy) + max(all_cy)) / 2
    print(f"\nMidpoint: cx={mid_x:.1f}, cy={mid_y:.1f}")

    # Split into groups - try left/right
    left = [(p['d'], p['color']) for p in path_data if p['cx'] < mid_x]
    right = [(p['d'], p['color']) for p in path_data if p['cx'] >= mid_x]
    print(f"Left group: {len(left)} paths, Right group: {len(right)} paths")

    # Split into groups - try top/bottom
    top = [(p['d'], p['color']) for p in path_data if p['cy'] < mid_y]
    bottom = [(p['d'], p['color']) for p in path_data if p['cy'] >= mid_y]
    print(f"Top group: {len(top)} paths, Bottom group: {len(bottom)} paths")


if __name__ == '__main__':
    main()
