#!/bin/bash
# Runs the remaining takes at SLOW=0.025 (40 real seconds per game second). Usage: shots.sh [take ...]
B=$(dirname $0)
C=/tmp/claude-0/-home-user-core-utils/a71de292-2ad5-58a0-9c67-02d8fa2cd0cc/scratchpad/reel
R() { python3 $B/rcon.py "$@" > /dev/null; }
win() { export DISPLAY=:3; W=$(xdotool search --name Minecraft | head -1); xdotool windowfocus $W; }
take() { # take <name> <game_seconds>
  $B/cap.sh $1 $(echo "$2 * 40" | bc) ; $C/venv/bin/python $C/src/prep.py $1 0.025; }
spectate() { R "gamemode spectator Engineer" "tp Engineer $1"; sleep 30; }
for t in "$@"; do case $t in
solar) spectate "398.5 74.6 697.2 facing 401.5 72.2 703.5"; take solar 2.2 ;;
combustion) spectate "394.5 73.1 709.4 facing 394.5 72.45 706.5"; take combustion 2.2 ;;
geothermal) spectate "397.9 74.4 708.9 facing 396.5 72.4 705.6"; take geothermal 2.2 ;;
cables)
  R "fill 422 72 700 427 72 711 minecraft:air replace core_utilities:cable_elite"
  for x in 424 426 428; do R "setblock $x 72 709 minecraft:air"; done
  R "setblock 428 72 709 core_utilities:electric_furnace[facing=west]" "setblock 424 72 709 core_utilities:alloy_smelter[facing=east]" "setblock 426 72 711 minecraft:air" "setblock 426 72 711 core_utilities:crusher[facing=north]" \
    "item replace block 428 72 709 container.0 with minecraft:raw_gold 64" "item replace block 426 72 711 container.0 with minecraft:copper_ore 64" \
    "item replace block 424 72 709 container.0 with minecraft:gold_ingot 64" "item replace block 424 72 709 container.1 with minecraft:redstone 64"
  spectate "425.5 80.5 714.5 facing 425 72 705.5"
  $B/cap.sh cables 200 & CAP=$!
  sleep 30
  for p in "422 72 700" "422 72 701" "423 72 700" "424 72 700" "425 72 700" "426 72 700" "426 72 701" "426 72 702" "426 72 703" "426 72 704" "426 72 705" "426 72 706" "426 72 707" "426 72 708" "426 72 709" "426 72 710" "425 72 709" "427 72 709"; do
    R "setblock $p core_utilities:cable_elite"; sleep 1.5; done
  wait $CAP; $C/venv/bin/python $C/src/prep.py cables 0.025 ;;
crushergui)
  R "gamemode creative Engineer" "setblock 401 72 706 minecraft:air" "setblock 401 72 706 core_utilities:crusher[facing=south]" \
    "item replace block 401 72 706 container.0 with minecraft:iron_ore 48" "item replace block 401 72 706 container.4 with core_utilities:speed_upgrade 8" \
    "tp Engineer 401.5 72.0 708.6 facing 401.5 72.5 706.5"; sleep 30; win; xdotool click 3; sleep 20
  take crushergui 3.2; xdotool key Escape ;;
sidegui)
  R "gamemode creative Engineer" "tp Engineer 402.5 72.0 708.6 facing 402.5 72.5 706.5"; sleep 30; win; xdotool click 3; sleep 20
  $B/cap.sh sidegui 160 & CAP=$!
  sleep 22; for xy in "897 783" "843 837" "951 837" "897 891" "951 891" "843 837"; do xdotool mousemove $xy click 1; echo "click $xy $(date +%s.%N)" >> $C/raw/sidegui_clicks.txt; sleep 17; done
  wait $CAP; xdotool key Escape; $C/venv/bin/python $C/src/prep.py sidegui 0.025 ;;
alloy) spectate "403.5 73.1 709.3 facing 403.5 72.45 706.5"; take alloy 2.2 ;;
compressor) spectate "404.5 73.1 709.3 facing 404.5 72.45 706.5"; take compressor 2.2 ;;
furnace) spectate "399.5 73.1 709.3 facing 399.5 72.45 706.5"; take furnace 2.2 ;;
farm)
  R "fill 383 72 722 393 72 732 minecraft:wheat[age=7] replace minecraft:wheat" "item replace block 388 72 727 container.0 with minecraft:bone_meal 64" "data remove block 388 73 727 Items"
  spectate "388.5 81.5 738.5 facing 388.5 72 727.5"; take farm 6.5 ;;
grinder)
  for k in 0 1 2 3 4; do R "summon minecraft:husk $((400 + k)).5 72 72$((5 + k % 3)).5 {NoAI:1b,PersistenceRequired:1b,Silent:1b,Rotation:[0f,0f]}"; done
  R "summon minecraft:vindicator 403.5 72 727.5 {NoAI:1b,PersistenceRequired:1b,Silent:1b}" "summon minecraft:husk 401.5 72 727.5 {NoAI:1b,PersistenceRequired:1b,Silent:1b}"
  spectate "406.8 76.2 731.8 facing 402 72.3 725.3"; take grinder 4.5 ;;
wide) spectate "436.5 86.5 748.5 facing 407 71 715"; take wide 4 ;;
loopend)
  R "setblock 416 72 728 minecraft:air"; $B/reset_quarry.sh; R "fill 416 40 728 416 71 728 minecraft:stone" "setblock 416 71 728 minecraft:grass_block"
  spectate "421.5 79.5 738.5 facing 416 64 728"
  $B/cap.sh loopend 120 & CAP=$!; sleep 80
  R "setblock 416 72 728 core_utilities:quarry[facing=south]" "item replace block 416 72 728 container.9 with core_utilities:speed_upgrade 8" "setblock 417 72 728 core_utilities:rapid_item_pipe[west=extract,east=conduit]"
  wait $CAP; $C/venv/bin/python $C/src/prep.py loopend 0.025 ;;
esac; done
echo all done
