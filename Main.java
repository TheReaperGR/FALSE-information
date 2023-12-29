import com.opencsv.exceptions.CsvValidationException;
import org.apache.lucene.queryparser.classic.ParseException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    private static final String INDEX_DIR = "C:\\Users\\Reaper\\IdeaProjects\\false\\src\\";
    private static final String DATA_DIR = "C:\\Users\\Reaper\\IdeaProjects\\false\\src\\songsOutput";
    private static final String ALBUM_DIR = "C:\\Users\\Reaper\\IdeaProjects\\false\\src\\albumOutput";
    private static final String LYRICS_DIR = "C:\\Users\\Reaper\\IdeaProjects\\false\\src\\lyricsOutput";
    private static final Map<String, String> SEARCH_PATHS = new HashMap<>();

    // Additional variables to keep track of the current search type and search path
    private static String searchType;
    private static String searchPath;

    public static void main(String[] args) {
        // parse all the CSVs
        try {
            Parser.parseAll();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }

        // open the GUI window:
        GUI.renderGUI(new GUI.SearchCallback() {
            @Override
            public void onSearch(String selectedMode) {
                searchType = selectedMode;
                try {
                    regularSearch(searchType);
                } catch (IOException | ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // create indices for all paths
        try {
            createAllIndices();
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private static void createAllIndices() throws IOException, ParseException {
        Indexer indexer = new Indexer(INDEX_DIR);

        if (indexer.isIndexExists()) {
            System.out.println("Index already exists. Skipping indexing step.");
        } else {
            try {
                int numIndexed = 0;
                long startTime = System.currentTimeMillis();

                numIndexed += indexer.createIndex(LYRICS_DIR, new TextFileFilter());
                numIndexed += indexer.createIndex(DATA_DIR, new TextFileFilter());
                numIndexed += indexer.createIndex(ALBUM_DIR, new TextFileFilter());

                long endTime = System.currentTimeMillis();
                System.out.println(numIndexed + " File(s) indexed, time taken: " +
                        (endTime - startTime) + " ms");
            } finally {
                indexer.close(); // Ensure that the indexer is closed even if an exception occurs
            }
        }
    }

    public static void regularSearch(String st) throws IOException, ParseException {
        searchType = st;
        String searchDirectory = setSearchPath();

        Searcher searcher = new Searcher(INDEX_DIR);
        long startTime = System.currentTimeMillis();

        // get user query from GUI search-box:
        String query = GUI.getUserQuery();

        List<SearchResult> searchResults = searcher.search(query, searchType, searchDirectory);

        long endTime = System.currentTimeMillis();
        searcher.close();
        System.out.println("Search completed in " + (endTime - startTime) + " ms");

        // note: once the list of all results has been created we then send them to the GUI so it can print them out
        for (SearchResult result : searchResults) {
            GUI.appendResultTextArea("File Path: " + result.getFilePath());
            GUI.appendResultTextArea("Name: " + result.getName());
            GUI.appendResultTextArea("Artist: " + result.getArtist());

            // Check the search type for specific fields
            if ("song".equals(searchType)) {
                GUI.appendResultTextArea("Album Name: " + result.getAlbumName());
            } else if ("album".equals(searchType)) {
                GUI.appendResultTextArea("Year: " + result.getYear());
            }

            float score = result.getScore();    // get the score and send it
            GUI.appendInfoText("Score: " + Float.toString(score));
        }
    }

    private static String setSearchPath() {
        switch (searchType) {
            case "lyrics":
                searchPath = LYRICS_DIR;
                break;
            case "songs":
                searchPath = DATA_DIR;
                break;
            case "albums":
                searchPath = ALBUM_DIR;
                break;
            default:
                System.out.println("Invalid search type.");
                searchPath = ""; // or provide a default value
        }

        return searchPath;
    }
}
