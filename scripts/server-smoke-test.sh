#!/usr/bin/env bash
# Boots a dev server for one Minecraft version and loader and proves Core Utilities works at runtime, not only
# compiles (NightBeam convention; same pattern as NoMore63AE2's scripts/server-smoke-test.sh). A datapack, with no
# player online, on a superflat world:
#   builds the machine setups of scripts/servertest/machines.txt (generators, cells, cables, item pipes, electric
#   furnace, crusher, alloy smelter, compressor, quarry, block breaker, crop harvester, mob grinder, solar panel,
#   geothermal generator), lets them run for 900 ticks and checks what each one produced or stored,
#   sets every Core Utilities block (plates, filters, machines, cables...) with a pig on it,
#   summons every Core Utilities item as an item entity and checks each one is still there a few ticks later
#   (an unknown or broken item is dropped as air),
#   then runs /save-all and a guarded /reload and stops.
# The mod has no commands, no entities, no world generation and no mixins of its own.
# Fails on a crash report, a mixin error, an ERROR/WARN/exception line mentioning the mod, a failed command or
# machine check, or when the RAN/OK markers never show up.
#   usage: scripts/server-smoke-test.sh <1.20.1|1.21.1|26.2|26.3> <fabric|forge|neoforge>
set -u
VERSION="$1"; LOADER="$2"
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT/$VERSION"
MOD=core_utilities
NS=cu_smoke
RUN="$LOADER/runs/server"
rm -rf "$RUN/world" "$RUN/crash-reports" "$RUN/logs"
PACK="$RUN/world/datapacks/$NS"
mkdir -p "$PACK"
echo "eula=true" > "$RUN/eula.txt"
cat > "$RUN/server.properties" <<PROPS
online-mode=false
function-permission-level=4
spawn-protection=0
view-distance=6
simulation-distance=6
level-type=minecraft\:flat
level-seed=20261123
initial-enabled-packs=vanilla,file/$NS
PROPS
if [ "$LOADER" = forge ]; then
  # In the dev environment Forge's LAN advertiser loads a client-only class and can stop the server.
  mkdir -p "$RUN/defaultconfigs"
  printf '[server]\n\tadvertiseDedicatedServerToLan = false\n' > "$RUN/defaultconfigs/forge-server.toml"
fi

# pack.mcmeta as valid JSON, with the same format (range) as the mod's own pack (26.x uses min_format/max_format).
python3 - common/src/main/resources/pack.mcmeta "$PACK/pack.mcmeta" <<'PY'
import json, sys
src = json.load(open(sys.argv[1]))["pack"]
pack = {"description": "Core Utilities smoke test"}
for key in ("pack_format", "min_format", "max_format", "supported_formats"):
    if key in src:
        pack[key] = src[key]
json.dump({"pack": pack}, open(sys.argv[2], "w"))
PY
cat "$PACK/pack.mcmeta"; echo

# Every block and item, read from the mod's own assets.
ASSETS=common/src/main/resources/assets/$MOD
BLOCKS="$(ls "$ASSETS/blockstates" | sed 's/\.json$//' | sort)"
# 26.x has an item model definition (assets/<mod>/items) for every item; earlier versions an item model for every item.
if [ -d "$ASSETS/items" ]; then ITEM_DIR="$ASSETS/items"; else ITEM_DIR="$ASSETS/models/item"; fi
ITEMS="$(ls "$ITEM_DIR" | sed 's/\.json$//' | sort)"
BLOCK_COUNT=$(echo "$BLOCKS" | grep -c .); ITEM_COUNT=$(echo "$ITEMS" | grep -c .)
echo "Blocks ($BLOCK_COUNT), items ($ITEM_COUNT)"
[ "$BLOCK_COUNT" -gt 0 ] && [ "$ITEM_COUNT" -gt 0 ] || { echo "::error::could not read the mod's blocks or items"; exit 1; }

if [ "$VERSION" = 1.20.1 ]; then COUNT='Count:1b'; else COUNT='count:1'; fi
case "$VERSION" in
  26.*) DAYLIGHT=advance_time; WEATHER=advance_weather; MOBS=spawn_mobs ;;
  *) DAYLIGHT=doDaylightCycle; WEATHER=doWeatherCycle; MOBS=doMobSpawning ;;
