package be.uantwerpen.node.lifeCycle.running.services;

import be.uantwerpen.node.utils.NodeParameters;
import be.uantwerpen.node.utils.fileSystem.FileSystem;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

/**
 * Service listening to receive files
 */
public class FileReceiver extends Thread{

    public FileReceiver(){}

    public void run() {

        try{
            ServerSocket serverSocket = new ServerSocket(5044);
            if(NodeParameters.DEBUG) System.out.println("[FR] File receiver listening to port:5044");
            while (true) {

                Socket clientSocket = serverSocket.accept();
                if(NodeParameters.DEBUG) System.out.println("[FR] " + clientSocket + " connected.");
                DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                Map responseMap = new ObjectMapper().readValue(bufferedReader.readLine(), Map.class);
                if(NodeParameters.DEBUG) System.out.println("[FR] responseMap: " + responseMap);
                String filename = (String) responseMap.get("name");
                if(NodeParameters.DEBUG)
                    System.out.println("[FR] Filename: " + filename);
                int size = (int) responseMap.get("length");
                int id = (int) responseMap.get("id");
                String type = (String) responseMap.get("type");
                PrintWriter printWriter = new PrintWriter(clientSocket.getOutputStream());
                printWriter.println("OK");
                printWriter.flush();
                if(NodeParameters.DEBUG) System.out.println("[FR] Delivered");
                FileOutputStream fileOutputStream = new FileOutputStream("/root/data/replica/" + filename);

                int bytes;
                byte[] buffer = new byte[4 * 1024];
                while (size > 0 && (bytes = dataInputStream.read(buffer, 0, Math.min(buffer.length, size))) != -1) {
                    fileOutputStream.write(buffer, 0, bytes);
                    size -= bytes;      // read upto file size
                }
                if(type.equals("Owner")) {
                    if(FileSystem.addReplica(filename, id) == -1){
                        FileSystem.fs.get(filename).setReplicatedOnNode(NodeParameters.id);
                    }
                }
                if(NodeParameters.DEBUG) System.out.println("[FR] Filesystem: " +FileSystem.fs);
            }
            //fileOutputStream.close();
            //dataInputStream.close();
            //dataOutputStream.close();
            //clientSocket.close();
        } catch (Exception e){
            e.printStackTrace();
        }

    }

}