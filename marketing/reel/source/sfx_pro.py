"""Production sound design for the Core Utilities reel: real recorded sound effects, timed from cues.json.

Every sound is a professional recording from Mixkit (Mixkit Sound Effects Free License: free for commercial use in
videos, no attribution required; https://mixkit.co/license/), listed in SFX below by its Mixkit id. No synthesized
effects. Each cue places a recording so its main hit (its loudest 10 ms, measured per file) lands exactly on the
frame it belongs to; whooshes peak into the cut they lead to. Anything that starts before 0 wraps around to the end
of the reel, so the quarry's sound carries the loop. Writes sfx.wav (48 kHz stereo).

The library itself is not stored in the repository: fetch_sfx.sh downloads the files from Mixkit by id.
"""
import json
import os

import numpy as np
import soundfile as sf
from scipy.signal import butter, resample_poly, sosfilt

R = '/tmp/claude-0/-home-user-core-utils/a71de292-2ad5-58a0-9c67-02d8fa2cd0cc/scratchpad/reel'
E = f'{R}/edit'
LIB = f'{R}/sfxlib'
SR = 48000
C = json.load(open(f'{E}/cues.json'))
EV = C['events']
DUR = C['duration']
N = int(round(DUR * SR))

# Mixkit id -> what it is (titles as published on mixkit.co)
SFX = {
    1143: 'Cinematic whoosh deep impact', 855: 'Electrical drill', 815: 'Quick drilling machine hum',
    828: 'Loud construction machine', 804: 'Construction truck loading dirt', 2602: 'Electronics power up',
    2908: 'Movie trailer epic impact', 2599: 'Heavy electric shockwave impact', 3180: 'Machine activation short',
    3108: 'Crystal chime', 1349: 'Fast ignition fire', 2445: 'Volcano lava bubble', 2857: 'Gear fast lock tap',
    2600: 'Electricity static power up', 2132: 'Low electricity humming', 823: 'Machine grinder loud',
    824: 'Welding and sparks', 833: 'Metal hammer hit', 2757: 'Metal tank gear shift', 1348: 'Fire woosh',
    2430: 'Dry leaves rustling', 2155: 'Impact of a strong punch', 2143: 'Hard and quick punch',
    2568: 'Cool interface click tone', 1492: 'Cinematic whoosh fast transition', 1490: 'Fast whoosh transition',
    1481: 'Cinematic swoosh', 2901: 'Epic movie impact', 2950: 'Intro text glitch', 2504: 'Industrial machine hum',
    3117: 'Mechanical technology movements',
}

_cache = {}


def load(iid):
    """A recording at 48 kHz stereo, normalized to 0 dBFS peak, and the time of its main hit."""
    if iid not in _cache:
        a, sr = sf.read(os.path.join(LIB, f'{iid}.wav'), dtype='float32', always_2d=True)
        if a.shape[1] == 1:
            a = np.repeat(a, 2, axis=1)
        a = a[:, :2]
        if sr != SR:
            a = np.stack([resample_poly(a[:, c], SR, sr) for c in range(2)], axis=1).astype(np.float32)
        a /= max(1e-6, np.abs(a).max())
        m = a.mean(1)
        win = int(0.01 * SR)
        env = np.sqrt(np.convolve(m ** 2, np.ones(win) / win, 'same'))
        _cache[iid] = (a, int(np.argmax(env)) / SR)
    return _cache[iid]


out = np.zeros((N, 2), np.float32)


def place(iid, at, db=0.0, align='peak', start=0.0, length=None, fade_in=0.0, fade_out=0.08, hp=None, lp=None):
    """Places recording iid so its `align` point ("peak", "start" or "end") lands at `at` seconds.

    start/length pick a segment of the file (seconds); fades are in seconds; hp/lp are filter corners in Hz."""
    a, peak = load(iid)
    seg = a[int(start * SR): int((start + length) * SR) if length else None].copy()
    if hp:
        seg = sosfilt(butter(2, hp, 'high', fs=SR, output='sos'), seg, axis=0).astype(np.float32)
    if lp:
        seg = sosfilt(butter(2, lp, 'low', fs=SR, output='sos'), seg, axis=0).astype(np.float32)
    n = len(seg)
    if fade_in:
        k = min(n, int(fade_in * SR))
        seg[:k] *= np.linspace(0, 1, k)[:, None]
    if fade_out:
        k = min(n, int(fade_out * SR))
        seg[n - k:] *= np.linspace(1, 0, k)[:, None]
    ref = {'peak': peak - start, 'start': 0.0, 'end': n / SR}[align]
    i0 = int(round((at - ref) * SR))
    g = 10 ** (db / 20)
    idx = (np.arange(n) + i0) % N  # wraps around the loop
    np.add.at(out, idx, seg * g)


