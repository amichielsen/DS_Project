package be.uantwerpen.node.utils.cache;

import be.uantwerpen.node.utils.NodeParameters;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.HashMap;

public class IpTableCache {
    static final IpTableCache instance = new IpTableCache();
    private final HashMap<Integer, InetAddress> cache = new HashMap<>();

    private IpTableCache() {}
    public static IpTableCache getInstance(){
        return instance;
    }

    public void addIp(Integer id, InetAddress ip) {
        cache.put(id,ip);
    }
    public InetAddress getIp(Integer id) {
        if (cache.containsKey(id)) return cache.get(id);
        try {
            URL ns = new URL("http://"+ NodeParameters.getNameServerIp().getHostAddress()+":8080/naming/host2IP?host="+id.toString());
            System.out.println(ns);
            HttpURLConnection nsConnection = (HttpURLConnection) ns.openConnection();
            nsConnection.setRequestMethod("GET");

            if (nsConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(nsConnection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                // Adding IP to cache for faster access
                addIp(id, InetAddress.getByName(response.toString()));

                // print result
                System.out.println("[IPCACHE] [Done] added following IP to cache: " + response);
                System.out.println(response);
                return InetAddress.getByName(response.toString());
            }

            System.out.println("[IPCACHE] [Error] name server send non 200 code (likely shutting down/busy)");
            return null;
        } catch (IOException e) {
            System.out.println("[IPCACHE] [Error] connection error with name server (likely offline)");
            return null;
        }
    }
}
