package be.uantwerpen.node.lifeCycle.running.services;

import be.uantwerpen.node.utils.NodeParameters;
import be.uantwerpen.node.utils.cache.IpTableCache;
import be.uantwerpen.node.utils.fileSystem.FileSystem;
import be.uantwerpen.node.utils.Hash;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ReplicationService extends Thread {
    private final Path filename;

    public ReplicationService(Path filename) {
        this.filename = filename;
    }

    /*
      1. User adds file to directory by using a tcp socket
      2. The watchdog will constantly be looking inside that directory
      3. When a new file is detected there are 3 options
        a. The file will need to stay at this node and will be the MAIN
        b. The file is lower than our id and needs to go to our previous node -> directly send
        c. The file is neither, and we will contact the NS to ask it about the correct Node. -> send after we know ip
      4. IF a failure occurs -> data has to be send to new neighboring node
      5. IF a shutdown occurs -> data has to be send to previous node


      - TCP sockets - lexiflexie superRTOS 2000
      - Structuur van data (L of R met dan id van plek + naam bestand) - Vital
      - Watchdog (nieuwe bestanden toegevoegd?) - Louis
      - Aangeroepen code die bekijkt naar waar alles moet - Louis

     */
    public void run() {
        File f1 = new File(String.valueOf(this.filename));
        // 1. Get ID
        int hash = Hash.generateHash(f1.getName());
        int id = NodeParameters.id;
        String ip;
        // 2. Compare ID with itself to check where it belongs

        // A. Only one in network
        if (NodeParameters.id.equals(NodeParameters.nextID)) {
            if(NodeParameters.DEBUG) System.out.println("[RS] I'm the only one");
            // 3. Add to Filesystem
            FileSystem.addLocal(f1.getName(), id);
            FileSystem.addReplica(f1.getName(), id);
            Path destination = Paths.get(NodeParameters.replicaFolder+File.separator+f1.getName());
            try {
                Files.copy(f1.toPath(), destination);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }

        // B. Send to previous - LOCAL (or in some cases myself, but a copy will be sent)
        if (isGoingToPrevious(hash)) {
            System.out.println("[RS] File is for previous (or me)");
            id = NodeParameters.previousID;
            ip = IpTableCache.getInstance().getIp(id).getHostAddress();
            try {
                if (NodeParameters.DEBUG) {
                    System.out.println("[RS] File is being sent");
                }
                FileSender.sendFile(f1.getPath(), ip, NodeParameters.id, "Owner");
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 3. Add to Filesystem
            FileSystem.addLocal(f1.getName(), id);
            return;
        }

        // C. Send to other - LOCAL
        System.out.println("[RS] File is for someone else");
        // Contact NS for correct id
        try {
            HttpRequest request = HttpRequest.newBuilder(
                            URI.create("http://" + NodeParameters.getNameServerIp().getHostAddress() + ":8080/naming/file2host?filename=" + f1.getName()))
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.out.println("[RS] [Error] name server send non 200 code (likely shutting down/busy)");
                return;
            }

            if(NodeParameters.DEBUG) System.out.println("[RS] response: " +response.body());
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(response.body());
            // Adding to ip cache
            id = ((Long) json.get("id")).intValue();
            ip = String.valueOf(json.get("ip"));
            IpTableCache.getInstance().addIp(id, InetAddress.getByName(ip));

            if (NodeParameters.DEBUG) System.out.println("[RS] [Info] the correct node id/ip is: " + id + " | " + ip);
            try {
                if (NodeParameters.DEBUG) {
                    System.out.println("[RS] File is being sent");
                }
                FileSender.sendFile(f1.getPath(), ip, NodeParameters.id, "Owner");
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 3. Add to Filesystem
            FileSystem.addLocal(f1.getName(), id);

        } catch (IOException | InterruptedException e) {
            if (NodeParameters.DEBUG) System.out.println("[RS] [Error] connection error with name server (likely offline)");

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isGoingToPrevious(int hash){
        return (((hash <= NodeParameters.id && hash > NodeParameters.previousID) | (NodeParameters.previousID > NodeParameters.id && (hash > NodeParameters.previousID | hash <= NodeParameters.id))) | ((hash <= NodeParameters.nextID && hash > NodeParameters.id) | (hash > NodeParameters.nextID && hash > NodeParameters.id))) ;
    }
}
