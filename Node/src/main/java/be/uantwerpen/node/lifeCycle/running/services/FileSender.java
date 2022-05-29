package be.uantwerpen.node.lifeCycle.running.services;

import be.uantwerpen.node.utils.NodeParameters;
import org.json.simple.JSONObject;

import java.io.*;
import java.net.Socket;
import java.util.Objects;

/**
 * Service capable of sending files
 * Starts by sending the name and length to the receiver. Waits for confirmation and then sends the whole file
 */
public class FileSender {


    public static void sendFile(String path, String host, int id, String type) throws IOException {
            if(NodeParameters.DEBUG){
                System.out.println("[FS] Path: " +path);
                System.out.println("[FS] Host: " +host);
                System.out.println("[FS] ID: "+ id);
                System.out.println("[FS] Type: "+type);
            }
            Socket socket = new Socket(host, 5044);
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            int bytes;
            File file = new File(path);
            if(NodeParameters.DEBUG) System.out.println("[FS] filename: " +file.getName());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", file.getName());
            jsonObject.put("length", file.length());
            jsonObject.put("id", id);
            jsonObject.put("type", type);
            printWriter.println(jsonObject);
            if(NodeParameters.DEBUG) System.out.println("[FS] fileinfo: " +jsonObject);
            printWriter.flush();

            while (!Objects.equals(bufferedReader.readLine(), "OK")) {

            }
        if(NodeParameters.DEBUG) System.out.println("[FS] oke");
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