package pt.inevo.encontra.nbtree.index;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import pt.inevo.encontra.btree.IBTree;
import pt.inevo.encontra.btree.ITuple;
import pt.inevo.encontra.btree.ITupleBrowser;
import pt.inevo.encontra.btree.jdbmBTree;
import pt.inevo.encontra.index.AbstractIndex;
import pt.inevo.encontra.index.IndexEntry;
import pt.inevo.encontra.storage.IEntity;
import pt.inevo.encontra.storage.IEntry;

public class BTreeIndex<E extends IEntry> extends AbstractIndex<E> {

    protected IBTree index;

    public BTreeIndex(Class objectClass) {
        index = new jdbmBTree(NBTreeIndexEntry.class);
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
    public int size() {
        return index.size();
    }

    @Override
    public E get(int idx) {
        IndexEntry entry = index.find(idx);
        return (E) getEntryFactory().getObject(entry);
    }

    @Override
    public boolean contains(E entry) {
        NBTreeIndexEntry indexEntry = (NBTreeIndexEntry) getEntryFactory().createIndexEntry(entry);
        return index.hasEntry(indexEntry);
    }

    @Override
    public List<E> getAll() {
        ArrayList<E> entries = new ArrayList<E>();
        ITupleBrowser browser = index.browse();

        for(ITuple tuple = browser.getNext(); tuple != null; tuple = browser.getNext()){
            IndexEntry e = (IndexEntry) tuple.getEntry();
            entries.add((E) getEntryFactory().getObject(e));
        }

        return entries;
    }

    @Override
    public IEntity get(Serializable id) {

        IndexEntry ientry = index.find(id);
        IEntry entry = getEntryFactory().getObject(ientry);
        //TO DO - must conver to the IEntity

        return null;
    }

    public IBTree getBTree() {
        return index;
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