lab = {l['name']: (l['t0'], l['t1']) for l in C['labels']}

# ---------------------------------------------------------------- hook: the quarry is already digging
place(1143, 0.0, db=-6)                                                   # the opening hit (its swell wraps to the end)
place(855, 0.0, db=-9, align='start', start=0.3, length=EV['reveal'] - 0.05, fade_in=0.02, fade_out=0.25)  # the drill
place(804, 0.15, db=-15, align='start', length=EV['reveal'] - 0.3, fade_in=0.2, fade_out=0.3, hp=120)       # dirt
# ---------------------------------------------------------------- the drop: the factory lights up
DROP = EV['drop']
place(1492, EV['reveal'], db=-14)                                          # soft whoosh into the factory
place(2602, DROP, db=-9, align='end', fade_in=0.4)                         # power-up into the drop
place(2908, DROP, db=-3)                                                   # trailer hit on the drop
place(2599, DROP + 0.01, db=-9)                                            # electric shockwave
place(2504, DROP + 0.1, db=-22, align='start', length=EV['cables'] - DROP - 0.1, fade_in=0.3, fade_out=0.4)  # hum
# ---------------------------------------------------------------- power sources, one per word
place(3180, lab['SOLAR'][0] + 0.06, db=-12)
place(3108, lab['SOLAR'][0] + 0.12, db=-18)
place(1349, lab['COMBUSTION'][0] + 0.08, db=-9)
place(2445, lab['GEOTHERMAL'][0] + 0.05, db=-8, align='start', length=0.95, fade_out=0.25)
# ---------------------------------------------------------------- cables snake to the machines
for k, t in enumerate(EV['cable_ticks']):
    place(2857, t, db=-19 - (k % 3), hp=300)
place(2600, EV['cables_on'], db=-10)                                      # the machines light up
place(2132, EV['cables_on'] + 0.05, db=-22, align='start', length=EV['crusher'] - EV['cables_on'] - 0.05, fade_out=0.3)
# ---------------------------------------------------------------- processing
place(823, EV['crusher'] + 0.05, db=-14, align='start', start=0.5, length=lab['CRUSHER'][1] - EV['crusher'] - 0.1, fade_in=0.15, fade_out=0.3)
place(824, lab['ALLOY SMELTER'][0] + 0.05, db=-13, align='start', length=0.9, fade_out=0.2)
place(833, lab['COMPRESSOR'][0] + 0.2, db=-9)
place(2757, lab['COMPRESSOR'][0] + 0.05, db=-15, align='start', length=0.8, fade_out=0.2)
place(1348, lab['ELECTRIC FURNACE'][0] + 0.15, db=-10)
# ---------------------------------------------------------------- automation
place(2430, lab['CROP HARVESTER'][0] + 0.05, db=-12, align='start', length=lab['CROP HARVESTER'][1] - lab['CROP HARVESTER'][0] - 0.1, fade_in=0.1, fade_out=0.3)
place(3117, lab['CROP HARVESTER'][0] + 0.2, db=-20, align='start', length=1.6, fade_out=0.4)
for k, t in enumerate(EV['grinder_hits']):
    place(2155 if k % 2 == 0 else 2143, t, db=-8)
# ---------------------------------------------------------------- side configuration clicks
for t in C['clicks']:
    place(2568, t, db=-13)
# ---------------------------------------------------------------- loaders, title, back to the quarry
place(1490, EV['wide'], db=-15)                                            # out to the whole base
TITLE = EV['title']
place(2901, TITLE, db=-3)                                                  # title hit
place(2950, TITLE + 0.15, db=-18, hp=300)                                  # the name glitches in
place(1481, EV['loopend'], db=-15)                                         # back to the quarry plot
place(3180, EV['quarry_place'], db=-10)                                    # the quarry goes down
place(815, EV['quarry_place'] + 0.1, db=-14, align='start', length=DUR - EV['quarry_place'] - 0.1, fade_in=0.4, fade_out=0.02)

peak = np.abs(out).max()
out *= 0.9 / max(1e-6, peak)
sf.write(f'{E}/sfx.wav', out, SR)
print(f'sfx.wav: {len(SFX)} recordings, {DUR:.2f} s, peak normalized from {20 * np.log10(peak):.1f} dBFS')
