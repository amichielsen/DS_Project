package be.uantwerpen.namingserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.HashMap;


@RestController
@RequestMapping("/db")
public class Database {

    private NamingServer namingServer= new NamingServer();


    private int hostId;
    private Inet4Address hostIp;
    private static HashMap<Integer,Inet4Address> hostsDB = null;

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



    @GetMapping("/hosts")
    public static HashMap<Integer,Inet4Address> getInstance() {
        if(hostsDB == null)
            hostsDB = new HashMap<Integer, Inet4Address>();
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
