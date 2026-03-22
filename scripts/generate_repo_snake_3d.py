from __future__ import annotations

import datetime as dt
import math
import subprocess
from pathlib import Path
from typing import Dict, List, Tuple


ROOT = Path(__file__).resolve().parents[1]
DIST = ROOT / "dist"
DIST.mkdir(parents=True, exist_ok=True)

WEEKS = 53
ROWS = 7  # Sunday..Saturday
TILE_W = 18
TILE_H = 10
DEPTH = 6
MARGIN_X = 34
MARGIN_Y = 34


def run_git_dates() -> List[dt.date]:
    """
    Read commit dates from the current repository only.
    We stay on the checked-out branch history. `fetch-depth: 0`
    in the workflow ensures the full history is available.
    """
    cmd = [
        "git",
        "log",
        "--date=short",
        "--pretty=format:%ad",
        "--first-parent",
        "HEAD",
    ]
    out = subprocess.check_output(cmd, cwd=ROOT, text=True)
    dates: List[dt.date] = []
    for line in out.splitlines():
        line = line.strip()
        if not line:
            continue
        dates.append(dt.date.fromisoformat(line))
    return dates


def sunday_index(d: dt.date) -> int:
    # Python Monday=0..Sunday=6 -> Sunday=0..Saturday=6
    return (d.weekday() + 1) % 7


def range_start(today: dt.date) -> dt.date:
    # Start at the Sunday 52 weeks ago, so we always render 53 columns.
    start_of_this_week = today - dt.timedelta(days=sunday_index(today))
    return start_of_this_week - dt.timedelta(weeks=WEEKS - 1)


def project(col: int, row: int) -> Tuple[float, float]:
    """
    Isometric-ish projection for a faux-3D README-safe SVG.
    Returns the center of the top face.
    """
    x = MARGIN_X + (col - row) * (TILE_W / 2)
    y = MARGIN_Y + (col + row) * (TILE_H / 2)
    return x, y


def scale_color(n: int, theme: Dict[str, List[str]]) -> str:
    levels = theme["levels"]
    if n <= 0:
        return levels[0]
    if n == 1:
        return levels[1]
    if n <= 3:
        return levels[2]
    if n <= 6:
        return levels[3]
    return levels[4]


def hex_to_rgb(h: str) -> Tuple[int, int, int]:
    h = h.lstrip("#")
    return int(h[0:2], 16), int(h[2:4], 16), int(h[4:6], 16)


def rgb_to_hex(rgb: Tuple[int, int, int]) -> str:
    return "#{:02x}{:02x}{:02x}".format(*rgb)


def adjust(hex_color: str, factor: float) -> str:
    r, g, b = hex_to_rgb(hex_color)
    r = max(0, min(255, int(r * factor)))
    g = max(0, min(255, int(g * factor)))
    b = max(0, min(255, int(b * factor)))
    return rgb_to_hex((r, g, b))


def cube_faces(x: float, y: float, color: str) -> str:
    """
    Draw a small isometric block with top, left, right faces.
    """
    top = [
        (x, y - TILE_H / 2),
        (x + TILE_W / 2, y),
        (x, y + TILE_H / 2),
        (x - TILE_W / 2, y),
    ]
    left = [
        (x - TILE_W / 2, y),
        (x, y + TILE_H / 2),
        (x, y + TILE_H / 2 + DEPTH),
        (x - TILE_W / 2, y + DEPTH),
    ]
    right = [
        (x + TILE_W / 2, y),
        (x, y + TILE_H / 2),
        (x, y + TILE_H / 2 + DEPTH),
        (x + TILE_W / 2, y + DEPTH),
    ]

    top_fill = color
    left_fill = adjust(color, 0.82)
    right_fill = adjust(color, 0.64)
    stroke = adjust(color, 0.52)

    def poly(points: List[Tuple[float, float]], fill: str) -> str:
        pts = " ".join(f"{px:.1f},{py:.1f}" for px, py in points)
        return (
            f'<polygon points="{pts}" fill="{fill}" '
            f'stroke="{stroke}" stroke-width="0.6" />'
        )

    return poly(left, left_fill) + poly(right, right_fill) + poly(top, top_fill)


