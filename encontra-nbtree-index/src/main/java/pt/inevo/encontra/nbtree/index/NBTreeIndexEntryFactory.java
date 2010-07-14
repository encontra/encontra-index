package pt.inevo.encontra.nbtree.index;

import pt.inevo.encontra.index.IndexEntry;
import pt.inevo.encontra.index.IndexEntryFactory;
import pt.inevo.encontra.storage.IEntry;

public class NBTreeIndexEntryFactory<O extends IEntry> extends IndexEntryFactory<O,IndexEntry>{

    public NBTreeIndexEntryFactory(Class objectClass){
        super(NBTreeIndexEntry.class, objectClass);
    }

    @Override
    protected IndexEntry setupIndexEntry(O object, IndexEntry entry) {
        entry.setKey(object.getId());
        entry.setValue(object.getValue());
        return entry;
    }

    @Override
    protected O setupObject(IndexEntry entry, O object) {
        object.setId(entry.getKey());
        object.setValue(entry.getValue());
        return object;
    }
}