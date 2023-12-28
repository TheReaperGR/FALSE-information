import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Indexer {
    private Directory directory;
    private IndexWriter writer;

    public Indexer(String indexDirectoryPath) throws IOException {
        // this directory will contain the indexes
        Path indexPath = Paths.get(indexDirectoryPath);
        if (!Files.exists(indexPath)) {
            Files.createDirectory(indexPath);
        }
        // Path indexPath = Files.createTempDirectory(indexDirectoryPath);
        directory = FSDirectory.open(indexPath);

        // create the indexer
        IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
        writer = new IndexWriter(directory, config);
    }

    public void close() throws CorruptIndexException, IOException {
        writer.close();
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

    private void indexFile(File file) throws IOException {
        System.out.println("Indexing " + file.getCanonicalPath());
        Document document = getDocument(file);
        writer.addDocument(document);
    }

    public int createIndex(String dataDirPath, FileFilter filter) throws IOException {
        File[] files = new File(dataDirPath).listFiles();
        if (files != null) {
            for (File file : files) {
                if (!file.isDirectory() && !file.isHidden() && file.exists() && file.canRead() && filter.accept(file)) {
                    indexFile(file);
                }
            }
        }
        return writer.numRamDocs();
    }

    public boolean isIndexExists() throws IOException {
        return DirectoryReader.indexExists(directory);
    }
}