def build_counts(dates: List[dt.date], start: dt.date, end: dt.date) -> Dict[dt.date, int]:
    counts: Dict[dt.date, int] = {}
    for d in dates:
        if start <= d <= end:
            counts[d] = counts.get(d, 0) + 1
    return counts


def build_cells(counts: Dict[dt.date, int], start: dt.date) -> List[Dict]:
    cells: List[Dict] = []
    for col in range(WEEKS):
        for row in range(ROWS):
            day = start + dt.timedelta(days=col * 7 + row)
            x, y = project(col, row)
            cells.append(
                {
                    "date": day,
                    "col": col,
                    "row": row,
                    "count": counts.get(day, 0),
                    "x": x,
                    "y": y,
                }
            )
    return cells


def snake_order(cells: List[Dict]) -> List[Dict]:
    """
    Smooth serpentine path through the grid.
    We only keep active cells; if too few exist, fall back to all cells.
    """
    ordered: List[Dict] = []
    by_col: Dict[int, List[Dict]] = {}
    for c in cells:
        by_col.setdefault(c["col"], []).append(c)

    for col in range(WEEKS):
        col_cells = sorted(by_col[col], key=lambda c: c["row"])
        if col % 2 == 1:
            col_cells.reverse()
        ordered.extend(col_cells)

    active = [c for c in ordered if c["count"] > 0]
    return active if len(active) >= 2 else ordered


def path_d(points: List[Tuple[float, float]]) -> str:
    if not points:
        return ""
    chunks = [f"M {points[0][0]:.1f} {points[0][1]:.1f}"]
    for x, y in points[1:]:
        chunks.append(f"L {x:.1f} {y:.1f}")
    return " ".join(chunks)


def render_svg(theme: Dict[str, List[str]], out_name: str, title: str, subtitle: str, cells: List[Dict]) -> None:
    bg = theme["bg"]
    text = theme["text"]
    muted = theme["muted"]
    glow = theme["glow"]
    snake = theme["snake"]

    ordered = snake_order(cells)
    points = [(c["x"], c["y"] - 1.5) for c in ordered]
    d = path_d(points)

    xs = [c["x"] for c in cells]
    ys = [c["y"] for c in cells]
    width = int(max(xs) - min(xs) + 120)
    height = int(max(ys) - min(ys) + 120)

    offset_x = 60 - min(xs)
    offset_y = 52 - min(ys)

    def shift_path(path_str: str) -> str:
        if not path_str:
            return ""
        # Cheap coordinate shift for the "M x y L x y ..." format we generate.
        parts = path_str.split()
        out = []
        i = 0
        while i < len(parts):
            token = parts[i]
            if token in {"M", "L"}:
                x = float(parts[i + 1]) + offset_x
                y = float(parts[i + 2]) + offset_y
                out.extend([token, f"{x:.1f}", f"{y:.1f}"])
                i += 3
            else:
                out.append(token)
                i += 1
        return " ".join(out)

    shifted_d = shift_path(d)

    cubes = []
    labels = []
    for c in cells:
        x = c["x"] + offset_x
        y = c["y"] + offset_y
        color = scale_color(c["count"], theme)
        cubes.append(cube_faces(x, y, color))

    # Body segments.
    body = []
    segment_sizes = [7.2, 6.4, 5.7, 5.0, 4.4, 3.8]
    segment_delays = [0.0, -0.35, -0.70, -1.05, -1.40, -1.75]
    duration = "14s"

    for r, delay in zip(segment_sizes, segment_delays):
        body.append(
            f"""
            <g filter="url(#glow)">
              <ellipse cx="0" cy="0" rx="{r}" ry="{max(2.8, r * 0.62):.2f}"
                       fill="{snake}" opacity="0.94">
                <animateMotion dur="{duration}" repeatCount="indefinite" begin="{delay}s" rotate="auto">
                  <mpath href="#snakePath" />
                </animateMotion>
              </ellipse>
            </g>
            """
        )

    # Head with tiny eyes.
    head = f"""
    <g filter="url(#glow)">
      <g>
        <ellipse cx="0" cy="0" rx="8.5" ry="5.8" fill="{snake}" />
        <circle cx="2.4" cy="-1.2" r="0.9" fill="{theme['eye']}" />
        <circle cx="2.4" cy="1.2" r="0.9" fill="{theme['eye']}" />
        <circle cx="2.7" cy="-1.2" r="0.28" fill="{theme['eye_hi']}" />
        <circle cx="2.7" cy="1.2" r="0.28" fill="{theme['eye_hi']}" />
        <animateMotion dur="{duration}" repeatCount="indefinite" rotate="auto">
          <mpath href="#snakePath" />
        </animateMotion>
      </g>
    </g>
    """

    # Glow pulse on active tiles.
    highlights = []
    for idx, c in enumerate([c for c in ordered if c["count"] > 0][:140]):
        x = c["x"] + offset_x
        y = c["y"] + offset_y - 2
        begin = (idx * 0.08) % 6.0
        highlights.append(
            f"""
            <ellipse cx="{x:.1f}" cy="{y:.1f}" rx="5.8" ry="2.8"
                     fill="{glow}" opacity="0">
              <animate attributeName="opacity"
                       values="0;0.28;0"
                       dur="3.8s"
                       begin="{begin:.2f}s"
                       repeatCount="indefinite" />
            </ellipse>
            """
        )

    svg = f"""<svg xmlns="http://www.w3.org/2000/svg" width="{width}" height="{height}"
     viewBox="0 0 {width} {height}" role="img" aria-label="{title}">
  <defs>
    <filter id="glow" x="-60%" y="-60%" width="220%" height="220%">
      <feGaussianBlur stdDeviation="2.2" result="blur"/>
      <feMerge>
        <feMergeNode in="blur"/>
        <feMergeNode in="SourceGraphic"/>
      </feMerge>
    </filter>
    <linearGradient id="bgGrad" x1="0" y1="0" x2="1" y2="1">
      <stop offset="0%" stop-color="{bg}" />
      <stop offset="100%" stop-color="{theme['bg2']}" />
    </linearGradient>
  </defs>

  <rect width="100%" height="100%" rx="18" fill="url(#bgGrad)" />

  <text x="24" y="28" fill="{text}" font-size="16" font-family="Segoe UI, Arial, sans-serif" font-weight="700">
    {title}
  </text>
  <text x="24" y="48" fill="{muted}" font-size="11.5" font-family="Segoe UI, Arial, sans-serif">
    {subtitle}
  </text>

  <g>
    {''.join(cubes)}
  </g>

  <g filter="url(#glow)">
    {''.join(highlights)}
  </g>

  <path id="snakePath" d="{shifted_d}" fill="none" stroke="none" />

  <g>
    {''.join(body)}
    {head}
  </g>
</svg>
"""

    (DIST / out_name).write_text(svg, encoding="utf-8")


