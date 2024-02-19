public class LuceneConstants {
    public static final String CONTENTS = "fieldName";
    public static final String FILE_NAME = "filename";
    public static final String FILE_PATH = "filepath";
    public static final String LYRICS = "lyrics";      // Add this line for lyrics field
    public static final String SONG_NAME = "songName"; // Add this line for song name field
    public static final String ALBUM_NAME = "name"; // Add this line for song name field
    public static final String ARTIST = "artist";      // Add this line for artist field
    public static final String YEAR = "Year";

    // Fields for lyrics document
    public static final String[] LYRICS_SEARCH_FIELDS = {CONTENTS, LYRICS, ARTIST, SONG_NAME};

    // Fields for songs document
    public static final String[] SONGS_SEARCH_FIELDS = {CONTENTS, SONG_NAME, ARTIST, ALBUM_NAME};

    // Fields for albums document
    public static final String[] ALBUMS_SEARCH_FIELDS = {CONTENTS, ALBUM_NAME, ARTIST, YEAR};
    public static final int MAX_SEARCH = 10;
    public static final String DATA_DIR = "E:\\Intellij IDEA projects\\false_v1\\Data\\songsOutput";  // Add this line for songs directory
    public static final String ALBUM_DIR = "E:\\Intellij IDEA projects\\false_v1\\Data\\albumOutput"; // Add this line for albums directory
    public static final String LYRICS_DIR = "E:\\Intellij IDEA projects\\false_v1\\Data\\lyricsOutput"; // Add this line for lyrics directory
    public static final String SONGS_INDEX_DIR = "E:\\Intellij IDEA projects\\false_v1\\Data\\songs";
    public static final String LYRICS_INDEX_DIR = "E:\\Intellij IDEA projects\\false_v1\\Data\\lyrics";
    public static final String ALBUMS_INDEX_DIR = "E:\\Intellij IDEA projects\\false_v1\\Data\\albums";

}
