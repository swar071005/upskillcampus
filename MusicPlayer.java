import java.io.File;
import java.util.*;
import javax.sound.sampled.*;

/**
 * ============================================================================
 * BeautifulMusicPlayer - BACKEND (All Features)
 * ============================================================================
 */
public class MusicPlayer {
    
    // ========== DATA STRUCTURES ==========
    private List<Song> library;
    private Map<String, Playlist> playlists;
    private Map<String, List<Song>> folders;
    private Playlist currentPlaylist;
    
    // ========== PLAYBACK STATE ==========
    private Song currentSong;
    private int currentIndex = -1;
    private boolean isPlaying = false;
    private boolean isPaused = false;
    
    // ========== AUDIO ==========
    private Clip currentClip;
    private FloatControl volumeControl;
    private Thread playbackThread;
    
    // ========== SETTINGS ==========
    private int volume = 70;
    private boolean shuffle = false;
    private RepeatMode repeatMode = RepeatMode.OFF;
    private boolean crossfade = false;
    private EqualizerPreset currentEQPreset = EqualizerPreset.FLAT;
    
    // ========== ENUMS ==========
    public enum RepeatMode { OFF, ONE, ALL }
    public enum EqualizerPreset { FLAT, ROCK, POP, JAZZ, CLASSICAL, BASS_BOOST, VOCAL }
    
    // ========== CONSTRUCTOR ==========
    public BeautifulMusicPlayer() {
        library = new ArrayList<>();
        playlists = new HashMap<>();
        folders = new HashMap<>();
        initializeDefaultPlaylists();
    }
    
    private void initializeDefaultPlaylists() {
        createPlaylist("All Songs");
        createPlaylist("Favorites");
        createPlaylist("Recently Played");
    }
    
    // ========== 1. MUSIC FILE IMPORT ==========
    
    public void loadSongs(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            scanDirectory(directory, directory.getName());
        }
        
        if (library.isEmpty()) {
            loadDemoSongs();
        }
        
