package pt.inevo.encontra.btree;

import java.io.Serializable;

public interface ITuple<K extends Serializable,V extends Serializable> {

    /**
     *
     * @return
     */
    public K getKey();

    /**
     *
     * @return
     */
    public V getValue();

    /**
     *
     * @param key
     */
    public void setKey(K key);

    /**
     * 
     * @param value
     */
    public void setValue(V value);
}
