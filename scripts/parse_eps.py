import os

def parse_eps():
    with open(r'new service icons .eps', 'rb') as f:
        f.seek(32)
        ps = f.read(1580486).decode('latin1', errors='ignore')

    xmp_end = ps.find('<?xpacket end=')
    body = ps[xmp_end+50:]

    tokens = body.split()
    print(f'Total tokens: {len(tokens)}')

    svg_paths = []
    current_path = []
    current_color = '#000000'

    i = 0
    num_tokens = len(tokens)
    while i < num_tokens:
        tok = tokens[i]
        if tok == 'mo':
            x = tokens[i-2]
            y = tokens[i-1]
            current_path.append(f'M {x} {y}')
        elif tok == 'li':
            x = tokens[i-2]
            y = tokens[i-1]
            current_path.append(f'L {x} {y}')
        elif tok == 'cv':
            x1, y1 = tokens[i-6], tokens[i-5]
            x2, y2 = tokens[i-4], tokens[i-3]
            x3, y3 = tokens[i-2], tokens[i-1]
            current_path.append(f'C {x1} {y1} {x2} {y2} {x3} {y3}')
        elif tok in ('clp', 'np'):
            current_path = []
        elif tok == 'cp':
            current_path.append('Z')
        elif tok == 'cmyk':
            c = float(tokens[i-4])
            m = float(tokens[i-3])
            y = float(tokens[i-2])
            k = float(tokens[i-1])
            r = int(round(255 * (1 - c) * (1 - k)))
            g = int(round(255 * (1 - m) * (1 - k)))
            b = int(round(255 * (1 - y) * (1 - k)))
            current_color = f'#{r:02x}{g:02x}{b:02x}'
        elif tok == 'f':
            if current_path:
                d = ' '.join(current_path)
                svg_paths.append((d, current_color))
                current_path = []
        i += 1

    print(f'Extracted {len(svg_paths)} filled SVG paths!')
    with open('full_icons.svg', 'w', encoding='utf-8') as f:
        f.write('<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 2757 4779" width="2757" height="4779">\n')
        for d, color in svg_paths:
            f.write(f'  <path d="{d}" fill="{color}" fill-rule="evenodd"/>\n')
        f.write('</svg>\n')
    print('Saved full_icons.svg successfully!')

if __name__ == '__main__':
    parse_eps()
