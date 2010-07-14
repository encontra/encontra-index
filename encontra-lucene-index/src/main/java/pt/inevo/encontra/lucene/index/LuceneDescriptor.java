package pt.inevo.encontra.lucene.index;

import org.apache.lucene.document.Document;
import pt.inevo.encontra.descriptors.Descriptor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LuceneDescriptor implements Descriptor {

    private String name;
    private Document doc;
    
    private double[] weights;
    private List<Descriptor> descriptors=new ArrayList<Descriptor>();

    public LuceneDescriptor(){
        this(new Descriptor[] {});
    }

    public LuceneDescriptor(Descriptor[] descriptors) {
        this.descriptors = Arrays.asList(descriptors);
        this.weights=new double[descriptors.length];
        Arrays.fill(weights,1.0);
    }

    public void setDescriptors(Descriptor[] descriptors) {
        this.descriptors = Arrays.asList(descriptors);
        this.weights=new double[descriptors.length];
        Arrays.fill(weights,1.0);
    }

    @Override
    public double getDistance( Descriptor d) {
        double distance = 0f;
        int descriptorCount = 0;

        for(Descriptor descriptor : descriptors) {
            distance += descriptor.getDistance(d) * weights[descriptorCount];
            descriptorCount++;
        }

        if (descriptorCount > 0) {
            // TODO: find some better scoring mechanism, e.g. some normalization. One thing would be linearization of the features!
            // For now: Averaging ...
            distance = distance / (float) descriptorCount;
        }
        return distance;
    }

    @Override
    public String getStringRepresentation() {
        String str = "";
        for(Descriptor descriptor : descriptors) {
            str += descriptor.getStringRepresentation();
        }
        return str; // TODO Add a separator here
    }

    @Override
    public Descriptor setStringRepresentation(String d) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name=name;
    }

    public void setValue(Object obj) {
        Document doc=null;
        if(obj instanceof Document){
            doc=(Document) obj;
            for(Descriptor descriptor : descriptors) {
                descriptor.setStringRepresentation(doc.getField(descriptor.getName()).stringValue());
            }
        }
        this.doc=doc;
    }

    @Override
    public Serializable getValue() {
        return doc;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setValue(Serializable o) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
    /*
    @Override
    public double[] getWeights() {
        return weights;
    }

    @Override
    public void setWeights(double[] weights) {
        this.weights=weights;
    }*/


    @Override
    public String getId() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setId(Serializable id) {
        //To change body of implemented methods use File | Settings | File Templates.
    }




}
