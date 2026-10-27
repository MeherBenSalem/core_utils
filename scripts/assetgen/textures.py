"""Procedural pixel-art textures for Core Utilities.

Every texture is drawn from code with a fixed seed, so re-running the generator gives byte-identical files.
Block textures are 32x32 (matching the existing filter/plate art), items and conduits 16x16.
"""
import math
import random

from PIL import Image, ImageDraw

CASING = (43, 49, 58)
CASING_LIGHT = (62, 71, 83)
CASING_HI = (84, 95, 110)
CASING_DARK = (26, 30, 36)
EDGE = (16, 18, 22)

ACCENTS = {
    'electric_furnace': (232, 112, 40),
    'crusher': (150, 160, 175),
    'alloy_smelter': (240, 170, 50),
    'compressor': (90, 170, 230),
    'combustion_generator': (240, 120, 40),
    'geothermal_generator': (230, 70, 30),
    'quarry': (90, 220, 210),
    'crop_harvester': (120, 200, 70),
    'mob_grinder': (220, 50, 60),
    'block_breaker': (200, 200, 210),
    'block_placer': (120, 150, 230),
}

TIERS = {
    'basic': (230, 120, 50),
    'advanced': (240, 200, 60),
    'elite': (70, 220, 230),
    'creative': (220, 80, 230),
}


def rng(seed):
    return random.Random(seed)


def shade(c, f):
    return tuple(max(0, min(255, int(v * f))) for v in c[:3]) + ((c[3],) if len(c) > 3 else ())


def mix(a, b, t):
    return tuple(int(a[i] + (b[i] - a[i]) * t) for i in range(3))


def noise_fill(img, box, base, amount, seed):
    r = rng(seed)
    x0, y0, x1, y1 = box
    px = img.load()
    for y in range(y0, y1):
        for x in range(x0, x1):
            f = 1 + (r.random() - 0.5) * amount
            px[x, y] = shade(base, f) + (255,)


def casing(seed, size=32, base=CASING):
    """Dark machine casing: bevelled frame, brushed panel, corner rivets."""
    img = Image.new('RGBA', (size, size))
    noise_fill(img, (0, 0, size, size), base, 0.10, seed)
    d = ImageDraw.Draw(img)
    d.rectangle((0, 0, size - 1, size - 1), outline=EDGE + (255,))
    d.line((1, 1, size - 2, 1), fill=CASING_HI + (255,))
    d.line((1, 1, 1, size - 2), fill=CASING_HI + (255,))
    d.line((1, size - 2, size - 2, size - 2), fill=CASING_DARK + (255,))
    d.line((size - 2, 1, size - 2, size - 2), fill=CASING_DARK + (255,))
    d.rectangle((3, 3, size - 4, size - 4), outline=CASING_DARK + (255,))
    for (x, y) in ((4, 4), (size - 6, 4), (4, size - 6), (size - 6, size - 6)):
        d.rectangle((x, y, x + 1, y + 1), fill=CASING_HI + (255,))
        d.point((x + 1, y + 1), fill=EDGE + (255,))
    return img


def side_texture(seed, accent):
    img = casing(seed)
    d = ImageDraw.Draw(img)
    for i in range(4):
        y = 10 + i * 4
        d.rectangle((9, y, 22, y + 1), fill=EDGE + (255,))
        d.line((9, y + 2, 22, y + 2), fill=CASING_LIGHT + (255,))
    d.rectangle((6, 26, 25, 27), fill=shade(accent, 0.75) + (255,))
    d.line((6, 26, 25, 26), fill=accent + (255,))
    return img


def top_texture(seed, accent):
    img = casing(seed + 1)
    d = ImageDraw.Draw(img)
    d.rectangle((8, 8, 23, 23), outline=CASING_DARK + (255,))
    d.rectangle((9, 9, 22, 22), outline=CASING_LIGHT + (255,))
    d.line((9, 9, 22, 22), fill=CASING_DARK + (255,))
    d.line((9, 22, 22, 9), fill=CASING_DARK + (255,))
    d.rectangle((14, 14, 17, 17), fill=accent + (255,))
    d.rectangle((15, 15, 16, 16), fill=shade(accent, 1.3) + (255,))
    return img


