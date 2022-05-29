package be.uantwerpen.node.lifeCycle.running.services;

import be.uantwerpen.node.utils.NodeParameters;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class FileDeleter {

    private static final FileDeleter instance = new FileDeleter();
    private FileDeleter() {
    }
    
    public static FileDeleter getInstance(){
        return instance;
    }

    public void deleteFile(String filename){
        try {
            var request = HttpRequest.newBuilder(
                            URI.create("http://"+NodeParameters.getNameServerIp().getHostAddress()+":8080/naming/file2host?filename="+filename))
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                if (NodeParameters.DEBUG) System.out.println("[RS] [Error] name server send non 200 code (likely shutting down/busy)");
                return;
            }

            System.out.println(response.body());
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(response.body());
            // Adding to ip cache
            int id = ((Long) json.get("id")).intValue();
            String ip = String.valueOf(json.get("ip"));

            var deleteRequest = HttpRequest.newBuilder(
                            URI.create("http://"+ip+":8080/api/deleteFile?filename="+filename))
                    .build();
            HttpResponse<String> deleteResponse = HttpClient.newHttpClient().send(deleteRequest, HttpResponse.BodyHandlers.ofString());
            if (NodeParameters.DEBUG) System.out.println("[Deleter] " +deleteResponse.body());

        } catch (IOException | InterruptedException e) {
            if (NodeParameters.DEBUG) System.out.println("[RS] [Error] connection error with name server (likely offline)");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }

    public boolean deleteFromReplicaFolder(String filename){
        File toDelete = new File(NodeParameters.replicaFolder + "/"+filename);
        return toDelete.delete();
    }
}
