package pt.inevo.encontra.lucene.index;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.NoLockFactory;

import pt.inevo.encontra.index.*;
import pt.inevo.encontra.storage.IEntity;
import pt.inevo.encontra.storage.IEntry;

public class LuceneIndex<O extends IEntry> extends AbstractIndex<O> implements PersistentIndex<O> {

    protected String id;
    protected int iterator;
    protected IndexWriter writer;

    class LuceneIndexProvider implements EntryProvider<O> {

        IndexReader reader;

        LuceneIndexProvider() {
            try {
                reader = IndexReader.open(FSDirectory.open(new File(id), new NoLockFactory()));
            } catch (CorruptIndexException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public int size() {
            return reader.numDocs();
        }

        @Override
        public boolean contains(O entry) {

            LuceneIndexEntry indexEntry = (LuceneIndexEntry) getEntryFactory().createIndexEntry(entry);
            Term t = new Term("IDENTIFIER", indexEntry.getValue().get("IDENTIFIER"));
            TermQuery query = new TermQuery(t);

            Searcher searcher = new IndexSearcher(reader);
            try {
                TopDocs docs = searcher.search(query, 1);
                if (docs.totalHits != 0) {
                    return true;
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            return false;
        }

        @Override
        public List<O> getAll() {
            int numDocs = reader.numDocs();
            ArrayList<O> objs = new ArrayList<O>(numDocs);
            for (int i = 0; i < numDocs; i++) {
                try {
                    LuceneIndexEntry entry = new LuceneIndexEntry();
                    entry.setValue(reader.document(i));
                    objs.add((O) getEntryFactory().getObject(entry));
                } catch (CorruptIndexException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

            return objs;
        }

        @Override
        public void begin() {
            iterator = 0;
        }

        @Override
        public void end() {
            iterator = reader.numDocs();
        }

        @Override
        public boolean setCursor(O entry) {

            LuceneIndexEntry indexEntry = (LuceneIndexEntry) getEntryFactory().createIndexEntry(entry);
            Term t = new Term("IDENTIFIER", indexEntry.getValue().get("IDENTIFIER"));
            TermQuery query = new TermQuery(t);

            Searcher searcher = new IndexSearcher(reader);
            try {
                TopDocs docs = searcher.search(query, 1);
                if (docs.totalHits != 0) {
                    ScoreDoc[] sDocs = docs.scoreDocs;
                    iterator = sDocs[0].doc;
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return false;
        }

        @Override
        public O getFirst() {
            try {
                LuceneIndexEntry entry = new LuceneIndexEntry();
                entry.setValue(reader.document(0));
                return (O) getEntryFactory().getObject(entry);
            } catch (CorruptIndexException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            //the first was not found
            return null;
        }

        @Override
        public O getLast() {
            try {
                LuceneIndexEntry entry = new LuceneIndexEntry();
                entry.setValue(reader.document(reader.numDocs()));
                return (O) getEntryFactory().getObject(entry);
            } catch (CorruptIndexException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            //the first was not found
            return null;
        }

        @Override
        public O getEntry(Serializable key) {
            int numDocs = reader.numDocs();
            for (int i = 0; i < numDocs; i++) {
                try {
                    Document doc = reader.document(i);
                    String docKey = doc.get("IDENTIFIER");
                    if (docKey.equals(key)) {
                        LuceneIndexEntry entry = new LuceneIndexEntry();
                        entry.setValue(doc);
                        return (O) getEntryFactory().getObject(entry);
                    }
                } catch (CorruptIndexException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            //entry was not found in the index
            return null;
        }

        @Override
        public O getNext() {
            if (reader.numDocs() > iterator) {
                try {
                    LuceneIndexEntry entry = new LuceneIndexEntry();
                    entry.setValue(reader.document(iterator++));
                    return (O) getEntryFactory().getObject(entry);
                } catch (CorruptIndexException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            return null;
        }

        @Override
        public boolean hasNext() {
            if (reader.numDocs() > iterator) {
                return true;
            }
            return false;
        }

        @Override
        public O getPrevious() {
            if (iterator > 0) {
                try {
                    LuceneIndexEntry entry = new LuceneIndexEntry();
                    entry.setValue(reader.document(iterator--));
                    return (O) getEntryFactory().getObject(entry);
                } catch (CorruptIndexException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            return null;
        }

        @Override
        public boolean hasPrevious() {
            if (iterator > 0) {
                return true;
            }
            return false;
        }
    }

    public LuceneIndex(String id, Class<O> descriptorClass) {
        try {
            this.id = id;
            this.iterator = 0;
            writer = new IndexWriter(FSDirectory.open(new File(id)), new SimpleAnalyzer(), true, IndexWriter.MaxFieldLength.UNLIMITED);
            this.setEntryFactory(new LuceneIndexEntryFactory(descriptorClass));
        } catch (CorruptIndexException ex) {
            ex.printStackTrace();
        } catch (LockObtainFailedException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean insert(O obj) {
        try {
            LuceneIndexEntry entry = (LuceneIndexEntry) getEntryFactory().createIndexEntry(obj);
            writer.addDocument(entry.getValue());
            writer.commit();
        } catch (CorruptIndexException ex) {
            ex.printStackTrace();
            return false;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean remove(O obj) {

        LuceneIndexEntry indexEntry = (LuceneIndexEntry) getEntryFactory().createIndexEntry(obj);
        Term t = new Term("IDENTIFIER", indexEntry.getValue().get("IDENTIFIER"));
        TermQuery query = new TermQuery(t);
        try {
            writer.deleteDocuments(query);
            return true;
        } catch (CorruptIndexException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return false;
    }

    @Override
    public EntryProvider getEntryProvider() {
        return new LuceneIndexProvider();
    }

    @Override
    public boolean load(String path) {
        try {
            writer = new IndexWriter(FSDirectory.open(new File(id)), new SimpleAnalyzer(), true, IndexWriter.MaxFieldLength.UNLIMITED);
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean save(String path) {
        try {
            writer.commit();
            return true;
        } catch (CorruptIndexException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public IEntity get(Serializable id) {
        return null;
    }

    @Override
    public IEntity save(IEntity object) {
        return null;
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void save(IEntity... objects) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void delete(IEntity object) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
