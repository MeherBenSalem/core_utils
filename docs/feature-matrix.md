# Core Utilities: feature matrix and gap analysis

Research date: 2026-10-27. Loaders and versions come from the Modrinth API; feature details come from each mod's
documentation and wiki. "(?)" marks a detail that was not confirmed.

## 1. Where the field stands

| Mod | 1.20.1 | 1.21.1 | 26.x | Fabric on 1.21+? |
| --- | --- | --- | --- | --- |
| Mekanism | Forge, NeoForge | NeoForge | unofficial ports only | no |
| Thermal Series | Forge, NeoForge | none | none | no |
| Create | Forge, NeoForge (+ separate Fabric port) | NeoForge | unofficial forks | no |
| Immersive Engineering | Forge, NeoForge | NeoForge | none | no |
| Applied Energistics 2 | Fabric, Forge, NeoForge | NeoForge | NeoForge 26.1–26.3 | no |
| Refined Storage | Forge, NeoForge | Fabric, NeoForge | Fabric, NeoForge 26.1 | **yes** |
| Ender IO | Forge | NeoForge | NeoForge 26.1 | no |
| Industrial Foregoing | Forge | NeoForge | none | no |
| Powah | Fabric, Forge | NeoForge | NeoForge 26.1 | no |
| Pipez | Forge, NeoForge | NeoForge | NeoForge 26.1–26.3 | no |
| Extreme Reactors | Forge | NeoForge | none | no |
| Modern Industrialization | Fabric | NeoForge | none | no |
| Integrated Dynamics | Forge | NeoForge | NeoForge 26.1–26.3 | no |
| Functional Storage | Forge | NeoForge | NeoForge 26.1 | no |
| Sophisticated Storage/Backpacks | Forge, NeoForge | NeoForge | NeoForge 26.1–26.3 | no |
| **Core Utilities 2.0** | **Fabric, Forge** | **Fabric, NeoForge** | **Fabric, NeoForge 26.2** | **yes** |

The largest opening is loader parity. Fabric players on 1.21.1 and 26.x have almost no full tech mod, and most of
the big names have no official 26.x build yet.

## 2. Feature matrix

Legend: ● full, ◐ partial, ○ none. For Core Utilities, *before* is 1.4.0 and *after* is this release.

| Area | Mekanism | Thermal | Create | IE | AE2 | RS | Ender IO | IF | Powah | Pipez | MI | CU before | **CU after** |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| FE generation | ● 8+ types | ● 7 dynamos | ○ (SU) | ● | ○ | ○ | ◐ | ● | ● | ○ | ● (EU) | ◐ solar only, broken | ● solar ×4, combustion, geothermal |
| FE storage | ● cubes + matrix | ● cells | ○ | ● capacitors | ◐ | ○ | ● capacitor banks | ◐ | ● cells | ○ | ● | ○ | ● 3 tiers + creative, keeps charge |
| Energy transport | ● 4 tiers | ● ducts | ○ | ● wires | ○ | ○ | ● conduits | ○ | ● | ● | ● | ◐ (dupes, no interop) | ● 3 tiers, fair split, no ticking |
| Item logistics | ● transporters, sorter | ◐ | ● belts, funnels | ● conveyors | ● buses | ● importer/exporter | ● conduits | ◐ | ○ | ● | ● | ○ | ● pipes, extract per face, round robin |
| Fluid / gas logistics | ● | ● | ● | ● | ◐ | ◐ | ● | ◐ | ○ | ● | ● | ○ | ○ (planned) |
| Ore processing | ● 2x–5x | ◐ ~2x | ◐ ~2x | ◐ 2x | ○ | ○ | ◐ 2x | ○ | ○ | ○ | ● 2–3x | ○ | ● 2x crusher (6x copper), ~1.5x raw |
| Alloys / materials | ● | ● | ● | ● | ○ | ○ | ● | ◐ | ◐ | ○ | ● | ○ | ● steel, energized, ender alloy, plates |
| Machine upgrades | ● | ● augments | ○ | ◐ | ● cards | ● | ● capacitors | ● addons | ○ | ● | ◐ | ○ | ● speed, energy, range |
| Side configuration | ● | ● | ○ | ○ | ◐ | ◐ | ● | ● | ○ | ● | ● | ○ | ● per face I/O, auto-eject |
| Redstone control | ● | ● | ◐ | ◐ | ● | ● | ● | ● | ○ | ● | ● | ○ | ● ignore / high / low |
| Farming automation | ◐ | ● insolator | ● harvesters | ◐ | ○ | ○ | ◐ | ● | ○ | ○ | ○ | ○ | ● crop harvester with replant and bone meal |
| Mob automation | ○ | ○ | ◐ | ○ | ○ | ○ | ● spawner | ● | ○ | ○ | ○ | ◐ filters, plates | ● mob grinder + filters/plates |
| Mining | ● digital miner | ○ | ● drills | ● excavator | ○ | ○ | ○ | ● laser drill | ○ | ○ | ● | ○ | ● quarry |
| Block breaker / placer | ○ | ○ | ● deployers | ○ | ● planes | ● | ○ | ● | ○ | ○ | ○ | ○ | ● |
| Multiblocks | ● | ○ | ● | ● | ◐ | ○ | ◐ | ○ | ◐ | ○ | ● | ○ | ○ (planned) |
| Network / wireless storage | ● QIO | ○ | ◐ | ○ | ● | ● | ◐ | ◐ | ◐ | ○ | ○ | ○ | ○ (out of scope) |
| Tools | ● | ◐ | ● | ● | ◐ | ◐ | ● | ● | ○ | ○ | ◐ | ◐ wrench, mob net, backpacks | ● wrench (rotate/configure/dismantle), multimeter |
| Recipe viewer | JEI | JEI | JEI/EMI/REI | JEI | JEI/REI/EMI | JEI/REI/EMI | JEI | JEI | JEI | JEI | EMI/REI/JEI | ○ | ● JEI on 1.20.1 and 1.21.1 |
| Jade / WTHIT | ● | ● | ● | ● | ● | ● | ● | ● | ● | ● | ● | ○ | ● energy through Jade's FE support on Forge/NeoForge; ○ Fabric |
| Config | ● | ● | ● | ● | ● | ● | ● | ● | ● | ● | ● | ○ | ● one properties file on every loader |
| In-game guide | ○ (wiki) | ○ | ● Ponder | ● manual | ● guide | ◐ | ○ | ○ | ● manual | ○ | ● | ○ | ● 17-chapter manual |
| Cross-mod energy API | FE | FE | ○ | FE | FE | FE | FE | FE | FE | FE | EU/FE | ○ (claimed, not true) | ● FE (NeoForge/Forge caps), Team Reborn Energy (Fabric) |

