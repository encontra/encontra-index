package pt.inevo.encontra.nbtree.test;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import pt.inevo.encontra.nbtree.NBPoint;
import pt.inevo.encontra.nbtree.NBPointList;
import pt.inevo.encontra.nbtree.NBTree;
import pt.inevo.encontra.nbtree.distances.DistancePointCalculator;
import pt.inevo.encontra.nbtree.keys.KeyMapper;
import pt.inevo.encontra.nbtree.util.Util;

/**
 *
 * @author ricardo
 */
public class NBTreeTest {

    public NBTreeTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void test() {
        try {
            System.out.println("Testing the NBTree...");

            System.out.println("Creating the NBTree...");
            NBTree nbTree = new NBTree();
            //NBTree nbTree = new NBTree("testSmallNBTree","nbtreeData");

            System.out.println("Creating the points to insert into the NBTree...");
            double[][] descriptors = new double[][]{
                new double[]{2.0, 1.0}, //A
                new double[]{1.0, 3.0}, //B
                new double[]{5.0, 4.0}, //C
                new double[]{6.0, 3.0}, //D
                new double[]{5.0, 1.0}, //E
                new double[]{2.0, -2.0}, //F
                new double[]{1.0, -1.0}, //G
                new double[]{-2.0, 1.0}, //H
                new double[]{-2.0, 2.0}, //I
                new double[]{4.0, 1.0}, //J
                new double[]{0.0, 1.0}, //K
                new double[]{3.0, 1.0} //M
            };

            System.out.println("Normalizing the points to interval [0,1] ...");
            for (int i = 0; i < descriptors.length; i++) {
                descriptors[i] = Util.normalize(descriptors[i], -2, 6);
            }

            System.out.println("Inserting the points in the nbtree");
            for (int i = 0; i < descriptors.length; i++) {
                nbTree.insertPoint(new NBPoint(descriptors[i]));
            }
            System.out.println("All the elements inserted in the nbtree\n");

            int k = 12;
            NBPoint query = new NBPoint(descriptors[0]);
            System.out.println("Getting the " + k + " most similar to a given descriptor:");

            //testing the point A
            System.out.println("Descriptor used: " + Util.doubleArrayToString(descriptors[0], ','));
            NBPointList results = nbTree.knnQuery(query, k);

            System.out.println("Number of return elements: " + results.getSize());
            System.out.println("The most similar are: \n");
            DistancePointCalculator distanceCalculator = nbTree.getDistanceCalculator();
            KeyMapper keyMapper = nbTree.getPointMapper();
            for (NBPoint point : results.getAllPoints()) {
                double[] value = point.toArray();
                System.out.print(Util.doubleArrayToString(value, ','));
                System.out.println(" - distance:" + distanceCalculator.getDistance(query, point) + "[key="
                        + keyMapper.getKey(point).getValue() + "]");
            }

            System.out.println();
            System.out.println("Performing a range query on " + Util.doubleArrayToString(descriptors[0], ',') + " with radious= " + 0.2);
            results = nbTree.rangeQuery(query, 0.2);
            System.out.println("Number of returned results: " + results.getSize());

            for (NBPoint point : results.getAllPoints()) {
                double[] value = point.toArray();
                System.out.print(Util.doubleArrayToString(value, ','));
                System.out.println(" - distance:" + distanceCalculator.getDistance(query, point) + "[key="
                        + keyMapper.getKey(point).getValue() + "]");
            }

            System.out.println("\nDumping the NBTree:");
            nbTree.dump();

            System.out.println("Finishing the test on the NBTree...");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
