import xml.etree.ElementTree as ET

def convert():
    tree = ET.parse('20121209042755!Ministry_of_Interior_Saudi_Arabia.svg')
    root = tree.getroot()
    ns = {'svg': 'http://www.w3.org/2000/svg'}

    vd = [
        '<vector xmlns:android="http://schemas.android.com/apk/res/android"',
        '    android:width="48dp"',
        '    android:height="48dp"',
        '    android:viewportWidth="268.486"',
        '    android:viewportHeight="269.986">'
    ]

    for p in root.findall('.//svg:path', ns):
        d = p.attrib.get('d', '').replace('\n', ' ').strip()
        vd.append(f'  <path android:fillColor="#00673E" android:pathData="{d}" />')

    vd.append('</vector>')

    with open('app/src/main/res/drawable/ic_saudi_ministry_interior.xml', 'w', encoding='utf-8') as f:
        f.write('\n'.join(vd))
    print('Successfully generated ic_saudi_ministry_interior.xml')

if __name__ == '__main__':
    convert()
