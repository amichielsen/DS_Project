package be.uantwerpen.namingserver.utils.hash;

import static java.lang.Math.abs;

/**
 * Hashing class for generating a hash value within the 0-32768 range
 * --Alexander Michielsen
 */
public class Hash {

    public static int generateHash(String hostname){
        int hashCode = hostname.hashCode();
        double max = Integer.MAX_VALUE;
        double min = Integer.MIN_VALUE;
        double hashValue  = (hostname.hashCode()+ max)*(32768/(max +abs(min)));
        return (int)hashValue;
    }
}
