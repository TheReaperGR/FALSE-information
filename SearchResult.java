public class SearchResult {
    private final String searchDirectory;
    private String filePath;
    private String name;
    private String artist;
    private String albumName;
    private int year;
    private float score;

    public SearchResult(String filePath, String content, float score, String searchType, String searchDirectory) {
        this.filePath = filePath;
        this.score = score;
        this.searchDirectory = searchDirectory;
        // Split the content into parts using commas only if it is not null
        if (content != null) {
            String[] parts = content.split(",");

            // Initialize name, artist, albumName, and year
            this.name = "N/A";
            this.artist = "N/A";
            this.albumName = "N/A";
            this.year = 0;

            // Check if there are enough parts
            if (parts.length >= 4) {
                this.name = parts[1].trim();
                this.artist = parts[2].trim();
            }
        } else {
            this.name = "N/A";
            this.artist = "N/A";
            this.albumName = "N/A";
            this.year = 0;
        }
    }

    public String getFilePath() {
        return filePath;
    }

    public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }

    public String getSearchDirectory() {
        return searchDirectory;
    }
    public String getAlbumName() {
        return albumName;
    }

    public int getYear() {
        return year;
    }

    public float getScore() {
        return score;
    }
}
