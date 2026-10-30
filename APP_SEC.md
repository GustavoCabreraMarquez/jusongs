# Local Hi-Fi Music Player ("S-WAVE 2000") — Specification

## 1. Project Overview & Philosophy
- **Purpose**: A local-only, ad-free, high-fidelity music player running natively on Android (Target device: Motorola Moto G54).
- **Core Principle**: Zero subscription, zero tracking, bit-perfect playback from local storage (music transferred via USB or local transfer).
- **Design Aesthetic**: "Tactile Y2K Retro-Futurism" inspired by:
  - *The Sims 2* HUD (glossy beveled surfaces, molded plastic framing, faceted crystal badge).
  - Early-2000s car stereo head units (tactile extruded push-buttons, rotary volume knob, recessed dot-matrix / LCD display).
  - **Color Palette**: Muted pastel sage greens, soft teals, and warm creams (harmonious, no aggressive red/blue clashing).

---

## 2. Screen & Navigation Structure
### Primary Navigation
1. **Albums Tab**:
   - 2-column tactile grid layout.
   - Album artwork enclosed in beveled, recessed containers with physical frame shadows.
   - Metadata label beneath each card: Album title (bold), Artist name (subtle).
2. **Songs Tab**:
   - List view of individual standalone/orphan tracks with track duration, format badge, and artist.
3. **Persistent Mini-Player (Bottom Bar)**:
   - Compact pill-shaped bar with track title, mini faceted gem icon, and basic transport controls (Play/Pause, Skip).

### Now Playing View
- **Recessed LCD/VFD Screen**:
  - Pixelated dot-matrix typography for Track Title, Artist, and Time Elapsed/Total.
  - Animated digital spectrum analyzer (EQ visualizer bars).
- **Tactile Button Bay**:
  - Heavy, beveled physical buttons: Play/Pause, Forward, Reverse, Eject, Band, Bass Boost, 3D Sound.
- **Rotary Master Volume**:
  - Central dial with textured grip ridges, MIN/MAX stops, and rotary indicator notch.
- **Lossless Badge**:
  - Discreet indicator displaying format and bit-rate (e.g., `FLAC 24-bit 96kHz`).

---

## 3. Recommended Tech Stack
- **Option A (Native Android - Recommended for bit-perfect audio)**:
  - Kotlin + Jetpack Compose.
  - Audio Engine: `androidx.media3` (`Media3 ExoPlayer`) with background `MediaSessionService`.
  - Storage / Metadata Indexing: Android `MediaStore` API + custom ID3/Vorbis tag reader.
- **Option B (Cross-Platform Flutter)**:
  - Flutter (Dart) with custom Canvas/Neumorphic styling.
  - Audio Engine: `just_audio` + `audio_service`.

---

## 4. Immediate Development Phases
- **Phase 1 (Current Goal)**:
  - Local file scanner picking up `.flac`, `.mp3`, `.wav`, and `.m4a` from device storage.
  - Background audio playback with notification and lock screen controls.
  - Basic 2-tab interface (Albums & Songs) using custom Compose / Flutter widgets reflecting the pastel retro style.
- **Phase 2 (Future Scope)**:
  - Guitar tablature integration (sidecar `.gp` / `.txt` files or embedded lyrics tags).
