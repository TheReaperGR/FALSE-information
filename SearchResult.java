public class SearchResult {
    private String searchDirectory;
    private String filePath;
    private String name;
    private String artist;
    private String albumName;
    private int year;
    private float score;
    private String songName;

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
    public void setSongName(String songName){this.songName = songName;}

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    public String getFilePath() {
        return filePath;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getArtist() {
        return artist;
    }
    public String getSearchDirectory() {
        return searchDirectory;
    }
    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }
    public String getAlbumName() {
        return albumName;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getYear() {
        return year;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public float getScore() {
        return score;
    }

}