## 3. Gaps closed in this release

1. **Energy that works.** The 1.4.0 energy system duplicated energy and could not power anything outside the mod,
   and the mod had nothing that consumed power. It is replaced by a standard FE system exposed through NeoForge and
   Forge capabilities and through Team Reborn Energy on Fabric, so Core Utilities generators can power Mekanism,
   Thermal, Powah and other FE machines, and the reverse.
2. **A real tech loop.** Generators, storage, transport, processing and automation, built from vanilla materials at
   first, with steel, Energized and Ender Alloy as the tier gates.
3. **Logistics.** Pipez-style item pipes, set to extract per face with the wrench, plus per-face side configuration
   and auto-eject on every machine.
4. **Automation.** A quarry, a crop harvester with replanting, a mob grinder and a block breaker/placer, which covers
   the most-used Industrial Foregoing and Mekanism automation blocks.
5. **Onboarding.** An in-game manual, JEI categories, tooltips with real numbers and a multimeter.

## 4. Where Core Utilities is better than the leaders

- **Loader parity.** The same blocks and behaviour ship on Fabric, Forge and NeoForge for 1.20.1, 1.21.1 and 26.2.
  None of the 15 mods above does this.
- **Performance by design.**
  - Cables hold no energy and never tick: energy moves only when a generator pushes it, along a cached network that
    is invalidated in O(1).
  - Pipes tick only on faces set to extract.
  - Machines skip work when they have no recipe or no output space.
  - The old 1.4.0 code ran a block swap every tick for every cable and solar panel, and sent a network packet to
    every player every tick. All of that is gone.
- **Simple progression.**
  - Five-minute bootstrap: basic circuits, frames, generators and the first machines need only vanilla materials.
  - At most three steps from ore to any component.
  - No library dependency (unlike Titanium or ZeroCore).
- **One UX for everything.**
  - The same GUI layout, Sides panel and redstone and auto-eject tabs on every machine.
  - One wrench to rotate, configure faces and dismantle.
  - Tooltips state rates in FE/t.
- **Tunable.** Every rate (generators, cables, cells, machine speed and energy multipliers, quarry and grinder costs)
  is in `config/core_utilities.properties`.

## 5. Still missing (roadmap, ordered by impact)

1. Fluid transport and tanks (fluid pipes, a pump, a fluid tank), plus geothermal and combustion generators that
   burn fluids.
2. A 3x ore tier, for example a Washer or Enricher that turns ores into 3 dusts.
3. Factory tiers (multi-slot machines) and a filtered quarry (Digital Miner style).
4. Item filters on pipes, and pipe distribution modes (nearest, round robin, random).
5. Wireless energy (an Ender Cell style shared buffer) and wireless charging for items.
6. EMI/REI plugins for Fabric players without JEI, a JEI plugin on 26.2 once JEI ships there, and a WTHIT/Jade
   plugin showing progress and side configuration.
7. One flagship multiblock, for example a large battery or a reactor, with a hologram build preview.
8. Data-driven machine recipes through datapacks/KubeJS. Recipes are currently defined in code and use tags, so
   other mods' ores work.
