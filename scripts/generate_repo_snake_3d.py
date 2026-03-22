from __future__ import annotations

import datetime as dt
import math
import subprocess
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
DIST = ROOT / "dist"
DIST.mkdir(parents=True, exist_ok=True)

ROWS = 7
DEFAULT_WEEKS = 12

TILE_W = 26
TILE_H = 14
DEPTH = 10
HEIGHT_STEP = 8

MARGIN_X = 80
MARGIN_Y = 90


def git_commit_dates() -> list[dt.date]:
    cmd = [
        "git",
        "log",
        "--date=short",
        "--pretty=format:%ad",
        "--first-parent",
        "HEAD",
    ]
    out = subprocess.check_output(cmd, cwd=ROOT, text=True)
    return [dt.date.fromisoformat(x.strip()) for x in out.splitlines() if x.strip()]


def sunday_index(d: dt.date) -> int:
    return (d.weekday() + 1) % 7


def start_of_week(d: dt.date) -> dt.date:
    return d - dt.timedelta(days=sunday_index(d))


def build_counts(dates: list[dt.date]) -> dict[dt.date, int]:
    counts: dict[dt.date, int] = {}
    for d in dates:
        counts[d] = counts.get(d, 0) + 1
    return counts


def recent_active_window(counts: dict[dt.date, int], min_weeks: int = DEFAULT_WEEKS) -> tuple[dt.date, int]:
    if not counts:
        today = dt.date.today()
        start = start_of_week(today) - dt.timedelta(weeks=min_weeks - 1)
        return start, min_weeks

    latest = max(counts)
    earliest = min(counts)

    start_latest = start_of_week(latest)
    start_earliest = start_of_week(earliest)

    total_weeks = ((start_latest - start_earliest).days // 7) + 1
    weeks = max(min_weeks, min(total_weeks, 18))
    start = start_latest - dt.timedelta(weeks=weeks - 1)
    return start, weeks


def project(col: int, row: int) -> tuple[float, float]:
    x = MARGIN_X + (col - row) * (TILE_W / 2)
    y = MARGIN_Y + (col + row) * (TILE_H / 2)
    return x, y


def hex_to_rgb(h: str) -> tuple[int, int, int]:
    h = h.lstrip("#")
    return int(h[0:2], 16), int(h[2:4], 16), int(h[4:6], 16)


def rgb_to_hex(rgb: tuple[int, int, int]) -> str:
    return "#{:02x}{:02x}{:02x}".format(*rgb)


def adjust(hex_color: str, factor: float) -> str:
    r, g, b = hex_to_rgb(hex_color)
    return rgb_to_hex((
        max(0, min(255, int(r * factor))),
        max(0, min(255, int(g * factor))),
        max(0, min(255, int(b * factor))),
    ))


def level_color(n: int, theme: dict) -> str:
    levels = theme["levels"]
    if n <= 0:
        return levels[0]
    if n == 1:
        return levels[1]
    if n <= 3:
        return levels[2]
    if n <= 5:
        return levels[3]
    return levels[4]


def prism_faces(x: float, y: float, height: float, color: str) -> str:
    top_y = y - height

    top = [
        (x, top_y - TILE_H / 2),
        (x + TILE_W / 2, top_y),
        (x, top_y + TILE_H / 2),
        (x - TILE_W / 2, top_y),
    ]
    left = [
        (x - TILE_W / 2, y),
        (x, y + TILE_H / 2),
        (x, top_y + TILE_H / 2),
        (x - TILE_W / 2, top_y + DEPTH),
    ]
    right = [
        (x + TILE_W / 2, y),
        (x, y + TILE_H / 2),
        (x, top_y + TILE_H / 2),
        (x + TILE_W / 2, top_y + DEPTH),
    ]

    top_fill = color
    left_fill = adjust(color, 0.82)
    right_fill = adjust(color, 0.62)
    stroke = adjust(color, 0.50)

    def poly(points, fill):
        pts = " ".join(f"{px:.1f},{py:.1f}" for px, py in points)
        return f'<polygon points="{pts}" fill="{fill}" stroke="{stroke}" stroke-width="0.7"/>'

    return poly(left, left_fill) + poly(right, right_fill) + poly(top, top_fill)


def build_cells(counts: dict[dt.date, int], start: dt.date, weeks: int) -> list[dict]:
    cells = []
    for col in range(weeks):
        for row in range(ROWS):
            day = start + dt.timedelta(days=col * 7 + row)
            x, y = project(col, row)
            count = counts.get(day, 0)
            height = DEPTH + (min(count, 8) * HEIGHT_STEP if count > 0 else 0)
            cells.append({
                "date": day,
                "col": col,
                "row": row,
                "x": x,
                "y": y,
                "count": count,
                "height": height,
            })
    return cells


def snake_path_cells(cells: list[dict]) -> list[dict]:
    by_col: dict[int, list[dict]] = {}
    for c in cells:
        by_col.setdefault(c["col"], []).append(c)

    ordered = []
    for col in sorted(by_col):
        col_cells = sorted(by_col[col], key=lambda c: c["row"])
        if col % 2:
            col_cells.reverse()
        ordered.extend(col_cells)

    active = [c for c in ordered if c["count"] > 0]
    return active if len(active) >= 2 else ordered


def svg_path(points: list[tuple[float, float]]) -> str:
    if not points:
        return ""
    if len(points) == 1:
        x, y = points[0]
        return f"M {x:.1f} {y:.1f}"

    out = [f"M {points[0][0]:.1f} {points[0][1]:.1f}"]
    for i in range(1, len(points)):
        px, py = points[i - 1]
        x, y = points[i]
        cx1 = px + (x - px) * 0.45
        cy1 = py
        cx2 = px + (x - px) * 0.55
        cy2 = y
        out.append(f"C {cx1:.1f} {cy1:.1f}, {cx2:.1f} {cy2:.1f}, {x:.1f} {y:.1f}")
    return " ".join(out)


def render(theme: dict, filename: str, cells: list[dict], weeks: int) -> None:
    bg1 = theme["bg1"]
    bg2 = theme["bg2"]
    text = theme["text"]
    muted = theme["muted"]
    snake = theme["snake"]
    glow = theme["glow"]

    xs = [c["x"] for c in cells]
    ys = [c["y"] - c["height"] for c in cells]

    min_x, max_x = min(xs), max(xs)
    min_y, max_y = min(ys), max(c["y"] for c in cells)

    width = int((max_x - min_x) + 260)
    height = int((max_y - min_y) + 220)

    shift_x = 120 - min_x
    shift_y = 120 - min_y

    cubes = []
    for c in cells:
        color = level_color(c["count"], theme)
        cubes.append(prism_faces(c["x"] + shift_x, c["y"] + shift_y, c["height"], color))

    path_cells = snake_path_cells(cells)
    points = [
        (c["x"] + shift_x, c["y"] + shift_y - c["height"] - 4)
        for c in path_cells
    ]
    path_d = svg_path(points)

    highlights = []
    for i, c in enumerate(path_cells[:80]):
        x = c["x"] + shift_x
        y = c["y"] + shift_y - c["height"] - 4
        begin = (i * 0.06) % 3.0
        highlights.append(
            f'''
            <circle cx="{x:.1f}" cy="{y:.1f}" r="6" fill="{glow}" opacity="0">
              <animate attributeName="opacity" values="0;0.18;0" dur="2.2s" begin="{begin:.2f}s" repeatCount="indefinite"/>
            </circle>
            '''
        )

    duration = "6.2s"

    body_segments = []
    for idx, (rx, ry, delay) in enumerate([
        (12, 8, 0.0),
        (10.5, 7.0, -0.22),
        (9.2, 6.2, -0.44),
        (8.0, 5.4, -0.66),
        (6.8, 4.7, -0.88),
    ]):
        body_segments.append(
            f'''
            <ellipse cx="0" cy="0" rx="{rx}" ry="{ry}" fill="{snake}" opacity="{0.95 - idx * 0.08:.2f}">
              <animateMotion dur="{duration}" repeatCount="indefinite" begin="{delay}s" rotate="auto">
                <mpath href="#snakePath"/>
              </animateMotion>
            </ellipse>
            '''
        )

    head = f'''
    <g>
      <ellipse cx="0" cy="0" rx="14" ry="9" fill="{snake}" />
      <circle cx="4.0" cy="-2.0" r="1.1" fill="{theme["eye"]}" />
      <circle cx="4.0" cy="2.0" r="1.1" fill="{theme["eye"]}" />
      <circle cx="4.3" cy="-2.1" r="0.35" fill="{theme["eye_hi"]}" />
      <circle cx="4.3" cy="2.1" r="0.35" fill="{theme["eye_hi"]}" />
      <animateMotion dur="{duration}" repeatCount="indefinite" rotate="auto">
        <mpath href="#snakePath"/>
      </animateMotion>
    </g>
    '''

    total_commits = sum(c["count"] for c in cells)
    active_days = sum(1 for c in cells if c["count"] > 0)

    svg = f'''<svg xmlns="http://www.w3.org/2000/svg" width="{width}" height="{height}" viewBox="0 0 {width} {height}" role="img" aria-label="Repo-only 3D contribution snake">
  <defs>
    <linearGradient id="bgGrad" x1="0" y1="0" x2="1" y2="1">
      <stop offset="0%" stop-color="{bg1}"/>
      <stop offset="100%" stop-color="{bg2}"/>
    </linearGradient>
    <filter id="glow" x="-60%" y="-60%" width="220%" height="220%">
      <feGaussianBlur stdDeviation="4" result="blur"/>
      <feMerge>
        <feMergeNode in="blur"/>
        <feMergeNode in="SourceGraphic"/>
      </feMerge>
    </filter>
  </defs>

  <rect width="100%" height="100%" rx="28" fill="url(#bgGrad)"/>

  <text x="30" y="42" fill="{text}" font-size="22" font-family="Segoe UI, Arial, sans-serif" font-weight="700">
    Repo-Only 3D Contribution Snake
  </text>
  <text x="30" y="70" fill="{muted}" font-size="15" font-family="Segoe UI, Arial, sans-serif">
    Recent {weeks} weeks • {active_days} active days • {total_commits} commits
  </text>

  <g opacity="0.98">
    {''.join(cubes)}
  </g>

  <g filter="url(#glow)">
    {''.join(highlights)}
  </g>

  <path id="snakePath" d="{path_d}" fill="none" stroke="none"/>

  <g filter="url(#glow)">
    {''.join(body_segments)}
    {head}
  </g>
</svg>'''

    (DIST / filename).write_text(svg, encoding="utf-8")


def main() -> None:
    dates = git_commit_dates()
    counts = build_counts(dates)
    start, weeks = recent_active_window(counts)
    cells = build_cells(counts, start, weeks)

    light = {
        "bg1": "#f8fbff",
        "bg2": "#e9f4ff",
        "text": "#0f172a",
        "muted": "#475569",
        "snake": "#00d9ff",
        "glow": "#8af7ff",
        "eye": "#082032",
        "eye_hi": "#ffffff",
        "levels": ["#d8dee9", "#9be9a8", "#40c463", "#30a14e", "#216e39"],
    }

    dark = {
        "bg1": "#071226",
        "bg2": "#0a1b3a",
        "text": "#e6f7ff",
        "muted": "#9ec8e0",
        "snake": "#00f0ff",
        "glow": "#5ffcff",
        "eye": "#04121b",
        "eye_hi": "#dfffff",
        "levels": ["#2d333b", "#00ff9c", "#00cc7a", "#00a86b", "#007a52"],
    }

    render(light, "repo-contribution-snake-3d.svg", cells, weeks)
    render(dark, "repo-contribution-snake-3d-dark.svg", cells, weeks)


if __name__ == "__main__":
    main()