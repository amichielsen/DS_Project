package be.uantwerpen.namingserver.hash;

import static java.lang.Math.abs;

public class Hash {

    double max = Integer.MAX_VALUE;
    double min = Integer.MIN_VALUE;

    public static int generateHash(String hostname){
        int hashCode = hostname.hashCode();
        double hashValue  = (hostname.hashCode()+max)*(32768/(max+abs(min)));
        return (int)hashValue;
    }
}
