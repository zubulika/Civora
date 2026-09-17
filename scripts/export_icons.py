import os, re
import resvg_py

def export_isolated_icons():
    os.makedirs('extracted_icons', exist_ok=True)
    with open('full_icons.svg', 'r', encoding='utf-8') as f:
        content = f.read()

    pattern = re.compile(r'<path d="([^"]+)" fill="([^"]+)" fill-rule="evenodd"/>')
    matches = pattern.findall(content)

    paths = []
    for idx, (d, color) in enumerate(matches):
        nums = [float(x) for x in re.findall(r'[-+]?(?:\d*\.\d+|\d+)(?:[eE][-+]?\d+)?', d)]
        if not nums: continue
        xs, ys = nums[0::2], nums[1::2]
        paths.append({
            'id': idx, 'd': d, 'color': color,
            'min_x': min(xs), 'max_x': max(xs),
            'min_y': min(ys), 'max_y': max(ys),
            'cx': (min(xs) + max(xs)) / 2,
            'cy': (min(ys) + max(ys)) / 2
        })

    # Named icons with their bounding regions (min_x, min_y, max_x, max_y)
    icon_defs = {
        'ic_manage_identity': (150, 20, 320, 220),
        'ic_absher_travel': (700, -20, 950, 230),
        'ic_appointment': (1250, 30, 1500, 240),
        'ic_passport_appointment': (1800, 30, 2050, 240),
        'ic_absher_authenticator': (2300, 0, 2600, 240),
        'ic_authenticator': (140, 620, 360, 840),
        'ic_qr_viewfinder': (700, 620, 930, 840),
        'ic_driver_license': (1280, 640, 1540, 840),
        'ic_activation_device': (1840, 580, 2040, 830),
        'ic_family_outline': (90, 1200, 450, 1620),
        'ic_labor_import': (510, 1260, 880, 1620),
        'ic_weapon': (1080, 1280, 1740, 1680),
        'ic_birth_certificates': (2160, 1140, 2460, 1480),
        'ic_family_solid': (40, 1940, 300, 2250),
        'ic_workers_solid': (560, 2020, 820, 2270),
        'ic_assistant_chat': (1080, 1880, 1640, 2440),
        'ic_other_grid': (1840, 1940, 2230, 2330),
        'ic_manage_auth': (2420, 1940, 2660, 2170),
        'ic_renew_driving_license': (350, 2580, 660, 2840),
        'ic_car_front_outline': (1040, 2600, 1450, 2950),
        'ic_gov_payments': (1650, 2690, 1980, 3010),
        'ic_parcel_box': (2250, 2630, 2560, 2960),
        'ic_ehsan': (100, 3180, 400, 3480),
        'ic_furijat': (720, 3220, 940, 3480),
        'ic_register_newborn': (280, 3740, 460, 4010),
        'ic_update_passport': (800, 3760, 1090, 4050),
        'ic_death_certificates': (1470, 3720, 1760, 3970),
        'ic_passport': (2060, 3710, 2350, 4050),
    }

    for name, (bx1, by1, bx2, by2) in icon_defs.items():
        matched = [p for p in paths if bx1 <= p['cx'] <= bx2 and by1 <= p['cy'] <= by2]
        if not matched:
            print(f"WARNING: No paths found for {name} in ({bx1},{by1},{bx2},{by2})")
            continue

        # Compute tight bounding box of matched paths
        min_x = min(p['min_x'] for p in matched)
        max_x = max(p['max_x'] for p in matched)
        min_y = min(p['min_y'] for p in matched)
        max_y = max(p['max_y'] for p in matched)
        w = max_x - min_x
        h = max_y - min_y

        # Add 5% padding
        pad = max(w, h) * 0.05
        vb_x = min_x - pad
        vb_y = min_y - pad
        vb_w = w + pad * 2
        vb_h = h + pad * 2

        svg = f'<svg xmlns="http://www.w3.org/2000/svg" viewBox="{vb_x:.2f} {vb_y:.2f} {vb_w:.2f} {vb_h:.2f}" width="200" height="200">\n'
        for p in matched:
            svg += f'  <path d="{p["d"]}" fill="{p["color"]}" fill-rule="evenodd"/>\n'
        svg += '</svg>'

        svg_path = f'extracted_icons/{name}.svg'
        with open(svg_path, 'w', encoding='utf-8') as f:
            f.write(svg)

        # Render PNG with white background for inspection
        svg_white = svg.replace('<svg xmlns="http://www.w3.org/2000/svg"', '<svg xmlns="http://www.w3.org/2000/svg"')
        # We can add a rect for background
        insert_pos = svg.find('>') + 1
        svg_with_bg = svg[:insert_pos] + f'\n<rect x="{vb_x}" y="{vb_y}" width="{vb_w}" height="{vb_h}" fill="white"/>' + svg[insert_pos:]
        try:
            png_bytes = resvg_py.svg_to_bytes(svg_with_bg)
            with open(f'extracted_icons/{name}.png', 'wb') as f:
                f.write(png_bytes)
        except Exception as e:
            print(f"Error rendering {name}.png:", e)

        print(f"Exported {name}: {len(matched)} paths, bbox=({min_x:.1f},{min_y:.1f},w={w:.1f},h={h:.1f})")

if __name__ == '__main__':
    export_isolated_icons()
