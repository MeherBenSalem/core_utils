"""Machine block textures (32x32): brushed steel casing with per-machine accent trim, and illustrated fronts.

Every front has an idle frame and a 4-frame animated active strip. Shapes are drawn pixel by pixel with explicit
3-tone shading (light top-left, dark bottom-right) so they read at a distance, which the flat procedural
icons did not.
"""
import math
import random

from PIL import Image

OUT = (24, 27, 33)
STEEL = (104, 113, 126)
STEEL_HI = (150, 159, 172)
STEEL_LO = (70, 77, 88)
PANEL = (86, 94, 106)
WINDOW = (18, 21, 26)
WINDOW_RIM = (44, 49, 57)


def col(c, a=255):
    return tuple(int(max(0, min(255, v))) for v in c[:3]) + (a,)


def shade(c, f):
    return tuple(max(0, min(255, int(v * f))) for v in c[:3])


def mix(a, b, t):
    return tuple(int(a[i] + (b[i] - a[i]) * t) for i in range(3))


class Canvas:
    def __init__(self, w=32, h=32):
        self.img = Image.new('RGBA', (w, h), (0, 0, 0, 0))
        self.px = self.img.load()
        self.w, self.h = w, h

    def set(self, x, y, c):
        if 0 <= x < self.w and 0 <= y < self.h:
            self.px[int(x), int(y)] = col(c)

    def get(self, x, y):
        return self.px[x, y][:3]

    def rect(self, x0, y0, x1, y1, c):
        for y in range(y0, y1 + 1):
            for x in range(x0, x1 + 1):
                self.set(x, y, c)

    def frame(self, x0, y0, x1, y1, light, dark):
        for x in range(x0, x1 + 1):
            self.set(x, y0, light)
            self.set(x, y1, dark)
        for y in range(y0, y1 + 1):
            self.set(x0, y, light)
            self.set(x1, y, dark)

    def disc(self, cx, cy, r, fn):
        """fn(dx, dy, dist) -> colour or None, over a filled disc."""
        for y in range(int(cy - r - 1), int(cy + r + 2)):
            for x in range(int(cx - r - 1), int(cx + r + 2)):
                dx, dy = x + 0.5 - cx, y + 0.5 - cy
                d = math.hypot(dx, dy)
                if d <= r:
                    c = fn(dx, dy, d)
                    if c is not None:
                        self.set(x, y, c)


def casing(accent, seed):
    cv = Canvas()
    r = random.Random(seed)
    for y in range(32):
        row = 1 + (r.random() - 0.5) * 0.06
        for x in range(32):
            cv.set(x, y, shade(STEEL, row * (1 + (r.random() - 0.5) * 0.05)))
    cv.frame(0, 0, 31, 31, OUT, OUT)
    cv.frame(1, 1, 30, 30, STEEL_HI, STEEL_LO)
    cv.frame(3, 3, 28, 28, STEEL_LO, STEEL_HI)
    # accent corner brackets
    a, ad = accent, shade(accent, 0.6)
    for (x, y, sx, sy) in ((1, 1, 1, 1), (30, 1, -1, 1), (1, 30, 1, -1), (30, 30, -1, -1)):
        for i in range(4):
            cv.set(x + sx * i, y, a if i < 3 else ad)
            cv.set(x, y + sy * i, a if i < 3 else ad)
    # rivets
    for (x, y) in ((5, 5), (26, 5), (5, 26), (26, 26)):
        cv.set(x, y, STEEL_HI)
        cv.set(x + 1, y + 1, OUT)
        cv.set(x + 1, y, STEEL_LO)
        cv.set(x, y + 1, STEEL_LO)
    return cv


def window(cv, x0=6, y0=6, x1=25, y1=25, glow=None):
    cv.rect(x0 - 1, y0 - 1, x1 + 1, y1 + 1, WINDOW_RIM)
    cv.frame(x0 - 1, y0 - 1, x1 + 1, y1 + 1, OUT, STEEL_HI)
    cv.rect(x0, y0, x1, y1, WINDOW)
    if glow:
        for i in range(2):
            cv.frame(x0 + i, y0 + i, x1 - i, y1 - i, shade(glow, 0.45 - 0.2 * i), shade(glow, 0.3 - 0.15 * i))


def led(cv, on):
    c = (90, 255, 120) if on else (150, 40, 40)
    cv.set(15, 3, c)
    cv.set(16, 3, shade(c, 0.7))


