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

    public void multicast(String name, String IP) throws IOException {
        String msg = name + " " + IP;
        byte[] buffer = msg.getBytes();
        Inet4Address multicastIP = (Inet4Address) Inet4Address.getByName("230.0.0.0");
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, multicastIP, 8080);
        socket.send(packet);
        byte[] answerBuf = new byte[256];
        DatagramPacket answerPacket = new DatagramPacket(answerBuf, answerBuf.length);
        socket.receive(answerPacket);
        String received = new String(answerPacket.getData(),0, answerPacket.getLength());
        this.handleResponse(received);
        socket.close();
    }

    public void handleResponse(String received){
        int number = Integer.parseInt(received);
        if(number < 1){
            NodeParameters.setIDsAsOwn();
        }
        else if (number > 1){

        }
    }

}