def main() -> None:
    today = dt.date.today()
    start = range_start(today)
    end = start + dt.timedelta(days=WEEKS * 7 - 1)

    dates = run_git_dates()
    counts = build_counts(dates, start, end)
    cells = build_cells(counts, start)

    total_commits = sum(c["count"] for c in cells)
    active_days = sum(1 for c in cells if c["count"] > 0)

    title = "Repo-Only 3D Contribution Snake"
    subtitle = f"DSAPractice commits only • {active_days} active days • {total_commits} commits • last {WEEKS} weeks"

    light_theme = {
        "bg": "#f8fbff",
        "bg2": "#eef6ff",
        "text": "#0f172a",
        "muted": "#475569",
        "snake": "#00cfe8",
        "glow": "#7df9ff",
        "eye": "#0f172a",
        "eye_hi": "#ffffff",
        "levels": ["#ebedf0", "#9be9a8", "#40c463", "#30a14e", "#216e39"],
    }

    dark_theme = {
        "bg": "#0b1220",
        "bg2": "#0f172a",
        "text": "#e5f3ff",
        "muted": "#8fb2c9",
        "snake": "#00ffff",
        "glow": "#5dfdff",
        "eye": "#03131a",
        "eye_hi": "#dffcff",
        "levels": ["#2b2f36", "#00ff9c", "#00cc7a", "#00994d", "#006633"],
    }

    render_svg(
        theme=light_theme,
        out_name="repo-contribution-snake-3d.svg",
        title=title,
        subtitle=subtitle,
        cells=cells,
    )
    render_svg(
        theme=dark_theme,
        out_name="repo-contribution-snake-3d-dark.svg",
        title=title,
        subtitle=subtitle,
        cells=cells,
    )


if __name__ == "__main__":
    main()