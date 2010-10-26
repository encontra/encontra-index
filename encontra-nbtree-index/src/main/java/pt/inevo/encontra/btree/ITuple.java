package pt.inevo.encontra.btree;

import java.io.Serializable;
import pt.inevo.encontra.index.IndexEntry;

/**
 * Tuple entry in the BTree.
 * @author Ricardo
 * @param <O>
 */
public interface ITuple<O extends IndexEntry> {

    /**
     * Gets the key of the tuple (entry).
     * @return
     */
    public Serializable getKey();

    /**
     * Gets the entry of the tuple.
     * @return
     */
    public Serializable getEntry();

    /**
     * Sets the key of the tuple.
     * @param key
     */
    public void setKey(Serializable key);

    /**
     * Sets the entry of the tuple.
     * @param value
     */
    public void setEntry(Serializable value);
}
