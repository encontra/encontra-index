package pt.inevo.encontra.nbtree.test;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import pt.inevo.encontra.descriptors.Descriptor;
import pt.inevo.encontra.descriptors.DescriptorExtractor;
import pt.inevo.encontra.descriptors.SimpleDescriptorExtractor;
import pt.inevo.encontra.engine.Engine;
import pt.inevo.encontra.engine.SimpleEngine;
import pt.inevo.encontra.engine.SimpleIndexedObjectFactory;
import pt.inevo.encontra.image.descriptors.ColorLayoutDescriptor;
import pt.inevo.encontra.index.IndexedObject;
import pt.inevo.encontra.index.ResultSet;
import pt.inevo.encontra.index.SimpleIndex;
import pt.inevo.encontra.index.search.SimpleCombinedSearcher;
import pt.inevo.encontra.index.search.SimpleSearcher;
import pt.inevo.encontra.nbtree.index.BTreeIndex;
import pt.inevo.encontra.nbtree.index.NBTreeSearcher;
import pt.inevo.encontra.query.KnnQuery;
import pt.inevo.encontra.query.Query;
import pt.inevo.encontra.storage.EntityStorage;
import pt.inevo.encontra.storage.IEntry;
import pt.inevo.encontra.storage.SimpleObjectStorage;

public class NBTreeTest2 {

    public NBTreeTest2() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    public static class FileUtil {

        private static boolean hasExtension(File f, String[] extensions) {
            int sz = extensions.length;
            String ext;
            String name = f.getName();
            for (int i = 0; i < sz; i++) {
                ext = (String) extensions[i];
                if (name.endsWith(ext)) {
                    return true;
                }
            }
            return false;
        }

        public static List<File> findFilesRecursively(File directory, String[] extensions) {
            List<File> list = new ArrayList<File>();
            if (directory.isFile()) {
                if (hasExtension(directory, extensions)) {
                    list.add(directory);
                }
                return list;
            }
            addFilesRecursevely(list, directory, extensions);
            return list;
        }

        private static void addFilesRecursevely(List<File> found, File rootDir, String[] extensions) {
            if (rootDir == null) {
                return; // we do not want waste time
            }
            File[] files = rootDir.listFiles();
            if (files == null) {
                return;
            }
            for (int i = 0; i < files.length; i++) {
                File file = new File(rootDir, files[i].getName());
                if (file.isDirectory()) {
                    addFilesRecursevely(found, file, extensions);
                } else {
                    if (hasExtension(files[i], extensions)) {
                        found.add(file);
                    }
                }
            }
        }
    }

    public class ImageModelLoader {

        protected String imagesPath = "";
        protected Long idCount = 0l;

        public ImageModelLoader() {
        }

        public ImageModelLoader(String imagesPath) {
            this.imagesPath = imagesPath;
        }

        public ImageModel loadImage(File image) {

            //for now only sets the filename
            ImageModel im = new ImageModel(image.getAbsolutePath(), "", null);
            im.setId(idCount++);

            //get the description
            //TO DO - load the description from here
            im.setDescription("Description: " + image.getAbsolutePath());

            //get the bufferedimage
            try {
                BufferedImage bufImg = ImageIO.read(image);
                im.setImage(bufImg);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            return im;
        }

        public List<ImageModel> getImages(String path) {
            File root = new File(path);
            String[] extensions = {"jpg", "png"};

            List<File> imageFiles = FileUtil.findFilesRecursively(root, extensions);
            List<ImageModel> images = new ArrayList<ImageModel>();

            for (File f : imageFiles) {
                images.add(loadImage(f));
            }

            return images;
        }

        public List<ImageModel> getImages() {
            return getImages(imagesPath);
        }
    }

    public class SimpleImageSearcher<O extends IndexedObject> extends NBTreeSearcher<O> {

        @Override
        public ResultSet<O> search(Query query) {
            ResultSet<IEntry> results = new ResultSet<IEntry>();
            if (supportsQueryType(query.getType())) {
                if (query.getType().equals(Query.QueryType.KNN)) {
                    KnnQuery q = (KnnQuery) query;
                    IndexedObject o = (IndexedObject) q.getQuery();
                    if (o.getValue() instanceof BufferedImage) {
                        Descriptor d = getDescriptorExtractor().extract(o);
                        results = performKnnQuery(d, q.getKnn());
                    }
                }
            }

            return getResultObjects(results);
        }
    }

    public class SimpleTextSearcher<O extends IndexedObject> extends SimpleSearcher<O> {

        @Override
        public ResultSet<O> search(Query query) {
            ResultSet<IEntry> results = new ResultSet<IEntry>();
            if (supportsQueryType(query.getType())) {
                if (query.getType().equals(Query.QueryType.KNN)) {
                    KnnQuery q = (KnnQuery) query;
                    IndexedObject o = (IndexedObject) q.getQuery();
                    if (o.getValue() instanceof String) {
                        Descriptor d = getDescriptorExtractor().extract(o);
                        results = performKnnQuery(d, q.getKnn());
                    }
                }
            }

            return getResultObjects(results);
        }
    }

    @Test
    public void test() {
        //Creating the EntityStorage for saving the objects
        EntityStorage storage = new SimpleObjectStorage(ImageModel.class);

        //Creating the descriptor extractor for strings
        DescriptorExtractor stringDescriptorExtractor = new SimpleDescriptorExtractor(StringDescriptor.class);

        //Creating the engine
        Engine<ImageModel> e = new SimpleEngine<ImageModel>();
        e.setObjectStorage(storage);
        e.setIndexedObjectFactory(new SimpleIndexedObjectFactory());

        //Creating the searchers for each type of the model
        SimpleCombinedSearcher searcher = new SimpleCombinedSearcher();

        //A searcher for the filename
        SimpleTextSearcher filenameSearcher = new SimpleTextSearcher();
        filenameSearcher.setDescriptorExtractor(stringDescriptorExtractor);
        filenameSearcher.setIndex(new SimpleIndex(StringDescriptor.class));

        //A searcher for the description
        SimpleTextSearcher descriptionSearcher = new SimpleTextSearcher();
        descriptionSearcher.setDescriptorExtractor(stringDescriptorExtractor);
        descriptionSearcher.setIndex(new SimpleIndex(StringDescriptor.class));

        //A searcher for the image content - using a simple descriptor
        SimpleImageSearcher imageSearcher = new SimpleImageSearcher();
        imageSearcher.setDescriptorExtractor(new ColorLayoutDescriptor());
        imageSearcher.setIndex(new BTreeIndex(ColorLayoutDescriptor.class));

        searcher.addSearcher("filename", filenameSearcher);
        searcher.addSearcher("description", descriptionSearcher);
        searcher.addSearcher("image", imageSearcher);

        e.setSearcher(searcher);

        ImageModelLoader loader = new ImageModelLoader();
        List<ImageModel> images = loader.getImages("./src/test/resources/additional_images");
        for (ImageModel im : images) {
            e.insert(im);
        }
        System.out.println("Images were successfully saved inserted in the engine.");
    }
}