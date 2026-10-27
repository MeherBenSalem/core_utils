"""Finds the game time in the loop-end take whose frame best matches the hook's first frame (the seamless loop point)."""
import json
import sys

import cv2
import numpy as np

R = '/tmp/claude-0/-home-user-core-utils/a71de292-2ad5-58a0-9c67-02d8fa2cd0cc/scratchpad/reel'
E = f'{R}/edit'
H0 = json.load(open(f'{E}/events.json'))['hook_start'] if len(sys.argv) < 2 else float(sys.argv[1])


def frames(name, want=None):
    j = json.load(open(f'{E}/idx/{name}.json'))
    cap, pos, keep = cv2.VideoCapture(f'{R}/raw/{name}.mkv'), 0, {}
    targets = set(j['idx']) if want is None else want
    while True:
        ok, f = cap.read()
        if not ok:
            break
        if pos in targets:
            g = cv2.resize(cv2.cvtColor(f, cv2.COLOR_BGR2GRAY), (270, 480), interpolation=cv2.INTER_AREA).astype(np.float32)
            keep[pos] = g[60:330]  # the quarry pit area (the hook crops to it)
        pos += 1
    return j, keep


hj, _ = frames('hook', set())
k = int(np.searchsorted(hj['t'], H0))
_, hf = frames('hook', {hj['idx'][k]})
ref = hf[hj['idx'][k]]
lj, lf = frames('loopend')
scores = [(float(np.abs(lf[i] - ref).mean()), t) for i, t in zip(lj['idx'], lj['t']) if i in lf]
scores.sort()
print('best matches (diff, game t):', [(round(d, 2), t) for d, t in scores[:8]])
