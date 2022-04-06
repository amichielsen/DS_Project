package be.uantwerpen.namingserver.utils.xmlParser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.TreeMap;

/**
 * This function will read a XML file and formats this as a Map.
 * -- Louis de Looze
 */
public class XMLRead {
    public static TreeMap<Integer, Inet4Address> serverList() {
        TreeMap<Integer, Inet4Address> list = new TreeMap<>();
        System.out.print("Loading XML ... ");
        try {
            // parse XML file to build DOM
            DocumentBuilder builder = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder();
            Document dom = builder.parse(new File("file.xml"));

            // normalize XML structure
            dom.getDocumentElement().normalize();
            NodeList nList = dom.getElementsByTagName("server");

            for (int i = 0; i < nList.getLength(); i++) {

                Node nNode = nList.item(i);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element elem = (Element) nNode;

                    Node node1 = elem.getElementsByTagName("hash").item(0);
                    String hash = node1.getTextContent();

                    Node node2 = elem.getElementsByTagName("ip").item(0);
                    String ip = node2.getTextContent();

                    list.put(Integer.valueOf(hash), (Inet4Address) InetAddress.getByName(ip));

                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("[DONE]");
        return list;
    }
}
