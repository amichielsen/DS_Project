package be.uantwerpen.namingserver.servers;

import be.uantwerpen.namingserver.services.NamingService;
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
    public static int addHost(@RequestParam(value = "host") String hostname,@RequestParam(value = "ip") String ip ) {
        int result = namingService.addIpAddress(hostname, ip);
        return result;
    }

    // Delete 1 host
    @DeleteMapping(path ="/host")
    public static void deleteHost(@RequestParam(value = "host") String ip) {
        namingService.deleteIpAddress(ip);
    }

    // Get IP from filename
    @GetMapping(path ="/file2host")
    public static String getHostIp(@RequestParam(value = "filename") String filename) {
        return namingService.getIpAddress(filename).getHostAddress();
    }

}