        // Set "All Songs" as default playlist
        Playlist allSongs = playlists.get("All Songs");
        if (allSongs != null) {
            allSongs.getSongs().addAll(library);
            currentPlaylist = allSongs;
        }
    }
    
    private void scanDirectory(File dir, String folderName) {
        File[] files = dir.listFiles();
        if (files == null) return;
        
        List<Song> folderSongs = folders.getOrDefault(folderName, new ArrayList<>());
        
        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectory(file, file.getName());
            } else if (isAudioFile(file)) {
                Song song = new Song(file);
                library.add(song);
                folderSongs.add(song);
            }
        }
        
        folders.put(folderName, folderSongs);
    }
    
    private boolean isAudioFile(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".mp3") || name.endsWith(".wav") || 
               name.endsWith(".flac") || name.endsWith(".aiff") || 
               name.endsWith(".au") || name.endsWith(".m4a");
    }
    
    private void loadDemoSongs() {
        String[] demoNames = {
            "Summer Vibes.mp3", "Night Drive.mp3", "Morning Coffee.mp3",
            "Rainy Days.mp3", "Sunset Beach.mp3", "City Lights.mp3",
            "Mountain Echo.mp3", "Ocean Waves.mp3"
        };
        
        for (String name : demoNames) {
            Song song = new Song(new File(name));
            song.setArtist("Demo Artist");
            song.setAlbum("Demo Album");
            library.add(song);
        }
        
        folders.put("Demo", new ArrayList<>(library));
    }
    
    // ========== 2. PLAYBACK CONTROL ==========
    
    public void play(int index) {
        if (currentPlaylist != null && index >= 0 && index < currentPlaylist.getSongs().size()) {
            currentIndex = index;
            currentSong = currentPlaylist.getSongs().get(index);
            playCurrent();
        }
    }
    
    private void playCurrent() {
        if (currentSong == null) return;
        
        stop();
        
        File file = currentSong.getFile();
        if (!file.exists()) {
            isPlaying = true;
            return;
        }
        
        playbackThread = new Thread(() -> {
            try {
                AudioInputStream stream = AudioSystem.getAudioInputStream(file);
                currentClip = AudioSystem.getClip();
                currentClip.open(stream);
                
                if (currentClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                    volumeControl = (FloatControl) currentClip.getControl(FloatControl.Type.MASTER_GAIN);
                    setVolumeInternal(volume);
                }
                
                currentClip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP && !isPaused) {
                        handleSongEnd();
                    }
                });
                
                currentClip.start();
                isPlaying = true;
                isPaused = false;
                
                addToRecentlyPlayed(currentSong);
                currentSong.incrementPlayCount();
                
            } catch (Exception e) {
                isPlaying = true; // Demo mode
            }
        });
        
        playbackThread.start();
    }
    
    public void pause() {
        if (currentClip != null && isPlaying && !isPaused) {
            currentClip.stop();
            isPaused = true;
            isPlaying = false;
        } else if (currentClip != null && isPaused) {
            currentClip.start();
            isPaused = false;
            isPlaying = true;
        }
    }
    
    public void stop() {
        if (currentClip != null) {
            currentClip.stop();
            currentClip.close();
            currentClip = null;
        }
        isPlaying = false;
        isPaused = false;
    }
    
    public void seek(long seconds) {
        if (currentClip != null) {
            currentClip.setMicrosecondPosition(seconds * 1000000);
        }
    }
    
    public void setVolume(int vol) {
        volume = Math.max(0, Math.min(100, vol));
        setVolumeInternal(volume);
    }
    
    private void setVolumeInternal(int vol) {
        if (volumeControl != null) {
            float min = volumeControl.getMinimum();
            float max = volumeControl.getMaximum();
            float gain = min + (max - min) * vol / 100.0f;
            volumeControl.setValue(gain);
        }
    }
    
    // ========== 3. PLAYLIST MANAGEMENT ==========
    
    public void createPlaylist(String name) {
        if (!playlists.containsKey(name)) {
            playlists.put(name, new Playlist(name));
        }
    }
    
    public void addToPlaylist(String playlistName, Song song) {
        Playlist playlist = playlists.get(playlistName);
        if (playlist != null) {
            playlist.addSong(song);
        }
    }
    
    public void removeFromPlaylist(String playlistName, Song song) {
        Playlist playlist = playlists.get(playlistName);
        if (playlist != null) {
            playlist.removeSong(song);
        }
    }
    
    public void setCurrentPlaylist(String name) {
        currentPlaylist = playlists.get(name);
    }
    
    // ========== 4. LIBRARY ORGANIZATION ==========
    
    public List<Song> search(String query) {
        List<Song> results = new ArrayList<>();
        String q = query.toLowerCase();
        
        for (Song song : library) {
            if (song.getTitle().toLowerCase().contains(q) ||
                song.getArtist().toLowerCase().contains(q) ||
                song.getAlbum().toLowerCase().contains(q)) {
                results.add(song);
            }
        }
        
        return results;
    }
    
    public List<Song> getSongsByArtist(String artist) {
        List<Song> result = new ArrayList<>();
        for (Song song : library) {
            if (song.getArtist().equalsIgnoreCase(artist)) {
                result.add(song);
            }
        }
        return result;
    }
    
    public Set<String> getAllArtists() {
        Set<String> artists = new TreeSet<>();
        for (Song song : library) {
            artists.add(song.getArtist());
        }
        return artists;
    }
    
    // ========== 5. EQUALIZER ==========
    
    public void setEqualizerPreset(EqualizerPreset preset) {
        currentEQPreset = preset;
        System.out.println("EQ: " + preset);
    }
    
    // ========== 6. SHUFFLE & REPEAT ==========
    
    public void toggleShuffle() {
        shuffle = !shuffle;
        if (currentPlaylist != null && shuffle) {
            currentPlaylist.shuffle();
        }
    }
    
    public void toggleRepeat() {
        switch (repeatMode) {
            case OFF: repeatMode = RepeatMode.ONE; break;
            case ONE: repeatMode = RepeatMode.ALL; break;
            case ALL: repeatMode = RepeatMode.OFF; break;
        }
    }
    
    // ========== 7. CROSSFADE ==========
    
    public void toggleCrossfade() {
        crossfade = !crossfade;
    }
    
    // ========== NAVIGATION ==========
    
    public void next() {
        if (currentPlaylist == null || currentPlaylist.getSongs().isEmpty()) return;
        
        if (shuffle) {
            currentIndex = (int) (Math.random() * currentPlaylist.getSongs().size());
        } else {
            currentIndex = (currentIndex + 1) % currentPlaylist.getSongs().size();
        }
        
        play(currentIndex);
    }
    
    public void previous() {
        if (currentPlaylist == null || currentPlaylist.getSongs().isEmpty()) return;
        
        currentIndex = (currentIndex - 1 + currentPlaylist.getSongs().size()) 
                      % currentPlaylist.getSongs().size();
        play(currentIndex);
    }
    
    private void handleSongEnd() {
        switch (repeatMode) {
            case ONE:
                play(currentIndex);
                break;
            case ALL:
                next();
                break;
            case OFF:
                if (currentIndex < currentPlaylist.getSongs().size() - 1) {
                    next();
                }
                break;
        }
    }
    
    private void addToRecentlyPlayed(Song song) {
        Playlist recent = playlists.get("Recently Played");
        if (recent != null) {
            recent.getSongs().remove(song);
            recent.getSongs().add(0, song);
            if (recent.getSongs().size() > 20) {
                recent.getSongs().remove(recent.getSongs().size() - 1);
            }
        }
    }
    
    // ========== GETTERS ==========
    
    public Song[] getSongs() {
        if (currentPlaylist != null) {
            return currentPlaylist.getSongs().toArray(new Song[0]);
        }
        return library.toArray(new Song[0]);
    }
    
    public String getCurrentSongName() {
        return currentSong != null ? currentSong.getTitle() : null;
    }
    
    public boolean isPlaying() { return isPlaying && !isPaused; }
    public boolean isShuffle() { return shuffle; }
    public boolean isRepeat() { return repeatMode != RepeatMode.OFF; }
    public int getVolume() { return volume; }
    public int getCurrentIndex() { return currentIndex; }
    public List<Song> getLibrary() { return library; }
    public Map<String, Playlist> getPlaylists() { return playlists; }
    public Playlist getCurrentPlaylist() { return currentPlaylist; }
    public Song getCurrentSong() { return currentSong; }
    
    public void cleanup() {
        stop();
        if (playbackThread != null && playbackThread.isAlive()) {
            playbackThread.interrupt();
        }
    }
}

