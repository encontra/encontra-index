//package pt.inevo.encontra.nbtree.test;
//
//import java.awt.image.BufferedImage;
//import java.io.File;
//import java.io.IOException;
//import java.io.Serializable;
//import java.util.List;
//import javax.imageio.ImageIO;
//import pt.inevo.encontra.descriptors.DescriptorExtractor;
//import pt.inevo.encontra.engine.SimpleEngine;
//import pt.inevo.encontra.engine.Engine;
//import junit.framework.TestCase;
//import pt.inevo.encontra.descriptors.CompositeDescriptor;
//import pt.inevo.encontra.descriptors.CompositeDescriptorExtractor;
//import pt.inevo.encontra.descriptors.Descriptor;
//import pt.inevo.encontra.descriptors.SimpleDescriptorExtractor;
//import pt.inevo.encontra.engine.SimpleIndexedObjectFactory;
//import pt.inevo.encontra.image.descriptors.ColorLayoutDescriptor;
//import pt.inevo.encontra.image.descriptors.ScalableColorDescriptor;
//import pt.inevo.encontra.index.*;
//import pt.inevo.encontra.index.search.SimpleCombinedSearcher;
//import pt.inevo.encontra.index.search.SimpleSearcher;
//import pt.inevo.encontra.nbtree.index.BTreeIndex;
//import pt.inevo.encontra.nbtree.index.NBTreeSearcher;
//import pt.inevo.encontra.query.*;
//import pt.inevo.encontra.storage.*;
//
///**
// * Smoke test: testing the creation of a simple engine, two indexes and the
// * execution of two random queries -> Using a CompositeDescriptor
// * @author ricardo
// */
//public class NBTreeIndexTest2 extends TestCase {
//
//    /**
//     * Simple searcher for images, using a NBTreeSearcher.
//     * @param <O>
//     */
//    public class SimpleImageSearcher<O extends IndexedObject> extends NBTreeSearcher<O> {
//
//        @Override
//        public ResultSet<O> search(Query query) {
//            ResultSet<IEntry> results = new ResultSet<IEntry>();
//            if (supportsQueryType(query.getType())) {
//                if (query.getType().equals(Query.QueryType.KNN)) {
//                    KnnQuery q = (KnnQuery) query;
//                    IndexedObject o = (IndexedObject) q.getQuery();
//                    if (o.getValue() instanceof BufferedImage) {
//                        Descriptor d = getDescriptorExtractor().extract(o);
//                        results = performKnnQuery(d, q.getKnn());
//                    }
//                }
//            }
//
//            return getResultObjects(results);
//        }
//    }
//
//    /**
//     * Simple textual searcher for the description and filename of the ImageModel
//     * @param <O>
//     */
//    public class SimpleTextSearcher<O extends IndexedObject> extends SimpleSearcher<O> {
//
//        @Override
//        public ResultSet<O> search(Query query) {
//            ResultSet<IEntry> results = new ResultSet<IEntry>();
//            if (supportsQueryType(query.getType())) {
//                if (query.getType().equals(Query.QueryType.KNN)) {
//                    KnnQuery q = (KnnQuery) query;
//                    IndexedObject o = (IndexedObject) q.getQuery();
//                    if (o.getValue() instanceof String) {
//                        Descriptor d = getDescriptorExtractor().extract(o);
//                        results = performKnnQuery(d, q.getKnn());
//                    }
//                }
//            }
//
//            return getResultObjects(results);
//        }
//    }
//
//    public NBTreeIndexTest2(String testName) {
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
//    public void testMain() {
//        //Creating the EntityStorage for saving the objects
//        EntityStorage storage = new SimpleObjectStorage(ImageModel.class);
//
//        //Creating the descriptor extractor for strings
//        DescriptorExtractor stringDescriptorExtractor = new SimpleDescriptorExtractor(StringDescriptor.class);
//
//        //Creating the engine
//        System.out.println("Creating the Retrieval Engine...");
//        Engine<ImageModel> e = new SimpleEngine<ImageModel>();
//        e.setObjectStorage(storage);
//        e.setIndexedObjectFactory(new SimpleIndexedObjectFactory());
//
//        //Creating the searchers for each type of the model
//        SimpleCombinedSearcher searcher = new SimpleCombinedSearcher();
//
//        //A searcher for the filename
//        SimpleTextSearcher filenameSearcher = new SimpleTextSearcher();
//        filenameSearcher.setDescriptorExtractor(stringDescriptorExtractor);
//        filenameSearcher.setIndex(new SimpleIndex(StringDescriptor.class));
//
//        //A searcher for the description
//        SimpleTextSearcher descriptionSearcher = new SimpleTextSearcher();
//        descriptionSearcher.setDescriptorExtractor(stringDescriptorExtractor);
//        descriptionSearcher.setIndex(new SimpleIndex(StringDescriptor.class));
//
//        CompositeDescriptorExtractor compositeImageDescriptorExtractor = new CompositeDescriptorExtractor(IndexedObject.class, null);
//        compositeImageDescriptorExtractor.addExtractor(new ColorLayoutDescriptor<IndexedObject>(), 1);
//        compositeImageDescriptorExtractor.addExtractor(new ScalableColorDescriptor(), 1);
//
//        //A searcher for the image content (using only one type of descriptor
//        SimpleImageSearcher imageSearcher = new SimpleImageSearcher();
//        //using a composite descriptor
//        imageSearcher.setDescriptorExtractor(compositeImageDescriptorExtractor);
//        //using a BTreeIndex
//        imageSearcher.setIndex(new BTreeIndex(CompositeDescriptor.class));
//
//        searcher.addSearcher("filename", filenameSearcher);
//        searcher.addSearcher("description", descriptionSearcher);
//        searcher.addSearcher("image", imageSearcher);
//
//        e.setSearcher(searcher);
//
//        System.out.println("Loading some objects to the test indexes...");
//        ImageModelLoader loader = new ImageModelLoader();
//        List<ImageModel> images = loader.getImages("C:\\Users\\Ricardo\\Desktop\\testcases\\test\\additional_images");
//        for (ImageModel im : images) {
//            e.insert(im);
//        }
//
//        try {
//            System.out.println("Creating a knn query...");
//            BufferedImage image = ImageIO.read(new File("C:\\Users\\Ricardo\\Desktop\\testcases\\28\\28004.jpg"));
//
//            Query knnQuery = new KnnQuery(new IndexedObject<Serializable, BufferedImage>(28004, image), 20);
//            System.out.println("Searching for elements in the engine...");
//            ResultSet<ImageModel> results = e.search(knnQuery);
//
//            System.out.println("Number of retrieved elements: " + results.size());
//            for (Result<ImageModel> r : results) {
//                System.out.print("Retrieved element: " + r.getResult().toString() + "\t");
//                System.out.println("Similarity: " + r.getSimilarity());
//            }
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//    }
//}
