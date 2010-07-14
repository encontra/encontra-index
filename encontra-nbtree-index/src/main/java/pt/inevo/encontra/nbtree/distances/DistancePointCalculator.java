package pt.inevo.encontra.nbtree.distances;

import pt.inevo.encontra.nbtree.NBPoint;

/**
 * A generic interface for calculating the distance between two points.
 * The distance function can be customized (for example, using the euclidean
 * distance or any other)
 * @author ricardo
 */
public interface DistancePointCalculator {

    /**
     * Gets the distance between the two points
     * @param point1
     * @param point2
     * @return
     */
    public double getDistance(NBPoint point1, NBPoint point2);
}
