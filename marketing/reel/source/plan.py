"""Single source of truth for the Core Utilities reel: beat grid, voice placement, shots, time maps and overlay cues.

120 BPM, 72 beats (36 s). The drop lands on beat 8 (4.0 s) together with the factory lighting up. Every cut sits on
a beat and every machine gets at least two beats (1 s) on screen. Gameplay never plays slower than real time, and each
voice line gets a slot that fits its real length (checked against the picked takes in vo_pick/). The reel ends on the hook's first frame, so it
loops without a seam. Source event times (game seconds inside each capture) come from events.json, measured from
the capture indexes by events.py. Writes edl.json, cues.json and timeline_plan.json.
"""
import json

R = '/tmp/claude-0/-home-user-core-utils/a71de292-2ad5-58a0-9c67-02d8fa2cd0cc/scratchpad/reel'
E = f'{R}/edit'
BPM = 120.0
BEAT = 60.0 / BPM
DROP = 8 * BEAT
DURATION = 72 * BEAT
EV = json.load(open(f'{E}/events.json'))


def b(k):
    return round(k * BEAT, 4)


# ------------------------------------------------------------------ shot grid (beats) and voice slots
GRID = [('hook', 0, 5), ('reveal', 5, 10), ('solar', 10, 12), ('combustion', 12, 14), ('geothermal', 14, 16),
        ('cables', 16, 22), ('crushergui', 22, 28), ('alloy', 28, 30), ('compressor', 30, 32), ('furnace', 32, 34),
        ('farm', 34, 39), ('grinder', 39, 44), ('sidegui', 44, 50), ('wide', 50, 58), ('title', 58, 65), ('loopend', 65, 72)]
G = {n: (b(a), b(z)) for n, a, z in GRID}
# (line index, start): each line starts just after its shot's cut
VO = [(0, 0.12), (1, G['reveal'][0] + 0.1), (2, G['solar'][0] + 0.05), (3, G['combustion'][0] + 0.05),
      (4, G['geothermal'][0] + 0.05), (5, G['cables'][0] + 0.15), (6, G['crushergui'][0] + 0.15), (7, G['alloy'][0] + 0.1),
      (8, G['farm'][0] + 0.15), (9, G['grinder'][0] + 0.15), (10, G['sidegui'][0] + 0.2), (11, G['wide'][0] + 0.3),
      (12, G['title'][0] + 0.3), (13, G['loopend'][0] + 0.4)]

shots = []


def shot(name, src, a, z, mp, cam, interp='linear', **kw):
    s = {'name': name, 'src': src, 'start': round(a, 4), 'end': round(z, 4), 'map': mp, 'interp': interp, 'cam': cam}
    s.update(kw)
    shots.append(s)


def realtime(a, z, g0, speed=1.0):
    assert speed >= 1.0, 'gameplay never plays slower than real time'
    return [[0.0, g0], [round(z - a, 4), round(g0 + (z - a) * speed, 4)]]


def drift(a, z, s0, s1, cx0=540, cy0=960, cx1=None, cy1=None):
    return [[a, s0, cx0, cy0], [z, s1, cx1 if cx1 is not None else cx0, cy1 if cy1 is not None else cy0]]


# 1. hook: the quarry timelapse, already digging on frame one, accelerating
H0 = EV['hook_start']
a, z = G['hook']
shot('hook', 'hook', a, z, [[0.0, H0], [0.7, H0 + 4.0], [z, H0 + 44.0]], drift(a, z, 1.32, 1.48, 540, 760, 540, 800),
     interp='smooth', camEase='in', blur=True)
# 2. reveal: the factory lights up on the drop
a, z = G['reveal']
shot('reveal', 'reveal', a, z, realtime(a, z, EV['reveal_on'] - (DROP - a)), drift(a, z, 1.02, 1.20, 540, 1030, 580, 990), punch=0.05)
# 3. power sources, one per word
for name, cam in (('solar', (560, 980)), ('combustion', (540, 1000)), ('geothermal', (540, 960))):
    a, z = G[name]
    shot(name, name, a, z, realtime(a, z, 0.6), drift(a, z, 1.08, 1.22, cam[0] - 20, cam[1] + 20, cam[0] + 20, cam[1] - 20), punch=0.05)
