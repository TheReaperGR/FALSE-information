import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Parser {
    private static final String INDEX_DIR = "E:\\Intellij IDEA projects\\false_v1\\Data";
   /* private static final String DATA_DIR = "E:\\Intellij IDEA projects\\false_v1\\Data\\songsOutput";
    private static final String ALBUM_DIR = "E:\\Intellij IDEA projects\\false_v1\\Data\\albumOutput";
    private static final String LYRICS_DIR = "E:\\Intellij IDEA projects\\false_v1\\Data\\lyricsOutput";*/
    private static final String CSV_FILE_PATH2 = "E:\\Intellij IDEA projects\\false_v1\\Data\\albums.csv";
    private static final String CSV_FILE_PATH = "E:\\Intellij IDEA projects\\false_v1\\Data\\songs.csv";
    private static final String CSV_FILE_PATH3 = "E:\\Intellij IDEA projects\\false_v1\\Data\\lyrics.csv";
    //CSV PARSER:
    private static void parseLyrics() throws IOException, CsvValidationException {
        try (CSVReader reader = new CSVReader(new FileReader(CSV_FILE_PATH3))) {
            String[] header = reader.readNext();
            String[] line;
            int lineNumber = 1;

            while ((line = reader.readNext()) != null) {
                // Adjust the file path to store in the "LyricsOutput" directory
                String txtFileName = LuceneConstants.LYRICS_DIR + "/output_" + lineNumber + ".txt";
                createLyricsTxt(txtFileName, header, line);
                lineNumber++;
            }

            System.out.println("Lyrics Conversion completed successfully.");
        }
    }
    private static void createLyricsTxt(String fileName, String[] header, String[] data) {
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
    private static void parseAlbums() throws IOException, CsvValidationException {
        try (CSVReader reader = new CSVReader(new FileReader(CSV_FILE_PATH2))) {
            String[] header = reader.readNext();
            String[] line;
            int lineNumber = 1;

            while ((line = reader.readNext()) != null) {
                String txtFileName = LuceneConstants.ALBUM_DIR + "/album_output_" + lineNumber + ".txt";
                createTxtFile(txtFileName, header, line);
                lineNumber++;
            }

            System.out.println("Albums conversion completed successfully.");
        }
    }
    private static void parseSongs() throws IOException, CsvValidationException {
        try (CSVReader reader = new CSVReader(new FileReader(CSV_FILE_PATH))) {
            String[] header = reader.readNext(); // Assuming the first line is a header, adjust if needed
            String[] line;
            int lineNumber = 1;

            while ((line = reader.readNext()) != null) {
                String txtFileName = LuceneConstants.DATA_DIR + "/output_" + lineNumber + ".txt";
                createTxtFile(txtFileName, header, line);
                lineNumber++;
            }

            System.out.println("Song Conversion completed successfully.");
        }
    }
    private static void createTxtFile(String fileName, String[] header, String[] data) {
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

    public static void parseAll() throws CsvValidationException, IOException {
        parseLyrics();
        parseAlbums();
        parseSongs();
    }
}
