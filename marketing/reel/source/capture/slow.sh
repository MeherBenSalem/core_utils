#!/bin/bash
# slow.sh <cmdfile> <cwd> <logfile> [display] -- relaunch a captured JVM command line on a clock slowed by $SLOW (default 0.1)
mapfile -t CMD < $1
cd $2
export DISPLAY=${4:-:3} LIBGL_ALWAYS_SOFTWARE=1 GALLIUM_DRIVER=llvmpipe MESA_GL_VERSION_OVERRIDE=4.5 MESA_GLSL_VERSION_OVERRIDE=450
export FAKETIME="+0 x${SLOW:-0.1}" FAKETIME_DONT_FAKE_MONOTONIC=0 LD_PRELOAD=/usr/lib/x86_64-linux-gnu/faketime/libfaketimeMT.so.1
exec "${CMD[@]}" > $3 2>&1
