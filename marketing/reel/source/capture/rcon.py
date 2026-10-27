#!/usr/bin/env python3
import socket, struct, sys, time
def rcon(cmd, port=25575, pw='test'):
    s = socket.create_connection(('127.0.0.1', port), timeout=30)
    def send(i, t, body):
        b = body.encode() + b'\x00\x00'; s.sendall(struct.pack('<iii', len(b) + 8, i, t) + b)
    def recv():
        h = b''
        while len(h) < 4: h += s.recv(4 - len(h))
        n = struct.unpack('<i', h)[0]; d = b''
        while len(d) < n: d += s.recv(n - len(d))
        return d[8:-2].decode(errors='replace')
    send(1, 3, pw); recv(); send(2, 2, cmd); out = recv(); s.close(); return out
if __name__ == '__main__':
    for c in sys.argv[1:]:
        print(rcon(c)); time.sleep(0.15)