# 4. cables snake from the generators to three machines, which light up
CA = EV['cables_first']
a, z = G['cables']
shot('cables', 'cables', a, z, realtime(a, z, CA - 0.3), drift(a, z, 1.05, 1.14, 540, 1000))
CABLE_TICKS = [round(a + (CA + 0.0375 * k) - (CA - 0.3), 3) for k in range(18)]
# 5. crusher GUI
a, z = G['crushergui']
shot('crushergui', 'crushergui', a, z, realtime(a, z, 0.2), drift(a, z, 1.22, 1.30, 600, 970, 610, 960))
# 6. alloy smelter, compressor, electric furnace
for name in ('alloy', 'compressor', 'furnace'):
    a, z = G[name]
    shot(name, name, a, z, realtime(a, z, 0.6), drift(a, z, 1.10, 1.18, 540, 1000), punch=0.05)
# 7. farm (2.4x), 8. grinder (1.5x)
a, z = G['farm']
shot('farm', 'farm', a, z, realtime(a, z, EV['farm_start'], 2.4), drift(a, z, 1.06, 1.16, 540, 980))
a, z = G['grinder']
GR0, GRS = 0.25, 1.5
shot('grinder', 'grinder', a, z, realtime(a, z, GR0, GRS), drift(a, z, 1.08, 1.18, 540, 1000))
GRINDER_HITS = [round(a + (h - GR0) / GRS, 3) for h in EV['grinder_hits'] if h > GR0]
# 9. side configuration clicks
SC = EV['side_clicks']
SC0 = SC[0] - 0.4
a, z = G['sidegui']
shot('sidegui', 'sidegui', a, z, realtime(a, z, SC0), drift(a, z, 1.34, 1.40, 640, 960))
# 10. the whole base for the loader pills, then the lit factory under the title card
a, z = G['wide']
shot('wide', 'wide', a, z, realtime(a, z, 0.0), drift(a, z, 1.0, 1.10, 540, 960, 560, 920))
a, z = G['title']
shot('title', 'reveal', a, z, realtime(a, z, 3.0), drift(a, z, 1.16, 1.26, 560, 1010, 560, 990))
# 11. back to the quarry plot: the quarry goes down and the reel ends on the hook's first frame
LE = EV['loop_end']
a, z = G['loopend']
shot('loopend', 'loopend', a, z, [[0.0, LE - (z - a)], [z - a, LE]], drift(a, z, 1.12, 1.32, 540, 860, 540, 760), camEase='out',
     fade_to_hook=0.2)
QUARRY_PLACE = round(a + EV['quarry_place'] - (LE - (z - a)), 3)

# where each click lands on screen: the captured cursor positions through the shot's camera at that moment
SRC_CLICKS = [(897, 783), (843, 837), (951, 837), (897, 891), (951, 891), (843, 837)]
SA = G['sidegui'][0]
click_pos = []
for c, (x, y) in zip(SC, SRC_CLICKS):
    t = SA + (c - SC0)
    (t0, s0, cx, cy), (t1, s1, _, _) = next(sh for sh in shots if sh['name'] == 'sidegui')['cam']
    sc = s0 + (s1 - s0) * (t - t0) / (t1 - t0)
    click_pos.append([round(540 + sc * (x - cx)), round(960 + sc * (y - cy))])