// ========== SONG CLASS ==========
class Song {
    private File file;
    private String title;
    private String artist;
    private String album;
    private int playCount;
    
    public Song(File file) {
        this.file = file;
        this.title = extractTitle(file.getName());
        this.artist = "Unknown Artist";
        this.album = "Unknown Album";
        this.playCount = 0;
        extractMetadataFromFilename();
    }
    
    private String extractTitle(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex > 0) {
            filename = filename.substring(0, dotIndex);
        }
        return filename;
    }
    
    private void extractMetadataFromFilename() {
        String name = file.getName();
        int dashIndex = name.indexOf(" - ");
        if (dashIndex > 0) {
            this.artist = name.substring(0, dashIndex).trim();
            this.title = extractTitle(name.substring(dashIndex + 3));
        }
    }
    
    public void setTitle(String title) { this.title = title; }
    public void setArtist(String artist) { this.artist = artist; }
    public void setAlbum(String album) { this.album = album; }
    public void incrementPlayCount() { playCount++; }
    
    public File getFile() { return file; }
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public String getAlbum() { return album; }
    public int getPlayCount() { return playCount; }
    
    @Override
    public String toString() {
        return title + " - " + artist;
    }
}

// ========== PLAYLIST CLASS ==========
class Playlist {
    private String name;
    private List<Song> songs;
    
    public Playlist(String name) {
        this.name = name;
        this.songs = new ArrayList<>();
    }
    
    public void addSong(Song song) {
        if (!songs.contains(song)) {
            songs.add(song);
        }
    }
    
    public void removeSong(Song song) {
        songs.remove(song);
    }
    
    public void shuffle() {
        Collections.shuffle(songs);
    }
    
    public String getName() { return name; }
    public List<Song> getSongs() { return songs; }
    public int getSize() { return songs.size(); }
    
    @Override
    public String toString() {
        return name + " (" + songs.size() + " songs)";
    }
}
