import sys, time
sys.path.insert(0, '/tmp/claude-0/-home-user-core-utils/a71de292-2ad5-58a0-9c67-02d8fa2cd0cc/scratchpad/bin')
from rcon import rcon
def R(*cs):
    for c in cs:
        out = rcon(c)
        if 'Unknown' in out or 'Incorrect' in out or 'Expected' in out or 'Invalid' in out:
            print('ERR', c, '->', out)
CU = 'core_utilities:'
# Factory floor
R('fill 391 71 699 419 71 715 minecraft:polished_deepslate',
  'fill 392 71 700 418 71 714 minecraft:deepslate_tiles',
  'fill 391 72 699 419 76 715 minecraft:air')
# floor cable trunk (elite) under the machine row and the solar rows
R(f'fill 393 71 706 412 71 706 {CU}cable_elite')
R(f'fill 393 71 709 412 71 709 {CU}cable_elite', f'fill 393 71 712 412 71 712 {CU}cable_elite',
  f'fill 392 71 706 392 71 712 {CU}cable_elite')
# solar field on top of the back cable rows
R(f'fill 393 72 709 412 72 709 {CU}solar_panel_tier_41', f'fill 393 72 712 412 72 712 {CU}solar_panel_tier_31')
# machine row (fronts face the camera, south)
row = ['combustion_generator', 'combustion_generator', 'geothermal_generator', 'energy_cell_elite', 'energy_cell_advanced',
       'electric_furnace', 'electric_furnace', 'crusher', 'crusher', 'alloy_smelter', 'compressor', 'energy_cell_basic']
for i, m in enumerate(row):
    R(f'setblock {394 + i} 72 706 {CU}{m}[facing=south]')
# lava for the geothermal generator behind it, glassed in
R('setblock 396 72 707 minecraft:lava', 'setblock 396 73 707 minecraft:glass', 'setblock 395 72 707 minecraft:glass', 'setblock 397 72 707 minecraft:glass', 'setblock 396 72 708 minecraft:glass')
def fill_slots(x, items):
    for slot, it in items:
        R(f'item replace block {x} 72 706 container.{slot} with {it}')
fill_slots(394, [(0, 'minecraft:coal_block 64')])
fill_slots(395, [(0, 'minecraft:coal_block 64')])
for x in (399, 400):
    fill_slots(x, [(0, 'minecraft:raw_iron 64'), (2, f'{CU}speed_upgrade 4')])
for x in (401, 402):
    fill_slots(x, [(0, 'minecraft:iron_ore 64'), (3, f'{CU}speed_upgrade 4')])
fill_slots(403, [(0, 'minecraft:iron_ingot 64'), (1, f'{CU}coal_dust 64'), (3, f'{CU}speed_upgrade 4')])
fill_slots(404, [(0, 'minecraft:iron_ingot 64'), (2, f'{CU}speed_upgrade 4')])
print('factory done')
