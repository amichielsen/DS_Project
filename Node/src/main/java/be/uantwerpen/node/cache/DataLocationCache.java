package be.uantwerpen.node.cache;


import java.net.InetAddress;
import java.util.HashMap;

// Bevat: bestandsnaam, bastandsid, local-replica, locatie van replica
public class DataLocationCache {
    static DataLocationCache instance = new DataLocationCache();
    // To be implemented...
    // {"id": [string filename, bool R/L, integer location replica] }
    //private TreeMap<Integer, InetAddress> cache = new TreeMap<Integer, InetAddress>();

    private DataLocationCache() {}
    public static DataLocationCache getInstance(){
        return instance;
    }
}
