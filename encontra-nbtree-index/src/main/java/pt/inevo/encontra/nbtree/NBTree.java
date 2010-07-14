package pt.inevo.encontra.nbtree;

import pt.inevo.encontra.nbtree.keys.Key;
import jdbm.btree.BTree;
import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.helper.CachePolicy;
import jdbm.helper.SoftCache;
import jdbm.helper.Tuple;
import jdbm.helper.TupleBrowser;
import jdbm.recman.CacheRecordManager;

import java.io.*;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Random;
import pt.inevo.encontra.nbtree.distances.DistancePointCalculator;
import pt.inevo.encontra.nbtree.distances.EuclideanDistanceCalculator;

import pt.inevo.encontra.nbtree.exceptions.NBTreeException;
import pt.inevo.encontra.nbtree.keys.EuclideanNormKeyMapper;
import pt.inevo.encontra.nbtree.keys.KeyMapper;
import pt.inevo.encontra.nbtree.util.KeyComparator;

/**
 * NBTree - Multidimensional indexing structure.
 * A java implementation of the structure developed by Manuel João da Fonseca.
 * @author Ricardo Dias
 */
public class NBTree implements Serializable {

    private static final long serialVersionUID = 8516386374296516305L;
    private RecordManager _recman;
    private BTree _tree;
    private CachePolicy _cache;
    private boolean hasChanged;
    private KeyMapper pointMapper;
    private DistancePointCalculator distanceCalculator;

    /**
     * Constructs a new NBTree.
     * Default BTree and RecordManager implementation will be used, as also the
     * distance calculator and key mapper.
     */
    public NBTree() throws NBTreeException {
        this(new EuclideanDistanceCalculator(), new EuclideanNormKeyMapper());
    }

    /**
     * Constructs a new NBTree.
     * Default BTree and RecordManager implementation will be used
     */
    public NBTree(DistancePointCalculator distanceCalculator, KeyMapper mapper) throws NBTreeException {
        try {
            Random r = new Random();
            _cache = new SoftCache();
            _recman = new CacheRecordManager(RecordManagerFactory.createRecordManager("RM" + r.nextInt()), _cache);
            _tree = BTree.createInstance(_recman, new KeyComparator());
            hasChanged = true;
            this.pointMapper = mapper;
            this.distanceCalculator = distanceCalculator;
        } catch (IOException e) {
            throw new NBTreeException("Cannot instanciate a NBTree. Possible reason: " + e.toString());
        }
    }

    /**
     * Constructs a new NBTree.
     * Default BTree and RecordManager implementation will be used.
     * If the NBTree already exists then it will be loaded into this instance.
     */
    public NBTree(String nbtreeName, String path) throws NBTreeException {
        this(nbtreeName, path, new EuclideanDistanceCalculator(), new EuclideanNormKeyMapper(), new KeyComparator());
    }

    /**
     * Constructs a new NBTree.
     * Default BTree and RecordManager implementation will be used.
     * If the NBTree already exists then it will be loaded into this instance.
     */
    public NBTree(String nbtreeName, String path, DistancePointCalculator distanceCalculator,
            KeyMapper mapper, Comparator comparator) throws NBTreeException {
        try {
            File directory = new File(path);
            if (!directory.exists()) {
                directory.mkdir();
            }
            String nbtreeFullPath = path + System.getProperty("file.separator") + nbtreeName;

            _cache = new SoftCache();
            _recman = new CacheRecordManager(RecordManagerFactory.createRecordManager(nbtreeFullPath + "RM"), _cache);

            long recid = _recman.getNamedObject(nbtreeName);
            if (recid != 0) {
                _tree = BTree.load(_recman, recid);
                System.out.println("Reloaded " + nbtreeName + " with " + _tree.size());
            } else {
                _tree = BTree.createInstance(_recman, new KeyComparator());
                _recman.setNamedObject(nbtreeName, _tree.getRecid());
                System.out.println("Created a new empty BTree");
                //commit only if a new BTree was created
                _recman.commit();
            }
            hasChanged = false;
            this.pointMapper = mapper;
            this.distanceCalculator = distanceCalculator;
        } catch (IOException e) {
            throw new NBTreeException("Cannot instanciate a NBTree. Possible reason: " + e.toString());
        }
    }

