package pt.inevo.encontra.luceneapp.test;

import java.io.FileNotFoundException;

import junit.framework.TestCase;
import pt.inevo.encontra.descriptors.Descriptor;
import pt.inevo.encontra.descriptors.DescriptorExtractor;
import pt.inevo.encontra.index.IndexedObject;
import pt.inevo.encontra.lucene.index.LuceneIndex;
import pt.inevo.encontra.descriptors.SimpleDescriptor;
import pt.inevo.encontra.lucene.index.LuceneIndexEntryFactory;

/**
 * Test the insertion of and indexobject in a LuceneIndex.
 * @author ricardo
 */
public class CreateIndexObjectTest extends TestCase {

    public static class TestObject extends IndexedObject<Integer,String> {}

    public static class D1 extends SimpleDescriptor{
        @Override
        public double getDistance(Descriptor other) {
            return 0;
        }
    }

    public static class D1Extractor extends DescriptorExtractor<TestObject,D1> {

        @Override
        protected TestObject setupIndexedObject(D1 descriptor, TestObject object) {
            object.setValue(descriptor.getValue());
            return object;
        }

        @Override
        public D1 extract(TestObject object) {
            D1 d = new D1();
            d.setValue("It works!");
            return d;
        }
    }

    public CreateIndexObjectTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testMain() throws FileNotFoundException {
        DescriptorExtractor<TestObject, D1> d=new D1Extractor();
        
        LuceneIndex<TestObject> index=new LuceneIndex<TestObject>("luceneCreate",TestObject.class);

        DescriptorExtractor<TestObject,D1> extractor=new D1Extractor();
        LuceneIndexEntryFactory<D1> entryFactory=new LuceneIndexEntryFactory<D1>(D1.class);

        index.setEntryFactory(entryFactory);

        TestObject object = new TestObject();
        object.setId(1);
        object.setValue("Does it work?");
        index.insert(object);
    }
}