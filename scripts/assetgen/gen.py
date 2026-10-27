#!/usr/bin/env python3
"""Generates Core Utilities resources for every version root.

    python3 scripts/assetgen/gen.py            # all versions
    python3 scripts/assetgen/gen.py 1.21.1     # one version

Textures, models and blockstates are identical everywhere; data files (recipes, loot tables, tags) are written in the
format of each Minecraft version. The legacy recipes in the 1.20.1 tree are the source of truth for the original
content and are converted for the newer versions.
"""
import copy
import json
import os
import shutil
import sys

sys.path.insert(0, os.path.dirname(__file__))
import textures as tx  # noqa: E402
import content as ct  # noqa: E402
import icons  # noqa: E402
import machines as mt  # noqa: E402

ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), '..', '..'))
NS = 'core_utilities'
VERSIONS = ['1.20.1', '1.21.1', '26.2']


def res(version):
    return os.path.join(ROOT, version, 'common', 'src', 'main', 'resources')


def write_json(path, data):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, 'w', encoding='utf-8') as f:
        json.dump(data, f, indent=2, ensure_ascii=False)
        f.write('\n')


def save_png(img, path):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    img.save(path, optimize=True)


# ------------------------------------------------------------------ formats per version

class Fmt:
    def __init__(self, version):
        self.v = version
        self.modern = version != '1.20.1'
        self.v26 = version.startswith('26')
        self.recipe_dir = 'recipe' if self.modern else 'recipes'
        self.loot_dir = 'loot_table' if self.modern else 'loot_tables'
        self.block_tags = 'tags/block' if self.modern else 'tags/blocks'
        self.item_tags = 'tags/item' if self.modern else 'tags/items'

    def ingredient(self, ref):
        if self.v26:
            return ref
        if ref.startswith('#'):
            return {'tag': ref[1:]}
        return {'item': ref}

    def result(self, item, count=1):
        if self.modern:
            r = {'id': item}
        else:
            r = {'item': item}
        if count != 1:
            r['count'] = count
        return r

    def cooking_result(self, item):
        return {'id': item} if self.modern else item


def shaped(fmt, pattern, key, result, count=1, category='misc'):
    return {
        'type': 'minecraft:crafting_shaped',
        'category': category,
        'pattern': pattern,
        'key': {k: fmt.ingredient(v) for k, v in key.items()},
        'result': fmt.result(result, count),
    }


def shapeless(fmt, ingredients, result, count=1, category='misc'):
    return {
        'type': 'minecraft:crafting_shapeless',
        'category': category,
        'ingredients': [fmt.ingredient(i) for i in ingredients],
        'result': fmt.result(result, count),
    }


def cooking(fmt, kind, ingredient, result, xp, time):
    return {
        'type': 'minecraft:' + kind,
        'category': 'misc',
        'ingredient': fmt.ingredient(ingredient),
        'result': fmt.cooking_result(result),
        'experience': xp,
        'cookingtime': time,
    }


def legacy_recipe(fmt, data):
    """Converts an original (1.20.1 format) recipe to this version."""
    data = copy.deepcopy(data)
    def conv(i):
        if isinstance(i, list):
            return [conv(x) for x in i]
        if 'tag' in i:
            return fmt.ingredient('#' + i['tag'])
        return fmt.ingredient(i['item'])
    if 'key' in data:
        data['key'] = {k: conv(v) for k, v in data['key'].items()}
    if 'ingredients' in data:
        data['ingredients'] = [conv(i) for i in data['ingredients']]
    r = data['result']
    data['result'] = fmt.result(r['item'], r.get('count', 1))
    return data


def loot_self(fmt, name, drop=None, keep_energy=False):
    entry = {'type': 'minecraft:item', 'name': f'{NS}:{drop or name}'}
    if keep_energy:
        if fmt.modern:
            entry['functions'] = [{'function': 'minecraft:copy_components', 'source': 'block_entity', 'include': ['minecraft:custom_data']}]
        else:
            entry['functions'] = [{'function': 'minecraft:copy_nbt', 'source': 'block_entity', 'ops': [{'source': 'Energy', 'target': 'BlockEntityTag.Energy', 'op': 'replace'}]}]
    table = {'type': 'minecraft:block', 'pools': [{'rolls': 1.0, 'entries': [entry], 'conditions': [{'condition': 'minecraft:survives_explosion'}]}]}
    if fmt.modern:
        table['random_sequence'] = f'{NS}:blocks/{name}'
    return table


