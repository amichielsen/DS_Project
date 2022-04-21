package be.uantwerpen.namingserver.services;

import be.uantwerpen.namingserver.servers.NamingServer;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class MulticastReceiver extends Thread{
    protected MulticastSocket socket = null;
    protected byte[] buf = new byte[256];


    public void run() {
        try {
            socket = new MulticastSocket(8080);
        } catch (IOException e) {
            e.printStackTrace();
        }
        InetAddress group = null;
        try {
            group = InetAddress.getByName("230.0.0.0");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        try {
            socket.joinGroup(group);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while(true){
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                this.processMulticast(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void processMulticast(DatagramPacket packet) throws IOException {
        String msg = new String(
                packet.getData(), 0, packet.getLength());
        String[] data = msg.split("");
        String name= data[0];
        String ip = data[1];
        NamingServer.addHost(name, ip);
        InetAddress responseIP = packet.getAddress();
        int responsePort = packet.getPort();
        this.respondToMC(responseIP, responsePort);
    }

    public void respondToMC(InetAddress ip, int port) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        String nrOfNodes = Integer.toString(NamingServer.getNumberOfNodes());
        byte[] buf = nrOfNodes.getBytes();

        DatagramPacket responsePacket =new DatagramPacket(buf, buf.length, ip, port);
        socket.send(responsePacket);
        socket.close();
    }
}