esac

FN="$PACK/data/$NS"
for dir in function functions; do
  mkdir -p "$FN/$dir" "$PACK/data/minecraft/tags/$dir"
  echo "{\"values\":[\"$NS:start\"]}" > "$PACK/data/minecraft/tags/$dir/load.json"
  check() { # marker command...
    local marker="$1"; shift
    echo "scoreboard players set #r cus 0"
    echo "execute store success score #r cus run $*"
    echo "execute if score #r cus matches 1 run say CU_OK $marker"
    echo "execute unless score #r cus matches 1 run say CU_FAILED $marker"
  }
  {
    echo 'scoreboard objectives add cus dummy'
    # /reload in finish.mcfunction runs this load function again; only the first load starts the test.
    echo 'execute if score #started cus matches 1 run say CU_SMOKE_RELOADED'
    echo "execute unless score #started cus matches 1 run function $NS:begin"
  } > "$FN/$dir/start.mcfunction"
  {
    echo 'scoreboard players set #started cus 1'
    echo "gamerule $DAYLIGHT false"
    echo "gamerule $WEATHER false"
    echo "gamerule $MOBS false"
    echo 'weather clear'
    echo 'time set noon'
    echo 'forceload add -16 -16 64 64'
    echo 'say CU_SMOKE_LOADED'
    echo "schedule function $NS:run 100t"
  } > "$FN/$dir/begin.mcfunction"

  # The machine setups of scripts/servertest/machines.txt (superflat: grass at y=-61, machines at y=-60).
  {
    echo 'fill -2 -60 -6 40 -55 12 minecraft:air'
    # 1. Combustion generator -> basic cables -> electric furnace + crusher
    check place_combustion_generator setblock 0 -60 0 $MOD:combustion_generator
    echo "setblock 1 -60 0 $MOD:cable"
    echo "setblock 2 -60 0 $MOD:cable"
    echo "setblock 3 -60 0 $MOD:cable"
    check place_electric_furnace setblock 4 -60 0 $MOD:electric_furnace
    check place_crusher setblock 2 -60 1 $MOD:crusher
    check fuel_generator item replace block 0 -60 0 container.0 with minecraft:coal 16
    check feed_furnace item replace block 4 -60 0 container.0 with minecraft:raw_iron 4
    check feed_crusher item replace block 2 -60 1 container.0 with minecraft:iron_ore 4
    # 2. Creative cell -> quarry (front of the cell faces north)
    check place_creative_cell setblock 10 -60 0 $MOD:energy_cell_creative
    check place_quarry setblock 10 -60 -1 $MOD:quarry
    # 3. Item pipe: chest -> extracting pipe -> pipe -> chest
    echo 'setblock 20 -60 0 minecraft:chest'
    echo 'setblock 23 -60 0 minecraft:chest'
    check place_extracting_pipe setblock 21 -60 0 "$MOD:item_pipe[west=extract,east=conduit]"
    check place_pipe setblock 22 -60 0 "$MOD:item_pipe[west=conduit,east=endpoint]"
    echo 'item replace block 20 -60 0 container.0 with minecraft:cobblestone 20'
    # 4. Solar panel on top of a basic cell (cell top is an input)
    check place_basic_cell setblock 30 -60 0 $MOD:energy_cell_basic
    check place_solar_panel setblock 30 -59 0 $MOD:solar_panel_tier_41
    # 5. Geothermal with lava
    check place_geothermal setblock 34 -60 0 $MOD:geothermal_generator
    echo 'setblock 35 -60 0 minecraft:lava'
    echo 'setblock 33 -60 0 minecraft:lava'
    # 6. Alloy smelter and compressor fed by a creative cell through an elite cable, and a block breaker
    check place_creative_cell_east setblock 0 -60 4 "$MOD:energy_cell_creative[facing=east]"
    echo "setblock 1 -60 4 $MOD:cable_elite"
    check place_alloy_smelter setblock 2 -60 4 $MOD:alloy_smelter
    check place_compressor setblock 1 -60 5 $MOD:compressor
    check place_block_breaker setblock 1 -60 3 "$MOD:block_breaker[facing=north]"
    echo 'setblock 1 -60 2 minecraft:stone'
    check feed_alloy_iron item replace block 2 -60 4 container.0 with minecraft:iron_ingot 2
    check feed_alloy_coal_dust item replace block 2 -60 4 container.1 with $MOD:coal_dust 4
    check feed_compressor item replace block 1 -60 5 container.0 with minecraft:iron_ingot 2
    # 7. Crop harvester next to grown wheat
    echo 'setblock 14 -61 4 minecraft:farmland'
    echo 'setblock 14 -60 4 minecraft:wheat[age=7]'
    check place_crop_harvester setblock 13 -60 4 $MOD:crop_harvester
    echo "setblock 13 -60 5 $MOD:energy_cell_creative[facing=north]"
    # 8. Mob grinder (facing south) with a zombie in front, powered from a creative cell behind it
    echo "setblock 20 -60 5 $MOD:energy_cell_creative[facing=south]"
    check place_mob_grinder setblock 20 -60 6 "$MOD:mob_grinder[facing=south]"
    echo 'summon minecraft:zombie 20 -60 9 {NoAI:1b,PersistenceRequired:1b,Tags:["cu_smoke","cu_grind"]}'

    # Every block on its own spot two blocks apart, each with a pig on it (plates and filters act on entities).
    j=0
    for b in $BLOCKS; do
      x=$((j % 12 * 2)); z=$((20 + j / 12 * 2)); j=$((j + 1))
      check "setblock_$b" setblock "$x" -60 "$z" "$MOD:$b"
      echo "summon minecraft:pig $x -60 $z {PersistenceRequired:1b,Tags:[\"cu_smoke\"]}"
    done
    # Every item as a floating item entity north-east of the blocks.
    j=0
    for it in $ITEMS; do
      x=$((30 + j % 20)); z=$((20 + j / 20)); j=$((j + 1))
      check "summon_$it" summon minecraft:item "$x" -50 "$z" "{Item:{id:\"$MOD:$it\",$COUNT},Tags:[\"cu_smoke\",\"cu_i_$it\"],NoGravity:1b,PickupDelay:32767,Age:-32768}"
    done
    echo 'say CU_SMOKE_RAN'
    echo "schedule function $NS:items 10t"
  } > "$FN/$dir/run.mcfunction"

  # An item that does not exist, or whose stack fails to load, leaves an empty item entity that is removed at once.
  {
    for it in $ITEMS; do
      check "item_$it" execute if entity "@e[type=minecraft:item,tag=cu_i_$it]"
    done
    echo "schedule function $NS:machines 900t"
  } > "$FN/$dir/items.mcfunction"

  # What each machine setup produced after 900 ticks (the #expect lines of scripts/servertest/machines.txt).
  {
    has() { echo "execute if data block $1 Items[{id:\"$2\"}]"; }
    check electric_furnace_smelted "$(has '4 -60 0' minecraft:iron_ingot)"
    check crusher_crushed "$(has '2 -60 1' $MOD:iron_dust)"
    echo "scoreboard players set #q cus 0"
    for b in minecraft:dirt minecraft:grass_block minecraft:bedrock; do
      echo "execute store success score #r cus run $(has '10 -60 -1' $b)"
      echo "execute if score #r cus matches 1 run scoreboard players set #q cus 1"
    done
    echo "execute if score #q cus matches 1 run say CU_OK quarry_mined"
    echo "execute unless score #q cus matches 1 run say CU_FAILED quarry_mined"
    check item_pipe_moved "$(has '23 -60 0' minecraft:cobblestone)"
    for spot in "solar_panel_charged_cell:30 -60 0" "geothermal_generated:34 -60 0"; do
      echo "scoreboard players set #e cus 0"
      echo "execute store result score #e cus run data get block ${spot#*:} Energy"
      echo "execute if score #e cus matches 1.. run say CU_OK ${spot%%:*}"
      echo "execute unless score #e cus matches 1.. run say CU_FAILED ${spot%%:*}"
    done
    check alloy_smelter_made_steel "$(has '2 -60 4' $MOD:steel_ingot)"
    check compressor_made_plate "$(has '1 -60 5' $MOD:iron_plate)"
    check block_breaker_broke_stone "$(has '1 -60 3' minecraft:cobblestone)"
    check crop_harvester_harvested "$(has '13 -60 4' minecraft:wheat)"
    check crop_harvester_replanted execute if block 14 -60 4 "minecraft:wheat[age=0]"
    check mob_grinder_killed execute unless entity "@e[type=minecraft:zombie,tag=cu_grind]"
    check save save-all flush
    echo 'reload'
    echo "schedule function $NS:end 100t"
  } > "$FN/$dir/machines.mcfunction"

  {
    echo 'kill @e[tag=cu_smoke]'
    echo 'say CU_SMOKE_OK'
    echo 'stop'
  } > "$FN/$dir/end.mcfunction"
