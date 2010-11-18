package pt.inevo.encontra.nbtree.index;

import java.util.Stack;
import pt.inevo.encontra.btree.DescriptorList;
import pt.inevo.encontra.descriptors.Descriptor;
import pt.inevo.encontra.descriptors.DescriptorExtractor;
import pt.inevo.encontra.index.IndexedObject;
import pt.inevo.encontra.index.Result;
import pt.inevo.encontra.index.ResultSet;
import pt.inevo.encontra.index.search.AbstractSearcher;
import pt.inevo.encontra.query.CriteriaQuery;
import pt.inevo.encontra.query.Query;
import pt.inevo.encontra.query.QueryParserNode;
import pt.inevo.encontra.query.criteria.exps.Similar;
import pt.inevo.encontra.storage.IEntity;
import pt.inevo.encontra.storage.IEntry;

/**
 * NBTree searcher. Searches in the underlying B+Tree using the NBTree
 * searching solution.
 * @author Ricardo
 * @param <O>
 */
public class NBTreeSearcher<O extends IEntity> extends AbstractSearcher<O> {

    protected DescriptorExtractor extractor;

    public void setDescriptorExtractor(DescriptorExtractor extractor) {
        this.extractor = extractor;
    }

    public DescriptorExtractor getDescriptorExtractor() {
        return extractor;
    }

    @Override
    public boolean insert(O entry) {
        assert (entry != null);
        Descriptor descriptor = extractor.extract(entry);
        return index.insert(descriptor);
    }

    @Override
    public boolean remove(O entry) {
        assert (entry != null);
        Descriptor descriptor = extractor.extract(entry);
        return index.remove(descriptor);
    }

    @Override
    public ResultSet<O> search(Query query) {
        ResultSet<IEntry> results = new ResultSet<IEntry>();

        if (query instanceof CriteriaQuery) {

            CriteriaQuery q = (CriteriaQuery) query;
            if (q.getRestriction().getClass().equals(Similar.class)) {
                Stack<QueryParserNode> nodes = queryProcessor.getQueryParser().parse(query);
                //can only process simple queries: similar, equals, etc.
                if (nodes.firstElement().predicateType.equals(Similar.class)) {
                    Descriptor d = getDescriptorExtractor().extract(nodes.firstElement().fieldObject);
                    results = performKnnQuery(d, 10);
                }
            } else {
                return getResultObjects(queryProcessor.search(query));
            }
        }

        return getResultObjects(results);
    }

    protected ResultSet<IEntry> performKnnQuery(Descriptor d, int maxHits) {

        ResultSet resultSet = new ResultSet<Descriptor>();
        DescriptorList results = new DescriptorList(maxHits, d);
        
        //two way knn - one way
        index.setCursor(d);
          while (index.hasNext()) {
            Descriptor p = index.getNext();
            if (!results.contains(p)) {
                //insert only if it doesn't already exists
                if (!results.addDescriptor(p)) {
                    /*we are not improving the results going
                    this way, so stop the search*/
                    break;
                }
            }
        }

        //two way knn - other way
        index.setCursor(d);
          while (index.hasPrevious()) {
            Descriptor p = index.getPrevious();
            if (!results.contains(p)) {
                //insert only if it doesn't already exists
                if (!results.addDescriptor(p)) {
                    /*we are not improving the results going
                    this way, so stop the search*/
                    break;
                }
            }
        }

        for (Descriptor descr : results.getDescriptors()) {
            Result<Descriptor> result = new Result<Descriptor>(descr);
            result.setSimilarity(descr.getDistance(d)); // TODO - This is distance not similarity!!!
            resultSet.add(result);
        }
        
        resultSet.normalizeScores();
        resultSet.invertScores(); // This is a distance (dissimilarity) and we need similarity

        return resultSet;
    }

    @Override
    protected Result<O> getResultObject(Result<IEntry> indexEntryresult) {
        return new Result<O>((O) getDescriptorExtractor().getIndexedObject((Descriptor) indexEntryresult.getResult()));
    }
}