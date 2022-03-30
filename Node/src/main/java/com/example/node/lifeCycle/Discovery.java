package com.example.node.lifeCycle;

import java.net.*;

public class Discovery extends State {
    private static DatagramSocket socket;

    public static void broadcast(String name, String IP) throws SocketException, UnknownHostException {
        String msg = name + " " + IP;
        socket = new DatagramSocket();
        socket.setBroadcast(true);

        byte[] buffer = msg.getBytes();

        Inet4Address broadcastIP = (Inet4Address) Inet4Address.getByName("255.255.255.255");
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, broadcastIP, 8080);
    }
}