def side(accent, seed):
    cv = casing(accent, seed)
    cv.rect(6, 6, 25, 25, PANEL)
    cv.frame(6, 6, 25, 25, STEEL_LO, STEEL_HI)
    for i in range(5):
        y = 9 + i * 3
        for x in range(9, 23):
            cv.set(x, y, OUT)
            cv.set(x, y + 1, STEEL_HI if x > 9 else STEEL_LO)
    for x in range(7, 25):
        cv.set(x, 24, accent)
        cv.set(x, 23, shade(accent, 0.65))
    return cv.img


def top(accent, seed):
    cv = casing(accent, seed + 1)
    cv.rect(7, 7, 24, 24, PANEL)
    cv.frame(7, 7, 24, 24, STEEL_LO, STEEL_HI)
    cv.disc(16, 16, 8, lambda dx, dy, d: OUT if d > 7 else (WINDOW if (int(math.degrees(math.atan2(dy, dx)) + 360) // 30) % 2 else STEEL_LO))
    cv.disc(16, 16, 2.6, lambda dx, dy, d: accent if d < 1.6 else shade(accent, 0.6))
    return cv.img


def bottom(accent, seed):
    cv = casing(accent, seed + 2)
    for x in range(7, 25, 3):
        for y in range(6, 26):
            cv.set(x, y, OUT)
            cv.set(x + 1, y, STEEL_LO)
    return cv.img


# ---------------------------------------------------------------- front art (window is 6..25)

def gear(cv, cx, cy, r, teeth, phase, base, light, dark):
    def fn(dx, dy, d):
        a = math.atan2(dy, dx) - phase
        tooth = (math.cos(a * teeth) > 0.2)
        outer = r if tooth else r - 1.6
        if d > outer:
            return None
        if d < r * 0.32:
            return OUT
        if d > outer - 1:
            return dark if dx + dy > 0 else light
        return mix(light, base, min(1, (dx + dy + r) / (2 * r)))
    cv.disc(cx, cy, r, fn)


def flames(cv, frame, x0, x1, base_y, height, seed):
    r = random.Random(seed + frame * 13)
    for x in range(x0, x1 + 1):
        h = height * (0.55 + 0.45 * math.sin(x * 0.9 + frame * 1.6)) + r.uniform(-1, 1)
        for i in range(int(h)):
            t = i / max(1, h)
            cv.set(x, base_y - i, mix((255, 245, 170), (220, 50, 10), t ** 0.8) if t < 0.95 else (120, 20, 5))


def front(name, accent, frame, active):
    cv = casing(accent, sum(map(ord, name)))
    window(cv, glow=accent if active else None)
    led(cv, active)
    f = frame if active else 0
    if name == 'electric_furnace':
        cv.rect(8, 9, 23, 22, (30, 26, 24))
        cv.frame(8, 9, 23, 22, (60, 54, 50), OUT)
        for i in range(4):
            y = 11 + i * 3
            for x in range(10, 22):
                if active:
                    t = (math.sin(x * 0.7 + i + f * 1.5) + 1) / 2
                    c = mix((255, 120, 30), (255, 230, 140), t)
                else:
                    c = (78, 70, 66) if x % 2 else (64, 58, 55)
                cv.set(x, y, c)
                cv.set(x, y + 1, shade(c, 0.55))
        if active:
            for x in range(9, 23):
                cv.set(x, 21, (255, 170, 60) if (x + f) % 3 else (255, 220, 120))
    elif name == 'crusher':
        phase = (f * math.pi / 8) if active else 0
        gear(cv, 11, 13, 5.5, 8, phase, (150, 158, 170), (210, 216, 226), (70, 76, 86))
        gear(cv, 20.5, 13, 5.5, 8, -phase + math.pi / 8, (150, 158, 170), (210, 216, 226), (70, 76, 86))
        for x in range(8, 24):
            cv.set(x, 22, (60, 56, 52))
            cv.set(x, 23, (40, 38, 36))
        if active:
            r = random.Random(f)
            for i in range(6):
                cv.set(15 + r.randint(0, 2), 19 + (i + f) % 3, (196, 184, 176))
            for x in range(9, 23, 2):
                cv.set(x + f % 2, 21, (196, 184, 176))
    elif name == 'alloy_smelter':
        for cx in (11, 20):
            for y in range(8, 14):
                w = 3 - (y - 8) // 3
                for x in range(cx - w, cx + w + 1):
                    cv.set(x, y, (120, 110, 104) if x < cx else (90, 82, 78))
                cv.set(cx - w - 1, y, OUT)
                cv.set(cx + w + 1, y, OUT)
            if active:
                cv.rect(cx - 2, 8, cx + 2, 9, (255, 190, 60))
        for y in range(17, 24):
            w = 5 - (y - 17) // 3
            for x in range(15 - w, 17 + w):
                cv.set(x, y, (110, 102, 96) if x < 16 else (84, 78, 74))
        if active:
            cv.rect(12, 17, 19, 18, mix((255, 240, 160), (255, 140, 40), f / 4))
            for i in range(3):
                cv.set(13 + i // 2, 14 + i, (255, 200, 80))
                cv.set(18 - i // 2, 14 + i, (255, 200, 80))
    elif name == 'compressor':
        off = [0, 2, 4, 2][f] if active else 0
        cv.rect(14, 7, 17, 9 + off, (120, 128, 140))
        cv.rect(9, 10 + off, 22, 13 + off, (170, 178, 190))
        cv.frame(9, 10 + off, 22, 13 + off, (220, 226, 234), (90, 96, 106))
        cv.rect(9, 20, 22, 23, (170, 178, 190))
        cv.frame(9, 20, 22, 23, (220, 226, 234), (90, 96, 106))
        plate = (90, 170, 230) if active else (70, 110, 140)
        cv.rect(11, 18, 20, 19, plate)
        for x in range(11, 21):
            cv.set(x, 18, shade(plate, 1.3))
    elif name == 'combustion_generator':
        cv.rect(8, 8, 23, 23, (26, 22, 20))
        for x in range(8, 24):
            cv.set(x, 23, (70, 64, 60) if x % 2 else (50, 46, 44))
        for x in range(9, 23, 3):
            cv.set(x, 22, (40, 34, 30))
            cv.set(x + 1, 22, (90, 70, 50) if active else (55, 50, 46))
        if active:
            flames(cv, f, 9, 22, 21, 11, 5)
        for y in range(8, 24, 3):
            for x in range(8, 24):
                if cv.get(x, y)[0] < 40:
                    cv.set(x, y, (36, 30, 28))
    elif name == 'geothermal_generator':
        for i, y in enumerate(range(8, 24, 3)):
            for x in range(8, 24):
                if active:
                    t = (math.sin(x * 0.5 + y * 0.3 - f * 1.4) + 1) / 2
                    c = mix((230, 70, 20), (255, 200, 60), t)
                else:
                    c = (100, 48, 36)
                cv.set(x, y, c)
                cv.set(x, y + 1, shade(c, 0.5))
        for x in (8, 23):
            for y in range(7, 25):
                cv.set(x, y, (120, 128, 140))
    elif name == 'quarry':
        cv.rect(14, 7, 17, 9, (170, 178, 190))
        for y in range(10, 24):
            w = max(0, int((24 - y) * 0.45))
            for x in range(16 - w - 1, 16 + w + 1):
                stripe = ((x + y * 2 + (f * 2 if active else 0)) // 2) % 2
                base = (90, 220, 210) if active else (70, 160, 160)
                c = shade(base, 1.15 if stripe else 0.75)
                if x < 16 - w or x > 16 + w - 1:
                    c = shade(base, 0.5)
                cv.set(x, y, c)
        cv.set(15, 24, (200, 255, 250))
        if active:
            for i in range(4):
                cv.set(10 + (i * 5 + f) % 12, 23 - (i + f) % 2, (140, 110, 80))
    elif name == 'crop_harvester':
        cv.rect(7, 21, 24, 24, (100, 66, 40))
        for x in range(7, 25):
            cv.set(x, 21, (130, 90, 56))
        for k, x in enumerate((10, 15, 20)):
            sway = (1 if (f + k) % 2 else 0) if active else 0
            for y in range(12, 21):
                cv.set(x + (sway if y < 16 else 0), y, (90, 160, 60))
            for y in range(8, 13):
                cv.set(x - 1 + sway, y, (230, 200, 80))
                cv.set(x + sway, y, (250, 220, 110))
                cv.set(x + 1 + sway, y, (200, 165, 60))
        if active:
            y = 17 - f % 2
            for x in range(7, 25):
                cv.set(x, y, (210, 216, 226) if x % 4 else (150, 156, 166))
    elif name == 'mob_grinder':
        phase = f * math.pi / 8 if active else 0
        blade = (220, 60, 70) if active else (140, 70, 76)

        def saw(dx, dy, d):
            a = math.atan2(dy, dx) - phase
            if d > 8.5 - (2 if (math.degrees(a) % 30) > 15 else 0):
                return None
            if d < 2:
                return (190, 196, 206)
            if d < 3:
                return OUT
            return shade(blade, 1.25 if dx + dy < 0 else 0.85)
        cv.disc(15.5, 15.5, 9, saw)
    elif name == 'block_breaker':
        crack = (f + 1) if active else 0
        cv.rect(14, 12, 23, 21, (128, 128, 132))
        cv.frame(14, 12, 23, 21, (170, 170, 176), (80, 80, 84))
        for i in range(crack * 2):
            cv.set(18 + (i % 3) - 1, 13 + i, OUT)
        for i in range(11):
            cv.set(8 + i, 23 - i, (150, 105, 60))
            cv.set(8 + i, 22 - i, (186, 136, 84))
        head = [(11, 10), (12, 9), (13, 8), (14, 8), (15, 7), (16, 7), (17, 7), (18, 8), (19, 8), (20, 9), (21, 10), (22, 11)]
        for i, (x, y) in enumerate(head):
            cv.set(x, y, (226, 232, 240))
            cv.set(x, y + 1, (150, 156, 166))
        cv.set(10, 11, (150, 156, 166))
        cv.set(23, 12, (150, 156, 166))
    elif name == 'block_placer':
        cv.rect(8, 11, 17, 20, (150, 108, 64))
        cv.frame(8, 11, 17, 20, (190, 140, 90), (100, 70, 40))
        ox = [0, 1, 2, 1][f] if active else 0
        arrow = (120, 160, 240) if active else (80, 100, 150)
        for i in range(4):
            for y in range(15 - i, 17 + i):
                cv.set(23 + ox - i, y, arrow)
        cv.rect(18 + ox, 15, 20 + ox, 16, arrow)
    return cv.img


def strip(frames):
    w, h = frames[0].size
    out = Image.new('RGBA', (w, h * len(frames)))
    for i, f in enumerate(frames):
        out.paste(f, (0, i * h))
    return out


def cell(tier_color, kind, seed):
    """Energy cell faces: 'side' shows the charge bars, 'front' the output port, 'top' a bolt."""
    cv = casing(tier_color, seed)
    if kind == 'side':
        window(cv, 9, 6, 22, 25)
        for i in range(6):
            y = 8 + i * 3
            c = shade(tier_color, 0.55 + 0.09 * (5 - i))
            for x in range(11, 21):
                cv.set(x, y, c if x < 20 else shade(c, 0.7))
                cv.set(x, y + 1, shade(c, 0.6))
        for x in range(11, 21):
            cv.set(x, 8, shade(tier_color, 1.3))
    elif kind == 'front':
        window(cv)
        cv.disc(16, 16, 7, lambda dx, dy, d: OUT if d > 6 else (shade(tier_color, 0.35) if d > 4.5 else WINDOW))
        for y in range(9, 14):
            for x in range(16 - (y - 9) - 1, 16 + (y - 9) + 1):
                cv.set(x, y, tier_color if x < 16 else shade(tier_color, 0.75))
        for y in range(14, 22):
            cv.set(15, y, tier_color)
            cv.set(16, y, shade(tier_color, 0.75))
    else:
        window(cv, 9, 9, 22, 22)
        bolt = [(17, 10), (16, 11), (16, 12), (15, 13), (14, 14), (15, 14), (16, 14), (17, 14), (18, 14), (17, 15), (16, 16), (16, 17), (15, 18), (15, 19), (14, 20)]
        for x, y in bolt:
            cv.set(x, y, tier_color)
            cv.set(x + 1, y, shade(tier_color, 0.7))
    return cv.img


def frame(advanced):
    accent = (240, 190, 60) if advanced else (200, 120, 70)
    cv = casing(accent, 300 + advanced)
    cv.rect(6, 6, 25, 25, PANEL)
    cv.frame(6, 6, 25, 25, STEEL_LO, STEEL_HI)
    for i in range(20):
        cv.set(6 + i, 6 + i, STEEL_LO)
        cv.set(25 - i, 6 + i, STEEL_LO)
        cv.set(7 + i, 6 + i, STEEL_HI)
    cv.frame(13, 13, 18, 18, accent, shade(accent, 0.6))
    return cv.img
