#!/bin/bash
B=$(dirname $0)
kill $(cat $B/cap_client.pid) 2>/dev/null
python3 $B/rcon.py "save-all flush" "stop" >/dev/null 2>&1
while kill -0 $(cat $B/cap_server.pid) 2>/dev/null; do sleep 2; done
echo stopped
