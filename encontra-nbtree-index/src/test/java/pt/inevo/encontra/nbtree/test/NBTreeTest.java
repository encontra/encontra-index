package pt.inevo.encontra.nbtree.test;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import pt.inevo.encontra.common.distance.DistanceMeasure;
import pt.inevo.encontra.common.distance.EuclideanDistanceMeasure;
import pt.inevo.encontra.nbtree.NBTreeDescriptorList;
import pt.inevo.encontra.nbtree.NBTree;
import pt.inevo.encontra.nbtree.descriptors.NBTreeDescriptor;
import pt.inevo.encontra.nbtree.keys.Key;
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
            NBTree<Key, NBTreeDescriptor> nbTree = new NBTree<Key, NBTreeDescriptor>();
            //NBTree nbTree = new NBTree("testSmallNBTree","nbtreeData");

            System.out.println("Creating the points to insert into the NBTree...");
            Double[][] descriptors = new Double[][]{
                new Double[]{2.0, 1.0}, //A
                new Double[]{1.0, 3.0}, //B
                new Double[]{5.0, 4.0}, //C
                new Double[]{6.0, 3.0}, //D
                new Double[]{5.0, 1.0}, //E
                new Double[]{2.0, -2.0}, //F
                new Double[]{1.0, -1.0}, //G
                new Double[]{-2.0, 1.0}, //H
                new Double[]{-2.0, 2.0}, //I
                new Double[]{4.0, 1.0}, //J
                new Double[]{0.0, 1.0}, //K
                new Double[]{3.0, 1.0} //M
            };

            System.out.println("Normalizing the points to interval [0,1] ...");
            for (int i = 0; i < descriptors.length; i++) {
                descriptors[i] = Util.normalize(descriptors[i], -2, 6);
            }

//            System.out.println("Inserting the points in the nbtree");
//            for (int i = 0; i < descriptors.length; i++) {
//                NBTreeDescriptor descriptor = new NBTreeDescriptor(Double.class, 2, "Object" + i, new EuclideanDistanceMeasure());
//                descriptor.setValues(descriptors[i]);
//                nbTree.insertPoint(descriptor);
//            }
//            System.out.println("All the elements inserted in the nbtree\n");
//
//            int k = 12;
//            NBTreeDescriptor query = new NBTreeDescriptor(Double.class, 2, "Object0", new EuclideanDistanceMeasure());
//            query.setValues(descriptors[0]);
//            System.out.println("Getting the " + k + " most similar to a given descriptor:");
//
//            //testing the point A
//            System.out.println("Descriptor used: " + Util.doubleArrayToString(descriptors[0], ','));
//            NBTreeDescriptorList results = nbTree.knnQuery(query, k);
//
//            System.out.println("Number of return elements: " + results.getSize());
//            System.out.println("The most similar are: \n");
//            DistanceMeasure distanceCalculator = nbTree.getDistanceMeasure();
//            KeyMapper keyMapper = nbTree.getKeyMapper();
//            for (NBTreeDescriptor point : results.getAllPoints()) {
//                Double[] value = (Double[])point.getValues(Double.class).toArray(new Double[1]);
//                System.out.print(Util.doubleArrayToString(value, ','));
//                System.out.println(" - distance:" + distanceCalculator.distance(query, point) + "[key="
//                        + keyMapper.getKey(point).getValue() + "]");
//            }
//
//            System.out.println();
//            System.out.println("Performing a range query on " + Util.doubleArrayToString(descriptors[0], ',') + " with radious= " + 0.2);
//            results = nbTree.rangeQuery(query, 0.2);
//            System.out.println("Number of returned results: " + results.getSize());
//
//            for (NBTreeDescriptor point : results.getAllPoints()) {
//                Double[] value = (Double[])point.getValues(Double.class).toArray(new Double[1]);
//                System.out.print(Util.doubleArrayToString(value, ','));
//                System.out.println(" - distance:" + distanceCalculator.distance(query, point) + "[key="
//                        + keyMapper.getKey(point).getValue() + "]");
//            }

            System.out.println("\nDumping the NBTree:");
            nbTree.dump();

            System.out.println("Finishing the test on the NBTree...");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
