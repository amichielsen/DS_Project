package com.example.node.lifeCycle;

import com.example.node.LifeCycleController;

/**
 * This is the first state
 * 1. Try to find the Nameserver -> we will send 5 broadcasts
 * 2. If no answer, retry after cooldown period
 */
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
