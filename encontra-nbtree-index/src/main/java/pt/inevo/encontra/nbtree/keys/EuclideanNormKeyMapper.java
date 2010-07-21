package pt.inevo.encontra.nbtree.keys;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import pt.inevo.encontra.nbtree.descriptors.NBTreeDescriptor;
import pt.inevo.encontra.nbtree.util.AeSimpleSHA1;
import pt.inevo.encontra.nbtree.util.Util;

/**
 * Given a NBPoint, it returns a key that represents the point
 * @author ricardo
 */
public class EuclideanNormKeyMapper implements KeyMapper<Key> {

    @Override
    public Key getKey(NBTreeDescriptor point) {

        Double [] p = (Double[])(point.getValues(Double.class).toArray(new Double[1]));
        double n = norm(p);
        String id = "";
        try {
            id = AeSimpleSHA1.SHA1(Util.doubleArrayToString(p, ','));
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
        return new Key(n, id);
    }

    //calculate the norm of a vector
    private double norm(Double[] descriptor) {
        int i;
        double val = 0.0;

        for (i = 0; i < descriptor.length; i++) {
            val += descriptor[i] * descriptor[i];
        }
        return Math.sqrt(val);
    }
}
