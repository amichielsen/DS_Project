package be.uantwerpen.node;

import be.uantwerpen.node.lifeCycle.LifeCycleController;
import be.uantwerpen.node.utils.Hash;
import be.uantwerpen.node.utils.NodeParameters;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication
public class NodeApplication {

    public static void main(String[] args) throws UnknownHostException {
        SpringApplication.run(NodeApplication.class, args);
        NodeParameters.getInstance().setup(InetAddress.getLocalHost().getHostName(), InetAddress.getLocalHost(), Hash.generateHash(InetAddress.getLocalHost().getHostName()) );
        LifeCycleController lifeCycleController = new LifeCycleController();
        Thread t1 = new Thread(lifeCycleController);
        NodeParameters.lifeCycleController = lifeCycleController;
        t1.start();
    }

}