def bottom_texture(seed):
    img = casing(seed + 2, base=shade(CASING, 0.8))
    d = ImageDraw.Draw(img)
    for x in range(7, 26, 4):
        d.line((x, 6, x, 25), fill=EDGE + (255,))
    return img


def screen(img, inner=(6, 6, 25, 25), glow=None):
    d = ImageDraw.Draw(img)
    x0, y0, x1, y1 = inner
    d.rectangle((x0 - 1, y0 - 1, x1 + 1, y1 + 1), outline=CASING_HI + (255,))
    d.rectangle(inner, fill=(14, 16, 20, 255), outline=EDGE + (255,))
    if glow:
        d.rectangle((x0 + 1, y0 + 1, x1 - 1, y1 - 1), outline=shade(glow, 0.35) + (255,))
    return d


def front_texture(name, frame, active):
    """Front face. Inactive = frame 0 without glow. Active textures get 4 animation frames."""
    accent = ACCENTS[name]
    img = casing(sum(map(ord, name)) % 1000)
    d = screen(img, glow=accent if active else None)
    on = active
    cx, cy = 15.5, 15.5
    if name == 'electric_furnace':
        for i in range(4):
            y = 10 + i * 4
            if on:
                c = mix((255, 220, 120), accent, ((i + frame) % 4) / 4)
            else:
                c = (60, 64, 70)
            d.rectangle((9, y, 22, y + 1), fill=c + (255,))
        d.rectangle((8, 24, 23, 24), fill=(80, 84, 92, 255))
    elif name == 'crusher':
        for k, ox in enumerate((11, 20)):
            for t in range(8):
                a = (t / 8) * 2 * math.pi + (frame * math.pi / 8 if on else 0) * (1 if k == 0 else -1)
                x = ox + math.cos(a) * 4.5
                y = cy + math.sin(a) * 4.5
                d.rectangle((round(x) - 1, round(y) - 1, round(x), round(y)), fill=accent + (255,))
            d.ellipse((ox - 3, cy - 3, ox + 3, cy + 3), fill=(70, 75, 85, 255), outline=(110, 118, 130, 255))
            d.point((ox, round(cy)), fill=(30, 30, 30, 255))
        if on:
            for i in range(3):
                d.point((15 + (i + frame) % 2, 22 + i), fill=(200, 180, 150, 255))
    elif name == 'alloy_smelter':
        for ox in (10, 21):
            d.polygon([(ox - 3, 9), (ox + 3, 9), (ox + 2, 15), (ox - 2, 15)], fill=(90, 95, 105, 255), outline=(130, 136, 146, 255))
            if on:
                d.line((ox - 2, 10, ox + 2, 10), fill=accent + (255,))
        d.polygon([(11, 18), (20, 18), (19, 24), (12, 24)], fill=(90, 95, 105, 255), outline=(130, 136, 146, 255))
        if on:
            mol = mix((255, 240, 160), accent, frame / 4)
            d.rectangle((13, 19, 18, 22), fill=mol + (255,))
            d.line((12, 16 + frame % 2, 13, 17), fill=accent + (255,))
            d.line((19, 16 + (frame + 1) % 2, 18, 17), fill=accent + (255,))
    elif name == 'compressor':
        off = [0, 1, 2, 1][frame] if on else 0
        d.rectangle((9, 8 + off, 22, 11 + off), fill=(120, 128, 140, 255), outline=(160, 168, 180, 255))
        d.rectangle((9, 20 - off, 22, 23 - off), fill=(120, 128, 140, 255), outline=(160, 168, 180, 255))
        d.rectangle((14, 13 + 0, 17, 18), fill=accent + (255,) if on else (60, 90, 110, 255))
    elif name == 'combustion_generator':
        d.rectangle((9, 9, 22, 22), fill=(22, 20, 20, 255), outline=(80, 70, 60, 255))
        if on:
            r = rng(frame * 7 + 3)
            for x in range(10, 22):
                h = 4 + int(5 * (0.5 + 0.5 * math.sin(x * 1.3 + frame * 1.7))) + r.randint(0, 2)
                for y in range(22 - h, 22):
                    t = (22 - y) / max(1, h)
                    d.point((x, y), fill=mix((255, 230, 120), (220, 60, 20), t) + (255,))
        else:
            for x in range(10, 22, 3):
                d.line((x, 20, x + 1, 21), fill=(60, 55, 50, 255))
    elif name == 'geothermal_generator':
        for i in range(5):
            y = 9 + i * 3
            c = mix((255, 200, 60), accent, ((i + frame) % 5) / 5) if on else (90, 50, 40)
            d.rectangle((9, y, 22, y + 1), fill=c + (255,))
        d.rectangle((9, 24, 22, 24), fill=(60, 60, 66, 255))
    elif name == 'quarry':
        pts = [(10, 9), (21, 9), (15.5, 23)]
        d.polygon(pts, fill=(70, 160, 160, 255) if not on else accent + (255,), outline=(180, 240, 240, 255))
        for i in range(4):
            y = 11 + i * 3 + (frame % 3 if on else 0)
            if y < 21:
                w = int(5 * (23 - y) / 14)
                d.line((15 - w, y, 16 + w, y - 1), fill=(30, 90, 90, 255))
        d.rectangle((14, 7, 17, 8), fill=(160, 168, 180, 255))
    elif name == 'crop_harvester':
        d.rectangle((8, 21, 23, 23), fill=(110, 75, 45, 255))
        for k, x in enumerate((10, 15, 20)):
            sway = (1 if (frame + k) % 2 else 0) if on else 0
            d.line((x, 21, x + sway, 12), fill=(90, 170, 60, 255))
            d.rectangle((x - 1 + sway, 10, x + 1 + sway, 13), fill=(220, 190, 70, 255))
        if on:
            d.line((8, 17 - frame % 2, 23, 17 - frame % 2), fill=(200, 210, 220, 255))
    elif name == 'mob_grinder':
        a0 = frame * math.pi / 8 if on else 0
        for k in range(4):
            a = a0 + k * math.pi / 2
            x = cx + math.cos(a) * 7
            y = cy + math.sin(a) * 7
            x2 = cx + math.cos(a + 0.6) * 4
            y2 = cy + math.sin(a + 0.6) * 4
            d.polygon([(cx, cy), (x, y), (x2, y2)], fill=accent + (255,) if on else (130, 60, 65, 255))
        d.ellipse((cx - 2, cy - 2, cx + 2, cy + 2), fill=(170, 175, 185, 255))
    elif name == 'block_breaker':
        d.line((10, 22, 20, 12), fill=(140, 100, 60, 255), width=2)
        d.arc((12, 7, 26, 21), 200, 330, fill=(200, 205, 215, 255), width=2)
        if on:
            for i in range(3):
                d.point((22 - frame + i, 10 + i * 2), fill=(255, 255, 255, 255))
    elif name == 'block_placer':
        d.rectangle((10, 13, 19, 22), fill=(120, 90, 60, 255), outline=(160, 125, 85, 255))
        ox = (frame % 3) if on else 0
        d.polygon([(20 + ox, 12), (23 + ox, 15), (20 + ox, 18)], fill=accent + (255,))
        d.line((14, 9, 22 + ox, 9), fill=accent + (255,))
    return img