# ------------------------------------------------------------------ overlay cues
L = lambda n: G[n]  # noqa: E731
labels = [
    dict(t0=0.05, t1=G['hook'][1] - 0.1, name='QUARRY', sub='DIGS TO BEDROCK', color='#5be0d6', icon='quarry'),
    dict(t0=G['solar'][0], t1=G['solar'][1], name='SOLAR', sub='UP TO 1,280 FE/T', color='#ffd84a'),
    dict(t0=G['combustion'][0], t1=G['combustion'][1], name='COMBUSTION', sub='ANY FUEL', color='#ff8a3d'),
    dict(t0=G['geothermal'][0], t1=G['geothermal'][1], name='GEOTHERMAL', sub='FREE LAVA POWER', color='#ff5a2a'),
    dict(t0=G['cables'][0], t1=G['cables'][1], name='ELITE CABLE', sub='32K FE/T PER NETWORK', color='#46dcea'),
    dict(t0=G['crushergui'][0], t1=G['crushergui'][1], name='CRUSHER', sub='2X ORE', color='#c8d0dc'),
    dict(t0=G['alloy'][0], t1=G['alloy'][1], name='ALLOY SMELTER', sub='STEEL · ENERGIZED · ENDER', color='#f2aa32'),
    dict(t0=G['compressor'][0], t1=G['compressor'][1], name='COMPRESSOR', sub='INGOTS → PLATES', color='#5aaae6'),
    dict(t0=G['furnace'][0], t1=G['furnace'][1], name='ELECTRIC FURNACE', sub='2X SPEED', color='#e87028'),
    dict(t0=G['farm'][0], t1=G['farm'][1], name='CROP HARVESTER', sub='AUTO REPLANT', color='#78c846'),
    dict(t0=G['grinder'][0], t1=G['grinder'][1], name='MOB GRINDER', sub='COLLECTS THE DROPS', color='#dc323c'),
    dict(t0=G['sidegui'][0], t1=G['sidegui'][1], name='SIDE CONFIG', sub='IN · OUT · BOTH', color='#3d7bd9'),
]
W0, T0, Q0 = G['wide'][0], G['title'][0], G['loopend'][0]
cues = {
    'duration': DURATION, 'beat': BEAT, 'drop': DROP, 'labels': labels,
    'timelapse': [0.05, G['hook'][1] - 0.1],
    'stat': [DROP, G['reveal'][1] - 0.05, 0.6],
    'loaders': {'after': W0, 'bg': [W0, T0 - 0.05], 'end': T0 - 0.05},
    'title': [T0, Q0 - 0.05],
    'cta': [Q0 + 0.1, DURATION - 0.35],
    'clicks': [round(SA + (c - SC0), 3) for c in SC],
    'clickPos': click_pos,
    'flashes': [[DROP, 0.35], [T0, 0.5]],
    'capTop': [{'t0': 0, 't1': SA, 'top': 1300}, {'t0': SA, 't1': G['sidegui'][1], 'top': 1500}, {'t0': G['sidegui'][1], 't1': 99, 'top': 1300}],
}
events = {
    'drop': DROP, 'reveal_on': DROP, 'reveal': G['reveal'][0],
    'cables': G['cables'][0], 'cable_ticks': CABLE_TICKS,
    'cables_on': round(G['cables'][0] + EV['cables_last'] - (CA - 0.3), 3),
    'crusher': G['crushergui'][0], 'grinder_hits': GRINDER_HITS,
    'wide': W0, 'title': T0, 'loopend': Q0, 'quarry_place': QUARRY_PLACE,
    'cuts': [s['start'] for s in shots[1:]],
    'whooshes': [[G['reveal'][0], True], [W0, True], [Q0, False]],
}
cues['events'] = events
json.dump({'fps': 30, 'size': [1080, 1920], 'shots': shots}, open(f'{E}/edl.json', 'w'), indent=1)
json.dump(cues, open(f'{E}/cues.json', 'w'), indent=1)
json.dump({'duration': DURATION, 'lines': [{'i': i, 'start': round(s, 3), 'cap': json.load(open(f'{E}/lines.json'))[i]['cap']} for i, s in VO]},
          open(f'{E}/timeline_plan.json', 'w'), indent=1)
for s in shots:
    print(f"{s['start']:6.2f}-{s['end']:6.2f} {s['name']:12s} map {s['map'][0][1]:.2f}->{s['map'][-1][1]:.2f}")

# fit check: every picked take must end before the next line starts (and the last before the reel ends)
import os  # noqa: E402
import soundfile as sf  # noqa: E402
starts = [st for _, st in VO] + [DURATION - 0.3]
for (i, st), nxt in zip(VO, starts[1:]):
    f = f'{E}/vo_pick/s{i + 1:02d}.wav'
    if os.path.exists(f):
        d = sf.info(f).duration
        flag = 'OK ' if st + d + 0.05 <= nxt else 'OVER'
        print(f'{flag} line {i + 1:2d} {st:6.2f}-{st + d:6.2f} (next {nxt:6.2f})')
