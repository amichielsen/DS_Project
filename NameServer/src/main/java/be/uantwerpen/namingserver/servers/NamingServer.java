package be.uantwerpen.namingserver.servers;

import be.uantwerpen.namingserver.services.NamingService;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.net.Inet4Address;
import java.util.TreeMap;


@RestController
@RequestMapping("/naming")
public class NamingServer {
    private static final NamingService namingService = new NamingService();

    // Getting all the hosts
    @GetMapping(path ="/hosts")
    public static TreeMap<Integer,Inet4Address> getHosts() {
        return namingService.getDatabase();
    }

    // Adding 1 host
    @PostMapping(path ="/host")
    public static String addHost(@RequestParam(value = "host") String hostname,@RequestParam(value = "ip") String ip ) throws JSONException {
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
    public static void deleteHost(@RequestParam(value = "host") String ip) {
        namingService.deleteIpAddress(ip);
    }

    // Get IP from filename
    @GetMapping(path ="/file2host")
    public static String getHostIp(@RequestParam(value = "filename") String filename) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        String ip = namingService.getIpAddress(filename).getHostAddress();

        jsonObject.put("ip", ip);
        jsonObject.put("filename", filename);
        return jsonObject.toString();
    }
}
