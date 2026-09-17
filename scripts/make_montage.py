from PIL import Image, ImageDraw, ImageFont
import glob, os

def make_montage():
    files = sorted(glob.glob('extracted_icons/*.png'))
    # Filter out any montage itself
    files = [f for f in files if 'montage' not in f]
    
    cols = 5
    rows = (len(files) + cols - 1) // cols
    card_w, card_h = 240, 260
    
    montage = Image.new('RGB', (cols * card_w, rows * card_h), (245, 247, 246))
    draw = ImageDraw.Draw(montage)
    
    for idx, f in enumerate(files):
        name = os.path.splitext(os.path.basename(f))[0]
        c = idx % cols
        r = idx // cols
        x = c * card_w
        y = r * card_h
        
        # Draw card background
        draw.rounded_rectangle([x + 10, y + 10, x + card_w - 10, y + card_h - 10], radius=12, fill=(255, 255, 255), outline=(220, 225, 222), width=1)
        
        # Paste icon using alpha mask
        icon_img = Image.open(f).convert('RGBA')
        icon_img.thumbnail((160, 160))
        iw, ih = icon_img.size
        # Create a white background for the icon
        icon_bg = Image.new('RGBA', icon_img.size, (255, 255, 255, 255))
        icon_composed = Image.alpha_composite(icon_bg, icon_img)
        montage.paste(icon_composed.convert('RGB'), (x + (card_w - iw) // 2, y + 25))
        
        # Draw label
        draw.text((x + card_w // 2, y + 205), name, fill=(20, 30, 25), anchor="mm")
        
    montage.save('extracted_icons/montage.png')
    print("Montage saved to extracted_icons/montage.png")

if __name__ == '__main__':
    make_montage()
