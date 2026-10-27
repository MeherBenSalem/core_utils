"""Content tables for the generator: ids, recipes, tags and English text."""
import json
import os

NS = 'core_utilities'

MACHINES = [
    'combustion_generator', 'geothermal_generator',
    'energy_cell_basic', 'energy_cell_advanced', 'energy_cell_elite', 'energy_cell_creative',
    'electric_furnace', 'crusher', 'alloy_smelter', 'compressor',
    'quarry', 'crop_harvester', 'block_breaker', 'block_placer', 'mob_grinder',
]
CONDUITS = ['cable', 'cable_advanced', 'cable_elite', 'item_pipe', 'rapid_item_pipe']
LEGACY_CABLES = ['e', 'i', 'l', 'lc', 'lcc', 'lt', 'ltc', 't', 'tc', 'tx', 'txc', 'txcc', 'x', 'xc', 'f', 's']
SOLAR = [
    (1, 'solar_panel', 'solar_panel_1', 'solar_panel_2'),
    (2, 'solar_panel_tier_21', 'solar_panel_tier_22', 'solar_panel_tier_23'),
    (3, 'solar_panel_tier_31', 'solar_panel_tier_32', 'solar_panel_tier_33'),
    (4, 'solar_panel_tier_41', 'solar_panel_tier_42', 'solar_panel_tier_43'),
]
OLD_BLOCKS = [
    'vector_plate_tier_1', 'vector_plate_tier_2', 'vector_plate_tier_3', 'vector_plate_tier_4', 'healing_plate', 'speed_plate',
    'fire_res_plate', 'strength_plate', 'poison_plate', 'weakness_plate', 'slowness_plate', 'wither_plate', 'player_filter',
    'mob_filter', 'neutral_filter', 'projectile_filter', 'filter_zombie', 'filter_skeleton', 'filter_spider', 'filter_villager',
    'filter_pet', 'filter_animal', 'deny_player_filter', 'deny_mob_filter', 'deny_neutral_filter', 'deny_projectile_filter',
    'deny_filter_zombie', 'deny_filter_skeleton', 'deny_filter_spider', 'deny_filter_villager', 'deny_filter_pet',
    'deny_filter_animal', 'floating_block', 'basic_machine_frame',
]
NEW_BLOCKS = ['advanced_machine_frame', 'steel_block'] + MACHINES + CONDUITS[1:]
ALL_BLOCKS = OLD_BLOCKS + CONDUITS + [f'cable_{c}' for c in LEGACY_CABLES] + [b for s in SOLAR for b in s[1:]] + ['advanced_machine_frame', 'steel_block'] + MACHINES
LOOT_REPLACEMENT = {f'cable_{c}': 'cable' for c in LEGACY_CABLES}
for _t, _p, _a, _b in SOLAR:
    LOOT_REPLACEMENT[_a] = _p
    LOOT_REPLACEMENT[_b] = _p
# Deny filters drop their normal filter (the wrench toggles them)
for _f in ['player_filter', 'mob_filter', 'neutral_filter', 'projectile_filter']:
    LOOT_REPLACEMENT['deny_' + _f] = _f
for _f in ['zombie', 'skeleton', 'spider', 'villager', 'pet', 'animal']:
    LOOT_REPLACEMENT['deny_filter_' + _f] = 'filter_' + _f
KEEP_ENERGY = {'energy_cell_basic', 'energy_cell_advanced', 'energy_cell_elite', 'energy_cell_creative'}

ITEM_TEXTURES = {
    'iron_dust': ('dust', (196, 184, 176)),
    'gold_dust': ('dust', (240, 200, 70)),
    'copper_dust': ('dust', (215, 120, 80)),
    'coal_dust': ('dust', (52, 52, 58)),
    'steel_ingot': ('ingot', (120, 128, 140)),
    'energized_ingot': ('ingot', (230, 80, 60)),
    'ender_alloy_ingot': ('ingot', (50, 170, 150)),
    'iron_plate': ('plate', (200, 200, 205)),
    'copper_plate': ('plate', (215, 125, 85)),
    'gold_plate': ('plate', (240, 205, 80)),
    'steel_plate': ('plate', (125, 133, 145)),
    'basic_circuit': ('circuit', (230, 120, 50)),
    'advanced_circuit': ('circuit', (240, 200, 60)),
    'elite_circuit': ('circuit', (70, 220, 230)),
    'speed_upgrade': ('upgrade:speed', (80, 200, 255)),
    'energy_upgrade': ('upgrade:energy', (255, 210, 60)),
    'range_upgrade': ('upgrade:range', (120, 230, 120)),
    'multimeter': ('multimeter', None),
    'guide_book': ('guide', None),
}
NEW_ITEMS = list(ITEM_TEXTURES)

