package pt.inevo.encontra.btree.comparators;

import java.io.Serializable;
import java.util.Comparator;

public class DoubleComparator implements Comparator<Double>, Serializable {

    @Override
    public int compare(Double o1, Double o2) {
        return o1.compareTo(o2);
    }

}
