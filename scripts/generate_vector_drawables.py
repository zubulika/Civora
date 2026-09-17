import os, re

def transform_tokens(d, min_x, min_y, scale, offset_x, offset_y):
    tokens = d.split()
    new_tokens = []
    i = 0
    while i < len(tokens):
        tok = tokens[i]
        if tok in ('M', 'L'):
            x = float(tokens[i+1])
            y = float(tokens[i+2])
            nx = round((x - min_x) * scale + offset_x, 3)
            ny = round((y - min_y) * scale + offset_y, 3)
            new_tokens.extend([tok, f"{nx:.3f}".rstrip('0').rstrip('.'), f"{ny:.3f}".rstrip('0').rstrip('.')])
            i += 3
        elif tok == 'C':
            coords = [float(tokens[i+k]) for k in range(1, 7)]
            ncoords = []
            for k in range(0, 6, 2):
                nx = round((coords[k] - min_x) * scale + offset_x, 3)
                ny = round((coords[k+1] - min_y) * scale + offset_y, 3)
                ncoords.extend([f"{nx:.3f}".rstrip('0').rstrip('.'), f"{ny:.3f}".rstrip('0').rstrip('.')])
            new_tokens.extend([tok] + ncoords)
            i += 7
        elif tok == 'Z':
            new_tokens.append('Z')
            i += 1
        else:
            i += 1
    return ' '.join(new_tokens)

def hex_to_android_color(hex_str, default_color="#FF084834"):
    if not hex_str or hex_str.lower() in ('#000000', '#000', 'black'):
        return default_color
    hex_clean = hex_str.lstrip('#')
    if len(hex_clean) == 6:
        return f"#FF{hex_clean.upper()}"
    elif len(hex_clean) == 8:
        return f"#{hex_clean.upper()}"
    return default_color

def generate_vector_drawable(name, paths_with_colors, is_multicolor=False, default_color="#FF084834"):
    # Collect all numbers to find global bounds of this icon
    all_xs = []
    all_ys = []
    for d, _ in paths_with_colors:
        nums = [float(x) for x in re.findall(r'[-+]?(?:\d*\.\d+|\d+)(?:[eE][-+]?\d+)?', d)]
        if nums:
            all_xs.extend(nums[0::2])
            all_ys.extend(nums[1::2])

    if not all_xs or not all_ys:
        print(f"Error: No coordinates for {name}")
        return None

    min_x, max_x = min(all_xs), max(all_xs)
    min_y, max_y = min(all_ys), max(all_ys)
    w = max_x - min_x
    h = max_y - min_y

    target_size = 20.0 # 2dp margin inside 24x24
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
        fill = hex_to_android_color(color, default_color) if is_multicolor else default_color
        xml += f'''    <path
        android:fillColor="{fill}"
        android:fillType="evenOdd"
        android:pathData="{transformed_d}" />
'''
    xml += '</vector>\n'
    return xml

