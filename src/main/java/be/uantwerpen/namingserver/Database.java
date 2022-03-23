package be.uantwerpen.namingserver;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.Inet4Address;
import java.util.HashMap;


@RestController
@RequestMapping("/db")
public class Database {


    private int hostId;
    private Inet4Address hostIp;
    private static HashMap<Integer,Inet4Address> hostsDB = null;



    @GetMapping("/hosts")
    public static HashMap<Integer,Inet4Address> getInstance() {
        if(hostsDB == null)
            hostsDB = new HashMap<Integer, Inet4Address>();
        System.out.println("hi");
        return hostsDB;
    }


}
