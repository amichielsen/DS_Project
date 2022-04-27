package be.uantwerpen.node;

import be.uantwerpen.node.lifeCycle.Shutdown;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NodeApplication {

    public static void main(String[] args) throws InterruptedException {

        Thread t1 = new Thread(new LifeCycleController());
        t1.start();
        SpringApplication.run(NodeApplication.class, args);

        //simulate a Shutdown (delete after use)
//        Thread.sleep(10000);
//        var t2 = new LifeCycleController();
//        t2.ChangeState(new Shutdown(new LifeCycleController()));

    }

}
