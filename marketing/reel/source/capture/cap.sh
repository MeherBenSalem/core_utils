#!/bin/bash
# cap.sh <name> <real_seconds> [fps] -- grab the portrait display :3 (default 15 fps; the game runs at 0.1x)
FPS=${3:-15}
ffmpeg -loglevel error -y -f x11grab -video_size 1080x1920 -framerate $FPS -draw_mouse 0 -i :3 -t $2 -threads 2 -c:v libx264 -preset ultrafast -crf 8 -pix_fmt yuv444p /tmp/claude-0/-home-user-core-utils/a71de292-2ad5-58a0-9c67-02d8fa2cd0cc/scratchpad/reel/raw/$1.mkv
