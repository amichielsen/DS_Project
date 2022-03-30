package be.uantwerpen.namingserver;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.HashMap;



@RestController
@RequestMapping("/db")
public class Database {

    private static HashMap<Integer,Inet4Address> hostsDB = null;

    @GetMapping("/hosts")
    public static HashMap<Integer,Inet4Address> getInstance() {
        if(hostsDB == null)
            hostsDB = new HashMap< >();
        return hostsDB;
    }


    //Add an entry into the hosts database using parameters
    @PostMapping(
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            path = "/addEntryParam")
    public String addEntryParam(@RequestParam Integer hostId, String hostIp) throws UnknownHostException {
        Inet4Address tempIP = (Inet4Address) Inet4Address.getByName(hostIp);
        Database.getInstance().put(hostId, tempIP);
        return "added successfully";
    }

    //Add an entry into the hosts database using JSON body
    @PostMapping(
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            path = "/addEntryBody")
    public String addEntryBody(@RequestBody HostDetails hostDetails) throws UnknownHostException {
        Inet4Address tempIP = (Inet4Address) Inet4Address.getByName(hostDetails.getHostIP());
        Database.getInstance().put(   Integer.parseInt(hostDetails.getHostID()), tempIP                 );
        return "added successfully";
    }


    //sample GET request. Just for reference
    @GetMapping(path = "/getHostDetails")
    public HostDetails getHost(@RequestParam String hostId) throws UnknownHostException {

        //test hostDetails entry
        Inet4Address googleAdres = (Inet4Address) Inet4Address.getByName("www.google.com");
        HostDetails host1 = new HostDetails(hostId,googleAdres.getHostAddress());
        return  host1;
    }



}
