import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.lucene.queryparser.classic.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final String INDEX_DIR = "E:\\Intellij IDEA projects\\false_v1\\Data";
    private static final String DATA_DIR = "E:\\Intellij IDEA projects\\false_v1\\Data\\songsOutput";
    private static final String ALBUM_DIR = "E:\\Intellij IDEA projects\\false_v1\\Data\\albumOutput";
    private static final String LYRICS_DIR = "E:\\Intellij IDEA projects\\false_v1\\Data\\lyricsOutput";
    private static String searchPath;
    private static String searchType;

    public static void main(String[] args) {
        //open the GUI window:
        GUI.renderGUI();
        searchType = GUI.getSelectedMode();

        try {
            //parse all the CSVs
            Parser.parseAll();
            //create indeces
            createIndex(searchType);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CsvValidationException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private static void createIndex(String searchType) throws IOException, ParseException {
        Indexer indexer = new Indexer(INDEX_DIR);

        if (indexer.isIndexExists()) {
            System.out.println("Index already exists. Skipping indexing step.");
        } else {
            int numIndexed = 0;
            long startTime = System.currentTimeMillis();
            if (!(indexer.isIndexExists())) {
                switch (searchType) {
                    case "lyrics":
                        numIndexed = indexer.createIndex(LYRICS_DIR, new TextFileFilter());
                        break;
                    case "song":
                        numIndexed = indexer.createIndex(DATA_DIR, new TextFileFilter());
                        break;
                    case "album":
                        numIndexed = indexer.createIndex(ALBUM_DIR, new TextFileFilter());
                        break;
                    default:
                        System.out.println("Invalid search type.");
                        return;
                }
            }
            long endTime = System.currentTimeMillis();
            System.out.println(numIndexed + " File(s) indexed, time taken: " +
                    (endTime - startTime) + " ms");
        }

        indexer.close();
    }

    public static List<SearchResult> regularSearch(String searchType) throws IOException, ParseException {

        String searchDirectory = setSearchPath();

        Searcher searcher = new Searcher(INDEX_DIR);
        long startTime = System.currentTimeMillis();

        //get user query from gui search-box:
        String query = GUI.getUserQuery();

        List<SearchResult> searchResults = searcher.search(query, searchType, searchDirectory);

        long endTime = System.currentTimeMillis();
        searcher.close();
        System.out.println("Search completed in " + (endTime - startTime) + " ms");

        return searchResults;

        //note: once the list of all results has been created we then return them to the GUI so it can print them out
        /*
        for (SearchResult result : searchResults) {
            System.out.println("File Path: " + result.getFilePath());
            System.out.println("Name: " + result.getName());
            System.out.println("Artist: " + result.getArtist());

            // Check the search type for specific fields
            if ("song".equals(searchType)) {
                System.out.println("Album Name: " + result.getAlbumName());
            } else if ("album".equals(searchType)) {
                System.out.println("Year: " + result.getYear());
            }

            System.out.printf("Score: %.2f\n", result.getScore());
            System.out.println();
        }


         */
    }

    private static String setSearchPath(){
        switch (searchType) {
            case "lyrics":
                searchPath = LYRICS_DIR;

                break;
            case "song":
                searchPath = DATA_DIR;
                break;
            case "album":
                searchPath = ALBUM_DIR;
                break;
            default:
                System.out.println("Invalid search type.");
        }

        return searchPath;
    }



}