# ------------------------------------------------------------------ models

def orientable(name, front, side, top, bottom):
    return {'parent': 'minecraft:block/orientable_with_bottom', 'textures': {
        'front': f'{NS}:block/{front}', 'side': f'{NS}:block/{side}', 'top': f'{NS}:block/{top}',
        'bottom': f'{NS}:block/{bottom}', 'particle': f'{NS}:block/{side}'}}


def box_faces(tex, uv=None, skip=()):
    faces = {}
    for f in ('north', 'south', 'east', 'west', 'up', 'down'):
        if f in skip:
            continue
        face = {'texture': tex}
        if uv:
            face['uv'] = uv
        faces[f] = face
    return faces


def conduit_models(name, tex, connector, extract_connector=None):
    """Core, arm, endpoint (+extract) and inventory models for a conduit, all built from north-facing parts."""
    t = f'{NS}:block/{tex}'
    c = f'{NS}:block/{connector}'
    models = {}
    base = {'parent': 'minecraft:block/block', 'textures': {'particle': t, 'core': t, 'conn': c}}
    core = dict(base, elements=[{'from': [5, 5, 5], 'to': [11, 11, 11], 'faces': box_faces('#core', [5, 5, 11, 11])}])
    models[f'{name}_core'] = core
    arm = dict(base, elements=[{'from': [5, 5, 0], 'to': [11, 11, 5], 'faces': box_faces('#core', [5, 0, 11, 5], skip=('north', 'south'))}])
    models[f'{name}_arm'] = arm
    endpoint = dict(base, elements=[
        {'from': [5, 5, 1], 'to': [11, 11, 5], 'faces': box_faces('#core', [5, 0, 11, 4], skip=('north', 'south'))},
        {'from': [3, 3, 0], 'to': [13, 13, 1], 'faces': box_faces('#conn', [3, 3, 13, 13])},
    ])
    models[f'{name}_endpoint'] = endpoint
    if extract_connector:
        ex = copy.deepcopy(endpoint)
        ex['textures']['conn'] = f'{NS}:block/{extract_connector}'
        models[f'{name}_extract'] = ex
    inv = dict(base, elements=[
        {'from': [5, 5, 0], 'to': [11, 11, 16], 'faces': {
            'north': {'texture': '#conn', 'uv': [5, 5, 11, 11]}, 'south': {'texture': '#conn', 'uv': [5, 5, 11, 11]},
            'east': {'texture': '#core', 'uv': [0, 5, 16, 11]}, 'west': {'texture': '#core', 'uv': [0, 5, 16, 11]},
            'up': {'texture': '#core', 'uv': [5, 0, 11, 16]}, 'down': {'texture': '#core', 'uv': [5, 0, 11, 16]}}}])
    models[f'{name}_inventory'] = inv
    return models


ROT = {'north': {}, 'east': {'y': 90}, 'south': {'y': 180}, 'west': {'y': 270}, 'up': {'x': 270}, 'down': {'x': 90}}


def conduit_blockstate(name, extract):
    parts = [{'apply': {'model': f'{NS}:block/{name}_core'}}]
    for d, rot in ROT.items():
        parts.append({'when': {d: 'conduit'}, 'apply': dict({'model': f'{NS}:block/{name}_arm'}, **rot)})
        parts.append({'when': {d: 'endpoint'}, 'apply': dict({'model': f'{NS}:block/{name}_endpoint'}, **rot)})
        if extract:
            parts.append({'when': {d: 'extract'}, 'apply': dict({'model': f'{NS}:block/{name}_extract'}, **rot)})
    return {'multipart': parts}


