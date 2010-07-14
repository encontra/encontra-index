package pt.inevo.encontra.nbtree;

import java.io.Serializable;
import java.util.Arrays;
import pt.inevo.encontra.nbtree.keys.Key;

/**
 * Represents a point to be inserted into the NBTree structure.
 * @author ricardo
 */
public class NBPoint implements Serializable {

    private static final long serialVersionUID = -6035836203861956651L;

    /**
     * internal representation of the point
     * - A double vector for the point coordinates
     * - A Key
     */
    private double[] point;
    private Key key;

    /**
     * Creates a new instance of a point
     * @param point
     */
    public NBPoint(double[] point) {
        this.point = point;
    }

    /**
     * Gets the coordinate of the point in a specific position
     * @param index
     * @return
     * @throws Exception
     */
    public double getAt(int index) throws Exception {
        if (point.length >= index) {
            return point[index];
        } else {
            throw new Exception("Index not valid!");
        }
    }

    public int getPointDimensions(){
        return point.length;
    }

    public Key getKey(){
        return key;
    }

    protected void setKey(Key key){
        this.key = key;
    }

    /**
     * Gets a representation of this point as a double array
     * @return
     */
    public double[] toArray() {
        return Arrays.copyOf(point, point.length);
    }
}
