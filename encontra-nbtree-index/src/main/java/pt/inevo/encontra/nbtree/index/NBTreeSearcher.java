package pt.inevo.encontra.nbtree.index;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;
import akka.dispatch.CompletableFuture;
import akka.dispatch.Future;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import pt.inevo.encontra.descriptors.Descriptor;
import pt.inevo.encontra.descriptors.DescriptorExtractor;
import pt.inevo.encontra.descriptors.DescriptorList;
import pt.inevo.encontra.index.EntryProvider;
import pt.inevo.encontra.index.IndexedObject;
import pt.inevo.encontra.index.Result;
import pt.inevo.encontra.index.ResultSet;
import pt.inevo.encontra.index.search.AbstractSearcher;
import pt.inevo.encontra.index.search.ResultsProvider;
import pt.inevo.encontra.query.CriteriaQuery;
import pt.inevo.encontra.query.Query;
import pt.inevo.encontra.query.QueryParserNode;
import pt.inevo.encontra.query.criteria.exps.Similar;
import pt.inevo.encontra.storage.IEntity;
import pt.inevo.encontra.storage.IEntry;
import scala.Option;

/**
 * NBTree searcher. Searches in the underlying B+Tree using the NBTree
 * searching solution.
 * @author Ricardo
 * @param <O>
 */
public class NBTreeSearcher<O extends IEntity> extends AbstractSearcher<O> {

    protected DescriptorExtractor extractor;
    protected List<ActorRef> searchActors;

    class NBTreeResultsProvider implements ResultsProvider<O> {

        private String iteratorType;
        private Descriptor queryDescriptor;
        private DescriptorList iteratorList;
        private Iterator<Descriptor> resultIt;
        private ActorRef searchCoordinator;

        NBTreeResultsProvider(String queryType, Descriptor d) {
            this.iteratorType = queryType;
            this.queryDescriptor = d;

            setIteratorList(false);
        }

        private void setIteratorList(boolean previousSearch) {
            if (searchCoordinator == null) {
                searchCoordinator = UntypedActor.actorOf(new UntypedActorFactory() {

                    @Override
                    public UntypedActor create() {
                        return new NBTreeSearchCoordinator();
                    }
                });
            }
            searchCoordinator.start();

            Message m = new Message();
            m.operation = "SEARCH";
            m.obj = queryDescriptor;
            m.howMany = 20;
            m.previousSearch = previousSearch;

            Future future = searchCoordinator.sendRequestReplyFuture(m, Long.MAX_VALUE, null);
            future.await();

            if (future.isCompleted()) {
                Option resultOption = future.result();
                if (resultOption.isDefined()) {
                    //everything is ok, so the results were retrieved
                    Object result = resultOption.get();
                    if (!(result instanceof DescriptorList)) {
                        System.out.println("Processor returned results with wrong type.");
                    } else {
                        iteratorList = (DescriptorList) result;
                        resultIt = iteratorList.iterator();
                    }
                } else {
                    System.out.println("Processor didn't return a result.");
                }
            }
        }

        @Override
        public Result<O> getNext() {
            if (iteratorType.equals("SIMILAR")) {
                if (!resultIt.hasNext()) {
                    int oldSize = iteratorList.getSize();
                    setIteratorList(true);
                    resultIt = iteratorList.iterator();
                    for (int i = 0; i < oldSize && resultIt.hasNext(); i++) {
                        resultIt.next();
                    }
                }
                if (resultIt.hasNext()) {
                    Descriptor descr = resultIt.next();
                    Result<Descriptor> result = new Result<Descriptor>(descr);
                    result.setSimilarity(descr.getDistance(queryDescriptor)); // TODO - This is distance not similarity!!!

                    Result<O> r = new Result<O>((O) getDescriptorExtractor().getIndexedObject((Descriptor) result.getResult()));
                    r.setSimilarity(result.getSimilarity());
                    return r;
                } else {
                    return null;
                }
            } else {
                //must be carefull so this null doesn't pop out
                return null;
            }
        }

        @Override
        public List<Result<O>> getNext(int next) {
            return null;
        }
    }

