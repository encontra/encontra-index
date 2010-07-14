package pt.inevo.encontra.luceneapp.test;

import java.io.FileNotFoundException;
import junit.framework.TestCase;
import pt.inevo.encontra.descriptors.DescriptorExtractor;
import pt.inevo.encontra.index.AbstractObject;
import pt.inevo.encontra.lucene.index.LuceneEncontraIndex;
import pt.inevo.encontra.lucene.index.LuceneDescriptor;
import pt.inevo.encontra.lucene.index.LuceneDescriptorExtractor;
import pt.inevo.encontra.lucene.index.LuceneIndexEntryFactory;

/**
 * Test the creation of an ImageObject (with the underlying Document from Lucene)
 * @author ricardo
 */
public class CreateIndexObjectTest extends TestCase {

    private class TestObject extends AbstractObject<Integer,String>{}
    
    private class D1Extractor implements DescriptorExtractor<TestObject,LuceneDescriptor> {

        @Override
        public LuceneDescriptor extract(TestObject object) {
            return new LuceneDescriptor();
        }

        @Override
        public LuceneDescriptor newDescriptor(Class<LuceneDescriptor> clazz) {
            return new LuceneDescriptor();
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
        DescriptorExtractor<TestObject, LuceneDescriptor> d=new D1Extractor();
        
        LuceneEncontraIndex<TestObject> index=new LuceneEncontraIndex<TestObject>("lucene");

        LuceneDescriptorExtractor<TestObject> extractor=new LuceneDescriptorExtractor<TestObject>(new DescriptorExtractor[]{d});
        index.setExtractor(extractor);
        
        LuceneIndexEntryFactory<TestObject> entryFactory=new LuceneIndexEntryFactory<TestObject>(extractor);


        TestObject object = new TestObject();
        object.setId(1);
        object.setObject("test");
        LuceneDescriptor entry = entryFactory.createIndexEntry(object);
        index.insert(entry);

        //Integer objectId = (Integer) builder.getObjectId(entry);
        //assertEquals("Works",descriptor.getStringRepresentation());
    }
}