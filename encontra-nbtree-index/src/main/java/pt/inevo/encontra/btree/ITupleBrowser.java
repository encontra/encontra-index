package pt.inevo.encontra.btree;

import pt.inevo.encontra.index.IndexEntry;

/**
 * Generic interface for the Tuple Browser. An object that allows to browse
 * through the tuples.
 * @author Ricardo
 * @param <O>
 */
public interface ITupleBrowser<O extends IndexEntry> {

    /**
     * @return the tuple or null if it doesn't exist
     */
    public ITuple<O> getNext();

    /**
     * @return the tuple or null if it doesn't exist
     */
    public ITuple<O> getPrevious();
}
