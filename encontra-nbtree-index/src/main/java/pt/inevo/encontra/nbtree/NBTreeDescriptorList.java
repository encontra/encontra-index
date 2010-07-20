package pt.inevo.encontra.nbtree;

import pt.inevo.encontra.nbtree.descriptors.NBTreeDescriptor;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;
import pt.inevo.encontra.common.distance.DistanceMeasure;
import pt.inevo.encontra.common.distance.EuclideanDistanceMeasure;

/**
 * Object that holds a list of NBTreeDescriptors. Used as the result of the KNN / Range Query.
 * @author Ricardo
 */
public class NBTreeDescriptorList {

    //internal representation of the NBTreeDescriptorList
    private SortedSet<NBTreeDescriptor> sortedPoints;
    private NBTreeDescriptor seedP;
    private NBTreeDescriptor farPoint;
    private double farDistance;
    private int size;
    /**
     * The default distanceCalculator is the Euclidean distance but can change
     */
    private DistanceMeasure distanceCalculator;

    protected NBTreeDescriptorList(int size, NBTreeDescriptor seedPoint, DistanceMeasure distancePointCalculator) {
        this.seedP = seedPoint;
        this.size = size;
        this.distanceCalculator = distancePointCalculator;

        sortedPoints = new TreeSet<NBTreeDescriptor>(new Comparator<NBTreeDescriptor>() {

            @Override
            public int compare(NBTreeDescriptor o1, NBTreeDescriptor o2) {
                try {
                    double dist1 = distanceCalculator.distance(seedP, o1);
                    double dist2 = distanceCalculator.distance(seedP, o2);
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
     * Constructs a NBTreeDescriptorList with a specified default size.
     * @param size
     */
    protected NBTreeDescriptorList(int size, NBTreeDescriptor seedPoint) {
        this(size, seedPoint, new EuclideanDistanceMeasure());
    }

    /**
     * Add a NBPoint to the list.
     * @param point
     */
    public boolean addPoint(NBTreeDescriptor point) {
        try {
            if (sortedPoints.size() < size) {
                sortedPoints.add(point);
                farPoint = sortedPoints.last();
                farDistance = distanceCalculator.distance(seedP, farPoint);
                return true;
            } else {
                double distance = distanceCalculator.distance(seedP, point);
                if (distance > farDistance) {
                    return false;
                } else {
                    sortedPoints.remove(farPoint);
                    sortedPoints.add(point);
                    farPoint = sortedPoints.last();
                    farDistance = distanceCalculator.distance(seedP, farPoint);
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
    public boolean contains(NBTreeDescriptor point) {
        return sortedPoints.contains(point);
    }

    /**
     * Remove a point from the list.
     * @param point
     */
    public void removePoint(NBTreeDescriptor point) {
        if (sortedPoints.contains(point)) {
            sortedPoints.remove(point);
            if (sortedPoints.size() > 0) {
                farPoint = sortedPoints.last();
                try {
                    farDistance = distanceCalculator.distance(seedP, farPoint);
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
    public NBTreeDescriptor[] getAllPoints() {
        return sortedPoints.toArray(new NBTreeDescriptor[1]);
    }

    /**
     * Gets the number of points in the list.
     * @return
     */
    public int getSize() {
        return sortedPoints.size();
    }
}
