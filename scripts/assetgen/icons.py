"""Hand-placed 16x16 item icons.

Each icon is a grid of characters; each character picks a colour from the icon's palette ('.' is transparent).
Material palettes are 5-step ramps (0 outline, 1 shadow, 2 base, 3 light, 4 highlight), lit from the top left
like vanilla items. Detail colours use letters.
"""
from PIL import Image


def ramp(outline, shadow, base, light, highlight, **extra):
    p = {'0': outline, '1': shadow, '2': base, '3': light, '4': highlight}
    p.update(extra)
    return p


def render(grid, palette):
    assert len(grid) == 16, len(grid)
    img = Image.new('RGBA', (16, 16), (0, 0, 0, 0))
    px = img.load()
    for y, row in enumerate(grid):
        assert len(row) == 16, (y, row, len(row))
        for x, ch in enumerate(row):
            if ch != '.':
                c = palette[ch]
                px[x, y] = tuple(c[:3]) + (c[3] if len(c) > 3 else 255,)
    return img


# ---------------------------------------------------------------- material ramps

IRON = ramp((52, 46, 44), (120, 110, 104), (170, 162, 156), (206, 200, 194), (240, 236, 232))
GOLD = ramp((92, 52, 10), (196, 120, 20), (240, 180, 40), (252, 224, 90), (255, 250, 190))
COPPER = ramp((74, 30, 18), (156, 72, 44), (204, 112, 72), (232, 150, 104), (250, 196, 156))
COAL = ramp((12, 12, 14), (30, 30, 34), (48, 48, 54), (72, 72, 80), (110, 110, 122))
STEEL = ramp((34, 38, 46), (78, 86, 98), (116, 125, 138), (158, 168, 181), (212, 220, 230))
ENERGIZED = ramp((80, 14, 10), (170, 40, 26), (226, 78, 46), (250, 140, 80), (255, 224, 170))
ENDER = ramp((10, 44, 42), (24, 104, 98), (42, 158, 142), (88, 212, 186), (190, 250, 232), p=(150, 60, 200), q=(210, 140, 250))

# ---------------------------------------------------------------- shapes

DUST = [
    "................",
    "................",
    "................",
    "................",
    "................",
    "........4.......",
    "......000.......",
    ".....04320..3...",
    "....0433210.....",
    "...043332110....",
    "..04332321210...",
    ".0433322221110..",
    ".0332222212110..",
    ".0221212111100..",
    "..000000000000..",
    "................",
]

INGOT = [
    "................",
    "................",
    "................",
    "................",
    "................",
    "....00000000....",
    "...0444444430...",
    "...0433333330...",
    "..042222222210..",
    ".03222222222110.",
    ".02222222221110.",
    ".01111111111110.",
    "..000000000000..",
    "................",
    "................",
    "................",
]

# Ender alloy: the ingot with violet ender specks
ENDER_INGOT = [
    "................",
    "................",
    "................",
    "................",
    "................",
    "....00000000....",
    "...0444444430...",
    "...0433q33330...",
    "..042222222210..",
    ".0322p2222p110..",
    ".02222222221110.",
    ".011p111111p110.",
    "..000000000000..",
    "................",
    "................",
    "................",
]
ENDER_INGOT[9] = ".0322p22222p110."
ENDER_INGOT[11] = ".011p11111111p0."

# Energized: the ingot with a crackle of energy above it
ENERGIZED_INGOT = [
    "................",
    "................",
    "................",
    "........Ee......",
    ".......Ee.......",
    "......EEEE......",
    "....000eE000....",
    "...044Ee44430...",
    "...0433333330...",
    "..042222222210..",
    ".03222222222110.",
    ".02222222221110.",
    ".01111111111110.",
    "..000000000000..",
    "................",
    "................",
]

PLATE = [
    "................",
    "................",
    "..000000000000..",
    ".04444444444430.",
    ".04r33333333r20.",
    ".043111111114 20.",
    ".04314444443120.",
    ".04314333332120.",
    ".04314333332120.",
    ".04314333332120.",
    ".04312222221120.",
    ".0433444444412 0.",
    ".04r22222222r10.",
    ".02222222222110.",
    ".01111111111110.",
    "..000000000000..",
]
PLATE = [r.replace(' ', '') for r in PLATE]
PLATE[5] = ".04311111111420."
PLATE[11] = ".04334444444120."

CIRCUIT = [
    "................",
    "................",
    ".....p.p.p......",
    "..oooooooooooo..",
    ".oLLLLLLLLLLLLo.",
    ".oLbggbbbbbbbBo.",
    ".oLbgbcccccbbBo.",
    ".oLggbcCCecbgBo.",
    ".oLbbbcCccgggBo.",
    ".oLbgbcccccbbBo.",
    ".oLbggbbbbbggBo.",
    ".oLbbbbbbbbbbBo.",
    ".oBBBBBBBBBBBBo.",
    "..oGoGoGoGoGoo..",
    "................",
    "................",
]
CIRCUIT[2] = "................"
CIRCUIT[13] = "..oGoGoGoGoGo...".replace('...', '.o.')[:16]

UPGRADE = [
    "................",
    "................",
    "...0000000000...",
    "..034444444430..",
    "..04ddddddddd0..",
    "..03d........0..",
    "..03d........0..",
    "..03d........0..",
    "..03d........0..",
    "..03d........0..",
    "..03d........0..",
    "..022222222210..",
    "..011111111110..",
    "...0G0G0G0G00...",
    "................",
    "................",
]