done
EXPECTED_OK=$(cat "$FN/function/"*.mcfunction | grep -c "say CU_OK")

LOG="$ROOT/server-smoke-$VERSION-$LOADER.log"
./gradlew ":$LOADER:runServer" --stacktrace -Porg.gradle.java.installations.fromEnv=JAVA_HOME_17_X64,JAVA_HOME_21_X64,JAVA_HOME_25_X64 < /dev/null > "$LOG" 2>&1 &
PID=$!
CRASH='This crash report has been saved|Encountered an unexpected exception|Failed to start the minecraft server|Mixin apply.*failed|InvalidInjectionException|MixinApplyError|MixinTransformerError|Mixin.*(error|failed)'
result=timeout
for _ in $(seq 1 240); do
  sleep 5
  if grep -q "CU_SMOKE_OK" "$LOG"; then result=ok; break; fi
  if grep -q -E "$CRASH" "$LOG"; then result=crash; break; fi
  # A smoke function that does not parse never runs: no need to wait for the timeout.
  if grep -q "Failed to load function" "$LOG"; then result=bad_function; break; fi
  if ! kill -0 $PID 2>/dev/null; then result=exited; break; fi
done
sleep 10
pkill -P $PID 2>/dev/null; kill $PID 2>/dev/null; wait $PID 2>/dev/null

