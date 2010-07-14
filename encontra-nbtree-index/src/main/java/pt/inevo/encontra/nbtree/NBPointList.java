package pt.inevo.encontra.nbtree;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;
import pt.inevo.encontra.nbtree.distances.DistancePointCalculator;
import pt.inevo.encontra.nbtree.distances.EuclideanDistanceCalculator;

/**
 * Object that holds a list of NBPoints. Used as the result of the KNN / Range Query.
 * @author Ricardo
 */
public class NBPointList {

    //internal representation of the NBPointList
    private SortedSet<NBPoint> sortedPoints;
    private NBPoint seedP;
    private NBPoint farPoint;
    private double farDistance;
    private int size;
    /**
     * The default distanceCalculator is the Euclidean distance but can change
     */
    private DistancePointCalculator distanceCalculator;

    protected NBPointList(int size, NBPoint seedPoint, DistancePointCalculator distancePointCalculator) {
        this.seedP = seedPoint;
        this.size = size;
        this.distanceCalculator = distancePointCalculator;

        sortedPoints = new TreeSet<NBPoint>(new Comparator<NBPoint>() {

            @Override
            public int compare(NBPoint o1, NBPoint o2) {
                try {
                    double dist1 = distanceCalculator.getDistance(seedP, o1);
                    double dist2 = distanceCalculator.getDistance(seedP, o2);
                    if (o1.equals(o2)) {
                        return 0;
                    }
                    if (dist1 == dist2) {
                        return -1;
                    }
                    return new Double(dist1).compareTo(new Double(dist2));
                } catch (Exception ex) {
                    System.out.println("[Error]: Possible reason " + ex.toString());
                    return 0;
                }
            }
        });
    }

    /**
     * Constructs a NBPointList with a specified default size.
     * @param size
     */
    protected NBPointList(int size, NBPoint seedPoint) {
        this(size, seedPoint, new EuclideanDistanceCalculator());
    }

    /**
     * Add a NBPoint to the list.
     * @param point
     */
    public boolean addPoint(NBPoint point) {
        try {
            if (sortedPoints.size() < size) {
                sortedPoints.add(point);
                farPoint = sortedPoints.last();
                farDistance = distanceCalculator.getDistance(seedP, farPoint);
                return true;
            } else {
                double distance = distanceCalculator.getDistance(seedP, point);
                if (distance > farDistance) {
                    return false;
                } else {
                    sortedPoints.remove(farPoint);
                    sortedPoints.add(point);
                    farPoint = sortedPoints.last();
                    farDistance = distanceCalculator.getDistance(seedP, farPoint);
                    return true;
                }
            }
        } catch (Exception ex) {
            System.out.println("[Error]: Couldn't add the point. Possible reason: " + ex.toString());
        }
        return false;
    }

    /**
     * Checks if the list already contains the specified point.
     * @param point
     * @return
     */
    public boolean contains(NBPoint point) {
        return sortedPoints.contains(point);
    }

    /**
     * Remove a point from the list.
     * @param point
     */
    public void removePoint(NBPoint point) {
        if (sortedPoints.contains(point)) {
            sortedPoints.remove(point);
            if (sortedPoints.size() > 0) {
                farPoint = sortedPoints.last();
                try {
                    farDistance = distanceCalculator.getDistance(seedP, farPoint);
                } catch (Exception ex) {
                    System.out.println("[Error]: Problem when calculating the "
                            + "distance between points. Possible reason: " + ex.toString());
                }
            } else {
                farPoint = null;
                farDistance = 0;
            }
        }
    }

    /**
     * Retrieved all the points from the list.
     * @return
     */
    public NBPoint[] getAllPoints() {
        return sortedPoints.toArray(new NBPoint[1]);
    }

    /**
     * Gets the number of points in the list.
     * @return
     */
    public int getSize() {
        return sortedPoints.size();
    }
}
