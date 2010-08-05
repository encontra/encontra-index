package pt.inevo.encontra.btree;

import java.io.Serializable;
import pt.inevo.encontra.index.IndexEntry;

/**
 * Generic IBTree to be extended by diferent IBTree implementations.
 * @param <O> the type of the entries to this IBTree
 */
public interface IBTree<O extends IndexEntry & Serializable> {

    /**
     *
     * @param entry
     * @return
     */
    public abstract boolean insert(O entry);

    /**
     *
     * @param entry
     * @return
     */
    public abstract boolean remove(O entry);

    /**
     * 
     * @return
     */
    public abstract int size();

    /**
     *
     * @param entry
     * @return
     */
    public abstract boolean hasEntry(O entry);

    /**
     * 
     * @param key
     * @return
     */
    public abstract O find(Serializable key);

    /**
     * Get a browser initially positioned at the beginning of the IBTree.
     * @return
     */
    public abstract ITupleBrowser<O> browse();

    /**
     * Get a browser initially positioned just before the given key.
     * @param key
     * @return
     */
    public abstract ITupleBrowser<O> browse(Serializable key);
}
