package be.uantwerpen.node;



import be.uantwerpen.node.agents.SyncAgent;
import be.uantwerpen.node.cache.IpTableCache;
import be.uantwerpen.node.fileSystem.FileSystem;
import be.uantwerpen.node.utils.Hash;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class NodeParameters {
                // Bestandsnaam - Local/replica - ID van houder
    //public static Map<String, Map<String, Integer>> bookkeeper = new HashMap<>();

    public static LifeCycleController lifeCycleController;
    //public static HashMap<String, Boolean> systemFiles = new HashMap<>();
    public static Queue<String> lockRequest = new LinkedList<>();
    public static Queue<String> removeLocks = new LinkedList<>();
    public static final Integer FAILURE_TRESHOLD = 5;
    public static boolean DEBUG = true;
    private static NodeParameters instance;

    static {
        try {
            instance = new NodeParameters();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public static InetAddress nameServerIp;
    public static String name;
    public static InetAddress ip;
    public static Integer id;
    public static Integer previousID;
    public static Integer nextID;

    public static Integer failedPrevious;

    public static Integer failedNext;

    public static String localFolder;

    public static String replicaFolder;

    private NodeParameters() throws UnknownHostException {
    }

    public static InetAddress getNameServerIp() {
        return nameServerIp;
    }

    public static void setNameServerIp(InetAddress nameServerIp) {
        NodeParameters.nameServerIp = nameServerIp;
    }

    public static NodeParameters getInstance(){
        return instance;
    }

    public static Integer DELAY_BETWEEN_PING_S = 60;

    public void setup(String name, InetAddress ip, Integer ID) {
        failedNext = 0;
        failedPrevious = 0;
        NodeParameters.name = name;
        NodeParameters.id = Hash.generateHash(name);
        try {
            if (ip == null) {
                NodeParameters.ip = InetAddress.getLocalHost();
            } else {
                NodeParameters.ip = ip;
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        IpTableCache.getInstance().addIp(NodeParameters.id, InetAddress.getLoopbackAddress());
    }
    //Niet static want method gebruikt instance variables? Singleton?
    public Integer getPreviousID() {
        return previousID;
    }

    public void setPreviousID(Integer previousID) {
        this.previousID = previousID;
    }

    public Integer getNextID() {
        return nextID;
    }

    public void setNextID(Integer nextID) {
        this.nextID = nextID;
    }

    public Integer getId() {
        return id;
    }

    public void setIDsAsOwn(){
        nextID = id;
        previousID = id;
    }

    public InetAddress getIP(Integer id){
        return IpTableCache.getInstance().getIp(id);
    }

    public static String getIp() {
        return ip.getHostAddress();
    }

    public static String getName() {
        return name;
    }

    public Integer getFailedPrevious() {
        return failedPrevious;
    }

    public void incFailedPrevious() {
        NodeParameters.failedPrevious += 1;
    }

    public void resFailedPrevious() {
        NodeParameters.failedPrevious = 0;
    }

    public Integer getFailedNext() {
        return failedNext;
    }

    public void incFailedNext() {
        NodeParameters.failedNext += 1;
    }

    public void resFailedNext() {
        NodeParameters.failedNext = 0;
    }

    public static void addLockRequest(String filename){
        if(!FileSystem.fs.containsKey(filename)) return;
        lockRequest.add(filename);
    }

    public static void removeLock(String filename){
        if(!FileSystem.fs.containsKey(filename)) return;
        removeLocks.add(filename);
    }
}
