package pt.inevo.encontra.btree;

import java.io.Serializable;
import pt.inevo.encontra.index.IndexEntry;

public interface ITuple<O extends IndexEntry> {

    /**
     *
     * @return
     */
    public Serializable getKey();

    /**
     *
     * @return
     */
    public Serializable getEntry();

    /**
     *
     * @param key
     */
    public void setKey(Serializable key);

    /**
     * 
     * @param value
     */
    public void setEntry(Serializable value);
}
