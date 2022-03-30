package be.uantwerpen.namingserver;

import be.uantwerpen.namingserver.hash.Hash;
import be.uantwerpen.namingserver.hash.Main;
import be.uantwerpen.namingserver.hash.NodeFinder;
import be.uantwerpen.namingserver.xmlParser.XMLWrite;
import org.springframework.stereotype.Service;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@Service
public class NamingServer {

    private TreeMap<Integer, Inet4Address> database = new TreeMap<>();
    private final Hash hashGen = new Hash();

    public NamingServer() {
        database = XMLWrite.readServerList();
    }

    Inet4Address getIpAddress(String filename){
        NodeFinder nodeFinder = new NodeFinder(hashGen, database);
        int value = nodeFinder.findNodeFromFile(filename);
        return database.get(value);
    }

    public TreeMap<Integer, Inet4Address> getDatabase() {
        return database;
    }
}
