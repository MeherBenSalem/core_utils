#!/bin/bash
# Downloads the reel's sound effects from Mixkit (Mixkit Sound Effects Free License: free for commercial use in
# videos, no attribution required) into $1 (default ./sfxlib), as WAV. sfx_pro.py reads them from there.
# A few files are only served as MP3 previews; those are converted to WAV.
set -e
D=${1:-sfxlib}; mkdir -p "$D"
for id in 1143 855 815 828 804 2602 2908 2599 790 3180 3108 1349 1342 2445 2440 2857 2577 2600 2132 823 821 824 812 833 836 2757 1348 2430 2428 2155 2143 2153 2568 3124 1492 1490 1481 2901 2950 2504 3117; do
  f="$D/$id.wav"; [ -s "$f" ] && continue
  curl -sf --max-time 60 -o "$f" "https://assets.mixkit.co/active_storage/sfx/$id/$id.wav" || rm -f "$f"
  if ! head -c 4 "$f" 2>/dev/null | grep -q RIFF; then
    curl -sf --max-time 60 -o "$D/$id.mp3" "https://assets.mixkit.co/active_storage/sfx/$id/$id-preview.mp3"
    ffmpeg -loglevel error -y -i "$D/$id.mp3" -c:a pcm_s16le "$f" && rm "$D/$id.mp3"
  fi
done
ls "$D" | wc -l
