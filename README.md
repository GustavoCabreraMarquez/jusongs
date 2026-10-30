# JuSongs

A local-only, ad-free, high-fidelity music player for Android, with a modern glass look: frosted translucent panels over a painted landscape that changes with the time of day (morning, noon and a starry night with static snow).

No subscription. No tracking. No algorithm. Only the music you put on your phone.

---

## Why this app exists

### 1. Focus

Nowadays it is very easy to consume songs, videos and all kinds of content we never actually chose. How many times have you caught yourself watching a video with NO idea how you got there? It can be comical: hopping from video to video in the morning until you're watching something exotic about the life of zebras in a remote part of the world.

But there is a twist. Corporations do their very best to keep you engaged in their apps, and getting you to mindlessly watch things you don't care about is a great way to do it. I don't want to watch what these companies think I should watch, or watch more just because it serves their shareholders.

This app needs **intent** from its user. You have to actually add songs to your phone before you can listen to them (crazy, right?). Think about the songs you really want to hear, the artists you like, the great albums. Put them on your phone **intentionally** and listen to them **intentionally**.

### 2. Simplicity

The other day Spotify added audiobooks to its catalog, and I have two problems with that:

1. Someone has to pay the licensing for those audiobooks, and since I don't consume them, I don't want to pay for them.
2. Can't an app be good at one thing and nothing more? It has to have videos, lyrics, podcasts, audiobooks... what's next, recipes? "Cook with Spotify"?

That is not the app I want. I pay for that product, and it does nothing I stand for. There isn't a single update I look forward to testing. So why not make my own app?

### 3. Quality

This reason is much lower on the priority list, but since this is a local music player, you can have the highest possible quality: use the best audio files you can find and listen to them through a horrible Bluetooth codec. Sounds great, right? Give it a try!

---

## Features

- Plays local `.flac`, `.mp3`, `.wav` and `.m4a` files
- Albums and Songs views
- Background playback with notification and lock screen controls
- Time-of-day theme chosen when the app opens: 05:00-09:59 morning, 10:00-17:59 noon, 18:00-04:59 night
- Now Playing screen with progress bar, volume slider and a format badge (e.g. `FLAC 24-bit 96kHz`)
- Bass boost and 3D sound effects
- No accounts, no network, no ads

See [APP_SEC.md](APP_SEC.md) for the full specification and [Enhancements-I.md](Enhancements-I.md) for what's planned next.

## Tech stack

Kotlin, Jetpack Compose, AndroidX Media3 (ExoPlayer) and Android `MediaStore`.

---

## Installation

The app is built from source and installed on your phone. Requirements: Android 8.0 (API 26) or newer.

### 1. Install the prerequisites

- **Git**
- **JDK 17**
- **Android Studio** (recommended, it bundles the Android SDK), or the Android SDK command-line tools plus `adb`

### 2. Get the code

```bash
git clone <repository-url> Jusongs
cd Jusongs
```

### 3. Point the project to your Android SDK

Create (or check) `local.properties` in the project root:

```properties
sdk.dir=/path/to/your/Android/Sdk
```

Android Studio generates this file for you when you open the project. A typical Linux path is `/home/<user>/Android/Sdk`.

### 4. Enable USB debugging on your phone

1. Go to **Settings → About phone** and tap **Build number** 7 times to unlock Developer options.
2. Go to **Settings → System → Developer options** and enable **USB debugging**.
3. Connect the phone with a USB cable and accept the "Allow USB debugging" prompt.
4. Check that the phone is detected:

   ```bash
   adb devices
   ```

### 5. Build and install

```bash
./gradlew installDebug
```

This builds the app and installs it straight onto the connected phone. The first build downloads Gradle and dependencies, so it takes a few minutes.

Prefer Android Studio? Open the project folder, select your phone and press **Run**.

To only build the APK without installing:

```bash
./gradlew assembleDebug
# Output: app/build/outputs/apk/debug/app-debug.apk
```

### 6. Add your music (the intentional part)

Copy the songs, albums and artists you actually want to hear to your phone:

```bash
adb push "My Album" /sdcard/Music/Jusongs/
```

You can also use a file manager over USB (MTP) and drop the files into the `Music/Jusongs` folder.

### 7. Open the app

Launch **JuSongs** and grant the audio and notification permissions when asked. Your music will show up in the Albums and Songs tabs.

> **Note:** the app only lists audio inside `Music/Jusongs/`, so WhatsApp audios and other clutter stay out. If a freshly copied album does not show up, wait a moment (Android may still be reading its tags, especially large hi-res FLACs) or reopen the app.

---

## Development

```bash
./gradlew test          # run unit tests
./gradlew assembleDebug # build debug APK
```
