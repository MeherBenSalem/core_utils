"""Listing GIFs from the reel's slowed-clock captures: a 16:9 crop of each portrait take, sampled by game time."""
import bisect
import json
import os
import subprocess

import cv2

R = '/tmp/claude-0/-home-user-core-utils/a71de292-2ad5-58a0-9c67-02d8fa2cd0cc/scratchpad/reel'
OUT = '/home/user/core_utils/marketing/images/gifs'
FPS = 12
W = 560

# name: (take, crop x, y, w, h, [(game time, gif seconds), ...] keyframes)
GIFS = {
    'quarry': ('hook', 60, 330, 960, 540, [(2.0, 0.0), (5.0, 1.2), (49.0, 6.5), (49.0, 7.3)]),
    'cables': ('cables', 0, 700, 1080, 960, [(0.65, 0.0), (1.55, 3.6), (1.6, 4.6)]),
}


def game_at(keys, s):
    for (g0, s0), (g1, s1) in zip(keys, keys[1:]):
        if s0 <= s <= s1:
            return g0 + (g1 - g0) * (s - s0) / (s1 - s0) if s1 > s0 else g1
    return keys[-1][0]


for name, (take, x, y, w, h, keys) in GIFS.items():
    j = json.load(open(f'{R}/edit/idx/{take}.json'))
    total = keys[-1][1]
    want = []
    for n in range(int(total * FPS)):
        g = game_at(keys, n / FPS)
        want.append(j['idx'][max(0, bisect.bisect_right(j['t'], g) - 1)])
    frames, cap, pos = {}, cv2.VideoCapture(f'{R}/raw/{take}.mkv'), 0
    need = set(want)
    while need:
        ok, f = cap.read()
        if not ok:
            break
        if pos in need:
            frames[pos] = cv2.resize(f[y:y + h, x:x + w], (W, round(W * h / w)), interpolation=cv2.INTER_AREA)
            need.discard(pos)
        pos += 1
    tmp = f'{R}/edit/gif_{name}'
    os.makedirs(tmp, exist_ok=True)
    for n, k in enumerate(want):
        cv2.imwrite(f'{tmp}/{n:04d}.png', frames[k])
    subprocess.run(['ffmpeg', '-loglevel', 'error', '-y', '-framerate', str(FPS), '-i', f'{tmp}/%04d.png', '-vf',
                    'split[a][b];[a]palettegen=max_colors=128:stats_mode=diff[p];[b][p]paletteuse=dither=sierra2_4a',
                    '-loop', '0', f'{OUT}/{name}.gif'], check=True)
    print(name, len(want), 'frames', os.path.getsize(f'{OUT}/{name}.gif') // 1024, 'KB')
