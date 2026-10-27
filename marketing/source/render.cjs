// Renders the listing artwork in marketing/images/ from real in-game screenshots, the mod's own textures and the bundled OFL fonts.
// Usage (from the repository root): NODE_PATH="$(npm root -g)" node marketing/source/render.cjs [name...]
// Requires the `playwright` package with a Chromium build available.
const path = require('path');
const fs = require('fs');
const { chromium } = require('playwright');

// The release and the supported Minecraft versions. Adding a version is one line here
// (plus one row in the compatibility table of curseforge/description.md).
const RELEASE = { version: '2.0', name: 'The Technology Update' };
const VERSIONS = [
  { minecraft: '1.20.1', loaders: ['Fabric', 'Forge'] },
  { minecraft: '1.21.1', loaders: ['Fabric', 'NeoForge'] },
  { minecraft: '26.2', loaders: ['Fabric', 'NeoForge'] },
];
const LOADERS = ['Fabric', 'Forge', 'NeoForge'].filter((loader) => VERSIONS.some((v) => v.loaders.includes(loader)));

const root = path.resolve(__dirname, '..', '..');
const out = path.join(root, 'marketing', 'images');
const shots = path.join(out, 'screenshots');
const fonts = path.join(__dirname, 'fonts');
const textures = path.join(root, '1.21.1', 'common', 'src', 'main', 'resources', 'assets', 'core_utilities', 'textures');
const url = (file) => 'file://' + file;
const shot = (name) => url(path.join(shots, name));
const source = (name) => url(path.join(__dirname, name));
const item = (name) => url(path.join(textures, 'item', name + '.png'));
const block = (name) => url(path.join(textures, 'block', name + '.png'));

// Accents follow the in-game colours: cable cyan, energy-bar amber, and each machine's front light.
const accent = { power: '#ffd84a', energy: '#46dcea', process: '#f2aa32', auto: '#78c846', logistics: '#5aaae6', control: '#3d7bd9', grinder: '#dc323c' };

