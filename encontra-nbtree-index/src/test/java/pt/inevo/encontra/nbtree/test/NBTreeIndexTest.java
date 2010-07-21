package pt.inevo.encontra.nbtree.test;

import pt.inevo.encontra.common.distance.DistanceMeasure;
import pt.inevo.encontra.descriptors.DescriptorExtractor;
import pt.inevo.encontra.engine.SimpleEngine;
import pt.inevo.encontra.engine.Engine;
import junit.framework.TestCase;
import pt.inevo.encontra.common.distance.EuclideanDistanceMeasure;
import pt.inevo.encontra.engine.SimpleIndexedObjectFactory;
import pt.inevo.encontra.index.*;
import pt.inevo.encontra.index.annotation.Indexed;
import pt.inevo.encontra.index.search.SimpleCombinedSearcher;
import pt.inevo.encontra.index.search.SimpleSearcher;
import pt.inevo.encontra.nbtree.descriptors.NBTreeDescriptor;
import pt.inevo.encontra.nbtree.descriptors.NBTreeDescriptorExtractor;
import pt.inevo.encontra.query.*;
import pt.inevo.encontra.storage.*;

/**
 * Smoke test: testing the creation of a simple engine, two indexes and the
 * execution of two random queries (testing also the combination of the queries).
 * @author ricardo
 */
public class NBTreeIndexTest extends TestCase {

    public class TestModel implements IEntity<Long> {

        private Long id;
        private String title;
        private String content;

        public TestModel(String title, String content){
            this.title=title;
            this.content=content;
        }

        @Override
        public Long getId() {
            return id;
        }

        @Override
        public void setId(Long id) {
            this.id=id;
        }

        @Indexed
        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        @Indexed
        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        @Override
        public String toString() {
            return "TestModel{" +
                    "id=" + id +
                    ", title='" + title + '\'' +
                    ", content='" + content + '\'' +
                    '}';
        }
    }

    public static class TestNBTreeDescriptor extends NBTreeDescriptor {

        public TestNBTreeDescriptor(){
            super(Double.class);
            super.distanceMeasure = new EuclideanDistanceMeasure();
        }

        @Override
        public String getName() {
            return "TestNBTreeDescriptor";
        }

        @Override
        public void setValue(Object o) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Object getValue() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public DistanceMeasure getDistanceMeasure() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    public NBTreeIndexTest(String testName) {
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

    public void testMain() {
        DescriptorExtractor descriptorExtractor=new NBTreeDescriptorExtractor(TestNBTreeDescriptor.class);

        EntityStorage storage=new SimpleObjectStorage(TestModel.class);
        System.out.println("Creating the Retrieval Engine...");

        Engine<TestModel> e = new SimpleEngine<TestModel>();
        e.setObjectStorage(storage);
        e.setIndexedObjectFactory(new SimpleIndexedObjectFactory());

        System.out.println("Registering the two indexes in the Retrieval Engine");
        SimpleCombinedSearcher searcher=new SimpleCombinedSearcher();

        SimpleSearcher titleSearcher=new SimpleSearcher();
        titleSearcher.setDescriptorExtractor(descriptorExtractor);
        titleSearcher.setIndex(new SimpleIndex(TestNBTreeDescriptor.class));

        //TO DO -- we will be able to do this here
        //titleSearcher.setIndex(new NBTreeIndex(TestDescriptor.class));

        SimpleSearcher contentSearcher=new SimpleSearcher();
        contentSearcher.setDescriptorExtractor(descriptorExtractor);
        contentSearcher.setIndex(new SimpleIndex(TestNBTreeDescriptor.class));

        searcher.add("title",titleSearcher);
        searcher.add("content",contentSearcher);

        e.setSearcher(searcher);

        System.out.println("Loading some objects to the test indexes");
        e.insert(new TestModel("aaa","bbb"));
        e.insert(new TestModel("aab","bba"));
        e.insert(new TestModel("aba","bab"));
        e.insert(new TestModel("abb","baa"));
        e.insert(new TestModel("baa","abb"));
        e.insert(new TestModel("bab","aba"));
        e.insert(new TestModel("bba","aab"));
        e.insert(new TestModel("bbb","aaa"));

        System.out.println("Making some random queries and searching in the engine:");
        System.out.println("Creating two random queries...");
        Query randomQuery = new RandomQuery();
        Query anotherRandomQuery = new RandomQuery();
        System.out.println("Creating a knn query...");

        Query knnQuery = new KnnQuery(new IndexedObject(null,"aaa"), 8);

        System.out.println("Searching for elements in the engine...");
        QueryBuilder qb=new QueryBuilder();
        // select * from randomquery inner join anotherRandomQuery inner join randomQuery
        //qb.semijoin( randomQuery ,anotherRandomQuery, randomQuery ) <-> innerjoin <-> intersect
        //qb.antijoin( randomQuery ,anotherRandomQuery, randomQuery )  <-> !innerjoin <->

        ResultSet<TestModel> results = e.search(knnQuery); //new Query[]{randomQuery, anotherRandomQuery, });
        System.out.println("Number of retrieved elements: " + results.size());
        for ( Result<TestModel> r : results) {
            System.out.print("Retrieved element: " + r.getResult().toString() + "\t");
            System.out.println("Similarity: " + r.getSimilarity());
        }
    }
}