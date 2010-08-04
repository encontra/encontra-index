//package pt.inevo.encontra.nbtree.descriptors;
//
//import java.io.Serializable;
//import java.lang.reflect.Array;
//import java.util.Arrays;
//import pt.inevo.encontra.common.distance.DistanceMeasure;
//import pt.inevo.encontra.descriptors.VectorDescriptor;
//
///**
// * Represents a point to be inserted into the NBTree structure.
// * @author ricardo
// */
//public class NBTreeDescriptor<ID extends Serializable, T extends Number> extends VectorDescriptor<ID, T> {
//
//    private String name;
//
//    public NBTreeDescriptor(Class<T> type){
//        super(type, 1, "");
//    }
//
//    public NBTreeDescriptor(Class<T> type, int size, DistanceMeasure measure) {
//        super(type, size, "");
//        super.distanceMeasure = measure;
//    }
//
//    public NBTreeDescriptor(Class<T> type, int size, Serializable id, DistanceMeasure measure) {
//        super(type, size);
//        super.id = id;
//        super.weights = new double[size];
//        super.distanceMeasure = measure;
//        Arrays.fill(super.weights, 1.0);
//    }
//
//    @Override
//    public String getName(){
//        return name;
//    }
//
//    public void setName(String name){
//        this.name = name;
//    }
//
//    @Override
//    public void setValue(Object o){
//        setDoubleRepresentation((double[])o);
//    }
//
//    @Override
//    public Object getValue(){
//        return getDoubleRepresentation();
//    }
//
//    @Override
//    public void setId(Serializable id) {
//        super.id = id;
//    }
//
//    @Override
//    public DistanceMeasure getDistanceMeasure(){
//        return distanceMeasure;
//    }
//
//    public void setValues(T[] d) {
//        for (int i = 0; i < d.length; i++){
//            set(i, d[i]);
//        }
//    }
//
//    @Override
//    public double[] getDoubleRepresentation() {
//        int vectorSize = size();
//        double [] representation = new double[vectorSize];
//        for (int i = 0; i < vectorSize ; i++){
//            representation[i] = get(i).doubleValue();
//        }
//        return representation;
//    }
//
//    @Override
//    public void setDoubleRepresentation(double[] v) {
//
//        this.size = v.length;
//        this.values = (T[])Array.newInstance(typeT,size);
//        for (int i = 0; i < values.length ; i++){
//            this.values[i] = (T) Double.valueOf(v[i]);
//        }
//    }
//}