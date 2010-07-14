package pt.inevo.encontra.lucene.index;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import pt.inevo.encontra.index.IndexEntry;


public class LuceneIndexEntry implements IndexEntry<String,Document> {
    private Document doc;

    @Override
    public String getKey() {
        return doc.getField("IDENTIFIER").stringValue();
    }

    @Override
    public void setKey(String key) {
        doc.add(new Field("IDENTIFIER", key, Field.Store.YES, Field.Index.NO));
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
