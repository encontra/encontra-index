package pt.inevo.encontra.nbtree.index;

import pt.inevo.encontra.descriptors.Descriptor;
import pt.inevo.encontra.index.IndexEntryFactory;
import pt.inevo.encontra.storage.IEntry;

public class NBTreeIndexEntryFactory<O extends IEntry> extends IndexEntryFactory<O, NBTreeIndexEntry> {

    public NBTreeIndexEntryFactory(Class objectClass) {
        super(NBTreeIndexEntry.class, objectClass);
    }

    @Override
    protected NBTreeIndexEntry setupIndexEntry(O object, NBTreeIndexEntry entry) {

        if (object instanceof Descriptor) {
            //cast to NBTreeDescriptor
            Descriptor desc = (Descriptor) object;
            Descriptor origin;
            try {
                origin = (Descriptor) objectClass.newInstance();
                double dist = origin.getDistance(desc);

                //set the key and the value
                entry.setKey(dist);
                entry.setValue(desc);
            } catch (InstantiationException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (IllegalAccessException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

        } else {
            //TO DO - must do something here?
            entry.setKey(object.getId());
            try {
                entry.setValue((Descriptor) objectClass.newInstance());
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

        Descriptor desc = entry.getValue();

        object.setId(desc.getId());
        object.setValue(desc);

        return object;
    }
}
