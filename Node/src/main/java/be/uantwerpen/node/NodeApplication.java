package be.uantwerpen.node;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NodeApplication {

    public static void main(String[] args) {
        Thread t1 = new Thread(new LifeCycleController());
        t1.start();
        SpringApplication.run(NodeApplication.class, args);
    }

}
