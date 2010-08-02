package pt.inevo.encontra.btree;

import java.io.Serializable;
import jdbm.helper.Tuple;

public class jdbmTuple<K extends Serializable, V extends Serializable> implements ITuple<K,V> {

    private Tuple tuple;

    protected jdbmTuple(Tuple t){
        this.tuple = t;
    }

    @Override
    public K getKey() {
        return (K)tuple.getKey();
    }

    @Override
    public V getValue() {
        return (V)tuple.getValue();
    }

    @Override
    public void setKey(K key) {
        tuple.setKey(key);
    }

    @Override
    public void setValue(V value) {
        tuple.setValue(value);
    }

}
