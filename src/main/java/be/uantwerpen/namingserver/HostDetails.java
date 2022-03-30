package be.uantwerpen.namingserver;

public class HostDetails {


    private String hostID, hostIP;


    public HostDetails(String hostID, String hostIP) {
        this.hostID = hostID;
        this.hostIP = hostIP;
    }


    public String getHostID() {
        return hostID;
    }

    public void setHostID(String hostID) {
        this.hostID = hostID;
    }

    public String getHostIP() {
        return hostIP;
    }

    public void setHostIP(String hostIP) {
        this.hostIP = hostIP;
    }
}
