package pt.inevo.encontra.luceneapp.test;

import java.io.FileNotFoundException;

import junit.framework.TestCase;
import org.junit.Test;
import pt.inevo.encontra.lucene.index.LuceneIndex;
import pt.inevo.encontra.descriptors.SimpleDescriptor;
import pt.inevo.encontra.descriptors.SimpleDescriptorExtractor;
import pt.inevo.encontra.engine.SimpleIndexedObjectFactory;
import pt.inevo.encontra.common.Result;
import pt.inevo.encontra.index.ResultSetDefaultImp;
import pt.inevo.encontra.index.search.AbstractSearcher;
import pt.inevo.encontra.index.search.SimpleSearcher;
import pt.inevo.encontra.lucene.index.LuceneIndexEntryFactory;
import pt.inevo.encontra.query.CriteriaQuery;
import pt.inevo.encontra.query.Path;
import pt.inevo.encontra.query.QueryProcessorDefaultImpl;
import pt.inevo.encontra.query.criteria.CriteriaBuilderImpl;
import pt.inevo.encontra.storage.IEntity;
import pt.inevo.encontra.storage.IEntry;
import pt.inevo.encontra.storage.SimpleObjectStorage;

/**
 * Test for searching through a LuceneIndex.
 * @author ricardo
 */
public class SearchLuceneIndexTest extends TestCase {

    class LuceneEngine<O extends IEntity> extends AbstractSearcher<O> {

    @Override
    protected Result<O> getResultObject(Result<IEntry> entryresult) {
        return new Result<O>((O) storage.get(
                Long.parseLong((String) entryresult.getResultObject().getId())));
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

    @Test
    public void testMain() throws FileNotFoundException {

        LuceneEngine<ObjectModel> e = new LuceneEngine<ObjectModel>();
        e.setQueryProcessor(new QueryProcessorDefaultImpl());
        e.setObjectStorage(new SimpleObjectStorage(ObjectModel.class));
        e.getQueryProcessor().setIndexedObjectFactory(new SimpleIndexedObjectFactory());

        LuceneIndex index = new LuceneIndex("luceneSearch", SimpleDescriptor.class);
        SimpleSearcher searcher = new SimpleSearcher();
        searcher.setIndex(index);

        LuceneIndexEntryFactory<SimpleDescriptor> entryFactory = new LuceneIndexEntryFactory<SimpleDescriptor>(SimpleDescriptor.class);
        index.setEntryFactory(entryFactory);

        SimpleDescriptorExtractor d = new SimpleDescriptorExtractor(SimpleDescriptor.class);
        searcher.setDescriptorExtractor(d);

        e.getQueryProcessor().setSearcher("content", searcher);

        //inserting some objects in the index
        for (int i = 1; i < 10; i++) {
            ObjectModel object = new ObjectModel("object" + i);
            e.insert(object);
        }

        //performing the query
        ObjectModel queryObject = new ObjectModel("object1");
        queryObject.setId(Long.MIN_VALUE);

        CriteriaBuilderImpl cb = new CriteriaBuilderImpl();
        CriteriaQuery<ObjectModel> criteriaQuery = cb.createQuery(ObjectModel.class);

        //Create the Model/Attributes Path
        Path<ObjectModel> model = criteriaQuery.from(ObjectModel.class);
        //Create the Query
        CriteriaQuery query = cb.createQuery().where(cb.similar(model, queryObject));

        ResultSetDefaultImp<ObjectModel> results = e.search(query);
        System.out.println("The results for this query are: ");
        for (Result<ObjectModel> r : results) {
            System.out.println(r.getResultObject());
        }
    }
}