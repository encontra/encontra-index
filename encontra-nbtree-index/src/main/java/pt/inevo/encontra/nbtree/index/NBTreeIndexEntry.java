package pt.inevo.encontra.nbtree.index;

import java.io.Serializable;
import pt.inevo.encontra.index.IndexEntry;


public class NBTreeIndexEntry implements IndexEntry {
    private Serializable key;
    private Object value;

    public NBTreeIndexEntry(){
    }

    @Override
    public Serializable getKey() {
        return key;
    }

    @Override
    public void setKey(Serializable key) {
        this.key=key;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void setValue(Object o) {
       this.value=o;
    }
}