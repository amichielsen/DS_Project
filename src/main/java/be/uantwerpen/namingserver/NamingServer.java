package be.uantwerpen.namingserver;

import be.uantwerpen.namingserver.hash.Hash;
import be.uantwerpen.namingserver.hash.Main;
import be.uantwerpen.namingserver.hash.NodeFinder;
import org.springframework.stereotype.Service;

import java.net.Inet4Address;
import java.util.Map;

@Service
public class NamingServer {

    private Map<Integer, Inet4Address> database;
    private final Hash hashGen = new Hash();

    public NamingServer() {
    }

    Inet4Address getIpAddress(String filename){
        NodeFinder nodeFinder = new NodeFinder(hashGen, database);
        int value = nodeFinder.findNodeFromFile(filename);
        return database.get(value);
    }
}