def facing_blockstate(model_off, model_on):
    variants = {}
    for facing, y in (('north', 0), ('east', 90), ('south', 180), ('west', 270)):
        for active, model in (('false', model_off), ('true', model_on)):
            v = {'model': model}
            if y:
                v['y'] = y
            variants[f'active={active},facing={facing}'] = v
    return {'variants': variants}


# ------------------------------------------------------------------ generation

def gen_textures(version):
    base = os.path.join(res(version), 'assets', NS, 'textures')
    blk = os.path.join(base, 'block')
    itm = os.path.join(base, 'item')
    anim = {'animation': {'frametime': 3}}
    for name in ct.MACHINES:
        if name.startswith('energy_cell'):
            continue
        acc = tx.ACCENTS[name]
        seed = sum(map(ord, name))
        save_png(mt.side(acc, seed), os.path.join(blk, f'{name}_side.png'))
        save_png(mt.top(acc, seed), os.path.join(blk, f'{name}_top.png'))
        save_png(mt.bottom(acc, seed), os.path.join(blk, f'{name}_bottom.png'))
        save_png(mt.front(name, acc, 0, False), os.path.join(blk, f'{name}_front.png'))
        save_png(mt.strip([mt.front(name, acc, f, True) for f in range(4)]), os.path.join(blk, f'{name}_front_on.png'))
        write_json(os.path.join(blk, f'{name}_front_on.png.mcmeta'), anim)
    for tier in tx.TIERS:
        c = tx.TIERS[tier]
        save_png(mt.cell(c, 'side', 70 + len(tier)), os.path.join(blk, f'energy_cell_{tier}_side.png'))
        save_png(mt.cell(c, 'front', 70 + len(tier)), os.path.join(blk, f'energy_cell_{tier}_front.png'))
        save_png(mt.cell(c, 'top', 70 + len(tier)), os.path.join(blk, f'energy_cell_{tier}_top.png'))
    save_png(mt.frame(True), os.path.join(blk, 'advanced_machine_frame.png'))
    save_png(tx.steel_block(), os.path.join(blk, 'steel_block.png'))
    save_png(tx.conduit_texture((200, 110, 50), (60, 40, 30), 11), os.path.join(blk, 'cable_basic.png'))
    save_png(tx.conduit_texture((225, 185, 60), (90, 70, 20), 12), os.path.join(blk, 'cable_advanced.png'))
    save_png(tx.conduit_texture((60, 200, 210), (20, 70, 80), 13), os.path.join(blk, 'cable_elite.png'))
    save_png(tx.conduit_texture((150, 158, 170), (95, 100, 110), 14), os.path.join(blk, 'item_pipe.png'))
    save_png(tx.conduit_texture((210, 170, 70), (120, 90, 30), 15), os.path.join(blk, 'rapid_item_pipe.png'))
    for tier, c in (('basic', (230, 120, 50)), ('advanced', (240, 200, 60)), ('elite', (70, 220, 230))):
        save_png(tx.connector_texture(c), os.path.join(blk, f'cable_{tier}_connector.png'))
    save_png(tx.connector_texture((160, 200, 240)), os.path.join(blk, 'pipe_connector.png'))
    save_png(tx.connector_texture((240, 120, 60), extract=True), os.path.join(blk, 'pipe_connector_extract.png'))
    drawn = icons.icons()
    for name in ct.ITEM_TEXTURES:
        save_png(drawn[name], os.path.join(itm, f'{name}.png'))
    save_png(tx.gui_atlas(), os.path.join(base, 'gui', 'machine.png'))


