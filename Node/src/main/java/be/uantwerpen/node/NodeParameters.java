package be.uantwerpen.node;



import be.uantwerpen.node.utils.Hash;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class NodeParameters {
    private static NodeParameters instance = new NodeParameters();
    public static InetAddress nameServerIp;
    public static String name;
    public static InetAddress ip;
    public static Integer id;
    public static Integer previousID;
    public static Integer nextID;

    private NodeParameters() {
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
    public static Integer getPreviousID() {
        return previousID;
    }

    public static void setPreviousID(Integer previousID) {
        previousID = previousID;
    }

    public static Integer getNextID() {
        return nextID;
    }

    public static void setNextID(Integer nextID) {
        nextID = nextID;
    }

    public static Integer getId() {
        return id;
    }

    public static void setIDsAsOwn(){
        nextID = id;
        previousID = id;
    }
}
