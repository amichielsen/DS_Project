package be.uantwerpen.node.lifeCycle.running.services;

import be.uantwerpen.node.NodeParameters;
import org.json.simple.JSONObject;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Service capable of sending files
 * Starts by sending the name and length to the receiver. Waits for confirmation and then sends the whole file
 */
public class FileSender {


    public static void sendFile(String path, String host, int id, String type) throws IOException {
        if(NodeParameters.id == id){
            Map<String, Integer> places = new HashMap<>();
            places.put("Local", id);
            places.put("Owner", NodeParameters.id);
            NodeParameters.bookkeeper.put(new File(path).getName(), places);
        }
        else {
            if(NodeParameters.DEBUG){
                System.out.println(path);
                System.out.println(host);
                System.out.println(id);
                System.out.println(type);
            }
            Socket socket = new Socket(host, 5044);
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            int bytes = 0;
            File file = new File(path);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", file.getName());
            jsonObject.put("length", file.length());
            jsonObject.put("id", id);
            jsonObject.put("type", type);
            printWriter.println(jsonObject.toString());
            System.out.println(jsonObject);
            printWriter.flush();

            while (!Objects.equals(bufferedReader.readLine(), "OK")) {
            }
            System.out.println("oke");
            FileInputStream fileInputStream = new FileInputStream(file);
            // break file into chunks
            byte[] buffer = new byte[4 * 1024];
            while ((bytes = fileInputStream.read(buffer)) != -1) {
                dataOutputStream.write(buffer, 0, bytes);
                dataOutputStream.flush();
            }
            fileInputStream.close();
            dataInputStream.close();
            dataInputStream.close();
        }
    }
}