def process_all():
    # Load all parsed paths
    with open('full_icons.svg', 'r', encoding='utf-8') as f:
        content = f.read()

    pattern = re.compile(r'<path d="([^"]+)" fill="([^"]+)" fill-rule="evenodd"/>')
    matches = pattern.findall(content)

    all_paths = []
    for idx, (d, color) in enumerate(matches):
        nums = [float(x) for x in re.findall(r'[-+]?(?:\d*\.\d+|\d+)(?:[eE][-+]?\d+)?', d)]
        if not nums: continue
        xs, ys = nums[0::2], nums[1::2]
        all_paths.append({
            'id': idx, 'd': d, 'color': color,
            'min_x': min(xs), 'max_x': max(xs),
            'min_y': min(ys), 'max_y': max(ys),
            'cx': (min(xs) + max(xs)) / 2,
            'cy': (min(ys) + max(ys)) / 2
        })

    # Definitions of all icons
    icon_map = {
        'ic_manage_identity': {
            'bbox': (150, 20, 320, 220),
            'multicolor': False
        },
        'ic_absher_travel': {
            'bbox': (700, -20, 950, 230),
            'multicolor': False
        },
        'ic_appointment': {
            'bbox': (1250, 30, 1500, 240),
            'multicolor': False
        },
        'ic_passport_appointment': {
            'bbox': (1800, 30, 2050, 240),
            'multicolor': False
        },
        'ic_absher_authenticator': {
            'bbox': (2300, 0, 2600, 240),
            'multicolor': False
        },
        'ic_authenticator': {
            'bbox': (140, 620, 360, 840),
            'multicolor': False
        },
        'ic_qr_viewfinder': {
            'bbox': (700, 620, 930, 840),
            'multicolor': False
        },
        'ic_driver_license': {
            'bbox': (1280, 640, 1540, 840),
            'multicolor': False
        },
        'ic_activation_device': {
            'bbox': (1840, 580, 2040, 830),
            'multicolor': False
        },
        'ic_family_outline': {
            'bbox': (90, 1200, 450, 1620),
            'multicolor': False
        },
        'ic_family': {
            'bbox': (90, 1200, 450, 1620),
            'multicolor': False
        },
        'ic_labor_import': {
            'bbox': (510, 1260, 880, 1620),
            'multicolor': False
        },
        'ic_weapon': {
            'bbox': (1080, 1280, 1740, 1680),
            'multicolor': False
        },
        'ic_birth_certificates': {
            'bbox': (2160, 1140, 2460, 1480),
            'multicolor': False
        },
        'ic_family_solid': {
            'bbox': (40, 1940, 300, 2250),
            'multicolor': False
        },
        'ic_workers_solid': {
            'bbox': (560, 2020, 820, 2270),
            'multicolor': False
        },
        'ic_other_grid': {
            'bbox': (1840, 1940, 2230, 2330),
            'multicolor': False
        },
        'ic_manage_auth': {
            'bbox': (2420, 1940, 2660, 2170),
            'multicolor': False
        },
        'ic_renew_driving_license': {
            'bbox': (350, 2580, 660, 2840),
            'multicolor': False
        },
        'ic_car_front_outline': {
            'bbox': (1040, 2600, 1450, 2950),
            'multicolor': False
        },
        'ic_gov_payments': {
            'bbox': (1650, 2690, 1980, 3010),
            'multicolor': False
        },
        'ic_parcel_box': {
            'bbox': (2250, 2630, 2560, 2960),
            'multicolor': False
        },
        'ic_ehsan': {
            'bbox': (100, 3180, 400, 3480),
            'multicolor': True
        },
        'ic_furijat': {
            'bbox': (720, 3220, 940, 3480),
            'multicolor': True
        },
        'ic_register_newborn': {
            'bbox': (280, 3740, 460, 4010),
            'multicolor': False
        },
        'ic_update_passport': {
            'bbox': (800, 3760, 1090, 4050),
            'multicolor': True
        },
        'ic_death_certificates': {
            'bbox': (1470, 3720, 1760, 3970),
            'multicolor': False
        },
        'ic_passport': {
            'bbox': (2060, 3710, 2350, 4050),
            'multicolor': False
        },
    }

    output_dir = r'app/src/main/res/drawable'
    for name, conf in icon_map.items():
        bx1, by1, bx2, by2 = conf['bbox']
        matched = [p for p in all_paths if bx1 <= p['cx'] <= bx2 and by1 <= p['cy'] <= by2]
        if not matched:
            print(f"ERROR: {name} matched 0 paths!")
            continue
        
        paths_with_colors = [(p['d'], p['color']) for p in matched]
        xml_content = generate_vector_drawable(name, paths_with_colors, is_multicolor=conf['multicolor'])
        out_file = os.path.join(output_dir, f"{name}.xml")
        with open(out_file, 'w', encoding='utf-8') as f:
            f.write(xml_content)
        print(f"Generated {out_file} with {len(matched)} paths.")

    # Special handling for ic_assistant_chat: exclude the outer circular background
    chat_paths = [p for p in all_paths if 1080 <= p['cx'] <= 1640 and 1880 <= p['cy'] <= 2440]
    if chat_paths:
        p = chat_paths[0]
        subpaths = [sp.strip() + ' Z' for sp in p['d'].split(' Z') if sp.strip()]
        # Subpaths 0..5 are dots, sparkles, and speech bubble (subpath 6 is outer circle)
        d_clean = ' '.join(subpaths[:6])
        xml_content = generate_vector_drawable('ic_assistant_chat', [(d_clean, '#084834')], is_multicolor=False)
        out_file = os.path.join(output_dir, "ic_assistant_chat.xml")
        with open(out_file, 'w', encoding='utf-8') as f:
            f.write(xml_content)
        print(f"Generated {out_file} (clean bubble + sparkles).")

if __name__ == '__main__':
    process_all()