SPEED_SYMBOL = [
    "........",
    "s..s....",
    "Ss.Ss...",
    ".SS.SS..",
    "Ss.Ss...",
    "s..s....",
]
ENERGY_SYMBOL = [
    "....sS..",
    "...sS...",
    "..sSSSS.",
    "....Ss..",
    "...Ss...",
    "..s.....",
]
RANGE_SYMBOL = [
    "..sSs...",
    ".s...s..",
    "S.sSs.S.",
    "s.SSS.s.",
    ".s.s.s..",
    "..sSs...",
]

MULTIMETER = [
    "................",
    "...0000000000...",
    "..0YYYYYYYYYy0..",
    "..0Y00000000y0..",
    "..0Y0kkkkkk0y0..",
    "..0Y0kgkkgk0y0..",
    "..0Y0kkgkkg0y0..",
    "..0Y0gkkgkk0y0..",
    "..0Y00000000y0..",
    "..0YYYY00YYYy0..",
    "..0YYY0ww0YYy0..",
    "..0YYY0wW0YYy0..",
    "..0YYYY00YYYy0..",
    "..0YR0YYYY0by0..",
    "..0yyyyyyyyyy0..",
    "...0000000000...",
]

BOOK = [
    "................",
    "..00000000000...",
    ".0bBBBBBBBBBB0..",
    ".0bBBBBBBBBBBw0.",
    ".0bBBBgBgBBBBw0.",
    ".0bBBgggggBBBw0.",
    ".0bBBggGggBBBw0.",
    ".0bBgggBgggBBw0.",
    ".0bBBggGggBBBw0.",
    ".0bBBgggggBBBw0.",
    ".0bBBBgBgBBBBw0.",
    ".0bBBBBBBBBBBw0.",
    ".0bdddddddrdw0..",
    ".0bwwwwwwwrww0..",
    "..0000000000r0..",
    "................",
]

def stamp(grid, symbol, x0, y0):
    g = [list(r) for r in grid]
    for y, row in enumerate(symbol):
        for x, ch in enumerate(row):
            if ch != '.':
                g[y0 + y][x0 + x] = ch
    return [''.join(r) for r in g]


def icons():
    out = {}
    out['iron_dust'] = render(DUST, IRON)
    out['gold_dust'] = render(DUST, GOLD)
    out['copper_dust'] = render(DUST, COPPER)
    out['coal_dust'] = render(DUST, COAL)
    out['steel_ingot'] = render(INGOT, STEEL)
    out['energized_ingot'] = render(ENERGIZED_INGOT, dict(ENERGIZED, e=(255, 200, 60), E=(255, 250, 200)))
    out['ender_alloy_ingot'] = render(ENDER_INGOT, ENDER)
    out['iron_plate'] = render(PLATE, dict(IRON, r=IRON['1']))
    out['copper_plate'] = render(PLATE, dict(COPPER, r=COPPER['1']))
    out['gold_plate'] = render(PLATE, dict(GOLD, r=GOLD['1']))
    out['steel_plate'] = render(PLATE, dict(STEEL, r=STEEL['1']))
    boards = {
        'basic_circuit': ((34, 100, 52), (50, 140, 70), (24, 70, 38), (240, 110, 50)),
        'advanced_circuit': ((34, 70, 140), (60, 104, 190), (22, 46, 96), (250, 210, 70)),
        'elite_circuit': ((90, 40, 130), (130, 70, 180), (60, 24, 90), (80, 230, 240)),
    }
    for name, (base, light, dark, led) in boards.items():
        pal = {'o': (16, 20, 18), 'b': base, 'L': light, 'B': dark, 'g': (214, 176, 70), 'G': (240, 204, 90),
               'c': (28, 28, 32), 'C': (64, 64, 72), 'e': led, 'p': (170, 170, 176)}
        out[name] = render(CIRCUIT, pal)
    frame = dict(STEEL, G=(232, 190, 70))
    for name, symbol, inner, sym, sym_hi in (
            ('speed_upgrade', SPEED_SYMBOL, (22, 46, 70), (70, 190, 255), (200, 240, 255)),
            ('energy_upgrade', ENERGY_SYMBOL, (60, 44, 14), (250, 200, 50), (255, 246, 200)),
            ('range_upgrade', RANGE_SYMBOL, (22, 56, 30), (90, 220, 110), (210, 255, 210))):
        grid = [row[:4] + row[4:13].replace('.', 'i') + row[13:] if 5 <= y <= 10 else row for y, row in enumerate(UPGRADE)]
        grid = stamp(grid, symbol, 5, 5)
        pal = dict(frame, d=tuple(int(v * 0.6) for v in inner), i=inner, s=sym, S=sym_hi)
        out[name] = render(grid, pal)
    out['multimeter'] = render(MULTIMETER, {
        '0': (24, 22, 18), 'Y': (244, 192, 48), 'y': (190, 132, 20), 'k': (22, 40, 30), 'g': (110, 250, 140),
        'w': (70, 72, 80), 'W': (220, 222, 228), 'R': (220, 50, 40), 'b': (20, 20, 22)})
    out['guide_book'] = render(BOOK, {
        '0': (18, 24, 36), 'b': (24, 52, 92), 'B': (40, 88, 150), 'g': (240, 190, 60), 'G': (255, 236, 150),
        'd': (30, 66, 116), 'w': (236, 230, 214), 'r': (200, 40, 40)})
    return out
