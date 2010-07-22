package pt.inevo.encontra.nbtree.descriptors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import pt.inevo.encontra.common.distance.DistanceMeasure;
import pt.inevo.encontra.descriptors.VectorDescriptor;

/**
 * Represents a point to be inserted into the NBTree structure.
 * @author ricardo
 */
public abstract class NBTreeDescriptor<ID extends Serializable, T extends Number> extends VectorDescriptor<ID, T> {

    public NBTreeDescriptor(Class<T> type){
        super(type, 1, "");
    }

    public NBTreeDescriptor(Class<T> type, int size, DistanceMeasure measure) {
        super(type, size, "");
        super.distanceMeasure = measure;
    }

    public NBTreeDescriptor(Class<T> type, int size, Serializable id, DistanceMeasure measure) {
        super(type, size);
        super.id = id;
        super.weights = new double[size];
        super.distanceMeasure = measure;
        Arrays.fill(super.weights, 1.0);
    }

    @Override
    public abstract String getName();

    @Override
    public abstract void setValue(Object o);

    @Override
    public abstract Object getValue();

    @Override
    public void setId(Serializable id) {
        super.id = id;
    }

    @Override
    public abstract DistanceMeasure getDistanceMeasure();

    public void setValues(T[] d) {
        for (int i = 0; i < d.length; i++){
            set(i, d[i]);
        }
    }

    @Override
    public Collection<T> getValues(Class<T> type) {
        Collection<T> arrayValues = new ArrayList<T>();
        for (int i = 0; i < size() ; i++){
            arrayValues.add(get(i));
        }
        return arrayValues;
    }
}