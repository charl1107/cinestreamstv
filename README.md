# CineStreamTV 📺

**CineStreamTV** is an open-source, modern streaming platform designed specifically for **Android TV and Google TV** (10-foot user experience). It combines the TV-native UX patterns of StreamFlix TV with CloudStream's modular, dynamically loaded extension plugin system.

---

## ✨ Features

- **TV-First 10-Foot UI**: Built with **Jetpack Compose for TV** and Android Leanback design principles, fully optimized for D-pad remote control navigation.
- **CloudStream Extension Compatibility**: Dynamically load, install, and run CloudStream .cs3 / .dex plugin scrapers at runtime.
- **MegaRepo Out-of-the-Box**: Pre-configured with the [MegaRepo](https://raw.githubusercontent.com/self-similarity/MegaRepo/builds/repo.json) as the default repository for immediate access to hundreds of community providers.
- **ExoPlayer (Media3) Engine**: Full adaptive bitrate streaming support including **HLS (\.m3u8\)**, **DASH**, and Progressive MP4 streams with custom referer/headers.
- **Watch History & Playback Resumption**: Automatically saves playback positions into a local Room database and offers \"Continue Watching\" rows on the home screen.
- **Bookmark / Favorites**: Save movies and series to your personal watchlist.
- **Episode & Season Selector**: Dedicated TV-friendly season tabs and horizontal episode browser.

---

## 🏗️ Architecture & Module Structure

`
CineStreamTV/
├── app-tv/                 # Smart TV UI (Compose TV Material 3, Navigation, ViewModels)
├── core/
│   ├── core-domain/        # Pure Kotlin domain models, use cases, repository contracts
│   ├── core-data/          # Room database, DAOs, Entity mappers, repository implementations
│   └── core-common/        # Shared OkHttpClient, Resource wrappers, and utility extensions
├── extension-engine/       # CloudStream-compatible dynamic plugin loader and RepoManager
├── player/                 # Media3 ExoPlayer wrapper with TV D-pad overlay controls
└── gradle/
    └── libs.versions.toml  # Centralized TOML Version Catalog
`

---

## 🛠️ Prerequisites

Before building and running the application, make sure you have:

1. **Android Studio**: Android Studio Hedgehog (2023.1.1) or newer.
2. **JDK**: Java 17 or Java 21 (configured in Android Studio under *Settings > Build, Execution, Deployment > Build Tools > Gradle > Gradle JDK*).
3. **Android TV Device or Emulator**:
   - Physical Android TV / Google TV device with USB / Wi-Fi debugging enabled, OR
   - Android TV AVD Emulator (API 28+, 1080p or 4K TV resolution recommended).

---

## 🚀 Getting Started & Running

### 1. Clone the Repository
`ash
git clone https://github.com/charl1107/cinestreamstv.git
cd cinestreamstv
`

### 2. Open in Android Studio
1. Launch Android Studio.
2. Select **Open** and navigate to the cloned cinestreamstv directory.
3. Allow Gradle to download dependencies and sync the project.

### 3. Build & Run
1. In the top run configuration dropdown, select **pp-tv**.
2. Select your Android TV emulator or connected physical Android TV device.
3. Click the **Run ▶** button (or press Shift + F10).

### 4. Build APK via Command Line
`ash
# Windows
.\gradlew assembleDebug

# Linux / macOS
./gradlew assembleDebug
`
The compiled APK will be located at:
pp-tv/build/outputs/apk/debug/app-tv-debug.apk

---

## 🎮 Remote Control (D-pad) Controls

| Button / Key | In Menu / Browsing | During Video Playback |
|---|---|---|
| **D-pad Left / Right** | Navigate items horizontally | Seek backward / forward 10 seconds |
| **D-pad Up / Down** | Navigate items vertically / rows | Show / Hide playback controls overlay |
| **Center / Enter (OK)** | Select item / Play | Toggle Play / Pause |
| **Back Button** | Navigate to previous screen | Exit playback to detail view |
| **Play/Pause Key** | Play selected item | Toggle Play / Pause |

---

## 🧩 Managing Extensions & Repositories

1. On the home screen top bar, navigate to **Extensions**.
2. CineStreamTV automatically syncs plugins from the built-in default **MegaRepo**:
   https://raw.githubusercontent.com/self-similarity/MegaRepo/builds/repo.json
3. Click **Install** on any available extension to download and register its scrapers immediately.
4. Custom repository URLs can also be added directly from the Extensions screen.

---

## 🧰 Technology Stack

- **Language**: Kotlin 1.9.22
- **UI Framework**: Jetpack Compose for TV (ndroidx.tv.material3, ndroidx.tv.foundation) + Android Leanback
- **Architecture**: MVI (Model-View-Intent) & Clean Architecture
- **Dependency Injection**: Dagger-Hilt 2.50
- **Asynchronous**: Kotlin Coroutines & Flow
- **Local Persistence**: Room SQLite 2.6.1
- **Networking**: OkHttp 4.12.0 + Kotlinx Serialization
- **Video Engine**: AndroidX Media3 ExoPlayer 1.2.1 (HLS & DASH)
- **Image Loading**: Coil 2.5.0

---

## 📄 License

This project is licensed under the [GPL-3.0 License](LICENSE).
