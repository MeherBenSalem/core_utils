#!/bin/bash
B=$(dirname $0)
$B/cap.sh hook 540 2 &
CAP=$!
sleep 20
python3 $B/rcon.py "setblock 416 72 728 core_utilities:quarry[facing=south]" "item replace block 416 72 728 container.9 with core_utilities:speed_upgrade 8" "setblock 417 72 728 core_utilities:rapid_item_pipe[west=extract,east=conduit]" > /dev/null
wait $CAP
echo done