const css = `
@font-face { font-family: CuDisplay; src: url('${url(path.join(fonts, 'anton.ttf'))}'); }
@font-face { font-family: CuText; src: url('${url(path.join(fonts, 'montserrat.ttf'))}'); font-weight: 100 900; }
:root { --text:#d3dae3; --dim:#8d99a8; --header:#f2f6fa; --cyan:#46dcea; --cyan-hi:#a8f4fb; --amber:#ffb43c;
        --bg:#080b10; --panel:rgba(10,14,20,.88); --line:rgba(70,220,234,.35); }
* { box-sizing: border-box; }
html, body { margin: 0; background: var(--bg); color: var(--text); font-family: CuText, sans-serif; font-weight: 500; }
.stage { position: relative; overflow: hidden; background: radial-gradient(ellipse at 30% 40%, #141b24 0%, #080b10 70%); }
.display { font-family: CuDisplay, sans-serif; letter-spacing: .02em; text-transform: uppercase; }
.steel { background: linear-gradient(180deg, #ffffff 0%, #e4edf5 45%, #9fb3c6 100%); -webkit-background-clip: text; background-clip: text; color: transparent; }
.glow { filter: drop-shadow(0 0 18px rgba(70,220,234,.35)); }
.kicker { font-family: CuText, sans-serif; font-weight: 800; color: var(--cyan); letter-spacing: .24em; font-size: 20px; text-transform: uppercase; }
.rule { height: 2px; background: linear-gradient(90deg, rgba(70,220,234,0), rgba(70,220,234,.95) 18%, rgba(70,220,234,.95) 70%, rgba(70,220,234,0)); box-shadow: 0 0 12px rgba(70,220,234,.6); }
.chev { display: inline-block; width: 12px; height: 12px; border-top: 3px solid var(--cyan); border-right: 3px solid var(--cyan); transform: rotate(45deg); margin: 0 14px 2px 0; }
.frame { position: relative; border: 1px solid rgba(160,190,215,.45); box-shadow: 0 0 0 1px rgba(0,0,0,.6), 0 30px 80px rgba(0,0,0,.65), 0 0 50px rgba(70,220,234,.14); background: #000; }
.frame::before, .frame::after { content: ''; position: absolute; width: 22px; height: 22px; z-index: 3; border-color: var(--cyan); border-style: solid; }
.frame::before { left: -3px; top: -3px; border-width: 3px 0 0 3px; }
.frame::after { right: -3px; bottom: -3px; border-width: 0 3px 3px 0; }
.frame > img { display: block; width: 100%; }
.grid { position: absolute; inset: 0; background-image: linear-gradient(rgba(70,220,234,.05) 1px, transparent 1px), linear-gradient(90deg, rgba(70,220,234,.05) 1px, transparent 1px);
        background-size: 48px 48px; -webkit-mask-image: radial-gradient(ellipse at 30% 50%, #000 0%, transparent 75%); mask-image: radial-gradient(ellipse at 30% 50%, #000 0%, transparent 75%); }
canvas.traces { position: absolute; left: 0; top: 0; width: 100%; height: 100%; pointer-events: none; }
.vignette { position: absolute; inset: 0; background: radial-gradient(ellipse at center, rgba(0,0,0,0) 45%, rgba(0,0,0,.75) 100%); }
.backdrop { position: absolute; inset: -40px; background-size: cover; background-position: center; filter: blur(16px) brightness(.38) saturate(1.1); }
.pill { display: inline-block; border: 1px solid rgba(70,220,234,.7); border-radius: 999px; padding: 7px 18px 6px; margin-right: 10px;
        font-weight: 800; font-size: 18px; letter-spacing: .12em; text-transform: uppercase; color: var(--header); background: rgba(70,220,234,.12); }
.label { position: absolute; font-weight: 800; font-size: 19px; letter-spacing: .12em; text-transform: uppercase; color: var(--header);
         background: rgba(8,11,16,.88); border-left: 4px solid var(--cyan); padding: 8px 14px 7px; white-space: nowrap;
         box-shadow: 0 8px 26px rgba(0,0,0,.6); z-index: 2; }
.label small { display: block; font-weight: 500; letter-spacing: 0; text-transform: none; font-size: 17px; color: var(--dim); margin-top: 2px; }
ul.points { list-style: none; padding: 0; margin: 0; }
ul.points li { position: relative; padding-left: 28px; margin: 0 0 14px; font-size: 25px; line-height: 1.34; color: var(--text); }
ul.points li::before { content: ''; position: absolute; left: 0; top: 11px; width: 9px; height: 9px; border-top: 3px solid var(--cyan); border-right: 3px solid var(--cyan); transform: rotate(45deg); }
.stats { display: flex; margin-top: 22px; border: 1px solid rgba(160,190,215,.3); background: var(--panel); box-shadow: 0 18px 50px rgba(0,0,0,.55); }
.stats > div { flex: 1; padding: 14px 22px 12px; border-left: 1px solid rgba(160,190,215,.18); }
.stats > div:first-child { border-left: 0; }
.stats .t { font-weight: 800; font-size: 14px; letter-spacing: .18em; text-transform: uppercase; color: var(--dim); }
.stats .v { font-family: CuDisplay, sans-serif; font-size: 32px; color: var(--header); margin-top: 4px; white-space: nowrap; }
b.hi { color: var(--header); font-weight: 800; }
.px { image-rendering: pixelated; }
.tile { background: rgba(8,11,16,.88); border: 1px solid rgba(160,190,215,.28); display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 8px; }
.tile span { font-size: 15px; font-weight: 700; color: var(--dim); text-align: center; line-height: 1.15; }
`;

