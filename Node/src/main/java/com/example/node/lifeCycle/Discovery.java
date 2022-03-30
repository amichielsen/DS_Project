package com.example.node.lifeCycle;

import com.example.node.LifeCycleController;

import java.io.IOException;
import java.net.*;

public class Discovery extends State {
    private DatagramSocket socket;

    public Discovery(LifeCycleController lifeCycleController) throws SocketException {
        super(lifeCycleController);
        socket = new DatagramSocket();
        socket.setBroadcast(true);
    }

    public void sendBroadcast(String name, String IP) throws IOException {
        String msg = name + " " + IP;

        byte[] buffer = msg.getBytes();
        Inet4Address broadcastIP = (Inet4Address) Inet4Address.getByName("255.255.255.255");
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, broadcastIP, 8080);
        socket.send(packet);
        socket.close();
    }

}
