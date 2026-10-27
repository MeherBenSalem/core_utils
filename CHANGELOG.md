# Changelog

## 2.0.0

The technology update: power, machines, logistics and automation on every version and loader.

### Added
- **Generators:**
  - Combustion Generator (any furnace fuel, 40 FE/t, never wastes fuel).
  - Geothermal Generator (15 FE/t per adjacent lava source).
- **Energy storage:**
  - Basic, Advanced, Elite and Creative Energy Cells.
  - Configurable input and output faces.
  - Cells keep their charge when broken.
- **Energy transport:**
  - Advanced and Elite Energy Cables (4,096 and 32,768 FE/t per network).
  - The existing cable becomes the Basic tier (512 FE/t).
- **Machines:**
  - Processing: Electric Furnace, Crusher, Alloy Smelter, Compressor.
  - Automation: Quarry, Crop Harvester, Mob Grinder, Block Breaker, Block Placer.
- **Machine features:**
  - Shared GUI with an energy bar.
  - Per-face side configuration, redstone modes and auto-eject.
  - Comparator output.
  - Speed, Energy and Range upgrades.
- **Item logistics:** Item Pipe and Rapid Item Pipe. Extraction is set per face with the wrench, and items are
  delivered round robin.
- **Materials and progression:**
  - Iron, gold, copper and coal dusts.
  - Steel, Energized and Ender Alloy ingots.
  - Iron, copper, gold and steel plates.
  - Basic, Advanced and Elite circuits.
  - Advanced Machine Frame and Block of Steel.
  - Convention tags (`c:`, plus `forge:` on 1.20.1).
- **Tools:**
  - Multimeter.
  - Core Utilities Manual (in-game guide).
  - Configuration Wrench rework: rotate, configure conduit faces, sneak to dismantle.
- **Compatibility:**
  - Forge Energy capabilities (Forge, NeoForge).
  - Team Reborn Energy (Fabric, bundled).
  - Item handler and Transfer API support.
  - JEI categories and catalysts (1.20.1, 1.21.1).
  - Jade shows stored energy through the FE capability.
- **Configuration:** `config/core_utilities.properties` covers every rate and cost.
- **Developer tooling:**
  - Resource generator (`scripts/assetgen`).
  - In-game server test (`scripts/servertest`).
  - JUnit tests.
  - `docs/feature-matrix.md`.

### Changed
- Energy system rewritten:
  - Cables form cached networks.
  - Cables hold no energy and do no per-block work.
  - Power is split fairly between receivers.
- Solar panels:
  - They now need sky access and generate into an internal buffer.
  - They push energy to any neighbour below or beside them, not only to a cable directly underneath.
  - Rebalanced to 20 / 80 / 320 / 1280 FE/t; tier 1 was 64 FE/t.
- Old per-shape cable blocks and per-weather solar panel blocks convert to the new blocks in existing worlds.
- All machine, cell and conduit textures are new. Item icons are hand-drawn pixel art.
- Version 2.0.0 on every version root.

### Fixed
- **Crashes:**
  - 1.20.1 Fabric dedicated server crashed on start (client-only networking class loaded on the server).
  - 1.21.1 Fabric server crashed when a cable or solar panel was placed (block entity types never assigned).
  - 26.2 NeoForge crashed on start (stale example service file).
  - 26.2 Fabric crashed on start (`Block id not set`).
- **Data and assets:**
  - 1.21.1 and 26.2 loaded no recipes and no loot tables: wrong folder names and the pre-1.21 recipe format.
  - 26.2 rendered every item as a missing texture (no item model definitions).
  - Pushing and effect plates rendered as missing models (invalid `vector` parent model).
  - Non power-of-two filter textures disabled mipmapping for the whole block atlas.
- **Energy and performance:**
  - Cables duplicated energy, could not power blocks from other mods, and the mod had no consumers at all.
  - Every cable and solar panel swapped its block every tick, and a global timer sent a packet to every player every
    tick. Both caused lag and network spam.
- **Other:**
  - Missing mod icon (Fabric) and logo (NeoForge).
  - Duplicate legacy lang keys; "Resistence" typo.
  - Gradle wrappers were not executable.
  - The 26.2 tree contained decompiled Minecraft source code; it has been removed.

## 1.4.0

- MultiLoader release: Fabric + Forge on 1.20.1; Fabric + NeoForge on 1.21.1 and 26.2
- Relicensed under Apache License 2.0
- Removed MCreator project clutter; rebuilt as Apache MultiLoader workspaces
- Energy cables and solar panels use a shared EnergyBuffer with per-loader adapters
