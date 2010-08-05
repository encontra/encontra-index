package pt.inevo.encontra.nbtree.test;

import java.io.Serializable;
import pt.inevo.encontra.index.IndexEntry;

public class EntityTestObject implements IndexEntry<Double, String>, Serializable {

    private Double key;
    private String description;

    public EntityTestObject() {
        super();
    }

    public EntityTestObject(Double key, String desc) {
        super();
        this.key = key;
        this.description = desc;
    }

    @Override
    public Double getKey() {
        return key;
    }

    @Override
    public void setKey(Double key) {
        this.key = key;
    }

    @Override
    public String getValue() {
        return description;
    }

    @Override
    public void setValue(String o) {
        this.description = o;
    }

    @Override
    public String toString(){
        return "[Key: " + key + ", Description: " + description +"]";
    }
}
