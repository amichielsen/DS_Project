package com.example.node.lifeCycle;

import com.example.node.LifeCycleController;
import com.example.node.NodeParameters;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.TreeMap;

/**
 * The failure mode.
 * 1. If we detect a failure we will get here and report to the NameServer.
 * 2. Find a new route!!!
 */
public class Failure extends State {
    static TreeMap<Integer, Inet4Address> tree_map = new TreeMap<Integer, Inet4Address>();

    public Failure(LifeCycleController lifeCycleController) {
        super(lifeCycleController);


    }
    //check if node is still detected
    public static void sendPingRequest()
            throws UnknownHostException, IOException {
        String ip = null;
        Integer ID = 0;
        for (int i = 0; i < tree_map.size() ; i++) {
            InetAddress node = InetAddress.getByName(ip);
            System.out.println("Sending Ping to " + ip);
            if (node.isReachable(3000)){
                System.out.println("Host is reachable");
            i++;}
            else
            System.out.println("Sorry ! We can't reach to this host");
            ID = tree_map.firstKey();

            // change update next node on previous node
            int prevNode = NodeParameters.getPreviousID();
            int nextNode = NodeParameters.getNextID();
            //change update previous node on next node


            //delete node from naming server
            //NamingServer.deleteHost(ip);
        }
    }
}
