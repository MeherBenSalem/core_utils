#!/bin/bash
# Longer loop ending: 260 s real (6.5 game s); the quarry is placed 60 s in (game 1.5), so the dig state at game ~4.6 matches the hook's first frame
B=$(dirname $0)
C=/tmp/claude-0/-home-user-core-utils/a71de292-2ad5-58a0-9c67-02d8fa2cd0cc/scratchpad/reel
R() { python3 $B/rcon.py "$@" > /dev/null; }
R "setblock 416 72 728 minecraft:air"; $B/reset_quarry.sh; R "fill 416 40 728 416 71 728 minecraft:stone" "setblock 416 71 728 minecraft:grass_block"
R "gamemode spectator Engineer" "tp Engineer 421.5 79.5 738.5 facing 416 64 728"; sleep 30
date +%s.%N > $C/raw/loopend_start.txt
$B/cap.sh loopend 260 & CAP=$!; sleep 60
date +%s.%N > $C/raw/loopend_place.txt
R "setblock 416 72 728 core_utilities:quarry[facing=south]" "item replace block 416 72 728 container.9 with core_utilities:speed_upgrade 8" "setblock 417 72 728 core_utilities:rapid_item_pipe[west=extract,east=conduit]"
wait $CAP; $C/venv/bin/python $C/src/prep.py loopend 0.025
echo loopend done
