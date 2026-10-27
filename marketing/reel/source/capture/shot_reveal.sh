#!/bin/bash
# Factory reveal at 0.025x: the floor cable is laid 1.5 game s into the take and the machine row lights up.
B=$(dirname $0)
C=/tmp/claude-0/-home-user-core-utils/a71de292-2ad5-58a0-9c67-02d8fa2cd0cc/scratchpad/reel
sleep ${PRE:-0}
$B/cap.sh reveal 300 &
sleep 60
python3 $B/rcon.py "fill 393 71 706 412 71 706 core_utilities:cable_elite" > /dev/null
wait
$C/venv/bin/python $C/src/prep.py reveal 0.025
