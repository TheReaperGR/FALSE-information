import org.apache.lucene.LucenePackage;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.lucene.index.DirectoryReader;
import java.io.BufferedReader;
import java.io.FileReader;

public class Indexer {
    private Directory albumsDirectory;
    private IndexWriter albumsWriter;

    private Directory lyricsDirectory;
    private IndexWriter lyricsWriter;

    private Directory songsDirectory;
    private IndexWriter songsWriter;

    public Indexer(String indexDirectoryPath) throws IOException {
        // Create directories for albums, lyrics, and songs
        Path albumsPath = Paths.get(indexDirectoryPath, "albums");
        Path lyricsPath = Paths.get(indexDirectoryPath, "lyrics");
        Path songsPath = Paths.get(indexDirectoryPath, "songs");

        createDirectoryIfNotExists(albumsPath);
        createDirectoryIfNotExists(lyricsPath);
        createDirectoryIfNotExists(songsPath);

        // Initialize directories and writers for each type
        albumsDirectory = FSDirectory.open(albumsPath);
        albumsWriter = createWriter(albumsDirectory);

        lyricsDirectory = FSDirectory.open(lyricsPath);
        lyricsWriter = createWriter(lyricsDirectory);

        songsDirectory = FSDirectory.open(songsPath);
        songsWriter = createWriter(songsDirectory);
    }

    private IndexWriter createWriter(Directory directory) throws IOException {
        IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
        return new IndexWriter(directory, config);
    }

    private void createDirectoryIfNotExists(Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createDirectory(path);
        }
    }

    // Other methods remain the same

    public void close() throws IOException {
        albumsWriter.close();
        lyricsWriter.close();
        songsWriter.close();
    }

    private Document getDocument(File file) throws IOException {
        Document document = new Document();
        // index file contents
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append(" ");
            }

            // Use TextField for text content
            Field contentField = new TextField(LuceneConstants.CONTENTS, content.toString(), Field.Store.YES);
            // Use StringField for file name and file path
            Field fileNameField = new StringField(LuceneConstants.FILE_NAME, file.getName(), Field.Store.YES);
            Field filePathField = new StringField(LuceneConstants.FILE_PATH, file.getCanonicalPath(), Field.Store.YES);

            document.add(contentField);
            document.add(fileNameField);
            document.add(filePathField);
        }
        return document;
    }

    private void indexFile(IndexWriter writer, File file) throws IOException {
        System.out.println("Indexing " + file.getCanonicalPath());
        Document document = getDocument(file);
        writer.addDocument(document);
    }

    // Modify the createIndex method to accept the type of data to be indexed
    public int createIndex(String dataDirPath, FileFilter filter, String dataType) throws IOException {
        File[] files = new File(dataDirPath).listFiles();
        IndexWriter writer = null;
        if (files != null) {
            for (File file : files) {
                if (!file.isDirectory() && !file.isHidden() && file.exists() && file.canRead() && filter.accept(file)) {
                    // Choose the appropriate writer based on the data type
                    switch (dataType) {
                        case "albums":
                            writer = albumsWriter;
                            break;
                        case "lyrics":
                            writer = lyricsWriter;
                            break;
                        case "songs":
                            writer = songsWriter;
                            break;
                        default:
                            throw new IllegalArgumentException("Invalid data type: " + dataType);
                    }
                    indexFile(writer, file);
                }
            }
        }
        return writer.numRamDocs();
    }

    // Other methods remain the same

    public boolean isIndexExists(String dataType) throws IOException {
        switch (dataType) {
            case "albums":
                return DirectoryReader.indexExists(albumsDirectory);
            case "lyrics":
                return DirectoryReader.indexExists(lyricsDirectory);
            case "songs":
                return DirectoryReader.indexExists(songsDirectory);
            default:
                throw new IllegalArgumentException("Invalid data type: " + dataType);
        }
    }
}
