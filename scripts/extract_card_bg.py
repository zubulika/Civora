import base64
import os
import re

def extract():
    with open('carddocument.svg', 'r', encoding='utf-8', errors='ignore') as f:
        content = f.read()

    prefix = 'data:image/png;base64,'
    start = content.find(prefix)
    if start != -1:
        start += len(prefix)
        end = content.find('"', start)
        b64_str = content[start:end]
        img_data = base64.b64decode(b64_str)
        os.makedirs('app/src/main/res/drawable-nodpi', exist_ok=True)
        out_path = 'app/src/main/res/drawable-nodpi/bg_resident_card.png'
        with open(out_path, 'wb') as out_f:
            out_f.write(img_data)
        mb = len(img_data) / (1024 * 1024)
        print(f"Extracted {out_path}: {len(img_data)} bytes ({mb:.2f} MB)")
    else:
        print("No base64 image found")

if __name__ == '__main__':
    extract()