    /**
     * Inserts a point in the NBTree
     * @param point the point to be inserted
     * @return key that represents the inserted point and the point id
     * @throws NBTreeException
     */
    public Key insertPoint(NBPoint point) throws NBTreeException {
        Key key = getPointMapper().getKey(point);
        //update key on point properties
        point.setKey(key);
        try {
            if (_tree.insert(key, point, false) != null) {
                //when there is a collision, then associate a collection to the key
                if (isPoint(key)) {
                    HashSet<NBPoint> points = new HashSet<NBPoint>();
                    NBPoint p = (NBPoint) _tree.find(key);
                    points.add(p);
                    points.add(point);
                    //insert and replace the old value of the key with the new one
                    _tree.insert(key, points, true);
                } else {
                    HashSet<NBPoint> points = (HashSet<NBPoint>) _tree.find(key);
                    points.add(point);
                    //insert and replace the old value of the key with the new one
                    _tree.insert(key, points, true);
                }
            }
            hasChanged = true;
        } catch (Exception e) {
            throw new NBTreeException("Error: Could not insert the point. Possible reason: " + e.toString());
        }
        return key;
    }

    /**
     * Checks whether if the key is associate with a point or with a list of points
     * @param key
     * @return
     * @throws NBTreeException
     */
    protected boolean isPoint(Key key) throws NBTreeException {
        if (key != null) {
            try {
                Object o = _tree.find(key);
                if (o instanceof NBPoint) {
                    return true;
                }
            } catch (Exception e) {
                throw new NBTreeException(e.toString());
            }
        }
        return false;
    }

    /**
     * Removes a point given the key and the point, because now handles multiple
     * values with the same key.
     * @param key
     * @return a boolean that represents the success of the operation
     */
    public boolean removePoint(Key key) {
        try {
            if (isPoint(key)) {
                _tree.remove(key);
            } else {
                //must remove only the point that is necessary - not the others
                HashSet<NBPoint> points = (HashSet<NBPoint>) _tree.find(key);
                NBPoint toRemove = null;
                if (points.size() > 1) {
                    for (NBPoint p : points) {
                        if (p.getKey().equals(key)) {
                            toRemove = p;
                            break;
                        }
                    }
                    points.remove(toRemove);
                    //update just to contain the non removed values
                    _tree.insert(key, points, true);
                } else {
                    _tree.remove(key);
                }
            }

            hasChanged = true;
            return true;
        } catch (Exception e) {
            System.out.println("[Error]: Couldn't remove the point. Possible "
                    + "reason: " + e.toString());
            return false;
        }
    }

    /**
     * Gets a point given its key.
     * @param key the key we want to find
     * @return the point that matches the key
     */
    public NBPoint lookupPoint(Key key) throws NBTreeException {
        if (key != null) {
            try {
                if (isPoint(key)) {
                    return (NBPoint) _tree.find(key);
                } else {
                    HashSet<NBPoint> points = (HashSet<NBPoint>) _tree.find(key);
                    for (NBPoint p : points) {
                        if (p.getKey().equals(key)) {
                            return p;
                        }
                    }
                }

            } catch (Exception e) {
                throw new NBTreeException(e.toString());
            }
        }
        return null;
    }

