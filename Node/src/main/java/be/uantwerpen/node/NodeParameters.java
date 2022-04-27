package be.uantwerpen.node;



import be.uantwerpen.node.utils.Hash;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class NodeParameters {
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
    }
    //Niet static want method gebruikt instance variables? Singleton?
    public Integer getPreviousID() {
        return previousID;
    }

    public void setPreviousID(Integer previousID) {
        previousID = previousID;
    }

    public Integer getNextID() {
        return nextID;
    }

    public void setNextID(Integer nextID) {
        nextID = nextID;
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
}
