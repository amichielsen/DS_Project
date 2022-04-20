package com.example.node;

import be.uantwerpen.namingserver.utils.hash.Hash;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class NodeParameters {
    public static String name;
    public static InetAddress ip;
    public static Integer PreviousID;
    public static Integer NextID;
    public static Integer ID;

    public void setup(String name, InetAddress ip, Integer ID) {
        NodeParameters.name = name;
        NodeParameters.ID = Hash.generateHash(name);
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
        return PreviousID;
    }

    public static void setPreviousID(Integer previousID) {
        PreviousID = previousID;
    }

    public static Integer getNextID() {
        return NextID;
    }

    public static void setNextID(Integer nextID) {
        NextID = nextID;
    }
}
