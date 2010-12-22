package pt.inevo.encontra.nbtree.test;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import pt.inevo.encontra.btree.IBTree;
import pt.inevo.encontra.btree.ITuple;
import pt.inevo.encontra.btree.ITupleBrowser;
import pt.inevo.encontra.btree.jdbmBTree;

/**
 * Testing the jdbmBTree with the IBTree interface.
 * @author ricardo
 */
public class BTreeTest {

    public BTreeTest() {
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
        System.out.println("Testing the jdbmBTree...");
        IBTree<EntityTestObject> btree = new jdbmBTree<EntityTestObject>("btreeTest",EntityTestObject.class);

        System.out.println("Adding some elements to the BTree");
        for (double i = 0; i < 10; i++) {
            EntityTestObject o = new EntityTestObject(i, "Description of object " + i);
            btree.insert(o);
        }

        int btreeSize = btree.size();
        System.out.println("Getting the size of the btree: " + btreeSize);

        boolean exists = btree.hasEntry(new EntityTestObject(3d, "Description of object 3"));
        System.out.println("Getting an element from the BTree... : " + exists);

        EntityTestObject o = btree.find(3d);
        if (o != null) System.out.println("Object with id 3 retrieved." + o.getValue());

        EntityTestObject o2 = btree.find(12d);
        if (o2 != null) System.out.println("Object with id 12 retrieved.");
        else System.out.println("[Error]: object with id 12 doesn't not exist in the BTree.");

        boolean value = btree.remove(new EntityTestObject(3d, "Description of object 3"));
        System.out.println("Removing an existent entry:" + value);

        System.out.println("Object exists:" + btree.hasEntry(new EntityTestObject(3d, "Description of object 3")));

        value = btree.remove(new EntityTestObject(14d, "Description of an non existent object."));
        System.out.println("Removing a non existent entry:" + value);

        System.out.println("Object was not removed, because doesn't exist:" + btree.hasEntry(new EntityTestObject(14d, "Description of an non existent object.")));

        ITupleBrowser<EntityTestObject> browser = btree.browse();
        ITuple<EntityTestObject> tuple;

        System.out.println("Iterating over all the entries...");
        tuple = browser.getNext();
        while (tuple != null) {
            System.out.println("Key: " + tuple.getKey() + ", Value: " + tuple.getEntry());
            tuple = browser.getNext();
        }

        browser = btree.browse(5d);
        System.out.println("Iterating through some entries (next)...");
        tuple = browser.getNext();
        while (tuple != null) {
            System.out.println("Key: " + tuple.getKey() + ", Value: " + tuple.getEntry());
            tuple = browser.getNext();
        }

        browser = btree.browse(5d);
        System.out.println("Iterating through some entries (previous)...");
        tuple = browser.getPrevious();
        while (tuple != null) {
            System.out.println("Key: " + tuple.getKey() + ", Value: " + tuple.getEntry());
            tuple = browser.getPrevious();
        }

        btree.close();

        System.out.println("Final size: " + btree.size());
    }

    @Test
    public void testLoad() {
        System.out.println("TestLoad");
        IBTree<EntityTestObject> btree = new jdbmBTree<EntityTestObject>("btreeTest",EntityTestObject.class);
        ITupleBrowser<EntityTestObject> browser = btree.browse();
        while (browser.getNext() != null) {
            System.out.println("Loaded");
        }
    }
}
