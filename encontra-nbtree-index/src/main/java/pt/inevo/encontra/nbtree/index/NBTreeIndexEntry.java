package pt.inevo.encontra.nbtree.index;

import java.io.Serializable;
import pt.inevo.encontra.index.IndexEntry;
import pt.inevo.encontra.nbtree.descriptors.NBTreeDescriptor;

public class NBTreeIndexEntry<K extends Serializable, V extends NBTreeDescriptor> implements IndexEntry<K,V> {

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