def animate(frames):
    """Stack frames vertically for a .mcmeta animation."""
    w, h = frames[0].size
    strip = Image.new('RGBA', (w, h * len(frames)))
    for i, f in enumerate(frames):
        strip.paste(f, (0, i * h))
    return strip


def cell_side(tier):
    c = TIERS[tier]
    img = casing(77 + len(tier))
    d = ImageDraw.Draw(img)
    d.rectangle((8, 5, 23, 26), fill=(20, 22, 26, 255), outline=shade(c, 0.6) + (255,))
    for i in range(5):
        y = 8 + i * 4
        d.rectangle((10, y, 21, y + 2), fill=shade(c, 0.55 + 0.1 * i) + (255,))
    d.line((10, 8, 21, 8), fill=shade(c, 1.25) + (255,))
    return img


def cell_front(tier):
    c = TIERS[tier]
    img = cell_side(tier)
    d = ImageDraw.Draw(img)
    d.ellipse((11, 11, 20, 20), fill=(20, 22, 26, 255), outline=c + (255,))
    d.rectangle((14, 13, 17, 18), fill=c + (255,))
    d.polygon([(15, 12), (12, 16), (19, 16)], fill=c + (255,))
    return img


def cell_top(tier):
    c = TIERS[tier]
    img = casing(91 + len(tier))
    d = ImageDraw.Draw(img)
    d.rectangle((10, 10, 21, 21), fill=(20, 22, 26, 255), outline=c + (255,))
    d.polygon([(17, 11), (12, 17), (15, 17), (14, 21), (19, 14), (16, 14)], fill=c + (255,))
    return img


