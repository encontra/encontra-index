package pt.inevo.encontra.nbtree.keys;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import pt.inevo.encontra.nbtree.NBTreeDescriptor;
import pt.inevo.encontra.nbtree.util.AeSimpleSHA1;
import pt.inevo.encontra.nbtree.util.Util;

/**
 * Given a NBPoint, it returns a key that represents the point - Takes all
 * the elements in the vector (histogram) to represent the key.
 * @author ricardo
 */
public class HistogramKeyMapper implements KeyMapper<Key> {

    @Override
    public Key getKey(NBTreeDescriptor point) {
        Key key;
        double val = 0;
        try {
            double[] vec = point.getDoubleRepresentation();
            for (int i = 0; i < vec.length; i++) {
                val += (i + 1) * vec[i];
            }
            key = new Key(val, AeSimpleSHA1.SHA1(Util.doubleArrayToString(vec, ',')));
            return key;
        } catch (NoSuchAlgorithmException ex) {
            key = new Key(val, "");
            ex.printStackTrace();
        } catch (UnsupportedEncodingException ex) {
            key = new Key(val, "");
            ex.printStackTrace();
        }
        return key;
    }
}