    @Override
    public ResultsProvider<O> getResultsProvider(Query query) {
        if (query instanceof CriteriaQuery) {
            //parse the query
            QueryParserNode node = queryProcessor.getQueryParser().parse(query);
            //make the query
            if (node.predicateType.equals(Similar.class)) {
                Descriptor d = getDescriptorExtractor().extract(new IndexedObject(null, node.fieldObject));
                return new NBTreeResultsProvider("SIMILAR", d);
            } else {
                //we are not supporting any other
                return null;
            }
        }
        return null;
    }

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
        public int howMany;
        public boolean previousSearch;
    }

    class NBTreeSearchCoordinator extends UntypedActor {

        protected int count = 0;
        protected ActorRef originalActor;
        protected CompletableFuture future;
        protected ActorRef leftSearchActor, rightSearchActor;
        protected DescriptorList resultDescriptor;
        protected Descriptor leftDescriptor, rightDescriptor;

        NBTreeSearchCoordinator() {
            //initialize the two searchers, each one with a different provider
            initSearchActors();
        }

        private void initSearchActors() {
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
                if (message.previousSearch) {
                    resultDescriptor.setMaxSize(resultDescriptor.getSize() * 2);
                    initSearchActors();
                } else {
                    resultDescriptor = new DescriptorList(message.howMany, (Descriptor) message.obj);
                }

                if (getContext().getSenderFuture().isDefined()) {
                    future = (CompletableFuture) getContext().getSenderFuture().get();
                } else if (getContext().getSender().isDefined()) {
                    originalActor = (ActorRef) getContext().getSender().get();
                }

                Message goLeft = new Message();
                goLeft.operation = "GOLEFT";
                goLeft.obj = message.obj;
                if (message.previousSearch) {
                    goLeft.obj = leftDescriptor;
                }

                leftSearchActor.start();
                leftSearchActor.sendOneWay(goLeft, getContext());

                Message goRight = new Message();
                goRight.operation = "GORIGHT";
                goRight.obj = message.obj;
                if (message.previousSearch) {
                    goRight.obj = rightDescriptor;
                }

                rightSearchActor.start();
                rightSearchActor.sendOneWay(goRight, getContext());
            } else if (message.operation.equals("RESULT")) {
                Descriptor desc = (Descriptor) message.obj;
                //keep track of the last descriptor sent by the actor
                if (getContext().getSender().get().equals(leftSearchActor)) {
                    leftDescriptor = desc;
                } else {
                    rightDescriptor = desc;
                }
                if (!resultDescriptor.contains(desc)) {
                    //insert only if it doesn't already exists
                    if (!resultDescriptor.addDescriptor(desc)) {
                        //just don't ask for many results from here
                        count++;
                    } else {
                        Message getNext = new Message();
                        getNext.operation = "NEXT";
                        if (getContext().getSender().get().equals(leftSearchActor)) {
                            leftSearchActor.sendOneWay(getNext, getContext());
                        } else {
                            rightSearchActor.sendOneWay(getNext, getContext());
                        }
                    }
                }
            } else if (message.operation.equals("EMPTY")) {
                count++;
            }

            if (count >= 2) {
                if (originalActor != null) {
                    originalActor.sendOneWay(resultDescriptor);
                } else {
                    future.completeWithResult(resultDescriptor);
                }
            }
        }
    }

    class NBTreeSearchActor extends UntypedActor {

        protected EntryProvider<Descriptor> provider;
        protected Descriptor lastDescriptor;
        protected String direction;

        NBTreeSearchActor(EntryProvider<Descriptor> provider) {
            this.provider = provider;
        }

        private void sendPrevious() {
            Descriptor p = null;
            Message answer = new Message();
            if (provider.hasPrevious()) {
                p = provider.getPrevious();
                answer.operation = "RESULT";
                answer.obj = p;
                lastDescriptor = p;
            } else {
                answer.operation = "EMPTY";
            }
            getContext().replySafe(answer);
        }

        private void sendNext() {
            Descriptor p = null;
            Message answer = new Message();
            if (provider.hasNext()) {
                p = provider.getNext();
                answer.operation = "RESULT";
                answer.obj = p;
                lastDescriptor = p;
            } else {
                answer.operation = "EMPTY";
            }
            getContext().replySafe(answer);
        }

        @Override
        public void onReceive(Object o) throws Exception {
            Message message = (Message) o;
            Descriptor cursor = (Descriptor) message.obj;
            if (message.operation.equals("GOLEFT")) {
                this.direction = message.operation;
                provider.setCursor(cursor);
                sendPrevious();
            } else if (message.operation.equals("GORIGHT")) {
                this.direction = message.operation;
                provider.setCursor(cursor);
                sendNext();
            } else if (message.operation.equals("NEXT")) {
                if (direction.equals("GOLEFT")) {
                    sendPrevious();
                } else {
                    sendNext();
                }
            } else {
                System.out.println("Don't know what to do!");
            }
        }
    }

    protected ResultSet<IEntry> performKnnQuery(Descriptor d, int maxHits) {

        ResultSet resultSet = new ResultSet<Descriptor>();

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
            Option resultOption = future.result();
            if (resultOption.isDefined()) {
                //everything is ok, so the results were retrieved
                Object result = resultOption.get();
                if (!(result instanceof DescriptorList)) {
                    System.out.println("Processor returned results with wrong type.");
                } else {
                    DescriptorList resultsList = (DescriptorList) result;
                    for (Descriptor descr : resultsList.getDescriptors()) {
                        Result<Descriptor> resDesc = new Result<Descriptor>(descr);
                        resDesc.setSimilarity(descr.getDistance(d)); // TODO - This is distance not similarity!!!
                        resultSet.add(resDesc);
                    }

                    resultSet.normalizeScores();
                    resultSet.invertScores(); // This is a distance (dissimilarity) and we need similarity
                }
            } else {
                System.out.println("Processor didn't return a result.");
            }
        }

        return resultSet;
    }

    @Override
    protected Result<O> getResultObject(Result<IEntry> indexEntryresult) {
        return new Result<O>((O) getDescriptorExtractor().getIndexedObject((Descriptor) indexEntryresult.getResult()));
    }
}