def frame_texture(advanced):
    base = (78, 86, 98) if advanced else (110, 116, 124)
    img = casing(301 if advanced else 300, base=base)
    d = ImageDraw.Draw(img)
    trim = (230, 180, 60) if advanced else (200, 120, 70)
    d.rectangle((6, 6, 25, 25), outline=trim + (255,))
    d.line((6, 6, 25, 25), fill=shade(base, 0.6) + (255,))
    d.line((6, 25, 25, 6), fill=shade(base, 0.6) + (255,))
    return img


def steel_block():
    img = Image.new('RGBA', (32, 32))
    noise_fill(img, (0, 0, 32, 32), (122, 130, 140), 0.08, 400)
    d = ImageDraw.Draw(img)
    d.rectangle((0, 0, 31, 31), outline=(80, 86, 94, 255))
    d.line((1, 1, 30, 1), fill=(170, 178, 188, 255))
    d.line((1, 1, 1, 30), fill=(170, 178, 188, 255))
    for y in (10, 21):
        d.line((1, y, 30, y), fill=(95, 102, 110, 255))
        d.line((1, y + 1, 30, y + 1), fill=(150, 158, 168, 255))
    return img


# ---------------------------------------------------------------- conduits (16x16)

def conduit_texture(core, band, seed):
    img = Image.new('RGBA', (16, 16))
    noise_fill(img, (0, 0, 16, 16), core, 0.12, seed)
    d = ImageDraw.Draw(img)
    for x in (0, 15):
        d.line((x, 0, x, 15), fill=shade(core, 0.55) + (255,))
    for x in range(2, 14, 4):
        d.line((x, 0, x, 15), fill=band + (255,))
    d.line((0, 5, 15, 5), fill=shade(core, 1.25) + (255,))
    return img


def connector_texture(ring, extract=False):
    img = Image.new('RGBA', (16, 16))
    noise_fill(img, (0, 0, 16, 16), (50, 56, 64), 0.1, 501)
    d = ImageDraw.Draw(img)
    d.rectangle((0, 0, 15, 15), outline=(20, 22, 26, 255))
    d.rectangle((3, 3, 12, 12), outline=ring + (255,))
    if extract:
        d.polygon([(8, 4), (4, 9), (12, 9)], fill=ring + (255,))
        d.rectangle((6, 9, 9, 12), fill=ring + (255,))
    else:
        d.rectangle((6, 6, 9, 9), fill=ring + (255,))
    return img


# ---------------------------------------------------------------- items (16x16)

def item_canvas():
    return Image.new('RGBA', (16, 16), (0, 0, 0, 0))


def outline(img, color=(20, 20, 24, 255)):
    px = img.load()
    out = img.copy()
    po = out.load()
    for y in range(16):
        for x in range(16):
            if px[x, y][3] == 0:
                for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1)):
                    nx, ny = x + dx, y + dy
                    if 0 <= nx < 16 and 0 <= ny < 16 and px[nx, ny][3] > 0:
                        po[x, y] = color
                        break
    return out


def dust(color, seed):
    img = item_canvas()
    r = rng(seed)
    px = img.load()
    for y in range(6, 14):
        half = int((y - 5) * 0.9) + 1
        for x in range(8 - half, 8 + half):
            if 1 <= x <= 14:
                f = 0.75 + 0.45 * r.random() + (13 - y) * 0.02
                px[x, y] = shade(color, f) + (255,)
    for _ in range(5):
        x, y = r.randint(4, 11), r.randint(9, 13)
        px[x, y] = shade(color, 1.4) + (255,)
    return outline(img)


def ingot(color):
    img = item_canvas()
    d = ImageDraw.Draw(img)
    d.polygon([(2, 10), (5, 6), (14, 6), (11, 10)], fill=shade(color, 1.15) + (255,))
    d.polygon([(2, 10), (11, 10), (11, 13), (2, 13)], fill=color + (255,))
    d.polygon([(11, 10), (14, 6), (14, 9), (11, 13)], fill=shade(color, 0.7) + (255,))
    d.line((5, 7, 12, 7), fill=shade(color, 1.45) + (255,))
    return outline(img)


