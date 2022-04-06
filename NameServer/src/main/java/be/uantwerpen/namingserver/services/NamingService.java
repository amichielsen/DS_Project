package be.uantwerpen.namingserver.services;

import be.uantwerpen.namingserver.utils.hash.Hash;
import be.uantwerpen.namingserver.utils.hash.NodeFinder;
import be.uantwerpen.namingserver.utils.xmlParser.XMLRead;
import be.uantwerpen.namingserver.utils.xmlParser.XMLWrite;
import org.springframework.stereotype.Service;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

@Service
public class NamingService {

    private TreeMap<Integer, Inet4Address> database;
    private final Hash hashGen = new Hash();

    public NamingService() {
        database = XMLRead.serverList();
    }

    public Inet4Address getIpAddress(String filename){
        NodeFinder nodeFinder = new NodeFinder(hashGen, database);
        int value = nodeFinder.findNodeFromFile(filename);
        return database.get(value);
    }

    public void addIpAddress(String hostname, String ip) {
        try {
            if (!database.containsKey(Hash.generateHash(hostname))) {
                database.put(Hash.generateHash(hostname), (Inet4Address) InetAddress.getByName(ip));
                XMLWrite.serverList(database);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void deleteIpAddress(String ip) {
        database.keySet().removeIf(key -> key == Hash.generateHash(ip));
        XMLWrite.serverList(database);
    }

    public TreeMap<Integer, Inet4Address> getDatabase() {
        return database;
    }
}
