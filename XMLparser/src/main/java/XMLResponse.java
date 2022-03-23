import jakarta.xml.bind.annotation.XmlAnyAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

import javax.xml.namespace.QName;
import java.util.HashMap;

@XmlRootElement(name = "response")
public class XMLResponse {

    private HashMap<QName, String> serverList = new HashMap<>();;

    @XmlAnyAttribute
    public HashMap<QName, String> getServerList() {
        return serverList;
    }

    public void setServerList(HashMap<QName, String> serverList) {
        this.serverList = serverList;
    }
}