def plate(color):
    img = item_canvas()
    d = ImageDraw.Draw(img)
    d.polygon([(2, 9), (8, 5), (14, 8), (8, 12)], fill=shade(color, 1.1) + (255,))
    d.polygon([(2, 9), (8, 12), (8, 13), (2, 10)], fill=shade(color, 0.75) + (255,))
    d.polygon([(8, 12), (14, 8), (14, 9), (8, 13)], fill=shade(color, 0.6) + (255,))
    d.line((5, 8, 9, 6), fill=shade(color, 1.4) + (255,))
    return outline(img)


def circuit(chip):
    img = item_canvas()
    d = ImageDraw.Draw(img)
    d.rectangle((1, 3, 14, 12), fill=(36, 110, 60, 255))
    d.rectangle((1, 3, 14, 3), fill=(60, 150, 85, 255))
    for x in range(2, 14, 3):
        d.line((x, 4, x, 11), fill=(200, 170, 70, 255))
    d.rectangle((5, 5, 10, 10), fill=(25, 25, 30, 255))
    d.rectangle((6, 6, 9, 9), fill=chip + (255,))
    return outline(img)


def upgrade(color, kind):
    img = item_canvas()
    d = ImageDraw.Draw(img)
    d.rectangle((2, 2, 13, 13), fill=(40, 46, 54, 255))
    d.rectangle((3, 3, 12, 12), fill=(28, 32, 38, 255))
    d.line((2, 2, 13, 2), fill=color + (255,))
    d.line((2, 13, 13, 13), fill=shade(color, 0.6) + (255,))
    if kind == 'speed':
        d.polygon([(5, 4), (9, 8), (5, 12)], fill=color + (255,))
        d.polygon([(8, 4), (12, 8), (8, 12)], fill=shade(color, 1.3) + (255,))
    elif kind == 'energy':
        d.polygon([(9, 3), (5, 9), (8, 9), (7, 13), (11, 7), (8, 7)], fill=color + (255,))
    else:
        d.ellipse((4, 4, 11, 11), outline=color + (255,))
        d.ellipse((6, 6, 9, 9), outline=shade(color, 1.3) + (255,))
        d.point((7, 7), fill=(255, 255, 255, 255))
    return outline(img)


def multimeter():
    img = item_canvas()
    d = ImageDraw.Draw(img)
    d.rectangle((3, 1, 12, 14), fill=(230, 170, 40, 255))
    d.rectangle((4, 2, 11, 7), fill=(30, 50, 40, 255))
    d.line((5, 6, 7, 4), fill=(110, 240, 140, 255))
    d.line((7, 4, 10, 5), fill=(110, 240, 140, 255))
    d.ellipse((5, 9, 10, 13), fill=(40, 40, 46, 255))
    d.line((7, 11, 9, 10), fill=(230, 230, 230, 255))
    return outline(img)


def guide_book():
    img = item_canvas()
    d = ImageDraw.Draw(img)
    d.rectangle((3, 2, 13, 14), fill=(40, 90, 140, 255))
    d.rectangle((2, 2, 3, 14), fill=(25, 60, 95, 255))
    d.rectangle((4, 13, 13, 14), fill=(235, 230, 215, 255))
    d.ellipse((6, 5, 11, 10), outline=(240, 190, 60, 255))
    d.point((8, 7), fill=(240, 190, 60, 255))
    for (x, y) in ((8, 4), (8, 11), (5, 7), (12, 7)):
        d.point((x, y), fill=(240, 190, 60, 255))
    return outline(img)


# ---------------------------------------------------------------- GUI atlas (256x256)

PANEL = (54, 60, 70)
PANEL_LIGHT = (92, 101, 115)
PANEL_DARK = (28, 31, 37)
SLOT_BG = (24, 27, 32)


def bevel_box(d, box, fill, light, dark):
    x0, y0, x1, y1 = box
    d.rectangle(box, fill=fill)
    d.line((x0, y0, x1 - 1, y0), fill=light)
    d.line((x0, y0, x0, y1 - 1), fill=light)
    d.line((x1, y0 + 1, x1, y1), fill=dark)
    d.line((x0 + 1, y1, x1, y1), fill=dark)


