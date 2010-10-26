package pt.inevo.encontra.nbtree.index;

import java.util.ArrayList;
import java.util.List;
import pt.inevo.encontra.descriptors.CompositeDescriptor;
import pt.inevo.encontra.descriptors.Descriptor;
import pt.inevo.encontra.index.IndexEntryFactory;
import pt.inevo.encontra.storage.IEntry;

/**
 * Factory for creating the entries in the NBTree indexing structure.
 * @author Ricardo
 * @param <O>
 */
public class NBTreeIndexEntryFactory<O extends IEntry> extends IndexEntryFactory<O, NBTreeIndexEntry> {

    public NBTreeIndexEntryFactory(Class objectClass) {
        super(NBTreeIndexEntry.class, objectClass);
    }

    @Override
    protected NBTreeIndexEntry setupIndexEntry(O object, NBTreeIndexEntry entry) {

        if (object instanceof Descriptor) {
            //Get the Descriptor
            Descriptor desc = (Descriptor) object;
            Descriptor origin;
            try {
                origin = (Descriptor) objectClass.newInstance();

                if (desc instanceof CompositeDescriptor){
                    CompositeDescriptor d = (CompositeDescriptor)origin;
                    List<Descriptor> newDescriptors = new ArrayList<Descriptor>();

                    for (Descriptor ds: ((CompositeDescriptor)desc).getDescriptors()){
                        newDescriptors.add(ds.getClass().newInstance());
                    }
                    d.setDescriptors(newDescriptors);
                }

                double dist = desc.getDistance(origin);

                //set the key and the value
                entry.setKey(dist);
                entry.setValue(desc);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        } else {
            //TO DO - check if this is correct
            entry.setKey(object.getId());
            try {
                Descriptor desc = (Descriptor) objectClass.newInstance();
                entry.setValue(desc);
            } catch (InstantiationException ex) {
                ex.printStackTrace();
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            }
        }
        
        return entry;
    }

    @Override
    protected O setupObject(NBTreeIndexEntry entry, O object) {

        if (object instanceof Descriptor){
            return (O)entry.getValue();
        } else {
            try {
                //TO DO - check if this is correct
                object = (O) objectClass.newInstance();
                object.setId(entry.getKey());
            } catch (InstantiationException ex) {
                ex.printStackTrace();
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            }
        }
        return object;
    }
}
