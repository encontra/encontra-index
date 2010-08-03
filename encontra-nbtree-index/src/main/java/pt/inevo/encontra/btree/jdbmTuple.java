package pt.inevo.encontra.btree;

import java.io.Serializable;
import jdbm.helper.Tuple;
import pt.inevo.encontra.index.IndexEntry;

public class jdbmTuple<O extends IndexEntry<? extends Serializable, ? extends Serializable>> implements ITuple<O> {

    private Tuple tuple;

    protected jdbmTuple(Tuple t){
        this.tuple = t;
    }

    @Override
    public Serializable getKey() {
        return (Serializable)tuple.getKey();
    }

    @Override
    public Serializable getEntry() {
        return (Serializable)tuple.getValue();
    }

    @Override
    public void setKey(Serializable key) {
        tuple.setKey(key);
    }

    @Override
    public void setEntry(Serializable value) {
        tuple.setValue(value);
    }

}
