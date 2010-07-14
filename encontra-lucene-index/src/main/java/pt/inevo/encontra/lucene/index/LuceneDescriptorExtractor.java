package pt.inevo.encontra.lucene.index;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import pt.inevo.encontra.descriptors.Descriptor;
import pt.inevo.encontra.descriptors.DescriptorExtractor;
import pt.inevo.encontra.descriptors.Descriptor;
import pt.inevo.encontra.index.IndexEntry;
import pt.inevo.encontra.index.IndexedObject;
import pt.inevo.encontra.storage.IEntity;

import java.util.*;


public class LuceneDescriptorExtractor<O extends IndexedObject> extends DescriptorExtractor<O,LuceneDescriptor>{

    private List<DescriptorExtractor<O,Descriptor>> extractors=new ArrayList<DescriptorExtractor<O,Descriptor>>();

    List<Double> weights=new ArrayList<Double>();

    public void addExtractor(DescriptorExtractor<O,Descriptor> d,double weight) {
        extractors.add(d);
        weights.add(weight);
    }

    public LuceneDescriptorExtractor(Class<O> indexedObjectClass,DescriptorExtractor<O,Descriptor>[] extractors) {
        super(indexedObjectClass);
        this.extractors= Arrays.asList(extractors);
        for(int i=0;i<this.extractors.size();i++){
            weights.add(1.0);
        }
    }

    @Override
    public O setupIndexedObject(LuceneDescriptor descriptor, O object){
        object.setId(descriptor.getId());
        return object;
    }

    @Override
    public LuceneDescriptor extract(O object) {
        Document doc = new Document();
        Descriptor [] descriptors=new Descriptor[extractors.size()];
        int i=0;
        for(DescriptorExtractor<O,Descriptor> extractor : extractors) {
            Descriptor descriptor=extractor.extract(object);
            descriptors[i++]=descriptor;
            String str = descriptor.getStringRepresentation();
            if (str != null)
                doc.add(new Field(descriptor.getName(), str, Field.Store.YES, Field.Index.NO));
        }
        doc.add(new Field("IDENTIFIER", object.getId().toString(), Field.Store.YES, Field.Index.NO));

        LuceneDescriptor descriptor=new LuceneDescriptor(descriptors);
        descriptor.setValue(doc);

        return  descriptor;
    }




}
