package be.uantwerpen.namingserver;

import org.springframework.web.bind.annotation.*;

import java.net.Inet4Address;
import java.util.TreeMap;


@RestController
@RequestMapping("/db")
public class Database {
    private static final NamingServer namingServer = new NamingServer();

    // Getting all the hosts
    @GetMapping(path ="/hosts")
    public static TreeMap<Integer,Inet4Address> getHosts() {
        return namingServer.getDatabase();
    }

    // Adding 1 host
    @PostMapping(path ="/host")
    public static void addHost(@RequestParam(value = "host") String hostname,@RequestParam(value = "ip") String ip ) {
        namingServer.addIpAddress(hostname, ip);
    }

    // Delete 1 host
    @DeleteMapping(path ="/host")
    public static void deleteHost(@RequestParam(value = "host") String ip) {
        namingServer.deleteIpAddress(ip);
    }

    // Get IP from filename
    @GetMapping(path ="/file2host")
    public static String getHostIp(@RequestParam(value = "filename") String filename) {
        return namingServer.getIpAddress(filename).getHostAddress();
    }

}
