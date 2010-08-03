package pt.inevo.encontra.nbtree.index;

import pt.inevo.encontra.btree.IBTree;
import pt.inevo.encontra.btree.ITuple;
import pt.inevo.encontra.btree.ITupleBrowser;
import pt.inevo.encontra.common.distance.EuclideanDistanceMeasure;
import pt.inevo.encontra.descriptors.Descriptor;
import pt.inevo.encontra.descriptors.DescriptorExtractor;
import pt.inevo.encontra.index.IndexedObject;
import pt.inevo.encontra.index.Result;
import pt.inevo.encontra.index.ResultSet;
import pt.inevo.encontra.index.search.Searcher;
import pt.inevo.encontra.btree.DescriptorList;
import pt.inevo.encontra.nbtree.descriptors.NBTreeDescriptor;
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

        IBTree tree = index.getBTree();
        //        NBTreeDescriptor descriptor = (NBTreeDescriptor) d;
        //KNN search comes here
        final double MAX_FAR = Double.MAX_VALUE;
        final double DELTA = 0.01;
        ITupleBrowser lcursor;
        ITupleBrowser rcursor;
        ITuple tuple;
        DescriptorList results = new DescriptorList(maxHits, d, new EuclideanDistanceMeasure());
        double val;
        double rightLimit;
        double leftLimit;
        double startPoint;
        double farLimit;
        boolean keepSearchRight = true;
        boolean keepSearchLeft = true;
        boolean getPrevious = false;
        boolean getNext = false;

        farLimit = MAX_FAR;
        //        val = descriptor.norm(2);

//            val = origin.getDistance(d);
        val = 0; //TO DO - must change this part here to the distance to the origin point

        startPoint = val;
        rightLimit = startPoint + DELTA;
        leftLimit = startPoint - DELTA;
        rcursor = tree.browse(val);
        lcursor = tree.browse(val);

        while (keepSearchRight || keepSearchLeft) {
            //going right
            if (keepSearchRight) {
                if (getPrevious) {
                    getPrevious = false;
                    tuple = rcursor.getPrevious();
                }
                tuple = rcursor.getNext();
                if (tuple != null) {
                    double searchKey = Double.parseDouble(tuple.getKey().toString());
                    NBTreeDescriptor p = (NBTreeDescriptor) tuple.getEntry();
                    while (searchKey <= rightLimit && keepSearchRight) {
                        if (val <= farLimit) {
                            val = d.getDistance(p);
                        }
                        if (val <= farLimit) {
                            //original version was just '<'
                            if (!results.contains(p)) {
                                //insert only if it doesn't already exists
                                if (!results.addDescriptor(p)) {
                                    /*we are not improving the results going
                                    this way, so stop the search this way*/
                                    keepSearchRight = false;
                                    break;
                                }
                            }
                        }
                        tuple = rcursor.getNext();
                        if (tuple != null) {
                            keepSearchRight = false;
                            break;
                        }
                        searchKey = Double.parseDouble(tuple.getKey().toString());
                        p = (NBTreeDescriptor) tuple.getEntry();
                    }
                    if (keepSearchRight) {
                        rightLimit = Double.parseDouble(tuple.getKey().toString()) + DELTA;
                        getPrevious = true;
                    } else {
                        rightLimit += DELTA;
                        getPrevious = true;
                    }
                } else {
                    keepSearchRight = false;
                }
            }
            if (leftLimit < 0) {
                leftLimit = 0;
            }
            if (keepSearchLeft) {
                if (getNext) {
                    getNext = false;
                    tuple = lcursor.getNext();
                }
                tuple = lcursor.getPrevious();
                if (tuple != null) {
                    double searchKey = Double.parseDouble(tuple.getKey().toString());
                    NBTreeDescriptor p = (NBTreeDescriptor) tuple.getEntry();
                    while (searchKey >= leftLimit && keepSearchLeft) {
                        val = d.getDistance(p);
                        if (val <= farLimit) {
                            //original version is just '<'
                            if (!results.contains(p)) {
                                //insert only if it doesn't already exists
                                if (!results.addDescriptor(p)) {
                                    /*we are not improving the results going
                                    this way, so stop the search this way*/
                                    keepSearchLeft = false;
                                    break;
                                }
                            }
                        }
                        tuple = lcursor.getPrevious();
                        if (tuple != null) {
                            keepSearchLeft = false;
                            break;
                        }
                        searchKey = Double.parseDouble(tuple.getKey().toString());
                        p = (NBTreeDescriptor) tuple.getEntry();
                    }
                    if (keepSearchLeft) {
                        leftLimit = Double.parseDouble(tuple.getKey().toString()) - DELTA;
                        getNext = true;
                    }
                } else {
                    keepSearchLeft = false;
                }
            }
        }
        for (Descriptor desc : results.getDescriptors()) {
            Result<Descriptor> result = new Result<Descriptor>(desc);
            result.setSimilarity(desc.getDistance(d)); // TODO - This is distance not similarity!!!
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

