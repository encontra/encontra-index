package pt.inevo.encontra.nbtree.keys;

import pt.inevo.encontra.nbtree.NBTreeDescriptor;

/**
 * Generic interface for building an object that knows how to calculate
 * a key for a supplied point.
 * @author ricardo
 */
public interface KeyMapper<K extends Key> {

    /**
     * Gets the Key for the point
     * @param point the supplied point
     * @return the key for the point
     */
    public K getKey(NBTreeDescriptor point);
}
