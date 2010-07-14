package pt.inevo.encontra.lucene.index;

import org.apache.lucene.document.Document;
import pt.inevo.encontra.descriptors.Descriptor;
import pt.inevo.encontra.descriptors.DescriptorExtractor;
import pt.inevo.encontra.index.DescriptorIndexEntryFactory;
import pt.inevo.encontra.index.IndexedObject;

public class LuceneIndexEntryFactory<O extends IndexedObject> extends DescriptorIndexEntryFactory<O,LuceneIndexEntry> {

        public LuceneIndexEntryFactory(Class indexedObjectClass) {
            super(LuceneIndexEntry.class,indexedObjectClass);
        }

   
    
        @Override
        protected LuceneIndexEntry setupIndexEntry(O object, Descriptor descriptor, LuceneIndexEntry entry) {
            if (object.getId() != null) {
                entry.setKey(object.getId().toString());                
                entry.setValue((Document)descriptor.getValue());
            }
            return entry;
        }

    @Override
    protected O setupIndexedObject(LuceneIndexEntry entry, O object) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


}