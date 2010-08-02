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
import pt.inevo.encontra.btree.comparators.DoubleComparator;
import pt.inevo.encontra.index.IndexEntry;

public class jdbmBTree<O extends IndexEntry<? extends Serializable, ? extends Serializable>> extends IBTree<O> {

    private RecordManager recman;
    private BTree btree;
    private CachePolicy cache;

    public jdbmBTree() {
        this("RM" + new Random().nextInt());
    }

    /**
     * Uses a default Double Comparator
     * @param path
     */
    public jdbmBTree(String path) {
        super(path);
        try {
            cache = new SoftCache();
            recman = new CacheRecordManager(RecordManagerFactory.createRecordManager(path), cache);
            btree = BTree.createInstance(recman, new DoubleComparator());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //must specify a comparator for the objects
    public jdbmBTree(String path, Comparator comparator) {
        super(path);
        try {
            cache = new SoftCache();
            recman = new CacheRecordManager(RecordManagerFactory.createRecordManager(path), cache);

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
            if (btree.insert(entry.getKey(), entry.getValue(), false) != null) {
                //there was a collision with the key, user must re-try
                return false;
            }
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
    public Object find(Serializable key) {
        try {
            Object result = btree.find(key);
            if (result != null) {
                return result;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public ITupleBrowser browse() {
        try {
            return new jdbmTupleBrowser(btree.browse());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public ITupleBrowser browse(Serializable key) {
        try {
            return new jdbmTupleBrowser(btree.browse(key));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
