package pt.inevo.encontra.nbtree.index;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;
import akka.dispatch.CompletableFuture;
import akka.dispatch.Future;

import java.util.ArrayList;
import java.util.List;

import pt.inevo.encontra.descriptors.Descriptor;
import pt.inevo.encontra.descriptors.DescriptorExtractor;
import pt.inevo.encontra.index.EntryProvider;
import pt.inevo.encontra.index.IndexedObject;
import pt.inevo.encontra.common.Result;
import pt.inevo.encontra.common.ResultSet;
import pt.inevo.encontra.common.ResultSetDefaultImpl;
import pt.inevo.encontra.index.search.AbstractSearcher;
import pt.inevo.encontra.index.search.Searcher;
import pt.inevo.encontra.query.CriteriaQuery;
import pt.inevo.encontra.query.Query;
import pt.inevo.encontra.query.QueryParserNode;
import pt.inevo.encontra.query.criteria.exps.Similar;
import pt.inevo.encontra.storage.IEntity;
import pt.inevo.encontra.storage.IEntry;
import scala.Option;

/**
 * NBTree searcher. Searches in the underlying B+TreeIndex using the NBTree Approach.
 * searching solution. This is a parallel version of the NBTree.
 * @param <O>
 * @author Ricardo
 */
public class ParallelNBTreeSearcher<O extends IEntity> extends AbstractSearcher<O> {

    public ParallelNBTreeSearcher() {
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
                //can only process simple queries: similar, equals, etc.
                Descriptor d = getDescriptorExtractor().extract(new IndexedObject(null, node.fieldObject));
                results = performKnnQuery(d, index.getEntryProvider().size());
                System.out.println();
            } else {
                return getResultObjects(queryProcessor.search(query));
            }
        }

        return getResultObjects(results);
    }

    class Message {

        public String operation;
        public Object obj;
        public int howMany;
        public boolean previousSearch;
    }

    class NBTreeSearchCoordinator extends UntypedActor {

        protected List<ActorRef> stopActors;
        protected ActorRef originalActor;
        protected CompletableFuture future;
        protected ActorRef leftSearchActor, rightSearchActor;
        protected ResultSet<Descriptor> resultDescriptor;
        protected Descriptor leftDescriptor, rightDescriptor, queryDescriptor;

        NBTreeSearchCoordinator() {
            //initialize the two searchers, each one with a different provider
            initSearchActors();
            stopActors = new ArrayList<ActorRef>();
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
                queryDescriptor = (Descriptor) message.obj;
                if (message.previousSearch) {
                    resultDescriptor.setMaxSize(resultDescriptor.getSize() * 2);
                    initSearchActors();
                } else {
                    Result r = new Result((Descriptor) message.obj);
                    resultDescriptor = new ResultSetDefaultImpl<Descriptor>(r, message.howMany);
                    getResultProvider().setResultSet(resultDescriptor);
                }

                //save the original sender, so we can reply later
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
            } else if (message.operation.equals("RESULT") && stopActors.size() < 2) {
                Descriptor desc = (Descriptor) message.obj;

                //keep track of the last descriptor sent by the actor
                ActorRef sender = getContext().getSender().get();
                if (sender.equals(leftSearchActor)) {
                    leftDescriptor = desc;
                } else {
                    rightDescriptor = desc;
                }

                Result r = new Result(desc);
                r.setScore(desc.getDistance(queryDescriptor));

                resultDescriptor.add(r);
            } else if (message.operation.equals("EMPTY")) {
                stopActors.add(getContext().getSender().get());
            }

            if (stopActors.size() >= 2) {
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
        protected Descriptor cursor;

        NBTreeSearchActor(EntryProvider<Descriptor> provider) {
            this.provider = provider;
        }

        private void sendPrevious() {
            Descriptor p = null;
            Message answer = new Message();
            while (provider.hasPrevious()) {
                answer = new Message();
                p = provider.getPrevious();
                answer.operation = "RESULT";
                answer.obj = p;
                lastDescriptor = p;
                getContext().replySafe(answer);
            }
            answer.operation = "EMPTY";
            getContext().replySafe(answer);
        }

        private void sendNext() {
            Descriptor p = null;
            Message answer = new Message();
            while (provider.hasNext()) {
                answer = new Message();
                p = provider.getNext();
                answer.operation = "RESULT";
                answer.obj = p;
                lastDescriptor = p;
                getContext().replySafe(answer);
            }

            answer.operation = "EMPTY";
            getContext().replySafe(answer);
        }

        @Override
        public void onReceive(Object o) throws Exception {
            Message message = (Message) o;
            if (message.operation.equals("GOLEFT")) {
                cursor = (Descriptor) message.obj;
                this.direction = message.operation;
                provider.setCursor(cursor);
                sendPrevious();
            } else if (message.operation.equals("GORIGHT")) {
                cursor = (Descriptor) message.obj;
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

        ResultSet resultSet = new ResultSetDefaultImpl<Descriptor>();
        ActorRef searchCoordinator = UntypedActor.actorOf(new UntypedActorFactory() {

            @Override
            public UntypedActor create() {
                return new NBTreeSearchCoordinator();
            }
        }).start();

        Message m = new Message();
        m.operation = "SEARCH";
        m.howMany = maxHits;
        m.obj = d;
        m.previousSearch = false;

        Future future = searchCoordinator.sendRequestReplyFuture(m, Long.MAX_VALUE, null);
        future.await();

        if (future.isCompleted()) {
            Option resultOption = future.result();
            if (resultOption.isDefined()) {
                //everything is ok, so the results were retrieved
                Object result = resultOption.get();
                if (!(result instanceof ResultSet)) {
                    System.out.println("Processor returned results with wrong type.");
                } else {
                    resultSet = (ResultSet<Descriptor>) result;
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
        return new Result<O>((O) getDescriptorExtractor().getIndexedObject((Descriptor) indexEntryresult.getResultObject()));
    }
}
