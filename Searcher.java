import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Searcher {
    private IndexSearcher indexSearcher;
    private Directory indexDirectory;
    private QueryParser queryParser;
    private Query query;

    public Searcher(String indexDirectoryPath) throws IOException {
        Path indexPath = Paths.get(indexDirectoryPath);
        indexDirectory = FSDirectory.open(indexPath);
        indexSearcher = new IndexSearcher(DirectoryReader.open(indexDirectory));
        queryParser = new QueryParser(LuceneConstants.CONTENTS, new StandardAnalyzer());
    }

    public List<SearchResult> search(String searchQuery, String searchType, String searchDirectory) throws IOException, ParseException {
        String fieldToSearch;
        switch (searchType) {
            case "lyrics":
                fieldToSearch = LuceneConstants.LYRICS;
                break;
            case "song":
                fieldToSearch = LuceneConstants.SONG_NAME;
                break;
            case "album":
                fieldToSearch = LuceneConstants.ALBUM_DIR;
                break;
            default:
                throw new IllegalArgumentException("Invalid search type: " + searchType);
        }

        query = queryParser.parse(String.format("%s:%s", fieldToSearch, QueryParser.escape(searchQuery)));
        System.out.println("Query: " + query.toString());

        TopDocs hits = indexSearcher.search(query, LuceneConstants.MAX_SEARCH);

        List<SearchResult> results = new ArrayList<>();
        for (ScoreDoc scoreDoc : hits.scoreDocs) {
            Document doc = indexSearcher.doc(scoreDoc.doc);
            results.add(new SearchResult(
                    doc.get(LuceneConstants.FILE_PATH),
                    doc.get(fieldToSearch),
                    scoreDoc.score,
                    searchType,
                    searchDirectory
            ));
        }

        return results;
    }

    public void close() throws IOException {
        indexSearcher.getIndexReader().close();
        indexDirectory.close();
    }
}
