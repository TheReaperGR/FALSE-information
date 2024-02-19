import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Searcher {
    private IndexSearcher indexSearcher;
    private QueryParser queryParser;

    public Searcher(String indexDir, String searchType) throws IOException {
        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexDir)));
        indexSearcher = new IndexSearcher(reader);
        queryParser = new MultiFieldQueryParser(
                determineFieldsForSearchType(searchType),
                new StandardAnalyzer());
    }

    public List<SearchResult> search(String queryStr, String searchType, String searchPath) throws IOException {
        List<SearchResult> results = new ArrayList<>();

        String[] fields = determineFieldsForSearchType(searchType);

        Query query = null;
        try {
            query = queryParser.parse(queryStr);
        } catch (org.apache.lucene.queryparser.classic.ParseException e) {
            throw new IOException("Error parsing query: " + e.getMessage(), e);
        }

        TopDocs topDocs = indexSearcher.search(query, LuceneConstants.MAX_SEARCH);
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;

        for (ScoreDoc scoreDoc : scoreDocs) {
            Document doc = indexSearcher.doc(scoreDoc.doc);
            SearchResult result = createSearchResult(doc, fields, searchPath, scoreDoc.score);
            results.add(result);
        }

        return results;
    }

    public SearchResult createSearchResult(Document doc, String[] fields, String searchPath, float score) {
        SearchResult result = new SearchResult(doc.get(LuceneConstants.FILE_PATH), null, score, null, searchPath);
        result.setFilePath(doc.get(LuceneConstants.FILE_PATH));
        result.setName(doc.get(LuceneConstants.FILE_NAME));

        for (String field : fields) {
            switch (field) {
                case LuceneConstants.ARTIST:
                    result.setArtist(doc.get(LuceneConstants.ARTIST));
                    break;
                case LuceneConstants.SONG_NAME:
                    result.setSongName(doc.get(LuceneConstants.SONG_NAME));
                    break;
                case LuceneConstants.ALBUM_NAME:
                    result.setAlbumName(doc.get(LuceneConstants.ALBUM_NAME));
                    break;
                case LuceneConstants.YEAR:
                    result.setYear(Integer.parseInt(doc.get(LuceneConstants.YEAR)));
                    break;
                // Add other fields as needed
            }
        }

        return result;
    }

    private String[] determineFieldsForSearchType(String searchType) {
        switch (searchType) {
            case "lyrics":
                return LuceneConstants.LYRICS_SEARCH_FIELDS;
            case "songs":
                return LuceneConstants.SONGS_SEARCH_FIELDS;
            case "albums":
                return LuceneConstants.ALBUMS_SEARCH_FIELDS;
            default:
                throw new IllegalArgumentException("Invalid search type: " + searchType);
        }
    }

    public void close() throws IOException {
        indexSearcher.getIndexReader().close();
    }
}
