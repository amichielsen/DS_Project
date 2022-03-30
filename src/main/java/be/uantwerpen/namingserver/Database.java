package be.uantwerpen.namingserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


@RestController
@RequestMapping("/db")
public class Database {


    private int hostId;
    private Inet4Address hostIp;
    private static final NamingServer namingServer = new NamingServer();
    private static TreeMap<Integer,Inet4Address> hostsDB = namingServer.getDatabase();

    public Database() throws UnknownHostException {
    }

    /*{ Hosts:[
        {"filename": "doc1.txt",
                "hostId": "1",
                "hostIp": "127.0.0.3",

        },
        {"filename": "doc2.txt",
                "hostId": "17",
                "hostIp": "127.0.0.231",
        }]
        }*/



    @GetMapping(path ="/hosts")
    public static Map<Integer,Inet4Address> getInstance() {
        System.out.println("hi");
        return hostsDB;
    }

    @GetMapping(path = "/filename")
    public Inet4Address getId(@RequestParam(value = "fileName" ,defaultValue = "") String fileName){
        return  namingServer.getIpAddress(fileName);
    }

    @GetMapping(path ="/hosts/{Node}")
    public Inet4Address GetIP_ByNode(@PathVariable("Node") String Node){

        if (Node != null){
            return null;
        }
        else return hostIp;

    }





}
