package pt.inevo.encontra.nbtree.util;

/**
 * Util functions used by the NBTree.
 * @author Ricardo Dias
 */
public class Util {

    /**
     * Normalize a descriptor vector from one interval to the default [0,1] interval.
     * @param descriptor the vector to be normalized
     * @param minVal minimum value of the origin interval
     * @param maxVal maximum value of the origin interval
     * @return a normalized vector
     */
    public static double[] normalize(double[] descriptor, double minVal, double maxVal) {
        return normalize (descriptor, minVal, maxVal, 0, 1);
    }

    /**
     * Normalizes a descriptor vector from one interval to other interval
     * @param descriptor the vector to be normalized
     * @param minVal minimum value of the origin interval
     * @param maxVal maximum value of the origin interval
     * @param minValNorm minimum value of the destiny interval
     * @param maxValNorm maximum value of the destiny interval
     * @return a normalized vector
     */
    public static double[] normalize(double[] descriptor, 
            double minVal, double maxVal,
            double minValNorm, double maxValNorm) {

        double[] normalizedDescriptor = new double[descriptor.length];

        for (int i = 0; i < descriptor.length; i++) {

            double parc1 = descriptor[i] - minVal;
            double parc2 = maxVal - minVal;
            double num = minValNorm + (parc1 / parc2) * (maxValNorm - minValNorm);

            //update the new position
            normalizedDescriptor[i] = num;
        }

        return normalizedDescriptor;
    }

    /**
     * Converts a double array to a string representation
     * @param descriptor
     * @return a double array represented as "val1,val2,...,valn"
     */
    public static String doubleArrayToString(double[] descriptor, char separator) {

        String descriptorStr = "";
        for (int i = 0; i < descriptor.length; i++) {
            descriptorStr += descriptor[i];
            if (i != (descriptor.length - 1)) {
                descriptorStr += separator;
            }
        }
        return descriptorStr;
    }

    /**
     * Converts a string descriptor to a double array descriptor
     * @param descriptor
     * @return
     */
    public static double[] stringToDoubleArray(String descriptor, char separator) {

        String[] descriptors = descriptor.split(""+separator);

        double[] result = new double[descriptors.length];

        for (int i = 0; i < result.length; i++) {
            result[i] = Double.parseDouble(descriptors[i]);
        }

        return result;
    }
}