    /**
     * Gets a list of points given its key.
     * @param key the key we want to find
     * @return the point that matches the key
     */
    protected HashSet<NBPoint> lookupPoints(Key key) throws NBTreeException {
        if (key != null) {
            try {
                if (isPoint(key)) {
                    HashSet<NBPoint> points = new HashSet<NBPoint>();
                    points.add((NBPoint) _tree.find(key));
                    return points;
                } else {
                    return (HashSet<NBPoint>) _tree.find(key);
                }
            } catch (Exception e) {
                throw new NBTreeException(e.toString());
            }
        }
        return null;
    }

    /**
     * Method that checks if the tree has the given key object
     * @param tree
     * @param key
     * @return
     * @throws NBTreeException
     */
    public boolean hasPoint(Key key) throws NBTreeException {
        try {
            if (_tree.find(key) != null) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            throw new NBTreeException("Error: Could not complete the request. Possible reason: " + e.toString());
        }
    }

    /**
     * Executes the knn query, obtaining the k elements most similar to the query
     * @param query the initial point
     * @param knn the number of similar elements the user desires
     * @return a list with the most similar points
     * @throws NBTreeException
     */
    public NBPointList knnQuery(NBPoint query, int knn) throws NBTreeException {

        try {
            final double MAX_FAR = Double.MAX_VALUE;
            final double DELTA = 0.01;
            TupleBrowser lcursor, rcursor;
            Tuple tuple = new Tuple();
            NBPointList results = new NBPointList(knn, query, distanceCalculator.getClass().newInstance());

            double val;
            double rightLimit;
            double leftLimit;
            double startPoint;
            double farLimit;
            boolean keepSearchRight = true, keepSearchLeft = true;
            boolean getPrevious = false, getNext = false;

            farLimit = MAX_FAR;
            val = getPointMapper().getKey(query).getValue();
            startPoint = val;
            rightLimit = startPoint + DELTA;
            leftLimit = startPoint - DELTA;

            rcursor = _tree.browse(query.getKey());
            lcursor = _tree.browse(query.getKey());

            while (keepSearchRight || keepSearchLeft) {

                //going right
                if (keepSearchRight) {
                    if (getPrevious) {
                        getPrevious = false;
                        rcursor.getPrevious(tuple);
                    }
                    if (rcursor.getNext(tuple)) {
                        Key searchKey = ((Key) tuple.getKey());
                        double keyElem = searchKey.getValue();
                        HashSet<NBPoint> points = lookupPoints(searchKey);

                        while (keyElem <= rightLimit && keepSearchRight) {
                            for (NBPoint p : points) {
                                val = getDistanceCalculator().getDistance(query, p);
                                if (val <= farLimit) { //original version was just '<'
                                    if (!results.contains(p)) {	//insert only if it doesn't already exists
                                        if (!results.addPoint(p)) {
                                            /*we are not improving the results going
                                            this way, so stop the search this way*/
                                            keepSearchRight = false;
                                            break;
                                        }
                                    }
                                }
                            }

                            if (!rcursor.getNext(tuple)) {
                                keepSearchRight = false;
                                break;
                            }
                            keyElem = ((Key) tuple.getKey()).getValue();
                            points = lookupPoints(((Key) tuple.getKey()));
                        }
                        if (keepSearchRight) {
                            rightLimit = ((Key) tuple.getKey()).getValue() + DELTA;
                            getPrevious = true;
                        } else {
                            rightLimit += DELTA;
                            getPrevious = true;
                        }
                    } else {
                        keepSearchRight = false;
                    }
                }

                if (leftLimit < 0) {
                    leftLimit = 0;
                }
                //going left
                if (keepSearchLeft) {
                    if (getNext) {
                        getNext = false;
                        lcursor.getNext(tuple);
                    }
                    if (lcursor.getPrevious(tuple)) {
                        Key searchKey = ((Key) tuple.getKey());
                        Double keyElem = searchKey.getValue();
                        HashSet<NBPoint> points = lookupPoints(searchKey);

                        while (keyElem >= leftLimit && keepSearchLeft) {
                            for (NBPoint p : points) {
                                val = getDistanceCalculator().getDistance(query, p);
                                if (val <= farLimit) { //original version is just '<'
                                    if (!results.contains(p)) {	//insert only if it doesn't already exists
                                        if (!results.addPoint(p)) {
                                            /*we are not improving the results going
                                            this way, so stop the search this way*/
                                            keepSearchLeft = false;
                                            break;
                                        }
                                    }
                                }
                            }

                            if (!lcursor.getPrevious(tuple)) {
                                keepSearchLeft = false;
                                break;
                            }
                            keyElem = ((Key) tuple.getKey()).getValue();
                            points = lookupPoints(((Key) tuple.getKey()));
                        }
                        if (keepSearchLeft) {
                            leftLimit = ((Key) tuple.getKey()).getValue() - DELTA;
                            getNext = true;
                        }
                    } else {
                        keepSearchLeft = false;
                    }
                }
            }

            return results;
        } catch (Exception ex) {
            throw new NBTreeException("Error: Cannot perform KNN query.\nMessage: " + ex.toString());
        }
    }