ORIGINAL_RECIPES = (
    'animal_filter_recipe basic_machine_frame_recipe big_backpack_recipe blank_vector_plate_recipe copper_cable_recipe '
    'fire_res_vector_plate floating_block_recipe healing_vector_plate medium_backpack_recipe mob_filter_recipe '
    'mob_net_recipe neutral_filter_recipe pet_filter_recipe player_filter_recipe poison_vector_plate projetile_filter_recipe '
    'skeleton_filter_recipe slowness_vector_plate small_backpack_recipe solar_generator_recipe solar_generator_tier_2_recipe '
    'solar_generator_tier_3_recipe solar_generator_tier_4_recipe speed_vector_plate spider_filter_recipe strength_vector_plate '
    'tier_1_vector_plate tier_2_vector_plate tier_3_vector_plate tier_4_vector_plate villager_filter_recipe weakness_vector_plate '
    'wither_vector_plate wrench zombie_filter_recipe').split()


def load_legacy_recipes(directory):
    out = {}
    for name in ORIGINAL_RECIPES:
        with open(os.path.join(directory, name + '.json'), encoding='utf-8') as f:
            out[name] = json.load(f)
    return out


def i(name):
    return f'{NS}:{name}'


def recipes(fmt, shaped, shapeless, cooking):
    r = {}
    # Components (basic tier only needs vanilla materials, so the first machines can be built before any power)
    r['basic_circuit'] = shaped(fmt, [' R ', 'CIC', ' R '], {'R': 'minecraft:redstone', 'C': 'minecraft:copper_ingot', 'I': 'minecraft:iron_ingot'}, i('basic_circuit'), 2)
    r['advanced_circuit'] = shaped(fmt, [' G ', 'BEB', ' G '], {'G': i('gold_plate'), 'B': i('basic_circuit'), 'E': i('energized_ingot')}, i('advanced_circuit'))
    r['elite_circuit'] = shaped(fmt, [' D ', 'AEA', ' D '], {'D': 'minecraft:diamond', 'A': i('advanced_circuit'), 'E': i('ender_alloy_ingot')}, i('elite_circuit'))
    r['advanced_machine_frame'] = shaped(fmt, ['SCS', 'SFS', 'SSS'], {'S': i('steel_plate'), 'C': i('advanced_circuit'), 'F': i('basic_machine_frame')}, i('advanced_machine_frame'))
    r['steel_block'] = shaped(fmt, ['SSS', 'SSS', 'SSS'], {'S': i('steel_ingot')}, i('steel_block'))
    r['steel_ingot_from_block'] = shapeless(fmt, [i('steel_block')], i('steel_ingot'), 9)
    # Generators and storage
    r['combustion_generator'] = shaped(fmt, ['III', 'IFI', 'CMC'], {'I': 'minecraft:iron_ingot', 'F': 'minecraft:furnace', 'C': i('basic_circuit'), 'M': i('basic_machine_frame')}, i('combustion_generator'))
    r['geothermal_generator'] = shaped(fmt, ['OGO', 'CMC', 'OBO'], {'O': 'minecraft:obsidian', 'G': 'minecraft:glass', 'C': i('basic_circuit'), 'M': i('basic_machine_frame'), 'B': 'minecraft:bucket'}, i('geothermal_generator'))
    r['energy_cell_basic'] = shaped(fmt, ['IRI', 'RMR', 'ICI'], {'I': 'minecraft:iron_ingot', 'R': 'minecraft:redstone_block', 'M': i('basic_machine_frame'), 'C': i('basic_circuit')}, i('energy_cell_basic'))
    r['energy_cell_advanced'] = shaped(fmt, ['SAS', 'RER', 'SRS'], {'S': i('steel_plate'), 'A': i('advanced_circuit'), 'R': 'minecraft:redstone_block', 'E': i('energy_cell_basic')}, i('energy_cell_advanced'))
    r['energy_cell_elite'] = shaped(fmt, ['XCX', 'RER', 'XRX'], {'X': i('ender_alloy_ingot'), 'C': i('elite_circuit'), 'R': 'minecraft:redstone_block', 'E': i('energy_cell_advanced')}, i('energy_cell_elite'))
    # Transport
    r['cable_advanced'] = shaped(fmt, ['CCC', 'CEC', 'CCC'], {'C': i('cable'), 'E': i('energized_ingot')}, i('cable_advanced'), 8)
    r['cable_elite'] = shaped(fmt, ['CCC', 'CEC', 'CCC'], {'C': i('cable_advanced'), 'E': i('ender_alloy_ingot')}, i('cable_elite'), 8)
    r['item_pipe'] = shaped(fmt, ['IGI'], {'I': 'minecraft:iron_ingot', 'G': 'minecraft:glass'}, i('item_pipe'), 4)
    r['rapid_item_pipe'] = shaped(fmt, ['PPP', 'PEP', 'PPP'], {'P': i('item_pipe'), 'E': i('energized_ingot')}, i('rapid_item_pipe'), 8)
    # Machines
    r['electric_furnace'] = shaped(fmt, ['IRI', 'FMF', 'ICI'], {'I': 'minecraft:iron_ingot', 'R': 'minecraft:redstone', 'F': 'minecraft:furnace', 'M': i('basic_machine_frame'), 'C': i('basic_circuit')}, i('electric_furnace'))
    r['crusher'] = shaped(fmt, ['IFI', 'PMP', 'ICI'], {'I': 'minecraft:iron_ingot', 'F': 'minecraft:flint', 'P': 'minecraft:piston', 'M': i('basic_machine_frame'), 'C': i('basic_circuit')}, i('crusher'))
    r['compressor'] = shaped(fmt, ['IPI', 'PMP', 'ICI'], {'I': 'minecraft:iron_ingot', 'P': 'minecraft:piston', 'M': i('basic_machine_frame'), 'C': i('basic_circuit')}, i('compressor'))
    r['alloy_smelter'] = shaped(fmt, ['IRI', 'BMB', 'ICI'], {'I': 'minecraft:iron_ingot', 'R': 'minecraft:redstone', 'B': 'minecraft:blast_furnace', 'M': i('basic_machine_frame'), 'C': i('basic_circuit')}, i('alloy_smelter'))
    r['block_breaker'] = shaped(fmt, ['IPI', 'CMC', 'IRI'], {'I': 'minecraft:iron_ingot', 'P': 'minecraft:iron_pickaxe', 'C': i('basic_circuit'), 'M': i('basic_machine_frame'), 'R': 'minecraft:redstone'}, i('block_breaker'))
    r['block_placer'] = shaped(fmt, ['IDI', 'CMC', 'IRI'], {'I': 'minecraft:iron_ingot', 'D': 'minecraft:dispenser', 'C': i('basic_circuit'), 'M': i('basic_machine_frame'), 'R': 'minecraft:redstone'}, i('block_placer'))
    r['crop_harvester'] = shaped(fmt, ['IHI', 'CMC', 'IWI'], {'I': 'minecraft:iron_ingot', 'H': 'minecraft:iron_hoe', 'C': i('basic_circuit'), 'M': i('basic_machine_frame'), 'W': 'minecraft:water_bucket'}, i('crop_harvester'))
    r['quarry'] = shaped(fmt, ['SAS', 'PMP', 'SDS'], {'S': i('steel_plate'), 'A': i('advanced_circuit'), 'P': 'minecraft:piston', 'M': i('advanced_machine_frame'), 'D': 'minecraft:diamond_pickaxe'}, i('quarry'))
    r['mob_grinder'] = shaped(fmt, ['SWS', 'AMA', 'SSS'], {'S': i('steel_plate'), 'W': 'minecraft:diamond_sword', 'A': i('advanced_circuit'), 'M': i('advanced_machine_frame')}, i('mob_grinder'))
    # Upgrades and tools
    r['speed_upgrade'] = shaped(fmt, ['SRS', 'RCR', 'SRS'], {'S': i('steel_ingot'), 'R': 'minecraft:redstone', 'C': i('basic_circuit')}, i('speed_upgrade'))
    r['energy_upgrade'] = shaped(fmt, ['SGS', 'GCG', 'SGS'], {'S': i('steel_ingot'), 'G': 'minecraft:glowstone_dust', 'C': i('basic_circuit')}, i('energy_upgrade'))
    r['range_upgrade'] = shaped(fmt, ['SPS', 'PCP', 'SPS'], {'S': i('steel_ingot'), 'P': 'minecraft:ender_pearl', 'C': i('advanced_circuit')}, i('range_upgrade'))
    r['multimeter'] = shaped(fmt, ['IGI', 'ICI', 'IRI'], {'I': 'minecraft:iron_ingot', 'G': 'minecraft:glass_pane', 'C': i('basic_circuit'), 'R': 'minecraft:redstone'}, i('multimeter'))
    r['guide_book'] = shapeless(fmt, ['minecraft:book', 'minecraft:copper_ingot'], i('guide_book'))
    # Dusts back into ingots
    for metal in ('iron', 'gold', 'copper'):
        r[f'{metal}_ingot_from_dust_smelting'] = cooking(fmt, 'smelting', i(f'{metal}_dust'), f'minecraft:{metal}_ingot', 0.4, 200)
        r[f'{metal}_ingot_from_dust_blasting'] = cooking(fmt, 'blasting', i(f'{metal}_dust'), f'minecraft:{metal}_ingot', 0.4, 100)
    return r


