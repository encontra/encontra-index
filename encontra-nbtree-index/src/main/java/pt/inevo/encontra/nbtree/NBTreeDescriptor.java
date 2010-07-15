package pt.inevo.encontra.nbtree;

import java.io.Serializable;
import java.util.Arrays;
import pt.inevo.encontra.descriptors.Descriptor;
import pt.inevo.encontra.common.distance.DistanceMeasure;
import pt.inevo.encontra.descriptors.VectorDescriptor;
import pt.inevo.encontra.nbtree.keys.Key;
import pt.inevo.encontra.nbtree.util.Util;

/**
 * Represents a point to be inserted into the NBTree structure.
 * @author ricardo
 */
//public class NBTreeDescriptor<T extends Number> extends VectorDescriptor<T> {
public class NBTreeDescriptor extends VectorDescriptor<Double> {

    protected Key key;

    public NBTreeDescriptor(int size, DistanceMeasure measure) {
        super(size, "");
        super.distanceMeasure = measure;
    }

    public NBTreeDescriptor(int size, String id, DistanceMeasure measure) {
        super(size);
        super.id = id;
        super.weights = new double[size];
        super.distanceMeasure = measure;
        Arrays.fill(super.weights, 1.0);
    }

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    @Override
    public DistanceMeasure getDistanceMeasure() {
        return distanceMeasure;
    }

    public void setValues(double[] d) {
        for (int i = 0; i < d.length; i++){
            set(i, d[i]);
        }
    }

    @Override
    public double[] getDoubleRepresentation() {
        double [] values = new double[size()];
        for (int i = 0; i < values.length; i++){
            values[i] = get(i);
        }
        return values;
    }

    @Override
    public String getName() {
        return id;
    }

    @Override
    public Object getValue() {
        return this.getDoubleRepresentation();
    }

    @Override
    public void setValue(Object o) {
        setValues((double[])o);
    }

    @Override
    public void setId(Serializable id) {
        super.id = id.toString();
    }

    @Override
    public Descriptor setStringRepresentation(String descriptor) {
        double [] values = Util.stringToDoubleArray(descriptor, ',');
        for (int i = 0; i < values.length; i++){
            super.set(i, values[i]);
        }
        return this;
    }
}
