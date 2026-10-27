"""Mod icon: an isometric Quarry block with an energy badge, rendered from the generated textures."""
import os
import sys

from PIL import Image, ImageDraw


def iso_cube(top, left, right, size=64):
    """Projects three 32x32 faces onto a 2:1 isometric cube (pixel-exact affine mapping)."""
    out = Image.new('RGBA', (size, size), (0, 0, 0, 0))
    px = out.load()
    s = 32
    w = size * 0.375  # half width of the cube in output pixels
    cx, cy = size / 2, size * 0.06
    for y in range(size):
        for x in range(size):
            # invert the projection for each face
            dx, dy = (x + 0.5 - cx) / w, (y + 0.5 - cy) / (w / 2)
            # top face: u along +x/down-right, v along -x/down-left
            u, v = (dx + dy) / 2, (dy - dx) / 2
            if 0 <= u < 1 and 0 <= v < 1:
                px[x, y] = top.getpixel((int(u * s), int(v * s)))
                continue
            # left face (front-left), columns from the left corner, height down
            fx = (x + 0.5 - (cx - w)) / w
            fy = (y + 0.5 - (cy + w / 2) - (fx * w) / 2) / w + 0.5 * 0 - 0
            if 0 <= fx < 1 and 0 <= fy < 1:
                c = left.getpixel((int(fx * s), int(fy * s)))
                px[x, y] = tuple(int(v * 0.82) for v in c[:3]) + (c[3],)
                continue
            gx = (x + 0.5 - cx) / w
            gy = (y + 0.5 - (cy + w) + (gx * w) / 2) / w
            if 0 <= gx < 1 and 0 <= gy < 1:
                c = right.getpixel((int(gx * s), int(gy * s)))
                px[x, y] = tuple(int(v * 0.64) for v in c[:3]) + (c[3],)
    return out


def main(tex_dir, out_paths):
    t = lambda n: Image.open(os.path.join(tex_dir, n)).convert('RGBA').crop((0, 0, 32, 32))
    cube = iso_cube(t('quarry_top.png'), t('quarry_front_on.png'), t('quarry_side.png'))
    icon = Image.new('RGBA', (64, 64), (0, 0, 0, 0))
    icon.paste(cube, (0, 2), cube)
    d = ImageDraw.Draw(icon)
    # energy badge, bottom right
    d.ellipse((40, 40, 62, 62), fill=(24, 27, 33, 255), outline=(255, 200, 60, 255))
    bolt = [(53, 43), (46, 52), (51, 52), (48, 60), (56, 50), (51, 50)]
    d.polygon(bolt, fill=(255, 210, 70, 255))
    big = icon.resize((256, 256), Image.NEAREST)
    for p in out_paths:
        big.save(p)


if __name__ == '__main__':
    main(sys.argv[1], sys.argv[2:])
