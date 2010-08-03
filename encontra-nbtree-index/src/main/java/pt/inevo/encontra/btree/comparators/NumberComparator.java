package pt.inevo.encontra.btree.comparators;

import java.io.Serializable;
import java.util.Comparator;

/**
 * A generic comparator for numbers.
 * @author Ricardo
 */
public class NumberComparator implements Comparator<Number>, Serializable {

    @Override
    public int compare(Number o1, Number o2) {
        Double v1 = new Double(o1.doubleValue());
        Double v2 = new Double(o2.doubleValue());
        return v1.compareTo(v2);
    }
}
