
# 🎵 Naad Music Player (Java Swing)

A **beautiful, modern desktop music player** built using **Java Swing** with custom UI components, smooth playback controls, and advanced features like playlists, shuffle, repeat, and equalizer presets.

---

## ✨ Features

### 🎧 Core Playback
- ▶ Play / ⏸ Pause / ⏹ Stop  
- ⏭ Next / ⏮ Previous  
- 🔊 Volume control with slider  
- ⏱ Seek functionality  

### 📂 Music Library
- Load songs from local folders  
- Supported formats:
  - `.mp3`, `.wav`, `.flac`, `.aiff`, `.au`, `.m4a`  
- Auto metadata extraction (filename-based)

### 📜 Playlist Management
- Create custom playlists  
- Add / remove songs  
- Default playlists:
  - All Songs  
  - Favorites  
  - Recently Played  

### 🔀 Smart Controls
- Shuffle mode  
- Repeat modes:
  - OFF  
  - ONE  
  - ALL  

### 🎚 Equalizer Presets
- Flat, Rock, Pop, Jazz, Classical, Bass Boost, Vocal  

### 🎨 Modern UI Design
- Gradient dark theme (blue → purple)  
- Circular album art (vinyl style)  
- Glassmorphism playlist panel  
- Custom round buttons with hover effects  

---

## 🖼 UI Preview

```
Naad Music Player - Modern UI

   🎵 Circular Album Art (Glow Effect)

   Song Name
   Artist Name

   [ Progress Bar ]

   🔀 ⏮ ▶ ⏭ 🔁

   🔊 Volume Slider

   Playlist (Glass Panel)
   ----------------------
   Song 1
   Song 2
   Song 3
```

---

## 🏗 Project Structure

```
MusicPlayerProject/
│
├── MusicPlayer.java            # Backend Logic
├── BeautifulMusicPlayer.java   # Frontend UI
├── README.md                   # Documentation
└── assets/ (optional)
```

---

## ⚙️ How to Run

### 1️⃣ Prerequisites
- Java JDK 8 or higher  
- Terminal / Command Prompt  

### 2️⃣ Compile
```bash
javac MusicPlayer.java
javac BeautifulMusicPlayer.java
```

### 3️⃣ Run
```bash
java BeautifulMusicPlayer
```

---

## 🚀 Usage Guide

### ▶ Play Music
- Double-click a song  
OR  
- Select song → Click ▶ button  

### ⏸ Pause / Resume
- Click pause button  

### 🔀 Shuffle
- Click shuffle button  
- Turns active when enabled  

### 🔁 Repeat
- Toggle between:
  - OFF → ONE → ALL  

### 🔊 Volume
- Adjust using slider (0–100%)  

---

## 🎨 UI Highlights

### 🎵 Circular Album Art
- Vinyl-inspired design  
- Gradient glow effect  
- Center icon display  

### 🌈 Gradient Background
- Smooth dark theme  
- Easy on eyes  

### 🧊 Glass Playlist Panel
- Transparent effect  
- Modern UI  

### 🔘 Custom Buttons
- Circular design  
- Hover & click animations  

---

## 🧠 Technical Concepts Used

- Object-Oriented Programming (OOP)  
- Java Swing & AWT  
- Event-driven programming  
- Multithreading (audio playback)  
- File handling  
- Data Structures:
  - ArrayList  
  - HashMap  
  - TreeSet  

---

## 🔧 Customization

### 🎨 Colors
```java
private final Color BG_START = new Color(15, 15, 35);
private final Color BG_END = new Color(50, 20, 60);
```

### 🔘 Button Sizes
Edit in `createControlsPanel()`

### 🎵 Album Art Size
Modify in UI class  

---

## 🐛 Troubleshooting

| Problem | Solution |
|--------|---------|
| javac not recognized | Install JDK & set PATH |
| No songs loaded | Demo songs load automatically |
| UI looks different | Expected (custom rendering) |
| Audio not playing | Ensure valid audio files |

---

## 📌 Future Improvements

- 🎶 Real metadata extraction (ID3 tags)  
- 🎧 Streaming support  
- 💾 Save playlists to file  
- 🌐 Cloud integration  
- 🎨 Theme switching  

---

## 👨‍💻 Author

**Swar Sawalkar**  
Internship Project – Music Player System  

---

## 📄 License

This project is for **educational and internship purposes**.

---

## ⭐ Support

If you like this project:
- ⭐ Star the repository  
- 🍴 Fork it  
- 📢 Share it  

---

## 🎉 Final Note

This project demonstrates:
- Clean UI design  
- Strong backend logic  
- Real-world Java application development  

✨ *A perfect blend of music and modern UI design!* 🎵