def gen_models(version):
    a = os.path.join(res(version), 'assets', NS)
    bs = os.path.join(a, 'blockstates')
    mb = os.path.join(a, 'models', 'block')
    mi = os.path.join(a, 'models', 'item')
    item_models = {}
    for name in ct.MACHINES:
        if name.startswith('energy_cell'):
            tier = name.split('_')[-1]
            p = f'energy_cell_{tier}'
            write_json(os.path.join(mb, f'{name}.json'), orientable(name, f'{p}_front', f'{p}_side', f'{p}_top', f'{p}_top'))
            write_json(os.path.join(bs, f'{name}.json'), facing_blockstate(f'{NS}:block/{name}', f'{NS}:block/{name}'))
        else:
            write_json(os.path.join(mb, f'{name}.json'), orientable(name, f'{name}_front', f'{name}_side', f'{name}_top', f'{name}_bottom'))
            write_json(os.path.join(mb, f'{name}_on.json'), orientable(name, f'{name}_front_on', f'{name}_side', f'{name}_top', f'{name}_bottom'))
            write_json(os.path.join(bs, f'{name}.json'), facing_blockstate(f'{NS}:block/{name}', f'{NS}:block/{name}_on'))
        item_models[name] = {'parent': f'{NS}:block/{name}'}
    for name in ('advanced_machine_frame', 'steel_block'):
        write_json(os.path.join(mb, f'{name}.json'), {'parent': 'minecraft:block/cube_all', 'textures': {'all': f'{NS}:block/{name}'}})
        write_json(os.path.join(bs, f'{name}.json'), {'variants': {'': {'model': f'{NS}:block/{name}'}}})
        item_models[name] = {'parent': f'{NS}:block/{name}'}
    conduits = [('cable', 'cable_basic', 'cable_basic_connector', None), ('cable_advanced', 'cable_advanced', 'cable_advanced_connector', None),
                ('cable_elite', 'cable_elite', 'cable_elite_connector', None), ('item_pipe', 'item_pipe', 'pipe_connector', 'pipe_connector_extract'),
                ('rapid_item_pipe', 'rapid_item_pipe', 'pipe_connector', 'pipe_connector_extract')]
    for name, tex, conn, extract in conduits:
        for model, data in conduit_models(name, tex, conn, extract).items():
            write_json(os.path.join(mb, f'{model}.json'), data)
        write_json(os.path.join(bs, f'{name}.json'), conduit_blockstate(name, extract is not None))
        item_models[name] = {'parent': f'{NS}:block/{name}_inventory'}
    # Legacy ids: render as the modern block until they convert
    for c in ct.LEGACY_CABLES:
        write_json(os.path.join(bs, f'cable_{c}.json'), {'variants': {'': {'model': f'{NS}:block/cable_core'}}})
        item_models[f'cable_{c}'] = {'parent': f'{NS}:block/cable_inventory'}
        for old in (os.path.join(mb, f'cable_{c}.json'),):
            if os.path.exists(old):
                os.remove(old)
    if os.path.exists(os.path.join(mb, 'cable.json')):
        os.remove(os.path.join(mb, 'cable.json'))
    custom = os.path.join(a, 'models', 'custom')
    for f in list(os.listdir(custom)) if os.path.isdir(custom) else []:
        if f.startswith('electrical_cable'):
            os.remove(os.path.join(custom, f))
    for tier, primary, legacy1, legacy2 in ct.SOLAR:
        write_json(os.path.join(bs, f'{primary}.json'), {'variants': {
            'weather=0': {'model': f'{NS}:block/{primary}'}, 'weather=1': {'model': f'{NS}:block/{legacy1}'},
            'weather=2': {'model': f'{NS}:block/{legacy2}'}}})
    for name in ct.NEW_ITEMS:
        item_models[name] = {'parent': 'minecraft:item/generated', 'textures': {'layer0': f'{NS}:item/{name}'}}
    for name, data in item_models.items():
        write_json(os.path.join(mi, f'{name}.json'), data)
    if version.startswith('26'):
        gen_item_definitions(version)


def gen_item_definitions(version):
    """1.21.4+ requires assets/<ns>/items/<id>.json for every item."""
    a = os.path.join(res(version), 'assets', NS)
    mi = os.path.join(a, 'models', 'item')
    out = os.path.join(a, 'items')
    for f in sorted(os.listdir(mi)):
        if f.endswith('.json'):
            name = f[:-5]
            write_json(os.path.join(out, f), {'model': {'type': 'minecraft:model', 'model': f'{NS}:item/{name}'}})


