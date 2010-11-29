//package pt.inevo.encontra.luceneapp.test;
//
//import java.io.FileNotFoundException;
//
//import junit.framework.TestCase;
//import pt.inevo.encontra.descriptors.CompositeDescriptor;
//import pt.inevo.encontra.descriptors.CompositeDescriptorExtractor;
//import pt.inevo.encontra.lucene.index.LuceneIndex;
//import pt.inevo.encontra.engine.SimpleEngine;
//import pt.inevo.encontra.engine.SimpleIndexedObjectFactory;
//import pt.inevo.encontra.index.Result;
//import pt.inevo.encontra.index.ResultSet;
//import pt.inevo.encontra.index.annotation.Indexed;
//import pt.inevo.encontra.index.search.SimpleSearcher;
//import pt.inevo.encontra.lucene.index.LuceneIndexEntryFactory;
//import pt.inevo.encontra.query.CriteriaQuery;
//import pt.inevo.encontra.query.Path;
//import pt.inevo.encontra.query.QueryProcessorDefaultImpl;
//import pt.inevo.encontra.query.criteria.CriteriaBuilderImpl;
//import pt.inevo.encontra.storage.EntityStorage;
//import pt.inevo.encontra.storage.IEntity;
//import pt.inevo.encontra.storage.SimpleObjectStorage;
//
///**
// * Test for searching through a LuceneIndex.
// * @author ricardo
// */
//public class SearchLuceneIndexCompositeDesTest extends TestCase {
//
//    /**
//     * Indexable fields are marked with the @Indexed annotation
//     * @author Ricardo
//     */
//    class MetaTestModel implements IEntity<Long> {
//
//        private Long id;
//        private String title;
//        private String content;
//
//        public MetaTestModel(String title, String content) {
//            this.title = title;
//            this.content = content;
//        }
//
//        @Override
//        public Long getId() {
//            return id;
//        }
//
//        @Override
//        public void setId(Long id) {
//            this.id = id;
//        }
//
//        @Indexed
//        public String getTitle() {
//            return title;
//        }
//
//        public void setTitle(String title) {
//            this.title = title;
//        }
//
//        @Indexed
//        public String getContent() {
//            return content;
//        }
//
//        public void setContent(String content) {
//            this.content = content;
//        }
//
//        @Override
//        public String toString() {
//            return "MetaTestModel{"
//                    + "id=" + id
//                    + ", title='" + title + '\''
//                    + ", content='" + content + '\''
//                    + '}';
//        }
//    }
//
//    public SearchLuceneIndexCompositeDesTest(String testName) {
//        super(testName);
//    }
//
//    @Override
//    protected void setUp() throws Exception {
//        super.setUp();
//    }
//
//    @Override
//    protected void tearDown() throws Exception {
//        super.tearDown();
//    }
//
//    public void testMain() throws FileNotFoundException {
//        EntityStorage storage = new SimpleObjectStorage(TestObject.class);
//
//        SimpleEngine<MetaTestModel> e = new SimpleEngine<MetaTestModel>();
//        e.setQueryProcessor(new QueryProcessorDefaultImpl());
//        e.setObjectStorage(storage);
//        e.getQueryProcessor().setIndexedObjectFactory(new SimpleIndexedObjectFactory());
//        LuceneIndex index = new LuceneIndex("luceneSearchComposite", MetaTestModel.class);
//
//        SimpleSearcher searcher = new SimpleSearcher();
//        searcher.setIndex(index);
//
//        CompositeDescriptorExtractor compositeDescriptorExtractor = new CompositeDescriptorExtractor(TestObject.class, null);
//        compositeDescriptorExtractor.addExtractor(new D1Extractor(), 1);
//        compositeDescriptorExtractor.addExtractor(new D2Extractor(), 1);
//
//        searcher.setDescriptorExtractor(compositeDescriptorExtractor);
//        e.getQueryProcessor().setSearcher(TestObject.class.getName(), searcher);
//
//        LuceneIndexEntryFactory<CompositeDescriptor> entryFactory = new LuceneIndexEntryFactory<CompositeDescriptor>(CompositeDescriptor.class);
//        index.setEntryFactory(entryFactory);
//
//        //inserting some objects in the index
//        for (int i = 1; i < 100; i++) {
//            TestObject object = new TestObject();
//            object.setId(i);
//            object.setValue("Does it work? Event number " + i);
//            e.insert(object);
//        }
//
//        //performing the query
//        TestObject queryObject = new TestObject();
//        queryObject.setId(101);
//        queryObject CriteriaBuilderImpl cb = new CriteriaBuilderImpl();
//        CriteriaQuery<TestObject> criteriaQuery = cb.createQuery(TestObject.class);
//
//        //Create the Model/Attributes Path
//        Path<TestObject> model = criteriaQuery.from(TestObject.class);
//
//        //Create the Query
//        CriteriaQuery query = cb.createQuery().where(cb.similar(model, queryObject));
//
//        ResultSet<TestObject> results = e.search(query);
//        System.out.println("The results for this query are: ");
//        for (Result<TestObject> r : results) {
//            System.out.println(r.getResult());
//        }
//    }
//}
