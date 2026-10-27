#!/bin/bash
# capstart.sh -- start the slowed capture server and client (pids in cap_server.pid / cap_client.pid)
B=$(dirname $0)
: > $B/slowserver.log
nohup $B/slow.sh $B/server1211f_cmd.txt /home/user/core_utils/1.21.1/fabric/runs/server $B/slowserver.log > /dev/null 2>&1 &
echo $! > $B/cap_server.pid
until grep -q "Done (" $B/slowserver.log; do sleep 3; done
nohup $B/slow.sh $B/client_capture_cmd.txt /home/user/core_utils/1.21.1/fabric/runs/client $B/slowclient.log :3 > /dev/null 2>&1 &
echo $! > $B/cap_client.pid
until grep -q "Engineer joined" $B/slowserver.log; do sleep 3; done
echo up
