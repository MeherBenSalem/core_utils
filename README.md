# Core Utilities

Core Utilities is a compact technology mod for Minecraft: FE power, machines, cables, item pipes and automation,
with the same content on Fabric, Forge and NeoForge for every supported version. It also keeps the original
utility blocks: vector and effect plates, entity filters, backpacks and the mob net.

## Supported versions

| Minecraft | Loaders | Java | Folder |
| --- | --- | --- | --- |
| 1.20.1 | Fabric, Forge | 17 bytecode (build on 21) | `1.20.1/{common,fabric,forge}` |
| 1.21.1 | Fabric, NeoForge | 21 | `1.21.1/{common,fabric,neoforge}` |
| 26.2 | Fabric, NeoForge | 25 | `26.2/{common,fabric,neoforge}` |
| 26.3 | Fabric, NeoForge | 25 | `26.3/{common,fabric,neoforge}` |

These are the only supported targets (the NightBeam convention): 8 jars per release, all with the same version.
Each version root is an independent MultiLoader Gradle workspace with its own wrapper.

## Features

**Energy (FE)**
- Energy is exposed as Forge Energy capabilities on Forge and NeoForge, and as Team Reborn Energy on Fabric (bundled).
  Every block works with other tech mods.
- Generators:
  - Solar panels in 4 tiers (20 / 80 / 320 / 1280 FE/t). They need sky access; rain halves the output and storms
    quarter it.
  - Combustion Generator: any furnace fuel, including modded fuels. It never wastes fuel when full.
  - Geothermal Generator: 15 FE/t for each lava source touching it.
- Energy Cells in 3 tiers plus a creative cell (1M / 8M / 64M FE). They keep their charge when broken, and each
  face can be set to input or output.
- Energy cables in 3 tiers (512 / 4,096 / 32,768 FE/t per network). They hold no energy, are never ticked, and
  split power fairly over a cached network.

**Machines**
- Electric Furnace (2x furnace speed, any smelting recipe).
- Crusher: ore doubling, raw ore to 1.5x, plus gems and vanilla conversions.
- Alloy Smelter: Steel, Energized and Ender Alloy ingots, and Netherite.
- Compressor: plates, dense blocks, and 64 coal dust into a diamond.
- Quarry: mines down to bedrock, leaving fluids and blocks with inventories alone.
- Crop Harvester: harvests, replants and uses bone meal.
- Mob Grinder: never touches players, named mobs or bosses.
- Block Breaker and Block Placer.
- Every machine has:
  - a GUI with an energy bar;
  - a Sides panel: each face is disabled, input, output or both;
  - redstone modes: ignored, high or low;
  - auto-eject;
  - 3 upgrade slots: Speed, Energy, Range;
  - a comparator output for stored energy.

**Logistics**
- Item Pipe and Rapid Item Pipe: Pipez-style instant transfer, round robin, with extraction set per face with the
  wrench.

**Tools and QoL**
- Configuration Wrench: rotate machines, configure conduit faces, and sneak-click to dismantle.
- Multimeter: read any block's energy, or a cable network's size and capacity.
- Core Utilities Manual: an in-game guide with 17 chapters.
- JEI categories on 1.20.1 and 1.21.1.
- `config/core_utilities.properties`: every rate and cost is configurable.

**Original utilities**
- Vector (pushing) plates and effect plates, entity and projectile filters, three backpack sizes, the mob net and the
  floating block.

See [docs/feature-matrix.md](docs/feature-matrix.md) for how this compares with Mekanism, Thermal, Create, AE2,
Ender IO, Powah, Pipez and others, and for the roadmap.

## Building

```bash
./gradlew buildAll             # every workspace, then the 8 loader jars into releases/
cd 1.20.1 && ./gradlew build   # Gradle on Java 21 (Java 17 toolchain)
cd 1.21.1 && ./gradlew build   # Java 21
cd 26.3 && ./gradlew build     # Java 25 (the same for 26.2)
cd 26.3 && ./gradlew :common:test   # JUnit tests of the common module
```

The root build hands the 26.x workspaces `JAVA_HOME_25_X64` (or `-Pjava25Home=<jdk>`) and the others
`JAVA_HOME_21_X64` (or `-Pjava21Home=<jdk>`); without them each workspace uses the current `JAVA_HOME`.
Jars land in `<workspace>/<loader>/build/libs/core_utilities-<loader>-<minecraft>-<version>.jar`. Every
workspace's `gradle.properties` carries the same `version=`, and the root `VERSION` file matches it
(`./gradlew checkVersions`; set both with `./gradlew syncVersion -PmodVersion=x.y.z`). `python3 scripts/check_targets.py workspaces`
checks the targets, the shared version and the metadata ranges. Release notes for a version live in
`patchnotes/<version>.md` and `CHANGELOG.md`.

Add `-PwithJei` to a `runClient` task to test with JEI. On Forge and NeoForge, `-PquickPlay=host:port` makes
`runClient` join that server directly (on Fabric, pass `--args="--quickPlayMultiplayer host:port"`).

## Releasing

CI (`.github/workflows/ci.yml`) builds all 8 jars on every push to `main` that touches the build. Publishing to
Modrinth and CurseForge is `.github/workflows/publish.yml`, run by hand (`version`, `dry_run`, `update_listing`) or by
a `v*.*.*` tag; it never runs on a plain push. It refuses a second release of the mod on the same day (Africa/Tunis),
takes the jars from `releases/`, the GitHub Release or a build from source, and fails unless exactly the 8 jars are
found. The upload itself is `.github/scripts/publish.mjs` (Fabric jars declare Fabric API as required; the notes are
this version's section of `CHANGELOG.md`). It needs the secrets `MODRINTH_TOKEN`, `CURSEFORGE_TOKEN`,
`CURSEFORGE_API_KEY` and the variables `MODRINTH_ID`, `CURSEFORGE_ID`. `marketing/modrinth/` is made from the
CurseForge copy with `python3 tools/make_modrinth_md.py .`.

## Generated resources

Textures, models, blockstates, loot tables, recipes, tags and the English lang file are generated by
`scripts/assetgen/gen.py`. It writes the right data format for each Minecraft version:

```sh
python3 scripts/assetgen/gen.py            # all versions
python3 scripts/assetgen/gen.py 1.21.1     # one version
```

## In-game test

`scripts/servertest/run_server.py` boots a development dedicated server, builds every machine setup over RCON and
checks the results: smelting, crushing, alloying, pressing, quarrying, pipes, solar, geothermal, harvesting,
breaking and grinding.

```sh
cd 1.21.1
python3 ../scripts/servertest/run_server.py . neoforge "$JAVA_HOME" neoforge/run ../scripts/servertest/machines.txt test.log
```

For 26.x, use `machines_26.txt`, which has the renamed gamerules.

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for development and pull request expectations.

## Security

See [.github/SECURITY.md](.github/SECURITY.md) for responsible disclosure guidance.

## License

This project is licensed under the Apache License 2.0. See [LICENSE](LICENSE) and [NOTICE](NOTICE).
