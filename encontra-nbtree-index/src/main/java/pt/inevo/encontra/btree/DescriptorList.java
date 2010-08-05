package pt.inevo.encontra.btree;

import java.util.Iterator;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;
import pt.inevo.encontra.descriptors.Descriptor;

/**
 * Object that holds a list of Descriptors, as the result to a KNN query.
 * Descriptors are ordered by the distance to the query provided.
 * @author Ricardo
 */
public class DescriptorList implements Iterable<Descriptor> {

    //internal representation of the DescriptorList
    private SortedSet<Descriptor> sortedPoints;
    private Descriptor seedP, farPoint;
    private double farDistance;
    private int size;

    public DescriptorList(int size, Descriptor seedPoint) {
        this.seedP = seedPoint;
        this.size = size;

        sortedPoints = new TreeSet<Descriptor>(new Comparator<Descriptor>() {

            @Override
            public int compare(Descriptor o1, Descriptor o2) {
                try {
                    double dist1 = seedP.getDistance(o1);
                    double dist2 = seedP.getDistance(o2);
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
     * Add a NBPoint to the list.
     * @param point
     */
    public boolean addDescriptor(Descriptor point) {
        try {
            if (sortedPoints.size() < size) {
                sortedPoints.add(point);
                farPoint = sortedPoints.last();
                farDistance = seedP.getDistance(farPoint);
                return true;
            } else {
                double distance = seedP.getDistance(point);
                if (distance >= farDistance) {
                    return false;
                } else {
                    sortedPoints.remove(farPoint);
                    sortedPoints.add(point);
                    farPoint = sortedPoints.last();
                    farDistance = seedP.getDistance(farPoint);
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
    public boolean contains(Descriptor point) {
        return sortedPoints.contains(point);
    }

    /**
     * Remove a point from the list.
     * @param point
     */
    public void removeDescriptor(Descriptor point) {
        if (sortedPoints.contains(point)) {
            sortedPoints.remove(point);
            if (sortedPoints.size() > 0) {
                farPoint = sortedPoints.last();
                try {
                    farDistance = seedP.getDistance(farPoint);
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
    public Descriptor[] getDescriptors() {
        return sortedPoints.toArray(new Descriptor[1]);
    }

    /**
     * Gets the iterator object for this list.
     * @return
     */
    @Override
    public Iterator<Descriptor> iterator() {
        return sortedPoints.iterator();
    }

    /**
     * Gets the number of points in the list.
     * @return
     */
    public int getSize() {
        return sortedPoints.size();
    }
}