def glass_tag(fmt):
    return 'c:glass_blocks' if fmt.modern else 'forge:glass'


def glass_pane_tag(fmt):
    return 'c:glass_panes' if fmt.modern else 'forge:glass_panes'


def block_tags(fmt):
    pick = [i(b) for b in ALL_BLOCKS if not b.startswith(('filter_', 'deny_')) and b not in ('player_filter', 'mob_filter', 'neutral_filter', 'projectile_filter')]
    axe = [i(b) for b in ALL_BLOCKS if b.startswith(('filter_', 'deny_')) or b in ('player_filter', 'mob_filter', 'neutral_filter', 'projectile_filter')]
    iron = [i(b) for b in ('steel_block', 'advanced_machine_frame', 'quarry', 'mob_grinder', 'energy_cell_advanced', 'energy_cell_elite')]
    stone = [i(b) for b in MACHINES + ['basic_machine_frame'] if i(b) not in iron]
    return {
        'minecraft:mineable/pickaxe': pick,
        'minecraft:mineable/axe': axe,
        'minecraft:needs_iron_tool': iron,
        'minecraft:needs_stone_tool': stone,
        f'{NS}:drill_machine': ['minecraft:command_block', 'minecraft:structure_block', 'minecraft:chain_command_block', 'minecraft:repeating_command_block',
                                 'minecraft:structure_void', 'minecraft:barrier', 'minecraft:bedrock', 'minecraft:light', 'minecraft:end_portal_frame',
                                 'minecraft:reinforced_deepslate', 'minecraft:spawner', 'minecraft:budding_amethyst'],
        'minecraft:beacon_base_blocks': [i('steel_block')],
    }


