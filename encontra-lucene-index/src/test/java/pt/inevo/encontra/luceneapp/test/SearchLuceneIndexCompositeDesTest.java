//package pt.inevo.encontra.luceneapp.test;
//
//import java.io.FileNotFoundException;
//
//import junit.framework.TestCase;
//import org.junit.Test;
//import pt.inevo.encontra.descriptors.CompositeDescriptor;
//import pt.inevo.encontra.descriptors.CompositeDescriptorExtractor;
//import pt.inevo.encontra.descriptors.SimpleDescriptor;
//import pt.inevo.encontra.descriptors.SimpleDescriptorExtractor;
//import pt.inevo.encontra.lucene.index.LuceneIndex;
//import pt.inevo.encontra.engine.SimpleEngine;
//import pt.inevo.encontra.engine.SimpleIndexedObjectFactory;
//import pt.inevo.encontra.index.Result;
//import pt.inevo.encontra.index.ResultSet;
//import pt.inevo.encontra.index.search.SimpleSearcher;
//import pt.inevo.encontra.lucene.index.LuceneIndexEntryFactory;
//import pt.inevo.encontra.query.CriteriaQuery;
//import pt.inevo.encontra.query.Path;
//import pt.inevo.encontra.query.QueryProcessorDefaultImpl;
//import pt.inevo.encontra.query.criteria.CriteriaBuilderImpl;
//import pt.inevo.encontra.storage.EntityStorage;
//import pt.inevo.encontra.storage.SimpleObjectStorage;
//
///**
// * Test for searching through a LuceneIndex.
// * @author ricardo
// */
//public class SearchLuceneIndexCompositeDesTest extends TestCase {
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
//    @Test
//    public void testMain() throws FileNotFoundException {
//        EntityStorage storage = new SimpleObjectStorage(ObjectModel.class);
//
//        SimpleEngine<ObjectModel> e = new SimpleEngine<ObjectModel>();
//        e.setQueryProcessor(new QueryProcessorDefaultImpl());
//        e.setObjectStorage(storage);
//        e.getQueryProcessor().setIndexedObjectFactory(new SimpleIndexedObjectFactory());
//        LuceneIndex index = new LuceneIndex("luceneSearchComposite", CompositeDescriptor.class);
//
//        SimpleSearcher searcher = new SimpleSearcher();
//        searcher.setIndex(index);
//
//        CompositeDescriptorExtractor compositeDescriptorExtractor = new CompositeDescriptorExtractor(ObjectModel.class);
//        compositeDescriptorExtractor.addExtractor(new SimpleDescriptorExtractor(SimpleDescriptor.class), 1);
//        compositeDescriptorExtractor.addExtractor(new SimpleDescriptorExtractor(SimpleDescriptor.class), 1);
//
//        searcher.setDescriptorExtractor(compositeDescriptorExtractor);
//        e.getQueryProcessor().setSearcher("content", searcher);
//
//        LuceneIndexEntryFactory<CompositeDescriptor> entryFactory = new LuceneIndexEntryFactory<CompositeDescriptor>(CompositeDescriptor.class);
//        index.setEntryFactory(entryFactory);
//
//        //inserting some objects in the index
//        for (int i = 1; i < 100; i++) {
//            ObjectModel object = new ObjectModel("Does it work? Event number " + i);
//            e.insert(object);
//        }
//
//        //performing the query
//        ObjectModel queryObject = new ObjectModel("Does it work? Yes it works");
//        queryObject.setId(new Long(101));
//
//        CriteriaBuilderImpl cb = new CriteriaBuilderImpl();
//        CriteriaQuery<ObjectModel> criteriaQuery = cb.createQuery(ObjectModel.class);
//
//        //Create the Model/Attributes Path
//        Path<ObjectModel> model = criteriaQuery.from(ObjectModel.class);
//
//        //Create the Query
//        CriteriaQuery query = cb.createQuery().where(cb.similar(model, queryObject));
//
//        ResultSet<ObjectModel> results = e.search(query);
//        System.out.println("The results for this query are: ");
//        for (Result<ObjectModel> r : results) {
//            System.out.println(r.getResult());
//        }
//    }
//}
