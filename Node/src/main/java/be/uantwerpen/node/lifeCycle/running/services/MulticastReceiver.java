package be.uantwerpen.node.lifeCycle.running.services;

import be.uantwerpen.node.utils.NodeParameters;
import be.uantwerpen.node.utils.Hash;
import be.uantwerpen.node.utils.cache.IpTableCache;

import java.io.IOException;
import java.net.*;

/**
 * Class that listens to receive multicasts, processes them and responds back
 */
public class MulticastReceiver extends Thread{
    protected MulticastSocket socket = null;
    protected final byte[] buf = new byte[256];

    private NodeParameters nodeParameters;

    public void run() {
        if(NodeParameters.DEBUG) System.out.println("[MULTICAST] [Info] receiver started");
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
        if(NodeParameters.DEBUG) System.out.println("[MULTICAST] [Info] receiver listening");

        while (true){
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
            IpTableCache.getInstance().addIp(nameHash, InetAddress.getByName(ip));
            if(NodeParameters.DEBUG) System.out.println("[MCR] namehash: " + nameHash + " name: " + name);
            if (NodeParameters.nextID.equals(NodeParameters.id) && NodeParameters.previousID.equals(NodeParameters.id)) {
                nodeParameters.setNextID(nameHash);
                nodeParameters.setPreviousID(nameHash);
                this.respondToMC(packet.getAddress(), packet.getPort(), "NEXT+PREVIOUS " + NodeParameters.id);
                if(NodeParameters.DEBUG) System.out.println("[MCR] Sent Next+Previous");
            }
            else if (this.shouldBeNext(nameHash)) {
                nodeParameters.setNextID(nameHash);
                this.respondToMC(packet.getAddress(), packet.getPort(), "PREVIOUS " + NodeParameters.id);
                if(NodeParameters.DEBUG) System.out.println("[MCR] Sent Previous");
            } else if (this.shouldBePrevious(nameHash)) {
                nodeParameters.setPreviousID(nameHash);
                this.respondToMC(packet.getAddress(), packet.getPort(), "NEXT " + NodeParameters.id);
                if(NodeParameters.DEBUG) System.out.println("[MCR] Sent Next");
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
        if(NodeParameters.DEBUG) System.out.println("[MCR] Packet sent");
        socket.close();
    }

    /**
     * Checks whether this node should be the next of the node identified by nameHash
     * @param nameHash the hash of the queried node
     * @return returns true if this node is next otherwise false
     */
    private boolean shouldBeNext(int nameHash){
        if((nameHash < NodeParameters.nextID && nameHash > NodeParameters.id)){ //Regular case
            return true;
        }
        else return NodeParameters.nextID < NodeParameters.id && ((nameHash < NodeParameters.nextID) | (nameHash > NodeParameters.id)); //This node is last one in the network

    }

    /**
     * Checks whether this node should be the previous one of the node identified by nameHash
     * @param nameHash the hash of the queried node
     * @return returns true if this node is the previous one otherwise false
     */
    private boolean shouldBePrevious(int nameHash){
        if(nameHash > NodeParameters.previousID && nameHash < NodeParameters.id){ //Regular case
            return  true;
        }
        else return NodeParameters.previousID > NodeParameters.id && ((nameHash > NodeParameters.previousID) | (nameHash < NodeParameters.id)); //This node is the first one in the network
    }
}