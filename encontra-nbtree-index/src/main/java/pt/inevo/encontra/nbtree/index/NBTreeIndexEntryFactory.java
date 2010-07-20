package pt.inevo.encontra.nbtree.index;

import pt.inevo.encontra.common.distance.EuclideanDistanceMeasure;
import pt.inevo.encontra.index.IndexEntryFactory;
import pt.inevo.encontra.nbtree.descriptors.NBTreeDescriptor;
import pt.inevo.encontra.nbtree.keys.Key;
import pt.inevo.encontra.storage.IEntry;

public class NBTreeIndexEntryFactory<O extends IEntry> extends IndexEntryFactory<O,NBTreeIndexEntry>{

    public NBTreeIndexEntryFactory(Class objectClass){
        super(NBTreeIndexEntry.class, objectClass);
    }

    @Override
    protected NBTreeIndexEntry setupIndexEntry(O object, NBTreeIndexEntry entry) {
        entry.setKey(new Key());
        entry.setValue(new NBTreeDescriptor(1, new EuclideanDistanceMeasure()));
        return entry;
    }

    @Override
    protected O setupObject(NBTreeIndexEntry entry, O object) {
        object.setId(entry.getKey());
        object.setValue(entry.getValue());
        return object;
    }
}