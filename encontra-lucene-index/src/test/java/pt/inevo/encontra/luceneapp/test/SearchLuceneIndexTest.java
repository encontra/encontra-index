package pt.inevo.encontra.luceneapp.test;

import java.io.FileNotFoundException;

import junit.framework.TestCase;
import pt.inevo.encontra.descriptors.Descriptor;
import pt.inevo.encontra.descriptors.DescriptorExtractor;
import pt.inevo.encontra.index.IndexedObject;
import pt.inevo.encontra.lucene.index.LuceneIndex;
import pt.inevo.encontra.descriptors.SimpleDescriptor;
import pt.inevo.encontra.engine.Engine;
import pt.inevo.encontra.engine.SimpleEngine;
import pt.inevo.encontra.index.Result;
import pt.inevo.encontra.index.ResultSet;
import pt.inevo.encontra.index.search.SimpleSearcher;
import pt.inevo.encontra.lucene.index.LuceneIndexEntryFactory;
import pt.inevo.encontra.query.KnnQuery;
import pt.inevo.encontra.query.Query;
import pt.inevo.encontra.storage.EntityStorage;
import pt.inevo.encontra.storage.SimpleObjectStorage;

/**
 * Test the creation of an ImageObject (with the underlying Document from Lucene)
 * @author ricardo
 */
public class SearchLuceneIndexTest extends TestCase {

    public static class TestObject extends IndexedObject<Integer,String> {}

    public static class D1 extends SimpleDescriptor{
        @Override
        public double getDistance(Descriptor other) {
            return 0;  //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    public static class D1Extractor extends DescriptorExtractor<TestObject,D1> {

        @Override
        protected TestObject setupIndexedObject(D1 descriptor, TestObject object) {
            object.setId(Integer.parseInt(descriptor.getId().toString()));
            object.setValue(descriptor.getValue());
            return object;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public D1 extract(TestObject object) {
            D1 d = new D1();
            d.setId(object.getId());
            d.setValue("It works!");
            return d;
        }

    }

    public SearchLuceneIndexTest(String testName) {
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

        EntityStorage storage = new SimpleObjectStorage(TestObject.class);

        Engine<TestObject> e = new SimpleEngine<TestObject>();
        LuceneIndex<TestObject> index=new LuceneIndex<TestObject>("luceneSearch",TestObject.class);
        SimpleSearcher searcher = new SimpleSearcher();
        searcher.setIndex(index);
        searcher.setObjectStorage(storage);
        
        e.setSearcher(searcher);
        e.setObjectStorage(storage);
       
        LuceneIndexEntryFactory<D1> entryFactory=new LuceneIndexEntryFactory<D1>(D1.class);
        index.setEntryFactory(entryFactory);

        DescriptorExtractor<TestObject, D1> d=new D1Extractor();
        searcher.setDescriptorExtractor(d);

        //inserting some objects in the index
        for (int i= 1 ; i < 10 ; i++){
            TestObject object = new TestObject();
            object.setId(i);
            object.setValue("Does it work? Event number " + i);
            e.insert(object);
        }

        //performing the query
        TestObject queryObject = new TestObject();
        queryObject.setId(101);

        Query knnQuery = new KnnQuery(queryObject, 10);
        ResultSet<TestObject> results = e.search(knnQuery);
        System.out.println("The results for this query are: ");
        for (Result<TestObject> r : results){
            System.out.println(r.getResult());
        }
    }
}