![Core Utilities 2.0: The Technology Update](https://raw.githubusercontent.com/MeherBenSalem/core_utils/main/marketing/images/banner.png)

## Power it. Process it. Pipe it.

**Core Utilities** is a compact technology mod: generators, energy cells, cables, ore processing, a quarry, farms, a mob grinder and item pipes, all on Forge Energy. It runs on **Fabric, Forge and NeoForge** with the same content on every version, works with other tech mods out of the box, and comes with an in-game manual.

**2.0 "The Technology Update"** rebuilds the mod around a real energy system and adds 11 machines, 3 cable tiers, energy cells, item pipes, upgrades and a full material progression. The original pushing plates, effect plates, filters, backpacks and mob net are still here.

![divider](https://raw.githubusercontent.com/MeherBenSalem/core_utils/main/marketing/images/divider.png)

![The Factory](https://raw.githubusercontent.com/MeherBenSalem/core_utils/main/marketing/images/header-factory.png)

![Build a factory](https://raw.githubusercontent.com/MeherBenSalem/core_utils/main/marketing/images/card-factory.png)

- **Generators:** four tiers of solar panels, the Combustion Generator and the Geothermal Generator.
- **Storage and transport:** Basic, Advanced, Elite and Creative Energy Cells, and three tiers of energy cable.
- **Processing:** Crusher, Alloy Smelter, Compressor and Electric Furnace.
- **Automation:** Quarry, Crop Harvester, Mob Grinder, Block Breaker and Block Placer.
- **Logistics:** Item Pipe and Rapid Item Pipe.
- **Every machine** has a GUI with an energy bar, per-face side configuration, redstone modes, auto-eject, three upgrade slots and a comparator output.

![Quarry timelapse](https://raw.githubusercontent.com/MeherBenSalem/core_utils/main/marketing/images/gifs/quarry.gif)

![Power](https://raw.githubusercontent.com/MeherBenSalem/core_utils/main/marketing/images/header-power.png)

![Make power](https://raw.githubusercontent.com/MeherBenSalem/core_utils/main/marketing/images/card-power.png)

| Generator | Output | Notes |
| --- | --- | --- |
| Solar Panel tier 1 to 4 | 20 / 80 / 320 / 1,280 FE/t | Needs sky access. Rain halves the output and thunderstorms quarter it |
| Combustion Generator | 40 FE/t | Burns any furnace fuel, modded fuels included. Stops burning when its buffer is full, so no fuel is wasted |
| Geothermal Generator | 15 FE/t per lava source | Counts every lava source block touching any of its faces. The lava is never used up |

Generators push their energy into any neighbour that accepts it: a cable, a cell, a machine, or another mod's block.

![Storage & Cables](https://raw.githubusercontent.com/MeherBenSalem/core_utils/main/marketing/images/header-energy.png)

![Store it, share it](https://raw.githubusercontent.com/MeherBenSalem/core_utils/main/marketing/images/card-energy.png)

| Block | Capacity | Transfer |
| --- | --- | --- |
| Basic Energy Cell | 1,000,000 FE | 2,000 FE/t |
| Advanced Energy Cell | 8,000,000 FE | 16,000 FE/t |
| Elite Energy Cell | 64,000,000 FE | 128,000 FE/t |
| Creative Energy Cell | Infinite | Infinite |
| Basic Energy Cable | – | 512 FE/t per network |
| Advanced Energy Cable | – | 4,096 FE/t per network |
| Elite Energy Cable | – | 32,768 FE/t per network |

- **Cells keep their charge** when broken. Each face can be set to input or output in the Sides panel.
- **Cables hold no energy and are never ticked.** Connected cables form one cached network that is rebuilt only when it changes, so a thousand cables cost the same as one.
- **Fair split:** a network's budget is shared evenly between every receiver, and what one receiver cannot take goes to the others.

![Cables connecting a network](https://raw.githubusercontent.com/MeherBenSalem/core_utils/main/marketing/images/gifs/cables.gif)

![Processing](https://raw.githubusercontent.com/MeherBenSalem/core_utils/main/marketing/images/header-processing.png)

![Double your ores](https://raw.githubusercontent.com/MeherBenSalem/core_utils/main/marketing/images/card-processing.png)

| Machine | What it does |
| --- | --- |
| **Crusher** | Ore to 2 dusts (copper ore to 6), raw ore to 1 dust plus a 50% chance of a second, ingots back to dust. Also coal ore to 3 coal, diamond and emerald ore to 2 gems, redstone, lapis and quartz ores, cobblestone to gravel to sand, bones to bone meal, blaze rods to powder and wool to string |
| **Alloy Smelter** | Iron + 2 coal dust → **Steel Ingot**. Gold + 4 redstone → **Energized Ingot**. Energized Ingot + Ender Pearl → **Ender Alloy Ingot**. 4 Netherite Scrap + 4 gold → Netherite Ingot |
| **Compressor** | Iron, copper, gold and steel plates. 64 coal dust → a Diamond. Snowballs, ice, packed ice and sand into their blocks |
| **Electric Furnace** | Every smelting recipe, modded ones included, at twice furnace speed |

Dusts smelt back into ingots in a furnace or blast furnace. Every recipe accepts convention tags, so ores and ingots from other mods work too. JEI shows every machine recipe on 1.20.1 and 1.21.1.

![The Quarry](https://raw.githubusercontent.com/MeherBenSalem/core_utils/main/marketing/images/header-quarry.png)

![The quarry](https://raw.githubusercontent.com/MeherBenSalem/core_utils/main/marketing/images/card-quarry.png)

- Mines a 9×9 area below itself, layer by layer, down to bedrock. Each Range Upgrade adds 2 to the radius, up to 33×33.
- Leaves fluids, unbreakable blocks such as bedrock, blocks with inventories and its own column alone.
- Uses 400 FE per block. Speed Upgrades dig faster.
- Mined items go into its 9 output slots, or straight into a pipe or chest with auto-eject.

![Automation](https://raw.githubusercontent.com/MeherBenSalem/core_utils/main/marketing/images/header-automation.png)

![Farms that run themselves](https://raw.githubusercontent.com/MeherBenSalem/core_utils/main/marketing/images/card-automation.png)

- **Crop Harvester:** harvests ripe crops around it, replants them, and uses bone meal from its input slot.
- **Mob Grinder:** damages mobs in the area in front of it and collects their drops. It never touches players, named mobs or bosses.
- **Block Breaker:** breaks the block in front of it and keeps the drops. Pair it with a cobblestone generator or a tree farm.
- **Block Placer:** places blocks from its inventory in front of it.

![Machine Control](https://raw.githubusercontent.com/MeherBenSalem/core_utils/main/marketing/images/header-control.png)

![Configure every side](https://raw.githubusercontent.com/MeherBenSalem/core_utils/main/marketing/images/card-control.png)

- **Sides panel:** set each face to disabled, input, output or both. Faces are relative to the machine's front, so rotating it keeps your layout.
- **Redstone:** ignored, active on a high signal, or active on a low signal.
- **Auto-eject** pushes outputs into the neighbours on output faces.
- **Upgrades:** up to 8 of each in three slots. Speed (+50% speed each), Energy (−15% energy each) and Range (+2 radius each, for area machines).
- **Comparator output** for stored energy.

![Item Pipes](https://raw.githubusercontent.com/MeherBenSalem/core_utils/main/marketing/images/header-logistics.png)

![Pipes that just work](https://raw.githubusercontent.com/MeherBenSalem/core_utils/main/marketing/images/card-logistics.png)

- Use the wrench on a pipe face next to an inventory to make it **extract**. Everything else on the pipe network is a destination.
- Items move instantly and are handed out **round robin**.
- **Item Pipe:** 4 items every half second. **Rapid Item Pipe:** 32 items every quarter second.
- Pipes work with any inventory, from vanilla chests to other mods' storage.

![Tools & Guide](https://raw.githubusercontent.com/MeherBenSalem/core_utils/main/marketing/images/header-tools.png)

![Know your factory](https://raw.githubusercontent.com/MeherBenSalem/core_utils/main/marketing/images/card-tools.png)

- **Configuration Wrench:** rotate machines, cycle a cable or pipe face between connected, extract and disconnected, and sneak-use to dismantle a machine into your inventory.
- **Multimeter:** shows a block's stored energy, or a cable network's size, connections and throughput.
- **Core Utilities Manual:** craft it from a Book and a Copper Ingot. 17 chapters cover every block, every recipe chain and the numbers behind them.

![Progression](https://raw.githubusercontent.com/MeherBenSalem/core_utils/main/marketing/images/header-materials.png)

![From copper to ender](https://raw.githubusercontent.com/MeherBenSalem/core_utils/main/marketing/images/card-materials.png)

1. **Basic Circuits** need only redstone, copper and iron, so your first generator and machines need no power to build.
2. Crush coal and ore, then alloy **Steel**. Press it into **Steel Plates** for the Advanced Machine Frame.
3. Alloy gold with redstone into **Energized Ingots** for Advanced Circuits, Advanced Cells and Advanced Cable.
4. Alloy Energized Ingots with Ender Pearls into **Ender Alloy** for Elite Circuits, Elite Cells and Elite Cable.
5. The Quarry and the Mob Grinder need the Advanced Machine Frame.

![Configuration](https://raw.githubusercontent.com/MeherBenSalem/core_utils/main/marketing/images/header-config.png)

![Every number is yours](https://raw.githubusercontent.com/MeherBenSalem/core_utils/main/marketing/images/card-config.png)

`config/core_utilities.properties` is created on first launch. It holds every generator rate, cable throughput, cell capacity and transfer rate, machine speed and energy multipliers, and the quarry, harvester, grinder and pipe settings. Missing keys are added with their defaults, and your values are kept.

![Original Utilities](https://raw.githubusercontent.com/MeherBenSalem/core_utils/main/marketing/images/header-utilities.png)

- **Pushing Plates** in four strengths move entities in the direction they face. Sneaking players are not pushed.
- **Effect Plates** give Speed, Strength, Regeneration, Fire Resistance, Slowness, Weakness, Poison or Wither to whoever steps on them.
- **Entity Filters** let only one kind of entity through (players, mobs, animals, pets, villagers, zombies, skeletons, spiders, neutral mobs or projectiles), or block only that kind.
- **Backpacks** in three sizes, the **Mob Net** to carry a mob, and the **Floating Block** that can be placed in mid-air.

![divider](https://raw.githubusercontent.com/MeherBenSalem/core_utils/main/marketing/images/divider.png)

![Everything Else](https://raw.githubusercontent.com/MeherBenSalem/core_utils/main/marketing/images/header-everything.png)

- **Works with other tech mods.** Every energy block is a Forge Energy capability on Forge and NeoForge and a Team Reborn Energy storage on Fabric (bundled, nothing to install). Item handlers and the Fabric Transfer API are supported, so hoppers, pipes and storage from other mods connect.
- **Jade** shows stored energy on Forge and NeoForge.
- **Older worlds convert.** The 1.x cable shapes and weather solar panels turn into the new blocks when a world loads.
- **Lightweight.** Cables are never ticked, machines only update their block when they start or stop, and nothing sends packets every tick.

![Downloads & Compatibility](https://raw.githubusercontent.com/MeherBenSalem/core_utils/main/marketing/images/header-compatibility.png)

| Minecraft | Loaders | Java | Also install |
| --- | --- | --- | --- |
| 1.20.1 | Fabric, Forge | 17+ | Fabric API (Fabric) |
| 1.21.1 | Fabric, NeoForge | 21+ | Fabric API (Fabric) |
| 26.2 | Fabric, NeoForge | 25+ | Fabric API (Fabric) |

- Download the file with your loader in its name: `-fabric-`, `-forge-` or `-neoforge-`.
- Install Core Utilities on **both the server and every client**, at the same version.
- JEI is optional. Install it for the recipe categories (1.20.1 and 1.21.1).
- Core Utilities is a mod. It does not work as a Paper, Spigot or Folia plugin.

![Questions](https://raw.githubusercontent.com/MeherBenSalem/core_utils/main/marketing/images/header-faq.png)

**Does it work with Mekanism, Thermal or other energy mods?**
Yes. Core Utilities uses Forge Energy (FE) on Forge and NeoForge and Team Reborn Energy on Fabric, the same systems most tech mods use. Cables connect to their machines and cells, and theirs connect to ours.

**Why is my solar panel not producing?**
It needs a clear view of the sky, and it only works during the day. Rain halves its output and thunderstorms quarter it. Use the Multimeter to check.

**My machine has power but does nothing.**
Check the redstone mode in its GUI, and that its output slots are not full. Turn on auto-eject or connect a pipe to an output face.

**How do I make a pipe pull from a chest?**
Hold the Configuration Wrench and use it on the pipe face that touches the chest. The connector changes to the extract texture.

**Will cables lag my server?**
No. Cables are never ticked. A network is cached and rebuilt only when you place or break a cable on it.

**I updated from 1.x. What happens to my cables and panels?**
The old cable shapes become Basic Energy Cables and the old weather variants become the matching solar panel when the world loads.

**Where are the docs?**
The in-game **Core Utilities Manual** · [Feature matrix](https://github.com/MeherBenSalem/core_utils/blob/main/docs/feature-matrix.md) · [Changelog](https://github.com/MeherBenSalem/core_utils/blob/main/CHANGELOG.md) · [Issues](https://github.com/MeherBenSalem/core_utils/issues)

![Gallery](https://raw.githubusercontent.com/MeherBenSalem/core_utils/main/marketing/images/header-gallery.png)

![The factory](https://raw.githubusercontent.com/MeherBenSalem/core_utils/main/marketing/images/screenshots/factory.jpg)

![A solar array on an Advanced Energy Cable](https://raw.githubusercontent.com/MeherBenSalem/core_utils/main/marketing/images/screenshots/solar-array.jpg)

![Combustion Generators](https://raw.githubusercontent.com/MeherBenSalem/core_utils/main/marketing/images/screenshots/generators.jpg)

![A Geothermal Generator on a lava basin](https://raw.githubusercontent.com/MeherBenSalem/core_utils/main/marketing/images/screenshots/geothermal.jpg)

![Energy Cells](https://raw.githubusercontent.com/MeherBenSalem/core_utils/main/marketing/images/screenshots/energy-cells.jpg)

![An Elite Energy Cable network](https://raw.githubusercontent.com/MeherBenSalem/core_utils/main/marketing/images/screenshots/cables.jpg)

![The Crusher's GUI](https://raw.githubusercontent.com/MeherBenSalem/core_utils/main/marketing/images/screenshots/crusher-gui.jpg)

![Alloy Smelter, Compressor and Electric Furnace](https://raw.githubusercontent.com/MeherBenSalem/core_utils/main/marketing/images/screenshots/machine-row.jpg)

![The Sides panel](https://raw.githubusercontent.com/MeherBenSalem/core_utils/main/marketing/images/screenshots/side-config.jpg)

![The Quarry](https://raw.githubusercontent.com/MeherBenSalem/core_utils/main/marketing/images/screenshots/quarry.jpg)

![Rapid Item Pipes into chests](https://raw.githubusercontent.com/MeherBenSalem/core_utils/main/marketing/images/screenshots/item-pipes.jpg)

![The Crop Harvester](https://raw.githubusercontent.com/MeherBenSalem/core_utils/main/marketing/images/screenshots/crop-harvester.jpg)

![The Mob Grinder](https://raw.githubusercontent.com/MeherBenSalem/core_utils/main/marketing/images/screenshots/mob-grinder.jpg)

![Block Breaker and Block Placer](https://raw.githubusercontent.com/MeherBenSalem/core_utils/main/marketing/images/screenshots/breaker-placer.jpg)

![The Core Utilities Manual](https://raw.githubusercontent.com/MeherBenSalem/core_utils/main/marketing/images/screenshots/guide.jpg)

![JEI recipes](https://raw.githubusercontent.com/MeherBenSalem/core_utils/main/marketing/images/screenshots/jei.jpg)

---

Core Utilities is licensed under Apache-2.0. The Anton and Montserrat fonts used in this artwork are licensed under the SIL Open Font License 1.1.