def opt(tag):
    return {'id': '#' + tag, 'required': False}


def item_tags(fmt):
    t = {}

    def common(kind, material):
        """Optional references to every loader's convention tag for kind/material."""
        refs = [opt(f'c:{kind}/{material}')]
        if not fmt.modern:
            refs += [opt(f'forge:{kind}/{material}'), opt(f'c:{material}_{kind}')]
        return refs

    for metal in ('iron', 'gold', 'copper'):
        t[f'{NS}:ores/{metal}'] = [f'#minecraft:{metal}_ores'] + common('ores', metal)
        t[f'{NS}:raw_materials/{metal}'] = [f'minecraft:raw_{metal}'] + common('raw_materials', metal)
        t[f'{NS}:ingots/{metal}'] = [f'minecraft:{metal}_ingot'] + common('ingots', metal)
    for gem in ('coal', 'diamond', 'emerald', 'lapis', 'redstone'):
        t[f'{NS}:ores/{gem}'] = [f'#minecraft:{gem}_ores'] + common('ores', gem)
    t[f'{NS}:ores/quartz'] = ['minecraft:nether_quartz_ore'] + common('ores', 'quartz')
    # Our materials in the convention tags so other mods accept them
    conv = {'dusts': ['iron', 'gold', 'copper', 'coal'], 'ingots': ['steel', 'energized', 'ender_alloy'], 'plates': ['iron', 'copper', 'gold', 'steel']}
    names = {('dusts', m): f'{m}_dust' for m in conv['dusts']}
    names.update({('ingots', m): f'{m}_ingot' for m in conv['ingots']})
    names.update({('plates', m): f'{m}_plate' for m in conv['plates']})
    for (kind, mat), item in names.items():
        t[f'c:{kind}/{mat}'] = [i(item)]
        if not fmt.modern:
            t[f'forge:{kind}/{mat}'] = [i(item)]
            t[f'c:{mat}_{kind}'] = [i(item)]
    t['c:storage_blocks/steel'] = [i('steel_block')]
    if not fmt.modern:
        t['forge:storage_blocks/steel'] = [i('steel_block')]
    t[f'{NS}:upgrades'] = [i('speed_upgrade'), i('energy_upgrade'), i('range_upgrade')]
    return t


