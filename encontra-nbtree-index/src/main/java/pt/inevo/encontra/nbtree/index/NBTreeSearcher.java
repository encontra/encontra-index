package pt.inevo.encontra.nbtree.index;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;
import akka.dispatch.CompletableFuture;
import akka.dispatch.Future;
import java.util.ArrayList;
import java.util.List;
import pt.inevo.encontra.btree.DescriptorList;
import pt.inevo.encontra.descriptors.Descriptor;
import pt.inevo.encontra.descriptors.DescriptorExtractor;
import pt.inevo.encontra.index.EntryProvider;
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
    protected List<ActorRef> searchActors;
    protected DescriptorList resultList;

    public NBTreeSearcher() {
        searchActors = new ArrayList<ActorRef>();
    }

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
            QueryParserNode node = queryProcessor.getQueryParser().parse(query);
            if (node.predicateType.equals(Similar.class)) {
                //can only process simple queries: similar, equals, etc.
                Descriptor d = getDescriptorExtractor().extract(new IndexedObject(null, node.fieldObject));
                results = performKnnQuery(d, 10);
            } else {
                return getResultObjects(queryProcessor.search(query));
            }
        }

        results.sort();
        return getResultObjects(results);
    }

    class Message {
        public String operation;
        public Object obj;
    }

    class NBTreeSearchCoordinator extends UntypedActor {

        protected int count = 0;
        protected ActorRef originalActor;
        protected CompletableFuture future;
        protected ActorRef leftSearchActor, rightSearchActor;

        NBTreeSearchCoordinator() {
            //initialize the two searchers, each one with a different provider
            final EntryProvider<Descriptor> provider = index.getEntryProvider();
            leftSearchActor = UntypedActor.actorOf(new UntypedActorFactory() {
                @Override
                public UntypedActor create() {
                    return new NBTreeSearchActor(provider);
                }
            });

            final EntryProvider<Descriptor> provider2 = index.getEntryProvider();
            rightSearchActor = UntypedActor.actorOf(new UntypedActorFactory() {
                @Override
                public UntypedActor create() {
                    return new NBTreeSearchActor(provider2);
                }
            });
        }

        @Override
        public void onReceive(Object o) throws Exception {
            Message message = (Message) o;
            if (message.operation.equals("SEARCH")) {
                count = 0;
                if (getContext().getSenderFuture().isDefined()) {
                    future = (CompletableFuture) getContext().getSenderFuture().get();
                } else if (getContext().getSender().isDefined()) {
                    originalActor = (ActorRef) getContext().getSender().get();
                }

                Message goLeft = new Message();
                goLeft.operation = "GOLEFT";
                goLeft.obj = message.obj;

                leftSearchActor.start();
                leftSearchActor.sendOneWay(goLeft, getContext());

                Message goRight = new Message();
                goRight.operation = "GORIGHT";
                goRight.obj = message.obj;

                rightSearchActor.start();
                rightSearchActor.sendOneWay(goRight, getContext());
            } else if (message.operation.equals("FINISHED")) {
                count++;
                if (count == 2) {
                    if (originalActor != null) {
                        originalActor.sendOneWay(true);
                    } else {
                        future.completeWithResult(true);
                    }
                }
            }
        }
    }

    class NBTreeSearchActor extends UntypedActor {

        protected EntryProvider<Descriptor> provider;

        NBTreeSearchActor(EntryProvider<Descriptor> provider) {
            this.provider = provider;
        }

        @Override
        public void onReceive(Object o) throws Exception {
            Message message = (Message) o;
            Descriptor cursor = (Descriptor) message.obj;
            if (message.operation.equals("GOLEFT")) {
                provider.setCursor(cursor);
                while (provider.hasNext()) {
                    Descriptor p = provider.getNext();
                    if (!resultList.contains(p)) {
                        //insert only if it doesn't already exists
                        if (!resultList.addDescriptor(p)) {
                            /*we are not improving the resultList going
                            this way, so stop the search*/
                            break;
                        }
                    }
                }
            } else if (message.operation.equals("GORIGHT")) {
                provider.setCursor(cursor);
                while (provider.hasPrevious()) {
                    Descriptor p = provider.getPrevious();
                    if (!resultList.contains(p)) {
                        //insert only if it doesn't already exists
                        if (!resultList.addDescriptor(p)) {
                            /*we are not improving the resultList going
                            this way, so stop the search*/
                            break;
                        }
                    }
                }
            } else {
                System.out.println("Don't know what to do!");
            }

            Message m = new Message();
            m.operation = "FINISHED";
            getContext().replySafe(m);
        }
    }

    protected ResultSet<IEntry> performKnnQuery(Descriptor d, int maxHits) {

        ResultSet resultSet = new ResultSet<Descriptor>();
        resultList = new DescriptorList(maxHits, d);

        ActorRef searchCoordinator = UntypedActor.actorOf(new UntypedActorFactory() {

            @Override
            public UntypedActor create() {
                return new NBTreeSearchCoordinator();
            }
        }).start();

        Message m = new Message();
        m.operation = "SEARCH";
        m.obj = d;

        Future future = searchCoordinator.sendRequestReplyFuture(m, Long.MAX_VALUE, null);
        future.await();

        if (future.isCompleted()) {
            for (Descriptor descr : resultList.getDescriptors()) {
                Result<Descriptor> result = new Result<Descriptor>(descr);
                result.setSimilarity(descr.getDistance(d)); // TODO - This is distance not similarity!!!
                resultSet.add(result);
            }

            resultSet.normalizeScores();
            resultSet.invertScores(); // This is a distance (dissimilarity) and we need similarity
        }
        return resultSet;
    }

    @Override
    protected Result<O> getResultObject(Result<IEntry> indexEntryresult) {
        return new Result<O>((O) getDescriptorExtractor().getIndexedObject((Descriptor) indexEntryresult.getResult()));
    }
}
