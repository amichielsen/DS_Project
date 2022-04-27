package be.uantwerpen.node.lifeCycle;

import be.uantwerpen.node.LifeCycleController;

/**
 * The last and final state...
 * Here we send a REST to our current neighbors, updating them with their new neighbor.
 * At the same time we also let this know to our NameServer.
 * Now we can peacefully go to sleep...
 */
public class Shutdown extends State {
    public Shutdown(LifeCycleController lifeCycleController) {
        super(lifeCycleController);
    }

    @Override
    public void run() {

        //contact previous node to update its next
        //contact next node to update its previous
        //remove itself from Naming server map

    }
}
