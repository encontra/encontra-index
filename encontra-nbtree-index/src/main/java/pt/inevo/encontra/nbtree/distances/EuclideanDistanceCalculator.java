package pt.inevo.encontra.nbtree.distances;

import pt.inevo.encontra.nbtree.NBPoint;

/**
 * Generic Euclidean Distance calculator for NBPoints
 * @author ricardo
 */
public class EuclideanDistanceCalculator implements DistancePointCalculator{

    @Override
    public double getDistance(NBPoint point1, NBPoint point2) {

        Double val = new Double(0);
        try {
            double tmp;

            for (int i = 0; i < point1.getPointDimensions(); i++) {
                tmp = point1.getAt(i) - point2.getAt(i);
                tmp *= tmp;
                val += tmp;
            }
            return Math.sqrt(val);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return val;
    }
}
