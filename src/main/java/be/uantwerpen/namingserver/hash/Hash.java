package be.uantwerpen.namingserver.hash;

import static java.lang.Math.abs;

public class Hash {

    int max = Integer.MAX_VALUE;
    int min = Integer.MIN_VALUE;

    public int generateHash(String hostname){
        int hashCode = hostname.hashCode();
        int hashValue  = hashCode;
        return (int)hashValue;
    }
}