LANG = {
    'item_group.core_utilities.essential_utilities_creative_tab': 'Core Utilities',
    'block.core_utilities.cable': 'Basic Energy Cable',
    'block.core_utilities.cable_advanced': 'Advanced Energy Cable',
    'block.core_utilities.cable_elite': 'Elite Energy Cable',
    'block.core_utilities.item_pipe': 'Item Pipe',
    'block.core_utilities.rapid_item_pipe': 'Rapid Item Pipe',
    'block.core_utilities.solar_panel': 'Solar Panel',
    'block.core_utilities.solar_panel_tier_21': 'Advanced Solar Panel',
    'block.core_utilities.solar_panel_tier_31': 'Elite Solar Panel',
    'block.core_utilities.solar_panel_tier_41': 'Ultimate Solar Panel',
    'block.core_utilities.basic_machine_frame': 'Basic Machine Frame',
    'block.core_utilities.advanced_machine_frame': 'Advanced Machine Frame',
    'block.core_utilities.steel_block': 'Block of Steel',
    'block.core_utilities.combustion_generator': 'Combustion Generator',
    'block.core_utilities.geothermal_generator': 'Geothermal Generator',
    'block.core_utilities.energy_cell_basic': 'Basic Energy Cell',
    'block.core_utilities.energy_cell_advanced': 'Advanced Energy Cell',
    'block.core_utilities.energy_cell_elite': 'Elite Energy Cell',
    'block.core_utilities.energy_cell_creative': 'Creative Energy Cell',
    'block.core_utilities.electric_furnace': 'Electric Furnace',
    'block.core_utilities.crusher': 'Crusher',
    'block.core_utilities.alloy_smelter': 'Alloy Smelter',
    'block.core_utilities.compressor': 'Compressor',
    'block.core_utilities.quarry': 'Quarry',
    'block.core_utilities.crop_harvester': 'Crop Harvester',
    'block.core_utilities.block_breaker': 'Block Breaker',
    'block.core_utilities.block_placer': 'Block Placer',
    'block.core_utilities.mob_grinder': 'Mob Grinder',
    'item.core_utilities.iron_dust': 'Iron Dust',
    'item.core_utilities.gold_dust': 'Gold Dust',
    'item.core_utilities.copper_dust': 'Copper Dust',
    'item.core_utilities.coal_dust': 'Coal Dust',
    'item.core_utilities.steel_ingot': 'Steel Ingot',
    'item.core_utilities.energized_ingot': 'Energized Ingot',
    'item.core_utilities.ender_alloy_ingot': 'Ender Alloy Ingot',
    'item.core_utilities.iron_plate': 'Iron Plate',
    'item.core_utilities.copper_plate': 'Copper Plate',
    'item.core_utilities.gold_plate': 'Gold Plate',
    'item.core_utilities.steel_plate': 'Steel Plate',
    'item.core_utilities.basic_circuit': 'Basic Circuit',
    'item.core_utilities.advanced_circuit': 'Advanced Circuit',
    'item.core_utilities.elite_circuit': 'Elite Circuit',
    'item.core_utilities.speed_upgrade': 'Speed Upgrade',
    'item.core_utilities.energy_upgrade': 'Energy Upgrade',
    'item.core_utilities.range_upgrade': 'Range Upgrade',
    'item.core_utilities.guide_book': 'Core Utilities Manual',
    'item.core_utilities.guide_book.description': 'Right-click to read about every block and machine.',
    'item.core_utilities.multimeter': 'Multimeter',
    'item.core_utilities.multimeter.description': 'Right-click any block to read its stored energy, or a cable to inspect its network.',
    'item.core_utilities.configuration_wrench.description_0': 'Right-click: rotate machines, toggle cable and pipe faces (pipes: insert / extract / off).',
    'item.core_utilities.configuration_wrench.description_1': 'Sneak + right-click: dismantle a Core Utilities block.',
    'tooltip.core_utilities.energy': '%s / %s FE',
    'tooltip.core_utilities.generates': 'Generates up to %s FE/t',
    'tooltip.core_utilities.generating': 'Generating %s FE/t',
    'tooltip.core_utilities.cable_rate': 'Transfers up to %s FE/t per network',
    'tooltip.core_utilities.cable_hint': 'Connects to any FE block. Wrench a face to disconnect it.',
    'tooltip.core_utilities.pipe_rate': 'Moves %s items every %ss',
    'tooltip.core_utilities.pipe_hint': 'Wrench a face touching an inventory to make it extract.',
    'tooltip.core_utilities.solar_panel': 'Needs sky access. Half output in rain, a quarter in storms.',
    'tooltip.core_utilities.combustion_generator': 'Burns any furnace fuel: 40 FE/t. Never wastes fuel when full.',
    'tooltip.core_utilities.geothermal_generator': '15 FE/t for every lava source block touching it. Lava is not used up.',
    'tooltip.core_utilities.energy_cell_basic': 'Stores 1M FE, 2k FE/t in and out. Keeps its charge when broken.',
    'tooltip.core_utilities.energy_cell_advanced': 'Stores 8M FE, 16k FE/t in and out. Keeps its charge when broken.',
    'tooltip.core_utilities.energy_cell_elite': 'Stores 64M FE, 128k FE/t in and out. Keeps its charge when broken.',
    'tooltip.core_utilities.energy_cell_creative': 'Infinite energy for testing.',
    'tooltip.core_utilities.electric_furnace': 'Smelts any furnace recipe twice as fast as a furnace. 16 FE/t.',
    'tooltip.core_utilities.crusher': 'Doubles ores into dusts and grinds blocks. 24 FE/t.',
    'tooltip.core_utilities.alloy_smelter': 'Makes Steel, Energized and Ender Alloy ingots. 32 FE/t.',
    'tooltip.core_utilities.compressor': 'Presses ingots into plates. 24 FE/t.',
    'tooltip.core_utilities.quarry': 'Mines a 9x9 area down to bedrock. Range upgrades widen it.',
    'tooltip.core_utilities.crop_harvester': 'Harvests and replants mature crops in a 9x9 area. Uses bone meal.',
    'tooltip.core_utilities.block_breaker': 'Breaks the block in front of it.',
    'tooltip.core_utilities.block_placer': 'Places blocks from its inventory in front of it.',
    'tooltip.core_utilities.mob_grinder': 'Damages mobs in front of it and collects their drops.',
    'tooltip.core_utilities.upgrade.speed': '+50% speed per upgrade (uses more FE per tick).',
    'tooltip.core_utilities.upgrade.energy': '-15% energy use per upgrade (+10% output in generators).',
    'tooltip.core_utilities.upgrade.range': '+2 blocks working radius for area machines.',
    'tooltip.core_utilities.upgrade.stack': 'Up to 8 per machine.',
    'gui.core_utilities.sides': 'Sides',
    'gui.core_utilities.side.front': 'Front',
    'gui.core_utilities.side.back': 'Back',
    'gui.core_utilities.side.left': 'Left',
    'gui.core_utilities.side.right': 'Right',
    'gui.core_utilities.side.top': 'Top',
    'gui.core_utilities.side.bottom': 'Bottom',
    'gui.core_utilities.side.short.front': 'F',
    'gui.core_utilities.side.short.back': 'B',
    'gui.core_utilities.side.short.left': 'L',
    'gui.core_utilities.side.short.right': 'R',
    'gui.core_utilities.side.short.top': 'T',
    'gui.core_utilities.side.short.bottom': 'D',
    'gui.core_utilities.mode.none': 'Disabled',
    'gui.core_utilities.mode.input': 'Input',
    'gui.core_utilities.mode.output': 'Output',
    'gui.core_utilities.mode.both': 'Input + Output',
    'gui.core_utilities.redstone.ignored': 'Redstone: ignored',
    'gui.core_utilities.redstone.high': 'Redstone: runs while powered',
    'gui.core_utilities.redstone.low': 'Redstone: runs while unpowered',
    'gui.core_utilities.click_to_cycle': 'Click to change',
    'gui.core_utilities.auto_eject.on': 'Auto-eject: on (outputs push into neighbours on output faces)',
    'gui.core_utilities.auto_eject.off': 'Auto-eject: off',
    'gui.core_utilities.lava_sources': 'Lava sources: %s',
    'gui.core_utilities.quarry_layer': 'Mining layer Y %s',
    'message.core_utilities.conduit.connected': 'Connected %s',
    'message.core_utilities.conduit.disconnected': 'Disconnected %s',
    'message.core_utilities.conduit.extract': 'Extracting from %s',
    'message.core_utilities.multimeter.network': 'Network: %s conduits, %s connections.',
    'message.core_utilities.multimeter.rate': 'Max %s FE/t',
    'message.core_utilities.multimeter.none': 'This block does not store energy.',
    'direction.core_utilities.north': 'north',
    'direction.core_utilities.south': 'south',
    'direction.core_utilities.east': 'east',
    'direction.core_utilities.west': 'west',
    'direction.core_utilities.up': 'up',
    'direction.core_utilities.down': 'down',
    'jei.core_utilities.chance': '%s%% chance',
    'jei.core_utilities.energy': '%s FE',
    'jei.core_utilities.ticks': '%ss',
}

