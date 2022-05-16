package be.uantwerpen.node;

import be.uantwerpen.node.lifeCycle.Shutdown;
import be.uantwerpen.node.utils.Hash;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication
public class NodeApplication {

    public static void main(String[] args) throws InterruptedException, UnknownHostException {
        NodeParameters.getInstance().setup(InetAddress.getLocalHost().getHostName(), InetAddress.getLocalHost(), Hash.generateHash(InetAddress.getLocalHost().getHostName()) );
        Thread t1 = new Thread(new LifeCycleController());
        t1.start();
        SpringApplication.run(NodeApplication.class, args);
    }

}