def slot(d, x, y, w=18, h=18):
    d.rectangle((x, y, x + w - 1, y + h - 1), fill=SLOT_BG + (255,))
    d.line((x, y, x + w - 2, y), fill=PANEL_DARK + (255,))
    d.line((x, y, x, y + h - 2), fill=PANEL_DARK + (255,))
    d.line((x + w - 1, y + 1, x + w - 1, y + h - 1), fill=PANEL_LIGHT + (255,))
    d.line((x + 1, y + h - 1, x + w - 1, y + h - 1), fill=PANEL_LIGHT + (255,))


def gui_atlas():
    img = Image.new('RGBA', (256, 256), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    # main panel 176x166
    bevel_box(d, (0, 0, 175, 165), PANEL + (255,), PANEL_LIGHT + (255,), PANEL_DARK + (255,))
    d.rectangle((0, 0, 175, 165), outline=(12, 14, 17, 255))
    d.rectangle((3, 3, 172, 14), fill=(44, 49, 58, 255))
    d.line((3, 15, 172, 15), fill=(39, 125, 190, 255))
    for row in range(3):
        for col in range(9):
            slot(d, 7 + col * 18, 83 + row * 18)
    for col in range(9):
        slot(d, 7 + col * 18, 141)
    # slot frames
    slot(d, 176, 0)
    slot(d, 176, 18, 26, 26)
    # energy bar frame (202,0) 14x52 and fill (216,0) 12x52
    d.rectangle((202, 0, 215, 51), fill=SLOT_BG + (255,), outline=(12, 14, 17, 255))
    for y in range(1, 51):
        t = y / 50
        c = mix((255, 90, 60), (255, 210, 60), 1 - t) if t > 0.5 else mix((255, 210, 60), (80, 230, 120), 1 - t * 2)
        d.line((216, y, 227, y), fill=c + (255,))
        if y % 5 == 0:
            d.line((216, y, 227, y), fill=shade(c, 0.75) + (255,))
    # progress arrow (176,52) empty / (200,52) full
    for ox, col in ((176, (70, 76, 86)), (200, (70, 200, 255))):
        d.rectangle((ox, 58, ox + 15, 62), fill=col + (255,))
        d.polygon([(ox + 15, 52), (ox + 23, 60), (ox + 15, 68)], fill=col + (255,))
    # flame (176,70) empty / (190,70) full
    for ox, top, bot in ((176, (70, 76, 86), (60, 64, 72)), (190, (255, 230, 120), (230, 70, 20))):
        for y in range(14):
            w = int(6 * math.sin(math.pi * (y + 1) / 15)) + (1 if y > 6 else 0)
            c = mix(top, bot, y / 13)
            d.line((ox + 7 - w, 70 + y, ox + 6 + w, 70 + y), fill=c + (255,))
    # redstone icons (176/192/208, 84): ignored, high, low
    for i, ox in enumerate((176, 192, 208)):
        d.rectangle((ox + 3, 84 + 11, ox + 12, 84 + 13), fill=(120, 120, 128, 255))
        d.rectangle((ox + 6, 84 + 4, ox + 9, 84 + 11), fill=(140, 100, 60, 255))
        torch = [(130, 130, 136), (255, 50, 40), (90, 20, 20)][i]
        d.rectangle((ox + 6, 84 + 2, ox + 9, 84 + 5), fill=torch + (255,))
        if i == 0:
            d.line((ox + 2, 84 + 2, ox + 13, 84 + 13), fill=(230, 230, 230, 255))
    # auto eject on (224,84) / off (240,84)
    for ox, col in ((224, (90, 220, 120)), (240, (110, 110, 118))):
        d.rectangle((ox + 2, 84 + 6, ox + 9, 84 + 9), fill=col + (255,))
        d.polygon([(ox + 9, 84 + 3), (ox + 14, 84 + 8), (ox + 9, 84 + 12)], fill=col + (255,))
    # tab background (176,104) 22x22
    bevel_box(d, (176, 104, 197, 125), PANEL + (255,), PANEL_LIGHT + (255,), PANEL_DARK + (255,))
    d.rectangle((176, 104, 197, 125), outline=(12, 14, 17, 255))
    # side config panel (0,166) 62x70
    bevel_box(d, (0, 166, 61, 235), PANEL + (255,), PANEL_LIGHT + (255,), PANEL_DARK + (255,))
    d.rectangle((0, 166, 61, 235), outline=(12, 14, 17, 255))
    return img
