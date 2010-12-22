package pt.inevo.encontra.nbtree.test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import javax.imageio.ImageIO;
import pt.inevo.encontra.descriptors.DescriptorExtractor;
import pt.inevo.encontra.engine.SimpleEngine;
import junit.framework.TestCase;
import org.junit.Test;
import pt.inevo.encontra.descriptors.SimpleDescriptorExtractor;
import pt.inevo.encontra.engine.SimpleIndexedObjectFactory;
import pt.inevo.encontra.image.descriptors.ColorLayoutDescriptor;
import pt.inevo.encontra.index.*;
import pt.inevo.encontra.index.search.SimpleSearcher;
import pt.inevo.encontra.nbtree.index.BTreeIndex;
import pt.inevo.encontra.nbtree.index.NBTreeSearcher;
import pt.inevo.encontra.query.*;
import pt.inevo.encontra.query.criteria.CriteriaBuilderImpl;
import pt.inevo.encontra.storage.*;

/**
 * Smoke test: testing the creation of a simple engine, two indexes and the
 * execution of a similarity query (testing also the combination of the queries).
 * @author ricardo
 */
public class NBTreeIndexTest extends TestCase {

    public NBTreeIndexTest(String testName) {
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
    
    public void testSave() {
        //Creating the EntityStorage for saving the objects
        EntityStorage storage = new SimpleObjectStorage(ImageModel.class);

        //Creating the descriptor extractor for strings
        DescriptorExtractor stringDescriptorExtractor = new SimpleDescriptorExtractor(StringDescriptor.class);

        //Creating the engine
        System.out.println("Creating the Retrieval Engine...");
        SimpleEngine<ImageModel> e = new SimpleEngine<ImageModel>();
        e.setObjectStorage(storage);
        e.setQueryProcessor(new QueryProcessorDefaultImpl());
        e.getQueryProcessor().setIndexedObjectFactory(new SimpleIndexedObjectFactory());

        //A searcher for the filename
        SimpleSearcher filenameSearcher = new SimpleSearcher();
        filenameSearcher.setDescriptorExtractor(stringDescriptorExtractor);
        filenameSearcher.setIndex(new SimpleIndex(StringDescriptor.class));
        filenameSearcher.setQueryProcessor(new QueryProcessorDefaultImpl());

        //A searcher for the description
        SimpleSearcher descriptionSearcher = new SimpleSearcher();
        descriptionSearcher.setDescriptorExtractor(stringDescriptorExtractor);
        descriptionSearcher.setIndex(new SimpleIndex(StringDescriptor.class));
        descriptionSearcher.setQueryProcessor(new QueryProcessorDefaultImpl());

        //A searcher for the image content (using only one type of descriptor
        NBTreeSearcher imageSearcher = new NBTreeSearcher();
        //using a single descriptor
        imageSearcher.setDescriptorExtractor(new ColorLayoutDescriptor<IndexedObject>());
        //using a BTreeIndex
        imageSearcher.setIndex(new BTreeIndex(ColorLayoutDescriptor.class));
        imageSearcher.setQueryProcessor(new QueryProcessorDefaultImpl());

        e.getQueryProcessor().setSearcher("filename", filenameSearcher);
        e.getQueryProcessor().setSearcher("description", descriptionSearcher);
        e.getQueryProcessor().setSearcher("image", imageSearcher);

        System.out.println("Loading some objects to the test indexes...");
        ImageModelLoader loader = new ImageModelLoader("C:\\Users\\Ricardo\\Desktop\\testcases\\test\\additional_images");
        loader.load();
        Iterator<File> it = loader.iterator();

        for (int i = 0; it.hasNext(); i++) {
            File f = it.next();
            ImageModel im = loader.loadImage(f);
            e.insert(im);
        }

        try {
            System.out.println("Creating a knn query...");
            BufferedImage image = ImageIO.read(new File("C:\\Users\\Ricardo\\Desktop\\testcases\\28\\28004.jpg"));

            CriteriaBuilderImpl cb = new CriteriaBuilderImpl();
            CriteriaQuery<ImageModel> query = cb.createQuery(ImageModel.class);
            Path imagePath = query.from(ImageModel.class).get("image");
            query = query.where(cb.similar(imagePath, image));

            ResultSet<ImageModel> results = e.search(query);

            System.out.println("Number of retrieved elements: " + results.size());
            for (Result<ImageModel> r : results) {
                System.out.print("Retrieved element: " + r.getResult().toString() + "\t");
                System.out.println("Similarity: " + r.getSimilarity());
            }
        } catch (IOException ex) {
            System.out.println("[Error] Couldn't load the query image. Possible reason: " + ex.getMessage());
        }
    }
}