echo "===== markers"
grep -n -E "CU_SMOKE|CU_FAILED|Found new data pack" "$LOG" | head -80
for f in "$RUN"/crash-reports/*.txt; do [ -f "$f" ] && { echo "=== $f"; head -100 "$f"; }; done
echo "===== errors and warnings"
grep -n -E "ERROR|WARN|Exception|Caused by|[Mm]ixin.*(fail|error)|Failed to|Couldn't|Unknown" "$LOG" | grep -v -E "Download|Task :" | head -80
echo "Result: $result"

fail=0
[ "$result" = ok ] || { echo "::error::server did not reach CU_SMOKE_OK ($result)"; fail=1; }
grep -q -E "$CRASH" "$LOG" && { echo "::error::crash or mixin error"; fail=1; }
grep -q "CU_SMOKE_RAN" "$LOG" || { echo "::error::the smoke function never ran"; fail=1; }
grep -q "CU_SMOKE_RELOADED" "$LOG" || { echo "::error::/reload never finished"; fail=1; }
if grep -n "CU_FAILED" "$LOG"; then echo "::error::a command or machine check failed"; fail=1; fi
oks=$(grep -c "CU_OK " "$LOG")
echo "Commands OK: $oks of $EXPECTED_OK"
[ "$oks" -ge "$EXPECTED_OK" ] || { echo "::error::not every command check passed"; fail=1; }
# (The checkout path is left out: it holds the repository name. NeoForge's deprecation note on the mods.toml
# logoFile key is metadata, not a load error.)
# Errors, warnings and exceptions that mention the mod (its id, its name, its package or the smoke pack).
if grep -n -E "(ERROR|WARN|Exception|Caused by|Failed|Couldn't|Unknown|Errors in)" "$LOG" | sed "s|$ROOT|<checkout>|g" \
    | grep -v -E "JAVA_TOOL_OPTIONS|deprecated \`logoFile\`|Reference map '[a-z_.]*refmap.json' .* could not be read" \
    | grep -i -E "$MOD|Core Utilities|com\.nightbeam\.core_utilities|$NS"; then
  echo "::error::error or warning mentioning the mod"; fail=1
fi
exit $fail
