package pt.inevo.encontra.nbtree.keys;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import pt.inevo.encontra.nbtree.NBPoint;
import pt.inevo.encontra.nbtree.util.AeSimpleSHA1;
import pt.inevo.encontra.nbtree.util.Util;

/**
 * Given a NBPoint, it returns a key that represents the point
 * @author ricardo
 */
public class EuclideanNormKeyMapper implements KeyMapper {

    @Override
    public Key getKey(NBPoint point) {

        double [] p = point.toArray();
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

    private double norm(double[] descriptor) {
        int i;
        double val = 0.0;

        for (i = 0; i < descriptor.length; i++) {
            val += descriptor[i] * descriptor[i];
        }
        return Math.sqrt(val);
    }
}
