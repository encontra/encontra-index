package pt.inevo.encontra.lucene.index;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import pt.inevo.encontra.index.IndexEntry;


public class LuceneIndexEntry implements IndexEntry<String,Document> {
    private String key;
    private Document doc;

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void setKey(String key) {
        this.key=key;
    }

    @Override
    public Document getValue() {
        return doc;
    }
    
    @Override
    public void setValue(Document doc) {
        this.doc=doc;
    }
    
}
