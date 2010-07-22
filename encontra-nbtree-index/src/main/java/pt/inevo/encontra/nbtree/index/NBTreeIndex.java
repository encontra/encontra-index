package pt.inevo.encontra.nbtree.index;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import pt.inevo.encontra.index.AbstractIndex;
import pt.inevo.encontra.index.IndexEntry;
import pt.inevo.encontra.nbtree.NBTree;
import pt.inevo.encontra.nbtree.exceptions.NBTreeException;
import pt.inevo.encontra.query.Query.QueryType;
import pt.inevo.encontra.storage.IEntity;
import pt.inevo.encontra.storage.IEntry;

/**
 * NBTreeIndex - An index that uses an NBTree indexing structure for indexing
 * the specified entries.
 * @author ricardo
 */
public class NBTreeIndex<E extends IEntry> extends AbstractIndex<E> {

    protected NBTree<NBTreeIndexEntry> nbtree;
    protected int size;
    protected HashMap<Integer, IndexEntry> elements;
    protected static QueryType[] supportedTypes = new QueryType[]{QueryType.KNN};

    public NBTreeIndex(Class objectClass) {
        try {
            nbtree = new NBTree<NBTreeIndexEntry>();
        } catch (NBTreeException ex) {
            ex.printStackTrace();
        }
        this.setEntryFactory(new NBTreeIndexEntryFactory(objectClass));
        elements = new HashMap<Integer, IndexEntry>();
    }

    @Override
    public boolean insert(E entry) {
        try {
            NBTreeIndexEntry indexEntry = (NBTreeIndexEntry) getEntryFactory().createIndexEntry(entry);
            nbtree.addEntry(indexEntry);
            elements.put(size + 1, indexEntry);
            size++;
            return true;
        } catch (NBTreeException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean remove(E entry) {
        try {
            NBTreeIndexEntry indexEntry = (NBTreeIndexEntry) getEntryFactory().createIndexEntry(entry);
            nbtree.removeEntry(indexEntry);
            return true;
        } catch (NBTreeException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public E get(int i) {
        return (E) getEntryFactory().getObject(elements.get(i));
    }

    @Override
    public boolean contains(E object) {
        for (int i = 0; i < elements.size(); i++) {
            if (((E) getEntryFactory().getObject(elements.get(i))).equals(object)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public List<E> getAll() {
        List<E> list = new ArrayList<E>();
        for (IndexEntry entry : elements.values()) {
            list.add((E) getEntryFactory().getObject(entry));
        }
        return list;
    }

    @Override
    public IEntity get(Serializable id) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
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
