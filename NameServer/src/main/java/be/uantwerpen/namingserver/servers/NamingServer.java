package be.uantwerpen.namingserver.servers;

import be.uantwerpen.namingserver.services.MulticastReceiver;
import be.uantwerpen.namingserver.services.NamingService;
import be.uantwerpen.namingserver.utils.hash.Hash;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.TreeMap;

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/naming")
public class NamingServer {
    private static final NamingService namingService = new NamingService();
    private MulticastReceiver multicastReceiver;

    public NamingServer() {
        this.multicastReceiver = new MulticastReceiver();
        this.multicastReceiver.start();
    }

    // Getting all the hosts
    @GetMapping(path ="/hosts")
    public static TreeMap<Integer,Inet4Address> getHosts() {
        return namingService.getDatabase();
    }

    // Getting IP address given a hostId
    @GetMapping(path ="/host2IP")
    public static String host2IP(@RequestParam(value = "host") Integer hostId) {
        return namingService.getDatabase().get(hostId).getHostAddress();
    }

    // Adding 1 host
    @PostMapping(path ="/host")
    public static String addHost(@RequestParam(value = "host") String hostname,@RequestParam(value = "ip") String ip ) {
        JSONObject jsonObject = new JSONObject();
        Integer hash = namingService.addIpAddress(hostname, ip);
        jsonObject.put("hostname", hostname);
        jsonObject.put("ip", ip);
        if (hash == -1) {
            jsonObject.put("status", "failed - entry already exists");
            return jsonObject.toString();
        }
        jsonObject.put("hash", hash);
        jsonObject.put("status", "success");
        return jsonObject.toString();
    }

    // Delete 1 host
    @DeleteMapping(path ="/host")
    public static String deleteHost(@RequestParam(value = "host") String host) {
        JSONObject jsonObject = new JSONObject();
        String status = namingService.deleteHost(host) ? "success": "failed";

        jsonObject.put("status", status);
        return jsonObject.toJSONString();
    }
    @DeleteMapping(path ="/Id")
    public static String deleteId(@RequestParam(value = "Id") int Id) {
        JSONObject jsonObject = new JSONObject();
        String status = namingService.deleteID(Id) ? "success": "failed";

        jsonObject.put("status", status);
        return jsonObject.toJSONString();
    }

    // Get IP from filename
    @GetMapping(path ="/file2host")
    public static String getHostIp(@RequestParam(value = "filename") String filename) {
        JSONObject jsonObject = new JSONObject();
        String ip = namingService.getIpAddress(filename).getHostAddress();
        int id = namingService.getNodeID(filename);
        System.out.println(Hash.generateHash(filename));
        jsonObject.put("ip", ip);
        jsonObject.put("id", id);
        jsonObject.put("filename", filename);
        return jsonObject.toString();
    }

    @PostMapping(path="/failure")
    public static String failure(@RequestParam(value = "id") int failedID){
        JSONObject jsonObject = new JSONObject();
        ArrayList<Integer> neighbours = namingService.getNeighbours(failedID);
        if(!namingService.deleteID(failedID)){
            return jsonObject.toJSONString();
        }
        jsonObject.put("previous", neighbours.get(0));
        jsonObject.put("next", neighbours.get(1));
        return jsonObject.toJSONString();
    }

    public static int getNumberOfNodes(){
        return namingService.getNrOfNodes();
    }

}
