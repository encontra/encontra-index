//package pt.inevo.encontra.nbtree.descriptors;
//
//import pt.inevo.encontra.descriptors.DescriptorExtractor;
//import pt.inevo.encontra.index.IndexedObject;
//
//public class NBTreeDescriptorExtractor extends DescriptorExtractor<IndexedObject,NBTreeDescriptor> {
//
//    public NBTreeDescriptorExtractor(Class descriptorClass){
//        super(IndexedObject.class,descriptorClass);
//    }
//
//    @Override
//    public NBTreeDescriptor extract(IndexedObject object) {
//        NBTreeDescriptor descriptor= newDescriptor();
//        descriptor.setId(object.getId());
//        descriptor.setValue(object.getValue());
//        return descriptor;
//    }
//
//    @Override
//    protected IndexedObject setupIndexedObject(NBTreeDescriptor descriptor, IndexedObject object){
//        object.setId(descriptor.getId());
//        object.setValue(descriptor.getValue());
//        return object;
//    }
//}