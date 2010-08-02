package pt.inevo.encontra.nbtree.index;

import java.io.Serializable;
import java.util.List;
import pt.inevo.encontra.btree.IBTree;
import pt.inevo.encontra.btree.jdbmBTree;
import pt.inevo.encontra.index.AbstractIndex;
import pt.inevo.encontra.storage.IEntity;
import pt.inevo.encontra.storage.IEntry;

public class BTreeIndex<E extends IEntry> extends AbstractIndex<E> {

    protected IBTree<NBTreeIndexEntry> index;

    public BTreeIndex(Class objectClass) {
        index = new jdbmBTree();
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
        throw new UnsupportedOperationException("Does this make sense?");
    }

    @Override
    public boolean contains(E entry) {
        NBTreeIndexEntry indexEntry = (NBTreeIndexEntry) getEntryFactory().createIndexEntry(entry);
        return index.hasEntry(indexEntry);
    }

    @Override
    public List<E> getAll() {
        throw new UnsupportedOperationException("Does this make sense?");
    }

    @Override
    public IEntity get(Serializable id) {

        Object o = index.find(id);
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
