#!/bin/bash
# Re-places the machine row with empty buffers and fresh inputs, and removes the floor cable under it (so
# nothing has power until the cable is laid during the reveal take).
B=$(dirname $0)
R() { python3 $B/rcon.py "$@" > /dev/null; }
R "fill 393 71 706 412 71 706 minecraft:deepslate_tiles" "fill 394 72 706 405 72 706 minecraft:air"
row=(combustion_generator combustion_generator geothermal_generator energy_cell_elite energy_cell_advanced electric_furnace electric_furnace crusher crusher alloy_smelter compressor energy_cell_basic)
for i in "${!row[@]}"; do R "setblock $((394+i)) 72 706 core_utilities:${row[$i]}[facing=south]"; done
R "item replace block 394 72 706 container.0 with minecraft:coal_block 64" "item replace block 395 72 706 container.0 with minecraft:coal_block 64"
for x in 399 400; do R "item replace block $x 72 706 container.0 with minecraft:raw_iron 64" "item replace block $x 72 706 container.2 with core_utilities:speed_upgrade 4"; done
for x in 401 402; do R "item replace block $x 72 706 container.0 with minecraft:iron_ore 64" "item replace block $x 72 706 container.3 with core_utilities:speed_upgrade 4"; done
R "item replace block 403 72 706 container.0 with minecraft:iron_ingot 64" "item replace block 403 72 706 container.1 with core_utilities:coal_dust 64" "item replace block 403 72 706 container.3 with core_utilities:speed_upgrade 4"
R "item replace block 404 72 706 container.0 with minecraft:iron_ingot 64" "item replace block 404 72 706 container.2 with core_utilities:speed_upgrade 4"
# the geothermal generator touches lava: it would power its neighbours through the cable only, so it stays dark until then
