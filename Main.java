import com.opencsv.exceptions.CsvValidationException;
import org.apache.lucene.queryparser.classic.ParseException;

import java.io.IOException;
import java.util.List;

public class Main {
    private static final String INDEX_DIR = "E:\\Intellij IDEA projects\\false_v1\\Data";
    private String searchType;
    private String searchPath;

    public static void main(String[] args) throws IOException, ParseException {

        Main instanceOfMain = new Main();

        // parse all the CSVs
        try {
            Parser.parseAll();
            instanceOfMain.searchType = null;
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }

        try {
            createAllIndices();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        // open the GUI window:
        GUI.renderGUI(new GUI.SearchCallback() {
            @Override
            public void onSearch(String selectedMode) {
                instanceOfMain.searchType = selectedMode;
                System.out.println("in main search type set to: " + instanceOfMain.searchType);
                try {
                    createAndSearchIndex(instanceOfMain.searchType);
                } catch (IOException | ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private static void createAndSearchIndex(String searchType) throws IOException, ParseException {
        /*
        Indexer indexer = new Indexer(INDEX_DIR);

        if (!indexer.isIndexExists(searchType)) {
            try {
                int numIndexed = 0;
                long startTime = System.currentTimeMillis();


                // Create index based on search type
                switch (searchType) {
                    case "lyrics":
                        numIndexed = indexer.createIndex(LuceneConstants.LYRICS_DIR, new TextFileFilter(), "lyrics");
                        break;
                    case "songs":
                        numIndexed2 = indexer.createIndex(LuceneConstants.DATA_DIR, new TextFileFilter(), "songs");
                        break;
                    case "albums":
                        numIndexed3 = indexer.createIndex(LuceneConstants.ALBUM_DIR, new TextFileFilter(), "albums");
                        break;
                    default:
                        System.out.println("Invalid search type.");
                }

                long endTime = System.currentTimeMillis();
                System.out.println(numIndexed + " File(s) indexed, time taken: " +
                        (endTime - startTime) + " ms");
            } finally {
                indexer.close();
            }
        } else {
            System.out.println("Index for " + searchType + " already exists. Skipping indexing step.");
        }*/

        // Perform the search
        performSearch(searchType);
    }

    private static void performSearch(String searchType) throws IOException, ParseException {
        Main mainInstance = new Main();
        String searchDirectory = mainInstance.setSearchPath(searchType);
        System.out.println("in main / regularSearch search path set to: " + searchDirectory);

        List<SearchResult> searchResults;

        // get user query from GUI search-box:
        String query = GUI.getUserQuery();


        Searcher searcher = null;
        switch (searchType) {
            case "lyrics":
                searcher = new Searcher(LuceneConstants.LYRICS_INDEX_DIR, searchType);
                searchResults = searcher.search(query, searchType, searchDirectory);
                searcher.close();
                break;
            case "songs":
                searcher = new Searcher(LuceneConstants.SONGS_INDEX_DIR, searchType);
                searchResults = searcher.search(query, searchType, searchDirectory);
                searcher.close();
                break;
            case "albums":
                searcher = new Searcher(LuceneConstants.ALBUMS_INDEX_DIR, searchType);
                searchResults = searcher.search(query, searchType, searchDirectory);
                searcher.close();
                break;
            default:
                System.out.println("Invalid search type.");
                searchResults = searcher.search(null, null, null);
        }

        long startTime = System.currentTimeMillis();
        long endTime = System.currentTimeMillis();
        
        GUI.appendInfoText("Search completed in " + (endTime - startTime) + " ms");

        // note: once the list of all results has been created we then send them to the GUI so it can print them out
        for (SearchResult result : searchResults) {
            GUI.appendResultTextArea("File Path: " + result.getFilePath());
            System.out.println("in main.regSearch path set to: " + result.getFilePath());
            GUI.appendResultTextArea("Name: " + result.getName());
            GUI.appendResultTextArea("Artist: " + result.getArtist());

            // Check the search type for specific fields
            if ("songs".equals(searchType)) {
                GUI.appendResultTextArea("Album Name: " + result.getAlbumName());
            } else if ("albums".equals(searchType)) {
                GUI.appendResultTextArea("Year: " + result.getYear());
            }

            float score = result.getScore();    // get the score and send it
            GUI.appendResultTextArea("Score: " + Float.toString(score));
        }
    }

    private String setSearchPath(String searchType) {
        switch (searchType) {
            case "lyrics":
                searchPath = LuceneConstants.LYRICS_DIR;
                break;
            case "songs":
                searchPath = LuceneConstants.DATA_DIR;
                break;
            case "albums":
                searchPath = LuceneConstants.ALBUM_DIR;
                break;
            default:
                System.out.println("Invalid search type.");
                searchPath = ""; // or provide a default value
        }

        return searchPath;
    }

    private static void createAllIndices() throws IOException, ParseException {
        Indexer indexer = new Indexer(INDEX_DIR);

        if (indexer.isIndexExists("lyrics") && indexer.isIndexExists("songs") && indexer.isIndexExists("albums")) {
            System.out.println("Indices already exist. Skipping indexing step.");
        } else {
            try {
                long startTime = System.currentTimeMillis();

                int numLyricsIndexed = indexer.createIndex(LuceneConstants.LYRICS_DIR, new TextFileFilter(), "lyrics");
                int numSongsIndexed = indexer.createIndex(LuceneConstants.DATA_DIR, new TextFileFilter(), "songs");
                int numAlbumsIndexed = indexer.createIndex(LuceneConstants.ALBUM_DIR, new TextFileFilter(), "albums");

                long endTime = System.currentTimeMillis();

                System.out.println("Lyrics: " + numLyricsIndexed + " file(s) indexed.");
                System.out.println("Songs: " + numSongsIndexed + " file(s) indexed.");
                System.out.println("Albums: " + numAlbumsIndexed + " file(s) indexed.");
                System.out.println("Time taken: " + (endTime - startTime) + " ms");
            } finally {
                indexer.close(); // Ensure that the indexer is closed even if an exception occurs
            }
        }
    }
}
