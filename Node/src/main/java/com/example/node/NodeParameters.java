package com.example.node;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class NodeParameters {
    public static InetAddress nameServerIp;
    public static String name;
    public static InetAddress ip;
    public static Integer id;
    public static Integer previousID;
    public static Integer nextID;

    public static Integer DELAY_BETWEEN_PING_S = 60;

    public void setup(String name, InetAddress ip) {
        NodeParameters.name = name;
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
}