    /**
     * Performs a range Query.
     * @param query the point query
     * @param bRadious the radious of the range query
     * @return
     */
    public NBPointList rangeQuery(NBPoint query, double bRadious) throws NBTreeException {

        NBPointList results = null;
        try {
            results = new NBPointList(Integer.MAX_VALUE, query, distanceCalculator.getClass().newInstance());
            TupleBrowser rcursor;
            Tuple tuple = new Tuple();
            double rightLimit, leftLimit, startPoint;

            startPoint = getPointMapper().getKey(query).getValue();
            leftLimit = startPoint - bRadious;

            if (leftLimit < 0) {
                leftLimit = 0;
            }
            rightLimit = startPoint + bRadious;


            //init the cursor just before the element at the leftLimit
            rcursor = _tree.browse(new Key(leftLimit, ""));

            //going from left to right with the cursor
            while (rcursor.getNext(tuple)) {
                Double keyElem = ((Key) tuple.getKey()).getValue();
                if (keyElem <= rightLimit) {
                    HashSet<NBPoint> points = lookupPoints(((Key) tuple.getKey()));
                    for (NBPoint p : points) {
                        results.addPoint(p);
                    }
                } else {
                    break;
                }

            }

        } catch (Exception ex) {
            throw new NBTreeException("Error: Cannot perform range query. Possible reason: " + ex.toString());
        }
        return results;
    }

    /**
     * Save the NBTree.
     * @throws NBTreeException
     */
    public void save() throws NBTreeException {
        try {
            if (hasChanged) {    //only saves the ntree is something has changed
                _recman.commit();
            }
        } catch (IOException e) {
            throw new NBTreeException("Cannot save the NBTree. Possible reason: " + e.toString());
        }
    }

    /**
     * Prints all the elements in the NBTree
     */
    public void dump() throws NBTreeException {
        Tuple tuple = new Tuple();
        try {
            for (TupleBrowser cursor = _tree.browse(); cursor.getNext(tuple);) {
                System.out.println("Key: " + ((Key) tuple.getKey()).getValue());

                HashSet<NBPoint> points = lookupPoints(((Key) tuple.getKey()));
                for (NBPoint p : points) {
                    double[] value = p.toArray();
                    for (int i = 0; i < value.length; i++) {
                        System.out.println("   Val[" + i + "]: " + value[i]);
                    }
                }

            }
        } catch (Exception e) {
            throw new NBTreeException("Error: Cannot dump the NBTree. Possible reason: " + e.toString());
        }
    }

    /**
     * Gets the Distance Calculator used by the NBTree
     * @return the distanceCalculator
     */
    public DistancePointCalculator getDistanceCalculator() {
        return distanceCalculator;
    }

    /**
     * Gets the Key Mapper used by the NBTree
     * @return the pointMapper
     */
    public KeyMapper getPointMapper() {
        return pointMapper;
    }
}
