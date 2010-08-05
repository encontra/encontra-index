package pt.inevo.encontra.nbtree.index;

import pt.inevo.encontra.btree.DescriptorList;
import pt.inevo.encontra.descriptors.Descriptor;
import pt.inevo.encontra.descriptors.DescriptorExtractor;
import pt.inevo.encontra.index.IndexedObject;
import pt.inevo.encontra.index.Result;
import pt.inevo.encontra.index.ResultSet;
import pt.inevo.encontra.index.search.Searcher;
import pt.inevo.encontra.query.KnnQuery;
import pt.inevo.encontra.query.Query;
import pt.inevo.encontra.query.Query.QueryType;
import pt.inevo.encontra.storage.EntityStorage;
import pt.inevo.encontra.storage.IEntity;
import pt.inevo.encontra.storage.IEntry;

public class NBTreeSearcher<O extends IEntity> implements Searcher<O> {

    protected DescriptorExtractor extractor;
    protected BTreeIndex<Descriptor> index;
    protected EntityStorage storage;

    public void setDescriptorExtractor(DescriptorExtractor extractor) {
        this.extractor = extractor;
    }

    public DescriptorExtractor getDescriptorExtractor() {
        return extractor;
    }

    public void setIndex(BTreeIndex index) {
        this.index = index;
    }

    public BTreeIndex getIndex() {
        return index;
    }

    @Override
    public void setObjectStorage(EntityStorage storage) {
        this.storage = storage;
    }

    @Override
    public EntityStorage getObjectStorage() {
        return storage;
    }

    @Override
    public QueryType[] getSupportedQueryTypes() {
        return new QueryType[]{QueryType.KNN};
    }

    @Override
    public boolean supportsQueryType(QueryType type) {
        if (type.equals(QueryType.KNN)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean insert(O entry) {
        assert (entry != null);
        Descriptor descriptor = extractor.extract(entry);
        return index.insert(descriptor);
    }

    @Override
    public ResultSet<O> search(Query query) {
        ResultSet<IEntry> results = new ResultSet<IEntry>();
        if (supportsQueryType(query.getType())) {
            if (query.getType().equals(Query.QueryType.KNN)) {
                KnnQuery q = (KnnQuery) query;
                Descriptor d = getDescriptorExtractor().extract((IndexedObject) q.getQuery());
                results = performKnnQuery(d, q.getKnn());
            }
        }

        return getResultObjects(results);
    }

    protected ResultSet<IEntry> performKnnQuery(Descriptor d, int maxHits) {

        ResultSet resultSet = new ResultSet<Descriptor>();
        DescriptorList results = new DescriptorList(maxHits, d);

        //linear knn search, start from the beginning
        index.begin();
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

        for (Descriptor descr : results.getDescriptors()) {
            Result<Descriptor> result = new Result<Descriptor>(descr);
            result.setSimilarity(descr.getDistance(d)); // TODO - This is distance not similarity!!!
            resultSet.add(result);
        }
        //IS THIS NECESSARY
        resultSet.normalizeScores();
        resultSet.invertScores(); // This is a distance (dissimilarity) and we need similarity

        return resultSet;
    }

    protected Result<O> getResultObject(Result<IEntry> indexEntryresult) {
        return new Result<O>((O) getDescriptorExtractor().getIndexedObject((Descriptor) indexEntryresult.getResult()));
    }

    protected ResultSet<O> getResultObjects(ResultSet<IEntry> indexEntryResultSet) {
        ResultSet<O> results = new ResultSet<O>();

        for (Result entryResult : indexEntryResultSet) {
            Result r = getResultObject(entryResult);
            r.setSimilarity(entryResult.getSimilarity());
            results.add(r);
        }
        return results;
    }
}

