package be.uantwerpen.namingserver.hash;

import static java.lang.Math.abs;

public class Hash {

    public static int generateHash(String hostname){
        int hashCode = hostname.hashCode();
        double max = Integer.MAX_VALUE;
        double min = Integer.MIN_VALUE;
        double hashValue  = (hostname.hashCode()+ max)*(32768/(max +abs(min)));
        return (int)hashValue;
    }
}