// Faint circuit traces with glowing nodes. Deterministic, so renders are reproducible.
const traces = `<canvas class="traces"></canvas><script>
(() => { const c = document.querySelector('canvas.traces'); const r = c.getBoundingClientRect();
  c.width = r.width; c.height = r.height; const g = c.getContext('2d'); let s = 11;
  const rnd = () => (s = (s * 16807) % 2147483647) / 2147483647; const step = 48;
  g.lineWidth = 2; g.lineCap = 'round';
  for (let i = 0; i < 26; i++) { let x = Math.round(rnd() * c.width / step) * step, y = Math.round(rnd() * c.height / step) * step;
    const a = .05 + rnd() * .12; g.strokeStyle = 'rgba(70,220,234,' + a + ')'; g.beginPath(); g.moveTo(x, y);
    for (let k = 0; k < 4; k++) { const len = (1 + Math.floor(rnd() * 4)) * step; if (rnd() < .5) x += rnd() < .5 ? len : -len; else y += rnd() < .5 ? len : -len; g.lineTo(x, y); }
    g.stroke(); const h = g.createRadialGradient(x, y, 0, x, y, 14); h.addColorStop(0, 'rgba(120,240,250,' + (a * 4) + ')'); h.addColorStop(1, 'rgba(70,220,234,0)');
    g.fillStyle = h; g.beginPath(); g.arc(x, y, 14, 0, Math.PI * 2); g.fill(); g.fillStyle = 'rgba(200,250,255,' + (a * 5) + ')'; g.fillRect(x - 2, y - 2, 4, 4); } })();
</script>`;

const page = (width, height, body, transparent = false) => `<!doctype html><html><head><meta charset="utf-8"><style>${css}
body { width: ${width}px; height: ${height}px; } .stage { width: ${width}px; height: ${height}px; }
${transparent ? 'html, body, .stage { background: transparent; }' : ''}</style></head>
<body><div class="stage">${body}</div></body></html>`;

const header = (title) => page(1400, 120, `
  <div style="position:absolute;inset:0;display:flex;align-items:center;justify-content:center;gap:26px;background:linear-gradient(90deg,rgba(0,0,0,0),rgba(10,14,20,.92) 28%,rgba(10,14,20,.92) 72%,rgba(0,0,0,0))">
    <div class="rule" style="width:300px;transform:scaleX(-1)"></div>
    <span class="chev" style="margin:0;transform:rotate(-135deg)"></span>
    <div class="display steel glow" style="font-size:52px;white-space:nowrap">${title}</div>
    <span class="chev" style="margin:0"></span>
    <div class="rule" style="width:300px"></div>
  </div>`, true);

// A feature card: copy on the left, artwork on the right. Bullets never end on a one-word line.
const card = ({ kicker, title, points, art, color = accent.energy, width = 1600, height = 900, artWidth = 980 }) => page(width, height, `
  <div class="backdrop" style="background-image:url('${source('backdrop.jpg')}');opacity:.6"></div>
  <div class="grid"></div><div class="vignette"></div>${traces}
  <div style="position:absolute;left:0;top:0;bottom:0;width:6px;background:${color};box-shadow:0 0 30px ${color}"></div>
  <div style="position:absolute;left:80px;top:0;bottom:0;width:${width - artWidth - 150}px;display:flex;flex-direction:column;justify-content:center">
    <div class="kicker" style="color:${color}"><span class="chev" style="border-color:${color}"></span>${kicker}</div>
    <div class="display steel" style="font-size:72px;line-height:1.0;margin:16px 0 22px">${title}</div>
    <div class="rule" style="width:80%;margin-bottom:30px"></div>
    <ul class="points">${points.map((point) => `<li>${point.replace(/ (\S+)$/, '&nbsp;$1')}</li>`).join('')}</ul>
  </div>
  <div style="position:absolute;right:60px;top:50%;transform:translateY(-50%);width:${artWidth}px">${art}</div>`);

// An exact region (x, y, w, h in 1920x1080 pixels) of a capture, scaled to W pixels wide.
const region = (file, x, y, w, h, W) => `<div style="position:relative;width:${W}px;height:${Math.round(W * h / w)}px;overflow:hidden">
    <img src="${shot(file)}" style="position:absolute;max-width:none;width:${W * 1920 / w}px;left:${-x * W / w}px;top:${-y * W / w}px"></div>`;
const framed = (inner, style = '') => `<div class="frame" style="${style}">${inner}</div>`;
const label = (text, style, small = '') => `<div class="label" style="${style}">${text}${small ? `<small>${small}</small>` : ''}</div>`;
const stats = (items, color = accent.energy) => `<div class="stats" style="border-top:2px solid ${color}">${items.map(([t, v, flex]) =>
  `<div${flex ? ` style="flex:${flex}"` : ''}><div class="t">${t}</div><div class="v">${v}</div></div>`).join('')}</div>`;
