package be.uantwerpen.node.lifeCycle.running.services;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.Buffer;
import java.util.Map;
import java.util.TreeMap;

/**
 * Service listening to receive files
 */
public class FileReceiver extends Thread{

    public FileReceiver(){
    }

    public void run() {

        try{
            ServerSocket serverSocket = new ServerSocket(5044);
            System.out.println("listening to port:5044");
            while(true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println(clientSocket + " connected.");
                DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                Map responseMap = new ObjectMapper().readValue(bufferedReader.readLine(), Map.class);
                System.out.println(responseMap);
                String filename = (String) responseMap.get("name");
                int size = (int) responseMap.get("length");
                PrintWriter printWriter = new PrintWriter(clientSocket.getOutputStream());
                printWriter.println("OK");
                printWriter.flush();

                FileOutputStream fileOutputStream = new FileOutputStream("/root/data/replica/" + filename);

                int bytes = 0;
                byte[] buffer = new byte[4 * 1024];
                while (size > 0 && (bytes = dataInputStream.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
                    fileOutputStream.write(buffer, 0, bytes);
                    size -= bytes;      // read upto file size
                }
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