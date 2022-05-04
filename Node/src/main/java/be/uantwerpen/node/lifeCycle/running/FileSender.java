package be.uantwerpen.node.lifeCycle.running;

import be.uantwerpen.node.NodeParameters;
import org.json.simple.JSONObject;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class FileSender {


    private static void sendFile(String path, InetAddress host) throws IOException {
        Socket socket = new Socket(host,5044);
        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
        int bytes = 0;
        File file = new File(path);
        PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", "Running");
        jsonObject.put("online", true);
        jsonObject.put("thisID", NodeParameters.id);
        jsonObject.put("thisIP", NodeParameters.ip);
        jsonObject.put("previousNeighbor", NodeParameters.previousID);
        jsonObject.put("nextNeighbor", NodeParameters.nextID);
        jsonObject.toString();
        FileInputStream fileInputStream = new FileInputStream(file);
        String filename = file.getName() + "/";
        // send file size
        byte[] filenameBytes = filename.getBytes();
        dataOutputStream.write(filenameBytes, 0, filenameBytes.length);
        dataOutputStream.writeLong(file.length());
        // break file into chunks
        byte[] buffer = new byte[4*1024];
        while ((bytes=fileInputStream.read(buffer))!=-1){
            dataOutputStream.write(buffer,0,bytes);
            dataOutputStream.flush();
        }
        fileInputStream.close();
        dataInputStream.close();
        dataInputStream.close();
    }
}
