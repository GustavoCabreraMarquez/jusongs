---
name: app-rebrand
description: Restyle the Jusongs (S-WAVE 2000) look quickly by generating standalone HTML prototypes of its screens from user pointers, before touching any Compose code. Use when the user wants to try a new visual direction, palette, or layout for the app.
---

# app-rebrand

Goal: let the user judge a visual direction in a browser for few tokens, and only port the winner to Compose.

## Steps
1. Gather pointers from the user (mood, palette, reference apps, things to keep/drop). If none, ask for one or two; don't over-ask.
2. Read only `app/src/main/java/com/swave/swave2000/theme/Color.kt` and skim `components/` names for the current tokens. Do not read the whole UI.
3. Write ONE self-contained file `prototypes/<variant-name>.html` (inline CSS, no JS frameworks, no network assets). Define the palette as CSS variables mirroring the `Y2K*` names in `Color.kt`.
4. Show these screens side by side in phone-sized frames (360x780): Home, Albums grid, Songs list, Now Playing (LCD, buttons, knob), with the Spotify-style bottom nav (Home / Albums / Songs) and the mini-player. Use fake data.
5. Offer 2-3 variants as separate files when the user's direction is open; reuse the same markup and swap only the variables.
6. Tell the user the file path to open (`xdg-open prototypes/<name>.html`). Stop and wait for feedback; iterate by editing only the changed CSS.
7. When the user picks a variant, port it: update `theme/Color.kt`, `theme/Type.kt`, then the `components/` that need shape/bevel changes. Keep `prototypes/` out of the app build.

## Rules
- Prototypes are throwaway: no tests, no build, minimal explanation.
- Keep the app's identity (tactile, beveled, LCD) unless the user says to drop it.
