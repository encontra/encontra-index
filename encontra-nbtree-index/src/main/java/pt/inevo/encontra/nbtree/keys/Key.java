package pt.inevo.encontra.nbtree.keys;

import java.io.Serializable;

/**
 * Generic Key class.
 * Represents a pair (keyValue, keyId) <- this is the Key Object
 * @author Ricardo
 */
public class Key implements Serializable, Comparable {

    protected double keyValue;
    protected String keyId;

    public Key(){}

    public Key(double value, String id){
        this.keyValue = value;
        this.keyId = id;
    }

    public double getValue(){
        return keyValue;
    }

    public void setValue(double keyValue){
        this.keyValue = keyValue;
    }

    public String getId() {
        return keyId;
    }

    public void setId(String id){
        this.keyId = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Key){
            Key key = (Key)obj;
            if (keyValue == key.getValue() && keyId.equals(key.getId())){
                return true;
            }
            return false;
        }
        else return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.keyValue) ^ (Double.doubleToLongBits(this.keyValue) >>> 32));
        hash = 79 * hash + (this.keyId != null ? this.keyId.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString(){
        return "[Value: " + keyValue + ", Id: " + keyId + "]";
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof Key){
            Key k = (Key)o;
            if (k.getValue() == keyValue && k.getId().equals(keyId)){
                return 0;
            } else if (k.getValue() == keyValue && !k.getId().equals(keyId)){
                return -1;
            } else {
                Double o1D = new Double(keyValue);
                Double o2D = new Double(k.getValue());
                return o1D.compareTo(o2D);
            }
        }
        return 1;
    }
}