"""
Proper SVG to Android VectorDrawable converter.
Handles the coordinate transformation correctly for both absolute and relative commands.
"""
import re

def convert_svg_to_vector_drawable(svg_file, output_file):
    with open(svg_file, 'r', encoding='utf-8') as f:
        svg = f.read()

    # Extract viewBox
    vb_match = re.search(r'viewBox="([^"]+)"', svg)
    if not vb_match:
        raise ValueError("No viewBox found")
    vb = [float(v) for v in vb_match.group(1).split()]
    vb_x, vb_y, vb_w, vb_h = vb

    # Scale: fit into 20x20 centered in 24x24
    scale = 20.0 / max(vb_w, vb_h)
    off_x = (24.0 - vb_w * scale) / 2.0
    off_y = (24.0 - vb_h * scale) / 2.0

    def tx(x): return (x - vb_x) * scale + off_x
    def ty(y): return (y - vb_y) * scale + off_y
    def fmt(v): return f"{v:.4f}".rstrip('0').rstrip('.')

    def transform_path_data(d):
        # Tokenize: commands and numbers
        tokens = re.findall(r'[MmLlHhVvCcSsQqTtAaZz]|[-+]?(?:\d+\.?\d*|\.\d+)(?:[eE][-+]?\d+)?', d)
        out = []
        i = 0
        cur_cmd = None
        # current pen position (in original coords) for relative commands
        cur_x, cur_y = 0.0, 0.0

        def read_float():
            nonlocal i
            v = float(tokens[i]); i += 1; return v

        while i < len(tokens):
            t = tokens[i]
            if t.isalpha():
                cur_cmd = t
                i += 1
                # Don't add Z yet; handle below
                if t in ('Z', 'z'):
                    out.append('Z')
                continue

            cmd = cur_cmd
            if cmd in ('M', 'L', 'T'):
                x = read_float(); y = read_float()
                cur_x, cur_y = x, y
                out.extend([cmd, fmt(tx(x)), fmt(ty(y))])
                cur_cmd = 'L' if cmd == 'M' else cmd
            elif cmd in ('m', 'l', 't'):
                dx = read_float(); dy = read_float()
                cur_x += dx; cur_y += dy
                out.extend([cmd.upper(), fmt(tx(cur_x)), fmt(ty(cur_y))])
                cur_cmd = 'l' if cmd == 'm' else cmd
            elif cmd == 'H':
                x = read_float(); cur_x = x
                out.extend(['L', fmt(tx(x)), fmt(ty(cur_y))])
            elif cmd == 'h':
                dx = read_float(); cur_x += dx
                out.extend(['L', fmt(tx(cur_x)), fmt(ty(cur_y))])
            elif cmd == 'V':
                y = read_float(); cur_y = y
                out.extend(['L', fmt(tx(cur_x)), fmt(ty(y))])
            elif cmd == 'v':
                dy = read_float(); cur_y += dy
                out.extend(['L', fmt(tx(cur_x)), fmt(ty(cur_y))])
            elif cmd == 'C':
                x1 = read_float(); y1 = read_float()
                x2 = read_float(); y2 = read_float()
                x  = read_float(); y  = read_float()
                cur_x, cur_y = x, y
                out.extend(['C',
                    fmt(tx(x1)), fmt(ty(y1)),
                    fmt(tx(x2)), fmt(ty(y2)),
                    fmt(tx(x)),  fmt(ty(y))])
            elif cmd == 'c':
                x1 = read_float(); y1 = read_float()
                x2 = read_float(); y2 = read_float()
                dx = read_float(); dy = read_float()
                ax1, ay1 = cur_x + x1, cur_y + y1
                ax2, ay2 = cur_x + x2, cur_y + y2
                cur_x += dx; cur_y += dy
                out.extend(['C',
                    fmt(tx(ax1)), fmt(ty(ay1)),
                    fmt(tx(ax2)), fmt(ty(ay2)),
                    fmt(tx(cur_x)), fmt(ty(cur_y))])
            elif cmd == 'S':
                x2 = read_float(); y2 = read_float()
                x  = read_float(); y  = read_float()
                cur_x, cur_y = x, y
                out.extend(['S',
                    fmt(tx(x2)), fmt(ty(y2)),
                    fmt(tx(x)),  fmt(ty(y))])
            elif cmd == 's':
                dx2 = read_float(); dy2 = read_float()
                dx  = read_float(); dy  = read_float()
                ax2, ay2 = cur_x + dx2, cur_y + dy2
                cur_x += dx; cur_y += dy
                out.extend(['S',
                    fmt(tx(ax2)), fmt(ty(ay2)),
                    fmt(tx(cur_x)), fmt(ty(cur_y))])
            elif cmd in ('Z', 'z'):
                out.append('Z')
            else:
                # Unknown command, pass through
                out.append(tokens[i]); i += 1

        return ' '.join(out)

    # Extract all path d attributes
    paths = re.findall(r'<path\b[^>]*\bd="([^"]+)"', svg)
    
    lines = [
        '<vector xmlns:android="http://schemas.android.com/apk/res/android"',
        '    android:width="28dp"',
        '    android:height="28dp"',
        '    android:viewportWidth="24"',
        '    android:viewportHeight="24">',
    ]
    for d in paths:
        transformed = transform_path_data(d)
        lines += [
            '    <path',
            '        android:fillColor="#FF000000"',
            '        android:fillType="evenOdd"',
            f'        android:pathData="{transformed}" />',
        ]
    lines.append('</vector>')

    xml = '\n'.join(lines) + '\n'
    with open(output_file, 'w', encoding='utf-8') as f:
        f.write(xml)
    print(f"Written: {output_file}")
    return xml


convert_svg_to_vector_drawable(
    'my visa.svg',
    'app/src/main/res/drawable/ic_visa.xml'
)

convert_svg_to_vector_drawable(
    'residnet icons.svg',
    'app/src/main/res/drawable/ic_update_photo.xml'
)
