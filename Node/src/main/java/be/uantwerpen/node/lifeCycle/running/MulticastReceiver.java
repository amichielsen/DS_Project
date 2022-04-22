package be.uantwerpen.node.lifeCycle.running;

import be.uantwerpen.node.NodeParameters;
import be.uantwerpen.node.utils.Hash;
import org.w3c.dom.Node;

import java.io.IOException;
import java.net.*;

/**
 * Class that listens to receive multicasts, processes them and responds back
 */
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

    /**
     * Method that processes the multicast and acts appropriately
     * @param packet the packat that has been received
     * @throws IOException exception thrown whenever packet can't be accessed
     */
    public void processMulticast(DatagramPacket packet) throws IOException {
        String msg = new String(
                packet.getData(), 0, packet.getLength());
        String[] data = msg.split(" ");
        String name= data[0];
        String ip = data[1];
        int nameHash = Hash.generateHash(name);
        if (nameHash < NodeParameters.nextID && nameHash > NodeParameters.id){
            NodeParameters.setNextID(nameHash);
            this.respondToMC(packet.getAddress(), packet.getPort(), "PREVIOUS " + NodeParameters.id);
        }
        else if(nameHash > NodeParameters.previousID && nameHash < NodeParameters.id){
            NodeParameters.setPreviousID(nameHash);
            this.respondToMC(packet.getAddress(), packet.getPort(), "NEXT " + NodeParameters.id);
        }
    }

    /**
     * Method that responds to the multicast message
     * @param ip ip to which the respond has to be sent
     * @param port port to which the respond has to be sent
     * @throws IOException Thrown when communication fails
     */
    public void respondToMC(InetAddress ip, int port, String msg) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        byte[] response = msg.getBytes();
        DatagramPacket responsePacket =new DatagramPacket(response, response.length, ip, port);
        socket.send(responsePacket);
        socket.close();
    }
}