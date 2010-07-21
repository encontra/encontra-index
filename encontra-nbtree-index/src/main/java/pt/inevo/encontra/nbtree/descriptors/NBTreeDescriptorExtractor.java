package pt.inevo.encontra.nbtree.descriptors;

import pt.inevo.encontra.descriptors.DescriptorExtractor;
import pt.inevo.encontra.index.IndexedObject;

public class NBTreeDescriptorExtractor extends DescriptorExtractor<IndexedObject,NBTreeDescriptor> {

    public NBTreeDescriptorExtractor(Class descriptorClass){
        super(IndexedObject.class,descriptorClass);
    }

    @Override
    public NBTreeDescriptor extract(IndexedObject object) {
        NBTreeDescriptor descriptor= newDescriptor();

        descriptor.setId(object.getId());
        Object o = (Object)new Double[]{(double)object.getValue().hashCode()};
        descriptor.setValue(o);
        return descriptor;
    }

    @Override
    protected IndexedObject setupIndexedObject(NBTreeDescriptor descriptor, IndexedObject object){
        object.setId(descriptor.getId());
        object.setValue(descriptor.getValue());
        return object;
    }

}