GUIDE = {
    'welcome': ('Welcome', 'Core Utilities is a compact technology mod: power, storage, logistics and automation without endless chains. Every block works with Forge Energy on Forge and NeoForge, and with Team Reborn Energy on Fabric, so it plugs into your other tech mods.\n\nStart with a Combustion Generator or a Solar Panel, run Basic Energy Cables to an Electric Furnace and a Crusher, and grow from there. Hover any block in the item list for its numbers; JEI shows every machine recipe.'),
    'energy': ('Energy (FE)', 'All machines use FE (also called RF). Generators fill their own buffer and push it into anything touching them that accepts energy. Machines accept energy on every face.\n\nRight-click a block with the Multimeter to read how much energy it holds. Right-click a cable to see how many conduits and connections its network has and how much it can carry.\n\nA comparator next to any machine outputs a signal proportional to its stored energy.'),
    'solar': ('Solar Panels', 'Solar panels need a clear view of the sky. They make full power on clear days, half in rain, a quarter in thunderstorms and nothing at night.\n\nSolar Panel: 20 FE/t\nAdvanced: 80 FE/t\nElite: 320 FE/t\nUltimate: 1280 FE/t\n\nPanels push their energy down and sideways, so put a cable or machine under them.'),
    'combustion': ('Combustion', 'Burns any furnace fuel, including fuels from other mods, for 40 FE/t. A piece of coal gives 64,000 FE, a lava bucket 800,000 FE (the empty bucket stays in the slot).\n\nIt only starts burning new fuel when its buffer has room, so nothing is wasted. Speed upgrades burn faster for more FE/t; energy upgrades add 10% output each.'),
    'geothermal': ('Geothermal', 'Makes 15 FE/t for every lava source block touching one of its six faces, up to 90 FE/t. The lava is never consumed, so a Nether lava lake is a free power plant.\n\nIt pushes energy into every neighbour that is not lava.'),
    'cells': ('Energy Cells', 'Store energy for the night or for bursty machines.\n\nBasic: 1M FE, 2k FE/t\nAdvanced: 8M FE, 16k FE/t\nElite: 64M FE, 128k FE/t\n\nBy default every face is an input and the front is an output that pushes energy every tick. Change any face in the Sides panel of the GUI. Cells keep their charge when you break them.'),
    'cables': ('Cables', 'Cables carry energy instantly to every block on the network that accepts it, shared fairly between them. They hold no energy and do no work when nothing flows, so large networks cost almost nothing.\n\nBasic: 512 FE/t per network\nAdvanced: 4,096 FE/t\nElite: 32,768 FE/t\n\nDifferent tiers do not connect to each other. Right-click a face with the wrench to disconnect it from a machine or cable.'),
    'pipes': ('Item Pipes', 'Pipes move items between inventories, including chests, furnaces and other mods\' machines.\n\nRight-click the face of a pipe that touches an inventory with the wrench to set it to extract (orange arrow). Every few ticks it pulls items out and delivers them to the other inventories on the network, round robin. Wrench again to turn that face off, and once more to reconnect it.\n\nItem Pipe: 4 items every 0.5s\nRapid Item Pipe: 32 items every 0.25s'),
    'machines': ('Machines & sides', 'Every machine has a GUI with its energy bar on the left and a Sides panel on the right. Click a face (Front, Back, Left, Right, Top, Bottom) to cycle it between Disabled, Input, Output and Input + Output. Hoppers, pipes and other mods use these faces.\n\nThe tabs on the left set the redstone mode (ignored, run while powered, run while unpowered) and Auto-eject, which pushes finished items into inventories on output faces.\n\nWrench: right-click rotates a machine, sneak + right-click dismantles it.'),
    'upgrades': ('Upgrades', 'Each machine has three upgrade slots that take up to 8 upgrades in total.\n\nSpeed Upgrade: +50% speed each, at a higher energy cost per tick.\nEnergy Upgrade: -15% energy per tick each.\nRange Upgrade: +2 blocks radius for the Quarry, Crop Harvester and Mob Grinder.'),
    'ores': ('Ore doubling', 'The Crusher turns one ore block into two dusts (copper ore gives six), and raw ore into one dust with a 50% chance of a second. Smelt the dusts in an Electric Furnace, which runs any furnace recipe twice as fast as a vanilla furnace.\n\nIt also crushes coal into Coal Dust, cobblestone into gravel, gravel into sand, blaze rods into four blaze powder, bones into six bone meal and diamond, emerald, lapis, redstone and quartz ores into extra gems.'),
    'alloys': ('Alloys & tiers', 'The Alloy Smelter combines two inputs:\n\nIron Ingot + 2 Coal Dust: Steel Ingot\nGold Ingot + 4 Redstone: Energized Ingot\nEnergized Ingot + Ender Pearl: Ender Alloy Ingot\n4 Netherite Scrap + 4 Gold: Netherite Ingot\n\nThe Compressor presses ingots into plates, and 64 Coal Dust into a diamond.\n\nBasic gear needs only vanilla items. Steel and gold plates unlock Advanced Circuits and the Advanced Machine Frame; Ender Alloy unlocks the Elite tier.'),
    'quarry': ('Quarry', 'The Quarry mines a 9x9 area below itself, one layer at a time, all the way down to bedrock. It skips fluids, unbreakable blocks, blocks with inventories and anything in the core_utilities:drill_machine tag.\n\nEach block costs 400 FE. Range upgrades widen the area by 2 blocks per upgrade (up to 33x33), speed upgrades mine faster. Mined items fill its buffer; give it an output face with a chest or pipe and Auto-eject on.'),
    'farming': ('Crop Harvester', 'Place the Crop Harvester among your crops. It checks its own layer and the one above in a 9x9 area, harvests anything fully grown and replants it from the drops: wheat, carrots, potatoes, beetroot, nether wart, cocoa, sweet berries, melons, pumpkins, sugar cane and cactus.\n\nPut bone meal in its input slot and it will also fertilize young crops.'),
    'grinder': ('Mob Grinder', 'The Mob Grinder damages every mob in an area in front of it and vacuums up their drops. It never touches players, named mobs or bosses.\n\nEach hit costs 200 FE. Range upgrades make the area bigger, speed upgrades hit more often.'),
    'breaker': ('Breaker & Placer', 'The Block Breaker breaks the block in front of it as soon as there is one, which makes cobblestone generators and tree farms easy. The Block Placer places blocks from its inventory in front of it.\n\nBoth respect the redstone mode, so you can pulse them with a clock.'),
    'utilities': ('Utility blocks', 'The original Core Utilities blocks are still here:\n\nPushing Plates launch entities; effect plates heal, speed up or harm whoever stands on them.\nFilters only let certain entities through. Right-click a filter with the wrench to invert it.\nBackpacks carry 9, 18 or 27 stacks.\nThe Mob Net captures a mob to carry it somewhere else.\nThe Floating Block can be placed in mid-air.'),
}
for _k, (_t, _x) in GUIDE.items():
    LANG[f'guide.core_utilities.{_k}.title'] = _t
    LANG[f'guide.core_utilities.{_k}.text'] = _x
for _c in LEGACY_CABLES:
    LANG[f'block.core_utilities.cable_{_c}'] = 'Basic Energy Cable (legacy)'
for _t, _p, _a, _b in SOLAR:
    for _x in (_a, _b):
        LANG[f'block.core_utilities.{_x}'] = LANG[f'block.core_utilities.{_p}'] + ' (legacy)'
for _k in list(LANG):
    if _k.startswith('block.core_utilities.'):
        LANG.setdefault('item.' + _k[len('block.'):], LANG[_k])
