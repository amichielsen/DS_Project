package be.uantwerpen.node.lifeCycle.running.services;

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

    private NodeParameters nodeParameters;

    public void run() {
        System.out.println("[MULTICAST] [Info] receiver started");
        nodeParameters = NodeParameters.getInstance();
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
        System.out.println("[MULTICAST] [Info] receiver listening");
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
        String identifier = data[0];
        if(identifier.equals("DSCVRY")) {
            String name = data[1];
            String ip = data[2];
            int nameHash = Hash.generateHash(name);
            System.out.println("namehash: " + nameHash + " name: " + name);
            if (NodeParameters.nextID.equals(NodeParameters.id) && NodeParameters.previousID.equals(NodeParameters.id)) {
                nodeParameters.setNextID(nameHash);
                nodeParameters.setPreviousID(nameHash);
                this.respondToMC(packet.getAddress(), packet.getPort(), "NEXT+PREVIOUS " + NodeParameters.id);
                System.out.println("Sent Next+Previous");
            }
            else if (this.shouldBeNext(nameHash)) {
                nodeParameters.setNextID(nameHash);
                this.respondToMC(packet.getAddress(), packet.getPort(), "PREVIOUS " + NodeParameters.id);
                System.out.println("Sent Previous");
            } else if (this.shouldBePrevious(nameHash)) {
                nodeParameters.setPreviousID(nameHash);
                this.respondToMC(packet.getAddress(), packet.getPort(), "NEXT " + NodeParameters.id);
                System.out.println("Sent Next");
            }
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
        System.out.println("Packet sent");
        socket.close();
    }

    private boolean shouldBeNext(int nameHash){
        if((nameHash < NodeParameters.nextID && nameHash > NodeParameters.id)){ //Regular case
            return true;
        }
        else return NodeParameters.nextID < NodeParameters.id && ((nameHash < NodeParameters.nextID) | (nameHash > NodeParameters.id)); //This node is last one in the network

    }

    private boolean shouldBePrevious(int nameHash){
        if(nameHash > NodeParameters.previousID && nameHash < NodeParameters.id){ //Regular case
            return  true;
        }
        else return NodeParameters.previousID > NodeParameters.id && ((nameHash > NodeParameters.previousID) | (nameHash < NodeParameters.id)); //This node is the first one in the network
    }
}