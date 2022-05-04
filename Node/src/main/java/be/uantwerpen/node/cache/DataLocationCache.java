package be.uantwerpen.node.cache;


import be.uantwerpen.node.CustomMap;
import be.uantwerpen.node.FileParameters;

import java.net.InetAddress;
import java.util.HashMap;

// Bevat: bestandsnaam, bastandsid, local-replica, locatie van replica
public class DataLocationCache {
    static DataLocationCache instance = new DataLocationCache();
    // To be implemented...
    // {"id": [string filename, bool R/L, integer location replica] }
    //private TreeMap<Integer, InetAddress> cache = new TreeMap<Integer, InetAddress>();

    //Create a new instance
    CustomMap<Integer, FileParameters> FileMap = new CustomMap<>();

    public void AddFIle(FileParameters f1){
        FileMap.put(1, new FileParameters(f1.FileId(), f1.getFileName(),f1.LocalOrReplica(), f1.ReplicaLocation()));
    }

    private DataLocationCache() {}
    public static DataLocationCache getInstance(){
        return instance;
    }
}
