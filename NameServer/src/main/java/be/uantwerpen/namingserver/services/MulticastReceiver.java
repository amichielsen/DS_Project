package be.uantwerpen.namingserver.services;

import be.uantwerpen.namingserver.servers.NamingServer;

import java.io.IOException;
import java.net.*;

/**
 * Class that receives multicast messages, processes them and respond adequately
 */
public class MulticastReceiver extends Thread{
    protected MulticastSocket socket = null;
    protected byte[] buf = new byte[256];


    /**
     * Start multicast listener
     */
    public void run() {
        try {
            socket = new MulticastSocket(5000);
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

    /**
     * Process the multicast message
     * @param packet the received packet
     * @throws IOException thrown when packet can't be read
     */
    public void processMulticast(DatagramPacket packet) throws IOException {
        String msg = new String(
                packet.getData(), 0, packet.getLength());
        String[] data = msg.split(" ");
        if(data[0].equals("DSCVRY")) {
            String name = data[1];
            String ip = data[2];
            NamingServer.addHost(name, ip);
            InetAddress responseIP = packet.getAddress();
            int responsePort = packet.getPort();
            this.respondToMC(responseIP, responsePort);
        }
    }

    /**
     * Responds to receive multicast
     * @param ip ip to which to respond
     * @param port port to which to respond
     * @throws IOException thrown when communication fails
     */
    public void respondToMC(InetAddress ip, int port) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        String nrOfNodes = Integer.toString(NamingServer.getNumberOfNodes()-1); //-1 because current one added already
        String response = "NS " + nrOfNodes;
        byte[] buf = response.getBytes();

        DatagramPacket responsePacket =new DatagramPacket(buf, buf.length, ip, port);
        socket.send(responsePacket);
        socket.close();
    }
}
