#!/usr/bin/env python3
"""
Convert a 19x19 PNG with black pixels into a ShapeTemplates method.

Usage:
    python png_to_template.py <image.png> <method_name> [--threshold 128] [--small]

Output: Java method body for ShapeTemplates.java, printed to stdout.
Expects black (or dark) pixels on a light/transparent background.
Orders pixels via nearest-neighbor walk for smooth stroke ordering.
"""

import argparse
import sys
from pathlib import Path

try:
    from PIL import Image
except ImportError:
    print("Pillow required: pip install Pillow", file=sys.stderr)
    sys.exit(1)


def extract_dark_pixels(img: Image.Image, threshold: int) -> list[tuple[int, int]]:
    img = img.convert("RGBA")
    pixels = []
    for y in range(img.height):
        for x in range(img.width):
            r, g, b, a = img.getpixel((x, y))
            if a > 128 and (r + g + b) / 3 < threshold:
                pixels.append((x, y))
    return pixels


def nearest_neighbor_walk(pixels: list[tuple[int, int]]) -> list[tuple[int, int]]:
    if not pixels:
        return []
    remaining = list(pixels)
    # Start from top-left-most pixel
    remaining.sort(key=lambda p: (p[1], p[0]))
    ordered = [remaining.pop(0)]
    while remaining:
        lx, ly = ordered[-1]
        best_i = 0
        best_dist = float("inf")
        for i, (px, py) in enumerate(remaining):
            d = (px - lx) ** 2 + (py - ly) ** 2
            if d < best_dist:
                best_dist = d
                best_i = i
        ordered.append(remaining.pop(best_i))
    return ordered


def to_java(ordered: list[tuple[int, int]], method_name: str, width: int, height: int) -> str:
    cx = (width - 1) / 2.0
    cy = (height - 1) / 2.0
    scale = 100.0 / max(cx, cy) if max(cx, cy) > 0 else 1.0

    coords = []
    for x, y in ordered:
        sx = (x - cx) * scale
        sy = (y - cy) * scale
        coords.append(f"{sx:.1f},{sy:.1f}")

    PAIRS_PER_LINE = 4
    coord_lines = []
    for i in range(0, len(coords), PAIRS_PER_LINE):
        coord_lines.append("            " + ", ".join(coords[i:i + PAIRS_PER_LINE]))

    lines = []
    lines.append(f"    public static List<StrokePoint> {method_name}() {{")
    lines.append(f"        return pts(")
    lines.append(",\n".join(coord_lines))
    lines.append(f"        );")
    lines.append(f"    }}")
    return "\n".join(lines)


def main():
    parser = argparse.ArgumentParser(description="Convert 19x19 PNG to ShapeTemplates method")
    parser.add_argument("image", help="Path to PNG file")
    parser.add_argument("method_name", help="Java method name (e.g. spiralShape)")
    parser.add_argument("--threshold", type=int, default=64,
                        help="Brightness threshold (0-255). Pixels darker than this count as shape. Default: 128")
    parser.add_argument("--small", action="store_true",
                        help="Expect 11x11 input instead of 19x19")
    args = parser.parse_args()

    expected = 11 if args.small else 19
    img = Image.open(args.image)
    if img.width != expected or img.height != expected:
        print(f"Warning: image is {img.width}x{img.height}, expected {expected}x{expected}", file=sys.stderr)

    pixels = extract_dark_pixels(img, args.threshold)
    if not pixels:
        print("No dark pixels found!", file=sys.stderr)
        sys.exit(1)

    ordered = nearest_neighbor_walk(pixels)
    print(to_java(ordered, args.method_name, img.width, img.height))
    print(f"\n// {len(ordered)} points from {Path(args.image).name}", file=sys.stderr)


if __name__ == "__main__":
    main()
