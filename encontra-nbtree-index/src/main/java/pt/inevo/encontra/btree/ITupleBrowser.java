package pt.inevo.encontra.btree;

import java.io.Serializable;
import pt.inevo.encontra.index.IndexEntry;

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
