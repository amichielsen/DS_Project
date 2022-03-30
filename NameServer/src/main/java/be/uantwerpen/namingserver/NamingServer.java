package be.uantwerpen.namingserver;

import be.uantwerpen.namingserver.hash.Hash;
import be.uantwerpen.namingserver.hash.NodeFinder;
import be.uantwerpen.namingserver.xmlParser.XMLRead;
import be.uantwerpen.namingserver.xmlParser.XMLWrite;
import org.springframework.stereotype.Service;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

@Service
public class NamingServer {

    private TreeMap<Integer, Inet4Address> database;
    private final Hash hashGen = new Hash();

    public NamingServer() {
        database = XMLRead.serverList();
    }

    Inet4Address getIpAddress(String filename){
        NodeFinder nodeFinder = new NodeFinder(hashGen, database);
        int value = nodeFinder.findNodeFromFile(filename);
        return database.get(value);
    }

    public void addIpAddress(String ip) {
        try {
            database.put(Hash.generateHash(ip), (Inet4Address) InetAddress.getByName(ip));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        XMLWrite.serverList(database);
    }

    public TreeMap<Integer, Inet4Address> getDatabase() {
        return database;
    }
}
