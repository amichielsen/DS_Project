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
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
public class NamingService {

    private TreeMap<Integer, Inet4Address> database;
    private final Hash hashGen = new Hash();
    private ReadWriteLock lock = new ReentrantReadWriteLock();
    private Lock writeLock = lock.writeLock();
    private Lock readLock = lock.readLock();
    public NamingService() {
        database = XMLRead.serverList();
    }

    public Inet4Address getIpAddress(String filename){
        try{
            readLock.lock();
            NodeFinder nodeFinder = new NodeFinder(hashGen, database);
            int value = nodeFinder.findNodeFromFile(filename);
            return database.get(value);
        }finally {
            readLock.unlock();
        }

    }

    public Integer addIpAddress(String hostname, String ip) {
        try {
            if (!database.containsKey(Hash.generateHash(hostname))) {
                Integer hash = Hash.generateHash(hostname);
                try{
                    writeLock.lock();
                    database.put(hash, (Inet4Address) InetAddress.getByName(ip));
                }finally {
                    writeLock.unlock();
                }
                XMLWrite.serverList(database);
                return hash;
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean deleteIpAddress(String ip) {
        boolean status = false;
        try{
            writeLock.lock();
            status = database.keySet().removeIf(key -> key == Hash.generateHash(ip));
        }finally {
            writeLock.unlock();
        }
        XMLWrite.serverList(database);
        return status;
    }

    public TreeMap<Integer, Inet4Address> getDatabase() {
        try{
            readLock.lock();
            return database;
        }finally {
            readLock.unlock();
        }
    }

    public int getNrOfNodes(){
        return this.database.size();
    }
}
