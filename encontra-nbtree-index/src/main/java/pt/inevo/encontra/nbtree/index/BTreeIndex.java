package pt.inevo.encontra.nbtree.index;

import pt.inevo.encontra.btree.IBTree;
import pt.inevo.encontra.btree.ITuple;
import pt.inevo.encontra.btree.ITupleBrowser;
import pt.inevo.encontra.btree.jdbmBTree;
import pt.inevo.encontra.index.AbstractIndex;
import pt.inevo.encontra.index.EntryProvider;
import pt.inevo.encontra.index.IndexEntry;
import pt.inevo.encontra.query.criteria.StorageCriteria;
import pt.inevo.encontra.storage.IEntity;
import pt.inevo.encontra.storage.IEntry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Generic Index using a BTree as the indexing structure.
 * Uses the implemented jdbmBTree.
 * @author Ricardo
 * @param <E>
 */
public class BTreeIndex<E extends IEntry> extends AbstractIndex<E> {

    protected IBTree index;

    class BTreeIndexProvider implements EntryProvider<E> {

        protected ITupleBrowser entryBrowser;

        @Override
        public int size() {
            return index.size();
        }

        @Override
        public boolean contains(E entry) {
            NBTreeIndexEntry indexEntry = (NBTreeIndexEntry) getEntryFactory().createIndexEntry(entry);
            return index.hasEntry(indexEntry);
        }

        @Override
        public List<E> getAll() {
            ArrayList<E> entries = new ArrayList<E>();
            ITupleBrowser tempBrowser = index.browse();

            for (ITuple tuple = tempBrowser.getNext(); tuple != null; tuple = tempBrowser.getNext()) {
                IndexEntry e = (IndexEntry) tuple.getEntry();
                entries.add((E) getEntryFactory().getObject(e));
            }

            return entries;
        }

        @Override
        @SuppressWarnings("empty-statement")
        public void begin() {
            entryBrowser = index.browse();
        }

        @Override
        @SuppressWarnings("empty-statement")
        public void end() {
            entryBrowser = index.browse();
            while (entryBrowser.getNext() != null);
        }

        @Override
        public E getFirst() {
            return (E) getEntryFactory().getObject((IndexEntry) index.browse().getNext().getEntry());
        }

        @Override
        @SuppressWarnings("empty-statement")
        public E getLast() {
            ITupleBrowser tempBrowser = index.browse();
            while (tempBrowser.getNext() != null);
            return (E) getEntryFactory().getObject((IndexEntry) tempBrowser.getPrevious().getEntry());
        }

        @Override
        public E getNext() {
            if (entryBrowser == null) {
                entryBrowser = index.browse();
            }
            return (E) getEntryFactory().getObject((IndexEntry) entryBrowser.getNext().getEntry());
        }

        @Override
        public boolean hasNext() {
            if (entryBrowser == null) {
                entryBrowser = index.browse();
            }

            if (entryBrowser.getNext() != null) {
                entryBrowser.getPrevious();
                return true;
            } else {
                entryBrowser.getPrevious();
                return false;
            }
        }

        @Override
        public E getPrevious() {
            if (entryBrowser == null) {
                entryBrowser = index.browse();
            }
            return (E) getEntryFactory().getObject((IndexEntry) entryBrowser.getPrevious().getEntry());
        }

        @Override
        public boolean hasPrevious() {
            if (entryBrowser == null) {
                entryBrowser = index.browse();
            }
            if (entryBrowser.getPrevious() != null) {
                entryBrowser.getNext();
                return true;
            } else {
                entryBrowser.getNext();
                return false;
            }
        }

        @Override
        public boolean setCursor(E entry) {
            NBTreeIndexEntry indexEntry = (NBTreeIndexEntry) getEntryFactory().createIndexEntry(entry);
            entryBrowser = index.browse(indexEntry.getKey());
            return true;
        }

        @Override
        public E getEntry(Serializable key) {
            IndexEntry entry = index.find(key);
            return (E) getEntryFactory().getObject(entry);
        }
    }

    public BTreeIndex(Class objectClass) {
        index = new jdbmBTree(NBTreeIndexEntry.class);
        this.setEntryFactory(new NBTreeIndexEntryFactory(objectClass));
    }

    public BTreeIndex(String path, String name, Class objectClass) {
        index = new jdbmBTree(path, name, objectClass);
        this.setEntryFactory(new NBTreeIndexEntryFactory(objectClass));
    }

    public BTreeIndex(String path, Class objectClass) {
        index = new jdbmBTree(path, NBTreeIndexEntry.class);
        this.setEntryFactory(new NBTreeIndexEntryFactory(objectClass));
    }

    @Override
    public boolean insert(E entry) {
        NBTreeIndexEntry indexEntry = (NBTreeIndexEntry) getEntryFactory().createIndexEntry(entry);
        return index.insert(indexEntry);
    }

    @Override
    public boolean remove(E entry) {
        NBTreeIndexEntry indexEntry = (NBTreeIndexEntry) getEntryFactory().createIndexEntry(entry);
        return index.remove(indexEntry);
    }

    @Override
    public EntryProvider getEntryProvider() {
        return new BTreeIndexProvider();
    }

    public void close() {
        index.close();
    }

    @Override
    public IEntity get(Serializable id) {

        IndexEntry ientry = index.find(id);
        IEntry entry = getEntryFactory().getObject(ientry);
        //TO DO - must convert to the IEntity

        return null;
    }

    @Override
    public boolean validate(Serializable id, StorageCriteria criteria) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Serializable> getValidIds(StorageCriteria criteria){
        return null;
    }

    @Override
    public IEntity save(IEntity object) {
        //TO DO
        return null;
    }

    @Override
    public void save(IEntity... objects) {
        //TO DO
    }

    @Override
    public void delete(IEntity object) {
        //TO DO 
    }
}
