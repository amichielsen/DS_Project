import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class XMLWrite {
    private HashMap<Integer, String> serverList = new HashMap<>();

    public XMLWrite() {
        serverList.put(1223, "192.168.1.1");
        serverList.put(1223232, "192.168.1.2");
        //saveServerList(serverList);
        readServerList();
    }

    private void saveServerList(HashMap<Integer, String> list) {
        try {
            // create new `Document`
            DocumentBuilder builder = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder();
            Document dom = builder.newDocument();

            // first create root element
            Element root = dom.createElement("servers");
            dom.appendChild(root);

            for(Map.Entry<Integer, String> entry : list.entrySet()){
                Element element = dom.createElement("server");
                root.appendChild(element);
                // now create child elements (name, email, phone)
                Element hash = dom.createElement("hash");
                hash.setTextContent(String.valueOf(entry.getKey()));

                Element ip = dom.createElement("ip");
                ip.setTextContent(entry.getValue());
                // add child nodes to root node
                element.appendChild(hash);
                element.appendChild(ip);
            }

            // write DOM to XML file
            Transformer tr = TransformerFactory.newInstance().newTransformer();
            tr.setOutputProperty(OutputKeys.INDENT, "yes");
            tr.transform(new DOMSource(dom), new StreamResult(new File("file.xml")));

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private HashMap<Integer, String> readServerList() {
        HashMap<Integer, String> list = new HashMap<>();
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

                    list.put(Integer.valueOf(hash), ip);

                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return list;
    }
}