// Two overlapping captures.
const pair = (first, second, firstLabel, secondLabel, firstSpot = 'left:20px;top:20px', height = 700) => `<div style="position:relative;height:${height}px">
    <div class="frame" style="position:absolute;left:0;top:0;width:74%"><img src="${shot(first)}" style="display:block;width:100%"></div>
    <div class="frame" style="position:absolute;right:0;bottom:0;width:74%"><img src="${shot(second)}" style="display:block;width:100%"></div>
    ${label(firstLabel, firstSpot)}${label(secondLabel, 'right:20px;bottom:20px')}</div>`;
// A big capture with two smaller ones underneath.
const trio = (big, [a, aLabel], [b, bLabel], bigLabel, crop = [0, 40, 1920, 830]) => `<div style="display:flex;flex-direction:column;gap:20px">
    ${framed(region(big, ...crop, 978) + label(bigLabel, 'left:18px;bottom:18px'))}
    <div style="display:flex;gap:20px">
      ${framed(region(a, 0, 0, 1920, 1080, 478) + label(aLabel, 'left:14px;bottom:14px;font-size:16px'), 'flex:1')}
      ${framed(region(b, 0, 0, 1920, 1080, 478) + label(bLabel, 'left:14px;bottom:14px;font-size:16px'), 'flex:1')}</div></div>`;
// A row of item icons drawn from the mod's own 16x16 textures, scaled without smoothing.
const icons = (list, size = 64, cols = 6) => `<div style="display:grid;grid-template-columns:repeat(${cols},1fr);gap:14px">${list.map(([file, name, kind = 'item']) =>
  `<div class="tile" style="height:${size + 64}px"><img class="px" src="${kind === 'item' ? item(file) : block(file)}" style="width:${size}px;height:${size}px"><span>${name}</span></div>`).join('')}</div>`;

const versionText = VERSIONS.map((v) => v.minecraft).join(' · ');

