package pt.inevo.encontra.btree;

import java.io.IOException;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Random;
import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.btree.BTree;
import jdbm.helper.CachePolicy;
import jdbm.helper.SoftCache;
import jdbm.recman.CacheRecordManager;
import pt.inevo.encontra.btree.comparators.NumberComparator;
import pt.inevo.encontra.index.IndexEntry;

public class jdbmBTree<O extends IndexEntry> implements IBTree<O> {

    private RecordManager recman;
    private BTree btree;
    private CachePolicy cache;
    private Class entryClass;
    private String path;

    public jdbmBTree(Class entryClass) {
        this("RM" + new Random().nextInt(), entryClass);
    }

    /**
     * Uses a default Double Comparator
     * @param path
     */
    public jdbmBTree(String path, Class entryClass) {
        this.path = path;
        try {
            cache = new SoftCache();
            recman = new CacheRecordManager(RecordManagerFactory.createRecordManager(path), cache);
            btree = BTree.createInstance(recman, new NumberComparator());
            this.entryClass = entryClass;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //must specify a comparator for the objects
    public jdbmBTree(String path, Class entryClass, Comparator comparator) {
        this.path = path;
        try {
            cache = new SoftCache();
            recman = new CacheRecordManager(RecordManagerFactory.createRecordManager(path), cache);
            this.entryClass = entryClass;

            long recid = recman.getNamedObject(path);
            if (recid != 0) { //reload the btree
                btree = BTree.load(recman, recid);
            } else { //create a new btree
                btree = BTree.createInstance(recman, comparator);
                recman.setNamedObject(path, btree.getRecid());
                recman.commit();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public int size() {
        return btree.size();
    }

    @Override
    public boolean insert(O entry) {
        try {
            if (btree.insert(entry.getKey(), entry, false) != null) {
                //there was a collision with the key, user must re-try
                return false;
            }
            recman.commit();
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
        
        return true;
    }

    @Override
    public boolean remove(O entry) {
        try {
            if (btree.find(entry.getKey()) != null) {
                btree.remove(entry.getKey());
            } else return false;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean hasEntry(O entry) {
        try {
            if (btree.find(entry.getKey()) == null) {
                return false;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public O find(Serializable key) {
        try {
            Object result = btree.find(key);
            if (result != null) {
                O entry = (O) entryClass.newInstance();
                entry.setKey(key);
                entry.setValue((Serializable)result);
                return entry;
            }
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public ITupleBrowser<O> browse() {
        try {
            return new jdbmTupleBrowser(btree.browse());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public ITupleBrowser<O> browse(Serializable key) {
        try {
            return new jdbmTupleBrowser(btree.browse(key));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
