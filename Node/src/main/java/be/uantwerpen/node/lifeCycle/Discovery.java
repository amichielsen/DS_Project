package be.uantwerpen.node.lifeCycle;

import be.uantwerpen.node.LifeCycleController;
import be.uantwerpen.node.NodeParameters;
import org.w3c.dom.Node;

import java.io.IOException;
import java.net.*;

/**
 * This is the first state
 * 1. Try to find the Nameserver -> we will send 5 broadcasts
 * 2. If no answer, retry after cooldown period
 */
public class Discovery extends State {
    private DatagramSocket socket;
    private InetAddress group;
    private boolean allAnswersReceived = false;
    private int answerCounter = 0;
    private int nextTemp;
    private int previousTemp;

    public Discovery(LifeCycleController lifeCycleController) {
        super(lifeCycleController);
        try {
            socket = new DatagramSocket();
            socket.setBroadcast(true);
        } catch (SocketException e) {
            System.out.println(e);
            //throw new RuntimeException(e);
        }

    }

    @Override
    public void run() {
        try {
            this.multicast("fakename", "1.2.3.4");
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
        String msg = name + " " + IP;
        byte[] buffer = msg.getBytes();
        Inet4Address multicastIP = (Inet4Address) Inet4Address.getByName("230.0.0.0");
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, multicastIP, 8080);
        socket.send(packet);
        while(!this.allAnswersReceived) {
            byte[] answerBuf = new byte[256];
            DatagramPacket answerPacket = new DatagramPacket(answerBuf, answerBuf.length);
            socket.receive(answerPacket);
            this.handleResponse(answerPacket);
        }
        socket.close();
    }

    /**
     * Handle response of multicast
     * @param answerPacket received packet
     */
    public void handleResponse(DatagramPacket answerPacket){
        String received = new String(answerPacket.getData(),0, answerPacket.getLength());
        String[] contents = received.split(" ");
        if(contents.length > 1){
            String position = contents[0];
            int answerID= Integer.parseInt(contents[1]);
            if(position.equals("NEXT")){
                this.nextTemp = answerID;
                this.answerCounter += 1;
            }
            else if(position.equals("PREVIOUS")){
                this.previousTemp = answerID;
                this.answerCounter +=1;
            }
        }
        else if(contents.length == 1){
            int number = Integer.parseInt(contents[0]);
            if(number < 1){
                NodeParameters.setIDsAsOwn();
                this.allAnswersReceived = true;
            }
            else {
                this.answerCounter += 1;
            }
        }
        if(this.answerCounter == 3){
            NodeParameters.setNextID(this.nextTemp);
            NodeParameters.setPreviousID(this.previousTemp);
            this.allAnswersReceived = true;
            this.answerCounter = 0;
        }


    }

}