const assets = {
  banner: () => page(1800, 600, `
    <img src="${shot('factory.jpg')}" style="position:absolute;right:-60px;top:-110px;width:1440px;-webkit-mask-image:linear-gradient(90deg,transparent 0%,#000 38%);mask-image:linear-gradient(90deg,transparent 0%,#000 38%)">
    <div style="position:absolute;inset:0;background:linear-gradient(90deg,rgba(8,11,16,.97) 0%,rgba(8,11,16,.9) 30%,rgba(8,11,16,.35) 56%,rgba(8,11,16,0) 76%)"></div>
    <div style="position:absolute;inset:0;background:linear-gradient(0deg,rgba(8,11,16,.7) 0%,rgba(8,11,16,0) 30%)"></div>
    <div class="grid"></div><div class="vignette"></div>${traces}
    <div style="position:absolute;left:90px;top:74px;width:900px">
      <div class="kicker"><span class="chev"></span>${RELEASE.version} · ${RELEASE.name}</div>
      <div style="display:flex;align-items:center;gap:30px;margin:20px 0 24px">
        <img class="px" src="${source('logo.png')}" style="width:150px;height:150px;filter:drop-shadow(0 0 24px rgba(70,220,234,.45))">
        <div class="display steel glow" style="font-size:104px;line-height:.98;white-space:nowrap">Core<br>Utilities</div></div>
      <div class="rule" style="width:600px;margin-bottom:24px"></div>
      <div style="font-size:32px;color:var(--header);line-height:1.3;font-weight:600">Power it. Process it. Pipe it.<br>Automate everything.</div>
      <div style="margin-top:32px">${LOADERS.map((l) => `<span class="pill">${l}</span>`).join('')}
        <span style="font-size:24px;color:var(--dim);margin-left:8px;font-weight:700">${versionText}</span></div>
    </div>`),

  icon: () => page(512, 512, `
    <div style="position:absolute;inset:0;background:radial-gradient(circle at 50% 45%,#1b2633 0%,#0c1118 60%,#06080c 100%)"></div><div class="grid"></div>${traces}
    <div style="position:absolute;left:76px;top:76px;width:360px;height:360px;clip-path:polygon(25% 0,75% 0,100% 50%,75% 100%,25% 100%,0 50%);background:#0b1016"></div>
    <img class="px" src="${source('logo.png')}" style="position:absolute;left:116px;top:116px;width:280px;height:280px;filter:drop-shadow(0 0 22px rgba(70,220,234,.5))">
    <svg width="512" height="512" style="position:absolute;left:0;top:0"><polygon points="166,66 346,66 446,256 346,446 166,446 66,256" fill="none" stroke="#46dcea" stroke-width="6" style="filter:drop-shadow(0 0 12px #46dcea)"/>
      <polygon points="178,88 334,88 422,256 334,424 178,424 90,256" fill="none" stroke="rgba(70,220,234,.45)" stroke-width="2"/></svg>
    ${[[166, 66], [346, 66], [446, 256], [346, 446], [166, 446], [66, 256]].map(([x, y]) => `<div style="position:absolute;left:${x - 9}px;top:${y - 9}px;width:18px;height:18px;background:#a8f4fb;box-shadow:0 0 16px #46dcea"></div>`).join('')}`),

  'card-factory': () => card({
    kicker: 'A whole tech mod, compact', title: 'Build a<br>factory', color: accent.energy,
    points: ['Three generators and four tiers of solar', 'Energy cells and lag-free cable networks', 'Crusher, alloy smelter, compressor and electric furnace',
      'Quarry, crop harvester, mob grinder, block breaker and placer', 'Item pipes, upgrades and side configuration on every machine'],
    art: `${framed(`<img src="${shot('factory.jpg')}">`)}${stats([['Machines', '11'], ['Cable tiers', '3'], ['Energy', 'FE · TR Energy', 1.6]])}` }),

  'card-power': () => card({
    kicker: 'Generators', title: 'Make<br>power', color: accent.power,
    points: ['<b class="hi">Solar panels</b> in four tiers, with sky access; rain halves them', '<b class="hi">Combustion Generator</b> burns any furnace fuel, modded fuels too',
      'It never wastes fuel when its buffer is full', '<b class="hi">Geothermal Generator</b>: free power from every lava source it touches'],
    art: `${trio('solar-array.jpg', ['generators.jpg', 'Combustion'], ['geothermal.jpg', 'Geothermal'], 'Solar array')}
      ${stats([['Solar', '20 – 1,280 FE/t', 1.6], ['Combustion', '40 FE/t'], ['Geothermal', '15 FE/t per lava', 1.6]], accent.power)}` }),

  'card-energy': () => card({
    kicker: 'Storage & transport', title: 'Store it.<br>Share it.', color: accent.energy,
    points: ['<b class="hi">Energy Cells</b> keep their charge when broken', 'Every face can be set to input or output', '<b class="hi">Cables</b> form one cached network: no per-block ticking',
      'Power is split fairly between every receiver', 'Works with any mod that speaks FE or Team Reborn Energy'],
    art: `${pair('cables.jpg', 'energy-cells.jpg', 'Elite cable network', 'Basic · Advanced · Elite')}
      ${stats([['Cells', '1M · 8M · 64M FE', 1.5], ['Cables', '512 · 4,096 · 32,768 FE/t', 2]])}` }),

  'card-processing': () => card({
    kicker: 'Processing', title: 'Double<br>your ores', color: accent.process,
    points: ['<b class="hi">Crusher</b>: ores to two dusts, raw ore to 1.5×, plus vanilla conversions', '<b class="hi">Alloy Smelter</b>: Steel, Energized, Ender Alloy and Netherite',
      '<b class="hi">Compressor</b>: plates, packed ice, and 64 coal dust into a diamond', '<b class="hi">Electric Furnace</b>: every smelting recipe at twice the speed'],
    art: `<div style="position:relative;height:680px">${framed(region('crusher-gui.jpg', 420, 120, 1080, 760, 760), 'position:absolute;left:0;top:0')}
      <div class="frame" style="position:absolute;right:0;bottom:0;width:470px">${region('machine-row.jpg', 240, 200, 1440, 810, 470)}</div>
      ${label('Crusher', 'left:20px;top:20px', 'with 8 speed upgrades')}${label('Alloy · Compressor · Furnace', 'right:20px;bottom:20px')}</div>
      ${stats([['Ore', '×2 dust'], ['Speed upgrade', '+50% each', 1.3], ['Recipes', 'JEI', 0.8]], accent.process)}` }),

  'card-quarry': () => card({
    kicker: 'Mining', title: 'The<br>quarry', color: accent.energy,
    points: ['Digs a square down to bedrock, layer by layer', 'Leaves fluids, blocks with inventories and its own column alone',
      'Range upgrades widen it, speed upgrades dig faster', 'Pipe the output straight into storage'],
    art: `${pair('quarry.jpg', 'item-pipes.jpg', 'Quarry', 'Rapid item pipes')}
      ${stats([['Area', '9×9 → 33×33', 1.4], ['Cost', '400 FE / block', 1.4], ['Depth', 'Bedrock']])}` }),

  'card-automation': () => card({
    kicker: 'Automation', title: 'Farms that<br>run themselves', color: accent.auto,
    points: ['<b class="hi">Crop Harvester</b>: harvests ripe crops, replants and uses bone meal', '<b class="hi">Mob Grinder</b>: kills in an area and collects the drops',
      'It never touches players, named mobs or bosses', '<b class="hi">Block Breaker</b> and <b class="hi">Block Placer</b> for cobble and tree farms'],
    art: trio('crop-harvester.jpg', ['mob-grinder.jpg', 'Mob Grinder'], ['breaker-placer.jpg', 'Breaker & Placer'], 'Crop Harvester') }),

  'card-control': () => card({
    kicker: 'Every machine', title: 'Configure<br>every side', color: accent.control,
    points: ['Set each face to disabled, input, output or both', 'Redstone modes: ignored, high or low', 'Auto-eject outputs into neighbours and pipes',
      'Three upgrade slots: Speed, Energy, Range', 'Comparator output for stored energy'],
    art: `${framed(region('side-config.jpg', 300, 90, 1320, 900, 978))}${stats([['Faces', '6'], ['Modes', 'Off · In · Out · Both', 2], ['Upgrades', '8 each']], accent.control)}` }),

  'card-logistics': () => card({
    kicker: 'Logistics', title: 'Pipes that<br>just work', color: accent.logistics,
    points: ['Instant transfer, round robin between every destination', 'Use the wrench on a face to make it extract', 'Pipes connect to any inventory, from any mod',
      'Rapid pipes move a stack-sized batch every quarter second'],
    art: `${framed(`<img src="${shot('item-pipes.jpg')}">`)}${stats([['Item Pipe', '4 / 0.5 s', 1.2], ['Rapid Item Pipe', '32 / 0.25 s', 1.4], ['Mode', 'Round robin', 1.2]], accent.logistics)}` }),

  'card-tools': () => card({
    kicker: 'Tools', title: 'Know your<br>factory', color: accent.energy,
    points: ['<b class="hi">Configuration Wrench</b>: rotate, set pipe faces, sneak to dismantle', '<b class="hi">Multimeter</b>: read any block’s energy or a network’s capacity',
      '<b class="hi">Core Utilities Manual</b>: an in-game guide with 17 chapters', 'JEI categories for every machine on 1.20.1 and 1.21.1'],
    art: pair('guide.jpg', 'jei.jpg', 'Core Utilities Manual', 'JEI') }),

  'card-materials': () => card({
    kicker: 'Progression', title: 'From copper<br>to ender', color: accent.process,
    points: ['Start with vanilla materials: the first circuits need no power', 'Crush, alloy and press your way to steel', 'Energized and Ender Alloy unlock advanced and elite tiers',
      'Convention tags, so other mods’ ores and ingots work too'],
    art: icons([['iron_dust', 'Iron Dust'], ['gold_dust', 'Gold Dust'], ['copper_dust', 'Copper Dust'], ['coal_dust', 'Coal Dust'], ['steel_ingot', 'Steel Ingot'], ['energized_ingot', 'Energized Ingot'],
      ['ender_alloy_ingot', 'Ender Alloy'], ['iron_plate', 'Iron Plate'], ['copper_plate', 'Copper Plate'], ['gold_plate', 'Gold Plate'], ['steel_plate', 'Steel Plate'], ['basic_circuit', 'Basic Circuit'],
      ['advanced_circuit', 'Advanced Circuit'], ['elite_circuit', 'Elite Circuit'], ['speed_upgrade', 'Speed Upgrade'], ['energy_upgrade', 'Energy Upgrade'], ['range_upgrade', 'Range Upgrade'], ['wrench', 'Wrench'],
      ['multimeter', 'Multimeter'], ['guide_book', 'Manual'], ['advanced_machine_frame', 'Machine Frame', 'block'], ['steel_block', 'Steel Block', 'block'], ['backpack_3', 'Backpack'], ['animal_net', 'Mob Net']], 64, 6) }),

  'card-config': () => card({
    kicker: 'Make it yours', title: 'Every number<br>is yours', color: accent.energy,
    points: ['Every generator rate and cable throughput', 'Cell capacities and transfer rates', 'Machine speed and energy multipliers', 'Quarry, harvester, grinder and pipe settings'],
    art: `<div class="frame" style="background:rgba(8,11,16,.94);padding:30px 40px;font-family:monospace;font-size:24px;line-height:1.55;color:var(--text)">
      <div style="color:var(--dim)">config/core_utilities.properties</div>
      <pre style="margin:12px 0 0;font-family:inherit">${[['# Generators (FE per tick)'], ['solarTier4Rate', '1280'], ['combustionGeneratorRate', '40'], ['geothermalRatePerLava', '15'],
        ['# Cables (FE per tick per network)'], ['cableEliteRate', '32768'], ['# Energy cells'], ['cellEliteCapacity', '64000000'],
        ['# Machines'], ['machineSpeedMultiplier', '1.0'], ['quarryEnergyPerBlock', '400'], ['quarryMaxRadius', '16'], ['itemPipeRapidItems', '32']]
        .map(([k, v]) => v === undefined ? `<span style="color:var(--dim)">${k}</span>` : `<span style="color:var(--cyan)">${k}</span>=<span style="color:var(--amber)">${v}</span>`).join('\n')}</pre></div>` }),
};

