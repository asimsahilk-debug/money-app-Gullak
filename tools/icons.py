import os, glob
from PIL import Image, ImageDraw, ImageFont
NAVY=(4,44,83,255); AMBER=(239,159,39,255)
FONTS=["/usr/share/fonts/opentype/inter/Inter-Bold.otf","/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf"]
def font(sz):
    for f in FONTS:
        if os.path.exists(f):
            ft=ImageFont.truetype(f,sz)
            if ft.getbbox("₹")[2]>0: return ft
    raise SystemExit("no font")
def glyph(img,box_frac):
    W,H=img.size; d=ImageDraw.Draw(img); sz=int(H*box_frac); ft=font(sz)
    b=d.textbbox((0,0),"₹",font=ft); w,h=b[2]-b[0],b[3]-b[1]
    d.text(((W-w)/2-b[0],(H-h)/2-b[1]),"₹",font=ft,fill=AMBER)
def tile(n,round_=False,radius=0.22,pad=0):
    im=Image.new("RGBA",(n,n),(0,0,0,0)); d=ImageDraw.Draw(im)
    if round_: d.ellipse((pad,pad,n-1-pad,n-1-pad),fill=NAVY)
    else: d.rounded_rectangle((pad,pad,n-1-pad,n-1-pad),radius=int(n*radius),fill=NAVY)
    glyph(im,0.52); return im
res="android/app/src/main/res"
for dens,n in {"mdpi":48,"hdpi":72,"xhdpi":96,"xxhdpi":144,"xxxhdpi":192}.items():
    tile(n).save(f"{res}/mipmap-{dens}/ic_launcher.png")
    tile(n,True).save(f"{res}/mipmap-{dens}/ic_launcher_round.png")
    fg=Image.new("RGBA",(int(n*2.25),int(n*2.25)),(0,0,0,0)); glyph(fg,0.30); fg.save(f"{res}/mipmap-{dens}/ic_launcher_foreground.png")
open(f"{res}/values/ic_launcher_background.xml","w").write('<?xml version="1.0" encoding="utf-8"?>\n<resources>\n    <color name="ic_launcher_background">#042C53</color>\n</resources>\n')
for p in glob.glob(f"{res}/drawable*/splash.png"):
    w,h=Image.open(p).size; im=Image.new("RGBA",(w,h),NAVY); s=int(min(w,h)*0.28)
    t=Image.new("RGBA",(s,s),(0,0,0,0)); glyph(t,0.9); im.alpha_composite(t,((w-s)//2,(h-s)//2)); im.convert("RGB").save(p)
for n in (192,512):
    tile(n,radius=0.22).save(f"www/icons/icon-{n}.png")
    m=Image.new("RGBA",(n,n),NAVY); glyph(m,0.40); m.save(f"www/icons/maskable-{n}.png")
tile(180,radius=0).save("www/icons/apple-touch-icon.png")
print("icons done")
