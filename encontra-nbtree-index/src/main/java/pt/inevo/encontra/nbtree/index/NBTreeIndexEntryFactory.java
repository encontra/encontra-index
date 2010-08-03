package pt.inevo.encontra.nbtree.index;

import pt.inevo.encontra.common.distance.EuclideanDistanceMeasure;
import pt.inevo.encontra.descriptors.Descriptor;
import pt.inevo.encontra.index.IndexEntryFactory;
import pt.inevo.encontra.nbtree.descriptors.NBTreeDescriptor;
import pt.inevo.encontra.storage.IEntry;

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
                double dist = desc.getDistance(origin);

                double [] descRep = desc.getDoubleRepresentation();
                //HORRIBLE HACK
                NBTreeDescriptor newDesc = new NBTreeDescriptor(Double.class, descRep.length, desc.getId(), new EuclideanDistanceMeasure());
                newDesc.setName(desc.getName());
                newDesc.setDoubleRepresentation(descRep);

                //set the key and the value
                entry.setKey(dist);
                entry.setValue(newDesc);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        } else {
            //TO DO - check if this is correct
            entry.setKey(object.getId());
            try {
                Descriptor d = (Descriptor) objectClass.newInstance();
                NBTreeDescriptor newDesc = new NBTreeDescriptor(Double.class, d.getDoubleRepresentation().length, d.getId(), new EuclideanDistanceMeasure());
                entry.setValue(newDesc);
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
            object = (O) entry.getValue();
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
