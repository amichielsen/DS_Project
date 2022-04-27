package be.uantwerpen.node.lifeCycle;

import be.uantwerpen.node.LifeCycleController;
import be.uantwerpen.node.NodeParameters;
import be.uantwerpen.node.lifeCycle.running.Running;

import java.io.IOException;
import java.net.*;

/**
 * This is the first state
 * 1. Try to find the Nameserver -> we will send 5 broadcasts
 * 2. If no answer, retry after cooldown period
 */
public class DiscoveryBootstrap extends State {
    private boolean allAnswersReceived = false;
    private int answerCounter = 0;
    private int nextTemp;
    private int previousTemp;

    public DiscoveryBootstrap(LifeCycleController lifeCycleController) {
        
        super(lifeCycleController);
    }

    @Override
    public void run() {
        try {
            this.multicast(NodeParameters.getInstance().getName(), (NodeParameters.getInstance().getIp()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends multicast message to all nodes and NS to discover them
     * @param name name of this node
     * @param IP ip of this node
     * @throws IOException thrown when communication fails
     */
    public void multicast(String name, String IP) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        socket.setBroadcast(true);
        String msg = "DSCVRY "+ name + " " + IP;
        byte[] buffer = msg.getBytes();
        Inet4Address multicastIP = (Inet4Address) Inet4Address.getByName("230.0.0.0"); //MC group
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, multicastIP, 8080);
        socket.send(packet);
        if (NodeParameters.DEBUG) {
            System.out.println("[DISCOVERY] [Info] multicast has been send");
        }
        while(!this.allAnswersReceived) {

            byte[] answerBuf = new byte[256];
            DatagramPacket answerPacket = new DatagramPacket(answerBuf, answerBuf.length);
            socket.receive(answerPacket);
            this.handleResponse(answerPacket);
        }
        socket.close();
        if (NodeParameters.DEBUG) {
            System.out.println("[DISCOVERY] [Info] all responses have been handled -- Socket closed");
        }
        lifeCycleController.ChangeState(new Running(lifeCycleController));
    }

    /**
     * Handle response of multicast
     * TOEVOEGEN: na bepaalde tijd niet alle responses -> failure
     * @param answerPacket received packet
     */
    public void handleResponse(DatagramPacket answerPacket){
        String received = new String(answerPacket.getData(),0, answerPacket.getLength());
        String[] contents = received.split(" ");
        if(this.answerCounter == 3){
            NodeParameters.getInstance().setNextID(this.nextTemp);
            NodeParameters.getInstance().setPreviousID(this.previousTemp);
            this.allAnswersReceived = true;
            this.answerCounter = 0;
            return;
        }
        if(contents.length > 1){
            String identifier = contents[0];
            System.out.println(identifier);
            switch (identifier) {
                case "NS" -> {
                    int number = Integer.parseInt(contents[1]);
                    NodeParameters.setNameServerIp(answerPacket.getAddress());
                    if (number < 1) {
                        NodeParameters.getInstance().setIDsAsOwn();
                        this.allAnswersReceived = true;
                    } else {
                        this.answerCounter += 1;
                    }
                }
                case "NEXT+PREVIOUS" -> {
                    this.nextTemp = Integer.parseInt(contents[1]);
                    this.previousTemp = Integer.parseInt(contents[1]);
                    this.answerCounter += 2;
                }
                case "NEXT" -> {
                    this.nextTemp = Integer.parseInt(contents[1]);
                    this.answerCounter += 1;
                }
                case "PREVIOUS" -> {
                    this.previousTemp = Integer.parseInt(contents[1]);
                    this.answerCounter += 1;
                }
            }
        }
    }

}
