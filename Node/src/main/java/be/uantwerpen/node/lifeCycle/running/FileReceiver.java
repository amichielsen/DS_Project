package be.uantwerpen.node.lifeCycle.running;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class FileReceiver extends Thread{

    public FileReceiver(){
    }

    public void run() {

        try{
            ServerSocket serverSocket = new ServerSocket(5000);
            System.out.println("listening to port:5044");
            Socket clientSocket = serverSocket.accept();
            System.out.println(clientSocket+" connected.");
            DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
            int bytes = 0;

            int x;
            StringBuffer stringBuffer = new StringBuffer();
            int i = 0;
            while (true) {
                x = dataInputStream.read(); //Get a character
                if (x == '/' || x == -1) break; //reads until stop character
                stringBuffer.append((char) x);
            }
            String filename = stringBuffer.toString();
            FileOutputStream fileOutputStream = new FileOutputStream(filename); //TODO: Pas pad aan
            long size = dataInputStream.readLong();     // read file size

            byte[] buffer = new byte[4*1024];
            while (size > 0 && (bytes = dataInputStream.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1) {
                fileOutputStream.write(buffer,0,bytes);
                size -= bytes;      // read upto file size
            }
            fileOutputStream.close();
            dataInputStream.close();
            dataOutputStream.close();
            clientSocket.close();
        } catch (Exception e){
            e.printStackTrace();
        }

    }



}
