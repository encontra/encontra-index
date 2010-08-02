package pt.inevo.encontra.btree;

import java.io.Serializable;

public interface ITupleBrowser<K extends Serializable, V extends Serializable> {

    /**
     * @return the tuple or null if it doesn't exist
     */
    public ITuple<K,V> getNext();

    /**
     * @return the tuple or null if it doesn't exist
     */
    public ITuple<K,V> getPrevious();
}
