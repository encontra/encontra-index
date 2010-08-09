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
    protected IndexReader reader;

    public LuceneIndex(String id, Class<O> descriptorClass) {
        try {
            this.id = id;
            this.iterator = 0;
            writer = new IndexWriter(FSDirectory.open(new File(id)), new SimpleAnalyzer(), true, IndexWriter.MaxFieldLength.UNLIMITED);
            reader = IndexReader.open(FSDirectory.open(new File(id), new NoLockFactory()));
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
    public int size() {
        return reader.numDocs();
    }

//    @Override
//    public O get(int idx) {
//        boolean hasDeletions = reader.hasDeletions();
//        // bugfix by Roman Kern
//        if (hasDeletions && reader.isDeleted(idx)) {
//            return null;
//        }
//        try {
//            Document doc=reader.document(idx);
//            LuceneIndexEntry entry=new LuceneIndexEntry();
//            entry.setValue(reader.document(idx));
//            return (O) getEntryFactory().getObject(entry);
//        } catch (IOException e) {
//            return null;
//        }
//    }
    @Override
    public boolean contains(O entry) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean remove(O obj) {
        //TO DO - remove the object from the index
        return true;
    }


    /*
     * Create a new object of type T
     * @param 
     * @return
     *
    protected T newAbstractObject(Object id){
    T instance= null;
    try {
    instance = Instantiator.<T>fromTemplate(this,0).instantiate();
    } catch (InvocationTargetException e) {
    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }
    //T instance=null;
    Class<T> tClass;
    Type type = getClass().getGenericSuperclass();
    if (type instanceof ParameterizedType) {
    ParameterizedType paramType = (ParameterizedType)type;
    tClass = (Class<T>) paramType.getActualTypeArguments()[0].getClass(); // Type parameter 0 is the AbstractObject class

    try {
    instance = tClass.newInstance();
    } catch (InstantiationException e) {
    e.printStackTrace();
    } catch (IllegalAccessException e) {
    e.printStackTrace();
    }
    }
    if(instance!=null) {
    instance.setId(id);
    }
    return instance;
    }*/
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
        throw new UnsupportedOperationException("Not supported yet.");
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
        if (iterator > 0){
            return true;
        }
        return false;
    }

    @Override
    public boolean load(String path) {
        try {
            writer = new IndexWriter(FSDirectory.open(new File(id)), new SimpleAnalyzer(), true, IndexWriter.MaxFieldLength.UNLIMITED);
            reader = IndexReader.open(FSDirectory.open(new File(id), new NoLockFactory()));
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
