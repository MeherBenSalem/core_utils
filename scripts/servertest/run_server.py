#!/usr/bin/env python3
"""Boots a development dedicated server through Gradle, runs a command script over RCON and checks the output.

usage: run_server.py <version_dir> <loader> <java_home> <run_dir> <commands_file|-> <log_out>

Command file lines: a console command, '#wait N' (sleep N seconds), '#expect REGEX' (the previous command's output
must match) or '# comment'. Exit code 0 only if the server started, every expectation matched and no
exception from core_utilities was logged.
"""
import os, subprocess, sys, time, threading, re, socket, struct

def rcon(cmd, host='127.0.0.1', port=25575, pw='test'):
    s = socket.create_connection((host, port), timeout=30)
    def send(i, t, body):
        b = body.encode() + b'\x00\x00'
        s.sendall(struct.pack('<iii', len(b) + 8, i, t) + b)
    def recv():
        hdr = b''
        while len(hdr) < 4:
            hdr += s.recv(4 - len(hdr))
        n = struct.unpack('<i', hdr)[0]
        data = b''
        while len(data) < n:
            data += s.recv(n - len(data))
        i, t = struct.unpack('<ii', data[:8])
        return i, data[8:-2].decode(errors='replace')
    send(1, 3, pw); recv()
    send(2, 2, cmd); _, body = recv()
    s.close()
    return body

proj, loader, jh, rundir, cmdfile, logout = sys.argv[1:7]
os.makedirs(rundir, exist_ok=True)
with open(os.path.join(rundir, 'eula.txt'), 'w') as f:
    f.write('eula=true\n')
props = os.path.join(rundir, 'server.properties')
if not os.path.exists(props):
    with open(props, 'w') as f:
        f.write('online-mode=false\nlevel-type=minecraft\\:flat\nspawn-protection=0\nmotd=core_utilities test\n'
                'enable-rcon=true\nrcon.password=test\nrcon.port=25575\nbroadcast-rcon-to-ops=false\nsync-chunk-writes=false\nview-distance=6\nsimulation-distance=6\ngamemode=creative\n')
env = dict(os.environ, JAVA_HOME=jh)
p = subprocess.Popen(['./gradlew', f':{loader}:runServer', '--no-daemon', '--args=nogui'] if loader == 'fabric' else ['./gradlew', f':{loader}:runServer', '--no-daemon'],
                     cwd=proj, env=env, stdin=subprocess.PIPE, stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True, bufsize=1)
lines = []
done = threading.Event()

def reader():
    for line in p.stdout:
        lines.append(line)
        if re.search(r'Done \(\d', line):
            done.set()
    done.set()

t = threading.Thread(target=reader, daemon=True)
t.start()
ok = done.wait(1500)
started = any(re.search(r'Done \(\d', l) for l in lines)
cmds = [] if cmdfile == '-' else open(cmdfile).read().splitlines()
results = []
last_cmd, last_output = '', ''
if started:
    time.sleep(3)
    for c in cmds:
        if not c.strip():
            continue
        if c.startswith('#wait'):
            time.sleep(float(c.split()[1]))
            continue
        if c.startswith('#expect'):
            pattern = c[len('#expect'):].strip()
            ok_line = bool(re.search(pattern, last_output))
            results.append((last_cmd, pattern, ok_line))
            lines.append(f'=== EXPECT {pattern}: {"OK" if ok_line else "FAIL"}\n')
            continue
        if c.startswith('#'):
            continue
        try:
            out = rcon(c)
        except Exception as e:
            out = f'RCON ERROR {e}'
        lines.append(f'>>> {c}\n<<< {out}\n')
        last_cmd, last_output = c, out
        time.sleep(0.2)
    time.sleep(2)
    try:
        rcon('stop')
    except Exception:
        pass
try:
    p.stdin.close()
    p.wait(300)
except subprocess.TimeoutExpired:
    p.kill()
t.join(5)
with open(logout, 'w') as f:
    f.writelines(lines)
errors = [l for l in lines if re.search(r'(Exception|ERROR).*', l) and ('core_utilities' in l or 'nightbeam' in l)]
traces = [l for l in lines if 'at com.nightbeam' in l]
failed = [r for r in results if not r[2]]
print('STARTED' if started else 'NOT STARTED', 'exit', p.returncode, f'expectations {len(results) - len(failed)}/{len(results)} passed', f'mod errors {len(errors) + len(traces)}')
for cmd, pattern, ok in failed:
    print('  FAILED:', cmd, '->', pattern)
sys.exit(0 if started and not failed and not errors and not traces else 1)
