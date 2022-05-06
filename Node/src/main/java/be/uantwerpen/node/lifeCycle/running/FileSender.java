package be.uantwerpen.node.lifeCycle.running;

import be.uantwerpen.node.NodeParameters;
import org.json.simple.JSONObject;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Objects;

public class FileSender {


    public static void sendFile(String path, String host) throws IOException {
        Socket socket = new Socket(host,5044);
        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
        int bytes = 0;
        File file = new File(path);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", file.getName());
        jsonObject.put("length", file.length());
        printWriter.println(jsonObject.toString());
        System.out.println(jsonObject);
        printWriter.flush();

        while (!Objects.equals(bufferedReader.readLine(), "OK")){}
        System.out.println("oke");
        FileInputStream fileInputStream = new FileInputStream(file);
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