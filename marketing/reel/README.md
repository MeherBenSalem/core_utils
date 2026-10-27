# Instagram Reel: "This machine just ate my backyard"

| File | Use |
| --- | --- |
| `core-utilities-reel.mp4` | The Reel: 1080×1920, 30 fps, 36 s, H.264 + AAC. Not tracked in git. Rebuild it with the steps below |
| `cover.jpg` | Cover image. The text sits inside the centre 3:4 area, so it survives the profile-grid crop |
| `caption.txt` | Post caption with hashtags |

## Structure

The edit puts the payoff first, gives each machine its own beat, burns in captions for sound-off viewers, and loops seamlessly into the start. Every cut, voice line, label and sound sits on a 120 BPM grid. The Reel is exactly 72 beats long, so the beat carries on across the loop.

| Time | Shot | Voiceover |
| --- | --- | --- |
| 0.0–2.5 | Quarry timelapse, already digging on frame one and accelerating | "This machine just ate my backyard!" |
| 2.5–5.0 | The factory row lights up on the drop (4.0 s) | "So I built a whole factory!" |
| 5.0–8.0 | Solar array, combustion generators, geothermal on its lava basin, 1 s each | "Solar power!" "Burn your coal!" "Even lava!" |
| 8.0–11.0 | An elite cable network snakes out to three machines, which light up | "Cables share the power. Zero lag!" |
| 11.0–14.0 | The Crusher's GUI with 8 speed upgrades | "Ores go in. Double comes out!" |
| 14.0–17.0 | Alloy Smelter, Compressor, Electric Furnace, 1 s each | "Steel, alloys, twice the speed!" |
| 17.0–19.5 | Crop Harvester harvesting and replanting (2.4×) | "Farms that replant themselves!" |
| 19.5–22.0 | Mob Grinder hitting a pen of husks (1.5×) | "And mobs get ground up!" |
| 22.0–25.0 | Sides panel: six clicks cycle faces through in, out and both | "Every side, configurable!" |
| 25.0–29.0 | The whole base, with Fabric, Forge and NeoForge pills | "Fabric, Forge and NeoForge!" |
| 29.0–32.5 | Title card over the lit factory | "It's called Core Utilities!" |
| 32.5–36.0 | Back on the quarry plot: the quarry goes down, ending on the Reel's first frame | "Now go feed the quarry!" |

## How it was made

- **Footage:** unedited captures of the 1.21.1 Fabric development client at 1080×1920 (a portrait virtual display) on a 1.21.1 Fabric dedicated development server, both running Core Utilities 2.0.0. Both game processes ran on a clock slowed 40× with libfaketime (10× for the hook), so the software renderer still produced a new frame every few milliseconds of game time, and the screen was grabbed often enough to keep every one of them (`source/capture/`). The scenes were built with commands (`build_scene.py`, `shots.sh`); every machine runs on real power from the generators and cells in the scene. Grinder targets are `NoAI` husks and a vindicator.
- **Smooth playback:** `source/prep.py` indexes which captured frames are new renders and their game time. `source/compose.py` asks for a game time per output frame and cross-fades the two renders around it, so no frame repeats or skips at any speed. Gameplay never plays slower than real time, the timelapse gets a 180° shutter blur, and a slow camera drift keeps still scenes moving. `source/loopmatch.py` found the frame of the loop-end take that matches the hook's first frame. `source/verify.py` checks the result for repeated frames.
- **Voice:** the NightBeam channel voice: Chatterbox (MIT) cloning the channel reference clip with the channel's fixed settings, four takes per line. `../voice/pick_takes.py` kept the take that transcribes exactly and reads with the most energy. Nothing is time-stretched; the edit is timed around the real line lengths (`plan.py` prints a fit check). Captions are timed per word from a Whisper transcript (`source/align.py`, `source/captions.py`).
- **Sound effects:** real recordings from Mixkit (free for commercial use in videos, no attribution required), listed by id in `source/sfx_pro.py` and downloaded by `source/fetch_sfx.sh`. Each recording's main hit is measured and placed on its frame: drill and dirt under the quarry, a power-up and trailer hit on the drop, ignition, lava, cable clicks, the crusher's grind, welding, a hammer, punches on the grinder hits, interface clicks on the Sides panel, a title impact.
- **Music:** an original 120 BPM bed in E minor, synthesized in `source/audio.py` and `source/synth.py`, with no hits or risers in it. The music is sidechained under the voice and the effects are ducked lightly under it in the final mix (`source/final.sh`), which applies the channel narration chain to the voice only.
- **Edit:** `source/plan.py` is the single source of truth (beat grid, shots, time maps, voice slots, cues). `source/overlay.html` is rendered frame by frame with Playwright (`source/render_overlay.cjs`) for captions, machine labels, the timelapse badge, the click cursor, loader pills and the title card.
- **Fonts:** Anton and Montserrat (SIL OFL 1.1, in `source/fonts/`).

## Rebuilding

The scripts use absolute scratch paths at the top of each file (`R = ...`); point them at your working folder first. Then, from that folder: capture with `capture/capstart.sh` and `capture/shots.sh`, index each take with `prep.py <take> 0.025`, generate the voice with the channel runner and `pick_takes.py`, and run `plan.py`, `build_vo.py`, `align.py`, `captions.py`, `audio.py`, `fetch_sfx.sh`, `sfx_pro.py`, `compose.py`, `render_overlay.cjs`, `final.sh` and `verify.py <video>` in that order.

## Posting checklist

- Upload the MP4 as a Reel and set `cover.jpg` as the cover. Keep the original audio; a trending track on top would bury the voiceover.
- Paste `caption.txt`. Put the Modrinth link in your bio before posting, since the caption points there.
- Add alt text, for example: "Minecraft gameplay of a tech mod: a quarry digging a pit, a row of glowing machines, cables, farms and a mob grinder."
- Pin a first comment asking which machine people would automate first.
- Cross-post the same file to TikTok and YouTube Shorts.
