# Marketing assets: 2.0 "The Technology Update"

Listing copy and artwork for CurseForge and Modrinth, and the Instagram Reel. Nothing here ships in the mod JARs.

## Listing

| File | Use |
| --- | --- |
| [`curseforge/description.md`](curseforge/description.md) | Full project description (Markdown; also works on Modrinth) |
| [`curseforge/summary.txt`](curseforge/summary.txt) | Short project summary (219 characters) |
| [`../CHANGELOG.md`](../CHANGELOG.md) | File changelog for 2.0.0 |
| [`reel/`](reel/) | The Instagram Reel's cover, caption and how it was made |

Image links in the description point at `raw.githubusercontent.com/MeherBenSalem/core_utils/main/marketing/images/...`, so they work once this folder is on `main`. To host on CurseForge instead, upload the images to the project gallery and replace each URL.

The description leads with the factory overview, then power, storage and cables, processing, the quarry, automation, machine control, item pipes, tools and the guide, progression, configuration, the original utilities, everything else, downloads and compatibility, the FAQ and a gallery.

## Adding a Minecraft version

The supported versions are listed in exactly two places:

1. `VERSIONS` at the top of [`source/render.cjs`](source/render.cjs). Add one line, for example `{ minecraft: '26.3', loaders: ['Fabric', 'NeoForge'] },`. The banner's loader pills and version line are built from it. Then rerun the script for `banner`.
2. The table under **Downloads & Compatibility** in `curseforge/description.md`. Add one row.

No other copy or artwork names a Minecraft version. A new release name or number goes in `RELEASE` in `render.cjs` and in the first lines of the description.

## Images

| File | Size | Use |
| --- | --- | --- |
| `images/banner.png` | 1800×600 | Description header: the factory, the mod logo, "2.0 · The Technology Update", loader pills and versions |
| `images/icon.png` | 512×512 | Project avatar: the mod logo in a glowing hex frame |
| `images/card-factory.png` | 1600×900 | The whole factory; machine, cable and energy-system counts |
| `images/card-power.png` | 1600×900 | Solar array, combustion and geothermal generators with their rates |
| `images/card-energy.png` | 1600×900 | An elite cable network and the three energy cells; capacities and throughputs |
| `images/card-processing.png` | 1600×900 | The Crusher's GUI and the machine row |
| `images/card-quarry.png` | 1600×900 | The quarry pit and its rapid item pipes |
| `images/card-automation.png` | 1600×900 | Crop Harvester, Mob Grinder, Block Breaker and Placer |
| `images/card-control.png` | 1600×900 | The Sides panel, redstone modes and upgrades |
| `images/card-logistics.png` | 1600×900 | Rapid item pipes into chests; pipe rates |
| `images/card-tools.png` | 1600×900 | The Core Utilities Manual and JEI |
| `images/card-materials.png` | 1600×900 | Every progression item, drawn from the mod's own 16×16 textures |
| `images/card-config.png` | 1600×900 | An excerpt of `config/core_utilities.properties` |
| `images/header-*.png` | 1400×120, transparent | 16 section headings for the description |
| `images/divider.png` | 1400×40, transparent | Section divider |
| `images/gifs/quarry.gif` | 560×315, 7.3 s | Quarry timelapse: placed, then digging the pit layer by layer |
| `images/gifs/cables.gif` | 560×498, 4.6 s | An elite cable network laid from two generators to three machines |
| `images/screenshots/*.jpg` | 1920×1080 | Gallery uploads and card art |

### How the screenshots were captured

Every screenshot is an unedited capture of the **1.21.1 Fabric development client** (`./gradlew :fabric:runClient`, Core Utilities 2.0.0, default configuration, JEI 19.21) on a dedicated development server, with software rendering, GUI scale 3 and the HUD hidden (F1) outside GUI shots. The world is a normal generated world ("capture", seed `core-utilities-reel`); the factory, farm, mob pen, quarry plot and cable demo were built with commands. Machines show real running state: power comes from the generators and cells in the scene.

- **Mobs** in the grinder pen are `NoAI` husks and a vindicator, so they stand still and do not burn in daylight.
- **GIFs** come from the Reel's captures (see [`reel/README.md`](reel/README.md)): the game ran on a slowed clock, and frames were picked by game time, so motion is even.

## Regenerating the artwork

The cards, banner, icon and headers are HTML rendered with Playwright from the screenshots, the mod's textures and the bundled fonts:

```bash
NODE_PATH="$(npm root -g)" node marketing/source/render.cjs            # everything
NODE_PATH="$(npm root -g)" node marketing/source/render.cjs banner icon  # selected assets
```

- `source/backdrop.jpg` is the blurred card background, a 1280×720 copy of `screenshots/factory.jpg`.
- `source/logo.png` is the mod logo (`*/common/src/main/resources/core_utilities.png`).
- `source/fonts/` holds Anton and Montserrat with their SIL Open Font License 1.1 texts. The script uses only these files and the textures under `1.21.1/common/src/main/resources`.

To refresh screenshots, launch a client (see the root `README.md`), replace the files in `images/screenshots/` with the same names, and rerun the script. Card crops are pixel regions of the 1920×1080 captures (`region()` in `render.cjs`), so new captures may need their crops adjusted.

## Wording

Every number in the copy and on the cards is a 2.0.0 default from `CoreUtilitiesConfig`, a recipe in `scripts/assetgen/content.py` or `MachineRecipes`, or the code. Update the copy when those defaults change.