def gen_data(version):
    fmt = Fmt(version)
    d = os.path.join(res(version), 'data')
    nsd = os.path.join(d, NS)
    # Migrate old directory names for 1.21+
    if fmt.modern:
        for old, new in (('recipes', 'recipe'), ('loot_tables', 'loot_table')):
            if os.path.isdir(os.path.join(nsd, old)):
                shutil.rmtree(os.path.join(nsd, old))
        for ns_dir in os.listdir(d):
            for old in ('blocks', 'items'):
                p = os.path.join(d, ns_dir, 'tags', old)
                if os.path.isdir(p):
                    shutil.rmtree(p)
    else:
        for ns_dir in os.listdir(d):
            p = os.path.join(d, ns_dir, 'tags', 'block')
            if os.path.isdir(p):
                shutil.rmtree(p)
    # Stale tags from the MCreator era
    for stale in (os.path.join(d, 'forge', 'tags'), os.path.join(nsd, 'tags', 'block', 'cables'), os.path.join(nsd, 'tags', 'block', 'pipes'),
                  os.path.join(nsd, 'tags', 'blocks', 'cables'), os.path.join(nsd, 'tags', 'blocks', 'pipes')):
        if os.path.isdir(stale):
            shutil.rmtree(stale)
    # Recipes
    rdir = os.path.join(nsd, fmt.recipe_dir)
    if fmt.modern:
        os.makedirs(rdir, exist_ok=True)
    legacy = ct.load_legacy_recipes(os.path.join(res('1.20.1'), 'data', NS, 'recipes'))
    for name, data in legacy.items():
        write_json(os.path.join(rdir, f'{name}.json'), legacy_recipe(fmt, data))
    for name, recipe in ct.recipes(fmt, shaped, shapeless, cooking).items():
        write_json(os.path.join(rdir, f'{name}.json'), recipe)
    # Loot tables
    ldir = os.path.join(nsd, fmt.loot_dir, 'blocks')
    for name in ct.ALL_BLOCKS:
        write_json(os.path.join(ldir, f'{name}.json'), loot_self(fmt, name, ct.LOOT_REPLACEMENT.get(name), name in ct.KEEP_ENERGY))
    # Tags
    for tag, values in ct.block_tags(fmt).items():
        ns, path = tag.split(':')
        write_json(os.path.join(d, ns, fmt.block_tags, f'{path}.json'), {'replace': False, 'values': values})
    for tag, values in ct.item_tags(fmt).items():
        ns, path = tag.split(':')
        write_json(os.path.join(d, ns, fmt.item_tags, f'{path}.json'), {'replace': False, 'values': values})


def fix_legacy_assets(version):
    """Repairs original assets: invalid 'vector' model parents and non power-of-two textures (which disabled mipmaps)."""
    from PIL import Image
    a = os.path.join(res(version), 'assets', NS)
    custom = os.path.join(a, 'models', 'custom')
    for f in os.listdir(custom):
        path = os.path.join(custom, f)
        with open(path, encoding='utf-8') as fh:
            data = json.load(fh)
        if data.get('parent') == 'vector':
            del data['parent']
            write_json(path, data)
    tdir = os.path.join(a, 'textures')
    for root, _, files in os.walk(tdir):
        for f in files:
            if not f.endswith('.png'):
                continue
            path = os.path.join(root, f)
            img = Image.open(path)
            w, h = img.size
            if w == h and w & (w - 1) != 0:
                img.convert('RGBA').resize((64, 64), Image.LANCZOS).save(path, optimize=True)


def gen_lang(version):
    path = os.path.join(res(version), 'assets', NS, 'lang', 'en_us.json')
    with open(path, encoding='utf-8') as f:
        old = json.load(f)
    lang = {k: v for k, v in old.items() if not k.startswith(('block.essential_utilities', 'item.essential_utilities', 'item_group.essential_utilities'))}
    for k, v in list(lang.items()):
        lang[k] = v.replace('Resistence', 'Resistance')
    lang.update(ct.LANG)
    write_json(path, dict(sorted(lang.items())))


def main():
    versions = sys.argv[1:] or VERSIONS
    for v in versions:
        gen_textures(v)
        gen_models(v)
        gen_data(v)
        gen_lang(v)
        fix_legacy_assets(v)
        print('generated', v)


if __name__ == '__main__':
    main()
