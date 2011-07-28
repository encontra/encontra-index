package pt.inevo.encontra.luceneapp.test;

import junit.framework.TestCase;
import pt.inevo.encontra.common.Result;
import pt.inevo.encontra.common.ResultSet;
import pt.inevo.encontra.descriptors.Descriptor;
import pt.inevo.encontra.descriptors.DescriptorExtractor;
import pt.inevo.encontra.descriptors.SimpleDescriptor;
import pt.inevo.encontra.engine.SimpleEngine;
import pt.inevo.encontra.engine.SimpleIndexedObjectFactory;
import pt.inevo.encontra.index.IndexedObject;
import pt.inevo.encontra.index.search.SimpleSearcher;
import pt.inevo.encontra.lucene.index.LuceneIndex;
import pt.inevo.encontra.lucene.index.LuceneIndexEntryFactory;
import pt.inevo.encontra.query.CriteriaQuery;
import pt.inevo.encontra.query.Path;
import pt.inevo.encontra.query.QueryProcessorDefaultImpl;
import pt.inevo.encontra.query.criteria.CriteriaBuilderImpl;
import pt.inevo.encontra.storage.EntityStorage;
import pt.inevo.encontra.storage.SimpleObjectStorage;

import java.io.FileNotFoundException;

/**
 * Test for searching through a LuceneIndex.
 * @author ricardo
 */
public class SearchLuceneIndexesTest extends TestCase {

    public static class TestObject extends IndexedObject<Integer,String> {}

    public static class D1 extends SimpleDescriptor{
        @Override
        public double getDistance(Descriptor other) {
            return 0;
        }
    }

    public static class D2 extends SimpleDescriptor{
        @Override
        public double getDistance(Descriptor other) {
            return 0;
        }
    }

    public static class D1Extractor extends DescriptorExtractor<TestObject,D1> {

        @Override
        protected TestObject setupIndexedObject(D1 descriptor, TestObject object) {
            object.setId(Integer.parseInt(descriptor.getId().toString()));
            object.setValue(descriptor.getValue());
            return object;
        }

        @Override
        public D1 extract(TestObject object) {
            D1 d = new D1();
            d.setId(object.getId());
            d.setValue("It works!");
            return d;
        }
    }

    public static class D2Extractor extends DescriptorExtractor<TestObject,D2> {

        @Override
        protected TestObject setupIndexedObject(D2 descriptor, TestObject object) {
            object.setId(Integer.parseInt(descriptor.getId().toString()));
            object.setValue(descriptor.getValue());
            return object;
        }

        @Override
        public D2 extract(TestObject object) {
            D2 d = new D2();
            d.setId(object.getId());
            d.setValue("It now works!");
            return d;
        }
    }

    public SearchLuceneIndexesTest(String testName) {
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

        SimpleEngine<TestObject> e = new SimpleEngine<TestObject>();
        e.setQueryProcessor(new QueryProcessorDefaultImpl());
        e.setIndexedObjectFactory(new SimpleIndexedObjectFactory());

        LuceneIndex<TestObject> indexD1 =new LuceneIndex<TestObject>("luceneSearchD1",TestObject.class);
        LuceneIndex<TestObject> indexD2 =new LuceneIndex<TestObject>("luceneSearchD2",TestObject.class);
        e.setObjectStorage(storage);

        SimpleSearcher d1Searcher = new SimpleSearcher();
        d1Searcher.setDescriptorExtractor(new D1Extractor());
        d1Searcher.setIndex(indexD1);

        SimpleSearcher d2Searcher = new SimpleSearcher();
        d2Searcher.setDescriptorExtractor(new D2Extractor());
        d2Searcher.setIndex(indexD2);

        e.setSearcher("d1", d1Searcher);
        e.setSearcher("d2", d2Searcher);
       
        LuceneIndexEntryFactory<D1> entryFactoryD1 =new LuceneIndexEntryFactory<D1>(D1.class);
        indexD1.setEntryFactory(entryFactoryD1);

        LuceneIndexEntryFactory<D2> entryFactoryD2 =new LuceneIndexEntryFactory<D2>(D2.class);
        indexD2.setEntryFactory(entryFactoryD2);

        //inserting some objects in the indexD1
        for (int i= 1 ; i < 10 ; i++){
            TestObject object = new TestObject();
            object.setId(i);
            object.setValue("Does it work? Event number " + i);
            e.insert(object);
        }

        //performing the query
        TestObject queryObject = new TestObject();
        queryObject.setId(101);

        CriteriaBuilderImpl cb = new CriteriaBuilderImpl();
        CriteriaQuery<TestObject> criteriaQuery = cb.createQuery(TestObject.class);

        //Create the Model/Attributes Path
        Path<TestObject> model = criteriaQuery.from(TestObject.class);

        //Create the Query
        CriteriaQuery query = cb.createQuery().where(cb.similar(model, queryObject));

        // TODO perform the call to e.search
        ResultSet<TestObject> results = e.search(query);
        System.out.println("The results for this query are: ");
        for (Result<TestObject> r : results){
            System.out.println(r.getResultObject());
        }
    }
}