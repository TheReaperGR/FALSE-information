import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.lucene.queryparser.classic.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final String INDEX_DIR = "C:\\Users\\liona\\IdeaProjects\\untitled\\src\\";
    private static final String DATA_DIR = "C:\\Users\\liona\\IdeaProjects\\untitled\\src\\songsOutput";
    private static final String ALBUM_DIR = "C:\\Users\\liona\\IdeaProjects\\untitled\\src\\AlbumOutput";
    private static final String LYRICS_DIR = "C:\\Users\\liona\\IdeaProjects\\untitled\\src\\LyricsOutput";
    private static final String CSV_FILE_PATH2 = "C:\\Users\\liona\\IdeaProjects\\untitled\\src\\albums.csv";
    private static final String CSV_FILE_PATH = "C:\\Users\\liona\\IdeaProjects\\untitled\\src\\songs.csv";
    private static final String CSV_FILE_PATH3 = "C:\\Users\\liona\\IdeaProjects\\untitled\\src\\lyrics.csv";

    public static void main(String[] args) {
        try {
            Main converter = new Main();
            converter.parseSongsAndCreateTxtFiles();
            converter.parseAlbumCSVAndCreateTxtFiles();
            converter.parselyricsCSVAndCreateTxtFiles();

            // Allow the user to choose what to search for
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter 'lyrics' to search for lyrics, 'song' to search for songs, or 'album' to search for albums:");
            String searchType = scanner.nextLine().toLowerCase();

            // Validate the user input
            if ("lyrics".equals(searchType) || "song".equals(searchType) || "album".equals(searchType)) {
                converter.createIndexAndSearch(searchType);
            } else {
                System.out.println("Invalid input. Please enter 'lyrics', 'song', or 'album'.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (CsvValidationException | ParseException e) {
            throw new RuntimeException(e);
        }

        gui applicationGUI = new gui();
        applicationGUI.displayGUI();
    }



    private void parselyricsCSVAndCreateTxtFiles() throws IOException, CsvValidationException {
        try (CSVReader reader = new CSVReader(new FileReader(CSV_FILE_PATH3))) {
            String[] header = reader.readNext();
            String[] line;
            int lineNumber = 1;

            while ((line = reader.readNext()) != null) {
                // Adjust the file path to store in the "LyricsOutput" directory
                String txtFileName = LYRICS_DIR + "/output_" + lineNumber + ".txt";
                createTxtFileLyrics(txtFileName, header, line);
                lineNumber++;
            }

            System.out.println("Lyrics Conversion completed successfully.");
        }
    }

    private void createTxtFileLyrics(String fileName, String[] header, String[] data) {
        try (FileWriter writer = new FileWriter(fileName)) {
            // Write data in the specified format
            for (int i = 0; i < data.length; i++) {
                // If the column is the lyrics column, handle the formatting
                if (header[i].equalsIgnoreCase("Lyrics")) {
                    // Remove everything between square brackets and colons
                    String lyrics = data[i].replaceAll("\\[.*?\\]|:", "").trim();
                    writer.write(lyrics);
                } else {
                    writer.write(data[i]);
                }

                if (i < data.length - 1) {
                    writer.write(",");
                }
            }
            writer.write("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseAlbumCSVAndCreateTxtFiles() throws IOException, CsvValidationException {
        try (CSVReader reader = new CSVReader(new FileReader(CSV_FILE_PATH2))) {
            String[] header = reader.readNext();
            String[] line;
            int lineNumber = 1;

            while ((line = reader.readNext()) != null) {
                String txtFileName = ALBUM_DIR + "/album_output_" + lineNumber + ".txt";
                createTxtFile(txtFileName, header, line);
                lineNumber++;
            }

            System.out.println("Albums conversion completed successfully.");
        }
    }

    private void parseSongsAndCreateTxtFiles() throws IOException, CsvValidationException {
        try (CSVReader reader = new CSVReader(new FileReader(CSV_FILE_PATH))) {
            String[] header = reader.readNext(); // Assuming the first line is a header, adjust if needed
            String[] line;
            int lineNumber = 1;

            while ((line = reader.readNext()) != null) {
                String txtFileName = DATA_DIR + "/output_" + lineNumber + ".txt";
                createTxtFile(txtFileName, header, line);
                lineNumber++;
            }

            System.out.println("Song Conversion completed successfully.");
        }
    }

    private void createTxtFile(String fileName, String[] header, String[] data) {
        try (FileWriter writer = new FileWriter(fileName)) {
            // Write data in the specified format
            for (int i = 0; i < data.length; i++) {
                writer.write(data[i]);
                if (i < data.length - 1) {
                    writer.write(",");
                }
            }
            writer.write("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createIndexAndSearch(String searchType) throws IOException, ParseException {
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


            // For lyrics and songs, proceed with the regular search
            regularSearch(searchType,ALBUM_DIR);

    }



    private void regularSearch(String searchType, String searchDirectory) throws IOException, ParseException {
        Searcher searcher = new Searcher(INDEX_DIR);
        long startTime = System.currentTimeMillis();

        // Allow the user to enter the search query
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your search query:");
        String query = scanner.nextLine();

        List<SearchResult> searchResults = searcher.search(query, searchType, searchDirectory);
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

        long endTime = System.currentTimeMillis();
        searcher.close();
        System.out.println("Search completed in " + (endTime - startTime) + " ms");
    }
}