const headers = { 'header-factory': 'The Factory', 'header-power': 'Power', 'header-energy': 'Storage & Cables', 'header-processing': 'Processing',
  'header-quarry': 'The Quarry', 'header-automation': 'Automation', 'header-control': 'Machine Control', 'header-logistics': 'Item Pipes',
  'header-tools': 'Tools & Guide', 'header-materials': 'Progression', 'header-config': 'Configuration', 'header-utilities': 'Original Utilities',
  'header-everything': 'Everything Else', 'header-compatibility': 'Downloads & Compatibility', 'header-faq': 'Questions', 'header-gallery': 'Gallery' };
for (const [name, title] of Object.entries(headers)) assets[name] = () => header(title);
assets.divider = () => page(1400, 40, `<div style="position:absolute;inset:0;display:flex;align-items:center;justify-content:center;gap:18px">
  <div class="rule" style="width:520px;transform:scaleX(-1)"></div><span class="chev" style="margin:0;transform:rotate(-135deg)"></span><span class="chev" style="margin:0"></span><div class="rule" style="width:520px"></div></div>`, true);

(async () => {
  const wanted = process.argv.slice(2);
  const browser = await chromium.launch();
  fs.mkdirSync(out, { recursive: true });
  for (const [name, build] of Object.entries(assets)) {
    if (wanted.length && !wanted.includes(name)) continue;
    const html = build();
    const size = html.match(/body \{ width: (\d+)px; height: (\d+)px; \}/);
    const tab = await browser.newPage({ viewport: { width: +size[1], height: +size[2] }, deviceScaleFactor: 1 });
    const file = path.join(__dirname, `.render-${name}.html`);
    fs.writeFileSync(file, html);
    await tab.goto(url(file));
    await tab.evaluate(() => document.fonts.ready);
    await tab.waitForLoadState('networkidle');
    await tab.screenshot({ path: path.join(out, `${name}.png`), omitBackground: name === 'divider' || name.startsWith('header-') });
    fs.unlinkSync(file);
    await tab.close();
    console.log('rendered', name);
  }
  await browser.close();
})().catch((error) => { console.error(error); process.exit(1); });
