package pt.inevo.encontra.nbtree.index;

import pt.inevo.encontra.common.Result;
import pt.inevo.encontra.common.ResultSet;
import pt.inevo.encontra.common.ResultSetDefaultImpl;
import pt.inevo.encontra.descriptors.Descriptor;
import pt.inevo.encontra.descriptors.DescriptorExtractor;
import pt.inevo.encontra.index.EntryProvider;
import pt.inevo.encontra.index.IndexedObject;
import pt.inevo.encontra.index.search.AbstractSearcher;
import pt.inevo.encontra.query.CriteriaQuery;
import pt.inevo.encontra.query.Query;
import pt.inevo.encontra.query.QueryParserNode;
import pt.inevo.encontra.query.criteria.exps.Similar;
import pt.inevo.encontra.storage.IEntity;
import pt.inevo.encontra.storage.IEntry;

import javax.persistence.criteria.Expression;

/**
 * NBTree searcher. Searches in the underlying B+Tree using the NBTree
 * searching solution.
 *
 * @param <O>
 * @author Ricardo
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
        ResultSet<IEntry> results = new ResultSetDefaultImpl<IEntry>();

        if (query instanceof CriteriaQuery) {
            QueryParserNode node = queryProcessor.getQueryParser().parse(query);
            if (node.predicateType.equals(Similar.class)) {
                //can only process similar queries
                Descriptor d = getDescriptorExtractor().extract(new IndexedObject(null, node.fieldObject));
                results = performKnnQuery(d, index.getEntryProvider().size());
            } else {
                return getResultObjects(queryProcessor.search(query), null);
            }
        }

        return getResultObjects(results, null);
    }

    protected ResultSet<IEntry> performKnnQuery(Descriptor d, int maxHits) {

        Result r = new Result(d);
        ResultSet resultSet = new ResultSetDefaultImpl<Descriptor>(r, maxHits);
        getResultProvider().setResultSet(resultSet);

        EntryProvider<Descriptor> provider = index.getEntryProvider();

        provider.setCursor(d);
        while (provider.hasNext()) {
            Descriptor p = provider.getNext();
            //insert only if it doesn't already exists
            Result rs = new Result(p);
            rs.setScore(d.getDistance(p));
            if (!resultSet.add(rs)) {
                /*we are not improving the results going
           this way, so stop the search*/
                break;
            }
        }

        //two way knn - other way
        provider.setCursor(d);
        while (provider.hasPrevious()) {
            Descriptor p = provider.getPrevious();
            //insert only if it doesn't already exists
            Result rs = new Result(p);
            rs.setScore(d.getDistance(p));
            if (!resultSet.add(rs)) {
                /*we are not improving the results going
           this way, so stop the search*/
               break;
            }
        }

        resultSet.normalizeScores();
        resultSet.invertScores(); // This is a distance (dissimilarity) and we need similarity

        return resultSet.getCopy();
    }

    @Override
    protected Result<O> getResultObject(Result<IEntry> indexEntryresult, String criteria) {
        return new Result<O>((O) getDescriptorExtractor().getIndexedObject((Descriptor) indexEntryresult.getResultObject()));
    }
}
