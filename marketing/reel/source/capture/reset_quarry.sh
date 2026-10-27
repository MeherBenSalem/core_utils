#!/bin/bash
B=$(dirname $0)
python3 $B/rcon.py "setblock 416 72 728 minecraft:air" "fill 410 40 722 422 70 734 minecraft:stone" "fill 410 40 722 422 58 734 minecraft:deepslate" "fill 410 66 722 422 70 734 minecraft:dirt" "fill 410 71 722 422 71 734 minecraft:grass_block" \
 "fill 411 60 723 413 61 724 minecraft:iron_ore" "fill 418 63 730 419 64 731 minecraft:coal_ore" "fill 414 55 726 415 56 727 minecraft:deepslate_iron_ore" "fill 417 50 724 418 51 725 minecraft:deepslate_gold_ore" "fill 413 62 731 414 63 732 minecraft:copper_ore" "fill 419 57 723 419 58 724 minecraft:deepslate_redstone_ore" \
 "kill @e[type=item]" "item replace block 420 72 728 container.0 with minecraft:air" > /dev/null
python3 $B/rcon.py "data remove block 420 72 728 Items" "data remove block 420 73 728 Items" >/dev/null
