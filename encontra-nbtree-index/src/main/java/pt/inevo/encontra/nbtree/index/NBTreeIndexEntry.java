package pt.inevo.encontra.nbtree.index;

import java.io.Serializable;
import pt.inevo.encontra.descriptors.Descriptor;
import pt.inevo.encontra.index.IndexEntry;

/**
 * An entry in the NBTree structure.
 * @author Ricardo
 * @param <K>
 * @param <V>
 */
public class NBTreeIndexEntry<K extends Serializable, V extends Descriptor> implements IndexEntry<K,V>, Serializable {

    protected K key;
    protected V value;

    public NBTreeIndexEntry(){
    }

    @Override
    public K getKey() {
        return key;
    }

    @Override
    public void setKey(K key) {
        this.key=key;
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public void setValue(V o) {
       this.value=o;
    }
}