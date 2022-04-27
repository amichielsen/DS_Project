package be.uantwerpen.node.lifeCycle;

import be.uantwerpen.node.LifeCycleController;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

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

        try {
            getIPfromHostId(21926);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //contact previous node to update its next
        //contact next node to update its previous
        //remove itself from Naming server map

    }

    public void getIPfromHostId(Integer hostId) throws IOException, InterruptedException {
        // create a client
        var client = HttpClient.newHttpClient();

        // create a request
        var request = HttpRequest.newBuilder(
        URI.create("http://localhost:8080/naming/host2IP?host="+hostId.toString()))
        .header("accept", "application/json")
        .build();

        // use the client to send the request
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // the response:
        System.out.println(response.body());
    }
}
