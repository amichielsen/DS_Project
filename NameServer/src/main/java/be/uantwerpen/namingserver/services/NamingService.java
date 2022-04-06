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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class NamingService {

    private TreeMap<Integer, Inet4Address> database;
    private final Hash hashGen = new Hash();
    private Lock lock = new ReentrantLock();
    public NamingService() {
        database = XMLRead.serverList();
    }

    public Inet4Address getIpAddress(String filename){
        NodeFinder nodeFinder = new NodeFinder(hashGen, database);
        int value = nodeFinder.findNodeFromFile(filename);
        return database.get(value);
    }

    public Integer addIpAddress(String hostname, String ip) {
        try {
            if (!database.containsKey(Hash.generateHash(hostname))) {
                Integer hash = Hash.generateHash(hostname);
                lock.lock();
                try{
                    database.put(hash, (Inet4Address) InetAddress.getByName(ip));
                }finally {
                    lock.unlock();
                }
                XMLWrite.serverList(database);
                return hash;
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void deleteIpAddress(String ip) {
        lock.lock();
        try{
            database.keySet().removeIf(key -> key == Hash.generateHash(ip));
        }finally {
            lock.unlock();
        }
        XMLWrite.serverList(database);
    }

    public TreeMap<Integer, Inet4Address> getDatabase() {
        lock.lock();
        try{
            return database;
        }finally {
            lock.unlock();
        }
    }
}
